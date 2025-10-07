package com.climasys.visits.service;

import com.climasys.entity.PatientVisit;
import com.climasys.repository.PatientVisitRepository;
import com.climasys.repository.DoctorClinicShiftRepository;
import com.climasys.repository.StatusRefRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    /**
     * Save or update a comprehensive patient visit using JPA
     */
    @Transactional
    public Map<String, Object> saveComprehensiveVisit(ComprehensiveVisitRequest request) {
        logger.info("Saving comprehensive visit for patient: {} using JPA", request.patientId());
        
        try {
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
     * Get the last visit details for a patient
     */
    public Map<String, Object> getLastVisitDetails(String patientId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Getting last visit details for patient: {}", patientId);
            
            Optional<PatientVisit> lastVisit = patientVisitRepository
                .findFirstByPatientIdAndDeleteFlagOrderByVisitDateDesc(patientId, false);
            
            if (lastVisit.isPresent()) {
                PatientVisit visit = lastVisit.get();
                
                response.put("success", true);
                response.put("found", true);
                response.put("visit", mapVisitToResponse(visit));
                
                logger.info("Found last visit for patient {}: {}", patientId, visit.getVisitDate());
            } else {
                response.put("success", true);
                response.put("found", false);
                response.put("message", "No visits found for patient");
                
                logger.info("No visits found for patient: {}", patientId);
            }
            
        } catch (Exception e) {
            logger.error("Error getting last visit details for patient {}: {}", patientId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get last visit details: " + e.getMessage());
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
                    visitList.add(mapVisitToResponse(visit));
                }
                
                response.put("success", true);
                response.put("found", true);
                response.put("totalVisits", allVisits.size());
                response.put("visits", visitList);
                
                logger.info("Found {} visits for patient {}", allVisits.size(), patientId);
            } else {
                response.put("success", true);
                response.put("found", false);
                response.put("totalVisits", 0);
                response.put("message", "No visits found for patient");
                
                logger.info("No visits found for patient: {}", patientId);
            }
            
        } catch (Exception e) {
            logger.error("Error getting all visits for patient {}: {}", patientId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get all visits: " + e.getMessage());
        }
        
        return response;
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
                    "includesDoctorInfo", true
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
                    "includesDoctorInfo", true
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
        visitMap.put("Weight_IN_KGS", visitData.get("weight_in_kgs") != null ? visitData.get("weight_in_kgs") : 0);
        visitMap.put("Visit_Comments", visitData.get("visit_comments") != null ? visitData.get("visit_comments").toString() : "");
        visitMap.put("Observation", visitData.get("observation") != null ? visitData.get("observation").toString() : "");
        
        // Visit type and additional info with actual data
        visitMap.put("Visit_Type", "Patient_Visit");
        visitMap.put("Complaints", visitData.get("complaints") != null ? visitData.get("complaints").toString() : "");
        visitMap.put("Diagnosis", visitData.get("diagnosis") != null ? visitData.get("diagnosis").toString() : "");
        visitMap.put("FollowUp_Description", visitData.get("followup_description") != null ? visitData.get("followup_description").toString() : "");
        
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
        visitMap.put("Complaints", ""); // Would need to join with complaints table
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
        visitMap.put("currentComplaint", visit.getCurrentComplaints());
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
        
        // Audit fields
        visitMap.put("createdOn", visit.getCreatedOn());
        visitMap.put("createdBy", visit.getCreatedbyName());
        visitMap.put("modifiedOn", visit.getModifiedOn());
        visitMap.put("modifiedBy", visit.getModifiedbyName());
        
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
}

