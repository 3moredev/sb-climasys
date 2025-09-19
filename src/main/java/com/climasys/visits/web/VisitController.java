package com.climasys.visits.web;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
}
