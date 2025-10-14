package com.institute.management.service;

import com.institute.management.entity.Company;
import com.institute.management.entity.Employee;
import com.institute.management.entity.Placement;
import com.institute.management.entity.Student;
import com.institute.management.repository.PlacementRepository;
import com.institute.management.security.AuthorizationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class PlacementServiceAuthorizationTest extends AuthorizationTestBase {
    
    @Mock
    private PlacementRepository placementRepository;
    
    @InjectMocks
    private PlacementService placementService;
    
    private Placement testPlacement;
    private UUID placementId;
    
    @BeforeEach
    void setUp() {
        placementId = UUID.randomUUID();
        testPlacement = new Placement();
        testPlacement.setId(placementId);
        Student student = new Student();
        student.setId(UUID.randomUUID());
        testPlacement.setStudent(student);
        
        Company company = new Company();
        company.setId(UUID.randomUUID());
        testPlacement.setCompany(company);
        testPlacement.setPosition("Software Developer");
        testPlacement.setSalary(new BigDecimal("50000"));
        testPlacement.setPlacementDate(LocalDate.now());
        testPlacement.setStatus(Placement.PlacementStatus.PLACED);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanCreatePlacement() {
        when(placementRepository.save(any(Placement.class))).thenReturn(testPlacement);
        
        Placement result = placementService.createPlacement(testPlacement);
        
        assertNotNull(result);
        verify(placementRepository).save(testPlacement);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCanCreatePlacement() {
        when(placementRepository.save(any(Placement.class))).thenReturn(testPlacement);
        
        Placement result = placementService.createPlacement(testPlacement);
        
        assertNotNull(result);
        verify(placementRepository).save(testPlacement);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotCreatePlacement() {
        assertThrows(AccessDeniedException.class, () -> {
            placementService.createPlacement(testPlacement);
        });
        
        verify(placementRepository, never()).save(any(Placement.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotCreatePlacement() {
        assertThrows(AccessDeniedException.class, () -> {
            placementService.createPlacement(testPlacement);
        });
        
        verify(placementRepository, never()).save(any(Placement.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCannotCreatePlacement() {
        assertThrows(AccessDeniedException.class, () -> {
            placementService.createPlacement(testPlacement);
        });
        
        verify(placementRepository, never()).save(any(Placement.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanViewPlacement() {
        when(placementRepository.findById(placementId)).thenReturn(Optional.of(testPlacement));
        
        Optional<Placement> result = placementService.getPlacementById(placementId);
        
        assertTrue(result.isPresent());
        verify(placementRepository).findById(placementId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCanViewPlacement() {
        when(placementRepository.findById(placementId)).thenReturn(Optional.of(testPlacement));
        
        Optional<Placement> result = placementService.getPlacementById(placementId);
        
        assertTrue(result.isPresent());
        verify(placementRepository).findById(placementId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotViewPlacement() {
        assertThrows(AccessDeniedException.class, () -> {
            placementService.getPlacementById(placementId);
        });
        
        verify(placementRepository, never()).findById(any(UUID.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotViewPlacement() {
        assertThrows(AccessDeniedException.class, () -> {
            placementService.getPlacementById(placementId);
        });
        
        verify(placementRepository, never()).findById(any(UUID.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCannotViewPlacement() {
        assertThrows(AccessDeniedException.class, () -> {
            placementService.getPlacementById(placementId);
        });
        
        verify(placementRepository, never()).findById(any(UUID.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanUpdatePlacement() {
        when(placementRepository.findById(placementId)).thenReturn(Optional.of(testPlacement));
        when(placementRepository.save(any(Placement.class))).thenReturn(testPlacement);
        
        Placement updatedPlacement = new Placement();
        updatedPlacement.setPosition("Senior Developer");
        updatedPlacement.setSalary(new BigDecimal("60000"));
        
        Placement result = placementService.updatePlacement(placementId, updatedPlacement);
        
        assertNotNull(result);
        verify(placementRepository).findById(placementId);
        verify(placementRepository).save(any(Placement.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCanUpdatePlacement() {
        when(placementRepository.findById(placementId)).thenReturn(Optional.of(testPlacement));
        when(placementRepository.save(any(Placement.class))).thenReturn(testPlacement);
        
        Placement updatedPlacement = new Placement();
        updatedPlacement.setPosition("Senior Developer");
        
        Placement result = placementService.updatePlacement(placementId, updatedPlacement);
        
        assertNotNull(result);
        verify(placementRepository).findById(placementId);
        verify(placementRepository).save(any(Placement.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotUpdatePlacement() {
        Placement updatedPlacement = new Placement();
        updatedPlacement.setPosition("Senior Developer");
        
        assertThrows(AccessDeniedException.class, () -> {
            placementService.updatePlacement(placementId, updatedPlacement);
        });
        
        verify(placementRepository, never()).findById(any(UUID.class));
        verify(placementRepository, never()).save(any(Placement.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanDeletePlacement() {
        when(placementRepository.existsById(placementId)).thenReturn(true);
        
        assertDoesNotThrow(() -> {
            placementService.deletePlacement(placementId);
        });
        
        verify(placementRepository).existsById(placementId);
        verify(placementRepository).deleteById(placementId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCannotDeletePlacement() {
        assertThrows(AccessDeniedException.class, () -> {
            placementService.deletePlacement(placementId);
        });
        
        verify(placementRepository, never()).existsById(any(UUID.class));
        verify(placementRepository, never()).deleteById(any(UUID.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanViewPlacementStatistics() {
        when(placementRepository.count()).thenReturn(100L);
        when(placementRepository.countByStatus(Placement.PlacementStatus.PLACED)).thenReturn(80L);
        when(placementRepository.getAverageSalary()).thenReturn(new BigDecimal("55000"));
        
        PlacementService.PlacementStatistics stats = placementService.getPlacementStatistics();
        
        assertNotNull(stats);
        assertEquals(100L, stats.getTotalPlacements());
        assertEquals(80L, stats.getActivePlacements());
        assertEquals(new BigDecimal("55000"), stats.getAverageSalary());
        
        verify(placementRepository).count();
        verify(placementRepository).countByStatus(Placement.PlacementStatus.PLACED);
        verify(placementRepository).getAverageSalary();
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCanViewPlacementStatistics() {
        when(placementRepository.count()).thenReturn(100L);
        when(placementRepository.countByStatus(Placement.PlacementStatus.PLACED)).thenReturn(80L);
        when(placementRepository.getAverageSalary()).thenReturn(new BigDecimal("55000"));
        
        PlacementService.PlacementStatistics stats = placementService.getPlacementStatistics();
        
        assertNotNull(stats);
        verify(placementRepository).count();
        verify(placementRepository).countByStatus(Placement.PlacementStatus.PLACED);
        verify(placementRepository).getAverageSalary();
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotViewPlacementStatistics() {
        assertThrows(AccessDeniedException.class, () -> {
            placementService.getPlacementStatistics();
        });
        
        verify(placementRepository, never()).count();
        verify(placementRepository, never()).countByStatus(any());
        verify(placementRepository, never()).getAverageSalary();
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanUpdatePlacementStatus() {
        when(placementRepository.findById(placementId)).thenReturn(Optional.of(testPlacement));
        when(placementRepository.save(any(Placement.class))).thenReturn(testPlacement);
        
        Placement result = placementService.updatePlacementStatus(placementId, Placement.PlacementStatus.TERMINATED);
        
        assertNotNull(result);
        verify(placementRepository).findById(placementId);
        verify(placementRepository).save(any(Placement.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCanUpdatePlacementStatus() {
        when(placementRepository.findById(placementId)).thenReturn(Optional.of(testPlacement));
        when(placementRepository.save(any(Placement.class))).thenReturn(testPlacement);
        
        Placement result = placementService.updatePlacementStatus(placementId, Placement.PlacementStatus.TERMINATED);
        
        assertNotNull(result);
        verify(placementRepository).findById(placementId);
        verify(placementRepository).save(any(Placement.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotUpdatePlacementStatus() {
        assertThrows(AccessDeniedException.class, () -> {
            placementService.updatePlacementStatus(placementId, Placement.PlacementStatus.TERMINATED);
        });
        
        verify(placementRepository, never()).findById(any(UUID.class));
        verify(placementRepository, never()).save(any(Placement.class));
    }
}