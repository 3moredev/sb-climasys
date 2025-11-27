package com.climasys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class for doctor_procedure_master table
 * Represents procedure master data with multi-clinic support
 * 
 * Note: This entity has a composite primary key (procedure_description, doctor_id, clinic_id)
 * to support multi-clinic functionality
 */
@Entity
@Table(name = "doctor_procedure_master")
@IdClass(ProcedureMasterId.class)
public class ProcedureMaster {

    @Id
    @Column(name = "procedure_description", length = 100, nullable = false)
    private String procedureDescription;
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Id
    @Column(name = "clinic_id", length = 30, nullable = false)
    private String clinicId;

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
    public ProcedureMaster() {}

    public ProcedureMaster(String procedureDescription, String doctorId, String clinicId) {
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
        return "ProcedureMaster{" +
                "procedureDescription='" + procedureDescription + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", clinicId='" + clinicId + '\'' +
                ", priorityValue=" + priorityValue +
                '}';
    }
}

