package com.institute.management.dto;

import java.math.BigDecimal;

public class CourseStatisticsDTO {
    
    private Long totalCourses;
    private Long activeCourses;
    private Long inactiveCourses;
    private Long archivedCourses;
    private Double averageDuration;
    private BigDecimal averageFees;
    private BigDecimal minFees;
    private BigDecimal maxFees;
    
    // Constructors
    public CourseStatisticsDTO() {}
    
    public CourseStatisticsDTO(Long totalCourses, Long activeCourses, Long inactiveCourses, 
                              Long archivedCourses, Double averageDuration, BigDecimal averageFees,
                              BigDecimal minFees, BigDecimal maxFees) {
        this.totalCourses = totalCourses;
        this.activeCourses = activeCourses;
        this.inactiveCourses = inactiveCourses;
        this.archivedCourses = archivedCourses;
        this.averageDuration = averageDuration;
        this.averageFees = averageFees;
        this.minFees = minFees;
        this.maxFees = maxFees;
    }
    
    // Getters and Setters
    public Long getTotalCourses() {
        return totalCourses;
    }
    
    public void setTotalCourses(Long totalCourses) {
        this.totalCourses = totalCourses;
    }
    
    public Long getActiveCourses() {
        return activeCourses;
    }
    
    public void setActiveCourses(Long activeCourses) {
        this.activeCourses = activeCourses;
    }
    
    public Long getInactiveCourses() {
        return inactiveCourses;
    }
    
    public void setInactiveCourses(Long inactiveCourses) {
        this.inactiveCourses = inactiveCourses;
    }
    
    public Long getArchivedCourses() {
        return archivedCourses;
    }
    
    public void setArchivedCourses(Long archivedCourses) {
        this.archivedCourses = archivedCourses;
    }
    
    public Double getAverageDuration() {
        return averageDuration;
    }
    
    public void setAverageDuration(Double averageDuration) {
        this.averageDuration = averageDuration;
    }
    
    public BigDecimal getAverageFees() {
        return averageFees;
    }
    
    public void setAverageFees(BigDecimal averageFees) {
        this.averageFees = averageFees;
    }
    
    public BigDecimal getMinFees() {
        return minFees;
    }
    
    public void setMinFees(BigDecimal minFees) {
        this.minFees = minFees;
    }
    
    public BigDecimal getMaxFees() {
        return maxFees;
    }
    
    public void setMaxFees(BigDecimal maxFees) {
        this.maxFees = maxFees;
    }
    
    @Override
    public String toString() {
        return "CourseStatisticsDTO{" +
                "totalCourses=" + totalCourses +
                ", activeCourses=" + activeCourses +
                ", inactiveCourses=" + inactiveCourses +
                ", archivedCourses=" + archivedCourses +
                ", averageDuration=" + averageDuration +
                ", averageFees=" + averageFees +
                ", minFees=" + minFees +
                ", maxFees=" + maxFees +
                '}';
    }
}