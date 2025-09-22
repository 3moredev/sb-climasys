package com.climasys.repository;

import com.climasys.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Object> {
    
    @Query("SELECT c FROM City c WHERE c.stateId = :stateId ORDER BY c.id")
    List<City> findByStateId(@Param("stateId") String stateId);
    
    @Query("SELECT c FROM City c ORDER BY c.id")
    List<City> findAllOrdered();
}
