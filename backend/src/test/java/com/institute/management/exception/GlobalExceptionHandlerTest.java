package com.institute.management.exception;

import com.institute.management.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;
    
    @Mock
    private HttpServletRequest request;
    
    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/v1/test");
    }
    
    @Test
    void handleValidationErrors_ShouldReturnBadRequest() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("testObject", "testField", "Test error message");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
        
        // Act
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleValidationErrors(ex, request);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
        assertNotNull(response.getBody().getFieldErrors());
        assertTrue(response.getBody().getFieldErrors().containsKey("testField"));
    }
    
    @Test
    void handleConstraintViolation_ShouldReturnBadRequest() {
        // Arrange
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        
        when(ex.getConstraintViolations()).thenReturn(Set.of(violation));
        when(violation.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation.getPropertyPath().toString()).thenReturn("testProperty");
        when(violation.getMessage()).thenReturn("Test constraint violation");
        
        // Act
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleConstraintViolation(ex, request);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Constraint violation", response.getBody().getMessage());
        assertEquals("CONSTRAINT_VIOLATION", response.getBody().getErrorCode());
    }
    
    @Test
    void handleResourceNotFound_ShouldReturnNotFound() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Student", "id", "123");
        
        // Act
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleResourceNotFound(ex, request);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Student not found with id: 123", response.getBody().getMessage());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().getErrorCode());
    }
    
    @Test
    void handleDuplicateResource_ShouldReturnConflict() {
        // Arrange
        DuplicateResourceException ex = new DuplicateResourceException("Student", "email", "test@example.com");
        
        // Act
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleDuplicateResource(ex, request);
        
        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Student already exists with email: test@example.com", response.getBody().getMessage());
        assertEquals("DUPLICATE_RESOURCE", response.getBody().getErrorCode());
    }
    
    @Test
    void handleValidationException_ShouldReturnUnprocessableEntity() {
        // Arrange
        ValidationException ex = new ValidationException("Custom validation error");
        
        // Act
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleValidationException(ex, request);
        
        // Assert
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Custom validation error", response.getBody().getMessage());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
    }
    
    @Test
    void handleBusinessException_ShouldReturnUnprocessableEntity() {
        // Arrange
        BusinessException ex = new BusinessException("Business logic error", "BUSINESS_ERROR");
        
        // Act
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleBusinessException(ex, request);
        
        // Assert
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Business logic error", response.getBody().getMessage());
        assertEquals("BUSINESS_ERROR", response.getBody().getErrorCode());
    }
    
    @Test
    void handleAuthenticationException_ShouldReturnUnauthorized() {
        // Arrange
        BadCredentialsException ex = new BadCredentialsException("Invalid credentials");
        
        // Act
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleAuthenticationException(ex, request);
        
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Authentication failed", response.getBody().getMessage());
        assertEquals("AUTHENTICATION_ERROR", response.getBody().getErrorCode());
    }
    
    @Test
    void handleAccessDenied_ShouldReturnForbidden() {
        // Arrange
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        
        // Act
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleAccessDenied(ex, request);
        
        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Access denied", response.getBody().getMessage());
        assertEquals("ACCESS_DENIED", response.getBody().getErrorCode());
    }
    
    @Test
    void handleDataIntegrityViolation_ShouldReturnConflict() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException("unique constraint violation");
        
        // Act
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleDataIntegrityViolation(ex, request);
        
        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Resource already exists with the provided data", response.getBody().getMessage());
        assertEquals("DUPLICATE_RESOURCE", response.getBody().getErrorCode());
    }
    
    @Test
    void handleTypeMismatch_ShouldReturnBadRequest() {
        // Arrange
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("id");
        when(ex.getValue()).thenReturn("invalid-uuid");
        when(ex.getRequiredType()).thenReturn((Class) String.class);
        
        // Act
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleTypeMismatch(ex, request);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Invalid value"));
        assertEquals("TYPE_MISMATCH", response.getBody().getErrorCode());
    }
    
    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        // Arrange
        Exception ex = new RuntimeException("Unexpected error");
        
        // Act
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleGenericException(ex, request);
        
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertEquals("INTERNAL_ERROR", response.getBody().getErrorCode());
    }
}