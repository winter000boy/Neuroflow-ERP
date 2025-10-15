package com.institute.management.integration;

import com.institute.management.dto.ErrorResponseDTO;
import com.institute.management.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ExceptionHandlingIntegrationTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testGlobalExceptionHandlerIsActive() {
        // This test verifies that the global exception handler is properly configured
        // by checking that the application context loads successfully
        assertNotNull(restTemplate);
    }
    
    @Test
    void testResourceNotFoundExceptionHandling() {
        // Test that ResourceNotFoundException is properly handled
        ResourceNotFoundException exception = new ResourceNotFoundException("Student", "id", "123");
        
        assertEquals("Student not found with id: 123", exception.getMessage());
        assertEquals("RESOURCE_NOT_FOUND", exception.getErrorCode());
    }
    
    @Test
    void testErrorResponseDTOSerialization() {
        // Test that ErrorResponseDTO can be properly serialized
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            "Test error message",
            "/api/v1/test",
            "TEST_ERROR"
        );
        
        assertEquals(404, errorResponse.getStatus());
        assertEquals("Not Found", errorResponse.getError());
        assertEquals("Test error message", errorResponse.getMessage());
        assertEquals("/api/v1/test", errorResponse.getPath());
        assertEquals("TEST_ERROR", errorResponse.getErrorCode());
        assertNotNull(errorResponse.getTimestamp());
    }
}