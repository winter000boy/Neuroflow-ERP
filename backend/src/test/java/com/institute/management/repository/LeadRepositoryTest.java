package com.institute.management.repository;

import com.institute.management.entity.Lead;
import com.institute.management.entity.Lead.LeadStatus;
import com.institute.management.entity.Employee;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LeadRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LeadRepository leadRepository;

    private Employee counsellor;
    private Lead lead1;
    private Lead lead2;
    private Lead lead3;

    @BeforeEach
    void setUp() {
        // Create test counsellor
        counsellor = new Employee();
        counsellor.setEmployeeCode("EMP001");
        counsellor.setFirstName("John");
        counsellor.setLastName("Doe");
        counsellor.setEmail("john.doe@institute.com");
        counsellor.setRole(EmployeeRole.COUNSELLOR);
        counsellor.setStatus(EmployeeStatus.ACTIVE);
        counsellor.setHireDate(LocalDate.now().minusYears(1));
        entityManager.persistAndFlush(counsellor);

        // Create test leads
        lead1 = new Lead();
        lead1.setFirstName("Alice");
        lead1.setLastName("Smith");
        lead1.setEmail("alice.smith@email.com");
        lead1.setPhone("1234567890");
        lead1.setCourseInterest("Java Development");
        lead1.setSource("Website");
        lead1.setStatus(LeadStatus.NEW);
        lead1.setAssignedCounsellor(counsellor);
        lead1.setNextFollowUpDate(LocalDateTime.now().plusDays(1));
        entityManager.persistAndFlush(lead1);

        lead2 = new Lead();
        lead2.setFirstName("Bob");
        lead2.setLastName("Johnson");
        lead2.setEmail("bob.johnson@email.com");
        lead2.setPhone("0987654321");
        lead2.setCourseInterest("Python Development");
        lead2.setSource("Referral");
        lead2.setStatus(LeadStatus.CONVERTED);
        lead2.setAssignedCounsellor(counsellor);
        lead2.setConvertedDate(LocalDateTime.now().minusDays(5));
        entityManager.persistAndFlush(lead2);

        lead3 = new Lead();
        lead3.setFirstName("Charlie");
        lead3.setLastName("Brown");
        lead3.setEmail("charlie.brown@email.com");
        lead3.setPhone("5555555555");
        lead3.setCourseInterest("Java Development");
        lead3.setSource("Social Media");
        lead3.setStatus(LeadStatus.INTERESTED);
        lead3.setNextFollowUpDate(LocalDateTime.now().minusDays(1)); // Overdue follow-up
        entityManager.persistAndFlush(lead3);
    }

    @Test
    void testFindByStatus() {
        List<Lead> newLeads = leadRepository.findByStatus(LeadStatus.NEW);
        assertThat(newLeads).hasSize(1);
        assertThat(newLeads.get(0).getFirstName()).isEqualTo("Alice");

        List<Lead> convertedLeads = leadRepository.findByStatus(LeadStatus.CONVERTED);
        assertThat(convertedLeads).hasSize(1);
        assertThat(convertedLeads.get(0).getFirstName()).isEqualTo("Bob");
    }

    @Test
    void testFindByStatusWithPagination() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Lead> newLeadsPage = leadRepository.findByStatus(LeadStatus.NEW, pageable);
        
        assertThat(newLeadsPage.getContent()).hasSize(1);
        assertThat(newLeadsPage.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testFindByAssignedCounsellor() {
        List<Lead> counsellorLeads = leadRepository.findByAssignedCounsellor(counsellor);
        assertThat(counsellorLeads).hasSize(2); // lead1 and lead2 are assigned to counsellor
    }

    @Test
    void testFindBySource() {
        List<Lead> websiteLeads = leadRepository.findBySource("Website");
        assertThat(websiteLeads).hasSize(1);
        assertThat(websiteLeads.get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void testFindByCourseInterest() {
        List<Lead> javaLeads = leadRepository.findByCourseInterest("Java Development");
        assertThat(javaLeads).hasSize(2); // lead1 and lead3
    }

    @Test
    void testFindByEmail() {
        Optional<Lead> lead = leadRepository.findByEmail("alice.smith@email.com");
        assertThat(lead).isPresent();
        assertThat(lead.get().getFirstName()).isEqualTo("Alice");
    }

    @Test
    void testExistsByEmail() {
        assertThat(leadRepository.existsByEmail("alice.smith@email.com")).isTrue();
        assertThat(leadRepository.existsByEmail("nonexistent@email.com")).isFalse();
    }

    @Test
    void testFindByPhone() {
        Optional<Lead> lead = leadRepository.findByPhone("1234567890");
        assertThat(lead).isPresent();
        assertThat(lead.get().getFirstName()).isEqualTo("Alice");
    }

    @Test
    void testExistsByPhone() {
        assertThat(leadRepository.existsByPhone("1234567890")).isTrue();
        assertThat(leadRepository.existsByPhone("0000000000")).isFalse();
    }

    @Test
    void testFindByCreatedDateBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        
        List<Lead> recentLeads = leadRepository.findByCreatedDateBetween(startDate, endDate);
        assertThat(recentLeads).hasSize(3); // All leads created today
    }

    @Test
    void testFindByConvertedDateBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(10);
        LocalDateTime endDate = LocalDateTime.now();
        
        List<Lead> convertedLeads = leadRepository.findByConvertedDateBetween(startDate, endDate);
        assertThat(convertedLeads).hasSize(1); // Only lead2 is converted
        assertThat(convertedLeads.get(0).getFirstName()).isEqualTo("Bob");
    }

    @Test
    void testFindByNextFollowUpDateBefore() {
        LocalDateTime now = LocalDateTime.now();
        List<Lead> overdueLeads = leadRepository.findByNextFollowUpDateBefore(now);
        
        assertThat(overdueLeads).hasSize(1); // Only lead3 has overdue follow-up
        assertThat(overdueLeads.get(0).getFirstName()).isEqualTo("Charlie");
    }

    @Test
    void testFindLeadsWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        
        // Test with status filter
        Page<Lead> filteredLeads = leadRepository.findLeadsWithFilters(
            LeadStatus.NEW, null, null, null, null, pageable);
        assertThat(filteredLeads.getContent()).hasSize(1);
        
        // Test with search term
        Page<Lead> searchResults = leadRepository.findLeadsWithFilters(
            null, null, null, null, "Alice", pageable);
        assertThat(searchResults.getContent()).hasSize(1);
        assertThat(searchResults.getContent().get(0).getFirstName()).isEqualTo("Alice");
        
        // Test with course interest filter
        Page<Lead> courseResults = leadRepository.findLeadsWithFilters(
            null, null, "Java Development", null, null, pageable);
        assertThat(courseResults.getContent()).hasSize(2); // lead1 and lead3
    }

    @Test
    void testCountByStatus() {
        long newCount = leadRepository.countByStatus(LeadStatus.NEW);
        assertThat(newCount).isEqualTo(1);
        
        long convertedCount = leadRepository.countByStatus(LeadStatus.CONVERTED);
        assertThat(convertedCount).isEqualTo(1);
        
        long interestedCount = leadRepository.countByStatus(LeadStatus.INTERESTED);
        assertThat(interestedCount).isEqualTo(1);
    }

    @Test
    void testCountBySource() {
        List<Object[]> sourceStats = leadRepository.countBySource();
        assertThat(sourceStats).hasSize(3); // Website, Referral, Social Media
        
        // Verify that each source has 1 lead
        for (Object[] stat : sourceStats) {
            assertThat((Long) stat[1]).isEqualTo(1L);
        }
    }

    @Test
    void testCountByCourseInterest() {
        List<Object[]> courseStats = leadRepository.countByCourseInterest();
        assertThat(courseStats).hasSize(2); // Java Development, Python Development
        
        // Find Java Development count
        Object[] javaStats = courseStats.stream()
            .filter(stat -> "Java Development".equals(stat[0]))
            .findFirst()
            .orElse(null);
        assertThat(javaStats).isNotNull();
        assertThat((Long) javaStats[1]).isEqualTo(2L);
    }

    @Test
    void testCountByCounsellorAndStatus() {
        long newLeadsCount = leadRepository.countByCounsellorAndStatus(counsellor, LeadStatus.NEW);
        assertThat(newLeadsCount).isEqualTo(1);
        
        long convertedLeadsCount = leadRepository.countByCounsellorAndStatus(counsellor, LeadStatus.CONVERTED);
        assertThat(convertedLeadsCount).isEqualTo(1);
    }

    @Test
    void testCountConvertedLeadsByCounsellor() {
        long convertedCount = leadRepository.countConvertedLeadsByCounsellor(counsellor);
        assertThat(convertedCount).isEqualTo(1);
    }

    @Test
    void testFindRecentLeads() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Lead> recentLeads = leadRepository.findRecentLeads(pageable);
        
        assertThat(recentLeads.getContent()).hasSize(2);
        // Should be ordered by creation date descending
    }

    @Test
    void testFindLeadsRequiringFollowUp() {
        LocalDateTime now = LocalDateTime.now();
        List<Lead> followUpLeads = leadRepository.findLeadsRequiringFollowUp(now);
        
        assertThat(followUpLeads).hasSize(1); // Only lead3 has overdue follow-up
        assertThat(followUpLeads.get(0).getFirstName()).isEqualTo("Charlie");
    }

    @Test
    void testFindLeadsWithoutFollowUp() {
        List<Lead> noFollowUpLeads = leadRepository.findLeadsWithoutFollowUp();
        
        // lead2 is converted (excluded), lead1 and lead3 have follow-up dates
        // So this should return empty list based on our test data
        assertThat(noFollowUpLeads).isEmpty();
    }
}