package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "follow-up_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowUpType {
    
    @Id
    @Column(name = "id", nullable = false)
    private Short id;
    
    @Column(name = "followup_description", length = 100)
    private String followUpDescription;
    
    @Column(name = "followup_code", length = 20)
    private String followUpCode;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "created_on")
    private java.time.LocalDateTime createdOn;
    
    @Column(name = "created_by", length = 50)
    private String createdBy;
    
    @Column(name = "modified_on")
    private java.time.LocalDateTime modifiedOn;
    
    @Column(name = "modified_by", length = 50)
    private String modifiedBy;
    
    @Column(name = "delete_flag")
    private Boolean deleteFlag;
}
