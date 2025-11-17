package com.climasys.billing.repository;

import com.climasys.entity.PatientReceipt;
import com.climasys.entity.PatientReceiptId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for PatientReceipt entity
 */
@Repository
public interface PatientReceiptRepository extends JpaRepository<PatientReceipt, PatientReceiptId> {
    
    /**
     * Find receipt details for a specific patient visit
     * Replicates the first query from USP_Get_PatientReceiptData_For_Receipt
     */
    @Query(value = """
        SELECT 
            pr.receipt_number,
            pr.receipt_date,
            pr.receipt_type,
            pr.receipt_amount,
            pr.treatment_details,
            pr.title,
            pt.title_description,
            pr.to_date,
            pr.from_date,
            COALESCE(pv.discount, 0) AS discount
        FROM patient_receipts pr
        INNER JOIN patient_title pt ON pr.title = pt.id
        LEFT JOIN patient_visits pv ON 
            pv.receipt_number = pr.receipt_number
            AND pv.patient_id = pr.patient_id
            AND pv.clinic_id = pr.clinic_id
            AND pv.doctor_id = pr.doctor_id
            AND pv.shift_id = pr.shift_id
        WHERE pr.patient_id = :patientId
            AND pr.clinic_id = :clinicId
            AND pr.doctor_id = :doctorId
            AND CAST(pr.receipt_date AS DATE) = :visitDate
            AND pr.shift_id = :shiftId
            AND pv.patient_visit_no = :patientVisitNo
        ORDER BY pr.receipt_date DESC
        LIMIT 1
        """, nativeQuery = true)
    List<Object[]> findReceiptDetails(
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId,
        @Param("doctorId") String doctorId,
        @Param("visitDate") LocalDate visitDate,
        @Param("shiftId") Short shiftId,
        @Param("patientVisitNo") Integer patientVisitNo
    );
    
    /**
     * Find receipt details without patient visit number
     */
    @Query(value = """
        SELECT 
            pr.receipt_number,
            pr.receipt_date,
            pr.receipt_type,
            pr.receipt_amount,
            pr.treatment_details,
            pr.title,
            pt.title_description,
            pr.to_date,
            pr.from_date,
            COALESCE(pv.discount, 0) AS discount
        FROM patient_receipts pr
        INNER JOIN patient_title pt ON pr.title = pt.id
        LEFT JOIN patient_visits pv ON 
            pv.receipt_number = pr.receipt_number
            AND pv.patient_id = pr.patient_id
            AND pv.clinic_id = pr.clinic_id
            AND pv.doctor_id = pr.doctor_id
            AND pv.shift_id = pr.shift_id
        WHERE pr.patient_id = :patientId
            AND pr.clinic_id = :clinicId
            AND pr.doctor_id = :doctorId
            AND CAST(pr.receipt_date AS DATE) = :visitDate
            AND pr.shift_id = :shiftId
        ORDER BY pr.receipt_date DESC
        LIMIT 1
        """, nativeQuery = true)
    List<Object[]> findReceiptDetailsWithoutVisitNo(
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId,
        @Param("doctorId") String doctorId,
        @Param("visitDate") LocalDate visitDate,
        @Param("shiftId") Short shiftId
    );
}

