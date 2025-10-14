package com.institute.management.repository;

import com.institute.management.entity.Batch;
import com.institute.management.entity.Batch.BatchStatus;
import com.institute.management.entity.Course;
import com.institute.management.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BatchRepository extends JpaRepository<Batch, UUID> {
    
    // Basic queries by status
    List<Batch> findByStatus(BatchStatus status);
    Page<Batch> findByStatus(BatchStatus status, Pageable pageable);
    
    // Find by course
    List<Batch> findByCourse(Course course);
    Page<Batch> findByCourse(Course course, Pageable pageable);
    
    // Find by instructor
    List<Batch> findByInstructor(Employee instructor);
    Page<Batch> findByInstructor(Employee instructor, Pageable pageable);
    
    // Find by name (for duplicate checking)
    Optional<Batch> findByName(String name);
    boolean existsByName(String name);
    
    // Date range queries
    List<Batch> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    Page<Batch> findByStartDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    List<Batch> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Current and upcoming batches
    @Query("SELECT b FROM Batch b WHERE b.startDate <= :currentDate AND (b.endDate IS NULL OR b.endDate >= :currentDate) AND b.status = 'ACTIVE'")
    List<Batch> findCurrentBatches(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT b FROM Batch b WHERE b.startDate > :currentDate AND b.status IN ('PLANNED', 'ACTIVE')")
    List<Batch> findUpcomingBatches(@Param("currentDate") LocalDate currentDate);
    
    // Capacity related queries
    @Query("SELECT b FROM Batch b WHERE b.currentEnrollment < b.capacity AND b.status IN ('PLANNED', 'ACTIVE')")
    List<Batch> findBatchesWithAvailableSlots();
    
    @Query("SELECT b FROM Batch b WHERE b.currentEnrollment >= b.capacity")
    List<Batch> findFullBatches();
    
    @Query("SELECT b FROM Batch b WHERE (CAST(b.currentEnrollment AS double) / CAST(b.capacity AS double)) >= :utilizationThreshold")
    List<Batch> findBatchesAboveUtilization(@Param("utilizationThreshold") double utilizationThreshold);
    
    // Complex search queries
    @Query("SELECT b FROM Batch b WHERE " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:courseId IS NULL OR b.course.id = :courseId) AND " +
           "(:instructorId IS NULL OR b.instructor.id = :instructorId) AND " +
           "(:hasAvailableSlots IS NULL OR " +
           "(:hasAvailableSlots = true AND b.currentEnrollment < b.capacity) OR " +
           "(:hasAvailableSlots = false AND b.currentEnrollment >= b.capacity)) AND " +
           "(:searchTerm IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.course.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Batch> findBatchesWithFilters(@Param("status") BatchStatus status,
                                      @Param("courseId") UUID courseId,
                                      @Param("instructorId") UUID instructorId,
                                      @Param("hasAvailableSlots") Boolean hasAvailableSlots,
                                      @Param("searchTerm") String searchTerm,
                                      Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(b) FROM Batch b WHERE b.status = :status")
    long countByStatus(@Param("status") BatchStatus status);
    
    @Query("SELECT b.course, COUNT(b) FROM Batch b GROUP BY b.course")
    List<Object[]> countByCourse();
    
    @Query("SELECT b.instructor, COUNT(b) FROM Batch b WHERE b.instructor IS NOT NULL GROUP BY b.instructor")
    List<Object[]> countByInstructor();
    
    // Utilization statistics
    @Query("SELECT AVG(CAST(b.currentEnrollment AS double) / CAST(b.capacity AS double) * 100) FROM Batch b WHERE b.status = 'ACTIVE'")
    Double getAverageUtilization();
    
    @Query("SELECT b, (CAST(b.currentEnrollment AS double) / CAST(b.capacity AS double) * 100) as utilization " +
           "FROM Batch b WHERE b.status = 'ACTIVE' ORDER BY utilization DESC")
    List<Object[]> getBatchUtilizationReport();
    
    // Revenue related queries
    @Query("SELECT SUM(b.currentEnrollment * b.course.fees) FROM Batch b WHERE b.status IN ('ACTIVE', 'COMPLETED')")
    Double getTotalRevenue();
    
    @Query("SELECT b.course, SUM(b.currentEnrollment * b.course.fees) FROM Batch b WHERE b.status IN ('ACTIVE', 'COMPLETED') GROUP BY b.course")
    List<Object[]> getRevenueByCourse();
    
    // Batch scheduling queries
    @Query("SELECT b FROM Batch b WHERE b.instructor = :instructor AND " +
           "((b.startDate <= :endDate AND (b.endDate IS NULL OR b.endDate >= :startDate)) OR " +
           "(b.startDate >= :startDate AND b.startDate <= :endDate))")
    List<Batch> findConflictingBatches(@Param("instructor") Employee instructor,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);
    
    // Batches by course and status
    List<Batch> findByCourseAndStatus(Course course, BatchStatus status);
    
    // Batches starting in date range
    @Query("SELECT b FROM Batch b WHERE b.startDate >= :startDate AND b.startDate <= :endDate ORDER BY b.startDate")
    List<Batch> findBatchesStartingInRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Batches ending in date range
    @Query("SELECT b FROM Batch b WHERE b.endDate >= :startDate AND b.endDate <= :endDate ORDER BY b.endDate")
    List<Batch> findBatchesEndingInRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Batches without instructor
    @Query("SELECT b FROM Batch b WHERE b.instructor IS NULL AND b.status IN ('PLANNED', 'ACTIVE')")
    List<Batch> findBatchesWithoutInstructor();
    
    // Recent batches
    @Query("SELECT b FROM Batch b ORDER BY b.createdDate DESC")
    Page<Batch> findRecentBatches(Pageable pageable);
}