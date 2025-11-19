package com.climasys.advance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for previous advance collection records
 * Used in "Previous Advance Collection Records" table on Advance Collection page
 * Based on USP_GET_AdvanceDetails enhanced view
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Previous advance collection record")
public class PreviousAdvanceCollectionDTO {
    
    @JsonProperty("admissionIpdNo")
    @Schema(description = "Admission / IPD Reference Number", example = "IPD-2024-01-0500")
    private String admissionIpdNo;
    
    @JsonProperty("admissionDate")
    @Schema(description = "Admission Date and Time", example = "20 Jan 2024 - 09:30:00")
    private String admissionDate;
    
    @JsonProperty("dischargeDate")
    @Schema(description = "Discharge Date and Time", example = "25 Jan 2024 - 14:30:00")
    private String dischargeDate;
    
    @JsonProperty("reasonOfAdmission")
    @Schema(description = "Reason of Admission", example = "RESPIRATORY INFECTION")
    private String reasonOfAdmission;
    
    @JsonProperty("insurance")
    @Schema(description = "Insurance status", example = "Yes")
    private String insurance;
    
    @JsonProperty("advanceDate")
    @Schema(description = "Advance Date", example = "21 Jan 2024")
    private String advanceDate;
    
    @JsonProperty("receiptNo")
    @Schema(description = "Receipt Number", example = "R-100")
    private String receiptNo;
    
    @JsonProperty("amount")
    @Schema(description = "Amount Received (Rs)", example = "5000.00")
    private BigDecimal amount;
}

