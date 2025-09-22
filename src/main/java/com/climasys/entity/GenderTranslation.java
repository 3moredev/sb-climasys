package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "gender_translations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenderTranslation {
    
    @EmbeddedId
    private GenderTranslationId id;
    
    @Column(name = "gender_description", length = 15)
    private String genderDescription;
}

