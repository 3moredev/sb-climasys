package com.climasys.repository;

import com.climasys.entity.GenderTranslations;
import com.climasys.entity.GenderTranslationsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenderTranslationsRepository extends JpaRepository<GenderTranslations, GenderTranslationsId> {
    
    @Query("SELECT g FROM GenderTranslations g WHERE g.genderId = :genderId AND g.languageId = :languageId AND (g.deleteFlag = false OR g.deleteFlag IS NULL)")
    Optional<GenderTranslations> findByGenderIdAndLanguageIdAndActive(@Param("genderId") Short genderId, @Param("languageId") Integer languageId);
    
    @Query("SELECT g FROM GenderTranslations g WHERE g.languageId = :languageId AND (g.deleteFlag = false OR g.deleteFlag IS NULL)")
    List<GenderTranslations> findByLanguageIdAndActive(@Param("languageId") Integer languageId);
    
    @Query("SELECT g FROM GenderTranslations g WHERE g.genderCode = :genderCode AND g.languageId = :languageId AND (g.deleteFlag = false OR g.deleteFlag IS NULL)")
    Optional<GenderTranslations> findByGenderCodeAndLanguageIdAndActive(@Param("genderCode") String genderCode, @Param("languageId") Integer languageId);
}
