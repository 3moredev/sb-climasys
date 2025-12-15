package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "clinic_doctor_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ClinicDoctorMasterId.class)
public class ClinicDoctorMaster{

    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;

    @Id
    @Column(name = "clinic_id", length = 30, nullable = false)
    private String clinicId;
}
