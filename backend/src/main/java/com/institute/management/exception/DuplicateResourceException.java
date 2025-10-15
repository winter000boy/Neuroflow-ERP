package com.institute.management.exception;

/**
 * Exception thrown when attempting to create a resource that already exists
 */
public class DuplicateResourceException extends BusinessException {
    
    public DuplicateResourceException(String message) {
        super(message, "DUPLICATE_RESOURCE");
    }
    
    public DuplicateResourceException(String resourceType, String field, Object value) {
        super(String.format("%s already exists with %s: %s", resourceType, field, value), "DUPLICATE_RESOURCE");
    }
}