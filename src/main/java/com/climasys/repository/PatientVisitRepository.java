package com.climasys.repository;

import com.climasys.entity.PatientVisit;
import com.climasys.entity.PatientVisitId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientVisitRepository extends JpaRepository<PatientVisit, PatientVisitId> {
    
    /**
     * Find a patient visit by composite key fields
     */
    Optional<PatientVisit> findByPatientIdAndDoctorIdAndClinicIdAndShiftIdAndPatientVisitNoAndVisitDate(
        String patientId, 
        String doctorId, 
        String clinicId, 
        Short shiftId, 
        Integer patientVisitNo, 
        LocalDateTime visitDate
    );
    
    /**
     * Find all visits for a patient
     */
    List<PatientVisit> findByPatientIdAndDeleteFlagOrderByVisitDateDesc(
        String patientId, 
        Boolean deleteFlag
    );
    
    /**
     * Find visits by doctor and date
     */
    @Query("SELECT pv FROM PatientVisit pv WHERE pv.doctorId = :doctorId " +
           "AND pv.clinicId = :clinicId " +
           "AND CAST(pv.visitDate AS date) = CAST(:visitDate AS date) " +
           "AND pv.deleteFlag = false " +
           "ORDER BY pv.visitDate, pv.visitTime")
    List<PatientVisit> findVisitsByDoctorAndDate(
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId,
        @Param("visitDate") LocalDateTime visitDate
    );
    
    /**
     * Check if a visit exists
     */
    boolean existsByPatientIdAndDoctorIdAndClinicIdAndShiftIdAndPatientVisitNoAndVisitDate(
        String patientId,
        String doctorId,
        String clinicId,
        Short shiftId,
        Integer patientVisitNo,
        LocalDateTime visitDate
    );
}

