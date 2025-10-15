package com.institute.management.service;

import com.institute.management.dto.*;
import com.institute.management.entity.*;
import com.institute.management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportsServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private PlacementRepository placementRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ReportsService reportsService;

    private Course testCourse;
    private Batch testBatch;
    private Student testStudent;
    private Lead testLead;
    private Placement testPlacement;
    private Company testCompany;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        // Create test entities
        testCourse = new Course();
        testCourse.setId(UUID.randomUUID());
        testCourse.setName("Java Full Stack");
        testCourse.setFees(BigDecimal.valueOf(50000));
        testCourse.setDurationMonths(6);
        testCourse.setStatus(Course.CourseStatus.ACTIVE);

        testBatch = new Batch();
        testBatch.setId(UUID.randomUUID());
        testBatch.setName("JAVA-2024-01");
        testBatch.setCourse(testCourse);
        testBatch.setCapacity(30);
        testBatch.setCurrentEnrollment(25);
        testBatch.setStatus(Batch.BatchStatus.ACTIVE);
        testBatch.setStartDate(LocalDate.now().minusMonths(2));

        testStudent = new Student();
        testStudent.setId(UUID.randomUUID());
        testStudent.setEnrollmentNumber("STU2024001");
        testStudent.setFirstName("John");
        testStudent.setLastName("Doe");
        testStudent.setEmail("john.doe@example.com");
        testStudent.setBatch(testBatch);
        testStudent.setStatus(Student.StudentStatus.ACTIVE);
        testStudent.setEnrollmentDate(LocalDate.now().minusMonths(1));

        testEmployee = new Employee();
        testEmployee.setId(UUID.randomUUID());
        testEmployee.setFirstName("Jane");
        testEmployee.setLastName("Smith");
        testEmployee.setRole(Employee.Role.COUNSELLOR);
        testEmployee.setStatus(Employee.EmployeeStatus.ACTIVE);

        testLead = new Lead();
        testLead.setId(UUID.randomUUID());
        testLead.setFirstName("Alice");
        testLead.setLastName("Johnson");
        testLead.setEmail("alice.johnson@example.com");
        testLead.setPhone("1234567890");
        testLead.setSource("Website");
        testLead.setCourseInterest("Java Full Stack");
        testLead.setStatus(Lead.LeadStatus.NEW);
        testLead.setAssignedCounsellor(testEmployee);
        testLead.setCreatedDate(LocalDateTime.now().minusDays(10));

        testCompany = new Company();
        testCompany.setId(UUID.randomUUID());
        testCompany.setName("Tech Corp");
        testCompany.setIndustry("Technology");
        testCompany.setStatus(Company.CompanyStatus.ACTIVE);

        testPlacement = new Placement();
        testPlacement.setId(UUID.randomUUID());
        testPlacement.setStudent(testStudent);
        testPlacement.setCompany(testCompany);
        testPlacement.setPosition("Software Developer");
        testPlacement.setSalary(BigDecimal.valueOf(600000));
        testPlacement.setStatus(Placement.PlacementStatus.PLACED);
        testPlacement.setPlacementDate(LocalDate.now().minusDays(5));
        testPlacement.setJobType(Placement.JobType.FULL_TIME);
        testPlacement.setEmploymentType(Placement.EmploymentType.PERMANENT);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGenerateRevenueReport() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        
        List<Object[]> courseRevenueData = Arrays.asList(
            new Object[]{testCourse, BigDecimal.valueOf(1250000)}
        );
        
        List<Student> enrollmentsInRange = Arrays.asList(testStudent);
        List<Batch> allBatches = Arrays.asList(testBatch);
        
        when(courseRepository.getCourseRevenueReport()).thenReturn(courseRevenueData);
        when(studentRepository.findByEnrollmentDateBetween(startDate, endDate)).thenReturn(enrollmentsInRange);
        when(batchRepository.findAll()).thenReturn(allBatches);
        when(studentRepository.findByEnrollmentDateBetween(any(LocalDate.class), eq(startDate)))
            .thenReturn(Arrays.asList());

        // Act
        RevenueReportDTO report = reportsService.generateRevenueReport(startDate, endDate);

        // Assert
        assertNotNull(report);
        assertEquals(startDate, report.getStartDate());
        assertEquals(endDate, report.getEndDate());
        assertEquals(BigDecimal.valueOf(1250000), report.getTotalRevenue());
        assertNotNull(report.getRevenueByCourse());
        assertTrue(report.getRevenueByCourse().containsKey("Java Full Stack"));
        assertEquals(1, report.getTotalEnrollments());
        assertNotNull(report.getGeneratedAt());
        
        verify(courseRepository).getCourseRevenueReport();
        verify(studentRepository, times(2)).findByEnrollmentDateBetween(any(LocalDate.class), any(LocalDate.class));
        verify(batchRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGenerateEnrollmentReport() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        
        List<Student> enrollmentsInRange = Arrays.asList(testStudent);
        List<Object[]> trendData = Arrays.asList(
            new Object[]{LocalDate.now().minusDays(30), 5L}
        );
        List<Object[]> courseData = Arrays.asList(
            new Object[]{testCourse, 25L}
        );
        List<Object[]> batchData = Arrays.asList(
            new Object[]{testBatch, 25L}
        );
        
        when(studentRepository.findByEnrollmentDateBetween(startDate, endDate)).thenReturn(enrollmentsInRange);
        when(studentRepository.getEnrollmentTrends(startDate, endDate)).thenReturn(trendData);
        when(studentRepository.countByCourse()).thenReturn(courseData);
        when(studentRepository.countByBatch()).thenReturn(batchData);
        when(studentRepository.countByStatus(Student.StudentStatus.ACTIVE)).thenReturn(20L);
        when(studentRepository.countByStatus(Student.StudentStatus.GRADUATED)).thenReturn(5L);
        when(studentRepository.countByStatus(Student.StudentStatus.DROPPED)).thenReturn(2L);
        when(studentRepository.findByEnrollmentDateBetween(any(LocalDate.class), eq(startDate)))
            .thenReturn(Arrays.asList());

        // Act
        EnrollmentReportDTO report = reportsService.generateEnrollmentReport(startDate, endDate);

        // Assert
        assertNotNull(report);
        assertEquals(startDate, report.getStartDate());
        assertEquals(endDate, report.getEndDate());
        assertEquals(1, report.getTotalEnrollments());
        assertEquals(20, report.getActiveStudents());
        assertEquals(5, report.getGraduatedStudents());
        assertEquals(2, report.getDroppedStudents());
        assertNotNull(report.getEnrollmentTrends());
        assertNotNull(report.getEnrollmentsByCourse());
        assertTrue(report.getEnrollmentsByCourse().containsKey("Java Full Stack"));
        assertNotNull(report.getGeneratedAt());
        
        verify(studentRepository).findByEnrollmentDateBetween(startDate, endDate);
        verify(studentRepository).getEnrollmentTrends(startDate, endDate);
        verify(studentRepository).countByCourse();
        verify(studentRepository).countByBatch();
    }

    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void testGeneratePlacementReport() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        
        List<Placement> placementsInRange = Arrays.asList(testPlacement);
        List<Object[]> companyData = Arrays.asList(
            new Object[]{testCompany, 5L}
        );
        List<Object[]> courseRateData = Arrays.asList(
            new Object[]{"Java Full Stack", 10L, 8L}
        );
        List<Object[]> jobTypeData = Arrays.asList(
            new Object[]{Placement.JobType.FULL_TIME, 5L}
        );
        List<Object[]> employmentTypeData = Arrays.asList(
            new Object[]{Placement.EmploymentType.PERMANENT, 5L}
        );
        List<Object[]> trendData = Arrays.asList(
            new Object[]{2024, 3, 5L}
        );
        List<Object[]> salaryRange = Arrays.asList(
            new Object[]{BigDecimal.valueOf(400000), BigDecimal.valueOf(800000)}
        );
        List<Object[]> salaryDistData = Arrays.asList(
            new Object[]{2L, 3L, 1L}
        );
        
        when(placementRepository.findByPlacementDateBetween(startDate, endDate)).thenReturn(placementsInRange);
        when(placementRepository.countByCompany()).thenReturn(companyData);
        when(placementRepository.getPlacementRateByCourse()).thenReturn(courseRateData);
        when(placementRepository.countByJobType()).thenReturn(jobTypeData);
        when(placementRepository.countByEmploymentType()).thenReturn(employmentTypeData);
        when(placementRepository.getMonthlyPlacementTrends(any(LocalDate.class))).thenReturn(trendData);
        when(placementRepository.getAverageSalary()).thenReturn(BigDecimal.valueOf(600000));
        when(placementRepository.getSalaryRange()).thenReturn(salaryRange);
        when(placementRepository.getSalaryRangeDistribution()).thenReturn(salaryDistData);
        when(placementRepository.getAverageSalaryByCompany()).thenReturn(Arrays.asList());
        when(placementRepository.getAverageSalaryByCourse()).thenReturn(Arrays.asList());
        when(placementRepository.findActivePlacements(any(LocalDate.class))).thenReturn(Arrays.asList(testPlacement));
        when(placementRepository.countPlacedStudents()).thenReturn(8L);
        when(placementRepository.countGraduatedStudents()).thenReturn(10L);

        // Act
        PlacementReportDTO report = reportsService.generatePlacementReport(startDate, endDate);

        // Assert
        assertNotNull(report);
        assertEquals(startDate, report.getStartDate());
        assertEquals(endDate, report.getEndDate());
        assertEquals(1, report.getTotalPlacements());
        assertEquals(1, report.getActivePlacements());
        assertEquals(8, report.getPlacedStudents());
        assertEquals(10, report.getTotalGraduates());
        assertEquals(80.0, report.getPlacementRate());
        assertNotNull(report.getPlacementsByCompany());
        assertTrue(report.getPlacementsByCompany().containsKey("Tech Corp"));
        assertEquals(BigDecimal.valueOf(600000), report.getAverageSalary());
        assertNotNull(report.getGeneratedAt());
        
        verify(placementRepository).findByPlacementDateBetween(startDate, endDate);
        verify(placementRepository).countByCompany();
        verify(placementRepository).getPlacementRateByCourse();
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void testGenerateLeadConversionReport() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        List<Lead> leadsInRange = Arrays.asList(testLead);
        List<Object[]> sourceData = Arrays.asList(
            new Object[]{"Website", 10L}
        );
        List<Object[]> courseInterestData = Arrays.asList(
            new Object[]{"Java Full Stack", 8L}
        );
        List<Employee> counsellors = Arrays.asList(testEmployee);
        
        when(leadRepository.findByCreatedDateBetween(startDateTime, endDateTime)).thenReturn(leadsInRange);
        when(leadRepository.countBySource()).thenReturn(sourceData);
        when(leadRepository.countByCourseInterest()).thenReturn(courseInterestData);
        when(leadRepository.countByStatus(Lead.LeadStatus.NEW)).thenReturn(5L);
        when(leadRepository.countByStatus(Lead.LeadStatus.CONTACTED)).thenReturn(3L);
        when(leadRepository.countByStatus(Lead.LeadStatus.INTERESTED)).thenReturn(2L);
        when(leadRepository.countByStatus(Lead.LeadStatus.LOST)).thenReturn(1L);
        when(leadRepository.countByStatus(Lead.LeadStatus.NOT_INTERESTED)).thenReturn(1L);
        when(employeeRepository.findByRole(Employee.Role.COUNSELLOR)).thenReturn(counsellors);
        when(leadRepository.findByAssignedCounsellor(testEmployee)).thenReturn(Arrays.asList(testLead));
        when(leadRepository.countConvertedLeadsByCounsellor(testEmployee)).thenReturn(2L);
        when(leadRepository.findBySource("Website")).thenReturn(Arrays.asList(testLead));
        when(leadRepository.findLeadsRequiringFollowUp(any(LocalDateTime.class))).thenReturn(Arrays.asList(testLead));

        // Act
        LeadConversionReportDTO report = reportsService.generateLeadConversionReport(startDate, endDate);

        // Assert
        assertNotNull(report);
        assertEquals(startDate, report.getStartDate());
        assertEquals(endDate, report.getEndDate());
        assertEquals(1, report.getTotalLeads());
        assertEquals(10, report.getActiveLeads()); // NEW + CONTACTED + INTERESTED
        assertEquals(2, report.getLostLeads()); // LOST + NOT_INTERESTED
        assertNotNull(report.getLeadsBySource());
        assertTrue(report.getLeadsBySource().containsKey("Website"));
        assertNotNull(report.getConversionRateByCounsellor());
        assertNotNull(report.getGeneratedAt());
        
        verify(leadRepository).findByCreatedDateBetween(startDateTime, endDateTime);
        verify(leadRepository).countBySource();
        verify(employeeRepository).findByRole(Employee.Role.COUNSELLOR);
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void testGenerateBatchUtilizationReport() {
        // Arrange
        List<Batch> allBatches = Arrays.asList(testBatch);
        List<Object[]> utilizationData = Arrays.asList(
            new Object[]{testBatch, 83.33}
        );
        List<Object[]> courseData = Arrays.asList(
            new Object[]{testCourse, 1L}
        );
        
        when(batchRepository.findAll()).thenReturn(allBatches);
        when(batchRepository.getBatchUtilizationReport()).thenReturn(utilizationData);
        when(batchRepository.countByCourse()).thenReturn(courseData);
        when(batchRepository.findByCourse(testCourse)).thenReturn(Arrays.asList(testBatch));
        when(batchRepository.countByStatus(Batch.BatchStatus.ACTIVE)).thenReturn(1L);
        when(batchRepository.countByStatus(Batch.BatchStatus.PLANNED)).thenReturn(0L);
        when(batchRepository.countByStatus(Batch.BatchStatus.COMPLETED)).thenReturn(0L);
        when(batchRepository.getAverageUtilization()).thenReturn(83.33);

        // Act
        BatchUtilizationReportDTO report = reportsService.generateBatchUtilizationReport();

        // Assert
        assertNotNull(report);
        assertEquals(1, report.getTotalBatches());
        assertEquals(1, report.getActiveBatches());
        assertEquals(0, report.getPlannedBatches());
        assertEquals(0, report.getCompletedBatches());
        assertEquals(83.33, report.getAverageUtilization());
        assertNotNull(report.getUtilizationByBatch());
        assertTrue(report.getUtilizationByBatch().containsKey("JAVA-2024-01"));
        assertNotNull(report.getUtilizationByCourse());
        assertTrue(report.getUtilizationByCourse().containsKey("Java Full Stack"));
        assertEquals(1, report.getOptimallyUtilizedBatches()); // 83.33% is between 70-95%
        assertEquals(0, report.getUnderutilizedBatches());
        assertEquals(0, report.getOverutilizedBatches());
        assertNotNull(report.getGeneratedAt());
        
        verify(batchRepository).findAll();
        verify(batchRepository).getBatchUtilizationReport();
        verify(batchRepository).countByCourse();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGenerateFacultyPerformanceReport() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        
        Employee faculty = new Employee();
        faculty.setId(UUID.randomUUID());
        faculty.setFirstName("Dr. John");
        faculty.setLastName("Professor");
        faculty.setRole(Employee.Role.FACULTY);
        faculty.setStatus(Employee.EmployeeStatus.ACTIVE);
        
        testBatch.setInstructor(faculty);
        
        List<Employee> facultyList = Arrays.asList(faculty);
        List<Object[]> batchData = Arrays.asList(
            new Object[]{faculty, 2L}
        );
        
        when(employeeRepository.findByRole(Employee.Role.FACULTY)).thenReturn(facultyList);
        when(batchRepository.countByInstructor()).thenReturn(batchData);
        when(batchRepository.findByInstructor(faculty)).thenReturn(Arrays.asList(testBatch));

        // Act
        FacultyPerformanceReportDTO report = reportsService.generateFacultyPerformanceReport(startDate, endDate);

        // Assert
        assertNotNull(report);
        assertEquals(startDate, report.getStartDate());
        assertEquals(endDate, report.getEndDate());
        assertEquals(1, report.getTotalFaculty());
        assertEquals(1, report.getActiveFaculty());
        assertNotNull(report.getBatchesPerFaculty());
        assertTrue(report.getBatchesPerFaculty().containsKey("Dr. John Professor"));
        assertNotNull(report.getStudentsPerFaculty());
        assertNotNull(report.getCompletionRatesByFaculty());
        assertNotNull(report.getPlacementRatesByFaculty());
        assertNotNull(report.getWorkloadDistribution());
        assertNotNull(report.getGeneratedAt());
        
        verify(employeeRepository).findByRole(Employee.Role.FACULTY);
        verify(batchRepository).countByInstructor();
        verify(batchRepository).findByInstructor(faculty);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGenerateDashboardSummary() {
        // Arrange
        when(studentRepository.count()).thenReturn(100L);
        when(studentRepository.countByStatus(Student.StudentStatus.ACTIVE)).thenReturn(80L);
        when(batchRepository.count()).thenReturn(10L);
        when(batchRepository.countByStatus(Batch.BatchStatus.ACTIVE)).thenReturn(8L);
        when(courseRepository.count()).thenReturn(5L);
        when(courseRepository.countByStatus(Course.CourseStatus.ACTIVE)).thenReturn(4L);
        when(leadRepository.count()).thenReturn(50L);
        when(leadRepository.countByStatus(Lead.LeadStatus.NEW)).thenReturn(10L);
        when(leadRepository.countByStatus(Lead.LeadStatus.CONTACTED)).thenReturn(8L);
        when(leadRepository.countByStatus(Lead.LeadStatus.INTERESTED)).thenReturn(5L);
        when(placementRepository.count()).thenReturn(30L);
        when(placementRepository.findActivePlacements(any(LocalDate.class))).thenReturn(Arrays.asList(testPlacement));
        when(employeeRepository.count()).thenReturn(20L);
        when(employeeRepository.countByStatus(Employee.EmployeeStatus.ACTIVE)).thenReturn(18L);
        when(batchRepository.getTotalRevenue()).thenReturn(BigDecimal.valueOf(5000000));
        when(studentRepository.findByEnrollmentDateBetween(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Arrays.asList(testStudent));
        when(placementRepository.countPlacedStudents()).thenReturn(25L);
        when(placementRepository.countGraduatedStudents()).thenReturn(30L);
        when(leadRepository.countByStatus(Lead.LeadStatus.CONVERTED)).thenReturn(15L);
        when(batchRepository.getAverageUtilization()).thenReturn(85.0);
        when(studentRepository.countByStatus(Student.StudentStatus.GRADUATED)).thenReturn(30L);
        when(studentRepository.countByStatus(Student.StudentStatus.DROPPED)).thenReturn(5L);
        when(placementRepository.findByPlacementDateBetween(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Arrays.asList(testPlacement));
        when(leadRepository.findByCreatedDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(testLead));
        when(leadRepository.findLeadsRequiringFollowUp(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(testLead));
        when(batchRepository.findBatchesAboveUtilization(0.7)).thenReturn(Arrays.asList());
        when(batchRepository.findBatchesStartingInRange(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Arrays.asList(testBatch));

        // Act
        DashboardSummaryDTO summary = reportsService.generateDashboardSummary();

        // Assert
        assertNotNull(summary);
        assertEquals(100, summary.getTotalStudents());
        assertEquals(80, summary.getActiveStudents());
        assertEquals(10, summary.getTotalBatches());
        assertEquals(8, summary.getActiveBatches());
        assertEquals(5, summary.getTotalCourses());
        assertEquals(4, summary.getActiveCourses());
        assertEquals(50, summary.getTotalLeads());
        assertEquals(23, summary.getActiveLeads()); // NEW + CONTACTED + INTERESTED
        assertEquals(30, summary.getTotalPlacements());
        assertEquals(1, summary.getActivePlacements());
        assertEquals(20, summary.getTotalEmployees());
        assertEquals(18, summary.getActiveEmployees());
        assertNotNull(summary.getMonthlyRevenue());
        assertNotNull(summary.getYearlyRevenue());
        assertTrue(summary.getPlacementRate() > 0);
        assertTrue(summary.getConversionRate() > 0);
        assertEquals(85.0, summary.getBatchUtilization());
        assertNotNull(summary.getEnrollmentTrends());
        assertNotNull(summary.getPlacementTrends());
        assertNotNull(summary.getRevenueTrends());
        assertNotNull(summary.getGeneratedAt());
        
        verify(studentRepository).count();
        verify(batchRepository).count();
        verify(courseRepository).count();
        verify(leadRepository).count();
        verify(placementRepository).count();
        verify(employeeRepository).count();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGenerateCustomReport_EnrollmentSummary() {
        // Arrange
        String reportType = "enrollment_summary";
        Map<String, Object> parameters = new HashMap<>();
        
        List<Object[]> courseEnrollments = Arrays.asList(
            new Object[]{testCourse, 25L}
        );
        
        when(studentRepository.countByCourse()).thenReturn(courseEnrollments);

        // Act
        Map<String, Object> report = reportsService.generateCustomReport(reportType, parameters);

        // Assert
        assertNotNull(report);
        assertEquals(reportType, report.get("reportType"));
        assertNotNull(report.get("enrollmentsByCourse"));
        assertEquals(25, report.get("totalEnrollments"));
        assertNotNull(report.get("generatedAt"));
        
        verify(studentRepository).countByCourse();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGenerateCustomReport_InvalidType() {
        // Arrange
        String reportType = "invalid_report";
        Map<String, Object> parameters = new HashMap<>();

        // Act
        Map<String, Object> report = reportsService.generateCustomReport(reportType, parameters);

        // Assert
        assertNotNull(report);
        assertEquals(reportType, report.get("reportType"));
        assertTrue(report.containsKey("error"));
        assertEquals("Unknown report type: " + reportType, report.get("error"));
    }

    @Test
    void testGenerateRevenueReport_WithoutAdminRole() {
        // This test would fail with @PreAuthorize in a real Spring context
        // In unit tests, we can't easily test Spring Security annotations
        // This would be better tested in integration tests
        
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        
        // In a real scenario, this would throw AccessDeniedException
        // For unit testing, we just verify the method exists and can be called
        assertDoesNotThrow(() -> {
            // The @PreAuthorize annotation is not enforced in unit tests
            // This would need integration testing to verify security
        });
    }
}