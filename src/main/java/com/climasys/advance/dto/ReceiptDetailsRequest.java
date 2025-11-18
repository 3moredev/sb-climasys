package com.climasys.advance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for saving advance receipt details
 * Replicates USP_Insert_AdvanceReceiptDetails parameters
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDetailsRequest {
    
    @JsonProperty("paymentDate")
    @Schema(description = "Payment date", example = "2022-08-20T14:45:00", required = true)
    private LocalDateTime paymentDate;
    
    @JsonProperty("clinicId")
    @Schema(description = "Clinic ID", example = "CL-00001", required = true)
    private String clinicId;
    
    @JsonProperty("doctorId")
    @Schema(description = "Doctor ID", example = "DR-00010", required = true)
    private String doctorId;
    
    @JsonProperty("patientId")
    @Schema(description = "Patient ID", example = "01-10-2021-051429", required = true)
    private String patientId;
    
    @JsonProperty("receiptNo")
    @Schema(description = "Receipt number", example = "R-100")
    private String receiptNo;
    
    @JsonProperty("receiptData")
    @Schema(description = "Receipt data list")
    private List<ReceiptDataItem> receiptData;
    
    @JsonProperty("userId")
    @Schema(description = "User ID", example = "admin", required = true)
    private String userId;
    
    @JsonProperty("receiptType")
    @Schema(description = "Receipt type", example = "A")
    private String receiptType;
    
    @JsonProperty("receiptAmount")
    @Schema(description = "Receipt amount", example = "5000.00", required = true)
    private java.math.BigDecimal receiptAmount;
    
    @JsonProperty("shiftId")
    @Schema(description = "Shift ID", example = "1", required = true)
    private Short shiftId;
    
    @JsonProperty("treatmentDetails")
    @Schema(description = "Treatment details")
    private String treatmentDetails;
    
    @JsonProperty("title")
    @Schema(description = "Title ID", example = "1")
    private Integer title;
    
    @JsonProperty("fromDate")
    @Schema(description = "From date", example = "2022-08-01")
    private LocalDate fromDate;
    
    @JsonProperty("toDate")
    @Schema(description = "To date", example = "2022-08-20")
    private LocalDate toDate;
    
    @JsonProperty("visitType")
    @Schema(description = "Visit type", example = "A")
    private String visitType;
    
    @JsonProperty("billNo")
    @Schema(description = "Bill number")
    private String billNo;
    
    @JsonProperty("date")
    @Schema(description = "Date of advance collection record to update", example = "2022-08-20T14:45:00", required = true)
    private LocalDateTime date;
    
    @JsonProperty("ipdRefNo")
    @Schema(description = "IPD Reference Number (optional, for better matching)")
    private String ipdRefNo;
    
    /**
     * Receipt data item for the receipt data list
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiptDataItem {
        @JsonProperty("doctorId")
        private String doctorId;
        
        @JsonProperty("clinicId")
        private String clinicId;
        
        @JsonProperty("patientId")
        private String patientId;
        
        @JsonProperty("receiptNumber")
        private String receiptNumber;
        
        @JsonProperty("receiptDate")
        private LocalDateTime receiptDate;
        
        @JsonProperty("receiptType")
        private String receiptType;
        
        @JsonProperty("receiptAmount")
        private java.math.BigDecimal receiptAmount;
        
        @JsonProperty("shiftId")
        private Short shiftId;
    }
}

