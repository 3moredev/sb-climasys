package com.climasys.patients.exception;

/**
 * Custom exception for validation failures in patient operations
 */
public class ValidationException extends RuntimeException {
    
    private final String field;
    private final String errorCode;
    
    public ValidationException(String message) {
        super(message);
        this.field = null;
        this.errorCode = "VALIDATION_ERROR";
    }
    
    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
        this.errorCode = "VALIDATION_ERROR";
    }
    
    public ValidationException(String field, String message, String errorCode) {
        super(message);
        this.field = field;
        this.errorCode = errorCode;
    }
    
    public String getField() {
        return field;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
