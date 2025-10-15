package com.institute.management.service;

import com.institute.management.dto.*;
import com.institute.management.entity.Company;
import com.institute.management.entity.Placement;
import com.institute.management.entity.Student;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.repository.CompanyRepository;
import com.institute.management.repository.PlacementRepository;
import com.institute.management.repository.StudentRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class PlacementServiceTest {
    
    @Mock
    private PlacementRepository placementRepository;
    
    @Mock
    private StudentRepository studentRepository;
    
    @Mock
    private CompanyRepository companyRepository;
    
    @InjectMocks
    private PlacementService placementService;
    
    private PlacementCreateRequestDTO createRequest;
    private PlacementUpdateRequestDTO updateRequest;
    private Placement placement;
    private Student student;
    private Company company;
    private UUID placementId;
    private UUID studentId;
    private UUID companyId;
    
    @BeforeEach
    void setUp() {
        placementId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        companyId = UUID.randomUUID();
        
        // Setup entities
        student = new Student();
        student.setId(studentId);
        student.setEnrollmentNumber("STU001");
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("john.doe@example.com");
        
        company = new Company();
        company.setId(companyId);
        company.setName("Tech Corp");
        company.setIndustry("Technology");
        company.setContactPerson("Jane Smith");
        company.setEmail("contact@techcorp.com");
        company.setStatus(Company.CompanyStatus.ACTIVE);
        
        placement = new Placement();
        placement.setId(placementId);
        placement.setStudent(student);
        placement.setCompany(company);
        placement.setPosition("Software Engineer");
        placement.setSalary(new BigDecimal("500000"));
        placement.setPlacementDate(LocalDate.now());
        placement.setStatus(Placement.PlacementStatus.PLACED);
        placement.setJobType(Placement.JobType.FULL_TIME);
        placement.setEmploymentType(Placement.EmploymentType.PERMANENT);
        placement.setWorkLocation("Bangalore");
        placement.setProbationPeriodMonths(6);
        placement.setJoiningDate(LocalDate.now().plusDays(30));
        placement.setCreatedDate(LocalDateTime.now());
        placement.setUpdatedDate(LocalDateTime.now());
        
        // Setup DTOs
        createRequest = new PlacementCreateRequestDTO();
        createRequest.setStudentId(studentId);
        createRequest.setCompanyId(companyId);
        createRequest.setPosition("Software Engineer");
        createRequest.setSalary(new BigDecimal("500000"));
        createRequest.setPlacementDate(LocalDate.now());
        createRequest.setJobType(Placement.JobType.FULL_TIME);
        createRequest.setEmploymentType(Placement.EmploymentType.PERMANENT);
        createRequest.setWorkLocation("Bangalore");
        createRequest.setProbationPeriodMonths(6);
        createRequest.setJoiningDate(LocalDate.now().plusDays(30));
        
        updateRequest = new PlacementUpdateRequestDTO();
        updateRequest.setStudentId(studentId);
        updateRequest.setCompanyId(companyId);
        updateRequest.setPosition("Senior Software Engineer");
        updateRequest.setSalary(new BigDecimal("600000"));
        updateRequest.setPlacementDate(LocalDate.now());
        updateRequest.setStatus(Placement.PlacementStatus.PLACED);
        updateRequest.setJobType(Placement.JobType.FULL_TIME);
        updateRequest.setEmploymentType(Placement.EmploymentType.PERMANENT);
        updateRequest.setWorkLocation("Bangalore");
        updateRequest.setProbationPeriodMonths(6);
        updateRequest.setJoiningDate(LocalDate.now().plusDays(30));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void createPlacement_Success() {
        // Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(placementRepository.save(any(Placement.class))).thenReturn(placement);
        
        // Act
        PlacementResponseDTO result = placementService.createPlacement(createRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(placementId, result.getId());
        assertEquals("Software Engineer", result.getPosition());
        assertEquals(new BigDecimal("500000"), result.getSalary());
        assertEquals("John", result.getStudent().getFirstName());
        assertEquals("Tech Corp", result.getCompany().getName());
        
        verify(studentRepository).findById(studentId);
        verify(companyRepository).findById(companyId);
        verify(placementRepository).save(any(Placement.class));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void createPlacement_StudentNotFound() {
        // Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            placementService.createPlacement(createRequest);
        });
        
        verify(studentRepository).findById(studentId);
        verify(companyRepository, never()).findById(any());
        verify(placementRepository, never()).save(any());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void createPlacement_CompanyNotFound() {
        // Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            placementService.createPlacement(createRequest);
        });
        
        verify(studentRepository).findById(studentId);
        verify(companyRepository).findById(companyId);
        verify(placementRepository, never()).save(any());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void updatePlacement_Success() {
        // Arrange
        when(placementRepository.findById(placementId)).thenReturn(Optional.of(placement));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        
        Placement updatedPlacement = new Placement();
        updatedPlacement.setId(placementId);
        updatedPlacement.setPosition("Senior Software Engineer");
        updatedPlacement.setSalary(new BigDecimal("600000"));
        updatedPlacement.setStudent(student);
        updatedPlacement.setCompany(company);
        updatedPlacement.setStatus(Placement.PlacementStatus.PLACED);
        
        when(placementRepository.save(any(Placement.class))).thenReturn(updatedPlacement);
        
        // Act
        PlacementResponseDTO result = placementService.updatePlacement(placementId, updateRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(placementId, result.getId());
        assertEquals("Senior Software Engineer", result.getPosition());
        assertEquals(new BigDecimal("600000"), result.getSalary());
        
        verify(placementRepository).findById(placementId);
        verify(studentRepository).findById(studentId);
        verify(companyRepository).findById(companyId);
        verify(placementRepository).save(any(Placement.class));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPlacementById_Success() {
        // Arrange
        when(placementRepository.findById(placementId)).thenReturn(Optional.of(placement));
        
        // Act
        PlacementResponseDTO result = placementService.getPlacementById(placementId);
        
        // Assert
        assertNotNull(result);
        assertEquals(placementId, result.getId());
        assertEquals("Software Engineer", result.getPosition());
        assertEquals("John", result.getStudent().getFirstName());
        assertEquals("Tech Corp", result.getCompany().getName());
        
        verify(placementRepository).findById(placementId);
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPlacementById_NotFound() {
        // Arrange
        when(placementRepository.findById(placementId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            placementService.getPlacementById(placementId);
        });
        
        verify(placementRepository).findById(placementId);
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getAllPlacements_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Placement> placementPage = new PageImpl<>(Arrays.asList(placement), pageable, 1);
        
        when(placementRepository.findPlacementsWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), eq(pageable)))
            .thenReturn(placementPage);
        
        // Act
        Page<PlacementResponseDTO> result = placementService.getAllPlacements(
            pageable, null, null, null, null, null, null, null, null, null, null);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(placementId, result.getContent().get(0).getId());
        
        verify(placementRepository).findPlacementsWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), eq(pageable));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPlacementsByStudent_Success() {
        // Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(placementRepository.findByStudent(student)).thenReturn(Arrays.asList(placement));
        
        // Act
        List<PlacementResponseDTO> result = placementService.getPlacementsByStudent(studentId);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(placementId, result.get(0).getId());
        
        verify(studentRepository).findById(studentId);
        verify(placementRepository).findByStudent(student);
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPlacementsByCompany_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Placement> placementPage = new PageImpl<>(Arrays.asList(placement), pageable, 1);
        
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(placementRepository.findByCompany(company, pageable)).thenReturn(placementPage);
        
        // Act
        Page<PlacementResponseDTO> result = placementService.getPlacementsByCompany(companyId, pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(placementId, result.getContent().get(0).getId());
        
        verify(companyRepository).findById(companyId);
        verify(placementRepository).findByCompany(company, pageable);
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void updatePlacementStatus_Success() {
        // Arrange
        when(placementRepository.findById(placementId)).thenReturn(Optional.of(placement));
        
        Placement updatedPlacement = new Placement();
        updatedPlacement.setId(placementId);
        updatedPlacement.setStatus(Placement.PlacementStatus.RESIGNED);
        updatedPlacement.setStudent(student);
        updatedPlacement.setCompany(company);
        updatedPlacement.setPosition("Software Engineer");
        updatedPlacement.setSalary(new BigDecimal("500000"));
        
        when(placementRepository.save(any(Placement.class))).thenReturn(updatedPlacement);
        
        // Act
        PlacementResponseDTO result = placementService.updatePlacementStatus(placementId, Placement.PlacementStatus.RESIGNED);
        
        // Assert
        assertNotNull(result);
        assertEquals(Placement.PlacementStatus.RESIGNED, result.getStatus());
        
        verify(placementRepository).findById(placementId);
        verify(placementRepository).save(any(Placement.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePlacement_Success() {
        // Arrange
        when(placementRepository.existsById(placementId)).thenReturn(true);
        
        // Act
        assertDoesNotThrow(() -> {
            placementService.deletePlacement(placementId);
        });
        
        // Assert
        verify(placementRepository).existsById(placementId);
        verify(placementRepository).deleteById(placementId);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePlacement_NotFound() {
        // Arrange
        when(placementRepository.existsById(placementId)).thenReturn(false);
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            placementService.deletePlacement(placementId);
        });
        
        verify(placementRepository).existsById(placementId);
        verify(placementRepository, never()).deleteById(any());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getActivePlacements_Success() {
        // Arrange
        when(placementRepository.findActivePlacements(any(LocalDate.class)))
            .thenReturn(Arrays.asList(placement));
        
        // Act
        List<PlacementResponseDTO> result = placementService.getActivePlacements();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(placementId, result.get(0).getId());
        
        verify(placementRepository).findActivePlacements(any(LocalDate.class));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPlacementStatistics_Success() {
        // Arrange
        when(placementRepository.count()).thenReturn(100L);
        when(placementRepository.countByStatus(Placement.PlacementStatus.PLACED)).thenReturn(80L);
        when(placementRepository.countByStatus(Placement.PlacementStatus.RESIGNED)).thenReturn(15L);
        when(placementRepository.countByStatus(Placement.PlacementStatus.TERMINATED)).thenReturn(5L);
        when(placementRepository.countGraduatedStudents()).thenReturn(120L);
        when(placementRepository.countPlacedStudents()).thenReturn(95L);
        
        // Act
        Map<String, Object> result = placementService.getPlacementStatistics();
        
        // Assert
        assertNotNull(result);
        assertEquals(100L, result.get("totalPlacements"));
        assertEquals(80L, result.get("activePlacements"));
        assertEquals(15L, result.get("resignedPlacements"));
        assertEquals(5L, result.get("terminatedPlacements"));
        
        double expectedPlacementRate = (95.0 / 120.0) * 100;
        assertEquals(expectedPlacementRate, (Double) result.get("placementRate"), 0.01);
        
        verify(placementRepository).count();
        verify(placementRepository, times(3)).countByStatus(any());
        verify(placementRepository).countGraduatedStudents();
        verify(placementRepository).countPlacedStudents();
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getSalaryStatistics_Success() {
        // Arrange
        BigDecimal avgSalary = new BigDecimal("550000");
        when(placementRepository.getAverageSalary()).thenReturn(avgSalary);
        when(placementRepository.getSalaryRange()).thenReturn(Arrays.asList(new Object[]{new BigDecimal("300000"), new BigDecimal("1200000")}));
        when(placementRepository.getSalaryRangeDistribution()).thenReturn(Arrays.asList(new Object[]{20L, 50L, 30L}));
        
        // Act
        Map<String, Object> result = placementService.getSalaryStatistics();
        
        // Assert
        assertNotNull(result);
        assertEquals(avgSalary, result.get("averageSalary"));
        assertEquals(new BigDecimal("300000"), result.get("minSalary"));
        assertEquals(new BigDecimal("1200000"), result.get("maxSalary"));
        assertEquals(20L, result.get("lowSalaryCount"));
        assertEquals(50L, result.get("mediumSalaryCount"));
        assertEquals(30L, result.get("highSalaryCount"));
        
        verify(placementRepository).getAverageSalary();
        verify(placementRepository).getSalaryRange();
        verify(placementRepository).getSalaryRangeDistribution();
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPlacementTrends_Success() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusYears(1);
        List<Object[]> trends = new ArrayList<>();
        trends.add(new Object[]{2024, 1, 15L});
        trends.add(new Object[]{2024, 2, 20L});
        when(placementRepository.getMonthlyPlacementTrends(startDate)).thenReturn(trends);
        
        // Act
        List<Map<String, Object>> result = placementService.getPlacementTrends(startDate);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2024, result.get(0).get("year"));
        assertEquals(1, result.get(0).get("month"));
        assertEquals(15L, result.get(0).get("count"));
        
        verify(placementRepository).getMonthlyPlacementTrends(startDate);
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPlacementRateByCourse_Success() {
        // Arrange
        List<Object[]> rates = new ArrayList<>();
        rates.add(new Object[]{"Java Full Stack", 50L, 45L});
        rates.add(new Object[]{"Python Data Science", 30L, 25L});
        when(placementRepository.getPlacementRateByCourse()).thenReturn(rates);
        
        // Act
        List<Map<String, Object>> result = placementService.getPlacementRateByCourse();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java Full Stack", result.get(0).get("courseName"));
        assertEquals(50L, result.get(0).get("totalGraduates"));
        assertEquals(45L, result.get(0).get("placedStudents"));
        assertEquals(90.0, (Double) result.get(0).get("placementRate"), 0.01);
        
        verify(placementRepository).getPlacementRateByCourse();
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getCompanyPerformanceStats_Success() {
        // Arrange
        List<Object[]> performance = new ArrayList<>();
        performance.add(new Object[]{company, 25L, new BigDecimal("600000")});
        when(placementRepository.getCompanyPerformanceStats()).thenReturn(performance);
        
        // Act
        List<Map<String, Object>> result = placementService.getCompanyPerformanceStats();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(companyId, result.get(0).get("companyId"));
        assertEquals("Tech Corp", result.get(0).get("companyName"));
        assertEquals(25L, result.get(0).get("placementCount"));
        assertEquals(new BigDecimal("600000"), result.get(0).get("averageSalary"));
        
        verify(placementRepository).getCompanyPerformanceStats();
    }
}