package com.institute.management.repository;

import com.institute.management.entity.*;
import com.institute.management.entity.Student.StudentStatus;
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
class StudentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudentRepository studentRepository;

    private Course course;
    private Batch batch1;
    private Batch batch2;
    private Student student1;
    private Student student2;
    private Student student3;
    private Lead lead;

    @BeforeEach
    void setUp() {
        // Create test course
        course = new Course();
        course.setName("Java Development");
        course.setDescription("Full Stack Java Development Course");
        course.setDurationMonths(6);
        course.setFees(new BigDecimal("50000"));
        entityManager.persistAndFlush(course);

        // Create test batches
        batch1 = new Batch();
        batch1.setName("JAVA-2024-01");
        batch1.setCourse(course);
        batch1.setStartDate(LocalDate.now().minusMonths(2));
        batch1.setCapacity(30);
        batch1.setCurrentEnrollment(2);
        entityManager.persistAndFlush(batch1);

        batch2 = new Batch();
        batch2.setName("JAVA-2024-02");
        batch2.setCourse(course);
        batch2.setStartDate(LocalDate.now().plusMonths(1));
        batch2.setCapacity(25);
        batch2.setCurrentEnrollment(1);
        entityManager.persistAndFlush(batch2);

        // Create test lead
        lead = new Lead();
        lead.setFirstName("John");
        lead.setLastName("Doe");
        lead.setEmail("john.doe@email.com");
        lead.setPhone("1234567890");
        lead.setStatus(Lead.LeadStatus.CONVERTED);
        entityManager.persistAndFlush(lead);

        // Create test students
        student1 = new Student();
        student1.setEnrollmentNumber("STU2024001");
        student1.setFirstName("Alice");
        student1.setLastName("Smith");
        student1.setEmail("alice.smith@email.com");
        student1.setPhone("1111111111");
        student1.setEnrollmentDate(LocalDate.now().minusMonths(2));
        student1.setBatch(batch1);
        student1.setStatus(StudentStatus.ACTIVE);
        student1.setLead(lead);
        entityManager.persistAndFlush(student1);

        student2 = new Student();
        student2.setEnrollmentNumber("STU2024002");
        student2.setFirstName("Bob");
        student2.setLastName("Johnson");
        student2.setEmail("bob.johnson@email.com");
        student2.setPhone("2222222222");
        student2.setEnrollmentDate(LocalDate.now().minusMonths(1));
        student2.setBatch(batch1);
        student2.setStatus(StudentStatus.GRADUATED);
        student2.setGraduationDate(LocalDate.now().minusDays(10));
        student2.setFinalGrade("A");
        entityManager.persistAndFlush(student2);

        student3 = new Student();
        student3.setEnrollmentNumber("STU2024003");
        student3.setFirstName("Charlie");
        student3.setLastName("Brown");
        student3.setEmail("charlie.brown@email.com");
        student3.setPhone("3333333333");
        student3.setEnrollmentDate(LocalDate.now().minusDays(15));
        student3.setBatch(batch2);
        student3.setStatus(StudentStatus.ACTIVE);
        entityManager.persistAndFlush(student3);
    }

    @Test
    void testFindByStatus() {
        List<Student> activeStudents = studentRepository.findByStatus(StudentStatus.ACTIVE);
        assertThat(activeStudents).hasSize(2); // student1 and student3
        
        List<Student> graduatedStudents = studentRepository.findByStatus(StudentStatus.GRADUATED);
        assertThat(graduatedStudents).hasSize(1); // student2
        assertThat(graduatedStudents.get(0).getFirstName()).isEqualTo("Bob");
    }

    @Test
    void testFindByStatusWithPagination() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Student> activeStudentsPage = studentRepository.findByStatus(StudentStatus.ACTIVE, pageable);
        
        assertThat(activeStudentsPage.getContent()).hasSize(1);
        assertThat(activeStudentsPage.getTotalElements()).isEqualTo(2);
    }

    @Test
    void testFindByBatch() {
        List<Student> batch1Students = studentRepository.findByBatch(batch1);
        assertThat(batch1Students).hasSize(2); // student1 and student2
        
        List<Student> batch2Students = studentRepository.findByBatch(batch2);
        assertThat(batch2Students).hasSize(1); // student3
    }

    @Test
    void testFindByEnrollmentNumber() {
        Optional<Student> student = studentRepository.findByEnrollmentNumber("STU2024001");
        assertThat(student).isPresent();
        assertThat(student.get().getFirstName()).isEqualTo("Alice");
    }

    @Test
    void testExistsByEnrollmentNumber() {
        assertThat(studentRepository.existsByEnrollmentNumber("STU2024001")).isTrue();
        assertThat(studentRepository.existsByEnrollmentNumber("STU2024999")).isFalse();
    }

    @Test
    void testFindByEmail() {
        Optional<Student> student = studentRepository.findByEmail("alice.smith@email.com");
        assertThat(student).isPresent();
        assertThat(student.get().getEnrollmentNumber()).isEqualTo("STU2024001");
    }

    @Test
    void testExistsByEmail() {
        assertThat(studentRepository.existsByEmail("alice.smith@email.com")).isTrue();
        assertThat(studentRepository.existsByEmail("nonexistent@email.com")).isFalse();
    }

    @Test
    void testFindByPhone() {
        Optional<Student> student = studentRepository.findByPhone("1111111111");
        assertThat(student).isPresent();
        assertThat(student.get().getFirstName()).isEqualTo("Alice");
    }

    @Test
    void testFindByLead() {
        Optional<Student> student = studentRepository.findByLead(lead);
        assertThat(student).isPresent();
        assertThat(student.get().getFirstName()).isEqualTo("Alice");
    }

    @Test
    void testFindByLeadIsNotNull() {
        List<Student> convertedStudents = studentRepository.findByLeadIsNotNull();
        assertThat(convertedStudents).hasSize(1); // Only student1 has a lead
        assertThat(convertedStudents.get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void testFindByEnrollmentDateBetween() {
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        
        List<Student> studentsInRange = studentRepository.findByEnrollmentDateBetween(startDate, endDate);
        assertThat(studentsInRange).hasSize(3); // All students enrolled in this range
    }

    @Test
    void testFindByGraduationDateBetween() {
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        
        List<Student> graduatedInRange = studentRepository.findByGraduationDateBetween(startDate, endDate);
        assertThat(graduatedInRange).hasSize(1); // Only student2 graduated in this range
        assertThat(graduatedInRange.get(0).getFirstName()).isEqualTo("Bob");
    }

    @Test
    void testFindByBatchAndStatus() {
        List<Student> activeBatch1Students = studentRepository.findByBatchAndStatus(batch1, StudentStatus.ACTIVE);
        assertThat(activeBatch1Students).hasSize(1); // Only student1 is active in batch1
        assertThat(activeBatch1Students.get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void testFindStudentsWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        
        // Test with status filter
        Page<Student> activeStudents = studentRepository.findStudentsWithFilters(
            StudentStatus.ACTIVE, null, null, null, pageable);
        assertThat(activeStudents.getContent()).hasSize(2);
        
        // Test with batch filter
        Page<Student> batch1Students = studentRepository.findStudentsWithFilters(
            null, batch1.getId(), null, null, pageable);
        assertThat(batch1Students.getContent()).hasSize(2);
        
        // Test with search term
        Page<Student> searchResults = studentRepository.findStudentsWithFilters(
            null, null, null, "Alice", pageable);
        assertThat(searchResults.getContent()).hasSize(1);
        assertThat(searchResults.getContent().get(0).getFirstName()).isEqualTo("Alice");
        
        // Test with course filter
        Page<Student> courseStudents = studentRepository.findStudentsWithFilters(
            null, null, course.getId(), null, pageable);
        assertThat(courseStudents.getContent()).hasSize(3); // All students are in the same course
    }

    @Test
    void testCountByStatus() {
        long activeCount = studentRepository.countByStatus(StudentStatus.ACTIVE);
        assertThat(activeCount).isEqualTo(2);
        
        long graduatedCount = studentRepository.countByStatus(StudentStatus.GRADUATED);
        assertThat(graduatedCount).isEqualTo(1);
    }

    @Test
    void testCountByBatch() {
        List<Object[]> batchStats = studentRepository.countByBatch();
        assertThat(batchStats).hasSize(2); // Two batches
        
        // Find batch1 stats
        Object[] batch1Stats = batchStats.stream()
            .filter(stat -> batch1.equals(stat[0]))
            .findFirst()
            .orElse(null);
        assertThat(batch1Stats).isNotNull();
        assertThat((Long) batch1Stats[1]).isEqualTo(2L);
    }

    @Test
    void testCountByCourse() {
        List<Object[]> courseStats = studentRepository.countByCourse();
        assertThat(courseStats).hasSize(1); // One course
        assertThat((Long) courseStats.get(0)[1]).isEqualTo(3L); // All 3 students
    }

    @Test
    void testCountByBatchAndStatus() {
        long activeBatch1Count = studentRepository.countByBatchAndStatus(batch1, StudentStatus.ACTIVE);
        assertThat(activeBatch1Count).isEqualTo(1);
        
        long graduatedBatch1Count = studentRepository.countByBatchAndStatus(batch1, StudentStatus.GRADUATED);
        assertThat(graduatedBatch1Count).isEqualTo(1);
    }

    @Test
    void testCountEnrollmentsInDateRange() {
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        
        long enrollmentCount = studentRepository.countEnrollmentsInDateRange(startDate, endDate);
        assertThat(enrollmentCount).isEqualTo(3);
    }

    @Test
    void testGetEnrollmentTrends() {
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        
        List<Object[]> trends = studentRepository.getEnrollmentTrends(startDate, endDate);
        assertThat(trends).isNotEmpty();
    }

    @Test
    void testCountGraduationsInDateRange() {
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        
        long graduationCount = studentRepository.countGraduationsInDateRange(startDate, endDate);
        assertThat(graduationCount).isEqualTo(1);
    }

    @Test
    void testGetGradeDistribution() {
        List<Object[]> gradeStats = studentRepository.getGradeDistribution();
        assertThat(gradeStats).hasSize(1); // Only one grade "A"
        assertThat(gradeStats.get(0)[0]).isEqualTo("A");
        assertThat((Long) gradeStats.get(0)[1]).isEqualTo(1L);
    }

    @Test
    void testFindUnplacedGraduates() {
        List<Student> unplacedGraduates = studentRepository.findUnplacedGraduates();
        assertThat(unplacedGraduates).hasSize(1); // student2 is graduated but has no placements
        assertThat(unplacedGraduates.get(0).getFirstName()).isEqualTo("Bob");
    }

    @Test
    void testFindActiveStudentsByBatch() {
        List<Student> activeStudents = studentRepository.findActiveStudentsByBatch(batch1);
        assertThat(activeStudents).hasSize(1); // Only student1 is active in batch1
        assertThat(activeStudents.get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void testFindStudentsWithoutBatch() {
        // Create a student without batch
        Student noBatchStudent = new Student();
        noBatchStudent.setEnrollmentNumber("STU2024004");
        noBatchStudent.setFirstName("David");
        noBatchStudent.setLastName("Wilson");
        noBatchStudent.setEmail("david.wilson@email.com");
        noBatchStudent.setPhone("4444444444");
        noBatchStudent.setEnrollmentDate(LocalDate.now());
        noBatchStudent.setStatus(StudentStatus.ACTIVE);
        entityManager.persistAndFlush(noBatchStudent);
        
        List<Student> studentsWithoutBatch = studentRepository.findStudentsWithoutBatch();
        assertThat(studentsWithoutBatch).hasSize(1);
        assertThat(studentsWithoutBatch.get(0).getFirstName()).isEqualTo("David");
    }

    @Test
    void testFindRecentEnrollments() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Student> recentEnrollments = studentRepository.findRecentEnrollments(pageable);
        
        assertThat(recentEnrollments.getContent()).hasSize(2);
        // Should be ordered by enrollment date descending
    }

    @Test
    void testFindByEnrollmentYear() {
        int currentYear = LocalDate.now().getYear();
        List<Student> studentsThisYear = studentRepository.findByEnrollmentYear(currentYear);
        assertThat(studentsThisYear).hasSize(3); // All students enrolled this year
    }

    @Test
    void testFindEnrollmentNumbersWithPrefix() {
        List<String> enrollmentNumbers = studentRepository.findEnrollmentNumbersWithPrefix("STU2024");
        assertThat(enrollmentNumbers).hasSize(3);
        assertThat(enrollmentNumbers).contains("STU2024001", "STU2024002", "STU2024003");
    }
}