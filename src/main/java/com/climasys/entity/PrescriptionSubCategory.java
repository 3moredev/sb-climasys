package com.climasys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing the prescription_subcategory table
 * Maps to the prescription subcategory master data
 * 
 * Note: This entity has a composite primary key (cat_short_name, catsub_description, doctor_id)
 */
@Entity
@Table(name = "prescription_subcategory")
@IdClass(PrescriptionSubCategoryId.class)
public class PrescriptionSubCategory {
    
    @Id
    @Column(name = "cat_short_name", length = 60, nullable = false)
    private String catShortName;
    
    @Id
    @Column(name = "catsub_description", length = 200, nullable = false)
    private String catsubDescription;
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdByName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedByName;
    
    // Constructors
    public PrescriptionSubCategory() {}
    
    public PrescriptionSubCategory(String catShortName, String catsubDescription, String doctorId) {
        this.catShortName = catShortName;
        this.catsubDescription = catsubDescription;
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
    
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
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
        return "PrescriptionSubCategory{" +
                "catShortName='" + catShortName + '\'' +
                ", catsubDescription='" + catsubDescription + '\'' +
                ", doctorId='" + doctorId + '\'' +
                '}';
    }
}

