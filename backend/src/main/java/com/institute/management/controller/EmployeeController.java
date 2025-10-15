package com.institute.management.controller;

import com.institute.management.dto.*;
import com.institute.management.entity.Employee;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @Operation(summary = "Create a new employee", description = "Create a new employee record (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Employee created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "409", description = "Employee with code/email/phone already exists")
    })
    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@Valid @RequestBody EmployeeCreateRequestDTO request) {
        EmployeeResponseDTO response = employeeService.createEmployee(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Get employee by ID", description = "Retrieve an employee by their unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee found"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable UUID id) {
        EmployeeResponseDTO response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update employee", description = "Update an existing employee's information (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Employee not found"),
        @ApiResponse(responseCode = "409", description = "Employee with code/email/phone already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeUpdateRequestDTO request) {
        EmployeeResponseDTO response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Delete employee", description = "Soft delete an employee by setting status to TERMINATED (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Get all employees", description = "Retrieve all employees with pagination and filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public ResponseEntity<Page<EmployeeResponseDTO>> getAllEmployees(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "firstName") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(description = "Filter by status") @RequestParam(required = false) Employee.EmployeeStatus status,
            @Parameter(description = "Filter by role") @RequestParam(required = false) Employee.EmployeeRole role,
            @Parameter(description = "Filter by department") @RequestParam(required = false) String department,
            @Parameter(description = "Search term (name, email, phone, employee code)") @RequestParam(required = false) String search) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EmployeeResponseDTO> response = employeeService.getAllEmployees(pageable, status, role, department, search);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get employees by role", description = "Retrieve all employees with a specific role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/role/{role}")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeesByRole(@PathVariable Employee.EmployeeRole role) {
        List<EmployeeResponseDTO> response = employeeService.getEmployeesByRole(role);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get employees by department", description = "Retrieve all employees in a specific department")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/department/{department}")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeesByDepartment(@PathVariable String department) {
        List<EmployeeResponseDTO> response = employeeService.getEmployeesByDepartment(department);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get employees by status", description = "Retrieve all employees with a specific status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeesByStatus(@PathVariable Employee.EmployeeStatus status) {
        List<EmployeeResponseDTO> response = employeeService.getEmployeesByStatus(status);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get active employees", description = "Retrieve all active employees")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active employees retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/active")
    public ResponseEntity<List<EmployeeResponseDTO>> getActiveEmployees() {
        List<EmployeeResponseDTO> response = employeeService.getActiveEmployees();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get counsellors", description = "Retrieve all active counsellors")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Counsellors retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/counsellors")
    public ResponseEntity<List<EmployeeResponseDTO>> getCounsellors() {
        List<EmployeeResponseDTO> response = employeeService.getCounsellors();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get faculty members", description = "Retrieve all active faculty members")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Faculty members retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/faculty")
    public ResponseEntity<List<EmployeeResponseDTO>> getFaculty() {
        List<EmployeeResponseDTO> response = employeeService.getFaculty();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update employee role", description = "Update an employee's role (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee role updated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PutMapping("/{id}/role")
    public ResponseEntity<EmployeeResponseDTO> updateEmployeeRole(
            @PathVariable UUID id,
            @RequestParam Employee.EmployeeRole role) {
        EmployeeResponseDTO response = employeeService.updateEmployeeRole(id, role);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update employee status", description = "Update an employee's status (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee status updated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<EmployeeResponseDTO> updateEmployeeStatus(
            @PathVariable UUID id,
            @RequestParam Employee.EmployeeStatus status) {
        EmployeeResponseDTO response = employeeService.updateEmployeeStatus(id, status);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Deactivate employee", description = "Deactivate an employee by setting status to INACTIVE (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee deactivated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<EmployeeResponseDTO> deactivateEmployee(@PathVariable UUID id) {
        EmployeeResponseDTO response = employeeService.deactivateEmployee(id);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Search employees", description = "Search employees by name, email, phone, or employee code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<EmployeeResponseDTO>> searchEmployees(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "firstName") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EmployeeResponseDTO> response = employeeService.searchEmployees(searchTerm, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get employees by role and department", description = "Retrieve employees filtered by both role and department")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/filter")
    public ResponseEntity<Page<EmployeeResponseDTO>> getEmployeesByRoleAndDepartment(
            @Parameter(description = "Filter by role") @RequestParam(required = false) Employee.EmployeeRole role,
            @Parameter(description = "Filter by department") @RequestParam(required = false) String department,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "firstName") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EmployeeResponseDTO> response = employeeService.getEmployeesByRoleAndDepartment(role, department, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get employees hired in date range", description = "Retrieve employees hired within a specific date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/hired-between")
    public ResponseEntity<Page<EmployeeResponseDTO>> getEmployeesHiredInDateRange(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "hireDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EmployeeResponseDTO> response = employeeService.getEmployeesHiredInDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(response);
    }
}