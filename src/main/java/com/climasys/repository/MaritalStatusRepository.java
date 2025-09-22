package com.climasys.repository;

import com.climasys.entity.MaritalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaritalStatusRepository extends JpaRepository<MaritalStatus, String> {
    
    @Query("SELECT ms FROM MaritalStatus ms ORDER BY ms.id")
    List<MaritalStatus> findAllOrdered();
}
