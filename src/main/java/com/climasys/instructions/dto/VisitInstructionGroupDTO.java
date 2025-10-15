package com.climasys.instructions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for instruction groups associated with patient visits
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Instruction group data for a patient visit")
public class VisitInstructionGroupDTO {
    
    @Schema(description = "Doctor ID", example = "DR-00001")
    private String doctorId;
    
    @Schema(description = "Clinic ID", example = "CL-00001")
    private String clinicId;
    
    @Schema(description = "Shift ID", example = "1")
    private Short shiftId;
    
    @Schema(description = "Patient ID", example = "11-02-2019-020500")
    private String patientId;
    
    @Schema(description = "Patient visit number", example = "1")
    private Integer patientVisitNo;
    
    @Schema(description = "Visit date", example = "2023-10-13T10:00:00")
    private LocalDateTime visitDate;
    
    @Schema(description = "Instruction group description", example = "Post-Surgery Care")
    private String groupDescription;
    
    @Schema(description = "Instruction description", example = "Take prescribed antibiotics for 7 days")
    private String instructionsDescription;
    
    @Schema(description = "Sequence number for ordering", example = "1")
    private Integer sequenceNo;
    
    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdOn;
    
    @Schema(description = "Name of user who created the record")
    private String createdByName;
}

