package com.institute.management.service;

import com.institute.management.entity.Batch;
import com.institute.management.entity.Student;
import com.institute.management.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    /**
     * Create a new student - Only ADMIN and COUNSELLOR can create students
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public Student createStudent(Student student) {
        // Generate enrollment number if not provided
        if (student.getEnrollmentNumber() == null || student.getEnrollmentNumber().isEmpty()) {
            student.setEnrollmentNumber(generateEnrollmentNumber());
        }
        return studentRepository.save(student);
    }
    
    /**
     * Update an existing student - Only ADMIN and COUNSELLOR can update students
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public Student updateStudent(UUID id, Student studentDetails) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        
        student.setFirstName(studentDetails.getFirstName());
        student.setLastName(studentDetails.getLastName());
        student.setEmail(studentDetails.getEmail());
        student.setPhone(studentDetails.getPhone());
        student.setDateOfBirth(studentDetails.getDateOfBirth());
        student.setAddress(studentDetails.getAddress());
        student.setStatus(studentDetails.getStatus());
        
        return studentRepository.save(student);
    }
    
    /**
     * Get student by ID - ADMIN, COUNSELLOR, and FACULTY can view students
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY')")
    public Optional<Student> getStudentById(UUID id) {
        return studentRepository.findById(id);
    }
    
    /**
     * Get all students with pagination - ADMIN, COUNSELLOR, and FACULTY can view students
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY')")
    public Page<Student> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }
    
    /**
     * Get students by batch - ADMIN, COUNSELLOR, and FACULTY can view students
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY')")
    public List<Student> getStudentsByBatch(Batch batch) {
        return studentRepository.findByBatch(batch);
    }
    
    /**
     * Get students by status - ADMIN, COUNSELLOR, and FACULTY can view students
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY')")
    public List<Student> getStudentsByStatus(Student.StudentStatus status) {
        return studentRepository.findByStatus(status);
    }
    
    /**
     * Assign student to batch - Only ADMIN and COUNSELLOR can assign students to batches
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public Student assignToBatch(UUID studentId, Batch batch) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        // Note: Batch capacity validation should be done here
        // This would require BatchService injection and capacity check
        
        student.setBatch(batch);
        return studentRepository.save(student);
    }
    
    /**
     * Update student status - Only ADMIN and COUNSELLOR can update student status
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public Student updateStudentStatus(UUID studentId, Student.StudentStatus status) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        student.setStatus(status);
        return studentRepository.save(student);
    }
    
    /**
     * Delete student - Only ADMIN can delete students
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStudent(UUID id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }
    
    /**
     * Get students enrolled within date range - ADMIN, COUNSELLOR, and FACULTY can view
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY')")
    public List<Student> getStudentsByEnrollmentDateRange(LocalDate startDate, LocalDate endDate) {
        return studentRepository.findByEnrollmentDateBetween(startDate, endDate);
    }
    
    /**
     * Generate unique enrollment number
     */
    private String generateEnrollmentNumber() {
        String year = String.valueOf(LocalDate.now().getYear());
        long count = studentRepository.count() + 1;
        return "ENR" + year + String.format("%04d", count);
    }
}