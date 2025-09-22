package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "area_translations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaTranslation {
    
    @EmbeddedId
    private AreaTranslationId id;
    
    @Column(name = "area_name", length = 60)
    private String areaName;
}

