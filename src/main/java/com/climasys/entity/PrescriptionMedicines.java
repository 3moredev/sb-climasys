package com.climasys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing the prescription_medicines table
 * Maps to the prescription medicines master data (Prescription Details)
 * 
 * Note: This entity has a composite primary key (cat_short_name, catsub_description, medicine_name, brand_name, doctor_id)
 */
@Entity
@Table(name = "prescription_medicines")
@IdClass(PrescriptionMedicinesId.class)
public class PrescriptionMedicines {
    
    @Id
    @Column(name = "cat_short_name", length = 60, nullable = false)
    private String catShortName;
    
    @Id
    @Column(name = "catsub_description", length = 200, nullable = false)
    private String catsubDescription;
    
    @Id
    @Column(name = "medicine_name", length = 200, nullable = false)
    private String medicineName;
    
    @Id
    @Column(name = "brand_name", length = 200, nullable = false)
    private String brandName;
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;

    // Optional clinic ID, if schema has clinic_id column
    @Column(name = "clinic_id", length = 30)
    private String clinicId;

    @Column(name = "marketed_by", length = 200)
    private String marketedBy;
    
    @Column(name = "active")
    private Boolean active;
    
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
    
    @Column(name = "morning")
    private Double morning;
    
    @Column(name = "afternoon")
    private Double afternoon;
    
    @Column(name = "night")
    private Double night;
    
    @Column(name = "no_of_days")
    private Integer noOfDays;
    
    @Column(name = "instruction", length = 4000)
    private String instruction;
    
    @Column(name = "marketed_by_dept", length = 200)
    private String marketedByDept;
    
    // Constructors
    public PrescriptionMedicines() {}
    
    public PrescriptionMedicines(String catShortName, String catsubDescription, String medicineName, 
                                 String brandName, String doctorId) {
        this.catShortName = catShortName;
        this.catsubDescription = catsubDescription;
        this.medicineName = medicineName;
        this.brandName = brandName;
        this.doctorId = doctorId;
    }
    
    // Getters and Setters
    public String getCatShortName() {
        return catShortName;
    }
    
    public void setCatShortName(String catShortName) {
        this.catShortName = catShortName;
    }
    
    public String getCatsubDescription() {
        return catsubDescription;
    }
    
    public void setCatsubDescription(String catsubDescription) {
        this.catsubDescription = catsubDescription;
    }
    
    public String getMedicineName() {
        return medicineName;
    }
    
    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }
    
    public String getBrandName() {
        return brandName;
    }
    
    public void setBrandName(String brandName) {
        this.brandName = brandName;
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
    
    public String getMarketedBy() {
        return marketedBy;
    }
    
    public void setMarketedBy(String marketedBy) {
        this.marketedBy = marketedBy;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
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
    
    public Double getMorning() {
        return morning;
    }
    
    public void setMorning(Double morning) {
        this.morning = morning;
    }
    
    public Double getAfternoon() {
        return afternoon;
    }
    
    public void setAfternoon(Double afternoon) {
        this.afternoon = afternoon;
    }
    
    public Double getNight() {
        return night;
    }
    
    public void setNight(Double night) {
        this.night = night;
    }
    
    public Integer getNoOfDays() {
        return noOfDays;
    }
    
    public void setNoOfDays(Integer noOfDays) {
        this.noOfDays = noOfDays;
    }
    
    public String getInstruction() {
        return instruction;
    }
    
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
    
    public String getMarketedByDept() {
        return marketedByDept;
    }
    
    public void setMarketedByDept(String marketedByDept) {
        this.marketedByDept = marketedByDept;
    }
    
    @Override
    public String toString() {
        return "PrescriptionMedicines{" +
                "catShortName='" + catShortName + '\'' +
                ", catsubDescription='" + catsubDescription + '\'' +
                ", medicineName='" + medicineName + '\'' +
                ", brandName='" + brandName + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", priorityValue=" + priorityValue +
                '}';
    }
}

