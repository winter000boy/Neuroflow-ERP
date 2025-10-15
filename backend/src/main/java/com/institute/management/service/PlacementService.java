package com.institute.management.service;

import com.institute.management.dto.*;
import com.institute.management.entity.Company;
import com.institute.management.entity.Placement;
import com.institute.management.entity.Student;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.repository.CompanyRepository;
import com.institute.management.repository.PlacementRepository;
import com.institute.management.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlacementService {
    
    @Autowired
    private PlacementRepository placementRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    /**
     * Create a new placement record - Only ADMIN and PLACEMENT_OFFICER can create placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public PlacementResponseDTO createPlacement(PlacementCreateRequestDTO request) {
        Student student = studentRepository.findById(request.getStudentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));
        
        Company company = companyRepository.findById(request.getCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Company", "id", request.getCompanyId()));
        
        Placement placement = new Placement();
        placement.setStudent(student);
        placement.setCompany(company);
        placement.setPosition(request.getPosition());
        placement.setSalary(request.getSalary());
        placement.setPlacementDate(request.getPlacementDate());
        placement.setJobType(request.getJobType());
        placement.setWorkLocation(request.getWorkLocation());
        placement.setEmploymentType(request.getEmploymentType());
        placement.setProbationPeriodMonths(request.getProbationPeriodMonths());
        placement.setJoiningDate(request.getJoiningDate());
        placement.setNotes(request.getNotes());
        
        Placement savedPlacement = placementRepository.save(placement);
        return convertToResponseDTO(savedPlacement);
    }
    
    /**
     * Update an existing placement - Only ADMIN and PLACEMENT_OFFICER can update placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public PlacementResponseDTO updatePlacement(UUID id, PlacementUpdateRequestDTO request) {
        Placement placement = placementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Placement", "id", id));
        
        Student student = studentRepository.findById(request.getStudentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));
        
        Company company = companyRepository.findById(request.getCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Company", "id", request.getCompanyId()));
        
        placement.setStudent(student);
        placement.setCompany(company);
        placement.setPosition(request.getPosition());
        placement.setSalary(request.getSalary());
        placement.setPlacementDate(request.getPlacementDate());
        placement.setStatus(request.getStatus());
        placement.setJobType(request.getJobType());
        placement.setWorkLocation(request.getWorkLocation());
        placement.setEmploymentType(request.getEmploymentType());
        placement.setProbationPeriodMonths(request.getProbationPeriodMonths());
        placement.setJoiningDate(request.getJoiningDate());
        placement.setEndDate(request.getEndDate());
        placement.setNotes(request.getNotes());
        
        Placement savedPlacement = placementRepository.save(placement);
        return convertToResponseDTO(savedPlacement);
    }
    
    /**
     * Get placement by ID - ADMIN and PLACEMENT_OFFICER can view placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public PlacementResponseDTO getPlacementById(UUID id) {
        Placement placement = placementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Placement", "id", id));
        return convertToResponseDTO(placement);
    }
    
    /**
     * Get all placements with pagination and filtering - ADMIN and PLACEMENT_OFFICER can view placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Page<PlacementResponseDTO> getAllPlacements(Pageable pageable, Placement.PlacementStatus status, 
            UUID companyId, Placement.JobType jobType, Placement.EmploymentType employmentType, 
            BigDecimal minSalary, BigDecimal maxSalary, UUID courseId, String search, 
            LocalDate startDate, LocalDate endDate) {
        
        // If date range is provided, use it for filtering
        if (startDate != null && endDate != null) {
            Page<Placement> placements = placementRepository.findByPlacementDateBetween(startDate, endDate, pageable);
            return placements.map(this::convertToResponseDTO);
        }
        
        // Use the complex filter query
        Page<Placement> placements = placementRepository.findPlacementsWithFilters(
            status, companyId, jobType, employmentType, minSalary, maxSalary, courseId, search, pageable);
        return placements.map(this::convertToResponseDTO);
    }
    
    /**
     * Get placements by student - ADMIN and PLACEMENT_OFFICER can view placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<PlacementResponseDTO> getPlacementsByStudent(UUID studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        
        List<Placement> placements = placementRepository.findByStudent(student);
        return placements.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get placements by company - ADMIN and PLACEMENT_OFFICER can view placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Page<PlacementResponseDTO> getPlacementsByCompany(UUID companyId, Pageable pageable) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
        
        Page<Placement> placements = placementRepository.findByCompany(company, pageable);
        return placements.map(this::convertToResponseDTO);
    }
    
    /**
     * Get placements by status - ADMIN and PLACEMENT_OFFICER can view placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Placement> getPlacementsByStatus(Placement.PlacementStatus status) {
        return placementRepository.findByStatus(status);
    }
    
    /**
     * Get placements within date range - ADMIN and PLACEMENT_OFFICER can view placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Placement> getPlacementsByDateRange(LocalDate startDate, LocalDate endDate) {
        return placementRepository.findByPlacementDateBetween(startDate, endDate);
    }
    
    /**
     * Get placements within salary range - ADMIN and PLACEMENT_OFFICER can view placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Placement> getPlacementsBySalaryRange(BigDecimal minSalary, BigDecimal maxSalary) {
        return placementRepository.findBySalaryBetween(minSalary, maxSalary);
    }
    
    /**
     * Update placement status - Only ADMIN and PLACEMENT_OFFICER can update status
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public PlacementResponseDTO updatePlacementStatus(UUID placementId, Placement.PlacementStatus status) {
        Placement placement = placementRepository.findById(placementId)
            .orElseThrow(() -> new ResourceNotFoundException("Placement", "id", placementId));
        
        placement.setStatus(status);
        Placement savedPlacement = placementRepository.save(placement);
        return convertToResponseDTO(savedPlacement);
    }
    
    /**
     * Delete placement - Only ADMIN can delete placements
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePlacement(UUID id) {
        if (!placementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Placement", "id", id);
        }
        placementRepository.deleteById(id);
    }
    
    /**
     * Get recent placements - ADMIN and PLACEMENT_OFFICER can view recent placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Page<PlacementResponseDTO> getRecentPlacements(Pageable pageable) {
        Page<Placement> placements = placementRepository.findRecentPlacements(pageable);
        return placements.map(this::convertToResponseDTO);
    }
    
    /**
     * Get active placements - ADMIN and PLACEMENT_OFFICER can view active placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<PlacementResponseDTO> getActivePlacements() {
        List<Placement> placements = placementRepository.findActivePlacements(LocalDate.now());
        return placements.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get comprehensive placement statistics - ADMIN and PLACEMENT_OFFICER can view statistics
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Map<String, Object> getPlacementStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalPlacements = placementRepository.count();
        long activePlacements = placementRepository.countByStatus(Placement.PlacementStatus.PLACED);
        long resignedPlacements = placementRepository.countByStatus(Placement.PlacementStatus.RESIGNED);
        long terminatedPlacements = placementRepository.countByStatus(Placement.PlacementStatus.TERMINATED);
        
        stats.put("totalPlacements", totalPlacements);
        stats.put("activePlacements", activePlacements);
        stats.put("resignedPlacements", resignedPlacements);
        stats.put("terminatedPlacements", terminatedPlacements);
        
        // Calculate placement rate
        long totalGraduates = placementRepository.countGraduatedStudents();
        long placedStudents = placementRepository.countPlacedStudents();
        double placementRate = totalGraduates > 0 ? (double) placedStudents / totalGraduates * 100 : 0.0;
        stats.put("placementRate", placementRate);
        
        return stats;
    }
    
    /**
     * Get salary statistics - ADMIN and PLACEMENT_OFFICER can view salary statistics
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Map<String, Object> getSalaryStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        BigDecimal averageSalary = placementRepository.getAverageSalary();
        stats.put("averageSalary", averageSalary != null ? averageSalary : BigDecimal.ZERO);
        
        List<Object[]> salaryRange = placementRepository.getSalaryRange();
        if (!salaryRange.isEmpty()) {
            Object[] range = salaryRange.get(0);
            stats.put("minSalary", range[0]);
            stats.put("maxSalary", range[1]);
        }
        
        List<Object[]> salaryDistribution = placementRepository.getSalaryRangeDistribution();
        if (!salaryDistribution.isEmpty()) {
            Object[] distribution = salaryDistribution.get(0);
            stats.put("lowSalaryCount", distribution[0]);
            stats.put("mediumSalaryCount", distribution[1]);
            stats.put("highSalaryCount", distribution[2]);
        }
        
        return stats;
    }
    
    /**
     * Get placement trends - ADMIN and PLACEMENT_OFFICER can view trends
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Map<String, Object>> getPlacementTrends(LocalDate startDate) {
        List<Object[]> trends = placementRepository.getMonthlyPlacementTrends(startDate);
        return trends.stream()
            .map(trend -> {
                Map<String, Object> trendMap = new HashMap<>();
                trendMap.put("year", trend[0]);
                trendMap.put("month", trend[1]);
                trendMap.put("count", trend[2]);
                return trendMap;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get placement rate by course - ADMIN and PLACEMENT_OFFICER can view placement rates
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Map<String, Object>> getPlacementRateByCourse() {
        List<Object[]> rates = placementRepository.getPlacementRateByCourse();
        return rates.stream()
            .map(rate -> {
                Map<String, Object> rateMap = new HashMap<>();
                rateMap.put("courseName", rate[0]);
                rateMap.put("totalGraduates", rate[1]);
                rateMap.put("placedStudents", rate[2]);
                Long totalGrads = (Long) rate[1];
                Long placedStds = (Long) rate[2];
                double placementRate = totalGrads > 0 ? (double) placedStds / totalGrads * 100 : 0.0;
                rateMap.put("placementRate", placementRate);
                return rateMap;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get company performance statistics - ADMIN and PLACEMENT_OFFICER can view company performance
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Map<String, Object>> getCompanyPerformanceStats() {
        List<Object[]> performance = placementRepository.getCompanyPerformanceStats();
        return performance.stream()
            .map(perf -> {
                Map<String, Object> perfMap = new HashMap<>();
                Company company = (Company) perf[0];
                perfMap.put("companyId", company.getId());
                perfMap.put("companyName", company.getName());
                perfMap.put("placementCount", perf[1]);
                perfMap.put("averageSalary", perf[2]);
                return perfMap;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Convert Placement entity to PlacementResponseDTO
     */
    private PlacementResponseDTO convertToResponseDTO(Placement placement) {
        PlacementResponseDTO dto = new PlacementResponseDTO();
        dto.setId(placement.getId());
        dto.setPosition(placement.getPosition());
        dto.setSalary(placement.getSalary());
        dto.setPlacementDate(placement.getPlacementDate());
        dto.setStatus(placement.getStatus());
        dto.setJobType(placement.getJobType());
        dto.setWorkLocation(placement.getWorkLocation());
        dto.setEmploymentType(placement.getEmploymentType());
        dto.setProbationPeriodMonths(placement.getProbationPeriodMonths());
        dto.setJoiningDate(placement.getJoiningDate());
        dto.setEndDate(placement.getEndDate());
        dto.setNotes(placement.getNotes());
        dto.setCreatedDate(placement.getCreatedDate());
        dto.setUpdatedDate(placement.getUpdatedDate());
        
        // Set computed fields
        dto.setIsActive(placement.isActive());
        dto.setIsInProbation(placement.isInProbation());
        dto.setTenureInMonths(placement.getTenureInMonths());
        
        // Set student basic info
        if (placement.getStudent() != null) {
            Student student = placement.getStudent();
            StudentBasicDTO studentDTO = new StudentBasicDTO();
            studentDTO.setId(student.getId());
            studentDTO.setEnrollmentNumber(student.getEnrollmentNumber());
            studentDTO.setFirstName(student.getFirstName());
            studentDTO.setLastName(student.getLastName());
            studentDTO.setEmail(student.getEmail());
            studentDTO.setStatus(student.getStatus());
            dto.setStudent(studentDTO);
        }
        
        // Set company basic info
        if (placement.getCompany() != null) {
            Company company = placement.getCompany();
            CompanyBasicDTO companyDTO = new CompanyBasicDTO();
            companyDTO.setId(company.getId());
            companyDTO.setName(company.getName());
            companyDTO.setIndustry(company.getIndustry());
            companyDTO.setContactPerson(company.getContactPerson());
            companyDTO.setEmail(company.getEmail());
            companyDTO.setStatus(company.getStatus());
            dto.setCompany(companyDTO);
        }
        
        return dto;
    }
}