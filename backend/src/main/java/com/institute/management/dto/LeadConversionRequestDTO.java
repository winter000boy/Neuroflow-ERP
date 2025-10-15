package com.institute.management.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for converting a lead to a student
 */
public class LeadConversionRequestDTO {
    
    @NotNull(message = "Enrollment date is required")
    private LocalDate enrollmentDate;
    
    private UUID batchId;
    
    private String address;
    
    private LocalDate dateOfBirth;
    
    // Additional student-specific fields that might not be in the lead
    private String additionalNotes;
    
    // Constructors
    public LeadConversionRequestDTO() {}
    
    // Getters and Setters
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }
    
    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
    
    public UUID getBatchId() {
        return batchId;
    }
    
    public void setBatchId(UUID batchId) {
        this.batchId = batchId;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getAdditionalNotes() {
        return additionalNotes;
    }
    
    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }
}