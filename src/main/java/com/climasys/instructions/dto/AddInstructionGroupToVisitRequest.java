package com.climasys.instructions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for adding instruction groups to a patient visit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to add instruction groups to a patient visit")
public class AddInstructionGroupToVisitRequest {
    
    @Schema(description = "Doctor ID", example = "DR-00001", required = true)
    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
    
    @Schema(description = "Clinic ID", example = "CL-00001", required = true)
    @NotBlank(message = "Clinic ID is required")
    private String clinicId;
    
    @Schema(description = "Shift ID", example = "1", required = true)
    @NotNull(message = "Shift ID is required")
    private Short shiftId;
    
    @Schema(description = "Patient ID", example = "11-02-2019-020500", required = true)
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    @Schema(description = "Patient visit number", example = "1", required = true)
    @NotNull(message = "Patient visit number is required")
    private Integer patientVisitNo;
    
    @Schema(description = "Visit date", example = "2023-10-13T10:00:00", required = true)
    @NotNull(message = "Visit date is required")
    private LocalDateTime visitDate;
    
    @Schema(description = "List of instruction group descriptions to add", required = true)
    @NotEmpty(message = "At least one instruction group is required")
    private List<String> groupDescriptions;
    
    @Schema(description = "Name of user adding the instructions")
    private String createdByName;
}

