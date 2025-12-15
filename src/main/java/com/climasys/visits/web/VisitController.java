package com.climasys.visits.web;

import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.*;

import com.climasys.utils.TimezoneUtils;
import com.climasys.utils.CorsUtils;
import com.climasys.visits.service.VisitJpaService;
import com.climasys.repository.VisitGroupsInstructionsRepository;
import com.climasys.entity.VisitGroupsInstructions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private static final Logger logger = LoggerFactory.getLogger(VisitController.class);
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    private TimezoneUtils timezoneUtils;
    
    @Autowired
    private CorsUtils corsUtils;
    
    @Autowired
    private VisitJpaService visitJpaService;
    
    @Autowired
    private VisitGroupsInstructionsRepository visitGroupsInstructionsRepository;

    public VisitController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Helper method to parse date strings with flexible format handling
     */
    private LocalDateTime parseDateTime(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            if (dateString.contains("T")) {
                // ISO format: 2025-10-06T11:30:00
                return LocalDateTime.parse(dateString);
            } else if (dateString.contains(" ")) {
                // Format: 2025-10-06 11:30:00
                return LocalDateTime.parse(dateString, 
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } else {
                // Date only: 2025-10-06 - assume start of day
                return LocalDate.parse(dateString).atStartOfDay();
            }
        } catch (Exception e) {
            // Fallback: try to parse as LocalDate and convert to LocalDateTime
            try {
                return LocalDate.parse(dateString).atStartOfDay();
            } catch (Exception e2) {
                throw new IllegalArgumentException("Invalid date format: " + dateString + 
                    ". Expected formats: yyyy-MM-dd, yyyy-MM-dd HH:mm:ss, or yyyy-MM-ddTHH:mm:ss", e2);
            }
        }
    }

    private Integer toIntSafe(Object val) {
        try {
            if (val == null) return 0;
            if (val instanceof Number n) return n.intValue();
            String s = val.toString().trim();
            if (s.isEmpty()) return 0;
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private Double parseDoubleSafe(String val) {
        try {
            if (val == null || val.trim().isEmpty()) return 0.0;
            return Double.parseDouble(val.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * Helper method to extract string value from Map with multiple possible key names
     */
    private String getStringValue(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                return value.toString().trim();
            }
        }
        return null;
    }
    
    /**
     * Helper method to extract integer value from Map with multiple possible key names
     */
    private Integer getIntegerValue(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                }
                try {
                    return Integer.parseInt(value.toString().trim());
                } catch (NumberFormatException e) {
                    // Try next key
                }
            }
        }
        return null;
    }
    
    
    // Convert timezone in Java for time fields
    private void convertTimezoneInRow(Map<String, Object> row) {
        try {
            // Convert Visit_Time
            Object rawVisitTime = row.get("Raw_Visit_Time");
            System.out.println("DEBUG - Raw_Visit_Time: " + rawVisitTime + " (type: " + (rawVisitTime != null ? rawVisitTime.getClass().getSimpleName() : "null") + ")");
            
            if (rawVisitTime != null) {
                String timeStr = rawVisitTime.toString();
                System.out.println("DEBUG - Processing visit time string: " + timeStr);
                
                // Parse the time string (format: HH:MM:SS or HH:MM)
                if (timeStr.matches("\\d{2}:\\d{2}(:\\d{2})?")) {
                    String[] parts = timeStr.split(":");
                    int hours = Integer.parseInt(parts[0]);
                    int minutes = Integer.parseInt(parts[1]);
                    
                    System.out.println("DEBUG - Parsed visit time: " + hours + ":" + minutes);
                    
                    // Convert from UTC to target timezone using TimezoneUtils
                    try {
                        java.time.LocalTime utcTime = java.time.LocalTime.of(hours, minutes);
                        java.time.LocalTime targetTime = timezoneUtils.convertUtcToTargetTimezone(utcTime);
                        
                        String convertedTime = String.format("%02d:%02d", targetTime.getHour(), targetTime.getMinute());
                        System.out.println("DEBUG - Converted UTC to " + timezoneUtils.getTimezoneDisplayName() + ": " + timeStr + " -> " + convertedTime);
                        
                        row.put("Visit_Time", convertedTime);
                        System.out.println("DEBUG - Set Visit_Time field to: " + convertedTime);
                        
                    } catch (Exception timezoneException) {
                        System.out.println("ERROR - Timezone conversion failed, using original time: " + timezoneException.getMessage());
                        // Fallback to original time if timezone conversion fails
                        String fallbackTime = String.format("%02d:%02d", hours, minutes);
                        row.put("Visit_Time", fallbackTime);
                    }
                } else {
                    System.out.println("DEBUG - Visit time format doesn't match expected pattern: " + timeStr);
                }
            }
            
            // Convert Online_Appointment_Time
            Object onlineTime = row.get("Online_Appointment_Time");
            System.out.println("DEBUG - Online_Appointment_Time: " + onlineTime + " (type: " + (onlineTime != null ? onlineTime.getClass().getSimpleName() : "null") + ")");
            
            if (onlineTime != null) {
                String onlineTimeStr = onlineTime.toString();
                System.out.println("DEBUG - Processing online time string: " + onlineTimeStr);
                
                // Parse the time string (format: HH:MM:SS or HH:MM)
                if (onlineTimeStr.matches("\\d{2}:\\d{2}(:\\d{2})?")) {
                    String[] parts = onlineTimeStr.split(":");
                    int hours = Integer.parseInt(parts[0]);
                    int minutes = Integer.parseInt(parts[1]);
                    
                    System.out.println("DEBUG - Parsed online time: " + hours + ":" + minutes);
                    
                    // Convert from UTC to target timezone using TimezoneUtils
                    try {
                        java.time.LocalTime utcTime = java.time.LocalTime.of(hours, minutes);
                        java.time.LocalTime targetTime = timezoneUtils.convertUtcToTargetTimezone(utcTime);
                        
                        String convertedOnlineTime = String.format("%02d:%02d", targetTime.getHour(), targetTime.getMinute());
                        System.out.println("DEBUG - Converted online time UTC to " + timezoneUtils.getTimezoneDisplayName() + ": " + onlineTimeStr + " -> " + convertedOnlineTime);
                        
                        row.put("Online_Appointment_Time", convertedOnlineTime);
                        System.out.println("DEBUG - Set Online_Appointment_Time field to: " + convertedOnlineTime);
                        
                    } catch (Exception timezoneException) {
                        System.out.println("ERROR - Online time timezone conversion failed, using original time: " + timezoneException.getMessage());
                        // Fallback to original time if timezone conversion fails
                        String fallbackTime = String.format("%02d:%02d", hours, minutes);
                        row.put("Online_Appointment_Time", fallbackTime);
                    }
                } else {
                    System.out.println("DEBUG - Online time format doesn't match expected pattern: " + onlineTimeStr);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR - Timezone conversion failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public record AddToVisitRequest(
            @NotBlank String patientId,
            @NotBlank String doctorId,
            @NotBlank String clinicId,
            @NotBlank String visitDate,
            @NotBlank String shiftId,
            String visitTime,
            Boolean reportsAsked,
            Boolean reportsReceived,
            String userId
    ) {}

    public record ComprehensiveVisitDataRequest(
            // Composite Key Fields
            @NotBlank String patientId,
            @NotBlank String doctorId,
            @NotBlank String clinicId,
            @NotBlank String shiftId,
            @NotBlank String visitDate,
            @NotBlank String patientVisitNo,
            
            // Referral Information
            String referBy,
            String referralName,
            String referralContact,
            String referralEmail,
            String referralAddress,
            
            // Patient Vitals
            Integer pulse,
            BigDecimal heightInCms,
            BigDecimal weightInKgs,
            String bloodPressure,
            String sugar,
            String tft,
            
            // Medical History
            String pastSurgicalHistory,
            String previousVisitPlan,
            String chiefComplaint,
            String visitComments,
            String currentMedicines,
            
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
            String reason,
            String impression,
            String attendedBy,
            Integer paymentById,
            String paymentRemark,
            Integer attendedById,
            String followUp,
            Boolean followUpFlag,
            String currentComplaint,
            String visitCommentsField,
            
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
            String followUpType,
            
            // Financial Fields
            BigDecimal feesToCollect,
            BigDecimal feesPaid,
            BigDecimal discount,
            BigDecimal originalDiscount,
            
            // Status and User
            Integer statusId,
            String userId,
            Boolean isSubmitPatientVisitDetails,
            
            // Instructions
            String instructions,
            String additionalInstructions,
            
            // Instruction Groups (structured instruction data)
            // Each item should have: groupDescription, instructionsDescription, sequenceNo
            java.util.List<Map<String, Object>> instructionGroups,
            
            // Treatment arrays (diagnosis, medicines, prescriptions, investigations, complaints)
            java.util.List<Map<String, Object>> complaintsRows,
            
            // Optional treatment arrays
            java.util.List<Map<String, Object>> diagnosisRows,
            java.util.List<Map<String, Object>> medicineRows,
            java.util.List<Map<String, Object>> prescriptionRows,
            java.util.List<Map<String, Object>> investigationRows,
            
            // Dressing (body parts) - textbox field
            String dressingBodyParts
    ) {}

    @PostMapping
    public ResponseEntity<?> addToVisit(@RequestBody AddToVisitRequest req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_Insert_PatientToVisitQueue");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("p_date_Visit_Date", parseDateTime(req.visitDate()));
            
            // Parse shift ID with null check
            Short shiftId = null;
            if (req.shiftId() != null && !req.shiftId().trim().isEmpty()) {
                shiftId = Short.parseShort(req.shiftId());
            }
            parameters.put("p_int_Shift_ID", shiftId);
            parameters.put("p_nvar_Clinic_ID", req.clinicId());
            parameters.put("p_nvar_Doctor_ID", req.doctorId());
            parameters.put("p_nvar_Patient_ID", req.patientId());
            parameters.put("p_time_Visit_Time", req.visitTime() != null ? java.sql.Time.valueOf(req.visitTime()) : null);
            parameters.put("p_bit_ReportAsked", req.reportsAsked());
            parameters.put("p_bit_ReportReceived", req.reportsReceived());
            parameters.put("p_var_User_Id", req.userId());

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to add patient to visit list: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodaysVisits(
            @RequestParam String doctorId,
            @RequestParam String shiftId,
            @RequestParam String clinicId,
            @RequestParam String roleId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetTodaysVisitDetails");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("DoctorId", doctorId);
            parameters.put("ShiftId", shiftId);
            parameters.put("ClinicId", clinicId);
            parameters.put("RoleId", roleId);

            Map<String, Object> result = jdbcCall.execute(parameters);

            // Augment with a direct query that includes status 9 (saved) for doctor views
            try {
                String itemsQuery = "SELECT " +
                        "PV.patient_id AS patientId, " +
                        "PM.first_name || ' ' || PM.last_name AS patientName, " +
                        "PV.doctor_id AS doctorId, " +
                        "DM.prefix || ' ' || DM.first_name AS doctorName, " +
                        "PV.clinic_id AS clinicId, " +
                        "TO_CHAR(PV.visit_date, 'YYYY-MM-DD') AS visitDate, " +
                        "PV.shift_id AS shiftId, " +
                        "PV.status_id AS status, " +
                        "PV.patient_visit_no AS visitId " +
                        "FROM patient_visits PV " +
                        "INNER JOIN patient_master PM ON PV.patient_id = PM.id " +
                        "INNER JOIN doctor_master DM ON PV.doctor_id = DM.doctor_id " +
                        "WHERE PV.delete_flag = false " +
                        "AND PV.doctor_id = ? " +
                        "AND PV.clinic_id = ? " +
                        "AND PV.shift_id = CAST(? AS SMALLINT) " +
                        "AND PV.visit_date::date = CURRENT_DATE " +
                        "AND PV.status_id NOT IN (4, 12) " +
                        "ORDER BY PV.visit_time ASC";

                List<Map<String, Object>> items = jdbcTemplate.queryForList(itemsQuery, doctorId, clinicId, shiftId);
                result.put("items", items);
            } catch (Exception augmentEx) {
                logger.warn("Failed to augment today's visits with direct query: {}", augmentEx.getMessage());
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get today's visits: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * JPA replacement for USP_Get_MasterLists (subset required by UI)
     */
    @GetMapping("/master-lists")
    public ResponseEntity<?> getMasterLists(
            @RequestParam String patientId,
            @RequestParam Short shiftId,
            @RequestParam String clinicId,
            @RequestParam String doctorId,
            @RequestParam String visitDate, // YYYY-MM-DD
            @RequestParam Integer patientVisitNo) {
        try {
            LocalDate date = LocalDate.parse(visitDate);
            Map<String, Object> result = visitJpaService.getMasterLists(
                patientId, shiftId, clinicId, doctorId, date, patientVisitNo);
            if (result.get("success") != null && (Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get master lists: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete visit using composite key parameters
     */
    @DeleteMapping
    public ResponseEntity<?> deleteVisit(
            @RequestParam String patientId,
            @RequestParam String doctorId,
            @RequestParam String clinicId,
            @RequestParam String shiftId,
            @RequestParam String visitDate,
            @RequestParam String patientVisitNo) {
        try {
            // Use the existing appointment deletion logic
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_Delete_PatientAppointment");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("PatientId", patientId);
            parameters.put("DoctorId", doctorId);
            parameters.put("VisitDate", visitDate);
            parameters.put("UserId", "system");

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to delete visit: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Comprehensive API to save all patient visit data matching the form fields from screenshot
     * This API handles all the fields shown in the patient visit details form
     */
    @PostMapping("/comprehensive-save")
    public ResponseEntity<?> saveComprehensiveVisitData(@RequestBody ComprehensiveVisitDataRequest req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_Insert_PatientVisitData");

            Map<String, Object> parameters = new HashMap<>();
            
            // Composite Key Fields
            parameters.put("p_var_Patient_ID", req.patientId());
            parameters.put("p_var_Doctor_ID", req.doctorId());
            parameters.put("p_var_Clinic_ID", req.clinicId());
            // Parse shift ID with null check
            Short shiftId = null;
            if (req.shiftId() != null && !req.shiftId().trim().isEmpty()) {
                shiftId = Short.parseShort(req.shiftId());
            }
            parameters.put("p_var_Shift_ID", shiftId);
            
            parameters.put("p_var_Visit_Date", parseDateTime(req.visitDate()));
            
            // Parse patient visit number with null check
            Integer patientVisitNo = null;
            if (req.patientVisitNo() != null && !req.patientVisitNo().trim().isEmpty()) {
                patientVisitNo = Integer.parseInt(req.patientVisitNo());
            }
            parameters.put("p_var_Patient_Visit_No", patientVisitNo);
            
            // Patient Vitals (from form screenshot)
            parameters.put("p_var_Pulse", req.pulse());
            parameters.put("p_var_Height_In_CMS", req.heightInCms());
            parameters.put("p_var_Weight_IN_KGS", req.weightInKgs());
            parameters.put("p_var_Blood_Pressure", req.bloodPressure());
            parameters.put("p_var_sugar", req.sugar());
            parameters.put("p_var_THtext", req.tft());
            
            // Medical History (from form screenshot)
            parameters.put("p_var_SurgicalHistory", req.pastSurgicalHistory());
            parameters.put("p_var_previous_visit_plan", req.previousVisitPlan());
            parameters.put("p_var_current_complaint", req.chiefComplaint());
            parameters.put("p_var_visit_comments", req.visitComments());
            parameters.put("p_var_current_medicines", req.currentMedicines());
            
            // Medical Conditions
            parameters.put("p_var_Hypertension", req.hypertension());
            parameters.put("p_var_Diabetes", req.diabetes());
            parameters.put("p_var_Cholestrol", req.cholestrol());
            parameters.put("p_var_IHD", req.ihd());
            parameters.put("p_var_TH", req.th());
            parameters.put("p_var_Asthama", req.asthama());
            parameters.put("p_var_Smoking", req.smoking());
            parameters.put("p_var_Tobaco", req.tobaco());
            parameters.put("p_var_Alchohol", req.alchohol());
            
            // Additional Fields
            parameters.put("p_var_Habit_Details", req.habitDetails());
            parameters.put("p_var_Allergy_Details", req.allergyDetails());
            parameters.put("p_var_Observation", req.observation());
            parameters.put("p_bit_In_Person", req.inPerson());
            parameters.put("p_var_Symptom_Comment", req.symptomComment());
            parameters.put("p_var_Reason", req.reason());
            parameters.put("p_var_Impression", req.impression());
            parameters.put("p_var_Attended_By", req.attendedBy());
            parameters.put("p_var_PaymentBy_ID", req.paymentById());
            parameters.put("p_var_Payment_Remark", req.paymentRemark());
            parameters.put("p_var_AttendedBy_ID", req.attendedById());
            parameters.put("p_var_Follow_Up", req.followUp());
            parameters.put("p_bit_follow_up", req.followUpFlag());
            parameters.put("p_var_current_complaint", req.currentComplaint());
            parameters.put("p_var_visit_comments", req.visitCommentsField());
            
            // Clinical Fields
            parameters.put("p_var_TPR", req.tpr());
            parameters.put("p_var_Important_Findings", req.importantFindings());
            parameters.put("p_var_Additional_Comments", req.additionalComments());
            parameters.put("p_var_Systemic", req.systemic());
            parameters.put("p_var_Odeama", req.odeama());
            parameters.put("p_var_Pallor", req.pallor());
            parameters.put("p_var_GC", req.gc());
            
            // Gynecological Fields
            parameters.put("p_var_FMP", req.fmp());
            parameters.put("p_var_PRMC", req.prmc());
            parameters.put("p_var_PAMC", req.pamc());
            parameters.put("p_var_LMP", req.lmp());
            parameters.put("p_var_ObstetricHistory", req.obstetricHistory());
            parameters.put("p_var_SurgicalHistory", req.surgicalHistory());
            parameters.put("p_var_Menstrual_Add_Comments", req.menstrualAddComments());
            parameters.put("p_var_FollowUp_comment", req.followUpComment());
            parameters.put("p_var_FollowUp_Date", req.followUpDate());
            parameters.put("p_var_Pregnant", req.pregnant());
            parameters.put("p_var_EDD", req.edd());
            parameters.put("p_var_Follow_up_Type", req.followUpType());
            
            // Financial Fields
            parameters.put("p_var_Fees_To_Collect", req.feesToCollect());
            parameters.put("p_var_Fees_Paid", req.feesPaid());
            parameters.put("p_var_Discount", req.discount());
            parameters.put("p_var_Original_Discount", req.originalDiscount());
            
            // Status and User
            parameters.put("p_var_Status_ID", req.statusId());
            parameters.put("p_var_User_Id", req.userId());
            parameters.put("Is_Submit_Patient_Visit_Details", req.isSubmitPatientVisitDetails());
            
            // Empty UDT parameters (these would need to be populated with actual data)
            parameters.put("p_var_Insert_PatientComplaintData", null);
            parameters.put("p_var_Insert_PatientDiagnosisData", null);
            parameters.put("p_var_Insert_PatientDressingData", null);
            parameters.put("p_var_Insert_PatientMedicineData", null);
            parameters.put("p_var_Insert_PatientPrescriptionData", null);
            parameters.put("p_var_Insert_PatientProcedureData", null);
            parameters.put("p_var_Insert_PatientInstructionData", null);
            parameters.put("p_var_Insert_AbdominalData", null);
            
            // Instructions field - use value from request or default to empty string
            parameters.put("p_var_Instructions", req.instructions() != null ? req.instructions() : "");
            
            // Additional required fields with defaults
            parameters.put("p_var_offline_reason", "");
            parameters.put("p_bit_offlineflag", false);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to save comprehensive visit data: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Comprehensive API to save all patient visit data using JPA (new implementation)
     * This endpoint uses JPA instead of stored procedures for better maintainability
     */
    @PostMapping("/comprehensive-save-jpa")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> saveComprehensiveVisitDataJpa(@RequestBody ComprehensiveVisitDataRequest req) {
        try {
            logger.info("Received comprehensive-save-jpa request - PatientId: {}, DoctorId: {}, ClinicId: {}, ShiftId: {}, VisitNo: {}, StatusId: {}", 
                req.patientId(), req.doctorId(), req.clinicId(), req.shiftId(), req.patientVisitNo(), req.statusId());
            
            // Log instruction groups immediately when request arrives
            logger.info("🔍 INSTRUCTION GROUPS IN REQUEST - isNull: {}, isEmpty: {}, size: {}", 
                req.instructionGroups() == null, 
                req.instructionGroups() == null ? "N/A" : req.instructionGroups().isEmpty(),
                req.instructionGroups() == null ? "N/A" : req.instructionGroups().size());
            if (req.instructionGroups() != null && !req.instructionGroups().isEmpty()) {
                logger.info("📋 INSTRUCTION GROUPS DATA RECEIVED: {}", req.instructionGroups());
            }
            
            // Validate and parse required fields
            if (req.shiftId() == null || req.shiftId().trim().isEmpty()) {
                logger.warn("Shift ID validation failed - received: {}", req.shiftId());
                throw new IllegalArgumentException("Shift ID is required");
            }
            Short shiftId;
            try {
                shiftId = Short.parseShort(req.shiftId());
            } catch (NumberFormatException e) {
                logger.warn("Shift ID parse error - received: {}", req.shiftId());
                throw new IllegalArgumentException("Shift ID must be a valid number: " + req.shiftId());
            }
            
            if (req.patientVisitNo() == null || req.patientVisitNo().trim().isEmpty()) {
                logger.warn("Patient Visit No validation failed - received: {}", req.patientVisitNo());
                throw new IllegalArgumentException("Patient Visit Number is required");
            }
            Integer patientVisitNo;
            try {
                patientVisitNo = Integer.parseInt(req.patientVisitNo());
            } catch (NumberFormatException e) {
                logger.warn("Patient Visit No parse error - received: {}", req.patientVisitNo());
                throw new IllegalArgumentException("Patient Visit Number must be a valid number: " + req.patientVisitNo());
            }
            
            if (req.visitDate() == null || req.visitDate().trim().isEmpty()) {
                throw new IllegalArgumentException("Visit Date is required");
            }
            // Parse the visit date and convert to UTC (database stores in UTC)
            LocalDateTime parsedVisitDate = parseDateTime(req.visitDate());
            LocalDateTime visitDate = timezoneUtils.convertTargetTimezoneToUtc(parsedVisitDate);
            logger.info("Visit date conversion - Original: {} (target timezone), Converted to UTC: {}", 
                parsedVisitDate, visitDate);
            
            // Validate other required fields
            if (req.patientId() == null || req.patientId().trim().isEmpty()) {
                throw new IllegalArgumentException("Patient ID is required");
            }
            if (req.doctorId() == null || req.doctorId().trim().isEmpty()) {
                throw new IllegalArgumentException("Doctor ID is required");
            }
            if (req.clinicId() == null || req.clinicId().trim().isEmpty()) {
                throw new IllegalArgumentException("Clinic ID is required");
            }
            if (req.statusId() == null) {
                throw new IllegalArgumentException("Status ID is required");
            }
            if (req.statusId() != null && req.statusId() <= 0) {
                throw new IllegalArgumentException("Status ID must be greater than 0. Valid status IDs are 6 (Submit) or 9 (Save)");
            }
            if (req.userId() == null || req.userId().trim().isEmpty()) {
                throw new IllegalArgumentException("User ID is required");
            }
            if (req.discount() == null) {
                throw new IllegalArgumentException("Discount is required");
            }
            
            // Create service request
            VisitJpaService.ComprehensiveVisitRequest serviceRequest = new VisitJpaService.ComprehensiveVisitRequest(
                    // Composite Key Fields
                    req.patientId(),
                    req.doctorId(),
                    req.clinicId(),
                    shiftId,
                    visitDate,
                    patientVisitNo,
                    
                    // Patient Vitals
                    req.pulse(),
                    req.heightInCms(),
                    req.weightInKgs(),
                    req.bloodPressure(),
                    req.sugar(),
                    req.tft(),
                    
                    // Medical Conditions
                    req.hypertension(),
                    req.diabetes(),
                    req.cholestrol(),
                    req.ihd(),
                    req.th(),
                    req.asthama(),
                    req.smoking(),
                    req.tobaco(),
                    req.alchohol(),
                    
                    // Additional Fields
                    req.habitDetails(),
                    req.allergyDetails(),
                    req.observation(),
                    req.inPerson(),
                    req.symptomComment(),
                    req.impression(),
                    req.attendedBy(),
                    req.paymentById(),
                    req.paymentRemark(),
                    req.attendedById(),
                    req.followUp(),
                    req.followUpFlag(),
                    req.currentComplaint(),
                    req.currentMedicines(),
                    req.visitComments(),
                    
                    // Clinical Fields
                    req.tpr(),
                    req.importantFindings(),
                    req.additionalComments(),
                    req.systemic(),
                    req.odeama(),
                    req.pallor(),
                    req.gc(),
                    
                    // Gynecological Fields
                    req.fmp(),
                    req.prmc(),
                    req.pamc(),
                    req.lmp(),
                    req.obstetricHistory(),
                    req.surgicalHistory(),
                    req.menstrualAddComments(),
                    req.followUpComment(),
                    req.followUpDate(),
                    req.pregnant(),
                    req.edd(),
                    req.followUpType() != null && !req.followUpType().trim().isEmpty() 
                        ? Short.parseShort(req.followUpType()) : null,
                    
                    // Financial Fields
                    req.feesToCollect(),
                    req.discount(),
                    req.originalDiscount(),
                    
                    // Status and User
                    req.statusId() != null ? req.statusId().shortValue() : null,
                    req.userId(),
                    req.isSubmitPatientVisitDetails(),
                    
                    // Treatment fields (not in current request, set to null)
                    null, // treatmentComment
                    null, // treatmentPlan
                    null, // plan
                    null, // notes
                    null, // impressionFinding
                    req.additionalInstructions(),
                    
                    // Referral fields
                    req.referBy(), // referId
                    req.referralName(), // referDoctorName
                    req.referralAddress(),
                    req.referralContact(),
                    req.referralEmail(),
                    
                    // Additional fields - use value from request or default to empty string
                    req.instructions() != null ? req.instructions() : "", // instructions
                    "", // offlineReason
                    false // offlineFlag
                );
            
            // Save using JPA service
            Map<String, Object> result = visitJpaService.saveComprehensiveVisit(serviceRequest);
            logger.info("Comprehensive visit save result: {}", result);
            
            // Declare variables outside try-catch for proper scope
            String patientId = req.patientId();
            String doctorId = req.doctorId();
            String clinicId = req.clinicId();
            String userIdVal = req.userId();
            Short shiftIdVal = shiftId;
            Integer patientVisitNoVal = patientVisitNo;
            
            // Use the actual visitDate from the saved visit to ensure foreign key matches
            // The result map contains the exact visitDate from the saved entity
            LocalDateTime actualVisitDate = visitDate;
            java.sql.Timestamp visitDateTs = null;
            
            try {
                // Get visitDate from the result - this is the exact date that was saved
                Object visitDateObj = result.get("visitDate");
                if (visitDateObj != null) {
                    if (visitDateObj instanceof LocalDateTime) {
                        actualVisitDate = (LocalDateTime) visitDateObj;
                    } else if (visitDateObj instanceof java.sql.Timestamp) {
                        actualVisitDate = ((java.sql.Timestamp) visitDateObj).toLocalDateTime();
                        visitDateTs = (java.sql.Timestamp) visitDateObj;
                    } else if (visitDateObj instanceof String) {
                        actualVisitDate = parseDateTime((String) visitDateObj);
                    }
                    logger.info("Using visit_date from saved result: {} (original parsed: {})", actualVisitDate, visitDate);
                } else {
                    logger.warn("visitDate not found in result, using parsed visitDate: {}", visitDate);
                }
            } catch (Exception e) {
                logger.warn("Error extracting visitDate from result, using parsed visitDate: {}", e.getMessage(), e);
            }
            
            if (visitDateTs == null) {
                visitDateTs = java.sql.Timestamp.valueOf(actualVisitDate);
            }
            
            logger.info("Final visitDate to use for diagnosis/medicine inserts: {} (Timestamp: {})", actualVisitDate, visitDateTs);
            logger.info("Composite key values - patientId: {}, doctorId: {}, clinicId: {}, shiftId: {}, patientVisitNo: {}", 
                patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal);
            
            logger.info("Preparing to persist treatment arrays - complaintsRows: {}, diagnosisRows: {}, medicineRows: {}, prescriptionRows: {}, investigationRows: {}, dressingBodyParts: {}", 
                req.complaintsRows() != null ? req.complaintsRows().size() : 0,
                req.diagnosisRows() != null ? req.diagnosisRows().size() : 0,
                req.medicineRows() != null ? req.medicineRows().size() : 0,
                req.prescriptionRows() != null ? req.prescriptionRows().size() : 0,
                req.investigationRows() != null ? req.investigationRows().size() : 0,
                req.dressingBodyParts() != null && !req.dressingBodyParts().trim().isEmpty() ? "present" : "empty");
            
            if (result.get("success") != null && (Boolean) result.get("success")) {
                logger.info("Visit saved successfully, proceeding with treatment arrays persistence");
                // Persist treatment detail arrays if provided
                try {

                    // Diagnosis
                    if (req.diagnosisRows() != null && !req.diagnosisRows().isEmpty()) {
                        logger.info("Processing {} diagnosis rows for patient: {}, visit: {}", req.diagnosisRows().size(), patientId, patientVisitNoVal);
                        
                        // CRITICAL: Query the EXACT visit_date from patient_visits to ensure foreign key matches
                        // This is essential because the saved visit might have a slightly different timestamp
                        String getExactVisitDateSql = "SELECT visit_date FROM patient_visits WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND delete_flag = false ORDER BY visit_date DESC LIMIT 1";
                        List<java.sql.Timestamp> exactVisitDates = jdbcTemplate.queryForList(getExactVisitDateSql, java.sql.Timestamp.class, 
                            patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal);
                        
                        java.sql.Timestamp exactVisitDateTs = visitDateTs;
                        if (exactVisitDates != null && !exactVisitDates.isEmpty() && exactVisitDates.get(0) != null) {
                            exactVisitDateTs = exactVisitDates.get(0);
                            logger.info("Retrieved EXACT visit_date from patient_visits: {} (was using: {})", exactVisitDateTs, visitDateTs);
                        } else {
                            logger.error("CRITICAL: Could not find saved visit in patient_visits! Cannot insert diagnosis without parent record.");
                            result.put("diagnosisInsertError", "Parent visit not found in patient_visits table");
                            throw new IllegalStateException("Parent visit not found: Cannot insert diagnosis without matching patient_visits record");
                        }
                        
                        String delDiag = "DELETE FROM visit_diagnosis WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND DATE(visit_date) = DATE(?)";
                        int deletedCount = jdbcTemplate.update(delDiag, patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal, exactVisitDateTs);
                        logger.info("Deleted {} existing diagnosis records", deletedCount);

                        LocalDateTime now = timezoneUtils.convertTargetTimezoneToUtc(LocalDateTime.now());
                        String insDiag = "INSERT INTO visit_diagnosis (patient_id, visit_date, patient_visit_no, shift_id, clinic_id, doctor_id, short_description, desease_description, delete_flag, created_on, createdby_name, modified_on, modifiedby_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        int insertedCount = 0;
                        for (Map<String, Object> row : req.diagnosisRows()) {
                            Object shortDescObj = row.get("short_description");
                            Object diagnosisObj = row.get("diagnosis");
                            
                            String shortDesc = (shortDescObj != null) ? shortDescObj.toString().trim() : "";
                            String diagnosis = (diagnosisObj != null) ? diagnosisObj.toString().trim() : "";
                            
                            logger.debug("Processing diagnosis row - shortDesc: '{}', diagnosis: '{}'", shortDesc, diagnosis);
                            
                            // Insert if at least one field has content (after trimming)
                            if (!shortDesc.isEmpty() || !diagnosis.isEmpty()) {
                                try {
                                    logger.info("Attempting INSERT with exact visit_date: {} for diagnosis: {}", exactVisitDateTs, diagnosis);
                                    int rowsAffected = jdbcTemplate.update(insDiag, patientId, exactVisitDateTs, patientVisitNoVal, shiftIdVal, clinicId, doctorId, shortDesc, diagnosis, false, now, userIdVal, now, userIdVal);
                                    if (rowsAffected > 0) {
                                        insertedCount++;
                                        logger.info("✓ Successfully inserted diagnosis row {}: short_description='{}', desease_description='{}', visitDate={}", 
                                            insertedCount, shortDesc, diagnosis, exactVisitDateTs);
                                    } else {
                                        logger.error("✗ INSERT statement returned 0 rows affected for diagnosis: short_description='{}', desease_description='{}'", shortDesc, diagnosis);
                                        result.put("diagnosisInsertWarning", "Some diagnosis rows may not have been inserted");
                                    }
                                } catch (Exception insertEx) {
                                    logger.error("✗ Error inserting diagnosis row: short_description='{}', desease_description='{}', error: {}", 
                                        shortDesc, diagnosis, insertEx.getMessage(), insertEx);
                                    logger.error("✗ Failed INSERT parameters - patientId: {}, visitDate: {}, patientVisitNo: {}, shiftId: {}, clinicId: {}, doctorId: {}", 
                                        patientId, exactVisitDateTs, patientVisitNoVal, shiftIdVal, clinicId, doctorId);
                                    throw insertEx; // Re-throw to be caught by outer catch
                                }
                            } else {
                                logger.warn("Skipping empty diagnosis row: {}", row);
                            }
                        }
                        logger.info("Successfully inserted {} diagnosis records for patient: {}, visit: {}", insertedCount, patientId, patientVisitNoVal);
                        
                        // Verify the insert worked by querying back using the exact visit_date
                        if (insertedCount > 0) {
                            String verifySql = "SELECT COUNT(*) FROM visit_diagnosis WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND visit_date = ? AND delete_flag = false";
                            Integer actualCount = jdbcTemplate.queryForObject(verifySql, Integer.class, patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal, exactVisitDateTs);
                            logger.info("✓ Verification: Found {} diagnosis records in database after insert (expected: {}, visitDate: {})", actualCount, insertedCount, exactVisitDateTs);
                            if (actualCount == null || actualCount == 0) {
                                logger.error("✗ CRITICAL: No diagnosis records found in database after insert attempt! visitDate used: {}", exactVisitDateTs);
                                result.put("diagnosisInsertWarning", "Diagnosis rows were not saved. Check logs for details.");
                                result.put("diagnosisInsertedCount", 0);
                            } else {
                                result.put("diagnosisInsertedCount", actualCount);
                                logger.info("✓ Confirmed: {} diagnosis records are now in visit_diagnosis table", actualCount);
                            }
                        }
                    } else {
                        logger.debug("No diagnosis rows to process (null or empty)");
                    }

                    // Complaints
                    if (req.complaintsRows() != null && !req.complaintsRows().isEmpty()) {
                        logger.info("Processing {} complaints rows for patient: {}, visit: {}", req.complaintsRows().size(), patientId, patientVisitNoVal);
                        
                        // CRITICAL: Query the EXACT visit_date from patient_visits to ensure foreign key matches
                        String getExactVisitDateSql = "SELECT visit_date FROM patient_visits WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND delete_flag = false ORDER BY visit_date DESC LIMIT 1";
                        List<java.sql.Timestamp> exactVisitDates = jdbcTemplate.queryForList(getExactVisitDateSql, java.sql.Timestamp.class, 
                            patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal);
                        
                        java.sql.Timestamp exactVisitDateTs = visitDateTs;
                        if (exactVisitDates != null && !exactVisitDates.isEmpty() && exactVisitDates.get(0) != null) {
                            exactVisitDateTs = exactVisitDates.get(0);
                            logger.info("Retrieved EXACT visit_date from patient_visits for complaints: {} (was using: {})", exactVisitDateTs, visitDateTs);
                        } else {
                            logger.error("CRITICAL: Could not find saved visit in patient_visits! Cannot insert complaints without parent record.");
                            result.put("complaintsInsertError", "Parent visit not found in patient_visits table");
                            throw new IllegalStateException("Parent visit not found: Cannot insert complaints without matching patient_visits record");
                        }
                        
                        // Delete existing complaint records for this visit (using exact visit_date to match foreign key)
                        String delComplaints = "DELETE FROM visit_complaints WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND DATE(visit_date) = DATE(?)";
                        int deletedCount = jdbcTemplate.update(delComplaints, patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal, exactVisitDateTs);
                        logger.info("Deleted {} existing complaint records", deletedCount);

                        LocalDateTime now = timezoneUtils.convertTargetTimezoneToUtc(LocalDateTime.now());
                        String insComplaints = "INSERT INTO visit_complaints (patient_id, visit_date, patient_visit_no, shift_id, clinic_id, doctor_id, short_description, complaint_description, complaint_comment, delete_flag, created_on, createdby_name, modified_on, modifiedby_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        int insertedCount = 0;
                        for (Map<String, Object> row : req.complaintsRows()) {
                            Object shortDescObj = row.get("short_description");
                            Object complaintDescObj = row.get("complaint_description");
                            Object complaintCommentObj = row.get("complaint_comment");
                            
                            String shortDesc = (shortDescObj != null) ? shortDescObj.toString().trim() : "";
                            String complaintDesc = (complaintDescObj != null) ? complaintDescObj.toString().trim() : "";
                            String complaintComment = (complaintCommentObj != null) ? complaintCommentObj.toString().trim() : "";
                            
                            logger.debug("Processing complaint row - shortDesc: '{}', complaintDesc: '{}', complaintComment: '{}'", shortDesc, complaintDesc, complaintComment);
                            
                            // Insert if at least short_description has content (required field)
                            if (!shortDesc.isEmpty()) {
                                try {
                                    logger.info("Attempting INSERT with exact visit_date: {} for complaint: short_description='{}', complaint_description='{}'", exactVisitDateTs, shortDesc, complaintDesc);
                                    int rowsAffected = jdbcTemplate.update(insComplaints, patientId, exactVisitDateTs, patientVisitNoVal, shiftIdVal, clinicId, doctorId, shortDesc, complaintDesc, complaintComment, false, now, userIdVal, now, userIdVal);
                                    if (rowsAffected > 0) {
                                        insertedCount++;
                                        logger.info("✓ Successfully inserted complaint row {}: short_description='{}', complaint_description='{}', visitDate={}", 
                                            insertedCount, shortDesc, complaintDesc, exactVisitDateTs);
                                    } else {
                                        logger.error("✗ INSERT statement returned 0 rows affected for complaint: short_description='{}', complaint_description='{}'", shortDesc, complaintDesc);
                                        result.put("complaintsInsertWarning", "Some complaint rows may not have been inserted");
                                    }
                                } catch (Exception insertEx) {
                                    logger.error("✗ Error inserting complaint row: short_description='{}', complaint_description='{}', error: {}", 
                                        shortDesc, complaintDesc, insertEx.getMessage(), insertEx);
                                    logger.error("✗ Failed INSERT parameters - patientId: {}, visitDate: {}, patientVisitNo: {}, shiftId: {}, clinicId: {}, doctorId: {}", 
                                        patientId, exactVisitDateTs, patientVisitNoVal, shiftIdVal, clinicId, doctorId);
                                    throw insertEx; // Re-throw to be caught by outer catch
                                }
                            } else {
                                logger.warn("Skipping complaint row with empty short_description: {}", row);
                            }
                        }
                        logger.info("Successfully inserted {} complaint records for patient: {}, visit: {}", insertedCount, patientId, patientVisitNoVal);
                        
                        // Verify the insert worked by querying back using the exact visit_date
                        if (insertedCount > 0) {
                            String verifySql = "SELECT COUNT(*) FROM visit_complaints WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND visit_date = ? AND delete_flag = false";
                            Integer actualCount = jdbcTemplate.queryForObject(verifySql, Integer.class, patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal, exactVisitDateTs);
                            logger.info("✓ Verification: Found {} complaint records in database after insert (expected: {}, visitDate: {})", actualCount, insertedCount, exactVisitDateTs);
                            if (actualCount == null || actualCount == 0) {
                                logger.error("✗ CRITICAL: No complaint records found in database after insert attempt! visitDate used: {}", exactVisitDateTs);
                                result.put("complaintsInsertWarning", "Complaint rows were not saved. Check logs for details.");
                                result.put("complaintsInsertedCount", 0);
                            } else {
                                result.put("complaintsInsertedCount", actualCount);
                                logger.info("✓ Confirmed: {} complaint records are now in visit_complaints table", actualCount);
                            }
                        }
                    } else {
                        logger.debug("No complaints rows to process (null or empty)");
                    }

                    // Medicines
                    if (req.medicineRows() != null && !req.medicineRows().isEmpty()) {
                        logger.info("Processing {} medicine rows for patient: {}, visit: {}", req.medicineRows().size(), patientId, patientVisitNoVal);
                        
                        // CRITICAL: Query the EXACT visit_date from patient_visits to ensure foreign key matches
                        String getExactVisitDateSql = "SELECT visit_date FROM patient_visits WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND delete_flag = false ORDER BY visit_date DESC LIMIT 1";
                        List<java.sql.Timestamp> exactVisitDates = jdbcTemplate.queryForList(getExactVisitDateSql, java.sql.Timestamp.class, 
                            patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal);
                        
                        java.sql.Timestamp exactVisitDateTs = visitDateTs;
                        if (exactVisitDates != null && !exactVisitDates.isEmpty() && exactVisitDates.get(0) != null) {
                            exactVisitDateTs = exactVisitDates.get(0);
                            logger.info("Retrieved EXACT visit_date from patient_visits for medicines: {} (was using: {})", exactVisitDateTs, visitDateTs);
                        } else {
                            logger.error("CRITICAL: Could not find saved visit in patient_visits! Cannot insert medicines without parent record.");
                            result.put("medicineInsertError", "Parent visit not found in patient_visits table");
                            throw new IllegalStateException("Parent visit not found: Cannot insert medicines without matching patient_visits record");
                        }
                        
                        String delMed = "DELETE FROM visit_medicine WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND DATE(visit_date) = DATE(?)";
                        int deletedCount = jdbcTemplate.update(delMed, patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal, exactVisitDateTs);
                        logger.info("Deleted {} existing medicine records", deletedCount);

                        LocalDateTime now = timezoneUtils.convertTargetTimezoneToUtc(LocalDateTime.now());
                        String insMed = "INSERT INTO visit_medicine (patient_id, visit_date, patient_visit_no, shift_id, clinic_id, doctor_id, short_description, medicine_description, morning, afternoon, night, no_of_days, instruction, delete_flag, created_on, createdby_name, modified_on, modifiedby_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        int insertedCount = 0;
                        for (Map<String, Object> row : req.medicineRows()) {
                            String shortDesc = String.valueOf(row.getOrDefault("short_description", ""));
                            String medicine = String.valueOf(row.getOrDefault("medicine", ""));
                            Integer morning = toIntSafe(row.get("morning"));
                            Integer afternoon = toIntSafe(row.get("afternoon"));
                            Integer night = toIntSafe(row.get("night"));
                            Integer noOfDays = toIntSafe(row.get("days"));
                            String instruction = String.valueOf(row.getOrDefault("instruction", ""));
                            if ((shortDesc != null && !shortDesc.isEmpty()) || (medicine != null && !medicine.isEmpty())) {
                                try {
                                    logger.info("Attempting INSERT with exact visit_date: {} for medicine: {}", exactVisitDateTs, medicine);
                                    int rowsAffected = jdbcTemplate.update(insMed, patientId, exactVisitDateTs, patientVisitNoVal, shiftIdVal, clinicId, doctorId,
                                            shortDesc, medicine, morning, afternoon, night, noOfDays, instruction, false, now, userIdVal, now, userIdVal);
                                    if (rowsAffected > 0) {
                                        insertedCount++;
                                        logger.info("✓ Successfully inserted medicine row {}: short_description='{}', medicine_description='{}', visitDate={}", 
                                            insertedCount, shortDesc, medicine, exactVisitDateTs);
                                    } else {
                                        logger.error("✗ INSERT statement returned 0 rows affected for medicine: short_description='{}', medicine_description='{}'", shortDesc, medicine);
                                        result.put("medicineInsertWarning", "Some medicine rows may not have been inserted");
                                    }
                                } catch (Exception insertEx) {
                                    logger.error("✗ Error inserting medicine row: short_description='{}', medicine_description='{}', error: {}", 
                                        shortDesc, medicine, insertEx.getMessage(), insertEx);
                                    logger.error("✗ Failed INSERT parameters - patientId: {}, visitDate: {}, patientVisitNo: {}, shiftId: {}, clinicId: {}, doctorId: {}", 
                                        patientId, exactVisitDateTs, patientVisitNoVal, shiftIdVal, clinicId, doctorId);
                                    throw insertEx; // Re-throw to be caught by outer catch
                                }
                            } else {
                                logger.warn("Skipping empty medicine row: {}", row);
                            }
                        }
                        logger.info("Successfully inserted {} medicine records for patient: {}, visit: {}", insertedCount, patientId, patientVisitNoVal);
                        result.put("medicineInsertedCount", insertedCount);
                    }

                    // Prescriptions
                    if (req.prescriptionRows() != null && !req.prescriptionRows().isEmpty()) {
                        logger.info("Processing {} prescription rows for patient: {}, visit: {}", req.prescriptionRows().size(), patientId, patientVisitNoVal);
                        
                        // CRITICAL: Query the EXACT visit_date from patient_visits to ensure foreign key matches
                        String getExactVisitDateSql = "SELECT visit_date FROM patient_visits WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND delete_flag = false ORDER BY visit_date DESC LIMIT 1";
                        List<java.sql.Timestamp> exactVisitDates = jdbcTemplate.queryForList(getExactVisitDateSql, java.sql.Timestamp.class, 
                            patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal);
                        
                        java.sql.Timestamp exactVisitDateTs = visitDateTs;
                        if (exactVisitDates != null && !exactVisitDates.isEmpty() && exactVisitDates.get(0) != null) {
                            exactVisitDateTs = exactVisitDates.get(0);
                            logger.info("Retrieved EXACT visit_date from patient_visits for prescriptions: {} (was using: {})", exactVisitDateTs, visitDateTs);
                        } else {
                            logger.error("CRITICAL: Could not find saved visit in patient_visits! Cannot insert prescriptions without parent record.");
                            result.put("prescriptionInsertError", "Parent visit not found in patient_visits table");
                            throw new IllegalStateException("Parent visit not found: Cannot insert prescriptions without matching patient_visits record");
                        }
                        
                        String delPres = "DELETE FROM visit_prescription_overwrite WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND DATE(visit_date) = DATE(?)";
                        int deletedCount = jdbcTemplate.update(delPres, patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal, exactVisitDateTs);
                        logger.info("Deleted {} existing prescription records", deletedCount);

                        // Get next sequence ID for prescriptions
                        Integer nextSequenceId = 1;
                        try {
                            String maxSeqSql = "SELECT COALESCE(MAX(sequence_id), 0) + 1 FROM visit_prescription_overwrite WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND DATE(visit_date) = DATE(?)";
                            List<Integer> maxSeq = jdbcTemplate.queryForList(maxSeqSql, Integer.class, patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal, exactVisitDateTs);
                            if (maxSeq != null && !maxSeq.isEmpty() && maxSeq.get(0) != null) {
                                nextSequenceId = maxSeq.get(0);
                            }
                        } catch (Exception e) {
                            logger.warn("Failed to get next sequence ID for prescriptions, using default: {}", e.getMessage());
                        }

                        String insPres = "INSERT INTO visit_prescription_overwrite (patient_id, visit_date, patient_visit_no, shift_id, clinic_id, doctor_id, brand_name, medicine_name, catsub_description, cat_short_name, marketed_by, morning, afternoon, night, no_of_days, instruction, sequence_id, created_on, createdby_name, modified_on, modifiedby_name, delete_indicator) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        LocalDateTime now = timezoneUtils.convertTargetTimezoneToUtc(LocalDateTime.now());
                        int insertedCount = 0;
                        for (Map<String, Object> row : req.prescriptionRows()) {
                            String prescription = String.valueOf(row.getOrDefault("prescription", ""));
                            String b = String.valueOf(row.getOrDefault("b", ""));
                            String l = String.valueOf(row.getOrDefault("l", ""));
                            String d = String.valueOf(row.getOrDefault("d", ""));
                            String days = String.valueOf(row.getOrDefault("days", ""));
                            String instruction = String.valueOf(row.getOrDefault("instruction", ""));

                            // Parse prescription: expected format "Brand Name (Generic Name)" or just the name
                            String brandName = prescription;
                            String medicineName = "";
                            if (prescription.contains("(") && prescription.contains(")")) {
                                int openParen = prescription.indexOf('(');
                                int closeParen = prescription.lastIndexOf(')');
                                if (openParen > 0 && closeParen > openParen) {
                                    brandName = prescription.substring(0, openParen).trim();
                                    medicineName = prescription.substring(openParen + 1, closeParen).trim();
                                }
                            }

                            if ((brandName != null && !brandName.isEmpty()) || (medicineName != null && !medicineName.isEmpty())) {
                                Double morning = parseDoubleSafe(b.isEmpty() ? "0" : b);
                                Double afternoon = parseDoubleSafe(l.isEmpty() ? "0" : l);
                                Double night = parseDoubleSafe(d.isEmpty() ? "0" : d);
                                Integer noOfDays = toIntSafe(days.isEmpty() ? null : days);

                                try {
                                    logger.info("Attempting INSERT with exact visit_date: {} for prescription: {}", exactVisitDateTs, prescription);
                                    int rowsAffected = jdbcTemplate.update(insPres, patientId, exactVisitDateTs, patientVisitNoVal, shiftIdVal, clinicId, doctorId,
                                            brandName, medicineName, "", "", "", morning, afternoon, night, noOfDays, instruction,
                                            nextSequenceId++, now, userIdVal, now, userIdVal, false);
                                    if (rowsAffected > 0) {
                                        insertedCount++;
                                        logger.info("✓ Successfully inserted prescription row {}: brand_name='{}', medicine_name='{}', visitDate={}", 
                                            insertedCount, brandName, medicineName, exactVisitDateTs);
                                    } else {
                                        logger.error("✗ INSERT statement returned 0 rows affected for prescription: brand_name='{}', medicine_name='{}'", brandName, medicineName);
                                        result.put("prescriptionInsertWarning", "Some prescription rows may not have been inserted");
                                    }
                                } catch (Exception insertEx) {
                                    logger.error("✗ Error inserting prescription row: brand_name='{}', medicine_name='{}', error: {}", 
                                        brandName, medicineName, insertEx.getMessage(), insertEx);
                                    logger.error("✗ Failed INSERT parameters - patientId: {}, visitDate: {}, patientVisitNo: {}, shiftId: {}, clinicId: {}, doctorId: {}", 
                                        patientId, exactVisitDateTs, patientVisitNoVal, shiftIdVal, clinicId, doctorId);
                                    throw insertEx; // Re-throw to be caught by outer catch
                                }
                            } else {
                                logger.warn("Skipping empty prescription row: {}", row);
                            }
                        }
                        logger.info("Successfully inserted {} prescription records for patient: {}, visit: {}", insertedCount, patientId, patientVisitNoVal);
                        result.put("prescriptionInsertedCount", insertedCount);
                    }

                    // Investigations
                    if (req.investigationRows() != null && !req.investigationRows().isEmpty()) {
                        logger.info("Processing {} investigation rows for patient: {}, visit: {}", req.investigationRows().size(), patientId, patientVisitNoVal);
                        
                        // CRITICAL: Query the EXACT visit_date from patient_visits to ensure foreign key matches
                        String getExactVisitDateSql = "SELECT visit_date FROM patient_visits WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND delete_flag = false ORDER BY visit_date DESC LIMIT 1";
                        List<java.sql.Timestamp> exactVisitDates = jdbcTemplate.queryForList(getExactVisitDateSql, java.sql.Timestamp.class, 
                            patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal);
                        
                        java.sql.Timestamp exactVisitDateTs = visitDateTs;
                        if (exactVisitDates != null && !exactVisitDates.isEmpty() && exactVisitDates.get(0) != null) {
                            exactVisitDateTs = exactVisitDates.get(0);
                            logger.info("Retrieved EXACT visit_date from patient_visits for investigations: {} (was using: {})", exactVisitDateTs, visitDateTs);
                        } else {
                            logger.error("CRITICAL: Could not find saved visit in patient_visits! Cannot insert investigations without parent record.");
                            result.put("investigationInsertError", "Parent visit not found in patient_visits table");
                            throw new IllegalStateException("Parent visit not found: Cannot insert investigations without matching patient_visits record");
                        }
                        
                        // Delete existing investigation records for this visit (using exact visit_date to match foreign key)
                        String delInv = "DELETE FROM patient_visit_labtestasked WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND visit_date = ?";
                        int deletedCount = jdbcTemplate.update(delInv, patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal, exactVisitDateTs);
                        logger.info("Deleted {} existing investigation records", deletedCount);

                        String insInv = "INSERT INTO patient_visit_labtestasked (patient_id, visit_date, patient_visit_no, shift_id, clinic_id, doctor_id, lab_test_description, delete_flag) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        int insertedCount = 0;
                        Set<String> processedInvestigations = new HashSet<>(); // Track duplicates
                        
                        for (Map<String, Object> row : req.investigationRows()) {
                            String investigation = String.valueOf(row.getOrDefault("investigation", "")).trim();
                            if (investigation != null && !investigation.isEmpty() && !investigation.equals("null")) {
                                // Check for duplicate lab tests in the same request to avoid primary key violations
                                if (processedInvestigations.contains(investigation)) {
                                    logger.warn("Skipping duplicate investigation in request: '{}'", investigation);
                                    continue;
                                }
                                processedInvestigations.add(investigation);
                                
                                try {
                                    logger.info("Attempting INSERT with exact visit_date: {} for investigation: {}", exactVisitDateTs, investigation);
                                    int rowsAffected = jdbcTemplate.update(insInv, patientId, exactVisitDateTs, patientVisitNoVal, shiftIdVal, clinicId, doctorId,
                                            investigation, false);
                                    if (rowsAffected > 0) {
                                        insertedCount++;
                                        logger.info("✓ Successfully inserted investigation row {}: lab_test_description='{}', visitDate={}", 
                                            insertedCount, investigation, exactVisitDateTs);
                                    } else {
                                        logger.error("✗ INSERT statement returned 0 rows affected for investigation: lab_test_description='{}'", investigation);
                                        result.put("investigationInsertWarning", "Some investigation rows may not have been inserted");
                                    }
                                } catch (Exception insertEx) {
                                    String errorMsg = insertEx.getMessage();
                                    // Check for specific error types
                                    if (errorMsg != null && errorMsg.contains("duplicate key")) {
                                        logger.warn("Duplicate key violation for investigation '{}' - may already exist in database, skipping", investigation);
                                        // Don't fail the entire request, just log and continue
                                    } else if (errorMsg != null && errorMsg.contains("foreign key constraint")) {
                                        logger.error("✗ Foreign key constraint violation for investigation: '{}' - visit may not exist with exact visit_date: {}", 
                                            investigation, exactVisitDateTs);
                                        logger.error("✗ Failed INSERT parameters - patientId: {}, visitDate: {}, patientVisitNo: {}, shiftId: {}, clinicId: {}, doctorId: {}", 
                                            patientId, exactVisitDateTs, patientVisitNoVal, shiftIdVal, clinicId, doctorId);
                                        result.put("investigationInsertError", "Foreign key constraint violation - visit may not exist");
                                        throw insertEx; // Re-throw foreign key errors as they indicate a serious issue
                                    } else {
                                        logger.error("✗ Error inserting investigation row: lab_test_description='{}', error: {}", 
                                            investigation, insertEx.getMessage(), insertEx);
                                        logger.error("✗ Failed INSERT parameters - patientId: {}, visitDate: {}, patientVisitNo: {}, shiftId: {}, clinicId: {}, doctorId: {}", 
                                            patientId, exactVisitDateTs, patientVisitNoVal, shiftIdVal, clinicId, doctorId);
                                        throw insertEx; // Re-throw to be caught by outer catch
                                    }
                                }
                            } else {
                                logger.warn("Skipping empty investigation row: {}", row);
                            }
                        }
                        logger.info("Successfully inserted {} investigation records for patient: {}, visit: {}", insertedCount, patientId, patientVisitNoVal);
                        result.put("investigationInsertedCount", insertedCount);
                    } else {
                        logger.debug("No investigation rows to process (null or empty)");
                    }

                    // Dressing (body parts) - textbox field
                    if (req.dressingBodyParts() != null && !req.dressingBodyParts().trim().isEmpty()) {
                        String dressingBodyParts = req.dressingBodyParts().trim();
                        logger.info("Processing dressing (body parts) textbox data for patient: {}, visit: {}, content length: {}", 
                            patientId, patientVisitNoVal, dressingBodyParts.length());
                        
                        // CRITICAL: Query the EXACT visit_date from patient_visits to ensure foreign key matches
                        String getExactVisitDateSql = "SELECT visit_date FROM patient_visits WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND delete_flag = false ORDER BY visit_date DESC LIMIT 1";
                        List<java.sql.Timestamp> exactVisitDates = jdbcTemplate.queryForList(getExactVisitDateSql, java.sql.Timestamp.class, 
                            patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal);
                        
                        java.sql.Timestamp exactVisitDateTs = visitDateTs;
                        if (exactVisitDates != null && !exactVisitDates.isEmpty() && exactVisitDates.get(0) != null) {
                            exactVisitDateTs = exactVisitDates.get(0);
                            logger.info("Retrieved EXACT visit_date from patient_visits for dressings: {} (was using: {})", exactVisitDateTs, visitDateTs);
                        } else {
                            logger.error("CRITICAL: Could not find saved visit in patient_visits! Cannot insert dressings without parent record.");
                            result.put("dressingInsertError", "Parent visit not found in patient_visits table");
                            throw new IllegalStateException("Parent visit not found: Cannot insert dressings without matching patient_visits record");
                        }
                        
                        // Delete existing dressing records for this visit (matching stored procedure logic)
                        // The stored procedure deletes all dressings for the visit before inserting new ones
                        String delDressing = "DELETE FROM visit_dressing WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND DATE(visit_date) = DATE(?)";
                        int deletedCount = jdbcTemplate.update(delDressing, patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal, exactVisitDateTs);
                        logger.info("Deleted {} existing dressing records", deletedCount);

                        LocalDateTime now = timezoneUtils.convertTargetTimezoneToUtc(LocalDateTime.now());
                        String insDressing = "INSERT INTO visit_dressing (patient_id, visit_date, patient_visit_no, shift_id, clinic_id, doctor_id, dressing_description, delete_flag, created_on, createdby_name, modified_on, modifiedby_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        
                        try {
                            logger.info("Attempting INSERT with exact visit_date: {} for dressing (body parts): {}", exactVisitDateTs, dressingBodyParts);
                            int rowsAffected = jdbcTemplate.update(insDressing, patientId, exactVisitDateTs, patientVisitNoVal, shiftIdVal, clinicId, doctorId, 
                                    dressingBodyParts, false, now, userIdVal, now, userIdVal);
                            if (rowsAffected > 0) {
                                logger.info("✓ Successfully inserted dressing (body parts): dressing_description='{}', visitDate={}", 
                                    dressingBodyParts, exactVisitDateTs);
                                
                                // Verify the insert worked by querying back using the exact visit_date
                                String verifySql = "SELECT COUNT(*) FROM visit_dressing WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND visit_date = ? AND delete_flag = false";
                                Integer actualCount = jdbcTemplate.queryForObject(verifySql, Integer.class, patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal, exactVisitDateTs);
                                logger.info("✓ Verification: Found {} dressing record(s) in database after insert (visitDate: {})", actualCount, exactVisitDateTs);
                                if (actualCount == null || actualCount == 0) {
                                    logger.error("✗ CRITICAL: No dressing record found in database after insert attempt! visitDate used: {}", exactVisitDateTs);
                                    result.put("dressingInsertWarning", "Dressing (body parts) was not saved. Check logs for details.");
                                    result.put("dressingInsertedCount", 0);
                                } else {
                                    result.put("dressingInsertedCount", actualCount);
                                    logger.info("✓ Confirmed: Dressing (body parts) is now in visit_dressing table");
                                }
                            } else {
                                logger.error("✗ INSERT statement returned 0 rows affected for dressing (body parts): '{}'", dressingBodyParts);
                                result.put("dressingInsertWarning", "Dressing (body parts) may not have been inserted");
                                result.put("dressingInsertedCount", 0);
                            }
                        } catch (Exception insertEx) {
                            String errorMsg = insertEx.getMessage();
                            // Check for specific error types
                            if (errorMsg != null && errorMsg.contains("duplicate key")) {
                                logger.warn("Duplicate key violation for dressing (body parts) '{}' - may already exist in database", dressingBodyParts);
                                result.put("dressingInsertWarning", "Dressing may already exist");
                            } else if (errorMsg != null && errorMsg.contains("foreign key constraint")) {
                                logger.error("✗ Foreign key constraint violation for dressing (body parts): '{}' - visit may not exist with exact visit_date: {}", 
                                    dressingBodyParts, exactVisitDateTs);
                                logger.error("✗ Failed INSERT parameters - patientId: {}, visitDate: {}, patientVisitNo: {}, shiftId: {}, clinicId: {}, doctorId: {}", 
                                    patientId, exactVisitDateTs, patientVisitNoVal, shiftIdVal, clinicId, doctorId);
                                result.put("dressingInsertError", "Foreign key constraint violation - visit may not exist");
                                throw insertEx; // Re-throw foreign key errors as they indicate a serious issue
                            } else {
                                logger.error("✗ Error inserting dressing (body parts): '{}', error: {}", 
                                    dressingBodyParts, insertEx.getMessage(), insertEx);
                                logger.error("✗ Failed INSERT parameters - patientId: {}, visitDate: {}, patientVisitNo: {}, shiftId: {}, clinicId: {}, doctorId: {}", 
                                    patientId, exactVisitDateTs, patientVisitNoVal, shiftIdVal, clinicId, doctorId);
                                throw insertEx; // Re-throw to be caught by outer catch
                            }
                        }
                    } else {
                        logger.debug("No dressing (body parts) data to process (null or empty)");
                    }
                    
                    // Instruction Groups - Save using JPA (matching stored procedure logic)
                    // Log instruction groups status BEFORE checking
                    logger.info("🔍 Checking instruction groups - isNull: {}, isEmpty: {}, size: {}", 
                        req.instructionGroups() == null, 
                        req.instructionGroups() == null ? "N/A" : req.instructionGroups().isEmpty(),
                        req.instructionGroups() == null ? "N/A" : req.instructionGroups().size());
                    
                    if (req.instructionGroups() == null) {
                        logger.warn("⚠️ instructionGroups is NULL in request");
                    } else if (req.instructionGroups().isEmpty()) {
                        logger.warn("⚠️ instructionGroups is EMPTY in request (size: 0)");
                    }
                    
                    if (req.instructionGroups() != null && !req.instructionGroups().isEmpty()) {
                        logger.info("📋 Processing {} instruction group items for patient: {}, visit: {}", 
                            req.instructionGroups().size(), patientId, patientVisitNoVal);
                        
                        // Log raw data received for debugging (INFO level so it shows in logs)
                        logger.info("Raw instructionGroups data: {}", req.instructionGroups());
                        
                        try {
                            // CRITICAL: Use the EXACT visit_date from the saved visit entity (from result map)
                            // This ensures the visit_date used matches exactly what was saved in patient_visits table
                            // The saved entity's visitDate is the authoritative source - no need to query again
                            LocalDateTime exactVisitDateForInstructions = actualVisitDate;
                            
                            // First, try to get visitDate from the saved result (most reliable)
                            Object visitDateFromResult = result.get("visitDate");
                            boolean useResultVisitDate = false;
                            if (visitDateFromResult != null) {
                                if (visitDateFromResult instanceof LocalDateTime) {
                                    exactVisitDateForInstructions = (LocalDateTime) visitDateFromResult;
                                    useResultVisitDate = true;
                                    logger.info("Using visit_date from saved result: {}", exactVisitDateForInstructions);
                                } else if (visitDateFromResult instanceof java.sql.Timestamp) {
                                    exactVisitDateForInstructions = ((java.sql.Timestamp) visitDateFromResult).toLocalDateTime();
                                    useResultVisitDate = true;
                                    logger.info("Using visit_date from saved result (converted from Timestamp): {}", exactVisitDateForInstructions);
                                }
                            }
                            
                            // Fallback: Query the database if result doesn't have visitDate (shouldn't happen, but safety check)
                            if (!useResultVisitDate) {
                                String getExactVisitDateSql = "SELECT visit_date FROM patient_visits WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND shift_id = ? AND patient_visit_no = ? AND delete_flag = false ORDER BY visit_date DESC LIMIT 1";
                                List<java.sql.Timestamp> exactVisitDates = jdbcTemplate.queryForList(getExactVisitDateSql, java.sql.Timestamp.class, 
                                    patientId, doctorId, clinicId, shiftIdVal, patientVisitNoVal);
                                
                                if (exactVisitDates != null && !exactVisitDates.isEmpty() && exactVisitDates.get(0) != null) {
                                    // Use the exact Timestamp from database - convert preserving exact value
                                    java.sql.Timestamp timestamp = exactVisitDates.get(0);
                                    exactVisitDateForInstructions = timestamp.toLocalDateTime();
                                    logger.info("Retrieved EXACT visit_date from patient_visits query: {} (Timestamp: {})", 
                                        exactVisitDateForInstructions, timestamp);
                                } else {
                                    logger.error("CRITICAL: Could not find saved visit in patient_visits! Cannot insert instruction groups without parent record.");
                                    result.put("instructionGroupsInsertError", "Parent visit not found in patient_visits table");
                                    throw new IllegalStateException("Parent visit not found: Cannot insert instruction groups without matching patient_visits record");
                                }
                            }
                            
                            logger.info("Final visit_date to use for instruction groups: {}", exactVisitDateForInstructions);
                            
                            // NOTE: We don't need to verify the visit exists because we just saved it in the same transaction
                            // The visit is guaranteed to exist since saveComprehensiveVisit succeeded
                            // Verification was causing issues because JPA entities might not be visible to JDBC queries
                            // until flush, but we can trust the visitDate from the saved entity
                            logger.info("✓ Using visit_date from saved visit entity (no verification needed - visit was just saved)");
                            
                            // Delete existing instruction groups for this visit (matching stored procedure logic)
                            visitGroupsInstructionsRepository.deleteByVisit(
                                doctorId, clinicId, shiftIdVal, patientId, patientVisitNoVal, exactVisitDateForInstructions);
                            logger.info("Deleted existing instruction groups for visit using exact visit_date: {}", exactVisitDateForInstructions);
                            
                            // Create new instruction group entries
                            List<VisitGroupsInstructions> visitInstructions = new ArrayList<>();
                            LocalDateTime now = LocalDateTime.now();
                            
                            for (Map<String, Object> instructionItem : req.instructionGroups()) {
                                String groupDescription = getStringValue(instructionItem, "groupDescription", "group_description");
                                String instructionsDescription = getStringValue(instructionItem, "instructionsDescription", "instructions_description");
                                Integer sequenceNo = getIntegerValue(instructionItem, "sequenceNo", "sequence_no");
                                
                                logger.info("Processing instruction item {} - groupDescription: '{}', instructionsDescription: '{}', sequenceNo: {}, fullItem: {}", 
                                    visitInstructions.size() + 1, groupDescription, instructionsDescription, sequenceNo, instructionItem);
                                
                                // Allow saving if at least groupDescription OR instructionsDescription is present
                                // Both fields are required in the table, but we'll use empty string if one is missing
                                boolean hasGroupDesc = groupDescription != null && !groupDescription.trim().isEmpty();
                                boolean hasInstrDesc = instructionsDescription != null && !instructionsDescription.trim().isEmpty();
                                
                                if (hasGroupDesc || hasInstrDesc) {
                                    // Use empty string if a field is missing (table allows it)
                                    String finalGroupDesc = hasGroupDesc ? groupDescription.trim() : "";
                                    String finalInstrDesc = hasInstrDesc ? instructionsDescription.trim() : "";
                                    
                                    if (!hasGroupDesc) {
                                        logger.warn("⚠️ Instruction item missing groupDescription, using empty string. Item: {}", instructionItem);
                                    }
                                    if (!hasInstrDesc) {
                                        logger.warn("⚠️ Instruction item missing instructionsDescription, using empty string. Item: {}", instructionItem);
                                    }
                                    
                                    VisitGroupsInstructions visitInstruction = new VisitGroupsInstructions();
                                    visitInstruction.setDoctorId(doctorId);
                                    visitInstruction.setClinicId(clinicId);
                                    visitInstruction.setShiftId(shiftIdVal);
                                    visitInstruction.setPatientId(patientId);
                                    visitInstruction.setPatientVisitNo(patientVisitNoVal);
                                    visitInstruction.setVisitDate(exactVisitDateForInstructions);
                                    visitInstruction.setGroupDescription(finalGroupDesc);
                                    visitInstruction.setInstructionsDescription(finalInstrDesc);
                                    visitInstruction.setSequenceNo(sequenceNo != null ? sequenceNo : 0);
                                    visitInstruction.setCreatedByName(userIdVal);
                                    visitInstruction.setCreatedOn(now);
                                    
                                    visitInstructions.add(visitInstruction);
                                    logger.info("✓ Added instruction item to save list: group='{}', instruction='{}'", finalGroupDesc, finalInstrDesc);
                                } else {
                                    logger.warn("⚠️ Skipping instruction group item - both groupDescription and instructionsDescription are missing/empty. Full item: {}", 
                                        instructionItem);
                                }
                            }
                            
                            if (!visitInstructions.isEmpty()) {
                                logger.info("Attempting to save {} instruction group items for patient: {}, visit: {}", 
                                    visitInstructions.size(), patientId, patientVisitNoVal);
                                
                                // Log each instruction being saved for debugging (INFO level)
                                for (int i = 0; i < visitInstructions.size(); i++) {
                                    VisitGroupsInstructions vi = visitInstructions.get(i);
                                    logger.info("Instruction {}: doctorId={}, clinicId={}, shiftId={}, patientId={}, visitNo={}, visitDate={}, group={}, instruction={}", 
                                        i + 1, vi.getDoctorId(), vi.getClinicId(), vi.getShiftId(), vi.getPatientId(), 
                                        vi.getPatientVisitNo(), vi.getVisitDate(), vi.getGroupDescription(), vi.getInstructionsDescription());
                                }
                                
                                // Save all instruction groups
                                List<VisitGroupsInstructions> savedInstructions = visitGroupsInstructionsRepository.saveAll(visitInstructions);
                                
                                logger.info("✓ saveAll() returned {} instruction group items (expected {})", 
                                    savedInstructions.size(), visitInstructions.size());
                                
                                // Verify the save by counting records
                                long countAfterSave = visitGroupsInstructionsRepository.countByDoctorIdAndClinicIdAndShiftIdAndPatientIdAndPatientVisitNoAndVisitDate(
                                    doctorId, clinicId, shiftIdVal, patientId, patientVisitNoVal, exactVisitDateForInstructions);
                                logger.info("✓ Verified: {} instruction group records now exist in database for this visit", countAfterSave);
                                
                                result.put("instructionGroupsInsertedCount", visitInstructions.size());
                                result.put("instructionGroupsVerifiedCount", countAfterSave);
                            } else {
                                logger.warn("No valid instruction group items to save after filtering. Total items received: {}", 
                                    req.instructionGroups() != null ? req.instructionGroups().size() : 0);
                                result.put("instructionGroupsInsertedCount", 0);
                                result.put("instructionGroupsFilteredOut", true);
                            }
                        } catch (Exception instructionEx) {
                            logger.error("❌ Failed to save instruction groups for patient: {}, visit: {}, error: {}", 
                                patientId, patientVisitNoVal, instructionEx.getMessage(), instructionEx);
                            logger.error("Exception stack trace:", instructionEx);
                            result.put("instructionGroupsSaveError", true);
                            result.put("instructionGroupsErrorMessage", instructionEx.getMessage());
                            
                            // Re-throw if it's a critical error (like foreign key violation)
                            if (instructionEx instanceof IllegalStateException || 
                                (instructionEx.getCause() != null && instructionEx.getCause().getMessage() != null && 
                                 instructionEx.getCause().getMessage().contains("foreign key"))) {
                                throw instructionEx;
                            }
                        }
                    }
                } catch (Exception persistEx) {
                    logger.error("Failed to persist treatment arrays (diagnosis/medicines/prescriptions/investigations/dressings/instructionGroups) for patient: {}, visit: {}", 
                        req.patientId(), patientVisitNoVal, persistEx);
                    logger.error("Exception details - Message: {}, Cause: {}", 
                        persistEx.getMessage(), persistEx.getCause(), persistEx);
                    
                    // Add error details to response so client knows what failed
                    result.put("treatmentArraySaveError", true);
                    result.put("treatmentArrayErrorMessage", persistEx.getMessage());
                    if (persistEx.getCause() != null) {
                        result.put("treatmentArrayErrorCause", persistEx.getCause().getMessage());
                    }
                    
                    // Check if it's a foreign key constraint violation
                    if (persistEx.getMessage() != null && persistEx.getMessage().contains("foreign key constraint")) {
                        logger.error("FOREIGN KEY CONSTRAINT VIOLATION - The visit might not exist or visitDate doesn't match exactly");
                        result.put("foreignKeyError", true);
                        result.put("foreignKeyDetails", "Visit record might not exist with exact composite key. visitDate used: " + visitDateTs);
                    }
                    
                    // Don't fail the entire request, but ensure error is visible
                }
                
                // Add the saved arrays to the response for confirmation
                if (req.diagnosisRows() != null) {
                    result.put("diagnosisRows", req.diagnosisRows());
                    result.put("diagnosisRowsCount", req.diagnosisRows().size());
                }
                if (req.medicineRows() != null) {
                    result.put("medicineRows", req.medicineRows());
                    result.put("medicineRowsCount", req.medicineRows().size());
                }
                if (req.prescriptionRows() != null) {
                    result.put("prescriptionRows", req.prescriptionRows());
                    result.put("prescriptionRowsCount", req.prescriptionRows().size());
                }
                if (req.investigationRows() != null) {
                    result.put("investigationRows", req.investigationRows());
                    result.put("investigationRowsCount", req.investigationRows().size());
                }
                if (req.dressingBodyParts() != null && !req.dressingBodyParts().trim().isEmpty()) {
                    result.put("dressingBodyParts", req.dressingBodyParts());
                    result.put("dressingBodyPartsSaved", true);
                }
                if (req.instructionGroups() != null) {
                    result.put("instructionGroups", req.instructionGroups());
                    result.put("instructionGroupsCount", req.instructionGroups().size());
                }
                
                // Add key missing fields from the request
                result.put("shiftId", shiftId);
                result.put("statusId", req.statusId());
                result.put("userId", req.userId());
                result.put("isSubmitPatientVisitDetails", req.isSubmitPatientVisitDetails());
                
                // Financial fields
                result.put("feesToCollect", req.feesToCollect());
                result.put("feesPaid", req.feesPaid());
                result.put("discount", req.discount());
                result.put("originalDiscount", req.originalDiscount());
                
                // Patient vitals
                result.put("pulse", req.pulse());
                result.put("heightInCms", req.heightInCms());
                result.put("weightInKgs", req.weightInKgs());
                result.put("bloodPressure", req.bloodPressure());
                result.put("sugar", req.sugar());
                result.put("tft", req.tft());
                result.put("pallor", req.pallor());
                
                // Follow-up information
                result.put("followUp", req.followUp());
                result.put("followUpType", req.followUpType());
                result.put("followUpFlag", req.followUpFlag());
                result.put("followUpComment", req.followUpComment());
                result.put("followUpDate", req.followUpDate());
                
                // Medical conditions
                result.put("hypertension", req.hypertension());
                result.put("diabetes", req.diabetes());
                result.put("cholestrol", req.cholestrol());
                result.put("ihd", req.ihd());
                result.put("th", req.th());
                result.put("asthama", req.asthama());
                result.put("smoking", req.smoking());
                result.put("tobaco", req.tobaco());
                result.put("alchohol", req.alchohol());
                result.put("inPerson", req.inPerson());
                
                // Additional medical fields
                result.put("allergyDetails", req.allergyDetails());
                result.put("habitDetails", req.habitDetails());
                result.put("observation", req.observation());
                result.put("importantFindings", req.importantFindings());
                result.put("additionalComments", req.additionalComments());
                result.put("symptomComment", req.symptomComment());
                result.put("impression", req.impression());
                result.put("currentComplaint", req.currentComplaint());
                result.put("chiefComplaint", req.chiefComplaint());
                result.put("visitComments", req.visitComments());
                result.put("currentMedicines", req.currentMedicines());
                result.put("pastSurgicalHistory", req.pastSurgicalHistory());
                result.put("surgicalHistory", req.surgicalHistory());
                
                // Referral information
                result.put("referBy", req.referBy());
                result.put("referralName", req.referralName());
                result.put("referralContact", req.referralContact());
                result.put("referralEmail", req.referralEmail());
                result.put("referralAddress", req.referralAddress());
                
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (IllegalArgumentException e) {
            // Validation errors - return 400 with detailed message
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Validation failed: " + e.getMessage());
            error.put("errorType", "VALIDATION_ERROR");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            // Other errors - return 500 with generic message
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to save comprehensive visit data: " + e.getMessage());
            error.put("errorType", "SYSTEM_ERROR");
            logger.error("Unexpected error saving visit: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Get visit details using composite key parameters
     */
    @GetMapping("/details")
    public ResponseEntity<?> getVisitDetails(
            @RequestParam String patientId,
            @RequestParam String doctorId,
            @RequestParam String clinicId,
            @RequestParam String shiftId,
            @RequestParam String visitDate,
            @RequestParam String patientVisitNo) {
        try {
            // Use the existing appointment details API
            return getPatientAppointmentDetails(patientId, Short.parseShort(shiftId), clinicId, 
                    doctorId, Integer.parseInt(patientVisitNo), 1);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get visit details: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get comprehensive patient appointment details using JPA
     * This replaces the JDBC-based USP_Get_PatientAppointmentDetailsNew implementation
     * 
     * @param patientId Patient ID
     * @param shiftId Shift ID (not currently used in JPA version)
     * @param clinicId Clinic ID
     * @param doctorId Doctor ID (not currently used in JPA version)
     * @param patientVisitNo Patient visit number
     * @param languageId Language ID for translations
     * @return Comprehensive patient appointment details
     */
    @GetMapping("/appointment-details")
    public ResponseEntity<?> getPatientAppointmentDetails(
            @RequestParam String patientId,
            @RequestParam Short shiftId,
            @RequestParam String clinicId,
            @RequestParam String doctorId,
            @RequestParam Integer patientVisitNo,
            @RequestParam(defaultValue = "1") Integer languageId) {
        try {
            logger.info("Getting appointment details using JPA for patient: {}, clinic: {}, visitNo: {}", 
                patientId, clinicId, patientVisitNo);
            
            // Use JPA service to get appointment details
            Map<String, Object> result = visitJpaService.getPatientAppointmentDetails(
                patientId, clinicId, patientVisitNo, languageId);
            
            if (result.get("success") != null && (Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("ErrorNumber", -1);
                error.put("ErrorMessage", result.get("error") != null ? result.get("error") : result.get("message"));
                return ResponseEntity.badRequest().body(error);
            }
            
        } catch (Exception e) {
            logger.error("Error getting appointment details: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("ErrorNumber", -1);
            error.put("ErrorMessage", "Failed to get patient appointment details: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private String buildExistsCheckQuery() {
        // Simplified query to test basic join structure
        return "SELECT 1 FROM patient_visits PV " +
                "INNER JOIN patient_master PM ON PV.patient_id = PM.id " +
                "WHERE PV.patient_id = ? " +
                "AND PV.clinic_id = ? " +
                "AND PV.patient_visit_no = ? " +
                "AND PV.delete_flag = false";
    }

    private List<Map<String, Object>> getComprehensiveVisitData(String patientId, String clinicId, 
            Integer patientVisitNo, Integer languageId, boolean includePaymentMaster) {
        
        String mainQuery = buildMainQuery(includePaymentMaster);
        
        // SIMPLIFIED: Parameters: language_id (for GT join), patient_id, clinic_id, patient_visit_no
        return jdbcTemplate.queryForList(mainQuery, languageId, patientId, clinicId, patientVisitNo);
    }

    private String buildMainQuery(boolean includePaymentMaster) {
        StringBuilder query = new StringBuilder();
        
        // Simplified query for testing
        query.append("SELECT ")
                .append("PM.first_name, ")
                .append("PM.last_name, ")
                .append("PV.patient_id, ")
                .append("PV.visit_date, ")
                .append("PV.patient_visit_no, ")
                .append("GT.gender_description, ")
                .append("DMS.prefix, ")
                .append("DMS.speciality ")
                .append("FROM patient_visits PV ")
                .append("INNER JOIN patient_master PM ON PV.patient_id = PM.id ")
                .append("INNER JOIN gender_translations GT ON PM.gender_id = GT.gender_id AND GT.language_id = ? ")
                .append("LEFT JOIN doctor_master DMS ON DMS.doctor_id = PV.doctor_id ")
                .append("WHERE PV.patient_id = ? ")
                .append("AND PV.clinic_id = ? ")
                .append("AND PV.patient_visit_no = ? ")
                .append("AND PV.delete_flag = false");
        
        return query.toString();
        
        /* ORIGINAL COMPLEX QUERY - TO BE RESTORED AFTER TESTING
        query.append("SELECT ")
                .append("PM.first_name || ' ' || COALESCE(PM.middle_name, '') || ' ' || PM.last_name as Name, ")
                .append("PM.first_name || ' ' || PM.last_name as Partial_Name, ")
                .append("PM.age_given, ")
                .append("PM.date_of_birth, ")
                .append("PV.folder_no, ")
                .append("PV.visit_date, ")
                .append("PV.weight_in_kgs, ")
                .append("PV.height_in_cms, ")
                .append("PV.pulse, ")
                .append("PV.blood_pressure, ")
                .append("COALESCE(PV.diabetes, false) AS Diabetes, ")
                .append("COALESCE(PV.cholestrol, false) AS Cholestrol, ")
                .append("PV.fees_to_collect, ")
                .append("PV.instructions, ")
                .append("PV.folder_no, ")
                .append("PV.financial_year, ")
                .append("PV.patient_visit_no, ")
                .append("PV.status_id, ")
                .append("PV.instructions, ")
                .append("PV.observation, ")
                .append("PV.fees_collected, ")
                .append("PV.discount, ")
                .append("PV.original_discount, ")
                .append("PV.comment, ")
                .append("PM.first_name || ' ' || PM.last_name as FirstLastName, ")
                .append("PV.sugar, ")
                .append("PV.thtext, ")
                .append("COALESCE(PV.in_person, false) AS In_Person, ")
                .append("COALESCE(PV.on_call_status, false) AS On_Call_Status, ")
                .append("PV.impression, ")
                .append("PM.gender_id, ")
                .append("GT.gender_description, ")
                .append("EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - PM.date_of_birth)) / 31557600.0 AS AgeYearsDecimal, ")
                .append("ROUND(EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - PM.date_of_birth)) / 31557600.0)::INT AS AgeYearsIntRound, ")
                .append("FLOOR(EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - PM.date_of_birth)) / 31557600.0)::INT AS AgeYearsIntTrunc, ")
                .append("PM.mobile_1, ")
                .append("PV.weight_in_kgs, ")
                .append("CASE WHEN POSITION(':' IN PV.refer_doctor_details) > 0 THEN ")
                .append("SUBSTRING(PV.refer_doctor_details FROM POSITION(':' IN PV.refer_doctor_details) + 1) ")
                .append("ELSE PV.refer_doctor_details END AS Refer_Doctor_Details, ")
                .append("PV.refer_id, ")
                .append("RT.refer_by_description, ")
                .append("RT.refer_by_description || ' -- ' || ")
                .append("CASE WHEN POSITION(':' IN PV.refer_doctor_details) > 0 THEN ")
                .append("SUBSTRING(PV.refer_doctor_details FROM POSITION(':' IN PV.refer_doctor_details) + 1) ")
                .append("ELSE PV.refer_doctor_details END AS REFERDETAILS, ")
                .append("PV.payment_by_id, ")
                .append("PV.payment_remark, ")
                .append("COALESCE(PV.follow_up, '') AS follow_up, ")
                .append("COALESCE(PV.current_medicines, '') AS Current_Medicines, ")
                .append("COALESCE(PV.visit_comments, '') AS Visit_Comments, ")
                .append("COALESCE(PV.current_complaints, '') AS Current_Complaints, ")
                .append("COALESCE(PV.is_follow_up, false) AS Is_follow_Up, ")
                .append("COALESCE(PV.is_submit_patient_visit_details, false) AS Is_Submit_Patient_Visit_Details, ")
                .append("COALESCE(PV.tpr, '') AS TPR, ")
                .append("PV.important_findings, ")
                .append("PV.additional_comments, ")
                .append("PV.systemic, ")
                .append("PV.odeama, ")
                .append("PV.pallor, ")
                .append("COALESCE(PV.is_submit_gynec_details, false) AS IS_Submit_Gynec_Details, ")
                .append("COALESCE(PV.gc, '') AS GC, ")
                .append("COALESCE(PV.fmp, '') AS FMP, ")
                .append("COALESCE(PV.prmc, '') AS PRMC, ")
                .append("COALESCE(PV.pamc, '') AS PAMC, ")
                .append("COALESCE(PV.lmp, '') AS LMP, ")
                .append("COALESCE(PV.obstetrics_history, '') AS Obstetrics_History, ")
                .append("COALESCE(PV.surgical_history_past_history, '') AS Surgical_History_Past_History, ")
                .append("COALESCE(PV.gynec_additional_comments, '') AS Gynec_Additional_Comments, ")
                .append("COALESCE(PV.follow_up_type, 0) AS follow_up_type, ")
                .append("FUT.followup_description AS FollowUp_Description, ")
                .append("CASE WHEN PV.follow_up_date IS NULL THEN '' ")
                .append("ELSE REPLACE(TO_CHAR(PV.follow_up_date, 'DD Mon YYYY'), ' ', '-') END AS Follow_Up_Date, ")
                .append("CASE WHEN PV.edd IS NULL THEN 'NULL' ")
                .append("ELSE REPLACE(TO_CHAR(PV.edd, 'DD Mon YYYY'), ' ', '-') END AS EDD, ")
                .append("PV.plan, ")
                .append("PV.notes, ")
                .append("PV.follow_up_comment, ")
                .append("PV.treatment_comment, ")
                .append("PV.treatment_plan, ")
                .append("PV.in_person as Person, ")
                .append("PV.doctor_id, ")
                .append("DMS.prefix || DMS.first_name || ' - ' || DMS.speciality AS DOCTOR_NAME, ")
                .append("COALESCE(FUT.followup_description, '0') || ' - ' || ")
                .append("CASE WHEN PV.follow_up_date IS NULL THEN '' ")
                .append("ELSE REPLACE(TO_CHAR(PV.follow_up_date, 'DD Mon YYYY'), ' ', '-') END AS Folloupdateprint, ")
                .append("COALESCE(FAM.followup_after, 0) AS Followup_After, ")
                .append("COALESCE(PV.schedule, 0) AS Schedule, ")
                .append("PV.additional_instructions, ")
                .append("FAM.days as followuP_after_Days, ")
                .append("PV.followup_after as followupafter_Id, ")
                .append("PV.visit_date, ")
                .append("PV.treatment_comment, ")
                .append("PV.treatment_plan, ")
                .append("PV.impression_finding, ")
                .append("PV.follow_up, ")
                .append("PV.complaints_by_patient_per_visit, ")
                .append("PV.receipt_number, ")
                .append("rs.receipt_date, ")
                .append("rs.receipt_amount, ")
                .append("CASE WHEN TO_CHAR(PV.online_appointment_time, 'HH24:MI') = '00:00' THEN NULL ")
                .append("ELSE TO_CHAR(PV.online_appointment_time, 'HH24:MI') END AS Online_Appointment_Time, ")
                .append("PV.doctor_address, ")
                .append("PV.doctor_mobile, ")
                .append("PV.doctor_email ");
        
        if (includePaymentMaster) {
            query.append(", PTM.payment_description ");
        }
        
        query.append("FROM patient_visits PV ")
                .append("INNER JOIN patient_master PM ON PV.patient_id = PM.id ")
                .append("INNER JOIN gender_translations GT ON PM.gender_id = GT.gender_id AND GT.language_id = ? ")
                .append("LEFT JOIN follow_up_type FUT ON PV.follow_up_type = FUT.id ")
                .append("LEFT JOIN refer_by_translations RT ON PM.refer_id = RT.refer_id AND RT.language_id = ? ")
                .append("LEFT JOIN doctor_master DMS ON DMS.doctor_id = PV.doctor_id ")
                .append("LEFT JOIN followup_after_master FAM ON FAM.id = PV.followup_after ");
        
        if (includePaymentMaster) {
            query.append("INNER JOIN payment_type_master PTM ON PTM.id = PV.payment_by_id ");
        }
        
        query.append("LEFT JOIN patient_receipts rs ON PV.receipt_number = rs.receipt_number ")
                .append("WHERE PV.patient_id = ? ")
                .append("AND PV.clinic_id = ? ")
                .append("AND PV.patient_visit_no = ? ")
                .append("AND PV.delete_flag = false");
        
        return query.toString();
        */
    }

    private List<Map<String, Object>> getAdditionalVisitData(String patientId, String clinicId, 
            String doctorId, Short shiftId, Integer patientVisitNo) {
        
        String additionalQuery = "SELECT " +
                "PV.weight_in_kgs, " +
                "PV.height_in_cms, " +
                "PV.pulse, " +
                "PV.blood_pressure, " +
                "COALESCE(PV.asthama, false) AS Asthama, " +
                "COALESCE(PV.hypertension, false) AS Hypertension, " +
                "COALESCE(PV.diabetes, false) AS Diabetes, " +
                "COALESCE(PV.cholestrol, false) AS Cholestrol, " +
                "COALESCE(PV.ihd, false) AS IHD, " +
                "COALESCE(PV.th, false) AS TH, " +
                "PV.instructions, " +
                "PV.fees_to_collect, " +
                "PV.instructions, " +
                "PV.patient_visit_no, " +
                "PV.status_id, " +
                "COALESCE(PV.smoking, false) AS Smoking, " +
                "COALESCE(PV.tobaco, false) AS Tobaco, " +
                "COALESCE(PV.alchohol, false) AS Alchohol, " +
                "COALESCE(PV.pregnant, false) AS Pregnant, " +
                "COALESCE(PV.discount, 0) AS Discount, " +
                "PV.habits_comments, " +
                "PV.allergy_dtls, " +
                "PV.instructions, " +
                "PV.observation, " +
                "PV.original_billed_amount, " +
                "PV.symptom_comment, " +
                "PV.on_call_status, " +
                "PV.fees_collected, " +
                "PV.comment, " +
                "PV.discount, " +
                "PV.original_discount, " +
                "COALESCE(PV.impression, '') AS Impression, " +
                "COALESCE(PV.payment_by_id, 0) AS Payment_By_ID, " +
                "PV.payment_remark, " +
                "PTM.payment_description, " +
                "COALESCE(PV.follow_up, '') AS follow_up, " +
                "COALESCE(PV.follow_up_type, 0) AS follow_up_type, " +
                "FUT.followup_description AS FollowUp_Description, " +
                "CASE WHEN PV.follow_up_date IS NULL THEN '' " +
                "ELSE REPLACE(TO_CHAR(PV.follow_up_date, 'DD Mon YYYY'), ' ', '-') END AS Follow_Up_Date, " +
                "PV.plan, " +
                "PV.notes, " +
                "PV.follow_up_comment, " +
                "PV.treatment_comment, " +
                "PV.treatment_plan, " +
                "PV.in_person as Person, " +
                "COALESCE(FUT.followup_description, '0') || ' - ' || " +
                "CASE WHEN PV.follow_up_date IS NULL THEN '' " +
                "ELSE REPLACE(TO_CHAR(PV.follow_up_date, 'DD Mon YYYY'), ' ', '-') END AS Folloupdateprint, " +
                "COALESCE(FAM.followup_after, 0) AS Followup_After, " +
                "COALESCE(PV.schedule, 0) AS Schedule, " +
                "PV.additional_instructions, " +
                "FAM.days as followuP_after_Days, " +
                "PV.visit_date, " +
                "PV.followup_after as followupafter_Id, " +
                "PV.treatment_comment, " +
                "PV.treatment_plan, " +
                "PV.impression_finding, " +
                "PV.follow_up, " +
                "PV.receipt_number, " +
                "rs.receipt_date, " +
                "rs.receipt_amount, " +
                "CASE WHEN TO_CHAR(PV.online_appointment_time, 'HH24:MI') = '00:00' THEN NULL " +
                "ELSE TO_CHAR(PV.online_appointment_time, 'HH24:MI') END AS \"Online_Appointment_Time\", " +
                "PV.refer_id, " +
                "CASE WHEN POSITION(':' IN PV.refer_doctor_details) > 0 THEN " +
                "SUBSTRING(PV.refer_doctor_details FROM POSITION(':' IN PV.refer_doctor_details) + 1) " +
                "ELSE PV.refer_doctor_details END AS Refer_Doctor_Details, " +
                "PV.doctor_address, " +
                "PV.doctor_mobile, " +
                "PV.doctor_email " +
                "FROM patient_visits PV " +
                "INNER JOIN patient_master PM ON PV.patient_id = PM.id " +
                "LEFT JOIN payment_type_master PTM ON PV.payment_by_id = PTM.id " +
                "LEFT JOIN follow_up_type FUT ON PV.follow_up_type = FUT.id " +
                "LEFT JOIN followup_after_master FAM ON FAM.id = PV.followup_after " +
                "LEFT JOIN patient_receipts rs ON PV.receipt_number = rs.receipt_number " +
                "WHERE PV.patient_id = ? " +
                "AND PV.clinic_id = ? " +
                "AND PV.doctor_id = ? " +
                "AND PV.shift_id = ? " +
                "AND PV.patient_visit_no = ? " +
                "AND PV.delete_flag = false";
        
        return jdbcTemplate.queryForList(additionalQuery, patientId, clinicId, 
                doctorId, shiftId, patientVisitNo);
    }

    /**
     * Get today's appointments for given date matching USP_Get_TodaysAppointments_ForGivenDate stored procedure
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param futureDate Future date to query appointments for
     * @param languageId Language ID for translations
     * @return Comprehensive appointment data with 4 result sets
     */
    @GetMapping("/last-visit/{patientId}")
    public ResponseEntity<?> getLastVisitDetails(@PathVariable String patientId) {
        try {
            logger.info("Getting last visit details for patient: {}", patientId);
            
            Map<String, Object> result = visitJpaService.getLastVisitDetails(patientId);
            
            if (result.get("success") != null && (Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            logger.error("Error getting last visit details for patient {}: {}", patientId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get last visit details: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Get previous date data for a patient (USP_Get_PrevDateData equivalent)
     * Returns data from the last completed visit to pre-populate form fields
     * 
     * @param patientId Patient ID
     * @param doctorId Doctor ID (optional, for logging)
     * @param patientVisitNo Patient visit number for today's visit
     * @param todaysVisitDate Today's visit date (YYYY-MM-DD)
     * @param shiftId Shift ID
     * @param clinicId Clinic ID
     * @return Previous visit data for form pre-population
     */
    @GetMapping("/prev-date-data")
    public ResponseEntity<?> getPreviousDateData(
            @RequestParam String patientId,
            @RequestParam(required = false) String doctorId,
            @RequestParam Integer patientVisitNo,
            @RequestParam String todaysVisitDate,
            @RequestParam Short shiftId,
            @RequestParam String clinicId) {
        try {
            logger.info("Getting previous date data for patient: {}, visitNo: {}, date: {}", 
                patientId, patientVisitNo, todaysVisitDate);
            
            // Parse the date
            LocalDate visitDate = LocalDate.parse(todaysVisitDate);
            
            Map<String, Object> result = visitJpaService.getPreviousDateData(
                patientId, doctorId, patientVisitNo, visitDate, shiftId, clinicId);
            
            if (result.get("success") != null && (Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            logger.error("Error getting previous date data for patient {}: {}", patientId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get previous date data: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/all-visits/{patientId}")
    public ResponseEntity<?> getAllVisitsForPatient(@PathVariable String patientId) {
        try {
            logger.info("Getting all visits for patient: {}", patientId);
            
            Map<String, Object> result = visitJpaService.getAllVisitsForPatient(patientId);
            
            if (result.get("success") != null && (Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            logger.error("Error getting all visits for patient {}: {}", patientId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get all visits: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/previous-visits/{patientId}")
    public ResponseEntity<?> getPatientPreviousVisits(
            @PathVariable String patientId,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String clinicId,
            @RequestParam(required = false) String todaysVisitDate) {
        try {
            logger.info("Getting previous visits for patient: {}, doctor: {}, clinic: {}, today: {}", 
                patientId, doctorId, clinicId, todaysVisitDate);
            
            // Parse today's visit date, default to current date if not provided
            LocalDate todayDate;
            if (todaysVisitDate != null && !todaysVisitDate.trim().isEmpty()) {
                try {
                    todayDate = LocalDate.parse(todaysVisitDate);
                } catch (Exception e) {
                    todayDate = LocalDate.now();
                    logger.warn("Invalid date format provided: {}, using current date: {}", todaysVisitDate, todayDate);
                }
            } else {
                todayDate = LocalDate.now();
            }
            
            Map<String, Object> result = visitJpaService.getPatientPreviousVisits(
                patientId, doctorId, clinicId, todayDate);
            
            if (result.get("success") != null && (Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            logger.error("Error getting previous visits for patient {}: {}", patientId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get previous visits: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/debug-visits/{patientId}")
    public ResponseEntity<?> debugPatientVisits(@PathVariable String patientId) {
        try {
            logger.info("DEBUG: Getting all visits for patient: {}", patientId);
            
            Map<String, Object> result = visitJpaService.getAllVisitsForPatient(patientId);
            
            // Add additional debug information
            if (result.get("success") != null && (Boolean) result.get("success")) {
                List<Map<String, Object>> visits = (List<Map<String, Object>>) result.get("visits");
                if (visits != null) {
                    Map<String, Integer> statusCount = new HashMap<>();
                    Map<String, Integer> dateCount = new HashMap<>();
                    
                    for (Map<String, Object> visit : visits) {
                        // Count by status
                        Object statusId = visit.get("statusId");
                        String statusKey = statusId != null ? statusId.toString() : "null";
                        statusCount.put(statusKey, statusCount.getOrDefault(statusKey, 0) + 1);
                        
                        // Count by date
                        Object visitDate = visit.get("visitDate");
                        String dateKey = visitDate != null ? visitDate.toString() : "null";
                        dateCount.put(dateKey, dateCount.getOrDefault(dateKey, 0) + 1);
                    }
                    
                    result.put("debug", Map.of(
                        "statusBreakdown", statusCount,
                        "dateBreakdown", dateCount,
                        "totalVisits", visits.size()
                    ));
                }
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Error debugging visits for patient {}: {}", patientId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to debug visits: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }


    @GetMapping("/appointments-for-date")
    public ResponseEntity<?> getTodaysAppointmentsForGivenDate(
            @RequestParam String doctorId,
            @RequestParam String clinicId,
            @RequestParam String futureDate,
            @RequestParam(defaultValue = "1") Integer languageId) {
        try {
            System.out.println("DEBUG - API called with parameters:");
            System.out.println("  doctorId: " + doctorId);
            System.out.println("  clinicId: " + clinicId);
            System.out.println("  futureDate: " + futureDate);
            System.out.println("  languageId: " + languageId);
            System.out.println("  Target timezone: " + timezoneUtils.getTimezoneDisplayName());
            
            // Parse the future date
            java.sql.Date queryDate;
            try {
                // Handle both date and datetime formats
                String dateString = futureDate;
                if (futureDate.contains(" ")) {
                    // If it's a datetime string, extract just the date part
                    dateString = futureDate.split(" ")[0];
                    System.out.println("DEBUG - Extracted date from datetime: " + futureDate + " -> " + dateString);
                }
                queryDate = java.sql.Date.valueOf(dateString);
                System.out.println("DEBUG - Parsed date: " + queryDate);
            } catch (Exception dateParseException) {
                System.out.println("ERROR - Date parsing failed: " + dateParseException.getMessage());
                Map<String, Object> error = new HashMap<>();
                error.put("ErrorNumber", -1);
                error.put("ErrorMessage", "Invalid date format: " + futureDate + ". Expected format: YYYY-MM-DD");
                return ResponseEntity.badRequest().body(error);
            }
            
            // First, let's test basic data availability
            List<Map<String, Object>> testData = testBasicDataAvailability(doctorId, clinicId, queryDate);
            System.out.println("DEBUG - Test data count: " + testData.size());
            
            // Let's also check what times are actually in the database
            String timeCheckQuery = "SELECT DISTINCT visit_time, visit_date, TO_CHAR(visit_time::time, 'HH24:MI') as time_part FROM patient_visits WHERE doctor_id = ? AND visit_date::date = ? LIMIT 5";
            List<Map<String, Object>> timeCheck = jdbcTemplate.queryForList(timeCheckQuery, doctorId, queryDate);
            System.out.println("DEBUG - Sample visit times in database:");
            for (Map<String, Object> timeRow : timeCheck) {
                System.out.println("  Raw visit_time: " + timeRow.get("visit_time"));
                System.out.println("  Raw visit_date: " + timeRow.get("visit_date"));
                System.out.println("  Time part: " + timeRow.get("time_part"));
                
                // Test timezone conversion on this raw data
                if (timeRow.get("time_part") != null) {
                    String timeStr = timeRow.get("time_part").toString();
                    System.out.println("  Testing timezone conversion for: " + timeStr);
                    try {
                        String[] parts = timeStr.split(":");
                        if (parts.length >= 2) {
                            int hours = Integer.parseInt(parts[0]);
                            int minutes = Integer.parseInt(parts[1]);
                            java.time.LocalTime utcTime = java.time.LocalTime.of(hours, minutes);
                            java.time.LocalTime targetTime = timezoneUtils.convertUtcToTargetTimezone(utcTime);
                            System.out.println("    UTC " + utcTime + " -> " + timezoneUtils.getTimezoneDisplayName() + " " + targetTime);
                        }
                    } catch (Exception e) {
                        System.out.println("    Timezone conversion test failed: " + e.getMessage());
                    }
                }
            }
            
            // Execute 4 different queries matching the stored procedure logic
            List<Map<String, Object>> resultSet1 = getSpecificDateAppointments(doctorId, clinicId, queryDate, languageId);
            List<Map<String, Object>> resultSet2 = getFutureAppointments(clinicId, languageId);
            List<Map<String, Object>> resultSet3 = getTodayAndFutureAppointments(doctorId, clinicId, languageId);
            List<Map<String, Object>> resultSet4 = getSpecificDateAppointmentsNoDoctor(clinicId, queryDate, languageId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("resultSet1", resultSet1); // Specific date with doctor filter
            response.put("resultSet2", resultSet2); // Future appointments without doctor filter
            response.put("resultSet3", resultSet3); // Today and future with doctor filter
            response.put("resultSet4", resultSet4); // Specific date without doctor filter
            response.put("doctorId", doctorId);
            response.put("clinicId", clinicId);
            response.put("futureDate", futureDate);
            response.put("languageId", languageId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("ERROR - Exception in getTodaysAppointmentsForGivenDate: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("ErrorNumber", -1);
            error.put("ErrorMessage", "Failed to get appointments for date: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private List<Map<String, Object>> getSpecificDateAppointments(String doctorId, String clinicId, 
            java.sql.Date futureDate, Integer languageId) {
        
        try {
            // Debug logging
            System.out.println("DEBUG - getSpecificDateAppointments called with:");
            System.out.println("  doctorId: " + doctorId);
            System.out.println("  clinicId: " + clinicId);
            System.out.println("  futureDate: " + futureDate);
            System.out.println("  languageId: " + languageId);
        
            String query = "SELECT * FROM (" +
                "SELECT DISTINCT ON (PV.patient_id, PV.patient_visit_no) " +
                "TO_CHAR(PV.visit_time::time, 'HH24:MI') AS Visit_Time, " +
                "PV.visit_time AS Raw_Visit_Time, " +
                "PV.visit_date AS Full_DateTime, " +
                "PM.first_name || ' ' || PM.last_name AS Name, " +
                "PV.doctor_id, " +
                "DM.prefix || ' ' || DM.first_name || ' - ' || DM.speciality AS Doctor_Name, " +
                "PM.mobile_1 AS Mobile, " +
                "PV.patient_id, " +
                "PM.date_of_birth, " +
                "PM.age_given, " +
                "COALESCE(EXTRACT(YEAR FROM AGE(CURRENT_DATE, PM.date_of_birth)), PM.age_given, 0) AS AgeYearsIntRound, " +
                "GT.gender_description, " +
                "TO_CHAR(PV.visit_date, 'DD-MM-YYYY') AS Visit_Date, " +
                "PV.visit_time::time AS VTime, " +
                "PV.patient_visit_no, " +
                "PV.shift_id, " +
                "PV.clinic_id, " +
                "CASE WHEN TO_CHAR(PV.online_appointment_time, 'HH24:MI') = '00:00' THEN NULL " +
                "ELSE TO_CHAR(PV.online_appointment_time, 'HH24:MI') END AS \"Online_Appointment_Time\", " +
                "COALESCE(SR.status_description, 'SUBMITTED') AS status_description, " +
                "COALESCE(SR.id, PV.status_id) AS Status_ID, " +
                "TO_CHAR(PV.visit_time::time, 'HH24:MI') AS From_time, " +
                "FU.followup_description AS follow_up_type, " +
                "PV.is_submit_patient_labtest AS isSubmitPatientLabtest, " +
                "PV.created_on AS created_on, " +
                "CASE WHEN CURRENT_TIMESTAMP IS NOT NULL THEN " +
                "TO_CHAR(CURRENT_TIMESTAMP - CURRENT_TIMESTAMP, 'MI:SS') " +
                "ELSE NULL END AS Duration " +
                "FROM patient_visits PV " +
                "INNER JOIN patient_master PM ON PV.patient_id = PM.id " +
                "INNER JOIN doctor_master DM ON PV.doctor_id = DM.doctor_id " +
                "INNER JOIN gender_translations GT ON PM.gender_id = GT.gender_id AND GT.language_id = ? " +
                "LEFT JOIN status_ref SR ON PV.status_id = SR.id AND PV.clinic_id = SR.clinic_id " +
                "LEFT JOIN follow_up_type FU ON FU.id = PV.follow_up_type " +
                "WHERE PV.delete_flag = false " +
                "AND PV.doctor_id = ? " +
                "AND CAST(PV.visit_date AT TIME ZONE 'UTC' AT TIME ZONE ? AS date) = ? " +
                "AND PV.status_id NOT IN (11, 12) " +
                "ORDER BY PV.patient_id, PV.patient_visit_no, PV.status_id ASC, PV.created_on ASC, PV.visit_time ASC" +
                ") AS distinct_appointments " +
                "ORDER BY Status_ID ASC, created_on ASC, Visit_Time ASC";
        
        System.out.println("DEBUG - Query: " + query);
        System.out.println("DEBUG - Timezone for date conversion: " + timezoneUtils.getTimezoneDisplayName());
        
            // Get timezone ID for PostgreSQL AT TIME ZONE conversion
            String timezoneId = timezoneUtils.getTimezoneDisplayName();
            // Parameter order: languageId (JOIN), doctorId, timezoneId, futureDate
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, languageId, doctorId, timezoneId, futureDate);
            System.out.println("DEBUG - Result count: " + result.size());
            
            // Debug the time values being returned and convert timezone
            for (int i = 0; i < Math.min(result.size(), 3); i++) {
                Map<String, Object> row = result.get(i);
                System.out.println("DEBUG - Row " + i + " BEFORE conversion:");
                System.out.println("  Visit_Time: " + row.get("Visit_Time"));
                System.out.println("  Raw_Visit_Time: " + row.get("Raw_Visit_Time"));
                System.out.println("  Full_DateTime: " + row.get("Full_DateTime"));
                System.out.println("  VTime: " + row.get("VTime"));
                System.out.println("  From_time: " + row.get("From_time"));
                System.out.println("  full_time: " + row.get("full_time"));
                System.out.println("  Online_Appointment_Time: " + row.get("Online_Appointment_Time"));
                System.out.println("  online_appointment_time: " + row.get("online_appointment_time"));
                
                // Convert timezone in Java if needed
                convertTimezoneInRow(row);
                
                System.out.println("DEBUG - Row " + i + " AFTER conversion:");
                System.out.println("  Visit_Time: " + row.get("Visit_Time"));
                System.out.println("  Online_Appointment_Time: " + row.get("Online_Appointment_Time"));
            }
            
            // Convert timezone for all remaining rows
            for (int i = 3; i < result.size(); i++) {
                Map<String, Object> row = result.get(i);
                convertTimezoneInRow(row);
            }
            
            return result;
        } catch (Exception e) {
            System.out.println("ERROR - getSpecificDateAppointments failed: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    private List<Map<String, Object>> getFutureAppointments(String clinicId, Integer languageId) {
        try {
            String query = "SELECT " +
                "TO_CHAR(PV.visit_time::time, 'HH24:MI') AS Visit_Time, " +
                "PV.visit_time AS Raw_Visit_Time, " +
                "PV.visit_date AS Full_DateTime, " +
                "PM.first_name || ' ' || PM.last_name AS Name, " +
                "PV.doctor_id, " +
                "DM.prefix || ' ' || DM.first_name AS Doctor_Name, " +
                "PM.mobile_1 AS Mobile, " +
                "PV.patient_id, " +
                "PM.date_of_birth, " +
                "PM.age_given, " +
                "COALESCE(EXTRACT(YEAR FROM AGE(CURRENT_DATE, PM.date_of_birth)), PM.age_given, 0) AS AgeYearsIntRound, " +
                "GT.gender_description, " +
                "TO_CHAR(PV.visit_date, 'DD-MM-YYYY') as visit_date, " +
                "PV.visit_time::time AS VTime, " +
                "PV.patient_visit_no, " +
                "PV.shift_id, " +
                "PV.clinic_id, " +
                "CASE WHEN TO_CHAR(PV.online_appointment_time, 'HH24:MI') = '00:00' THEN NULL " +
                "ELSE TO_CHAR(PV.online_appointment_time, 'HH24:MI') END AS \"Online_Appointment_Time\", " +
                "COALESCE(SR.status_description, 'SUBMITTED') AS status_description, " +
                "COALESCE(SR.id, PV.status_id) AS Status_ID, " +
                "TO_CHAR(PV.visit_time::time, 'HH24:MI') AS From_time, " +
                "FU.followup_description AS follow_up_type, " +
                "PV.is_submit_patient_labtest AS isSubmitPatientLabtest, " +
                "COALESCE(PV.is_submit_patient_visit_details, false) AS Is_Submit_Patient_Visit_Details " +
                "FROM patient_visits PV " +
                "INNER JOIN patient_master PM ON PV.patient_id = PM.id " +
                "INNER JOIN doctor_master DM ON PV.doctor_id = DM.doctor_id " +
                "INNER JOIN gender_translations GT ON PM.gender_id = GT.gender_id " +
                "LEFT JOIN status_ref SR ON PV.status_id = SR.id AND PV.clinic_id = SR.clinic_id " +
                "LEFT JOIN follow_up_type FU ON FU.id = PV.follow_up_type " +
                "WHERE PV.delete_flag = false " +
                "AND PV.visit_date > CURRENT_DATE " +
                "AND PV.status_id NOT IN (11, 12) " +
                "AND GT.language_id = ? " +
                "ORDER BY PV.visit_time ASC";
        
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, languageId);
            
            // Convert timezone for all rows
            for (Map<String, Object> row : result) {
                convertTimezoneInRow(row);
            }
            
            return result;
        } catch (Exception e) {
            System.out.println("ERROR - getFutureAppointments failed: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    private List<Map<String, Object>> getTodayAndFutureAppointments(String doctorId, String clinicId, Integer languageId) {
        try {
            String query = "SELECT " +
                "TO_CHAR(PV.visit_time::time, 'HH24:MI') AS Visit_Time, " +
                "PV.visit_time AS Raw_Visit_Time, " +
                "PV.visit_date AS Full_DateTime, " +
                "PM.first_name || ' ' || PM.last_name AS Name, " +
                "PV.doctor_id, " +
                "DM.prefix || ' ' || DM.first_name || ' - ' || DM.speciality AS Doctor_Name, " +
                "PM.mobile_1 AS Mobile, " +
                "PV.patient_id, " +
                "PM.date_of_birth, " +
                "PM.age_given, " +
                "COALESCE(EXTRACT(YEAR FROM AGE(CURRENT_DATE, PM.date_of_birth)), PM.age_given, 0) AS AgeYearsIntRound, " +
                "GT.gender_description, " +
                "TO_CHAR(PV.visit_date, 'DD-MM-YYYY') AS Visit_Date, " +
                "PV.visit_time::time AS VTime, " +
                "PV.patient_visit_no, " +
                "PV.shift_id, " +
                "PV.clinic_id, " +
                "CASE WHEN TO_CHAR(PV.online_appointment_time, 'HH24:MI') = '00:00' THEN NULL " +
                "ELSE TO_CHAR(PV.online_appointment_time, 'HH24:MI') END AS \"Online_Appointment_Time\", " +
                "COALESCE(SR.status_description, 'SUBMITTED') AS status_description, " +
                "COALESCE(SR.id, PV.status_id) AS Status_ID, " +
                "TO_CHAR(PV.visit_time::time, 'HH24:MI') AS From_time, " +
                "PV.visit_date as fulldate, " +
                "PV.visit_time::time as full_time, " +
                "FU.followup_description AS follow_up_type, " +
                "PV.is_submit_patient_labtest AS isSubmitPatientLabtest, " +
                "CASE WHEN CURRENT_TIMESTAMP IS NOT NULL THEN " +
                "TO_CHAR(CURRENT_TIMESTAMP - CURRENT_TIMESTAMP, 'MI:SS') " +
                "ELSE NULL END AS Duration " +
                "FROM patient_visits PV " +
                "INNER JOIN patient_master PM ON PV.patient_id = PM.id " +
                "INNER JOIN doctor_master DM ON PV.doctor_id = DM.doctor_id " +
                "INNER JOIN gender_translations GT ON PM.gender_id = GT.gender_id " +
                "LEFT JOIN status_ref SR ON PV.status_id = SR.id AND PV.clinic_id = SR.clinic_id " +
                "LEFT JOIN follow_up_type FU ON FU.id = PV.follow_up_type " +
                "WHERE PV.delete_flag = false " +
                "AND PV.doctor_id = ? " +
                "AND PV.visit_date >= CURRENT_DATE " +
                "AND PV.status_id NOT IN (4, 11, 12) " +
                "AND GT.language_id = ? " +
                "ORDER BY PV.visit_date ASC, PV.visit_time ASC";
        
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, doctorId, languageId);
            
            // Convert timezone for all rows
            for (Map<String, Object> row : result) {
                convertTimezoneInRow(row);
            }
            
            return result;
        } catch (Exception e) {
            System.out.println("ERROR - getTodayAndFutureAppointments failed: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    private List<Map<String, Object>> getSpecificDateAppointmentsNoDoctor(String clinicId, 
            java.sql.Date futureDate, Integer languageId) {
        try {
            String query = "SELECT " +
                "TO_CHAR(PV.visit_time::time, 'HH24:MI') AS Visit_Time, " +
                "PV.visit_time AS Raw_Visit_Time, " +
                "PV.visit_date AS Full_DateTime, " +
                "PM.first_name || ' ' || PM.last_name AS Name, " +
                "PV.doctor_id, " +
                "DM.prefix || ' ' || DM.first_name || ' - ' || DM.speciality AS Doctor_Name, " +
                "PM.mobile_1 AS Mobile, " +
                "PV.patient_id, " +
                "PM.date_of_birth, " +
                "PM.age_given, " +
                "COALESCE(EXTRACT(YEAR FROM AGE(CURRENT_DATE, PM.date_of_birth)), PM.age_given, 0) AS AgeYearsIntRound, " +
                "GT.gender_description, " +
                "TO_CHAR(PV.visit_date, 'DD-MM-YYYY') AS Visit_Date, " +
                "PV.visit_time::time AS VTime, " +
                "PV.patient_visit_no, " +
                "PV.shift_id, " +
                "PV.clinic_id, " +
                "CASE WHEN TO_CHAR(PV.online_appointment_time, 'HH24:MI') = '00:00' THEN NULL " +
                "ELSE TO_CHAR(PV.online_appointment_time, 'HH24:MI') END AS \"Online_Appointment_Time\", " +
                "COALESCE(SR.status_description, 'SUBMITTED') AS status_description, " +
                "COALESCE(SR.id, PV.status_id) AS Status_ID, " +
                "TO_CHAR(PV.visit_time::time, 'HH24:MI') AS From_time, " +
                "PV.visit_date as fulldate, " +
                "PV.visit_time::time as full_time, " +
                "FU.followup_description AS follow_up_type, " +
                "PV.is_submit_patient_labtest AS isSubmitPatientLabtest, " +
                "CASE WHEN CURRENT_TIMESTAMP IS NOT NULL THEN " +
                "TO_CHAR(CURRENT_TIMESTAMP - CURRENT_TIMESTAMP, 'MI:SS') " +
                "ELSE NULL END AS Duration " +
                "FROM patient_visits PV " +
                "INNER JOIN patient_master PM ON PV.patient_id = PM.id " +
                "INNER JOIN doctor_master DM ON PV.doctor_id = DM.doctor_id " +
                "INNER JOIN gender_translations GT ON PM.gender_id = GT.gender_id " +
                "LEFT JOIN status_ref SR ON PV.status_id = SR.id AND PV.clinic_id = SR.clinic_id " +
                "LEFT JOIN follow_up_type FU ON FU.id = PV.follow_up_type " +
                "WHERE PV.delete_flag = false " +
                "AND PV.visit_date::date = ? " +
                "AND PV.status_id NOT IN (11, 12) " +
                "AND GT.language_id = ? " +
                "ORDER BY PV.visit_date ASC, PV.visit_time ASC";
        
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, futureDate, languageId);
            
            // Convert timezone for all rows
            for (Map<String, Object> row : result) {
                convertTimezoneInRow(row);
            }
            
            return result;
        } catch (Exception e) {
            System.out.println("ERROR - getSpecificDateAppointmentsNoDoctor failed: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    private List<Map<String, Object>> testBasicDataAvailability(String doctorId, String clinicId, java.sql.Date queryDate) {
        // Simple test query to check if basic data exists
        String testQuery = "SELECT " +
                "PV.patient_id, " +
                "PV.doctor_id, " +
                "PV.visit_date, " +
                "PV.visit_time, " +
                "PV.status_id, " +
                "PV.delete_flag, " +
                "PM.first_name, " +
                "PM.last_name, " +
                "PM.date_of_birth, " +
                "PM.age_given, " +
                "EXTRACT(YEAR FROM AGE(CURRENT_DATE, PM.date_of_birth)) AS calculated_age " +
                "FROM patient_visits PV " +
                "LEFT JOIN patient_master PM ON PV.patient_id = PM.id " +
                "WHERE PV.doctor_id = ? " +
                "AND PV.visit_date::date = ? " +
                "LIMIT 5";
        
        System.out.println("DEBUG - Test query: " + testQuery);
        System.out.println("DEBUG - Test parameters: doctorId=" + doctorId + ", queryDate=" + queryDate);
        
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(testQuery, doctorId, queryDate);
            
            System.out.println("DEBUG - Test query result count: " + result.size());
            
            // Debug each result
            for (Map<String, Object> row : result) {
                System.out.println("DEBUG - Row data:");
                System.out.println("  patient_id: " + row.get("patient_id"));
                System.out.println("  date_of_birth: " + row.get("date_of_birth"));
                System.out.println("  age_given: " + row.get("age_given"));
                System.out.println("  calculated_age: " + row.get("calculated_age"));
            }
            
            return result;
        } catch (Exception e) {
            System.out.println("ERROR - testBasicDataAvailability failed: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Save medicine and prescription data to overwrite tables and update visit payment details
     * This endpoint replicates the functionality of USP_Insert_MedicineDataOverwrite stored procedure
     * Used by the Receive Payment submit functionality
     * 
     * @param request - Request body containing medicine and prescription data
     * @return ResponseEntity with success status and message
     */
    @PostMapping("/save-medicine-overwrite")
    public ResponseEntity<?> saveMedicineOverwrite(@RequestBody SaveMedicineOverwriteRequest request) {
        try {
            // Parse visit date from string
            LocalDateTime visitDate = parseDateTime(request.visitDate());
            
            // Call service method
            Map<String, Object> result = visitJpaService.saveMedicineOverwrite(
                visitDate,
                request.patientVisitNo(),
                request.shiftId(),
                request.clinicId(),
                request.doctorId(),
                request.patientId(),
                request.medicineRows(),
                request.prescriptionRows(),
                request.feesToCollect(),
                request.feesCollected(),
                request.userId(),
                request.statusId(),
                request.bloodPressure(),
                request.allergyDetails(),
                request.habitDetails(),
                request.comment(),
                request.paymentById(),
                request.paymentRemark(),
                request.discount(),
                request.reason()
            );
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            logger.error("Error in saveMedicineOverwrite endpoint", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error processing request: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Update addendum for a patient visit
     * This endpoint replicates the functionality of USP_Update_Addendum stored procedure
     * 
     * @param request - Request body containing addendum text and visit identifiers
     * @return ResponseEntity with success status and message
     */
    @PostMapping("/update-addendum")
    public ResponseEntity<?> updateAddendum(@RequestBody UpdateAddendumRequest request) {
        try {
            logger.info("Received update addendum request for patient: {}, visitNo: {}", 
                request.patientId(), request.patientVisitNo());
            
            // Validate required fields
            if (request.patientId() == null || request.patientId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Patient ID is required"
                ));
            }
            if (request.visitDate() == null || request.visitDate().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Visit date is required"
                ));
            }
            if (request.patientVisitNo() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Patient visit number is required"
                ));
            }
            if (request.userId() == null || request.userId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "User ID is required"
                ));
            }
            
            // Create service request
            VisitJpaService.UpdateAddendumRequest serviceRequest = 
                new VisitJpaService.UpdateAddendumRequest(
                    request.addendum(),
                    request.visitDate(),
                    request.patientId(),
                    request.patientVisitNo(),
                    request.userId()
                );
            
            // Call service method
            Map<String, Object> result = visitJpaService.updateAddendum(serviceRequest);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            logger.error("Error in updateAddendum endpoint", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error processing request: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
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
        String userId
    ) {}
    
    /**
     * Exception handler for JSON parsing errors
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleJsonParseError(HttpMessageNotReadableException ex) {
        logger.error("JSON parsing error: {}", ex.getMessage(), ex);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Invalid JSON format. Please check your request body.");
        error.put("errorType", "JSON_PARSING_ERROR");
        
        String details = ex.getMessage() != null ? ex.getMessage() : "Unknown JSON parsing error";
        error.put("details", details);
        
        // Add helpful hints for common JSON errors
        String hints = "";
        if (details.contains("trailing comma") || details.contains("Unexpected character") && details.contains("}")) {
            hints = "Common issue: Trailing comma before closing brace '}' or bracket ']'. Remove any comma before the last item in arrays or objects.";
        } else if (details.contains("Unterminated string")) {
            hints = "Common issue: Missing closing quote in a string value.";
        } else if (details.contains("Expected a value")) {
            hints = "Common issue: Missing value after a colon ':' or trailing comma.";
        }
        if (!hints.isEmpty()) {
            error.put("hint", hints);
        }
        
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Exception handler for validation errors (from @NotBlank, etc.)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationError(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage(), ex);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Validation failed. Please check your request data.");
        error.put("errorType", "VALIDATION_ERROR");
        
        // Collect all field errors
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        error.put("fieldErrors", fieldErrors);
        error.put("details", ex.getMessage());
        
        return ResponseEntity.badRequest().body(error);
    }
}
