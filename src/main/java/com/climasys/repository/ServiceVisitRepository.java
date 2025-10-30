package com.climasys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository focused on Services flow (separate from OPD visits).
 * Provides minimal native queries equivalent to legacy SPs used by WebForms:
 * - USP_Get_PatientVisitDatesForServices
 * - Part of USP_Get_MasterLists_Services related to previously selected service items
 */
@Repository
public interface ServiceVisitRepository extends JpaRepository<com.climasys.entity.PatientVisit, String> {

    /**
     * Previous completed services visits for a patient, sorted newest first.
     * Returns rows with visit_date, shift_id, patient_visit_no.
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
              AND pvs.status_id IN (
                    SELECT sr.id
                    FROM status_ref sr
                    WHERE sr.doctor_id = :doctorId
                      AND sr.clinic_id = :clinicId
                      AND sr.status_description = 'Service Completed'
                )
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
     * Previously selected service line-items for a given services visit.
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
              AND doctor_id = :doctorId
              AND clinic_id = :clinicId
              AND shift_id = :shiftId
              AND patient_visit_no = :visitNo
              AND CAST(visit_date AS date) = CAST(:visitDate AS date)
              AND COALESCE(delete_flag, false) = false
            ORDER BY billing_group_name, billing_subgroup_name, billing_details
            """, nativeQuery = true)
    List<Object[]> findServiceVisitLineItems(
            @Param("patientId") String patientId,
            @Param("doctorId") String doctorId,
            @Param("clinicId") String clinicId,
            @Param("shiftId") Short shiftId,
            @Param("visitNo") Integer visitNo,
            @Param("visitDate") LocalDate visitDate
    );
}


