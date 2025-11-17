package com.climasys.billing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for receipt details
 * Contains both receipt details and payment details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDetailsResponse {
    
    @JsonProperty("success")
    private Boolean success;
    
    @JsonProperty("receiptDetails")
    private List<ReceiptDetailsDTO> receiptDetails;
    
    @JsonProperty("paymentDetails")
    private List<PaymentDetailsDTO> paymentDetails;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("error")
    private String error;
}

