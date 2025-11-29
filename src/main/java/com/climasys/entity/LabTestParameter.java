package com.climasys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing the Lab_Test_Parameter table
 * Maps to the USP_Get_LabTestAndParameter stored procedure functionality
 * 
 * Note: This entity maps to the updated database schema which has:
 * - Composite primary key (doctor_id, id, lab_test_id, clinic_id)
 * - Foreign key relationship with Lab_Test_Master table
 * - Multi-clinic support with clinic_id field
 */
@Entity
@Table(name = "lab_test_parameter")
@IdClass(LabTestParameterId.class)
public class LabTestParameter {
    
    @Id
    @Column(name = "doctor_id", length = 30)
    private String doctorId;
    
    @Id
    @Column(name = "id", insertable = false, updatable = false)
    private Integer id;
    
    @Id
    @Column(name = "lab_test_id")
    private Integer labTestId;
    
    @Id
    @Column(name = "clinic_id", length = 30)
    private String clinicId;
    
    @Column(name = "parameter_name", length = 100)
    private String parameterName;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdbyName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedbyName;
    
    // Constructors
    public LabTestParameter() {}
    
    public LabTestParameter(String doctorId, Integer id, Integer labTestId, String clinicId, String parameterName) {
        this.doctorId = doctorId;
        this.id = id;
        this.labTestId = labTestId;
        this.clinicId = clinicId;
        this.parameterName = parameterName;
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
    
    public String getClinicId() {
        return clinicId;
    }
    
    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }
    
    public String getParameterName() {
        return parameterName;
    }
    
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }
    
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }
    
    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }
    
    public String getCreatedbyName() {
        return createdbyName;
    }
    
    public void setCreatedbyName(String createdbyName) {
        this.createdbyName = createdbyName;
    }
    
    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }
    
    public void setModifiedOn(LocalDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }
    
    public String getModifiedbyName() {
        return modifiedbyName;
    }
    
    public void setModifiedbyName(String modifiedbyName) {
        this.modifiedbyName = modifiedbyName;
    }
    
    @Override
    public String toString() {
        return "LabTestParameter{" +
                "labTestId=" + labTestId +
                ", doctorId='" + doctorId + '\'' +
                ", id=" + id +
                ", parameterName='" + parameterName + '\'' +
                '}';
    }
}
