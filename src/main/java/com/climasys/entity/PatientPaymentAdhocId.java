package com.climasys.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Composite primary key for PatientPaymentAdhoc entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PatientPaymentAdhocId implements Serializable {
    
    private String doctorId;
    private String clinicId;
    private Short shiftId;
    private String patientId;
    private LocalDateTime paymentDate;
}

