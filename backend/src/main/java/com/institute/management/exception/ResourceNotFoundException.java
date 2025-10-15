package com.institute.management.exception;

/**
 * Exception thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }
    
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s not found with identifier: %s", resourceType, identifier), "RESOURCE_NOT_FOUND");
    }
    
    public ResourceNotFoundException(String resourceType, String field, Object value) {
        super(String.format("%s not found with %s: %s", resourceType, field, value), "RESOURCE_NOT_FOUND");
    }
}