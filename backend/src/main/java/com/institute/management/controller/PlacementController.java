package com.institute.management.controller;

import com.institute.management.dto.*;
import com.institute.management.entity.Placement;
import com.institute.management.service.PlacementService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/placements")
@Tag(name = "Placement Management", description = "APIs for managing student placements")
public class PlacementController {
    
    @Autowired
    private PlacementService placementService;
    
    @Operation(summary = "Create a new placement", description = "Record a new student placement")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Placement created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Student or company not found")
    })
    @PostMapping
    public ResponseEntity<PlacementResponseDTO> createPlacement(@Valid @RequestBody PlacementCreateRequestDTO request) {
        PlacementResponseDTO response = placementService.createPlacement(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Get placement by ID", description = "Retrieve a placement by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Placement found"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Placement not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PlacementResponseDTO> getPlacementById(@PathVariable UUID id) {
        PlacementResponseDTO response = placementService.getPlacementById(id);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update placement", description = "Update an existing placement record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Placement updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Placement not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PlacementResponseDTO> updatePlacement(
            @PathVariable UUID id,
            @Valid @RequestBody PlacementUpdateRequestDTO request) {
        PlacementResponseDTO response = placementService.updatePlacement(id, request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Delete placement", description = "Delete a placement record from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Placement deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Placement not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlacement(@PathVariable UUID id) {
        placementService.deletePlacement(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Get all placements", description = "Retrieve all placements with pagination and filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Placements retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public ResponseEntity<Page<PlacementResponseDTO>> getAllPlacements(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "placementDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Filter by status") @RequestParam(required = false) Placement.PlacementStatus status,
            @Parameter(description = "Filter by company ID") @RequestParam(required = false) UUID companyId,
            @Parameter(description = "Filter by job type") @RequestParam(required = false) Placement.JobType jobType,
            @Parameter(description = "Filter by employment type") @RequestParam(required = false) Placement.EmploymentType employmentType,
            @Parameter(description = "Minimum salary filter") @RequestParam(required = false) BigDecimal minSalary,
            @Parameter(description = "Maximum salary filter") @RequestParam(required = false) BigDecimal maxSalary,
            @Parameter(description = "Filter by course ID") @RequestParam(required = false) UUID courseId,
            @Parameter(description = "Search term (position, company name, student name)") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by placement start date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Filter by placement end date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PlacementResponseDTO> response = placementService.getAllPlacements(
            pageable, status, companyId, jobType, employmentType, minSalary, maxSalary, courseId, search, startDate, endDate);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update placement status", description = "Update the status of a placement")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Placement status updated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Placement not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<PlacementResponseDTO> updatePlacementStatus(
            @PathVariable UUID id,
            @RequestParam Placement.PlacementStatus status) {
        PlacementResponseDTO response = placementService.updatePlacementStatus(id, status);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get placements by student", description = "Retrieve all placements for a specific student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Placements retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<PlacementResponseDTO>> getPlacementsByStudent(@PathVariable UUID studentId) {
        List<PlacementResponseDTO> response = placementService.getPlacementsByStudent(studentId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get placements by company", description = "Retrieve all placements for a specific company")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Placements retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @GetMapping("/company/{companyId}")
    public ResponseEntity<Page<PlacementResponseDTO>> getPlacementsByCompany(
            @PathVariable UUID companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "placementDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PlacementResponseDTO> response = placementService.getPlacementsByCompany(companyId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get recent placements", description = "Retrieve the most recent placements")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recent placements retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/recent")
    public ResponseEntity<Page<PlacementResponseDTO>> getRecentPlacements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PlacementResponseDTO> response = placementService.getRecentPlacements(pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get active placements", description = "Retrieve all currently active placements")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active placements retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/active")
    public ResponseEntity<List<PlacementResponseDTO>> getActivePlacements() {
        List<PlacementResponseDTO> response = placementService.getActivePlacements();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get placement statistics", description = "Get comprehensive placement statistics and analytics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getPlacementStatistics() {
        Map<String, Object> response = placementService.getPlacementStatistics();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get salary statistics", description = "Get salary-related statistics and analytics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Salary statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/statistics/salary")
    public ResponseEntity<Map<String, Object>> getSalaryStatistics() {
        Map<String, Object> response = placementService.getSalaryStatistics();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get placement trends", description = "Get monthly placement trends over time")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Placement trends retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/statistics/trends")
    public ResponseEntity<List<Map<String, Object>>> getPlacementTrends(
            @Parameter(description = "Start date for trend analysis") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusYears(1);
        }
        
        List<Map<String, Object>> response = placementService.getPlacementTrends(startDate);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get placement rate by course", description = "Get placement rates grouped by course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Placement rates retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/statistics/placement-rate")
    public ResponseEntity<List<Map<String, Object>>> getPlacementRateByCourse() {
        List<Map<String, Object>> response = placementService.getPlacementRateByCourse();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get company performance", description = "Get performance statistics for hiring companies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Company performance retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/statistics/company-performance")
    public ResponseEntity<List<Map<String, Object>>> getCompanyPerformance() {
        List<Map<String, Object>> response = placementService.getCompanyPerformanceStats();
        return ResponseEntity.ok(response);
    }
}