package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "state_translations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateTranslation {
    
    @EmbeddedId
    private StateTranslationId id;
    
    @Column(name = "state_name", length = 60)
    private String stateName;
}

