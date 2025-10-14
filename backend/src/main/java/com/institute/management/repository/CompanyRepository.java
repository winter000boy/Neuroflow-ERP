package com.institute.management.repository;

import com.institute.management.entity.Company;
import com.institute.management.entity.Company.CompanyStatus;
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
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    
    // Basic queries by status
    List<Company> findByStatus(CompanyStatus status);
    Page<Company> findByStatus(CompanyStatus status, Pageable pageable);
    
    // Find by industry
    List<Company> findByIndustry(String industry);
    Page<Company> findByIndustry(String industry, Pageable pageable);
    
    // Find by name (for duplicate checking)
    Optional<Company> findByName(String name);
    boolean existsByName(String name);
    
    // Find by email (for duplicate checking)
    Optional<Company> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Find by phone (for duplicate checking)
    Optional<Company> findByPhone(String phone);
    boolean existsByPhone(String phone);
    
    // Date range queries
    List<Company> findByPartnershipDateBetween(LocalDate startDate, LocalDate endDate);
    Page<Company> findByPartnershipDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    // Complex search queries
    @Query("SELECT c FROM Company c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:industry IS NULL OR c.industry = :industry) AND " +
           "(:searchTerm IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.contactPerson) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "c.phone LIKE CONCAT('%', :searchTerm, '%'))")
    Page<Company> findCompaniesWithFilters(@Param("status") CompanyStatus status,
                                          @Param("industry") String industry,
                                          @Param("searchTerm") String searchTerm,
                                          Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(c) FROM Company c WHERE c.status = :status")
    long countByStatus(@Param("status") CompanyStatus status);
    
    @Query("SELECT c.industry, COUNT(c) FROM Company c WHERE c.status = 'ACTIVE' AND c.industry IS NOT NULL GROUP BY c.industry ORDER BY COUNT(c) DESC")
    List<Object[]> countByIndustry();
    
    // Partnership statistics
    @Query("SELECT COUNT(c) FROM Company c WHERE c.partnershipDate >= :startDate AND c.partnershipDate <= :endDate")
    long countPartnershipsInDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT c.partnershipDate, COUNT(c) FROM Company c WHERE c.partnershipDate >= :startDate AND c.partnershipDate <= :endDate GROUP BY c.partnershipDate ORDER BY c.partnershipDate")
    List<Object[]> getPartnershipTrends(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Placement related queries
    @Query("SELECT c, COUNT(p) as placementCount FROM Company c LEFT JOIN c.placements p WHERE p.status = 'PLACED' GROUP BY c ORDER BY placementCount DESC")
    List<Object[]> findCompaniesByPlacementCount();
    
    @Query("SELECT c FROM Company c WHERE SIZE(c.placements) > 0 ORDER BY SIZE(c.placements) DESC")
    List<Company> findCompaniesWithPlacements();
    
    @Query("SELECT c FROM Company c WHERE SIZE(c.placements) = 0 AND c.status = 'ACTIVE'")
    List<Company> findCompaniesWithoutPlacements();
    
    // Active hiring companies
    @Query("SELECT DISTINCT c FROM Company c JOIN c.placements p WHERE p.placementDate >= :cutoffDate AND c.status = 'ACTIVE'")
    List<Company> findActiveHiringCompanies(@Param("cutoffDate") LocalDate cutoffDate);
    
    // Top hiring companies by period
    @Query("SELECT c, COUNT(p) as recentPlacements FROM Company c JOIN c.placements p WHERE p.placementDate >= :startDate AND p.placementDate <= :endDate AND p.status = 'PLACED' GROUP BY c ORDER BY recentPlacements DESC")
    List<Object[]> findTopHiringCompaniesByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Salary statistics by company
    @Query("SELECT c, AVG(p.salary) as avgSalary, COUNT(p) as placementCount FROM Company c JOIN c.placements p WHERE p.status = 'PLACED' AND p.salary IS NOT NULL GROUP BY c HAVING COUNT(p) >= :minPlacements ORDER BY avgSalary DESC")
    List<Object[]> findCompaniesBySalaryOffered(@Param("minPlacements") long minPlacements);
    
    // Companies by partnership year
    @Query("SELECT c FROM Company c WHERE YEAR(c.partnershipDate) = :year")
    List<Company> findByPartnershipYear(@Param("year") int year);
    
    // Long-term partners
    @Query("SELECT c FROM Company c WHERE c.partnershipDate <= :cutoffDate AND c.status = 'ACTIVE' ORDER BY c.partnershipDate ASC")
    List<Company> findLongTermPartners(@Param("cutoffDate") LocalDate cutoffDate);
    
    // Recent partners
    @Query("SELECT c FROM Company c WHERE c.partnershipDate >= :cutoffDate AND c.status = 'ACTIVE' ORDER BY c.partnershipDate DESC")
    List<Company> findRecentPartners(@Param("cutoffDate") LocalDate cutoffDate);
    
    // Companies by multiple industries
    @Query("SELECT c FROM Company c WHERE c.industry IN :industries AND c.status = 'ACTIVE'")
    List<Company> findByIndustries(@Param("industries") List<String> industries);
    
    // Companies with contact information
    @Query("SELECT c FROM Company c WHERE c.email IS NOT NULL AND c.phone IS NOT NULL AND c.contactPerson IS NOT NULL AND c.status = 'ACTIVE'")
    List<Company> findCompaniesWithCompleteContactInfo();
    
    // Companies missing contact information
    @Query("SELECT c FROM Company c WHERE (c.email IS NULL OR c.phone IS NULL OR c.contactPerson IS NULL) AND c.status = 'ACTIVE'")
    List<Company> findCompaniesWithIncompleteContactInfo();
    
    // Industry diversity statistics
    @Query("SELECT COUNT(DISTINCT c.industry) FROM Company c WHERE c.status = 'ACTIVE' AND c.industry IS NOT NULL")
    long countDistinctIndustries();
    
    // Placement success rate by company
    @Query("SELECT c, " +
           "COUNT(p) as totalPlacements, " +
           "COUNT(CASE WHEN p.status = 'PLACED' THEN 1 END) as activePlacements, " +
           "COUNT(CASE WHEN p.status IN ('RESIGNED', 'TERMINATED') THEN 1 END) as endedPlacements " +
           "FROM Company c LEFT JOIN c.placements p " +
           "GROUP BY c " +
           "HAVING COUNT(p) > 0")
    List<Object[]> getCompanyPlacementSuccessStats();
    
    // Recent companies
    @Query("SELECT c FROM Company c ORDER BY c.createdDate DESC")
    Page<Company> findRecentCompanies(Pageable pageable);
    
    // Companies by status and industry
    List<Company> findByStatusAndIndustry(CompanyStatus status, String industry);
}