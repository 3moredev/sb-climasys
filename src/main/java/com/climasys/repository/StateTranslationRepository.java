package com.climasys.repository;

import com.climasys.entity.StateTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateTranslationRepository extends JpaRepository<StateTranslation, Object> {
    
    @Query("SELECT st FROM StateTranslation st WHERE st.stateId = :stateId AND st.languageId = :languageId")
    StateTranslation findByStateIdAndLanguageId(@Param("stateId") String stateId, @Param("languageId") Integer languageId);
    
    @Query("SELECT st FROM StateTranslation st WHERE st.stateId = :stateId")
    List<StateTranslation> findByStateId(@Param("stateId") String stateId);
}
