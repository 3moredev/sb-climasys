package com.climasys.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class VisitMedicineOverwriteId implements Serializable {
    private LocalDateTime visitDate;
    private Integer patientVisitNo;
    private Short shiftId;
    private String clinicId;
    private String doctorId;
    private String patientId;
    private String shortDescription;

    public VisitMedicineOverwriteId() {}

    public VisitMedicineOverwriteId(LocalDateTime visitDate, Integer patientVisitNo, Short shiftId,
                                   String clinicId, String doctorId, String patientId, String shortDescription) {
        this.visitDate = visitDate;
        this.patientVisitNo = patientVisitNo;
        this.shiftId = shiftId;
        this.clinicId = clinicId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.shortDescription = shortDescription;
    }

    public LocalDateTime getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDateTime visitDate) { this.visitDate = visitDate; }

    public Integer getPatientVisitNo() { return patientVisitNo; }
    public void setPatientVisitNo(Integer patientVisitNo) { this.patientVisitNo = patientVisitNo; }

    public Short getShiftId() { return shiftId; }
    public void setShiftId(Short shiftId) { this.shiftId = shiftId; }

    public String getClinicId() { return clinicId; }
    public void setClinicId(String clinicId) { this.clinicId = clinicId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitMedicineOverwriteId that = (VisitMedicineOverwriteId) o;
        return Objects.equals(visitDate, that.visitDate) &&
               Objects.equals(patientVisitNo, that.patientVisitNo) &&
               Objects.equals(shiftId, that.shiftId) &&
               Objects.equals(clinicId, that.clinicId) &&
               Objects.equals(doctorId, that.doctorId) &&
               Objects.equals(patientId, that.patientId) &&
               Objects.equals(shortDescription, that.shortDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitDate, patientVisitNo, shiftId, clinicId, doctorId, patientId, shortDescription);
    }
}

