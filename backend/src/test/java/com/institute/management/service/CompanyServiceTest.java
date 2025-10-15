package com.institute.management.service;

import com.institute.management.dto.*;
import com.institute.management.entity.Company;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class CompanyServiceTest {
    
    @Mock
    private CompanyRepository companyRepository;
    
    @InjectMocks
    private CompanyService companyService;
    
    private CompanyCreateRequestDTO createRequest;
    private CompanyUpdateRequestDTO updateRequest;
    private Company company;
    private UUID companyId;
    
    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        
        // Setup entity
        company = new Company();
        company.setId(companyId);
        company.setName("Tech Corp");
        company.setIndustry("Technology");
        company.setContactPerson("Jane Smith");
        company.setEmail("contact@techcorp.com");
        company.setPhone("+1234567890");
        company.setAddress("123 Tech Street, Silicon Valley");
        company.setPartnershipDate(LocalDate.now());
        company.setStatus(Company.CompanyStatus.ACTIVE);
        company.setCreatedDate(LocalDateTime.now());
        company.setUpdatedDate(LocalDateTime.now());
        
        // Setup DTOs
        createRequest = new CompanyCreateRequestDTO();
        createRequest.setName("Tech Corp");
        createRequest.setIndustry("Technology");
        createRequest.setContactPerson("Jane Smith");
        createRequest.setEmail("contact@techcorp.com");
        createRequest.setPhone("+1234567890");
        createRequest.setAddress("123 Tech Street, Silicon Valley");
        createRequest.setPartnershipDate(LocalDate.now());
        
        updateRequest = new CompanyUpdateRequestDTO();
        updateRequest.setName("Tech Corp Ltd");
        updateRequest.setIndustry("Information Technology");
        updateRequest.setContactPerson("Jane Smith");
        updateRequest.setEmail("contact@techcorp.com");
        updateRequest.setPhone("+1234567890");
        updateRequest.setAddress("123 Tech Street, Silicon Valley");
        updateRequest.setPartnershipDate(LocalDate.now());
        updateRequest.setStatus(Company.CompanyStatus.ACTIVE);
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void createCompany_Success() {
        // Arrange
        when(companyRepository.existsByName("Tech Corp")).thenReturn(false);
        when(companyRepository.existsByEmail("contact@techcorp.com")).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        
        // Act
        CompanyResponseDTO result = companyService.createCompany(createRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(companyId, result.getId());
        assertEquals("Tech Corp", result.getName());
        assertEquals("Technology", result.getIndustry());
        assertEquals("Jane Smith", result.getContactPerson());
        assertEquals("contact@techcorp.com", result.getEmail());
        assertEquals(Company.CompanyStatus.ACTIVE, result.getStatus());
        
        verify(companyRepository).existsByName("Tech Corp");
        verify(companyRepository).existsByEmail("contact@techcorp.com");
        verify(companyRepository).save(any(Company.class));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void createCompany_DuplicateName() {
        // Arrange
        when(companyRepository.existsByName("Tech Corp")).thenReturn(true);
        
        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            companyService.createCompany(createRequest);
        });
        
        verify(companyRepository).existsByName("Tech Corp");
        verify(companyRepository, never()).save(any());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void createCompany_DuplicateEmail() {
        // Arrange
        when(companyRepository.existsByName("Tech Corp")).thenReturn(false);
        when(companyRepository.existsByEmail("contact@techcorp.com")).thenReturn(true);
        
        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            companyService.createCompany(createRequest);
        });
        
        verify(companyRepository).existsByName("Tech Corp");
        verify(companyRepository).existsByEmail("contact@techcorp.com");
        verify(companyRepository, never()).save(any());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void updateCompany_Success() {
        // Arrange
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyRepository.existsByName("Tech Corp Ltd")).thenReturn(false);
        when(companyRepository.existsByEmail("contact@techcorp.com")).thenReturn(false);
        
        Company updatedCompany = new Company();
        updatedCompany.setId(companyId);
        updatedCompany.setName("Tech Corp Ltd");
        updatedCompany.setIndustry("Information Technology");
        updatedCompany.setStatus(Company.CompanyStatus.ACTIVE);
        
        when(companyRepository.save(any(Company.class))).thenReturn(updatedCompany);
        
        // Act
        CompanyResponseDTO result = companyService.updateCompany(companyId, updateRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(companyId, result.getId());
        assertEquals("Tech Corp Ltd", result.getName());
        assertEquals("Information Technology", result.getIndustry());
        
        verify(companyRepository).findById(companyId);
        verify(companyRepository).save(any(Company.class));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void updateCompany_NotFound() {
        // Arrange
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            companyService.updateCompany(companyId, updateRequest);
        });
        
        verify(companyRepository).findById(companyId);
        verify(companyRepository, never()).save(any());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getCompanyById_Success() {
        // Arrange
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        
        // Act
        CompanyResponseDTO result = companyService.getCompanyById(companyId);
        
        // Assert
        assertNotNull(result);
        assertEquals(companyId, result.getId());
        assertEquals("Tech Corp", result.getName());
        assertEquals("Technology", result.getIndustry());
        
        verify(companyRepository).findById(companyId);
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getCompanyById_NotFound() {
        // Arrange
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            companyService.getCompanyById(companyId);
        });
        
        verify(companyRepository).findById(companyId);
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getAllCompanies_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Company> companyPage = new PageImpl<>(Arrays.asList(company), pageable, 1);
        
        when(companyRepository.findCompaniesWithFilters(any(), any(), any(), eq(pageable)))
            .thenReturn(companyPage);
        
        // Act
        Page<CompanyResponseDTO> result = companyService.getAllCompanies(
            pageable, null, null, null, null, null);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(companyId, result.getContent().get(0).getId());
        
        verify(companyRepository).findCompaniesWithFilters(any(), any(), any(), eq(pageable));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getCompaniesByIndustry_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Company> companyPage = new PageImpl<>(Arrays.asList(company), pageable, 1);
        
        when(companyRepository.findByIndustry("Technology", pageable)).thenReturn(companyPage);
        
        // Act
        Page<CompanyResponseDTO> result = companyService.getCompaniesByIndustry("Technology", pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Technology", result.getContent().get(0).getIndustry());
        
        verify(companyRepository).findByIndustry("Technology", pageable);
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void updateCompanyStatus_Success() {
        // Arrange
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        
        Company updatedCompany = new Company();
        updatedCompany.setId(companyId);
        updatedCompany.setName("Tech Corp");
        updatedCompany.setStatus(Company.CompanyStatus.INACTIVE);
        
        when(companyRepository.save(any(Company.class))).thenReturn(updatedCompany);
        
        // Act
        CompanyResponseDTO result = companyService.updateCompanyStatus(companyId, Company.CompanyStatus.INACTIVE);
        
        // Assert
        assertNotNull(result);
        assertEquals(Company.CompanyStatus.INACTIVE, result.getStatus());
        
        verify(companyRepository).findById(companyId);
        verify(companyRepository).save(any(Company.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCompany_Success() {
        // Arrange
        when(companyRepository.existsById(companyId)).thenReturn(true);
        
        // Act
        assertDoesNotThrow(() -> {
            companyService.deleteCompany(companyId);
        });
        
        // Assert
        verify(companyRepository).existsById(companyId);
        verify(companyRepository).deleteById(companyId);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCompany_NotFound() {
        // Arrange
        when(companyRepository.existsById(companyId)).thenReturn(false);
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            companyService.deleteCompany(companyId);
        });
        
        verify(companyRepository).existsById(companyId);
        verify(companyRepository, never()).deleteById(any());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getActiveCompanies_Success() {
        // Arrange
        when(companyRepository.findByStatus(Company.CompanyStatus.ACTIVE))
            .thenReturn(Arrays.asList(company));
        
        // Act
        List<CompanyResponseDTO> result = companyService.getActiveCompanies();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(companyId, result.get(0).getId());
        assertEquals(Company.CompanyStatus.ACTIVE, result.get(0).getStatus());
        
        verify(companyRepository).findByStatus(Company.CompanyStatus.ACTIVE);
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getCompaniesWithPlacements_Success() {
        // Arrange
        when(companyRepository.findCompaniesWithPlacements())
            .thenReturn(Arrays.asList(company));
        
        // Act
        List<CompanyResponseDTO> result = companyService.getCompaniesWithPlacements();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(companyId, result.get(0).getId());
        
        verify(companyRepository).findCompaniesWithPlacements();
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getRecentPartners_Success() {
        // Arrange
        LocalDate cutoffDate = LocalDate.now().minusMonths(6);
        when(companyRepository.findRecentPartners(cutoffDate))
            .thenReturn(Arrays.asList(company));
        
        // Act
        List<CompanyResponseDTO> result = companyService.getRecentPartners(6);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(companyId, result.get(0).getId());
        
        verify(companyRepository).findRecentPartners(cutoffDate);
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getLongTermPartners_Success() {
        // Arrange
        LocalDate cutoffDate = LocalDate.now().minusYears(2);
        when(companyRepository.findLongTermPartners(cutoffDate))
            .thenReturn(Arrays.asList(company));
        
        // Act
        List<CompanyResponseDTO> result = companyService.getLongTermPartners(2);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(companyId, result.get(0).getId());
        
        verify(companyRepository).findLongTermPartners(cutoffDate);
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getTopHiringCompanies_Success() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusYears(1);
        LocalDate endDate = LocalDate.now();
        List<Object[]> topHiring = new ArrayList<>();
        topHiring.add(new Object[]{company, 25L});
        when(companyRepository.findTopHiringCompaniesByPeriod(startDate, endDate))
            .thenReturn(topHiring);
        
        // Act
        List<Map<String, Object>> result = companyService.getTopHiringCompanies(startDate, endDate, 10);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(companyId, result.get(0).get("companyId"));
        assertEquals("Tech Corp", result.get(0).get("companyName"));
        assertEquals("Technology", result.get(0).get("industry"));
        assertEquals(25L, result.get(0).get("placementCount"));
        
        verify(companyRepository).findTopHiringCompaniesByPeriod(startDate, endDate);
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getCompanyStatistics_Success() {
        // Arrange
        when(companyRepository.count()).thenReturn(50L);
        when(companyRepository.countByStatus(Company.CompanyStatus.ACTIVE)).thenReturn(45L);
        when(companyRepository.countByStatus(Company.CompanyStatus.INACTIVE)).thenReturn(3L);
        when(companyRepository.countByStatus(Company.CompanyStatus.BLACKLISTED)).thenReturn(2L);
        when(companyRepository.countDistinctIndustries()).thenReturn(8L);
        
        // Act
        Map<String, Object> result = companyService.getCompanyStatistics();
        
        // Assert
        assertNotNull(result);
        assertEquals(50L, result.get("totalCompanies"));
        assertEquals(45L, result.get("activeCompanies"));
        assertEquals(3L, result.get("inactiveCompanies"));
        assertEquals(2L, result.get("blacklistedCompanies"));
        assertEquals(8L, result.get("distinctIndustries"));
        
        verify(companyRepository).count();
        verify(companyRepository, times(3)).countByStatus(any());
        verify(companyRepository).countDistinctIndustries();
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getIndustryDistribution_Success() {
        // Arrange
        List<Object[]> distribution = Arrays.asList(
            new Object[]{"Technology", 20L},
            new Object[]{"Finance", 15L}
        );
        when(companyRepository.countByIndustry()).thenReturn(distribution);
        
        // Act
        List<Map<String, Object>> result = companyService.getIndustryDistribution();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Technology", result.get(0).get("industry"));
        assertEquals(20L, result.get(0).get("count"));
        assertEquals("Finance", result.get(1).get("industry"));
        assertEquals(15L, result.get(1).get("count"));
        
        verify(companyRepository).countByIndustry();
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPartnershipTrends_Success() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusYears(2);
        LocalDate endDate = LocalDate.now();
        List<Object[]> trends = Arrays.asList(
            new Object[]{LocalDate.now().minusMonths(1), 5L},
            new Object[]{LocalDate.now(), 8L}
        );
        when(companyRepository.getPartnershipTrends(startDate, endDate)).thenReturn(trends);
        
        // Act
        List<Map<String, Object>> result = companyService.getPartnershipTrends(startDate, endDate);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(5L, result.get(0).get("count"));
        assertEquals(8L, result.get(1).get("count"));
        
        verify(companyRepository).getPartnershipTrends(startDate, endDate);
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getCompaniesBySalaryOffered_Success() {
        // Arrange
        List<Object[]> salaryRanking = new ArrayList<>();
        salaryRanking.add(new Object[]{company, 650000, 15L});
        when(companyRepository.findCompaniesBySalaryOffered(3)).thenReturn(salaryRanking);
        
        // Act
        List<Map<String, Object>> result = companyService.getCompaniesBySalaryOffered(3);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(companyId, result.get(0).get("companyId"));
        assertEquals("Tech Corp", result.get(0).get("companyName"));
        assertEquals("Technology", result.get(0).get("industry"));
        assertEquals(650000, result.get(0).get("averageSalary"));
        assertEquals(15L, result.get(0).get("placementCount"));
        
        verify(companyRepository).findCompaniesBySalaryOffered(3);
    }
}