package com.climasys.admission.repository;

import com.climasys.entity.AdmissionData;
import com.climasys.entity.AdmissionDataId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
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
     * Used for "List of Admitted Patient/s" table on Manage Advance Collection screen
     * 
     * Business Logic:
     * - Starts from Admission_Data to show ALL admitted patients (not just those with discharge_data)
     * - LEFT JOIN Discharge_Data to check if patient is discharged
     * - LEFT JOIN Advance_Collection_details to get advance date and receipt number
     * - Filters for non-discharged patients (Discharge_Date IS NULL or no discharge_data record)
     * - Orders by Admission_Date DESC (newest first, matching original behavior)
     * - Includes duplicate removal by IPD_RefNo in service layer
     * 
     * @param patientId Patient ID (optional)
     * @param doctorId Doctor ID (optional - if null, returns all doctors for the clinic)
     * @param clinicId Clinic ID (required)
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
            COALESCE(ad.insurancedetails, '') AS company,
            COALESCE(TO_CHAR(acd.advance_date, 'DD Mon YYYY'), '') AS dateOfAdvance,
            COALESCE(acd.receipt_number, '') AS receiptNo,
            COALESCE((
                SELECT SUM(acd_sum.amount_received) 
                FROM advance_collection_details acd_sum 
                WHERE acd_sum.ipd_refno = ad.ipd_refno
            ), 0.00) AS advanceRs,
            ad.patient_id AS patientId
        FROM admission_data ad
        INNER JOIN patient_master pm ON ad.patient_id = pm.id
        LEFT JOIN discharge_data dd ON ad.ipd_refno = dd.ipd_refno 
            AND ad.patient_id = dd.patient_id
            AND ad.doctor_id = dd.doctor_id
            AND ad.clinic_id = dd.clinic_id
        LEFT JOIN LATERAL (
            SELECT 
                advance_date,
                receipt_number
            FROM advance_collection_details acd_inner
            WHERE acd_inner.patient_id = ad.patient_id
              AND acd_inner.clinic_id = ad.clinic_id
              AND acd_inner.ipd_refno = ad.ipd_refno
            ORDER BY 
                CASE WHEN acd_inner.advance_date IS NOT NULL THEN 0 ELSE 1 END,
                COALESCE(acd_inner.advance_date, acd_inner.date) DESC NULLS LAST
            LIMIT 1
        ) acd ON true
        WHERE (:patientId IS NULL OR ad.patient_id = :patientId)
          AND (:doctorId IS NULL OR ad.doctor_id = :doctorId)
          AND ad.clinic_id = :clinicId
          AND (dd.discharge_date IS NULL OR dd.ipd_refno IS NULL)
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
              OR TRIM(pm.first_name || ' ' || COALESCE(pm.middle_name, '') || ' ' || COALESCE(pm.last_name, '')) ILIKE '%' || :searchStr || '%'
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
    
    /**
     * Get admission data by patient ID
     * Returns all admission records for a specific patient
     * 
     * @param patientId Patient ID
     * @return List of admission data records
     */
    @Query(value = """
        SELECT 
            ad.patient_id,
            ad.doctor_id,
            ad.clinic_id,
            ad.ipd_refno,
            ad.relativename,
            ad.relation,
            ad.contactno,
            ad.admission_date,
            ad.admission_time,
            ad.reasonofadmission,
            ad.shift_id,
            ad.department,
            ad.isinsurance,
            ad.insurancedetails,
            ad.treatingdoctor,
            ad.consultantdoctor,
            ad.ipdfileno,
            ad.roomno,
            ad.packageremarks,
            ad.bedno,
            ad.referred_doctor,
            ad.comments_note,
            ad.insurance_company_id,
            ad.createdby_name,
            ad.created_on,
            ad.modifiedby_name,
            ad.modified_on
        FROM admission_data ad
        WHERE ad.patient_id = :patientId
        ORDER BY ad.admission_date DESC, ad.admission_time DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findByPatientId(
        @Param("patientId") String patientId
    );
    
    /**
     * Get patient admission card data for advance collection page
     * Replicates USP_Get_Patient_AdmissionCard_data stored procedure
     * 
     * Returns: Admission No, IPD File No, Admission Date, Discharge Date, 
     * Room-Bed, Department, Insurance, Company, Hospital bill No, 
     * Hospital bill Date, Package remarks, Total Advance
     * 
     * @param patientId Patient ID
     * @param clinicId Clinic ID
     * @param doctorId Doctor ID
     * @param ipdRefNo IPD Reference Number
     * @return Patient admission card data
     */
    @Query(value = """
        SELECT 
            ad.ipd_refno AS admissionNo,
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
            CASE 
                WHEN dd.discharge_date IS NOT NULL THEN 
                    TO_CHAR(dd.discharge_date, 'DD Mon YYYY') || 
                    CASE WHEN dd.discharge_time IS NOT NULL 
                        THEN ' - ' || TO_CHAR(dd.discharge_time, 'HH24:MI:SS')
                        ELSE ''
                    END
                ELSE ''
            END AS dischargeDate,
            CASE 
                WHEN ad.roomno IS NOT NULL AND ad.bedno IS NOT NULL THEN 
                    ad.roomno || '-' || ad.bedno
                WHEN ad.roomno IS NOT NULL THEN 
                    ad.roomno
                WHEN ad.bedno IS NOT NULL THEN 
                    ad.bedno
                ELSE ''
            END AS roomBed,
            COALESCE(ad.department, '') AS department,
            CASE WHEN ad.isinsurance = true THEN 'Yes' ELSE 'No' END AS insurance,
            COALESCE(icm.company_name, ad.insurancedetails, '') AS company,
            COALESCE(db.bill_no, '') AS hospitalBillNo,
            CASE 
                WHEN db.bill_date IS NOT NULL THEN 
                    TO_CHAR(db.bill_date, 'DD Mon YYYY')
                ELSE ''
            END AS hospitalBillDate,
            COALESCE(ad.packageremarks, '') AS packageRemarks,
            COALESCE((
                SELECT SUM(acd.amount_received) 
                FROM advance_collection_details acd 
                WHERE acd.patient_id = ad.patient_id
                  AND acd.clinic_id = ad.clinic_id
                  AND acd.ipd_refno = ad.ipd_refno
            ), 0.00) AS totalAdvance,
            COALESCE(ad.reasonofadmission, '') AS reasonOfAdmission
        FROM admission_data ad
        LEFT JOIN discharge_data dd ON ad.ipd_refno = dd.ipd_refno 
            AND ad.patient_id = dd.patient_id
            AND ad.doctor_id = dd.doctor_id
            AND ad.clinic_id = dd.clinic_id
        LEFT JOIN insurance_company_master icm ON ad.insurance_company_id = icm.company_id
        LEFT JOIN discharge_bill_hdr db ON ad.ipd_refno = db.ipd_refno
            AND ad.patient_id = db.patient_id
            AND ad.doctor_id = db.doctor_id
            AND ad.clinic_id = db.clinic_id
        WHERE ad.patient_id = :patientId
          AND ad.clinic_id = :clinicId
          AND ad.doctor_id = :doctorId
          AND ad.ipd_refno = :ipdRefNo
        ORDER BY ad.admission_date DESC, ad.admission_time DESC
        LIMIT 1
        """, nativeQuery = true)
    Map<String, Object> getPatientAdmissionCardData(
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId,
        @Param("doctorId") String doctorId,
        @Param("ipdRefNo") String ipdRefNo
    );
    
    /**
     * Get sequence number for IPD entity type
     * Used for generating IPD Reference Number
     * Returns null if sequence doesn't exist
     */
    @Query(value = """
        SELECT last_sequenceno, prefix_char, total_length
        FROM sequence_nos
        WHERE clinic_id = :clinicId AND entity_type = 'IPD'
        LIMIT 1
        """, nativeQuery = true)
    Map<String, Object> getSequenceForIpd(@Param("clinicId") String clinicId);
    
    /**
     * Create default sequence entry for IPD if not exists
     * Uses doctorId from request to satisfy foreign key constraint
     */
    @Modifying(clearAutomatically = true)
    @Query(value = """
        INSERT INTO sequence_nos
        (doctor_id, entity_type, entity_name, prefix_char, total_length, last_sequenceno, clinic_id)
        VALUES (:doctorId, 'IPD', 'IPD', '', 5, 0, :clinicId)
        ON CONFLICT (doctor_id, entity_type, entity_name, clinic_id) DO NOTHING
        """, nativeQuery = true)
    void createDefaultIpdSequence(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);
    
    /**
     * Update sequence number for IPD entity type
     */
    @Modifying(clearAutomatically = true)
    @Query(value = """
        UPDATE sequence_nos
        SET last_sequenceno = :lastSequenceNo
        WHERE clinic_id = :clinicId AND entity_type = 'IPD'
        """, nativeQuery = true)
    void updateIpdSequence(@Param("lastSequenceNo") Long lastSequenceNo, @Param("clinicId") String clinicId);
    
    /**
     * Insert discharge data record
     * Replicates the INSERT INTO discharge_data logic from USP_Insert_AdmissionCard
     */
    @Modifying(clearAutomatically = true)
    @Query(value = """
        INSERT INTO discharge_data
        (doctor_id, clinic_id, patient_id, ipd_refno, admission_date, admission_time,
         treating_doctor, consulting_doctor, ipd_no, createdby_name, created_on, bedno, room, referred_doctor, visit_date)
        VALUES (:doctorId, :clinicId, :patientId, :ipdRefNo, :admissionDate, :admissionTime,
                :treatingDoctor, :consultingDoctor, :ipdFileNo, :loginId, CURRENT_TIMESTAMP, :bedNo, :roomNo, :referredDoctor, :admissionDate)
        """, nativeQuery = true)
    void insertDischargeData(
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId,
        @Param("patientId") String patientId,
        @Param("ipdRefNo") String ipdRefNo,
        @Param("admissionDate") LocalDate admissionDate,
        @Param("admissionTime") LocalTime admissionTime,
        @Param("treatingDoctor") String treatingDoctor,
        @Param("consultingDoctor") String consultingDoctor,
        @Param("ipdFileNo") String ipdFileNo,
        @Param("loginId") String loginId,
        @Param("bedNo") String bedNo,
        @Param("roomNo") String roomNo,
        @Param("referredDoctor") String referredDoctor
    );
    
    /**
     * Update discharge data record
     * Replicates the UPDATE discharge_data logic from USP_Insert_AdmissionCard
     */
    @Modifying(clearAutomatically = true)
    @Query(value = """
        UPDATE discharge_data
        SET ipd_no = :ipdFileNo,
            admission_date = :admissionDate,
            admission_time = :admissionTime,
            treating_doctor = :treatingDoctor,
            consulting_doctor = :consultingDoctor,
            bedno = :bedNo,
            room = :roomNo,
            modified_on = CURRENT_TIMESTAMP,
            modifiedby_name = :loginId,
            referred_doctor = :referredDoctor
        WHERE patient_id = :patientId
          AND clinic_id = :clinicId
          AND ipd_refno = :ipdRefNo
        """, nativeQuery = true)
    int updateDischargeData(
        @Param("ipdFileNo") String ipdFileNo,
        @Param("admissionDate") LocalDate admissionDate,
        @Param("admissionTime") LocalTime admissionTime,
        @Param("treatingDoctor") String treatingDoctor,
        @Param("consultingDoctor") String consultingDoctor,
        @Param("bedNo") String bedNo,
        @Param("roomNo") String roomNo,
        @Param("loginId") String loginId,
        @Param("referredDoctor") String referredDoctor,
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId,
        @Param("ipdRefNo") String ipdRefNo
    );
}

