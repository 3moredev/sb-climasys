package com.climasys.repository;

import com.climasys.entity.CountryTranslation;
import com.climasys.entity.CountryTranslationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryTranslationRepository extends JpaRepository<CountryTranslation, CountryTranslationId> {
    
    @Query("SELECT ct FROM CountryTranslation ct WHERE ct.id.countryId = :countryId AND ct.id.languageId = :languageId")
    CountryTranslation findByCountryIdAndLanguageId(@Param("countryId") String countryId, @Param("languageId") Integer languageId);
    
    @Query("SELECT ct FROM CountryTranslation ct WHERE ct.id.countryId = :countryId")
    List<CountryTranslation> findByCountryId(@Param("countryId") String countryId);
}
