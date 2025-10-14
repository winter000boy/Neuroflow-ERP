package com.institute.management.repository;

import com.institute.management.entity.*;
import com.institute.management.entity.Batch.BatchStatus;
import com.institute.management.entity.Course.CourseStatus;
import com.institute.management.entity.Employee.EmployeeRole;
import com.institute.management.entity.Employee.EmployeeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BatchRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BatchRepository batchRepository;

    private Course course1;
    private Course course2;
    private Employee instructor;
    private Batch batch1;
    private Batch batch2;
    private Batch batch3;

    @BeforeEach
    void setUp() {
        // Create test courses
        course1 = new Course();
        course1.setName("Java Development");
        course1.setDescription("Full Stack Java Development Course");
        course1.setDurationMonths(6);
        course1.setFees(new BigDecimal("50000"));
        course1.setStatus(CourseStatus.ACTIVE);
        entityManager.persistAndFlush(course1);

        course2 = new Course();
        course2.setName("Python Development");
        course2.setDescription("Python Web Development Course");
        course2.setDurationMonths(4);
        course2.setFees(new BigDecimal("40000"));
        course2.setStatus(CourseStatus.ACTIVE);
        entityManager.persistAndFlush(course2);

        // Create test instructor
        instructor = new Employee();
        instructor.setEmployeeCode("EMP001");
        instructor.setFirstName("John");
        instructor.setLastName("Instructor");
        instructor.setEmail("john.instructor@institute.com");
        instructor.setRole(EmployeeRole.FACULTY);
        instructor.setStatus(EmployeeStatus.ACTIVE);
        instructor.setHireDate(LocalDate.now().minusYears(2));
        entityManager.persistAndFlush(instructor);

        // Create test batches
        batch1 = new Batch();
        batch1.setName("JAVA-2024-01");
        batch1.setCourse(course1);
        batch1.setStartDate(LocalDate.now().minusMonths(1));
        batch1.setEndDate(LocalDate.now().plusMonths(5));
        batch1.setCapacity(30);
        batch1.setCurrentEnrollment(25);
        batch1.setStatus(BatchStatus.ACTIVE);
        batch1.setInstructor(instructor);
        entityManager.persistAndFlush(batch1);

        batch2 = new Batch();
        batch2.setName("JAVA-2024-02");
        batch2.setCourse(course1);
        batch2.setStartDate(LocalDate.now().plusMonths(1));
        batch2.setEndDate(LocalDate.now().plusMonths(7));
        batch2.setCapacity(25);
        batch2.setCurrentEnrollment(15);
        batch2.setStatus(BatchStatus.PLANNED);
        batch2.setInstructor(instructor);
        entityManager.persistAndFlush(batch2);

        batch3 = new Batch();
        batch3.setName("PYTHON-2024-01");
        batch3.setCourse(course2);
        batch3.setStartDate(LocalDate.now().minusMonths(2));
        batch3.setEndDate(LocalDate.now().plusMonths(2));
        batch3.setCapacity(20);
        batch3.setCurrentEnrollment(20);
        batch3.setStatus(BatchStatus.ACTIVE);
        entityManager.persistAndFlush(batch3);
    }

    @Test
    void testFindByStatus() {
        List<Batch> activeBatches = batchRepository.findByStatus(BatchStatus.ACTIVE);
        assertThat(activeBatches).hasSize(2); // batch1 and batch3
        
        List<Batch> plannedBatches = batchRepository.findByStatus(BatchStatus.PLANNED);
        assertThat(plannedBatches).hasSize(1); // batch2
        assertThat(plannedBatches.get(0).getName()).isEqualTo("JAVA-2024-02");
    }

    @Test
    void testFindByStatusWithPagination() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Batch> activeBatchesPage = batchRepository.findByStatus(BatchStatus.ACTIVE, pageable);
        
        assertThat(activeBatchesPage.getContent()).hasSize(1);
        assertThat(activeBatchesPage.getTotalElements()).isEqualTo(2);
    }

    @Test
    void testFindByCourse() {
        List<Batch> javaBatches = batchRepository.findByCourse(course1);
        assertThat(javaBatches).hasSize(2); // batch1 and batch2
        
        List<Batch> pythonBatches = batchRepository.findByCourse(course2);
        assertThat(pythonBatches).hasSize(1); // batch3
    }

    @Test
    void testFindByInstructor() {
        List<Batch> instructorBatches = batchRepository.findByInstructor(instructor);
        assertThat(instructorBatches).hasSize(2); // batch1 and batch2 (batch3 has no instructor)
    }

    @Test
    void testFindByName() {
        Optional<Batch> batch = batchRepository.findByName("JAVA-2024-01");
        assertThat(batch).isPresent();
        assertThat(batch.get().getCourse().getName()).isEqualTo("Java Development");
    }

    @Test
    void testExistsByName() {
        assertThat(batchRepository.existsByName("JAVA-2024-01")).isTrue();
        assertThat(batchRepository.existsByName("NONEXISTENT-BATCH")).isFalse();
    }

    @Test
    void testFindByStartDateBetween() {
        LocalDate startDate = LocalDate.now().minusMonths(2);
        LocalDate endDate = LocalDate.now().plusMonths(2);
        
        List<Batch> batchesInRange = batchRepository.findByStartDateBetween(startDate, endDate);
        assertThat(batchesInRange).hasSize(3); // All batches start within this range
    }

    @Test
    void testFindByEndDateBetween() {
        LocalDate startDate = LocalDate.now().plusMonths(1);
        LocalDate endDate = LocalDate.now().plusMonths(8);
        
        List<Batch> batchesEndingInRange = batchRepository.findByEndDateBetween(startDate, endDate);
        assertThat(batchesEndingInRange).hasSize(3); // All batches end within this range
    }

    @Test
    void testFindCurrentBatches() {
        LocalDate currentDate = LocalDate.now();
        List<Batch> currentBatches = batchRepository.findCurrentBatches(currentDate);
        
        assertThat(currentBatches).hasSize(2); // batch1 and batch3 are currently active
    }

    @Test
    void testFindUpcomingBatches() {
        LocalDate currentDate = LocalDate.now();
        List<Batch> upcomingBatches = batchRepository.findUpcomingBatches(currentDate);
        
        assertThat(upcomingBatches).hasSize(1); // batch2 starts in the future
        assertThat(upcomingBatches.get(0).getName()).isEqualTo("JAVA-2024-02");
    }

    @Test
    void testFindBatchesWithAvailableSlots() {
        List<Batch> availableBatches = batchRepository.findBatchesWithAvailableSlots();
        assertThat(availableBatches).hasSize(2); // batch1 and batch2 have available slots
    }

    @Test
    void testFindFullBatches() {
        List<Batch> fullBatches = batchRepository.findFullBatches();
        assertThat(fullBatches).hasSize(1); // batch3 is full (20/20)
        assertThat(fullBatches.get(0).getName()).isEqualTo("PYTHON-2024-01");
    }

    @Test
    void testFindBatchesAboveUtilization() {
        double utilizationThreshold = 0.8; // 80%
        List<Batch> highUtilizationBatches = batchRepository.findBatchesAboveUtilization(utilizationThreshold);
        
        assertThat(highUtilizationBatches).hasSize(2); // batch1 (83.3%) and batch3 (100%)
    }

    @Test
    void testFindBatchesWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        
        // Test with status filter
        Page<Batch> activeBatches = batchRepository.findBatchesWithFilters(
            BatchStatus.ACTIVE, null, null, null, null, pageable);
        assertThat(activeBatches.getContent()).hasSize(2);
        
        // Test with course filter
        Page<Batch> javaBatches = batchRepository.findBatchesWithFilters(
            null, course1.getId(), null, null, null, pageable);
        assertThat(javaBatches.getContent()).hasSize(2);
        
        // Test with instructor filter
        Page<Batch> instructorBatches = batchRepository.findBatchesWithFilters(
            null, null, instructor.getId(), null, null, pageable);
        assertThat(instructorBatches.getContent()).hasSize(2);
        
        // Test with available slots filter
        Page<Batch> availableBatches = batchRepository.findBatchesWithFilters(
            null, null, null, true, null, pageable);
        assertThat(availableBatches.getContent()).hasSize(2);
        
        // Test with search term
        Page<Batch> searchResults = batchRepository.findBatchesWithFilters(
            null, null, null, null, "JAVA", pageable);
        assertThat(searchResults.getContent()).hasSize(2);
    }

    @Test
    void testCountByStatus() {
        long activeCount = batchRepository.countByStatus(BatchStatus.ACTIVE);
        assertThat(activeCount).isEqualTo(2);
        
        long plannedCount = batchRepository.countByStatus(BatchStatus.PLANNED);
        assertThat(plannedCount).isEqualTo(1);
    }

    @Test
    void testCountByCourse() {
        List<Object[]> courseStats = batchRepository.countByCourse();
        assertThat(courseStats).hasSize(2); // Two courses
        
        // Find Java course stats
        Object[] javaStats = courseStats.stream()
            .filter(stat -> course1.equals(stat[0]))
            .findFirst()
            .orElse(null);
        assertThat(javaStats).isNotNull();
        assertThat((Long) javaStats[1]).isEqualTo(2L);
    }

    @Test
    void testCountByInstructor() {
        List<Object[]> instructorStats = batchRepository.countByInstructor();
        assertThat(instructorStats).hasSize(1); // One instructor
        assertThat((Long) instructorStats.get(0)[1]).isEqualTo(2L);
    }

    @Test
    void testGetAverageUtilization() {
        Double avgUtilization = batchRepository.getAverageUtilization();
        assertThat(avgUtilization).isNotNull();
        // batch1: 83.33%, batch3: 100% -> average should be around 91.67%
        assertThat(avgUtilization).isGreaterThan(90.0);
    }

    @Test
    void testGetBatchUtilizationReport() {
        List<Object[]> utilizationReport = batchRepository.getBatchUtilizationReport();
        assertThat(utilizationReport).hasSize(2); // Only active batches
        
        // Should be ordered by utilization descending
        Object[] firstBatch = utilizationReport.get(0);
        assertThat((Double) firstBatch[1]).isEqualTo(100.0); // batch3 should be first
    }

    @Test
    void testGetTotalRevenue() {
        Double totalRevenue = batchRepository.getTotalRevenue();
        assertThat(totalRevenue).isNotNull();
        // batch1: 25 * 50000 = 1,250,000
        // batch3: 20 * 40000 = 800,000
        // Total: 2,050,000
        assertThat(totalRevenue).isEqualTo(2050000.0);
    }

    @Test
    void testGetRevenueByCourse() {
        List<Object[]> revenueStats = batchRepository.getRevenueByCourse();
        assertThat(revenueStats).hasSize(2); // Two courses
        
        // Find Java course revenue
        Object[] javaRevenue = revenueStats.stream()
            .filter(stat -> course1.equals(stat[0]))
            .findFirst()
            .orElse(null);
        assertThat(javaRevenue).isNotNull();
        assertThat(((BigDecimal) javaRevenue[1]).doubleValue()).isEqualTo(1250000.0);
    }

    @Test
    void testFindConflictingBatches() {
        LocalDate startDate = LocalDate.now().minusDays(15);
        LocalDate endDate = LocalDate.now().plusDays(15);
        
        List<Batch> conflictingBatches = batchRepository.findConflictingBatches(instructor, startDate, endDate);
        assertThat(conflictingBatches).hasSize(1); // batch1 conflicts with this date range
    }

    @Test
    void testFindByCourseAndStatus() {
        List<Batch> activeJavaBatches = batchRepository.findByCourseAndStatus(course1, BatchStatus.ACTIVE);
        assertThat(activeJavaBatches).hasSize(1); // batch1
        
        List<Batch> plannedJavaBatches = batchRepository.findByCourseAndStatus(course1, BatchStatus.PLANNED);
        assertThat(plannedJavaBatches).hasSize(1); // batch2
    }

    @Test
    void testFindBatchesStartingInRange() {
        LocalDate startDate = LocalDate.now().minusDays(15);
        LocalDate endDate = LocalDate.now().plusDays(45);
        
        List<Batch> batchesStarting = batchRepository.findBatchesStartingInRange(startDate, endDate);
        assertThat(batchesStarting).hasSize(2); // batch1 and batch2
    }

    @Test
    void testFindBatchesEndingInRange() {
        LocalDate startDate = LocalDate.now().plusMonths(1);
        LocalDate endDate = LocalDate.now().plusMonths(8);
        
        List<Batch> batchesEnding = batchRepository.findBatchesEndingInRange(startDate, endDate);
        assertThat(batchesEnding).hasSize(3); // All batches end in this range
    }

    @Test
    void testFindBatchesWithoutInstructor() {
        List<Batch> batchesWithoutInstructor = batchRepository.findBatchesWithoutInstructor();
        assertThat(batchesWithoutInstructor).hasSize(1); // batch3 has no instructor
        assertThat(batchesWithoutInstructor.get(0).getName()).isEqualTo("PYTHON-2024-01");
    }

    @Test
    void testFindRecentBatches() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Batch> recentBatches = batchRepository.findRecentBatches(pageable);
        
        assertThat(recentBatches.getContent()).hasSize(2);
        // Should be ordered by creation date descending
    }
}