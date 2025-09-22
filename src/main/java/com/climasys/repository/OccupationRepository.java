package com.climasys.repository;

import com.climasys.entity.Occupation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OccupationRepository extends JpaRepository<Occupation, Integer> {
    
    @Query("SELECT o FROM Occupation o ORDER BY o.id")
    List<Occupation> findAllOrdered();
}
