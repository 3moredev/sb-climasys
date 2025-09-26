package com.climasys.visits.web;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final JdbcTemplate jdbcTemplate;

    public VisitController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record AddToVisitRequest(
            @NotBlank String patientId,
            @NotBlank String doctorId,
            @NotBlank String clinicId,
            @NotBlank String visitDate,
            @NotBlank String shiftId,
            String visitType,
            String priority,
            String notes
    ) {}

    public record VisitDetailsRequest(
            @NotBlank String visitId,
            @NotBlank String patientId,
            @NotBlank String doctorId,
            String chiefComplaint,
            String historyOfPresentIllness,
            String physicalExamination,
            String vitalSigns,
            String assessment,
            String plan,
            String notes,
            String followUpDate,
            String followUpNotes
    ) {}

    @PostMapping
    public ResponseEntity<?> addToVisit(@RequestBody AddToVisitRequest req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_Save_PatientAddToVisitList");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("PatientId", req.patientId());
            parameters.put("DoctorId", req.doctorId());
            parameters.put("ClinicId", req.clinicId());
            parameters.put("VisitDate", req.visitDate());
            parameters.put("ShiftId", req.shiftId());
            parameters.put("VisitType", req.visitType());
            parameters.put("Priority", req.priority());
            parameters.put("Notes", req.notes());

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

    @DeleteMapping("/{visitId}")
    public ResponseEntity<?> deleteVisit(@PathVariable String visitId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_DeleteTodaysVisitRecord");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to delete visit: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{visitId}/save")
    public ResponseEntity<?> saveVisitDetails(@PathVariable String visitId, @RequestBody VisitDetailsRequest req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_TodaysVisit_Details_Save");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.put("PatientId", req.patientId());
            parameters.put("DoctorId", req.doctorId());
            parameters.put("ChiefComplaint", req.chiefComplaint());
            parameters.put("HistoryOfPresentIllness", req.historyOfPresentIllness());
            parameters.put("PhysicalExamination", req.physicalExamination());
            parameters.put("VitalSigns", req.vitalSigns());
            parameters.put("Assessment", req.assessment());
            parameters.put("Plan", req.plan());
            parameters.put("Notes", req.notes());
            parameters.put("FollowUpDate", req.followUpDate());
            parameters.put("FollowUpNotes", req.followUpNotes());

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to save visit details: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{visitId}/details")
    public ResponseEntity<?> getVisitDetails(@PathVariable String visitId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetPreviousPatientVisitData");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get visit details: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{visitId}/labs")
    public ResponseEntity<?> getVisitLabResults(@PathVariable String visitId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetPreviousPatientVisitLabResult");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get visit lab results: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{visitId}/profile")
    public ResponseEntity<?> insertPatientProfile(@PathVariable String visitId, @RequestBody Map<String, Object> profileData) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_InsertPatientProfile");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.putAll(profileData);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to insert patient profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{visitId}/complaints")
    public ResponseEntity<?> insertComplaints(@PathVariable String visitId, @RequestBody Map<String, Object> complaintsData) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_InsertComplaintsGrid");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.putAll(complaintsData);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to insert complaints: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{visitId}/diagnoses")
    public ResponseEntity<?> insertDiagnoses(@PathVariable String visitId, @RequestBody Map<String, Object> diagnosesData) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_InsertDignosisGrid");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.putAll(diagnosesData);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to insert diagnoses: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{visitId}/dressings")
    public ResponseEntity<?> insertDressings(@PathVariable String visitId, @RequestBody Map<String, Object> dressingsData) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_InsertDressingGrid");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.putAll(dressingsData);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to insert dressings: " + e.getMessage());
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
    @GetMapping("/appointments-for-date")
    public ResponseEntity<?> getTodaysAppointmentsForGivenDate(
            @RequestParam String doctorId,
            @RequestParam String clinicId,
            @RequestParam String futureDate,
            @RequestParam(defaultValue = "1") Integer languageId) {
        try {
            // Parse the future date
            java.sql.Date queryDate = java.sql.Date.valueOf(futureDate);
            
            // First, let's test basic data availability
            List<Map<String, Object>> testData = testBasicDataAvailability(doctorId, clinicId, queryDate);
            System.out.println("DEBUG - Test data count: " + testData.size());
            
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
            Map<String, Object> error = new HashMap<>();
            error.put("ErrorNumber", -1);
            error.put("ErrorMessage", "Failed to get appointments for date: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private List<Map<String, Object>> getSpecificDateAppointments(String doctorId, String clinicId, 
            java.sql.Date futureDate, Integer languageId) {
        
        // Debug logging
        System.out.println("DEBUG - getSpecificDateAppointments called with:");
        System.out.println("  doctorId: " + doctorId);
        System.out.println("  clinicId: " + clinicId);
        System.out.println("  futureDate: " + futureDate);
        System.out.println("  languageId: " + languageId);
        
        String query = "SELECT " +
                "TO_CHAR(PV.visit_time, 'HH24:MI') AS Visit_Time, " +
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
                "PV.visit_time AS VTime, " +
                "PV.patient_visit_no, " +
                "SR.status_description, " +
                "SR.id AS Status_ID, " +
                "TO_CHAR(PV.from_time, 'HH24:MI') AS From_time, " +
                "FU.followup_description AS follow_up_type, " +
                "CASE WHEN PV.from_time IS NOT NULL AND PV.visit_time IS NOT NULL THEN " +
                "TO_CHAR(PV.from_time - PV.visit_time, 'MI:SS') " +
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
        
        return result;
    }

    private List<Map<String, Object>> getFutureAppointments(String clinicId, Integer languageId) {
        
        String query = "SELECT " +
                "TO_CHAR(PV.visit_time, 'HH24:MI') AS Visit_Time, " +
                "PM.first_name || ' ' || PM.last_name AS Name, " +
                "PV.doctor_id, " +
                "DM.prefix || ' ' || DM.first_name AS Doctor_Name, " +
                "PM.mobile_1 AS Mobile, " +
                "PV.patient_id, " +
                "PM.date_of_birth, " +
                "PM.age_given, " +
                "COALESCE(EXTRACT(YEAR FROM AGE(CURRENT_DATE, PM.date_of_birth)), PM.age_given, 0) AS AgeYearsIntRound, " +
                "GT.gender_description, " +
                "PV.visit_date, " +
                "PV.visit_time AS VTime, " +
                "PV.patient_visit_no, " +
                "SR.status_description, " +
                "SR.id AS Status_ID, " +
                "TO_CHAR(PV.from_time, 'HH24:MI') AS From_time, " +
                "FU.followup_description AS follow_up_type " +
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
        
        return jdbcTemplate.queryForList(query, languageId);
    }

    private List<Map<String, Object>> getTodayAndFutureAppointments(String doctorId, String clinicId, Integer languageId) {
        
        String query = "SELECT " +
                "TO_CHAR(PV.visit_time, 'HH24:MI') AS Visit_Time, " +
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
                "PV.visit_time AS VTime, " +
                "PV.patient_visit_no, " +
                "SR.status_description, " +
                "SR.id AS Status_ID, " +
                "TO_CHAR(PV.from_time, 'HH24:MI') AS From_time, " +
                "PV.visit_date as fulldate, " +
                "PV.visit_time as full_time, " +
                "FU.followup_description AS follow_up_type, " +
                "CASE WHEN PV.from_time IS NOT NULL AND PV.visit_time IS NOT NULL THEN " +
                "TO_CHAR(PV.from_time - PV.visit_time, 'MI:SS') " +
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
        
        return jdbcTemplate.queryForList(query, doctorId, languageId);
    }

    private List<Map<String, Object>> getSpecificDateAppointmentsNoDoctor(String clinicId, 
            java.sql.Date futureDate, Integer languageId) {
        
        String query = "SELECT " +
                "TO_CHAR(PV.visit_time, 'HH24:MI') AS Visit_Time, " +
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
                "PV.visit_time AS VTime, " +
                "PV.patient_visit_no, " +
                "SR.status_description, " +
                "SR.id AS Status_ID, " +
                "TO_CHAR(PV.from_time, 'HH24:MI') AS From_time, " +
                "PV.visit_date as fulldate, " +
                "PV.visit_time as full_time, " +
                "FU.followup_description AS follow_up_type, " +
                "CASE WHEN PV.from_time IS NOT NULL AND PV.visit_time IS NOT NULL THEN " +
                "TO_CHAR(PV.from_time - PV.visit_time, 'MI:SS') " +
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
        
        return jdbcTemplate.queryForList(query, futureDate, languageId);
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
        
        List<Map<String, Object>> result = jdbcTemplate.queryForList(testQuery, doctorId, queryDate);
        
        // Debug each result
        for (Map<String, Object> row : result) {
            System.out.println("DEBUG - Row data:");
            System.out.println("  patient_id: " + row.get("patient_id"));
            System.out.println("  date_of_birth: " + row.get("date_of_birth"));
            System.out.println("  age_given: " + row.get("age_given"));
            System.out.println("  calculated_age: " + row.get("calculated_age"));
        }
        
        return result;
    }
}
