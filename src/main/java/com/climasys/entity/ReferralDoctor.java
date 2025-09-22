package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "referrel_doctors_list")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferralDoctor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rd_id")
    private Integer rdId;
    
    @Column(name = "doctor_name", length = 50, nullable = false)
    private String doctorName;
    
    @Column(name = "doctor_address", length = 150)
    private String doctorAddress;
    
    @Column(name = "doctor_mob", length = 20)
    private String doctorMob;
    
    @Column(name = "doctor_mail", length = 60)
    private String doctorMail;
    
    @Column(name = "refer_id", length = 1, nullable = false)
    private String referId;
    
    @Column(name = "language_id")
    private Integer languageId;
    
    @Column(name = "remarks", length = 500)
    private String remarks;
}
