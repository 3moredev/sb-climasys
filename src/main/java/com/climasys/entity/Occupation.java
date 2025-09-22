package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "occupation_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Occupation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "occupation_description", length = 100)
    private String occupationDescription;
}
