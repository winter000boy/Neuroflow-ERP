package com.institute.management.dto;

import java.util.UUID;

public class BatchAvailabilityDTO {
    
    private UUID batchId;
    private Boolean hasAvailableSlots;
    private Integer availableSlots;
    
    // Constructors
    public BatchAvailabilityDTO() {}
    
    public BatchAvailabilityDTO(UUID batchId, Boolean hasAvailableSlots, Integer availableSlots) {
        this.batchId = batchId;
        this.hasAvailableSlots = hasAvailableSlots;
        this.availableSlots = availableSlots;
    }
    
    // Getters and Setters
    public UUID getBatchId() {
        return batchId;
    }
    
    public void setBatchId(UUID batchId) {
        this.batchId = batchId;
    }
    
    public Boolean getHasAvailableSlots() {
        return hasAvailableSlots;
    }
    
    public void setHasAvailableSlots(Boolean hasAvailableSlots) {
        this.hasAvailableSlots = hasAvailableSlots;
    }
    
    public Integer getAvailableSlots() {
        return availableSlots;
    }
    
    public void setAvailableSlots(Integer availableSlots) {
        this.availableSlots = availableSlots;
    }
    
    @Override
    public String toString() {
        return "BatchAvailabilityDTO{" +
                "batchId=" + batchId +
                ", hasAvailableSlots=" + hasAvailableSlots +
                ", availableSlots=" + availableSlots +
                '}';
    }
}