package com.climasys.repository;

import com.climasys.entity.CountryTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryTranslationRepository extends JpaRepository<CountryTranslation, Object> {
    
    @Query("SELECT ct FROM CountryTranslation ct WHERE ct.countryId = :countryId AND ct.languageId = :languageId")
    CountryTranslation findByCountryIdAndLanguageId(@Param("countryId") String countryId, @Param("languageId") Integer languageId);
    
    @Query("SELECT ct FROM CountryTranslation ct WHERE ct.countryId = :countryId")
    List<CountryTranslation> findByCountryId(@Param("countryId") String countryId);
}
