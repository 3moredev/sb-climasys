package com.climasys.trends.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for patient's trends - previous visit vitals and clinical measurements
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Patient trends - previous visit vitals and clinical measurements")
public class PatientTrendsDTO {
    
    @Schema(description = "Visit date", example = "2019-02-11")
    private LocalDate visitDate;
    
    @Schema(description = "Patient ID", example = "11-02-2019-020500")
    private String patientId;
    
    @Schema(description = "Patient visit number", example = "1")
    private Integer patientVisitNo;
    
    @Schema(description = "Status ID", example = "5")
    private Short statusId;
    
    @Schema(description = "Visit time", example = "15:41:14")
    private LocalTime visitTime;
    
    @Schema(description = "Shift ID", example = "1")
    private Short shiftId;
    
    @Schema(description = "Shift description (M/E/N)", example = "M")
    private String shiftDescription;
    
    @Schema(description = "Blood pressure reading", example = "120/80")
    private String bloodPressure;
    
    @Schema(description = "Blood sugar level", example = "95")
    private String sugar;
    
    @Schema(description = "Thyroid hormone text", example = "Normal")
    private String thtext;
    
    @Schema(description = "Weight in kilograms", example = "68.5")
    private BigDecimal weightInKgs;
    
    @Schema(description = "Pulse rate", example = "72")
    private Integer pulse;
    
    @Schema(description = "Height in centimeters", example = "175.0")
    private BigDecimal heightInCms;
    
    @Schema(description = "TPR (Temperature, Pulse, Respiration)", example = "98.6")
    private String tpr;
    
    @Schema(description = "Important findings")
    private String importantFindings;
    
    @Schema(description = "Additional comments")
    private String additionalComments;
    
    @Schema(description = "Symptom comment (Detailed History)")
    private String symptomComment;
    
    @Schema(description = "Systemic examination findings")
    private String systemic;
    
    @Schema(description = "Edema findings")
    private String odeama;
    
    @Schema(description = "Pallor findings")
    private String pallor;
    
    @Schema(description = "General condition")
    private String gc;
    
    // Formatted display fields
    @Schema(description = "Formatted BP display", example = "13-Feb-2019 : M : 120/80")
    private String lastFiveBpValues;
    
    @Schema(description = "Formatted sugar display", example = "13-Feb-2019 : M : 95")
    private String lastFiveSugarValues;
    
    @Schema(description = "Formatted TH display", example = "13-Feb-2019 : M : Normal")
    private String lastFiveTHValues;
    
    @Schema(description = "Formatted weight display", example = "13-Feb-2019 : M : 68.5")
    private String lastFiveWeightValues;
    
    @Schema(description = "Previous date", example = "13-Feb-2019 : M")
    private String preDates;
    
    @Schema(description = "Previous BP", example = "120/80")
    private String preBp;
    
    @Schema(description = "Previous sugar", example = "95")
    private String preSugar;
    
    @Schema(description = "Previous TH", example = "Normal")
    private String preThtext;
    
    @Schema(description = "Previous weight", example = "68.5")
    private String preWeight;
    
    @Schema(description = "Previous pulse", example = "72")
    private String prePulse;
    
    @Schema(description = "Previous TPR", example = "98.6")
    private String preTpr;
    
    @Schema(description = "Previous systemic", example = "NAD")
    private String preSystemic;
    
    @Schema(description = "Previous odeama", example = "Nil")
    private String preOdeama;
    
    @Schema(description = "Previous height", example = "175.0")
    private String preHeightInCms;
    
    @Schema(description = "Previous important findings")
    private String preImportantFindings;
    
    @Schema(description = "Previous additional comments")
    private String preAdditionalComments;
    
    @Schema(description = "Previous symptom comment (Detailed History)")
    private String preSymptomComment;
    
    @Schema(description = "Previous pallor", example = "Nil")
    private String prePallor;
    
    @Schema(description = "Previous GC", example = "Good")
    private String preGc;
}

