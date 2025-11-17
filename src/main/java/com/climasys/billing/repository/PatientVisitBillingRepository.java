package com.climasys.billing.repository;

import com.climasys.entity.PatientVisit;
import com.climasys.entity.PatientVisitId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Repository for PatientVisit entity - billing operations
 * Named differently to avoid conflict with existing PatientVisitRepository
 */
@Repository
public interface PatientVisitBillingRepository extends JpaRepository<PatientVisit, PatientVisitId> {
    
    /**
     * Update payment details for a patient visit
     */
    @Modifying
    @Query(value = """
        UPDATE patient_visits
        SET payment_by_id = :paymentById,
            payment_remark = :paymentRemark,
            fees_collected = :feesCollected,
            receipt_number = :receiptNumber,
            modified_on = CURRENT_TIMESTAMP,
            modifiedby_name = :modifiedByName
        WHERE patient_id = :patientId
            AND clinic_id = :clinicId
            AND doctor_id = :doctorId
            AND shift_id = :shiftId
            AND CAST(visit_date AS DATE) = :visitDate
            AND patient_visit_no = :patientVisitNo
        """, nativeQuery = true)
    int updatePaymentDetails(
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId,
        @Param("doctorId") String doctorId,
        @Param("shiftId") Short shiftId,
        @Param("visitDate") LocalDate visitDate,
        @Param("patientVisitNo") Integer patientVisitNo,
        @Param("paymentById") Short paymentById,
        @Param("paymentRemark") String paymentRemark,
        @Param("feesCollected") BigDecimal feesCollected,
        @Param("receiptNumber") String receiptNumber,
        @Param("modifiedByName") String modifiedByName
    );
    
    /**
     * Find a patient visit by composite key fields
     */
    @Query(value = """
        SELECT *
        FROM patient_visits
        WHERE patient_id = :patientId
            AND clinic_id = :clinicId
            AND doctor_id = :doctorId
            AND shift_id = :shiftId
            AND CAST(visit_date AS DATE) = :visitDate
            AND patient_visit_no = :patientVisitNo
        LIMIT 1
        """, nativeQuery = true)
    PatientVisit findByCompositeKey(
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId,
        @Param("doctorId") String doctorId,
        @Param("shiftId") Short shiftId,
        @Param("visitDate") LocalDate visitDate,
        @Param("patientVisitNo") Integer patientVisitNo
    );
}

