package com.climasys.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for PrescriptionCategory entity
 * Represents the (cat_short_name, doctor_id) composite key from the database
 */
public class PrescriptionCategoryId implements Serializable {
    
    private String catShortName;
    private String doctorId;
    
    // Default constructor
    public PrescriptionCategoryId() {}
    
    // Constructor with parameters
    public PrescriptionCategoryId(String catShortName, String doctorId) {
        this.catShortName = catShortName;
        this.doctorId = doctorId;
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
    
    // equals and hashCode methods are required for composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrescriptionCategoryId that = (PrescriptionCategoryId) o;
        return Objects.equals(catShortName, that.catShortName) && 
               Objects.equals(doctorId, that.doctorId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(catShortName, doctorId);
    }
    
    @Override
    public String toString() {
        return "PrescriptionCategoryId{" +
                "catShortName='" + catShortName + '\'' +
                ", doctorId='" + doctorId + '\'' +
                '}';
    }
}

