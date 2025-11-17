package com.climasys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity for patient_title table
 */
@Entity
@Table(name = "patient_title")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientTitle {
    
    @Id
    @Column(name = "id", nullable = false)
    private Short id;
    
    @Column(name = "title_description", length = 10)
    private String titleDescription;
}

