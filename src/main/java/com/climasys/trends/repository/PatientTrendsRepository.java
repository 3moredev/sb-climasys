package com.climasys.trends.repository;

import com.climasys.entity.PatientVisit;
import com.climasys.entity.PatientVisitId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PatientTrendsRepository extends JpaRepository<PatientVisit, PatientVisitId> {

    @Query(value = """
        WITH LastVisitDate AS (
            SELECT patient_id,
                   (CAST(visit_date AS timestamp) + CAST(COALESCE(visit_time, '00:00:00') AS time)) AS last_visit_datetime
            FROM patient_visits
            WHERE patient_id = :patientId
              AND shift_id = :shiftId
              AND clinic_id = :clinicId
              AND patient_visit_no = :patientVisitNo
              AND CAST(visit_date AS date) = :visitDate
              AND delete_flag = false
            LIMIT 1
        ),
        PreviousVisits AS (
            SELECT 
                pv.visit_date,
                pv.patient_id,
                pv.patient_visit_no,
                pv.status_id,
                pv.visit_time,
                pv.shift_id,
                ROW_NUMBER() OVER (
                    PARTITION BY pv.patient_id 
                    ORDER BY pv.visit_date DESC, pv.visit_time DESC
                ) AS rownum,
                LEFT(COALESCE(sm.description, ''), 1) AS shift_description,
                pv.blood_pressure,
                pv.sugar,
                pv.thtext,
                pv.weight_in_kgs,
                pv.pulse,
                pv.height_in_cms,
                pv.tpr,
                pv.important_findings,
                pv.additional_comments,
                pv.symptom_comment,
                pv.systemic,
                pv.odeama,
                pv.pallor,
                pv.gc
            FROM patient_visits pv
            INNER JOIN shift_master sm ON pv.shift_id = sm.shift_id
            LEFT JOIN LastVisitDate lv ON pv.patient_id = lv.patient_id
            WHERE pv.patient_id = :patientId
              AND (CAST(pv.visit_date AS timestamp) + CAST(COALESCE(pv.visit_time, '00:00:00') AS time)) < lv.last_visit_datetime
              AND pv.status_id = 5
              AND pv.delete_flag = false
        )
        SELECT 
            visit_date, patient_id, status_id, shift_description,
            blood_pressure, sugar, thtext, weight_in_kgs,
            pulse, height_in_cms, tpr, important_findings,
            additional_comments, symptom_comment, systemic, odeama, pallor, gc,
            patient_visit_no, visit_time, shift_id
        FROM PreviousVisits
        WHERE rownum <= 5
        ORDER BY visit_date DESC, rownum, visit_time DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findPreviousTrends(
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId,
        @Param("shiftId") Short shiftId,
        @Param("patientVisitNo") Integer patientVisitNo,
        @Param("visitDate") java.time.LocalDate visitDate
    );
}


