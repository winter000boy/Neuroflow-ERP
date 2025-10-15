package com.institute.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.institute.management.dto.*;
import com.institute.management.entity.Batch;
import com.institute.management.entity.Course;
import com.institute.management.entity.Employee;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.exception.BatchCapacityExceededException;
import com.institute.management.exception.ValidationException;
import com.institute.management.service.BatchService;
import com.institute.management.service.CourseService;
import com.institute.management.service.EmployeeService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BatchController.class)
class BatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BatchService batchService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Batch testBatch;
    private Course testCourse;
    private Employee testInstructor;
    private BatchCreateRequestDTO createRequest;
    private BatchUpdateRequestDTO updateRequest;

    @BeforeEach
    void setUp() {
        // Setup test course
        testCourse = new Course();
        testCourse.setId(UUID.randomUUID());
        testCourse.setName("Java Programming");
        testCourse.setDurationMonths(6);
        testCourse.setFees(new BigDecimal("50000"));
        testCourse.setStatus(Course.CourseStatus.ACTIVE);

        // Setup test instructor
        testInstructor = new Employee();
        testInstructor.setId(UUID.randomUUID());
        testInstructor.setEmployeeCode("EMP001");
        testInstructor.setFirstName("John");
        testInstructor.setLastName("Doe");
        testInstructor.setRole(Employee.EmployeeRole.FACULTY);

        // Setup test batch
        testBatch = new Batch();
        testBatch.setId(UUID.randomUUID());
        testBatch.setName("Java Batch 2024-01");
        testBatch.setCourse(testCourse);
        testBatch.setStartDate(LocalDate.now().plusDays(30));
        testBatch.setCapacity(30);
        testBatch.setCurrentEnrollment(0);
        testBatch.setStatus(Batch.BatchStatus.PLANNED);
        testBatch.setInstructor(testInstructor);
        testBatch.setCreatedDate(LocalDateTime.now());

        // Setup create request
        createRequest = new BatchCreateRequestDTO();
        createRequest.setName("Java Batch 2024-01");
        createRequest.setCourseId(testCourse.getId());
        createRequest.setStartDate(LocalDate.now().plusDays(30));
        createRequest.setCapacity(30);
        createRequest.setInstructorId(testInstructor.getId());
        // Status will be set to default PLANNED in entity

        // Setup update request
        updateRequest = new BatchUpdateRequestDTO();
        updateRequest.setName("Java Batch 2024-01 Updated");
        updateRequest.setCourseId(testCourse.getId());
        updateRequest.setStartDate(LocalDate.now().plusDays(35));
        updateRequest.setCapacity(35);
        updateRequest.setInstructorId(testInstructor.getId());
        updateRequest.setStatus(Batch.BatchStatus.ACTIVE);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBatch_Success() throws Exception {
        when(courseService.getCourseById(testCourse.getId())).thenReturn(Optional.of(testCourse));
        when(employeeService.getEmployeeById(testInstructor.getId())).thenReturn(Optional.of(testInstructor));
        when(batchService.createBatch(any(Batch.class))).thenReturn(testBatch);

        mockMvc.perform(post("/api/v1/batches")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testBatch.getId().toString()))
                .andExpect(jsonPath("$.name").value(testBatch.getName()))
                .andExpect(jsonPath("$.capacity").value(testBatch.getCapacity()))
                .andExpect(jsonPath("$.currentEnrollment").value(testBatch.getCurrentEnrollment()))
                .andExpect(jsonPath("$.status").value(testBatch.getStatus().toString()));

        verify(batchService).createBatch(any(Batch.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBatch_CourseNotFound() throws Exception {
        when(courseService.getCourseById(testCourse.getId())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/batches")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isNotFound());

        verify(batchService, never()).createBatch(any(Batch.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBatch_InstructorNotFound() throws Exception {
        when(courseService.getCourseById(testCourse.getId())).thenReturn(Optional.of(testCourse));
        when(employeeService.getEmployeeById(testInstructor.getId())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/batches")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isNotFound());

        verify(batchService, never()).createBatch(any(Batch.class));
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void createBatch_AccessDenied() throws Exception {
        mockMvc.perform(post("/api/v1/batches")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());

        verify(batchService, never()).createBatch(any(Batch.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBatchById_Success() throws Exception {
        when(batchService.getBatchById(testBatch.getId())).thenReturn(Optional.of(testBatch));

        mockMvc.perform(get("/api/v1/batches/{id}", testBatch.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBatch.getId().toString()))
                .andExpect(jsonPath("$.name").value(testBatch.getName()))
                .andExpect(jsonPath("$.course.name").value(testCourse.getName()))
                .andExpect(jsonPath("$.instructor.firstName").value(testInstructor.getFirstName()));

        verify(batchService).getBatchById(testBatch.getId());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBatchById_NotFound() throws Exception {
        UUID batchId = UUID.randomUUID();
        when(batchService.getBatchById(batchId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/batches/{id}", batchId))
                .andExpect(status().isNotFound());

        verify(batchService).getBatchById(batchId);
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getAllBatches_Success() throws Exception {
        List<Batch> batches = Arrays.asList(testBatch);
        Page<Batch> batchPage = new PageImpl<>(batches, PageRequest.of(0, 10), 1);
        
        when(batchService.getBatchesWithFilters(any(), any(), any(), any(), any(), any()))
                .thenReturn(batchPage);

        mockMvc.perform(get("/api/v1/batches")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(testBatch.getId().toString()))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(batchService).getBatchesWithFilters(any(), any(), any(), any(), any(), any());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void updateBatch_Success() throws Exception {
        when(courseService.getCourseById(testCourse.getId())).thenReturn(Optional.of(testCourse));
        when(employeeService.getEmployeeById(testInstructor.getId())).thenReturn(Optional.of(testInstructor));
        when(batchService.updateBatch(eq(testBatch.getId()), any(Batch.class))).thenReturn(testBatch);

        mockMvc.perform(put("/api/v1/batches/{id}", testBatch.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBatch.getId().toString()));

        verify(batchService).updateBatch(eq(testBatch.getId()), any(Batch.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBatch_Success() throws Exception {
        doNothing().when(batchService).deleteBatch(testBatch.getId());

        mockMvc.perform(delete("/api/v1/batches/{id}", testBatch.getId())
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(batchService).deleteBatch(testBatch.getId());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBatch_WithEnrolledStudents() throws Exception {
        doThrow(new ValidationException("Cannot delete batch with enrolled students"))
                .when(batchService).deleteBatch(testBatch.getId());

        mockMvc.perform(delete("/api/v1/batches/{id}", testBatch.getId())
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(batchService).deleteBatch(testBatch.getId());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void updateBatchCapacity_Success() throws Exception {
        Integer newCapacity = 40;
        testBatch.setCapacity(newCapacity);
        when(batchService.updateBatchCapacity(testBatch.getId(), newCapacity)).thenReturn(testBatch);

        mockMvc.perform(patch("/api/v1/batches/{id}/capacity", testBatch.getId())
                .with(csrf())
                .param("capacity", newCapacity.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(newCapacity));

        verify(batchService).updateBatchCapacity(testBatch.getId(), newCapacity);
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void updateBatchCapacity_InvalidCapacity() throws Exception {
        Integer invalidCapacity = 0;

        mockMvc.perform(patch("/api/v1/batches/{id}/capacity", testBatch.getId())
                .with(csrf())
                .param("capacity", invalidCapacity.toString()))
                .andExpect(status().isBadRequest());

        verify(batchService, never()).updateBatchCapacity(any(), any());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void updateBatchCapacity_CapacityLessThanEnrollment() throws Exception {
        Integer newCapacity = 5;
        when(batchService.updateBatchCapacity(testBatch.getId(), newCapacity))
                .thenThrow(new BatchCapacityExceededException("New capacity cannot be less than current enrollment"));

        mockMvc.perform(patch("/api/v1/batches/{id}/capacity", testBatch.getId())
                .with(csrf())
                .param("capacity", newCapacity.toString()))
                .andExpect(status().isBadRequest());

        verify(batchService).updateBatchCapacity(testBatch.getId(), newCapacity);
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void updateBatchStatus_Success() throws Exception {
        Batch.BatchStatus newStatus = Batch.BatchStatus.ACTIVE;
        testBatch.setStatus(newStatus);
        when(batchService.updateBatchStatus(testBatch.getId(), newStatus)).thenReturn(testBatch);

        mockMvc.perform(patch("/api/v1/batches/{id}/status", testBatch.getId())
                .with(csrf())
                .param("status", newStatus.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(newStatus.toString()));

        verify(batchService).updateBatchStatus(testBatch.getId(), newStatus);
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void getBatchUtilizationReport_Success() throws Exception {
        BatchUtilizationDTO utilizationDTO = new BatchUtilizationDTO();
        utilizationDTO.setBatchId(testBatch.getId());
        utilizationDTO.setBatchName(testBatch.getName());
        utilizationDTO.setCourseName(testCourse.getName());
        utilizationDTO.setCapacity(30);
        utilizationDTO.setCurrentEnrollment(20);
        utilizationDTO.setUtilizationPercentage(66.67);
        utilizationDTO.setAvailableSlots(10);
        utilizationDTO.setStatus(Batch.BatchStatus.ACTIVE);

        List<BatchUtilizationDTO> report = Arrays.asList(utilizationDTO);
        when(batchService.getBatchUtilizationReport()).thenReturn(report);

        mockMvc.perform(get("/api/v1/batches/utilization-report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].batchId").value(testBatch.getId().toString()))
                .andExpect(jsonPath("$[0].utilizationPercentage").value(66.67));

        verify(batchService).getBatchUtilizationReport();
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getBatchesByCourse_Success() throws Exception {
        List<Batch> batches = Arrays.asList(testBatch);
        when(courseService.getCourseById(testCourse.getId())).thenReturn(Optional.of(testCourse));
        when(batchService.getBatchesByCourse(testCourse)).thenReturn(batches);

        mockMvc.perform(get("/api/v1/batches/by-course/{courseId}", testCourse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testBatch.getId().toString()));

        verify(batchService).getBatchesByCourse(testCourse);
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getBatchesByStatus_Success() throws Exception {
        List<Batch> batches = Arrays.asList(testBatch);
        when(batchService.getBatchesByStatus(Batch.BatchStatus.PLANNED)).thenReturn(batches);

        mockMvc.perform(get("/api/v1/batches/by-status/{status}", Batch.BatchStatus.PLANNED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testBatch.getId().toString()));

        verify(batchService).getBatchesByStatus(Batch.BatchStatus.PLANNED);
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getBatchesByDateRange_Success() throws Exception {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(60);
        List<Batch> batches = Arrays.asList(testBatch);
        when(batchService.getBatchesByStartDateRange(startDate, endDate)).thenReturn(batches);

        mockMvc.perform(get("/api/v1/batches/by-date-range")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testBatch.getId().toString()));

        verify(batchService).getBatchesByStartDateRange(startDate, endDate);
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void checkBatchAvailability_Success() throws Exception {
        when(batchService.hasAvailableCapacity(testBatch.getId())).thenReturn(true);
        when(batchService.getAvailableSlots(testBatch.getId())).thenReturn(10);

        mockMvc.perform(get("/api/v1/batches/{id}/availability", testBatch.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchId").value(testBatch.getId().toString()))
                .andExpect(jsonPath("$.hasAvailableSlots").value(true))
                .andExpect(jsonPath("$.availableSlots").value(10));

        verify(batchService).hasAvailableCapacity(testBatch.getId());
        verify(batchService).getAvailableSlots(testBatch.getId());
    }
}