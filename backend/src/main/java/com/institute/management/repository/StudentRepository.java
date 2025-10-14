package com.institute.management.repository;

import com.institute.management.entity.Student;
import com.institute.management.entity.Student.StudentStatus;
import com.institute.management.entity.Batch;
import com.institute.management.entity.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    
    // Basic queries by status
    List<Student> findByStatus(StudentStatus status);
    Page<Student> findByStatus(StudentStatus status, Pageable pageable);
    
    // Find by batch
    List<Student> findByBatch(Batch batch);
    Page<Student> findByBatch(Batch batch, Pageable pageable);
    
    // Find by enrollment number (unique identifier)
    Optional<Student> findByEnrollmentNumber(String enrollmentNumber);
    boolean existsByEnrollmentNumber(String enrollmentNumber);
    
    // Find by email (for duplicate checking)
    Optional<Student> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Find by phone (for duplicate checking)
    Optional<Student> findByPhone(String phone);
    boolean existsByPhone(String phone);
    
    // Find by lead (converted students)
    Optional<Student> findByLead(Lead lead);
    List<Student> findByLeadIsNotNull();
    
    // Date range queries
    List<Student> findByEnrollmentDateBetween(LocalDate startDate, LocalDate endDate);
    Page<Student> findByEnrollmentDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    List<Student> findByGraduationDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Batch and status combinations
    List<Student> findByBatchAndStatus(Batch batch, StudentStatus status);
    Page<Student> findByBatchAndStatus(Batch batch, StudentStatus status, Pageable pageable);
    
    // Complex search queries
    @Query("SELECT s FROM Student s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:batchId IS NULL OR s.batch.id = :batchId) AND " +
           "(:courseId IS NULL OR s.batch.course.id = :courseId) AND " +
           "(:searchTerm IS NULL OR LOWER(CONCAT(s.firstName, ' ', s.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "s.phone LIKE CONCAT('%', :searchTerm, '%') OR " +
           "LOWER(s.enrollmentNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Student> findStudentsWithFilters(@Param("status") StudentStatus status,
                                         @Param("batchId") UUID batchId,
                                         @Param("courseId") UUID courseId,
                                         @Param("searchTerm") String searchTerm,
                                         Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(s) FROM Student s WHERE s.status = :status")
    long countByStatus(@Param("status") StudentStatus status);
    
    @Query("SELECT s.batch, COUNT(s) FROM Student s WHERE s.batch IS NOT NULL GROUP BY s.batch")
    List<Object[]> countByBatch();
    
    @Query("SELECT s.batch.course, COUNT(s) FROM Student s WHERE s.batch IS NOT NULL GROUP BY s.batch.course")
    List<Object[]> countByCourse();
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.batch = :batch AND s.status = :status")
    long countByBatchAndStatus(@Param("batch") Batch batch, @Param("status") StudentStatus status);
    
    // Enrollment statistics
    @Query("SELECT COUNT(s) FROM Student s WHERE s.enrollmentDate >= :startDate AND s.enrollmentDate <= :endDate")
    long countEnrollmentsInDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT s.enrollmentDate, COUNT(s) FROM Student s WHERE s.enrollmentDate >= :startDate AND s.enrollmentDate <= :endDate GROUP BY s.enrollmentDate ORDER BY s.enrollmentDate")
    List<Object[]> getEnrollmentTrends(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Graduation statistics
    @Query("SELECT COUNT(s) FROM Student s WHERE s.status = 'GRADUATED' AND s.graduationDate >= :startDate AND s.graduationDate <= :endDate")
    long countGraduationsInDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT s.finalGrade, COUNT(s) FROM Student s WHERE s.status = 'GRADUATED' AND s.finalGrade IS NOT NULL GROUP BY s.finalGrade")
    List<Object[]> getGradeDistribution();
    
    // Placement related queries
    @Query("SELECT s FROM Student s WHERE s.status = 'GRADUATED' AND SIZE(s.placements) = 0")
    List<Student> findUnplacedGraduates();
    
    @Query("SELECT s FROM Student s WHERE s.status = 'GRADUATED' AND EXISTS (SELECT p FROM s.placements p WHERE p.status = 'PLACED')")
    List<Student> findPlacedGraduates();
    
    // Active students by batch
    @Query("SELECT s FROM Student s WHERE s.batch = :batch AND s.status = 'ACTIVE'")
    List<Student> findActiveStudentsByBatch(@Param("batch") Batch batch);
    
    // Students without batch assignment
    @Query("SELECT s FROM Student s WHERE s.batch IS NULL AND s.status = 'ACTIVE'")
    List<Student> findStudentsWithoutBatch();
    
    // Recent enrollments
    @Query("SELECT s FROM Student s ORDER BY s.enrollmentDate DESC")
    Page<Student> findRecentEnrollments(Pageable pageable);
    
    // Students by enrollment year
    @Query("SELECT s FROM Student s WHERE YEAR(s.enrollmentDate) = :year")
    List<Student> findByEnrollmentYear(@Param("year") int year);
    
    // Generate next enrollment number (helper for service layer)
    @Query("SELECT s.enrollmentNumber FROM Student s WHERE s.enrollmentNumber LIKE :prefix% ORDER BY s.enrollmentNumber DESC")
    List<String> findEnrollmentNumbersWithPrefix(@Param("prefix") String prefix);
}