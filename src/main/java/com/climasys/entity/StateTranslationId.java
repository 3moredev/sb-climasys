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
public class StateTranslationId implements Serializable {
    
    @Column(name = "state_id", length = 6, nullable = false)
    private String stateId;
    
    @Column(name = "country_id", length = 6, nullable = false)
    private String countryId;
    
    @Column(name = "language_id", nullable = false)
    private Integer languageId;
}
