package com.institute.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "students",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "enrollment_number"),
           @UniqueConstraint(columnNames = "email")
       })
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "enrollment_number", nullable = false, length = 20, unique = true)
    @NotBlank(message = "Enrollment number is required")
    @Size(max = 20, message = "Enrollment number must not exceed 20 characters")
    private String enrollmentNumber;
    
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
    
    @Column(name = "date_of_birth")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;
    
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private StudentStatus status = StudentStatus.ACTIVE;
    
    @Column(name = "enrollment_date", nullable = false)
    @NotNull(message = "Enrollment date is required")
    private LocalDate enrollmentDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead lead;
    
    @Column(name = "graduation_date")
    private LocalDate graduationDate;
    
    @Column(name = "final_grade", length = 5)
    private String finalGrade;
    
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Placement> placements = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "student_status_history", joinColumns = @JoinColumn(name = "student_id"))
    private List<StatusHistory> statusHistory = new ArrayList<>();
    
    // Constructors
    public Student() {}
    
    public Student(String enrollmentNumber, String firstName, String lastName, String email, 
                  String phone, LocalDate enrollmentDate) {
        this.enrollmentNumber = enrollmentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.enrollmentDate = enrollmentDate;
        this.status = StudentStatus.ACTIVE;
    }
    
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
    
    public Batch getBatch() {
        return batch;
    }
    
    public void setBatch(Batch batch) {
        this.batch = batch;
    }
    
    public StudentStatus getStatus() {
        return status;
    }
    
    public void setStatus(StudentStatus status) {
        StudentStatus oldStatus = this.status;
        this.status = status;
        if (oldStatus != status) {
            addStatusHistory(status, "Status changed from " + oldStatus + " to " + status);
        }
    }
    
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }
    
    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
    
    public Lead getLead() {
        return lead;
    }
    
    public void setLead(Lead lead) {
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
    
    public List<Placement> getPlacements() {
        return placements;
    }
    
    public void setPlacements(List<Placement> placements) {
        this.placements = placements;
    }
    
    public List<StatusHistory> getStatusHistory() {
        return statusHistory;
    }
    
    public void setStatusHistory(List<StatusHistory> statusHistory) {
        this.statusHistory = statusHistory;
    }
    
    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean isActive() {
        return status == StudentStatus.ACTIVE;
    }
    
    public boolean isGraduated() {
        return status == StudentStatus.GRADUATED;
    }
    
    public boolean isPlaced() {
        return placements.stream().anyMatch(p -> p.getStatus() == Placement.PlacementStatus.PLACED);
    }
    
    public void addPlacement(Placement placement) {
        placements.add(placement);
        placement.setStudent(this);
    }
    
    public void removePlacement(Placement placement) {
        placements.remove(placement);
        placement.setStudent(null);
    }
    
    private void addStatusHistory(StudentStatus status, String notes) {
        StatusHistory history = new StatusHistory();
        history.setStatus(status);
        history.setChangeDate(LocalDateTime.now());
        history.setNotes(notes);
        this.statusHistory.add(history);
    }
    
    public void graduate(String finalGrade) {
        this.status = StudentStatus.GRADUATED;
        this.graduationDate = LocalDate.now();
        this.finalGrade = finalGrade;
        addStatusHistory(StudentStatus.GRADUATED, "Student graduated with grade: " + finalGrade);
    }
    
    @PostPersist
    private void initializeStatusHistory() {
        if (statusHistory.isEmpty()) {
            addStatusHistory(this.status, "Student enrolled");
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return id != null && id.equals(student.getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", enrollmentNumber='" + enrollmentNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", enrollmentDate=" + enrollmentDate +
                '}';
    }
    
    public enum StudentStatus {
        ACTIVE, INACTIVE, GRADUATED, DROPPED_OUT, SUSPENDED
    }
    
    @Embeddable
    public static class StatusHistory {
        @Column(name = "status")
        @Enumerated(EnumType.STRING)
        private StudentStatus status;
        
        @Column(name = "change_date")
        private LocalDateTime changeDate;
        
        @Column(name = "notes", columnDefinition = "TEXT")
        private String notes;
        
        // Constructors
        public StatusHistory() {}
        
        public StatusHistory(StudentStatus status, LocalDateTime changeDate, String notes) {
            this.status = status;
            this.changeDate = changeDate;
            this.notes = notes;
        }
        
        // Getters and Setters
        public StudentStatus getStatus() {
            return status;
        }
        
        public void setStatus(StudentStatus status) {
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