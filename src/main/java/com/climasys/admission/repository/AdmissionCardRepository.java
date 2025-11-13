package com.climasys.admission.repository;

import com.climasys.entity.AdmissionData;
import com.climasys.entity.AdmissionDataId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository for admission card operations
 * Handles queries for patient admission information
 */
@Repository
public interface AdmissionCardRepository extends JpaRepository<AdmissionData, AdmissionDataId> {
    
    /**
     * Get all admission cards (list of admitted patients)
     * Based on the Manage Admission Card page fields
     * 
     * @param patientId Patient ID (optional)
     * @param doctorId Doctor ID (optional - if null, returns all doctors for the clinic)
     * @param clinicId Clinic ID
     * @return List of admission cards
     */
    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER (ORDER BY ad.admission_date DESC, ad.admission_time DESC) AS serialNumber,
            TRIM(pm.first_name || ' ' || COALESCE(pm.middle_name, '') || ' ' || COALESCE(pm.last_name, '')) AS patientName,
            ad.ipd_refno AS admissionIpdNo,
            COALESCE(ad.ipdfileno, '') AS ipdFileNo,
            CASE 
                WHEN ad.admission_date IS NOT NULL THEN 
                    TO_CHAR(ad.admission_date, 'DD Mon YYYY') || 
                    CASE WHEN ad.admission_time IS NOT NULL 
                        THEN ' - ' || TO_CHAR(ad.admission_time, 'HH24:MI:SS')
                        ELSE ''
                    END
                ELSE ''
            END AS admissionDate,
            COALESCE(ad.reasonofadmission, '') AS reasonOfAdmission,
            CASE 
                WHEN dd.discharge_date IS NOT NULL THEN 
                    TO_CHAR(dd.discharge_date, 'DD Mon YYYY') || 
                    CASE WHEN dd.discharge_time IS NOT NULL 
                        THEN ' - ' || TO_CHAR(dd.discharge_time, 'HH24:MI:SS')
                        ELSE ''
                    END
                ELSE ''
            END AS dischargeDate,
            CASE WHEN ad.isinsurance = true THEN 'Yes' ELSE 'No' END AS insurance,
            COALESCE(icm.company_name, '') AS company,
            COALESCE((
                SELECT SUM(acd.amount_received) 
                FROM advance_collection_details acd 
                WHERE acd.ipd_refno = ad.ipd_refno
            ), 0.00) AS advanceRs,
            ad.patient_id AS patientId
        FROM admission_data ad
        INNER JOIN patient_master pm ON ad.patient_id = pm.id
        LEFT JOIN discharge_data dd ON ad.ipd_refno = dd.ipd_refno 
            AND ad.patient_id = dd.patient_id
            AND ad.doctor_id = dd.doctor_id
            AND ad.clinic_id = dd.clinic_id
        LEFT JOIN insurance_company_master icm ON ad.insurance_company_id = icm.company_id
        WHERE (:patientId IS NULL OR ad.patient_id = :patientId)
          AND (:doctorId IS NULL OR ad.doctor_id = :doctorId)
          AND ad.clinic_id = :clinicId
        ORDER BY ad.admission_date DESC, ad.admission_time DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findAllAdmissionCards(
        @Param("patientId") String patientId,
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId
    );
    
    /**
     * Search admission cards by patient ID, name, or contact
     * 
     * @param searchStr Search string
     * @param doctorId Doctor ID (optional - if null, searches all doctors for the clinic)
     * @param clinicId Clinic ID
     * @return List of matching admission cards
     */
    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER (ORDER BY ad.admission_date DESC, ad.admission_time DESC) AS serialNumber,
            TRIM(pm.first_name || ' ' || COALESCE(pm.middle_name, '') || ' ' || COALESCE(pm.last_name, '')) AS patientName,
            ad.ipd_refno AS admissionIpdNo,
            COALESCE(ad.ipdfileno, '') AS ipdFileNo,
            CASE 
                WHEN ad.admission_date IS NOT NULL THEN 
                    TO_CHAR(ad.admission_date, 'DD Mon YYYY') || 
                    CASE WHEN ad.admission_time IS NOT NULL 
                        THEN ' - ' || TO_CHAR(ad.admission_time, 'HH24:MI:SS')
                        ELSE ''
                    END
                ELSE ''
            END AS admissionDate,
            COALESCE(ad.reasonofadmission, '') AS reasonOfAdmission,
            CASE 
                WHEN dd.discharge_date IS NOT NULL THEN 
                    TO_CHAR(dd.discharge_date, 'DD Mon YYYY') || 
                    CASE WHEN dd.discharge_time IS NOT NULL 
                        THEN ' - ' || TO_CHAR(dd.discharge_time, 'HH24:MI:SS')
                        ELSE ''
                    END
                ELSE ''
            END AS dischargeDate,
            CASE WHEN ad.isinsurance = true THEN 'Yes' ELSE 'No' END AS insurance,
            COALESCE(icm.company_name, '') AS company,
            COALESCE((
                SELECT SUM(acd.amount_received) 
                FROM advance_collection_details acd 
                WHERE acd.ipd_refno = ad.ipd_refno
            ), 0.00) AS advanceRs,
            ad.patient_id AS patientId
        FROM admission_data ad
        INNER JOIN patient_master pm ON ad.patient_id = pm.id
        LEFT JOIN discharge_data dd ON ad.ipd_refno = dd.ipd_refno 
            AND ad.patient_id = dd.patient_id
            AND ad.doctor_id = dd.doctor_id
            AND ad.clinic_id = dd.clinic_id
        LEFT JOIN insurance_company_master icm ON ad.insurance_company_id = icm.company_id
        WHERE (:doctorId IS NULL OR ad.doctor_id = :doctorId)
          AND ad.clinic_id = :clinicId
          AND (
              ad.patient_id ILIKE '%' || :searchStr || '%'
              OR pm.first_name ILIKE '%' || :searchStr || '%'
              OR pm.middle_name ILIKE '%' || :searchStr || '%'
              OR pm.last_name ILIKE '%' || :searchStr || '%'
              OR (pm.first_name || ' ' || pm.last_name) ILIKE '%' || :searchStr || '%'
              OR pm.mobile_1 ILIKE '%' || :searchStr || '%'
              OR ad.ipd_refno ILIKE '%' || :searchStr || '%'
          )
        ORDER BY ad.admission_date DESC, ad.admission_time DESC
        """, nativeQuery = true)
    List<Map<String, Object>> searchAdmissionCards(
        @Param("searchStr") String searchStr,
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId
    );
    
    /**
     * Check if admission card already exists
     * Matches USP_Insert_AdmissionCard logic: Patient_ID + Clinic_ID + IPD_RefNo
     */
    @Query(value = """
        SELECT COUNT(*) > 0
        FROM admission_data
        WHERE patient_id = :patientId
          AND clinic_id = :clinicId
          AND ipd_refno = :ipdRefNo
        """, nativeQuery = true)
    boolean existsByCompositeKey(
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId,
        @Param("ipdRefNo") String ipdRefNo
    );
}

