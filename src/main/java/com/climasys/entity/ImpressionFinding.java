package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "balance_impression_finding")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImpressionFinding {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Column(name = "Group", length = 1000, nullable = false)
    private String group;
    
    @Column(name = "subgroup_name", length = 2000, nullable = false)
    private String subgroupName;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdbyName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedbyName;
    
    @Column(name = "sequence_no")
    private Integer sequenceNo;
    
    @Column(name = "isdefault")
    private Boolean isDefault;
}
