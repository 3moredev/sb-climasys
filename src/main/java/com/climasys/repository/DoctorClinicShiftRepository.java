package com.climasys.repository;

import com.climasys.entity.DoctorClinicShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorClinicShiftRepository extends JpaRepository<DoctorClinicShift, Object> {
    
    @Query("SELECT dcs FROM DoctorClinicShift dcs WHERE dcs.clinicId = :clinicId")
    List<DoctorClinicShift> findByClinicId(@Param("clinicId") String clinicId);
    
    @Query("SELECT dcs FROM DoctorClinicShift dcs WHERE dcs.clinicId = :clinicId AND dcs.doctorId = :doctorId")
    List<DoctorClinicShift> findByClinicIdAndDoctorId(@Param("clinicId") String clinicId, @Param("doctorId") String doctorId);
    
    @Query("SELECT dcs FROM DoctorClinicShift dcs WHERE dcs.clinicId = :clinicId AND dcs.doctorId = :doctorId AND dcs.shiftId IN " +
           "(SELECT s.shiftId FROM Shift s WHERE s.shiftDay = :day)")
    List<DoctorClinicShift> findByClinicIdAndDoctorIdAndDay(@Param("clinicId") String clinicId, @Param("doctorId") String doctorId, @Param("day") String day);
}
