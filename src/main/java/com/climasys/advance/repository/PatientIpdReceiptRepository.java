package com.climasys.advance.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Repository for patient_ipd_receipts table
 * Used for advance collection receipt operations
 */
@Repository
public class PatientIpdReceiptRepository {
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    /**
     * Check if receipt exists
     * Used in USP_Insert_AdvanceReceiptDetails to determine if receipt should be generated
     */
    public boolean receiptExists(
            LocalDateTime paymentDate,
            String doctorId,
            String clinicId,
            String patientId,
            Short shiftId,
            String receiptNo
    ) {
        String sql = """
            SELECT COUNT(*) > 0
            FROM patient_ipd_receipts
            WHERE receipt_date = CAST(:paymentDate AS DATE)
              AND doctor_id = :doctorId
              AND clinic_id = :clinicId
              AND patient_id = :patientId
              AND shift_id = :shiftId
              AND receipt_number = :receiptNo
              AND COALESCE(visit_type, '') = 'A'
            """;
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("paymentDate", paymentDate)
                .addValue("doctorId", doctorId)
                .addValue("clinicId", clinicId)
                .addValue("patientId", patientId)
                .addValue("shiftId", shiftId)
                .addValue("receiptNo", receiptNo);
        
        Boolean result = namedParameterJdbcTemplate.queryForObject(sql, params, Boolean.class);
        return result != null && result;
    }
    
    /**
     * Get receipt details for printing (Table[0] from USP_Get_PatientHospitalBillReceiptData)
     */
    public Map<String, Object> getReceiptDetails(
            String patientId,
            String clinicId,
            String doctorId,
            String receiptNo,
            String visitType
    ) {
        String sql = """
            SELECT 
                pir.receipt_number AS receiptNumber,
                CAST(pir.receipt_date AS DATE) AS receiptDate,
                pir.receipt_type AS receiptType,
                pir.receipt_amount AS receiptAmount,
                COALESCE(pir.treatment_details, '') AS treatmentDetails,
                pir.title AS title,
                COALESCE(pt.title_description, '') AS titleDescription,
                CAST(pir.to_date AS DATE) AS toDate,
                CAST(pir.from_date AS DATE) AS fromDate
            FROM patient_ipd_receipts pir
            INNER JOIN patient_title pt ON pir.title = pt.id
            WHERE pir.patient_id = :patientId
              AND pir.clinic_id = :clinicId
              AND pir.doctor_id = :doctorId
              AND pir.receipt_number = :receiptNo
              AND pir.visit_type = :visitType
            LIMIT 1
            """;
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("patientId", patientId)
                .addValue("clinicId", clinicId)
                .addValue("doctorId", doctorId)
                .addValue("receiptNo", receiptNo)
                .addValue("visitType", visitType);
        
        try {
            return namedParameterJdbcTemplate.queryForMap(sql, params);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Get payment details from Discharge_Bill_Hdr (Table[1] from USP_Get_PatientHospitalBillReceiptData)
     */
    public Map<String, Object> getBillPaymentDetails(
            String patientId,
            String clinicId,
            String receiptNo,
            String billNo
    ) {
        String sql = """
            SELECT 
                dbh.collected_amount AS amount,
                COALESCE(ptm.payment_description, '') AS paymentDescription,
                COALESCE(dbh.payment_remark, '') AS paymentRemark
            FROM discharge_bill_hdr dbh
            LEFT JOIN payment_type_master ptm ON ptm.id = dbh.payment_by_id
            WHERE dbh.patient_id = :patientId
              AND dbh.clinic_id = :clinicId
              AND dbh.receipt_number = :receiptNo
              AND dbh.bill_no = :billNo
            LIMIT 1
            """;
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("patientId", patientId)
                .addValue("clinicId", clinicId)
                .addValue("receiptNo", receiptNo)
                .addValue("billNo", billNo);
        
        try {
            return namedParameterJdbcTemplate.queryForMap(sql, params);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Check if bill payment exists
     */
    public boolean billPaymentExists(
            String patientId,
            String clinicId,
            String receiptNo,
            String billNo
    ) {
        String sql = """
            SELECT COUNT(*) > 0
            FROM discharge_bill_hdr dbh
            INNER JOIN payment_type_master ptm ON ptm.id = dbh.payment_by_id
            WHERE dbh.patient_id = :patientId
              AND dbh.clinic_id = :clinicId
              AND dbh.receipt_number = :receiptNo
              AND dbh.bill_no = :billNo
            """;
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("patientId", patientId)
                .addValue("clinicId", clinicId)
                .addValue("receiptNo", receiptNo)
                .addValue("billNo", billNo);
        
        Boolean result = namedParameterJdbcTemplate.queryForObject(sql, params, Boolean.class);
        return result != null && result;
    }
    
    /**
     * Get payment details from Advance_Collection_details (Table[2] from USP_Get_PatientHospitalBillReceiptData)
     */
    public Map<String, Object> getAdvancePaymentDetails(
            String patientId,
            String clinicId,
            String receiptNo
    ) {
        String sql = """
            SELECT 
                acd.amount_received AS amount,
                COALESCE(ptm.payment_description, '') AS paymentDescription,
                COALESCE(acd.payment_remark, '') AS paymentRemark
            FROM advance_collection_details acd
            LEFT JOIN payment_type_master ptm ON ptm.id = acd.payment_by_id
            WHERE acd.patient_id = :patientId
              AND acd.clinic_id = :clinicId
              AND acd.receipt_number = :receiptNo
            LIMIT 1
            """;
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("patientId", patientId)
                .addValue("clinicId", clinicId)
                .addValue("receiptNo", receiptNo);
        
        try {
            return namedParameterJdbcTemplate.queryForMap(sql, params);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Check if advance payment exists
     */
    public boolean advancePaymentExists(
            String patientId,
            String clinicId,
            String receiptNo
    ) {
        String sql = """
            SELECT COUNT(*) > 0
            FROM advance_collection_details acd
            INNER JOIN payment_type_master ptm ON ptm.id = acd.payment_by_id
            WHERE acd.patient_id = :patientId
              AND acd.clinic_id = :clinicId
              AND acd.receipt_number = :receiptNo
            """;
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("patientId", patientId)
                .addValue("clinicId", clinicId)
                .addValue("receiptNo", receiptNo);
        
        Boolean result = namedParameterJdbcTemplate.queryForObject(sql, params, Boolean.class);
        return result != null && result;
    }
    
    /**
     * Check if receipt record exists for update
     */
    public boolean receiptRecordExists(
            String doctorId,
            String clinicId,
            String patientId,
            String receiptNo,
            Short shiftId
    ) {
        String sql = """
            SELECT COUNT(*) > 0
            FROM patient_ipd_receipts
            WHERE doctor_id = :doctorId
              AND clinic_id = :clinicId
              AND patient_id = :patientId
              AND receipt_number = :receiptNo
              AND shift_id = :shiftId
            """;
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("doctorId", doctorId)
                .addValue("clinicId", clinicId)
                .addValue("patientId", patientId)
                .addValue("receiptNo", receiptNo)
                .addValue("shiftId", shiftId);
        
        Boolean result = namedParameterJdbcTemplate.queryForObject(sql, params, Boolean.class);
        return result != null && result;
    }
    
    /**
     * Insert or update receipt using check-then-update-or-insert pattern
     * Replicates the MERGE logic from USP_Insert_AdvanceReceiptDetails
     */
    public void upsertReceipt(
            String doctorId,
            String clinicId,
            String patientId,
            String receiptNo,
            LocalDateTime paymentDate,
            String receiptType,
            java.math.BigDecimal receiptAmount,
            String userId,
            Short shiftId,
            String treatmentDetails,
            Short title,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        // Convert LocalDate to LocalDateTime for database
        // Use paymentDate as fallback if fromDate/toDate are null (columns are NOT NULL)
        LocalDateTime paymentDateOnly = paymentDate != null ? paymentDate.toLocalDate().atStartOfDay() : LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : paymentDateOnly;
        LocalDateTime toDateTime = toDate != null ? toDate.atStartOfDay() : paymentDateOnly;
        
        // Check if record exists
        boolean exists = receiptRecordExists(doctorId, clinicId, patientId, receiptNo, shiftId);
        
        if (exists) {
            // Update existing record
            String updateSql = """
                UPDATE patient_ipd_receipts
                SET receipt_amount = :receiptAmount,
                    modified_on = CURRENT_TIMESTAMP,
                    modifiedby_name = :userId,
                    treatment_details = :treatmentDetails,
                    title = :title,
                    from_date = :fromDate,
                    to_date = :toDate
                WHERE doctor_id = :doctorId
                  AND clinic_id = :clinicId
                  AND patient_id = :patientId
                  AND receipt_number = :receiptNo
                  AND shift_id = :shiftId
                """;
            
            MapSqlParameterSource updateParams = new MapSqlParameterSource()
                    .addValue("receiptAmount", receiptAmount)
                    .addValue("userId", userId)
                    .addValue("treatmentDetails", treatmentDetails)
                    .addValue("title", title)
                    .addValue("fromDate", fromDateTime)
                    .addValue("toDate", toDateTime)
                    .addValue("doctorId", doctorId)
                    .addValue("clinicId", clinicId)
                    .addValue("patientId", patientId)
                    .addValue("receiptNo", receiptNo)
                    .addValue("shiftId", shiftId);
            
            namedParameterJdbcTemplate.update(updateSql, updateParams);
        } else {
            // Insert new record
            String insertSql = """
                INSERT INTO patient_ipd_receipts (
                    doctor_id, clinic_id, patient_id, receipt_number, receipt_date,
                    receipt_type, receipt_amount, created_on, createdby_name,
                    modified_on, modifiedby_name, shift_id, treatment_details,
                    title, from_date, to_date, visit_type
                ) VALUES (
                    :doctorId, :clinicId, :patientId, :receiptNo, CAST(:paymentDate AS DATE),
                    :receiptType, :receiptAmount, CURRENT_TIMESTAMP, :userId,
                    CURRENT_TIMESTAMP, :userId, :shiftId, :treatmentDetails,
                    :title, :fromDate, :toDate, 'A'
                )
                """;
            
            MapSqlParameterSource insertParams = new MapSqlParameterSource()
                    .addValue("doctorId", doctorId)
                    .addValue("clinicId", clinicId)
                    .addValue("patientId", patientId)
                    .addValue("receiptNo", receiptNo)
                    .addValue("paymentDate", paymentDate)
                    .addValue("receiptType", receiptType)
                    .addValue("receiptAmount", receiptAmount)
                    .addValue("userId", userId)
                    .addValue("shiftId", shiftId)
                    .addValue("treatmentDetails", treatmentDetails)
                    .addValue("title", title)
                    .addValue("fromDate", fromDateTime)
                    .addValue("toDate", toDateTime);
            
            namedParameterJdbcTemplate.update(insertSql, insertParams);
        }
    }
}

