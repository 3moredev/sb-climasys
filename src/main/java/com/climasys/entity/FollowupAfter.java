package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing the followup_after table
 * Maps to follow-up after period master data for the system
 */
@Entity
@Table(name = "followup_after")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowupAfter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    
    @Column(name = "followup_after", length = 100)
    private String followupAfter;
    
    @Column(name = "days")
    private Integer days;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "created_by", length = 50)
    private String createdBy;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modified_by", length = 50)
    private String modifiedBy;
    
    @Column(name = "delete_flag")
    private Boolean deleteFlag;
    
    @PrePersist
    protected void onCreate() {
        if (createdOn == null) {
            createdOn = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
        if (deleteFlag == null) {
            deleteFlag = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        modifiedOn = LocalDateTime.now();
    }
}
