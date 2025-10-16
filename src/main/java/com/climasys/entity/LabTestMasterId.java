package com.climasys.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for LabTestMaster entity
 * Represents the (doctor_id, id) composite key from the database
 */
public class LabTestMasterId implements Serializable {
    
    private String doctorId;
    private Integer id;
    
    // Default constructor
    public LabTestMasterId() {}
    
    // Constructor with parameters
    public LabTestMasterId(String doctorId, Integer id) {
        this.doctorId = doctorId;
        this.id = id;
    }
    
    // Getters and Setters
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    // equals and hashCode methods are required for composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabTestMasterId that = (LabTestMasterId) o;
        return Objects.equals(doctorId, that.doctorId) && Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(doctorId, id);
    }
    
    @Override
    public String toString() {
        return "LabTestMasterId{" +
                "doctorId='" + doctorId + '\'' +
                ", id=" + id +
                '}';
    }
}
