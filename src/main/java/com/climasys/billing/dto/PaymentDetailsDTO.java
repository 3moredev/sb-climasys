package com.climasys.billing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for payment details (Table[1] from stored procedure)
 * Contains payment method and remarks
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailsDTO {
    
    @JsonProperty("paymentRemark")
    private String paymentRemark;
    
    @JsonProperty("paymentDescription")
    private String paymentDescription;
}

