package com.climasys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class for doctor_procedure_findings table
 * Represents procedure findings data associated with procedures
 * 
 * Note: This entity has a composite primary key (doctor_id, procedure_description, findings_description)
 */
@Entity
@Table(name = "doctor_procedure_findings")
@IdClass(ProcedureFindingsId.class)
public class ProcedureFindings {

    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Id
    @Column(name = "procedure_description", length = 100, nullable = false)
    private String procedureDescription;
    
    @Id
    @Column(name = "findings_description", length = 200, nullable = false)
    private String findingsDescription;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "createdby_name", length = 90)
    private String createdByName;

    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;

    @Column(name = "modifiedby_name", length = 90)
    private String modifiedByName;

    @Column(name = "priority_value")
    private Integer priorityValue;

    // Constructors
    public ProcedureFindings() {}

    public ProcedureFindings(String doctorId, String procedureDescription, String findingsDescription) {
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

    public Integer getPriorityValue() {
        return priorityValue;
    }

    public void setPriorityValue(Integer priorityValue) {
        this.priorityValue = priorityValue;
    }

    @Override
    public String toString() {
        return "ProcedureFindings{" +
                "doctorId='" + doctorId + '\'' +
                ", procedureDescription='" + procedureDescription + '\'' +
                ", findingsDescription='" + findingsDescription + '\'' +
                ", priorityValue=" + priorityValue +
                '}';
    }
}

