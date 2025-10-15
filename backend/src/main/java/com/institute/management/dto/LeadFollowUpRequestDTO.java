package com.institute.management.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * DTO for adding a follow-up to a lead
 */
public class LeadFollowUpRequestDTO {
    
    @NotBlank(message = "Follow-up notes are required")
    private String notes;
    
    private LocalDateTime nextFollowUpDate;
    
    private String nextAction;
    
    // Constructors
    public LeadFollowUpRequestDTO() {}
    
    // Getters and Setters
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getNextFollowUpDate() {
        return nextFollowUpDate;
    }
    
    public void setNextFollowUpDate(LocalDateTime nextFollowUpDate) {
        this.nextFollowUpDate = nextFollowUpDate;
    }
    
    public String getNextAction() {
        return nextAction;
    }
    
    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }
}