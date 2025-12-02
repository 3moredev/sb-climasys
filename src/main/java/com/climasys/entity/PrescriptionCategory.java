package com.climasys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing the prescription_category table
 * Maps to the prescription category master data
 * 
 * Note: This entity has a composite primary key (cat_short_name, doctor_id)
 */
@Entity
@Table(name = "prescription_category")
@IdClass(PrescriptionCategoryId.class)
public class PrescriptionCategory {
    
    @Id
    @Column(name = "cat_short_name", length = 60, nullable = false)
    private String catShortName;
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Column(name = "cat_long_description", length = 300)
    private String catLongDescription;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdByName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedByName;
    
    // Constructors
    public PrescriptionCategory() {}
    
    public PrescriptionCategory(String catShortName, String doctorId, String catLongDescription) {
        this.catShortName = catShortName;
        this.doctorId = doctorId;
        this.catLongDescription = catLongDescription;
    }
    
    // Getters and Setters
    public String getCatShortName() {
        return catShortName;
    }
    
    public void setCatShortName(String catShortName) {
        this.catShortName = catShortName;
    }
    
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getCatLongDescription() {
        return catLongDescription;
    }
    
    public void setCatLongDescription(String catLongDescription) {
        this.catLongDescription = catLongDescription;
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
    
    @Override
    public String toString() {
        return "PrescriptionCategory{" +
                "catShortName='" + catShortName + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", catLongDescription='" + catLongDescription + '\'' +
                '}';
    }
}

