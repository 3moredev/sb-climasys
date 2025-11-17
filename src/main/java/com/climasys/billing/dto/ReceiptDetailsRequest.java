package com.climasys.billing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for getting patient receipt details
 * Equivalent to USP_Get_PatientReceiptData_For_Receipt parameters
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDetailsRequest {
    
    @NotBlank(message = "Patient ID is required")
    @JsonProperty("patientId")
    private String patientId;
    
    @NotNull(message = "Shift ID is required")
    @JsonProperty("shiftId")
    private Integer shiftId;
    
    @NotBlank(message = "Clinic ID is required")
    @JsonProperty("clinicId")
    private String clinicId;
    
    @NotBlank(message = "Doctor ID is required")
    @JsonProperty("doctorId")
    private String doctorId;
    
    @NotNull(message = "Visit Date is required")
    @JsonProperty("visitDate")
    private LocalDate visitDate;
    
    @NotBlank(message = "Visit Type is required")
    @JsonProperty("visitType")
    private String visitType;
    
    @JsonProperty("pvisitno")
    private String pvisitno; // Patient Visit Number - optional
}

