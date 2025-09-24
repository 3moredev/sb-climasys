package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "status_ref")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(StatusRefId.class)
public class StatusRef {
    
    @Id
    @Column(name = "id", nullable = false)
    private Short id;
    
    @Id
    @Column(name = "clinic_id", length = 10, nullable = false)
    private String clinicId;
    
    @Column(name = "status_description", length = 100)
    private String statusDescription;
    
    @Column(name = "status_code", length = 20)
    private String statusCode;
    
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
