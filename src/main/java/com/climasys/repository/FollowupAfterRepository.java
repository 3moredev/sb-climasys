package com.climasys.repository;

import com.climasys.entity.FollowupAfter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for FollowupAfter entity
 * Provides data access methods for follow-up after period master data
 */
@Repository
public interface FollowupAfterRepository extends JpaRepository<FollowupAfter, Integer> {
    
    /**
     * Find all follow-up after periods ordered by ID
     * @return List of all follow-up after periods
     */
    @Query("SELECT fa FROM FollowupAfter fa ORDER BY fa.id")
    List<FollowupAfter> findAllActiveOrdered();
    
    /**
     * Find all follow-up after periods ordered by ID
     * @return List of all follow-up after periods
     */
    @Query("SELECT fa FROM FollowupAfter fa ORDER BY fa.id")
    List<FollowupAfter> findAllOrdered();
    
    /**
     * Find follow-up after period by description
     * @param followupAfter Follow-up after description
     * @return FollowupAfter if found
     */
    @Query("SELECT fa FROM FollowupAfter fa WHERE fa.followupAfter = :followupAfter")
    FollowupAfter findByFollowupAfter(@Param("followupAfter") String followupAfter);
    
    /**
     * Check if follow-up after period exists by description
     * @param followupAfter Follow-up after description
     * @return True if exists
     */
    @Query("SELECT COUNT(fa) > 0 FROM FollowupAfter fa WHERE fa.followupAfter = :followupAfter")
    boolean existsByFollowupAfter(@Param("followupAfter") String followupAfter);
    
    /**
     * Find follow-up after periods by number of days
     * @param days Number of days
     * @return List of matching follow-up after periods
     */
    List<FollowupAfter> findByDays(Integer days);
}
