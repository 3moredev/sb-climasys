package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "bloodgroup_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloodGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "bloodgroup_description", length = 10)
    private String bloodgroupDescription;
}
