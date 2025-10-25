package com.climasys.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for LabTestParameter entity
 * Represents the (doctor_id, id, lab_test_id, clinic_id) composite key from the database
 */
public class LabTestParameterId implements Serializable {
    
    private String doctorId;
    private Integer id;
    private Integer labTestId;
    private String clinicId;
    
    // Default constructor
    public LabTestParameterId() {}
    
    // Constructor with parameters
    public LabTestParameterId(String doctorId, Integer id, Integer labTestId, String clinicId) {
        this.doctorId = doctorId;
        this.id = id;
        this.labTestId = labTestId;
        this.clinicId = clinicId;
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
    
    public Integer getLabTestId() {
        return labTestId;
    }
    
    public void setLabTestId(Integer labTestId) {
        this.labTestId = labTestId;
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
        LabTestParameterId that = (LabTestParameterId) o;
        return Objects.equals(doctorId, that.doctorId) && 
               Objects.equals(id, that.id) && 
               Objects.equals(labTestId, that.labTestId) && 
               Objects.equals(clinicId, that.clinicId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(doctorId, id, labTestId, clinicId);
    }
    
    @Override
    public String toString() {
        return "LabTestParameterId{" +
                "doctorId='" + doctorId + '\'' +
                ", id=" + id +
                ", labTestId=" + labTestId +
                ", clinicId='" + clinicId + '\'' +
                '}';
    }
}
