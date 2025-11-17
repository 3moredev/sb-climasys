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
     * Search patients with advance cards (autocomplete)
     * Replicates USP_Search_Patient_With_AdvanceCard
     * Note: doctorId is optional - if null, searches across all doctors
     */
    @Query(value = """
        SELECT 
            COALESCE(ad.ipd_refno, '') || '   :  ' || 
            COALESCE(pm.id, '') || '   :   ' || 
            COALESCE(pm.first_name, '') || ' ' || 
            COALESCE(pm.middle_name, '') || ' ' || 
            COALESCE(pm.last_name, '') || '   :  ' || 
            COALESCE(pm.mobile_1, '') || '   :  ' || 
            COALESCE(TO_CHAR(dd.visit_date, 'DD Mon YYYY'), '') as searchValue
        FROM patient_master pm
        INNER JOIN admission_data ad ON ad.patient_id = pm.id
        INNER JOIN discharge_data dd ON dd.ipd_refno = ad.ipd_refno
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
        ORDER BY ad.ipd_refno DESC
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

