package com.climasys.instructions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating a new instruction group
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new instruction group with instructions")
public class CreateInstructionGroupRequest {
    
    @Schema(description = "Doctor ID", example = "DR-00001", required = true)
    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
    
    @Schema(description = "Instruction group description", example = "Post-Surgery Care", required = true)
    @NotBlank(message = "Group description is required")
    private String groupDescription;
    
    @Schema(description = "Priority value for sorting", example = "1")
    private Integer priorityValue;
    
    @Schema(description = "List of instructions to add to the group", required = true)
    @NotEmpty(message = "At least one instruction is required")
    @Valid
    private List<InstructionItemRequest> instructions;
    
    @Schema(description = "Name of user creating the group")
    private String createdByName;
}

