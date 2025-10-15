package com.institute.management.controller;

import com.institute.management.dto.*;
import com.institute.management.service.ReportsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "Comprehensive reporting and analytics API")
@SecurityRequirement(name = "bearerAuth")
public class ReportsController {

    @Autowired
    private ReportsService reportsService;

    @Operation(
        summary = "Generate revenue report",
        description = "Generate comprehensive revenue report with breakdown by course, batch, and time period. Only accessible by ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Revenue report generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "400", description = "Invalid date range provided")
    })
    @GetMapping("/revenue")
    public ResponseEntity<RevenueReportDTO> generateRevenueReport(
            @Parameter(description = "Start date for the report (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date for the report (YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        RevenueReportDTO report = reportsService.generateRevenueReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @Operation(
        summary = "Generate enrollment report",
        description = "Generate detailed enrollment report with trends, course-wise breakdown, and retention metrics. Accessible by ADMIN and OPERATIONS roles."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Enrollment report generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "400", description = "Invalid date range provided")
    })
    @GetMapping("/enrollment")
    public ResponseEntity<EnrollmentReportDTO> generateEnrollmentReport(
            @Parameter(description = "Start date for the report (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date for the report (YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        EnrollmentReportDTO report = reportsService.generateEnrollmentReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @Operation(
        summary = "Generate placement report",
        description = "Generate comprehensive placement report with statistics, salary analysis, and company-wise breakdown. Accessible by ADMIN and PLACEMENT_OFFICER roles."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Placement report generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "400", description = "Invalid date range provided")
    })
    @GetMapping("/placement")
    public ResponseEntity<PlacementReportDTO> generatePlacementReport(
            @Parameter(description = "Start date for the report (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date for the report (YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        PlacementReportDTO report = reportsService.generatePlacementReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @Operation(
        summary = "Generate lead conversion report",
        description = "Generate detailed lead conversion report with source analysis, counsellor performance, and conversion trends. Accessible by ADMIN and COUNSELLOR roles."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lead conversion report generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "400", description = "Invalid date range provided")
    })
    @GetMapping("/lead-conversion")
    public ResponseEntity<LeadConversionReportDTO> generateLeadConversionReport(
            @Parameter(description = "Start date for the report (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date for the report (YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        LeadConversionReportDTO report = reportsService.generateLeadConversionReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @Operation(
        summary = "Generate batch utilization report",
        description = "Generate comprehensive batch utilization report with capacity analysis, efficiency metrics, and optimization recommendations. Accessible by ADMIN and OPERATIONS roles."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batch utilization report generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    @GetMapping("/batch-utilization")
    public ResponseEntity<BatchUtilizationReportDTO> generateBatchUtilizationReport() {
        BatchUtilizationReportDTO report = reportsService.generateBatchUtilizationReport();
        return ResponseEntity.ok(report);
    }

    @Operation(
        summary = "Generate faculty performance report",
        description = "Generate detailed faculty performance report with workload analysis, completion rates, and performance metrics. Only accessible by ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Faculty performance report generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "400", description = "Invalid date range provided")
    })
    @GetMapping("/faculty-performance")
    public ResponseEntity<FacultyPerformanceReportDTO> generateFacultyPerformanceReport(
            @Parameter(description = "Start date for the report (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date for the report (YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        FacultyPerformanceReportDTO report = reportsService.generateFacultyPerformanceReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @Operation(
        summary = "Generate dashboard summary",
        description = "Generate comprehensive dashboard summary with key metrics, trends, and role-specific data. Accessible by all authenticated users with appropriate role-based data filtering."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard summary generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - authentication required")
    })
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummaryDTO> generateDashboardSummary() {
        DashboardSummaryDTO summary = reportsService.generateDashboardSummary();
        return ResponseEntity.ok(summary);
    }

    @Operation(
        summary = "Generate custom report",
        description = "Generate custom report based on specified type and parameters. Report types: enrollment_summary, revenue_analysis, placement_summary, lead_analysis. Accessible based on user role and report type."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Custom report generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid report type or parameters"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    @PostMapping("/custom")
    public ResponseEntity<Map<String, Object>> generateCustomReport(
            @Parameter(description = "Type of custom report to generate", example = "enrollment_summary")
            @RequestParam String reportType,
            
            @Parameter(description = "Custom parameters for report generation")
            @RequestBody(required = false) Map<String, Object> parameters) {
        
        if (parameters == null) {
            parameters = Map.of();
        }
        
        Map<String, Object> report = reportsService.generateCustomReport(reportType, parameters);
        return ResponseEntity.ok(report);
    }

    @Operation(
        summary = "Get available report types",
        description = "Get list of available report types based on user role and permissions."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Available report types retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - authentication required")
    })
    @GetMapping("/types")
    public ResponseEntity<Map<String, Object>> getAvailableReportTypes() {
        Map<String, Object> reportTypes = Map.of(
            "standard_reports", Map.of(
                "revenue", "Revenue Report - Financial analysis and revenue breakdown",
                "enrollment", "Enrollment Report - Student enrollment trends and statistics",
                "placement", "Placement Report - Job placement statistics and salary analysis",
                "lead_conversion", "Lead Conversion Report - Lead management and conversion analysis",
                "batch_utilization", "Batch Utilization Report - Capacity and efficiency analysis",
                "faculty_performance", "Faculty Performance Report - Faculty workload and performance metrics",
                "dashboard", "Dashboard Summary - Comprehensive overview and key metrics"
            ),
            "custom_reports", Map.of(
                "enrollment_summary", "Custom enrollment summary with flexible parameters",
                "revenue_analysis", "Custom revenue analysis with specific filters",
                "placement_summary", "Custom placement summary with company/course filters",
                "lead_analysis", "Custom lead analysis with source and counsellor breakdown"
            ),
            "access_info", Map.of(
                "note", "Report access is role-based. Some reports may require specific permissions.",
                "roles", Map.of(
                    "ADMIN", "Full access to all reports",
                    "OPERATIONS", "Access to enrollment, batch utilization reports",
                    "COUNSELLOR", "Access to lead conversion reports",
                    "PLACEMENT_OFFICER", "Access to placement reports",
                    "FACULTY", "Access to dashboard summary only"
                )
            )
        );
        
        return ResponseEntity.ok(reportTypes);
    }
}