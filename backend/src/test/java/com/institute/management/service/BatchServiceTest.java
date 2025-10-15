package com.institute.management.service;

import com.institute.management.dto.BatchUtilizationDTO;
import com.institute.management.entity.Batch;
import com.institute.management.entity.Course;
import com.institute.management.entity.Employee;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.exception.BatchCapacityExceededException;
import com.institute.management.exception.ValidationException;
import com.institute.management.repository.BatchRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchServiceTest {

    @Mock
    private BatchRepository batchRepository;

    @InjectMocks
    private BatchService batchService;

    private Batch testBatch;
    private Course testCourse;
    private Employee testInstructor;

    @BeforeEach
    void setUp() {
        // Setup test course
        testCourse = new Course();
        testCourse.setId(UUID.randomUUID());
        testCourse.setName("Java Programming");
        testCourse.setDurationMonths(6);
        testCourse.setFees(new BigDecimal("50000"));
        testCourse.setStatus(Course.CourseStatus.ACTIVE);

        // Setup test instructor
        testInstructor = new Employee();
        testInstructor.setId(UUID.randomUUID());
        testInstructor.setEmployeeCode("EMP001");
        testInstructor.setFirstName("John");
        testInstructor.setLastName("Doe");
        testInstructor.setRole(Employee.EmployeeRole.FACULTY);

        // Setup test batch
        testBatch = new Batch();
        testBatch.setId(UUID.randomUUID());
        testBatch.setName("Java Batch 2024-01");
        testBatch.setCourse(testCourse);
        testBatch.setStartDate(LocalDate.now().plusDays(30));
        testBatch.setCapacity(30);
        testBatch.setCurrentEnrollment(0);
        testBatch.setStatus(Batch.BatchStatus.PLANNED);
        testBatch.setInstructor(testInstructor);
        testBatch.setCreatedDate(LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBatch_Success() {
        when(batchRepository.save(any(Batch.class))).thenReturn(testBatch);

        Batch result = batchService.createBatch(testBatch);

        assertNotNull(result);
        assertEquals(testBatch.getName(), result.getName());
        assertEquals(testBatch.getCapacity(), result.getCapacity());
        verify(batchRepository).save(testBatch);
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void createBatch_Success_Operations() {
        when(batchRepository.save(any(Batch.class))).thenReturn(testBatch);

        Batch result = batchService.createBatch(testBatch);

        assertNotNull(result);
        assertEquals(testBatch.getName(), result.getName());
        verify(batchRepository).save(testBatch);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBatch_Success() {
        Batch updatedDetails = new Batch();
        updatedDetails.setName("Updated Batch Name");
        updatedDetails.setCourse(testCourse);
        updatedDetails.setCapacity(35);
        updatedDetails.setStatus(Batch.BatchStatus.ACTIVE);

        when(batchRepository.findById(testBatch.getId())).thenReturn(Optional.of(testBatch));
        when(batchRepository.save(any(Batch.class))).thenReturn(testBatch);

        Batch result = batchService.updateBatch(testBatch.getId(), updatedDetails);

        assertNotNull(result);
        verify(batchRepository).findById(testBatch.getId());
        verify(batchRepository).save(testBatch);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBatch_NotFound() {
        UUID batchId = UUID.randomUUID();
        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            batchService.updateBatch(batchId, testBatch);
        });

        verify(batchRepository).findById(batchId);
        verify(batchRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBatch_CapacityLessThanEnrollment() {
        testBatch.setCurrentEnrollment(20);
        Batch updatedDetails = new Batch();
        updatedDetails.setCapacity(15); // Less than current enrollment

        when(batchRepository.findById(testBatch.getId())).thenReturn(Optional.of(testBatch));

        assertThrows(BatchCapacityExceededException.class, () -> {
            batchService.updateBatch(testBatch.getId(), updatedDetails);
        });

        verify(batchRepository).findById(testBatch.getId());
        verify(batchRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getBatchById_Success() {
        when(batchRepository.findById(testBatch.getId())).thenReturn(Optional.of(testBatch));

        Optional<Batch> result = batchService.getBatchById(testBatch.getId());

        assertTrue(result.isPresent());
        assertEquals(testBatch.getId(), result.get().getId());
        verify(batchRepository).findById(testBatch.getId());
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getBatchById_NotFound() {
        UUID batchId = UUID.randomUUID();
        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        Optional<Batch> result = batchService.getBatchById(batchId);

        assertFalse(result.isPresent());
        verify(batchRepository).findById(batchId);
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void getAllBatches_Success() {
        List<Batch> batches = Arrays.asList(testBatch);
        Page<Batch> batchPage = new PageImpl<>(batches);
        Pageable pageable = PageRequest.of(0, 10);

        when(batchRepository.findAll(pageable)).thenReturn(batchPage);

        Page<Batch> result = batchService.getAllBatches(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testBatch.getId(), result.getContent().get(0).getId());
        verify(batchRepository).findAll(pageable);
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getBatchesByCourse_Success() {
        List<Batch> batches = Arrays.asList(testBatch);
        when(batchRepository.findByCourse(testCourse)).thenReturn(batches);

        List<Batch> result = batchService.getBatchesByCourse(testCourse);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBatch.getId(), result.get(0).getId());
        verify(batchRepository).findByCourse(testCourse);
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getBatchesByStatus_Success() {
        List<Batch> batches = Arrays.asList(testBatch);
        when(batchRepository.findByStatus(Batch.BatchStatus.PLANNED)).thenReturn(batches);

        List<Batch> result = batchService.getBatchesByStatus(Batch.BatchStatus.PLANNED);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBatch.getId(), result.get(0).getId());
        verify(batchRepository).findByStatus(Batch.BatchStatus.PLANNED);
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getBatchesByInstructor_Success() {
        List<Batch> batches = Arrays.asList(testBatch);
        when(batchRepository.findByInstructor(testInstructor)).thenReturn(batches);

        List<Batch> result = batchService.getBatchesByInstructor(testInstructor);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBatch.getId(), result.get(0).getId());
        verify(batchRepository).findByInstructor(testInstructor);
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void updateBatchCapacity_Success() {
        Integer newCapacity = 40;
        when(batchRepository.findById(testBatch.getId())).thenReturn(Optional.of(testBatch));
        when(batchRepository.save(any(Batch.class))).thenReturn(testBatch);

        Batch result = batchService.updateBatchCapacity(testBatch.getId(), newCapacity);

        assertNotNull(result);
        verify(batchRepository).findById(testBatch.getId());
        verify(batchRepository).save(testBatch);
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void updateBatchCapacity_NotFound() {
        UUID batchId = UUID.randomUUID();
        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            batchService.updateBatchCapacity(batchId, 40);
        });

        verify(batchRepository).findById(batchId);
        verify(batchRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void updateBatchCapacity_CapacityLessThanEnrollment() {
        testBatch.setCurrentEnrollment(25);
        Integer newCapacity = 20; // Less than current enrollment

        when(batchRepository.findById(testBatch.getId())).thenReturn(Optional.of(testBatch));

        assertThrows(BatchCapacityExceededException.class, () -> {
            batchService.updateBatchCapacity(testBatch.getId(), newCapacity);
        });

        verify(batchRepository).findById(testBatch.getId());
        verify(batchRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void updateBatchStatus_Success() {
        Batch.BatchStatus newStatus = Batch.BatchStatus.ACTIVE;
        when(batchRepository.findById(testBatch.getId())).thenReturn(Optional.of(testBatch));
        when(batchRepository.save(any(Batch.class))).thenReturn(testBatch);

        Batch result = batchService.updateBatchStatus(testBatch.getId(), newStatus);

        assertNotNull(result);
        verify(batchRepository).findById(testBatch.getId());
        verify(batchRepository).save(testBatch);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBatch_Success() {
        when(batchRepository.findById(testBatch.getId())).thenReturn(Optional.of(testBatch));
        doNothing().when(batchRepository).deleteById(testBatch.getId());

        assertDoesNotThrow(() -> {
            batchService.deleteBatch(testBatch.getId());
        });

        verify(batchRepository).findById(testBatch.getId());
        verify(batchRepository).deleteById(testBatch.getId());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBatch_NotFound() {
        UUID batchId = UUID.randomUUID();
        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            batchService.deleteBatch(batchId);
        });

        verify(batchRepository).findById(batchId);
        verify(batchRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBatch_WithEnrolledStudents() {
        testBatch.setCurrentEnrollment(10); // Has enrolled students
        when(batchRepository.findById(testBatch.getId())).thenReturn(Optional.of(testBatch));

        assertThrows(ValidationException.class, () -> {
            batchService.deleteBatch(testBatch.getId());
        });

        verify(batchRepository).findById(testBatch.getId());
        verify(batchRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getBatchesByStartDateRange_Success() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(60);
        List<Batch> batches = Arrays.asList(testBatch);

        when(batchRepository.findByStartDateBetween(startDate, endDate)).thenReturn(batches);

        List<Batch> result = batchService.getBatchesByStartDateRange(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBatch.getId(), result.get(0).getId());
        verify(batchRepository).findByStartDateBetween(startDate, endDate);
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void hasAvailableCapacity_True() {
        testBatch.setCapacity(30);
        testBatch.setCurrentEnrollment(20);
        when(batchRepository.findById(testBatch.getId())).thenReturn(Optional.of(testBatch));

        boolean result = batchService.hasAvailableCapacity(testBatch.getId());

        assertTrue(result);
        verify(batchRepository).findById(testBatch.getId());
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void hasAvailableCapacity_False() {
        testBatch.setCapacity(30);
        testBatch.setCurrentEnrollment(30);
        when(batchRepository.findById(testBatch.getId())).thenReturn(Optional.of(testBatch));

        boolean result = batchService.hasAvailableCapacity(testBatch.getId());

        assertFalse(result);
        verify(batchRepository).findById(testBatch.getId());
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getAvailableSlots_Success() {
        testBatch.setCapacity(30);
        testBatch.setCurrentEnrollment(20);
        when(batchRepository.findById(testBatch.getId())).thenReturn(Optional.of(testBatch));

        Integer result = batchService.getAvailableSlots(testBatch.getId());

        assertEquals(10, result);
        verify(batchRepository).findById(testBatch.getId());
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getBatchesWithFilters_Success() {
        List<Batch> batches = Arrays.asList(testBatch);
        Page<Batch> batchPage = new PageImpl<>(batches);
        Pageable pageable = PageRequest.of(0, 10);

        when(batchRepository.findBatchesWithFilters(any(), any(), any(), any(), any(), eq(pageable)))
                .thenReturn(batchPage);

        Page<Batch> result = batchService.getBatchesWithFilters(
                Batch.BatchStatus.PLANNED, testCourse.getId(), testInstructor.getId(),
                true, "Java", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(batchRepository).findBatchesWithFilters(any(), any(), any(), any(), any(), eq(pageable));
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void getBatchUtilizationReport_Success() {
        Object[] reportData = { testBatch, 66.67 };
        List<Object[]> reportResults = Arrays.<Object[]>asList(reportData);

        when(batchRepository.getBatchUtilizationReport()).thenReturn(reportResults);

        List<BatchUtilizationDTO> result = batchService.getBatchUtilizationReport();

        assertNotNull(result);
        assertEquals(1, result.size());

        BatchUtilizationDTO dto = result.get(0);
        assertEquals(testBatch.getId(), dto.getBatchId());
        assertEquals(testBatch.getName(), dto.getBatchName());
        assertEquals(testCourse.getName(), dto.getCourseName());
        assertEquals(66.67, dto.getUtilizationPercentage());

        verify(batchRepository).getBatchUtilizationReport();
    }
}