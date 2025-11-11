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
     * Returns consolidated fees aggregated by financial year for a specific patient
     * 
     * @param patientId Patient ID (required)
     * @param doctorId Doctor ID (optional, filters by doctor if provided)
     * @param clinicId Clinic ID (required, filters by clinic)
     * @return Map with success, patientId, doctorId, clinicId, and rows (list of financial year summaries)
     */
    public Map<String, Object> getConsolidatedFamilyFees(String patientId, String doctorId, String clinicId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Object[]> rows = feeDetailsRepository.findConsolidatedFamilyFees(patientId, doctorId, clinicId);
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
            response.put("patientId", patientId);
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
     * JPA/JDBC equivalent for USP_Get_PatientFolderAmount.
     * Returns ALL visits and adhoc payments for a patient to calculate total A/C balance.
     * This matches the exact stored procedure logic including:
     * - Patient_Visits (status_id=5)
     * - Patient_Visits_Services (status_id=8)
     * - Patient_Payments_AdHoc (advance payments)
     * 
     * Note: The stored procedure uses Folder_No as parameter, but this method accepts patientId
     * and derives folderNo from patient_master. Alternatively, you can pass folderNo directly.
     * 
     * Returns: { success, clinicId, doctorId, patientId, folderNo, rows: [ { Patient_ID, Full_Name, Patient_Visit_No, 
     *          Visit_Date, Financial_Year, Bill, Collected, Balance, Discount, Dues, Folder_No }, ... ], totalAcBalance }
     */
    public Map<String, Object> getPatientFolderAmountForBilling(String clinicId, String doctorId, String patientId) {
        Map<String, Object> res = new LinkedHashMap<>();
        try {
            // First, get folder_no from patient_master if patientId is provided
            String folderNo = null;
            if (patientId != null && !patientId.trim().isEmpty()) {
                String folderSql = "SELECT folder_no FROM patient_master WHERE id = ? AND clinic_id = ?";
                List<Map<String, Object>> folderRows = jdbcTemplate.queryForList(folderSql, patientId, clinicId);
                if (!folderRows.isEmpty()) {
                    folderNo = (String) folderRows.get(0).get("folder_no");
                }
            }
            
            if (folderNo == null || folderNo.trim().isEmpty()) {
                res.put("success", false);
                res.put("error", "Folder number not found for patient: " + patientId);
                return res;
            }
            
            // Query matching the exact stored procedure USP_Get_PatientFolderAmount
            // Part 1: Patient_Visits (status_id=5)
            // Part 2: Patient_Visits_Services (status_id=8)
            // Part 3: Patient_Payments_AdHoc (advance payments)
            String visitSql = """
                WITH FeesCollectionData AS (
                    -- Part 1: Patient_Visits (status_id=5)
                    SELECT pv.patient_id AS Patient_ID,
                           (pm.first_name || ' ' || pm.last_name) AS Full_Name,
                           pv.patient_visit_no AS Patient_Visit_No,
                           pv.visit_date AS Visit_Date,
                           pm.folder_no AS Folder_No,
                           pv.doctor_id AS Doctor_ID,
                           pv.financial_year AS Financial_Year,
                           pv.fees_to_collect AS Bill,
                           COALESCE(pv.fees_collected, 0) AS Collected,
                           ((pv.fees_to_collect - COALESCE(pv.discount, 0)) - COALESCE(pv.fees_collected, 0)) AS Balance,
                           COALESCE(pv.discount, 0) AS Discount,
                           (pv.fees_to_collect - COALESCE(pv.discount, 0)) AS Dues
                      FROM patient_master pm
                      INNER JOIN patient_visits pv
                        ON pm.id = pv.patient_id
                       AND pm.clinic_id = pv.clinic_id
                     WHERE pm.clinic_id = ?
                       AND pm.folder_no = ?
                       AND COALESCE(pv.delete_flag, false) = false
                       AND pv.fees_to_collect IS NOT NULL
                       AND pv.status_id = 5
                    
                    UNION ALL
                    
                    -- Part 2: Patient_Visits_Services (status_id=8)
                    SELECT pv.patient_id AS Patient_ID,
                           (pm.first_name || ' ' || pm.last_name) AS Full_Name,
                           pv.patient_visit_no AS Patient_Visit_No,
                           pv.visit_date AS Visit_Date,
                           pm.folder_no AS Folder_No,
                           pv.doctor_id AS Doctor_ID,
                           pv.financial_year AS Financial_Year,
                           pv.fees_to_collect AS Bill,
                           COALESCE(pv.fees_collected, 0) AS Collected,
                           ((pv.fees_to_collect - COALESCE(pv.discount, 0)) - COALESCE(pv.fees_collected, 0)) AS Balance,
                           COALESCE(pv.discount, 0) AS Discount,
                           (pv.fees_to_collect - COALESCE(pv.discount, 0)) AS Dues
                      FROM patient_master pm
                      INNER JOIN patient_visits_services pv
                        ON pm.id = pv.patient_id
                       AND pm.clinic_id = pv.clinic_id
                     WHERE pm.clinic_id = ?
                       AND pm.folder_no = ?
                       AND COALESCE(pv.delete_flag, false) = false
                       AND pv.fees_to_collect IS NOT NULL
                       AND pv.status_id = 8
                    
                    UNION ALL
                    
                    -- Part 3: Patient_Payments_AdHoc (advance payments)
                    SELECT pv.patient_id AS Patient_ID,
                           (pm.first_name || ' ' || pm.last_name) AS Full_Name,
                           0 AS Patient_Visit_No,
                           pv.payment_date AS Visit_Date,
                           pm.folder_no AS Folder_No,
                           pv.doctor_id AS Doctor_ID,
                           pv.financial_year AS Financial_Year,
                           0 AS Bill,
                           COALESCE(pv.fees_collected, 0) AS Collected,
                           (0 - COALESCE(pv.fees_collected, 0)) AS Balance,
                           0 AS Discount,
                           0 AS Dues
                      FROM patient_payments_adhoc pv
                      INNER JOIN patient_master pm
                        ON pm.id = pv.patient_id
                       AND pm.clinic_id = pv.clinic_id
                     WHERE pm.clinic_id = ?
                       AND pm.folder_no = ?
                       AND COALESCE(pv.delete_flag, false) = false
                       AND pv.fees_collected IS NOT NULL
                )
                SELECT Patient_ID, Full_Name, Patient_Visit_No, Visit_Date, Financial_Year,
                       Bill, Collected, Folder_No, Balance, Discount, Dues
                  FROM FeesCollectionData
                 ORDER BY Visit_Date ASC
            """;
            
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                visitSql, clinicId, folderNo, clinicId, folderNo, clinicId, folderNo);
            
            List<Map<String, Object>> data = new ArrayList<>();
            
            for (Map<String, Object> row : rows) {
                Map<String, Object> visit = new LinkedHashMap<>();
                visit.put("Patient_ID", row.get("Patient_ID"));
                visit.put("Full_Name", row.get("Full_Name"));
                visit.put("Patient_Visit_No", row.get("Patient_Visit_No"));
                visit.put("Visit_Date", row.get("Visit_Date"));
                visit.put("Financial_Year", row.get("Financial_Year"));
                visit.put("Folder_No", row.get("Folder_No"));
                
                // Convert numeric values to double for consistency
                Object billObj = row.get("Bill");
                visit.put("Bill", billObj != null ? ((Number) billObj).doubleValue() : 0.0);
                
                Object collectedObj = row.get("Collected");
                visit.put("Collected", collectedObj != null ? ((Number) collectedObj).doubleValue() : 0.0);
                
                Object balanceObj = row.get("Balance");
                visit.put("Balance", balanceObj != null ? ((Number) balanceObj).doubleValue() : 0.0);
                
                Object discountObj = row.get("Discount");
                visit.put("Discount", discountObj != null ? ((Number) discountObj).doubleValue() : 0.0);
                
                Object duesObj = row.get("Dues");
                visit.put("Dues", duesObj != null ? ((Number) duesObj).doubleValue() : 0.0);
                
                data.add(visit);
            }

            res.put("success", true);
            res.put("clinicId", clinicId);
            res.put("doctorId", doctorId);
            res.put("patientId", patientId);
            res.put("folderNo", folderNo);
            res.put("rows", data);
            
            // Calculate total A/C balance (sum of all balances)
            // This matches the stored procedure logic where A/C balance is the sum of all Balance values
            double totalAcBalance = data.stream()
                .mapToDouble(v -> {
                    Object balance = v.get("Balance");
                    return balance != null ? ((Number) balance).doubleValue() : 0.0;
                })
                .sum();
            res.put("totalAcBalance", totalAcBalance);
            
            return res;
        } catch (Exception e) {
            res.put("success", false);
            res.put("error", "Failed to get patient folder amount: " + e.getMessage());
            return res;
        }
    }
}


