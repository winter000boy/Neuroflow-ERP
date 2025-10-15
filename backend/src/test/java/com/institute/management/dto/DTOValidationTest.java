package com.institute.management.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DTOValidationTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void leadCreateRequestDTO_ValidData_ShouldPassValidation() {
        // Arrange
        LeadCreateRequestDTO dto = new LeadCreateRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhone("1234567890");
        dto.setCourseInterest("Java Development");
        dto.setSource("Website");
        
        // Act
        Set<ConstraintViolation<LeadCreateRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void leadCreateRequestDTO_InvalidData_ShouldFailValidation() {
        // Arrange
        LeadCreateRequestDTO dto = new LeadCreateRequestDTO();
        dto.setFirstName(""); // Invalid: blank
        dto.setLastName("Doe");
        dto.setEmail("invalid-email"); // Invalid: not a valid email
        dto.setPhone("123"); // Invalid: too short
        
        // Act
        Set<ConstraintViolation<LeadCreateRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size());
    }
    
    @Test
    void studentCreateRequestDTO_ValidData_ShouldPassValidation() {
        // Arrange
        StudentCreateRequestDTO dto = new StudentCreateRequestDTO();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setEmail("jane.smith@example.com");
        dto.setPhone("9876543210");
        dto.setDateOfBirth(LocalDate.of(1995, 5, 15));
        dto.setEnrollmentDate(LocalDate.now());
        
        // Act
        Set<ConstraintViolation<StudentCreateRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void studentCreateRequestDTO_FutureDateOfBirth_ShouldFailValidation() {
        // Arrange
        StudentCreateRequestDTO dto = new StudentCreateRequestDTO();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setEmail("jane.smith@example.com");
        dto.setPhone("9876543210");
        dto.setDateOfBirth(LocalDate.now().plusDays(1)); // Invalid: future date
        dto.setEnrollmentDate(LocalDate.now());
        
        // Act
        Set<ConstraintViolation<StudentCreateRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be in the past")));
    }
    
    @Test
    void batchCreateRequestDTO_ValidData_ShouldPassValidation() {
        // Arrange
        BatchCreateRequestDTO dto = new BatchCreateRequestDTO();
        dto.setName("Java Batch 2024");
        dto.setCourseId(UUID.randomUUID());
        dto.setStartDate(LocalDate.now().plusDays(7));
        dto.setCapacity(30);
        
        // Act
        Set<ConstraintViolation<BatchCreateRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void batchCreateRequestDTO_InvalidCapacity_ShouldFailValidation() {
        // Arrange
        BatchCreateRequestDTO dto = new BatchCreateRequestDTO();
        dto.setName("Java Batch 2024");
        dto.setCourseId(UUID.randomUUID());
        dto.setStartDate(LocalDate.now().plusDays(7));
        dto.setCapacity(0); // Invalid: must be at least 1
        
        // Act
        Set<ConstraintViolation<BatchCreateRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be at least 1")));
    }
    
    @Test
    void courseCreateRequestDTO_ValidData_ShouldPassValidation() {
        // Arrange
        CourseCreateRequestDTO dto = new CourseCreateRequestDTO();
        dto.setName("Full Stack Java Development");
        dto.setDescription("Comprehensive Java development course");
        dto.setDurationMonths(6);
        dto.setFees(new BigDecimal("50000.00"));
        
        // Act
        Set<ConstraintViolation<CourseCreateRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void courseCreateRequestDTO_InvalidFees_ShouldFailValidation() {
        // Arrange
        CourseCreateRequestDTO dto = new CourseCreateRequestDTO();
        dto.setName("Full Stack Java Development");
        dto.setDescription("Comprehensive Java development course");
        dto.setDurationMonths(6);
        dto.setFees(new BigDecimal("0")); // Invalid: must be greater than 0
        
        // Act
        Set<ConstraintViolation<CourseCreateRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be greater than 0")));
    }
    
    @Test
    void placementCreateRequestDTO_ValidData_ShouldPassValidation() {
        // Arrange
        PlacementCreateRequestDTO dto = new PlacementCreateRequestDTO();
        dto.setStudentId(UUID.randomUUID());
        dto.setCompanyId(UUID.randomUUID());
        dto.setPosition("Software Developer");
        dto.setSalary(new BigDecimal("600000"));
        dto.setPlacementDate(LocalDate.now());
        
        // Act
        Set<ConstraintViolation<PlacementCreateRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void placementCreateRequestDTO_InvalidProbationPeriod_ShouldFailValidation() {
        // Arrange
        PlacementCreateRequestDTO dto = new PlacementCreateRequestDTO();
        dto.setStudentId(UUID.randomUUID());
        dto.setCompanyId(UUID.randomUUID());
        dto.setPosition("Software Developer");
        dto.setSalary(new BigDecimal("600000"));
        dto.setPlacementDate(LocalDate.now());
        dto.setProbationPeriodMonths(25); // Invalid: exceeds 24 months
        
        // Act
        Set<ConstraintViolation<PlacementCreateRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("cannot exceed 24 months")));
    }
}