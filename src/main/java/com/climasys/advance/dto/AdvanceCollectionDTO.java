package com.climasys.advance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for advance collection details
 * Used in Manage Advance Collection page
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvanceCollectionDTO {
    
    @JsonProperty("advanceDate")
    private String advanceDate;
    
    @JsonProperty("advance")
    private BigDecimal advance;
}

