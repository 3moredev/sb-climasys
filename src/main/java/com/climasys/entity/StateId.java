package com.climasys.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StateId implements Serializable {
    
    @Column(name = "id", length = 6, nullable = false)
    private String id;
    
    @Column(name = "country_id", length = 6, nullable = false)
    private String countryId;
}
