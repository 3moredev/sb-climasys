package com.climasys.auth.repository;

import com.climasys.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicMasterRepository extends JpaRepository<Clinic, String> {

    Optional<Clinic> findByClinicIdAndIsPrint(String clinicId, Boolean isPrint);

    @Query("SELECT c FROM Clinic c " +
            "JOIN UserRole ur ON c.clinicId = ur.clinicId " +
            "WHERE ur.doctorId = :doctorId AND c.isPrint = true AND ur.isDefaultClinic = true")
    Optional<Clinic> findClinicByDoctorId(@Param("doctorId") String doctorId);

    @Query("SELECT c FROM Clinic c " +
            "JOIN UserRole ur ON c.clinicId = ur.clinicId " +
            "JOIN User u ON ur.userId = u.id " +
            "WHERE u.loginId = :loginId AND c.isPrint = true AND ur.isDefaultClinic = true")
    List<Clinic> findDefaultClinicsByLoginId(@Param("loginId") String loginId);
}
