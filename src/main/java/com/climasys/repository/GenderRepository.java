package com.climasys.repository;

import com.climasys.entity.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenderRepository extends JpaRepository<Gender, String> {
    
    @Query("SELECT g FROM Gender g ORDER BY g.id")
    List<Gender> findAllOrdered();
}
