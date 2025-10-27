package com.climasys.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity representing the Status_Order table
 * Maps to the status ordering functionality for roles
 * 
 * Note: This entity has a composite primary key (doctor_id, role_id, status_id, clinic_id)
 * to support multi-clinic functionality
 */
@Entity
@Table(name = "status_order")
@IdClass(StatusOrderId.class)
public class StatusOrder {
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Id
    @Column(name = "role_id", nullable = false)
    private Integer roleId;
    
    @Id
    @Column(name = "status_id", nullable = false)
    private Short statusId;
    
    @Id
    @Column(name = "clinic_id", length = 30, nullable = false)
    private String clinicId;
    
    @Column(name = "sort_order")
    private Short sortOrder;
    
    // Constructors
    public StatusOrder() {}
    
    public StatusOrder(String doctorId, Integer roleId, Short statusId, String clinicId, Short sortOrder) {
        this.doctorId = doctorId;
        this.roleId = roleId;
        this.statusId = statusId;
        this.clinicId = clinicId;
        this.sortOrder = sortOrder;
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
    
    public Short getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Short sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    @Override
    public String toString() {
        return "StatusOrder{" +
                "doctorId='" + doctorId + '\'' +
                ", roleId=" + roleId +
                ", statusId=" + statusId +
                ", clinicId='" + clinicId + '\'' +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
