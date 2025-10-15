package com.institute.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.institute.management.entity.Lead;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for lead response data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeadResponseDTO {
    
    private UUID id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String courseInterest;
    private String source;
    private Lead.LeadStatus status;
    private EmployeeBasicDTO assignedCounsellor;
    private String notes;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime nextFollowUpDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime convertedDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedDate;
    
    private List<FollowUpDTO> followUps;
    private List<StudentBasicDTO> convertedStudents;
    
    // Constructors
    public LeadResponseDTO() {}
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getCourseInterest() {
        return courseInterest;
    }
    
    public void setCourseInterest(String courseInterest) {
        this.courseInterest = courseInterest;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public Lead.LeadStatus getStatus() {
        return status;
    }
    
    public void setStatus(Lead.LeadStatus status) {
        this.status = status;
    }
    
    public EmployeeBasicDTO getAssignedCounsellor() {
        return assignedCounsellor;
    }
    
    public void setAssignedCounsellor(EmployeeBasicDTO assignedCounsellor) {
        this.assignedCounsellor = assignedCounsellor;
    }
    
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
    
    public LocalDateTime getConvertedDate() {
        return convertedDate;
    }
    
    public void setConvertedDate(LocalDateTime convertedDate) {
        this.convertedDate = convertedDate;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    public List<FollowUpDTO> getFollowUps() {
        return followUps;
    }
    
    public void setFollowUps(List<FollowUpDTO> followUps) {
        this.followUps = followUps;
    }
    
    public List<StudentBasicDTO> getConvertedStudents() {
        return convertedStudents;
    }
    
    public void setConvertedStudents(List<StudentBasicDTO> convertedStudents) {
        this.convertedStudents = convertedStudents;
    }
    
    /**
     * DTO for follow-up information
     */
    public static class FollowUpDTO {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime date;
        private String notes;
        private String nextAction;
        
        // Constructors
        public FollowUpDTO() {}
        
        public FollowUpDTO(LocalDateTime date, String notes, String nextAction) {
            this.date = date;
            this.notes = notes;
            this.nextAction = nextAction;
        }
        
        // Getters and Setters
        public LocalDateTime getDate() {
            return date;
        }
        
        public void setDate(LocalDateTime date) {
            this.date = date;
        }
        
        public String getNotes() {
            return notes;
        }
        
        public void setNotes(String notes) {
            this.notes = notes;
        }
        
        public String getNextAction() {
            return nextAction;
        }
        
        public void setNextAction(String nextAction) {
            this.nextAction = nextAction;
        }
    }
}