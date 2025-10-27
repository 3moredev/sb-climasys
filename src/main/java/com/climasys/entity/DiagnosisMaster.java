package com.climasys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing the Diagnosis_Master table
 * Maps to the diagnosis master functionality
 * 
 * Note: This entity has a composite primary key (short_description, doctor_id, clinic_id)
 * to support multi-clinic functionality
 */
@Entity
@Table(name = "diagnosis_master")
@IdClass(DiagnosisMasterId.class)
public class DiagnosisMaster {
    
    @Id
    @Column(name = "short_description", length = 40, nullable = false)
    private String shortDescription;
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Id
    @Column(name = "clinic_id", length = 30, nullable = false)
    private String clinicId;
    
    @Column(name = "diagnosis_description", length = 1000)
    private String diagnosisDescription;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdByName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedByName;
    
    @Column(name = "priority_value")
    private Integer priorityValue;
    
    
    // Constructors
    public DiagnosisMaster() {}
    
    public DiagnosisMaster(String shortDescription, String doctorId, String clinicId, String diagnosisDescription) {
        this.shortDescription = shortDescription;
        this.doctorId = doctorId;
        this.clinicId = clinicId;
        this.diagnosisDescription = diagnosisDescription;
        this.createdOn = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getShortDescription() {
        return shortDescription;
    }
    
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    
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
    
    public String getDiagnosisDescription() {
        return diagnosisDescription;
    }
    
    public void setDiagnosisDescription(String diagnosisDescription) {
        this.diagnosisDescription = diagnosisDescription;
    }
    
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }
    
    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }
    
    public String getCreatedByName() {
        return createdByName;
    }
    
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
    
    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }
    
    public void setModifiedOn(LocalDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }
    
    public String getModifiedByName() {
        return modifiedByName;
    }
    
    public void setModifiedByName(String modifiedByName) {
        this.modifiedByName = modifiedByName;
    }
    
    public Integer getPriorityValue() {
        return priorityValue;
    }
    
    public void setPriorityValue(Integer priorityValue) {
        this.priorityValue = priorityValue;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        modifiedOn = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "DiagnosisMaster{" +
                "shortDescription='" + shortDescription + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", clinicId='" + clinicId + '\'' +
                ", diagnosisDescription='" + diagnosisDescription + '\'' +
                ", priorityValue=" + priorityValue +
                ", active=" + active +
                '}';
    }
}
