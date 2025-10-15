package com.institute.management.service;

import com.institute.management.dto.BatchUtilizationDTO;
import com.institute.management.entity.Batch;
import com.institute.management.entity.Course;
import com.institute.management.entity.Employee;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.exception.BatchCapacityExceededException;
import com.institute.management.exception.ValidationException;
import com.institute.management.repository.BatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BatchService {
    
    @Autowired
    private BatchRepository batchRepository;
    
    /**
     * Create a new batch - Only ADMIN and OPERATIONS can create batches
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public Batch createBatch(Batch batch) {
        return batchRepository.save(batch);
    }
    
    /**
     * Update an existing batch - Only ADMIN and OPERATIONS can update batches
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public Batch updateBatch(UUID id, Batch batchDetails) {
        Batch batch = batchRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + id));
        
        // Validate capacity constraints if capacity is being updated
        if (batchDetails.getCapacity() != null && batchDetails.getCapacity() < batch.getCurrentEnrollment()) {
            throw new BatchCapacityExceededException("New capacity cannot be less than current enrollment: " + batch.getCurrentEnrollment());
        }
        
        batch.setName(batchDetails.getName());
        if (batchDetails.getCourse() != null) {
            batch.setCourse(batchDetails.getCourse());
        }
        batch.setStartDate(batchDetails.getStartDate());
        batch.setEndDate(batchDetails.getEndDate());
        if (batchDetails.getCapacity() != null) {
            batch.setCapacity(batchDetails.getCapacity());
        }
        if (batchDetails.getStatus() != null) {
            batch.setStatus(batchDetails.getStatus());
        }
        if (batchDetails.getInstructor() != null) {
            batch.setInstructor(batchDetails.getInstructor());
        }
        
        return batchRepository.save(batch);
    }
    
    /**
     * Get batch by ID - ADMIN, OPERATIONS, and FACULTY can view batches
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY')")
    public Optional<Batch> getBatchById(UUID id) {
        return batchRepository.findById(id);
    }
    
    /**
     * Get all batches with pagination - ADMIN, OPERATIONS, and FACULTY can view batches
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY')")
    public Page<Batch> getAllBatches(Pageable pageable) {
        return batchRepository.findAll(pageable);
    }
    
    /**
     * Get batches by course - ADMIN, OPERATIONS, and FACULTY can view batches
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY')")
    public List<Batch> getBatchesByCourse(Course course) {
        return batchRepository.findByCourse(course);
    }
    
    /**
     * Get batches by status - ADMIN, OPERATIONS, and FACULTY can view batches
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY')")
    public List<Batch> getBatchesByStatus(Batch.BatchStatus status) {
        return batchRepository.findByStatus(status);
    }
    
    /**
     * Get batches by instructor - ADMIN, OPERATIONS, and FACULTY can view batches
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY')")
    public List<Batch> getBatchesByInstructor(Employee instructor) {
        return batchRepository.findByInstructor(instructor);
    }
    
    /**
     * Update batch capacity - Only ADMIN and OPERATIONS can update capacity
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public Batch updateBatchCapacity(UUID batchId, Integer newCapacity) {
        Batch batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + batchId));
        
        // Validate that new capacity is not less than current enrollment
        if (newCapacity < batch.getCurrentEnrollment()) {
            throw new BatchCapacityExceededException("New capacity cannot be less than current enrollment: " + batch.getCurrentEnrollment());
        }
        
        batch.setCapacity(newCapacity);
        return batchRepository.save(batch);
    }
    
    /**
     * Update batch status - Only ADMIN and OPERATIONS can update status
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public Batch updateBatchStatus(UUID batchId, Batch.BatchStatus status) {
        Batch batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + batchId));
        
        batch.setStatus(status);
        return batchRepository.save(batch);
    }
    
    /**
     * Delete batch - Only ADMIN can delete batches
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBatch(UUID id) {
        Batch batch = batchRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + id));
        
        // Prevent deletion if students are enrolled
        if (batch.getCurrentEnrollment() > 0) {
            throw new ValidationException("Cannot delete batch with enrolled students");
        }
        
        batchRepository.deleteById(id);
    }
    
    /**
     * Get batches starting within date range - ADMIN, OPERATIONS, and FACULTY can view
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY')")
    public List<Batch> getBatchesByStartDateRange(LocalDate startDate, LocalDate endDate) {
        return batchRepository.findByStartDateBetween(startDate, endDate);
    }
    
    /**
     * Check if batch has available capacity
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('COUNSELLOR')")
    public boolean hasAvailableCapacity(UUID batchId) {
        Batch batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + batchId));
        
        return batch.getCurrentEnrollment() < batch.getCapacity();
    }
    
    /**
     * Get available slots in batch
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('COUNSELLOR')")
    public Integer getAvailableSlots(UUID batchId) {
        Batch batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + batchId));
        
        return batch.getCapacity() - batch.getCurrentEnrollment();
    }
    
    /**
     * Get batches with filters - ADMIN, OPERATIONS, and FACULTY can view batches
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY')")
    public Page<Batch> getBatchesWithFilters(Batch.BatchStatus status, UUID courseId, UUID instructorId, 
                                           Boolean hasAvailableSlots, String searchTerm, Pageable pageable) {
        return batchRepository.findBatchesWithFilters(status, courseId, instructorId, hasAvailableSlots, searchTerm, pageable);
    }
    
    /**
     * Get batch utilization report - ADMIN and OPERATIONS can view reports
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public List<BatchUtilizationDTO> getBatchUtilizationReport() {
        List<Object[]> results = batchRepository.getBatchUtilizationReport();
        
        return results.stream().map(result -> {
            Batch batch = (Batch) result[0];
            Double utilization = (Double) result[1];
            
            BatchUtilizationDTO dto = new BatchUtilizationDTO();
            dto.setBatchId(batch.getId());
            dto.setBatchName(batch.getName());
            dto.setCourseName(batch.getCourse().getName());
            dto.setCapacity(batch.getCapacity());
            dto.setCurrentEnrollment(batch.getCurrentEnrollment());
            dto.setUtilizationPercentage(utilization);
            dto.setAvailableSlots(batch.getAvailableSlots());
            dto.setStatus(batch.getStatus());
            
            return dto;
        }).collect(Collectors.toList());
    }
}