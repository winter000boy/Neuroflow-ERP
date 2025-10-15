package com.institute.management.controller;

import com.institute.management.dto.*;
import com.institute.management.entity.Course;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.exception.ValidationException;
import com.institute.management.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "Course Management", description = "APIs for managing courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Operation(summary = "Create a new course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Course created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseCreateRequestDTO request) {
        // Create course entity
        Course course = new Course();
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setDurationMonths(request.getDurationMonths());
        course.setFees(request.getFees());
        
        // Status will be set to default ACTIVE in entity

        Course savedCourse = courseService.createCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(savedCourse));
    }

    @Operation(summary = "Get course by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course found"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable UUID id) {
        Course course = courseService.getCourseById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        return ResponseEntity.ok(convertToResponseDTO(course));
    }

    @Operation(summary = "Get all courses with pagination and filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public ResponseEntity<Page<CourseResponseDTO>> getAllCourses(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Filter by status") @RequestParam(required = false) Course.CourseStatus status,
            @Parameter(description = "Minimum duration in months") @RequestParam(required = false) Integer minDuration,
            @Parameter(description = "Maximum duration in months") @RequestParam(required = false) Integer maxDuration,
            @Parameter(description = "Minimum fees") @RequestParam(required = false) BigDecimal minFees,
            @Parameter(description = "Maximum fees") @RequestParam(required = false) BigDecimal maxFees,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Course> courses = courseService.getCoursesWithFilters(status, minDuration, maxDuration, minFees, maxFees, search, pageable);
        Page<CourseResponseDTO> response = courses.map(this::convertToResponseDTO);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable UUID id, 
            @Valid @RequestBody CourseUpdateRequestDTO request) {
        
        // Create course entity with updates
        Course courseDetails = new Course();
        courseDetails.setName(request.getName());
        courseDetails.setDescription(request.getDescription());
        courseDetails.setDurationMonths(request.getDurationMonths());
        courseDetails.setFees(request.getFees());
        
        if (request.getStatus() != null) {
            courseDetails.setStatus(request.getStatus());
        }

        Course updatedCourse = courseService.updateCourse(id, courseDetails);
        return ResponseEntity.ok(convertToResponseDTO(updatedCourse));
    }

    @Operation(summary = "Delete course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "400", description = "Cannot delete course with active batches")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update course status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<CourseResponseDTO> updateCourseStatus(
            @PathVariable UUID id,
            @RequestParam Course.CourseStatus status) {
        
        Course updatedCourse = courseService.updateCourseStatus(id, status);
        return ResponseEntity.ok(convertToResponseDTO(updatedCourse));
    }

    @Operation(summary = "Get courses by status")
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesByStatus(@PathVariable Course.CourseStatus status) {
        List<Course> courses = courseService.getCoursesByStatus(status);
        List<CourseResponseDTO> response = courses.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get active courses")
    @GetMapping("/active")
    public ResponseEntity<List<CourseResponseDTO>> getActiveCourses() {
        List<Course> courses = courseService.getActiveCourses();
        List<CourseResponseDTO> response = courses.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search courses by name")
    @GetMapping("/search")
    public ResponseEntity<Page<CourseResponseDTO>> searchCourses(
            @Parameter(description = "Search term") @RequestParam String query,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> courses = courseService.searchCoursesByName(query, pageable);
        Page<CourseResponseDTO> response = courses.map(this::convertToResponseDTO);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get course statistics")
    @GetMapping("/statistics")
    public ResponseEntity<CourseStatisticsDTO> getCourseStatistics() {
        CourseStatisticsDTO statistics = courseService.getCourseStatistics();
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Get course revenue report")
    @GetMapping("/revenue-report")
    public ResponseEntity<List<CourseRevenueDTO>> getCourseRevenueReport() {
        List<CourseRevenueDTO> report = courseService.getCourseRevenueReport();
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Get course enrollment statistics")
    @GetMapping("/enrollment-stats")
    public ResponseEntity<List<CourseEnrollmentStatsDTO>> getCourseEnrollmentStats() {
        List<CourseEnrollmentStatsDTO> stats = courseService.getCourseEnrollmentStats();
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Get courses by fee range")
    @GetMapping("/by-fee-range")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesByFeeRange(
            @RequestParam BigDecimal minFees,
            @RequestParam BigDecimal maxFees) {
        
        if (minFees.compareTo(BigDecimal.ZERO) < 0 || maxFees.compareTo(minFees) < 0) {
            throw new ValidationException("Invalid fee range");
        }
        
        List<Course> courses = courseService.getCoursesByFeeRange(minFees, maxFees);
        List<CourseResponseDTO> response = courses.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get courses by duration range")
    @GetMapping("/by-duration-range")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesByDurationRange(
            @RequestParam Integer minDuration,
            @RequestParam Integer maxDuration) {
        
        if (minDuration <= 0 || maxDuration < minDuration) {
            throw new ValidationException("Invalid duration range");
        }
        
        List<Course> courses = courseService.getCoursesByDurationRange(minDuration, maxDuration);
        List<CourseResponseDTO> response = courses.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    // Helper method to convert entity to DTO
    private CourseResponseDTO convertToResponseDTO(Course course) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setDescription(course.getDescription());
        dto.setDurationMonths(course.getDurationMonths());
        dto.setFees(course.getFees());
        dto.setStatus(course.getStatus());
        dto.setCreatedDate(course.getCreatedDate());
        dto.setUpdatedDate(course.getUpdatedDate());
        
        // Set batch count and enrollment statistics
        if (course.getBatches() != null) {
            dto.setBatchCount(course.getBatches().size());
            
            // Calculate total enrollments across all batches
            int totalEnrollments = course.getBatches().stream()
                .mapToInt(batch -> batch.getCurrentEnrollment())
                .sum();
            dto.setTotalEnrollments(totalEnrollments);
        } else {
            dto.setBatchCount(0);
            dto.setTotalEnrollments(0);
        }
        
        return dto;
    }
}