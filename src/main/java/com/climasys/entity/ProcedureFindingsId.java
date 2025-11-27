package com.climasys.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for ProcedureFindings entity
 * Represents the (doctor_id, procedure_description, findings_description) composite key from the database
 */
public class ProcedureFindingsId implements Serializable {
    
    private String doctorId;
    private String procedureDescription;
    private String findingsDescription;
    
    // Default constructor
    public ProcedureFindingsId() {}
    
    // Constructor with parameters
    public ProcedureFindingsId(String doctorId, String procedureDescription, String findingsDescription) {
        this.doctorId = doctorId;
        this.procedureDescription = procedureDescription;
        this.findingsDescription = findingsDescription;
    }
    
    // Getters and Setters
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getProcedureDescription() {
        return procedureDescription;
    }
    
    public void setProcedureDescription(String procedureDescription) {
        this.procedureDescription = procedureDescription;
    }
    
    public String getFindingsDescription() {
        return findingsDescription;
    }
    
    public void setFindingsDescription(String findingsDescription) {
        this.findingsDescription = findingsDescription;
    }
    
    // equals and hashCode methods are required for composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcedureFindingsId that = (ProcedureFindingsId) o;
        return Objects.equals(doctorId, that.doctorId) && 
               Objects.equals(procedureDescription, that.procedureDescription) && 
               Objects.equals(findingsDescription, that.findingsDescription);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(doctorId, procedureDescription, findingsDescription);
    }
    
    @Override
    public String toString() {
        return "ProcedureFindingsId{" +
                "doctorId='" + doctorId + '\'' +
                ", procedureDescription='" + procedureDescription + '\'' +
                ", findingsDescription='" + findingsDescription + '\'' +
                '}';
    }
}

