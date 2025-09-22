package com.climasys.repository;

import com.climasys.entity.AreaTranslation;
import com.climasys.entity.AreaTranslationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaTranslationRepository extends JpaRepository<AreaTranslation, AreaTranslationId> {
    
    @Query("SELECT at FROM AreaTranslation at WHERE at.id.areaId = :areaId AND at.id.languageId = :languageId")
    AreaTranslation findByAreaIdAndLanguageId(@Param("areaId") Integer areaId, @Param("languageId") Integer languageId);
    
    @Query("SELECT at FROM AreaTranslation at WHERE at.id.areaId = :areaId")
    List<AreaTranslation> findByAreaId(@Param("areaId") Integer areaId);
}
