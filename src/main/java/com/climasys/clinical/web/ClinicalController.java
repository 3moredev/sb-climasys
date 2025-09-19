package com.climasys.clinical.web;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/clinical")
public class ClinicalController {

    private final JdbcTemplate jdbcTemplate;

    public ClinicalController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record ComplaintRequest(
            @NotBlank String visitId,
            @NotBlank String complaint,
            String duration,
            String severity,
            String notes
    ) {}

    public record DiagnosisRequest(
            @NotBlank String visitId,
            @NotBlank String diagnosis,
            String diagnosisType,
            String notes,
            String treatmentPlan
    ) {}

    public record TreatmentRequest(
            @NotBlank String visitId,
            @NotBlank String treatment,
            String treatmentType,
            String instructions,
            String followUpRequired,
            String followUpDate
    ) {}

    public record VitalSignsRequest(
            @NotBlank String visitId,
            String bloodPressure,
            String heartRate,
            String temperature,
            String respiratoryRate,
            String oxygenSaturation,
            String weight,
            String height,
            String bmi,
            String notes
    ) {}

    // Complaints Management
    @PostMapping("/visits/{visitId}/complaints")
    public ResponseEntity<?> addComplaint(@PathVariable String visitId, @RequestBody ComplaintRequest req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_AddComplaint");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.put("Complaint", req.complaint());
            parameters.put("Duration", req.duration());
            parameters.put("Severity", req.severity());
            parameters.put("Notes", req.notes());

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to add complaint: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/visits/{visitId}/complaints")
    public ResponseEntity<?> getComplaints(@PathVariable String visitId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetComplaints");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get complaints: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Diagnosis Management
    @PostMapping("/visits/{visitId}/diagnoses")
    public ResponseEntity<?> addDiagnosis(@PathVariable String visitId, @RequestBody DiagnosisRequest req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_AddDiagnosis");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.put("Diagnosis", req.diagnosis());
            parameters.put("DiagnosisType", req.diagnosisType());
            parameters.put("Notes", req.notes());
            parameters.put("TreatmentPlan", req.treatmentPlan());

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to add diagnosis: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/visits/{visitId}/diagnoses")
    public ResponseEntity<?> getDiagnoses(@PathVariable String visitId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetDiagnoses");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get diagnoses: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Treatment Management
    @PostMapping("/visits/{visitId}/treatments")
    public ResponseEntity<?> addTreatment(@PathVariable String visitId, @RequestBody TreatmentRequest req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_AddTreatment");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.put("Treatment", req.treatment());
            parameters.put("TreatmentType", req.treatmentType());
            parameters.put("Instructions", req.instructions());
            parameters.put("FollowUpRequired", req.followUpRequired());
            parameters.put("FollowUpDate", req.followUpDate());

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to add treatment: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/visits/{visitId}/treatments")
    public ResponseEntity<?> getTreatments(@PathVariable String visitId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetTreatments");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get treatments: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Vital Signs Management
    @PostMapping("/visits/{visitId}/vital-signs")
    public ResponseEntity<?> addVitalSigns(@PathVariable String visitId, @RequestBody VitalSignsRequest req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_AddVitalSigns");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.put("BloodPressure", req.bloodPressure());
            parameters.put("HeartRate", req.heartRate());
            parameters.put("Temperature", req.temperature());
            parameters.put("RespiratoryRate", req.respiratoryRate());
            parameters.put("OxygenSaturation", req.oxygenSaturation());
            parameters.put("Weight", req.weight());
            parameters.put("Height", req.height());
            parameters.put("BMI", req.bmi());
            parameters.put("Notes", req.notes());

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to add vital signs: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/visits/{visitId}/vital-signs")
    public ResponseEntity<?> getVitalSigns(@PathVariable String visitId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetVitalSigns");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get vital signs: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Clinical Notes
    @PostMapping("/visits/{visitId}/notes")
    public ResponseEntity<?> addClinicalNotes(@PathVariable String visitId, @RequestBody Map<String, Object> notesData) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_AddClinicalNotes");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.putAll(notesData);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to add clinical notes: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/visits/{visitId}/notes")
    public ResponseEntity<?> getClinicalNotes(@PathVariable String visitId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetClinicalNotes");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get clinical notes: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Physical Examination
    @PostMapping("/visits/{visitId}/physical-exam")
    public ResponseEntity<?> addPhysicalExam(@PathVariable String visitId, @RequestBody Map<String, Object> examData) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_AddPhysicalExam");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.putAll(examData);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to add physical exam: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/visits/{visitId}/physical-exam")
    public ResponseEntity<?> getPhysicalExam(@PathVariable String visitId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetPhysicalExam");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get physical exam: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Follow-up Management
    @PostMapping("/visits/{visitId}/follow-up")
    public ResponseEntity<?> scheduleFollowUp(@PathVariable String visitId, @RequestBody Map<String, Object> followUpData) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_ScheduleFollowUp");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.putAll(followUpData);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to schedule follow-up: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/visits/{visitId}/follow-up")
    public ResponseEntity<?> getFollowUpDetails(@PathVariable String visitId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetFollowUpDetails");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get follow-up details: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Complete Clinical Summary
    @GetMapping("/visits/{visitId}/summary")
    public ResponseEntity<?> getClinicalSummary(@PathVariable String visitId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetClinicalSummary");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get clinical summary: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
