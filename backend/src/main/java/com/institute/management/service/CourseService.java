package com.institute.management.service;

import com.institute.management.dto.CourseStatisticsDTO;
import com.institute.management.dto.CourseRevenueDTO;
import com.institute.management.dto.CourseEnrollmentStatsDTO;
import com.institute.management.entity.Course;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.exception.ValidationException;
import com.institute.management.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    /**
     * Create a new course - Only ADMIN and OPERATIONS can create courses
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }
    
    /**
     * Update an existing course - Only ADMIN and OPERATIONS can update courses
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public Course updateCourse(UUID id, Course courseDetails) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        course.setName(courseDetails.getName());
        course.setDescription(courseDetails.getDescription());
        course.setDurationMonths(courseDetails.getDurationMonths());
        course.setFees(courseDetails.getFees());
        if (courseDetails.getStatus() != null) {
            course.setStatus(courseDetails.getStatus());
        }
        
        return courseRepository.save(course);
    }
    
    /**
     * Get course by ID - All authenticated users can view courses
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY') or hasRole('COUNSELLOR') or hasRole('PLACEMENT_OFFICER')")
    public Optional<Course> getCourseById(UUID id) {
        return courseRepository.findById(id);
    }
    
    /**
     * Get all courses with pagination - All authenticated users can view courses
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY') or hasRole('COUNSELLOR') or hasRole('PLACEMENT_OFFICER')")
    public Page<Course> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }
    
    /**
     * Get courses by status - All authenticated users can view courses by status
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY') or hasRole('COUNSELLOR') or hasRole('PLACEMENT_OFFICER')")
    public List<Course> getCoursesByStatus(Course.CourseStatus status) {
        return courseRepository.findByStatus(status);
    }
    
    /**
     * Update course status - Only ADMIN and OPERATIONS can update status
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public Course updateCourseStatus(UUID courseId, Course.CourseStatus status) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        course.setStatus(status);
        return courseRepository.save(course);
    }
    
    /**
     * Delete course - Only ADMIN can delete courses
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCourse(UUID id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        // Check if course has active batches
        if (course.getBatches() != null && !course.getBatches().isEmpty()) {
            boolean hasActiveBatches = course.getBatches().stream()
                .anyMatch(batch -> batch.getStatus() == com.institute.management.entity.Batch.BatchStatus.ACTIVE ||
                                 batch.getStatus() == com.institute.management.entity.Batch.BatchStatus.PLANNED);
            if (hasActiveBatches) {
                throw new ValidationException("Cannot delete course with active batches");
            }
        }
        
        courseRepository.deleteById(id);
    }
    
    /**
     * Get active courses - All authenticated users can view active courses
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY') or hasRole('COUNSELLOR') or hasRole('PLACEMENT_OFFICER')")
    public List<Course> getActiveCourses() {
        return courseRepository.findByStatus(Course.CourseStatus.ACTIVE);
    }
    
    /**
     * Search courses by name - All authenticated users can search courses
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY') or hasRole('COUNSELLOR') or hasRole('PLACEMENT_OFFICER')")
    public Page<Course> searchCoursesByName(String name, Pageable pageable) {
        return courseRepository.findCoursesWithFilters(null, null, null, null, null, name, pageable);
    }
    
    /**
     * Get courses with filters - All authenticated users can view courses
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY') or hasRole('COUNSELLOR') or hasRole('PLACEMENT_OFFICER')")
    public Page<Course> getCoursesWithFilters(Course.CourseStatus status, Integer minDuration, Integer maxDuration,
                                            BigDecimal minFees, BigDecimal maxFees, String searchTerm, Pageable pageable) {
        return courseRepository.findCoursesWithFilters(status, minDuration, maxDuration, minFees, maxFees, searchTerm, pageable);
    }
    
    /**
     * Get course statistics - ADMIN and OPERATIONS can view statistics
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public CourseStatisticsDTO getCourseStatistics() {
        CourseStatisticsDTO stats = new CourseStatisticsDTO();
        
        stats.setTotalCourses(courseRepository.count());
        stats.setActiveCourses(courseRepository.countByStatus(Course.CourseStatus.ACTIVE));
        stats.setInactiveCourses(courseRepository.countByStatus(Course.CourseStatus.INACTIVE));
        stats.setArchivedCourses(courseRepository.countByStatus(Course.CourseStatus.ARCHIVED));
        
        Double avgDuration = courseRepository.getAverageDuration();
        stats.setAverageDuration(avgDuration != null ? avgDuration : 0.0);
        
        BigDecimal avgFees = courseRepository.getAverageFees();
        stats.setAverageFees(avgFees != null ? avgFees : BigDecimal.ZERO);
        
        List<Object[]> feesRange = courseRepository.getFeesRange();
        if (!feesRange.isEmpty() && feesRange.get(0) != null) {
            Object[] range = feesRange.get(0);
            stats.setMinFees((BigDecimal) range[0]);
            stats.setMaxFees((BigDecimal) range[1]);
        }
        
        return stats;
    }
    
    /**
     * Get course revenue report - ADMIN and OPERATIONS can view reports
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public List<CourseRevenueDTO> getCourseRevenueReport() {
        List<Object[]> results = courseRepository.getCourseRevenueReport();
        
        return results.stream().map(result -> {
            Course course = (Course) result[0];
            BigDecimal revenue = (BigDecimal) result[1];
            
            CourseRevenueDTO dto = new CourseRevenueDTO();
            dto.setCourseId(course.getId());
            dto.setCourseName(course.getName());
            dto.setTotalRevenue(revenue != null ? revenue : BigDecimal.ZERO);
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * Get course enrollment statistics - ADMIN and OPERATIONS can view statistics
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public List<CourseEnrollmentStatsDTO> getCourseEnrollmentStats() {
        List<Object[]> results = courseRepository.getCourseEnrollmentStats();
        
        return results.stream().map(result -> {
            Course course = (Course) result[0];
            Long enrollmentCount = (Long) result[1];
            
            CourseEnrollmentStatsDTO dto = new CourseEnrollmentStatsDTO();
            dto.setCourseId(course.getId());
            dto.setCourseName(course.getName());
            dto.setTotalEnrollments(enrollmentCount != null ? enrollmentCount.intValue() : 0);
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * Get courses by fee range - All authenticated users can view courses by fee range
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY') or hasRole('COUNSELLOR') or hasRole('PLACEMENT_OFFICER')")
    public List<Course> getCoursesByFeeRange(BigDecimal minFees, BigDecimal maxFees) {
        return courseRepository.findByFeesBetween(minFees, maxFees);
    }
    
    /**
     * Get courses by duration range - All authenticated users can view courses by duration range
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY') or hasRole('COUNSELLOR') or hasRole('PLACEMENT_OFFICER')")
    public List<Course> getCoursesByDurationRange(Integer minDuration, Integer maxDuration) {
        return courseRepository.findByDurationMonthsBetween(minDuration, maxDuration);
    }
}