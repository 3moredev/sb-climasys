package com.climasys.advance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for inserting/updating advance collection
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvanceCollectionRequest {
    
    @JsonProperty("patientId")
    @Schema(description = "Patient ID", example = "01-10-2021-051429", required = true)
    private String patientId;
    
    @JsonProperty("doctorId")
    @Schema(description = "Doctor ID", example = "DR-00010", required = true)
    private String doctorId;
    
    @JsonProperty("clinicId")
    @Schema(description = "Clinic ID", example = "CL-00001", required = true)
    private String clinicId;
    
    @JsonProperty("ipdRefNo")
    @Schema(description = "IPD Reference Number", example = "IPD-2022-08-0312", required = true)
    private String ipdRefNo;
    
    @JsonProperty("date")
    @Schema(description = "Transaction date and time", example = "2022-08-20T14:45:00", required = true)
    private LocalDateTime date;
    
    @JsonProperty("amountReceived")
    @Schema(description = "Amount received", example = "5000.00", required = true)
    private BigDecimal amountReceived;
    
    @JsonProperty("paymentById")
    @Schema(description = "Payment type ID (1=Cash, etc.)", example = "1", required = true)
    private Short paymentById;
    
    @JsonProperty("paymentRemark")
    @Schema(description = "Payment remark/note", example = "Initial advance payment")
    private String paymentRemark;
    
    @JsonProperty("shiftId")
    @Schema(description = "Shift ID", example = "1", required = true)
    private Short shiftId;
    
    @JsonProperty("loginId")
    @Schema(description = "User login ID", example = "admin", required = true)
    private String loginId;
    
    @JsonProperty("advanceDate")
    @Schema(description = "Advance payment date and time", example = "2022-08-20T14:45:00", required = true)
    private LocalDateTime advanceDate;
}

