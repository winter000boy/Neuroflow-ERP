package com.institute.management.controller;

import com.institute.management.dto.*;
import com.institute.management.entity.Student;
import com.institute.management.service.StudentService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Student Management", description = "APIs for managing students")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @Operation(summary = "Create a new student", description = "Enroll a new student in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Student created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "409", description = "Student with email/phone already exists")
    })
    @PostMapping
    public ResponseEntity<StudentResponseDTO> createStudent(@Valid @RequestBody StudentCreateRequestDTO request) {
        StudentResponseDTO response = studentService.createStudent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Get student by ID", description = "Retrieve a student by their unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student found"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable UUID id) {
        StudentResponseDTO response = studentService.getStudentById(id);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update student", description = "Update an existing student's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable UUID id,
            @Valid @RequestBody StudentUpdateRequestDTO request) {
        StudentResponseDTO response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Delete student", description = "Delete a student from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Student deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Get all students", description = "Retrieve all students with pagination and filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public ResponseEntity<Page<StudentResponseDTO>> getAllStudents(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "enrollmentDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Filter by status") @RequestParam(required = false) Student.StudentStatus status,
            @Parameter(description = "Filter by batch ID") @RequestParam(required = false) UUID batchId,
            @Parameter(description = "Filter by course ID") @RequestParam(required = false) UUID courseId,
            @Parameter(description = "Search term (name, email, phone, enrollment number)") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by enrollment start date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate enrollmentStartDate,
            @Parameter(description = "Filter by enrollment end date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate enrollmentEndDate) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<StudentResponseDTO> response = studentService.getAllStudents(
            pageable, status, batchId, courseId, search, enrollmentStartDate, enrollmentEndDate);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Assign student to batch", description = "Assign a student to a specific batch")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student assigned to batch successfully"),
        @ApiResponse(responseCode = "400", description = "Batch capacity exceeded"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Student or batch not found")
    })
    @PutMapping("/{id}/batch/{batchId}")
    public ResponseEntity<StudentResponseDTO> assignToBatch(
            @PathVariable UUID id,
            @PathVariable UUID batchId) {
        StudentResponseDTO response = studentService.assignToBatch(id, batchId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Remove student from batch", description = "Remove a student from their current batch")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student removed from batch successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @DeleteMapping("/{id}/batch")
    public ResponseEntity<StudentResponseDTO> removeFromBatch(@PathVariable UUID id) {
        StudentResponseDTO response = studentService.removeFromBatch(id);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update student status", description = "Update a student's status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student status updated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<StudentResponseDTO> updateStudentStatus(
            @PathVariable UUID id,
            @RequestParam Student.StudentStatus status) {
        StudentResponseDTO response = studentService.updateStudentStatus(id, status);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Graduate student", description = "Mark a student as graduated with final grade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student graduated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @PutMapping("/{id}/graduate")
    public ResponseEntity<StudentResponseDTO> graduateStudent(
            @PathVariable UUID id,
            @RequestParam String finalGrade) {
        StudentResponseDTO response = studentService.graduateStudent(id, finalGrade);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get students by batch", description = "Retrieve all students in a specific batch")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Batch not found")
    })
    @GetMapping("/batch/{batchId}")
    public ResponseEntity<Page<StudentResponseDTO>> getStudentsByBatch(
            @PathVariable UUID batchId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<StudentResponseDTO> response = studentService.getStudentsByBatch(batchId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get students without batch", description = "Retrieve all students not assigned to any batch")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/unassigned")
    public ResponseEntity<Page<StudentResponseDTO>> getStudentsWithoutBatch(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "enrollmentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<StudentResponseDTO> response = studentService.getStudentsWithoutBatch(pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get student statistics", description = "Get statistics about students")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/statistics")
    public ResponseEntity<StudentStatisticsDTO> getStudentStatistics() {
        StudentStatisticsDTO response = studentService.getStudentStatistics();
        return ResponseEntity.ok(response);
    }
}