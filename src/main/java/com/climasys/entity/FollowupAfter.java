package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entity representing the followup_after_master table
 * Maps to follow-up after period master data for the system
 */
@Entity
@Table(name = "followup_after_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowupAfter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    
    @Column(name = "followup_after", length = 50)
    private String followupAfter;
    
    @Column(name = "days")
    private Integer days;
}
