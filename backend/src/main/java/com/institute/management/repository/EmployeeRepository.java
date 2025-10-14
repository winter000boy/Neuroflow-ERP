package com.institute.management.repository;

import com.institute.management.entity.Employee;
import com.institute.management.entity.Employee.EmployeeRole;
import com.institute.management.entity.Employee.EmployeeStatus;
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
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    
    // Basic queries by status
    List<Employee> findByStatus(EmployeeStatus status);
    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);
    
    // Find by role
    List<Employee> findByRole(EmployeeRole role);
    Page<Employee> findByRole(EmployeeRole role, Pageable pageable);
    
    // Find by department
    List<Employee> findByDepartment(String department);
    Page<Employee> findByDepartment(String department, Pageable pageable);
    
    // Find by employee code (unique identifier)
    Optional<Employee> findByEmployeeCode(String employeeCode);
    boolean existsByEmployeeCode(String employeeCode);
    
    // Find by email (for duplicate checking)
    Optional<Employee> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Find by phone (for duplicate checking)
    Optional<Employee> findByPhone(String phone);
    boolean existsByPhone(String phone);
    
    // Role and status combinations
    List<Employee> findByRoleAndStatus(EmployeeRole role, EmployeeStatus status);
    Page<Employee> findByRoleAndStatus(EmployeeRole role, EmployeeStatus status, Pageable pageable);
    
    // Department and status combinations
    List<Employee> findByDepartmentAndStatus(String department, EmployeeStatus status);
    
    // Date range queries
    List<Employee> findByHireDateBetween(LocalDate startDate, LocalDate endDate);
    Page<Employee> findByHireDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    // Complex search queries
    @Query("SELECT e FROM Employee e WHERE " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:role IS NULL OR e.role = :role) AND " +
           "(:department IS NULL OR e.department = :department) AND " +
           "(:searchTerm IS NULL OR LOWER(CONCAT(e.firstName, ' ', e.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "e.phone LIKE CONCAT('%', :searchTerm, '%'))")
    Page<Employee> findEmployeesWithFilters(@Param("status") EmployeeStatus status,
                                           @Param("role") EmployeeRole role,
                                           @Param("department") String department,
                                           @Param("searchTerm") String searchTerm,
                                           Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = :status")
    long countByStatus(@Param("status") EmployeeStatus status);
    
    @Query("SELECT e.role, COUNT(e) FROM Employee e WHERE e.status = 'ACTIVE' GROUP BY e.role")
    List<Object[]> countByRole();
    
    @Query("SELECT e.department, COUNT(e) FROM Employee e WHERE e.status = 'ACTIVE' AND e.department IS NOT NULL GROUP BY e.department")
    List<Object[]> countByDepartment();
    
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.role = :role AND e.status = :status")
    long countByRoleAndStatus(@Param("role") EmployeeRole role, @Param("status") EmployeeStatus status);
    
    // Hiring statistics
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.hireDate >= :startDate AND e.hireDate <= :endDate")
    long countHiresInDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT e.hireDate, COUNT(e) FROM Employee e WHERE e.hireDate >= :startDate AND e.hireDate <= :endDate GROUP BY e.hireDate ORDER BY e.hireDate")
    List<Object[]> getHiringTrends(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Counsellor specific queries
    @Query("SELECT e FROM Employee e WHERE e.role = 'COUNSELLOR' AND e.status = 'ACTIVE'")
    List<Employee> findActiveCounsellors();
    
    @Query("SELECT e, COUNT(l) as leadCount FROM Employee e LEFT JOIN e.assignedLeads l " +
           "WHERE e.role = 'COUNSELLOR' AND e.status = 'ACTIVE' " +
           "GROUP BY e ORDER BY leadCount ASC")
    List<Object[]> findCounsellorsWithLeadCount();
    
    @Query("SELECT e FROM Employee e WHERE e.role = 'COUNSELLOR' AND e.status = 'ACTIVE' " +
           "AND SIZE(e.assignedLeads) < :maxLeads")
    List<Employee> findAvailableCounsellors(@Param("maxLeads") int maxLeads);
    
    // Faculty specific queries
    @Query("SELECT e FROM Employee e WHERE e.role = 'FACULTY' AND e.status = 'ACTIVE'")
    List<Employee> findActiveFaculty();
    
    @Query("SELECT e, COUNT(b) as batchCount FROM Employee e LEFT JOIN e.instructedBatches b " +
           "WHERE e.role = 'FACULTY' AND e.status = 'ACTIVE' " +
           "GROUP BY e ORDER BY batchCount ASC")
    List<Object[]> findFacultyWithBatchCount();
    
    @Query("SELECT e FROM Employee e WHERE e.role = 'FACULTY' AND e.status = 'ACTIVE' " +
           "AND SIZE(e.instructedBatches) < :maxBatches")
    List<Employee> findAvailableFaculty(@Param("maxBatches") int maxBatches);
    
    // Performance queries for counsellors
    @Query("SELECT e, COUNT(l) as totalLeads, " +
           "COUNT(CASE WHEN l.status = 'CONVERTED' THEN 1 END) as convertedLeads " +
           "FROM Employee e LEFT JOIN e.assignedLeads l " +
           "WHERE e.role = 'COUNSELLOR' AND e.status = 'ACTIVE' " +
           "GROUP BY e")
    List<Object[]> getCounsellorPerformanceStats();
    
    // Employees without user accounts
    @Query("SELECT e FROM Employee e WHERE e.user IS NULL AND e.status = 'ACTIVE'")
    List<Employee> findEmployeesWithoutUserAccount();
    
    // Employees by hire year
    @Query("SELECT e FROM Employee e WHERE YEAR(e.hireDate) = :year")
    List<Employee> findByHireYear(@Param("year") int year);
    
    // Senior employees (by hire date)
    @Query("SELECT e FROM Employee e WHERE e.hireDate <= :cutoffDate AND e.status = 'ACTIVE' ORDER BY e.hireDate ASC")
    List<Employee> findSeniorEmployees(@Param("cutoffDate") LocalDate cutoffDate);
    
    // Recent hires
    @Query("SELECT e FROM Employee e WHERE e.hireDate >= :cutoffDate AND e.status = 'ACTIVE' ORDER BY e.hireDate DESC")
    List<Employee> findRecentHires(@Param("cutoffDate") LocalDate cutoffDate);
    
    // Employees by multiple roles
    @Query("SELECT e FROM Employee e WHERE e.role IN :roles AND e.status = 'ACTIVE'")
    List<Employee> findByRoles(@Param("roles") List<EmployeeRole> roles);
    
    // Generate next employee code (helper for service layer)
    @Query("SELECT e.employeeCode FROM Employee e WHERE e.employeeCode LIKE :prefix% ORDER BY e.employeeCode DESC")
    List<String> findEmployeeCodesWithPrefix(@Param("prefix") String prefix);
    
    // Recent employees
    @Query("SELECT e FROM Employee e ORDER BY e.createdDate DESC")
    Page<Employee> findRecentEmployees(Pageable pageable);
}