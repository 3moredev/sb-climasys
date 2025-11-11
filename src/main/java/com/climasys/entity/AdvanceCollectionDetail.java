package com.climasys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class for advance_collection_details table
 */
@Entity
@Table(name = "advance_collection_details")
@IdClass(AdvanceCollectionDetailId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvanceCollectionDetail {
    
    @Id
    @Column(name = "patient_id", length = 32, nullable = false)
    private String patientId;
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Id
    @Column(name = "clinic_id", length = 10, nullable = false)
    private String clinicId;
    
    @Id
    @Column(name = "ipd_refno", length = 100, nullable = false)
    private String ipdRefno;
    
    @Id
    @Column(name = "date", nullable = false)
    private LocalDateTime date;
    
    @Column(name = "receipt_number", length = 32)
    private String receiptNumber;
    
    @Column(name = "receipt_date")
    private LocalDateTime receiptDate;
    
    @Column(name = "amount_received", precision = 10, scale = 2)
    private BigDecimal amountReceived;
    
    @Column(name = "payment_by_id", nullable = false)
    private Short paymentById;
    
    @Column(name = "payment_remark", length = 100)
    private String paymentRemark;
    
    @Column(name = "shift_id", nullable = false)
    private Short shiftId;
    
    @Column(name = "createdby_name", length = 100)
    private String createdbyName;
    
    @Column(name = "modifiedby_name", length = 100)
    private String modifiedbyName;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "advance_date")
    private LocalDateTime advanceDate;
    
    @Column(name = "charges_details", length = 100)
    private String chargesDetails;
}

