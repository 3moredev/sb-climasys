package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "follow_up_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowUpType {
    
    @Id
    @Column(name = "id", nullable = false)
    private Short id;
    
    @Column(name = "followup_description", length = 15)
    private String followUpDescription;
}
