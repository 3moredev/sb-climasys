package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "refer_by_translations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferByTranslation {
    
    @EmbeddedId
    private ReferByTranslationId id;
    
    @Column(name = "refer_by_description", length = 20)
    private String referByDescription;
}

