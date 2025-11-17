package com.climasys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity for patient_receipts table
 * Composite Primary Key: (doctor_id, clinic_id, patient_id, receipt_number, to_date, from_date)
 */
@Entity
@Table(name = "patient_receipts")
@IdClass(PatientReceiptId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientReceipt {
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Id
    @Column(name = "clinic_id", length = 10, nullable = false)
    private String clinicId;
    
    @Id
    @Column(name = "patient_id", length = 32, nullable = false)
    private String patientId;
    
    @Id
    @Column(name = "receipt_number", length = 10, nullable = false)
    private String receiptNumber;
    
    @Column(name = "receipt_date")
    private LocalDateTime receiptDate;
    
    @Column(name = "receipt_type", length = 1)
    private String receiptType;
    
    @Column(name = "receipt_amount", precision = 10, scale = 2)
    private BigDecimal receiptAmount;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdbyName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedbyName;
    
    @Column(name = "shift_id")
    private Short shiftId;
    
    @Column(name = "treatment_details", length = 500)
    private String treatmentDetails;
    
    @Column(name = "title")
    private Short title;
    
    @Id
    @Column(name = "to_date", nullable = false)
    private LocalDateTime toDate;
    
    @Id
    @Column(name = "from_date", nullable = false)
    private LocalDateTime fromDate;
    
    @Column(name = "visit_type", length = 1)
    private String visitType;
    
    @Column(name = "patient_visit_no")
    private Integer patientVisitNo;
}

