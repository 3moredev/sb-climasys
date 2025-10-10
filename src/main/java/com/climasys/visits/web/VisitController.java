package com.climasys.visits.web;

import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.*;

import com.climasys.utils.TimezoneUtils;
import com.climasys.utils.CorsUtils;
import com.climasys.visits.service.VisitJpaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    
    // Convert timezone in Java for time fields
    private void convertTimezoneInRow(Map<String, Object> row) {
        try {
            Object rawVisitTime = row.get("Raw_Visit_Time");
            System.out.println("DEBUG - Raw_Visit_Time: " + rawVisitTime + " (type: " + (rawVisitTime != null ? rawVisitTime.getClass().getSimpleName() : "null") + ")");
            
            if (rawVisitTime != null) {
                String timeStr = rawVisitTime.toString();
                System.out.println("DEBUG - Processing time string: " + timeStr);
                
                // Parse the time string (format: HH:MM:SS or HH:MM)
                if (timeStr.matches("\\d{2}:\\d{2}(:\\d{2})?")) {
                    String[] parts = timeStr.split(":");
                    int hours = Integer.parseInt(parts[0]);
                    int minutes = Integer.parseInt(parts[1]);
                    
                    System.out.println("DEBUG - Parsed time: " + hours + ":" + minutes);
                    
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
                    System.out.println("DEBUG - Time format doesn't match expected pattern: " + timeStr);
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
            Short statusId,
            String userId,
            Boolean isSubmitPatientVisitDetails
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
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get today's visits: " + e.getMessage());
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
            
            // Additional required fields with defaults
            parameters.put("p_var_Instructions", "");
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
    public ResponseEntity<?> saveComprehensiveVisitDataJpa(@RequestBody ComprehensiveVisitDataRequest req) {
        try {
            // Validate and parse required fields
            if (req.shiftId() == null || req.shiftId().trim().isEmpty()) {
                throw new IllegalArgumentException("Shift ID is required");
            }
            Short shiftId = Short.parseShort(req.shiftId());
            
            if (req.patientVisitNo() == null || req.patientVisitNo().trim().isEmpty()) {
                throw new IllegalArgumentException("Patient Visit Number is required");
            }
            Integer patientVisitNo = Integer.parseInt(req.patientVisitNo());
            
            if (req.visitDate() == null || req.visitDate().trim().isEmpty()) {
                throw new IllegalArgumentException("Visit Date is required");
            }
            LocalDateTime visitDate = parseDateTime(req.visitDate());
            
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
            if (req.userId() == null || req.userId().trim().isEmpty()) {
                throw new IllegalArgumentException("User ID is required");
            }
            if (req.discount() == null) {
                throw new IllegalArgumentException("Discount is required");
            }
            
            // Create service request
            VisitJpaService.ComprehensiveVisitRequest serviceRequest = 
                new VisitJpaService.ComprehensiveVisitRequest(
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
                    req.statusId(),
                    req.userId(),
                    req.isSubmitPatientVisitDetails(),
                    
                    // Treatment fields (not in current request, set to null)
                    null, // treatmentComment
                    null, // treatmentPlan
                    null, // plan
                    null, // notes
                    null, // impressionFinding
                    null, // additionalInstructions
                    
                    // Referral fields
                    req.referBy(), // referId
                    req.referralName(), // referDoctorName
                    req.referralAddress(),
                    req.referralContact(),
                    req.referralEmail(),
                    
                    // Additional fields (not in current request, set to defaults)
                    "", // instructions
                    "", // offlineReason
                    false // offlineFlag
                );
            
            // Save using JPA service
            Map<String, Object> result = visitJpaService.saveComprehensiveVisit(serviceRequest);
            
            if (result.get("success") != null && (Boolean) result.get("success")) {
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
     * Get comprehensive patient appointment details matching USP_Get_PatientAppointmentDetailsNew stored procedure
     * 
     * @param patientId Patient ID
     * @param shiftId Shift ID
     * @param clinicId Clinic ID
     * @param doctorId Doctor ID
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
            // Check if patient visit exists with comprehensive data
            String existsCheckSql = buildExistsCheckQuery();
            
            List<Map<String, Object>> existsResult = jdbcTemplate.queryForList(existsCheckSql,
                    patientId, clinicId, patientVisitNo, languageId);
            
            List<Map<String, Object>> mainResult;
            List<Map<String, Object>> additionalResult;
            
            if (!existsResult.isEmpty()) {
                // Patient visit exists - get comprehensive data with Payment_type_Master join
                mainResult = getComprehensiveVisitData(patientId, clinicId, patientVisitNo, languageId, true);
                additionalResult = getAdditionalVisitData(patientId, clinicId, shiftId, patientVisitNo);
            } else {
                // Patient visit doesn't exist - get basic data without Payment_type_Master join
                mainResult = getComprehensiveVisitData(patientId, clinicId, patientVisitNo, languageId, false);
                additionalResult = getAdditionalVisitData(patientId, clinicId, shiftId, patientVisitNo);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mainData", mainResult);
            response.put("additionalData", additionalResult);
            response.put("patientId", patientId);
            response.put("clinicId", clinicId);
            response.put("visitNo", patientVisitNo);
            response.put("languageId", languageId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("ErrorNumber", -1);
            error.put("ErrorMessage", "Failed to get patient appointment details: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private String buildExistsCheckQuery() {
        return "SELECT 1 FROM Patient_Visits PV " +
                "INNER JOIN Patient_Master PM ON pv.Patient_ID = PM.ID " +
                "INNER JOIN Gender_Translations GT ON PM.Gender_ID = GT.Gender_ID " +
                "LEFT JOIN Follow_Up_type FUT ON PV.follow_up_type = FUT.ID " +
                "LEFT JOIN Refer_By_Translations RT ON PM.Refer_ID = RT.Refer_ID " +
                "LEFT JOIN Doctor_Master DMS ON DMS.Doctor_ID = PV.Doctor_ID " +
                "LEFT JOIN Followup_After_Master FAM ON FAM.ID = PV.Followup_After " +
                "INNER JOIN Payment_type_Master PTM ON PTM.ID = PV.payment_by_ID " +
                "LEFT JOIN Patient_Receipts rs ON pv.Receipt_Number = rs.Receipt_Number " +
                "WHERE PV.Patient_ID = ? " +
                "AND PV.Clinic_ID = ? " +
                "AND PV.Patient_Visit_No = ? " +
                "AND PV.Delete_Flag = 0 " +
                "AND GT.Language_Id = ?";
    }

    private List<Map<String, Object>> getComprehensiveVisitData(String patientId, String clinicId, 
            Integer patientVisitNo, Integer languageId, boolean includePaymentMaster) {
        
        String mainQuery = buildMainQuery(includePaymentMaster);
        
        return jdbcTemplate.queryForList(mainQuery, patientId, clinicId, patientVisitNo, languageId);
    }

    private String buildMainQuery(boolean includePaymentMaster) {
        StringBuilder query = new StringBuilder();
        
        query.append("SELECT ")
                .append("PM.First_Name || ' ' || COALESCE(PM.Middle_Name, '') || ' ' || PM.Last_Name as Name, ")
                .append("PM.First_Name || ' ' || PM.Last_Name as Partial_Name, ")
                .append("PM.Age_Given, ")
                .append("PM.Date_Of_Birth, ")
                .append("PV.Folder_No, ")
                .append("PV.Visit_Date, ")
                .append("PV.Weight_IN_KGS, ")
                .append("PV.Height_In_CMS, ")
                .append("PV.Pulse, ")
                .append("PV.Blood_Pressure, ")
                .append("COALESCE(PV.Diabetes, false) AS Diabetes, ")
                .append("COALESCE(PV.Cholestrol, false) AS Cholestrol, ")
                .append("PV.Fees_To_Collect, ")
                .append("PV.Instructions, ")
                .append("PV.Folder_No, ")
                .append("PV.Financial_Year, ")
                .append("PV.Patient_Visit_No, ")
                .append("PV.Status_ID, ")
                .append("PV.Instructions, ")
                .append("PV.Observation, ")
                .append("PV.Fees_Collected, ")
                .append("PV.discount, ")
                .append("PV.Original_discount, ")
                .append("PV.Comment, ")
                .append("PM.First_Name || ' ' || PM.Last_Name as FirstLastName, ")
                .append("PV.Sugar, ")
                .append("PV.THtext, ")
                .append("COALESCE(PV.In_Person, false) AS In_Person, ")
                .append("COALESCE(PV.On_Call_Status, false) AS On_Call_Status, ")
                .append("PV.Impression, ")
                .append("PM.Gender_ID, ")
                .append("GT.Gender_Description, ")
                .append("EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - PM.Date_Of_Birth)) / 31557600.0 AS AgeYearsDecimal, ")
                .append("ROUND(EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - PM.Date_Of_Birth)) / 31557600.0)::INT AS AgeYearsIntRound, ")
                .append("FLOOR(EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - PM.Date_Of_Birth)) / 31557600.0)::INT AS AgeYearsIntTrunc, ")
                .append("PM.Mobile_1, ")
                .append("PV.Weight_IN_KGS, ")
                .append("CASE WHEN POSITION(':' IN PV.Refer_Doctor_Details) > 0 THEN ")
                .append("SUBSTRING(PV.Refer_Doctor_Details FROM POSITION(':' IN PV.Refer_Doctor_Details) + 1) ")
                .append("ELSE PV.Refer_Doctor_Details END AS Refer_Doctor_Details, ")
                .append("PV.Refer_ID, ")
                .append("RT.Refer_By_Description, ")
                .append("RT.Refer_By_Description || ' -- ' || ")
                .append("CASE WHEN POSITION(':' IN PV.Refer_Doctor_Details) > 0 THEN ")
                .append("SUBSTRING(PV.Refer_Doctor_Details FROM POSITION(':' IN PV.Refer_Doctor_Details) + 1) ")
                .append("ELSE PV.Refer_Doctor_Details END AS REFERDETAILS, ")
                .append("PV.Payment_By_ID, ")
                .append("PV.Payment_Remark, ")
                .append("COALESCE(PV.follow_up, '') AS follow_up, ")
                .append("COALESCE(PV.Current_Medicines, '') AS Current_Medicines, ")
                .append("COALESCE(PV.Visit_Comments, '') AS Visit_Comments, ")
                .append("COALESCE(PV.Current_Complaints, '') AS Current_Complaints, ")
                .append("COALESCE(PV.Is_follow_Up, false) AS Is_follow_Up, ")
                .append("COALESCE(PV.Is_Submit_Patient_Visit_Details, false) AS Is_Submit_Patient_Visit_Details, ")
                .append("COALESCE(PV.TPR, '') AS TPR, ")
                .append("PV.Important_Findings, ")
                .append("PV.Additional_Comments, ")
                .append("PV.Systemic, ")
                .append("PV.Odeama, ")
                .append("PV.Pallor, ")
                .append("COALESCE(PV.IS_Submit_Gynec_Details, false) AS IS_Submit_Gynec_Details, ")
                .append("COALESCE(PV.GC, '') AS GC, ")
                .append("COALESCE(PV.FMP, '') AS FMP, ")
                .append("COALESCE(PV.PRMC, '') AS PRMC, ")
                .append("COALESCE(PV.PAMC, '') AS PAMC, ")
                .append("COALESCE(PV.LMP, '') AS LMP, ")
                .append("COALESCE(PV.Obstetrics_History, '') AS Obstetrics_History, ")
                .append("COALESCE(PV.Surgical_History_Past_History, '') AS Surgical_History_Past_History, ")
                .append("COALESCE(PV.Gynec_Additional_Comments, '') AS Gynec_Additional_Comments, ")
                .append("COALESCE(PV.follow_up_type, 0) AS follow_up_type, ")
                .append("FUT.FollowUp_Description AS FollowUp_Description, ")
                .append("CASE WHEN PV.Follow_Up_Date IS NULL THEN '' ")
                .append("ELSE REPLACE(TO_CHAR(PV.Follow_Up_Date, 'DD Mon YYYY'), ' ', '-') END AS Follow_Up_Date, ")
                .append("CASE WHEN PV.EDD IS NULL THEN 'NULL' ")
                .append("ELSE REPLACE(TO_CHAR(PV.EDD, 'DD Mon YYYY'), ' ', '-') END AS EDD, ")
                .append("PV.plan, ")
                .append("PV.Notes, ")
                .append("PV.follow_up_Comment, ")
                .append("PV.Treatment_comment, ")
                .append("PV.Treatment_plan, ")
                .append("PV.In_Person as Person, ")
                .append("PV.Doctor_ID, ")
                .append("DMS.Prefix || DMS.First_Name || ' - ' || DMS.Speciality AS DOCTOR_NAME, ")
                .append("COALESCE(FUT.FollowUp_Description, '0') || ' - ' || ")
                .append("CASE WHEN PV.Follow_Up_Date IS NULL THEN '' ")
                .append("ELSE REPLACE(TO_CHAR(PV.Follow_Up_Date, 'DD Mon YYYY'), ' ', '-') END AS Folloupdateprint, ")
                .append("COALESCE(FAM.Followup_After, 0) AS Followup_After, ")
                .append("COALESCE(PV.Schedule, 0) AS Schedule, ")
                .append("PV.Additional_Instructions, ")
                .append("FAM.Days as followuP_after_Days, ")
                .append("PV.Followup_After as followupafter_Id, ")
                .append("PV.Visit_Date, ")
                .append("PV.Treatment_comment, ")
                .append("PV.Treatment_plan, ")
                .append("PV.Impression_Finding, ")
                .append("PV.follow_up, ")
                .append("PV.Complaints_by_Patient_per_visit, ")
                .append("PV.Receipt_Number, ")
                .append("rs.Receipt_Date, ")
                .append("rs.Receipt_Amount, ")
                .append("CASE WHEN TO_CHAR(PV.Online_Appointment_Time, 'HH24:MI') = '00:00' THEN NULL ")
                .append("ELSE TO_CHAR(PV.Online_Appointment_Time, 'HH24:MI') END AS Online_Appointment_Time, ")
                .append("PV.Doctor_Address, ")
                .append("PV.Doctor_Mobile, ")
                .append("PV.Doctor_Email ");
        
        if (includePaymentMaster) {
            query.append(", PTM.Payment_Description ");
        }
        
        query.append("FROM Patient_Visits PV ")
                .append("INNER JOIN Patient_Master PM ON pv.Patient_ID = PM.ID ")
                .append("INNER JOIN Gender_Translations GT ON PM.Gender_ID = GT.Gender_ID ")
                .append("LEFT JOIN Follow_Up_type FUT ON PV.follow_up_type = FUT.ID ")
                .append("LEFT JOIN Refer_By_Translations RT ON PM.Refer_ID = RT.Refer_ID ")
                .append("LEFT JOIN Doctor_Master DMS ON DMS.Doctor_ID = PV.Doctor_ID ")
                .append("LEFT JOIN Followup_After_Master FAM ON FAM.ID = PV.Followup_After ");
        
        if (includePaymentMaster) {
            query.append("INNER JOIN Payment_type_Master PTM ON PTM.ID = PV.payment_by_ID ");
        }
        
        query.append("LEFT JOIN Patient_Receipts rs ON pv.Receipt_Number = rs.Receipt_Number ")
                .append("WHERE PV.Patient_ID = ? ")
                .append("AND PV.Clinic_ID = ? ")
                .append("AND PV.Patient_Visit_No = ? ")
                .append("AND PV.Delete_Flag = 0 ")
                .append("AND GT.Language_Id = ?");
        
        return query.toString();
    }

    private List<Map<String, Object>> getAdditionalVisitData(String patientId, String clinicId, 
            Short shiftId, Integer patientVisitNo) {
        
        String additionalQuery = "SELECT " +
                "PV.Weight_IN_KGS, " +
                "PV.Height_In_CMS, " +
                "PV.Pulse, " +
                "PV.Blood_Pressure, " +
                "COALESCE(PV.Asthama, false) AS Asthama, " +
                "COALESCE(PV.Hypertension, false) AS Hypertension, " +
                "COALESCE(PV.Diabetes, false) AS Diabetes, " +
                "COALESCE(PV.Cholestrol, false) AS Cholestrol, " +
                "COALESCE(PV.IHD, false) AS IHD, " +
                "COALESCE(PV.TH, false) AS TH, " +
                "PV.Instructions, " +
                "PV.Fees_To_Collect, " +
                "PV.Instructions, " +
                "PV.Patient_Visit_No, " +
                "PV.Status_ID, " +
                "COALESCE(PV.Smoking, false) AS Smoking, " +
                "COALESCE(PV.Tobaco, false) AS Tobaco, " +
                "COALESCE(PV.Alchohol, false) AS Alchohol, " +
                "COALESCE(PV.Pregnant, false) AS Pregnant, " +
                "COALESCE(PV.Discount, 0) AS Discount, " +
                "PV.Habits_Comments, " +
                "PV.Allergy_dtls, " +
                "PV.Instructions, " +
                "PV.Observation, " +
                "PV.Original_Billed_Amount, " +
                "PV.Symptom_Comment, " +
                "PV.On_Call_Status, " +
                "PV.Fees_Collected, " +
                "PV.Comment, " +
                "PV.discount, " +
                "PV.Original_discount, " +
                "COALESCE(PV.Impression, '') AS Impression, " +
                "COALESCE(PV.Payment_By_ID, 0) AS Payment_By_ID, " +
                "PV.Payment_Remark, " +
                "PTM.Payment_Description, " +
                "COALESCE(PV.follow_up, '') AS follow_up, " +
                "COALESCE(PV.follow_up_type, 0) AS follow_up_type, " +
                "FUT.FollowUp_Description AS FollowUp_Description, " +
                "CASE WHEN PV.Follow_Up_Date IS NULL THEN '' " +
                "ELSE REPLACE(TO_CHAR(PV.Follow_Up_Date, 'DD Mon YYYY'), ' ', '-') END AS Follow_Up_Date, " +
                "PV.plan, " +
                "PV.Notes, " +
                "PV.follow_up_Comment, " +
                "PV.Treatment_comment, " +
                "PV.Treatment_plan, " +
                "PV.In_Person as Person, " +
                "COALESCE(FUT.FollowUp_Description, '0') || ' - ' || " +
                "CASE WHEN PV.Follow_Up_Date IS NULL THEN '' " +
                "ELSE REPLACE(TO_CHAR(PV.Follow_Up_Date, 'DD Mon YYYY'), ' ', '-') END AS Folloupdateprint, " +
                "COALESCE(FAM.Followup_After, 0) AS Followup_After, " +
                "COALESCE(PV.Schedule, 0) AS Schedule, " +
                "PV.Additional_Instructions, " +
                "FAM.Days as followuP_after_Days, " +
                "PV.Visit_Date, " +
                "PV.Followup_After as followupafter_Id, " +
                "PV.Treatment_comment, " +
                "PV.Treatment_plan, " +
                "PV.Impression_Finding, " +
                "PV.follow_up, " +
                "PV.Receipt_Number, " +
                "rs.Receipt_Date, " +
                "rs.Receipt_Amount, " +
                "CASE WHEN TO_CHAR(PV.Online_Appointment_Time, 'HH24:MI') = '00:00' THEN NULL " +
                "ELSE TO_CHAR(PV.Online_Appointment_Time, 'HH24:MI') END AS Online_Appointment_Time, " +
                "PV.Refer_ID, " +
                "CASE WHEN POSITION(':' IN PV.Refer_Doctor_Details) > 0 THEN " +
                "SUBSTRING(PV.Refer_Doctor_Details FROM POSITION(':' IN PV.Refer_Doctor_Details) + 1) " +
                "ELSE PV.Refer_Doctor_Details END AS Refer_Doctor_Details, " +
                "PV.Doctor_Address, " +
                "PV.Doctor_Mobile, " +
                "PV.Doctor_Email " +
                "FROM Patient_Visits PV " +
                "INNER JOIN Patient_Master PM ON pv.Patient_ID = PM.ID " +
                "LEFT JOIN Payment_type_Master PTM ON pv.Payment_By_ID = PTM.ID " +
                "LEFT JOIN Follow_Up_type FUT ON pv.follow_up_type = FUT.ID " +
                "LEFT JOIN Followup_After_Master FAM ON FAM.ID = PV.Followup_After " +
                "LEFT JOIN Patient_Receipts rs ON pv.Receipt_Number = rs.Receipt_Number " +
                "WHERE PV.Patient_ID = ? " +
                "AND PV.Clinic_ID = ? " +
                "AND PV.Doctor_ID = ? " +
                "AND PV.Shift_Id = ? " +
                "AND PV.Patient_Visit_No = ? " +
                "AND PV.Delete_Flag = 0";
        
        return jdbcTemplate.queryForList(additionalQuery, patientId, clinicId, 
                "DEFAULT_DOCTOR", shiftId, patientVisitNo);
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
                "SR.status_description, " +
                "SR.id AS Status_ID, " +
                "TO_CHAR(PV.visit_time::time, 'HH24:MI') AS From_time, " +
                "FU.followup_description AS follow_up_type, " +
                "PV.is_submit_patient_labtest AS isSubmitPatientLabtest, " +
                "CASE WHEN CURRENT_TIMESTAMP IS NOT NULL THEN " +
                "TO_CHAR(CURRENT_TIMESTAMP - CURRENT_TIMESTAMP, 'MI:SS') " +
                "ELSE NULL END AS Duration " +
                "FROM patient_visits PV " +
                "INNER JOIN patient_master PM ON PV.patient_id = PM.id " +
                "INNER JOIN doctor_master DM ON PV.doctor_id = DM.doctor_id " +
                "INNER JOIN gender_translations GT ON PM.gender_id = GT.gender_id " +
                "INNER JOIN status_ref SR ON PV.status_id = SR.id AND PV.clinic_id = SR.clinic_id " +
                "LEFT JOIN follow_up_type FU ON FU.id = PV.follow_up_type " +
                "WHERE PV.delete_flag = false " +
                "AND PV.doctor_id = ? " +
                "AND PV.visit_date::date = ? " +
                "AND PV.status_id NOT IN (4, 5, 11, 12) " +
                "AND GT.language_id = ? " +
                "ORDER BY PV.visit_time ASC";
        
        System.out.println("DEBUG - Query: " + query);
        
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, doctorId, futureDate, languageId);
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
                
                // Convert timezone in Java if needed
                convertTimezoneInRow(row);
                
                System.out.println("DEBUG - Row " + i + " AFTER conversion:");
                System.out.println("  Visit_Time: " + row.get("Visit_Time"));
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
                "SR.status_description, " +
                "SR.id AS Status_ID, " +
                "TO_CHAR(PV.visit_time::time, 'HH24:MI') AS From_time, " +
                "FU.followup_description AS follow_up_type, " +
                "PV.is_submit_patient_labtest AS isSubmitPatientLabtest " +
                "FROM patient_visits PV " +
                "INNER JOIN patient_master PM ON PV.patient_id = PM.id " +
                "INNER JOIN doctor_master DM ON PV.doctor_id = DM.doctor_id " +
                "INNER JOIN gender_translations GT ON PM.gender_id = GT.gender_id " +
                "INNER JOIN status_ref SR ON PV.status_id = SR.id AND PV.clinic_id = SR.clinic_id " +
                "LEFT JOIN follow_up_type FU ON FU.id = PV.follow_up_type " +
                "WHERE PV.delete_flag = false " +
                "AND PV.visit_date > CURRENT_DATE " +
                "AND PV.status_id NOT IN (4, 5, 11, 12) " +
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
                "SR.status_description, " +
                "SR.id AS Status_ID, " +
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
                "INNER JOIN status_ref SR ON PV.status_id = SR.id AND PV.clinic_id = SR.clinic_id " +
                "LEFT JOIN follow_up_type FU ON FU.id = PV.follow_up_type " +
                "WHERE PV.delete_flag = false " +
                "AND PV.doctor_id = ? " +
                "AND PV.visit_date >= CURRENT_DATE " +
                "AND PV.status_id NOT IN (4, 5, 11, 12) " +
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
                "SR.status_description, " +
                "SR.id AS Status_ID, " +
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
                "INNER JOIN status_ref SR ON PV.status_id = SR.id AND PV.clinic_id = SR.clinic_id " +
                "LEFT JOIN follow_up_type FU ON FU.id = PV.follow_up_type " +
                "WHERE PV.delete_flag = false " +
                "AND PV.visit_date::date = ? " +
                "AND PV.status_id NOT IN (4, 5, 11, 12) " +
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
}
