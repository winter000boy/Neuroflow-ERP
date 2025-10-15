package com.institute.management.dto;

import java.util.UUID;

public class CourseEnrollmentStatsDTO {
    
    private UUID courseId;
    private String courseName;
    private Integer totalEnrollments;
    
    // Constructors
    public CourseEnrollmentStatsDTO() {}
    
    public CourseEnrollmentStatsDTO(UUID courseId, String courseName, Integer totalEnrollments) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.totalEnrollments = totalEnrollments;
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
    
    public Integer getTotalEnrollments() {
        return totalEnrollments;
    }
    
    public void setTotalEnrollments(Integer totalEnrollments) {
        this.totalEnrollments = totalEnrollments;
    }
    
    @Override
    public String toString() {
        return "CourseEnrollmentStatsDTO{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", totalEnrollments=" + totalEnrollments +
                '}';
    }
}