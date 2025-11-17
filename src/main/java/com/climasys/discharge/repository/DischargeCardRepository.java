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
}

