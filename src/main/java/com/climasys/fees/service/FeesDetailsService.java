package com.climasys.fees.service;

import com.climasys.repository.FeeDetailsRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FeesDetailsService {

    private final FeeDetailsRepository feeDetailsRepository;
    private final JdbcTemplate jdbcTemplate;

    public FeesDetailsService(FeeDetailsRepository feeDetailsRepository, JdbcTemplate jdbcTemplate) {
        this.feeDetailsRepository = feeDetailsRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * JPA/JDBC equivalent for USP_Get_Patient_FeesDetails
     * Returns patient fees details for individual visits
     * 
     * @param patientId Patient ID (required)
     * @param doctorId Doctor ID (optional, filters by doctor if provided)
     * @param clinicId Clinic ID (required, filters by clinic)
     * @return Map with success, patientId, header (folder_no, full_name), and rows (list of fee details)
     */
    public Map<String, Object> getPatientFeesDetails(String patientId, String doctorId, String clinicId) {
        List<Object[]> rows = feeDetailsRepository.findFeesDetailsByPatientId(patientId, doctorId, clinicId);
        List<Map<String, Object>> data = new ArrayList<>();

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        for (Object[] r : rows) {
            int i = 0;
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("Patient_ID", r[i++]);
            m.put("Full_Name", r[i++]);
            m.put("Patient_Visit_No", r[i++]);
            Object visitDate = r[i++];
            m.put("Visit_Date", visitDate);
            m.put("Bill", r[i++]);
            m.put("Collected", r[i++]);
            m.put("Folder_No", r[i++]);
            m.put("Balance", r[i++]);
            m.put("Discount", r[i++]);
            m.put("Dues", r[i++]);
            String visitTimeText = Objects.toString(r[i++], "");
            String shiftInitial = Objects.toString(r[i++], "");
            m.put("Status_Description", r[i++]);
            m.put("ISadhoc", r[i++]);
            String receiptNumber = Objects.toString(r[i++], "");
            String receiptType = Objects.toString(r[i++], "");
            m.put("DoctorName", r[i++]);

            String lastVisitDate;
            if (visitDate instanceof java.sql.Timestamp ts) {
                LocalDate d = ts.toLocalDateTime().toLocalDate();
                lastVisitDate = dateFmt.format(d) + " - " + visitTimeText + " - " + shiftInitial;
            } else if (visitDate instanceof java.sql.Date d) {
                LocalDate ld = d.toLocalDate();
                lastVisitDate = dateFmt.format(ld) + " - " + visitTimeText + " - " + shiftInitial;
            } else if (visitDate instanceof LocalDateTime ldt) {
                lastVisitDate = dateFmt.format(ldt.toLocalDate()) + " - " + visitTimeText + " - " + shiftInitial;
            } else {
                lastVisitDate = Objects.toString(visitDate, "") + " - " + visitTimeText + " - " + shiftInitial;
            }
            m.put("LAST_VISIT_DATE", lastVisitDate);
            m.put("Receipt_Number", (receiptType + " " + receiptNumber).trim());

            data.add(m);
        }

        Map<String, Object> header = new HashMap<>();
        List<Object[]> folderName = feeDetailsRepository.findFolderAndName(patientId);
        if (!folderName.isEmpty()) {
            header.put("Folder_No", folderName.get(0)[0]);
            header.put("Full_Name", folderName.get(0)[1]);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("patientId", patientId);
        response.put("doctorId", doctorId);
        response.put("clinicId", clinicId);
        response.put("header", header);
        response.put("rows", data);
        return response;
    }

    /**
     * JPA/JDBC equivalent for USP_Get_ConsolidatedFamilyFees
     * Returns consolidated fees aggregated by financial year
     * 
     * @param doctorId Doctor ID (optional, filters by doctor if provided)
     * @param clinicId Clinic ID (required, filters by clinic)
     * @return Map with success, doctorId, clinicId, and rows (list of financial year summaries)
     */
    public Map<String, Object> getConsolidatedFamilyFees(String doctorId, String clinicId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Object[]> rows = feeDetailsRepository.findConsolidatedFamilyFees(doctorId, clinicId);
            List<Map<String, Object>> data = new ArrayList<>();

            for (Object[] r : rows) {
                int i = 0;
                Map<String, Object> m = new LinkedHashMap<>();
                Object financialYearObj = r[i++];
                Integer financialYear = financialYearObj != null ? 
                    (financialYearObj instanceof Integer ? (Integer) financialYearObj : Integer.valueOf(financialYearObj.toString())) : null;
                m.put("Financial_Year", financialYear);
                
                Object billedObj = r[i++];
                m.put("Billed", billedObj != null ? ((Number) billedObj).doubleValue() : 0.0);
                
                Object discountObj = r[i++];
                m.put("Discount", discountObj != null ? ((Number) discountObj).doubleValue() : 0.0);
                
                Object duesObj = r[i++];
                m.put("Dues", duesObj != null ? ((Number) duesObj).doubleValue() : 0.0);
                
                Object collectedObj = r[i++];
                m.put("Collected", collectedObj != null ? ((Number) collectedObj).doubleValue() : 0.0);
                
                Object balanceObj = r[i++];
                m.put("Balance", balanceObj != null ? ((Number) balanceObj).doubleValue() : 0.0);
                
                data.add(m);
            }

            response.put("success", true);
            response.put("doctorId", doctorId);
            response.put("clinicId", clinicId);
            response.put("rows", data);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to get consolidated family fees: " + e.getMessage());
        }
        return response;
    }

    /**
     * JPA/JDBC equivalent for USP_Get_PatientFolderAmountForBilling.
     * Computes folder-level billed, collected, discount and dues for a specific visit.
     * Returns: { success, clinicId, doctorId, folderNo, patientVisitNo, billed, collected, discount, dues, patientId }
     */
    public Map<String, Object> getPatientFolderAmountForBilling(String clinicId, String doctorId, String folderNo, Integer patientVisitNo) {
        Map<String, Object> res = new LinkedHashMap<>();
        try {
            // Resolve patient_id from folder number
            String patientIdSql = "SELECT id FROM patient_master WHERE folder_no = ? LIMIT 1";
            List<Map<String, Object>> pid = jdbcTemplate.queryForList(patientIdSql, folderNo);
            if (pid.isEmpty()) {
                res.put("success", false);
                res.put("error", "Folder not found");
                return res;
            }
            String patientId = Objects.toString(pid.get(0).get("id"), null);

            // Fetch the visit row matching patient + clinic + doctor + visitNo (latest date for that visitNo)
            String visitSql = """
                SELECT fees_to_collect AS billed,
                       COALESCE(fees_collected, 0) AS collected,
                       COALESCE(discount, 0) AS discount
                  FROM patient_visits
                 WHERE patient_id = ? AND clinic_id = ? AND doctor_id = ?
                   AND patient_visit_no = ? AND COALESCE(delete_flag,false) = false
                 ORDER BY visit_date DESC
                 LIMIT 1
            """;
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(visitSql, patientId, clinicId, doctorId, patientVisitNo);

            double billed = 0.0;
            double collected = 0.0;
            double discount = 0.0;
            if (!rows.isEmpty()) {
                Map<String, Object> r = rows.get(0);
                billed = r.get("billed") == null ? 0.0 : ((Number) r.get("billed")).doubleValue();
                collected = r.get("collected") == null ? 0.0 : ((Number) r.get("collected")).doubleValue();
                discount = r.get("discount") == null ? 0.0 : ((Number) r.get("discount")).doubleValue();
            }

            double dues = (billed - discount) - collected;

            res.put("success", true);
            res.put("clinicId", clinicId);
            res.put("doctorId", doctorId);
            res.put("folderNo", folderNo);
            res.put("patientVisitNo", patientVisitNo);
            res.put("patientId", patientId);
            res.put("billed", billed);
            res.put("collected", collected);
            res.put("discount", discount);
            res.put("dues", dues);
            return res;
        } catch (Exception e) {
            res.put("success", false);
            res.put("error", e.getMessage());
            return res;
        }
    }
}


