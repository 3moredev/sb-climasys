package com.climasys.visits.service;

import com.climasys.repository.ServiceVisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class ServiceVisitService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceVisitService.class);

    private final ServiceVisitRepository serviceVisitRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ServiceVisitService(ServiceVisitRepository serviceVisitRepository) {
        this.serviceVisitRepository = serviceVisitRepository;
    }
    
    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        try {
            return new BigDecimal(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getPreviousServiceVisitDates(String patientId, String doctorId, String clinicId, LocalDate todaysVisitDate) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching previous service visit dates for patient: {}, doctor: {}, clinic: {}, today: {}", 
                patientId, doctorId, clinicId, todaysVisitDate);
            
            List<Object[]> rows;
            if (doctorId != null && !doctorId.trim().isEmpty()) {
                // If doctorId is provided, use the filtered query
                rows = serviceVisitRepository.findPreviousServiceVisitDates(patientId, doctorId, clinicId, todaysVisitDate);
            } else {
                // If doctorId is not provided, fetch all visits for the patient and clinic (ignoring doctor)
                rows = serviceVisitRepository.findPreviousServiceVisitDatesWithoutDoctor(patientId, clinicId, todaysVisitDate);
                logger.info("Fetching previous service visit dates without doctor filter for patient: {}, clinic: {}", 
                    patientId, clinicId);
            }
            logger.info("Found {} previous service visit records for patient: {}", rows.size(), patientId);
            
            List<Map<String, Object>> visits = new ArrayList<>();
            for (Object[] r : rows) {
                Map<String, Object> m = new HashMap<>();
                m.put("visitDate", r[0]);
                m.put("shiftId", r[1]);
                m.put("patientVisitNo", r[2]);
                visits.add(m);
                logger.debug("Added service visit: date={}, shiftId={}, visitNo={}", r[0], r[1], r[2]);
            }
            response.put("success", true);
            response.put("visits", visits);
            logger.info("Returning {} service visits for patient: {}", visits.size(), patientId);
        } catch (Exception e) {
            logger.error("Failed to fetch previous service visit dates for patient: {}, doctor: {}, clinic: {}: {}", 
                patientId, doctorId, clinicId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to fetch previous service visit dates: " + e.getMessage());
        }
        return response;
    }

    public Map<String, Object> getPreviousServiceVisitLineItems(
            String patientId,
            String doctorId,
            String clinicId,
            Short shiftId,
            Integer visitNo,
            LocalDate visitDate
    ) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>(); // Always initialize
        try {
            logger.info("Fetching previous service visit line-items for patient: {}, doctor: {}, clinic: {}, shiftId: {}, visitNo: {}, visitDate: {}", 
                patientId, doctorId, clinicId, shiftId, visitNo, visitDate);
            
            // Match stored procedure logic: check overwrite table first, then fallback to base table
            // Stored procedure uses: Patient_ID, Clinic_ID, Patient_Visit_No only (Shift_ID, Doctor_ID, Visit_Date are not filtered)
            List<Object[]> rows;
            
            boolean existsInOverwrite = serviceVisitRepository.existsInBillingInfoOverwrite(patientId, clinicId, visitNo);
            
            if (existsInOverwrite) {
                logger.info("Found data in overwrite table, querying from patient_visit_services_billinginfooverwrite");
                rows = serviceVisitRepository.findServiceVisitLineItemsFromOverwrite(patientId, clinicId, visitNo);
                logger.info("Found {} service visit line-item records from overwrite table", rows.size());
            } else {
                logger.info("No data in overwrite table, querying from base table patient_visit_services_billinginfo");
                rows = serviceVisitRepository.findServiceVisitLineItemsFromBase(patientId, clinicId, visitNo);
                logger.info("Found {} service visit line-item records from base table", rows.size());
            }
            
            if (rows.isEmpty()) {
                logger.warn("No service visit line-items found for patient: {}, clinic: {}, visitNo: {}. " +
                    "This could mean: 1) No billing info exists in either table for this visit, " +
                    "2) All records are marked as deleted (delete_flag=true) in overwrite table", 
                    patientId, clinicId, visitNo);
                response.put("success", true);
                response.put("items", items); // Return empty array
                response.put("message", "No service visit line-items found for the specified visit");
                return response;
            }
            
            for (Object[] r : rows) {
                if (r == null || r.length < 6) {
                    logger.warn("Skipping invalid row data - row is null or has insufficient columns (expected 6, got {})", r != null ? r.length : 0);
                    continue;
                }
                Map<String, Object> m = new HashMap<>();
                int i = 0;
                m.put("group", r[i++]);
                m.put("subGroup", r[i++]);
                m.put("details", r[i++]);
                m.put("amount", r[i++]);
                m.put("defaultFees", r[i++]);
                m.put("collectedFees", r[i]);
                items.add(m);
                logger.debug("Added service line-item: group={}, subGroup={}, details={}, amount={}", 
                    m.get("group"), m.get("subGroup"), m.get("details"), m.get("amount"));
            }
            response.put("success", true);
            response.put("items", items);
            logger.info("Returning {} service visit line-items (from {})", items.size(), existsInOverwrite ? "overwrite table" : "base table");
        } catch (Exception e) {
            logger.error("Failed to fetch previous service line-items for patient: {}, doctor: {}, clinic: {}, shiftId: {}, visitNo: {}, visitDate: {}: {}", 
                patientId, doctorId, clinicId, shiftId, visitNo, visitDate, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to fetch previous service line-items: " + e.getMessage());
            response.put("items", items); // Always include items, even on error
        }
        return response;
    }

    /**
     * JPA/JDBC replacement for USP_Get_MasterLists_Services.
     * Returns grouped lists for services visits: vitals (from patient_visits_services), complaints, diagnosis, dressing,
     * medicines (overwrite preferred), prescriptions (overwrite preferred), labTestsAsked, billing (overwrite preferred),
     * procedure findings, instruction groups, receipts, payments, lab test results, and previous visit vitals.
     */
    public Map<String, Object> getMasterListsForServices(
            String patientId,
            Short shiftId,
            String clinicId,
            String doctorId,
            LocalDate visitDate,
            Integer patientVisitNo) {

        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Building master-lists for services visit: patient={}, visitNo={}, date={}", patientId, patientVisitNo, visitDate);

            // 1) Vitals from patient_visits_services (PRIMARY TABLE FOR SERVICES)
            // Includes billing/payment fields and joins with receipt table for receipt details
            String vitalsSql = """
                SELECT pv.weight_in_kgs, pv.height_in_cms, pv.pulse, pv.blood_pressure,
                       COALESCE(pv.asthama,false) AS asthama,
                       COALESCE(pv.hypertension,false) AS hypertension,
                       COALESCE(pv.diabetes,false) AS diabetes,
                       COALESCE(pv.cholestrol,false) AS cholestrol,
                       COALESCE(pv.ihd,false) AS ihd,
                       COALESCE(pv.th,false) AS th,
                       pv.instructions, pv.fees_to_collect,
                       pv.fees_collected, pv.discount,
                       pv.payment_by_id, ptm.payment_description, pv.payment_remark, pv.referred_by,
                       pv.receipt_number, pv.offline_reason, pv.comment,
                       pv.patient_visit_no, pv.status_id,
                       COALESCE(pv.smoking,false) AS smoking,
                       COALESCE(pv.tobaco,false) AS tobaco,
                       COALESCE(pv.alchohol,false) AS alchohol,
                       pv.habits_comments, pv.allergy_dtls,
                       prs.receipt_date, prs.receipt_amount
                FROM patient_visits_services pv
                INNER JOIN patient_master pm ON pv.patient_id = pm.id
                LEFT JOIN payment_type_master ptm ON pv.payment_by_id = ptm.id
                LEFT JOIN patient_receipts_services prs ON pv.receipt_number = prs.receipt_number
                    AND prs.patient_id = pv.patient_id
                    AND prs.clinic_id = pv.clinic_id
                    AND prs.doctor_id = pv.doctor_id
                WHERE pv.patient_id = ? AND pv.shift_id = ? AND pv.clinic_id = ?
                  AND pv.doctor_id = ? AND DATE(pv.visit_date) = ? AND pv.patient_visit_no = ?
                  AND COALESCE(pv.delete_flag,false) = false
            """;
            logger.info("Executing vitals query with params: patientId={}, shiftId={}, clinicId={}, doctorId={}, visitDate={}, patientVisitNo={}", 
                patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            
            List<Map<String, Object>> vitals = jdbcTemplate.queryForList(
                vitalsSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            
            logger.info("Query executed for vitals. Found {} records for patient={}, visitNo={}, date={}", 
                vitals.size(), patientId, patientVisitNo, visitDate);
            
            if (vitals.isEmpty()) {
                logger.warn("⚠️  No vitals found for services visit - this might indicate the visit doesn't exist yet or date mismatch");
                logger.warn("Query parameters were: patientId={}, shiftId={}, clinicId={}, doctorId={}, visitDate={}, patientVisitNo={}", 
                    patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            } else {
                Map<String, Object> firstVital = vitals.get(0);
                logger.info("✅ Vitals query returned data. First record has {} keys", firstVital.size());
                logger.info("📋 All keys in vitals map: {}", firstVital.keySet());
                
                // Log specific billing-related fields to see what we're getting
                logger.info("💰 Billing fields from vitals:");
                logger.info("   fees_to_collect: {} (type: {})", firstVital.get("fees_to_collect"), 
                    firstVital.get("fees_to_collect") != null ? firstVital.get("fees_to_collect").getClass().getName() : "null");
                logger.info("   discount: {} (type: {})", firstVital.get("discount"), 
                    firstVital.get("discount") != null ? firstVital.get("discount").getClass().getName() : "null");
                logger.info("   fees_collected: {} (type: {})", firstVital.get("fees_collected"), 
                    firstVital.get("fees_collected") != null ? firstVital.get("fees_collected").getClass().getName() : "null");
                logger.info("   payment_by_id: {} (type: {})", firstVital.get("payment_by_id"), 
                    firstVital.get("payment_by_id") != null ? firstVital.get("payment_by_id").getClass().getName() : "null");
                logger.info("   payment_description: {} (type: {})", firstVital.get("payment_description"), 
                    firstVital.get("payment_description") != null ? firstVital.get("payment_description").getClass().getName() : "null");
                logger.info("   payment_remark: {} (type: {})", firstVital.get("payment_remark"), 
                    firstVital.get("payment_remark") != null ? firstVital.get("payment_remark").getClass().getName() : "null");
                logger.info("   receipt_number: {} (type: {})", firstVital.get("receipt_number"), 
                    firstVital.get("receipt_number") != null ? firstVital.get("receipt_number").getClass().getName() : "null");
                logger.info("   receipt_date: {} (type: {})", firstVital.get("receipt_date"), 
                    firstVital.get("receipt_date") != null ? firstVital.get("receipt_date").getClass().getName() : "null");
                logger.info("   receipt_amount: {} (type: {})", firstVital.get("receipt_amount"), 
                    firstVital.get("receipt_amount") != null ? firstVital.get("receipt_amount").getClass().getName() : "null");
            }

            // 2) Complaints
            String complaintsSql = """
                SELECT short_description || '*' || complaint_description AS id,
                       short_description || ' : ' || complaint_description AS symptoms_description,
                       complaint_description, COALESCE(complaint_comment,'') AS complaint_comment
                FROM visit_complaints vc
                WHERE vc.patient_id = ? AND vc.shift_id = ? AND vc.clinic_id = ? AND vc.doctor_id = ?
                  AND DATE(vc.visit_date) = ? AND vc.patient_visit_no = ? AND COALESCE(vc.delete_flag,false) = false
            """;
            List<Map<String, Object>> complaints = jdbcTemplate.queryForList(
                complaintsSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);

            // 3) Diagnosis
            String diagnosisSql = """
                SELECT short_description || '*' || desease_description AS id,
                       short_description || ' : ' || desease_description AS diagnosis_description,
                       desease_description
                FROM visit_diagnosis vd
                WHERE vd.patient_id = ? AND vd.shift_id = ? AND vd.clinic_id = ? AND vd.doctor_id = ?
                  AND DATE(vd.visit_date) = ? AND vd.patient_visit_no = ? AND COALESCE(vd.delete_flag,false) = false
            """;
            List<Map<String, Object>> diagnosis = jdbcTemplate.queryForList(
                diagnosisSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);

            // 4) Dressing
            String dressingSql = """
                SELECT dressing_description AS dressing_description,
                       dressing_description AS short_description,
                       dressing_description AS longdressing_description
                FROM visit_dressing dd
                WHERE dd.patient_id = ? AND dd.shift_id = ? AND dd.clinic_id = ? AND dd.doctor_id = ?
                  AND DATE(dd.visit_date) = ? AND dd.patient_visit_no = ? AND COALESCE(dd.delete_flag,false) = false
            """;
            List<Map<String, Object>> dressing = jdbcTemplate.queryForList(
                dressingSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);

            // 5) Medicines - prefer overwrite (check if exists with status_id = 5 from patient_visits_services)
            String medicineOverwriteCheckSql = """
                SELECT COUNT(*) FROM visit_medicine_overwrite vm
                INNER JOIN patient_visits_services pv ON vm.patient_id = pv.patient_id
                WHERE vm.patient_id = ? AND vm.shift_id = ? AND vm.clinic_id = ? AND vm.doctor_id = ?
                  AND DATE(vm.visit_date) = ? AND vm.patient_visit_no = ?
                  AND pv.status_id = 5
                  AND COALESCE(vm.delete_indicator,false) = false AND COALESCE(vm.delete_flag,false) = false
            """;
            Integer medicineOverwriteCount = 0;
            try {
                medicineOverwriteCount = jdbcTemplate.queryForObject(
                    medicineOverwriteCheckSql, Integer.class, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            } catch (Exception e) {
                logger.debug("No medicine overwrite records found or error checking: {}", e.getMessage());
                medicineOverwriteCount = 0;
            }
            
            List<Map<String, Object>> medicines;
            if (medicineOverwriteCount != null && medicineOverwriteCount > 0) {
                // Use overwrite table
                String medicineOverwriteSql = """
                    SELECT vm.short_description || ' : ' || vm.medicine_description AS medicine_description,
                           vm.short_description || '*' || vm.medicine_description AS id,
                           vm.morning, vm.afternoon, vm.night, vm.no_of_days, vm.instruction,
                           vm.short_description AS short_description,
                           REPLACE(vm.short_description, '''', '') AS med_replace
                    FROM visit_medicine_overwrite vm
                    WHERE vm.patient_id = ? AND vm.shift_id = ? AND vm.clinic_id = ? AND vm.doctor_id = ?
                      AND DATE(vm.visit_date) = ? AND vm.patient_visit_no = ?
                      AND COALESCE(vm.delete_indicator,false) = false AND COALESCE(vm.delete_flag,false) = false
                """;
                medicines = jdbcTemplate.queryForList(
                    medicineOverwriteSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            } else {
                // Use regular table
                String medicineSql = """
                    SELECT vm.short_description AS medicine_description,
                           vm.short_description || '*' || vm.medicine_description AS id,
                           vm.morning, vm.afternoon, vm.night, vm.no_of_days, vm.instruction,
                           vm.short_description AS short_description,
                           REPLACE(vm.short_description, '''', '') AS med_replace
                    FROM visit_medicine vm
                    WHERE vm.patient_id = ? AND vm.shift_id = ? AND vm.clinic_id = ? AND vm.doctor_id = ?
                      AND DATE(vm.visit_date) = ? AND vm.patient_visit_no = ?
                      AND COALESCE(vm.delete_flag,false) = false
                """;
                medicines = jdbcTemplate.queryForList(
                    medicineSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            }

            // 6) Prescriptions - prefer overwrite (check if exists with status_id = 5 from patient_visits_services)
            String prescriptionOverwriteCheckSql = """
                SELECT COUNT(*) FROM visit_prescription_overwrite vp
                INNER JOIN patient_visits_services pv ON vp.patient_id = pv.patient_id
                WHERE vp.patient_id = ? AND vp.shift_id = ? AND vp.clinic_id = ? AND vp.doctor_id = ?
                  AND DATE(vp.visit_date) = ? AND vp.patient_visit_no = ?
                  AND pv.status_id = 5
                  AND COALESCE(vp.delete_indicator,false) = false AND COALESCE(vp.delete_flag,false) = false
            """;
            Integer prescriptionOverwriteCount = 0;
            try {
                prescriptionOverwriteCount = jdbcTemplate.queryForObject(
                    prescriptionOverwriteCheckSql, Integer.class, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            } catch (Exception e) {
                logger.debug("No prescription overwrite records found or error checking: {}", e.getMessage());
                prescriptionOverwriteCount = 0;
            }
            
            List<Map<String, Object>> prescriptions;
            if (prescriptionOverwriteCount != null && prescriptionOverwriteCount > 0) {
                // Use overwrite table
                String prescriptionOverwriteSql = """
                    SELECT vp.medicine_name AS prescription_description,
                           vp.medicine_name || '*' || vp.brand_name || '*' || vp.cat_short_name || '*' || vp.catsub_description AS id,
                           vp.morning, vp.afternoon, vp.night, vp.no_of_days, vp.instruction,
                           REPLACE(vp.medicine_name, '''', '') AS med_replace,
                           '' AS generic_name,
                           vp.medicine_name,
                           vp.sequence_id
                    FROM visit_prescription_overwrite vp
                    WHERE vp.patient_id = ? AND vp.shift_id = ? AND vp.clinic_id = ? AND vp.doctor_id = ?
                      AND DATE(vp.visit_date) = ? AND vp.patient_visit_no = ?
                      AND COALESCE(vp.delete_indicator,false) = false AND COALESCE(vp.delete_flag,false) = false
                    ORDER BY vp.sequence_id
                """;
                prescriptions = jdbcTemplate.queryForList(
                    prescriptionOverwriteSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            } else {
                // Use regular table
                String prescriptionSql = """
                    SELECT vp.medicine_name || ' : ' || vp.brand_name AS prescription_description,
                           vp.medicine_name || '*' || vp.brand_name || '*' || vp.cat_short_name || '*' || vp.catsub_description AS id,
                           vp.morning, vp.afternoon, vp.night, vp.no_of_days, vp.instruction,
                           REPLACE(vp.medicine_name, '''', '') AS med_replace,
                           '' AS generic_name,
                           vp.medicine_name,
                           vp.sequence_id
                    FROM visit_prescription vp
                    WHERE vp.patient_id = ? AND vp.shift_id = ? AND vp.clinic_id = ? AND vp.doctor_id = ?
                      AND DATE(vp.visit_date) = ? AND vp.patient_visit_no = ?
                      AND COALESCE(vp.delete_flag,false) = false
                    ORDER BY vp.sequence_id
                """;
                prescriptions = jdbcTemplate.queryForList(
                    prescriptionSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            }

            // 7) Lab tests asked
            String labsSql = """
                SELECT lab_test_description AS id
                FROM patient_visit_labtestasked pvla
                WHERE pvla.patient_id = ? AND pvla.shift_id = ? AND pvla.clinic_id = ? AND pvla.doctor_id = ?
                  AND DATE(pvla.visit_date) = ? AND pvla.patient_visit_no = ? AND COALESCE(pvla.delete_flag,false) = false
            """;
            List<Map<String, Object>> labTestsAsked = jdbcTemplate.queryForList(
                labsSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);

            // 8) Previous visit vitals from patient_visits (using CTE logic from stored procedure)
            // This finds the previous visit number and then gets vitals from patient_visits
            String previousVisitVitalsSql = """
                WITH MaxDateTime AS (
                    SELECT ROW_NUMBER() OVER (PARTITION BY patient_id ORDER BY visit_date DESC, visit_time DESC) AS rownum,
                           patient_visit_no
                    FROM patient_visits_services
                    WHERE patient_visit_no < ? AND patient_id = ? AND doctor_id = ? AND COALESCE(delete_flag,false) = false
                ),
                LastVisitNo AS (
                    SELECT rownum, patient_visit_no FROM MaxDateTime WHERE rownum = 1
                )
                SELECT pv.weight_in_kgs, pv.height_in_cms, pv.pulse, pv.blood_pressure,
                       COALESCE(pv.asthama,false) AS asthama,
                       COALESCE(pv.hypertension,false) AS hypertension,
                       COALESCE(pv.diabetes,false) AS diabetes,
                       COALESCE(pv.cholestrol,false) AS cholestrol,
                       COALESCE(pv.ihd,false) AS ihd,
                       COALESCE(pv.th,false) AS th,
                       pv.instructions, pv.fees_to_collect,
                       pv.patient_visit_no, pv.status_id,
                       COALESCE(pv.smoking,false) AS smoking,
                       COALESCE(pv.tobaco,false) AS tobaco,
                       COALESCE(pv.alchohol,false) AS alchohol,
                       pv.habits_comments, pv.allergy_dtls
                FROM patient_visits pv
                INNER JOIN patient_master pm ON pv.patient_id = pm.id
                LEFT JOIN LastVisitNo lvn ON pv.patient_visit_no = lvn.patient_visit_no
                WHERE pv.patient_id = ? AND pv.doctor_id = ? AND COALESCE(pv.delete_flag,false) = false
                ORDER BY pv.patient_visit_no DESC
                LIMIT 1
            """;
            List<Map<String, Object>> previousVisitVitals = jdbcTemplate.queryForList(
                previousVisitVitalsSql, patientVisitNo, patientId, doctorId, patientId, doctorId);

            // 9) Procedure findings
            String procedureFindingsSql = """
                SELECT procedure_description, findings_description, findings_comment,
                       procedure_description AS pro_replace, findings_description AS find_replace
                FROM visit_procedure_findings vpf
                WHERE vpf.patient_id = ? AND vpf.shift_id = ? AND vpf.clinic_id = ? AND vpf.doctor_id = ?
                  AND DATE(vpf.visit_date) = ? AND vpf.patient_visit_no = ?
                  AND COALESCE(vpf.delete_flag,false) = false
            """;
            List<Map<String, Object>> procedureFindings = jdbcTemplate.queryForList(
                procedureFindingsSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);

            // 10) Instruction groups
            String instructionGroupsSql = """
                SELECT group_description, instructions_description, sequence_no
                FROM visit_groups_instructions vgi
                WHERE vgi.patient_id = ? AND vgi.shift_id = ? AND vgi.clinic_id = ? AND vgi.doctor_id = ?
                  AND DATE(vgi.visit_date) = ? AND vgi.patient_visit_no = ?
                ORDER BY vgi.group_description, vgi.sequence_no
            """;
            List<Map<String, Object>> instructionGroupsRaw = jdbcTemplate.queryForList(
                instructionGroupsSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            
            // Deduplicate instruction groups
            List<Map<String, Object>> instructionGroups = new ArrayList<>();
            List<Map<String, Object>> instructionDetails = new ArrayList<>();
            java.util.Set<String> uniqueGroups = new java.util.HashSet<>();
            java.util.Set<String> seenInstructions = new java.util.LinkedHashSet<>();
            
            for (Map<String, Object> row : instructionGroupsRaw) {
                String groupDesc = row.get("group_description") != null ? ((String) row.get("group_description")).trim() : "";
                String instructionDesc = row.get("instructions_description") != null ? ((String) row.get("instructions_description")).trim() : "";
                Object seqNoObj = row.get("sequence_no");
                Integer sequenceNo = seqNoObj != null ? 
                    (seqNoObj instanceof Integer ? (Integer) seqNoObj : Integer.valueOf(seqNoObj.toString())) : 0;
                
                String uniqueKey = (groupDesc + "|||" + instructionDesc + "|||" + sequenceNo).toLowerCase();
                
                if (!seenInstructions.contains(uniqueKey)) {
                    seenInstructions.add(uniqueKey);
                    
                    if (!uniqueGroups.contains(groupDesc)) {
                        Map<String, Object> group = new HashMap<>();
                        group.put("group_description", groupDesc);
                        group.put("Group_Description", groupDesc);
                        instructionGroups.add(group);
                        uniqueGroups.add(groupDesc);
                    }
                    
                    Map<String, Object> instructionDetail = new HashMap<>();
                    instructionDetail.put("group_description", groupDesc);
                    instructionDetail.put("Group_Description", groupDesc);
                    instructionDetail.put("instructions_description", instructionDesc);
                    instructionDetail.put("Instructions_Description", instructionDesc);
                    instructionDetail.put("sequence_no", sequenceNo);
                    instructionDetail.put("Sequence_No", sequenceNo);
                    instructionDetails.add(instructionDetail);
                }
            }

            // 11) Receipts (all)
            String receiptsSql = """
                SELECT pr.receipt_number, pr.receipt_date, pr.receipt_type, pr.receipt_amount,
                       pr.treatment_details, pr.title, pt.title_description, pr.to_date, pr.from_date
                FROM patient_receipts pr
                INNER JOIN patient_title pt ON pr.title = pt.id
                WHERE pr.patient_id = ? AND pr.clinic_id = ? AND pr.doctor_id = ?
                  AND DATE(pr.receipt_date) = ? AND pr.shift_id = ? AND pr.patient_visit_no = ?
            """;
            List<Map<String, Object>> receipts = jdbcTemplate.queryForList(
                receiptsSql, patientId, clinicId, doctorId, visitDate, shiftId, patientVisitNo);

            // 12) Receipts (excluding type 'L')
            String receiptsExcludingLSql = """
                SELECT pr.receipt_number, pr.receipt_date, pr.receipt_type, pr.receipt_amount,
                       pr.treatment_details, pr.title, pt.title_description, pr.to_date, pr.from_date
                FROM patient_receipts pr
                INNER JOIN patient_title pt ON pr.title = pt.id
                WHERE pr.patient_id = ? AND pr.clinic_id = ? AND pr.doctor_id = ?
                  AND DATE(pr.receipt_date) = ? AND pr.shift_id = ? AND pr.patient_visit_no = ?
                  AND pr.receipt_type != 'L'
            """;
            List<Map<String, Object>> receiptsExcludingL = jdbcTemplate.queryForList(
                receiptsExcludingLSql, patientId, clinicId, doctorId, visitDate, shiftId, patientVisitNo);

            // 13) Payments (AdHoc)
            String paymentsSql = """
                SELECT fees_collected
                FROM patient_payments_adhoc ppa
                WHERE ppa.patient_id = ? AND ppa.clinic_id = ? AND ppa.doctor_id = ?
                  AND DATE(ppa.payment_date) = ? AND ppa.shift_id = ?
            """;
            List<Map<String, Object>> payments = jdbcTemplate.queryForList(
                paymentsSql, patientId, clinicId, doctorId, visitDate, shiftId);

            // 14) Lab test results
            String labTestResultsSql = """
                SELECT lab_test_description AS id
                FROM patient_visit_labtestresults pvlr
                WHERE pvlr.patient_id = ? AND pvlr.shift_id = ? AND pvlr.clinic_id = ? AND pvlr.doctor_id = ?
                  AND DATE(pvlr.visit_date) = ? AND pvlr.patient_visit_no = ?
                  AND COALESCE(pvlr.delete_flag,false) = false
            """;
            List<Map<String, Object>> labTestResults = jdbcTemplate.queryForList(
                labTestResultsSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);

            // 15) Billing - prefer overwrite (check if exists)
            String billingOverwriteCheckSql = """
                SELECT COUNT(*) FROM patient_visit_services_billinginfooverwrite pvb
                WHERE pvb.patient_id = ? AND pvb.clinic_id = ? AND pvb.patient_visit_no = ?
            """;
            Integer billingOverwriteCount = 0;
            try {
                billingOverwriteCount = jdbcTemplate.queryForObject(
                    billingOverwriteCheckSql, Integer.class, patientId, clinicId, patientVisitNo);
            } catch (Exception e) {
                logger.debug("No billing overwrite records found or error checking: {}", e.getMessage());
                billingOverwriteCount = 0;
            }
            
            List<Map<String, Object>> billing;
            if (billingOverwriteCount != null && billingOverwriteCount > 0) {
                // Use overwrite table
                String billingOverwriteSql = """
                    SELECT billing_details, billing_group_name, billing_subgroup_name,
                           default_fees, collected_fees,
                           billing_group_name || '*' || billing_subgroup_name || '*' || billing_details AS billing_id
                    FROM patient_visit_services_billinginfooverwrite pvb
                    WHERE pvb.patient_id = ? AND pvb.clinic_id = ? AND pvb.patient_visit_no = ?
                      AND COALESCE(pvb.delete_flag,false) = false
                """;
                billing = jdbcTemplate.queryForList(
                    billingOverwriteSql, patientId, clinicId, patientVisitNo);
            } else {
                // Use base table
                String billingSql = """
                    SELECT billing_details, billing_group_name, billing_subgroup_name,
                           default_fees, collected_fees,
                           billing_group_name || '*' || billing_subgroup_name || '*' || billing_details AS billing_id
                    FROM patient_visit_services_billinginfo pvb
                    WHERE pvb.patient_id = ? AND pvb.clinic_id = ? AND pvb.patient_visit_no = ?
                """;
                billing = jdbcTemplate.queryForList(
                    billingSql, patientId, clinicId, patientVisitNo);
            }

            // 16) Billing/Payment Fields - Extract from vitals and format for UI
            Map<String, Object> billingFields = new HashMap<>();
            logger.info("🔍 Extracting billing fields from vitals. Vitals count: {}", vitals.size());
            
            if (!vitals.isEmpty()) {
                Map<String, Object> v = vitals.get(0);
                
                logger.info("📊 Starting billing fields extraction from vitals map");
                logger.info("   Vitals map contains {} keys", v.size());
                
                // Calculate amounts - ensure we always have valid BigDecimal values (not null)
                BigDecimal feesToCollect = toBigDecimal(v.get("fees_to_collect"));
                if (feesToCollect == null) feesToCollect = BigDecimal.ZERO;
                
                BigDecimal discount = toBigDecimal(v.get("discount"));
                if (discount == null) discount = BigDecimal.ZERO;
                
                BigDecimal collected = toBigDecimal(v.get("fees_collected"));
                if (collected == null) collected = BigDecimal.ZERO;
                
                // Calculate dues: fees_to_collect - discount - fees_collected
                BigDecimal dues = feesToCollect.subtract(discount).subtract(collected);
                
                logger.debug("Calculated billing amounts - billed: {}, discount: {}, collected: {}, dues: {}", 
                    feesToCollect, discount, collected, dues);
                
                // Billing amounts - always set to BigDecimal values (never null)
                billingFields.put("billedRs", feesToCollect);
                billingFields.put("discountRs", discount);
                billingFields.put("collectedRs", collected);
                billingFields.put("duesRs", dues);
                billingFields.put("acBalanceRs", BigDecimal.ZERO); // Not tracked currently - may need separate calculation
                
                // Payment information - extract with null handling
                Object paymentByIdObj = v.get("payment_by_id");
                billingFields.put("paymentById", paymentByIdObj);
                
                Object paymentDescObj = v.get("payment_description");
                billingFields.put("paymentBy", paymentDescObj != null ? paymentDescObj.toString() : "");
                
                Object paymentRemarkObj = v.get("payment_remark");
                billingFields.put("paymentRemark", paymentRemarkObj != null ? paymentRemarkObj.toString() : "");
                
                Object referredByObj = v.get("referred_by");
                billingFields.put("referredBy", referredByObj != null ? referredByObj.toString() : "");
                
                // Receipt information
                Object receiptNoObj = v.get("receipt_number");
                billingFields.put("receiptNo", receiptNoObj != null ? receiptNoObj.toString() : "");
                
                Object receiptDateObj = v.get("receipt_date");
                billingFields.put("receiptDate", receiptDateObj);
                
                Object receiptAmountObj = v.get("receipt_amount");
                BigDecimal receiptAmount = toBigDecimal(receiptAmountObj);
                billingFields.put("receiptAmount", receiptAmount != null ? receiptAmount : BigDecimal.ZERO);
                
                // Reason field - use offline_reason or comment as fallback
                String reason = "";
                Object offlineReasonObj = v.get("offline_reason");
                if (offlineReasonObj != null) {
                    reason = offlineReasonObj.toString();
                } else {
                    Object commentObj = v.get("comment");
                    if (commentObj != null) {
                        reason = commentObj.toString();
                    }
                }
                billingFields.put("reason", reason);
                
                logger.info("✅ Successfully extracted billing fields:");
                logger.info("   billedRs: {}", billingFields.get("billedRs"));
                logger.info("   discountRs: {}", billingFields.get("discountRs"));
                logger.info("   collectedRs: {}", billingFields.get("collectedRs"));
                logger.info("   duesRs: {}", billingFields.get("duesRs"));
                logger.info("   paymentById: {}", billingFields.get("paymentById"));
                logger.info("   paymentBy: '{}'", billingFields.get("paymentBy"));
                logger.info("   receiptNo: '{}'", billingFields.get("receiptNo"));
                logger.info("   receiptAmount: {}", billingFields.get("receiptAmount"));
            } else {
                logger.warn("No vitals found for services visit - using default empty values");
                // Default empty values if no vitals found - use empty strings and BigDecimal.ZERO for consistency
                billingFields.put("billedRs", BigDecimal.ZERO);
                billingFields.put("discountRs", BigDecimal.ZERO);
                billingFields.put("collectedRs", BigDecimal.ZERO);
                billingFields.put("duesRs", BigDecimal.ZERO);
                billingFields.put("acBalanceRs", BigDecimal.ZERO);
                billingFields.put("paymentById", null); // ID can be null
                billingFields.put("paymentBy", ""); // Empty string for description
                billingFields.put("paymentRemark", "");
                billingFields.put("referredBy", "");
                billingFields.put("receiptNo", "");
                billingFields.put("receiptDate", null); // Date can be null
                billingFields.put("receiptAmount", BigDecimal.ZERO);
                billingFields.put("reason", "");
            }

            // Build response
            Map<String, Object> data = new HashMap<>();
            data.put("vitals", vitals);
            data.put("complaints", complaints);
            data.put("diagnosis", diagnosis);
            data.put("dressing", dressing);
            data.put("medicines", medicines);
            data.put("prescriptions", prescriptions);
            data.put("labTestsAsked", labTestsAsked);
            data.put("previousVisitVitals", previousVisitVitals);
            data.put("procedureFindings", procedureFindings);
            data.put("instructionGroups", instructionGroups);
            data.put("instructions", instructionDetails);
            data.put("receipts", receipts);
            data.put("receiptsExcludingL", receiptsExcludingL);
            data.put("payments", payments);
            data.put("labTestResults", labTestResults);
            data.put("billing", billing);
            data.put("billingFields", billingFields); // Structured billing/payment fields for UI

            response.put("success", true);
            response.put("patientId", patientId);
            response.put("clinicId", clinicId);
            response.put("doctorId", doctorId);
            response.put("shiftId", shiftId);
            response.put("visitDate", visitDate);
            response.put("patientVisitNo", patientVisitNo);
            response.put("data", data);

            logger.info("✅ Successfully built master-lists for services visit: patient={}, visitNo={}", patientId, patientVisitNo);
            logger.info("📦 Final billingFields in response: {}", billingFields);
            logger.info("📦 billingFields keys: {}", billingFields.keySet());
            return response;
        } catch (Exception e) {
            logger.error("Error building master-lists for services: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to build master lists for services: " + e.getMessage());
            return response;
        }
    }
}


