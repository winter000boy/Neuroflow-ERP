package com.institute.management.controller;

import com.institute.management.dto.*;
import com.institute.management.entity.Batch;
import com.institute.management.entity.Course;
import com.institute.management.entity.Employee;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.exception.ValidationException;
import com.institute.management.exception.BatchCapacityExceededException;
import com.institute.management.service.BatchService;
import com.institute.management.service.CourseService;
import com.institute.management.service.EmployeeService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/batches")
@Tag(name = "Batch Management", description = "APIs for managing batches")
public class BatchController {

    @Autowired
    private BatchService batchService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EmployeeService employeeService;

    @Operation(summary = "Create a new batch")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Batch created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    public ResponseEntity<BatchResponseDTO> createBatch(@Valid @RequestBody BatchCreateRequestDTO request) {
        // Validate course exists
        Course course = courseService.getCourseById(request.getCourseId())
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        // Validate instructor if provided
        Employee instructor = null;
        if (request.getInstructorId() != null) {
            instructor = employeeService.getEmployeeById(request.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + request.getInstructorId()));
        }

        // Create batch entity
        Batch batch = new Batch();
        batch.setName(request.getName());
        batch.setCourse(course);
        batch.setStartDate(request.getStartDate());
        batch.setCapacity(request.getCapacity());
        batch.setInstructor(instructor);
        
        // Status will be set to default PLANNED in entity

        Batch savedBatch = batchService.createBatch(batch);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(savedBatch));
    }

    @Operation(summary = "Get batch by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batch found"),
        @ApiResponse(responseCode = "404", description = "Batch not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BatchResponseDTO> getBatchById(@PathVariable UUID id) {
        Batch batch = batchService.getBatchById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + id));
        
        return ResponseEntity.ok(convertToResponseDTO(batch));
    }

    @Operation(summary = "Get all batches with pagination and filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batches retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public ResponseEntity<Page<BatchResponseDTO>> getAllBatches(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Filter by status") @RequestParam(required = false) Batch.BatchStatus status,
            @Parameter(description = "Filter by course ID") @RequestParam(required = false) UUID courseId,
            @Parameter(description = "Filter by instructor ID") @RequestParam(required = false) UUID instructorId,
            @Parameter(description = "Filter by available slots") @RequestParam(required = false) Boolean hasAvailableSlots,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Batch> batches = batchService.getBatchesWithFilters(status, courseId, instructorId, hasAvailableSlots, search, pageable);
        Page<BatchResponseDTO> response = batches.map(this::convertToResponseDTO);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update batch")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batch updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Batch not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BatchResponseDTO> updateBatch(
            @PathVariable UUID id, 
            @Valid @RequestBody BatchUpdateRequestDTO request) {
        
        // Validate course exists if provided
        Course course = null;
        if (request.getCourseId() != null) {
            course = courseService.getCourseById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));
        }

        // Validate instructor if provided
        Employee instructor = null;
        if (request.getInstructorId() != null) {
            instructor = employeeService.getEmployeeById(request.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + request.getInstructorId()));
        }

        // Create batch entity with updates
        Batch batchDetails = new Batch();
        batchDetails.setName(request.getName());
        batchDetails.setCourse(course);
        batchDetails.setStartDate(request.getStartDate());
        batchDetails.setCapacity(request.getCapacity());
        batchDetails.setInstructor(instructor);
        
        if (request.getStatus() != null) {
            batchDetails.setStatus(request.getStatus());
        }

        Batch updatedBatch = batchService.updateBatch(id, batchDetails);
        return ResponseEntity.ok(convertToResponseDTO(updatedBatch));
    }

    @Operation(summary = "Delete batch")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Batch deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Batch not found"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "400", description = "Cannot delete batch with enrolled students")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBatch(@PathVariable UUID id) {
        batchService.deleteBatch(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update batch capacity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batch capacity updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid capacity or capacity less than current enrollment"),
        @ApiResponse(responseCode = "404", description = "Batch not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PatchMapping("/{id}/capacity")
    public ResponseEntity<BatchResponseDTO> updateBatchCapacity(
            @PathVariable UUID id,
            @RequestParam Integer capacity) {
        
        if (capacity <= 0) {
            throw new ValidationException("Capacity must be greater than 0");
        }
        
        Batch updatedBatch = batchService.updateBatchCapacity(id, capacity);
        return ResponseEntity.ok(convertToResponseDTO(updatedBatch));
    }

    @Operation(summary = "Update batch status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batch status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Batch not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<BatchResponseDTO> updateBatchStatus(
            @PathVariable UUID id,
            @RequestParam Batch.BatchStatus status) {
        
        Batch updatedBatch = batchService.updateBatchStatus(id, status);
        return ResponseEntity.ok(convertToResponseDTO(updatedBatch));
    }

    @Operation(summary = "Get batch utilization report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilization report retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/utilization-report")
    public ResponseEntity<List<BatchUtilizationDTO>> getBatchUtilizationReport() {
        List<BatchUtilizationDTO> report = batchService.getBatchUtilizationReport();
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Get batches by course")
    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<BatchResponseDTO>> getBatchesByCourse(@PathVariable UUID courseId) {
        Course course = courseService.getCourseById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        List<Batch> batches = batchService.getBatchesByCourse(course);
        List<BatchResponseDTO> response = batches.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get batches by status")
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<BatchResponseDTO>> getBatchesByStatus(@PathVariable Batch.BatchStatus status) {
        List<Batch> batches = batchService.getBatchesByStatus(status);
        List<BatchResponseDTO> response = batches.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get batches by date range")
    @GetMapping("/by-date-range")
    public ResponseEntity<List<BatchResponseDTO>> getBatchesByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        
        List<Batch> batches = batchService.getBatchesByStartDateRange(startDate, endDate);
        List<BatchResponseDTO> response = batches.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check batch availability")
    @GetMapping("/{id}/availability")
    public ResponseEntity<BatchAvailabilityDTO> checkBatchAvailability(@PathVariable UUID id) {
        boolean hasCapacity = batchService.hasAvailableCapacity(id);
        Integer availableSlots = batchService.getAvailableSlots(id);
        
        BatchAvailabilityDTO availability = new BatchAvailabilityDTO();
        availability.setBatchId(id);
        availability.setHasAvailableSlots(hasCapacity);
        availability.setAvailableSlots(availableSlots);
        
        return ResponseEntity.ok(availability);
    }

    // Helper method to convert entity to DTO
    private BatchResponseDTO convertToResponseDTO(Batch batch) {
        BatchResponseDTO dto = new BatchResponseDTO();
        dto.setId(batch.getId());
        dto.setName(batch.getName());
        dto.setStartDate(batch.getStartDate());
        dto.setEndDate(batch.getEndDate());
        dto.setCapacity(batch.getCapacity());
        dto.setCurrentEnrollment(batch.getCurrentEnrollment());
        dto.setStatus(batch.getStatus());
        dto.setCreatedDate(batch.getCreatedDate());
        dto.setUpdatedDate(batch.getUpdatedDate());
        
        // Set course basic info
        if (batch.getCourse() != null) {
            CourseBasicDTO courseDto = new CourseBasicDTO();
            courseDto.setId(batch.getCourse().getId());
            courseDto.setName(batch.getCourse().getName());
            courseDto.setDurationMonths(batch.getCourse().getDurationMonths());
            courseDto.setFees(batch.getCourse().getFees());
            dto.setCourse(courseDto);
        }
        
        // Set instructor basic info
        if (batch.getInstructor() != null) {
            EmployeeBasicDTO instructorDto = new EmployeeBasicDTO();
            instructorDto.setId(batch.getInstructor().getId());
            instructorDto.setEmployeeCode(batch.getInstructor().getEmployeeCode());
            instructorDto.setFirstName(batch.getInstructor().getFirstName());
            instructorDto.setLastName(batch.getInstructor().getLastName());
            instructorDto.setRole(batch.getInstructor().getRole());
            dto.setInstructor(instructorDto);
        }
        
        return dto;
    }
}