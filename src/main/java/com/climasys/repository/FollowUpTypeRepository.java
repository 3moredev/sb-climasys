package com.climasys.repository;

import com.climasys.entity.FollowUpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowUpTypeRepository extends JpaRepository<FollowUpType, Short> {
    
    @Query("SELECT f FROM FollowUpType f WHERE f.id = :id AND (f.deleteFlag = false OR f.deleteFlag IS NULL)")
    Optional<FollowUpType> findByIdAndActive(@Param("id") Short id);
    
    @Query("SELECT f FROM FollowUpType f WHERE (f.deleteFlag = false OR f.deleteFlag IS NULL)")
    List<FollowUpType> findAllActive();
    
    @Query("SELECT f FROM FollowUpType f WHERE f.followUpCode = :followUpCode AND (f.deleteFlag = false OR f.deleteFlag IS NULL)")
    Optional<FollowUpType> findByFollowUpCodeAndActive(@Param("followUpCode") String followUpCode);
}
