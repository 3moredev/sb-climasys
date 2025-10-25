package com.climasys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing the Medicine_Master table
 * Maps to the medicine master functionality
 * 
 * Note: This entity has a composite primary key (short_description, clinic_id)
 * to support multi-clinic functionality
 */
@Entity
@Table(name = "medicine_master")
@IdClass(MedicineMasterId.class)
public class MedicineMaster {
    
    @Id
    @Column(name = "short_description", length = 40, nullable = false)
    private String shortDescription;
    
    @Id
    @Column(name = "clinic_id", length = 30, nullable = false)
    private String clinicId;
    
    @Column(name = "medicine_description", length = 1000)
    private String medicineDescription;
    
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
    
    // Constructors
    public MedicineMaster() {}
    
    public MedicineMaster(String shortDescription, String clinicId, String medicineDescription) {
        this.shortDescription = shortDescription;
        this.clinicId = clinicId;
        this.medicineDescription = medicineDescription;
    }
    
    // Getters and Setters
    public String getShortDescription() {
        return shortDescription;
    }
    
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    
    public String getClinicId() {
        return clinicId;
    }
    
    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }
    
    public String getMedicineDescription() {
        return medicineDescription;
    }
    
    public void setMedicineDescription(String medicineDescription) {
        this.medicineDescription = medicineDescription;
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
    
    @Override
    public String toString() {
        return "MedicineMaster{" +
                "shortDescription='" + shortDescription + '\'' +
                ", clinicId='" + clinicId + '\'' +
                ", medicineDescription='" + medicineDescription + '\'' +
                ", active=" + active +
                ", priorityValue=" + priorityValue +
                '}';
    }
}
