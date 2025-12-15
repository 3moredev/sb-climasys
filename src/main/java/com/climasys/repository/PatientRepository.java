package com.climasys.repository;

import com.climasys.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    Optional<Patient> findByFolderNo(String folderNo);
    boolean existsByFolderNo(String folderNo);
    boolean existsByEmailId(String emailId);
    
    @Query("SELECT COUNT(p.id) FROM Patient p WHERE p.areaId = :areaId")
    Long countByAreaId(@Param("areaId") Integer areaId);
}
