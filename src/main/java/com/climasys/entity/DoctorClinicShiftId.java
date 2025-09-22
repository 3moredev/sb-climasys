package com.climasys.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DoctorClinicShiftId implements Serializable {
    
    @Column(name = "shift_id", nullable = false)
    private Short shiftId;
    
    @Column(name = "clinic_id", length = 10, nullable = false)
    private String clinicId;
    
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
}
