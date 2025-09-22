package com.climasys.repository;

import com.climasys.entity.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodGroupRepository extends JpaRepository<BloodGroup, Integer> {
    
    @Query("SELECT bg FROM BloodGroup bg ORDER BY bg.id")
    List<BloodGroup> findAllOrdered();
}
