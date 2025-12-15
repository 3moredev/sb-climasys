package com.climasys.repository;

import com.climasys.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, String> {

    @Query("SELECT COUNT(DISTINCT c.clinicId) FROM Clinic c")
    long countUniqueClinics();

    void deleteByClinicId(String clinicId);

    List<Clinic> findByClinicId(String clinicId);
}
