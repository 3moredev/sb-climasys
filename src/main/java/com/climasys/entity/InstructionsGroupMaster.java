package com.climasys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class for instructions_group_master table
 * Represents master data for instruction groups
 */
@Entity
@Table(name = "instructions_group_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(InstructionsGroupMasterId.class)
public class InstructionsGroupMaster {
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Id
    @Column(name = "group_description", length = 200, nullable = false)
    private String groupDescription;
    
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
    
    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        modifiedOn = LocalDateTime.now();
    }
}

