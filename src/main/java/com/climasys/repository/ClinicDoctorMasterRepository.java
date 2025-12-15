package com.climasys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.climasys.entity.ClinicDoctorMaster;
import com.climasys.entity.ClinicDoctorMasterId;

@Repository
public interface ClinicDoctorMasterRepository extends JpaRepository<ClinicDoctorMaster, ClinicDoctorMasterId> {
    ClinicDoctorMaster findByDoctorId(String doctorId);

    @Modifying
    @Transactional
    @Query("UPDATE ClinicDoctorMaster c SET c.clinicId = ?1 WHERE c.doctorId = ?2")
    void updateClinicIdByDoctorId(String clinicId, String doctorId);
}
