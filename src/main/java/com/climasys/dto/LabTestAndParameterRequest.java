package com.climasys.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Request DTO for inserting/updating lab test and parameters
 * Equivalent to the parameters passed to USP_Insert_LabTest_And_Parameters stored procedure
 * 
 * This replaces the stored procedure functionality with JPA
 * 
 * Supports multiple field name formats from frontend:
 * - doctorId/doctor_id, clinicId/clinic_id
 * - parameters/parameterData
 * - Description/New_Description/Old_Description at root level
 * - Priority_Value/priority/priorityValue
 */
public record LabTestAndParameterRequest(
        
        @JsonProperty("doctorId")
        @JsonAlias({"doctor_id"})
        @NotBlank(message = "Doctor ID is required")
        @Size(max = 30, message = "Doctor ID must not exceed 30 characters")
        String doctorId,
        
        @JsonProperty("clinicId")
        @JsonAlias({"clinic_id"})
        @NotBlank(message = "Clinic ID is required")
        @Size(max = 30, message = "Clinic ID must not exceed 30 characters")
        String clinicId,
        
        @JsonProperty("groupName")
        @JsonAlias({"group_name", "groupName"})
        @Size(max = 40, message = "Group name must not exceed 40 characters")
        String groupName,
        
        @JsonProperty("createdBy")
        @JsonAlias({"created_by", "createdBy"})
        @Size(max = 90, message = "Created by name must not exceed 90 characters")
        String createdBy,
        
        @JsonProperty("modifiedBy")
        @JsonAlias({"modified_by", "modifiedBy"})
        @Size(max = 90, message = "Modified by name must not exceed 90 characters")
        String modifiedBy,
        
        @JsonProperty("priority")
        @JsonAlias({"Priority_Value", "priorityValue", "Priority"})
        Integer priority,
        
        // Lab test description at root level (for frontend compatibility)
        @JsonProperty("description")
        @JsonAlias({"Description", "newDescription", "New_Description", "newLabTest"})
        String description,
        
        @JsonProperty("oldDescription")
        @JsonAlias({"Old_Description", "old_description", "oldDescription", "oldLabTest"})
        String oldDescription,
        
        @JsonProperty("parameterData")
        @JsonAlias({"parameters", "parameterData"})
        List<LabTestParameterData> parameterData
) {
    
    /**
     * Nested DTO for individual lab test parameter data
     * Represents the structure of UDT_Insert_LabTest_And_Parameter
     * 
     * Supports multiple field name formats from frontend
     */
    public record LabTestParameterData(
            
            @JsonProperty("parameterName")
            @JsonAlias({"parameter_name", "parameterName"})
            @Size(max = 100, message = "Parameter name must not exceed 100 characters")
            String parameterName,
            
            @JsonProperty("oldLabTest")
            @JsonAlias({"Old_Description", "old_description", "oldDescription", "oldLabTest"})
            @Size(max = 80, message = "Old lab test must not exceed 80 characters")
            String oldLabTest,
            
            @JsonProperty("newLabTest")
            @JsonAlias({"New_Description", "new_description", "newDescription", "newLabTest", "Description", "description"})
            @Size(max = 80, message = "New lab test must not exceed 80 characters")
            String newLabTest, // Optional - can be provided at root level instead
            
            @JsonProperty("oldPriority")
            @JsonAlias({"old_priority", "oldPriority", "Old_Priority"})
            Integer oldPriority,
            
            @JsonProperty("newPriority")
            @JsonAlias({"new_priority", "newPriority", "New_Priority"})
            Integer newPriority
    ) {}
}

