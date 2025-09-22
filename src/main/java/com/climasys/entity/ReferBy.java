package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "refer_by")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferBy {
    
    @Id
    @Column(name = "id", length = 1)
    private String id;
}
