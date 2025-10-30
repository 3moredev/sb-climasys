package com.climasys.visits.service;

import com.climasys.entity.PatientVisit;
import com.climasys.repository.PatientVisitRepository;
import com.climasys.repository.DoctorClinicShiftRepository;
import com.climasys.repository.StatusRefRepository;
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

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal bd) return bd;
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
                request.shiftId()
            );
            
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
            
            // Check if visit already exists using a custom query that compares only the date part
            Optional<PatientVisit> existingVisit = findExistingVisitByDate(
                request.patientId(),
                request.doctorId(),
                request.clinicId(),
                request.shiftId(),
                request.patientVisitNo(),
                request.visitDate().toLocalDate()
            );
            
            logger.info("Existing visit found: {}", existingVisit.isPresent());
            
            // Debug: Check what visits exist for this patient
            List<PatientVisit> allPatientVisits = patientVisitRepository.findByPatientIdAndDeleteFlagOrderByVisitDateDesc(
                request.patientId(), false);
            logger.info("Total visits found for patient {}: {}", request.patientId(), allPatientVisits.size());
            for (PatientVisit pv : allPatientVisits) {
                logger.info("  Existing visit: PatientId={}, DoctorId={}, ClinicId={}, ShiftId={}, PatientVisitNo={}, VisitDate={}", 
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
                visit.getPatientId(), visit.getDoctorId(), visit.getStatusId(), visit.getIsSubmitPatientVisitDetails());
            
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
            
            // Persist treatment details (diagnosis and medicines) if provided via request extras
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
     * Get the last visit details for a patient (only completed visits with status 5)
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
                
                logger.info("Found last completed visit for patient {}: {} with PLR: {}", patientId, visit.getVisitDate(), plrIndicators);
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
     * This replaces the JDBC-based USP_Get_PatientAppointmentDetailsNew implementation
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
            
            // Calculate PLR indicators
            String plrIndicators = calculatePlrIndicators(visit);
            
            // Map visit to detailed response
            Map<String, Object> visitData = mapVisitToResponseWithPlr(visit, plrIndicators);
            
            // Build response structure matching the stored procedure format
            response.put("success", true);
            response.put("found", true);
            response.put("mainData", List.of(visitData));
            response.put("additionalData", List.of(visitData)); // Same data for now
            response.put("patientId", patientId);
            response.put("clinicId", clinicId);
            response.put("visitNo", patientVisitNo);
            response.put("languageId", languageId);
            
            logger.info("Successfully retrieved appointment details for patient: {}", patientId);
            
        } catch (Exception e) {
            logger.error("Error getting appointment details for patient {}: {}", patientId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get appointment details: " + e.getMessage());
        }
        
        return response;
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
            logger.info("Building master-lists for patient: {}, visitNo: {}, date: {}", patientId, patientVisitNo, visitDate);

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
                       pv.follow_up, pv.follow_up_type, pv.follow_up_date
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
                """;
                billing = jdbcTemplate.queryForList(
                    billingSql, patientId, clinicId, doctorId, patientVisitNo);
            }

            // 9) UI field mapping from vitals and receipt
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

                // Optional: receipt details (date/amount) from receipts table
                if (v.get("receipt_number") != null) {
                    try {
                        Map<String, Object> receipt = jdbcTemplate.queryForMap(
                            "SELECT receipt_date, receipt_amount FROM patient_receipts WHERE receipt_number = ?",
                            v.get("receipt_number")
                        );
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
     * Get patient previous visits with comprehensive details (replicating USP_Get_Patient_Previous_Visits logic)
     * Now includes prescriptions, complaints, diagnosis, and doctor information
     */
    public Map<String, Object> getPatientPreviousVisits(String patientId, String doctorId, String clinicId, LocalDate todaysVisitDate) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Getting previous visits for patient: {}, doctor: {}, clinic: {}, today: {}", 
                patientId, doctorId, clinicId, todaysVisitDate);
            
            // Use the comprehensive query that includes all related data
            List<Map<String, Object>> visitResults = patientVisitRepository
                .findPatientPreviousVisitsWithDetails(patientId, todaysVisitDate);
            
            logger.info("DEBUG: Found {} visits with comprehensive data for patient {}", visitResults.size(), patientId);
            
            if (!visitResults.isEmpty()) {
                List<Map<String, Object>> visitList = new ArrayList<>();
                
                // Process each visit result
                for (Map<String, Object> visitData : visitResults) {
                    Map<String, Object> formattedVisit = formatComprehensiveVisitData(visitData);
                    visitList.add(formattedVisit);
                    
                    logger.info("DEBUG: Processed visit - Date: {}, Doctor: {}, Medicine: {}, Complaints: {}, Diagnosis: {}", 
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
                    "includesLabTestDescriptions", true
                ));
                
                logger.info("Found {} previous visits with comprehensive data for patient {}", visitList.size(), patientId);
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
                    "includesLabTestDescriptions", true
                ));
                
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
                logger.warn("Missing required fields for prescription lookup: patientId={}, visitDate={}, visitNo={}, doctorId={}, clinicId={}", 
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
            List<Map<String, Object>> formattedPrescriptions = new ArrayList<>();
            for (Map<String, Object> prescription : prescriptions) {
                Map<String, Object> formattedPrescription = new HashMap<>();
                
                // Basic medicine information
                formattedPrescription.put("medicineName", prescription.get("medicine_name"));
                formattedPrescription.put("brandName", prescription.get("brand_name"));
                formattedPrescription.put("categoryDescription", prescription.get("catsub_description"));
                formattedPrescription.put("categoryShortName", prescription.get("cat_short_name"));
                formattedPrescription.put("marketedBy", prescription.get("marketed_by"));
                
                // Dosage information
                formattedPrescription.put("morningDose", prescription.get("morning"));
                formattedPrescription.put("afternoonDose", prescription.get("afternoon"));
                formattedPrescription.put("nightDose", prescription.get("night"));
                formattedPrescription.put("noOfDays", prescription.get("no_of_days"));
                
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
                if (prescription.get("morning") != null) {
                    doseSummary.append("M:").append(prescription.get("morning"));
                }
                if (prescription.get("afternoon") != null) {
                    if (doseSummary.length() > 0) doseSummary.append(", ");
                    doseSummary.append("A:").append(prescription.get("afternoon"));
                }
                if (prescription.get("night") != null) {
                    if (doseSummary.length() > 0) doseSummary.append(", ");
                    doseSummary.append("N:").append(prescription.get("night"));
                }
                if (prescription.get("no_of_days") != null) {
                    if (doseSummary.length() > 0) doseSummary.append(" ");
                    doseSummary.append("for ").append(prescription.get("no_of_days")).append(" days");
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
                formattedDate = timestamp.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
                formattedTime = timestamp.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
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
        visitMap.put("Medicine_Name", visitData.get("medicine_names") != null ? visitData.get("medicine_names").toString() : "");
        visitMap.put("Instructions", visitData.get("instructions") != null ? visitData.get("instructions").toString() : "");
        
        // Fetch detailed prescription data as nested object
        List<Map<String, Object>> detailedPrescriptions = getDetailedPrescriptionsForVisit(visitData);
        visitMap.put("Prescriptions", detailedPrescriptions);
        visitMap.put("Weight_IN_KGS", visitData.get("weight_in_kgs") != null ? visitData.get("weight_in_kgs") : 0);
        visitMap.put("Visit_Comments", visitData.get("visit_comments") != null ? visitData.get("visit_comments").toString() : "");
        visitMap.put("Observation", visitData.get("observation") != null ? visitData.get("observation").toString() : "");
        
        // Visit type and additional info with actual data
        visitMap.put("Visit_Type", "Patient_Visit");
        visitMap.put("Complaints", visitData.get("complaints") != null ? visitData.get("complaints").toString() : "");
        visitMap.put("complaint_comments", visitData.get("complaint_comments") != null ? visitData.get("complaint_comments").toString() : "");
        visitMap.put("Diagnosis", visitData.get("diagnosis") != null ? visitData.get("diagnosis").toString() : "");
        visitMap.put("FollowUp_Description", visitData.get("followup_description") != null ? visitData.get("followup_description").toString() : "");
        visitMap.put("Lab_Test_Descriptions", visitData.get("lab_test_descriptions") != null ? visitData.get("lab_test_descriptions").toString() : "");
        
        // Financial information
        visitMap.put("Fees_To_Collect", visitData.get("fees_to_collect") != null ? visitData.get("fees_to_collect") : 0);
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
        visitMap.put("Current_Complaints", visitData.get("current_complaints"));
        visitMap.put("Current_Medicines", visitData.get("current_medicines"));
        visitMap.put("Important_Findings", visitData.get("important_findings"));
        visitMap.put("Additional_Comments", visitData.get("additional_comments"));
        visitMap.put("Systemic", visitData.get("systemic"));
        visitMap.put("Odeama", visitData.get("odeama"));
        visitMap.put("Pallor", visitData.get("pallor"));
        visitMap.put("GC", visitData.get("gc"));
        
        // Follow-up information
        visitMap.put("Follow_Up", visitData.get("follow_up"));
        visitMap.put("Follow_Up_Flag", visitData.get("is_follow_up"));
        visitMap.put("Follow_Up_Comment", visitData.get("follow_up_comment"));
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
        visitMap.put("Treatment_Plan", visitData.get("treatment_plan") != null ? visitData.get("treatment_plan").toString() : "");
        visitMap.put("Treatment_Comment", visitData.get("treatment_comment") != null ? visitData.get("treatment_comment").toString() : "");
        
        // Audit fields
        visitMap.put("Created_On", visitData.get("created_on"));
        visitMap.put("Created_By", visitData.get("createdby_name"));
        visitMap.put("Modified_On", visitData.get("modified_on"));
        visitMap.put("Modified_By", visitData.get("modifiedby_name"));
        
        return visitMap;
    }
    
    /**
     * Map PatientVisit entity to comprehensive response (matching stored procedure output)
     */
    private Map<String, Object> mapVisitToComprehensiveResponse(PatientVisit visit) {
        Map<String, Object> visitMap = new HashMap<>();
        
        // Format visit date similar to stored procedure: "DD-MMM-YYYY"
        String formattedDate = visit.getVisitDate().format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        
        // Format visit time
        String formattedTime = visit.getVisitTime() != null ? 
            visit.getVisitTime().toString() : "00:00:00";
        
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
        visitMap.put("Complaints", visit.getCurrentComplaints() != null ? visit.getCurrentComplaints() : ""); // Include current_complaints from patient_visits table
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
        // TFT field not available in entity
        
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
        visitMap.put("inPerson", visit.getInPerson());
        visitMap.put("symptomComment", visit.getSymptomComment());
        visitMap.put("impression", visit.getImpression());
        visitMap.put("attendedBy", visit.getAttendedBy());
        visitMap.put("paymentById", visit.getPaymentById());
        visitMap.put("paymentRemark", visit.getPaymentRemark());
        visitMap.put("attendedById", visit.getAttendedById());
        visitMap.put("followUp", visit.getFollowUp());
        visitMap.put("followUpFlag", visit.getIsFollowUp());
        // Fetch complaints from visit_complaints table (matching stored procedure logic)
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
        visitMap.put("referralAddress", visit.getReferDoctorDetails());
        visitMap.put("referralContact", visit.getReferDoctorDetails());
        visitMap.put("referralEmail", visit.getReferDoctorDetails());
        
        // Plan and treatment information
        visitMap.put("plan", visit.getPlan());
        visitMap.put("notes", visit.getNotes());
        visitMap.put("treatmentPlan", visit.getTreatmentPlan());
        visitMap.put("treatmentComment", visit.getTreatmentComment());
        
        // Instructions
        visitMap.put("instructions", visit.getInstructions());
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
                visit.getClinicId()
            );
            
            if (complaints.isEmpty()) {
                logger.info("No complaints found in visit_complaints table for visit: patientId={}, visitNo={}, doctorId={}, clinicId={}", 
                    visit.getPatientId(), visit.getPatientVisitNo(), visit.getDoctorId(), visit.getClinicId());
                return "";
            }
            
            // Join complaint descriptions and comments with comma and space (matching stored procedure format)
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
                
            logger.info("Found {} complaints in visit_complaints table for visit: patientId={}, visitNo={}, result='{}'", 
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
        visit.setPaymentById(req.paymentById() != null ? req.paymentById().shortValue() : null);
        visit.setPaymentRemark(req.paymentRemark());
        visit.setAttendedById(req.attendedById());
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
        Boolean offlineFlag
    ) {}

    private void persistDiagnosisAndMedicinesIfProvided(ComprehensiveVisitRequest req, PatientVisit savedVisit) {
        // Expect optional arrays provided through controller layer via request context map
        // For simplicity, read them from a ThreadLocal or expand signature later. Here we attempt to fetch
        // from a well-known key map attached to the visit entity is not available, so no-op.
    }
    
}

