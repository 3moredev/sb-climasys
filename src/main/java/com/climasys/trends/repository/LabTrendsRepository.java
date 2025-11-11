package com.climasys.trends.repository;

import com.climasys.entity.PatientVisitLabTestResult;
import com.climasys.entity.PatientVisitLabTestResultId;
import com.climasys.trends.dto.LabTrend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Lab Trends
 * Provides queries for retrieving lab test results and trends
 */
@Repository
public interface LabTrendsRepository extends JpaRepository<PatientVisitLabTestResult, PatientVisitLabTestResultId> {
    
    /**
     * Get all previous lab test results for a patient (all dates)
     * Replicates USP_Get_LabTestDetails12 stored procedure
     * Returns all lab test results for the patient across all visit dates
     * Matches the Lab Trend popup behavior
     * Filtered by clinic_id for multi-clinic isolation
     */
    @Query(value = """
        SELECT 
            CAST(pvl.visit_date AS date) AS visitDate,
            pvl.patient_visit_no AS patientVisitNo,
            pvl.lab_test_description AS labTestDescription,
            pvl.parameter_name AS parameterName,
            pvl.test_parameter_value AS parameterValue,
            pvl.doctor_name AS doctorName,
            pvl.lab_name AS labName,
            pvl.report_date AS reportDate,
            (pm.first_name || ' ' || COALESCE(pm.middle_name, '') || ' ' || COALESCE(pm.last_name, '')) AS patientFullName,
            pvl.comment AS comment,
            NULL AS patientLastVisitNo
        FROM patient_visit_labtestresults pvl
        INNER JOIN patient_master pm 
            ON pvl.patient_id = pm.id
        WHERE pvl.patient_id = :patientId
          AND pvl.clinic_id = :clinicId
          AND pvl.delete_flag = false
        ORDER BY pvl.visit_date DESC, pvl.patient_visit_no DESC
        """, nativeQuery = true)
    List<LabTrend> findAllLabTrendsForPatient(
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId
    );
    
    /**
     * Get previous lab test results for a specific patient visit (date-specific)
     * Replicates USP_Get_PreviousLabReports stored procedure
     * Returns lab test results for a specific visit date
     */
    @Query(value = """
        SELECT 
            CAST(pvl.visit_date AS date) AS visitDate,
            pvl.patient_visit_no AS patientVisitNo,
            pvl.lab_test_description AS labTestDescription,
            pvl.parameter_name AS parameterName,
            pvl.test_parameter_value AS parameterValue,
            pvl.doctor_name AS doctorName,
            pvl.lab_name AS labName,
            pvl.report_date AS reportDate,
            (pm.first_name || ' ' || COALESCE(pm.middle_name, '') || ' ' || COALESCE(pm.last_name, '')) AS patientFullName,
            pvl.comment AS comment,
            pv.patient_last_visit_no AS patientLastVisitNo
        FROM patient_visit_labtestresults pvl
        INNER JOIN patient_visits pv 
            ON pvl.doctor_id = pv.doctor_id 
            AND pvl.patient_id = pv.patient_id 
            AND pvl.patient_visit_no = pv.patient_visit_no
            AND pvl.visit_date = pv.visit_date
            AND pvl.clinic_id = pv.clinic_id
            AND pvl.shift_id = pv.shift_id
        INNER JOIN patient_master pm 
            ON pvl.patient_id = pm.id
        WHERE pvl.patient_id = :patientId
          AND pvl.doctor_id = :doctorId
          AND pvl.clinic_id = :clinicId
          AND CAST(pvl.visit_date AS date) = :visitDate
          AND pvl.shift_id = :shiftId
          AND pvl.patient_visit_no = :patientVisitNo
          AND pvl.delete_flag = false
        ORDER BY pvl.lab_test_description, pvl.parameter_name, pvl.test_parameter_value ASC
        """, nativeQuery = true)
    List<LabTrend> findPreviousLabReports(
        @Param("patientId") String patientId,
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId,
        @Param("visitDate") LocalDate visitDate,
        @Param("shiftId") Short shiftId,
        @Param("patientVisitNo") Integer patientVisitNo
    );
}



