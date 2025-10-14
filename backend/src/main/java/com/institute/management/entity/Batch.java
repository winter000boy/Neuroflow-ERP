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
@Table(name = "batches")
public class Batch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Batch name is required")
    @Size(max = 100, message = "Batch name must not exceed 100 characters")
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @NotNull(message = "Course is required")
    private Course course;
    
    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "capacity", nullable = false)
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 100, message = "Capacity must not exceed 100")
    private Integer capacity;
    
    @Column(name = "current_enrollment", nullable = false)
    private Integer currentEnrollment = 0;
    
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private BatchStatus status = BatchStatus.PLANNED;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private Employee instructor;
    
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Student> students = new ArrayList<>();
    
    // Constructors
    public Batch() {}
    
    public Batch(String name, Course course, LocalDate startDate, Integer capacity) {
        this.name = name;
        this.course = course;
        this.startDate = startDate;
        this.capacity = capacity;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Course getCourse() {
        return course;
    }
    
    public void setCourse(Course course) {
        this.course = course;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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
    
    public BatchStatus getStatus() {
        return status;
    }
    
    public void setStatus(BatchStatus status) {
        this.status = status;
    }
    
    public Employee getInstructor() {
        return instructor;
    }
    
    public void setInstructor(Employee instructor) {
        this.instructor = instructor;
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
    
    public List<Student> getStudents() {
        return students;
    }
    
    public void setStudents(List<Student> students) {
        this.students = students;
    }
    
    // Helper methods
    public boolean hasAvailableSlots() {
        return currentEnrollment < capacity;
    }
    
    public int getAvailableSlots() {
        return capacity - currentEnrollment;
    }
    
    public double getUtilizationPercentage() {
        return (double) currentEnrollment / capacity * 100;
    }
    
    public boolean canEnrollStudent() {
        return hasAvailableSlots() && (status == BatchStatus.PLANNED || status == BatchStatus.ACTIVE);
    }
    
    public void addStudent(Student student) {
        if (!canEnrollStudent()) {
            throw new IllegalStateException("Cannot enroll student: batch is full or not accepting enrollments");
        }
        students.add(student);
        student.setBatch(this);
        this.currentEnrollment = students.size();
    }
    
    public void removeStudent(Student student) {
        students.remove(student);
        student.setBatch(null);
        this.currentEnrollment = students.size();
    }
    
    @PrePersist
    @PreUpdate
    private void calculateEndDate() {
        if (startDate != null && course != null && course.getDurationMonths() != null) {
            this.endDate = startDate.plusMonths(course.getDurationMonths());
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Batch)) return false;
        Batch batch = (Batch) o;
        return id != null && id.equals(batch.getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Batch{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", capacity=" + capacity +
                ", currentEnrollment=" + currentEnrollment +
                ", status=" + status +
                '}';
    }
    
    public enum BatchStatus {
        PLANNED, ACTIVE, COMPLETED, CANCELLED
    }
}