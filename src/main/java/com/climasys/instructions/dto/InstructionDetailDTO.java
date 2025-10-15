package com.climasys.instructions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for individual instruction details within a group
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Individual instruction detail within a group")
public class InstructionDetailDTO {
    
    @Schema(description = "Doctor ID", example = "DR-00001")
    private String doctorId;
    
    @Schema(description = "Instruction group description", example = "Post-Surgery Care")
    private String groupDescription;
    
    @Schema(description = "Instruction description", example = "Take prescribed antibiotics for 7 days", required = true)
    private String instructionsDescription;
    
    @Schema(description = "Sequence number for ordering", example = "1")
    private Integer sequenceNo;
    
    @Schema(description = "Priority value for sorting", example = "1")
    private Integer priorityValue;
    
    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdOn;
    
    @Schema(description = "Name of user who created the record")
    private String createdByName;
    
    @Schema(description = "Record modification timestamp")
    private LocalDateTime modifiedOn;
    
    @Schema(description = "Name of user who last modified the record")
    private String modifiedByName;
    
    @Schema(description = "Concatenated instruction group identifier (group*instruction)", 
            example = "Post-Surgery Care*Take prescribed antibiotics for 7 days")
    private String instructionGroup;
}

