package com.climasys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request DTO for lab test result submission
 * Equivalent to the parameters passed to USP_Insert_LabTestAllData stored procedure
 */
public record LabTestResultRequest(
        
        @NotBlank(message = "Patient ID is required")
        @Size(max = 32, message = "Patient ID must not exceed 32 characters")
        String patientId,
        
        @NotNull(message = "Patient Visit Number is required")
        Integer patientVisitNo,
        
        @NotBlank(message = "Doctor ID is required")
        @Size(max = 30, message = "Doctor ID must not exceed 30 characters")
        String doctorId,
        
        @NotBlank(message = "Clinic ID is required")
        @Size(max = 10, message = "Clinic ID must not exceed 10 characters")
        String clinicId,
        
        @NotNull(message = "Shift ID is required")
        Short shiftId,
        
        @NotBlank(message = "User ID is required")
        @Size(max = 32, message = "User ID must not exceed 32 characters")
        String userId,
        
        @Size(max = 200, message = "Doctor name must not exceed 200 characters")
        String doctorName,
        
        @Size(max = 200, message = "Lab name must not exceed 200 characters")
        String labName,
        
        @Size(max = 200, message = "Report date must not exceed 200 characters")
        String reportDate,
        
        @Size(max = 1000, message = "Comment must not exceed 1000 characters")
        String comment,
        
        @NotNull(message = "Test report data is required")
        List<LabTestParameterData> testReportData
) {
    
    /**
     * Nested DTO for individual lab test parameter data
     * Represents the structure of UDT_InsertTestReport
     */
    public record LabTestParameterData(
            
            @NotBlank(message = "Visit date is required")
            String visitDate,
            
            @NotNull(message = "Patient visit number is required")
            Integer patientVisitNo,
            
            @NotNull(message = "Shift ID is required")
            Short shiftId,
            
            @NotBlank(message = "Clinic ID is required")
            String clinicId,
            
            @NotBlank(message = "Doctor ID is required")
            String doctorId,
            
            @NotBlank(message = "Patient ID is required")
            String patientId,
            
            @NotBlank(message = "Lab test description is required")
            @Size(max = 80, message = "Lab test description must not exceed 80 characters")
            String labTestDescription,
            
            @NotBlank(message = "Parameter name is required")
            @Size(max = 100, message = "Parameter name must not exceed 100 characters")
            String parameterName,
            
            @Size(max = 2000, message = "Test parameter value must not exceed 2000 characters")
            String testParameterValue
    ) {}
}
