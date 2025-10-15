package com.institute.management.service;

import com.institute.management.dto.*;
import com.institute.management.entity.Batch;
import com.institute.management.entity.Course;
import com.institute.management.entity.Lead;
import com.institute.management.entity.Student;
import com.institute.management.exception.BatchCapacityExceededException;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.repository.BatchRepository;
import com.institute.management.repository.LeadRepository;
import com.institute.management.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    
    @Mock
    private StudentRepository studentRepository;
    
    @Mock
    private BatchRepository batchRepository;
    
    @Mock
    private LeadRepository leadRepository;
    
    @InjectMocks
    private StudentService studentService;
    
    private Student testStudent;
    private Batch testBatch;
    private Course testCourse;
    private Lead testLead;
    private StudentCreateRequestDTO createRequest;
    private StudentUpdateRequestDTO updateRequest;
    
    @BeforeEach
    void setUp() {
        // Setup test course
        testCourse = new Course();
        testCourse.setId(UUID.randomUUID());
        testCourse.setName("Java Development");
        testCourse.setDurationMonths(6);
        testCourse.setFees(new BigDecimal("50000"));
        
        // Setup test batch
        testBatch = new Batch();
        testBatch.setId(UUID.randomUUID());
        testBatch.setName("JAVA-2024-01");
        testBatch.setCourse(testCourse);
        testBatch.setCapacity(30);
        testBatch.setCurrentEnrollment(15);
        testBatch.setStartDate(LocalDate.now().plusDays(30));
        testBatch.setStatus(Batch.BatchStatus.PLANNED);
        
        // Setup test lead
        testLead = new Lead();
        testLead.setId(UUID.randomUUID());
        testLead.setFirstName("Jane");
        testLead.setLastName("Smith");
        testLead.setEmail("jane.smith@example.com");
        testLead.setPhone("9876543210");
        testLead.setStatus(Lead.LeadStatus.CONVERTED);
        
        // Setup test student
        testStudent = new Student();
        testStudent.setId(UUID.randomUUID());
        testStudent.setEnrollmentNumber("ENR202400001");
        testStudent.setFirstName("John");
        testStudent.setLastName("Doe");
        testStudent.setEmail("john.doe@example.com");
        testStudent.setPhone("1234567890");
        testStudent.setDateOfBirth(LocalDate.of(1995, 5, 15));
        testStudent.setAddress("123 Main St, City");
        testStudent.setEnrollmentDate(LocalDate.now());
        testStudent.setStatus(Student.StudentStatus.ACTIVE);
        testStudent.setBatch(testBatch);
        testStudent.setLead(testLead);
        
        // Setup create request DTO
        createRequest = new StudentCreateRequestDTO();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setEmail("john.doe@example.com");
        createRequest.setPhone("1234567890");
        createRequest.setDateOfBirth(LocalDate.of(1995, 5, 15));
        createRequest.setAddress("123 Main St, City");
        createRequest.setEnrollmentDate(LocalDate.now());
        createRequest.setBatchId(testBatch.getId());
        createRequest.setLeadId(testLead.getId());
        
        // Setup update request DTO
        updateRequest = new StudentUpdateRequestDTO();
        updateRequest.setFirstName("John Updated");
        updateRequest.setLastName("Doe Updated");
        updateRequest.setEmail("john.updated@example.com");
        updateRequest.setPhone("1234567891");
        updateRequest.setDateOfBirth(LocalDate.of(1995, 5, 15));
        updateRequest.setAddress("456 Updated St, City");
        updateRequest.setStatus(Student.StudentStatus.ACTIVE);
        updateRequest.setBatchId(testBatch.getId());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateStudent_Success() {
        // Arrange
        when(studentRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(studentRepository.existsByPhone(createRequest.getPhone())).thenReturn(false);
        when(leadRepository.findById(createRequest.getLeadId())).thenReturn(Optional.of(testLead));
        when(batchRepository.findById(createRequest.getBatchId())).thenReturn(Optional.of(testBatch));
        when(studentRepository.findEnrollmentNumbersWithPrefix(anyString())).thenReturn(Arrays.asList());
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);
        when(batchRepository.save(any(Batch.class))).thenReturn(testBatch);
        
        // Act
        StudentResponseDTO result = studentService.createStudent(createRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(testStudent.getFirstName(), result.getFirstName());
        assertEquals(testStudent.getLastName(), result.getLastName());
        assertEquals(testStudent.getEmail(), result.getEmail());
        assertEquals(testStudent.getPhone(), result.getPhone());
        assertNotNull(result.getBatch());
        assertEquals(testBatch.getName(), result.getBatch().getName());
        
        verify(studentRepository).save(any(Student.class));
        verify(batchRepository).save(testBatch);
        assertEquals(16, testBatch.getCurrentEnrollment()); // Should increment
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateStudent_DuplicateEmail() {
        // Arrange
        when(studentRepository.existsByEmail(createRequest.getEmail())).thenReturn(true);
        
        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            studentService.createStudent(createRequest);
        });
        
        verify(studentRepository, never()).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateStudent_DuplicatePhone() {
        // Arrange
        when(studentRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(studentRepository.existsByPhone(createRequest.getPhone())).thenReturn(true);
        
        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            studentService.createStudent(createRequest);
        });
        
        verify(studentRepository, never()).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateStudent_BatchCapacityExceeded() {
        // Arrange
        testBatch.setCurrentEnrollment(30); // At capacity
        when(studentRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(studentRepository.existsByPhone(createRequest.getPhone())).thenReturn(false);
        when(batchRepository.findById(createRequest.getBatchId())).thenReturn(Optional.of(testBatch));
        
        // Act & Assert
        assertThrows(BatchCapacityExceededException.class, () -> {
            studentService.createStudent(createRequest);
        });
        
        verify(studentRepository, never()).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateStudent_LeadNotFound() {
        // Arrange
        when(studentRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(studentRepository.existsByPhone(createRequest.getPhone())).thenReturn(false);
        when(leadRepository.findById(createRequest.getLeadId())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.createStudent(createRequest);
        });
        
        verify(studentRepository, never()).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateStudent_BatchNotFound() {
        // Arrange
        when(studentRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(studentRepository.existsByPhone(createRequest.getPhone())).thenReturn(false);
        when(leadRepository.findById(createRequest.getLeadId())).thenReturn(Optional.of(testLead));
        when(batchRepository.findById(createRequest.getBatchId())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.createStudent(createRequest);
        });
        
        verify(studentRepository, never()).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStudent_Success() {
        // Arrange
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));
        when(studentRepository.existsByEmail(updateRequest.getEmail())).thenReturn(false);
        when(studentRepository.existsByPhone(updateRequest.getPhone())).thenReturn(false);
        when(batchRepository.findById(updateRequest.getBatchId())).thenReturn(Optional.of(testBatch));
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);
        
        // Act
        StudentResponseDTO result = studentService.updateStudent(testStudent.getId(), updateRequest);
        
        // Assert
        assertNotNull(result);
        verify(studentRepository).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStudent_NotFound() {
        // Arrange
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.updateStudent(testStudent.getId(), updateRequest);
        });
        
        verify(studentRepository, never()).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentById_Success() {
        // Arrange
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));
        
        // Act
        StudentResponseDTO result = studentService.getStudentById(testStudent.getId());
        
        // Assert
        assertNotNull(result);
        assertEquals(testStudent.getId(), result.getId());
        assertEquals(testStudent.getFirstName(), result.getFirstName());
        assertEquals(testStudent.getLastName(), result.getLastName());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentById_NotFound() {
        // Arrange
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.getStudentById(testStudent.getId());
        });
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllStudents_Success() {
        // Arrange
        List<Student> students = Arrays.asList(testStudent);
        Page<Student> studentPage = new PageImpl<>(students);
        Pageable pageable = PageRequest.of(0, 20);
        
        when(studentRepository.findStudentsWithFilters(any(), any(), any(), any(), eq(pageable)))
            .thenReturn(studentPage);
        
        // Act
        Page<StudentResponseDTO> result = studentService.getAllStudents(
            pageable, null, null, null, null, null, null);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testStudent.getId(), result.getContent().get(0).getId());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignToBatch_Success() {
        // Arrange
        Batch newBatch = new Batch();
        newBatch.setId(UUID.randomUUID());
        newBatch.setName("NEW-BATCH");
        newBatch.setCapacity(25);
        newBatch.setCurrentEnrollment(10);
        
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));
        when(batchRepository.findById(newBatch.getId())).thenReturn(Optional.of(newBatch));
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);
        when(batchRepository.save(any(Batch.class))).thenReturn(newBatch);
        
        // Act
        StudentResponseDTO result = studentService.assignToBatch(testStudent.getId(), newBatch.getId());
        
        // Assert
        assertNotNull(result);
        verify(batchRepository, times(2)).save(any(Batch.class)); // Old batch and new batch
        assertEquals(14, testBatch.getCurrentEnrollment()); // Should decrement old batch
        assertEquals(11, newBatch.getCurrentEnrollment()); // Should increment new batch
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignToBatch_StudentNotFound() {
        // Arrange
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.assignToBatch(testStudent.getId(), testBatch.getId());
        });
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignToBatch_BatchNotFound() {
        // Arrange
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));
        when(batchRepository.findById(testBatch.getId())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.assignToBatch(testStudent.getId(), testBatch.getId());
        });
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignToBatch_CapacityExceeded() {
        // Arrange
        testBatch.setCurrentEnrollment(30); // At capacity
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));
        when(batchRepository.findById(testBatch.getId())).thenReturn(Optional.of(testBatch));
        
        // Act & Assert
        assertThrows(BatchCapacityExceededException.class, () -> {
            studentService.assignToBatch(testStudent.getId(), testBatch.getId());
        });
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testRemoveFromBatch_Success() {
        // Arrange
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);
        when(batchRepository.save(any(Batch.class))).thenReturn(testBatch);
        
        // Act
        StudentResponseDTO result = studentService.removeFromBatch(testStudent.getId());
        
        // Assert
        assertNotNull(result);
        verify(batchRepository).save(testBatch);
        assertEquals(14, testBatch.getCurrentEnrollment()); // Should decrement
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStudentStatus_Success() {
        // Arrange
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);
        
        // Act
        StudentResponseDTO result = studentService.updateStudentStatus(
            testStudent.getId(), Student.StudentStatus.GRADUATED);
        
        // Assert
        assertNotNull(result);
        verify(studentRepository).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGraduateStudent_Success() {
        // Arrange
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);
        
        // Act
        StudentResponseDTO result = studentService.graduateStudent(testStudent.getId(), "A+");
        
        // Assert
        assertNotNull(result);
        verify(studentRepository).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteStudent_Success() {
        // Arrange
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));
        when(batchRepository.save(any(Batch.class))).thenReturn(testBatch);
        
        // Act
        studentService.deleteStudent(testStudent.getId());
        
        // Assert
        verify(studentRepository).deleteById(testStudent.getId());
        verify(batchRepository).save(testBatch);
        assertEquals(14, testBatch.getCurrentEnrollment()); // Should decrement
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteStudent_NotFound() {
        // Arrange
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.deleteStudent(testStudent.getId());
        });
        
        verify(studentRepository, never()).deleteById(any());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentsByBatch_Success() {
        // Arrange
        List<Student> students = Arrays.asList(testStudent);
        Page<Student> studentPage = new PageImpl<>(students);
        Pageable pageable = PageRequest.of(0, 20);
        
        when(batchRepository.findById(testBatch.getId())).thenReturn(Optional.of(testBatch));
        when(studentRepository.findByBatch(testBatch, pageable)).thenReturn(studentPage);
        
        // Act
        Page<StudentResponseDTO> result = studentService.getStudentsByBatch(testBatch.getId(), pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testStudent.getId(), result.getContent().get(0).getId());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentsByBatch_BatchNotFound() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        when(batchRepository.findById(testBatch.getId())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.getStudentsByBatch(testBatch.getId(), pageable);
        });
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentsWithoutBatch_Success() {
        // Arrange
        Student studentWithoutBatch = new Student();
        studentWithoutBatch.setId(UUID.randomUUID());
        studentWithoutBatch.setFirstName("Jane");
        studentWithoutBatch.setLastName("Smith");
        studentWithoutBatch.setBatch(null);
        
        List<Student> students = Arrays.asList(studentWithoutBatch);
        Page<Student> studentPage = new PageImpl<>(students);
        Pageable pageable = PageRequest.of(0, 20);
        
        when(studentRepository.findStudentsWithoutBatch(pageable)).thenReturn(studentPage);
        
        // Act
        Page<StudentResponseDTO> result = studentService.getStudentsWithoutBatch(pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(studentWithoutBatch.getId(), result.getContent().get(0).getId());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentStatistics_Success() {
        // Arrange
        when(studentRepository.count()).thenReturn(100L);
        when(studentRepository.countByStatus(Student.StudentStatus.ACTIVE)).thenReturn(80L);
        when(studentRepository.countByStatus(Student.StudentStatus.GRADUATED)).thenReturn(15L);
        when(studentRepository.countByStatus(Student.StudentStatus.DROPPED_OUT)).thenReturn(3L);
        when(studentRepository.countByStatus(Student.StudentStatus.SUSPENDED)).thenReturn(1L);
        when(studentRepository.countByStatus(Student.StudentStatus.INACTIVE)).thenReturn(1L);
        when(studentRepository.findStudentsWithoutBatch(any(Pageable.class)))
            .thenReturn(new PageImpl<>(Arrays.asList(), PageRequest.of(0, 1), 5));
        when(studentRepository.findPlacedGraduates()).thenReturn(Arrays.asList(testStudent));
        when(studentRepository.findUnplacedGraduates()).thenReturn(Arrays.asList());
        when(studentRepository.getEnrollmentTrends(any(), any())).thenReturn(Arrays.asList());
        when(studentRepository.countByBatch()).thenReturn(Arrays.asList());
        when(studentRepository.countByCourse()).thenReturn(Arrays.asList());
        when(studentRepository.getGradeDistribution()).thenReturn(Arrays.asList());
        
        // Act
        StudentStatisticsDTO result = studentService.getStudentStatistics();
        
        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getTotalStudents());
        assertEquals(80L, result.getActiveStudents());
        assertEquals(15L, result.getGraduatedStudents());
        assertEquals(3L, result.getDroppedOutStudents());
        assertEquals(1L, result.getSuspendedStudents());
        assertEquals(1L, result.getInactiveStudents());
        assertEquals(5L, result.getStudentsWithoutBatch());
        assertEquals(1L, result.getPlacedGraduates());
        assertEquals(0L, result.getUnplacedGraduates());
    }
    
    @Test
    void testGenerateEnrollmentNumber_FirstStudent() {
        // Arrange
        when(studentRepository.findEnrollmentNumbersWithPrefix(anyString())).thenReturn(Arrays.asList());
        
        // Act - Use reflection to test private method or create a public wrapper for testing
        // For now, we'll test it indirectly through createStudent
        when(studentRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(studentRepository.existsByPhone(createRequest.getPhone())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student student = invocation.getArgument(0);
            // Verify enrollment number format
            assertTrue(student.getEnrollmentNumber().matches("ENR\\d{8}"));
            return student;
        });
        
        // Remove batch and lead to simplify test
        createRequest.setBatchId(null);
        createRequest.setLeadId(null);
        
        // Act
        studentService.createStudent(createRequest);
        
        // Assert
        verify(studentRepository).save(any(Student.class));
    }
    
    @Test
    void testGenerateEnrollmentNumber_WithExistingNumbers() {
        // Arrange
        String currentYear = String.valueOf(LocalDate.now().getYear());
        String prefix = "ENR" + currentYear;
        List<String> existingNumbers = Arrays.asList(
            prefix + "0001",
            prefix + "0002",
            prefix + "0003"
        );
        
        when(studentRepository.findEnrollmentNumbersWithPrefix(prefix)).thenReturn(existingNumbers);
        when(studentRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(studentRepository.existsByPhone(createRequest.getPhone())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student student = invocation.getArgument(0);
            // Should generate ENR20240004
            assertEquals(prefix + "0004", student.getEnrollmentNumber());
            return student;
        });
        
        // Remove batch and lead to simplify test
        createRequest.setBatchId(null);
        createRequest.setLeadId(null);
        
        // Act
        studentService.createStudent(createRequest);
        
        // Assert
        verify(studentRepository).save(any(Student.class));
    }
}