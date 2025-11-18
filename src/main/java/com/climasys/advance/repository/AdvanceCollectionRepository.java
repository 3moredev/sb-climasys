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
    
    /**
     * Get previous advance collection records (Table[0] from USP_Get_Patient_AdmissionCard_data)
     */
    @Query(value = """
        SELECT 
            ADV.ipd_refno AS ipdRefNo,
            CASE 
                WHEN AD.admission_date IS NOT NULL THEN 
                    TO_CHAR(AD.admission_date, 'DD Mon YYYY') || '-' || TO_CHAR(AD.admission_time, 'HH24:MI:SS')
                ELSE ''
            END AS admissionDate,
            COALESCE(AD.reasonofadmission, '') AS reasonOfAdmission,
            ADV.receipt_number AS receiptNumber,
            ADV.date AS date,
            CASE WHEN AD.isinsurance = true THEN 'Yes' ELSE 'No' END AS isInsurance,
            CASE 
                WHEN ADV.advance_date IS NOT NULL THEN TO_CHAR(ADV.advance_date, 'DD Mon YYYY')
                ELSE ''
            END AS dateOfAdvance,
            ADV.doctor_id AS doctorId,
            ADV.amount_received AS amountReceived,
            CASE 
                WHEN DD.discharge_date IS NOT NULL THEN 
                    TO_CHAR(DD.discharge_date, 'DD Mon YYYY') || '-' || TO_CHAR(DD.discharge_time, 'HH24:MI:SS')
                ELSE ''
            END AS dischargeDate,
            SUM(ADV.amount_received) OVER () AS sumTotal,
            CASE 
                WHEN DD.discharge_date IS NOT NULL THEN TO_CHAR(DD.discharge_date, 'DD Mon YYYY')
                ELSE ''
            END AS validDischargeDate
        FROM advance_collection_details ADV
        INNER JOIN admission_data AD ON ADV.ipd_refno = AD.ipd_refno
        LEFT JOIN discharge_data DD ON DD.ipd_refno = AD.ipd_refno
        WHERE ADV.patient_id = :patientId
          AND ADV.clinic_id = :clinicId
          AND ADV.ipd_refno = :ipdRefNo
        ORDER BY ADV.ipd_refno DESC
        """, nativeQuery = true)
    List<java.util.Map<String, Object>> getPreviousAdvanceRecords(
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId,
        @Param("ipdRefNo") String ipdRefNo
    );
    
    /**
     * Get current advance collection details (Table[1] from USP_Get_Patient_AdmissionCard_data)
     */
    @Query(value = """
        SELECT 
            ADV.amount_received AS amountReceived,
            ADV.advance_date AS advanceDate,
            ADV.payment_by_id AS paymentById,
            COALESCE(ADV.payment_remark, '') AS paymentRemark,
            COALESCE(ADV.receipt_number, '') AS receiptNumber,
            ADV.receipt_date AS receiptDate
        FROM advance_collection_details ADV
        INNER JOIN admission_data AD ON ADV.ipd_refno = AD.ipd_refno
        WHERE ADV.patient_id = :patientId
          AND ADV.clinic_id = :clinicId
          AND ADV.ipd_refno = :ipdRefNo
          AND CAST(ADV.date AS DATE) = CAST(:date AS DATE)
        LIMIT 1
        """, nativeQuery = true)
    java.util.Map<String, Object> getCurrentAdvanceDetails(
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId,
        @Param("ipdRefNo") String ipdRefNo,
        @Param("date") LocalDateTime date
    );
    
    /**
     * Get admission data (Table[2] from USP_Get_Patient_AdmissionCard_data)
     */
    @Query(value = """
        SELECT 
            AD.admission_date AS admissionDate,
            COALESCE(AD.ipdfileno, '') AS ipdFileNo,
            COALESCE(AD.department, '') AS department,
            COALESCE(AD.reasonofadmission, '') AS reasonOfAdmission,
            COALESCE(AD.insurancedetails, '') AS insuranceDetails,
            CASE WHEN AD.isinsurance = true THEN 'Yes' ELSE 'No' END AS isInsurance,
            COALESCE(AD.packageremarks, '') AS packageRemarks,
            COALESCE(DBH.bill_no, '') AS billNo,
            DBH.bill_date AS billDate,
            COALESCE(DIH.invoice_no, '') AS invoiceNo,
            AD.admission_time AS admissionTime,
            DD.discharge_date AS dischargeDate,
            DD.discharge_time AS dischargeTime,
            COALESCE(AD.roomno, '') AS roomNo,
            COALESCE(AD.bedno, '') AS bedNo
        FROM admission_data AD
        LEFT JOIN discharge_bill_hdr DBH ON DBH.ipd_refno = AD.ipd_refno
        LEFT JOIN discharge_invoice_hdr DIH ON DIH.ipd_refno = AD.ipd_refno
        LEFT JOIN discharge_data DD ON DD.ipd_refno = AD.ipd_refno
        WHERE AD.ipd_refno = :ipdRefNo
          AND AD.patient_id = :patientId
        LIMIT 1
        """, nativeQuery = true)
    java.util.Map<String, Object> getAdmissionData(
        @Param("patientId") String patientId,
        @Param("ipdRefNo") String ipdRefNo
    );
    
    /**
     * Get total advance amount (Table[3] from USP_Get_Patient_AdmissionCard_data)
     */
    @Query(value = """
        SELECT COALESCE(SUM(amount_received), 0) AS totalAmount
        FROM advance_collection_details
        WHERE ipd_refno = :ipdRefNo
          AND patient_id = :patientId
        """, nativeQuery = true)
    BigDecimal getTotalAdvanceAmount(
        @Param("patientId") String patientId,
        @Param("ipdRefNo") String ipdRefNo
    );
    
    /**
     * Update receipt number in advance collection details
     * Replicates the UPDATE logic from USP_Insert_AdvanceReceiptDetails when Visit_Type='A'
     * Matches by IPD refno (primary) or by date if IPD refno not provided
     */
    @Modifying
    @Query(value = """
        UPDATE advance_collection_details
        SET receipt_number = :receiptNo,
            receipt_date = CAST(:receiptDate AS TIMESTAMP),
            charges_details = :treatmentDetails
        WHERE patient_id = :patientId
          AND doctor_id = :doctorId
          AND clinic_id = :clinicId
          AND (receipt_number IS NULL OR receipt_number = '')
          AND (
              -- Primary: Match by IPD refno if provided
              (:ipdRefNo IS NOT NULL AND ipd_refno = :ipdRefNo)
              OR
              -- Fallback: Match by date if IPD refno not provided
              (:ipdRefNo IS NULL AND CAST(date AS DATE) = CAST(:date AS DATE))
          )
        """, nativeQuery = true)
    int updateReceiptNumber(
        @Param("receiptNo") String receiptNo,
        @Param("receiptDate") LocalDateTime receiptDate,
        @Param("treatmentDetails") String treatmentDetails,
        @Param("date") LocalDateTime date,
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId,
        @Param("patientId") String patientId,
        @Param("ipdRefNo") String ipdRefNo
    );
}

