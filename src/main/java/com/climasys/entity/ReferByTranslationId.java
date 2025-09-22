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
public class ReferByTranslationId implements Serializable {
    
    @Column(name = "refer_id", length = 1, nullable = false)
    private String referId;
    
    @Column(name = "language_id", nullable = false)
    private Integer languageId;
}
