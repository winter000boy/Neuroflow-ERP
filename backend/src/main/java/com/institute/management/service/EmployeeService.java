package com.institute.management.service;

import com.institute.management.dto.*;
import com.institute.management.entity.Employee;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.exception.ValidationException;
import com.institute.management.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    /**
     * Create a new employee - Only ADMIN can create employees
     */
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponseDTO createEmployee(EmployeeCreateRequestDTO request) {
        // Validate unique constraints
        if (employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new DuplicateResourceException("Employee with code " + request.getEmployeeCode() + " already exists");
        }
        
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee with email " + request.getEmail() + " already exists");
        }
        
        if (request.getPhone() != null && employeeRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Employee with phone " + request.getPhone() + " already exists");
        }
        
        // Validate role
        validateEmployeeRole(request.getRole());
        
        // Create employee entity
        Employee employee = new Employee();
        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setDepartment(request.getDepartment());
        employee.setRole(request.getRole());
        employee.setHireDate(request.getHireDate());
        employee.setStatus(Employee.EmployeeStatus.ACTIVE);
        
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToResponseDTO(savedEmployee);
    }
    
    /**
     * Update an existing employee - Only ADMIN can update employees
     */
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponseDTO updateEmployee(UUID id, EmployeeUpdateRequestDTO request) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        // Validate unique constraints (excluding current employee)
        if (!employee.getEmployeeCode().equals(request.getEmployeeCode()) && 
            employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new DuplicateResourceException("Employee with code " + request.getEmployeeCode() + " already exists");
        }
        
        if (!employee.getEmail().equals(request.getEmail()) && 
            employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee with email " + request.getEmail() + " already exists");
        }
        
        if (request.getPhone() != null && !request.getPhone().equals(employee.getPhone()) && 
            employeeRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Employee with phone " + request.getPhone() + " already exists");
        }
        
        // Validate role
        validateEmployeeRole(request.getRole());
        
        // Update employee fields
        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setDepartment(request.getDepartment());
        employee.setRole(request.getRole());
        employee.setHireDate(request.getHireDate());
        employee.setStatus(request.getStatus());
        
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToResponseDTO(savedEmployee);
    }
    
    /**
     * Get employee by ID - All authenticated users can view employee details (read-only for non-admin)
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public EmployeeResponseDTO getEmployeeById(UUID id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return convertToResponseDTO(employee);
    }
    
    /**
     * Get employee entity by ID - Internal use for other services
     */
    public Optional<Employee> getEmployeeEntityById(UUID id) {
        return employeeRepository.findById(id);
    }
    
    /**
     * Get all employees with pagination and filtering - All authenticated users can view employees (read-only for non-admin)
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public Page<EmployeeResponseDTO> getAllEmployees(Pageable pageable, Employee.EmployeeStatus status, 
                                                    Employee.EmployeeRole role, String department, String searchTerm) {
        Page<Employee> employees = employeeRepository.findEmployeesWithFilters(status, role, department, searchTerm, pageable);
        return employees.map(this::convertToResponseDTO);
    }
    
    /**
     * Get employees by role - All authenticated users can view employees by role
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public List<EmployeeResponseDTO> getEmployeesByRole(Employee.EmployeeRole role) {
        List<Employee> employees = employeeRepository.findByRole(role);
        return employees.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }
    
    /**
     * Get employees by department - All authenticated users can view employees by department
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public List<EmployeeResponseDTO> getEmployeesByDepartment(String department) {
        List<Employee> employees = employeeRepository.findByDepartment(department);
        return employees.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }
    
    /**
     * Get employees by status - All authenticated users can view employees by status
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public List<EmployeeResponseDTO> getEmployeesByStatus(Employee.EmployeeStatus status) {
        List<Employee> employees = employeeRepository.findByStatus(status);
        return employees.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }
    
    /**
     * Update employee role - Only ADMIN can update employee roles
     */
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponseDTO updateEmployeeRole(UUID employeeId, Employee.EmployeeRole newRole) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        validateEmployeeRole(newRole);
        employee.setRole(newRole);
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToResponseDTO(savedEmployee);
    }
    
    /**
     * Update employee status - Only ADMIN can update employee status
     */
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponseDTO updateEmployeeStatus(UUID employeeId, Employee.EmployeeStatus status) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        employee.setStatus(status);
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToResponseDTO(savedEmployee);
    }
    
    /**
     * Deactivate employee - Only ADMIN can deactivate employees
     */
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponseDTO deactivateEmployee(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        employee.setStatus(Employee.EmployeeStatus.INACTIVE);
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToResponseDTO(savedEmployee);
    }
    
    /**
     * Delete employee - Only ADMIN can delete employees (soft delete by setting status)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        // Soft delete by setting status to TERMINATED
        employee.setStatus(Employee.EmployeeStatus.TERMINATED);
        employeeRepository.save(employee);
    }
    
    /**
     * Get active employees - All authenticated users can view active employees
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public List<EmployeeResponseDTO> getActiveEmployees() {
        List<Employee> employees = employeeRepository.findByStatus(Employee.EmployeeStatus.ACTIVE);
        return employees.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }
    
    /**
     * Get counsellors - All authenticated users can view counsellors
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public List<EmployeeResponseDTO> getCounsellors() {
        List<Employee> employees = employeeRepository.findByRoleAndStatus(Employee.EmployeeRole.COUNSELLOR, Employee.EmployeeStatus.ACTIVE);
        return employees.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }
    
    /**
     * Get faculty members - All authenticated users can view faculty
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public List<EmployeeResponseDTO> getFaculty() {
        List<Employee> employees = employeeRepository.findByRoleAndStatus(Employee.EmployeeRole.FACULTY, Employee.EmployeeStatus.ACTIVE);
        return employees.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }
    
    /**
     * Get employees by role and department - All authenticated users can view employees
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public Page<EmployeeResponseDTO> getEmployeesByRoleAndDepartment(Employee.EmployeeRole role, String department, Pageable pageable) {
        Page<Employee> employees = employeeRepository.findEmployeesWithFilters(Employee.EmployeeStatus.ACTIVE, role, department, null, pageable);
        return employees.map(this::convertToResponseDTO);
    }
    
    /**
     * Search employees by term - All authenticated users can search employees
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public Page<EmployeeResponseDTO> searchEmployees(String searchTerm, Pageable pageable) {
        Page<Employee> employees = employeeRepository.findEmployeesWithFilters(null, null, null, searchTerm, pageable);
        return employees.map(this::convertToResponseDTO);
    }
    
    /**
     * Get employees hired in date range - All authenticated users can view employees
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public Page<EmployeeResponseDTO> getEmployeesHiredInDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Employee> employees = employeeRepository.findByHireDateBetween(startDate, endDate, pageable);
        return employees.map(this::convertToResponseDTO);
    }
    
    // Helper methods
    
    /**
     * Convert Employee entity to EmployeeResponseDTO
     */
    private EmployeeResponseDTO convertToResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(employee.getId());
        dto.setEmployeeCode(employee.getEmployeeCode());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setFullName(employee.getFullName());
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        dto.setDepartment(employee.getDepartment());
        dto.setRole(employee.getRole());
        dto.setHireDate(employee.getHireDate());
        dto.setStatus(employee.getStatus());
        dto.setCreatedDate(employee.getCreatedDate());
        dto.setUpdatedDate(employee.getUpdatedDate());
        
        // Convert assigned leads to basic DTOs (if needed)
        if (employee.getAssignedLeads() != null && !employee.getAssignedLeads().isEmpty()) {
            dto.setAssignedLeads(employee.getAssignedLeads().stream()
                .map(this::convertLeadToBasicDTO)
                .collect(Collectors.toList()));
        }
        
        // Convert instructed batches to basic DTOs (if needed)
        if (employee.getInstructedBatches() != null && !employee.getInstructedBatches().isEmpty()) {
            dto.setInstructedBatches(employee.getInstructedBatches().stream()
                .map(this::convertBatchToBasicDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    /**
     * Convert Lead to LeadBasicDTO
     */
    private LeadBasicDTO convertLeadToBasicDTO(com.institute.management.entity.Lead lead) {
        LeadBasicDTO dto = new LeadBasicDTO();
        dto.setId(lead.getId());
        dto.setFirstName(lead.getFirstName());
        dto.setLastName(lead.getLastName());
        dto.setFullName(lead.getFullName());
        dto.setEmail(lead.getEmail());
        dto.setPhone(lead.getPhone());
        dto.setCourseInterest(lead.getCourseInterest());
        dto.setStatus(lead.getStatus());
        dto.setCreatedDate(lead.getCreatedDate());
        return dto;
    }
    
    /**
     * Convert Batch to BatchBasicDTO
     */
    private BatchBasicDTO convertBatchToBasicDTO(com.institute.management.entity.Batch batch) {
        BatchBasicDTO dto = new BatchBasicDTO();
        dto.setId(batch.getId());
        dto.setName(batch.getName());
        dto.setStartDate(batch.getStartDate());
        dto.setEndDate(batch.getEndDate());
        dto.setStatus(batch.getStatus());
        dto.setCapacity(batch.getCapacity());
        dto.setCurrentEnrollment(batch.getCurrentEnrollment());
        return dto;
    }
    
    /**
     * Validate employee role
     */
    private void validateEmployeeRole(Employee.EmployeeRole role) {
        if (role == null) {
            throw new ValidationException("Employee role is required");
        }
        
        // Additional role-specific validations can be added here
        switch (role) {
            case ADMIN:
            case COUNSELLOR:
            case FACULTY:
            case PLACEMENT_OFFICER:
            case OPERATIONS:
                // Valid roles
                break;
            default:
                throw new ValidationException("Invalid employee role: " + role);
        }
    }
}