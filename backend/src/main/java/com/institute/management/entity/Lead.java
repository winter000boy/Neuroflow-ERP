package com.institute.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "leads",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "email")
       })
public class Lead {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "first_name", nullable = false, length = 50)
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 50)
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @Column(name = "email", length = 100, unique = true)
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Column(name = "phone", nullable = false, length = 15)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phone;
    
    @Column(name = "course_interest", length = 100)
    @Size(max = 100, message = "Course interest must not exceed 100 characters")
    private String courseInterest;
    
    @Column(name = "source", length = 50)
    @Size(max = 50, message = "Source must not exceed 50 characters")
    private String source;
    
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private LeadStatus status = LeadStatus.NEW;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_counsellor_id")
    private Employee assignedCounsellor;
    
    @Column(name = "converted_date")
    private LocalDateTime convertedDate;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "next_follow_up_date")
    private LocalDateTime nextFollowUpDate;
    
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Student> convertedStudents = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "lead_follow_ups", joinColumns = @JoinColumn(name = "lead_id"))
    private List<FollowUp> followUps = new ArrayList<>();
    
    // Constructors
    public Lead() {}
    
    public Lead(String firstName, String lastName, String email, String phone, String courseInterest, String source) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.courseInterest = courseInterest;
        this.source = source;
    }
    
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
    
    public LeadStatus getStatus() {
        return status;
    }
    
    public void setStatus(LeadStatus status) {
        this.status = status;
    }
    
    public Employee getAssignedCounsellor() {
        return assignedCounsellor;
    }
    
    public void setAssignedCounsellor(Employee assignedCounsellor) {
        this.assignedCounsellor = assignedCounsellor;
    }
    
    public LocalDateTime getConvertedDate() {
        return convertedDate;
    }
    
    public void setConvertedDate(LocalDateTime convertedDate) {
        this.convertedDate = convertedDate;
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
    
    public List<Student> getConvertedStudents() {
        return convertedStudents;
    }
    
    public void setConvertedStudents(List<Student> convertedStudents) {
        this.convertedStudents = convertedStudents;
    }
    
    public List<FollowUp> getFollowUps() {
        return followUps;
    }
    
    public void setFollowUps(List<FollowUp> followUps) {
        this.followUps = followUps;
    }
    
    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean isConverted() {
        return status == LeadStatus.CONVERTED;
    }
    
    public void convertToStudent() {
        this.status = LeadStatus.CONVERTED;
        this.convertedDate = LocalDateTime.now();
    }
    
    public void addFollowUp(String notes, LocalDateTime nextFollowUpDate) {
        FollowUp followUp = new FollowUp();
        followUp.setDate(LocalDateTime.now());
        followUp.setNotes(notes);
        followUp.setNextAction(nextFollowUpDate != null ? "Follow up on " + nextFollowUpDate : "No further action");
        this.followUps.add(followUp);
        this.nextFollowUpDate = nextFollowUpDate;
    }
    
    public void addStudent(Student student) {
        convertedStudents.add(student);
        student.setLead(this);
        if (!isConverted()) {
            convertToStudent();
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lead)) return false;
        Lead lead = (Lead) o;
        return id != null && id.equals(lead.getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Lead{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                ", courseInterest='" + courseInterest + '\'' +
                '}';
    }
    
    public enum LeadStatus {
        NEW, CONTACTED, INTERESTED, NOT_INTERESTED, CONVERTED, LOST
    }
    
    @Embeddable
    public static class FollowUp {
        @Column(name = "follow_up_date")
        private LocalDateTime date;
        
        @Column(name = "follow_up_notes", columnDefinition = "TEXT")
        private String notes;
        
        @Column(name = "next_action", length = 200)
        private String nextAction;
        
        // Constructors
        public FollowUp() {}
        
        public FollowUp(LocalDateTime date, String notes, String nextAction) {
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