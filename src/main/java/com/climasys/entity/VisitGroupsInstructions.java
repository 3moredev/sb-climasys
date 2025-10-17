package com.climasys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class for visit_groups_instructions table
 * Represents instruction groups associated with patient visits
 */
@Entity
@Table(name = "visit_groups_instructions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(VisitGroupsInstructionsId.class)
public class VisitGroupsInstructions {
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Id
    @Column(name = "clinic_id", length = 10, nullable = false)
    private String clinicId;
    
    @Id
    @Column(name = "shift_id", nullable = false)
    private Short shiftId;
    
    @Id
    @Column(name = "patient_id", length = 32, nullable = false)
    private String patientId;
    
    @Id
    @Column(name = "patient_visit_no", nullable = false)
    private Integer patientVisitNo;
    
    @Id
    @Column(name = "visit_date", nullable = false)
    private LocalDateTime visitDate;
    
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

