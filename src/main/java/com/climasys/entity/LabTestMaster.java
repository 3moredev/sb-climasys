package com.climasys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing the Lab_Test_Master table
 * Maps to the USP_Get_LabTest stored procedure functionality
 * 
 * Note: This entity maps to the updated database schema which has:
 * - Composite primary key (doctor_id, id, clinic_id)
 * - Multi-clinic support with clinic_id field
 */
@Entity
@Table(name = "lab_test_master")
@IdClass(LabTestMasterId.class)
public class LabTestMaster {
    
    @Id
    @Column(name = "doctor_id", length = 30)
    private String doctorId;
    
    @Id
    @Column(name = "id")
    private Integer id;
    
    @Id
    @Column(name = "clinic_id", length = 30)
    private String clinicId;
    
    @Column(name = "lab_test_description", length = 80)
    private String labTestDescription;
    
    @Column(name = "group_name", length = 40)
    private String groupName;
    
    @Column(name = "priority_value")
    private Integer priorityValue;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdbyName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedbyName;
    
    // Constructors
    public LabTestMaster() {}
    
    public LabTestMaster(String doctorId, Integer id, String clinicId, String labTestDescription, Integer priorityValue) {
        this.doctorId = doctorId;
        this.id = id;
        this.clinicId = clinicId;
        this.labTestDescription = labTestDescription;
        this.priorityValue = priorityValue;
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
    
    public String getClinicId() {
        return clinicId;
    }
    
    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }
    
    public String getLabTestDescription() {
        return labTestDescription;
    }
    
    public void setLabTestDescription(String labTestDescription) {
        this.labTestDescription = labTestDescription;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public Integer getPriorityValue() {
        return priorityValue;
    }
    
    public void setPriorityValue(Integer priorityValue) {
        this.priorityValue = priorityValue;
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
        return "LabTestMaster{" +
                "doctorId='" + doctorId + '\'' +
                ", id=" + id +
                ", labTestDescription='" + labTestDescription + '\'' +
                ", groupName='" + groupName + '\'' +
                ", priorityValue=" + priorityValue +
                '}';
    }
}
