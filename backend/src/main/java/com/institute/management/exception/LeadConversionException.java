package com.institute.management.exception;

/**
 * Exception thrown when lead conversion fails
 */
public class LeadConversionException extends BusinessException {
    
    public LeadConversionException(String message) {
        super(message, "LEAD_CONVERSION_ERROR");
    }
    
    public LeadConversionException(String leadId, String reason) {
        super(String.format("Cannot convert lead %s: %s", leadId, reason), "LEAD_CONVERSION_ERROR");
    }
}