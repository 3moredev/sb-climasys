package com.climasys.auth.repository;

import com.climasys.auth.entity.DoctorMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorMasterRepository extends JpaRepository<DoctorMaster, String> {
    
    // Simple method to find doctor by ID (since isActive column doesn't exist in database)
    Optional<DoctorMaster> findByDoctorId(String doctorId);
}
