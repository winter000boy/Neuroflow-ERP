package com.institute.management.dto;

import com.institute.management.entity.Batch;

import java.util.UUID;

public class BatchUtilizationDTO {
    
    private UUID batchId;
    private String batchName;
    private String courseName;
    private Integer capacity;
    private Integer currentEnrollment;
    private Double utilizationPercentage;
    private Integer availableSlots;
    private Batch.BatchStatus status;
    
    // Constructors
    public BatchUtilizationDTO() {}
    
    public BatchUtilizationDTO(UUID batchId, String batchName, String courseName, 
                              Integer capacity, Integer currentEnrollment, 
                              Double utilizationPercentage, Integer availableSlots, 
                              Batch.BatchStatus status) {
        this.batchId = batchId;
        this.batchName = batchName;
        this.courseName = courseName;
        this.capacity = capacity;
        this.currentEnrollment = currentEnrollment;
        this.utilizationPercentage = utilizationPercentage;
        this.availableSlots = availableSlots;
        this.status = status;
    }
    
    // Getters and Setters
    public UUID getBatchId() {
        return batchId;
    }
    
    public void setBatchId(UUID batchId) {
        this.batchId = batchId;
    }
    
    public String getBatchName() {
        return batchName;
    }
    
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public Integer getCurrentEnrollment() {
        return currentEnrollment;
    }
    
    public void setCurrentEnrollment(Integer currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }
    
    public Double getUtilizationPercentage() {
        return utilizationPercentage;
    }
    
    public void setUtilizationPercentage(Double utilizationPercentage) {
        this.utilizationPercentage = utilizationPercentage;
    }
    
    public Integer getAvailableSlots() {
        return availableSlots;
    }
    
    public void setAvailableSlots(Integer availableSlots) {
        this.availableSlots = availableSlots;
    }
    
    public Batch.BatchStatus getStatus() {
        return status;
    }
    
    public void setStatus(Batch.BatchStatus status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "BatchUtilizationDTO{" +
                "batchId=" + batchId +
                ", batchName='" + batchName + '\'' +
                ", courseName='" + courseName + '\'' +
                ", capacity=" + capacity +
                ", currentEnrollment=" + currentEnrollment +
                ", utilizationPercentage=" + utilizationPercentage +
                ", availableSlots=" + availableSlots +
                ", status=" + status +
                '}';
    }
}