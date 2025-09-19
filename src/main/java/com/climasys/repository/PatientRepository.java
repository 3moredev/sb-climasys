package com.climasys.repository;

import com.climasys.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByFolderNo(String folderNo);
    boolean existsByFolderNo(String folderNo);
    boolean existsByEmailId(String emailId);
}
