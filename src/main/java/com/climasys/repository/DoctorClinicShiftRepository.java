package com.climasys.repository;

import com.climasys.entity.DoctorClinicShift;
import com.climasys.entity.DoctorClinicShiftId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorClinicShiftRepository extends JpaRepository<DoctorClinicShift, DoctorClinicShiftId> {

    @Query("SELECT dcs FROM DoctorClinicShift dcs WHERE dcs.id.clinicId = :clinicId")
    List<DoctorClinicShift> findByClinicId(@Param("clinicId") String clinicId);

    @Query("SELECT dcs FROM DoctorClinicShift dcs WHERE dcs.id.clinicId = :clinicId AND dcs.id.doctorId = :doctorId")
    List<DoctorClinicShift> findByClinicIdAndDoctorId(@Param("clinicId") String clinicId,
            @Param("doctorId") String doctorId);

    @Query("SELECT dcs FROM DoctorClinicShift dcs WHERE dcs.id.clinicId = :clinicId AND dcs.id.doctorId = :doctorId AND dcs.id.shiftId IN "
            +
            "(SELECT s.shiftId FROM Shift s WHERE s.shiftDay = :day)")
    List<DoctorClinicShift> findByClinicIdAndDoctorIdAndDay(@Param("clinicId") String clinicId,
            @Param("doctorId") String doctorId, @Param("day") String day);

    @Query("SELECT dcs FROM DoctorClinicShift dcs WHERE dcs.id.doctorId = :doctorId")
    List<DoctorClinicShift> findByDoctorId(@Param("doctorId") String doctorId);

    /**
     * Check if a doctor is assigned to a clinic for a specific shift
     */
    boolean existsByIdDoctorIdAndIdClinicIdAndIdShiftId(String doctorId, String clinicId, Short shiftId);
}
