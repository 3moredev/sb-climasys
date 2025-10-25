package com.climasys.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for ComplaintMaster entity
 * Represents the (short_description, doctor_id, clinic_id) composite key from the database
 */
public class ComplaintMasterId implements Serializable {
    
    private String shortDescription;
    private String doctorId;
    private String clinicId;
    
    // Default constructor
    public ComplaintMasterId() {}
    
    // Constructor with parameters
    public ComplaintMasterId(String shortDescription, String doctorId, String clinicId) {
        this.shortDescription = shortDescription;
        this.doctorId = doctorId;
        this.clinicId = clinicId;
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
    
    // equals and hashCode methods are required for composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplaintMasterId that = (ComplaintMasterId) o;
        return Objects.equals(shortDescription, that.shortDescription) && 
               Objects.equals(doctorId, that.doctorId) && 
               Objects.equals(clinicId, that.clinicId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(shortDescription, doctorId, clinicId);
    }
    
    @Override
    public String toString() {
        return "ComplaintMasterId{" +
                "shortDescription='" + shortDescription + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", clinicId='" + clinicId + '\'' +
                '}';
    }
}
