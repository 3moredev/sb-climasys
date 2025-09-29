package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "status_ref")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(StatusRefId.class)
public class StatusRef {
    
    @Id
    @Column(name = "id", nullable = false)
    private Short id;
    
    @Id
    @Column(name = "clinic_id", length = 10, nullable = false)
    private String clinicId;
    
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Column(name = "status_description", length = 60)
    private String statusDescription;
}
