package com.institute.management.service;

import com.institute.management.entity.Batch;
import com.institute.management.entity.Course;
import com.institute.management.entity.Employee;
import com.institute.management.repository.BatchRepository;
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

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class BatchServiceAuthorizationTest extends AuthorizationTestBase {
    
    @Mock
    private BatchRepository batchRepository;
    
    @InjectMocks
    private BatchService batchService;
    
    private Batch testBatch;
    private UUID batchId;
    
    @BeforeEach
    void setUp() {
        batchId = UUID.randomUUID();
        testBatch = new Batch();
        testBatch.setId(batchId);
        testBatch.setName("Java Batch 2024");
        Course course = new Course();
        course.setId(UUID.randomUUID());
        testBatch.setCourse(course);
        testBatch.setStartDate(LocalDate.now());
        testBatch.setEndDate(LocalDate.now().plusMonths(6));
        testBatch.setCapacity(30);
        testBatch.setCurrentEnrollment(0);
        testBatch.setStatus(Batch.BatchStatus.PLANNED);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanCreateBatch() {
        when(batchRepository.save(any(Batch.class))).thenReturn(testBatch);
        
        Batch result = batchService.createBatch(testBatch);
        
        assertNotNull(result);
        verify(batchRepository).save(testBatch);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCanCreateBatch() {
        when(batchRepository.save(any(Batch.class))).thenReturn(testBatch);
        
        Batch result = batchService.createBatch(testBatch);
        
        assertNotNull(result);
        verify(batchRepository).save(testBatch);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotCreateBatch() {
        assertThrows(AccessDeniedException.class, () -> {
            batchService.createBatch(testBatch);
        });
        
        verify(batchRepository, never()).save(any(Batch.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotCreateBatch() {
        assertThrows(AccessDeniedException.class, () -> {
            batchService.createBatch(testBatch);
        });
        
        verify(batchRepository, never()).save(any(Batch.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCannotCreateBatch() {
        assertThrows(AccessDeniedException.class, () -> {
            batchService.createBatch(testBatch);
        });
        
        verify(batchRepository, never()).save(any(Batch.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanViewBatch() {
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(testBatch));
        
        Optional<Batch> result = batchService.getBatchById(batchId);
        
        assertTrue(result.isPresent());
        verify(batchRepository).findById(batchId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCanViewBatch() {
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(testBatch));
        
        Optional<Batch> result = batchService.getBatchById(batchId);
        
        assertTrue(result.isPresent());
        verify(batchRepository).findById(batchId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCanViewBatch() {
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(testBatch));
        
        Optional<Batch> result = batchService.getBatchById(batchId);
        
        assertTrue(result.isPresent());
        verify(batchRepository).findById(batchId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotViewBatch() {
        assertThrows(AccessDeniedException.class, () -> {
            batchService.getBatchById(batchId);
        });
        
        verify(batchRepository, never()).findById(any(UUID.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCannotViewBatch() {
        assertThrows(AccessDeniedException.class, () -> {
            batchService.getBatchById(batchId);
        });
        
        verify(batchRepository, never()).findById(any(UUID.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanUpdateBatch() {
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(testBatch));
        when(batchRepository.save(any(Batch.class))).thenReturn(testBatch);
        
        Batch updatedBatch = new Batch();
        updatedBatch.setName("Updated Batch Name");
        
        Batch result = batchService.updateBatch(batchId, updatedBatch);
        
        assertNotNull(result);
        verify(batchRepository).findById(batchId);
        verify(batchRepository).save(any(Batch.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCanUpdateBatch() {
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(testBatch));
        when(batchRepository.save(any(Batch.class))).thenReturn(testBatch);
        
        Batch updatedBatch = new Batch();
        updatedBatch.setName("Updated Batch Name");
        
        Batch result = batchService.updateBatch(batchId, updatedBatch);
        
        assertNotNull(result);
        verify(batchRepository).findById(batchId);
        verify(batchRepository).save(any(Batch.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotUpdateBatch() {
        Batch updatedBatch = new Batch();
        updatedBatch.setName("Updated Batch Name");
        
        assertThrows(AccessDeniedException.class, () -> {
            batchService.updateBatch(batchId, updatedBatch);
        });
        
        verify(batchRepository, never()).findById(any(UUID.class));
        verify(batchRepository, never()).save(any(Batch.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanDeleteBatch() {
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(testBatch));
        
        assertDoesNotThrow(() -> {
            batchService.deleteBatch(batchId);
        });
        
        verify(batchRepository).findById(batchId);
        verify(batchRepository).deleteById(batchId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCannotDeleteBatch() {
        assertThrows(AccessDeniedException.class, () -> {
            batchService.deleteBatch(batchId);
        });
        
        verify(batchRepository, never()).findById(any(UUID.class));
        verify(batchRepository, never()).deleteById(any(UUID.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotDeleteBatch() {
        assertThrows(AccessDeniedException.class, () -> {
            batchService.deleteBatch(batchId);
        });
        
        verify(batchRepository, never()).findById(any(UUID.class));
        verify(batchRepository, never()).deleteById(any(UUID.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanUpdateBatchCapacity() {
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(testBatch));
        when(batchRepository.save(any(Batch.class))).thenReturn(testBatch);
        
        Batch result = batchService.updateBatchCapacity(batchId, 40);
        
        assertNotNull(result);
        verify(batchRepository).findById(batchId);
        verify(batchRepository).save(any(Batch.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCanUpdateBatchCapacity() {
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(testBatch));
        when(batchRepository.save(any(Batch.class))).thenReturn(testBatch);
        
        Batch result = batchService.updateBatchCapacity(batchId, 40);
        
        assertNotNull(result);
        verify(batchRepository).findById(batchId);
        verify(batchRepository).save(any(Batch.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotUpdateBatchCapacity() {
        assertThrows(AccessDeniedException.class, () -> {
            batchService.updateBatchCapacity(batchId, 40);
        });
        
        verify(batchRepository, never()).findById(any(UUID.class));
        verify(batchRepository, never()).save(any(Batch.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCanCheckAvailableCapacity() {
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(testBatch));
        
        boolean result = batchService.hasAvailableCapacity(batchId);
        
        assertTrue(result);
        verify(batchRepository).findById(batchId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCannotCheckAvailableCapacity() {
        assertThrows(AccessDeniedException.class, () -> {
            batchService.hasAvailableCapacity(batchId);
        });
        
        verify(batchRepository, never()).findById(any(UUID.class));
    }
}