package com.climasys.repository;

import com.climasys.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, Object> {
    
    @Query("SELECT s FROM State s WHERE s.countryId = :countryId ORDER BY s.id")
    List<State> findByCountryId(@Param("countryId") String countryId);
    
    @Query("SELECT s FROM State s ORDER BY s.id")
    List<State> findAllOrdered();
}
