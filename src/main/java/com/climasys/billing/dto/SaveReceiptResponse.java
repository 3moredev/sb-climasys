package com.climasys.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for save receipt operation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveReceiptResponse {
    
    private boolean success;
    
    private String message;
    
    private String error;
    
    private String receiptNumber;  // Generated or provided receipt number
    
    private String receiptDate;    // Date when receipt was saved
    
    public SaveReceiptResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public SaveReceiptResponse(boolean success, String message, String receiptNumber) {
        this.success = success;
        this.message = message;
        this.receiptNumber = receiptNumber;
    }
}

