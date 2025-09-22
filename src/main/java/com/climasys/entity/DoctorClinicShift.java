package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "doctor_clinic_shift")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorClinicShift {
    
    @EmbeddedId
    private DoctorClinicShiftId id;
}

