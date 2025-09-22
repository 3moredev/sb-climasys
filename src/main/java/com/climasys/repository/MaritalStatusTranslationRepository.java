package com.climasys.repository;

import com.climasys.entity.MaritalStatusTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaritalStatusTranslationRepository extends JpaRepository<MaritalStatusTranslation, Object> {
    
    @Query("SELECT mst FROM MaritalStatusTranslation mst WHERE mst.maritalStatusId = :maritalStatusId AND mst.languageId = :languageId")
    MaritalStatusTranslation findByMaritalStatusIdAndLanguageId(@Param("maritalStatusId") String maritalStatusId, @Param("languageId") Integer languageId);
    
    @Query("SELECT mst FROM MaritalStatusTranslation mst WHERE mst.maritalStatusId = :maritalStatusId")
    List<MaritalStatusTranslation> findByMaritalStatusId(@Param("maritalStatusId") String maritalStatusId);
}
