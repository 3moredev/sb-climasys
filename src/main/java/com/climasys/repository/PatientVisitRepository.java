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
     * Find the last visit for a patient (most recent)
     */
    Optional<PatientVisit> findFirstByPatientIdAndDeleteFlagOrderByVisitDateDesc(
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
    
    /**
     * Find a visit by composite key fields, comparing only the date part (ignoring time)
     */
    @Query("SELECT pv FROM PatientVisit pv WHERE pv.patientId = :patientId " +
           "AND pv.doctorId = :doctorId " +
           "AND pv.clinicId = :clinicId " +
           "AND pv.shiftId = :shiftId " +
           "AND pv.patientVisitNo = :patientVisitNo " +
           "AND CAST(pv.visitDate AS date) = :visitDate " +
           "AND pv.deleteFlag = false")
    Optional<PatientVisit> findByCompositeKeyAndDate(
        @Param("patientId") String patientId,
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId,
        @Param("shiftId") Short shiftId,
        @Param("patientVisitNo") Integer patientVisitNo,
        @Param("visitDate") java.time.LocalDate visitDate
    );
}

