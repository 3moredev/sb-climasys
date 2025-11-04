package com.climasys.trends.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for patient's lab test trends
 * Based on USP_Get_PreviousLabReports stored procedure
 * 
 * Constructor parameter order must match the SQL SELECT statement order in LabTrendsRepository
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Lab test trends and results for patient visits")
public class LabTrendDTO {
    
    @Schema(description = "Visit date", example = "2025-10-29")
    private LocalDate visitDate;
    
    @Schema(description = "Patient visit number", example = "7")
    private Integer patientVisitNo;
    
    @Schema(description = "Lab test description", example = "Orthopedic Referral")
    private String labTestDescription;
    
    @Schema(description = "Parameter name", example = "Orthopedic Referral")
    private String parameterName;
    
    @Schema(description = "Parameter value/result", example = "11")
    private String parameterValue;
    
    @Schema(description = "Doctor name who ordered the test")
    private String doctorName;
    
    @Schema(description = "Lab name where test was conducted")
    private String labName;
    
    @Schema(description = "Report date", example = "2025-10-29")
    private String reportDate;
    
    @Schema(description = "Patient full name")
    private String patientFullName;
    
    @Schema(description = "Additional comments on the test result")
    private String comment;
    
    @Schema(description = "Patient's last visit number")
    private Integer patientLastVisitNo;
}

