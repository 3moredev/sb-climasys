package com.climasys.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for lab test result submission
 * Provides feedback on the operation result
 */
public record LabTestResultResponse(
        
        boolean success,
        String message,
        String patientId,
        Integer patientVisitNo,
        String doctorId,
        String clinicId,
        Short shiftId,
        LocalDateTime visitDate,
        Integer recordsInserted,
        Integer recordsUpdated,
        List<String> errors,
        LocalDateTime processedAt
) {
    
    /**
     * Create a successful response
     */
    public static LabTestResultResponse success(String patientId, Integer patientVisitNo, 
                                              String doctorId, String clinicId, Short shiftId,
                                              LocalDateTime visitDate, Integer recordsInserted, 
                                              Integer recordsUpdated) {
        return new LabTestResultResponse(
                true,
                "Lab test results saved successfully",
                patientId,
                patientVisitNo,
                doctorId,
                clinicId,
                shiftId,
                visitDate,
                recordsInserted,
                recordsUpdated,
                null,
                LocalDateTime.now()
        );
    }
    
    /**
     * Create an error response
     */
    public static LabTestResultResponse error(String message, List<String> errors) {
        return new LabTestResultResponse(
                false,
                message,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                0,
                errors,
                LocalDateTime.now()
        );
    }
    
    /**
     * Create a simple error response
     */
    public static LabTestResultResponse error(String message) {
        return error(message, List.of(message));
    }
}
