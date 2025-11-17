package com.climasys.billing.repository;

import com.climasys.entity.PatientVisit;
import com.climasys.entity.PatientVisitId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for fetching payment details from patient_visits
 */
@Repository
public interface PatientVisitPaymentRepository extends JpaRepository<PatientVisit, PatientVisitId> {
    
    /**
     * Find payment details for a specific patient visit
     */
    @Query(value = """
        SELECT 
            pv.payment_remark,
            COALESCE(ptm.payment_description, 'Cash') AS payment_description
        FROM patient_visits pv
        LEFT JOIN payment_type_master ptm ON pv.payment_by_id = ptm.id
        WHERE pv.patient_id = :patientId
            AND pv.clinic_id = :clinicId
            AND pv.doctor_id = :doctorId
            AND pv.shift_id = :shiftId
            AND CAST(pv.visit_date AS DATE) = :visitDate
            AND pv.patient_visit_no = :patientVisitNo
        ORDER BY pv.visit_date DESC, pv.patient_visit_no DESC
        LIMIT 1
        """, nativeQuery = true)
    List<Object[]> findPaymentDetails(
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId,
        @Param("doctorId") String doctorId,
        @Param("shiftId") Short shiftId,
        @Param("visitDate") LocalDate visitDate,
        @Param("patientVisitNo") Integer patientVisitNo
    );
    
    /**
     * Find payment details without patient visit number
     */
    @Query(value = """
        SELECT 
            pv.payment_remark,
            COALESCE(ptm.payment_description, 'Cash') AS payment_description
        FROM patient_visits pv
        LEFT JOIN payment_type_master ptm ON pv.payment_by_id = ptm.id
        WHERE pv.patient_id = :patientId
            AND pv.clinic_id = :clinicId
            AND pv.doctor_id = :doctorId
            AND pv.shift_id = :shiftId
            AND CAST(pv.visit_date AS DATE) = :visitDate
        ORDER BY pv.visit_date DESC, pv.patient_visit_no DESC
        LIMIT 1
        """, nativeQuery = true)
    List<Object[]> findPaymentDetailsWithoutVisitNo(
        @Param("patientId") String patientId,
        @Param("clinicId") String clinicId,
        @Param("doctorId") String doctorId,
        @Param("shiftId") Short shiftId,
        @Param("visitDate") LocalDate visitDate
    );
}

