package com.climasys.admission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request DTO for inserting/updating admission card
 * Based on USP_Insert_AdmissionCard stored procedure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdmissionCardRequest {
    
    @JsonProperty("patientId")
    @Schema(description = "Patient ID", example = "01-10-2021-051429", required = true)
    private String patientId;
    
    @JsonProperty("doctorId")
    @Schema(description = "Doctor ID", example = "DR-00010", required = true)
    private String doctorId;
    
    @JsonProperty("clinicId")
    @Schema(description = "Clinic ID", example = "CL-00001", required = true)
    private String clinicId;
    
    @JsonProperty("ipdRefNo")
    @Schema(description = "IPD Reference Number (auto-generated if empty)", example = "")
    private String ipdRefNo;
    
    @JsonProperty("relativeName")
    @Schema(description = "Relative Name", example = "John Doe")
    private String relativeName;
    
    @JsonProperty("relation")
    @Schema(description = "Relation with patient", example = "Brother")
    private String relation;
    
    @JsonProperty("contactNo")
    @Schema(description = "Relative Contact Number", example = "9876543210")
    private String contactNo;
    
    @JsonProperty("admissionDate")
    @Schema(description = "Date of Admission", example = "2022-08-20", required = true)
    private LocalDate admissionDate;
    
    @JsonProperty("admissionTime")
    @Schema(description = "Time of Admission", example = "14:45:00", required = true)
    private LocalTime admissionTime;
    
    @JsonProperty("reasonOfAdmission")
    @Schema(description = "Reason for admission", example = "SPINAL INJURY WITH PARAPLEGIA")
    private String reasonOfAdmission;
    
    @JsonProperty("department")
    @Schema(description = "Department", example = "Medicine")
    private String department;
    
    @JsonProperty("isInsurance")
    @Schema(description = "Insurance flag", example = "false")
    private Boolean isInsurance;
    
    @JsonProperty("insuranceDetails")
    @Schema(description = "Insurance Details", example = "")
    private String insuranceDetails;
    
    @JsonProperty("treatingDoctor")
    @Schema(description = "Treating Dr. / Surgeon", example = "Dr. Smith")
    private String treatingDoctor;
    
    @JsonProperty("consultingDoctor")
    @Schema(description = "Consulting Doctor", example = "Dr. Johnson")
    private String consultingDoctor;
    
    @JsonProperty("ipdFileNo")
    @Schema(description = "IPD File No", example = "152")
    private String ipdFileNo;
    
    @JsonProperty("roomNo")
    @Schema(description = "Room number", example = "101")
    private String roomNo;
    
    @JsonProperty("bedNo")
    @Schema(description = "Bed number", example = "A1")
    private String bedNo;
    
    @JsonProperty("packageRemarks")
    @Schema(description = "Package Remarks", example = "Standard package")
    private String packageRemarks;
    
    @JsonProperty("shiftId")
    @Schema(description = "Shift ID", example = "1", required = true)
    private Short shiftId;
    
    @JsonProperty("loginId")
    @Schema(description = "User login ID", example = "admin", required = true)
    private String loginId;
    
    @JsonProperty("referredDoctor")
    @Schema(description = "Referred By", example = "Dr. External")
    private String referredDoctor;
    
    @JsonProperty("commentsNote")
    @Schema(description = "Comments / Notes", example = "Patient requires special care")
    private String commentsNote;
    
    @JsonProperty("insuranceCompanyId")
    @Schema(description = "Insurance Company ID", example = "1")
    private Integer insuranceCompanyId;
}

