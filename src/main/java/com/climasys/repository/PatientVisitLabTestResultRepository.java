package com.climasys.repository;

import com.climasys.entity.PatientVisitLabTestResult;
import com.climasys.entity.PatientVisitLabTestResultId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA Repository for PatientVisitLabTestResult entity
 * Provides data access methods for lab test results
 */
@Repository
public interface PatientVisitLabTestResultRepository extends JpaRepository<PatientVisitLabTestResult, PatientVisitLabTestResultId> {

    /**
     * Find lab test results for a specific patient visit (exact date match)
     */
    @Query("SELECT p FROM PatientVisitLabTestResult p WHERE " +
           "p.patientId = :patientId AND " +
           "p.patientVisitNo = :patientVisitNo AND " +
           "p.shiftId = :shiftId AND " +
           "p.clinicId = :clinicId AND " +
           "p.doctorId = :doctorId AND " +
           "p.visitDate = :visitDate AND " +
           "p.deleteFlag = false")
    List<PatientVisitLabTestResult> findByPatientVisit(
            @Param("patientId") String patientId,
            @Param("patientVisitNo") Integer patientVisitNo,
            @Param("shiftId") Short shiftId,
            @Param("clinicId") String clinicId,
            @Param("doctorId") String doctorId,
            @Param("visitDate") LocalDateTime visitDate);
    
    /**
     * Find lab test results for a patient visit by composite key without exact date match
     * Used when lab test date may differ from visit date - returns results for the most recent visit matching the composite key
     */
    @Query("SELECT p FROM PatientVisitLabTestResult p WHERE " +
           "p.patientId = :patientId AND " +
           "p.patientVisitNo = :patientVisitNo AND " +
           "p.shiftId = :shiftId AND " +
           "p.clinicId = :clinicId AND " +
           "p.doctorId = :doctorId AND " +
           "p.deleteFlag = false " +
           "ORDER BY p.visitDate DESC")
    List<PatientVisitLabTestResult> findByPatientVisitWithoutExactDate(
            @Param("patientId") String patientId,
            @Param("patientVisitNo") Integer patientVisitNo,
            @Param("shiftId") Short shiftId,
            @Param("clinicId") String clinicId,
            @Param("doctorId") String doctorId);
    
    /**
     * Find lab test results using date comparison (native query for better date matching)
     * Uses CAST to compare dates at the database level, which handles timestamp precision issues
     */
    @Query(value = "SELECT * FROM patient_visit_labtestresults p WHERE " +
           "p.patient_id = :patientId AND " +
           "p.patient_visit_no = :patientVisitNo AND " +
           "p.shift_id = :shiftId AND " +
           "p.clinic_id = :clinicId AND " +
           "p.doctor_id = :doctorId AND " +
           "CAST(p.visit_date AS DATE) = CAST(:visitDate AS DATE) AND " +
           "p.delete_flag = false " +
           "ORDER BY p.lab_test_description, p.parameter_name", nativeQuery = true)
    List<PatientVisitLabTestResult> findByPatientVisitByDateOnly(
            @Param("patientId") String patientId,
            @Param("patientVisitNo") Integer patientVisitNo,
            @Param("shiftId") Short shiftId,
            @Param("clinicId") String clinicId,
            @Param("doctorId") String doctorId,
            @Param("visitDate") LocalDateTime visitDate);

    /**
     * Find lab test results for a specific patient (all visits)
     */
    @Query("SELECT p FROM PatientVisitLabTestResult p WHERE " +
           "p.patientId = :patientId AND " +
           "p.deleteFlag = false " +
           "ORDER BY p.visitDate DESC, p.patientVisitNo DESC")
    List<PatientVisitLabTestResult> findByPatientIdOrderByVisitDateDesc(@Param("patientId") String patientId);

    /**
     * Find distinct visit numbers for a patient
     */
    @Query("SELECT DISTINCT p.patientVisitNo FROM PatientVisitLabTestResult p WHERE " +
           "p.patientId = :patientId AND " +
           "p.deleteFlag = false " +
           "ORDER BY p.patientVisitNo DESC")
    List<Integer> findDistinctVisitNumbersByPatientId(@Param("patientId") String patientId);

    /**
     * Find lab test results by lab test description for a patient
     */
    @Query("SELECT p FROM PatientVisitLabTestResult p WHERE " +
           "p.patientId = :patientId AND " +
           "p.labTestDescription = :labTestDescription AND " +
           "p.deleteFlag = false " +
           "ORDER BY p.visitDate DESC")
    List<PatientVisitLabTestResult> findByPatientIdAndLabTestDescription(
            @Param("patientId") String patientId,
            @Param("labTestDescription") String labTestDescription);

    /**
     * Soft delete lab test results for a specific patient visit
     */
    @Modifying
    @Transactional
    @Query("UPDATE PatientVisitLabTestResult p SET p.deleteFlag = true, p.modifiedOn = :modifiedOn, p.modifiedbyName = :modifiedBy WHERE " +
           "p.patientId = :patientId AND " +
           "p.patientVisitNo = :patientVisitNo AND " +
           "p.shiftId = :shiftId AND " +
           "p.clinicId = :clinicId AND " +
           "p.doctorId = :doctorId AND " +
           "p.visitDate = :visitDate")
    int softDeleteByPatientVisit(
            @Param("patientId") String patientId,
            @Param("patientVisitNo") Integer patientVisitNo,
            @Param("shiftId") Short shiftId,
            @Param("clinicId") String clinicId,
            @Param("doctorId") String doctorId,
            @Param("visitDate") LocalDateTime visitDate,
            @Param("modifiedOn") LocalDateTime modifiedOn,
            @Param("modifiedBy") String modifiedBy);

    /**
     * Check if lab test results exist for a patient visit
     */
    @Query("SELECT COUNT(p) > 0 FROM PatientVisitLabTestResult p WHERE " +
           "p.patientId = :patientId AND " +
           "p.patientVisitNo = :patientVisitNo AND " +
           "p.shiftId = :shiftId AND " +
           "p.clinicId = :clinicId AND " +
           "p.doctorId = :doctorId AND " +
           "p.visitDate = :visitDate AND " +
           "p.deleteFlag = false")
    boolean existsByPatientVisit(
            @Param("patientId") String patientId,
            @Param("patientVisitNo") Integer patientVisitNo,
            @Param("shiftId") Short shiftId,
            @Param("clinicId") String clinicId,
            @Param("doctorId") String doctorId,
            @Param("visitDate") LocalDateTime visitDate);

    /**
     * Find lab test results with specific parameters for a patient visit
     */
    @Query("SELECT p FROM PatientVisitLabTestResult p WHERE " +
           "p.patientId = :patientId AND " +
           "p.patientVisitNo = :patientVisitNo AND " +
           "p.shiftId = :shiftId AND " +
           "p.clinicId = :clinicId AND " +
           "p.doctorId = :doctorId AND " +
           "p.visitDate = :visitDate AND " +
           "p.labTestDescription = :labTestDescription AND " +
           "p.parameterName = :parameterName AND " +
           "p.deleteFlag = false")
    PatientVisitLabTestResult findByPatientVisitAndTestParameter(
            @Param("patientId") String patientId,
            @Param("patientVisitNo") Integer patientVisitNo,
            @Param("shiftId") Short shiftId,
            @Param("clinicId") String clinicId,
            @Param("doctorId") String doctorId,
            @Param("visitDate") LocalDateTime visitDate,
            @Param("labTestDescription") String labTestDescription,
            @Param("parameterName") String parameterName);

    /**
     * Soft delete a specific lab test result parameter
     * Equivalent to USP_Delete_LabtestParameter stored procedure
     */
    @Modifying
    @Transactional
    @Query("UPDATE PatientVisitLabTestResult p SET p.deleteFlag = true, p.modifiedOn = :modifiedOn, p.modifiedbyName = :modifiedBy WHERE " +
           "p.patientId = :patientId AND " +
           "p.patientVisitNo = :patientVisitNo AND " +
           "p.shiftId = :shiftId AND " +
           "p.clinicId = :clinicId AND " +
           "p.doctorId = :doctorId AND " +
           "p.visitDate = :visitDate AND " +
           "p.labTestDescription = :labTestDescription AND " +
           "p.parameterName = :parameterName")
    int softDeleteByPatientVisitAndParameter(
            @Param("patientId") String patientId,
            @Param("patientVisitNo") Integer patientVisitNo,
            @Param("shiftId") Short shiftId,
            @Param("clinicId") String clinicId,
            @Param("doctorId") String doctorId,
            @Param("visitDate") LocalDateTime visitDate,
            @Param("labTestDescription") String labTestDescription,
            @Param("parameterName") String parameterName,
            @Param("modifiedOn") LocalDateTime modifiedOn,
            @Param("modifiedBy") String modifiedBy);

    /**
     * Check if a specific lab test result parameter exists
     */
    @Query("SELECT COUNT(p) > 0 FROM PatientVisitLabTestResult p WHERE " +
           "p.patientId = :patientId AND " +
           "p.patientVisitNo = :patientVisitNo AND " +
           "p.shiftId = :shiftId AND " +
           "p.clinicId = :clinicId AND " +
           "p.doctorId = :doctorId AND " +
           "p.visitDate = :visitDate AND " +
           "p.labTestDescription = :labTestDescription AND " +
           "p.parameterName = :parameterName AND " +
           "p.deleteFlag = false")
    boolean existsByPatientVisitAndParameter(
            @Param("patientId") String patientId,
            @Param("patientVisitNo") Integer patientVisitNo,
            @Param("shiftId") Short shiftId,
            @Param("clinicId") String clinicId,
            @Param("doctorId") String doctorId,
            @Param("visitDate") LocalDateTime visitDate,
            @Param("labTestDescription") String labTestDescription,
            @Param("parameterName") String parameterName);
}
