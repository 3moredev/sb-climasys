package com.climasys.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for PrescriptionSubCategory entity
 * Represents the (cat_short_name, catsub_description, doctor_id) composite key from the database
 */
public class PrescriptionSubCategoryId implements Serializable {
    
    private String catShortName;
    private String catsubDescription;
    private String doctorId;
    
    // Default constructor
    public PrescriptionSubCategoryId() {}
    
    // Constructor with parameters
    public PrescriptionSubCategoryId(String catShortName, String catsubDescription, String doctorId) {
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
    
    // equals and hashCode methods are required for composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrescriptionSubCategoryId that = (PrescriptionSubCategoryId) o;
        return Objects.equals(catShortName, that.catShortName) && 
               Objects.equals(catsubDescription, that.catsubDescription) &&
               Objects.equals(doctorId, that.doctorId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(catShortName, catsubDescription, doctorId);
    }
    
    @Override
    public String toString() {
        return "PrescriptionSubCategoryId{" +
                "catShortName='" + catShortName + '\'' +
                ", catsubDescription='" + catsubDescription + '\'' +
                ", doctorId='" + doctorId + '\'' +
                '}';
    }
}

