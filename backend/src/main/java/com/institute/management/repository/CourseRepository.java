package com.institute.management.repository;

import com.institute.management.entity.Course;
import com.institute.management.entity.Course.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    
    // Basic queries by status
    List<Course> findByStatus(CourseStatus status);
    Page<Course> findByStatus(CourseStatus status, Pageable pageable);
    
    // Find by name (for duplicate checking)
    Optional<Course> findByName(String name);
    boolean existsByName(String name);
    
    // Find by duration
    List<Course> findByDurationMonths(Integer durationMonths);
    List<Course> findByDurationMonthsBetween(Integer minDuration, Integer maxDuration);
    
    // Find by fees range
    List<Course> findByFeesBetween(BigDecimal minFees, BigDecimal maxFees);
    List<Course> findByFeesLessThanEqual(BigDecimal maxFees);
    List<Course> findByFeesGreaterThanEqual(BigDecimal minFees);
    
    // Complex search queries
    @Query("SELECT c FROM Course c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:minDuration IS NULL OR c.durationMonths >= :minDuration) AND " +
           "(:maxDuration IS NULL OR c.durationMonths <= :maxDuration) AND " +
           "(:minFees IS NULL OR c.fees >= :minFees) AND " +
           "(:maxFees IS NULL OR c.fees <= :maxFees) AND " +
           "(:searchTerm IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Course> findCoursesWithFilters(@Param("status") CourseStatus status,
                                       @Param("minDuration") Integer minDuration,
                                       @Param("maxDuration") Integer maxDuration,
                                       @Param("minFees") BigDecimal minFees,
                                       @Param("maxFees") BigDecimal maxFees,
                                       @Param("searchTerm") String searchTerm,
                                       Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(c) FROM Course c WHERE c.status = :status")
    long countByStatus(@Param("status") CourseStatus status);
    
    @Query("SELECT AVG(c.durationMonths) FROM Course c WHERE c.status = 'ACTIVE'")
    Double getAverageDuration();
    
    @Query("SELECT AVG(c.fees) FROM Course c WHERE c.status = 'ACTIVE'")
    BigDecimal getAverageFees();
    
    @Query("SELECT MIN(c.fees), MAX(c.fees) FROM Course c WHERE c.status = 'ACTIVE'")
    List<Object[]> getFeesRange();
    
    // Popular courses (by batch count)
    @Query("SELECT c, COUNT(b) as batchCount FROM Course c LEFT JOIN c.batches b GROUP BY c ORDER BY batchCount DESC")
    List<Object[]> findCoursesByPopularity();
    
    @Query("SELECT c FROM Course c WHERE SIZE(c.batches) > 0 ORDER BY SIZE(c.batches) DESC")
    List<Course> findCoursesWithBatches();
    
    // Revenue related queries
    @Query("SELECT c, SUM(b.currentEnrollment * c.fees) as revenue FROM Course c " +
           "LEFT JOIN c.batches b WHERE b.status IN ('ACTIVE', 'COMPLETED') " +
           "GROUP BY c ORDER BY revenue DESC")
    List<Object[]> getCourseRevenueReport();
    
    @Query("SELECT SUM(b.currentEnrollment * c.fees) FROM Course c " +
           "JOIN c.batches b WHERE c = :course AND b.status IN ('ACTIVE', 'COMPLETED')")
    BigDecimal getTotalRevenueByCourse(@Param("course") Course course);
    
    // Enrollment statistics
    @Query("SELECT c, COUNT(s) as enrollmentCount FROM Course c " +
           "LEFT JOIN c.batches b LEFT JOIN b.students s " +
           "WHERE s.status = 'ACTIVE' GROUP BY c ORDER BY enrollmentCount DESC")
    List<Object[]> getCourseEnrollmentStats();
    
    @Query("SELECT COUNT(s) FROM Course c JOIN c.batches b JOIN b.students s WHERE c = :course AND s.status = 'ACTIVE'")
    long getActiveEnrollmentsByCourse(@Param("course") Course course);
    
    // Duration-based queries
    @Query("SELECT c FROM Course c WHERE c.status = 'ACTIVE' ORDER BY c.durationMonths ASC")
    List<Course> findActiveCoursesOrderByDuration();
    
    @Query("SELECT c FROM Course c WHERE c.status = 'ACTIVE' ORDER BY c.fees ASC")
    List<Course> findActiveCoursesOrderByFees();
    
    // Courses without batches
    @Query("SELECT c FROM Course c WHERE SIZE(c.batches) = 0 AND c.status = 'ACTIVE'")
    List<Course> findCoursesWithoutBatches();
    
    // Courses with active batches
    @Query("SELECT DISTINCT c FROM Course c JOIN c.batches b WHERE b.status = 'ACTIVE'")
    List<Course> findCoursesWithActiveBatches();
    
    // Fee range statistics
    @Query("SELECT " +
           "COUNT(CASE WHEN c.fees < 10000 THEN 1 END) as lowFee, " +
           "COUNT(CASE WHEN c.fees >= 10000 AND c.fees < 50000 THEN 1 END) as mediumFee, " +
           "COUNT(CASE WHEN c.fees >= 50000 THEN 1 END) as highFee " +
           "FROM Course c WHERE c.status = 'ACTIVE'")
    List<Object[]> getFeeRangeDistribution();
    
    // Duration range statistics
    @Query("SELECT " +
           "COUNT(CASE WHEN c.durationMonths <= 6 THEN 1 END) as shortTerm, " +
           "COUNT(CASE WHEN c.durationMonths > 6 AND c.durationMonths <= 12 THEN 1 END) as mediumTerm, " +
           "COUNT(CASE WHEN c.durationMonths > 12 THEN 1 END) as longTerm " +
           "FROM Course c WHERE c.status = 'ACTIVE'")
    List<Object[]> getDurationRangeDistribution();
    
    // Recent courses
    @Query("SELECT c FROM Course c ORDER BY c.createdDate DESC")
    Page<Course> findRecentCourses(Pageable pageable);
}