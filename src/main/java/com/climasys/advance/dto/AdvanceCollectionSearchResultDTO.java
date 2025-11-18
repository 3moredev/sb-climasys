package com.climasys.advance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for advance collection search results
 * Used to display in the "List of Admitted Patient/s" table
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvanceCollectionSearchResultDTO {
    
    @JsonProperty("sr")
    private Integer sr;
    
    @JsonProperty("patientName")
    private String patientName;
    
    @JsonProperty("admissionIpdNo")
    private String admissionIpdNo;
    
    @JsonProperty("admissionDate")
    private String admissionDate;
    
    @JsonProperty("reasonOfAdmission")
    private String reasonOfAdmission;
    
    @JsonProperty("insurance")
    private String insurance;
    
    @JsonProperty("dateOfAdvance")
    private String dateOfAdvance;
    
    @JsonProperty("receiptNo")
    private String receiptNo;
    
    @JsonProperty("advance")
    private BigDecimal advance;
    
    @JsonProperty("patientId")
    private String patientId;
    
    @JsonProperty("clinicId")
    private String clinicId;
    
    @JsonProperty("doctorId")
    private String doctorId;
    
    // Legacy fields for backward compatibility (autocomplete)
    @JsonProperty("ipdRefNo")
    private String ipdRefNo;
    
    @JsonProperty("mobile")
    private String mobile;
    
    @JsonProperty("visitDate")
    private String visitDate;
    
    @JsonProperty("searchValue")
    private String searchValue;
}

