package com.climasys.auth.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for RoleMaster entity
 * Represents the (role_id, clinic_id) composite key from the database
 */
public class RoleMasterId implements Serializable {
    
    private Integer roleId;
    private String clinicId;
    
    // Default constructor
    public RoleMasterId() {}
    
    // Constructor with parameters
    public RoleMasterId(Integer roleId, String clinicId) {
        this.roleId = roleId;
        this.clinicId = clinicId;
    }
    
    // Getters and Setters
    public Integer getRoleId() {
        return roleId;
    }
    
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
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
        RoleMasterId that = (RoleMasterId) o;
        return Objects.equals(roleId, that.roleId) && 
               Objects.equals(clinicId, that.clinicId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(roleId, clinicId);
    }
    
    @Override
    public String toString() {
        return "RoleMasterId{" +
                "roleId=" + roleId +
                ", clinicId='" + clinicId + '\'' +
                '}';
    }
}
