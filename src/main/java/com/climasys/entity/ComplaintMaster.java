package com.climasys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class for Complaint_Master table
 * Represents complaint master data with operator display filtering
 */
@Entity
@Table(name = "complaint_master")
public class ComplaintMaster {

    @Id
    @Column(name = "short_description", length = 40, nullable = false)
    private String shortDescription;

    @Column(name = "complaint_description", length = 1000)
    private String complaintDescription;

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

    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;

    @Column(name = "display_to_operator", nullable = false)
    private Short displayToOperator = 0;

    // Constructors
    public ComplaintMaster() {}

    public ComplaintMaster(String shortDescription, String complaintDescription, String doctorId) {
        this.shortDescription = shortDescription;
        this.complaintDescription = complaintDescription;
        this.doctorId = doctorId;
    }

    // Getters and Setters
    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getComplaintDescription() {
        return complaintDescription;
    }

    public void setComplaintDescription(String complaintDescription) {
        this.complaintDescription = complaintDescription;
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

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public Short getDisplayToOperator() {
        return displayToOperator;
    }

    public void setDisplayToOperator(Short displayToOperator) {
        this.displayToOperator = displayToOperator;
    }

    // Helper methods
    public boolean isDisplayToOperator() {
        return displayToOperator != null && displayToOperator == 1;
    }

    public void setDisplayToOperator(boolean displayToOperator) {
        this.displayToOperator = displayToOperator ? (short) 1 : (short) 0;
    }

    @Override
    public String toString() {
        return "ComplaintMaster{" +
                "shortDescription='" + shortDescription + '\'' +
                ", complaintDescription='" + complaintDescription + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", displayToOperator=" + displayToOperator +
                ", priorityValue=" + priorityValue +
                '}';
    }
}
