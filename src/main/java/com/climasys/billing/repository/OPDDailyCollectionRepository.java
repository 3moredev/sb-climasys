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
 * Repository for OPD Daily Collection operations
 * Uses JPA queries instead of stored procedures
 */
@Repository
public interface OPDDailyCollectionRepository extends JpaRepository<PatientVisit, PatientVisitId> {
    
    /**
     * Get OPD Daily Collection data for completed visits (Status_ID = 5)
     * This query replicates the main part of USP_Get_OPDDailyCollection_For_Operator
     */
    @Query(value = """
        SELECT 
            TO_CHAR(PV.visit_date, 'DD Mon YYYY') || CASE WHEN PV.visit_time IS NOT NULL THEN ' ' || TO_CHAR(PV.visit_time, 'HH24:MI:SS') ELSE '' END as visitDate,
            PM.first_name || ' ' || COALESCE(PM.middle_name, '') || ' ' || PM.last_name as name,
            PV.patient_id as patientId,
            SR.status_description as statusDescription,
            SR.id as statusId,
            PV.fees_to_collect as feesToCollect,
            PV.fees_collected as feesCollected,
            0.00 as adhocFees,
            PV.original_billed_amount as originalBilledAmount,
            PV.folder_no as folderNo,
            PV.comment as comment,
            PV.original_billed_amount - PV.fees_to_collect as difference,
            (PV.fees_to_collect - COALESCE(PV.discount, 0)) - PV.fees_collected as dues,
            PV.original_discount as originalDiscount,
            PV.discount as discount,
            PV.fees_to_collect - COALESCE(PV.discount, 0) as net,
            COALESCE(PV.in_person, false) as inPerson,
            CASE 
                WHEN SR.id IN (1, 2, 4, 5, 11) THEN DM.prefix || ' ' || DM.first_name
                ELSE ''
            END as attendedBy,
            PV.payment_by_id as paymentById,
            PV.payment_remark as paymentRemark,
            PD.payment_description as paymentDescription,
            PM.first_name || ' ' || PM.last_name as partialName,
            CAST(EXTRACT(YEAR FROM AGE(CURRENT_DATE, PM.date_of_birth)) AS INTEGER) as ageYearsIntRound,
            GT.gender_description as genderDescription,
            PV.patient_visit_no as patientVisitNo,
            PV.doctor_id as doctorId,
            DM.prefix || ' ' || DM.first_name as doctorName,
            CASE 
                WHEN PV.is_follow_up IS NULL OR PV.is_follow_up = false THEN 'New'
                ELSE 'Follow up'
            END as isFollowUp,
            NULL as baseLocation
        FROM patient_visits PV
        INNER JOIN patient_master PM ON PM.id = PV.patient_id AND PM.clinic_id = PV.clinic_id
        LEFT JOIN patient_visits PV1 ON PV.doctor_id = PV1.doctor_id 
            AND PV.patient_id = PV1.patient_id 
            AND PV.patient_last_visit_no = PV1.patient_visit_no
        INNER JOIN status_ref SR ON PV.status_id = SR.id AND PV.clinic_id = SR.clinic_id
        INNER JOIN status_order SO ON SR.id = SO.status_id AND SR.doctor_id = SO.doctor_id
        LEFT JOIN payment_type_master PD ON PD.id = PV.payment_by_id
        INNER JOIN gender_translations GT ON PM.gender_id = GT.gender_id
        INNER JOIN doctor_master DM ON DM.doctor_id = PV.doctor_id
        WHERE DATE(PV.visit_date) BETWEEN :fromDate AND :toDate
            AND PV.clinic_id = :clinicId
            AND (:doctorId IS NULL OR PV.doctor_id = :doctorId)
            AND SO.role_id = :roleId
            AND PV.delete_flag = false
            AND GT.language_id = :languageId
            AND SR.id NOT IN (12, 11)
            AND PV.status_id = 5
        ORDER BY PV.visit_date
        """, nativeQuery = true)
    List<Object[]> findOPDDailyCollectionVisits(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("clinicId") String clinicId,
            @Param("doctorId") String doctorId,
            @Param("roleId") Integer roleId,
            @Param("languageId") Integer languageId
    );
    
    /**
     * Get adhoc payments for the date range
     * This replicates the UNION ALL part of the stored procedure
     */
    @Query(value = """
        SELECT 
            TO_CHAR(PPA.payment_date, 'DD Mon YYYY') || CASE WHEN CAST(PPA.payment_date AS TIME) IS NOT NULL THEN ' ' || TO_CHAR(CAST(PPA.payment_date AS TIME), 'HH24:MI:SS') ELSE '' END as visitDate,
            PM.first_name || ' ' || COALESCE(PM.middle_name, '') || ' ' || PM.last_name as name,
            PPA.patient_id as patientId,
            NULL as statusDescription,
            NULL as statusId,
            NULL as feesToCollect,
            NULL as feesCollected,
            PPA.fees_collected as adhocFees,
            NULL as originalBilledAmount,
            PM.folder_no as folderNo,
            PPA.comment as comment,
            NULL as difference,
            NULL as dues,
            NULL as originalDiscount,
            NULL as discount,
            NULL as net,
            false as inPerson,
            PPA.attended_by as attendedBy,
            PPA.payment_by_id as paymentById,
            PPA.payment_remark as paymentRemark,
            PD.payment_description as paymentDescription,
            PM.first_name || ' ' || PM.last_name as partialName,
            CAST(EXTRACT(YEAR FROM AGE(CURRENT_DATE, PM.date_of_birth)) AS INTEGER) as ageYearsIntRound,
            GT.gender_description as genderDescription,
            0 as patientVisitNo,
            PPA.doctor_id as doctorId,
            DM.prefix || ' ' || DM.first_name as doctorName,
            'Adhoc - Followup' as isFollowUp,
            NULL as baseLocation
        FROM patient_payments_adhoc PPA
        INNER JOIN patient_master PM ON PM.id = PPA.patient_id
        LEFT JOIN payment_type_master PD ON PD.id = PPA.payment_by_id
        INNER JOIN gender_translations GT ON PM.gender_id = GT.gender_id
        INNER JOIN doctor_master DM ON DM.doctor_id = PPA.doctor_id
        WHERE DATE(PPA.created_on) BETWEEN :fromDate AND :toDate
            AND PPA.clinic_id = :clinicId
            AND (:doctorId IS NULL OR PPA.doctor_id = :doctorId)
            AND GT.language_id = :languageId
            AND COALESCE(PPA.delete_flag, false) = false
        ORDER BY PPA.payment_date
        """, nativeQuery = true)
    List<Object[]> findOPDDailyCollectionAdhocPayments(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("clinicId") String clinicId,
            @Param("doctorId") String doctorId,
            @Param("languageId") Integer languageId
    );
}

