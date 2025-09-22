package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "city_translations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityTranslation {
    
    @EmbeddedId
    private CityTranslationId id;
    
    @Column(name = "city_name", length = 60)
    private String cityName;
}

