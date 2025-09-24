package com.climasys.auth.repository;

import com.climasys.auth.entity.AuthDoctorMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthDoctorMasterRepository extends JpaRepository<AuthDoctorMaster, String> {
    
    // Simple method to find doctor by ID (since isActive column doesn't exist in database)
    Optional<AuthDoctorMaster> findByDoctorId(String doctorId);
}