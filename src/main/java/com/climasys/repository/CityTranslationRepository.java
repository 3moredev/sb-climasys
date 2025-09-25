package com.climasys.repository;

import com.climasys.entity.CityTranslation;
import com.climasys.entity.CityTranslationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityTranslationRepository extends JpaRepository<CityTranslation, CityTranslationId> {
    
    @Query("SELECT ct FROM CityTranslation ct WHERE ct.id.cityId = :cityId AND ct.id.languageId = :languageId")
    CityTranslation findByCityIdAndLanguageId(@Param("cityId") String cityId, @Param("languageId") Integer languageId);
    
    @Query("SELECT ct FROM CityTranslation ct WHERE ct.id.cityId = :cityId")
    List<CityTranslation> findByCityId(@Param("cityId") String cityId);
    
    @Query("SELECT ct FROM CityTranslation ct INNER JOIN City c ON ct.id.cityId = c.id.id " +
           "AND ct.id.stateId = c.id.stateId AND ct.id.countryId = c.id.countryId " +
           "WHERE ct.id.languageId = :languageId AND ct.cityName LIKE %:searchStr%")
    List<CityTranslation> searchCitiesByLanguageAndName(@Param("searchStr") String searchStr, @Param("languageId") Integer languageId);
}
