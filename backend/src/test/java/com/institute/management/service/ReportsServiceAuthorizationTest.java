package com.institute.management.service;

import com.institute.management.entity.Employee;
import com.institute.management.security.AuthorizationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class ReportsServiceAuthorizationTest extends AuthorizationTestBase {
    
    @Mock
    private StudentService studentService;
    
    @Mock
    private LeadService leadService;
    
    @Mock
    private BatchService batchService;
    
    @Mock
    private PlacementService placementService;
    
    @Mock
    private CourseService courseService;
    
    @InjectMocks
    private ReportsService reportsService;
    
    private LocalDate startDate;
    private LocalDate endDate;
    
    @BeforeEach
    void setUp() {
        startDate = LocalDate.now().minusMonths(1);
        endDate = LocalDate.now();
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanGenerateRevenueReport() {
        Map<String, Object> result = reportsService.generateRevenueReport(startDate, endDate);
        
        assertNotNull(result);
        assertTrue(result.containsKey("period"));
        assertTrue(result.containsKey("totalRevenue"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotGenerateRevenueReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateRevenueReport(startDate, endDate);
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotGenerateRevenueReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateRevenueReport(startDate, endDate);
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCannotGenerateRevenueReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateRevenueReport(startDate, endDate);
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCannotGenerateRevenueReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateRevenueReport(startDate, endDate);
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanGenerateEnrollmentReport() {
        Map<String, Object> result = reportsService.generateEnrollmentReport(startDate, endDate);
        
        assertNotNull(result);
        assertTrue(result.containsKey("period"));
        assertTrue(result.containsKey("totalEnrollments"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCanGenerateEnrollmentReport() {
        Map<String, Object> result = reportsService.generateEnrollmentReport(startDate, endDate);
        
        assertNotNull(result);
        assertTrue(result.containsKey("period"));
        assertTrue(result.containsKey("totalEnrollments"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotGenerateEnrollmentReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateEnrollmentReport(startDate, endDate);
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotGenerateEnrollmentReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateEnrollmentReport(startDate, endDate);
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCannotGenerateEnrollmentReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateEnrollmentReport(startDate, endDate);
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanGeneratePlacementReport() {
        when(placementService.getPlacementStatistics()).thenReturn(
            new PlacementService.PlacementStatistics(100L, 80L, java.math.BigDecimal.valueOf(50000))
        );
        
        Map<String, Object> result = reportsService.generatePlacementReport(startDate, endDate);
        
        assertNotNull(result);
        assertTrue(result.containsKey("period"));
        assertTrue(result.containsKey("totalPlacements"));
        assertTrue(result.containsKey("placementRate"));
        
        verify(placementService).getPlacementStatistics();
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCanGeneratePlacementReport() {
        when(placementService.getPlacementStatistics()).thenReturn(
            new PlacementService.PlacementStatistics(100L, 80L, java.math.BigDecimal.valueOf(50000))
        );
        
        Map<String, Object> result = reportsService.generatePlacementReport(startDate, endDate);
        
        assertNotNull(result);
        assertTrue(result.containsKey("period"));
        assertTrue(result.containsKey("totalPlacements"));
        
        verify(placementService).getPlacementStatistics();
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotGeneratePlacementReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generatePlacementReport(startDate, endDate);
        });
        
        verify(placementService, never()).getPlacementStatistics();
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotGeneratePlacementReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generatePlacementReport(startDate, endDate);
        });
        
        verify(placementService, never()).getPlacementStatistics();
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCannotGeneratePlacementReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generatePlacementReport(startDate, endDate);
        });
        
        verify(placementService, never()).getPlacementStatistics();
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanGenerateLeadConversionReport() {
        Map<String, Object> result = reportsService.generateLeadConversionReport(startDate, endDate);
        
        assertNotNull(result);
        assertTrue(result.containsKey("period"));
        assertTrue(result.containsKey("totalLeads"));
        assertTrue(result.containsKey("conversionRate"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCanGenerateLeadConversionReport() {
        Map<String, Object> result = reportsService.generateLeadConversionReport(startDate, endDate);
        
        assertNotNull(result);
        assertTrue(result.containsKey("period"));
        assertTrue(result.containsKey("totalLeads"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotGenerateLeadConversionReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateLeadConversionReport(startDate, endDate);
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCannotGenerateLeadConversionReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateLeadConversionReport(startDate, endDate);
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCannotGenerateLeadConversionReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateLeadConversionReport(startDate, endDate);
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanGenerateBatchUtilizationReport() {
        Map<String, Object> result = reportsService.generateBatchUtilizationReport();
        
        assertNotNull(result);
        assertTrue(result.containsKey("totalBatches"));
        assertTrue(result.containsKey("averageUtilization"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCanGenerateBatchUtilizationReport() {
        Map<String, Object> result = reportsService.generateBatchUtilizationReport();
        
        assertNotNull(result);
        assertTrue(result.containsKey("totalBatches"));
        assertTrue(result.containsKey("averageUtilization"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotGenerateBatchUtilizationReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateBatchUtilizationReport();
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotGenerateBatchUtilizationReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateBatchUtilizationReport();
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCannotGenerateBatchUtilizationReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateBatchUtilizationReport();
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanGenerateFacultyPerformanceReport() {
        Map<String, Object> result = reportsService.generateFacultyPerformanceReport(startDate, endDate);
        
        assertNotNull(result);
        assertTrue(result.containsKey("period"));
        assertTrue(result.containsKey("facultyMetrics"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotGenerateFacultyPerformanceReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateFacultyPerformanceReport(startDate, endDate);
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotGenerateFacultyPerformanceReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateFacultyPerformanceReport(startDate, endDate);
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCannotGenerateFacultyPerformanceReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateFacultyPerformanceReport(startDate, endDate);
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCannotGenerateFacultyPerformanceReport() {
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateFacultyPerformanceReport(startDate, endDate);
        });
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanGenerateDashboardSummary() {
        Map<String, Object> result = reportsService.generateDashboardSummary();
        
        assertNotNull(result);
        assertTrue(result.containsKey("totalStudents"));
        assertTrue(result.containsKey("activeBatches"));
        assertTrue(result.containsKey("totalCourses"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCanGenerateDashboardSummary() {
        Map<String, Object> result = reportsService.generateDashboardSummary();
        
        assertNotNull(result);
        assertTrue(result.containsKey("totalStudents"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCanGenerateDashboardSummary() {
        Map<String, Object> result = reportsService.generateDashboardSummary();
        
        assertNotNull(result);
        assertTrue(result.containsKey("totalStudents"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCanGenerateDashboardSummary() {
        Map<String, Object> result = reportsService.generateDashboardSummary();
        
        assertNotNull(result);
        assertTrue(result.containsKey("totalStudents"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCanGenerateDashboardSummary() {
        Map<String, Object> result = reportsService.generateDashboardSummary();
        
        assertNotNull(result);
        assertTrue(result.containsKey("totalStudents"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanGenerateCustomReport() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportType", "enrollment");
        
        Map<String, Object> result = reportsService.generateCustomReport("enrollment", parameters);
        
        assertNotNull(result);
        assertTrue(result.containsKey("reportType"));
        assertTrue(result.containsKey("parameters"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCanGenerateCustomReport() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportType", "leads");
        
        Map<String, Object> result = reportsService.generateCustomReport("leads", parameters);
        
        assertNotNull(result);
        assertTrue(result.containsKey("reportType"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCanGenerateCustomReport() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportType", "batches");
        
        Map<String, Object> result = reportsService.generateCustomReport("batches", parameters);
        
        assertNotNull(result);
        assertTrue(result.containsKey("reportType"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCanGenerateCustomReport() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportType", "placements");
        
        Map<String, Object> result = reportsService.generateCustomReport("placements", parameters);
        
        assertNotNull(result);
        assertTrue(result.containsKey("reportType"));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotGenerateCustomReport() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportType", "enrollment");
        
        assertThrows(AccessDeniedException.class, () -> {
            reportsService.generateCustomReport("enrollment", parameters);
        });
    }
}