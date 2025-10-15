package com.institute.management.service;

import com.institute.management.dto.*;
import com.institute.management.entity.Company;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyService {
    
    @Autowired
    private CompanyRepository companyRepository;
    
    /**
     * Create a new company - Only ADMIN and PLACEMENT_OFFICER can create companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public CompanyResponseDTO createCompany(CompanyCreateRequestDTO request) {
        // Check for duplicate name
        if (companyRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Company with name '" + request.getName() + "' already exists");
        }
        
        // Check for duplicate email if provided
        if (request.getEmail() != null && companyRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Company with email '" + request.getEmail() + "' already exists");
        }
        
        Company company = new Company();
        company.setName(request.getName());
        company.setIndustry(request.getIndustry());
        company.setContactPerson(request.getContactPerson());
        company.setEmail(request.getEmail());
        company.setPhone(request.getPhone());
        company.setAddress(request.getAddress());
        company.setPartnershipDate(request.getPartnershipDate());
        
        Company savedCompany = companyRepository.save(company);
        return convertToResponseDTO(savedCompany);
    }
    
    /**
     * Update an existing company - Only ADMIN and PLACEMENT_OFFICER can update companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public CompanyResponseDTO updateCompany(UUID id, CompanyUpdateRequestDTO request) {
        Company company = companyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        
        // Check for duplicate name if changed
        if (!company.getName().equals(request.getName()) && companyRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Company with name '" + request.getName() + "' already exists");
        }
        
        // Check for duplicate email if changed
        if (request.getEmail() != null && !Objects.equals(company.getEmail(), request.getEmail()) 
            && companyRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Company with email '" + request.getEmail() + "' already exists");
        }
        
        company.setName(request.getName());
        company.setIndustry(request.getIndustry());
        company.setContactPerson(request.getContactPerson());
        company.setEmail(request.getEmail());
        company.setPhone(request.getPhone());
        company.setAddress(request.getAddress());
        company.setPartnershipDate(request.getPartnershipDate());
        company.setStatus(request.getStatus());
        
        Company savedCompany = companyRepository.save(company);
        return convertToResponseDTO(savedCompany);
    }
    
    /**
     * Get company by ID - ADMIN and PLACEMENT_OFFICER can view companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public CompanyResponseDTO getCompanyById(UUID id) {
        Company company = companyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        return convertToResponseDTO(company);
    }
    
    /**
     * Get all companies with pagination and filtering - ADMIN and PLACEMENT_OFFICER can view companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Page<CompanyResponseDTO> getAllCompanies(Pageable pageable, Company.CompanyStatus status, 
            String industry, String search, LocalDate partnershipStartDate, LocalDate partnershipEndDate) {
        
        Page<Company> companies;
        
        // If date range is provided, use it for filtering
        if (partnershipStartDate != null && partnershipEndDate != null) {
            companies = companyRepository.findByPartnershipDateBetween(partnershipStartDate, partnershipEndDate, pageable);
        } else {
            // Use the complex filter query
            companies = companyRepository.findCompaniesWithFilters(status, industry, search, pageable);
        }
        
        return companies.map(this::convertToResponseDTO);
    }
    
    /**
     * Get companies by industry - ADMIN and PLACEMENT_OFFICER can view companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Page<CompanyResponseDTO> getCompaniesByIndustry(String industry, Pageable pageable) {
        Page<Company> companies = companyRepository.findByIndustry(industry, pageable);
        return companies.map(this::convertToResponseDTO);
    }
    
    /**
     * Get companies by status - ADMIN and PLACEMENT_OFFICER can view companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<CompanyResponseDTO> getCompaniesByStatus(Company.CompanyStatus status) {
        List<Company> companies = companyRepository.findByStatus(status);
        return companies.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Update company status - Only ADMIN and PLACEMENT_OFFICER can update status
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public CompanyResponseDTO updateCompanyStatus(UUID companyId, Company.CompanyStatus status) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
        
        company.setStatus(status);
        Company savedCompany = companyRepository.save(company);
        return convertToResponseDTO(savedCompany);
    }
    
    /**
     * Delete company - Only ADMIN can delete companies
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCompany(UUID id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company", "id", id);
        }
        companyRepository.deleteById(id);
    }
    
    /**
     * Get active companies - ADMIN and PLACEMENT_OFFICER can view active companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<CompanyResponseDTO> getActiveCompanies() {
        List<Company> companies = companyRepository.findByStatus(Company.CompanyStatus.ACTIVE);
        return companies.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get companies with placements - ADMIN and PLACEMENT_OFFICER can view companies with placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<CompanyResponseDTO> getCompaniesWithPlacements() {
        List<Company> companies = companyRepository.findCompaniesWithPlacements();
        return companies.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get companies without placements - ADMIN and PLACEMENT_OFFICER can view companies without placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<CompanyResponseDTO> getCompaniesWithoutPlacements() {
        List<Company> companies = companyRepository.findCompaniesWithoutPlacements();
        return companies.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get recent partners - ADMIN and PLACEMENT_OFFICER can view recent partners
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<CompanyResponseDTO> getRecentPartners(int months) {
        LocalDate cutoffDate = LocalDate.now().minusMonths(months);
        List<Company> companies = companyRepository.findRecentPartners(cutoffDate);
        return companies.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get long-term partners - ADMIN and PLACEMENT_OFFICER can view long-term partners
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<CompanyResponseDTO> getLongTermPartners(int years) {
        LocalDate cutoffDate = LocalDate.now().minusYears(years);
        List<Company> companies = companyRepository.findLongTermPartners(cutoffDate);
        return companies.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get active hiring companies - ADMIN and PLACEMENT_OFFICER can view active hiring companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<CompanyResponseDTO> getActiveHiringCompanies(int months) {
        LocalDate cutoffDate = LocalDate.now().minusMonths(months);
        List<Company> companies = companyRepository.findActiveHiringCompanies(cutoffDate);
        return companies.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get top hiring companies - ADMIN and PLACEMENT_OFFICER can view top hiring companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Map<String, Object>> getTopHiringCompanies(LocalDate startDate, LocalDate endDate, int limit) {
        List<Object[]> topHiring = companyRepository.findTopHiringCompaniesByPeriod(startDate, endDate);
        return topHiring.stream()
            .limit(limit)
            .map(hiring -> {
                Map<String, Object> hiringMap = new HashMap<>();
                Company company = (Company) hiring[0];
                hiringMap.put("companyId", company.getId());
                hiringMap.put("companyName", company.getName());
                hiringMap.put("industry", company.getIndustry());
                hiringMap.put("placementCount", hiring[1]);
                return hiringMap;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get company statistics - ADMIN and PLACEMENT_OFFICER can view company statistics
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Map<String, Object> getCompanyStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalCompanies = companyRepository.count();
        long activeCompanies = companyRepository.countByStatus(Company.CompanyStatus.ACTIVE);
        long inactiveCompanies = companyRepository.countByStatus(Company.CompanyStatus.INACTIVE);
        long blacklistedCompanies = companyRepository.countByStatus(Company.CompanyStatus.BLACKLISTED);
        
        stats.put("totalCompanies", totalCompanies);
        stats.put("activeCompanies", activeCompanies);
        stats.put("inactiveCompanies", inactiveCompanies);
        stats.put("blacklistedCompanies", blacklistedCompanies);
        
        long distinctIndustries = companyRepository.countDistinctIndustries();
        stats.put("distinctIndustries", distinctIndustries);
        
        return stats;
    }
    
    /**
     * Get industry distribution - ADMIN and PLACEMENT_OFFICER can view industry distribution
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Map<String, Object>> getIndustryDistribution() {
        List<Object[]> distribution = companyRepository.countByIndustry();
        return distribution.stream()
            .map(dist -> {
                Map<String, Object> distMap = new HashMap<>();
                distMap.put("industry", dist[0]);
                distMap.put("count", dist[1]);
                return distMap;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get partnership trends - ADMIN and PLACEMENT_OFFICER can view partnership trends
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Map<String, Object>> getPartnershipTrends(LocalDate startDate, LocalDate endDate) {
        List<Object[]> trends = companyRepository.getPartnershipTrends(startDate, endDate);
        return trends.stream()
            .map(trend -> {
                Map<String, Object> trendMap = new HashMap<>();
                trendMap.put("date", trend[0]);
                trendMap.put("count", trend[1]);
                return trendMap;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get companies by salary offered - ADMIN and PLACEMENT_OFFICER can view companies by salary
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Map<String, Object>> getCompaniesBySalaryOffered(int minPlacements) {
        List<Object[]> salaryRanking = companyRepository.findCompaniesBySalaryOffered(minPlacements);
        return salaryRanking.stream()
            .map(salary -> {
                Map<String, Object> salaryMap = new HashMap<>();
                Company company = (Company) salary[0];
                salaryMap.put("companyId", company.getId());
                salaryMap.put("companyName", company.getName());
                salaryMap.put("industry", company.getIndustry());
                salaryMap.put("averageSalary", salary[1]);
                salaryMap.put("placementCount", salary[2]);
                return salaryMap;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Convert Company entity to CompanyResponseDTO
     */
    private CompanyResponseDTO convertToResponseDTO(Company company) {
        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setIndustry(company.getIndustry());
        dto.setContactPerson(company.getContactPerson());
        dto.setEmail(company.getEmail());
        dto.setPhone(company.getPhone());
        dto.setAddress(company.getAddress());
        dto.setPartnershipDate(company.getPartnershipDate());
        dto.setStatus(company.getStatus());
        dto.setCreatedDate(company.getCreatedDate());
        dto.setUpdatedDate(company.getUpdatedDate());
        
        // Convert placements to basic DTOs if needed
        if (company.getPlacements() != null && !company.getPlacements().isEmpty()) {
            List<PlacementBasicDTO> placementDTOs = company.getPlacements().stream()
                .map(placement -> {
                    PlacementBasicDTO placementDTO = new PlacementBasicDTO();
                    placementDTO.setId(placement.getId());
                    placementDTO.setPosition(placement.getPosition());
                    placementDTO.setSalary(placement.getSalary());
                    placementDTO.setPlacementDate(placement.getPlacementDate());
                    placementDTO.setStatus(placement.getStatus());
                    placementDTO.setJobType(placement.getJobType());
                    placementDTO.setEmploymentType(placement.getEmploymentType());
                    return placementDTO;
                })
                .collect(Collectors.toList());
            dto.setPlacements(placementDTOs);
        }
        
        return dto;
    }
}