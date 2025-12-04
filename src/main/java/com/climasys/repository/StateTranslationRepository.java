package com.climasys.repository;

import com.climasys.entity.StateTranslation;
import com.climasys.entity.StateTranslationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateTranslationRepository extends JpaRepository<StateTranslation, StateTranslationId> {

    @Query("SELECT st FROM StateTranslation st WHERE st.id.stateId = :stateId AND st.id.languageId = :languageId")
    StateTranslation findByStateIdAndLanguageId(@Param("stateId") String stateId,
            @Param("languageId") Integer languageId);

    @Query("SELECT st FROM StateTranslation st WHERE st.id.countryId = :countryId AND st.id.languageId = :languageId")
    List<StateTranslation> findByCountryIdAndLanguageId(@Param("countryId") String countryId,
            @Param("languageId") Integer languageId);

    @Query("SELECT st FROM StateTranslation st WHERE st.id.stateId = :stateId")
    List<StateTranslation> findByStateId(@Param("stateId") String stateId);

    @Query("SELECT st FROM StateTranslation st WHERE st.id.languageId = :languageId")
    List<StateTranslation> findByLanguageId(@Param("languageId") Integer languageId);
}
