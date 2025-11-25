package com.climasys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Request DTO for inserting/updating lab test and parameters
 * Equivalent to the parameters passed to USP_Insert_LabTest_And_Parameters stored procedure
 * 
 * This replaces the stored procedure functionality with JPA
 */
public record LabTestAndParameterRequest(
        
        @NotBlank(message = "Doctor ID is required")
        @Size(max = 30, message = "Doctor ID must not exceed 30 characters")
        String doctorId,
        
        @NotBlank(message = "Clinic ID is required")
        @Size(max = 30, message = "Clinic ID must not exceed 30 characters")
        String clinicId,
        
        @Size(max = 40, message = "Group name must not exceed 40 characters")
        String groupName,
        
        @Size(max = 90, message = "Created by name must not exceed 90 characters")
        String createdBy,
        
        @Size(max = 90, message = "Modified by name must not exceed 90 characters")
        String modifiedBy,
        
        Integer priority,
        
        @NotNull(message = "Parameter data is required")
        List<LabTestParameterData> parameterData
) {
    
    /**
     * Nested DTO for individual lab test parameter data
     * Represents the structure of UDT_Insert_LabTest_And_Parameter
     */
    public record LabTestParameterData(
            
            @Size(max = 100, message = "Parameter name must not exceed 100 characters")
            String parameterName,
            
            @Size(max = 80, message = "Old lab test must not exceed 80 characters")
            String oldLabTest,
            
            @NotBlank(message = "New lab test is required")
            @Size(max = 80, message = "New lab test must not exceed 80 characters")
            String newLabTest,
            
            Integer oldPriority,
            
            Integer newPriority
    ) {}
}

