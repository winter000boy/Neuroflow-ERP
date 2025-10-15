package com.institute.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.institute.management.entity.Course;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for course response data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseResponseDTO {
    
    private UUID id;
    private String name;
    private String description;
    private Integer durationMonths;
    private BigDecimal fees;
    private Course.CourseStatus status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedDate;
    
    private List<BatchBasicDTO> batches;
    private Integer batchCount;
    private Integer totalEnrollments;
    
    // Constructors
    public CourseResponseDTO() {}
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getDurationMonths() {
        return durationMonths;
    }
    
    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }
    
    public BigDecimal getFees() {
        return fees;
    }
    
    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }
    
    public Course.CourseStatus getStatus() {
        return status;
    }
    
    public void setStatus(Course.CourseStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    public List<BatchBasicDTO> getBatches() {
        return batches;
    }
    
    public void setBatches(List<BatchBasicDTO> batches) {
        this.batches = batches;
    }
    
    public Integer getBatchCount() {
        return batchCount;
    }
    
    public void setBatchCount(Integer batchCount) {
        this.batchCount = batchCount;
    }
    
    public Integer getTotalEnrollments() {
        return totalEnrollments;
    }
    
    public void setTotalEnrollments(Integer totalEnrollments) {
        this.totalEnrollments = totalEnrollments;
    }
}