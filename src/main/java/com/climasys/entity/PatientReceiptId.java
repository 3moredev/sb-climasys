package com.climasys.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Composite primary key for PatientReceipt entity
 * Primary key: (doctor_id, clinic_id, patient_id, receipt_number, to_date, from_date)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientReceiptId implements Serializable {
    private String doctorId;
    private String clinicId;
    private String patientId;
    private String receiptNumber;
    private LocalDateTime toDate;
    private LocalDateTime fromDate;
}

