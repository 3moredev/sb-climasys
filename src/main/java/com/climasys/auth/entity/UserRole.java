package com.climasys.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_role")
@IdClass(UserRoleId.class)
public class UserRole {
    
    @Id
    @Column(name = "role_id", nullable = false)
    private Integer roleId;
    
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Id
    @Column(name = "clinic_id", length = 10, nullable = false)
    private String clinicId;
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Column(name = "is_default_clinic")
    private Boolean isDefaultClinic = false;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdbyName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedbyName;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private com.climasys.entity.User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private RoleMaster roleMaster;
    
    // Constructors
    public UserRole() {}
    
    public UserRole(Integer roleId, Long userId, String clinicId, String doctorId) {
        this.roleId = roleId;
        this.userId = userId;
        this.clinicId = clinicId;
        this.doctorId = doctorId;
        this.isDefaultClinic = false;
        this.createdOn = LocalDateTime.now();
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
    
    public Boolean getIsDefaultClinic() { return isDefaultClinic; }
    public void setIsDefaultClinic(Boolean isDefaultClinic) { this.isDefaultClinic = isDefaultClinic; }
    
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    
    public String getCreatedbyName() { return createdbyName; }
    public void setCreatedbyName(String createdbyName) { this.createdbyName = createdbyName; }
    
    public LocalDateTime getModifiedOn() { return modifiedOn; }
    public void setModifiedOn(LocalDateTime modifiedOn) { this.modifiedOn = modifiedOn; }
    
    public String getModifiedbyName() { return modifiedbyName; }
    public void setModifiedbyName(String modifiedbyName) { this.modifiedbyName = modifiedbyName; }
    
    public com.climasys.entity.User getUser() { return user; }
    public void setUser(com.climasys.entity.User user) { this.user = user; }
    
    public RoleMaster getRoleMaster() { return roleMaster; }
    public void setRoleMaster(RoleMaster roleMaster) { this.roleMaster = roleMaster; }
}
