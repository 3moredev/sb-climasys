package com.climasys.advance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response DTO for USP_Get_PatientHospitalBillReceiptData
 * Returns 3 tables:
 * - Table[0]: Receipt details from Patient_IPD_Receipts
 * - Table[1]: Payment details from Discharge_Bill_Hdr (if exists)
 * - Table[2]: Payment details from Advance_Collection_details (if exists)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalBillReceiptResponse {
    
    @JsonProperty("receiptDetails")
    private ReceiptDetails receiptDetails;
    
    @JsonProperty("billPaymentDetails")
    private PaymentDetails billPaymentDetails;
    
    @JsonProperty("advancePaymentDetails")
    private PaymentDetails advancePaymentDetails;
    
    /**
     * DTO for receipt details (Table[0])
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiptDetails {
        @JsonProperty("receiptNumber")
        private String receiptNumber;
        
        @JsonProperty("receiptDate")
        private LocalDate receiptDate;
        
        @JsonProperty("receiptType")
        private String receiptType;
        
        @JsonProperty("receiptAmount")
        private BigDecimal receiptAmount;
        
        @JsonProperty("treatmentDetails")
        private String treatmentDetails;
        
        @JsonProperty("title")
        private Short title;
        
        @JsonProperty("titleDescription")
        private String titleDescription;
        
        @JsonProperty("toDate")
        private LocalDate toDate;
        
        @JsonProperty("fromDate")
        private LocalDate fromDate;
    }
    
    /**
     * DTO for payment details (Table[1] and Table[2])
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentDetails {
        @JsonProperty("amount")
        private BigDecimal amount;
        
        @JsonProperty("paymentDescription")
        private String paymentDescription;
        
        @JsonProperty("paymentRemark")
        private String paymentRemark;
    }
}

