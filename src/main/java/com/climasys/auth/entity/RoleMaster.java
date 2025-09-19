package com.climasys.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "role_master")
public class RoleMaster {
    
    @Id
    @Column(name = "role_id")
    private Integer roleId;
    
    @Column(name = "role_name", length = 30)
    private String roleName;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdbyName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedbyName;
    
    // Constructors
    public RoleMaster() {}
    
    public RoleMaster(Integer roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.createdOn = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
    
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    
    public String getCreatedbyName() { return createdbyName; }
    public void setCreatedbyName(String createdbyName) { this.createdbyName = createdbyName; }
    
    public LocalDateTime getModifiedOn() { return modifiedOn; }
    public void setModifiedOn(LocalDateTime modifiedOn) { this.modifiedOn = modifiedOn; }
    
    public String getModifiedbyName() { return modifiedbyName; }
    public void setModifiedbyName(String modifiedbyName) { this.modifiedbyName = modifiedbyName; }
}
