package com.climasys.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for LabTestParameter entity
 * Represents the (lab_test_id, doctor_id, id) composite key from the database
 */
public class LabTestParameterId implements Serializable {
    
    private Integer labTestId;
    private String doctorId;
    private Integer id;
    
    // Default constructor
    public LabTestParameterId() {}
    
    // Constructor with parameters
    public LabTestParameterId(Integer labTestId, String doctorId, Integer id) {
        this.labTestId = labTestId;
        this.doctorId = doctorId;
        this.id = id;
    }
    
    // Getters and Setters
    public Integer getLabTestId() {
        return labTestId;
    }
    
    public void setLabTestId(Integer labTestId) {
        this.labTestId = labTestId;
    }
    
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
        LabTestParameterId that = (LabTestParameterId) o;
        return Objects.equals(labTestId, that.labTestId) && 
               Objects.equals(doctorId, that.doctorId) && 
               Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(labTestId, doctorId, id);
    }
    
    @Override
    public String toString() {
        return "LabTestParameterId{" +
                "labTestId=" + labTestId +
                ", doctorId='" + doctorId + '\'' +
                ", id=" + id +
                '}';
    }
}
