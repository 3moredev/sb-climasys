package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "country_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country {
    
    @Id
    @Column(name = "id", length = 6, nullable = false)
    private String id;
}
