package com.climasys.repository;

import com.climasys.entity.GenderTranslation;
import com.climasys.entity.GenderTranslationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenderTranslationRepository extends JpaRepository<GenderTranslation, GenderTranslationId> {
    
    @Query("SELECT gt FROM GenderTranslation gt WHERE gt.id.genderId = :genderId AND gt.id.languageId = :languageId")
    GenderTranslation findByGenderIdAndLanguageId(@Param("genderId") String genderId, @Param("languageId") Integer languageId);
    
    @Query("SELECT gt FROM GenderTranslation gt WHERE gt.id.genderId = :genderId")
    List<GenderTranslation> findByGenderId(@Param("genderId") String genderId);
    
    @Query("SELECT gt FROM GenderTranslation gt WHERE gt.id.languageId = :languageId ORDER BY gt.id.genderId")
    List<GenderTranslation> findByLanguageId(@Param("languageId") Integer languageId);
}
