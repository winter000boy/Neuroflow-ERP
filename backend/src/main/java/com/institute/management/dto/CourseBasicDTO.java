package com.institute.management.dto;

import com.institute.management.entity.Course;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Basic DTO for course information used in relationships
 */
public class CourseBasicDTO {
    
    private UUID id;
    private String name;
    private String description;
    private Integer durationMonths;
    private BigDecimal fees;
    private Course.CourseStatus status;
    
    // Constructors
    public CourseBasicDTO() {}
    
    public CourseBasicDTO(UUID id, String name, String description, Integer durationMonths, 
                         BigDecimal fees, Course.CourseStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationMonths = durationMonths;
        this.fees = fees;
        this.status = status;
    }
    
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
}