package com.climasys.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class PatientVisitId implements Serializable {
    private String doctorId;
    private String clinicId;
    private Short shiftId;
    private String patientId;
    private Integer patientVisitNo;
    private LocalDateTime visitDate;

    public PatientVisitId() {}

    public PatientVisitId(String doctorId, String clinicId, Short shiftId, String patientId, Integer patientVisitNo, LocalDateTime visitDate) {
        this.doctorId = doctorId;
        this.clinicId = clinicId;
        this.shiftId = shiftId;
        this.patientId = patientId;
        this.patientVisitNo = patientVisitNo;
        this.visitDate = visitDate;
    }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getClinicId() { return clinicId; }
    public void setClinicId(String clinicId) { this.clinicId = clinicId; }

    public Short getShiftId() { return shiftId; }
    public void setShiftId(Short shiftId) { this.shiftId = shiftId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public Integer getPatientVisitNo() { return patientVisitNo; }
    public void setPatientVisitNo(Integer patientVisitNo) { this.patientVisitNo = patientVisitNo; }

    public LocalDateTime getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDateTime visitDate) { this.visitDate = visitDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientVisitId that = (PatientVisitId) o;
        return Objects.equals(doctorId, that.doctorId) &&
               Objects.equals(clinicId, that.clinicId) &&
               Objects.equals(shiftId, that.shiftId) &&
               Objects.equals(patientId, that.patientId) &&
               Objects.equals(patientVisitNo, that.patientVisitNo) &&
               Objects.equals(visitDate, that.visitDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId, clinicId, shiftId, patientId, patientVisitNo, visitDate);
    }
}
