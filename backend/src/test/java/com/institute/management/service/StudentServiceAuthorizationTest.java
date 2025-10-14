package com.institute.management.service;

import com.institute.management.entity.Batch;
import com.institute.management.entity.Employee;
import com.institute.management.entity.Student;
import com.institute.management.repository.StudentRepository;
import com.institute.management.security.AuthorizationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class StudentServiceAuthorizationTest extends AuthorizationTestBase {
    
    @Mock
    private StudentRepository studentRepository;
    
    @InjectMocks
    private StudentService studentService;
    
    private Student testStudent;
    private UUID studentId;
    
    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        testStudent = new Student();
        testStudent.setId(studentId);
        testStudent.setEnrollmentNumber("ENR20240001");
        testStudent.setFirstName("John");
        testStudent.setLastName("Doe");
        testStudent.setEmail("john.doe@example.com");
        testStudent.setPhone("1234567890");
        testStudent.setEnrollmentDate(LocalDate.now());
        testStudent.setStatus(Student.StudentStatus.ACTIVE);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanCreateStudent() {
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);
        when(studentRepository.count()).thenReturn(0L);
        
        Student result = studentService.createStudent(testStudent);
        
        assertNotNull(result);
        verify(studentRepository).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCanCreateStudent() {
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);
        when(studentRepository.count()).thenReturn(0L);
        
        Student result = studentService.createStudent(testStudent);
        
        assertNotNull(result);
        verify(studentRepository).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotCreateStudent() {
        assertThrows(AccessDeniedException.class, () -> {
            studentService.createStudent(testStudent);
        });
        
        verify(studentRepository, never()).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCannotCreateStudent() {
        assertThrows(AccessDeniedException.class, () -> {
            studentService.createStudent(testStudent);
        });
        
        verify(studentRepository, never()).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCannotCreateStudent() {
        assertThrows(AccessDeniedException.class, () -> {
            studentService.createStudent(testStudent);
        });
        
        verify(studentRepository, never()).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanViewStudent() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        
        Optional<Student> result = studentService.getStudentById(studentId);
        
        assertTrue(result.isPresent());
        verify(studentRepository).findById(studentId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCanViewStudent() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        
        Optional<Student> result = studentService.getStudentById(studentId);
        
        assertTrue(result.isPresent());
        verify(studentRepository).findById(studentId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCanViewStudent() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        
        Optional<Student> result = studentService.getStudentById(studentId);
        
        assertTrue(result.isPresent());
        verify(studentRepository).findById(studentId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCannotViewStudent() {
        assertThrows(AccessDeniedException.class, () -> {
            studentService.getStudentById(studentId);
        });
        
        verify(studentRepository, never()).findById(any(UUID.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCannotViewStudent() {
        assertThrows(AccessDeniedException.class, () -> {
            studentService.getStudentById(studentId);
        });
        
        verify(studentRepository, never()).findById(any(UUID.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanUpdateStudent() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);
        
        Student updatedStudent = new Student();
        updatedStudent.setFirstName("Jane");
        updatedStudent.setLastName("Smith");
        
        Student result = studentService.updateStudent(studentId, updatedStudent);
        
        assertNotNull(result);
        verify(studentRepository).findById(studentId);
        verify(studentRepository).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCanUpdateStudent() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);
        
        Student updatedStudent = new Student();
        updatedStudent.setFirstName("Jane");
        updatedStudent.setLastName("Smith");
        
        Student result = studentService.updateStudent(studentId, updatedStudent);
        
        assertNotNull(result);
        verify(studentRepository).findById(studentId);
        verify(studentRepository).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotUpdateStudent() {
        Student updatedStudent = new Student();
        updatedStudent.setFirstName("Jane");
        
        assertThrows(AccessDeniedException.class, () -> {
            studentService.updateStudent(studentId, updatedStudent);
        });
        
        verify(studentRepository, never()).findById(any(UUID.class));
        verify(studentRepository, never()).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanDeleteStudent() {
        when(studentRepository.existsById(studentId)).thenReturn(true);
        
        assertDoesNotThrow(() -> {
            studentService.deleteStudent(studentId);
        });
        
        verify(studentRepository).existsById(studentId);
        verify(studentRepository).deleteById(studentId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotDeleteStudent() {
        assertThrows(AccessDeniedException.class, () -> {
            studentService.deleteStudent(studentId);
        });
        
        verify(studentRepository, never()).existsById(any(UUID.class));
        verify(studentRepository, never()).deleteById(any(UUID.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotDeleteStudent() {
        assertThrows(AccessDeniedException.class, () -> {
            studentService.deleteStudent(studentId);
        });
        
        verify(studentRepository, never()).existsById(any(UUID.class));
        verify(studentRepository, never()).deleteById(any(UUID.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanAssignStudentToBatch() {
        Batch batch = new Batch();
        batch.setId(UUID.randomUUID());
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);
        
        Student result = studentService.assignToBatch(studentId, batch);
        
        assertNotNull(result);
        verify(studentRepository).findById(studentId);
        verify(studentRepository).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCanAssignStudentToBatch() {
        Batch batch = new Batch();
        batch.setId(UUID.randomUUID());
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);
        
        Student result = studentService.assignToBatch(studentId, batch);
        
        assertNotNull(result);
        verify(studentRepository).findById(studentId);
        verify(studentRepository).save(any(Student.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotAssignStudentToBatch() {
        Batch batch = new Batch();
        batch.setId(UUID.randomUUID());
        
        assertThrows(AccessDeniedException.class, () -> {
            studentService.assignToBatch(studentId, batch);
        });
        
        verify(studentRepository, never()).findById(any(UUID.class));
        verify(studentRepository, never()).save(any(Student.class));
    }
}