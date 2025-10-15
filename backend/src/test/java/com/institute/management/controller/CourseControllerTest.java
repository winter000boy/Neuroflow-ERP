package com.institute.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.institute.management.dto.*;
import com.institute.management.entity.Course;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.exception.ValidationException;
import com.institute.management.service.CourseService;
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

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    private Course testCourse;
    private CourseCreateRequestDTO createRequest;
    private CourseUpdateRequestDTO updateRequest;

    @BeforeEach
    void setUp() {
        // Setup test course
        testCourse = new Course();
        testCourse.setId(UUID.randomUUID());
        testCourse.setName("Java Programming");
        testCourse.setDescription("Comprehensive Java programming course");
        testCourse.setDurationMonths(6);
        testCourse.setFees(new BigDecimal("50000"));
        testCourse.setStatus(Course.CourseStatus.ACTIVE);
        testCourse.setCreatedDate(LocalDateTime.now());

        // Setup create request
        createRequest = new CourseCreateRequestDTO();
        createRequest.setName("Java Programming");
        createRequest.setDescription("Comprehensive Java programming course");
        createRequest.setDurationMonths(6);
        createRequest.setFees(new BigDecimal("50000"));
        // Status will be set to default ACTIVE in entity

        // Setup update request
        updateRequest = new CourseUpdateRequestDTO();
        updateRequest.setName("Advanced Java Programming");
        updateRequest.setDescription("Advanced Java programming course with frameworks");
        updateRequest.setDurationMonths(8);
        updateRequest.setFees(new BigDecimal("60000"));
        updateRequest.setStatus(Course.CourseStatus.ACTIVE);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourse_Success() throws Exception {
        when(courseService.createCourse(any(Course.class))).thenReturn(testCourse);

        mockMvc.perform(post("/api/v1/courses")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testCourse.getId().toString()))
                .andExpect(jsonPath("$.name").value(testCourse.getName()))
                .andExpect(jsonPath("$.description").value(testCourse.getDescription()))
                .andExpect(jsonPath("$.durationMonths").value(testCourse.getDurationMonths()))
                .andExpect(jsonPath("$.fees").value(testCourse.getFees()))
                .andExpect(jsonPath("$.status").value(testCourse.getStatus().toString()));

        verify(courseService).createCourse(any(Course.class));
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void createCourse_Success_Operations() throws Exception {
        when(courseService.createCourse(any(Course.class))).thenReturn(testCourse);

        mockMvc.perform(post("/api/v1/courses")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        verify(courseService).createCourse(any(Course.class));
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void createCourse_AccessDenied() throws Exception {
        mockMvc.perform(post("/api/v1/courses")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());

        verify(courseService, never()).createCourse(any(Course.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourse_InvalidInput() throws Exception {
        CourseCreateRequestDTO invalidRequest = new CourseCreateRequestDTO();
        // Missing required fields

        mockMvc.perform(post("/api/v1/courses")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(courseService, never()).createCourse(any(Course.class));
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getCourseById_Success() throws Exception {
        when(courseService.getCourseById(testCourse.getId())).thenReturn(Optional.of(testCourse));

        mockMvc.perform(get("/api/v1/courses/{id}", testCourse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCourse.getId().toString()))
                .andExpect(jsonPath("$.name").value(testCourse.getName()))
                .andExpect(jsonPath("$.description").value(testCourse.getDescription()))
                .andExpect(jsonPath("$.durationMonths").value(testCourse.getDurationMonths()))
                .andExpect(jsonPath("$.fees").value(testCourse.getFees()));

        verify(courseService).getCourseById(testCourse.getId());
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getCourseById_NotFound() throws Exception {
        UUID courseId = UUID.randomUUID();
        when(courseService.getCourseById(courseId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/courses/{id}", courseId))
                .andExpect(status().isNotFound());

        verify(courseService).getCourseById(courseId);
    }

    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getAllCourses_Success() throws Exception {
        List<Course> courses = Arrays.asList(testCourse);
        Page<Course> coursePage = new PageImpl<>(courses, PageRequest.of(0, 10), 1);
        
        when(courseService.getCoursesWithFilters(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(coursePage);

        mockMvc.perform(get("/api/v1/courses")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(testCourse.getId().toString()))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(courseService).getCoursesWithFilters(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void updateCourse_Success() throws Exception {
        Course updatedCourse = new Course();
        updatedCourse.setId(testCourse.getId());
        updatedCourse.setName(updateRequest.getName());
        updatedCourse.setDescription(updateRequest.getDescription());
        updatedCourse.setDurationMonths(updateRequest.getDurationMonths());
        updatedCourse.setFees(updateRequest.getFees());
        updatedCourse.setStatus(updateRequest.getStatus());

        when(courseService.updateCourse(eq(testCourse.getId()), any(Course.class))).thenReturn(updatedCourse);

        mockMvc.perform(put("/api/v1/courses/{id}", testCourse.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCourse.getId().toString()))
                .andExpect(jsonPath("$.name").value(updateRequest.getName()));

        verify(courseService).updateCourse(eq(testCourse.getId()), any(Course.class));
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void updateCourse_AccessDenied() throws Exception {
        mockMvc.perform(put("/api/v1/courses/{id}", testCourse.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());

        verify(courseService, never()).updateCourse(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCourse_Success() throws Exception {
        doNothing().when(courseService).deleteCourse(testCourse.getId());

        mockMvc.perform(delete("/api/v1/courses/{id}", testCourse.getId())
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(courseService).deleteCourse(testCourse.getId());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCourse_WithActiveBatches() throws Exception {
        doThrow(new ValidationException("Cannot delete course with active batches"))
                .when(courseService).deleteCourse(testCourse.getId());

        mockMvc.perform(delete("/api/v1/courses/{id}", testCourse.getId())
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(courseService).deleteCourse(testCourse.getId());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void deleteCourse_AccessDenied() throws Exception {
        mockMvc.perform(delete("/api/v1/courses/{id}", testCourse.getId())
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(courseService, never()).deleteCourse(any());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void updateCourseStatus_Success() throws Exception {
        Course.CourseStatus newStatus = Course.CourseStatus.INACTIVE;
        testCourse.setStatus(newStatus);
        when(courseService.updateCourseStatus(testCourse.getId(), newStatus)).thenReturn(testCourse);

        mockMvc.perform(patch("/api/v1/courses/{id}/status", testCourse.getId())
                .with(csrf())
                .param("status", newStatus.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(newStatus.toString()));

        verify(courseService).updateCourseStatus(testCourse.getId(), newStatus);
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getCoursesByStatus_Success() throws Exception {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseService.getCoursesByStatus(Course.CourseStatus.ACTIVE)).thenReturn(courses);

        mockMvc.perform(get("/api/v1/courses/by-status/{status}", Course.CourseStatus.ACTIVE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testCourse.getId().toString()));

        verify(courseService).getCoursesByStatus(Course.CourseStatus.ACTIVE);
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getActiveCourses_Success() throws Exception {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseService.getActiveCourses()).thenReturn(courses);

        mockMvc.perform(get("/api/v1/courses/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testCourse.getId().toString()));

        verify(courseService).getActiveCourses();
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void searchCourses_Success() throws Exception {
        List<Course> courses = Arrays.asList(testCourse);
        Page<Course> coursePage = new PageImpl<>(courses, PageRequest.of(0, 10), 1);
        
        when(courseService.searchCoursesByName(eq("Java"), any())).thenReturn(coursePage);

        mockMvc.perform(get("/api/v1/courses/search")
                .param("query", "Java")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(testCourse.getId().toString()));

        verify(courseService).searchCoursesByName(eq("Java"), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCourseStatistics_Success() throws Exception {
        CourseStatisticsDTO statistics = new CourseStatisticsDTO();
        statistics.setTotalCourses(10L);
        statistics.setActiveCourses(8L);
        statistics.setInactiveCourses(1L);
        statistics.setArchivedCourses(1L);
        statistics.setAverageDuration(6.5);
        statistics.setAverageFees(new BigDecimal("45000"));
        statistics.setMinFees(new BigDecimal("20000"));
        statistics.setMaxFees(new BigDecimal("80000"));

        when(courseService.getCourseStatistics()).thenReturn(statistics);

        mockMvc.perform(get("/api/v1/courses/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCourses").value(10))
                .andExpect(jsonPath("$.activeCourses").value(8))
                .andExpect(jsonPath("$.averageDuration").value(6.5))
                .andExpect(jsonPath("$.averageFees").value(45000));

        verify(courseService).getCourseStatistics();
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void getCourseRevenueReport_Success() throws Exception {
        CourseRevenueDTO revenueDTO = new CourseRevenueDTO();
        revenueDTO.setCourseId(testCourse.getId());
        revenueDTO.setCourseName(testCourse.getName());
        revenueDTO.setTotalRevenue(new BigDecimal("500000"));

        List<CourseRevenueDTO> report = Arrays.asList(revenueDTO);
        when(courseService.getCourseRevenueReport()).thenReturn(report);

        mockMvc.perform(get("/api/v1/courses/revenue-report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].courseId").value(testCourse.getId().toString()))
                .andExpect(jsonPath("$[0].totalRevenue").value(500000));

        verify(courseService).getCourseRevenueReport();
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void getCourseEnrollmentStats_Success() throws Exception {
        CourseEnrollmentStatsDTO statsDTO = new CourseEnrollmentStatsDTO();
        statsDTO.setCourseId(testCourse.getId());
        statsDTO.setCourseName(testCourse.getName());
        statsDTO.setTotalEnrollments(50);

        List<CourseEnrollmentStatsDTO> stats = Arrays.asList(statsDTO);
        when(courseService.getCourseEnrollmentStats()).thenReturn(stats);

        mockMvc.perform(get("/api/v1/courses/enrollment-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].courseId").value(testCourse.getId().toString()))
                .andExpect(jsonPath("$[0].totalEnrollments").value(50));

        verify(courseService).getCourseEnrollmentStats();
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getCoursesByFeeRange_Success() throws Exception {
        BigDecimal minFees = new BigDecimal("30000");
        BigDecimal maxFees = new BigDecimal("60000");
        List<Course> courses = Arrays.asList(testCourse);
        
        when(courseService.getCoursesByFeeRange(minFees, maxFees)).thenReturn(courses);

        mockMvc.perform(get("/api/v1/courses/by-fee-range")
                .param("minFees", minFees.toString())
                .param("maxFees", maxFees.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testCourse.getId().toString()));

        verify(courseService).getCoursesByFeeRange(minFees, maxFees);
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getCoursesByFeeRange_InvalidRange() throws Exception {
        BigDecimal minFees = new BigDecimal("60000");
        BigDecimal maxFees = new BigDecimal("30000"); // Invalid: max < min

        mockMvc.perform(get("/api/v1/courses/by-fee-range")
                .param("minFees", minFees.toString())
                .param("maxFees", maxFees.toString()))
                .andExpect(status().isBadRequest());

        verify(courseService, never()).getCoursesByFeeRange(any(), any());
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getCoursesByDurationRange_Success() throws Exception {
        Integer minDuration = 3;
        Integer maxDuration = 12;
        List<Course> courses = Arrays.asList(testCourse);
        
        when(courseService.getCoursesByDurationRange(minDuration, maxDuration)).thenReturn(courses);

        mockMvc.perform(get("/api/v1/courses/by-duration-range")
                .param("minDuration", minDuration.toString())
                .param("maxDuration", maxDuration.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testCourse.getId().toString()));

        verify(courseService).getCoursesByDurationRange(minDuration, maxDuration);
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getCoursesByDurationRange_InvalidRange() throws Exception {
        Integer minDuration = 12;
        Integer maxDuration = 6; // Invalid: max < min

        mockMvc.perform(get("/api/v1/courses/by-duration-range")
                .param("minDuration", minDuration.toString())
                .param("maxDuration", maxDuration.toString()))
                .andExpect(status().isBadRequest());

        verify(courseService, never()).getCoursesByDurationRange(any(), any());
    }
}