package com.climasys.repository;

import com.climasys.entity.PatientVisit;
import com.climasys.entity.PatientVisitId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
     * Find the last completed visit for a patient (most recent with status 5)
     * This matches the stored procedure logic that only returns completed visits
     */
    Optional<PatientVisit> findFirstByPatientIdAndDeleteFlagAndStatusIdOrderByVisitDateDesc(
        String patientId, 
        Boolean deleteFlag,
        Short statusId
    );
    
    /**
     * Get previous date data for a patient (USP_Get_PrevDateData equivalent)
     * Finds the last completed visit (status 5) before or equal to today's visit
     * Using List<Object[]> to avoid JPA Map mapping issues
     */
    @Query(value = """
        WITH LastVisitDate AS (
            SELECT patient_id,
                   CAST(visit_date AS timestamp) + CAST(visit_time AS interval) AS last_visit_date
            FROM patient_visits pv
            WHERE CAST(pv.visit_date AS date) = CAST(:todaysVisitDate AS date)
              AND pv.shift_id = :shiftId
              AND pv.clinic_id = :clinicId
              AND pv.delete_flag = false
              AND pv.patient_id = :patientId
              AND pv.patient_visit_no = :patientVisitNo
        ),
        PreviousVisits AS (
            SELECT ROW_NUMBER() OVER (PARTITION BY pv.patient_id ORDER BY pv.visit_date DESC, pv.visit_time DESC) AS rownum,
                   pv.patient_id,
                   pv.doctor_id,
                   pv.patient_visit_no,
                   pv.visit_date,
                   pv.visit_time,
                   pv.shift_id
            FROM patient_visits pv
            LEFT JOIN LastVisitDate lv ON pv.patient_id = lv.patient_id
            WHERE (CAST(pv.visit_date AS timestamp) + CAST(pv.visit_time AS interval)) <= lv.last_visit_date
              AND pv.status_id = 5
              AND pv.delete_flag = false
        )
        SELECT pv.weight_in_kgs,
               pv.height_in_cms,
               pv.pulse,
               pv.blood_pressure,
               COALESCE(pv.asthama, false) AS asthama,
               COALESCE(pv.hypertension, false) AS hypertension,
               COALESCE(pv.diabetes, false) AS diabetes,
               COALESCE(pv.cholestrol, false) AS cholestrol,
               COALESCE(pv.ihd, false) AS ihd,
               COALESCE(pv.th, false) AS th,
               pv.instructions,
               pv.fees_to_collect,
               pv.patient_visit_no,
               pv.status_id,
               COALESCE(pv.smoking, false) AS smoking,
               COALESCE(pv.tobaco, false) AS tobaco,
               COALESCE(pv.alchohol, false) AS alchohol,
               pv.habits_comments,
               pv.allergy_dtls,
               pv.observation,
               pv.symptom_comment,
               COALESCE(pv.thtext, '') AS thtext,
               COALESCE(pv.sugar, '') AS sugar,
               COALESCE(pv.current_medicines, '') AS current_medicines,
               COALESCE(pv.visit_comments, '') AS visit_comments,
               COALESCE(pv.current_complaints, '') AS current_complaints,
               COALESCE(pv.fmp, '') AS fmp,
               COALESCE(pv.prmc, '') AS prmc,
               COALESCE(pv.pamc, '') AS pamc,
               COALESCE(pv.lmp, '') AS lmp,
               COALESCE(pv.obstetrics_history, '') AS obstetrics_history,
               COALESCE(pv.surgical_history_past_history, '') AS surgical_history_past_history,
               COALESCE(pv.gynec_additional_comments, '') AS gynec_additional_comments,
               pv.edd,
               COALESCE(pv.pregnant, false) AS pregnant,
               pv.visit_date AS prev_visit_date,
               pv.visit_time AS prev_visit_time,
               pv.doctor_id AS prev_doctor_id
        FROM patient_visits pv
        INNER JOIN PreviousVisits pvisit 
            ON pv.patient_visit_no = pvisit.patient_visit_no 
            AND pv.patient_id = pvisit.patient_id
            AND pv.shift_id = pvisit.shift_id
        WHERE pv.patient_id = :patientId
          AND pv.delete_flag = false
          AND pvisit.rownum = 1
        """, nativeQuery = true)
    List<Object[]> getPreviousDateDataRaw(
        @Param("patientId") String patientId,
        @Param("todaysVisitDate") java.time.LocalDate todaysVisitDate,
        @Param("shiftId") Short shiftId,
        @Param("clinicId") String clinicId,
        @Param("patientVisitNo") Integer patientVisitNo
    );
    
    /**
     * Find a specific visit by patient ID, clinic ID, and visit number
     */
    Optional<PatientVisit> findFirstByPatientIdAndClinicIdAndPatientVisitNoAndDeleteFlag(
        String patientId,
        String clinicId,
        Integer patientVisitNo,
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
    
    /**
     * Find a patient visit by composite key without date (for lab test results where lab test date may differ from visit date)
     * Returns the most recent visit matching the composite key
     */
    @Query("SELECT pv FROM PatientVisit pv WHERE pv.patientId = :patientId " +
           "AND pv.doctorId = :doctorId " +
           "AND pv.clinicId = :clinicId " +
           "AND pv.shiftId = :shiftId " +
           "AND pv.patientVisitNo = :patientVisitNo " +
           "AND pv.deleteFlag = false " +
           "ORDER BY pv.visitDate DESC")
    Optional<PatientVisit> findFirstByCompositeKeyWithoutDate(
        @Param("patientId") String patientId,
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId,
        @Param("shiftId") Short shiftId,
        @Param("patientVisitNo") Integer patientVisitNo
    );
    
    /**
     * Find patient visits with comprehensive data including prescriptions, complaints, diagnosis
     * This replicates the USP_Get_Patient_Previous_Visits stored procedure logic
     */
    @Query(value = """
        SELECT DISTINCT 
            pv.patient_id,
            pv.doctor_id,
            pv.clinic_id,
            pv.shift_id,
            pv.patient_visit_no,
            pv.visit_date,
            pv.visit_time,
            pv.status_id,
            pv.instructions,
            pv.fees_to_collect,
            pv.fees_collected,
            pv.weight_in_kgs,
            pv.visit_comments,
            pv.observation,
            pv.pulse,
            pv.blood_pressure,
            pv.height_in_cms,
            pv.sugar,
            pv.thtext,
            pv.hypertension,
            pv.diabetes,
            pv.cholestrol,
            pv.ihd,
            pv.th,
            pv.asthama,
            pv.smoking,
            pv.tobaco,
            pv.alchohol,
            pv.current_complaints,
            pv.current_medicines,
            pv.important_findings,
            pv.additional_comments,
            pv.systemic,
            pv.odeama,
            pv.pallor,
            pv.gc,
            pv.follow_up,
            pv.is_follow_up,
            pv.follow_up_comment,
            pv.follow_up_date,
            pv.follow_up_type,
            pv.pregnant,
            pv.edd,
            pv.obstetrics_history,
            pv.surgical_history_past_history,
            pv.gynec_additional_comments,
            pv.fmp,
            pv.prmc,
            pv.pamc,
            pv.lmp,
            pv.discount,
            pv.original_discount,
            pv.is_submit_patient_visit_details,
            pv.refer_id,
            pv.refer_doctor_details,
            pv.addendum,
            pv.plan,
            pv.notes,
            pv.treatment_plan,
            pv.treatment_comment,
            pv.created_on,
            pv.createdby_name,
            pv.modified_on,
            pv.modifiedby_name,
            -- Additional fields for form requirements
            pv.allergy_dtls,
            pv.habits_comments,
            pv.in_person,
            pv.symptom_comment,
            pv.impression,
            pv.attended_by,
            pv.payment_by_id,
            pv.payment_remark,
            pv.attended_by_id,
            pv.tpr,
            pv.comment,
            pv.receipt_number,
            pv.receipt_type,
            pv.is_submit_patient_labtest,
            pv.complaints_by_patient_per_visit,
            pv.additional_instructions,
            pv.impression_finding,
            pv.followup_after,
            pv.schedule,
            pv.online_appointment_time,
            pv.doctor_address,
            pv.doctor_mobile,
            pv.doctor_email,
            pv.folder_no,
            pv.financial_year,
            pv.appointment_sr_no,
            pv.patient_last_visit_no,
            pv.on_call_status,
            pv.original_billed_amount,
            pv.offline_reason,
            pv.offline_flag,
            pv.doctor_notes,
            pv.cat_id,
            pv.from_time,
            -- Patient master fields for patient information
            pm.first_name,
            pm.middle_name,
            pm.last_name,
            pm.age_given,
            pm.date_of_birth,
            pm.mobile_1,
            pm.gender_id,
            pm.refer_id as patient_refer_id,
            -- Medicine names from prescription table
            COALESCE((
                SELECT STRING_AGG(vpo.medicine_name, ', ')
                FROM visit_prescription_overwrite vpo
                WHERE vpo.patient_id = pv.patient_id
                  AND vpo.visit_date = pv.visit_date
                  AND vpo.patient_visit_no = pv.patient_visit_no
                  AND vpo.doctor_id = pv.doctor_id
                  AND vpo.clinic_id = pv.clinic_id
                  AND vpo.delete_indicator = false
            ), '') AS medicine_names,
            -- Medicines from visit_medicine table (short_description) - comma separated
            COALESCE((
                SELECT STRING_AGG(vm.short_description, ', ')
                FROM visit_medicine_overwrite vm
                WHERE vm.patient_id = pv.patient_id
                  AND vm.visit_date = pv.visit_date
                  AND vm.patient_visit_no = pv.patient_visit_no
                  AND vm.doctor_id = pv.doctor_id
                  AND vm.clinic_id = pv.clinic_id
                  AND vm.shift_id = pv.shift_id
                  AND COALESCE(vm.delete_indicator, false) = false
                  AND COALESCE(vm.delete_flag, false) = false
                  AND vm.short_description IS NOT NULL
                  AND vm.short_description != ''
            ), COALESCE((
                SELECT STRING_AGG(vm2.short_description, ', ')
                FROM visit_medicine vm2
                WHERE vm2.patient_id = pv.patient_id
                  AND DATE(vm2.visit_date) = DATE(pv.visit_date)
                  AND vm2.patient_visit_no = pv.patient_visit_no
                  AND vm2.doctor_id = pv.doctor_id
                  AND vm2.clinic_id = pv.clinic_id
                  AND vm2.shift_id = pv.shift_id
                  AND COALESCE(vm2.delete_flag, false) = false
                  AND vm2.short_description IS NOT NULL
                  AND vm2.short_description != ''
                  AND NOT EXISTS (
                      SELECT 1 FROM visit_medicine_overwrite vm3
                      WHERE vm3.patient_id = vm2.patient_id
                        AND vm3.visit_date = vm2.visit_date
                        AND vm3.patient_visit_no = vm2.patient_visit_no
                        AND vm3.doctor_id = vm2.doctor_id
                        AND vm3.clinic_id = vm2.clinic_id
                        AND vm3.shift_id = vm2.shift_id
                        AND COALESCE(vm3.delete_indicator, false) = false
                  )
            ), '')) AS visit_medicines_short_description,
            -- Complaints from complaints table (description only)
            COALESCE((
                SELECT STRING_AGG(vc.complaint_description, ', ')
                FROM visit_complaints vc
                WHERE vc.patient_id = pv.patient_id
                  AND vc.visit_date = pv.visit_date
                  AND vc.patient_visit_no = pv.patient_visit_no
                  AND vc.doctor_id = pv.doctor_id
                  AND vc.clinic_id = pv.clinic_id
                  AND vc.delete_flag = false
                  AND vc.complaint_description IS NOT NULL 
                  AND vc.complaint_description != ''
            ), '') AS complaints,
            -- Complaint comments separately
            COALESCE((
                SELECT STRING_AGG(vc.complaint_comment, ', ')
                FROM visit_complaints vc
                WHERE vc.patient_id = pv.patient_id
                  AND vc.visit_date = pv.visit_date
                  AND vc.patient_visit_no = pv.patient_visit_no
                  AND vc.doctor_id = pv.doctor_id
                  AND vc.clinic_id = pv.clinic_id
                  AND vc.delete_flag = false
                  AND vc.complaint_comment IS NOT NULL 
                  AND vc.complaint_comment != ''
            ), '') AS complaint_comments,
            -- Diagnosis from diagnosis table
            COALESCE((
                SELECT STRING_AGG(vd.desease_description, ', ')
                FROM visit_diagnosis vd
                WHERE vd.patient_id = pv.patient_id
                  AND vd.visit_date = pv.visit_date
                  AND vd.patient_visit_no = pv.patient_visit_no
                  AND vd.doctor_id = pv.doctor_id
                  AND vd.clinic_id = pv.clinic_id
                  AND vd.delete_flag = false
            ), '') AS diagnosis,
            -- Doctor name from doctor master
            COALESCE(dm.prefix || ' ' || dm.first_name, '') AS doctor_name,
            -- Follow-up description
            COALESCE(fut.followup_description, '') AS followup_description,
            -- Lab test descriptions from lab test table
            COALESCE((
                SELECT STRING_AGG(pvla.lab_test_description, ', ')
                FROM patient_visit_labtestasked pvla
                WHERE pvla.patient_id = pv.patient_id
                  AND pvla.visit_date = pv.visit_date
                  AND pvla.patient_visit_no = pv.patient_visit_no
                  AND pvla.doctor_id = pv.doctor_id
                  AND pvla.clinic_id = pv.clinic_id
                  AND pvla.delete_flag = false
            ), '') AS lab_test_descriptions,
            -- PLR indicators (Prescription, Lab, Radiology)
            CASE WHEN EXISTS (
                SELECT 1 FROM visit_prescription_overwrite vpo2
                WHERE vpo2.patient_id = pv.patient_id
                  AND vpo2.visit_date = pv.visit_date
                  AND vpo2.patient_visit_no = pv.patient_visit_no
                  AND vpo2.doctor_id = pv.doctor_id
                  AND vpo2.clinic_id = pv.clinic_id
                  AND vpo2.delete_indicator = false
            ) THEN 'P' ELSE '' END ||
            CASE WHEN EXISTS (
                SELECT 1 FROM patient_visit_labtestasked pvla
                WHERE pvla.patient_id = pv.patient_id
                  AND pvla.visit_date = pv.visit_date
                  AND pvla.patient_visit_no = pv.patient_visit_no
                  AND pvla.doctor_id = pv.doctor_id
                  AND pvla.clinic_id = pv.clinic_id
                  AND pvla.delete_flag = false
            ) THEN 'L' ELSE '' END ||
            CASE WHEN EXISTS (
                SELECT 1 FROM visit_procedure_findings vpf
                WHERE vpf.patient_id = pv.patient_id
                  AND vpf.visit_date = pv.visit_date
                  AND vpf.patient_visit_no = pv.patient_visit_no
                  AND vpf.doctor_id = pv.doctor_id
                  AND vpf.clinic_id = pv.clinic_id
                  AND vpf.delete_flag = false
            ) THEN 'R' ELSE '' END AS plr_indicators
        FROM patient_visits pv
        LEFT JOIN patient_master pm ON pm.id = pv.patient_id
        LEFT JOIN doctor_master dm ON dm.doctor_id = pv.doctor_id
        LEFT JOIN follow_up_type fut ON fut.id = pv.follow_up_type
        WHERE pv.patient_id = :patientId
          AND pv.delete_flag = false
          AND pv.status_id = 5
          AND CAST(pv.visit_date AS date) <= :todaysDate
        ORDER BY pv.visit_date DESC, pv.visit_time DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findPatientPreviousVisitsWithDetails(
        @Param("patientId") String patientId,
        @Param("todaysDate") java.time.LocalDate todaysDate
    );

    /**
     * Find detailed prescription data for a specific visit
     * Returns all prescription fields including dose, instructions, etc.
     */
    @Query(value = """
        SELECT 
            vpo.medicine_name,
            vpo.brand_name,
            vpo.catsub_description,
            vpo.cat_short_name,
            vpo.marketed_by,
            vpo.morning,
            vpo.afternoon,
            vpo.night,
            vpo.no_of_days,
            vpo.instruction,
            vpo.sequence_id,
            vpo.created_on,
            vpo.createdby_name,
            vpo.modified_on,
            vpo.modifiedby_name
        FROM visit_prescription_overwrite vpo
        WHERE vpo.patient_id = :patientId
          AND vpo.visit_date = :visitDate
          AND vpo.patient_visit_no = :patientVisitNo
          AND vpo.doctor_id = :doctorId
          AND vpo.clinic_id = :clinicId
          AND vpo.delete_indicator = false
        ORDER BY vpo.sequence_id ASC, vpo.medicine_name ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findDetailedPrescriptionsForVisit(
        @Param("patientId") String patientId,
        @Param("visitDate") java.time.LocalDateTime visitDate,
        @Param("patientVisitNo") Integer patientVisitNo,
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId
    );

    /**
     * Find complaints for a specific visit from visit_complaints table
     * This matches the stored procedure logic that fetches from visit_complaints
     */
    @Query(value = """
        SELECT 
            vc.complaint_description,
            vc.complaint_comment,
            vc.created_on,
            vc.createdby_name,
            vc.modified_on,
            vc.modifiedby_name
        FROM visit_complaints vc
        WHERE vc.patient_id = :patientId
          AND vc.visit_date = :visitDate
          AND vc.patient_visit_no = :patientVisitNo
          AND vc.doctor_id = :doctorId
          AND vc.clinic_id = :clinicId
          AND vc.delete_flag = false
        ORDER BY vc.complaint_description ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findComplaintsForVisit(
        @Param("patientId") String patientId,
        @Param("visitDate") java.time.LocalDateTime visitDate,
        @Param("patientVisitNo") Integer patientVisitNo,
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId
    );
    
    /**
     * Find medicines for a specific visit from visit_medicine table
     * Prefers visit_medicine_overwrite if available, otherwise falls back to visit_medicine
     * Returns short_description which is used for patching medicines field in previous visits
     */
    @Query(value = """
        SELECT 
            vm.short_description,
            vm.medicine_description,
            vm.morning,
            vm.afternoon,
            vm.night,
            vm.no_of_days,
            vm.instruction,
            vm.created_on,
            vm.createdby_name,
            vm.modified_on,
            vm.modifiedby_name
        FROM visit_medicine_overwrite vm
        WHERE vm.patient_id = :patientId
          AND vm.visit_date = :visitDate
          AND vm.patient_visit_no = :patientVisitNo
          AND vm.doctor_id = :doctorId
          AND vm.clinic_id = :clinicId
          AND vm.shift_id = :shiftId
          AND COALESCE(vm.delete_indicator, false) = false
          AND COALESCE(vm.delete_flag, false) = false
        UNION ALL
        SELECT 
            vm2.short_description,
            vm2.medicine_description,
            vm2.morning,
            vm2.afternoon,
            vm2.night,
            vm2.no_of_days,
            vm2.instruction,
            vm2.created_on,
            vm2.createdby_name,
            vm2.modified_on,
            vm2.modifiedby_name
        FROM visit_medicine vm2
        WHERE vm2.patient_id = :patientId
          AND DATE(vm2.visit_date) = DATE(:visitDate)
          AND vm2.patient_visit_no = :patientVisitNo
          AND vm2.doctor_id = :doctorId
          AND vm2.clinic_id = :clinicId
          AND vm2.shift_id = :shiftId
          AND COALESCE(vm2.delete_flag, false) = false
          AND NOT EXISTS (
              SELECT 1 FROM visit_medicine_overwrite vm3
              WHERE vm3.patient_id = vm2.patient_id
                AND vm3.visit_date = vm2.visit_date
                AND vm3.patient_visit_no = vm2.patient_visit_no
                AND vm3.doctor_id = vm2.doctor_id
                AND vm3.clinic_id = vm2.clinic_id
                AND vm3.shift_id = vm2.shift_id
                AND vm3.short_description = vm2.short_description
                AND COALESCE(vm3.delete_indicator, false) = false
          )
        ORDER BY short_description ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findMedicinesForVisit(
        @Param("patientId") String patientId,
        @Param("visitDate") java.time.LocalDateTime visitDate,
        @Param("patientVisitNo") Integer patientVisitNo,
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId,
        @Param("shiftId") Short shiftId
    );
    
    /**
     * Find visits by patient ID, visit date (date only), and patient visit number
     * This matches the USP_Update_Addendum stored procedure logic
     * which uses Visit_Date (date), Patient_ID, and Patient_Visit_No
     */
    @Query("SELECT pv FROM PatientVisit pv WHERE pv.patientId = :patientId " +
           "AND CAST(pv.visitDate AS date) = :visitDate " +
           "AND pv.patientVisitNo = :patientVisitNo " +
           "AND pv.deleteFlag = false " +
           "ORDER BY pv.visitDate DESC")
    List<PatientVisit> findByPatientIdAndVisitDateAndPatientVisitNo(
        @Param("patientId") String patientId,
        @Param("visitDate") java.time.LocalDate visitDate,
        @Param("patientVisitNo") Integer patientVisitNo
    );
}

