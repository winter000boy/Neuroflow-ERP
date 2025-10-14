package com.institute.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "placements")
public class Placement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull(message = "Student is required")
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @NotNull(message = "Company is required")
    private Company company;
    
    @Column(name = "position", nullable = false, length = 100)
    @NotBlank(message = "Position is required")
    @Size(max = 100, message = "Position must not exceed 100 characters")
    private String position;
    
    @Column(name = "salary", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
    private BigDecimal salary;
    
    @Column(name = "placement_date", nullable = false)
    @NotNull(message = "Placement date is required")
    private LocalDate placementDate;
    
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private PlacementStatus status = PlacementStatus.PLACED;
    
    @Column(name = "job_type", length = 20)
    @Enumerated(EnumType.STRING)
    private JobType jobType;
    
    @Column(name = "work_location", length = 100)
    @Size(max = 100, message = "Work location must not exceed 100 characters")
    private String workLocation;
    
    @Column(name = "employment_type", length = 20)
    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;
    
    @Column(name = "probation_period_months")
    @Min(value = 0, message = "Probation period cannot be negative")
    @Max(value = 24, message = "Probation period cannot exceed 24 months")
    private Integer probationPeriodMonths;
    
    @Column(name = "joining_date")
    private LocalDate joiningDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    // Constructors
    public Placement() {}
    
    public Placement(Student student, Company company, String position, BigDecimal salary, LocalDate placementDate) {
        this.student = student;
        this.company = company;
        this.position = position;
        this.salary = salary;
        this.placementDate = placementDate;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public Company getCompany() {
        return company;
    }
    
    public void setCompany(Company company) {
        this.company = company;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public BigDecimal getSalary() {
        return salary;
    }
    
    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
    
    public LocalDate getPlacementDate() {
        return placementDate;
    }
    
    public void setPlacementDate(LocalDate placementDate) {
        this.placementDate = placementDate;
    }
    
    public PlacementStatus getStatus() {
        return status;
    }
    
    public void setStatus(PlacementStatus status) {
        this.status = status;
    }
    
    public JobType getJobType() {
        return jobType;
    }
    
    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }
    
    public String getWorkLocation() {
        return workLocation;
    }
    
    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }
    
    public EmploymentType getEmploymentType() {
        return employmentType;
    }
    
    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }
    
    public Integer getProbationPeriodMonths() {
        return probationPeriodMonths;
    }
    
    public void setProbationPeriodMonths(Integer probationPeriodMonths) {
        this.probationPeriodMonths = probationPeriodMonths;
    }
    
    public LocalDate getJoiningDate() {
        return joiningDate;
    }
    
    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    // Helper methods
    public boolean isActive() {
        return status == PlacementStatus.PLACED && (endDate == null || endDate.isAfter(LocalDate.now()));
    }
    
    public boolean isInProbation() {
        if (joiningDate == null || probationPeriodMonths == null) {
            return false;
        }
        LocalDate probationEndDate = joiningDate.plusMonths(probationPeriodMonths);
        return LocalDate.now().isBefore(probationEndDate);
    }
    
    public long getTenureInMonths() {
        if (joiningDate == null) {
            return 0;
        }
        LocalDate endDateToUse = endDate != null ? endDate : LocalDate.now();
        return java.time.Period.between(joiningDate, endDateToUse).toTotalMonths();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Placement)) return false;
        Placement placement = (Placement) o;
        return id != null && id.equals(placement.getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Placement{" +
                "id=" + id +
                ", position='" + position + '\'' +
                ", salary=" + salary +
                ", placementDate=" + placementDate +
                ", status=" + status +
                ", jobType=" + jobType +
                ", employmentType=" + employmentType +
                '}';
    }
    
    public enum PlacementStatus {
        PLACED, RESIGNED, TERMINATED, COMPLETED
    }
    
    public enum JobType {
        FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE
    }
    
    public enum EmploymentType {
        PERMANENT, TEMPORARY, PROBATION, CONSULTANT
    }
}