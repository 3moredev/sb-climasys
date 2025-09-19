package com.climasys.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class MedicineId implements Serializable {
    private String doctorId;
    private String clinicId;
    private Short shiftId;
    private String patientId;
    private Integer patientVisitNo;
    private LocalDateTime visitDate;
    private String brandName;
    private String medicineName;
    private String catsubDescription;
    private String catShortName;

    public MedicineId() {}

    public MedicineId(String doctorId, String clinicId, Short shiftId, String patientId, 
                     Integer patientVisitNo, LocalDateTime visitDate, String brandName, 
                     String medicineName, String catsubDescription, String catShortName) {
        this.doctorId = doctorId;
        this.clinicId = clinicId;
        this.shiftId = shiftId;
        this.patientId = patientId;
        this.patientVisitNo = patientVisitNo;
        this.visitDate = visitDate;
        this.brandName = brandName;
        this.medicineName = medicineName;
        this.catsubDescription = catsubDescription;
        this.catShortName = catShortName;
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

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getCatsubDescription() { return catsubDescription; }
    public void setCatsubDescription(String catsubDescription) { this.catsubDescription = catsubDescription; }

    public String getCatShortName() { return catShortName; }
    public void setCatShortName(String catShortName) { this.catShortName = catShortName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicineId that = (MedicineId) o;
        return Objects.equals(doctorId, that.doctorId) &&
               Objects.equals(clinicId, that.clinicId) &&
               Objects.equals(shiftId, that.shiftId) &&
               Objects.equals(patientId, that.patientId) &&
               Objects.equals(patientVisitNo, that.patientVisitNo) &&
               Objects.equals(visitDate, that.visitDate) &&
               Objects.equals(brandName, that.brandName) &&
               Objects.equals(medicineName, that.medicineName) &&
               Objects.equals(catsubDescription, that.catsubDescription) &&
               Objects.equals(catShortName, that.catShortName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId, clinicId, shiftId, patientId, patientVisitNo, 
                           visitDate, brandName, medicineName, catsubDescription, catShortName);
    }
}
