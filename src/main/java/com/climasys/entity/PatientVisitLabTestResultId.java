package com.climasys.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Composite Primary Key for PatientVisitLabTestResult entity
 * Represents the composite key structure of the patient_visit_labtestresults table
 */
public class PatientVisitLabTestResultId implements Serializable {

    private LocalDateTime visitDate;
    private Integer patientVisitNo;
    private Short shiftId;
    private String clinicId;
    private String doctorId;
    private String patientId;
    private String labTestDescription;
    private String parameterName;

    // Default constructor
    public PatientVisitLabTestResultId() {
    }

    // Constructor with all fields
    public PatientVisitLabTestResultId(LocalDateTime visitDate, Integer patientVisitNo, Short shiftId,
                                     String clinicId, String doctorId, String patientId,
                                     String labTestDescription, String parameterName) {
        this.visitDate = visitDate;
        this.patientVisitNo = patientVisitNo;
        this.shiftId = shiftId;
        this.clinicId = clinicId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.labTestDescription = labTestDescription;
        this.parameterName = parameterName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientVisitLabTestResultId that = (PatientVisitLabTestResultId) o;
        return Objects.equals(visitDate, that.visitDate) &&
                Objects.equals(patientVisitNo, that.patientVisitNo) &&
                Objects.equals(shiftId, that.shiftId) &&
                Objects.equals(clinicId, that.clinicId) &&
                Objects.equals(doctorId, that.doctorId) &&
                Objects.equals(patientId, that.patientId) &&
                Objects.equals(labTestDescription, that.labTestDescription) &&
                Objects.equals(parameterName, that.parameterName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitDate, patientVisitNo, shiftId, clinicId, doctorId, patientId, labTestDescription, parameterName);
    }

    @Override
    public String toString() {
        return "PatientVisitLabTestResultId{" +
                "visitDate=" + visitDate +
                ", patientVisitNo=" + patientVisitNo +
                ", shiftId=" + shiftId +
                ", clinicId='" + clinicId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", labTestDescription='" + labTestDescription + '\'' +
                ", parameterName='" + parameterName + '\'' +
                '}';
    }
}
