package com.institute.management.repository;

import com.institute.management.entity.Lead;
import com.institute.management.entity.Lead.LeadStatus;
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
public interface LeadRepository extends JpaRepository<Lead, UUID> {
    
    // Basic queries by status
    List<Lead> findByStatus(LeadStatus status);
    Page<Lead> findByStatus(LeadStatus status, Pageable pageable);
    
    // Find by assigned counsellor
    List<Lead> findByAssignedCounsellor(Employee counsellor);
    Page<Lead> findByAssignedCounsellor(Employee counsellor, Pageable pageable);
    
    // Find by source
    List<Lead> findBySource(String source);
    Page<Lead> findBySource(String source, Pageable pageable);
    
    // Find by course interest
    List<Lead> findByCourseInterest(String courseInterest);
    Page<Lead> findByCourseInterest(String courseInterest, Pageable pageable);
    
    // Find by email (for duplicate checking)
    Optional<Lead> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Find by phone (for duplicate checking)
    Optional<Lead> findByPhone(String phone);
    boolean existsByPhone(String phone);
    
    // Date range queries
    List<Lead> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    Page<Lead> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    List<Lead> findByConvertedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Follow-up queries
    List<Lead> findByNextFollowUpDateBefore(LocalDateTime date);
    List<Lead> findByNextFollowUpDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Complex search queries
    @Query("SELECT l FROM Lead l WHERE " +
           "(:status IS NULL OR l.status = :status) AND " +
           "(:source IS NULL OR l.source = :source) AND " +
           "(:courseInterest IS NULL OR l.courseInterest = :courseInterest) AND " +
           "(:counsellorId IS NULL OR l.assignedCounsellor.id = :counsellorId) AND " +
           "(:searchTerm IS NULL OR LOWER(CONCAT(l.firstName, ' ', l.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(l.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "l.phone LIKE CONCAT('%', :searchTerm, '%'))")
    Page<Lead> findLeadsWithFilters(@Param("status") LeadStatus status,
                                   @Param("source") String source,
                                   @Param("courseInterest") String courseInterest,
                                   @Param("counsellorId") UUID counsellorId,
                                   @Param("searchTerm") String searchTerm,
                                   Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.status = :status")
    long countByStatus(@Param("status") LeadStatus status);
    
    @Query("SELECT l.source, COUNT(l) FROM Lead l GROUP BY l.source")
    List<Object[]> countBySource();
    
    @Query("SELECT l.courseInterest, COUNT(l) FROM Lead l WHERE l.courseInterest IS NOT NULL GROUP BY l.courseInterest")
    List<Object[]> countByCourseInterest();
    
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.assignedCounsellor = :counsellor AND l.status = :status")
    long countByCounsellorAndStatus(@Param("counsellor") Employee counsellor, @Param("status") LeadStatus status);
    
    // Conversion rate queries
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.assignedCounsellor = :counsellor AND l.status = 'CONVERTED'")
    long countConvertedLeadsByCounsellor(@Param("counsellor") Employee counsellor);
    
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.createdDate >= :startDate AND l.createdDate <= :endDate AND l.status = 'CONVERTED'")
    long countConvertedLeadsInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Recent leads
    @Query("SELECT l FROM Lead l ORDER BY l.createdDate DESC")
    Page<Lead> findRecentLeads(Pageable pageable);
    
    // Leads requiring follow-up
    @Query("SELECT l FROM Lead l WHERE l.nextFollowUpDate <= :date AND l.status NOT IN ('CONVERTED', 'LOST', 'NOT_INTERESTED')")
    List<Lead> findLeadsRequiringFollowUp(@Param("date") LocalDateTime date);
    
    // Leads without follow-up scheduled
    @Query("SELECT l FROM Lead l WHERE l.nextFollowUpDate IS NULL AND l.status IN ('NEW', 'CONTACTED', 'INTERESTED')")
    List<Lead> findLeadsWithoutFollowUp();
}