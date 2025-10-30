package com.climasys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Native-query repository to fetch fee details equivalent to USP_Get_Patient_FeesDetails
 */
@Repository
public interface FeeDetailsRepository extends JpaRepository<com.climasys.entity.Patient, String> {

    /**
     * Returns combined fee rows from patient_visits (status 5), patient_visits_services (status 8),
     * and patient_payments_adhoc, closely matching the stored procedure logic.
     *
     * Columns returned (in order):
     *  patient_id, full_name, patient_visit_no, visit_date, bill, collected, folder_no,
     *  balance, discount, dues, visit_time_text, shift_desc_initial, status_description,
     *  is_adhoc, receipt_number, receipt_type, doctor_name
     */
    @Query(value = "\n"
            + "SELECT pv.patient_id, (pm.first_name || ' ' || pm.last_name) AS full_name,\n"
            + "       pv.patient_visit_no, pv.visit_date, pv.fees_to_collect AS bill, pv.fees_collected AS collected,\n"
            + "       pm.folder_no, ((pv.fees_to_collect - pv.discount) - pv.fees_collected) AS balance,\n"
            + "       pv.discount AS discount, (pv.fees_to_collect - pv.discount) AS dues,\n"
            + "       to_char(pv.visit_time, 'HH24:MI:SS') AS visit_time_text,\n"
            + "       substr(sm.description, 1, 1) AS shift_desc_initial, sr.status_description,\n"
            + "       NULL AS is_adhoc, COALESCE(pv.receipt_number, '') AS receipt_number,\n"
            + "       COALESCE(pv.receipt_type, '') AS receipt_type, (dm.prefix || ' ' || dm.first_name) AS doctor_name\n"
            + "  FROM patient_master pm\n"
            + "  JOIN patient_visits pv         ON pm.id = pv.patient_id\n"
            + "  JOIN shift_master sm            ON pv.shift_id = sm.shift_id\n"
            + "  JOIN status_ref sr              ON pv.status_id = sr.id\n"
            + "  JOIN doctor_master dm           ON dm.doctor_id = pv.doctor_id\n"
            + " WHERE pm.id = :patientId\n"
            + "   AND pv.delete_flag = false\n"
            + "   AND pv.fees_to_collect IS NOT NULL\n"
            + "   AND pv.fees_collected  IS NOT NULL\n"
            + "   AND pv.status_id = 5\n"
            + "UNION ALL\n"
            + "SELECT pvs.patient_id, (pm.first_name || ' ' || pm.last_name) AS full_name,\n"
            + "       pvs.patient_visit_no, pvs.visit_date, pvs.fees_to_collect AS bill, pvs.fees_collected AS collected,\n"
            + "       pm.folder_no, ((pvs.fees_to_collect - pvs.discount) - pvs.fees_collected) AS balance,\n"
            + "       pvs.discount AS discount, (pvs.fees_to_collect - pvs.discount) AS dues,\n"
            + "       to_char(pvs.visit_time, 'HH24:MI:SS') AS visit_time_text,\n"
            + "       substr(sm.description, 1, 1) AS shift_desc_initial, sr.status_description,\n"
            + "       NULL AS is_adhoc, '' AS receipt_number, '' AS receipt_type, (dm.prefix || ' ' || dm.first_name) AS doctor_name\n"
            + "  FROM patient_master pm\n"
            + "  JOIN patient_visits_services pvs ON pm.id = pvs.patient_id\n"
            + "  JOIN shift_master sm            ON pvs.shift_id = sm.shift_id\n"
            + "  JOIN status_ref sr              ON pvs.status_id = sr.id\n"
            + "  JOIN doctor_master dm           ON dm.doctor_id = pvs.doctor_id\n"
            + " WHERE pm.id = :patientId\n"
            + "   AND pvs.delete_flag = false\n"
            + "   AND pvs.fees_to_collect IS NOT NULL\n"
            + "   AND pvs.fees_collected  IS NOT NULL\n"
            + "   AND pvs.status_id = 8\n"
            + "UNION ALL\n"
            + "SELECT ppa.patient_id, (pm.first_name || ' ' || pm.last_name) AS full_name,\n"
            + "       0 AS patient_visit_no, ppa.payment_date AS visit_date, 0 AS bill, ppa.fees_collected AS collected,\n"
            + "       pm.folder_no, (0 - ppa.fees_collected) AS balance, 0 AS discount, 0 AS dues,\n"
            + "       to_char(ppa.payment_date, 'HH24:MI:SS') AS visit_time_text,\n"
            + "       substr(sm.description, 1, 1) AS shift_desc_initial, NULL AS status_description,\n"
            + "       'Y' AS is_adhoc, COALESCE(ppa.receipt_number, '') AS receipt_number,\n"
            + "       COALESCE(ppa.receipt_type, '') AS receipt_type, ppa.attended_by AS doctor_name\n"
            + "  FROM patient_payments_adhoc ppa\n"
            + "  JOIN patient_master pm ON pm.id = ppa.patient_id\n"
            + "  JOIN shift_master sm  ON ppa.shift_id = sm.shift_id\n"
            + " WHERE pm.id = :patientId\n"
            + "   AND ppa.delete_flag = false\n"
            + "ORDER BY visit_date DESC\n",
            nativeQuery = true)
    List<Object[]> findFeesDetailsByPatientId(@Param("patientId") String patientId);

    @Query(value = "SELECT folder_no, (first_name || ' ' || last_name) AS full_name\n"
            + "FROM patient_master WHERE id = :patientId", nativeQuery = true)
    List<Object[]> findFolderAndName(@Param("patientId") String patientId);
}


