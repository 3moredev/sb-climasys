package com.climasys.repository;

import com.climasys.entity.ReferByTranslation;
import com.climasys.entity.ReferByTranslationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReferByTranslationRepository extends JpaRepository<ReferByTranslation, ReferByTranslationId> {
    
    @Query("SELECT DISTINCT rbt FROM ReferByTranslation rbt WHERE rbt.id.languageId = :languageId ORDER BY rbt.id.referId")
    List<ReferByTranslation> findByLanguageId(@Param("languageId") Integer languageId);
}
