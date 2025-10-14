package com.institute.management.repository;

import com.institute.management.entity.Placement;
import com.institute.management.entity.Placement.PlacementStatus;
import com.institute.management.entity.Placement.JobType;
import com.institute.management.entity.Placement.EmploymentType;
import com.institute.management.entity.Student;
import com.institute.management.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PlacementRepository extends JpaRepository<Placement, UUID> {
    
    // Basic queries by status
    List<Placement> findByStatus(PlacementStatus status);
    Page<Placement> findByStatus(PlacementStatus status, Pageable pageable);
    
    // Find by student
    List<Placement> findByStudent(Student student);
    Page<Placement> findByStudent(Student student, Pageable pageable);
    
    // Find by company
    List<Placement> findByCompany(Company company);
    Page<Placement> findByCompany(Company company, Pageable pageable);
    
    // Find by job type
    List<Placement> findByJobType(JobType jobType);
    Page<Placement> findByJobType(JobType jobType, Pageable pageable);
    
    // Find by employment type
    List<Placement> findByEmploymentType(EmploymentType employmentType);
    Page<Placement> findByEmploymentType(EmploymentType employmentType, Pageable pageable);
    
    // Date range queries
    List<Placement> findByPlacementDateBetween(LocalDate startDate, LocalDate endDate);
    Page<Placement> findByPlacementDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    List<Placement> findByJoiningDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Salary range queries
    List<Placement> findBySalaryBetween(BigDecimal minSalary, BigDecimal maxSalary);
    List<Placement> findBySalaryGreaterThanEqual(BigDecimal minSalary);
    List<Placement> findBySalaryLessThanEqual(BigDecimal maxSalary);
    
    // Complex search queries
    @Query("SELECT p FROM Placement p WHERE " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:companyId IS NULL OR p.company.id = :companyId) AND " +
           "(:jobType IS NULL OR p.jobType = :jobType) AND " +
           "(:employmentType IS NULL OR p.employmentType = :employmentType) AND " +
           "(:minSalary IS NULL OR p.salary >= :minSalary) AND " +
           "(:maxSalary IS NULL OR p.salary <= :maxSalary) AND " +
           "(:courseId IS NULL OR p.student.batch.course.id = :courseId) AND " +
           "(:searchTerm IS NULL OR LOWER(p.position) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.company.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(CONCAT(p.student.firstName, ' ', p.student.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Placement> findPlacementsWithFilters(@Param("status") PlacementStatus status,
                                             @Param("companyId") UUID companyId,
                                             @Param("jobType") JobType jobType,
                                             @Param("employmentType") EmploymentType employmentType,
                                             @Param("minSalary") BigDecimal minSalary,
                                             @Param("maxSalary") BigDecimal maxSalary,
                                             @Param("courseId") UUID courseId,
                                             @Param("searchTerm") String searchTerm,
                                             Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(p) FROM Placement p WHERE p.status = :status")
    long countByStatus(@Param("status") PlacementStatus status);
    
    @Query("SELECT p.company, COUNT(p) FROM Placement p WHERE p.status = 'PLACED' GROUP BY p.company ORDER BY COUNT(p) DESC")
    List<Object[]> countByCompany();
    
    @Query("SELECT p.jobType, COUNT(p) FROM Placement p WHERE p.status = 'PLACED' GROUP BY p.jobType")
    List<Object[]> countByJobType();
    
    @Query("SELECT p.employmentType, COUNT(p) FROM Placement p WHERE p.status = 'PLACED' GROUP BY p.employmentType")
    List<Object[]> countByEmploymentType();
    
    // Salary statistics
    @Query("SELECT AVG(p.salary) FROM Placement p WHERE p.status = 'PLACED' AND p.salary IS NOT NULL")
    BigDecimal getAverageSalary();
    
    @Query("SELECT MIN(p.salary), MAX(p.salary) FROM Placement p WHERE p.status = 'PLACED' AND p.salary IS NOT NULL")
    List<Object[]> getSalaryRange();
    
    @Query("SELECT p.company, AVG(p.salary) FROM Placement p WHERE p.status = 'PLACED' AND p.salary IS NOT NULL GROUP BY p.company ORDER BY AVG(p.salary) DESC")
    List<Object[]> getAverageSalaryByCompany();
    
    @Query("SELECT c.name, AVG(p.salary) FROM Placement p JOIN p.student.batch.course c WHERE p.status = 'PLACED' AND p.salary IS NOT NULL GROUP BY c ORDER BY AVG(p.salary) DESC")
    List<Object[]> getAverageSalaryByCourse();
    
    // Placement rate statistics
    @Query("SELECT COUNT(DISTINCT p.student) FROM Placement p WHERE p.status = 'PLACED'")
    long countPlacedStudents();
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.status = 'GRADUATED'")
    long countGraduatedStudents();
    
    @Query("SELECT c.name, " +
           "COUNT(DISTINCT s) as totalGraduates, " +
           "COUNT(DISTINCT CASE WHEN p.status = 'PLACED' THEN p.student END) as placedStudents " +
           "FROM Course c " +
           "LEFT JOIN c.batches b " +
           "LEFT JOIN b.students s ON s.status = 'GRADUATED' " +
           "LEFT JOIN s.placements p ON p.status = 'PLACED' " +
           "GROUP BY c")
    List<Object[]> getPlacementRateByCourse();
    
    // Recent placements
    @Query("SELECT p FROM Placement p ORDER BY p.placementDate DESC")
    Page<Placement> findRecentPlacements(Pageable pageable);
    
    // Active placements
    @Query("SELECT p FROM Placement p WHERE p.status = 'PLACED' AND (p.endDate IS NULL OR p.endDate > :currentDate)")
    List<Placement> findActivePlacements(@Param("currentDate") LocalDate currentDate);
    
    // Placements by position
    @Query("SELECT p.position, COUNT(p) FROM Placement p WHERE p.status = 'PLACED' GROUP BY p.position ORDER BY COUNT(p) DESC")
    List<Object[]> countByPosition();
    
    // Placements by work location
    @Query("SELECT p.workLocation, COUNT(p) FROM Placement p WHERE p.status = 'PLACED' AND p.workLocation IS NOT NULL GROUP BY p.workLocation ORDER BY COUNT(p) DESC")
    List<Object[]> countByWorkLocation();
    
    // Monthly placement trends
    @Query("SELECT YEAR(p.placementDate), MONTH(p.placementDate), COUNT(p) FROM Placement p WHERE p.status = 'PLACED' AND p.placementDate >= :startDate GROUP BY YEAR(p.placementDate), MONTH(p.placementDate) ORDER BY YEAR(p.placementDate), MONTH(p.placementDate)")
    List<Object[]> getMonthlyPlacementTrends(@Param("startDate") LocalDate startDate);
    
    // Placements in probation
    @Query("SELECT p FROM Placement p WHERE p.status = 'PLACED' AND p.joiningDate IS NOT NULL AND p.probationPeriodMonths IS NOT NULL AND p.joiningDate <= :currentDate AND p.joiningDate > :probationCutoff")
    List<Placement> findPlacementsInProbation(@Param("currentDate") LocalDate currentDate, @Param("probationCutoff") LocalDate probationCutoff);
    
    // High salary placements
    @Query("SELECT p FROM Placement p WHERE p.status = 'PLACED' AND p.salary >= :salaryThreshold ORDER BY p.salary DESC")
    List<Placement> findHighSalaryPlacements(@Param("salaryThreshold") BigDecimal salaryThreshold);
    
    // Company performance
    @Query("SELECT c, COUNT(p) as placementCount, AVG(p.salary) as avgSalary FROM Company c LEFT JOIN c.placements p WHERE p.status = 'PLACED' GROUP BY c ORDER BY placementCount DESC")
    List<Object[]> getCompanyPerformanceStats();
    
    // Placement duration analysis
    @Query("SELECT p FROM Placement p WHERE p.status IN ('RESIGNED', 'TERMINATED', 'COMPLETED') AND p.joiningDate IS NOT NULL AND p.endDate IS NOT NULL")
    List<Placement> findCompletedPlacementsWithDuration();
    
    // Salary range distribution
    @Query("SELECT " +
           "COUNT(CASE WHEN p.salary < 300000 THEN 1 END) as lowSalary, " +
           "COUNT(CASE WHEN p.salary >= 300000 AND p.salary < 600000 THEN 1 END) as mediumSalary, " +
           "COUNT(CASE WHEN p.salary >= 600000 THEN 1 END) as highSalary " +
           "FROM Placement p WHERE p.status = 'PLACED' AND p.salary IS NOT NULL")
    List<Object[]> getSalaryRangeDistribution();
}