package com.climasys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.climasys.entity.PatientVisitId;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository focused on Services flow (separate from OPD visits).
 * Provides minimal native queries equivalent to legacy SPs used by WebForms:
 * - USP_Get_PatientVisitDatesForServices
 * - Part of USP_Get_MasterLists_Services related to previously selected service items
 */
@Repository
public interface ServiceVisitRepository extends JpaRepository<com.climasys.entity.PatientVisit, PatientVisitId> {

    /**
     * Previous completed services visits for a patient, sorted newest first.
     * Returns rows with visit_date, shift_id, patient_visit_no.
     * Uses status_id = 8 for "Service Completed" status (consistent with FeeDetailsRepository).
     * Requires doctorId filter.
     */
    @Query(value = """
            SELECT CAST(pvs.visit_date AS date)            AS visit_date,
                   pvs.shift_id                           AS shift_id,
                   pvs.patient_visit_no                   AS patient_visit_no
            FROM patient_visits_services pvs
            WHERE pvs.patient_id = :patientId
              AND pvs.doctor_id = :doctorId
              AND pvs.clinic_id = :clinicId
              AND COALESCE(pvs.delete_flag, false) = false
              AND pvs.status_id = 8
              AND CAST(pvs.visit_date AS date) <= CAST(:todaysVisitDate AS date)
            ORDER BY CAST(pvs.visit_date AS date) DESC, pvs.visit_time DESC
            """, nativeQuery = true)
    List<Object[]> findPreviousServiceVisitDates(
            @Param("patientId") String patientId,
            @Param("doctorId") String doctorId,
            @Param("clinicId") String clinicId,
            @Param("todaysVisitDate") LocalDate todaysVisitDate
    );
    
    /**
     * Previous completed services visits for a patient without doctor filter, sorted newest first.
     * Returns rows with visit_date, shift_id, patient_visit_no.
     * Uses status_id = 8 for "Service Completed" status.
     * This method is used when doctorId is not provided.
     */
    @Query(value = """
            SELECT CAST(pvs.visit_date AS date)            AS visit_date,
                   pvs.shift_id                           AS shift_id,
                   pvs.patient_visit_no                   AS patient_visit_no
            FROM patient_visits_services pvs
            WHERE pvs.patient_id = :patientId
              AND pvs.clinic_id = :clinicId
              AND COALESCE(pvs.delete_flag, false) = false
              AND pvs.status_id = 8
              AND CAST(pvs.visit_date AS date) <= CAST(:todaysVisitDate AS date)
            ORDER BY CAST(pvs.visit_date AS date) DESC, pvs.visit_time DESC
            """, nativeQuery = true)
    List<Object[]> findPreviousServiceVisitDatesWithoutDoctor(
            @Param("patientId") String patientId,
            @Param("clinicId") String clinicId,
            @Param("todaysVisitDate") LocalDate todaysVisitDate
    );

    /**
     * Check if service visit billing info exists in overwrite table.
     * Matches stored procedure logic: checks Patient_ID, Clinic_ID, Patient_Visit_No only.
     * Uses EXISTS for efficient boolean check.
     */
    @Query(value = """
            SELECT EXISTS(
                SELECT 1
                FROM patient_visit_services_billinginfooverwrite
                WHERE patient_id = :patientId
                  AND clinic_id = :clinicId
                  AND patient_visit_no = :visitNo
            )
            """, nativeQuery = true)
    boolean existsInBillingInfoOverwrite(
            @Param("patientId") String patientId,
            @Param("clinicId") String clinicId,
            @Param("visitNo") Integer visitNo
    );

    /**
     * Previously selected service line-items from OVERWRITE table.
     * Matches stored procedure: uses Patient_ID, Clinic_ID, Patient_Visit_No, Delete_Flag.
     * Returns group, subgroup, details, default_fees, collected_fees.
     */
    @Query(value = """
            SELECT billing_group_name,
                   billing_subgroup_name,
                   billing_details,
                   COALESCE(collected_fees, default_fees) AS amount,
                   default_fees,
                   collected_fees
            FROM patient_visit_services_billinginfooverwrite
            WHERE patient_id = :patientId
              AND clinic_id = :clinicId
              AND patient_visit_no = :visitNo
              AND (delete_flag IS NULL OR delete_flag = false)
            ORDER BY billing_group_name, billing_subgroup_name, billing_details
            """, nativeQuery = true)
    List<Object[]> findServiceVisitLineItemsFromOverwrite(
            @Param("patientId") String patientId,
            @Param("clinicId") String clinicId,
            @Param("visitNo") Integer visitNo
    );

    /**
     * Previously selected service line-items from BASE table (fallback).
     * Matches stored procedure: uses Patient_ID, Clinic_ID, Patient_Visit_No only.
     * Returns group, subgroup, details, default_fees, collected_fees.
     */
    @Query(value = """
            SELECT billing_group_name,
                   billing_subgroup_name,
                   billing_details,
                   COALESCE(collected_fees, default_fees) AS amount,
                   default_fees,
                   collected_fees
            FROM patient_visit_services_billinginfo
            WHERE patient_id = :patientId
              AND clinic_id = :clinicId
              AND patient_visit_no = :visitNo
            ORDER BY billing_group_name, billing_subgroup_name, billing_details
            """, nativeQuery = true)
    List<Object[]> findServiceVisitLineItemsFromBase(
            @Param("patientId") String patientId,
            @Param("clinicId") String clinicId,
            @Param("visitNo") Integer visitNo
    );
}


