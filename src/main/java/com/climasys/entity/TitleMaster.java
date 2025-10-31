package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entity representing the patient_title table
 * Maps to title master data for the system
 */
@Entity
@Table(name = "patient_title")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TitleMaster {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    
    @Column(name = "title_description", length = 10)
    private String titleDescription;
}
