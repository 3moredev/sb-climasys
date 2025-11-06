package com.climasys.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class PatientVisitBillingInfoOverwriteId implements Serializable {
    private LocalDateTime visitDate;
    private String doctorId;
    private String clinicId;
    private Short shiftId;
    private String patientId;
    private Integer patientVisitNo;
    private String billingGroupName;
    private String billingSubgroupName;
    private String billingDetails;

    public PatientVisitBillingInfoOverwriteId() {}

    public PatientVisitBillingInfoOverwriteId(LocalDateTime visitDate, String doctorId, String clinicId,
                                              Short shiftId, String patientId, Integer patientVisitNo,
                                              String billingGroupName, String billingSubgroupName, String billingDetails) {
        this.visitDate = visitDate;
        this.doctorId = doctorId;
        this.clinicId = clinicId;
        this.shiftId = shiftId;
        this.patientId = patientId;
        this.patientVisitNo = patientVisitNo;
        this.billingGroupName = billingGroupName;
        this.billingSubgroupName = billingSubgroupName;
        this.billingDetails = billingDetails;
    }

    public LocalDateTime getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDateTime visitDate) { this.visitDate = visitDate; }

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

    public String getBillingGroupName() { return billingGroupName; }
    public void setBillingGroupName(String billingGroupName) { this.billingGroupName = billingGroupName; }

    public String getBillingSubgroupName() { return billingSubgroupName; }
    public void setBillingSubgroupName(String billingSubgroupName) { this.billingSubgroupName = billingSubgroupName; }

    public String getBillingDetails() { return billingDetails; }
    public void setBillingDetails(String billingDetails) { this.billingDetails = billingDetails; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientVisitBillingInfoOverwriteId that = (PatientVisitBillingInfoOverwriteId) o;
        return Objects.equals(visitDate, that.visitDate) &&
               Objects.equals(doctorId, that.doctorId) &&
               Objects.equals(clinicId, that.clinicId) &&
               Objects.equals(shiftId, that.shiftId) &&
               Objects.equals(patientId, that.patientId) &&
               Objects.equals(patientVisitNo, that.patientVisitNo) &&
               Objects.equals(billingGroupName, that.billingGroupName) &&
               Objects.equals(billingSubgroupName, that.billingSubgroupName) &&
               Objects.equals(billingDetails, that.billingDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitDate, doctorId, clinicId, shiftId, patientId, patientVisitNo,
                           billingGroupName, billingSubgroupName, billingDetails);
    }
}

