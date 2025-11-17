package com.climasys.billing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for saving receipt details
 * Used when submitting payment on Collection screen
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveReceiptRequest {
    
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    @NotBlank(message = "Clinic ID is required")
    private String clinicId;
    
    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
    
    @NotNull(message = "Shift ID is required")
    private Integer shiftId;
    
    @NotNull(message = "Visit date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate visitDate;
    
    @NotNull(message = "Patient visit number is required")
    private Integer patientVisitNo;
    
    // Receipt details
    private String receiptNumber;  // Can be auto-generated if not provided
    
    @NotNull(message = "Receipt amount is required")
    private BigDecimal receiptAmount;
    
    private String treatmentDetails;
    
    // Optional: fetched from patient_master if not provided
    private Integer title;
    
    // Optional: defaults to current date if not provided
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;
    
    // Optional: defaults to current date if not provided
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;
    
    @NotBlank(message = "Visit type is required")
    private String visitType;
    
    // Payment details
    // Payment method ID (0 or null = no payment method / default to cash)
    private Integer paymentById;

    private String paymentRemark;
    
    // User details for audit
    @NotBlank(message = "User ID is required")
    private String userId;
    
    private String userName;
    
    // Optional: Discount
    private BigDecimal discount;
    
    // Optional: Fees collected
    private BigDecimal feesCollected;
}

