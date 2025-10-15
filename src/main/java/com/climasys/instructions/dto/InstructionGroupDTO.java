package com.climasys.instructions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for instruction group master data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Instruction group master data")
public class InstructionGroupDTO {
    
    @Schema(description = "Doctor ID", example = "DR-00001", required = true)
    private String doctorId;
    
    @Schema(description = "Instruction group description", example = "Post-Surgery Care", required = true)
    private String groupDescription;
    
    @Schema(description = "Priority value for sorting", example = "1")
    private Integer priorityValue;
    
    @Schema(description = "List of instructions in this group")
    private List<InstructionDetailDTO> instructions;
    
    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdOn;
    
    @Schema(description = "Name of user who created the record")
    private String createdByName;
    
    @Schema(description = "Record modification timestamp")
    private LocalDateTime modifiedOn;
    
    @Schema(description = "Name of user who last modified the record")
    private String modifiedByName;
}

