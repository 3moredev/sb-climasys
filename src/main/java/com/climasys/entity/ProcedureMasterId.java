package com.climasys.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for ProcedureMaster entity
 * Represents the (procedure_description, doctor_id, clinic_id) composite key from the database
 */
public class ProcedureMasterId implements Serializable {
    
    private String procedureDescription;
    private String doctorId;
    private String clinicId;
    
    // Default constructor
    public ProcedureMasterId() {}
    
    // Constructor with parameters
    public ProcedureMasterId(String procedureDescription, String doctorId, String clinicId) {
        this.procedureDescription = procedureDescription;
        this.doctorId = doctorId;
        this.clinicId = clinicId;
    }
    
    // Getters and Setters
    public String getProcedureDescription() {
        return procedureDescription;
    }
    
    public void setProcedureDescription(String procedureDescription) {
        this.procedureDescription = procedureDescription;
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
        ProcedureMasterId that = (ProcedureMasterId) o;
        return Objects.equals(procedureDescription, that.procedureDescription) && 
               Objects.equals(doctorId, that.doctorId) && 
               Objects.equals(clinicId, that.clinicId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(procedureDescription, doctorId, clinicId);
    }
    
    @Override
    public String toString() {
        return "ProcedureMasterId{" +
                "procedureDescription='" + procedureDescription + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", clinicId='" + clinicId + '\'' +
                '}';
    }
}

