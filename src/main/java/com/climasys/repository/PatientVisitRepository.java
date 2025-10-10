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
            -- Complaints from complaints table
            COALESCE((
                SELECT STRING_AGG(vc.complaint_description, ', ')
                FROM visit_complaints vc
                WHERE vc.patient_id = pv.patient_id
                  AND vc.visit_date = pv.visit_date
                  AND vc.patient_visit_no = pv.patient_visit_no
                  AND vc.doctor_id = pv.doctor_id
                  AND vc.clinic_id = pv.clinic_id
                  AND vc.delete_flag = false
            ), '') AS complaints,
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
}

