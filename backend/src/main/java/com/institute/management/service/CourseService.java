package com.institute.management.service;

import com.institute.management.entity.Course;
import com.institute.management.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
            .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        
        course.setName(courseDetails.getName());
        course.setDescription(courseDetails.getDescription());
        course.setDurationMonths(courseDetails.getDurationMonths());
        course.setFees(courseDetails.getFees());
        course.setStatus(courseDetails.getStatus());
        
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
            .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        
        course.setStatus(status);
        return courseRepository.save(course);
    }
    
    /**
     * Delete course - Only ADMIN can delete courses
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCourse(UUID id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with id: " + id);
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
}