package com.institute.management.exception;

/**
 * Exception thrown when trying to enroll students beyond batch capacity
 */
public class BatchCapacityExceededException extends BusinessException {
    
    public BatchCapacityExceededException(String batchName, int capacity, int currentEnrollment) {
        super(String.format("Cannot enroll student in batch '%s'. Capacity: %d, Current enrollment: %d", 
              batchName, capacity, currentEnrollment), "BATCH_CAPACITY_EXCEEDED");
    }
    
    public BatchCapacityExceededException(String message) {
        super(message, "BATCH_CAPACITY_EXCEEDED");
    }
}