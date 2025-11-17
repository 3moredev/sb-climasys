package com.climasys.discharge.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Request DTO for updating discharge card
 * Replaces USP_Insert_DischargeData stored procedure parameters
 */
public class UpdateDischargeCardRequest {
    
    private String doctorId;
    private String clinicId;
    private Integer shiftId;
    private String patientId;
    private String ipdRefNo;
    private LocalDate admissionDate;
    private LocalTime admissionTime;
    private String treatingDoctor;
    private String consultingDoctor;
    private LocalDate dischargeDate;
    private LocalTime dischargeTime;
    private BigDecimal weight;
    private String ipdNo;
    private String userId;
    private List<DischargeDetailDTO> dischargeDetails;
    private List<String> keywordAttachments;
    private String keyword;
    private LocalDate visitDate;
    private LocalDate operationStartDate;
    private LocalDate operationEndDate;
    private LocalTime operationStartTime;
    private LocalTime operationEndTime;
    private String operativeNotes;
    private String remark;
    private String followUpComments;
    private String anesthesia;
    private String reasonForDischarge;
    private String referredDoctor;
    private String conditionOnDischarge;
    private String footer;
    private String defaultDate;
    private String ward;
    private String room;
    private String admittedDays;
    private String otHours;
    private String company;
    private LocalDate followUpDate;
    
    // Getters and Setters
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getClinicId() {
        return clinicId;
    }
    
    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }
    
    public Integer getShiftId() {
        return shiftId;
    }
    
    public void setShiftId(Integer shiftId) {
        this.shiftId = shiftId;
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
    
    public LocalDate getAdmissionDate() {
        return admissionDate;
    }
    
    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }
    
    public LocalTime getAdmissionTime() {
        return admissionTime;
    }
    
    public void setAdmissionTime(LocalTime admissionTime) {
        this.admissionTime = admissionTime;
    }
    
    public String getTreatingDoctor() {
        return treatingDoctor;
    }
    
    public void setTreatingDoctor(String treatingDoctor) {
        this.treatingDoctor = treatingDoctor;
    }
    
    public String getConsultingDoctor() {
        return consultingDoctor;
    }
    
    public void setConsultingDoctor(String consultingDoctor) {
        this.consultingDoctor = consultingDoctor;
    }
    
    public LocalDate getDischargeDate() {
        return dischargeDate;
    }
    
    public void setDischargeDate(LocalDate dischargeDate) {
        this.dischargeDate = dischargeDate;
    }
    
    public LocalTime getDischargeTime() {
        return dischargeTime;
    }
    
    public void setDischargeTime(LocalTime dischargeTime) {
        this.dischargeTime = dischargeTime;
    }
    
    public BigDecimal getWeight() {
        return weight;
    }
    
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    
    public String getIpdNo() {
        return ipdNo;
    }
    
    public void setIpdNo(String ipdNo) {
        this.ipdNo = ipdNo;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public List<DischargeDetailDTO> getDischargeDetails() {
        return dischargeDetails;
    }
    
    public void setDischargeDetails(List<DischargeDetailDTO> dischargeDetails) {
        this.dischargeDetails = dischargeDetails;
    }
    
    public List<String> getKeywordAttachments() {
        return keywordAttachments;
    }
    
    public void setKeywordAttachments(List<String> keywordAttachments) {
        this.keywordAttachments = keywordAttachments;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public LocalDate getVisitDate() {
        return visitDate;
    }
    
    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }
    
    public LocalDate getOperationStartDate() {
        return operationStartDate;
    }
    
    public void setOperationStartDate(LocalDate operationStartDate) {
        this.operationStartDate = operationStartDate;
    }
    
    public LocalDate getOperationEndDate() {
        return operationEndDate;
    }
    
    public void setOperationEndDate(LocalDate operationEndDate) {
        this.operationEndDate = operationEndDate;
    }
    
    public LocalTime getOperationStartTime() {
        return operationStartTime;
    }
    
    public void setOperationStartTime(LocalTime operationStartTime) {
        this.operationStartTime = operationStartTime;
    }
    
    public LocalTime getOperationEndTime() {
        return operationEndTime;
    }
    
    public void setOperationEndTime(LocalTime operationEndTime) {
        this.operationEndTime = operationEndTime;
    }
    
    public String getOperativeNotes() {
        return operativeNotes;
    }
    
    public void setOperativeNotes(String operativeNotes) {
        this.operativeNotes = operativeNotes;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public String getFollowUpComments() {
        return followUpComments;
    }
    
    public void setFollowUpComments(String followUpComments) {
        this.followUpComments = followUpComments;
    }
    
    public String getAnesthesia() {
        return anesthesia;
    }
    
    public void setAnesthesia(String anesthesia) {
        this.anesthesia = anesthesia;
    }
    
    public String getReasonForDischarge() {
        return reasonForDischarge;
    }
    
    public void setReasonForDischarge(String reasonForDischarge) {
        this.reasonForDischarge = reasonForDischarge;
    }
    
    public String getReferredDoctor() {
        return referredDoctor;
    }
    
    public void setReferredDoctor(String referredDoctor) {
        this.referredDoctor = referredDoctor;
    }
    
    public String getConditionOnDischarge() {
        return conditionOnDischarge;
    }
    
    public void setConditionOnDischarge(String conditionOnDischarge) {
        this.conditionOnDischarge = conditionOnDischarge;
    }
    
    public String getFooter() {
        return footer;
    }
    
    public void setFooter(String footer) {
        this.footer = footer;
    }
    
    public String getDefaultDate() {
        return defaultDate;
    }
    
    public void setDefaultDate(String defaultDate) {
        this.defaultDate = defaultDate;
    }
    
    public String getWard() {
        return ward;
    }
    
    public void setWard(String ward) {
        this.ward = ward;
    }
    
    public String getRoom() {
        return room;
    }
    
    public void setRoom(String room) {
        this.room = room;
    }
    
    public String getAdmittedDays() {
        return admittedDays;
    }
    
    public void setAdmittedDays(String admittedDays) {
        this.admittedDays = admittedDays;
    }
    
    public String getOtHours() {
        return otHours;
    }
    
    public void setOtHours(String otHours) {
        this.otHours = otHours;
    }
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public LocalDate getFollowUpDate() {
        return followUpDate;
    }
    
    public void setFollowUpDate(LocalDate followUpDate) {
        this.followUpDate = followUpDate;
    }
    
    /**
     * Inner DTO for discharge detail data (matches UDT_Insert_Discharge_Details_new)
     */
    public static class DischargeDetailDTO {
        private String doctorId;
        private String clinicId;
        private Integer shiftId;
        private String patientId;
        private String ipdRefNo;
        private String diagnosis;
        private String complaints;
        private String history;
        private String investigation;
        private String oe;
        private String se;
        private String procedure;
        private String treatment;
        private String discharge;
        private String instruction;
        
        // Getters and Setters
        public String getDoctorId() { return doctorId; }
        public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
        
        public String getClinicId() { return clinicId; }
        public void setClinicId(String clinicId) { this.clinicId = clinicId; }
        
        public Integer getShiftId() { return shiftId; }
        public void setShiftId(Integer shiftId) { this.shiftId = shiftId; }
        
        public String getPatientId() { return patientId; }
        public void setPatientId(String patientId) { this.patientId = patientId; }
        
        public String getIpdRefNo() { return ipdRefNo; }
        public void setIpdRefNo(String ipdRefNo) { this.ipdRefNo = ipdRefNo; }
        
        public String getDiagnosis() { return diagnosis; }
        public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
        
        public String getComplaints() { return complaints; }
        public void setComplaints(String complaints) { this.complaints = complaints; }
        
        public String getHistory() { return history; }
        public void setHistory(String history) { this.history = history; }
        
        public String getInvestigation() { return investigation; }
        public void setInvestigation(String investigation) { this.investigation = investigation; }
        
        public String getOe() { return oe; }
        public void setOe(String oe) { this.oe = oe; }
        
        public String getSe() { return se; }
        public void setSe(String se) { this.se = se; }
        
        public String getProcedure() { return procedure; }
        public void setProcedure(String procedure) { this.procedure = procedure; }
        
        public String getTreatment() { return treatment; }
        public void setTreatment(String treatment) { this.treatment = treatment; }
        
        public String getDischarge() { return discharge; }
        public void setDischarge(String discharge) { this.discharge = discharge; }
        
        public String getInstruction() { return instruction; }
        public void setInstruction(String instruction) { this.instruction = instruction; }
    }
}

