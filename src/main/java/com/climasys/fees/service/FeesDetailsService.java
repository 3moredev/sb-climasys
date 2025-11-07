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
     * JPA/JDBC equivalent for USP_Get_PatientFolderAmountForBilling.
     * Returns ALL visits for a patient from patient_visits_services table.
     * This matches the stored procedure behavior which returns all visits to calculate total A/C balance.
     * 
     * Returns: { success, clinicId, doctorId, patientId, rows: [ { Patient_ID, Full_Name, Patient_Visit_No, 
     *          Visit_Date, Financial_Year, Bill, Collected, Balance, Discount, Dues }, ... ] }
     */
    public Map<String, Object> getPatientFolderAmountForBilling(String clinicId, String doctorId, String patientId) {
        Map<String, Object> res = new LinkedHashMap<>();
        try {
            // Query matching the stored procedure USP_Get_PatientFolderAmountForBilling
            // Returns all visits from patient_visits_services with status_id=8
            // Using UNION ALL to also include patient_visits (status_id=5) for comprehensive billing data
            String visitSql = """
                SELECT pv.patient_id AS Patient_ID,
                       (pm.first_name || ' ' || pm.last_name) AS Full_Name,
                       pv.patient_visit_no AS Patient_Visit_No,
                       pv.visit_date AS Visit_Date,
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
                 WHERE pv.patient_id = ?
                   AND pv.clinic_id = ?
                   AND COALESCE(pv.delete_flag, false) = false
                   AND pv.fees_to_collect IS NOT NULL
                   AND pv.status_id = 8
                UNION ALL
                SELECT pv.patient_id AS Patient_ID,
                       (pm.first_name || ' ' || pm.last_name) AS Full_Name,
                       pv.patient_visit_no AS Patient_Visit_No,
                       pv.visit_date AS Visit_Date,
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
                 WHERE pv.patient_id = ?
                   AND pv.clinic_id = ?
                   AND COALESCE(pv.delete_flag, false) = false
                   AND pv.fees_to_collect IS NOT NULL
                   AND pv.status_id = 5
                 ORDER BY Visit_Date ASC
            """;
            
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(visitSql, patientId, clinicId, patientId, clinicId);
            
            List<Map<String, Object>> data = new ArrayList<>();
            
            for (Map<String, Object> row : rows) {
                Map<String, Object> visit = new LinkedHashMap<>();
                visit.put("Patient_ID", row.get("Patient_ID"));
                visit.put("Full_Name", row.get("Full_Name"));
                visit.put("Patient_Visit_No", row.get("Patient_Visit_No"));
                visit.put("Visit_Date", row.get("Visit_Date"));
                visit.put("Financial_Year", row.get("Financial_Year"));
                
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
            res.put("rows", data);
            
            // Calculate total A/C balance (sum of all balances)
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
            res.put("error", "Failed to get patient folder amount for billing: " + e.getMessage());
            return res;
        }
    }
}


