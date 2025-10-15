package com.institute.management.controller;

import com.institute.management.dto.*;
import com.institute.management.entity.Company;
import com.institute.management.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/companies")
@Tag(name = "Company Management", description = "APIs for managing partner companies")
public class CompanyController {
    
    @Autowired
    private CompanyService companyService;
    
    @Operation(summary = "Create a new company", description = "Add a new partner company to the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Company created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "409", description = "Company with name/email already exists")
    })
    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(@Valid @RequestBody CompanyCreateRequestDTO request) {
        CompanyResponseDTO response = companyService.createCompany(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Get company by ID", description = "Retrieve a company by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Company found"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> getCompanyById(@PathVariable UUID id) {
        CompanyResponseDTO response = companyService.getCompanyById(id);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update company", description = "Update an existing company's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Company updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(
            @PathVariable UUID id,
            @Valid @RequestBody CompanyUpdateRequestDTO request) {
        CompanyResponseDTO response = companyService.updateCompany(id, request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Delete company", description = "Delete a company from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Company deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable UUID id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Get all companies", description = "Retrieve all companies with pagination and filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Companies retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public ResponseEntity<Page<CompanyResponseDTO>> getAllCompanies(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(description = "Filter by status") @RequestParam(required = false) Company.CompanyStatus status,
            @Parameter(description = "Filter by industry") @RequestParam(required = false) String industry,
            @Parameter(description = "Search term (name, contact person, email, phone)") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by partnership start date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate partnershipStartDate,
            @Parameter(description = "Filter by partnership end date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate partnershipEndDate) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CompanyResponseDTO> response = companyService.getAllCompanies(
            pageable, status, industry, search, partnershipStartDate, partnershipEndDate);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update company status", description = "Update the status of a company")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Company status updated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<CompanyResponseDTO> updateCompanyStatus(
            @PathVariable UUID id,
            @RequestParam Company.CompanyStatus status) {
        CompanyResponseDTO response = companyService.updateCompanyStatus(id, status);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get active companies", description = "Retrieve all active partner companies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active companies retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/active")
    public ResponseEntity<List<CompanyResponseDTO>> getActiveCompanies() {
        List<CompanyResponseDTO> response = companyService.getActiveCompanies();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get companies by industry", description = "Retrieve companies filtered by industry")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Companies retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/industry/{industry}")
    public ResponseEntity<Page<CompanyResponseDTO>> getCompaniesByIndustry(
            @PathVariable String industry,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CompanyResponseDTO> response = companyService.getCompaniesByIndustry(industry, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get companies with placements", description = "Retrieve companies that have hired students")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Companies with placements retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/with-placements")
    public ResponseEntity<List<CompanyResponseDTO>> getCompaniesWithPlacements() {
        List<CompanyResponseDTO> response = companyService.getCompaniesWithPlacements();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get companies without placements", description = "Retrieve companies that haven't hired any students yet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Companies without placements retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/without-placements")
    public ResponseEntity<List<CompanyResponseDTO>> getCompaniesWithoutPlacements() {
        List<CompanyResponseDTO> response = companyService.getCompaniesWithoutPlacements();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get recent partners", description = "Retrieve recently partnered companies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recent partners retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/recent-partners")
    public ResponseEntity<List<CompanyResponseDTO>> getRecentPartners(
            @Parameter(description = "Number of months to look back") @RequestParam(defaultValue = "6") int months) {
        
        List<CompanyResponseDTO> response = companyService.getRecentPartners(months);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get long-term partners", description = "Retrieve companies with long-term partnerships")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Long-term partners retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/long-term-partners")
    public ResponseEntity<List<CompanyResponseDTO>> getLongTermPartners(
            @Parameter(description = "Minimum years of partnership") @RequestParam(defaultValue = "2") int years) {
        
        List<CompanyResponseDTO> response = companyService.getLongTermPartners(years);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get active hiring companies", description = "Retrieve companies that have hired recently")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active hiring companies retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/active-hiring")
    public ResponseEntity<List<CompanyResponseDTO>> getActiveHiringCompanies(
            @Parameter(description = "Number of months to look back") @RequestParam(defaultValue = "12") int months) {
        
        List<CompanyResponseDTO> response = companyService.getActiveHiringCompanies(months);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get top hiring companies", description = "Retrieve companies with the most placements in a period")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top hiring companies retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/top-hiring")
    public ResponseEntity<List<Map<String, Object>>> getTopHiringCompanies(
            @Parameter(description = "Start date for analysis") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for analysis") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Number of top companies to return") @RequestParam(defaultValue = "10") int limit) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusYears(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        List<Map<String, Object>> response = companyService.getTopHiringCompanies(startDate, endDate, limit);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get company statistics", description = "Get comprehensive company statistics and analytics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getCompanyStatistics() {
        Map<String, Object> response = companyService.getCompanyStatistics();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get industry distribution", description = "Get distribution of companies by industry")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Industry distribution retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/statistics/industry-distribution")
    public ResponseEntity<List<Map<String, Object>>> getIndustryDistribution() {
        List<Map<String, Object>> response = companyService.getIndustryDistribution();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get partnership trends", description = "Get trends of new partnerships over time")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partnership trends retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/statistics/partnership-trends")
    public ResponseEntity<List<Map<String, Object>>> getPartnershipTrends(
            @Parameter(description = "Start date for trend analysis") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for trend analysis") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusYears(2);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        List<Map<String, Object>> response = companyService.getPartnershipTrends(startDate, endDate);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get companies by salary offered", description = "Get companies ranked by average salary offered")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Companies by salary retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/statistics/salary-ranking")
    public ResponseEntity<List<Map<String, Object>>> getCompaniesBySalaryOffered(
            @Parameter(description = "Minimum number of placements required") @RequestParam(defaultValue = "3") int minPlacements) {
        
        List<Map<String, Object>> response = companyService.getCompaniesBySalaryOffered(minPlacements);
        return ResponseEntity.ok(response);
    }
}