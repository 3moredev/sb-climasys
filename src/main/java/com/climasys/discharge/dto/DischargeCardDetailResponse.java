package com.climasys.discharge.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Response DTO for Get Discharge Card Details
 * Replaces USP_Get_Patient_DischargeCard_Data stored procedure result sets
 */
public class DischargeCardDetailResponse {
    
    // Table[0]: Main discharge card data
    private DischargeCardMainData mainData;
    
    // Table[1]: Discharge investigations (attachments)
    private List<DischargeInvestigationDTO> investigations;
    
    // Table[2]: Discharge invoice details
    private List<DischargeInvoiceDetailDTO> invoiceDetails;
    
    // Table[3]: Discharge invoice header
    private DischargeInvoiceHeaderDTO invoiceHeader;
    
    // Table[4]: Discharge bill details
    private List<DischargeBillDetailDTO> billDetails;
    
    // Table[5]: Discharge bill header
    private DischargeBillHeaderDTO billHeader;
    
    // Table[6]: Labour card data
    private LabourCardDTO labourCard;
    
    // Table[7]: Total advance amount
    private BigDecimal totalAdvance;
    
    // Table[8]: Last advance date
    private LocalDate lastAdvanceDate;
    
    // Getters and Setters
    public DischargeCardMainData getMainData() {
        return mainData;
    }
    
    public void setMainData(DischargeCardMainData mainData) {
        this.mainData = mainData;
    }
    
    public List<DischargeInvestigationDTO> getInvestigations() {
        return investigations;
    }
    
    public void setInvestigations(List<DischargeInvestigationDTO> investigations) {
        this.investigations = investigations;
    }
    
    public List<DischargeInvoiceDetailDTO> getInvoiceDetails() {
        return invoiceDetails;
    }
    
    public void setInvoiceDetails(List<DischargeInvoiceDetailDTO> invoiceDetails) {
        this.invoiceDetails = invoiceDetails;
    }
    
    public DischargeInvoiceHeaderDTO getInvoiceHeader() {
        return invoiceHeader;
    }
    
    public void setInvoiceHeader(DischargeInvoiceHeaderDTO invoiceHeader) {
        this.invoiceHeader = invoiceHeader;
    }
    
    public List<DischargeBillDetailDTO> getBillDetails() {
        return billDetails;
    }
    
    public void setBillDetails(List<DischargeBillDetailDTO> billDetails) {
        this.billDetails = billDetails;
    }
    
    public DischargeBillHeaderDTO getBillHeader() {
        return billHeader;
    }
    
    public void setBillHeader(DischargeBillHeaderDTO billHeader) {
        this.billHeader = billHeader;
    }
    
    public LabourCardDTO getLabourCard() {
        return labourCard;
    }
    
    public void setLabourCard(LabourCardDTO labourCard) {
        this.labourCard = labourCard;
    }
    
    public BigDecimal getTotalAdvance() {
        return totalAdvance;
    }
    
    public void setTotalAdvance(BigDecimal totalAdvance) {
        this.totalAdvance = totalAdvance;
    }
    
    public LocalDate getLastAdvanceDate() {
        return lastAdvanceDate;
    }
    
    public void setLastAdvanceDate(LocalDate lastAdvanceDate) {
        this.lastAdvanceDate = lastAdvanceDate;
    }
    
    // Inner DTOs
    public static class DischargeCardMainData {
        // Patient Information
        private String patientName;
        private String patientId;
        private String gender;
        private Integer age;
        private String address;
        private String contactNo;
        
        // Discharge Card Information
        private String ipdRefNo;
        private LocalDate admissionDate;
        private LocalTime admissionTime;
        private String treatingDoctor;
        private String consultingDoctor;
        private LocalDate dischargeDate;
        private LocalTime dischargeTime;
        private BigDecimal weight;
        private String ipdNo;
        private String diagnosis;
        private String complaints;
        private String history;
        private String investigations;
        private String oe;
        private String se;
        private String procedure;
        private String treatment;
        private String discharge;
        private String instructions;
        private String keyword;
        private LocalDate operationStartDate;
        private LocalDate operationEndDate;
        private LocalTime operationStartTime;
        private LocalTime operationEndTime;
        private String operativeNotes;
        private String remark;
        private String followUpComments;
        private String anesthesia;
        private String doctorId;
        private String reasonForDischarge;
        private String emergencyNumber;
        private String company;
        private String referredDoctor;
        private String conditionDischarge;
        private String footer;
        private String printedOnDate;
        private String printedOnDateOp;
        private String room;
        private String bedNo;
        private String admittedDays;
        private String otHours;
        private String department;
        private LocalDate followUpDate;
        
        // Getters and Setters - Patient Information
        public String getPatientName() { return patientName; }
        public void setPatientName(String patientName) { this.patientName = patientName; }
        
        public String getPatientId() { return patientId; }
        public void setPatientId(String patientId) { this.patientId = patientId; }
        
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getContactNo() { return contactNo; }
        public void setContactNo(String contactNo) { this.contactNo = contactNo; }
        
        // Getters and Setters - Discharge Card Information
        public String getIpdRefNo() { return ipdRefNo; }
        public void setIpdRefNo(String ipdRefNo) { this.ipdRefNo = ipdRefNo; }
        
        public LocalDate getAdmissionDate() { return admissionDate; }
        public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }
        
        public LocalTime getAdmissionTime() { return admissionTime; }
        public void setAdmissionTime(LocalTime admissionTime) { this.admissionTime = admissionTime; }
        
        public String getTreatingDoctor() { return treatingDoctor; }
        public void setTreatingDoctor(String treatingDoctor) { this.treatingDoctor = treatingDoctor; }
        
        public String getConsultingDoctor() { return consultingDoctor; }
        public void setConsultingDoctor(String consultingDoctor) { this.consultingDoctor = consultingDoctor; }
        
        public LocalDate getDischargeDate() { return dischargeDate; }
        public void setDischargeDate(LocalDate dischargeDate) { this.dischargeDate = dischargeDate; }
        
        public LocalTime getDischargeTime() { return dischargeTime; }
        public void setDischargeTime(LocalTime dischargeTime) { this.dischargeTime = dischargeTime; }
        
        public BigDecimal getWeight() { return weight; }
        public void setWeight(BigDecimal weight) { this.weight = weight; }
        
        public String getIpdNo() { return ipdNo; }
        public void setIpdNo(String ipdNo) { this.ipdNo = ipdNo; }
        
        public String getDiagnosis() { return diagnosis; }
        public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
        
        public String getComplaints() { return complaints; }
        public void setComplaints(String complaints) { this.complaints = complaints; }
        
        public String getHistory() { return history; }
        public void setHistory(String history) { this.history = history; }
        
        public String getInvestigations() { return investigations; }
        public void setInvestigations(String investigations) { this.investigations = investigations; }
        
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
        
        public String getInstructions() { return instructions; }
        public void setInstructions(String instructions) { this.instructions = instructions; }
        
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        
        public LocalDate getOperationStartDate() { return operationStartDate; }
        public void setOperationStartDate(LocalDate operationStartDate) { this.operationStartDate = operationStartDate; }
        
        public LocalDate getOperationEndDate() { return operationEndDate; }
        public void setOperationEndDate(LocalDate operationEndDate) { this.operationEndDate = operationEndDate; }
        
        public LocalTime getOperationStartTime() { return operationStartTime; }
        public void setOperationStartTime(LocalTime operationStartTime) { this.operationStartTime = operationStartTime; }
        
        public LocalTime getOperationEndTime() { return operationEndTime; }
        public void setOperationEndTime(LocalTime operationEndTime) { this.operationEndTime = operationEndTime; }
        
        public String getOperativeNotes() { return operativeNotes; }
        public void setOperativeNotes(String operativeNotes) { this.operativeNotes = operativeNotes; }
        
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        
        public String getFollowUpComments() { return followUpComments; }
        public void setFollowUpComments(String followUpComments) { this.followUpComments = followUpComments; }
        
        public String getAnesthesia() { return anesthesia; }
        public void setAnesthesia(String anesthesia) { this.anesthesia = anesthesia; }
        
        public String getDoctorId() { return doctorId; }
        public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
        
        public String getReasonForDischarge() { return reasonForDischarge; }
        public void setReasonForDischarge(String reasonForDischarge) { this.reasonForDischarge = reasonForDischarge; }
        
        public String getEmergencyNumber() { return emergencyNumber; }
        public void setEmergencyNumber(String emergencyNumber) { this.emergencyNumber = emergencyNumber; }
        
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        
        public String getReferredDoctor() { return referredDoctor; }
        public void setReferredDoctor(String referredDoctor) { this.referredDoctor = referredDoctor; }
        
        public String getConditionDischarge() { return conditionDischarge; }
        public void setConditionDischarge(String conditionDischarge) { this.conditionDischarge = conditionDischarge; }
        
        public String getFooter() { return footer; }
        public void setFooter(String footer) { this.footer = footer; }
        
        public String getPrintedOnDate() { return printedOnDate; }
        public void setPrintedOnDate(String printedOnDate) { this.printedOnDate = printedOnDate; }
        
        public String getPrintedOnDateOp() { return printedOnDateOp; }
        public void setPrintedOnDateOp(String printedOnDateOp) { this.printedOnDateOp = printedOnDateOp; }
        
        public String getRoom() { return room; }
        public void setRoom(String room) { this.room = room; }
        
        public String getBedNo() { return bedNo; }
        public void setBedNo(String bedNo) { this.bedNo = bedNo; }
        
        public String getAdmittedDays() { return admittedDays; }
        public void setAdmittedDays(String admittedDays) { this.admittedDays = admittedDays; }
        
        public String getOtHours() { return otHours; }
        public void setOtHours(String otHours) { this.otHours = otHours; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public LocalDate getFollowUpDate() { return followUpDate; }
        public void setFollowUpDate(LocalDate followUpDate) { this.followUpDate = followUpDate; }
    }
    
    public static class DischargeInvestigationDTO {
        private String ipdRefNo;
        private String attachmentPath;
        private Integer id;
        
        // Getters and Setters
        public String getIpdRefNo() { return ipdRefNo; }
        public void setIpdRefNo(String ipdRefNo) { this.ipdRefNo = ipdRefNo; }
        
        public String getAttachmentPath() { return attachmentPath; }
        public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }
        
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
    }
    
    public static class DischargeInvoiceDetailDTO {
        private String ipdRefNo;
        private String invoiceNo;
        private String description;
        private BigDecimal unitPrice;
        private BigDecimal quantity;
        private String id;
        private String doctorId;
        
        // Getters and Setters
        public String getIpdRefNo() { return ipdRefNo; }
        public void setIpdRefNo(String ipdRefNo) { this.ipdRefNo = ipdRefNo; }
        
        public String getInvoiceNo() { return invoiceNo; }
        public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        
        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getDoctorId() { return doctorId; }
        public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    }
    
    public static class DischargeInvoiceHeaderDTO {
        private String ipdRefNo;
        private String invoiceNo;
        private LocalDate invoiceDate;
        private BigDecimal totalAmount;
        private BigDecimal collectedAmount;
        private BigDecimal discount;
        private BigDecimal balance;
        private BigDecimal netAmount;
        private String comments;
        private String doctorId;
        
        // Getters and Setters
        public String getIpdRefNo() { return ipdRefNo; }
        public void setIpdRefNo(String ipdRefNo) { this.ipdRefNo = ipdRefNo; }
        
        public String getInvoiceNo() { return invoiceNo; }
        public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
        
        public LocalDate getInvoiceDate() { return invoiceDate; }
        public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }
        
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        
        public BigDecimal getCollectedAmount() { return collectedAmount; }
        public void setCollectedAmount(BigDecimal collectedAmount) { this.collectedAmount = collectedAmount; }
        
        public BigDecimal getDiscount() { return discount; }
        public void setDiscount(BigDecimal discount) { this.discount = discount; }
        
        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }
        
        public BigDecimal getNetAmount() { return netAmount; }
        public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
        
        public String getComments() { return comments; }
        public void setComments(String comments) { this.comments = comments; }
        
        public String getDoctorId() { return doctorId; }
        public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    }
    
    public static class DischargeBillDetailDTO {
        private String idCharges;
        private LocalDate hospitalBillDate;
        private LocalDate hdnlblHstplAddDate;
        private String ipdRefNo;
        private String billNo;
        private String chargesCategory;
        private String chargesSubCategory;
        private String comments;
        private BigDecimal amount;
        private String id;
        private String doctorId;
        private BigDecimal totalAmount;
        private BigDecimal units;
        private String calculationType;
        
        // Getters and Setters
        public String getIdCharges() { return idCharges; }
        public void setIdCharges(String idCharges) { this.idCharges = idCharges; }
        
        public LocalDate getHospitalBillDate() { return hospitalBillDate; }
        public void setHospitalBillDate(LocalDate hospitalBillDate) { this.hospitalBillDate = hospitalBillDate; }
        
        public LocalDate getHdnlblHstplAddDate() { return hdnlblHstplAddDate; }
        public void setHdnlblHstplAddDate(LocalDate hdnlblHstplAddDate) { this.hdnlblHstplAddDate = hdnlblHstplAddDate; }
        
        public String getIpdRefNo() { return ipdRefNo; }
        public void setIpdRefNo(String ipdRefNo) { this.ipdRefNo = ipdRefNo; }
        
        public String getBillNo() { return billNo; }
        public void setBillNo(String billNo) { this.billNo = billNo; }
        
        public String getChargesCategory() { return chargesCategory; }
        public void setChargesCategory(String chargesCategory) { this.chargesCategory = chargesCategory; }
        
        public String getChargesSubCategory() { return chargesSubCategory; }
        public void setChargesSubCategory(String chargesSubCategory) { this.chargesSubCategory = chargesSubCategory; }
        
        public String getComments() { return comments; }
        public void setComments(String comments) { this.comments = comments; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getDoctorId() { return doctorId; }
        public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
        
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        
        public BigDecimal getUnits() { return units; }
        public void setUnits(BigDecimal units) { this.units = units; }
        
        public String getCalculationType() { return calculationType; }
        public void setCalculationType(String calculationType) { this.calculationType = calculationType; }
    }
    
    public static class DischargeBillHeaderDTO {
        private String ipdRefNo;
        private String billNo;
        private LocalDate billDate;
        private BigDecimal adjustAdvance;
        private BigDecimal totalAmount;
        private BigDecimal collectedAmount;
        private BigDecimal discount;
        private BigDecimal balance;
        private BigDecimal netAmount;
        private String comments;
        private String ipdNo;
        private String treatingDoctor;
        private String consultingDoctor;
        private String doctorId;
        private String insuranceDetails;
        private BigDecimal tds;
        
        // Getters and Setters
        public String getIpdRefNo() { return ipdRefNo; }
        public void setIpdRefNo(String ipdRefNo) { this.ipdRefNo = ipdRefNo; }
        
        public String getBillNo() { return billNo; }
        public void setBillNo(String billNo) { this.billNo = billNo; }
        
        public LocalDate getBillDate() { return billDate; }
        public void setBillDate(LocalDate billDate) { this.billDate = billDate; }
        
        public BigDecimal getAdjustAdvance() { return adjustAdvance; }
        public void setAdjustAdvance(BigDecimal adjustAdvance) { this.adjustAdvance = adjustAdvance; }
        
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        
        public BigDecimal getCollectedAmount() { return collectedAmount; }
        public void setCollectedAmount(BigDecimal collectedAmount) { this.collectedAmount = collectedAmount; }
        
        public BigDecimal getDiscount() { return discount; }
        public void setDiscount(BigDecimal discount) { this.discount = discount; }
        
        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }
        
        public BigDecimal getNetAmount() { return netAmount; }
        public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
        
        public String getComments() { return comments; }
        public void setComments(String comments) { this.comments = comments; }
        
        public String getIpdNo() { return ipdNo; }
        public void setIpdNo(String ipdNo) { this.ipdNo = ipdNo; }
        
        public String getTreatingDoctor() { return treatingDoctor; }
        public void setTreatingDoctor(String treatingDoctor) { this.treatingDoctor = treatingDoctor; }
        
        public String getConsultingDoctor() { return consultingDoctor; }
        public void setConsultingDoctor(String consultingDoctor) { this.consultingDoctor = consultingDoctor; }
        
        public String getDoctorId() { return doctorId; }
        public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
        
        public String getInsuranceDetails() { return insuranceDetails; }
        public void setInsuranceDetails(String insuranceDetails) { this.insuranceDetails = insuranceDetails; }
        
        public BigDecimal getTds() { return tds; }
        public void setTds(BigDecimal tds) { this.tds = tds; }
    }
    
    public static class LabourCardDTO {
        private String obstetricHistory;
        private LocalDate dateOfDelivery;
        private LocalTime timeOfDelivery;
        private String labourName;
        private String operativeInterference;
        private String indication;
        private String puerperium;
        private String childSex;
        private BigDecimal wtAtBirth;
        private BigDecimal wtAtDischarge;
        private String clinicId;
        private String doctorId;
        private Integer shiftId;
        private String patientId;
        private String ipdRefId;
        private LocalDate dateOfDelivery1;
        private LocalTime timeOfDelivery1;
        private String childSex1;
        private BigDecimal wtAtBirth1;
        private BigDecimal wtAtDischarge1;
        private LocalDate dateOfDelivery2;
        private LocalTime timeOfDelivery2;
        private String childSex2;
        private BigDecimal wtAtBirth2;
        private BigDecimal wtAtDischarge2;
        private String remark;
        
        // Getters and Setters (abbreviated for brevity - would include all fields)
        public String getObstetricHistory() { return obstetricHistory; }
        public void setObstetricHistory(String obstetricHistory) { this.obstetricHistory = obstetricHistory; }
        
        public LocalDate getDateOfDelivery() { return dateOfDelivery; }
        public void setDateOfDelivery(LocalDate dateOfDelivery) { this.dateOfDelivery = dateOfDelivery; }
        
        public LocalTime getTimeOfDelivery() { return timeOfDelivery; }
        public void setTimeOfDelivery(LocalTime timeOfDelivery) { this.timeOfDelivery = timeOfDelivery; }
        
        public String getLabourName() { return labourName; }
        public void setLabourName(String labourName) { this.labourName = labourName; }
        
        public String getOperativeInterference() { return operativeInterference; }
        public void setOperativeInterference(String operativeInterference) { this.operativeInterference = operativeInterference; }
        
        public String getIndication() { return indication; }
        public void setIndication(String indication) { this.indication = indication; }
        
        public String getPuerperium() { return puerperium; }
        public void setPuerperium(String puerperium) { this.puerperium = puerperium; }
        
        public String getChildSex() { return childSex; }
        public void setChildSex(String childSex) { this.childSex = childSex; }
        
        public BigDecimal getWtAtBirth() { return wtAtBirth; }
        public void setWtAtBirth(BigDecimal wtAtBirth) { this.wtAtBirth = wtAtBirth; }
        
        public BigDecimal getWtAtDischarge() { return wtAtDischarge; }
        public void setWtAtDischarge(BigDecimal wtAtDischarge) { this.wtAtDischarge = wtAtDischarge; }
        
        public String getClinicId() { return clinicId; }
        public void setClinicId(String clinicId) { this.clinicId = clinicId; }
        
        public String getDoctorId() { return doctorId; }
        public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
        
        public Integer getShiftId() { return shiftId; }
        public void setShiftId(Integer shiftId) { this.shiftId = shiftId; }
        
        public String getPatientId() { return patientId; }
        public void setPatientId(String patientId) { this.patientId = patientId; }
        
        public String getIpdRefId() { return ipdRefId; }
        public void setIpdRefId(String ipdRefId) { this.ipdRefId = ipdRefId; }
        
        public LocalDate getDateOfDelivery1() { return dateOfDelivery1; }
        public void setDateOfDelivery1(LocalDate dateOfDelivery1) { this.dateOfDelivery1 = dateOfDelivery1; }
        
        public LocalTime getTimeOfDelivery1() { return timeOfDelivery1; }
        public void setTimeOfDelivery1(LocalTime timeOfDelivery1) { this.timeOfDelivery1 = timeOfDelivery1; }
        
        public String getChildSex1() { return childSex1; }
        public void setChildSex1(String childSex1) { this.childSex1 = childSex1; }
        
        public BigDecimal getWtAtBirth1() { return wtAtBirth1; }
        public void setWtAtBirth1(BigDecimal wtAtBirth1) { this.wtAtBirth1 = wtAtBirth1; }
        
        public BigDecimal getWtAtDischarge1() { return wtAtDischarge1; }
        public void setWtAtDischarge1(BigDecimal wtAtDischarge1) { this.wtAtDischarge1 = wtAtDischarge1; }
        
        public LocalDate getDateOfDelivery2() { return dateOfDelivery2; }
        public void setDateOfDelivery2(LocalDate dateOfDelivery2) { this.dateOfDelivery2 = dateOfDelivery2; }
        
        public LocalTime getTimeOfDelivery2() { return timeOfDelivery2; }
        public void setTimeOfDelivery2(LocalTime timeOfDelivery2) { this.timeOfDelivery2 = timeOfDelivery2; }
        
        public String getChildSex2() { return childSex2; }
        public void setChildSex2(String childSex2) { this.childSex2 = childSex2; }
        
        public BigDecimal getWtAtBirth2() { return wtAtBirth2; }
        public void setWtAtBirth2(BigDecimal wtAtBirth2) { this.wtAtBirth2 = wtAtBirth2; }
        
        public BigDecimal getWtAtDischarge2() { return wtAtDischarge2; }
        public void setWtAtDischarge2(BigDecimal wtAtDischarge2) { this.wtAtDischarge2 = wtAtDischarge2; }
        
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }
}

