package com.climasys.repository;

import com.climasys.entity.TitleMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for TitleMaster entity
 * Provides data access methods for title master data
 */
@Repository
public interface TitleMasterRepository extends JpaRepository<TitleMaster, Integer> {
    
    /**
     * Find all active titles ordered by ID
     * @return List of active titles
     */
    @Query("SELECT tm FROM TitleMaster tm WHERE (tm.deleteFlag = false OR tm.deleteFlag IS NULL) ORDER BY tm.id")
    List<TitleMaster> findAllActiveOrdered();
    
    /**
     * Find all titles ordered by ID (including inactive)
     * @return List of all titles
     */
    @Query("SELECT tm FROM TitleMaster tm ORDER BY tm.id")
    List<TitleMaster> findAllOrdered();
    
    /**
     * Find title by description
     * @param description Title description
     * @return TitleMaster if found
     */
    TitleMaster findByTitleDescription(String description);
    
    /**
     * Check if title exists by description
     * @param description Title description
     * @return True if exists
     */
    boolean existsByTitleDescription(String description);
}
