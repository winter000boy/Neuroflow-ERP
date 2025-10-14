package com.institute.management.repository;

import com.institute.management.entity.User;
import com.institute.management.entity.User.UserStatus;
import com.institute.management.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    // Basic authentication queries
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    
    // Find by employee
    Optional<User> findByEmployee(Employee employee);
    boolean existsByEmployee(Employee employee);
    
    // Basic queries by status
    List<User> findByStatus(UserStatus status);
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    
    // Authentication and security queries
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.status = 'ACTIVE'")
    Optional<User> findActiveUserByUsername(@Param("username") String username);
    
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.status = 'ACTIVE' AND (u.accountLockedUntil IS NULL OR u.accountLockedUntil <= :currentTime)")
    Optional<User> findUnlockedUserByUsername(@Param("username") String username, @Param("currentTime") LocalDateTime currentTime);
    
    // Locked accounts
    @Query("SELECT u FROM User u WHERE u.accountLockedUntil IS NOT NULL AND u.accountLockedUntil > :currentTime")
    List<User> findLockedAccounts(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT u FROM User u WHERE u.failedLoginAttempts >= :threshold AND u.status = 'ACTIVE'")
    List<User> findUsersWithHighFailedAttempts(@Param("threshold") int threshold);
    
    // Password expiry queries
    @Query("SELECT u FROM User u WHERE u.passwordChangedDate IS NULL OR u.passwordChangedDate <= :expiryDate")
    List<User> findUsersWithExpiredPasswords(@Param("expiryDate") LocalDateTime expiryDate);
    
    @Query("SELECT u FROM User u WHERE u.passwordChangedDate <= :warningDate AND u.passwordChangedDate > :expiryDate AND u.status = 'ACTIVE'")
    List<User> findUsersWithPasswordsNearExpiry(@Param("warningDate") LocalDateTime warningDate, @Param("expiryDate") LocalDateTime expiryDate);
    
    // Activity queries
    @Query("SELECT u FROM User u WHERE u.lastLogin IS NULL AND u.status = 'ACTIVE'")
    List<User> findUsersWhoNeverLoggedIn();
    
    @Query("SELECT u FROM User u WHERE u.lastLogin <= :cutoffDate AND u.status = 'ACTIVE'")
    List<User> findInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT u FROM User u WHERE u.lastLogin >= :cutoffDate AND u.status = 'ACTIVE' ORDER BY u.lastLogin DESC")
    List<User> findRecentlyActiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Complex search queries
    @Query("SELECT u FROM User u WHERE " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:employeeRole IS NULL OR u.employee.role = :employeeRole) AND " +
           "(:searchTerm IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(CONCAT(u.employee.firstName, ' ', u.employee.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.employee.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<User> findUsersWithFilters(@Param("status") UserStatus status,
                                   @Param("employeeRole") Employee.EmployeeRole employeeRole,
                                   @Param("searchTerm") String searchTerm,
                                   Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    long countByStatus(@Param("status") UserStatus status);
    
    @Query("SELECT u.employee.role, COUNT(u) FROM User u WHERE u.status = 'ACTIVE' GROUP BY u.employee.role")
    List<Object[]> countByEmployeeRole();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLogin >= :startDate AND u.lastLogin <= :endDate")
    long countLoginsInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Login activity statistics
    @Query("SELECT DATE(u.lastLogin), COUNT(u) FROM User u WHERE u.lastLogin >= :startDate AND u.lastLogin <= :endDate GROUP BY DATE(u.lastLogin) ORDER BY DATE(u.lastLogin)")
    List<Object[]> getLoginActivityTrends(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Security statistics
    @Query("SELECT COUNT(u) FROM User u WHERE u.failedLoginAttempts > 0")
    long countUsersWithFailedAttempts();
    
    @Query("SELECT AVG(u.failedLoginAttempts) FROM User u WHERE u.failedLoginAttempts > 0")
    Double getAverageFailedLoginAttempts();
    
    // User account health
    @Query("SELECT " +
           "COUNT(CASE WHEN u.status = 'ACTIVE' THEN 1 END) as activeUsers, " +
           "COUNT(CASE WHEN u.status = 'INACTIVE' THEN 1 END) as inactiveUsers, " +
           "COUNT(CASE WHEN u.status = 'LOCKED' THEN 1 END) as lockedUsers, " +
           "COUNT(CASE WHEN u.status = 'SUSPENDED' THEN 1 END) as suspendedUsers " +
           "FROM User u")
    List<Object[]> getUserStatusDistribution();
    
    // Users by employee department
    @Query("SELECT u.employee.department, COUNT(u) FROM User u WHERE u.status = 'ACTIVE' AND u.employee.department IS NOT NULL GROUP BY u.employee.department")
    List<Object[]> countByEmployeeDepartment();
    
    // Users by creation date
    @Query("SELECT u FROM User u WHERE u.createdDate >= :startDate AND u.createdDate <= :endDate ORDER BY u.createdDate DESC")
    List<User> findUsersCreatedInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Recently created users
    @Query("SELECT u FROM User u ORDER BY u.createdDate DESC")
    Page<User> findRecentlyCreatedUsers(Pageable pageable);
    
    // Users with specific employee status
    @Query("SELECT u FROM User u WHERE u.employee.status = :employeeStatus")
    List<User> findByEmployeeStatus(@Param("employeeStatus") Employee.EmployeeStatus employeeStatus);
    
    // Active users with active employees
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND u.employee.status = 'ACTIVE'")
    List<User> findActiveUsersWithActiveEmployees();
    
    // Orphaned users (employees no longer active)
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND u.employee.status != 'ACTIVE'")
    List<User> findOrphanedUsers();
    
    // Password change statistics
    @Query("SELECT COUNT(u) FROM User u WHERE u.passwordChangedDate >= :startDate AND u.passwordChangedDate <= :endDate")
    long countPasswordChangesInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Users by role with login activity
    @Query("SELECT u.employee.role, COUNT(u), COUNT(CASE WHEN u.lastLogin >= :cutoffDate THEN 1 END) " +
           "FROM User u WHERE u.status = 'ACTIVE' " +
           "GROUP BY u.employee.role")
    List<Object[]> getUserActivityByRole(@Param("cutoffDate") LocalDateTime cutoffDate);
}