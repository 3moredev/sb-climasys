package com.climasys.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for StatusOrder entity
 * Represents the (doctor_id, role_id, status_id, clinic_id) composite key from the database
 */
public class StatusOrderId implements Serializable {
    
    private String doctorId;
    private Integer roleId;
    private Short statusId;
    private String clinicId;
    
    // Default constructor
    public StatusOrderId() {}
    
    // Constructor with parameters
    public StatusOrderId(String doctorId, Integer roleId, Short statusId, String clinicId) {
        this.doctorId = doctorId;
        this.roleId = roleId;
        this.statusId = statusId;
        this.clinicId = clinicId;
    }
    
    // Getters and Setters
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public Integer getRoleId() {
        return roleId;
    }
    
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
    
    public Short getStatusId() {
        return statusId;
    }
    
    public void setStatusId(Short statusId) {
        this.statusId = statusId;
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
        StatusOrderId that = (StatusOrderId) o;
        return Objects.equals(doctorId, that.doctorId) && 
               Objects.equals(roleId, that.roleId) && 
               Objects.equals(statusId, that.statusId) && 
               Objects.equals(clinicId, that.clinicId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(doctorId, roleId, statusId, clinicId);
    }
    
    @Override
    public String toString() {
        return "StatusOrderId{" +
                "doctorId='" + doctorId + '\'' +
                ", roleId=" + roleId +
                ", statusId=" + statusId +
                ", clinicId='" + clinicId + '\'' +
                '}';
    }
}
