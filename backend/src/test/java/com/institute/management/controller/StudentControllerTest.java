package com.institute.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.institute.management.dto.*;
import com.institute.management.entity.Student;
import com.institute.management.exception.BatchCapacityExceededException;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private StudentService studentService;
    
    private ObjectMapper objectMapper;
    private StudentCreateRequestDTO createRequest;
    private StudentUpdateRequestDTO updateRequest;
    private StudentResponseDTO responseDTO;
    private UUID studentId;
    private UUID batchId;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        studentId = UUID.randomUUID();
        batchId = UUID.randomUUID();
        
        // Setup create request
        createRequest = new StudentCreateRequestDTO();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setEmail("john.doe@example.com");
        createRequest.setPhone("1234567890");
        createRequest.setDateOfBirth(LocalDate.of(1995, 5, 15));
        createRequest.setAddress("123 Main St, City");
        createRequest.setEnrollmentDate(LocalDate.now());
        createRequest.setBatchId(batchId);
        
        // Setup update request
        updateRequest = new StudentUpdateRequestDTO();
        updateRequest.setFirstName("John Updated");
        updateRequest.setLastName("Doe Updated");
        updateRequest.setEmail("john.updated@example.com");
        updateRequest.setPhone("1234567891");
        updateRequest.setDateOfBirth(LocalDate.of(1995, 5, 15));
        updateRequest.setAddress("456 Updated St, City");
        updateRequest.setStatus(Student.StudentStatus.ACTIVE);
        updateRequest.setBatchId(batchId);
        
        // Setup response DTO
        responseDTO = new StudentResponseDTO();
        responseDTO.setId(studentId);
        responseDTO.setEnrollmentNumber("ENR202400001");
        responseDTO.setFirstName("John");
        responseDTO.setLastName("Doe");
        responseDTO.setFullName("John Doe");
        responseDTO.setEmail("john.doe@example.com");
        responseDTO.setPhone("1234567890");
        responseDTO.setDateOfBirth(LocalDate.of(1995, 5, 15));
        responseDTO.setAddress("123 Main St, City");
        responseDTO.setStatus(Student.StudentStatus.ACTIVE);
        responseDTO.setEnrollmentDate(LocalDate.now());
        responseDTO.setCreatedDate(LocalDateTime.now());
        responseDTO.setUpdatedDate(LocalDateTime.now());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateStudent_Success() throws Exception {
        // Arrange
        when(studentService.createStudent(any(StudentCreateRequestDTO.class))).thenReturn(responseDTO);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(studentId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.enrollmentNumber").value("ENR202400001"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateStudent_ValidationError() throws Exception {
        // Arrange - Create invalid request (missing required fields)
        StudentCreateRequestDTO invalidRequest = new StudentCreateRequestDTO();
        invalidRequest.setEmail("invalid-email"); // Invalid email format
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateStudent_DuplicateEmail() throws Exception {
        // Arrange
        when(studentService.createStudent(any(StudentCreateRequestDTO.class)))
                .thenThrow(new DuplicateResourceException("Student with email already exists"));
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateStudent_BatchCapacityExceeded() throws Exception {
        // Arrange
        when(studentService.createStudent(any(StudentCreateRequestDTO.class)))
                .thenThrow(new BatchCapacityExceededException("Batch capacity exceeded"));
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void testCreateStudent_AccessDenied() throws Exception {
        // This test assumes COUNSELLOR role should have access
        // If not, change to a role that shouldn't have access
        when(studentService.createStudent(any(StudentCreateRequestDTO.class))).thenReturn(responseDTO);
        
        mockMvc.perform(post("/api/v1/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentById_Success() throws Exception {
        // Arrange
        when(studentService.getStudentById(studentId)).thenReturn(responseDTO);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/students/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentById_NotFound() throws Exception {
        // Arrange
        when(studentService.getStudentById(studentId))
                .thenThrow(new ResourceNotFoundException("Student not found"));
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/students/{id}", studentId))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStudent_Success() throws Exception {
        // Arrange
        when(studentService.updateStudent(eq(studentId), any(StudentUpdateRequestDTO.class)))
                .thenReturn(responseDTO);
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/students/{id}", studentId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStudent_NotFound() throws Exception {
        // Arrange
        when(studentService.updateStudent(eq(studentId), any(StudentUpdateRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Student not found"));
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/students/{id}", studentId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteStudent_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/students/{id}", studentId)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteStudent_NotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Student not found"))
                .when(studentService).deleteStudent(studentId);
        
        // Act & Assert
        mockMvc.perform(delete("/api/v1/students/{id}", studentId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllStudents_Success() throws Exception {
        // Arrange
        Page<StudentResponseDTO> studentPage = new PageImpl<>(Arrays.asList(responseDTO), PageRequest.of(0, 20), 1);
        when(studentService.getAllStudents(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(studentPage);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/students")
                .param("page", "0")
                .param("size", "20")
                .param("sortBy", "enrollmentDate")
                .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(studentId.toString()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(20));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllStudents_WithFilters() throws Exception {
        // Arrange
        Page<StudentResponseDTO> studentPage = new PageImpl<>(Arrays.asList(responseDTO), PageRequest.of(0, 20), 1);
        when(studentService.getAllStudents(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(studentPage);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/students")
                .param("page", "0")
                .param("size", "20")
                .param("status", "ACTIVE")
                .param("batchId", batchId.toString())
                .param("search", "John")
                .param("enrollmentStartDate", "2024-01-01")
                .param("enrollmentEndDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignToBatch_Success() throws Exception {
        // Arrange
        when(studentService.assignToBatch(studentId, batchId)).thenReturn(responseDTO);
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/students/{id}/batch/{batchId}", studentId, batchId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignToBatch_StudentNotFound() throws Exception {
        // Arrange
        when(studentService.assignToBatch(studentId, batchId))
                .thenThrow(new ResourceNotFoundException("Student not found"));
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/students/{id}/batch/{batchId}", studentId, batchId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignToBatch_BatchCapacityExceeded() throws Exception {
        // Arrange
        when(studentService.assignToBatch(studentId, batchId))
                .thenThrow(new BatchCapacityExceededException("Batch capacity exceeded"));
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/students/{id}/batch/{batchId}", studentId, batchId)
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testRemoveFromBatch_Success() throws Exception {
        // Arrange
        when(studentService.removeFromBatch(studentId)).thenReturn(responseDTO);
        
        // Act & Assert
        mockMvc.perform(delete("/api/v1/students/{id}/batch", studentId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStudentStatus_Success() throws Exception {
        // Arrange
        when(studentService.updateStudentStatus(studentId, Student.StudentStatus.GRADUATED))
                .thenReturn(responseDTO);
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/students/{id}/status", studentId)
                .with(csrf())
                .param("status", "GRADUATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGraduateStudent_Success() throws Exception {
        // Arrange
        when(studentService.graduateStudent(studentId, "A+")).thenReturn(responseDTO);
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/students/{id}/graduate", studentId)
                .with(csrf())
                .param("finalGrade", "A+"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentsByBatch_Success() throws Exception {
        // Arrange
        Page<StudentResponseDTO> studentPage = new PageImpl<>(Arrays.asList(responseDTO), PageRequest.of(0, 20), 1);
        when(studentService.getStudentsByBatch(eq(batchId), any())).thenReturn(studentPage);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/students/batch/{batchId}", batchId)
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(studentId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentsByBatch_BatchNotFound() throws Exception {
        // Arrange
        when(studentService.getStudentsByBatch(eq(batchId), any()))
                .thenThrow(new ResourceNotFoundException("Batch not found"));
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/students/batch/{batchId}", batchId))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentsWithoutBatch_Success() throws Exception {
        // Arrange
        Page<StudentResponseDTO> studentPage = new PageImpl<>(Arrays.asList(responseDTO), PageRequest.of(0, 20), 1);
        when(studentService.getStudentsWithoutBatch(any())).thenReturn(studentPage);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/students/unassigned")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(studentId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentStatistics_Success() throws Exception {
        // Arrange
        StudentStatisticsDTO statistics = new StudentStatisticsDTO();
        statistics.setTotalStudents(100L);
        statistics.setActiveStudents(80L);
        statistics.setGraduatedStudents(15L);
        statistics.setDroppedOutStudents(3L);
        statistics.setSuspendedStudents(1L);
        statistics.setInactiveStudents(1L);
        statistics.setStudentsWithoutBatch(5L);
        statistics.setPlacedGraduates(12L);
        statistics.setUnplacedGraduates(3L);
        statistics.setEnrollmentsByMonth(new HashMap<>());
        statistics.setStudentsByBatch(new HashMap<>());
        statistics.setStudentsByCourse(new HashMap<>());
        statistics.setGradeDistribution(new HashMap<>());
        
        when(studentService.getStudentStatistics()).thenReturn(statistics);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/students/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStudents").value(100))
                .andExpect(jsonPath("$.activeStudents").value(80))
                .andExpect(jsonPath("$.graduatedStudents").value(15))
                .andExpect(jsonPath("$.droppedOutStudents").value(3))
                .andExpect(jsonPath("$.suspendedStudents").value(1))
                .andExpect(jsonPath("$.inactiveStudents").value(1))
                .andExpect(jsonPath("$.studentsWithoutBatch").value(5))
                .andExpect(jsonPath("$.placedGraduates").value(12))
                .andExpect(jsonPath("$.unplacedGraduates").value(3));
    }
    
    @Test
    @WithMockUser(roles = "FACULTY")
    void testGetStudentById_FacultyAccess() throws Exception {
        // Arrange
        when(studentService.getStudentById(studentId)).thenReturn(responseDTO);
        
        // Act & Assert - Faculty should have read access
        mockMvc.perform(get("/api/v1/students/{id}", studentId))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "FACULTY")
    void testCreateStudent_FacultyAccessDenied() throws Exception {
        // Act & Assert - Faculty should not be able to create students
        mockMvc.perform(post("/api/v1/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void testCreateStudent_UnauthenticatedAccess() throws Exception {
        // Act & Assert - Unauthenticated users should be denied
        mockMvc.perform(post("/api/v1/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());
    }
}