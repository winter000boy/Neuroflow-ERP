package com.institute.management.dto;

import com.institute.management.entity.Course;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO for updating an existing course
 */
public class CourseUpdateRequestDTO {
    
    @NotBlank(message = "Course name is required")
    @Size(max = 100, message = "Course name must not exceed 100 characters")
    private String name;
    
    private String description;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 month")
    @Max(value = 60, message = "Duration must not exceed 60 months")
    private Integer durationMonths;
    
    @NotNull(message = "Fees is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Fees must be greater than 0")
    private BigDecimal fees;
    
    @NotNull(message = "Status is required")
    private Course.CourseStatus status;
    
    // Constructors
    public CourseUpdateRequestDTO() {}
    
    // Getters and Setters
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
}