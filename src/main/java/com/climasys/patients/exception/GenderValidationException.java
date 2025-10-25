package com.climasys.patients.exception;

/**
 * Exception thrown when gender validation fails
 */
public class GenderValidationException extends ValidationException {
    
    private final String genderId;
    
    public GenderValidationException(String genderId) {
        super("gender", 
              String.format("Gender ID '%s' not found in gender_master table", genderId),
              "GENDER_NOT_FOUND");
        this.genderId = genderId;
    }
    
    public GenderValidationException(String message, boolean isCustomMessage) {
        super("gender", message, "GENDER_VALIDATION_ERROR");
        this.genderId = null;
    }
    
    public String getGenderId() {
        return genderId;
    }
}
