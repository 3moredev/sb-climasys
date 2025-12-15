package com.climasys.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "license_key")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ClinicDoctorMasterId.class)
public class LicenceKey {

    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;

    @Id
    @Column(name = "clinic_id", length = 10, nullable = false)
    private String clinicId;

    @Column(name = "installation_date")
    private LocalDateTime installationDate;

    @Column(name = "last_renewal_date")
    private LocalDateTime lastRenewalDate;

    @Column(name = "license_validity", nullable = false)
    private Integer licenseValidity;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo;

    @Column(name = "verno", nullable = false)
    private String version;

    @Column(name = "license_key", length = 4000)
    private String licenseKey;

    @Column(name = "start_verification")
    private Integer startVerification;
}
