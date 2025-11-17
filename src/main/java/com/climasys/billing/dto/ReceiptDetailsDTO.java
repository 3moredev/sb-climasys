package com.climasys.billing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for receipt details (Table[0] from stored procedure)
 * Contains main receipt information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDetailsDTO {
    
    @JsonProperty("receiptNumber")
    private String receiptNumber;
    
    @JsonProperty("receiptAmount")
    private BigDecimal receiptAmount;
    
    @JsonProperty("treatmentDetails")
    private String treatmentDetails;
    
    @JsonProperty("fromDate")
    private LocalDate fromDate;
    
    @JsonProperty("toDate")
    private LocalDate toDate;
    
    @JsonProperty("title")
    private Integer title;
    
    @JsonProperty("titleDescription")
    private String titleDescription;
    
    @JsonProperty("discount")
    private BigDecimal discount;
}

