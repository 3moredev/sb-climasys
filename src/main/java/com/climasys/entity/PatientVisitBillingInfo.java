package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_visit_billinginfo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PatientVisitBillingInfoId.class)
public class PatientVisitBillingInfo {
    
    @Id
    @Column(name = "visit_date", nullable = false)
    private LocalDateTime visitDate;
    
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
    @Column(name = "billing_group_name", length = 50, nullable = false)
    private String billingGroupName;
    
    @Id
    @Column(name = "billing_subgroup_name", length = 50, nullable = false)
    private String billingSubgroupName;
    
    @Id
    @Column(name = "billing_details", length = 50, nullable = false)
    private String billingDetails;
    
    @Column(name = "default_fees", precision = 10, scale = 2)
    private BigDecimal defaultFees;
    
    @Column(name = "collected_fees", precision = 10, scale = 2)
    private BigDecimal collectedFees;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdbyName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedbyName;
    
    @Column(name = "delete_flag")
    private Boolean deleteFlag;
}

