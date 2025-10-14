package com.climasys.repository;

import com.climasys.entity.PatientDocumentTreatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for PatientDocumentTreatment entity
 */
@Repository
public interface PatientDocumentTreatmentRepository extends JpaRepository<PatientDocumentTreatment, Integer> {

    /**
     * Find all documents for a specific patient visit
     */
    @Query("SELECT pdt FROM PatientDocumentTreatment pdt WHERE pdt.patientId = :patientId " +
           "AND pdt.patientVisitNo = :visitNo AND pdt.deleteFlag = false " +
           "ORDER BY pdt.createdOn DESC")
    List<PatientDocumentTreatment> findByPatientIdAndVisitNo(
            @Param("patientId") String patientId,
            @Param("visitNo") Integer visitNo);

    /**
     * Find all documents for a patient
     */
    @Query("SELECT pdt FROM PatientDocumentTreatment pdt WHERE pdt.patientId = :patientId " +
           "AND pdt.deleteFlag = false ORDER BY pdt.createdOn DESC")
    List<PatientDocumentTreatment> findByPatientId(@Param("patientId") String patientId);

    /**
     * Find documents by patient, doctor and visit date
     */
    @Query("SELECT pdt FROM PatientDocumentTreatment pdt WHERE pdt.patientId = :patientId " +
           "AND pdt.doctorId = :doctorId AND pdt.visitDate = :visitDate " +
           "AND pdt.deleteFlag = false ORDER BY pdt.createdOn DESC")
    List<PatientDocumentTreatment> findByPatientDoctorAndVisitDate(
            @Param("patientId") String patientId,
            @Param("doctorId") String doctorId,
            @Param("visitDate") LocalDateTime visitDate);

    /**
     * Count documents for a specific visit
     */
    @Query("SELECT COUNT(pdt) FROM PatientDocumentTreatment pdt WHERE pdt.patientId = :patientId " +
           "AND pdt.patientVisitNo = :visitNo AND pdt.deleteFlag = false")
    Long countByPatientIdAndVisitNo(
            @Param("patientId") String patientId,
            @Param("visitNo") Integer visitNo);
}

