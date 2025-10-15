package com.institute.management.service;

import com.institute.management.dto.CourseStatisticsDTO;
import com.institute.management.dto.CourseRevenueDTO;
import com.institute.management.dto.CourseEnrollmentStatsDTO;
import com.institute.management.entity.Course;
import com.institute.management.entity.Batch;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.exception.ValidationException;
import com.institute.management.repository.CourseRepository;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private Batch testBatch;

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

        // Setup test batch
        testBatch = new Batch();
        testBatch.setId(UUID.randomUUID());
        testBatch.setName("Java Batch 2024-01");
        testBatch.setCourse(testCourse);
        testBatch.setStatus(Batch.BatchStatus.ACTIVE);
        testBatch.setCurrentEnrollment(20);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourse_Success() {
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Course result = courseService.createCourse(testCourse);

        assertNotNull(result);
        assertEquals(testCourse.getName(), result.getName());
        assertEquals(testCourse.getFees(), result.getFees());
        verify(courseRepository).save(testCourse);
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void createCourse_Success_Operations() {
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Course result = courseService.createCourse(testCourse);

        assertNotNull(result);
        assertEquals(testCourse.getName(), result.getName());
        verify(courseRepository).save(testCourse);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourse_Success() {
        Course updatedDetails = new Course();
        updatedDetails.setName("Advanced Java Programming");
        updatedDetails.setDescription("Advanced Java course with frameworks");
        updatedDetails.setDurationMonths(8);
        updatedDetails.setFees(new BigDecimal("60000"));
        updatedDetails.setStatus(Course.CourseStatus.ACTIVE);

        when(courseRepository.findById(testCourse.getId())).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Course result = courseService.updateCourse(testCourse.getId(), updatedDetails);

        assertNotNull(result);
        verify(courseRepository).findById(testCourse.getId());
        verify(courseRepository).save(testCourse);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourse_NotFound() {
        UUID courseId = UUID.randomUUID();
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.updateCourse(courseId, testCourse);
        });

        verify(courseRepository).findById(courseId);
        verify(courseRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getCourseById_Success() {
        when(courseRepository.findById(testCourse.getId())).thenReturn(Optional.of(testCourse));

        Optional<Course> result = courseService.getCourseById(testCourse.getId());

        assertTrue(result.isPresent());
        assertEquals(testCourse.getId(), result.get().getId());
        verify(courseRepository).findById(testCourse.getId());
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getCourseById_NotFound() {
        UUID courseId = UUID.randomUUID();
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        Optional<Course> result = courseService.getCourseById(courseId);

        assertFalse(result.isPresent());
        verify(courseRepository).findById(courseId);
    }

    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getAllCourses_Success() {
        List<Course> courses = Arrays.asList(testCourse);
        Page<Course> coursePage = new PageImpl<>(courses);
        Pageable pageable = PageRequest.of(0, 10);

        when(courseRepository.findAll(pageable)).thenReturn(coursePage);

        Page<Course> result = courseService.getAllCourses(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testCourse.getId(), result.getContent().get(0).getId());
        verify(courseRepository).findAll(pageable);
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getCoursesByStatus_Success() {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.findByStatus(Course.CourseStatus.ACTIVE)).thenReturn(courses);

        List<Course> result = courseService.getCoursesByStatus(Course.CourseStatus.ACTIVE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCourse.getId(), result.get(0).getId());
        verify(courseRepository).findByStatus(Course.CourseStatus.ACTIVE);
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void updateCourseStatus_Success() {
        Course.CourseStatus newStatus = Course.CourseStatus.INACTIVE;
        when(courseRepository.findById(testCourse.getId())).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Course result = courseService.updateCourseStatus(testCourse.getId(), newStatus);

        assertNotNull(result);
        verify(courseRepository).findById(testCourse.getId());
        verify(courseRepository).save(testCourse);
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void updateCourseStatus_NotFound() {
        UUID courseId = UUID.randomUUID();
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.updateCourseStatus(courseId, Course.CourseStatus.INACTIVE);
        });

        verify(courseRepository).findById(courseId);
        verify(courseRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCourse_Success() {
        when(courseRepository.findById(testCourse.getId())).thenReturn(Optional.of(testCourse));
        doNothing().when(courseRepository).deleteById(testCourse.getId());

        assertDoesNotThrow(() -> {
            courseService.deleteCourse(testCourse.getId());
        });

        verify(courseRepository).findById(testCourse.getId());
        verify(courseRepository).deleteById(testCourse.getId());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCourse_NotFound() {
        UUID courseId = UUID.randomUUID();
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.deleteCourse(courseId);
        });

        verify(courseRepository).findById(courseId);
        verify(courseRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCourse_WithActiveBatches() {
        testCourse.setBatches(Arrays.asList(testBatch));
        when(courseRepository.findById(testCourse.getId())).thenReturn(Optional.of(testCourse));

        assertThrows(ValidationException.class, () -> {
            courseService.deleteCourse(testCourse.getId());
        });

        verify(courseRepository).findById(testCourse.getId());
        verify(courseRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getActiveCourses_Success() {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.findByStatus(Course.CourseStatus.ACTIVE)).thenReturn(courses);

        List<Course> result = courseService.getActiveCourses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCourse.getId(), result.get(0).getId());
        verify(courseRepository).findByStatus(Course.CourseStatus.ACTIVE);
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void searchCoursesByName_Success() {
        List<Course> courses = Arrays.asList(testCourse);
        Page<Course> coursePage = new PageImpl<>(courses);
        Pageable pageable = PageRequest.of(0, 10);

        when(courseRepository.findCoursesWithFilters(any(), any(), any(), any(), any(), eq("Java"), eq(pageable)))
                .thenReturn(coursePage);

        Page<Course> result = courseService.searchCoursesByName("Java", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(courseRepository).findCoursesWithFilters(any(), any(), any(), any(), any(), eq("Java"), eq(pageable));
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getCoursesWithFilters_Success() {
        List<Course> courses = Arrays.asList(testCourse);
        Page<Course> coursePage = new PageImpl<>(courses);
        Pageable pageable = PageRequest.of(0, 10);

        when(courseRepository.findCoursesWithFilters(any(), any(), any(), any(), any(), any(), eq(pageable)))
                .thenReturn(coursePage);

        Page<Course> result = courseService.getCoursesWithFilters(
                Course.CourseStatus.ACTIVE, 3, 12,
                new BigDecimal("30000"), new BigDecimal("70000"),
                "Java", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(courseRepository).findCoursesWithFilters(any(), any(), any(), any(), any(), any(), eq(pageable));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCourseStatistics_Success() {
        when(courseRepository.count()).thenReturn(10L);
        when(courseRepository.countByStatus(Course.CourseStatus.ACTIVE)).thenReturn(8L);
        when(courseRepository.countByStatus(Course.CourseStatus.INACTIVE)).thenReturn(1L);
        when(courseRepository.countByStatus(Course.CourseStatus.ARCHIVED)).thenReturn(1L);
        when(courseRepository.getAverageDuration()).thenReturn(6.5);
        when(courseRepository.getAverageFees()).thenReturn(new BigDecimal("45000"));

        Object[] feesRange = { new BigDecimal("20000"), new BigDecimal("80000") };
        when(courseRepository.getFeesRange()).thenReturn(Arrays.<Object[]>asList(feesRange));

        CourseStatisticsDTO result = courseService.getCourseStatistics();

        assertNotNull(result);
        assertEquals(10L, result.getTotalCourses());
        assertEquals(8L, result.getActiveCourses());
        assertEquals(1L, result.getInactiveCourses());
        assertEquals(1L, result.getArchivedCourses());
        assertEquals(6.5, result.getAverageDuration());
        assertEquals(new BigDecimal("45000"), result.getAverageFees());
        assertEquals(new BigDecimal("20000"), result.getMinFees());
        assertEquals(new BigDecimal("80000"), result.getMaxFees());

        verify(courseRepository).count();
        verify(courseRepository, times(3)).countByStatus(any());
        verify(courseRepository).getAverageDuration();
        verify(courseRepository).getAverageFees();
        verify(courseRepository).getFeesRange();
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void getCourseRevenueReport_Success() {
        Object[] revenueData = { testCourse, new BigDecimal("500000") };
        List<Object[]> revenueResults = Arrays.<Object[]>asList(revenueData);

        when(courseRepository.getCourseRevenueReport()).thenReturn(revenueResults);

        List<CourseRevenueDTO> result = courseService.getCourseRevenueReport();

        assertNotNull(result);
        assertEquals(1, result.size());

        CourseRevenueDTO dto = result.get(0);
        assertEquals(testCourse.getId(), dto.getCourseId());
        assertEquals(testCourse.getName(), dto.getCourseName());
        assertEquals(new BigDecimal("500000"), dto.getTotalRevenue());

        verify(courseRepository).getCourseRevenueReport();
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void getCourseEnrollmentStats_Success() {
        Object[] enrollmentData = { testCourse, 50L };
        List<Object[]> enrollmentResults = Arrays.<Object[]>asList(enrollmentData);

        when(courseRepository.getCourseEnrollmentStats()).thenReturn(enrollmentResults);

        List<CourseEnrollmentStatsDTO> result = courseService.getCourseEnrollmentStats();

        assertNotNull(result);
        assertEquals(1, result.size());

        CourseEnrollmentStatsDTO dto = result.get(0);
        assertEquals(testCourse.getId(), dto.getCourseId());
        assertEquals(testCourse.getName(), dto.getCourseName());
        assertEquals(50, dto.getTotalEnrollments());

        verify(courseRepository).getCourseEnrollmentStats();
    }

    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getCoursesByFeeRange_Success() {
        BigDecimal minFees = new BigDecimal("30000");
        BigDecimal maxFees = new BigDecimal("60000");
        List<Course> courses = Arrays.asList(testCourse);

        when(courseRepository.findByFeesBetween(minFees, maxFees)).thenReturn(courses);

        List<Course> result = courseService.getCoursesByFeeRange(minFees, maxFees);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCourse.getId(), result.get(0).getId());
        verify(courseRepository).findByFeesBetween(minFees, maxFees);
    }

    @Test
    @WithMockUser(roles = "FACULTY")
    void getCoursesByDurationRange_Success() {
        Integer minDuration = 3;
        Integer maxDuration = 12;
        List<Course> courses = Arrays.asList(testCourse);

        when(courseRepository.findByDurationMonthsBetween(minDuration, maxDuration)).thenReturn(courses);

        List<Course> result = courseService.getCoursesByDurationRange(minDuration, maxDuration);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCourse.getId(), result.get(0).getId());
        verify(courseRepository).findByDurationMonthsBetween(minDuration, maxDuration);
    }
}