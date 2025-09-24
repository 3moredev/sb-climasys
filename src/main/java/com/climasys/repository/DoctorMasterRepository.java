package com.climasys.repository;

import com.climasys.entity.DoctorMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorMasterRepository extends JpaRepository<DoctorMaster, String> {
    
    @Query("SELECT d FROM DoctorMaster d WHERE d.doctorId = :doctorId AND d.deleteFlag = false")
    Optional<DoctorMaster> findByDoctorIdAndActive(@Param("doctorId") String doctorId);
    
    @Query("SELECT d FROM DoctorMaster d WHERE d.deleteFlag = false OR d.deleteFlag IS NULL")
    List<DoctorMaster> findAllActive();
    
    @Query("SELECT d FROM DoctorMaster d WHERE d.speciality = :speciality AND (d.deleteFlag = false OR d.deleteFlag IS NULL)")
    List<DoctorMaster> findBySpecialityAndActive(@Param("speciality") String speciality);
}
