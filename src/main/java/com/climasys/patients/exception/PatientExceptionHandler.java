package com.climasys.patients.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for patient-related operations
 */
@RestControllerAdvice
public class PatientExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("field", ex.getField());
        errorResponse.put("errorCode", ex.getErrorCode());
        errorResponse.put("timestamp", java.time.LocalDateTime.now());
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(AreaValidationException.class)
    public ResponseEntity<Map<String, Object>> handleAreaValidationException(AreaValidationException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("field", ex.getField());
        errorResponse.put("errorCode", ex.getErrorCode());
        errorResponse.put("areaId", ex.getAreaId());
        errorResponse.put("cityId", ex.getCityId());
        errorResponse.put("stateId", ex.getStateId());
        errorResponse.put("countryId", ex.getCountryId());
        errorResponse.put("timestamp", java.time.LocalDateTime.now());
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(GenderValidationException.class)
    public ResponseEntity<Map<String, Object>> handleGenderValidationException(GenderValidationException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("field", ex.getField());
        errorResponse.put("errorCode", ex.getErrorCode());
        errorResponse.put("genderId", ex.getGenderId());
        errorResponse.put("timestamp", java.time.LocalDateTime.now());
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
