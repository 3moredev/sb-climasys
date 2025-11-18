package com.climasys.advance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for USP_Get_Patient_AdmissionCard_data
 * Returns 4 tables:
 * - Table[0]: Previous advance collection records (grid)
 * - Table[1]: Current advance collection details
 * - Table[2]: Admission data
 * - Table[3]: Total advance amount
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdmissionCardDataResponse {
    
    @JsonProperty("previousAdvanceRecords")
    private List<PreviousAdvanceRecord> previousAdvanceRecords;
    
    @JsonProperty("currentAdvanceDetails")
    private CurrentAdvanceDetails currentAdvanceDetails;
    
    @JsonProperty("admissionData")
    private AdmissionData admissionData;
    
    @JsonProperty("totalAdvance")
    private BigDecimal totalAdvance;
    
    /**
     * DTO for previous advance collection records (Table[0])
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreviousAdvanceRecord {
        @JsonProperty("ipdRefNo")
        private String ipdRefNo;
        
        @JsonProperty("admissionDate")
        private String admissionDate;
        
        @JsonProperty("reasonOfAdmission")
        private String reasonOfAdmission;
        
        @JsonProperty("receiptNumber")
        private String receiptNumber;
        
        @JsonProperty("date")
        private LocalDateTime date;
        
        @JsonProperty("isInsurance")
        private String isInsurance;
        
        @JsonProperty("dateOfAdvance")
        private String dateOfAdvance;
        
        @JsonProperty("doctorId")
        private String doctorId;
        
        @JsonProperty("amountReceived")
        private BigDecimal amountReceived;
        
        @JsonProperty("dischargeDate")
        private String dischargeDate;
        
        @JsonProperty("sumTotal")
        private BigDecimal sumTotal;
        
        @JsonProperty("validDischargeDate")
        private String validDischargeDate;
    }
    
    /**
     * DTO for current advance collection details (Table[1])
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentAdvanceDetails {
        @JsonProperty("amountReceived")
        private BigDecimal amountReceived;
        
        @JsonProperty("advanceDate")
        private LocalDateTime advanceDate;
        
        @JsonProperty("paymentById")
        private Short paymentById;
        
        @JsonProperty("paymentRemark")
        private String paymentRemark;
        
        @JsonProperty("receiptNumber")
        private String receiptNumber;
        
        @JsonProperty("receiptDate")
        private LocalDate receiptDate;
    }
    
    /**
     * DTO for admission data (Table[2])
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdmissionData {
        @JsonProperty("admissionDate")
        private LocalDate admissionDate;
        
        @JsonProperty("ipdFileNo")
        private String ipdFileNo;
        
        @JsonProperty("department")
        private String department;
        
        @JsonProperty("reasonOfAdmission")
        private String reasonOfAdmission;
        
        @JsonProperty("insuranceDetails")
        private String insuranceDetails;
        
        @JsonProperty("isInsurance")
        private String isInsurance;
        
        @JsonProperty("packageRemarks")
        private String packageRemarks;
        
        @JsonProperty("billNo")
        private String billNo;
        
        @JsonProperty("billDate")
        private LocalDate billDate;
        
        @JsonProperty("invoiceNo")
        private String invoiceNo;
        
        @JsonProperty("admissionTime")
        private LocalDateTime admissionTime;
        
        @JsonProperty("dischargeDate")
        private LocalDate dischargeDate;
        
        @JsonProperty("dischargeTime")
        private LocalDateTime dischargeTime;
        
        @JsonProperty("roomNo")
        private String roomNo;
        
        @JsonProperty("bedNo")
        private String bedNo;
    }
}

