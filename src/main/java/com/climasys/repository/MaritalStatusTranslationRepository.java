package com.climasys.repository;

import com.climasys.entity.MaritalStatusTranslation;
import com.climasys.entity.MaritalStatusTranslationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaritalStatusTranslationRepository extends JpaRepository<MaritalStatusTranslation, MaritalStatusTranslationId> {
    
    @Query("SELECT mst FROM MaritalStatusTranslation mst WHERE mst.id.maritalStatusId = :maritalStatusId AND mst.id.languageId = :languageId")
    MaritalStatusTranslation findByMaritalStatusIdAndLanguageId(@Param("maritalStatusId") String maritalStatusId, @Param("languageId") Integer languageId);
    
    @Query("SELECT mst FROM MaritalStatusTranslation mst WHERE mst.id.maritalStatusId = :maritalStatusId")
    List<MaritalStatusTranslation> findByMaritalStatusId(@Param("maritalStatusId") String maritalStatusId);
    
    @Query("SELECT mst FROM MaritalStatusTranslation mst WHERE mst.id.languageId = :languageId ORDER BY mst.id.maritalStatusId")
    List<MaritalStatusTranslation> findByLanguageId(@Param("languageId") Integer languageId);
}
