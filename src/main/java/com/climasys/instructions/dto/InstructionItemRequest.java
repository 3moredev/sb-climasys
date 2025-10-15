package com.climasys.instructions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for a single instruction item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Individual instruction item")
public class InstructionItemRequest {
    
    @Schema(description = "Instruction description", example = "Take prescribed antibiotics for 7 days", required = true)
    @NotBlank(message = "Instruction description is required")
    private String instructionsDescription;
    
    @Schema(description = "Sequence number for ordering", example = "1")
    private Integer sequenceNo;
    
    @Schema(description = "Priority value for sorting", example = "1")
    private Integer priorityValue;
}

