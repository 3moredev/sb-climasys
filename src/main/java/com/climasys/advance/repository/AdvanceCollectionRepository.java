package com.climasys.advance.repository;

import com.climasys.advance.dto.AdvanceDetail;
import com.climasys.advance.dto.AdvanceCollectionSearchResult;
import com.climasys.entity.AdvanceCollectionDetail;
import com.climasys.entity.AdvanceCollectionDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for advance collection operations
 */
@Repository
public interface AdvanceCollectionRepository extends JpaRepository<AdvanceCollectionDetail, AdvanceCollectionDetailId> {
    
    /**
     * Get advance details for a patient's IPD
     * Replicates USP_GET_AdvanceDetails
     */
    @Query(value = """
        SELECT 
            TO_CHAR(advance_date, 'DD Mon YYYY') as advanceDate,
            amount_received as advance
        FROM advance_collection_details
        WHERE patient_id = :patientId
          AND clinic_id = :clinicId
          AND ipd_refno = :ipdRefNo
        ORDER BY advance_date DESC
        """, nativeQuery = true)
    List<AdvanceDetail> findAdvanceDetails(
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId,
        @Param("ipdRefNo") String ipdRefNo
    );
    
    /**
     * Search patients with advance cards
     * Replicates USP_Search_Patient_With_AdvanceCard
     * Returns all fields needed for the "List of Admitted Patient/s" table
     * Note: doctorId is optional - if null, searches across all doctors
     */
    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER (ORDER BY ad.admission_date DESC, ad.admission_time DESC) AS serialNumber,
            TRIM(pm.first_name || ' ' || COALESCE(pm.middle_name, '') || ' ' || COALESCE(pm.last_name, '')) AS patientName,
            ad.ipd_refno AS ipdRefNo,
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
            CASE WHEN ad.isinsurance = true THEN 'Yes' ELSE 'No' END AS insurance,
            COALESCE(TO_CHAR(acd_latest.advance_date, 'DD Mon YYYY'), '') AS dateOfAdvance,
            COALESCE(acd_latest.receipt_number, '') AS receiptNo,
            COALESCE(acd_total.total_advance, 0.00) AS advanceRs,
            ad.patient_id AS patientId,
            ad.clinic_id AS clinicId,
            ad.doctor_id AS doctorId
        FROM admission_data ad
        INNER JOIN patient_master pm ON ad.patient_id = pm.id
        LEFT JOIN LATERAL (
            SELECT 
                advance_date,
                receipt_number
            FROM advance_collection_details acd
            WHERE acd.patient_id = ad.patient_id
              AND acd.clinic_id = ad.clinic_id
              AND acd.ipd_refno = ad.ipd_refno
            ORDER BY 
                CASE WHEN acd.advance_date IS NOT NULL THEN 0 ELSE 1 END,
                COALESCE(acd.advance_date, acd.date) DESC NULLS LAST
            LIMIT 1
        ) acd_latest ON true
        LEFT JOIN (
            SELECT 
                patient_id,
                clinic_id,
                doctor_id,
                ipd_refno,
                SUM(amount_received) AS total_advance
            FROM advance_collection_details
            GROUP BY patient_id, clinic_id, doctor_id, ipd_refno
        ) acd_total ON acd_total.patient_id = ad.patient_id 
            AND acd_total.clinic_id = ad.clinic_id 
            AND acd_total.doctor_id = ad.doctor_id 
            AND acd_total.ipd_refno = ad.ipd_refno
        WHERE (
            ad.patient_id ILIKE '%' || :searchStr || '%'
            OR pm.first_name ILIKE '%' || :searchStr || '%'
            OR pm.middle_name ILIKE '%' || :searchStr || '%'
            OR pm.last_name ILIKE '%' || :searchStr || '%'
            OR (pm.first_name || ' ' || pm.last_name) ILIKE '%' || :searchStr || '%'
            OR TRIM(pm.first_name || ' ' || COALESCE(pm.middle_name, '') || ' ' || COALESCE(pm.last_name, '')) ILIKE '%' || :searchStr || '%'
            OR pm.mobile_1 ILIKE '%' || :searchStr || '%'
            OR ad.ipd_refno ILIKE '%' || :searchStr || '%'
        )
        AND (:doctorId IS NULL OR ad.doctor_id = :doctorId)
        ORDER BY ad.admission_date DESC, ad.admission_time DESC
        LIMIT 20
        """, nativeQuery = true)
    List<AdvanceCollectionSearchResult> searchPatientsWithAdvanceCard(
        @Param("searchStr") String searchStr,
        @Param("doctorId") String doctorId
    );
    
    /**
     * Check if advance collection already exists
     */
    @Query(value = """
        SELECT COUNT(*) > 0
        FROM advance_collection_details
        WHERE patient_id = :patientId
          AND clinic_id = :clinicId
          AND ipd_refno = :ipdRefNo
          AND date = :date
        """, nativeQuery = true)
    boolean existsByCompositeKey(
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId,
        @Param("ipdRefNo") String ipdRefNo,
        @Param("date") LocalDateTime date
    );
    
    /**
     * Insert advance collection
     */
    @Modifying
    @Query(value = """
        INSERT INTO advance_collection_details (
            patient_id, doctor_id, clinic_id, ipd_refno,
            date, amount_received, payment_by_id, payment_remark,
            shift_id, created_on, createdby_name, advance_date
        ) VALUES (
            :patientId, :doctorId, :clinicId, :ipdRefNo,
            :date, :amountReceived, :paymentById, :paymentRemark,
            :shiftId, CURRENT_TIMESTAMP, :loginId, :advanceDate
        )
        """, nativeQuery = true)
    void insertAdvanceCollection(
        @Param("patientId") String patientId,
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId,
        @Param("ipdRefNo") String ipdRefNo,
        @Param("date") LocalDateTime date,
        @Param("amountReceived") BigDecimal amountReceived,
        @Param("paymentById") Short paymentById,
        @Param("paymentRemark") String paymentRemark,
        @Param("shiftId") Short shiftId,
        @Param("loginId") String loginId,
        @Param("advanceDate") LocalDateTime advanceDate
    );
    
    /**
     * Update advance collection
     */
    @Modifying
    @Query(value = """
        UPDATE advance_collection_details
        SET amount_received = :amountReceived,
            payment_by_id = :paymentById,
            payment_remark = :paymentRemark,
            modified_on = CURRENT_TIMESTAMP,
            modifiedby_name = :loginId,
            advance_date = :advanceDate
        WHERE patient_id = :patientId
          AND clinic_id = :clinicId
          AND ipd_refno = :ipdRefNo
          AND date = :date
        """, nativeQuery = true)
    void updateAdvanceCollection(
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId,
        @Param("ipdRefNo") String ipdRefNo,
        @Param("date") LocalDateTime date,
        @Param("amountReceived") BigDecimal amountReceived,
        @Param("paymentById") Short paymentById,
        @Param("paymentRemark") String paymentRemark,
        @Param("loginId") String loginId,
        @Param("advanceDate") LocalDateTime advanceDate
    );
}

