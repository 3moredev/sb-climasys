package com.climasys.repository;

import com.climasys.entity.PatientVisit;
import com.climasys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<PatientVisit, Long> {
    Optional<PatientVisit> findByPatientVisitNo(Integer patientVisitNo);
    boolean existsByPatientVisitNo(Integer patientVisitNo);
    
    // Find future appointments for a doctor
    @Query("SELECT pv FROM PatientVisit pv WHERE pv.doctorId = :doctorId AND pv.visitDate >= :date")
    List<PatientVisit> findByDoctorIdAndVisitDateAfter(@Param("doctorId") String doctorId, @Param("date") String date);
    
    // Find appointments for a doctor on a specific date
    @Query("SELECT pv FROM PatientVisit pv WHERE pv.doctorId = :doctorId AND pv.visitDate = :date")
    List<PatientVisit> findByDoctorIdAndVisitDate(@Param("doctorId") String doctorId, @Param("date") String date);
}
