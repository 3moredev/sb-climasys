package com.climasys.auth.repository;

import com.climasys.entity.Clinic;
import com.climasys.entity.ClinicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicMasterRepository extends JpaRepository<Clinic, ClinicId> {
    
    Optional<Clinic> findByClinicIdAndIsPrint(String clinicId, Boolean isPrint);
    
    List<Clinic> findByDoctorId(String doctorId);
    
    @Query("SELECT c FROM Clinic c " +
           "JOIN UserRole ur ON c.clinicId = ur.clinicId " +
           "JOIN User u ON ur.userId = u.id " +
           "WHERE u.loginId = :loginId AND c.isPrint = true AND ur.isDefaultClinic = true")
    List<Clinic> findDefaultClinicsByLoginId(@Param("loginId") String loginId);
}
