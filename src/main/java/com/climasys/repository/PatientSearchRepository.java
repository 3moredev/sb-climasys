package com.climasys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for patient search functionality matching USP_Search_Patient_With_OPD stored procedure
 */
@Repository
public interface PatientSearchRepository extends JpaRepository<com.climasys.entity.Patient, String> {

    /**
     * Result Set 1: Patient Master search
     * Format: ID : FirstName MiddleName LastName : Mobile
     * Filtered by clinic_id for multi-clinic isolation
     */
    @Query(value = """
        SELECT COALESCE(p.id, '') || '   :   ' || 
               COALESCE(p.first_name, '') || ' ' || 
               COALESCE(p.middle_name, '') || ' ' || 
               COALESCE(p.last_name, '') || '   :  ' || 
               COALESCE(p.mobile_1, '') AS search_value
        FROM patient_master p
        WHERE p.clinic_id = :clinicId
          AND (p.id LIKE :searchStr 
           OR p.first_name LIKE :searchStr
           OR p.middle_name LIKE :searchStr
           OR p.last_name LIKE :searchStr
           OR p.mobile_1 LIKE :searchStr
           OR p.mobile_2 LIKE :searchStr
           OR (p.first_name || ' ' || p.last_name) LIKE :searchStr)
        """, nativeQuery = true)
    List<String> searchPatientMaster(@Param("searchStr") String searchStr, @Param("clinicId") String clinicId);

    /**
     * Result Set 2: Discharge Data search
     * Format: IPD_RefNo : ID : FirstName MiddleName LastName : Mobile
     * Filtered by clinic_id for multi-clinic isolation
     */
    @Query(value = """
        SELECT COALESCE(dd.ipd_refno, '') || '   :  ' || 
               COALESCE(p.id, '') || '     :   ' || 
               COALESCE(p.first_name, '') || ' ' || 
               COALESCE(p.middle_name, '') || ' ' || 
               COALESCE(p.last_name, '') || '   :  ' || 
               COALESCE(p.mobile_1, '') AS search_value
        FROM patient_master p
        INNER JOIN discharge_data dd ON dd.patient_id = p.id 
            AND dd.clinic_id = :clinicId
        WHERE p.clinic_id = :clinicId
          AND (p.id LIKE :searchStr 
           OR dd.ipd_refno LIKE :searchStr
           OR p.first_name LIKE :searchStr
           OR p.middle_name LIKE :searchStr
           OR p.last_name LIKE :searchStr
           OR (p.first_name || ' ' || p.last_name) LIKE :searchStr
           OR p.mobile_1 LIKE :searchStr)
        ORDER BY dd.ipd_refno DESC
        """, nativeQuery = true)
    List<String> searchDischargeData(@Param("searchStr") String searchStr, @Param("clinicId") String clinicId);

    /**
     * Result Set 3: Discharge Bill search (unprinted bills only)
     * Format: FirstName LastName : IPD_RefNo : Bill_No : Bill_Date
     * Filtered by clinic_id and doctor_id for multi-clinic isolation
     */
    @Query(value = """
        SELECT pm.first_name || ' ' || pm.last_name || '   :  ' || 
               dd.ipd_refno || '   :  ' || 
               dbh.bill_no || '   :  ' || 
               COALESCE(TO_CHAR(dbh.bill_date, 'DD Mon YYYY'), '') AS search_value
        FROM discharge_bill_hdr dbh
        INNER JOIN discharge_data dd ON dd.ipd_refno = dbh.ipd_refno 
            AND dd.clinic_id = :clinicId
            AND dd.doctor_id = :doctorId
        INNER JOIN patient_master pm ON dbh.patient_id = pm.id 
            AND pm.clinic_id = :clinicId
        WHERE dbh.doctor_id = :doctorId
          AND dbh.clinic_id = :clinicId
          AND COALESCE(dbh.is_printed, 0) = 0
          AND (pm.id LIKE :searchStr 
           OR dbh.bill_no LIKE :searchStr
           OR dd.ipd_refno LIKE :searchStr
           OR pm.first_name LIKE :searchStr
           OR pm.last_name LIKE :searchStr
           OR (pm.first_name || ' ' || pm.last_name) LIKE :searchStr)
        ORDER BY dd.ipd_refno DESC
        """, nativeQuery = true)
    List<String> searchDischargeBills(@Param("searchStr") String searchStr, @Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

    /**
     * Result Set 4: Discharge Invoice search (unprinted invoices only)
     * Format: FirstName LastName : IPD_RefNo : Invoice_No : Invoice_Date
     * Filtered by clinic_id and doctor_id for multi-clinic isolation
     */
    @Query(value = """
        SELECT pm.first_name || ' ' || pm.last_name || '   :  ' || 
               dd.ipd_refno || '   :  ' || 
               dih.invoice_no || '   :  ' || 
               COALESCE(TO_CHAR(dih.invoice_date, 'DD Mon YYYY'), '') AS search_value
        FROM discharge_invoice_hdr dih
        INNER JOIN discharge_data dd ON dd.ipd_refno = dih.ipd_refno 
            AND dd.clinic_id = :clinicId
            AND dd.doctor_id = :doctorId
        INNER JOIN patient_master pm ON dih.patient_id = pm.id 
            AND pm.clinic_id = :clinicId
        WHERE dih.doctor_id = :doctorId
          AND dih.clinic_id = :clinicId
          AND COALESCE(dih.is_printed, 0) = 0
          AND (pm.id LIKE :searchStr 
           OR dih.invoice_no LIKE :searchStr
           OR dd.ipd_refno LIKE :searchStr
           OR pm.first_name LIKE :searchStr
           OR pm.last_name LIKE :searchStr
           OR (pm.first_name || ' ' || pm.last_name) LIKE :searchStr)
        ORDER BY dd.ipd_refno DESC
        """, nativeQuery = true)
    List<String> searchDischargeInvoices(@Param("searchStr") String searchStr, @Param("doctorId") String doctorId, @Param("clinicId") String clinicId);
}

