package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "country_translations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryTranslation {
    
    @EmbeddedId
    private CountryTranslationId id;
    
    @Column(name = "country_name", length = 60)
    private String countryName;
}

