package com.climasys.discharge.repository;

import com.climasys.entity.AdmissionData;
import com.climasys.entity.AdmissionDataId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository for discharge card operations
 * Replaces USP_Get_Patient_All_Discharge_Cards stored procedure
 * Handles queries for Manage Discharge Card screen
 */
@Repository
public interface DischargeCardRepository extends JpaRepository<AdmissionData, AdmissionDataId> {
    
    /**
     * Get all admitted patients for "List of Admitted Patient/s" table
     * Matches Table[5] from USP_Get_Patient_All_Discharge_Cards
     * Used on page load to show all admitted patients
     * 
     * Fields: Sr., Patient Name, IPD No, IPD File No, Admission Date, 
     *         Discharge Date, keyword / Operation, Advance (Rs)
     * 
     * @param doctorId Doctor ID (optional - if null, returns all doctors for the clinic)
     * @param clinicId Clinic ID
     * @return List of admitted patients with discharge card information
     */
    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER (ORDER BY ad.admission_date DESC, ad.admission_time DESC) AS serialNumber,
            TRIM(pm.first_name || ' ' || COALESCE(pm.middle_name, '') || ' ' || COALESCE(pm.last_name, '')) AS patientName,
            ad.ipd_refno AS ipdNo,
            COALESCE(ad.ipdfileno, '') AS ipdFileNo,
            CASE 
                WHEN ad.admission_date IS NOT NULL THEN 
                    TO_CHAR(ad.admission_date, 'DD Mon YYYY') || 
                    CASE WHEN ad.admission_time IS NOT NULL 
                        THEN '-' || TO_CHAR(ad.admission_time, 'HH24:MI:SS')
                        ELSE ''
                    END
                ELSE ''
            END AS admissionDate,
            CASE 
                WHEN dd.discharge_date IS NOT NULL THEN 
                    TO_CHAR(dd.discharge_date, 'DD Mon YYYY') || 
                    CASE WHEN dd.discharge_time IS NOT NULL 
                        THEN '-' || TO_CHAR(dd.discharge_time, 'HH24:MI:SS')
                        ELSE ''
                    END
                ELSE ''
            END AS dischargeDate,
            COALESCE(dd.keyword, '') AS keyword,
            COALESCE((
                SELECT SUM(acd.amount_received) 
                FROM advance_collection_details acd 
                WHERE acd.ipd_refno = ad.ipd_refno
            ), 0.00) AS advanceRs,
            ad.patient_id AS patientId,
            ad.ipd_refno AS ipdRefNo
        FROM admission_data ad
        INNER JOIN patient_master pm ON ad.patient_id = pm.id
        LEFT JOIN discharge_data dd ON ad.ipd_refno = dd.ipd_refno 
            AND ad.patient_id = dd.patient_id
            AND ad.doctor_id = dd.doctor_id
            AND ad.clinic_id = dd.clinic_id
        WHERE (:doctorId IS NULL OR ad.doctor_id = :doctorId)
          AND ad.clinic_id = :clinicId
        ORDER BY ad.admission_date DESC, ad.admission_time DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findAllAdmittedPatients(
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId
    );
    
    /**
     * Get discharge cards for a specific patient (search results)
     * Matches Table[0] from USP_Get_Patient_All_Discharge_Cards
     * Used when searching for a specific patient
     * 
     * @param patientId Patient ID
     * @param doctorId Doctor ID (optional - if null, returns all doctors for the clinic)
     * @param clinicId Clinic ID
     * @return List of discharge cards for the patient
     */
    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER (ORDER BY ad.admission_date DESC, ad.admission_time DESC) AS serialNumber,
            TRIM(pm.first_name || ' ' || COALESCE(pm.middle_name, '') || ' ' || COALESCE(pm.last_name, '')) AS patientName,
            ad.ipd_refno AS ipdNo,
            COALESCE(ad.ipdfileno, '') AS ipdFileNo,
            CASE 
                WHEN ad.admission_date IS NOT NULL THEN 
                    TO_CHAR(ad.admission_date, 'DD Mon YYYY') || 
                    CASE WHEN ad.admission_time IS NOT NULL 
                        THEN '-' || TO_CHAR(ad.admission_time, 'HH24:MI:SS')
                        ELSE ''
                    END
                ELSE ''
            END AS admissionDate,
            CASE 
                WHEN dd.discharge_date IS NOT NULL THEN 
                    TO_CHAR(dd.discharge_date, 'DD Mon YYYY') || 
                    CASE WHEN dd.discharge_time IS NOT NULL 
                        THEN '-' || TO_CHAR(dd.discharge_time, 'HH24:MI:SS')
                        ELSE ''
                    END
                ELSE ''
            END AS dischargeDate,
            COALESCE(dd.keyword, '') AS keyword,
            COALESCE((
                SELECT SUM(acd.amount_received) 
                FROM advance_collection_details acd 
                WHERE acd.ipd_refno = ad.ipd_refno
            ), 0.00) AS advanceRs,
            ad.patient_id AS patientId,
            ad.ipd_refno AS ipdRefNo
        FROM admission_data ad
        INNER JOIN patient_master pm ON ad.patient_id = pm.id
        LEFT JOIN discharge_data dd ON ad.ipd_refno = dd.ipd_refno 
            AND ad.patient_id = dd.patient_id
            AND ad.doctor_id = dd.doctor_id
            AND ad.clinic_id = dd.clinic_id
        WHERE ad.patient_id = :patientId
          AND (:doctorId IS NULL OR ad.doctor_id = :doctorId)
          AND ad.clinic_id = :clinicId
        ORDER BY ad.admission_date DESC, ad.admission_time DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findDischargeCardsByPatient(
        @Param("patientId") String patientId,
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId
    );
    
    /**
     * Search discharge cards by patient ID, name, contact, or IPD number
     * Used for the search functionality on Manage Discharge Card screen
     * 
     * @param searchStr Search string
     * @param doctorId Doctor ID (optional - if null, searches all doctors for the clinic)
     * @param clinicId Clinic ID
     * @return List of matching discharge cards
     */
    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER (ORDER BY ad.admission_date DESC, ad.admission_time DESC) AS serialNumber,
            TRIM(pm.first_name || ' ' || COALESCE(pm.middle_name, '') || ' ' || COALESCE(pm.last_name, '')) AS patientName,
            ad.ipd_refno AS ipdNo,
            COALESCE(ad.ipdfileno, '') AS ipdFileNo,
            CASE 
                WHEN ad.admission_date IS NOT NULL THEN 
                    TO_CHAR(ad.admission_date, 'DD Mon YYYY') || 
                    CASE WHEN ad.admission_time IS NOT NULL 
                        THEN '-' || TO_CHAR(ad.admission_time, 'HH24:MI:SS')
                        ELSE ''
                    END
                ELSE ''
            END AS admissionDate,
            CASE 
                WHEN dd.discharge_date IS NOT NULL THEN 
                    TO_CHAR(dd.discharge_date, 'DD Mon YYYY') || 
                    CASE WHEN dd.discharge_time IS NOT NULL 
                        THEN '-' || TO_CHAR(dd.discharge_time, 'HH24:MI:SS')
                        ELSE ''
                    END
                ELSE ''
            END AS dischargeDate,
            COALESCE(dd.keyword, '') AS keyword,
            COALESCE((
                SELECT SUM(acd.amount_received) 
                FROM advance_collection_details acd 
                WHERE acd.ipd_refno = ad.ipd_refno
            ), 0.00) AS advanceRs,
            ad.patient_id AS patientId,
            ad.ipd_refno AS ipdRefNo
        FROM admission_data ad
        INNER JOIN patient_master pm ON ad.patient_id = pm.id
        LEFT JOIN discharge_data dd ON ad.ipd_refno = dd.ipd_refno 
            AND ad.patient_id = dd.patient_id
            AND ad.doctor_id = dd.doctor_id
            AND ad.clinic_id = dd.clinic_id
        WHERE (:doctorId IS NULL OR ad.doctor_id = :doctorId)
          AND ad.clinic_id = :clinicId
          AND (
              ad.patient_id ILIKE '%' || :searchStr || '%'
              OR pm.first_name ILIKE '%' || :searchStr || '%'
              OR pm.middle_name ILIKE '%' || :searchStr || '%'
              OR pm.last_name ILIKE '%' || :searchStr || '%'
              OR TRIM(pm.first_name || ' ' || COALESCE(pm.middle_name, '') || ' ' || COALESCE(pm.last_name, '')) ILIKE '%' || :searchStr || '%'
              OR pm.mobile_1 ILIKE '%' || :searchStr || '%'
              OR ad.ipd_refno ILIKE '%' || :searchStr || '%'
          )
        ORDER BY ad.admission_date DESC, ad.admission_time DESC
        """, nativeQuery = true)
    List<Map<String, Object>> searchDischargeCards(
        @Param("searchStr") String searchStr,
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId
    );
    
    /**
     * Get discharge card details for a specific patient and IPD
     * Matches USP_Get_Patient_DischargeCard_Data stored procedure
     * Returns multiple result sets (main data, investigations, invoices, bills, labour card, advance)
     * 
     * Note: shiftId, clinicId, and invoiceNo are not used in the WHERE clause (matching stored procedure behavior)
     * 
     * @param patientId Patient ID
     * @param doctorId Doctor ID
     * @param ipdNo IPD Number
     * @return Main discharge card data (Table[0])
     */
    @Query(value = """
        SELECT 
            dd.ipd_refno AS ipdRefNo,
            dd.admission_date AS admissionDate,
            dd.admission_time AS admissionTime,
            dd.treating_doctor AS treatingDoctor,
            dd.consulting_doctor AS consultingDoctor,
            dd.discharge_date AS dischargeDate,
            dd.discharge_time AS dischargeTime,
            dd.weight,
            dd.ipd_no AS ipdNo,
            dd.diagnosis,
            dd.complaints,
            dd.history,
            dd.investigations,
            dd.oe,
            dd.se,
            dd.procedure,
            dd.treatment,
            dd.discharge,
            dd.instructions,
            COALESCE(dd.keyword, '') AS keyword,
            dd.operation_start_date AS operationStartDate,
            dd.operation_end_date AS operationEndDate,
            dd.operation_start_time AS operationStartTime,
            dd.operation_end_time AS operationEndTime,
            COALESCE(dd.operative_notes, '') AS operativeNotes,
            COALESCE(dd.remark, '') AS remark,
            COALESCE(dd.follow_up_comments, '') AS followUpComments,
            dd.anesthesia,
            dd.doctor_id AS doctorId,
            dd.reasonfordischarge AS reasonForDischarge,
            COALESCE(dm.emergency_number, '') AS emergencyNumber,
            COALESCE(ad.insurancedetails, '') AS company,
            COALESCE(dd.referred_doctor, '') AS referredDoctor,
            COALESCE(dd.condition_discharge, '') AS conditionDischarge,
            COALESCE(dd.footer, '') AS footer,
            COALESCE(dd.printed_on_date, '') AS printedOnDate,
            COALESCE(dd.printed_on_date_op, '') AS printedOnDateOp,
            COALESCE(dd.room, '') AS room,
            COALESCE(dd.bedno, '') AS bedNo,
            COALESCE(dd.admitted_days, '') AS admittedDays,
            COALESCE(dd.ot_hours, '') AS otHours,
            COALESCE(ad.department, '') AS department,
            dd.followup_date AS followUpDate,
            -- Patient information
            COALESCE(pm.first_name || ' ' || COALESCE(pm.middle_name || ' ', '') || COALESCE(pm.last_name, ''), '') AS patientName,
            pm.id AS patientId,
            COALESCE(gt.gender_description, '') AS gender,
            CASE 
                WHEN pm.date_of_birth IS NOT NULL THEN 
                    CAST(EXTRACT(YEAR FROM AGE(pm.date_of_birth)) AS INTEGER)
                WHEN pm.age_given IS NOT NULL THEN CAST(pm.age_given AS INTEGER)
                ELSE NULL
            END AS age,
            COALESCE(
                CASE 
                    WHEN pm.address_1 IS NOT NULL AND pm.city_id IS NOT NULL THEN 
                        pm.address_1 || ', ' || COALESCE((SELECT ct.city_name FROM city_translations ct WHERE ct.city_id = pm.city_id AND (ct.language_id = 1 OR ct.language_id IS NULL) LIMIT 1), '')
                    WHEN pm.address_1 IS NOT NULL THEN pm.address_1
                    WHEN pm.city_id IS NOT NULL THEN COALESCE((SELECT ct.city_name FROM city_translations ct WHERE ct.city_id = pm.city_id AND (ct.language_id = 1 OR ct.language_id IS NULL) LIMIT 1), '')
                    ELSE ''
                END, ''
            ) AS address,
            COALESCE(pm.mobile_1, '') AS contactNo
        FROM discharge_data dd
        INNER JOIN doctor_master dm ON dm.doctor_id = :doctorId
        LEFT JOIN admission_data ad ON ad.ipd_refno = :ipdNo
        LEFT JOIN patient_master pm ON pm.id = dd.patient_id
        LEFT JOIN gender_translations gt ON gt.gender_id = pm.gender_id AND (gt.language_id = 1 OR gt.language_id IS NULL)
        WHERE dd.patient_id = :patientId
          AND dd.ipd_refno = :ipdNo
        LIMIT 1
        """, nativeQuery = true)
    Map<String, Object> getDischargeCardMainData(
        @Param("patientId") String patientId,
        @Param("doctorId") String doctorId,
        @Param("ipdNo") String ipdNo
    );
}

