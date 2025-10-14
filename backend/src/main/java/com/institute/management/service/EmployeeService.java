package com.institute.management.service;

import com.institute.management.entity.Employee;
import com.institute.management.repository.EmployeeRepository;
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
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    /**
     * Create a new employee - Only ADMIN can create employees
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
    
    /**
     * Update an existing employee - Only ADMIN can update employees
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Employee updateEmployee(UUID id, Employee employeeDetails) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setPhone(employeeDetails.getPhone());
        employee.setDepartment(employeeDetails.getDepartment());
        employee.setRole(employeeDetails.getRole());
        employee.setStatus(employeeDetails.getStatus());
        
        return employeeRepository.save(employee);
    }
    
    /**
     * Get employee by ID - All authenticated users can view employee details (read-only for non-admin)
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public Optional<Employee> getEmployeeById(UUID id) {
        return employeeRepository.findById(id);
    }
    
    /**
     * Get all employees with pagination - All authenticated users can view employees (read-only for non-admin)
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }
    
    /**
     * Get employees by role - All authenticated users can view employees by role
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public List<Employee> getEmployeesByRole(Employee.EmployeeRole role) {
        return employeeRepository.findByRole(role);
    }
    
    /**
     * Get employees by department - All authenticated users can view employees by department
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }
    
    /**
     * Get employees by status - All authenticated users can view employees by status
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public List<Employee> getEmployeesByStatus(Employee.EmployeeStatus status) {
        return employeeRepository.findByStatus(status);
    }
    
    /**
     * Update employee role - Only ADMIN can update employee roles
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Employee updateEmployeeRole(UUID employeeId, Employee.EmployeeRole newRole) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        
        employee.setRole(newRole);
        return employeeRepository.save(employee);
    }
    
    /**
     * Update employee status - Only ADMIN can update employee status
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Employee updateEmployeeStatus(UUID employeeId, Employee.EmployeeStatus status) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        
        employee.setStatus(status);
        return employeeRepository.save(employee);
    }
    
    /**
     * Deactivate employee - Only ADMIN can deactivate employees
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Employee deactivateEmployee(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        
        employee.setStatus(Employee.EmployeeStatus.INACTIVE);
        return employeeRepository.save(employee);
    }
    
    /**
     * Delete employee - Only ADMIN can delete employees (soft delete by setting status)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        
        // Soft delete by setting status to TERMINATED
        employee.setStatus(Employee.EmployeeStatus.TERMINATED);
        employeeRepository.save(employee);
    }
    
    /**
     * Get active employees - All authenticated users can view active employees
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public List<Employee> getActiveEmployees() {
        return employeeRepository.findByStatus(Employee.EmployeeStatus.ACTIVE);
    }
    
    /**
     * Get counsellors - All authenticated users can view counsellors
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public List<Employee> getCounsellors() {
        return employeeRepository.findByRoleAndStatus(Employee.EmployeeRole.COUNSELLOR, Employee.EmployeeStatus.ACTIVE);
    }
    
    /**
     * Get faculty members - All authenticated users can view faculty
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR') or hasRole('FACULTY') or hasRole('PLACEMENT_OFFICER') or hasRole('OPERATIONS')")
    public List<Employee> getFaculty() {
        return employeeRepository.findByRoleAndStatus(Employee.EmployeeRole.FACULTY, Employee.EmployeeStatus.ACTIVE);
    }
}