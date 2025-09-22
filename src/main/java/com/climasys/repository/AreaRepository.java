package com.climasys.repository;

import com.climasys.entity.Area;
import com.climasys.entity.AreaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, AreaId> {
    
    @Query("SELECT a FROM Area a WHERE a.isActivate = true ORDER BY a.id")
    List<Area> findAllActive();
    
    @Query("SELECT a FROM Area a WHERE a.id = :areaId AND a.isActivate = true")
    Area findByIdAndActive(@Param("areaId") Integer areaId);
    
    @Query("SELECT a FROM Area a WHERE a.isActivate = true AND " +
           "(CAST(a.id AS string) LIKE %:query% OR " +
           "EXISTS (SELECT at FROM AreaTranslation at WHERE at.areaId = a.id AND at.areaName LIKE %:query%))")
    List<Area> searchAreas(@Param("query") String query);
}
