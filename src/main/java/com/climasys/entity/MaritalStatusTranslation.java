package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "marital_status_translations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaritalStatusTranslation {
    
    @EmbeddedId
    private MaritalStatusTranslationId id;
    
    @Column(name = "marital_status_description", length = 15)
    private String maritalStatusDescription;
}

