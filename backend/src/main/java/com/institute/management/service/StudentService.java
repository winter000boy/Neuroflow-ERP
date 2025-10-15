package com.institute.management.service;

import com.institute.management.dto.*;
import com.institute.management.entity.Batch;
import com.institute.management.entity.Lead;
import com.institute.management.entity.Student;
import com.institute.management.exception.BatchCapacityExceededException;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.repository.BatchRepository;
import com.institute.management.repository.LeadRepository;
import com.institute.management.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private BatchRepository batchRepository;
    
    @Autowired
    private LeadRepository leadRepository;
    
    /**
     * Create a new student - Only ADMIN and COUNSELLOR can create students
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public StudentResponseDTO createStudent(StudentCreateRequestDTO request) {
        // Check for duplicate email
        if (request.getEmail() != null && studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Student with email " + request.getEmail() + " already exists");
        }
        
        // Check for duplicate phone
        if (studentRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Student with phone " + request.getPhone() + " already exists");
        }
        
        Student student = new Student();
        student.setEnrollmentNumber(generateEnrollmentNumber());
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setAddress(request.getAddress());
        student.setEnrollmentDate(request.getEnrollmentDate());
        student.setStatus(Student.StudentStatus.ACTIVE);
        
        // Set lead if provided
        if (request.getLeadId() != null) {
            Lead lead = leadRepository.findById(request.getLeadId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + request.getLeadId()));
            student.setLead(lead);
        }
        
        // Assign to batch if provided and validate capacity
        if (request.getBatchId() != null) {
            Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + request.getBatchId()));
            
            validateBatchCapacity(batch);
            student.setBatch(batch);
            
            // Update batch enrollment count
            batch.setCurrentEnrollment(batch.getCurrentEnrollment() + 1);
            batchRepository.save(batch);
        }
        
        Student savedStudent = studentRepository.save(student);
        return convertToResponseDTO(savedStudent);
    }
    
    /**
     * Update an existing student - Only ADMIN and COUNSELLOR can update students
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public StudentResponseDTO updateStudent(UUID id, StudentUpdateRequestDTO request) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        
        // Check for duplicate email (excluding current student)
        if (request.getEmail() != null && !request.getEmail().equals(student.getEmail()) 
            && studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Student with email " + request.getEmail() + " already exists");
        }
        
        // Check for duplicate phone (excluding current student)
        if (!request.getPhone().equals(student.getPhone()) 
            && studentRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Student with phone " + request.getPhone() + " already exists");
        }
        
        // Update basic information
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setAddress(request.getAddress());
        student.setStatus(request.getStatus());
        student.setGraduationDate(request.getGraduationDate());
        student.setFinalGrade(request.getFinalGrade());
        
        // Handle batch assignment change
        if (request.getBatchId() != null) {
            Batch newBatch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + request.getBatchId()));
            
            // If changing batch, update enrollment counts
            if (student.getBatch() == null || !student.getBatch().getId().equals(request.getBatchId())) {
                // Remove from old batch
                if (student.getBatch() != null) {
                    Batch oldBatch = student.getBatch();
                    oldBatch.setCurrentEnrollment(oldBatch.getCurrentEnrollment() - 1);
                    batchRepository.save(oldBatch);
                }
                
                // Validate new batch capacity
                validateBatchCapacity(newBatch);
                
                // Add to new batch
                student.setBatch(newBatch);
                newBatch.setCurrentEnrollment(newBatch.getCurrentEnrollment() + 1);
                batchRepository.save(newBatch);
            }
        }
        
        Student savedStudent = studentRepository.save(student);
        return convertToResponseDTO(savedStudent);
    }
    
    /**
     * Get student by ID - ADMIN, COUNSELLOR, and FACULTY can view students
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY')")
    public StudentResponseDTO getStudentById(UUID id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return convertToResponseDTO(student);
    }
    
    /**
     * Get all students with pagination and filtering - ADMIN, COUNSELLOR, and FACULTY can view students
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY')")
    public Page<StudentResponseDTO> getAllStudents(Pageable pageable, Student.StudentStatus status, 
                                                  UUID batchId, UUID courseId, String searchTerm,
                                                  LocalDate enrollmentStartDate, LocalDate enrollmentEndDate) {
        Page<Student> students;
        
        if (enrollmentStartDate != null && enrollmentEndDate != null) {
            // If date range is specified, use date range filtering with other filters
            students = studentRepository.findStudentsWithFiltersAndDateRange(
                status, batchId, courseId, searchTerm, enrollmentStartDate, enrollmentEndDate, pageable);
        } else {
            // Use regular filtering
            students = studentRepository.findStudentsWithFilters(status, batchId, courseId, searchTerm, pageable);
        }
        
        return students.map(this::convertToResponseDTO);
    }
    
    /**
     * Assign student to batch - Only ADMIN and COUNSELLOR can assign students to batches
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public StudentResponseDTO assignToBatch(UUID studentId, UUID batchId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        
        Batch batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + batchId));
        
        // Remove from old batch if assigned
        if (student.getBatch() != null) {
            Batch oldBatch = student.getBatch();
            oldBatch.setCurrentEnrollment(oldBatch.getCurrentEnrollment() - 1);
            batchRepository.save(oldBatch);
        }
        
        // Validate new batch capacity
        validateBatchCapacity(batch);
        
        // Assign to new batch
        student.setBatch(batch);
        batch.setCurrentEnrollment(batch.getCurrentEnrollment() + 1);
        
        batchRepository.save(batch);
        Student savedStudent = studentRepository.save(student);
        return convertToResponseDTO(savedStudent);
    }
    
    /**
     * Remove student from batch - Only ADMIN and COUNSELLOR can remove students from batches
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public StudentResponseDTO removeFromBatch(UUID studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        
        if (student.getBatch() != null) {
            Batch batch = student.getBatch();
            batch.setCurrentEnrollment(batch.getCurrentEnrollment() - 1);
            batchRepository.save(batch);
            
            student.setBatch(null);
            Student savedStudent = studentRepository.save(student);
            return convertToResponseDTO(savedStudent);
        }
        
        return convertToResponseDTO(student);
    }
    
    /**
     * Update student status - Only ADMIN and COUNSELLOR can update student status
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public StudentResponseDTO updateStudentStatus(UUID studentId, Student.StudentStatus status) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        
        student.setStatus(status);
        Student savedStudent = studentRepository.save(student);
        return convertToResponseDTO(savedStudent);
    }
    
    /**
     * Graduate student - Only ADMIN and COUNSELLOR can graduate students
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public StudentResponseDTO graduateStudent(UUID studentId, String finalGrade) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        
        student.graduate(finalGrade);
        Student savedStudent = studentRepository.save(student);
        return convertToResponseDTO(savedStudent);
    }
    
    /**
     * Delete student - Only ADMIN can delete students
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStudent(UUID id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        
        // Remove from batch if assigned
        if (student.getBatch() != null) {
            Batch batch = student.getBatch();
            batch.setCurrentEnrollment(batch.getCurrentEnrollment() - 1);
            batchRepository.save(batch);
        }
        
        studentRepository.deleteById(id);
    }
    
    /**
     * Get students by batch - ADMIN, COUNSELLOR, and FACULTY can view students
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY')")
    public Page<StudentResponseDTO> getStudentsByBatch(UUID batchId, Pageable pageable) {
        Batch batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + batchId));
        
        Page<Student> students = studentRepository.findByBatch(batch, pageable);
        return students.map(this::convertToResponseDTO);
    }
    
    /**
     * Get students without batch assignment - ADMIN, COUNSELLOR, and FACULTY can view
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY')")
    public Page<StudentResponseDTO> getStudentsWithoutBatch(Pageable pageable) {
        Page<Student> students = studentRepository.findStudentsWithoutBatch(pageable);
        return students.map(this::convertToResponseDTO);
    }
    
    /**
     * Get student statistics - ADMIN, COUNSELLOR, and FACULTY can view
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY')")
    public StudentStatisticsDTO getStudentStatistics() {
        StudentStatisticsDTO stats = new StudentStatisticsDTO();
        
        // Basic counts
        stats.setTotalStudents(studentRepository.count());
        stats.setActiveStudents(studentRepository.countByStatus(Student.StudentStatus.ACTIVE));
        stats.setGraduatedStudents(studentRepository.countByStatus(Student.StudentStatus.GRADUATED));
        stats.setDroppedOutStudents(studentRepository.countByStatus(Student.StudentStatus.DROPPED_OUT));
        stats.setSuspendedStudents(studentRepository.countByStatus(Student.StudentStatus.SUSPENDED));
        stats.setInactiveStudents(studentRepository.countByStatus(Student.StudentStatus.INACTIVE));
        
        // Students without batch
        stats.setStudentsWithoutBatch(studentRepository.findStudentsWithoutBatch(Pageable.unpaged()).getTotalElements());
        
        // Placement statistics
        stats.setPlacedGraduates(studentRepository.findPlacedGraduates().size());
        stats.setUnplacedGraduates(studentRepository.findUnplacedGraduates().size());
        
        // Enrollment trends (last 12 months)
        LocalDate twelveMonthsAgo = LocalDate.now().minusMonths(12);
        List<Object[]> enrollmentTrends = studentRepository.getEnrollmentTrends(twelveMonthsAgo, LocalDate.now());
        Map<String, Long> enrollmentsByMonth = new HashMap<>();
        for (Object[] trend : enrollmentTrends) {
            LocalDate date = (LocalDate) trend[0];
            Long count = (Long) trend[1];
            enrollmentsByMonth.put(date.format(DateTimeFormatter.ofPattern("yyyy-MM")), count);
        }
        stats.setEnrollmentsByMonth(enrollmentsByMonth);
        
        // Students by batch
        List<Object[]> batchCounts = studentRepository.countByBatch();
        Map<String, Long> studentsByBatch = new HashMap<>();
        for (Object[] count : batchCounts) {
            Batch batch = (Batch) count[0];
            Long studentCount = (Long) count[1];
            studentsByBatch.put(batch.getName(), studentCount);
        }
        stats.setStudentsByBatch(studentsByBatch);
        
        // Students by course
        List<Object[]> courseCounts = studentRepository.countByCourse();
        Map<String, Long> studentsByCourse = new HashMap<>();
        for (Object[] count : courseCounts) {
            // Assuming course name is accessible
            String courseName = count[0].toString();
            Long studentCount = (Long) count[1];
            studentsByCourse.put(courseName, studentCount);
        }
        stats.setStudentsByCourse(studentsByCourse);
        
        // Grade distribution
        List<Object[]> gradeDistribution = studentRepository.getGradeDistribution();
        Map<String, Long> gradeStats = new HashMap<>();
        for (Object[] grade : gradeDistribution) {
            String gradeValue = (String) grade[0];
            Long count = (Long) grade[1];
            gradeStats.put(gradeValue, count);
        }
        stats.setGradeDistribution(gradeStats);
        
        return stats;
    }
    
    /**
     * Generate unique enrollment number
     */
    private String generateEnrollmentNumber() {
        String year = String.valueOf(LocalDate.now().getYear());
        String prefix = "ENR" + year;
        
        // Get existing enrollment numbers with this prefix to find the next sequence
        List<String> existingNumbers = studentRepository.findEnrollmentNumbersWithPrefix(prefix);
        
        int nextSequence = 1;
        if (!existingNumbers.isEmpty()) {
            // Extract the highest sequence number
            int maxSequence = existingNumbers.stream()
                .mapToInt(num -> {
                    try {
                        return Integer.parseInt(num.substring(prefix.length()));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);
            nextSequence = maxSequence + 1;
        }
        
        return prefix + String.format("%04d", nextSequence);
    }
    
    /**
     * Validate batch capacity before assignment
     */
    private void validateBatchCapacity(Batch batch) {
        if (batch.getCurrentEnrollment() >= batch.getCapacity()) {
            throw new BatchCapacityExceededException(
                batch.getName(), batch.getCapacity(), batch.getCurrentEnrollment());
        }
    }
    
    /**
     * Convert Student entity to StudentResponseDTO
     */
    private StudentResponseDTO convertToResponseDTO(Student student) {
        StudentResponseDTO dto = new StudentResponseDTO();
        dto.setId(student.getId());
        dto.setEnrollmentNumber(student.getEnrollmentNumber());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setFullName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setDateOfBirth(student.getDateOfBirth());
        dto.setAddress(student.getAddress());
        dto.setStatus(student.getStatus());
        dto.setEnrollmentDate(student.getEnrollmentDate());
        dto.setGraduationDate(student.getGraduationDate());
        dto.setFinalGrade(student.getFinalGrade());
        dto.setCreatedDate(student.getCreatedDate());
        dto.setUpdatedDate(student.getUpdatedDate());
        
        // Set batch information
        if (student.getBatch() != null) {
            BatchBasicDTO batchDTO = new BatchBasicDTO();
            batchDTO.setId(student.getBatch().getId());
            batchDTO.setName(student.getBatch().getName());
            batchDTO.setStartDate(student.getBatch().getStartDate());
            batchDTO.setEndDate(student.getBatch().getEndDate());
            batchDTO.setCapacity(student.getBatch().getCapacity());
            batchDTO.setCurrentEnrollment(student.getBatch().getCurrentEnrollment());
            batchDTO.setStatus(student.getBatch().getStatus());
            
            if (student.getBatch().getCourse() != null) {
                CourseBasicDTO courseDTO = new CourseBasicDTO();
                courseDTO.setId(student.getBatch().getCourse().getId());
                courseDTO.setName(student.getBatch().getCourse().getName());
                courseDTO.setDurationMonths(student.getBatch().getCourse().getDurationMonths());
                courseDTO.setFees(student.getBatch().getCourse().getFees());
                batchDTO.setCourse(courseDTO);
            }
            
            dto.setBatch(batchDTO);
        }
        
        // Set lead information
        if (student.getLead() != null) {
            LeadBasicDTO leadDTO = new LeadBasicDTO();
            leadDTO.setId(student.getLead().getId());
            leadDTO.setFirstName(student.getLead().getFirstName());
            leadDTO.setLastName(student.getLead().getLastName());
            leadDTO.setEmail(student.getLead().getEmail());
            leadDTO.setPhone(student.getLead().getPhone());
            leadDTO.setStatus(student.getLead().getStatus());
            dto.setLead(leadDTO);
        }
        
        // Set placement information
        if (student.getPlacements() != null && !student.getPlacements().isEmpty()) {
            List<PlacementBasicDTO> placementDTOs = student.getPlacements().stream()
                .map(placement -> {
                    PlacementBasicDTO placementDTO = new PlacementBasicDTO();
                    placementDTO.setId(placement.getId());
                    placementDTO.setPosition(placement.getPosition());
                    placementDTO.setSalary(placement.getSalary());
                    placementDTO.setPlacementDate(placement.getPlacementDate());
                    placementDTO.setStatus(placement.getStatus());
                    
                    if (placement.getCompany() != null) {
                        CompanyBasicDTO companyDTO = new CompanyBasicDTO();
                        companyDTO.setId(placement.getCompany().getId());
                        companyDTO.setName(placement.getCompany().getName());
                        companyDTO.setIndustry(placement.getCompany().getIndustry());
                        placementDTO.setCompany(companyDTO);
                    }
                    
                    return placementDTO;
                })
                .collect(Collectors.toList());
            dto.setPlacements(placementDTOs);
        }
        
        // Set status history
        if (student.getStatusHistory() != null && !student.getStatusHistory().isEmpty()) {
            List<StudentResponseDTO.StatusHistoryDTO> historyDTOs = student.getStatusHistory().stream()
                .map(history -> new StudentResponseDTO.StatusHistoryDTO(
                    history.getStatus(), history.getChangeDate(), history.getNotes()))
                .collect(Collectors.toList());
            dto.setStatusHistory(historyDTOs);
        }
        
        return dto;
    }
}