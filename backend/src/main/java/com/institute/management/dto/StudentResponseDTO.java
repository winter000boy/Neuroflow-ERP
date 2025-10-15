package com.institute.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.institute.management.entity.Student;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for student response data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentResponseDTO {
    
    private UUID id;
    private String enrollmentNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    
    private String address;
    private BatchBasicDTO batch;
    private Student.StudentStatus status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate enrollmentDate;
    
    private LeadBasicDTO lead;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate graduationDate;
    
    private String finalGrade;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedDate;
    
    private List<PlacementBasicDTO> placements;
    private List<StatusHistoryDTO> statusHistory;
    
    // Constructors
    public StudentResponseDTO() {}
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }
    
    public void setEnrollmentNumber(String enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
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
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public BatchBasicDTO getBatch() {
        return batch;
    }
    
    public void setBatch(BatchBasicDTO batch) {
        this.batch = batch;
    }
    
    public Student.StudentStatus getStatus() {
        return status;
    }
    
    public void setStatus(Student.StudentStatus status) {
        this.status = status;
    }
    
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }
    
    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
    
    public LeadBasicDTO getLead() {
        return lead;
    }
    
    public void setLead(LeadBasicDTO lead) {
        this.lead = lead;
    }
    
    public LocalDate getGraduationDate() {
        return graduationDate;
    }
    
    public void setGraduationDate(LocalDate graduationDate) {
        this.graduationDate = graduationDate;
    }
    
    public String getFinalGrade() {
        return finalGrade;
    }
    
    public void setFinalGrade(String finalGrade) {
        this.finalGrade = finalGrade;
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
    
    public List<PlacementBasicDTO> getPlacements() {
        return placements;
    }
    
    public void setPlacements(List<PlacementBasicDTO> placements) {
        this.placements = placements;
    }
    
    public List<StatusHistoryDTO> getStatusHistory() {
        return statusHistory;
    }
    
    public void setStatusHistory(List<StatusHistoryDTO> statusHistory) {
        this.statusHistory = statusHistory;
    }
    
    /**
     * DTO for student status history
     */
    public static class StatusHistoryDTO {
        private Student.StudentStatus status;
        
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime changeDate;
        
        private String notes;
        
        // Constructors
        public StatusHistoryDTO() {}
        
        public StatusHistoryDTO(Student.StudentStatus status, LocalDateTime changeDate, String notes) {
            this.status = status;
            this.changeDate = changeDate;
            this.notes = notes;
        }
        
        // Getters and Setters
        public Student.StudentStatus getStatus() {
            return status;
        }
        
        public void setStatus(Student.StudentStatus status) {
            this.status = status;
        }
        
        public LocalDateTime getChangeDate() {
            return changeDate;
        }
        
        public void setChangeDate(LocalDateTime changeDate) {
            this.changeDate = changeDate;
        }
        
        public String getNotes() {
            return notes;
        }
        
        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
}