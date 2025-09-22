package com.climasys.repository;

import com.climasys.entity.ReferBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReferByRepository extends JpaRepository<ReferBy, String> {
    
    @Query("SELECT rb FROM ReferBy rb ORDER BY rb.id")
    List<ReferBy> findAllOrdered();
}
