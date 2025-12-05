package com.climasys.billing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for OPD Daily Collection data
 * Maps the result set from USP_Get_OPDDailyCollection_For_Operator stored procedure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OPDDailyCollectionDTO {
    
    @JsonProperty("visitDate")
    private String visitDate;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("patientId")
    private String patientId;
    
    @JsonProperty("statusDescription")
    private String statusDescription;
    
    @JsonProperty("statusId")
    private Short statusId;
    
    @JsonProperty("feesToCollect")
    private BigDecimal feesToCollect;
    
    @JsonProperty("feesCollected")
    private BigDecimal feesCollected;
    
    @JsonProperty("adhocFees")
    private BigDecimal adhocFees;
    
    @JsonProperty("originalBilledAmount")
    private BigDecimal originalBilledAmount;
    
    @JsonProperty("folderNo")
    private String folderNo;
    
    @JsonProperty("comment")
    private String comment;
    
    @JsonProperty("difference")
    private BigDecimal difference;
    
    @JsonProperty("dues")
    private BigDecimal dues;
    
    @JsonProperty("originalDiscount")
    private BigDecimal originalDiscount;
    
    @JsonProperty("discount")
    private BigDecimal discount;
    
    @JsonProperty("net")
    private BigDecimal net;
    
    @JsonProperty("inPerson")
    private Boolean inPerson;
    
    @JsonProperty("attendedBy")
    private String attendedBy;
    
    @JsonProperty("paymentById")
    private Short paymentById;
    
    @JsonProperty("paymentRemark")
    private String paymentRemark;
    
    @JsonProperty("paymentDescription")
    private String paymentDescription;
    
    @JsonProperty("partialName")
    private String partialName;
    
    @JsonProperty("ageYearsIntRound")
    private Integer ageYearsIntRound;
    
    @JsonProperty("genderDescription")
    private String genderDescription;
    
    @JsonProperty("patientVisitNo")
    private Integer patientVisitNo;
    
    @JsonProperty("doctorId")
    private String doctorId;
    
    @JsonProperty("doctorName")
    private String doctorName;
    
    @JsonProperty("isFollowUp")
    private String isFollowUp;
    
    @JsonProperty("baseLocation")
    private String baseLocation;
}

