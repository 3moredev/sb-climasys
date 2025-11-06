package com.climasys.repository;

import com.climasys.entity.PatientVisitBillingInfoOverwrite;
import com.climasys.entity.PatientVisitBillingInfoOverwriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PatientVisitBillingInfoOverwriteRepository extends JpaRepository<PatientVisitBillingInfoOverwrite, PatientVisitBillingInfoOverwriteId> {
    
    /**
     * Soft delete all billing info overwrite records for a specific visit by setting delete_flag = true
     * This replicates the UPDATE logic from USP_Insert_Billing_BreakupData_Overwrite
     */
    @Modifying
    @Query("UPDATE PatientVisitBillingInfoOverwrite pvb SET pvb.deleteFlag = true " +
           "WHERE pvb.doctorId = :doctorId " +
           "AND pvb.clinicId = :clinicId " +
           "AND pvb.shiftId = :shiftId " +
           "AND pvb.patientId = :patientId " +
           "AND pvb.visitDate = :visitDate " +
           "AND pvb.patientVisitNo = :patientVisitNo " +
           "AND (pvb.deleteFlag IS NULL OR pvb.deleteFlag = false)")
    int softDeleteByVisit(
        @Param("doctorId") String doctorId,
        @Param("clinicId") String clinicId,
        @Param("shiftId") Short shiftId,
        @Param("patientId") String patientId,
        @Param("visitDate") LocalDateTime visitDate,
        @Param("patientVisitNo") Integer patientVisitNo
    );
}

