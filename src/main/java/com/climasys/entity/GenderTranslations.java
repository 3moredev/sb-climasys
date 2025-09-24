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
@IdClass(GenderTranslationsId.class)
public class GenderTranslations {
    
    @Id
    @Column(name = "gender_id", nullable = false)
    private Short genderId;
    
    @Id
    @Column(name = "language_id", nullable = false)
    private Integer languageId;
    
    @Column(name = "gender_description", length = 50)
    private String genderDescription;
    
    @Column(name = "gender_code", length = 10)
    private String genderCode;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "created_on")
    private java.time.LocalDateTime createdOn;
    
    @Column(name = "created_by", length = 50)
    private String createdBy;
    
    @Column(name = "modified_on")
    private java.time.LocalDateTime modifiedOn;
    
    @Column(name = "modified_by", length = 50)
    private String modifiedBy;
    
    @Column(name = "delete_flag")
    private Boolean deleteFlag;
}
