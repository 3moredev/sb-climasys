package com.climasys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity for Patient_Visit_LabTestResults table
 * Stores lab test results for patient visits
 * Equivalent to the data structure used in USP_Insert_LabTestAllData stored procedure
 */
@Entity
@Table(name = "patient_visit_labtestresults", schema = "public")
@IdClass(PatientVisitLabTestResultId.class)
public class PatientVisitLabTestResult {

    @Id
    @Column(name = "visit_date")
    private LocalDateTime visitDate;

    @Id
    @Column(name = "patient_visit_no")
    private Integer patientVisitNo;

    @Id
    @Column(name = "shift_id")
    private Short shiftId;

    @Id
    @Column(name = "clinic_id", length = 10)
    private String clinicId;

    @Id
    @Column(name = "doctor_id", length = 30)
    private String doctorId;

    @Id
    @Column(name = "patient_id", length = 32)
    private String patientId;

    @Id
    @Column(name = "lab_test_description", length = 80)
    private String labTestDescription;

    @Id
    @Column(name = "parameter_name", length = 100)
    private String parameterName;

    @Column(name = "test_parameter_value", length = 2000)
    private String testParameterValue;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "createdby_name", length = 90)
    private String createdbyName;

    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;

    @Column(name = "modifiedby_name", length = 90)
    private String modifiedbyName;

    @Column(name = "delete_flag")
    private Boolean deleteFlag;

    @Column(name = "doctor_name", length = 200)
    private String doctorName;

    @Column(name = "lab_name", length = 200)
    private String labName;

    @Column(name = "report_date", length = 200)
    private String reportDate;

    @Column(name = "comment", length = 1000)
    private String comment;

    // Constructors
    public PatientVisitLabTestResult() {
    }

    public PatientVisitLabTestResult(LocalDateTime visitDate, Integer patientVisitNo, Short shiftId,
                                   String clinicId, String doctorId, String patientId,
                                   String labTestDescription, String parameterName, String testParameterValue) {
        this.visitDate = visitDate;
        this.patientVisitNo = patientVisitNo;
        this.shiftId = shiftId;
        this.clinicId = clinicId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.labTestDescription = labTestDescription;
        this.parameterName = parameterName;
        this.testParameterValue = testParameterValue;
        this.deleteFlag = false;
        this.createdOn = LocalDateTime.now();
        this.modifiedOn = LocalDateTime.now();
    }

    // Getters and Setters
    public LocalDateTime getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }

    public Integer getPatientVisitNo() {
        return patientVisitNo;
    }

    public void setPatientVisitNo(Integer patientVisitNo) {
        this.patientVisitNo = patientVisitNo;
    }

    public Short getShiftId() {
        return shiftId;
    }

    public void setShiftId(Short shiftId) {
        this.shiftId = shiftId;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getLabTestDescription() {
        return labTestDescription;
    }

    public void setLabTestDescription(String labTestDescription) {
        this.labTestDescription = labTestDescription;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getTestParameterValue() {
        return testParameterValue;
    }

    public void setTestParameterValue(String testParameterValue) {
        this.testParameterValue = testParameterValue;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedbyName() {
        return createdbyName;
    }

    public void setCreatedbyName(String createdbyName) {
        this.createdbyName = createdbyName;
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(LocalDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getModifiedbyName() {
        return modifiedbyName;
    }

    public void setModifiedbyName(String modifiedbyName) {
        this.modifiedbyName = modifiedbyName;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "PatientVisitLabTestResult{" +
                "visitDate=" + visitDate +
                ", patientVisitNo=" + patientVisitNo +
                ", shiftId=" + shiftId +
                ", clinicId='" + clinicId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", labTestDescription='" + labTestDescription + '\'' +
                ", parameterName='" + parameterName + '\'' +
                ", testParameterValue='" + testParameterValue + '\'' +
                ", deleteFlag=" + deleteFlag +
                '}';
    }
}
