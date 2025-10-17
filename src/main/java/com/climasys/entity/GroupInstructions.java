package com.climasys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class for group_instructions table
 * Represents detailed instructions within an instruction group
 */
@Entity
@Table(name = "group_instructions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(GroupInstructionsId.class)
public class GroupInstructions {
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Id
    @Column(name = "group_description", length = 200, nullable = false)
    private String groupDescription;
    
    @Id
    @Column(name = "instructions_description", length = 1000, nullable = false)
    private String instructionsDescription;
    
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
    
    @Column(name = "sequence_no")
    private Integer sequenceNo;
    
    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        modifiedOn = LocalDateTime.now();
    }
}

