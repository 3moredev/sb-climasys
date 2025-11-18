package com.climasys.admission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for admission card list
 * Matches the fields shown in Manage Admission Card page
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdmissionCardDTO {
    
    @JsonProperty("serialNumber")
    private Integer serialNumber;
    
    @JsonProperty("patientName")
    private String patientName;
    
    @JsonProperty("admissionIpdNo")
    private String admissionIpdNo;
    
    @JsonProperty("ipdFileNo")
    private String ipdFileNo;
    
    @JsonProperty("admissionDate")
    private String admissionDate;
    
    @JsonProperty("reasonOfAdmission")
    private String reasonOfAdmission;
    
    @JsonProperty("dischargeDate")
    private String dischargeDate;
    
    @JsonProperty("insurance")
    private String insurance;
    
    @JsonProperty("company")
    private String company;
    
    @JsonProperty("advanceRs")
    private BigDecimal advanceRs;
    
    @JsonProperty("dateOfAdvance")
    private String dateOfAdvance;
    
    @JsonProperty("receiptNo")
    private String receiptNo;
    
    @JsonProperty("patientId")
    private String patientId;
}

