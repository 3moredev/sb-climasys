package com.climasys.visits.service;

import com.climasys.entity.PatientVisit;
import com.climasys.entity.PatientVisitId;
import com.climasys.entity.VisitMedicineOverwrite;
import com.climasys.entity.VisitMedicineOverwriteId;
import com.climasys.entity.Medicine;
import com.climasys.entity.MedicineId;
import com.climasys.repository.PatientVisitRepository;
import com.climasys.repository.DoctorClinicShiftRepository;
import com.climasys.repository.StatusRefRepository;
import com.climasys.repository.VisitMedicineOverwriteRepository;
import com.climasys.repository.VisitPrescriptionOverwriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VisitJpaService {

    private static final Logger logger = LoggerFactory.getLogger(VisitJpaService.class);

    @Autowired
    private PatientVisitRepository patientVisitRepository;

    @Autowired
    private DoctorClinicShiftRepository doctorClinicShiftRepository;

    @Autowired
    private StatusRefRepository statusRefRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private com.climasys.service.RelationshipService relationshipService;

    @Autowired
    private VisitMedicineOverwriteRepository visitMedicineOverwriteRepository;

    @Autowired
    private VisitPrescriptionOverwriteRepository visitPrescriptionOverwriteRepository;

    private BigDecimal toBigDecimal(Object value) {
        if (value == null)
            return null;
        if (value instanceof BigDecimal bd)
            return bd;
        try {
            return new BigDecimal(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Save or update a comprehensive patient visit using JPA
     */
    @Transactional
    public Map<String, Object> saveComprehensiveVisit(ComprehensiveVisitRequest request) {
        logger.info("Saving comprehensive visit for patient: {} using JPA", request.patientId());

        try {
            // Ensure required relationships exist before validation
            relationshipService.ensureVisitRelationships(
                    request.doctorId(),
                    request.clinicId(),
                    request.shiftId());

            // Log the search parameters for debugging
            logger.info("Searching for existing visit with parameters:");
            logger.info("  PatientId: {}", request.patientId());
            logger.info("  DoctorId: {}", request.doctorId());
            logger.info("  ClinicId: {}", request.clinicId());
            logger.info("  ShiftId: {}", request.shiftId());
            logger.info("  PatientVisitNo: {}", request.patientVisitNo());
            logger.info("  VisitDate: {}", request.visitDate());

            // For lookup, we need to find visits on the same date (ignoring time)
            // Normalize the visit date to start of day for comparison
            LocalDateTime normalizedVisitDate = request.visitDate().toLocalDate().atStartOfDay();
            logger.info("  Normalized VisitDate for lookup: {}", normalizedVisitDate);

            // Check if visit already exists using a custom query that compares only the
            // date part
            Optional<PatientVisit> existingVisit = findExistingVisitByDate(
                    request.patientId(),
                    request.doctorId(),
                    request.clinicId(),
                    request.shiftId(),
                    request.patientVisitNo(),
                    request.visitDate().toLocalDate());

            logger.info("Existing visit found: {}", existingVisit.isPresent());

            // Debug: Check what visits exist for this patient
            List<PatientVisit> allPatientVisits = patientVisitRepository
                    .findByPatientIdAndDeleteFlagOrderByVisitDateDesc(
                            request.patientId(), false);
            logger.info("Total visits found for patient {}: {}", request.patientId(), allPatientVisits.size());
            for (PatientVisit pv : allPatientVisits) {
                logger.info(
                        "  Existing visit: PatientId={}, DoctorId={}, ClinicId={}, ShiftId={}, PatientVisitNo={}, VisitDate={}",
                        pv.getPatientId(), pv.getDoctorId(), pv.getClinicId(), pv.getShiftId(),
                        pv.getPatientVisitNo(), pv.getVisitDate());
            }

            PatientVisit visit;
            boolean isUpdate = false;

            if (existingVisit.isPresent()) {
                // Update existing visit
                visit = existingVisit.get();
                isUpdate = true;
                logger.info("Updating existing visit for patient: {}", request.patientId());
            } else {
                // Create new visit
                visit = new PatientVisit();
                // Set composite key fields
                visit.setPatientId(request.patientId());
                visit.setDoctorId(request.doctorId());
                visit.setClinicId(request.clinicId());
                visit.setShiftId(request.shiftId());
                visit.setPatientVisitNo(request.patientVisitNo());
                visit.setVisitDate(request.visitDate());
                logger.info("Creating new visit for patient: {}", request.patientId());
            }

            // Map all fields from request to entity
            mapRequestToEntity(request, visit);

            // Validate required fields before saving
            validateRequiredFields(visit);

            // Log key fields for debugging
            logger.info("Mapped visit - PatientId: {}, DoctorId: {}, StatusId: {}, IsSubmit: {}",
                    visit.getPatientId(), visit.getDoctorId(), visit.getStatusId(),
                    visit.getIsSubmitPatientVisitDetails());

            // Set audit fields
            LocalDateTime now = LocalDateTime.now();
            if (!isUpdate) {
                visit.setCreatedOn(now);
                visit.setCreatedbyName(request.userId());
            }
            visit.setModifiedOn(now);
            visit.setModifiedbyName(request.userId());

            // Save the entity
            PatientVisit savedVisit = patientVisitRepository.save(visit);

            logger.info("Successfully saved visit for patient: {}", savedVisit.getPatientId());

            // Persist treatment details (diagnosis and medicines) if provided via request
            // extras
            try {
                persistDiagnosisAndMedicinesIfProvided(request, savedVisit);
            } catch (Exception persistEx) {
                logger.warn("Saved core visit, but failed to persist diagnosis/medicines: {}", persistEx.getMessage());
            }

            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", isUpdate ? "Visit updated successfully" : "Visit created successfully");
            response.put("patientId", savedVisit.getPatientId());
            response.put("doctorId", savedVisit.getDoctorId());
            response.put("clinicId", savedVisit.getClinicId());
            response.put("patientVisitNo", savedVisit.getPatientVisitNo());
            response.put("visitDate", savedVisit.getVisitDate());

            return response;

        } catch (Exception e) {
            logger.error("Error saving comprehensive visit: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to save visit: " + e.getMessage());
            return error;
        }
    }

    /**
     * Find existing visit by composite key and date (ignoring time)
     */
    private Optional<PatientVisit> findExistingVisitByDate(
            String patientId,
            String doctorId,
            String clinicId,
            Short shiftId,
            Integer patientVisitNo,
            java.time.LocalDate visitDate) {

        logger.info("Searching for existing visit by date: {}", visitDate);

        return patientVisitRepository.findByCompositeKeyAndDate(
                patientId, doctorId, clinicId, shiftId, patientVisitNo, visitDate);
    }

    /**
     * Get the last visit details for a patient (only completed visits with status
     * 5)
     * This matches the stored procedure logic that only returns completed visits
     */
    public Map<String, Object> getLastVisitDetails(String patientId) {
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Getting last completed visit details for patient: {}", patientId);

            // Use the new method that filters for status 5 (completed visits)
            // This matches the stored procedure logic: AND Status_ID = 5
            Optional<PatientVisit> lastVisit = patientVisitRepository
                    .findFirstByPatientIdAndDeleteFlagAndStatusIdOrderByVisitDateDesc(patientId, false, (short) 5);

            if (lastVisit.isPresent()) {
                PatientVisit visit = lastVisit.get();

                // Calculate PLR indicators for this visit
                String plrIndicators = calculatePlrIndicators(visit);

                response.put("success", true);
                response.put("found", true);
                response.put("visit", mapVisitToResponseWithPlr(visit, plrIndicators));

                logger.info("Found last completed visit for patient {}: {} with PLR: {}", patientId,
                        visit.getVisitDate(), plrIndicators);
            } else {
                response.put("success", true);
                response.put("found", false);
                response.put("message", "No completed visits found for patient");

                logger.info("No completed visits found for patient: {}", patientId);
            }

        } catch (Exception e) {
            logger.error("Error getting last visit details for patient {}: {}", patientId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get last visit details: " + e.getMessage());
        }

        return response;
    }

    /**
     * Get previous date data for a patient (USP_Get_PrevDateData equivalent)
     * Returns data from the last completed visit to pre-populate form fields
     */
    public Map<String, Object> getPreviousDateData(
            String patientId,
            String doctorId,
            Integer patientVisitNo,
            LocalDate todaysVisitDate,
            Short shiftId,
            String clinicId) {

        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Getting previous date data for patient: {}, visitNo: {}, date: {}, shift: {}, clinic: {}",
                    patientId, patientVisitNo, todaysVisitDate, shiftId, clinicId);

            List<Object[]> rawResults = patientVisitRepository.getPreviousDateDataRaw(
                    patientId, todaysVisitDate, shiftId, clinicId, patientVisitNo);

            if (!rawResults.isEmpty()) {
                Object[] row = rawResults.get(0);
                Map<String, Object> data = new HashMap<>();

                // Map array indices to field names (must match SELECT order in query)
                int i = 0;
                data.put("weight_in_kgs", row[i++]);
                data.put("height_in_cms", row[i++]);
                data.put("pulse", row[i++]);
                data.put("blood_pressure", row[i++]);
                data.put("asthama", row[i++]);
                data.put("hypertension", row[i++]);
                data.put("diabetes", row[i++]);
                data.put("cholestrol", row[i++]);
                data.put("ihd", row[i++]);
                data.put("th", row[i++]);
                data.put("instructions", row[i++]);
                data.put("fees_to_collect", row[i++]);
                data.put("patient_visit_no", row[i++]);
                data.put("status_id", row[i++]);
                data.put("smoking", row[i++]);
                data.put("tobaco", row[i++]);
                data.put("alchohol", row[i++]);
                data.put("habits_comments", row[i++]);
                data.put("allergy_dtls", row[i++]);
                data.put("observation", row[i++]);
                data.put("symptom_comment", row[i++]);
                data.put("thtext", row[i++]);
                data.put("sugar", row[i++]);
                data.put("current_medicines", row[i++]);
                data.put("visit_comments", row[i++]);
                data.put("current_complaints", row[i++]);
                data.put("fmp", row[i++]);
                data.put("prmc", row[i++]);
                data.put("pamc", row[i++]);
                data.put("lmp", row[i++]);
                data.put("obstetrics_history", row[i++]);
                data.put("surgical_history_past_history", row[i++]);
                data.put("gynec_additional_comments", row[i++]);
                data.put("edd", row[i++]);
                data.put("pregnant", row[i++]);
                data.put("prev_visit_date", row[i++]);
                data.put("prev_visit_time", row[i++]);
                data.put("prev_doctor_id", row[i++]);

                response.put("success", true);
                response.put("found", true);
                response.put("data", data);

                logger.info("Found previous visit data for patient {} from visit date: {} with {} fields",
                        patientId, data.get("prev_visit_date"), data.size());
            } else {
                response.put("success", true);
                response.put("found", false);
                response.put("message", "No previous completed visit found for patient");

                logger.info("No previous visit data found for patient: {}", patientId);
            }

        } catch (Exception e) {
            logger.error("Error getting previous date data for patient {}: {}", patientId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get previous date data: " + e.getMessage());
        }

        return response;
    }

    /**
     * Get patient appointment details by patient ID, clinic ID, and visit number
     * This replaces the JDBC-based USP_Get_PatientAppointmentDetailsNew
     * implementation
     */
    public Map<String, Object> getPatientAppointmentDetails(String patientId, String clinicId,
            Integer patientVisitNo, Integer languageId) {
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Getting appointment details for patient: {}, clinic: {}, visitNo: {}, languageId: {}",
                    patientId, clinicId, patientVisitNo, languageId);

            // Find the visit using JPA repository
            Optional<PatientVisit> visitOptional = patientVisitRepository
                    .findFirstByPatientIdAndClinicIdAndPatientVisitNoAndDeleteFlag(
                            patientId, clinicId, patientVisitNo, false);

            if (visitOptional.isEmpty()) {
                response.put("success", false);
                response.put("found", false);
                response.put("message", "Visit not found for patient: " + patientId +
                        ", clinic: " + clinicId + ", visitNo: " + patientVisitNo);
                logger.warn("Visit not found for patient: {}, clinic: {}, visitNo: {}",
                        patientId, clinicId, patientVisitNo);
                return response;
            }

            PatientVisit visit = visitOptional.get();

            // Extract visit parameters for fetching associated data
            Short shiftId = visit.getShiftId();
            String doctorId = visit.getDoctorId();
            LocalDate visitDate = visit.getVisitDate().toLocalDate();
            LocalDateTime exactVisitDate = visit.getVisitDate(); // Exact timestamp for instruction groups matching

            // Calculate PLR indicators
            String plrIndicators = calculatePlrIndicators(visit);

            // Map visit to detailed response
            Map<String, Object> visitData = mapVisitToResponseWithPlr(visit, plrIndicators);

            // Fetch associated table data (including instruction groups)
            Map<String, Object> associatedData = fetchAssociatedVisitData(
                    patientId, shiftId, clinicId, doctorId, visitDate, exactVisitDate, patientVisitNo);
            visitData.putAll(associatedData);

            // Log instruction groups presence in visitData
            Object instructionGroupsInData = visitData.get("instructionGroups");
            Object instructionsInData = visitData.get("instructions");
            logger.info("Instruction groups in visitData - instructionGroups: {}, instructions: {}",
                    instructionGroupsInData != null
                            ? (instructionGroupsInData instanceof List ? ((List<?>) instructionGroupsInData).size()
                                    : instructionGroupsInData)
                            : "null",
                    instructionsInData != null
                            ? (instructionsInData instanceof List ? ((List<?>) instructionsInData).size()
                                    : instructionsInData)
                            : "null");

            // Detailed logging of instructions array content
            if (instructionsInData instanceof List) {
                List<?> instructionsList = (List<?>) instructionsInData;
                logger.info("📋 Instructions array contains {} items", instructionsList.size());
                for (int i = 0; i < instructionsList.size(); i++) {
                    Object item = instructionsList.get(i);
                    if (item instanceof Map) {
                        Map<?, ?> instrMap = (Map<?, ?>) item;
                        logger.info("   Instruction[{}]: '{}' (group: '{}', sequence: {})",
                                i,
                                instrMap.get("instructions_description"),
                                instrMap.get("group_description"),
                                instrMap.get("sequence_no"));
                    }
                }

                // Check for duplicates within the instructions array
                java.util.Set<String> seenInResponse = new java.util.HashSet<>();
                for (Object item : instructionsList) {
                    if (item instanceof Map) {
                        Map<?, ?> instrMap = (Map<?, ?>) item;
                        String instrText = instrMap.get("instructions_description") != null
                                ? ((String) instrMap.get("instructions_description")).trim().toLowerCase()
                                : "";
                        if (seenInResponse.contains(instrText)) {
                            logger.error("❌ DUPLICATE FOUND IN RESPONSE! Instruction: '{}' appears multiple times!",
                                    instrMap.get("instructions_description"));
                        } else {
                            seenInResponse.add(instrText);
                        }
                    }
                }
            }

            // Build response structure matching the stored procedure format
            response.put("success", true);
            response.put("found", true);
            response.put("mainData", List.of(visitData));
            response.put("additionalData", List.of(visitData)); // Same data for now
            response.put("patientId", patientId);
            response.put("clinicId", clinicId);
            response.put("visitNo", patientVisitNo);
            response.put("languageId", languageId);

            logger.info("Successfully retrieved appointment details with associated data for patient: {}", patientId);

        } catch (Exception e) {
            logger.error("Error getting appointment details for patient {}: {}", patientId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get appointment details: " + e.getMessage());
        }

        return response;
    }

    /**
     * Fetch associated visit data (diagnosis, medicines, prescriptions,
     * investigations, complaints, dressing, billing, instruction groups)
     */
    private Map<String, Object> fetchAssociatedVisitData(
            String patientId, Short shiftId, String clinicId, String doctorId,
            LocalDate visitDate, LocalDateTime exactVisitDate, Integer patientVisitNo) {
        Map<String, Object> associatedData = new HashMap<>();

        try {
            logger.debug("Fetching associated data for visit: patientId={}, visitDate={}, visitNo={}",
                    patientId, visitDate, patientVisitNo);

            // 1) Complaints
            String complaintsSql = """
                        SELECT short_description || '*' || complaint_description AS id,
                               short_description || ' : ' || complaint_description AS symptoms_description,
                               complaint_description, COALESCE(complaint_comment,'') AS complaint_comment
                        FROM visit_complaints vc
                        WHERE vc.patient_id = ? AND vc.shift_id = ? AND vc.clinic_id = ? AND vc.doctor_id = ?
                          AND DATE(vc.visit_date) = DATE(?::date) AND vc.patient_visit_no = ?
                          AND (vc.delete_flag IS NULL OR vc.delete_flag = false)
                    """;
            List<Map<String, Object>> complaints = jdbcTemplate.queryForList(
                    complaintsSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            associatedData.put("complaints", complaints);

            // 2) Diagnosis
            String diagnosisSql = """
                        SELECT short_description || '*' || desease_description AS id,
                               short_description || ' : ' || desease_description AS diagnosis_description,
                               desease_description
                        FROM visit_diagnosis vd
                        WHERE vd.patient_id = ? AND vd.shift_id = ? AND vd.clinic_id = ? AND vd.doctor_id = ?
                          AND DATE(vd.visit_date) = DATE(?::date) AND vd.patient_visit_no = ?
                          AND (vd.delete_flag IS NULL OR vd.delete_flag = false)
                    """;
            List<Map<String, Object>> diagnosis = jdbcTemplate.queryForList(
                    diagnosisSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            associatedData.put("diagnosis", diagnosis);

            // 3) Dressing
            String dressingSql = """
                        SELECT dressing_description AS dressing_description,
                               dressing_description AS short_description,
                               dressing_description AS longdressing_description
                        FROM visit_dressing dd
                        WHERE dd.patient_id = ? AND dd.shift_id = ? AND dd.clinic_id = ? AND dd.doctor_id = ?
                          AND DATE(dd.visit_date) = DATE(?::date) AND dd.patient_visit_no = ?
                          AND (dd.delete_flag IS NULL OR dd.delete_flag = false)
                    """;
            List<Map<String, Object>> dressing = jdbcTemplate.queryForList(
                    dressingSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            associatedData.put("dressing", dressing);

            // Also return dressingBodyParts as a single string (concatenated from all
            // dressing records)
            // This matches the textbox field format used in the frontend
            if (dressing != null && !dressing.isEmpty()) {
                StringBuilder dressingBodyParts = new StringBuilder();
                for (Map<String, Object> dressingRow : dressing) {
                    Object dressingDesc = dressingRow.get("dressing_description");
                    if (dressingDesc != null && !dressingDesc.toString().trim().isEmpty()) {
                        if (dressingBodyParts.length() > 0) {
                            dressingBodyParts.append("\n"); // Separate multiple dressings with newline
                        }
                        dressingBodyParts.append(dressingDesc.toString().trim());
                    }
                }
                associatedData.put("dressingBodyParts", dressingBodyParts.toString());
            } else {
                associatedData.put("dressingBodyParts", "");
            }

            // 4) Medicines - prefer overwrite
            String medicineOverwriteSql = """
                        SELECT vm.short_description || '*' || vm.medicine_description AS id,
                               vm.short_description AS short_description,
                               vm.medicine_description,
                               vm.morning, vm.afternoon, vm.night, vm.no_of_days, vm.instruction
                        FROM visit_medicine_overwrite vm
                        WHERE vm.patient_id = ? AND vm.shift_id = ? AND vm.clinic_id = ? AND vm.doctor_id = ?
                          AND DATE(vm.visit_date) = DATE(?::date) AND vm.patient_visit_no = ?
                          AND (vm.delete_indicator IS NULL OR vm.delete_indicator = false)
                          AND (vm.delete_flag IS NULL OR vm.delete_flag = false)
                    """;
            List<Map<String, Object>> medicines = jdbcTemplate.queryForList(
                    medicineOverwriteSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            if (medicines.isEmpty()) {
                String medicineSql = """
                            SELECT vm.short_description || '*' || vm.medicine_description AS id,
                                   vm.short_description AS short_description,
                                   vm.medicine_description,
                                   vm.morning, vm.afternoon, vm.night, vm.no_of_days, vm.instruction
                            FROM visit_medicine vm
                            WHERE vm.patient_id = ? AND vm.shift_id = ? AND vm.clinic_id = ? AND vm.doctor_id = ?
                              AND DATE(vm.visit_date) = DATE(?::date) AND vm.patient_visit_no = ?
                              AND (vm.delete_flag IS NULL OR vm.delete_flag = false)
                        """;
                medicines = jdbcTemplate.queryForList(
                        medicineSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            }
            associatedData.put("medicines", medicines);

            // 5) Prescriptions - prefer overwrite
            String prescriptionOverwriteSql = """
                        SELECT vp.medicine_name AS medicine_name,
                               vp.brand_name AS brand_name,
                               vp.medicine_name || '*' || vp.brand_name || '*' || vp.cat_short_name || '*' || vp.catsub_description AS id,
                               vp.morning, vp.afternoon, vp.night, vp.no_of_days, vp.instruction, vp.sequence_id
                        FROM visit_prescription_overwrite vp
                        WHERE vp.patient_id = ? AND vp.shift_id = ? AND vp.clinic_id = ? AND vp.doctor_id = ?
                          AND DATE(vp.visit_date) = DATE(?::date) AND vp.patient_visit_no = ?
                          AND (vp.delete_indicator IS NULL OR vp.delete_indicator = false)
                          AND (vp.delete_flag IS NULL OR vp.delete_flag = false)
                        ORDER BY vp.sequence_id
                    """;
            List<Map<String, Object>> prescriptions = jdbcTemplate.queryForList(
                    prescriptionOverwriteSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            if (prescriptions.isEmpty()) {
                String prescriptionSql = """
                            SELECT vp.medicine_name AS medicine_name,
                                   vp.brand_name AS brand_name,
                                   vp.medicine_name || '*' || vp.brand_name || '*' || vp.cat_short_name || '*' || vp.catsub_description AS id,
                                   vp.morning, vp.afternoon, vp.night, vp.no_of_days, vp.instruction, vp.sequence_id
                            FROM visit_prescription vp
                            WHERE vp.patient_id = ? AND vp.shift_id = ? AND vp.clinic_id = ? AND vp.doctor_id = ?
                              AND DATE(vp.visit_date) = DATE(?::date) AND vp.patient_visit_no = ?
                              AND (vp.delete_flag IS NULL OR vp.delete_flag = false)
                            ORDER BY vp.sequence_id
                        """;
                prescriptions = jdbcTemplate.queryForList(
                        prescriptionSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            }
            associatedData.put("prescriptions", prescriptions);

            // 6) Investigations/Lab tests
            String labsSql = """
                        SELECT lab_test_description AS id
                        FROM patient_visit_labtestasked pvla
                        WHERE pvla.patient_id = ? AND pvla.shift_id = ? AND pvla.clinic_id = ? AND pvla.doctor_id = ?
                          AND DATE(pvla.visit_date) = DATE(?::date) AND pvla.patient_visit_no = ?
                          AND (pvla.delete_flag IS NULL OR pvla.delete_flag = false)
                    """;
            List<Map<String, Object>> labTests = jdbcTemplate.queryForList(
                    labsSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            associatedData.put("investigations", labTests);
            associatedData.put("labTests", labTests); // Alias for compatibility

            // 7) Billing - prefer overwrite
            String billingOverwriteSql = """
                        SELECT billing_details, billing_group_name, billing_subgroup_name,
                               default_fees, collected_fees,
                               billing_group_name || '*' || billing_subgroup_name || '*' || billing_details AS billing_id
                        FROM patient_visit_billinginfooverwrite pvb
                        WHERE pvb.patient_id = ? AND pvb.clinic_id = ? AND pvb.doctor_id = ?
                          AND pvb.patient_visit_no = ?
                          AND (pvb.delete_flag IS NULL OR pvb.delete_flag = false)
                    """;
            List<Map<String, Object>> billing = jdbcTemplate.queryForList(
                    billingOverwriteSql, patientId, clinicId, doctorId, patientVisitNo);
            if (billing.isEmpty()) {
                String billingSql = """
                            SELECT billing_details, billing_group_name, billing_subgroup_name,
                                   default_fees, collected_fees,
                                   billing_group_name || '*' || billing_subgroup_name || '*' || billing_details AS billing_id
                            FROM patient_visit_billinginfo pvb
                            WHERE pvb.patient_id = ? AND pvb.clinic_id = ? AND pvb.doctor_id = ?
                              AND pvb.patient_visit_no = ?
                              AND (pvb.delete_flag IS NULL OR pvb.delete_flag = false)
                        """;
                billing = jdbcTemplate.queryForList(
                        billingSql, patientId, clinicId, doctorId, patientVisitNo);
            }
            associatedData.put("billing", billing);

            // 8) Instruction Groups - Fetch from visit_groups_instructions table (matching
            // stored procedure logic)
            // Use date-only comparison (DATE(visit_date) = DATE(?)) to match stored
            // procedure behavior
            // and be consistent with how other tables (complaints, diagnosis, medicines,
            // etc.) are fetched
            try {
                logger.info(
                        "Fetching instruction groups for visit: patientId={}, shiftId={}, clinicId={}, doctorId={}, visitDate={}, patientVisitNo={}",
                        patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);

                // First, try date-only comparison (primary method)
                // Query without DISTINCT ON first to see all records, then deduplicate in code
                // This ensures we catch all duplicates even if they have slight differences
                String instructionGroupsSql = """
                            SELECT doctor_id, clinic_id, shift_id, patient_id, patient_visit_no, visit_date,
                                   group_description, instructions_description, sequence_no,
                                   created_on, createdby_name, modified_on, modifiedby_name
                            FROM visit_groups_instructions vgi
                            WHERE vgi.patient_id = ? AND vgi.shift_id = ? AND vgi.clinic_id = ? AND vgi.doctor_id = ?
                              AND DATE(vgi.visit_date) = DATE(?::date) AND vgi.patient_visit_no = ?
                            ORDER BY vgi.group_description ASC, vgi.instructions_description ASC, vgi.sequence_no ASC, vgi.visit_date DESC
                        """;
                List<Map<String, Object>> instructionGroupsRaw = jdbcTemplate.queryForList(
                        instructionGroupsSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);

                logger.info("Found {} raw instruction group records from database using date-only comparison",
                        instructionGroupsRaw.size());

                // Debug: Check for duplicates in raw results
                if (!instructionGroupsRaw.isEmpty()) {
                    java.util.Map<String, Integer> instructionCounts = new java.util.HashMap<>();
                    for (Map<String, Object> rawRow : instructionGroupsRaw) {
                        String instr = rawRow.get("instructions_description") != null
                                ? ((String) rawRow.get("instructions_description")).trim().toLowerCase()
                                : "";
                        instructionCounts.put(instr, instructionCounts.getOrDefault(instr, 0) + 1);
                    }
                    logger.info("Raw instruction breakdown: {} unique instruction texts from {} total records",
                            instructionCounts.size(), instructionGroupsRaw.size());
                    for (java.util.Map.Entry<String, Integer> entry : instructionCounts.entrySet()) {
                        if (entry.getValue() > 1) {
                            logger.warn("⚠️ Found {} duplicate(s) of instruction: '{}'", entry.getValue() - 1,
                                    entry.getKey());
                        }
                    }
                }

                // If no results found with date-only comparison, try exact timestamp matching
                // (instruction groups are saved with exact visit_date timestamp from
                // patient_visits)
                if (instructionGroupsRaw.isEmpty() && exactVisitDate != null) {
                    logger.info("Trying exact timestamp match for instruction groups: {}", exactVisitDate);
                    String exactMatchSql = """
                                SELECT doctor_id, clinic_id, shift_id, patient_id, patient_visit_no, visit_date,
                                       group_description, instructions_description, sequence_no,
                                       created_on, createdby_name, modified_on, modifiedby_name
                                FROM visit_groups_instructions vgi
                                WHERE vgi.patient_id = ? AND vgi.shift_id = ? AND vgi.clinic_id = ? AND vgi.doctor_id = ?
                                  AND vgi.visit_date = ? AND vgi.patient_visit_no = ?
                                ORDER BY vgi.group_description ASC, vgi.instructions_description ASC, vgi.sequence_no ASC, vgi.visit_date DESC
                            """;
                    instructionGroupsRaw = jdbcTemplate.queryForList(
                            exactMatchSql, patientId, shiftId, clinicId, doctorId, exactVisitDate, patientVisitNo);
                    logger.info("Found {} raw instruction group records using exact timestamp match",
                            instructionGroupsRaw.size());
                }

                // If still no results, try without DISTINCT ON as fallback (in case DISTINCT ON
                // syntax causes issues)
                if (instructionGroupsRaw.isEmpty() && exactVisitDate != null) {
                    logger.info("Trying fallback query without DISTINCT ON for instruction groups");
                    String fallbackSql = """
                                SELECT doctor_id, clinic_id, shift_id, patient_id, patient_visit_no, visit_date,
                                       group_description, instructions_description, sequence_no,
                                       created_on, createdby_name, modified_on, modifiedby_name
                                FROM visit_groups_instructions vgi
                                WHERE vgi.patient_id = ? AND vgi.shift_id = ? AND vgi.clinic_id = ? AND vgi.doctor_id = ?
                                  AND DATE(vgi.visit_date) = DATE(?::date) AND vgi.patient_visit_no = ?
                                ORDER BY vgi.group_description ASC, vgi.instructions_description ASC, vgi.sequence_no ASC
                            """;
                    instructionGroupsRaw = jdbcTemplate.queryForList(
                            fallbackSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
                    logger.info("Found {} raw instruction group records using fallback query (no DISTINCT ON)",
                            instructionGroupsRaw.size());
                }

                // If still no results, try to find any records for this composite key to help
                // debug
                if (instructionGroupsRaw.isEmpty()) {
                    String debugSql = """
                                SELECT COUNT(*) as count,
                                       MIN(visit_date) as min_date,
                                       MAX(visit_date) as max_date,
                                       STRING_AGG(DISTINCT visit_date::text, ', ') as all_dates
                                FROM visit_groups_instructions vgi
                                WHERE vgi.patient_id = ? AND vgi.shift_id = ? AND vgi.clinic_id = ?
                                  AND vgi.doctor_id = ? AND vgi.patient_visit_no = ?
                            """;
                    List<Map<String, Object>> debugInfo = jdbcTemplate.queryForList(
                            debugSql, patientId, shiftId, clinicId, doctorId, patientVisitNo);
                    if (!debugInfo.isEmpty() && debugInfo.get(0).get("count") != null) {
                        Long count = ((Number) debugInfo.get(0).get("count")).longValue();
                        if (count > 0) {
                            logger.warn(
                                    "⚠️ Found {} instruction group records for composite key but date mismatch! Query date: {}, Exact date: {}, DB dates: {}",
                                    count, visitDate, exactVisitDate, debugInfo.get(0).get("all_dates"));
                            logger.warn(
                                    "⚠️ This suggests instruction groups exist but with different visit_date. Try checking database directly.");
                        } else {
                            logger.info("No instruction group records found in database for this composite key");
                        }
                    }
                }

                // Convert to VisitGroupsInstructions-like structure for processing
                // Deduplicate aggressively: first by full composite key, then by instruction
                // description alone
                // This ensures we don't have duplicate instruction descriptions even with
                // different groups/sequences
                java.util.Set<String> seenInstructions = new java.util.LinkedHashSet<>();
                java.util.Set<String> seenInstructionDescriptions = new java.util.LinkedHashSet<>(); // For
                                                                                                     // instruction-only
                                                                                                     // deduplication
                List<Map<String, Object>> visitInstructionsList = new ArrayList<>();

                logger.info("Processing {} raw instruction records for deduplication", instructionGroupsRaw.size());

                // Log all raw records for debugging
                for (int i = 0; i < instructionGroupsRaw.size(); i++) {
                    Map<String, Object> rawRow = instructionGroupsRaw.get(i);
                    logger.debug("Raw record {}: group='{}', instruction='{}', sequence={}",
                            i, rawRow.get("group_description"), rawRow.get("instructions_description"),
                            rawRow.get("sequence_no"));
                }

                for (Map<String, Object> row : instructionGroupsRaw) {
                    // Normalize strings: trim, lowercase, and normalize whitespace (multiple spaces
                    // to single space)
                    String groupDesc = row.get("group_description") != null
                            ? ((String) row.get("group_description")).trim().replaceAll("\\s+", " ").toLowerCase()
                            : "";
                    String instructionDesc = row.get("instructions_description") != null
                            ? ((String) row.get("instructions_description")).trim().replaceAll("\\s+", " ")
                                    .toLowerCase()
                            : "";
                    Object seqNoObj = row.get("sequence_no");
                    Integer sequenceNo = seqNoObj != null
                            ? (seqNoObj instanceof Integer ? (Integer) seqNoObj : Integer.valueOf(seqNoObj.toString()))
                            : 0;

                    // Create unique keys for deduplication
                    // 1. Full composite key: group + instruction + sequence (for exact duplicates)
                    String compositeKey = String.format("%s|||%s|||%d", groupDesc, instructionDesc, sequenceNo);
                    // 2. Instruction description only (to catch duplicates with different
                    // groups/sequences)
                    // Normalize to handle any whitespace or case differences
                    String instructionOnlyKey = instructionDesc;

                    logger.debug("Processing: instruction='{}' (normalized: '{}'), group='{}', sequence={}",
                            row.get("instructions_description"), instructionOnlyKey, groupDesc, sequenceNo);

                    // Only add if we haven't seen this exact instruction description before
                    // This ensures each instruction description appears only once, regardless of
                    // group or sequence
                    if (!seenInstructionDescriptions.contains(instructionOnlyKey)) {
                        seenInstructionDescriptions.add(instructionOnlyKey);
                        seenInstructions.add(compositeKey);
                        visitInstructionsList.add(row);
                        logger.info("✅ Added unique instruction: '{}' (group={}, sequence={})",
                                row.get("instructions_description"), row.get("group_description"), sequenceNo);
                    } else {
                        logger.warn(
                                "⚠️ DUPLICATE DETECTED - Skipping instruction: '{}' (group={}, sequence={}) - Already seen!",
                                row.get("instructions_description"), row.get("group_description"), sequenceNo);
                    }
                }

                logger.info("Deduplication summary: {} unique instructions from {} raw records",
                        visitInstructionsList.size(), instructionGroupsRaw.size());

                if (instructionGroupsRaw.size() > visitInstructionsList.size()) {
                    logger.info("Processed {} unique instruction records (removed {} duplicates from {} total)",
                            visitInstructionsList.size(), instructionGroupsRaw.size() - visitInstructionsList.size(),
                            instructionGroupsRaw.size());
                }

                if (!visitInstructionsList.isEmpty()) {
                    // Format instruction groups according to stored procedure logic
                    // Group by group_description and create separate lists for groups and
                    // instructions
                    List<Map<String, Object>> instructionGroups = new ArrayList<>();
                    List<Map<String, Object>> instructionDetails = new ArrayList<>();

                    // Track unique groups
                    java.util.Set<String> uniqueGroups = new java.util.HashSet<>();

                    // Final deduplication pass: ensure we only add unique instruction descriptions
                    // Since we already deduplicated by instruction description in the first pass,
                    // this should be redundant but serves as a safety check
                    java.util.Set<String> seenInstructionDetails = new java.util.LinkedHashSet<>();

                    for (Map<String, Object> instructionRow : visitInstructionsList) {
                        // Normalize strings: trim and normalize whitespace
                        String groupDesc = instructionRow.get("group_description") != null
                                ? ((String) instructionRow.get("group_description")).trim().replaceAll("\\s+", " ")
                                : "";
                        String instructionDesc = instructionRow.get("instructions_description") != null
                                ? ((String) instructionRow.get("instructions_description")).trim().replaceAll("\\s+",
                                        " ")
                                : "";
                        Object seqNoObj = instructionRow.get("sequence_no");
                        Integer sequenceNo = seqNoObj != null
                                ? (seqNoObj instanceof Integer ? (Integer) seqNoObj
                                        : Integer.valueOf(seqNoObj.toString()))
                                : 0;

                        // Add to groups list if not already added
                        String groupKey = groupDesc.toLowerCase().replaceAll("\\s+", " ");
                        if (!uniqueGroups.contains(groupKey)) {
                            Map<String, Object> group = new HashMap<>();
                            group.put("group_description", groupDesc);
                            group.put("Group_Description", groupDesc); // Alias for compatibility
                            instructionGroups.add(group);
                            uniqueGroups.add(groupKey);
                            logger.debug("Added instruction group: '{}'", groupDesc);
                        }

                        // Final check: use instruction description only as unique key (normalized)
                        // Normalize: lowercase, trim, and collapse whitespace
                        String instructionDetailKey = instructionDesc.toLowerCase().replaceAll("\\s+", " ");

                        logger.debug("Final pass check: instruction='{}' (key: '{}')", instructionDesc,
                                instructionDetailKey);

                        // Only add if we haven't seen this exact instruction description before
                        if (!seenInstructionDetails.contains(instructionDetailKey)) {
                            seenInstructionDetails.add(instructionDetailKey);
                            Map<String, Object> instructionDetail = new HashMap<>();
                            instructionDetail.put("group_description", groupDesc);
                            instructionDetail.put("Group_Description", groupDesc); // Alias for compatibility
                            instructionDetail.put("instructions_description", instructionDesc);
                            instructionDetail.put("Instructions_Description", instructionDesc); // Alias for
                                                                                                // compatibility
                            instructionDetail.put("sequence_no", sequenceNo);
                            instructionDetail.put("Sequence_No", sequenceNo); // Alias for compatibility
                            instructionDetails.add(instructionDetail);
                            logger.info("✅ Added to final instructions list: '{}'", instructionDesc);
                        } else {
                            logger.warn("⚠️ DUPLICATE IN FINAL PASS - Skipping: '{}' (group={}, sequence={})",
                                    instructionDesc, groupDesc, sequenceNo);
                        }
                    }

                    logger.info("Final deduplication: {} unique instruction descriptions from {} processed records",
                            instructionDetails.size(), visitInstructionsList.size());

                    // Final verification: Check for any duplicates in instructionDetails before
                    // adding to response
                    java.util.Set<String> finalCheck = new java.util.HashSet<>();
                    List<Map<String, Object>> verifiedInstructions = new ArrayList<>();
                    for (Map<String, Object> instr : instructionDetails) {
                        String instrText = instr.get("instructions_description") != null
                                ? ((String) instr.get("instructions_description")).trim().toLowerCase()
                                        .replaceAll("\\s+", " ")
                                : "";
                        if (!finalCheck.contains(instrText)) {
                            finalCheck.add(instrText);
                            verifiedInstructions.add(instr);
                        } else {
                            logger.error(
                                    "❌ CRITICAL: Found duplicate in final instructionDetails list! Instruction: '{}'",
                                    instr.get("instructions_description"));
                        }
                    }

                    // instructionDetails will be replaced below if duplicates found

                    if (instructionDetails.size() != verifiedInstructions.size()) {
                        logger.info("✅ Final verified instructions count: {} (removed {} duplicates from {} original)",
                                verifiedInstructions.size(), instructionDetails.size() - verifiedInstructions.size(),
                                instructionDetails.size());
                        instructionDetails = verifiedInstructions; // Use the verified list
                    } else {
                        logger.info("✅ Final verified instructions count: {} (no duplicates found)",
                                instructionDetails.size());
                    }

                    // Store both for backward compatibility with stored procedure format
                    // instructionGroups: unique group descriptions (Tables[2] in stored procedure)
                    // instructions: all instruction details with group info (Tables[3] in stored
                    // procedure)
                    associatedData.put("instructionGroups", instructionGroups);
                    associatedData.put("instructions", instructionDetails);

                    // Log final counts before returning
                    logger.info("📊 Final counts being added to response - instructionGroups: {}, instructions: {}",
                            instructionGroups.size(), instructionDetails.size());

                    // Also create a nested structure for easier frontend consumption
                    // Group instructions by group_description
                    Map<String, List<Map<String, Object>>> instructionsByGroup = new HashMap<>();
                    for (Map<String, Object> instructionDetail : instructionDetails) {
                        String groupDesc = (String) instructionDetail.get("group_description");
                        instructionsByGroup.computeIfAbsent(groupDesc, k -> new ArrayList<>()).add(instructionDetail);
                    }
                    associatedData.put("instructionGroupsWithDetails", instructionsByGroup);

                    logger.info("✅ Successfully fetched {} instruction groups with {} instruction details for visit",
                            instructionGroups.size(), instructionDetails.size());
                } else {
                    logger.warn(
                            "⚠️ No instruction groups found for visit: patientId={}, visitNo={}, visitDate={}, exactVisitDate={}",
                            patientId, patientVisitNo, visitDate, exactVisitDate);
                    logger.warn(
                            "⚠️ This could mean: 1) No instruction groups were saved for this visit, 2) Date mismatch, 3) Query issue");
                    associatedData.put("instructionGroups", new ArrayList<>());
                    associatedData.put("instructions", new ArrayList<>());
                }
            } catch (Exception instructionEx) {
                logger.error("❌ Error fetching instruction groups for visit: patientId={}, visitNo={}, error: {}",
                        patientId, patientVisitNo, instructionEx.getMessage(), instructionEx);
                logger.error("❌ Exception details: ", instructionEx);
                // Don't fail the whole request, just log the error
                associatedData.put("instructionGroups", new ArrayList<>());
                associatedData.put("instructions", new ArrayList<>());
            }

            logger.debug(
                    "Fetched associated data: complaints={}, diagnosis={}, medicines={}, prescriptions={}, investigations={}, billing={}, instructionGroups={}",
                    complaints.size(), diagnosis.size(), medicines.size(), prescriptions.size(), labTests.size(),
                    billing.size(),
                    associatedData.get("instructionGroups") != null
                            ? ((List<?>) associatedData.get("instructionGroups")).size()
                            : 0);

        } catch (Exception e) {
            logger.error("Error fetching associated visit data: {}", e.getMessage(), e);
            // Don't fail the whole request, just log the error
            associatedData.put("error", "Failed to fetch some associated data: " + e.getMessage());
        }

        return associatedData;
    }

    /**
     * Get all visits for a patient with complete details
     */
    public Map<String, Object> getAllVisitsForPatient(String patientId) {
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Getting all visits for patient: {}", patientId);

            List<PatientVisit> allVisits = patientVisitRepository
                    .findByPatientIdAndDeleteFlagOrderByVisitDateDesc(patientId, false);

            if (!allVisits.isEmpty()) {
                List<Map<String, Object>> visitList = new ArrayList<>();

                for (PatientVisit visit : allVisits) {
                    // Calculate PLR indicators for each visit
                    String plrIndicators = calculatePlrIndicators(visit);
                    visitList.add(mapVisitToResponseWithPlr(visit, plrIndicators));
                }

                response.put("success", true);
                response.put("found", true);
                response.put("totalVisits", allVisits.size());
                response.put("visits", visitList);

                logger.info("Found {} visits for patient {} with PLR indicators", allVisits.size(), patientId);
            } else {
                response.put("success", true);
                response.put("found", false);
                response.put("totalVisits", 0);
                response.put("message", "No completed visits found for patient");

                logger.info("No completed visits found for patient: {}", patientId);
            }

        } catch (Exception e) {
            logger.error("Error getting all visits for patient {}: {}", patientId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get all visits: " + e.getMessage());
        }

        return response;
    }

    /**
     * JPA/JDBC replacement for USP_Get_MasterLists (subset required by UI).
     * Returns grouped lists: vitals, complaints, diagnosis, dressing,
     * medicines (overwrite preferred), prescriptions (overwrite preferred),
     * labTestsAsked, billing (overwrite preferred).
     */
    public Map<String, Object> getMasterLists(
            String patientId,
            Short shiftId,
            String clinicId,
            String doctorId,
            LocalDate visitDate,
            Integer patientVisitNo) {

        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Building master-lists for patient: {}, visitNo: {}, date: {}", patientId, patientVisitNo,
                    visitDate);

            // 1) Vitals from patient_visits
            String vitalsSql = """
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
                               pv.habits_comments, pv.allergy_dtls, pv.discount,
                               pv.sugar, pv.tpr, pv.odeama, pv.pallor,
                               COALESCE(pv.in_person,false) AS in_person,
                               pv.payment_by_id, pv.payment_remark, pv.fees_collected, pv.receipt_number,
                               pv.follow_up, pv.follow_up_type, pv.follow_up_date,pv.follow_up_comment,
                               pv.thtext, pv.offline_reason, pv.comment,pv.impression,pv.symptom_comment,pv.observation,pv.visit_comments,pv.current_medicines,pv.important_findings,pv.additional_comments,pv.surgical_history_past_history
                        FROM patient_visits pv
                        WHERE pv.patient_id = ? AND pv.shift_id = ? AND pv.clinic_id = ?
                          AND pv.doctor_id = ? AND DATE(pv.visit_date) = ? AND pv.patient_visit_no = ?
                          AND COALESCE(pv.delete_flag,false) = false
                    """;
            List<Map<String, Object>> vitals = jdbcTemplate.queryForList(
                    vitalsSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);

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

            // 5) Medicines - prefer overwrite
            String medicineOverwriteSql = """
                        SELECT vm.short_description || '*' || vm.medicine_description AS id,
                               vm.short_description AS short_description,
                               vm.medicine_description,
                               vm.morning, vm.afternoon, vm.night, vm.no_of_days, vm.instruction
                        FROM visit_medicine_overwrite vm
                        WHERE vm.patient_id = ? AND vm.shift_id = ? AND vm.clinic_id = ? AND vm.doctor_id = ?
                          AND DATE(vm.visit_date) = ? AND vm.patient_visit_no = ?
                          AND COALESCE(vm.delete_indicator,false) = false AND COALESCE(vm.delete_flag,false) = false
                    """;
            List<Map<String, Object>> medicines = jdbcTemplate.queryForList(
                    medicineOverwriteSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            if (medicines.isEmpty()) {
                String medicineSql = """
                            SELECT vm.short_description || '*' || vm.medicine_description AS id,
                                   vm.short_description AS short_description,
                                   vm.medicine_description,
                                   vm.morning, vm.afternoon, vm.night, vm.no_of_days, vm.instruction
                            FROM visit_medicine vm
                            WHERE vm.patient_id = ? AND vm.shift_id = ? AND vm.clinic_id = ? AND vm.doctor_id = ?
                              AND DATE(vm.visit_date) = ? AND vm.patient_visit_no = ?
                              AND COALESCE(vm.delete_flag,false) = false
                        """;
                medicines = jdbcTemplate.queryForList(
                        medicineSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            }

            // 6) Prescriptions - prefer overwrite
            String prescriptionOverwriteSql = """
                        SELECT vp.medicine_name AS medicine_name,
                               vp.brand_name AS brand_name,
                               vp.medicine_name || '*' || vp.brand_name || '*' || vp.cat_short_name || '*' || vp.catsub_description AS id,
                               vp.morning, vp.afternoon, vp.night, vp.no_of_days, vp.instruction, vp.sequence_id
                        FROM visit_prescription_overwrite vp
                        WHERE vp.patient_id = ? AND vp.shift_id = ? AND vp.clinic_id = ? AND vp.doctor_id = ?
                          AND DATE(vp.visit_date) = ? AND vp.patient_visit_no = ?
                          AND COALESCE(vp.delete_indicator,false) = false AND COALESCE(vp.delete_flag,false) = false
                        ORDER BY vp.sequence_id
                    """;
            List<Map<String, Object>> prescriptions = jdbcTemplate.queryForList(
                    prescriptionOverwriteSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);
            if (prescriptions.isEmpty()) {
                String prescriptionSql = """
                            SELECT vp.medicine_name AS medicine_name,
                                   vp.brand_name AS brand_name,
                                   vp.medicine_name || '*' || vp.brand_name || '*' || vp.cat_short_name || '*' || vp.catsub_description AS id,
                                   vp.morning, vp.afternoon, vp.night, vp.no_of_days, vp.instruction, vp.sequence_id
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
            List<Map<String, Object>> labTests = jdbcTemplate.queryForList(
                    labsSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);

            // 8) Billing - prefer overwrite
            String billingOverwriteSql = """
                        SELECT billing_details, billing_group_name, billing_subgroup_name,
                               default_fees, collected_fees,
                               billing_group_name || '*' || billing_subgroup_name || '*' || billing_details AS billing_id
                        FROM patient_visit_billinginfooverwrite pvb
                        WHERE pvb.patient_id = ? AND pvb.clinic_id = ? AND pvb.doctor_id = ?
                          AND pvb.patient_visit_no = ? AND COALESCE(pvb.delete_flag,false) = false
                    """;
            List<Map<String, Object>> billing = jdbcTemplate.queryForList(
                    billingOverwriteSql, patientId, clinicId, doctorId, patientVisitNo);
            if (billing.isEmpty()) {
                String billingSql = """
                            SELECT billing_details, billing_group_name, billing_subgroup_name,
                                   default_fees, collected_fees,
                                   billing_group_name || '*' || billing_subgroup_name || '*' || billing_details AS billing_id
                            FROM patient_visit_billinginfo pvb
                            WHERE pvb.patient_id = ? AND pvb.clinic_id = ? AND pvb.doctor_id = ?
                              AND pvb.patient_visit_no = ?
                              AND COALESCE(pvb.delete_flag,false) = false
                        """;
                billing = jdbcTemplate.queryForList(
                        billingSql, patientId, clinicId, doctorId, patientVisitNo);
            }

            // 9) Instruction Groups
            String instructionGroupsSql = """
                        SELECT doctor_id, clinic_id, shift_id, patient_id, patient_visit_no, visit_date,
                               group_description, instructions_description, sequence_no,
                               created_on, createdby_name, modified_on, modifiedby_name
                        FROM visit_groups_instructions vgi
                        WHERE vgi.patient_id = ? AND vgi.shift_id = ? AND vgi.clinic_id = ? AND vgi.doctor_id = ?
                          AND DATE(vgi.visit_date) = ? AND vgi.patient_visit_no = ?
                        ORDER BY vgi.group_description ASC, vgi.sequence_no ASC
                    """;
            List<Map<String, Object>> instructionGroupsRaw = jdbcTemplate.queryForList(
                    instructionGroupsSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);

            logger.info("Found {} raw instruction group records for master-lists", instructionGroupsRaw.size());

            // Deduplicate and format instruction groups
            List<Map<String, Object>> instructionGroups = new ArrayList<>();
            List<Map<String, Object>> instructionDetails = new ArrayList<>();
            java.util.Set<String> uniqueGroups = new java.util.HashSet<>();
            java.util.Set<String> seenInstructions = new java.util.LinkedHashSet<>();

            for (Map<String, Object> row : instructionGroupsRaw) {
                String groupDesc = row.get("group_description") != null ? ((String) row.get("group_description")).trim()
                        : "";
                String instructionDesc = row.get("instructions_description") != null
                        ? ((String) row.get("instructions_description")).trim()
                        : "";
                Object seqNoObj = row.get("sequence_no");
                Integer sequenceNo = seqNoObj != null
                        ? (seqNoObj instanceof Integer ? (Integer) seqNoObj : Integer.valueOf(seqNoObj.toString()))
                        : 0;

                // Create a unique key for deduplication
                String uniqueKey = (groupDesc + "|||" + instructionDesc + "|||" + sequenceNo).toLowerCase();

                // Only process if we haven't seen this exact instruction before
                if (!seenInstructions.contains(uniqueKey)) {
                    seenInstructions.add(uniqueKey);

                    // Add to groups list if not already added
                    if (!uniqueGroups.contains(groupDesc)) {
                        Map<String, Object> group = new HashMap<>();
                        group.put("group_description", groupDesc);
                        group.put("Group_Description", groupDesc); // Alias for compatibility
                        instructionGroups.add(group);
                        uniqueGroups.add(groupDesc);
                    }

                    // Add to instructions list
                    Map<String, Object> instructionDetail = new HashMap<>();
                    instructionDetail.put("group_description", groupDesc);
                    instructionDetail.put("Group_Description", groupDesc); // Alias for compatibility
                    instructionDetail.put("instructions_description", instructionDesc);
                    instructionDetail.put("Instructions_Description", instructionDesc); // Alias for compatibility
                    instructionDetail.put("sequence_no", sequenceNo);
                    instructionDetail.put("Sequence_No", sequenceNo); // Alias for compatibility
                    instructionDetails.add(instructionDetail);
                }
            }

            // 10) UI field mapping from vitals and receipt
            Map<String, Object> uiFields = new HashMap<>();
            if (!vitals.isEmpty()) {
                Map<String, Object> v = vitals.get(0);
                // Vitals
                uiFields.put("weightKg", v.get("weight_in_kgs"));
                uiFields.put("heightCm", v.get("height_in_cms"));
                uiFields.put("pulsePerMin", v.get("pulse"));
                uiFields.put("bloodPressure", v.get("blood_pressure"));
                uiFields.put("sugar", v.get("sugar"));
                uiFields.put("tpr", v.get("tpr"));
                uiFields.put("oedema", v.get("odeama"));
                uiFields.put("pallor", v.get("pallor"));
                uiFields.put("tft", v.get("thtext"));
                // Conditions
                uiFields.put("hypertension", v.get("hypertension"));
                uiFields.put("diabetes", v.get("diabetes"));
                uiFields.put("cholestrol", v.get("cholestrol"));
                uiFields.put("ihd", v.get("ihd"));
                uiFields.put("asthma", v.get("asthama"));
                uiFields.put("th", v.get("th"));
                uiFields.put("smoking", v.get("smoking"));
                uiFields.put("tobacco", v.get("tobaco"));
                uiFields.put("alcohol", v.get("alchohol"));
                // Comments / misc
                uiFields.put("instructions", v.get("instructions"));
                uiFields.put("allergyDetails", v.get("allergy_dtls"));
                uiFields.put("habitDetails", v.get("habits_comments"));
                uiFields.put("inPerson", v.get("in_person"));
                uiFields.put("followUp", v.get("follow_up"));
                uiFields.put("followUpType", v.get("follow_up_type"));
                uiFields.put("followUpDate", v.get("follow_up_date"));

                // Payment related
                BigDecimal feesToCollect = toBigDecimal(v.get("fees_to_collect"));
                BigDecimal discount = toBigDecimal(v.get("discount"));
                BigDecimal collected = toBigDecimal(v.get("fees_collected"));
                BigDecimal dues = null;
                if (feesToCollect != null) {
                    dues = feesToCollect
                            .subtract(discount != null ? discount : BigDecimal.ZERO)
                            .subtract(collected != null ? collected : BigDecimal.ZERO);
                }
                uiFields.put("billedRs", feesToCollect);
                uiFields.put("discountRs", discount);
                uiFields.put("collectedRs", collected);
                uiFields.put("duesRs", dues);
                uiFields.put("acBalanceRs", BigDecimal.ZERO); // Not tracked currently
                uiFields.put("paymentBy", v.get("payment_by_id"));
                uiFields.put("paymentRemark", v.get("payment_remark"));
                uiFields.put("receiptNo", v.get("receipt_number"));

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
                uiFields.put("reason", reason);

                // Optional: receipt details (date/amount) from receipts table
                if (v.get("receipt_number") != null) {
                    try {
                        Map<String, Object> receipt = jdbcTemplate.queryForMap(
                                "SELECT receipt_date, receipt_amount FROM patient_receipts WHERE receipt_number = ?",
                                v.get("receipt_number"));
                        uiFields.put("receiptDate", receipt.get("receipt_date"));
                        uiFields.put("receiptAmount", receipt.get("receipt_amount"));
                    } catch (Exception ignore) {
                        // keep optional fields absent if not found
                    }
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("vitals", vitals);
            data.put("complaints", complaints);
            data.put("diagnosis", diagnosis);
            data.put("dressing", dressing);
            data.put("medicines", medicines);
            data.put("prescriptions", prescriptions);
            data.put("labTestsAsked", labTests);
            data.put("billing", billing);
            data.put("instructionGroups", instructionGroups);
            data.put("instructions", instructionDetails);
            data.put("uiFields", uiFields);

            response.put("success", true);
            response.put("patientId", patientId);
            response.put("clinicId", clinicId);
            response.put("doctorId", doctorId);
            response.put("shiftId", shiftId);
            response.put("visitDate", visitDate);
            response.put("patientVisitNo", patientVisitNo);
            response.put("data", data);

            return response;
        } catch (Exception e) {
            logger.error("Error building master-lists: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to build master lists: " + e.getMessage());
            return response;
        }
    }

    /**
     * Get patient previous visits with comprehensive details (replicating
     * USP_Get_Patient_Previous_Visits logic)
     * Now includes prescriptions, complaints, diagnosis, and doctor information
     */
    public Map<String, Object> getPatientPreviousVisits(String patientId, String doctorId, String clinicId,
            LocalDate todaysVisitDate) {
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Getting previous visits for patient: {}, doctor: {}, clinic: {}, today: {}",
                    patientId, doctorId, clinicId, todaysVisitDate);

            // Use the comprehensive query that includes all related data
            List<Map<String, Object>> visitResults = patientVisitRepository
                    .findPatientPreviousVisitsWithDetails(patientId, todaysVisitDate);

            logger.info("DEBUG: Found {} visits with comprehensive data for patient {}", visitResults.size(),
                    patientId);

            if (!visitResults.isEmpty()) {
                List<Map<String, Object>> visitList = new ArrayList<>();

                // Process each visit result
                for (Map<String, Object> visitData : visitResults) {
                    Map<String, Object> formattedVisit = formatComprehensiveVisitData(visitData);
                    visitList.add(formattedVisit);

                    logger.info(
                            "DEBUG: Processed visit - Date: {}, Doctor: {}, Medicine: {}, Complaints: {}, Diagnosis: {}",
                            visitData.get("visit_date"), visitData.get("doctor_name"),
                            visitData.get("medicine_names"), visitData.get("complaints"), visitData.get("diagnosis"));
                }

                response.put("success", true);
                response.put("found", true);
                response.put("totalVisits", visitList.size());
                response.put("visits", visitList);
                response.put("debug", Map.of(
                        "totalVisitsFound", visitResults.size(),
                        "finalVisitCount", visitList.size(),
                        "includesPrescriptions", true,
                        "includesComplaints", true,
                        "includesDiagnosis", true,
                        "includesDoctorInfo", true,
                        "includesLabTestDescriptions", true));

                logger.info("Found {} previous visits with comprehensive data for patient {}", visitList.size(),
                        patientId);
            } else {
                response.put("success", true);
                response.put("found", false);
                response.put("totalVisits", 0);
                response.put("message", "No previous visits found for patient");
                response.put("debug", Map.of(
                        "totalVisitsFound", 0,
                        "finalVisitCount", 0,
                        "includesPrescriptions", true,
                        "includesComplaints", true,
                        "includesDiagnosis", true,
                        "includesDoctorInfo", true,
                        "includesLabTestDescriptions", true));

                logger.info("No previous visits found for patient: {}", patientId);
            }

        } catch (Exception e) {
            logger.error("Error getting previous visits for patient {}: {}", patientId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get previous visits: " + e.getMessage());
        }

        return response;
    }

    /**
     * Fetch detailed prescription data for a specific visit
     */
    private List<Map<String, Object>> getDetailedPrescriptionsForVisit(Map<String, Object> visitData) {
        try {
            String patientId = (String) visitData.get("patient_id");
            Object visitDateObj = visitData.get("visit_date");
            Integer patientVisitNo = (Integer) visitData.get("patient_visit_no");
            String doctorId = (String) visitData.get("doctor_id");
            String clinicId = (String) visitData.get("clinic_id");

            if (patientId == null || visitDateObj == null || patientVisitNo == null ||
                    doctorId == null || clinicId == null) {
                logger.warn(
                        "Missing required fields for prescription lookup: patientId={}, visitDate={}, visitNo={}, doctorId={}, clinicId={}",
                        patientId, visitDateObj, patientVisitNo, doctorId, clinicId);
                return new ArrayList<>();
            }

            // Convert visit date to LocalDateTime
            java.time.LocalDateTime visitDate;
            if (visitDateObj instanceof java.sql.Timestamp) {
                visitDate = ((java.sql.Timestamp) visitDateObj).toLocalDateTime();
            } else if (visitDateObj instanceof java.sql.Date) {
                visitDate = ((java.sql.Date) visitDateObj).toLocalDate().atStartOfDay();
            } else {
                logger.warn("Unsupported visit date type: {}", visitDateObj.getClass());
                return new ArrayList<>();
            }

            List<Map<String, Object>> prescriptions = patientVisitRepository
                    .findDetailedPrescriptionsForVisit(patientId, visitDate, patientVisitNo, doctorId, clinicId);

            // Format prescription data for better readability
            // Filter out empty/invalid prescriptions (no medicine name and no dosage)
            List<Map<String, Object>> formattedPrescriptions = new ArrayList<>();
            for (Map<String, Object> prescription : prescriptions) {
                // Get medicine information
                String brandName = prescription.get("brand_name") != null
                        ? prescription.get("brand_name").toString().trim()
                        : "";
                String medicineName = prescription.get("medicine_name") != null
                        ? prescription.get("medicine_name").toString().trim()
                        : "";

                // Get dosage information
                Object morningObj = prescription.get("morning");
                Object afternoonObj = prescription.get("afternoon");
                Object nightObj = prescription.get("night");
                Object noOfDaysObj = prescription.get("no_of_days");

                // Check if morning, afternoon, or night doses are non-null and non-zero
                boolean hasDosage = false;
                if (morningObj != null) {
                    try {
                        double morning = Double.parseDouble(morningObj.toString());
                        if (morning > 0)
                            hasDosage = true;
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
                if (!hasDosage && afternoonObj != null) {
                    try {
                        double afternoon = Double.parseDouble(afternoonObj.toString());
                        if (afternoon > 0)
                            hasDosage = true;
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
                if (!hasDosage && nightObj != null) {
                    try {
                        double night = Double.parseDouble(nightObj.toString());
                        if (night > 0)
                            hasDosage = true;
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }

                // Skip empty prescriptions: must have either medicine name/brand name OR valid
                // dosage
                if ((brandName.isEmpty() && medicineName.isEmpty()) && !hasDosage) {
                    logger.debug("Skipping empty prescription: brandName='{}', medicineName='{}', hasDosage={}",
                            brandName, medicineName, hasDosage);
                    continue;
                }

                Map<String, Object> formattedPrescription = new HashMap<>();

                // Basic medicine information
                formattedPrescription.put("medicineName", medicineName.isEmpty() ? null : medicineName);
                formattedPrescription.put("brandName", brandName.isEmpty() ? null : brandName);
                formattedPrescription.put("categoryDescription", prescription.get("catsub_description"));
                formattedPrescription.put("categoryShortName", prescription.get("cat_short_name"));
                formattedPrescription.put("marketedBy", prescription.get("marketed_by"));

                // Dosage information
                formattedPrescription.put("morningDose", morningObj);
                formattedPrescription.put("afternoonDose", afternoonObj);
                formattedPrescription.put("nightDose", nightObj);
                formattedPrescription.put("noOfDays", noOfDaysObj);

                // Instructions
                formattedPrescription.put("instruction", prescription.get("instruction"));

                // Sequence and audit information
                formattedPrescription.put("sequenceId", prescription.get("sequence_id"));
                formattedPrescription.put("createdOn", prescription.get("created_on"));
                formattedPrescription.put("createdBy", prescription.get("createdby_name"));
                formattedPrescription.put("modifiedOn", prescription.get("modified_on"));
                formattedPrescription.put("modifiedBy", prescription.get("modifiedby_name"));

                // Create a summary string for backward compatibility
                StringBuilder doseSummary = new StringBuilder();
                if (morningObj != null) {
                    doseSummary.append("M:").append(morningObj);
                }
                if (afternoonObj != null) {
                    if (doseSummary.length() > 0)
                        doseSummary.append(", ");
                    doseSummary.append("A:").append(afternoonObj);
                }
                if (nightObj != null) {
                    if (doseSummary.length() > 0)
                        doseSummary.append(", ");
                    doseSummary.append("N:").append(nightObj);
                }
                if (noOfDaysObj != null) {
                    if (doseSummary.length() > 0)
                        doseSummary.append(" ");
                    doseSummary.append("for ").append(noOfDaysObj).append(" days");
                }
                formattedPrescription.put("doseSummary", doseSummary.toString());

                formattedPrescriptions.add(formattedPrescription);
            }

            logger.debug("Found {} detailed prescriptions for visit: patientId={}, visitDate={}, visitNo={}",
                    formattedPrescriptions.size(), patientId, visitDate, patientVisitNo);

            return formattedPrescriptions;

        } catch (Exception e) {
            logger.error("Error fetching detailed prescriptions for visit: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Fetch dressing data for a specific visit and return as concatenated string
     */
    private String getDressingBodyPartsForVisit(Map<String, Object> visitData) {
        try {
            String patientId = (String) visitData.get("patient_id");
            Object visitDateObj = visitData.get("visit_date");
            Integer patientVisitNo = (Integer) visitData.get("patient_visit_no");
            String doctorId = (String) visitData.get("doctor_id");
            String clinicId = (String) visitData.get("clinic_id");
            Short shiftId = visitData.get("shift_id") != null ? ((Number) visitData.get("shift_id")).shortValue()
                    : null;

            if (patientId == null || visitDateObj == null || patientVisitNo == null ||
                    doctorId == null || clinicId == null || shiftId == null) {
                logger.warn(
                        "Missing required fields for dressing lookup: patientId={}, visitDate={}, visitNo={}, doctorId={}, clinicId={}, shiftId={}",
                        patientId, visitDateObj, patientVisitNo, doctorId, clinicId, shiftId);
                return "";
            }

            // Convert visit date to LocalDateTime
            java.time.LocalDateTime visitDate;
            if (visitDateObj instanceof java.sql.Timestamp) {
                visitDate = ((java.sql.Timestamp) visitDateObj).toLocalDateTime();
            } else if (visitDateObj instanceof java.sql.Date) {
                visitDate = ((java.sql.Date) visitDateObj).toLocalDate().atStartOfDay();
            } else {
                logger.warn("Unsupported visit date type for dressing: {}", visitDateObj.getClass());
                return "";
            }

            // Query dressing data
            String dressingSql = """
                        SELECT dressing_description AS dressing_description
                        FROM visit_dressing dd
                        WHERE dd.patient_id = ? AND dd.shift_id = ? AND dd.clinic_id = ? AND dd.doctor_id = ?
                          AND DATE(dd.visit_date) = DATE(?::date) AND dd.patient_visit_no = ?
                          AND (dd.delete_flag IS NULL OR dd.delete_flag = false)
                        ORDER BY dd.created_on ASC
                    """;

            List<Map<String, Object>> dressing = jdbcTemplate.queryForList(
                    dressingSql, patientId, shiftId, clinicId, doctorId, visitDate, patientVisitNo);

            // Concatenate dressing descriptions
            if (dressing != null && !dressing.isEmpty()) {
                StringBuilder dressingBodyParts = new StringBuilder();
                for (Map<String, Object> dressingRow : dressing) {
                    Object dressingDesc = dressingRow.get("dressing_description");
                    if (dressingDesc != null && !dressingDesc.toString().trim().isEmpty()) {
                        if (dressingBodyParts.length() > 0) {
                            dressingBodyParts.append("\n"); // Separate multiple dressings with newline
                        }
                        dressingBodyParts.append(dressingDesc.toString().trim());
                    }
                }
                logger.debug("Found {} dressing records for visit: patientId={}, visitDate={}, visitNo={}",
                        dressing.size(), patientId, visitDate, patientVisitNo);
                return dressingBodyParts.toString();
            }

            return "";

        } catch (Exception e) {
            logger.error("Error fetching dressing data for visit: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * Fetch billing data for a specific visit for breakup tooltip
     */
    private List<Map<String, Object>> getBillingDataForVisit(Map<String, Object> visitData) {
        try {
            String patientId = (String) visitData.get("patient_id");
            Integer patientVisitNo = (Integer) visitData.get("patient_visit_no");
            String doctorId = (String) visitData.get("doctor_id");
            String clinicId = (String) visitData.get("clinic_id");

            if (patientId == null || patientVisitNo == null || doctorId == null || clinicId == null) {
                logger.warn(
                        "Missing required fields for billing lookup: patientId={}, visitNo={}, doctorId={}, clinicId={}",
                        patientId, patientVisitNo, doctorId, clinicId);
                return new ArrayList<>();
            }

            // Try billing overwrite first, then fallback to regular billing
            String billingOverwriteSql = """
                        SELECT billing_details, billing_group_name, billing_subgroup_name,
                               default_fees, collected_fees,
                               billing_group_name || '*' || billing_subgroup_name || '*' || billing_details AS billing_id
                        FROM patient_visit_billinginfooverwrite pvb
                        WHERE pvb.patient_id = ? AND pvb.clinic_id = ? AND pvb.doctor_id = ?
                          AND pvb.patient_visit_no = ?
                          AND (pvb.delete_flag IS NULL OR pvb.delete_flag = false)
                        ORDER BY pvb.billing_group_name, pvb.billing_subgroup_name, pvb.billing_details
                    """;

            List<Map<String, Object>> billing = jdbcTemplate.queryForList(
                    billingOverwriteSql, patientId, clinicId, doctorId, patientVisitNo);

            if (billing.isEmpty()) {
                String billingSql = """
                            SELECT billing_details, billing_group_name, billing_subgroup_name,
                                   default_fees, collected_fees,
                                   billing_group_name || '*' || billing_subgroup_name || '*' || billing_details AS billing_id
                            FROM patient_visit_billinginfo pvb
                            WHERE pvb.patient_id = ? AND pvb.clinic_id = ? AND pvb.doctor_id = ?
                              AND pvb.patient_visit_no = ?
                              AND (pvb.delete_flag IS NULL OR pvb.delete_flag = false)
                            ORDER BY pvb.billing_group_name, pvb.billing_subgroup_name, pvb.billing_details
                        """;
                billing = jdbcTemplate.queryForList(
                        billingSql, patientId, clinicId, doctorId, patientVisitNo);
            }

            logger.debug("Found {} billing records for visit: patientId={}, visitNo={}",
                    billing.size(), patientId, patientVisitNo);
            return billing;

        } catch (Exception e) {
            logger.error("Error fetching billing data for visit: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Format comprehensive visit data from database query result
     */
    private Map<String, Object> formatComprehensiveVisitData(Map<String, Object> visitData) {
        Map<String, Object> visitMap = new HashMap<>();

        // Format visit date similar to stored procedure: "DD-MMM-YYYY"
        Object visitDateObj = visitData.get("visit_date");
        String formattedDate = "";
        String formattedTime = "";
        String visitDateTime = "";

        if (visitDateObj != null) {
            if (visitDateObj instanceof java.sql.Timestamp) {
                java.sql.Timestamp timestamp = (java.sql.Timestamp) visitDateObj;
                formattedDate = timestamp.toLocalDateTime()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
                formattedTime = timestamp.toLocalDateTime()
                        .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            } else if (visitDateObj instanceof java.sql.Date) {
                java.sql.Date date = (java.sql.Date) visitDateObj;
                formattedDate = date.toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
                formattedTime = "00:00:00";
            }
            visitDateTime = formattedDate + " - " + formattedTime;
        }

        // Basic visit information
        visitMap.put("Visit_Date", formattedDate);
        visitMap.put("Visit_DateTime", visitDateTime);
        visitMap.put("Patient_ID", visitData.get("patient_id"));
        visitMap.put("Patient_Visit_No", visitData.get("patient_visit_no"));
        visitMap.put("Shift_id", visitData.get("shift_id"));
        visitMap.put("Status_ID", visitData.get("status_id"));
        visitMap.put("Doctor_ID", visitData.get("doctor_id"));
        visitMap.put("Clinic_ID", visitData.get("clinic_id"));

        // Medical information with actual data from joins
        // visitMap.put("Medicine_Name", visitData.get("medicine_names") != null ?
        // visitData.get("medicine_names").toString() : "");
        visitMap.put("Medicine_Name",
                visitData.get("visit_medicines_short_description") != null
                        ? visitData.get("visit_medicines_short_description").toString()
                        : "");
        visitMap.put("Instructions",
                visitData.get("instructions") != null ? visitData.get("instructions").toString() : "");

        // Fetch detailed prescription data as nested object
        List<Map<String, Object>> detailedPrescriptions = getDetailedPrescriptionsForVisit(visitData);
        visitMap.put("Prescriptions", detailedPrescriptions);

        // Fetch dressing data and format as string
        String dressingBodyParts = getDressingBodyPartsForVisit(visitData);
        visitMap.put("Dressing", dressingBodyParts);

        // Fetch billing data for breakup tooltip
        List<Map<String, Object>> billingData = getBillingDataForVisit(visitData);
        visitMap.put("Billing", billingData);

        // Receipt information (if available)
        String receiptNo = visitData.get("receipt_number") != null ? visitData.get("receipt_number").toString() : "";
        visitMap.put("Receipt_No", receiptNo);
        if (receiptNo != null && !receiptNo.isEmpty()) {
            try {
                Map<String, Object> receipt = jdbcTemplate.queryForMap(
                        "SELECT receipt_date, receipt_amount FROM patient_receipts WHERE receipt_number = ?",
                        receiptNo);
                visitMap.put("Receipt_Date", receipt.get("receipt_date"));
                visitMap.put("Receipt_Amount", receipt.get("receipt_amount"));
            } catch (Exception ignore) {
                // keep optional fields absent if not found
            }
        }
        // Remark/comment field
        // visitMap.put("Remark", visitData.get("comment") != null ?
        // visitData.get("comment").toString() : "");
        visitMap.put("Remark",
                visitData.get("additional_instructions") != null ? visitData.get("additional_instructions").toString()
                        : "");
        visitMap.put("Weight_IN_KGS", visitData.get("weight_in_kgs") != null ? visitData.get("weight_in_kgs") : 0);
        visitMap.put("Visit_Comments",
                visitData.get("visit_comments") != null ? visitData.get("visit_comments").toString() : "");
        visitMap.put("Observation",
                visitData.get("observation") != null ? visitData.get("observation").toString() : "");

        // Visit type and additional info with actual data
        visitMap.put("Visit_Type", "Patient_Visit");
        visitMap.put("Complaints", visitData.get("complaints") != null ? visitData.get("complaints").toString() : "");
        visitMap.put("complaint_comments",
                visitData.get("complaint_comments") != null ? visitData.get("complaint_comments").toString() : "");
        visitMap.put("Diagnosis", visitData.get("diagnosis") != null ? visitData.get("diagnosis").toString() : "");
        visitMap.put("FollowUp_Description",
                visitData.get("followup_description") != null ? visitData.get("followup_description").toString() : "");
        visitMap.put("Lab_Test_Descriptions",
                visitData.get("lab_test_descriptions") != null ? visitData.get("lab_test_descriptions").toString()
                        : "");

        // Financial information
        visitMap.put("Fees_To_Collect",
                visitData.get("fees_to_collect") != null ? visitData.get("fees_to_collect") : 0);
        visitMap.put("Fees_Collected", visitData.get("fees_collected") != null ? visitData.get("fees_collected") : 0);

        // Doctor information from join
        visitMap.put("DoctorName", visitData.get("doctor_name") != null ? visitData.get("doctor_name").toString() : "");

        // PLR indicators from subqueries
        visitMap.put("PLR", visitData.get("plr_indicators") != null ? visitData.get("plr_indicators").toString() : "");

        // Additional formatted fields
        String shiftDesc = visitData.get("shift_id") != null ? visitData.get("shift_id").toString() : "";
        visitMap.put("Visit_Date_Shift", visitDateTime + " - " + shiftDesc);

        // Complex date-time-number field
        String dateTimeNumber = formattedDate + " - " + formattedTime + " * " +
                (visitData.get("shift_id") != null ? visitData.get("shift_id").toString() : "") + " * " +
                formattedDate + " * " +
                (visitData.get("patient_visit_no") != null ? visitData.get("patient_visit_no").toString() : "");
        visitMap.put("DATE_TIME_NUMBER", dateTimeNumber);

        visitMap.put("prevDoctor_ID", visitData.get("doctor_id"));
        visitMap.put("Addendum", visitData.get("addendum") != null ? visitData.get("addendum").toString() : "");

        // Vitals and Medical History
        visitMap.put("Pulse", visitData.get("pulse"));
        visitMap.put("Blood_Pressure", visitData.get("blood_pressure"));
        visitMap.put("Height_In_Cms", visitData.get("height_in_cms"));
        visitMap.put("Sugar", visitData.get("sugar"));
        visitMap.put("ThText", visitData.get("thtext"));
        visitMap.put("Hypertension", visitData.get("hypertension"));
        visitMap.put("Diabetes", visitData.get("diabetes"));
        visitMap.put("Cholestrol", visitData.get("cholestrol"));
        visitMap.put("Ihd", visitData.get("ihd"));
        visitMap.put("Th", visitData.get("th"));
        visitMap.put("Asthama", visitData.get("asthama"));
        visitMap.put("Smoking", visitData.get("smoking"));
        visitMap.put("Tobaco", visitData.get("tobaco"));
        visitMap.put("Alchohol", visitData.get("alchohol"));

        // Additional medical fields
        visitMap.put("Allergy", visitData.get("allergy_dtls") != null ? visitData.get("allergy_dtls").toString() : "");
        visitMap.put("Current_Complaints", visitData.get("current_complaints"));
        visitMap.put("Current_Medicines", visitData.get("current_medicines"));
        visitMap.put("Important_Findings", visitData.get("important_findings"));
        visitMap.put("Additional_Comments",
                visitData.get("additional_comments") != null ? visitData.get("additional_comments").toString() : "");
        visitMap.put("Systemic", visitData.get("systemic"));
        visitMap.put("Odeama", visitData.get("odeama"));
        visitMap.put("Pallor", visitData.get("pallor"));
        visitMap.put("GC", visitData.get("gc"));
        visitMap.put("Detailed_History", visitData.get("symptom_comment"));

        // Follow-up information
        visitMap.put("Follow_Up", visitData.get("follow_up_comment"));
        visitMap.put("Follow_Up_Flag", visitData.get("is_follow_up"));
        visitMap.put("Follow_Up_Date", visitData.get("follow_up_date"));
        visitMap.put("Follow_Up_Type", visitData.get("follow_up_type"));

        // Gynecological fields
        visitMap.put("Pregnant", visitData.get("pregnant"));
        visitMap.put("EDD", visitData.get("edd"));
        visitMap.put("Obstetric_History", visitData.get("obstetrics_history"));
        visitMap.put("Surgical_History", visitData.get("surgical_history_past_history"));
        visitMap.put("Gynec_Additional_Comments", visitData.get("gynec_additional_comments"));
        visitMap.put("FMP", visitData.get("fmp"));
        visitMap.put("PRMC", visitData.get("prmc"));
        visitMap.put("PAMC", visitData.get("pamc"));
        visitMap.put("LMP", visitData.get("lmp"));

        // Financial fields
        visitMap.put("Discount", visitData.get("discount"));
        visitMap.put("Original_Discount", visitData.get("original_discount"));

        // Status and submission
        visitMap.put("Is_Submit_Patient_Visit_Details", visitData.get("is_submit_patient_visit_details"));

        // Referral fields
        visitMap.put("Refer_ID", visitData.get("refer_id"));
        visitMap.put("Refer_Doctor_Details", visitData.get("refer_doctor_details"));

        // Plan and treatment information
        visitMap.put("Plan", visitData.get("plan") != null ? visitData.get("plan").toString() : "");
        visitMap.put("Notes", visitData.get("notes") != null ? visitData.get("notes").toString() : "");
        visitMap.put("Treatment_Plan",
                visitData.get("treatment_plan") != null ? visitData.get("treatment_plan").toString() : "");
        visitMap.put("Treatment_Comment",
                visitData.get("treatment_comment") != null ? visitData.get("treatment_comment").toString() : "");

        // Audit fields
        visitMap.put("Created_On", visitData.get("created_on"));
        visitMap.put("Created_By", visitData.get("createdby_name"));
        visitMap.put("Modified_On", visitData.get("modified_on"));
        visitMap.put("Modified_By", visitData.get("modifiedby_name"));

        return visitMap;
    }

    /**
     * Map PatientVisit entity to comprehensive response (matching stored procedure
     * output)
     */
    private Map<String, Object> mapVisitToComprehensiveResponse(PatientVisit visit) {
        Map<String, Object> visitMap = new HashMap<>();

        // Format visit date similar to stored procedure: "DD-MMM-YYYY"
        String formattedDate = visit.getVisitDate().format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

        // Format visit time
        String formattedTime = visit.getVisitTime() != null ? visit.getVisitTime().toString() : "00:00:00";

        // Create visit date-time string
        String visitDateTime = formattedDate + " - " + formattedTime;

        // Basic visit information
        visitMap.put("Visit_Date", formattedDate);
        visitMap.put("Visit_DateTime", visitDateTime);
        visitMap.put("Patient_ID", visit.getPatientId());
        visitMap.put("Patient_Visit_No", visit.getPatientVisitNo());
        visitMap.put("Shift_id", visit.getShiftId());
        visitMap.put("Status_ID", visit.getStatusId());
        visitMap.put("Doctor_ID", visit.getDoctorId());
        visitMap.put("Clinic_ID", visit.getClinicId());

        // Medical information
        visitMap.put("Medicine_Name", ""); // Would need to join with prescription table
        visitMap.put("Instructions", visit.getInstructions() != null ? visit.getInstructions() : "");
        visitMap.put("Weight_IN_KGS", visit.getWeightInKgs() != null ? visit.getWeightInKgs() : 0);
        visitMap.put("Visit_Comments", visit.getVisitComments() != null ? visit.getVisitComments() : "");
        visitMap.put("Observation", visit.getObservation() != null ? visit.getObservation() : "");

        // Visit type and additional info
        visitMap.put("Visit_Type", "Patient_Visit");
        visitMap.put("Complaints", visit.getCurrentComplaints() != null ? visit.getCurrentComplaints() : ""); // Include
                                                                                                              // current_complaints
                                                                                                              // from
                                                                                                              // patient_visits
                                                                                                              // table
        visitMap.put("Diagnosis", ""); // Would need to join with diagnosis table
        visitMap.put("FollowUp_Description", ""); // Would need to join with follow-up table

        // Financial information
        visitMap.put("Fees_To_Collect", visit.getFeesToCollect() != null ? visit.getFeesToCollect() : 0);
        visitMap.put("Fees_Collected", visit.getFeesCollected() != null ? visit.getFeesCollected() : 0);

        // Doctor information (would need to join with doctor_master)
        visitMap.put("DoctorName", ""); // Would need to join with doctor_master table

        // PLR indicators (Prescription, Lab, Radiology)
        visitMap.put("PLR", ""); // Would need to check related tables

        // Additional formatted fields
        String shiftDesc = visit.getShiftId() != null ? visit.getShiftId().toString() : "";
        visitMap.put("Visit_Date_Shift", visitDateTime + " - " + shiftDesc);

        // Complex date-time-number field
        String dateTimeNumber = formattedDate + " - " + formattedTime + " * " +
                (visit.getShiftId() != null ? visit.getShiftId().toString() : "") + " * " +
                formattedDate + " * " +
                (visit.getPatientVisitNo() != null ? visit.getPatientVisitNo().toString() : "");
        visitMap.put("DATE_TIME_NUMBER", dateTimeNumber);

        visitMap.put("prevDoctor_ID", visit.getDoctorId());
        visitMap.put("Addendum", visit.getAddendum() != null ? visit.getAddendum() : "");

        // Additional fields from entity
        visitMap.put("Pulse", visit.getPulse());
        visitMap.put("Blood_Pressure", visit.getBloodPressure());
        visitMap.put("Height_In_Cms", visit.getHeightInCms());
        visitMap.put("Sugar", visit.getSugar());
        visitMap.put("Hypertension", visit.getHypertension());
        visitMap.put("Diabetes", visit.getDiabetes());
        visitMap.put("Cholestrol", visit.getCholestrol());
        visitMap.put("Ihd", visit.getIhd());
        visitMap.put("Th", visit.getTh());
        visitMap.put("Asthama", visit.getAsthama());
        visitMap.put("Smoking", visit.getSmoking());
        visitMap.put("Tobaco", visit.getTobaco());
        visitMap.put("Alchohol", visit.getAlchohol());

        // Derive In_Person from statusId for consistency
        // WAITING (1) -> true, WITH DOCTOR (2) -> true, CONSULT ON CALL (3) -> false
        Boolean derivedInPerson = visit.getInPerson();
        if (visit.getStatusId() != null) {
            short sid = visit.getStatusId();
            if (sid == 1 || sid == 2) {
                derivedInPerson = true;
            } else if (sid == 3) {
                derivedInPerson = false;
            }
        }
        visitMap.put("In_Person", derivedInPerson != null ? derivedInPerson : false);

        // Audit fields
        visitMap.put("Created_On", visit.getCreatedOn());
        visitMap.put("Created_By", visit.getCreatedbyName());
        visitMap.put("Modified_On", visit.getModifiedOn());
        visitMap.put("Modified_By", visit.getModifiedbyName());

        return visitMap;
    }

    /**
     * Map PatientVisit entity to response map
     */
    private Map<String, Object> mapVisitToResponse(PatientVisit visit) {
        Map<String, Object> visitMap = new HashMap<>();

        // Composite Key Fields
        visitMap.put("patientId", visit.getPatientId());
        visitMap.put("doctorId", visit.getDoctorId());
        visitMap.put("clinicId", visit.getClinicId());
        visitMap.put("shiftId", visit.getShiftId());
        visitMap.put("patientVisitNo", visit.getPatientVisitNo());
        visitMap.put("visitDate", visit.getVisitDate());

        // Patient Vitals
        visitMap.put("pulse", visit.getPulse());
        visitMap.put("heightInCms", visit.getHeightInCms());
        visitMap.put("weightInKgs", visit.getWeightInKgs());
        visitMap.put("bloodPressure", visit.getBloodPressure());
        visitMap.put("sugar", visit.getSugar());
        visitMap.put("tft", visit.getThtext());
        visitMap.put("thtext", visit.getThtext()); // Also include as thtext for compatibility

        // Medical Conditions
        visitMap.put("hypertension", visit.getHypertension());
        visitMap.put("diabetes", visit.getDiabetes());
        visitMap.put("cholestrol", visit.getCholestrol());
        visitMap.put("ihd", visit.getIhd());
        visitMap.put("th", visit.getTh());
        visitMap.put("asthama", visit.getAsthama());
        visitMap.put("smoking", visit.getSmoking());
        visitMap.put("tobaco", visit.getTobaco());
        visitMap.put("alchohol", visit.getAlchohol());

        // Additional Fields
        visitMap.put("habitDetails", visit.getHabitsComments());
        visitMap.put("allergyDetails", visit.getAllergyDtls());
        visitMap.put("observation", visit.getObservation());
        // Derive inPerson from statusId for consistency
        // WAITING (1) -> true, WITH DOCTOR (2) -> true, CONSULT ON CALL (3) -> false
        Boolean derivedInPerson = visit.getInPerson();
        if (visit.getStatusId() != null) {
            short sid = visit.getStatusId();
            if (sid == 1 || sid == 2) {
                derivedInPerson = true;
            } else if (sid == 3) {
                derivedInPerson = false;
            }
        }
        visitMap.put("inPerson", derivedInPerson != null ? derivedInPerson : false);
        visitMap.put("symptomComment", visit.getSymptomComment());
        visitMap.put("impression", visit.getImpression());
        visitMap.put("attendedBy", visit.getAttendedBy());
        visitMap.put("paymentById", visit.getPaymentById());
        visitMap.put("paymentRemark", visit.getPaymentRemark());
        visitMap.put("attendedById", visit.getAttendedById());
        visitMap.put("followUp", visit.getFollowUp());
        visitMap.put("followUpFlag", visit.getIsFollowUp());
        // Fetch complaints from visit_complaints table (matching stored procedure
        // logic)
        String complaintsFromTable = getComplaintsFromVisitComplaintsTable(visit);
        visitMap.put("chiefComplaint", complaintsFromTable);
        // Also include the current_complaints field from patient_visits table
        visitMap.put("currentComplaints", visit.getCurrentComplaints());
        visitMap.put("currentMedicines", visit.getCurrentMedicines());
        visitMap.put("visitComments", visit.getVisitComments());

        // Clinical Fields
        visitMap.put("tpr", visit.getTpr());
        visitMap.put("importantFindings", visit.getImportantFindings());
        visitMap.put("additionalComments", visit.getAdditionalComments());
        visitMap.put("systemic", visit.getSystemic());
        visitMap.put("odeama", visit.getOdeama());
        visitMap.put("pallor", visit.getPallor());
        visitMap.put("gc", visit.getGc());

        // Gynecological Fields
        visitMap.put("fmp", visit.getFmp());
        visitMap.put("prmc", visit.getPrmc());
        visitMap.put("pamc", visit.getPamc());
        visitMap.put("lmp", visit.getLmp());
        visitMap.put("obstetricHistory", visit.getObstetricsHistory());
        visitMap.put("surgicalHistory", visit.getSurgicalHistoryPastHistory());
        visitMap.put("menstrualAddComments", visit.getGynecAdditionalComments());
        visitMap.put("followUpComment", visit.getFollowUpComment());
        visitMap.put("followUpDate", visit.getFollowUpDate());
        visitMap.put("pregnant", visit.getPregnant());
        visitMap.put("edd", visit.getEdd());
        visitMap.put("followUpType", visit.getFollowUpType());

        // Financial Fields
        visitMap.put("feesToCollect", visit.getFeesToCollect());
        visitMap.put("discount", visit.getDiscount());
        visitMap.put("originalDiscount", visit.getOriginalDiscount());

        // Status and User
        visitMap.put("statusId", visit.getStatusId());
        visitMap.put("isSubmitPatientVisitDetails", visit.getIsSubmitPatientVisitDetails());

        // Referral fields
        visitMap.put("referBy", visit.getReferId());
        visitMap.put("referralName", visit.getReferDoctorDetails());
        visitMap.put("referralAddress", visit.getDoctorAddress());
        visitMap.put("referralContact", visit.getDoctorMobile());
        visitMap.put("referralEmail", visit.getDoctorEmail());

        // Plan and treatment information
        visitMap.put("plan", visit.getPlan());
        visitMap.put("notes", visit.getNotes());
        visitMap.put("treatmentPlan", visit.getTreatmentPlan());
        visitMap.put("treatmentComment", visit.getTreatmentComment());

        // Instructions
        visitMap.put("instructions", visit.getInstructions());
        visitMap.put("instructionsText", visit.getInstructions());
        visitMap.put("planAdvText", visit.getInstructions());
        visitMap.put("additionalInstructions", visit.getAdditionalInstructions());

        // Audit fields
        visitMap.put("createdOn", visit.getCreatedOn());
        visitMap.put("createdBy", visit.getCreatedbyName());
        visitMap.put("modifiedOn", visit.getModifiedOn());
        visitMap.put("modifiedBy", visit.getModifiedbyName());

        return visitMap;
    }

    /**
     * Calculate PLR indicators for a visit
     */
    private String calculatePlrIndicators(PatientVisit visit) {
        try {
            String patientId = visit.getPatientId();
            LocalDateTime visitDate = visit.getVisitDate();
            Integer patientVisitNo = visit.getPatientVisitNo();
            String doctorId = visit.getDoctorId();
            String clinicId = visit.getClinicId();

            logger.info("DEBUG PLR: Calculating for patientId={}, visitDate={}, visitNo={}, doctorId={}, clinicId={}",
                    patientId, visitDate, patientVisitNo, doctorId, clinicId);

            StringBuilder plr = new StringBuilder();

            // Check for Prescription (P)
            // Use DATE() to compare only the date portion, ignoring time differences
            String prescriptionQuery = """
                    SELECT COUNT(*) FROM visit_prescription_overwrite vpo
                    WHERE vpo.patient_id = ? AND DATE(vpo.visit_date) = DATE(?) AND vpo.patient_visit_no = ?
                      AND vpo.doctor_id = ? AND vpo.clinic_id = ? AND vpo.delete_indicator = false
                    """;

            Integer prescriptionCount = jdbcTemplate.queryForObject(
                    prescriptionQuery, Integer.class, patientId, visitDate, patientVisitNo, doctorId, clinicId);

            logger.info("DEBUG PLR: Prescription count = {} (matching on DATE only)", prescriptionCount);

            if (prescriptionCount != null && prescriptionCount > 0) {
                plr.append("P");
            }

            // Check for Lab (L)
            // Use DATE() to compare only the date portion, ignoring time differences
            String labQuery = """
                    SELECT COUNT(*) FROM patient_visit_labtestasked pvla
                    WHERE pvla.patient_id = ? AND DATE(pvla.visit_date) = DATE(?) AND pvla.patient_visit_no = ?
                      AND pvla.doctor_id = ? AND pvla.clinic_id = ? AND pvla.delete_flag = false
                    """;

            Integer labCount = jdbcTemplate.queryForObject(
                    labQuery, Integer.class, patientId, visitDate, patientVisitNo, doctorId, clinicId);

            logger.info("DEBUG PLR: Lab count = {} (matching on DATE only)", labCount);

            if (labCount != null && labCount > 0) {
                plr.append("L");
            }

            // Check for Radiology (R)
            // Use DATE() to compare only the date portion, ignoring time differences
            String radiologyQuery = """
                    SELECT COUNT(*) FROM visit_procedure_findings vpf
                    WHERE vpf.patient_id = ? AND DATE(vpf.visit_date) = DATE(?) AND vpf.patient_visit_no = ?
                      AND vpf.doctor_id = ? AND vpf.clinic_id = ? AND vpf.delete_flag = false
                    """;

            Integer radiologyCount = jdbcTemplate.queryForObject(
                    radiologyQuery, Integer.class, patientId, visitDate, patientVisitNo, doctorId, clinicId);

            logger.info("DEBUG PLR: Radiology count = {} (matching on DATE only)", radiologyCount);

            if (radiologyCount != null && radiologyCount > 0) {
                plr.append("R");
            }

            String result = plr.toString();
            logger.info("DEBUG PLR: Final PLR result = '{}' (Prescription:{}, Lab:{}, Radiology:{})",
                    result, prescriptionCount, labCount, radiologyCount);

            return result;

        } catch (Exception e) {
            logger.error("Error calculating PLR indicators for visit: {}", e.getMessage(), e);
            return ""; // Return empty string if calculation fails
        }
    }

    /**
     * Get complaints from visit_complaints table for a specific visit
     * This matches the stored procedure logic exactly - no fallback mechanism
     * Returns empty string if no complaints found or on error (same as SP)
     * Now includes both complaint_description and complaint_comment fields
     */
    private String getComplaintsFromVisitComplaintsTable(PatientVisit visit) {
        try {
            List<Map<String, Object>> complaints = patientVisitRepository.findComplaintsForVisit(
                    visit.getPatientId(),
                    visit.getVisitDate(),
                    visit.getPatientVisitNo(),
                    visit.getDoctorId(),
                    visit.getClinicId());

            if (complaints.isEmpty()) {
                logger.info(
                        "No complaints found in visit_complaints table for visit: patientId={}, visitNo={}, doctorId={}, clinicId={}",
                        visit.getPatientId(), visit.getPatientVisitNo(), visit.getDoctorId(), visit.getClinicId());
                return "";
            }

            // Join complaint descriptions and comments with comma and space (matching
            // stored procedure format)
            String result = complaints.stream()
                    .map(complaint -> {
                        String description = (String) complaint.get("complaint_description");
                        String comment = (String) complaint.get("complaint_comment");

                        // Combine description and comment if both exist
                        if (description != null && !description.trim().isEmpty()) {
                            if (comment != null && !comment.trim().isEmpty()) {
                                return description + " (" + comment + ")";
                            } else {
                                return description;
                            }
                        } else if (comment != null && !comment.trim().isEmpty()) {
                            return comment;
                        }
                        return null;
                    })
                    .filter(combined -> combined != null && !combined.trim().isEmpty())
                    .collect(java.util.stream.Collectors.joining(", "));

            logger.info(
                    "Found {} complaints in visit_complaints table for visit: patientId={}, visitNo={}, result='{}'",
                    complaints.size(), visit.getPatientId(), visit.getPatientVisitNo(), result);
            return result;

        } catch (Exception e) {
            logger.error("Error fetching complaints from visit_complaints table for visit: {}", e.getMessage(), e);
            // Return empty string like stored procedure - no fallback
            return "";
        }
    }

    /**
     * Public method to test complaints fetching from visit_complaints table
     */
    public List<Map<String, Object>> getComplaintsForVisitFromTable(
            String patientId, LocalDateTime visitDate, Integer visitNo, String doctorId, String clinicId) {
        return patientVisitRepository.findComplaintsForVisit(patientId, visitDate, visitNo, doctorId, clinicId);
    }

    /**
     * Map PatientVisit entity to response map with PLR indicators
     */
    private Map<String, Object> mapVisitToResponseWithPlr(PatientVisit visit, String plrIndicators) {
        Map<String, Object> visitMap = mapVisitToResponse(visit);

        // Add PLR indicators
        visitMap.put("plr", plrIndicators);
        visitMap.put("PLR", plrIndicators); // Also add uppercase version for consistency

        return visitMap;
    }

    /**
     * Map request fields to PatientVisit entity
     */
    private void mapRequestToEntity(ComprehensiveVisitRequest req, PatientVisit visit) {
        // Patient Vitals
        visit.setPulse(req.pulse());
        visit.setHeightInCms(req.heightInCms());
        visit.setWeightInKgs(req.weightInKgs());
        visit.setBloodPressure(req.bloodPressure());
        visit.setSugar(req.sugar());
        visit.setThtext(req.tft());

        // Medical Conditions
        visit.setHypertension(req.hypertension());
        visit.setDiabetes(req.diabetes());
        visit.setCholestrol(req.cholestrol());
        visit.setIhd(req.ihd());
        visit.setTh(req.th());
        visit.setAsthama(req.asthama());
        visit.setSmoking(req.smoking());
        visit.setTobaco(req.tobaco());
        visit.setAlchohol(req.alchohol());

        // Additional Fields
        visit.setHabitsComments(req.habitDetails());
        visit.setAllergyDtls(req.allergyDetails());
        visit.setObservation(req.observation());
        visit.setInPerson(req.inPerson());
        visit.setSymptomComment(req.symptomComment());
        visit.setImpression(req.impression());
        visit.setAttendedBy(req.attendedBy());
        // Set paymentById to null if it's 0 or null (0 doesn't exist in
        // payment_type_master)
        visit.setPaymentById(
                req.paymentById() != null && req.paymentById() > 0 ? req.paymentById().shortValue() : null);
        visit.setPaymentRemark(req.paymentRemark());
        // Set attendedById to null if it's 0 or null (to avoid foreign key constraint
        // issues)
        visit.setAttendedById(req.attendedById() != null && req.attendedById() > 0 ? req.attendedById() : null);
        visit.setFollowUp(req.followUp());
        visit.setIsFollowUp(req.followUpFlag());
        visit.setCurrentMedicines(req.currentMedicines());
        visit.setVisitComments(req.visitComments());
        visit.setCurrentComplaints(req.currentComplaint());

        // Clinical Fields
        visit.setTpr(req.tpr());
        visit.setImportantFindings(req.importantFindings());
        visit.setAdditionalComments(req.additionalComments());
        visit.setSystemic(req.systemic());
        visit.setOdeama(req.odeama());
        visit.setPallor(req.pallor());
        visit.setGc(req.gc());

        // Gynecological Fields
        visit.setFmp(req.fmp());
        visit.setPrmc(req.prmc());
        visit.setPamc(req.pamc());
        visit.setLmp(req.lmp());
        visit.setObstetricsHistory(req.obstetricHistory());
        visit.setSurgicalHistoryPastHistory(req.surgicalHistory());
        visit.setGynecAdditionalComments(req.menstrualAddComments());
        visit.setFollowUpComment(req.followUpComment());
        visit.setFollowUpDate(req.followUpDate());
        visit.setPregnant(req.pregnant());
        visit.setEdd(req.edd());
        visit.setFollowUpType(req.followUpType());

        // Financial Fields
        visit.setFeesToCollect(req.feesToCollect());
        visit.setDiscount(req.discount());
        visit.setOriginalDiscount(req.originalDiscount());

        // Status and submission flags
        visit.setStatusId(req.statusId());
        visit.setIsSubmitPatientVisitDetails(req.isSubmitPatientVisitDetails());

        // Treatment fields
        visit.setTreatmentComment(req.treatmentComment());
        visit.setTreatmentPlan(req.treatmentPlan());
        visit.setPlan(req.plan());
        visit.setNotes(req.notes());
        visit.setImpressionFinding(req.impressionFinding());
        visit.setAdditionalInstructions(req.additionalInstructions());

        // Referral fields
        visit.setReferId(req.referId());
        visit.setReferDoctorDetails(req.referDoctorName());
        visit.setDoctorAddress(req.referralAddress());
        visit.setDoctorMobile(req.referralContact());
        visit.setDoctorEmail(req.referralEmail());

        // Instructions and offline fields
        visit.setInstructions(req.instructions() != null ? req.instructions() : "");
        visit.setOfflineReason(req.offlineReason() != null ? req.offlineReason() : "");
        visit.setOfflineFlag(req.offlineFlag() != null ? req.offlineFlag() : false);

        // Default values for required fields
        visit.setDeleteFlag(false);
    }

    /**
     * Validate required fields before saving
     */
    private void validateRequiredFields(PatientVisit visit) {
        StringBuilder errors = new StringBuilder();

        // Check composite key fields
        if (visit.getPatientId() == null || visit.getPatientId().trim().isEmpty()) {
            errors.append("Patient ID is required. ");
        }
        if (visit.getDoctorId() == null || visit.getDoctorId().trim().isEmpty()) {
            errors.append("Doctor ID is required. ");
        }
        if (visit.getClinicId() == null || visit.getClinicId().trim().isEmpty()) {
            errors.append("Clinic ID is required. ");
        }
        if (visit.getShiftId() == null) {
            errors.append("Shift ID is required. ");
        }
        if (visit.getPatientVisitNo() == null) {
            errors.append("Patient Visit Number is required. ");
        }
        if (visit.getVisitDate() == null) {
            errors.append("Visit Date is required. ");
        }

        // Check other required fields
        if (visit.getStatusId() == null) {
            errors.append("Status ID is required. ");
        } else {
            // Validate status_id exists in status_ref table
            boolean statusExists = statusRefRepository.existsByIdAndClinicId(visit.getStatusId(), visit.getClinicId());
            if (!statusExists) {
                errors.append("Status ID ").append(visit.getStatusId())
                        .append(" is not valid for clinic ").append(visit.getClinicId()).append(". ");
            }
        }
        if (visit.getDiscount() == null) {
            errors.append("Discount is required. ");
        }

        // Validate doctor-clinic-shift relationship
        if (visit.getDoctorId() != null && visit.getClinicId() != null && visit.getShiftId() != null) {
            boolean doctorShiftExists = doctorClinicShiftRepository.existsByIdDoctorIdAndIdClinicIdAndIdShiftId(
                    visit.getDoctorId(), visit.getClinicId(), visit.getShiftId());
            if (!doctorShiftExists) {
                errors.append("Doctor ").append(visit.getDoctorId())
                        .append(" is not assigned to clinic ").append(visit.getClinicId())
                        .append(" for shift ").append(visit.getShiftId()).append(". ");
            }
        }

        // If there are validation errors, throw exception
        if (errors.length() > 0) {
            String errorMessage = "Validation failed: " + errors.toString().trim();
            logger.error("Validation failed for visit: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Request record for updating addendum
     * Matches the USP_Update_Addendum stored procedure parameters
     */
    public record UpdateAddendumRequest(
            String addendum,
            String visitDate,
            String patientId,
            Integer patientVisitNo,
            String userId) {
    }

    /**
     * Request record for comprehensive visit data
     */
    public record ComprehensiveVisitRequest(
            // Composite Key Fields
            String patientId,
            String doctorId,
            String clinicId,
            Short shiftId,
            LocalDateTime visitDate,
            Integer patientVisitNo,

            // Patient Vitals
            Integer pulse,
            BigDecimal heightInCms,
            BigDecimal weightInKgs,
            String bloodPressure,
            String sugar,
            String tft,

            // Medical Conditions
            Boolean hypertension,
            Boolean diabetes,
            Boolean cholestrol,
            Boolean ihd,
            Boolean th,
            Boolean asthama,
            Boolean smoking,
            Boolean tobaco,
            Boolean alchohol,

            // Additional Fields
            String habitDetails,
            String allergyDetails,
            String observation,
            Boolean inPerson,
            String symptomComment,
            String impression,
            String attendedBy,
            Integer paymentById,
            String paymentRemark,
            Integer attendedById,
            String followUp,
            Boolean followUpFlag,
            String currentComplaint,
            String currentMedicines,
            String visitComments,

            // Clinical Fields
            String tpr,
            String importantFindings,
            String additionalComments,
            String systemic,
            String odeama,
            String pallor,
            String gc,

            // Gynecological Fields
            String fmp,
            String prmc,
            String pamc,
            String lmp,
            String obstetricHistory,
            String surgicalHistory,
            String menstrualAddComments,
            String followUpComment,
            LocalDateTime followUpDate,
            Boolean pregnant,
            LocalDateTime edd,
            Short followUpType,

            // Financial Fields
            BigDecimal feesToCollect,
            BigDecimal discount,
            BigDecimal originalDiscount,

            // Status and User
            Short statusId,
            String userId,
            Boolean isSubmitPatientVisitDetails,

            // Treatment fields
            String treatmentComment,
            String treatmentPlan,
            String plan,
            String notes,
            String impressionFinding,
            String additionalInstructions,

            // Referral fields
            String referId,
            String referDoctorName,
            String referralAddress,
            String referralContact,
            String referralEmail,

            // Additional fields
            String instructions,
            String offlineReason,
            Boolean offlineFlag) {
    }

    private void persistDiagnosisAndMedicinesIfProvided(ComprehensiveVisitRequest req, PatientVisit savedVisit) {
        // Expect optional arrays provided through controller layer via request context
        // map
        // For simplicity, read them from a ThreadLocal or expand signature later. Here
        // we attempt to fetch
        // from a well-known key map attached to the visit entity is not available, so
        // no-op.
    }

    /**
     * Save medicine and prescription data to overwrite tables and update visit
     * payment details
     * This method replicates the logic of USP_Insert_MedicineDataOverwrite stored
     * procedure
     * 
     * @param visitDate        - Visit date
     * @param patientVisitNo   - Patient visit number
     * @param shiftId          - Shift ID
     * @param clinicId         - Clinic ID
     * @param doctorId         - Doctor ID
     * @param patientId        - Patient ID
     * @param medicineRows     - List of medicine rows (maps with short_description,
     *                         medicine_description, morning, afternoon, night,
     *                         no_of_days, instruction, delete)
     * @param prescriptionRows - List of prescription rows (maps with brand_name,
     *                         medicine_name, marketed_by, catsub_description,
     *                         cat_short_name, morning, afternoon, night,
     *                         no_of_days, instruction, delete)
     * @param feesToCollect    - Fees to collect
     * @param feesCollected    - Fees collected
     * @param userId           - User ID
     * @param statusId         - Status ID
     * @param bloodPressure    - Blood pressure
     * @param allergyDetails   - Allergy details
     * @param habitDetails     - Habit details
     * @param comment          - Comment
     * @param paymentById      - Payment by ID
     * @param paymentRemark    - Payment remark
     * @param discount         - Discount amount
     * @return Map with success status and message
     */
    @Transactional
    public Map<String, Object> saveMedicineOverwrite(
            LocalDateTime visitDate,
            Integer patientVisitNo,
            Short shiftId,
            String clinicId,
            String doctorId,
            String patientId,
            List<Map<String, Object>> medicineRows,
            List<Map<String, Object>> prescriptionRows,
            BigDecimal feesToCollect,
            BigDecimal feesCollected,
            String userId,
            Short statusId,
            String bloodPressure,
            String allergyDetails,
            String habitDetails,
            String comment,
            Short paymentById,
            String paymentRemark,
            BigDecimal discount,
            String reason) {

        try {
            LocalDateTime now = LocalDateTime.now();
            // Resolve the exact visitDate from DB to satisfy FK constraints on overwrite
            // tables
            LocalDateTime effectiveVisitDate = visitDate;
            PatientVisitId initialVisitId = new PatientVisitId(doctorId, clinicId, shiftId, patientId, patientVisitNo,
                    visitDate);
            Optional<PatientVisit> parentVisitOpt = patientVisitRepository.findById(initialVisitId);
            if (parentVisitOpt.isPresent()) {
                effectiveVisitDate = parentVisitOpt.get().getVisitDate();
            } else {
                // Fallback: match by same keys but only by date part (ignore time
                // discrepancies)
                Optional<PatientVisit> parentByDateOpt = patientVisitRepository.findByCompositeKeyAndDate(
                        patientId, doctorId, clinicId, shiftId, patientVisitNo, visitDate.toLocalDate());
                if (parentByDateOpt.isPresent()) {
                    effectiveVisitDate = parentByDateOpt.get().getVisitDate();
                    parentVisitOpt = parentByDateOpt;
                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message",
                            "Parent visit not found for given identifiers; cannot save overwrite data.");
                    return response;
                }
            }

            // 1. MERGE medicine data into visit_medicine_overwrite
            if (medicineRows != null) {
                for (Map<String, Object> row : medicineRows) {
                    Boolean deleteIndicator = toBooleanSafe(row.get("delete"), false);

                    if (Boolean.TRUE.equals(deleteIndicator)) {
                        // Delete the record
                        VisitMedicineOverwriteId id = new VisitMedicineOverwriteId(
                                effectiveVisitDate, patientVisitNo, shiftId, clinicId, doctorId, patientId,
                                toStringSafe(row.get("short_description"), ""));
                        visitMedicineOverwriteRepository.deleteById(id);
                    } else {
                        // MERGE (insert or update)
                        VisitMedicineOverwriteId id = new VisitMedicineOverwriteId(
                                effectiveVisitDate, patientVisitNo, shiftId, clinicId, doctorId, patientId,
                                toStringSafe(row.get("short_description"), ""));

                        Optional<VisitMedicineOverwrite> existing = visitMedicineOverwriteRepository.findById(id);
                        VisitMedicineOverwrite medicine;

                        if (existing.isPresent()) {
                            // UPDATE
                            medicine = existing.get();
                        } else {
                            // INSERT
                            medicine = new VisitMedicineOverwrite();
                            medicine.setVisitDate(effectiveVisitDate);
                            medicine.setPatientVisitNo(patientVisitNo);
                            medicine.setShiftId(shiftId);
                            medicine.setClinicId(clinicId);
                            medicine.setDoctorId(doctorId);
                            medicine.setPatientId(patientId);
                            medicine.setShortDescription(toStringSafe(row.get("short_description"), ""));
                            medicine.setCreatedOn(now);
                            medicine.setCreatedbyName(userId);
                        }

                        // Set/update fields
                        medicine.setMedicineDescription(toStringSafe(row.get("medicine_description"), ""));
                        medicine.setMorning(toDoubleSafe(row.get("morning")));
                        medicine.setAfternoon(toDoubleSafe(row.get("afternoon")));
                        medicine.setNight(toDoubleSafe(row.get("night")));
                        medicine.setNoOfDays(toIntegerSafe(row.get("no_of_days")));
                        medicine.setInstruction(toStringSafe(row.get("instruction"), ""));
                        medicine.setModifiedOn(now);
                        medicine.setModifiedbyName(userId);
                        medicine.setDeleteIndicator(false);

                        visitMedicineOverwriteRepository.save(medicine);
                    }
                }
            }

            // 2. MERGE prescription data into visit_prescription_overwrite
            if (prescriptionRows != null) {
                // Get next sequence ID
                Optional<Integer> nextSeqOpt = visitPrescriptionOverwriteRepository.getNextSequenceId();
                int nextSequenceId = nextSeqOpt.orElse(1);

                for (Map<String, Object> row : prescriptionRows) {
                    Boolean deleteIndicator = toBooleanSafe(row.get("delete"), false);

                    if (Boolean.TRUE.equals(deleteIndicator)) {
                        // Delete the record
                        MedicineId id = new MedicineId(
                                doctorId, clinicId, shiftId, patientId, patientVisitNo, effectiveVisitDate,
                                toStringSafe(row.get("brand_name"), ""),
                                toStringSafe(row.get("medicine_name"), ""),
                                toStringSafe(row.get("catsub_description"), ""),
                                toStringSafe(row.get("cat_short_name"), ""));
                        visitPrescriptionOverwriteRepository.deleteById(id);
                    } else {
                        // MERGE (insert or update)
                        MedicineId id = new MedicineId(
                                doctorId, clinicId, shiftId, patientId, patientVisitNo, effectiveVisitDate,
                                toStringSafe(row.get("brand_name"), ""),
                                toStringSafe(row.get("medicine_name"), ""),
                                toStringSafe(row.get("catsub_description"), ""),
                                toStringSafe(row.get("cat_short_name"), ""));

                        Optional<Medicine> existing = visitPrescriptionOverwriteRepository.findById(id);
                        Medicine prescription;

                        if (existing.isPresent()) {
                            // UPDATE
                            prescription = existing.get();
                        } else {
                            // INSERT
                            prescription = new Medicine();
                            prescription.setDoctorId(doctorId);
                            prescription.setClinicId(clinicId);
                            prescription.setShiftId(shiftId);
                            prescription.setPatientId(patientId);
                            prescription.setPatientVisitNo(patientVisitNo);
                            prescription.setVisitDate(effectiveVisitDate);
                            prescription.setBrandName(toStringSafe(row.get("brand_name"), ""));
                            prescription.setMedicineName(toStringSafe(row.get("medicine_name"), ""));
                            prescription.setCatsubDescription(toStringSafe(row.get("catsub_description"), ""));
                            prescription.setCatShortName(toStringSafe(row.get("cat_short_name"), ""));
                            prescription.setCreatedOn(now);
                            prescription.setCreatedbyName(userId);
                            prescription.setSequenceId(nextSequenceId++);
                        }

                        // Set/update fields
                        prescription.setMarketedBy(toStringSafe(row.get("marketed_by"), ""));
                        prescription.setMorning(toDoubleSafe(row.get("morning")));
                        prescription.setAfternoon(toDoubleSafe(row.get("afternoon")));
                        prescription.setNight(toDoubleSafe(row.get("night")));
                        prescription.setNoOfDays(toIntegerSafe(row.get("no_of_days")));
                        prescription.setInstruction(toStringSafe(row.get("instruction"), ""));
                        prescription.setModifiedOn(now);
                        prescription.setModifiedbyName(userId);
                        prescription.setDeleteIndicator(false);

                        visitPrescriptionOverwriteRepository.save(prescription);
                    }
                }
            }

            // 3. Update patient_visits table
            PatientVisitId visitId = new PatientVisitId(doctorId, clinicId, shiftId, patientId, patientVisitNo,
                    effectiveVisitDate);
            Optional<PatientVisit> visitOpt = Optional.of(parentVisitOpt.get());

            if (visitOpt.isPresent()) {
                PatientVisit visit = visitOpt.get();

                // Check if fees_collected > 0 (from stored procedure logic)
                if (visit.getFeesCollected() != null && visit.getFeesCollected().compareTo(BigDecimal.ZERO) > 0) {
                    // Only update status and clinical fields
                    visit.setStatusId(statusId);
                    visit.setBloodPressure(bloodPressure);
                    visit.setAllergyDtls(allergyDetails);
                    visit.setHabitsComments(habitDetails);
                    // Patch reason to patient_visit.comments
                    if (reason != null && !reason.trim().isEmpty()) {
                        visit.setComment(reason);
                    }
                    visit.setModifiedOn(now);
                    visit.setModifiedbyName(userId);
                } else {
                    // Update all fields including payment
                    visit.setFeesCollected(feesCollected);
                    visit.setFeesToCollect(feesToCollect);
                    visit.setStatusId(statusId);
                    visit.setBloodPressure(bloodPressure);
                    visit.setAllergyDtls(allergyDetails);
                    visit.setHabitsComments(habitDetails);
                    // Patch reason to patient_visit.comments if provided, otherwise use comment
                    if (reason != null && !reason.trim().isEmpty()) {
                        visit.setComment(reason);
                    } else {
                        visit.setComment(comment);
                    }
                    // Set paymentById to null if it's 0 (0 doesn't exist in payment_type_master)
                    visit.setPaymentById(paymentById != null && paymentById > 0 ? paymentById : null);
                    visit.setPaymentRemark(paymentRemark);
                    visit.setModifiedOn(now);
                    visit.setModifiedbyName(userId);
                    visit.setDiscount(discount);
                }

                patientVisitRepository.save(visit);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Medicine and prescription data saved successfully");
            return response;

        } catch (Exception e) {
            logger.error("Error saving medicine overwrite data", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error saving medicine overwrite data: " + e.getMessage());
            return response;
        }
    }

    /**
     * Update addendum for a patient visit
     * This method replicates the functionality of USP_Update_Addendum stored
     * procedure
     * 
     * @param request - Request containing addendum text and visit identifiers
     * @return Map with success status and message
     */
    @Transactional
    public Map<String, Object> updateAddendum(UpdateAddendumRequest request) {
        logger.info("Updating addendum for patient: {}, visitNo: {}, visitDate: {}",
                request.patientId(), request.patientVisitNo(), request.visitDate());

        try {
            // Validate required fields
            if (request.patientId() == null || request.patientId().trim().isEmpty()) {
                throw new IllegalArgumentException("Patient ID is required");
            }
            if (request.visitDate() == null || request.visitDate().trim().isEmpty()) {
                throw new IllegalArgumentException("Visit date is required");
            }
            if (request.patientVisitNo() == null) {
                throw new IllegalArgumentException("Patient visit number is required");
            }
            if (request.userId() == null || request.userId().trim().isEmpty()) {
                throw new IllegalArgumentException("User ID is required");
            }

            // Parse visit date
            java.time.LocalDate visitDate;
            try {
                visitDate = java.time.LocalDate.parse(request.visitDate());
            } catch (Exception e) {
                // Try parsing with time and extract date
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(request.visitDate());
                    visitDate = dateTime.toLocalDate();
                } catch (Exception e2) {
                    throw new IllegalArgumentException(
                            "Invalid visit date format. Expected format: yyyy-MM-dd or yyyy-MM-ddTHH:mm:ss", e);
                }
            }

            // Find visits matching the criteria (same as stored procedure)
            List<PatientVisit> visits = patientVisitRepository.findByPatientIdAndVisitDateAndPatientVisitNo(
                    request.patientId(),
                    visitDate,
                    request.patientVisitNo());

            if (visits.isEmpty()) {
                logger.warn("No visit found for patient: {}, visitNo: {}, visitDate: {}",
                        request.patientId(), request.patientVisitNo(), visitDate);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Visit not found for the specified criteria");
                return response;
            }

            // Update all matching visits (stored procedure updates all matching rows)
            LocalDateTime now = LocalDateTime.now();
            int updatedCount = 0;
            for (PatientVisit visit : visits) {
                visit.setAddendum(request.addendum() != null ? request.addendum() : "");
                visit.setModifiedOn(now);
                visit.setModifiedbyName(request.userId());
                patientVisitRepository.save(visit);
                updatedCount++;
            }

            logger.info("Successfully updated addendum for {} visit(s)", updatedCount);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Addendum updated successfully");
            response.put("updatedCount", updatedCount);
            response.put("patientId", request.patientId());
            response.put("patientVisitNo", request.patientVisitNo());
            response.put("visitDate", visitDate.toString());

            return response;

        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating addendum: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Validation error: " + e.getMessage());
            return response;
        } catch (Exception e) {
            logger.error("Error updating addendum", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating addendum: " + e.getMessage());
            return response;
        }
    }

    // Helper methods
    private String toStringSafe(Object value, String defaultValue) {
        if (value == null)
            return defaultValue;
        return value.toString().trim();
    }

    private Double toDoubleSafe(Object value) {
        if (value == null)
            return null;
        if (value instanceof Double)
            return (Double) value;
        if (value instanceof Number)
            return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Integer toIntegerSafe(Object value) {
        if (value == null)
            return null;
        if (value instanceof Integer)
            return (Integer) value;
        if (value instanceof Number)
            return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean toBooleanSafe(Object value, Boolean defaultValue) {
        if (value == null)
            return defaultValue;
        if (value instanceof Boolean)
            return (Boolean) value;
        if (value instanceof Number)
            return ((Number) value).intValue() != 0;
        String str = value.toString().trim().toLowerCase();
        return str.equals("true") || str.equals("1") || str.equals("yes");
    }

}
