package com.climasys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing the insurance_company_master table
 * Maps insurance company information
 */
@Entity
@Table(name = "insurance_company_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceCompanyMaster {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id", nullable = false)
    private Integer companyId;
    
    @Column(name = "company_name", nullable = false, columnDefinition = "TEXT")
    private String companyName;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdByName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedByName;
    
    @Column(name = "isdeleted")
    private Boolean isDeleted;
}

