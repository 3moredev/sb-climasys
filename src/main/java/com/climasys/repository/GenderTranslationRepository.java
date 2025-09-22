package com.climasys.repository;

import com.climasys.entity.GenderTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenderTranslationRepository extends JpaRepository<GenderTranslation, Object> {
    
    @Query("SELECT gt FROM GenderTranslation gt WHERE gt.genderId = :genderId AND gt.languageId = :languageId")
    GenderTranslation findByGenderIdAndLanguageId(@Param("genderId") String genderId, @Param("languageId") Integer languageId);
    
    @Query("SELECT gt FROM GenderTranslation gt WHERE gt.genderId = :genderId")
    List<GenderTranslation> findByGenderId(@Param("genderId") String genderId);
    
    @Query("SELECT gt FROM GenderTranslation gt WHERE gt.languageId = :languageId")
    List<GenderTranslation> findByLanguageId(@Param("languageId") Integer languageId);
}
