package com.climasys.repository;

import com.climasys.entity.Clinic;
import com.climasys.entity.ClinicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, ClinicId> {

    @Query("SELECT c FROM Clinic c WHERE c.doctorId IN (SELECT MIN(c2.doctorId) FROM Clinic c2 GROUP BY c2.clinicId)")
    List<Clinic> findUniqueClinics();

    @Query("SELECT COUNT(DISTINCT c.clinicId) FROM Clinic c")
    long countUniqueClinics();

    void deleteByClinicId(String clinicId);

    List<Clinic> findByClinicId(String clinicId);
}
