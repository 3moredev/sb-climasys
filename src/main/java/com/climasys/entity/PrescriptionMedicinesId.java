package com.climasys.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for PrescriptionMedicines entity
 * Represents the (cat_short_name, catsub_description, medicine_name, brand_name, doctor_id) composite key from the database
 */
public class PrescriptionMedicinesId implements Serializable {
    
    private String catShortName;
    private String catsubDescription;
    private String medicineName;
    private String brandName;
    private String doctorId;
    
    // Default constructor
    public PrescriptionMedicinesId() {}
    
    // Constructor with parameters
    public PrescriptionMedicinesId(String catShortName, String catsubDescription, String medicineName, 
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
    
    // equals and hashCode methods are required for composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrescriptionMedicinesId that = (PrescriptionMedicinesId) o;
        return Objects.equals(catShortName, that.catShortName) && 
               Objects.equals(catsubDescription, that.catsubDescription) &&
               Objects.equals(medicineName, that.medicineName) &&
               Objects.equals(brandName, that.brandName) &&
               Objects.equals(doctorId, that.doctorId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(catShortName, catsubDescription, medicineName, brandName, doctorId);
    }
    
    @Override
    public String toString() {
        return "PrescriptionMedicinesId{" +
                "catShortName='" + catShortName + '\'' +
                ", catsubDescription='" + catsubDescription + '\'' +
                ", medicineName='" + medicineName + '\'' +
                ", brandName='" + brandName + '\'' +
                ", doctorId='" + doctorId + '\'' +
                '}';
    }
}

