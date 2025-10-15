package com.institute.management.dto;

import com.institute.management.entity.Batch;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for updating an existing batch
 */
public class BatchUpdateRequestDTO {
    
    @NotBlank(message = "Batch name is required")
    @Size(max = 100, message = "Batch name must not exceed 100 characters")
    private String name;
    
    @NotNull(message = "Course is required")
    private UUID courseId;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 100, message = "Capacity must not exceed 100")
    private Integer capacity;
    
    @NotNull(message = "Status is required")
    private Batch.BatchStatus status;
    
    private UUID instructorId;
    
    // Constructors
    public BatchUpdateRequestDTO() {}
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public UUID getCourseId() {
        return courseId;
    }
    
    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public Batch.BatchStatus getStatus() {
        return status;
    }
    
    public void setStatus(Batch.BatchStatus status) {
        this.status = status;
    }
    
    public UUID getInstructorId() {
        return instructorId;
    }
    
    public void setInstructorId(UUID instructorId) {
        this.instructorId = instructorId;
    }
}