package com.institute.management.controller;

import com.institute.management.dto.*;
import com.institute.management.service.ReportsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportsController.class)
class ReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportsService reportsService;

    @Autowired
    private ObjectMapper objectMapper;

    private RevenueReportDTO sampleRevenueReport;
    private EnrollmentReportDTO sampleEnrollmentReport;
    private PlacementReportDTO samplePlacementReport;
    private LeadConversionReportDTO sampleLeadConversionReport;
    private BatchUtilizationReportDTO sampleBatchUtilizationReport;
    private FacultyPerformanceReportDTO sampleFacultyPerformanceReport;
    private DashboardSummaryDTO sampleDashboardSummary;

    @BeforeEach
    void setUp() {
        LocalDate now = LocalDate.now();
        
        sampleRevenueReport = RevenueReportDTO.builder()
            .startDate(now.minusMonths(3))
            .endDate(now)
            .totalRevenue(BigDecimal.valueOf(1500000))
            .projectedRevenue(BigDecimal.valueOf(1650000))
            .revenueByMonth(Map.of("2024-01", BigDecimal.valueOf(500000)))
            .revenueByCourse(Map.of("Java Full Stack", BigDecimal.valueOf(750000)))
            .revenueByBatch(Map.of("JAVA-2024-01", BigDecimal.valueOf(375000)))
            .averageRevenuePerStudent(BigDecimal.valueOf(50000))
            .totalEnrollments(30)
            .growthRate(BigDecimal.valueOf(15.5))
            .generatedAt(now)
            .build();

        sampleEnrollmentReport = EnrollmentReportDTO.builder()
            .startDate(now.minusMonths(3))
            .endDate(now)
            .totalEnrollments(30)
            .activeStudents(25)
            .graduatedStudents(5)
            .droppedStudents(2)
            .enrollmentTrends(Map.of("2024-01", 10))
            .enrollmentsByCourse(Map.of("Java Full Stack", 15))
            .enrollmentsByBatch(Map.of("JAVA-2024-01", 15))
            .enrollmentsByStatus(Map.of("ACTIVE", 25))
            .averageEnrollmentsPerMonth(10.0)
            .growthRate(20.0)
            .retentionRate(93.75)
            .completionRate(71.4)
            .generatedAt(now)
            .build();

        samplePlacementReport = PlacementReportDTO.builder()
            .startDate(now.minusMonths(3))
            .endDate(now)
            .totalPlacements(20)
            .activePlacements(18)
            .placedStudents(20)
            .totalGraduates(25)
            .placementRate(80.0)
            .placementsByCompany(Map.of("Tech Corp", 5))
            .placementsByCourse(Map.of("Java Full Stack", 12))
            .placementsByJobType(Map.of("FULL_TIME", 18))
            .placementsByEmploymentType(Map.of("PERMANENT", 16))
            .placementTrends(Map.of("2024-01", 8))
            .averageSalary(BigDecimal.valueOf(650000))
            .medianSalary(BigDecimal.valueOf(600000))
            .minSalary(BigDecimal.valueOf(400000))
            .maxSalary(BigDecimal.valueOf(1200000))
            .salaryRangeDistribution(Map.of("High", BigDecimal.valueOf(5)))
            .averageSalaryByCompany(Map.of("Tech Corp", BigDecimal.valueOf(700000)))
            .averageSalaryByCourse(Map.of("Java Full Stack", BigDecimal.valueOf(650000)))
            .generatedAt(now)
            .build();

        sampleLeadConversionReport = LeadConversionReportDTO.builder()
            .startDate(now.minusMonths(3))
            .endDate(now)
            .totalLeads(100)
            .convertedLeads(25)
            .activeLeads(50)
            .lostLeads(25)
            .conversionRate(25.0)
            .leadsBySource(Map.of("Website", 40))
            .leadsByStatus(Map.of("NEW", 20))
            .leadsByCourseInterest(Map.of("Java Full Stack", 35))
            .conversionRateBySource(Map.of("Website", 30.0))
            .conversionRateByCounsellor(Map.of("John Doe", 28.0))
            .conversionTrends(Map.of("2024-01", 8))
            .averageConversionTime(15.5)
            .leadsRequiringFollowUp(10)
            .followUpActivity(Map.of("Pending", 10))
            .generatedAt(now)
            .build();

        sampleBatchUtilizationReport = BatchUtilizationReportDTO.builder()
            .totalBatches(10)
            .activeBatches(8)
            .plannedBatches(2)
            .completedBatches(5)
            .averageUtilization(85.5)
            .utilizationByBatch(Map.of("JAVA-2024-01", 83.3))
            .utilizationByCourse(Map.of("Java Full Stack", 85.0))
            .batchesByStatus(Map.of("ACTIVE", 8))
            .underutilizedBatches(1)
            .overutilizedBatches(0)
            .optimallyUtilizedBatches(7)
            .utilizationTrends(Map.of("current", 85.5))
            .capacityEfficiency(85.5)
            .totalCapacity(300)
            .totalEnrollment(256)
            .generatedAt(now)
            .build();

        sampleFacultyPerformanceReport = FacultyPerformanceReportDTO.builder()
            .startDate(now.minusMonths(3))
            .endDate(now)
            .totalFaculty(5)
            .activeFaculty(5)
            .facultyMetrics(Map.of("totalFaculty", 5))
            .batchesPerFaculty(Map.of("Dr. John Professor", 2))
            .studentsPerFaculty(Map.of("Dr. John Professor", 50))
            .completionRatesByFaculty(Map.of("Dr. John Professor", 85.0))
            .placementRatesByFaculty(Map.of("Dr. John Professor", 80.0))
            .studentSatisfactionByFaculty(Map.of("Dr. John Professor", 4.5))
            .workloadDistribution(Map.of("Medium (3-5 batches)", 3))
            .averageBatchesPerFaculty(2.4)
            .averageStudentsPerFaculty(48.0)
            .overallCompletionRate(83.5)
            .performanceTrends(Map.of("completionRateTrend", "improving"))
            .generatedAt(now)
            .build();

        sampleDashboardSummary = DashboardSummaryDTO.builder()
            .totalStudents(150)
            .activeStudents(120)
            .totalBatches(12)
            .activeBatches(10)
            .totalCourses(6)
            .activeCourses(5)
            .totalLeads(200)
            .activeLeads(80)
            .totalPlacements(60)
            .activePlacements(55)
            .totalEmployees(25)
            .activeEmployees(23)
            .monthlyRevenue(BigDecimal.valueOf(500000))
            .yearlyRevenue(BigDecimal.valueOf(5500000))
            .projectedRevenue(BigDecimal.valueOf(6000000))
            .placementRate(75.0)
            .conversionRate(30.0)
            .batchUtilization(85.0)
            .studentRetentionRate(90.0)
            .recentEnrollments(15)
            .recentPlacements(8)
            .recentLeads(25)
            .pendingFollowUps(12)
            .enrollmentTrends(Map.of("2024-01", 12))
            .placementTrends(Map.of("2024-01", 8))
            .revenueTrends(Map.of("2024-01", BigDecimal.valueOf(450000)))
            .underutilizedBatches(2)
            .overdueTasks(5)
            .upcomingBatches(3)
            .roleSpecificData(Map.of("hasAccess", true))
            .generatedAt(now)
            .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGenerateRevenueReport_Success() throws Exception {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        
        when(reportsService.generateRevenueReport(startDate, endDate))
            .thenReturn(sampleRevenueReport);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reports/revenue")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.totalRevenue").value(1500000))
            .andExpect(jsonPath("$.totalEnrollments").value(30))
            .andExpect(jsonPath("$.growthRate").value(15.5))
            .andExpect(jsonPath("$.revenueByCourse['Java Full Stack']").value(750000));

        verify(reportsService).generateRevenueReport(startDate, endDate);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGenerateRevenueReport_InvalidDateRange() throws Exception {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusMonths(1);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reports/revenue")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .with(csrf()))
            .andExpect(status().isBadRequest());

        verify(reportsService, never()).generateRevenueReport(any(), any());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void testGenerateEnrollmentReport_Success() throws Exception {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        
        when(reportsService.generateEnrollmentReport(startDate, endDate))
            .thenReturn(sampleEnrollmentReport);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reports/enrollment")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.totalEnrollments").value(30))
            .andExpect(jsonPath("$.activeStudents").value(25))
            .andExpect(jsonPath("$.retentionRate").value(93.75))
            .andExpect(jsonPath("$.enrollmentsByCourse['Java Full Stack']").value(15));

        verify(reportsService).generateEnrollmentReport(startDate, endDate);
    }

    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void testGeneratePlacementReport_Success() throws Exception {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        
        when(reportsService.generatePlacementReport(startDate, endDate))
            .thenReturn(samplePlacementReport);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reports/placement")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.totalPlacements").value(20))
            .andExpect(jsonPath("$.placementRate").value(80.0))
            .andExpect(jsonPath("$.averageSalary").value(650000))
            .andExpect(jsonPath("$.placementsByCompany['Tech Corp']").value(5));

        verify(reportsService).generatePlacementReport(startDate, endDate);
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void testGenerateLeadConversionReport_Success() throws Exception {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        
        when(reportsService.generateLeadConversionReport(startDate, endDate))
            .thenReturn(sampleLeadConversionReport);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reports/lead-conversion")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.totalLeads").value(100))
            .andExpect(jsonPath("$.conversionRate").value(25.0))
            .andExpect(jsonPath("$.convertedLeads").value(25))
            .andExpect(jsonPath("$.leadsBySource['Website']").value(40));

        verify(reportsService).generateLeadConversionReport(startDate, endDate);
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void testGenerateBatchUtilizationReport_Success() throws Exception {
        // Arrange
        when(reportsService.generateBatchUtilizationReport())
            .thenReturn(sampleBatchUtilizationReport);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reports/batch-utilization")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.totalBatches").value(10))
            .andExpect(jsonPath("$.averageUtilization").value(85.5))
            .andExpect(jsonPath("$.optimallyUtilizedBatches").value(7))
            .andExpect(jsonPath("$.utilizationByBatch['JAVA-2024-01']").value(83.3));

        verify(reportsService).generateBatchUtilizationReport();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGenerateFacultyPerformanceReport_Success() throws Exception {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        
        when(reportsService.generateFacultyPerformanceReport(startDate, endDate))
            .thenReturn(sampleFacultyPerformanceReport);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reports/faculty-performance")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.totalFaculty").value(5))
            .andExpect(jsonPath("$.averageBatchesPerFaculty").value(2.4))
            .andExpect(jsonPath("$.overallCompletionRate").value(83.5))
            .andExpect(jsonPath("$.batchesPerFaculty['Dr. John Professor']").value(2));

        verify(reportsService).generateFacultyPerformanceReport(startDate, endDate);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGenerateDashboardSummary_Success() throws Exception {
        // Arrange
        when(reportsService.generateDashboardSummary())
            .thenReturn(sampleDashboardSummary);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reports/dashboard")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.totalStudents").value(150))
            .andExpect(jsonPath("$.placementRate").value(75.0))
            .andExpect(jsonPath("$.monthlyRevenue").value(500000))
            .andExpect(jsonPath("$.enrollmentTrends['2024-01']").value(12));

        verify(reportsService).generateDashboardSummary();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGenerateCustomReport_Success() throws Exception {
        // Arrange
        String reportType = "enrollment_summary";
        Map<String, Object> parameters = Map.of("courseId", "123");
        Map<String, Object> expectedReport = Map.of(
            "reportType", reportType,
            "data", Map.of("totalEnrollments", 50),
            "generatedAt", LocalDate.now().toString()
        );
        
        when(reportsService.generateCustomReport(eq(reportType), any()))
            .thenReturn(expectedReport);

        // Act & Assert
        mockMvc.perform(post("/api/v1/reports/custom")
                .param("reportType", reportType)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(parameters))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.reportType").value(reportType));

        verify(reportsService).generateCustomReport(eq(reportType), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGenerateCustomReport_WithoutParameters() throws Exception {
        // Arrange
        String reportType = "enrollment_summary";
        Map<String, Object> expectedReport = Map.of(
            "reportType", reportType,
            "data", Map.of("totalEnrollments", 50)
        );
        
        when(reportsService.generateCustomReport(eq(reportType), any()))
            .thenReturn(expectedReport);

        // Act & Assert
        mockMvc.perform(post("/api/v1/reports/custom")
                .param("reportType", reportType)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.reportType").value(reportType));

        verify(reportsService).generateCustomReport(eq(reportType), eq(Map.of()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAvailableReportTypes_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/reports/types")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.standard_reports").exists())
            .andExpect(jsonPath("$.custom_reports").exists())
            .andExpect(jsonPath("$.access_info").exists())
            .andExpect(jsonPath("$.standard_reports.revenue").exists())
            .andExpect(jsonPath("$.access_info.roles.ADMIN").value("Full access to all reports"));

        verifyNoInteractions(reportsService);
    }

    @Test
    void testGenerateRevenueReport_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/reports/revenue")
                .param("startDate", LocalDate.now().minusMonths(3).toString())
                .param("endDate", LocalDate.now().toString()))
            .andExpect(status().isUnauthorized());

        verifyNoInteractions(reportsService);
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void testGenerateRevenueReport_Forbidden() throws Exception {
        // Faculty role should not have access to revenue reports
        // Act & Assert
        mockMvc.perform(get("/api/v1/reports/revenue")
                .param("startDate", LocalDate.now().minusMonths(3).toString())
                .param("endDate", LocalDate.now().toString())
                .with(csrf()))
            .andExpect(status().isForbidden());

        verifyNoInteractions(reportsService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGenerateRevenueReport_MissingParameters() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/reports/revenue")
                .with(csrf()))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(reportsService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGenerateRevenueReport_InvalidDateFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/reports/revenue")
                .param("startDate", "invalid-date")
                .param("endDate", LocalDate.now().toString())
                .with(csrf()))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(reportsService);
    }
}