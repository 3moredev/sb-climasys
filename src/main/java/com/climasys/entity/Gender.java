package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "gender_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gender {
    
    @Id
    @Column(name = "id", length = 1)
    private String id;
}
