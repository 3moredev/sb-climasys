package com.climasys.admission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for patient admission card data
 * Based on USP_Get_Patient_AdmissionCard_data stored procedure
 * Used in Advance Collection Page to fetch admission details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Patient admission card data for advance collection")
public class PatientAdmissionCardDataDTO {
    
    @JsonProperty("admissionNo")
    @Schema(description = "Admission/IPD Reference Number", example = "IPD-2024-01-0500")
    private String admissionNo;
    
    @JsonProperty("ipdFileNo")
    @Schema(description = "IPD File Number", example = "152")
    private String ipdFileNo;
    
    @JsonProperty("admissionDate")
    @Schema(description = "Admission Date and Time", example = "20 Jan 2024 - 09:30:00")
    private String admissionDate;
    
    @JsonProperty("dischargeDate")
    @Schema(description = "Discharge Date and Time", example = "25 Jan 2024 - 14:30:00")
    private String dischargeDate;
    
    @JsonProperty("roomBed")
    @Schema(description = "Room and Bed Number", example = "201-C3")
    private String roomBed;
    
    @JsonProperty("department")
    @Schema(description = "Department", example = "Medicine")
    private String department;
    
    @JsonProperty("insurance")
    @Schema(description = "Insurance status", example = "Yes")
    private String insurance;
    
    @JsonProperty("company")
    @Schema(description = "Insurance Company Name", example = "ABC Insurance")
    private String company;
    
    @JsonProperty("hospitalBillNo")
    @Schema(description = "Hospital Bill Number", example = "BILL-001")
    private String hospitalBillNo;
    
    @JsonProperty("hospitalBillDate")
    @Schema(description = "Hospital Bill Date", example = "25 Jan 2024")
    private String hospitalBillDate;
    
    @JsonProperty("packageRemarks")
    @Schema(description = "Package Remarks", example = "Standard package")
    private String packageRemarks;
    
    @JsonProperty("totalAdvance")
    @Schema(description = "Total Advance Amount", example = "5000.00")
    private BigDecimal totalAdvance;
    
    @JsonProperty("reasonOfAdmission")
    @Schema(description = "Reason of Admission", example = "RESPIRATORY INFECTION")
    private String reasonOfAdmission;
}

