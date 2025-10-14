package com.institute.management.service;

import com.institute.management.entity.Company;
import com.institute.management.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CompanyService {
    
    @Autowired
    private CompanyRepository companyRepository;
    
    /**
     * Create a new company - Only ADMIN and PLACEMENT_OFFICER can create companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }
    
    /**
     * Update an existing company - Only ADMIN and PLACEMENT_OFFICER can update companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Company updateCompany(UUID id, Company companyDetails) {
        Company company = companyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
        
        company.setName(companyDetails.getName());
        company.setIndustry(companyDetails.getIndustry());
        company.setContactPerson(companyDetails.getContactPerson());
        company.setEmail(companyDetails.getEmail());
        company.setPhone(companyDetails.getPhone());
        company.setAddress(companyDetails.getAddress());
        company.setStatus(companyDetails.getStatus());
        
        return companyRepository.save(company);
    }
    
    /**
     * Get company by ID - ADMIN and PLACEMENT_OFFICER can view companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Optional<Company> getCompanyById(UUID id) {
        return companyRepository.findById(id);
    }
    
    /**
     * Get all companies with pagination - ADMIN and PLACEMENT_OFFICER can view companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Page<Company> getAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }
    
    /**
     * Get companies by industry - ADMIN and PLACEMENT_OFFICER can view companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Company> getCompaniesByIndustry(String industry) {
        return companyRepository.findByIndustry(industry);
    }
    
    /**
     * Get companies by status - ADMIN and PLACEMENT_OFFICER can view companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Company> getCompaniesByStatus(Company.CompanyStatus status) {
        return companyRepository.findByStatus(status);
    }
    
    /**
     * Update company status - Only ADMIN and PLACEMENT_OFFICER can update status
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Company updateCompanyStatus(UUID companyId, Company.CompanyStatus status) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));
        
        company.setStatus(status);
        return companyRepository.save(company);
    }
    
    /**
     * Delete company - Only ADMIN can delete companies
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCompany(UUID id) {
        if (!companyRepository.existsById(id)) {
            throw new RuntimeException("Company not found with id: " + id);
        }
        companyRepository.deleteById(id);
    }
    
    /**
     * Get active companies - ADMIN and PLACEMENT_OFFICER can view active companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Company> getActiveCompanies() {
        return companyRepository.findByStatus(Company.CompanyStatus.ACTIVE);
    }
    
    /**
     * Search companies by name - ADMIN and PLACEMENT_OFFICER can search companies
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Page<Company> searchCompaniesByName(String name, Pageable pageable) {
        return companyRepository.findCompaniesWithFilters(null, null, name, pageable);
    }
}