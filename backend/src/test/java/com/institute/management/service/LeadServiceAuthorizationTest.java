package com.institute.management.service;

import com.institute.management.entity.Employee;
import com.institute.management.entity.Lead;
import com.institute.management.repository.LeadRepository;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class LeadServiceAuthorizationTest extends AuthorizationTestBase {
    
    @Mock
    private LeadRepository leadRepository;
    
    @InjectMocks
    private LeadService leadService;
    
    private Lead testLead;
    private UUID leadId;
    
    @BeforeEach
    void setUp() {
        leadId = UUID.randomUUID();
        testLead = new Lead();
        testLead.setId(leadId);
        testLead.setFirstName("John");
        testLead.setLastName("Doe");
        testLead.setEmail("john.doe@example.com");
        testLead.setPhone("1234567890");
        testLead.setStatus(Lead.LeadStatus.NEW);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanCreateLead() {
        when(leadRepository.save(any(Lead.class))).thenReturn(testLead);
        
        Lead result = leadService.createLead(testLead);
        
        assertNotNull(result);
        verify(leadRepository).save(testLead);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCanCreateLead() {
        when(leadRepository.save(any(Lead.class))).thenReturn(testLead);
        
        Lead result = leadService.createLead(testLead);
        
        assertNotNull(result);
        verify(leadRepository).save(testLead);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotCreateLead() {
        assertThrows(AccessDeniedException.class, () -> {
            leadService.createLead(testLead);
        });
        
        verify(leadRepository, never()).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCannotCreateLead() {
        assertThrows(AccessDeniedException.class, () -> {
            leadService.createLead(testLead);
        });
        
        verify(leadRepository, never()).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCannotCreateLead() {
        assertThrows(AccessDeniedException.class, () -> {
            leadService.createLead(testLead);
        });
        
        verify(leadRepository, never()).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanViewLead() {
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        
        Optional<Lead> result = leadService.getLeadById(leadId);
        
        assertTrue(result.isPresent());
        verify(leadRepository).findById(leadId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCanViewLead() {
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        
        Optional<Lead> result = leadService.getLeadById(leadId);
        
        assertTrue(result.isPresent());
        verify(leadRepository).findById(leadId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotViewLead() {
        assertThrows(AccessDeniedException.class, () -> {
            leadService.getLeadById(leadId);
        });
        
        verify(leadRepository, never()).findById(any(UUID.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanUpdateLead() {
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        when(leadRepository.save(any(Lead.class))).thenReturn(testLead);
        
        Lead updatedLead = new Lead();
        updatedLead.setFirstName("Jane");
        updatedLead.setLastName("Smith");
        
        Lead result = leadService.updateLead(leadId, updatedLead);
        
        assertNotNull(result);
        verify(leadRepository).findById(leadId);
        verify(leadRepository).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCanUpdateLead() {
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        when(leadRepository.save(any(Lead.class))).thenReturn(testLead);
        
        Lead updatedLead = new Lead();
        updatedLead.setFirstName("Jane");
        updatedLead.setLastName("Smith");
        
        Lead result = leadService.updateLead(leadId, updatedLead);
        
        assertNotNull(result);
        verify(leadRepository).findById(leadId);
        verify(leadRepository).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotUpdateLead() {
        Lead updatedLead = new Lead();
        updatedLead.setFirstName("Jane");
        
        assertThrows(AccessDeniedException.class, () -> {
            leadService.updateLead(leadId, updatedLead);
        });
        
        verify(leadRepository, never()).findById(any(UUID.class));
        verify(leadRepository, never()).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanDeleteLead() {
        when(leadRepository.existsById(leadId)).thenReturn(true);
        
        assertDoesNotThrow(() -> {
            leadService.deleteLead(leadId);
        });
        
        verify(leadRepository).existsById(leadId);
        verify(leadRepository).deleteById(leadId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCanDeleteLead() {
        when(leadRepository.existsById(leadId)).thenReturn(true);
        
        assertDoesNotThrow(() -> {
            leadService.deleteLead(leadId);
        });
        
        verify(leadRepository).existsById(leadId);
        verify(leadRepository).deleteById(leadId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotDeleteLead() {
        assertThrows(AccessDeniedException.class, () -> {
            leadService.deleteLead(leadId);
        });
        
        verify(leadRepository, never()).existsById(any(UUID.class));
        verify(leadRepository, never()).deleteById(any(UUID.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanConvertLead() {
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        when(leadRepository.save(any(Lead.class))).thenReturn(testLead);
        
        Lead result = leadService.convertLead(leadId);
        
        assertNotNull(result);
        assertEquals(Lead.LeadStatus.CONVERTED, result.getStatus());
        verify(leadRepository).findById(leadId);
        verify(leadRepository).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCanConvertLead() {
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        when(leadRepository.save(any(Lead.class))).thenReturn(testLead);
        
        Lead result = leadService.convertLead(leadId);
        
        assertNotNull(result);
        assertEquals(Lead.LeadStatus.CONVERTED, result.getStatus());
        verify(leadRepository).findById(leadId);
        verify(leadRepository).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCannotConvertLead() {
        assertThrows(AccessDeniedException.class, () -> {
            leadService.convertLead(leadId);
        });
        
        verify(leadRepository, never()).findById(any(UUID.class));
        verify(leadRepository, never()).save(any(Lead.class));
    }
}