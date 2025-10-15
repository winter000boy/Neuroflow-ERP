package com.institute.management.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CourseRevenueDTO {
    
    private UUID courseId;
    private String courseName;
    private BigDecimal totalRevenue;
    
    // Constructors
    public CourseRevenueDTO() {}
    
    public CourseRevenueDTO(UUID courseId, String courseName, BigDecimal totalRevenue) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.totalRevenue = totalRevenue;
    }
    
    // Getters and Setters
    public UUID getCourseId() {
        return courseId;
    }
    
    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    @Override
    public String toString() {
        return "CourseRevenueDTO{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", totalRevenue=" + totalRevenue +
                '}';
    }
}