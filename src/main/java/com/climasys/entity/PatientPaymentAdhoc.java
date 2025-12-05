package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity for patient_payments_adhoc table
 * Represents adhoc payments made by patients
 */
@Entity
@Table(name = "patient_payments_adhoc")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PatientPaymentAdhocId.class)
public class PatientPaymentAdhoc {
    
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
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;
    
    @Column(name = "financial_year")
    private Integer financialYear;
    
    @Column(name = "fees_collected", precision = 10, scale = 2)
    private BigDecimal feesCollected;
    
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
    
    @Column(name = "comment", length = 500)
    private String comment;
    
    @Column(name = "payment_by_id")
    private Short paymentById;
    
    @Column(name = "payment_remark", length = 1000)
    private String paymentRemark;
    
    @Column(name = "attended_by", length = 30)
    private String attendedBy;
    
    @Column(name = "attended_by_id")
    private Integer attendedById;
    
    @Column(name = "receipt_number", length = 10)
    private String receiptNumber;
    
    @Column(name = "receipt_type", length = 1)
    private String receiptType;
}

