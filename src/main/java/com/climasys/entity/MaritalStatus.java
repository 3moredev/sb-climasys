package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "marital_status_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaritalStatus {
    
    @Id
    @Column(name = "id", length = 1)
    private String id;
}
