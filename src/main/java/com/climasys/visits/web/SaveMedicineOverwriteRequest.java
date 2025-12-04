package com.climasys.visits.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record SaveMedicineOverwriteRequest(
        @NotNull
        @JsonProperty("visitDate")
        String visitDate,
        
        @NotNull
        @JsonProperty("patientVisitNo")
        Integer patientVisitNo,
        
        @NotNull
        @JsonProperty("shiftId")
        Short shiftId,
        
        @NotNull
        @JsonProperty("clinicId")
        String clinicId,
        
        @NotNull
        @JsonProperty("doctorId")
        String doctorId,
        
        @NotNull
        @JsonProperty("patientId")
        String patientId,
        
        @JsonProperty("medicineRows")
        List<Map<String, Object>> medicineRows,
        
        @JsonProperty("prescriptionRows")
        List<Map<String, Object>> prescriptionRows,
        
        @JsonProperty("feesToCollect")
        BigDecimal feesToCollect,
        
        @JsonProperty("feesCollected")
        BigDecimal feesCollected,
        
        @NotNull
        @JsonProperty("userId")
        String userId,
        
        @NotNull
        @JsonProperty("statusId")
        Short statusId,
        
        @JsonProperty("bloodPressure")
        String bloodPressure,
        
        @JsonProperty("allergyDetails")
        String allergyDetails,
        
        @JsonProperty("habitDetails")
        String habitDetails,
        
        @JsonProperty("comment")
        String comment,
        
        @JsonProperty("paymentById")
        Short paymentById,
        
        @JsonProperty("paymentRemark")
        String paymentRemark,
        
        @JsonProperty("discount")
        BigDecimal discount,
        
        @JsonProperty("reason")
        String reason
) {
}

