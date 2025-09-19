package com.climasys.auth.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserRoleId implements Serializable {
    
    private Integer roleId;
    private Long userId;
    private String clinicId;
    private String doctorId;
    
    public UserRoleId() {}
    
    public UserRoleId(Integer roleId, Long userId, String clinicId, String doctorId) {
        this.roleId = roleId;
        this.userId = userId;
        this.clinicId = clinicId;
        this.doctorId = doctorId;
    }
    
    // Getters and Setters
    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getClinicId() { return clinicId; }
    public void setClinicId(String clinicId) { this.clinicId = clinicId; }
    
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleId that = (UserRoleId) o;
        return Objects.equals(roleId, that.roleId) &&
               Objects.equals(userId, that.userId) &&
               Objects.equals(clinicId, that.clinicId) &&
               Objects.equals(doctorId, that.doctorId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(roleId, userId, clinicId, doctorId);
    }
}
