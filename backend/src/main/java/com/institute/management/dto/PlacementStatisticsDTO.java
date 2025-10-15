package com.institute.management.dto;

import java.math.BigDecimal;

/**
 * DTO for placement statistics
 */
public class PlacementStatisticsDTO {
    
    private long totalPlacements;
    private long activePlacements;
    private long resignedPlacements;
    private long terminatedPlacements;
    private long completedPlacements;
    private double placementRate;
    private BigDecimal averageSalary;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private long placedStudents;
    private long totalGraduates;
    
    // Constructors
    public PlacementStatisticsDTO() {}
    
    // Getters and Setters
    public long getTotalPlacements() {
        return totalPlacements;
    }
    
    public void setTotalPlacements(long totalPlacements) {
        this.totalPlacements = totalPlacements;
    }
    
    public long getActivePlacements() {
        return activePlacements;
    }
    
    public void setActivePlacements(long activePlacements) {
        this.activePlacements = activePlacements;
    }
    
    public long getResignedPlacements() {
        return resignedPlacements;
    }
    
    public void setResignedPlacements(long resignedPlacements) {
        this.resignedPlacements = resignedPlacements;
    }
    
    public long getTerminatedPlacements() {
        return terminatedPlacements;
    }
    
    public void setTerminatedPlacements(long terminatedPlacements) {
        this.terminatedPlacements = terminatedPlacements;
    }
    
    public long getCompletedPlacements() {
        return completedPlacements;
    }
    
    public void setCompletedPlacements(long completedPlacements) {
        this.completedPlacements = completedPlacements;
    }
    
    public double getPlacementRate() {
        return placementRate;
    }
    
    public void setPlacementRate(double placementRate) {
        this.placementRate = placementRate;
    }
    
    public BigDecimal getAverageSalary() {
        return averageSalary;
    }
    
    public void setAverageSalary(BigDecimal averageSalary) {
        this.averageSalary = averageSalary;
    }
    
    public BigDecimal getMinSalary() {
        return minSalary;
    }
    
    public void setMinSalary(BigDecimal minSalary) {
        this.minSalary = minSalary;
    }
    
    public BigDecimal getMaxSalary() {
        return maxSalary;
    }
    
    public void setMaxSalary(BigDecimal maxSalary) {
        this.maxSalary = maxSalary;
    }
    
    public long getPlacedStudents() {
        return placedStudents;
    }
    
    public void setPlacedStudents(long placedStudents) {
        this.placedStudents = placedStudents;
    }
    
    public long getTotalGraduates() {
        return totalGraduates;
    }
    
    public void setTotalGraduates(long totalGraduates) {
        this.totalGraduates = totalGraduates;
    }
}