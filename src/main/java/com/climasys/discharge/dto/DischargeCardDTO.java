package com.climasys.discharge.dto;

import java.math.BigDecimal;

/**
 * DTO for discharge card data
 * Matches the fields displayed on Manage Discharge Card screen:
 * Sr., Patient Name, IPD No, IPD File No, Admission Date, 
 * Discharge Date, keyword / Operation, Advance (Rs)
 */
public class DischargeCardDTO {
    
    private Integer serialNumber;
    private String patientName;
    private String ipdNo;
    private String ipdFileNo;
    private String admissionDate;
    private String dischargeDate;
    private String keyword;
    private BigDecimal advanceRs;
    private String patientId;
    private String ipdRefNo;
    
    public DischargeCardDTO() {}
    
    public Integer getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public String getIpdNo() {
        return ipdNo;
    }
    
    public void setIpdNo(String ipdNo) {
        this.ipdNo = ipdNo;
    }
    
    public String getIpdFileNo() {
        return ipdFileNo;
    }
    
    public void setIpdFileNo(String ipdFileNo) {
        this.ipdFileNo = ipdFileNo;
    }
    
    public String getAdmissionDate() {
        return admissionDate;
    }
    
    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }
    
    public String getDischargeDate() {
        return dischargeDate;
    }
    
    public void setDischargeDate(String dischargeDate) {
        this.dischargeDate = dischargeDate;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public BigDecimal getAdvanceRs() {
        return advanceRs;
    }
    
    public void setAdvanceRs(BigDecimal advanceRs) {
        this.advanceRs = advanceRs;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getIpdRefNo() {
        return ipdRefNo;
    }
    
    public void setIpdRefNo(String ipdRefNo) {
        this.ipdRefNo = ipdRefNo;
    }
}

