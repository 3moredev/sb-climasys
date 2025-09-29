package com.climasys.appointments.web;

import com.climasys.appointments.service.AppointmentSchedulingService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentSchedulingService appointmentSchedulingService;

    public record AppointmentRequest(
            @NotBlank String visitDate,
            int shiftId,
            @NotBlank String clinicId,
            @NotBlank String doctorId,
            @NotBlank String patientId,
            String visitTime,
            Boolean reportsReceived,
            Boolean inPerson
    ) {}

    @PostMapping("/appointments")
    public ResponseEntity<?> book(@Valid @RequestBody AppointmentRequest req) {
        try {
            // Use JPA service to book appointment
            Map<String, Object> result = appointmentSchedulingService.bookAppointment(
                req.visitDate(),
                req.shiftId(),
                req.clinicId(),
                req.doctorId(),
                req.patientId(),
                req.visitTime() != null ? req.visitTime() : java.time.LocalTime.now().toString().substring(0, 5),
                req.reportsReceived() != null ? req.reportsReceived() : false,
                "system", // TODO: Get from authentication context
                req.inPerson() != null ? req.inPerson() : true
            );
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to book appointment: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleJsonParseError(HttpMessageNotReadableException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Invalid JSON format. Please check your request body.");
        error.put("details", "JSON parsing error: " + ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Validation failed. Please check your request data.");
        error.put("details", ex.getBindingResult().getFieldErrors());
        return ResponseEntity.badRequest().body(error);
    }

    @GetMapping("/appointments/today")
    public ResponseEntity<?> today(@RequestParam String doctorId, @RequestParam int shiftId, @RequestParam String clinicId, @RequestParam String roleId) {
        try {
            // Use JPA implementation to get today's appointments
            String today = LocalDate.now().toString();
            List<Map<String, Object>> appointments = appointmentSchedulingService.getTodaysAppointmentsJpa(
                doctorId, clinicId, today, 1); // Default language ID = 1
            
            // Create response structure similar to the original stored procedure
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("appointments", appointments);
            result.put("count", appointments.size());
            result.put("doctorId", doctorId);
            result.put("shiftId", shiftId);
            result.put("clinicId", clinicId);
            result.put("roleId", roleId);
            result.put("date", today);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get today's visits: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/appointments/future")
    public ResponseEntity<?> getFutureAppointments(
            @RequestParam String doctorId, 
            @RequestParam String clinicId, 
            @RequestParam String futureDate,
            @RequestParam(defaultValue = "1") Integer languageId) {
        try {
            List<Map<String, Object>> appointments = appointmentSchedulingService.getFutureAppointmentsJpa(
                doctorId, clinicId, futureDate, languageId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("appointments", appointments);
            result.put("count", appointments.size());
            result.put("doctorId", doctorId);
            result.put("clinicId", clinicId);
            result.put("futureDate", futureDate);
            result.put("languageId", languageId);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get future appointments: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/appointments/patient/{patientId}")
    public ResponseEntity<?> getPatientAppointments(@PathVariable String patientId) {
        try {
            List<Map<String, Object>> appointments = appointmentSchedulingService.getPatientAppointmentDetails(patientId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("appointments", appointments);
            result.put("count", appointments.size());
            result.put("patientId", patientId);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get patient appointments: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/appointments")
    public ResponseEntity<?> deleteAppointment(
            @RequestParam String patientId,
            @RequestParam String visitDate,
            @RequestParam String doctorId,
            @RequestParam(defaultValue = "system") String userId) {
        try {
            Map<String, Object> result = appointmentSchedulingService.deleteAppointment(
                patientId, visitDate, doctorId, userId);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to delete appointment: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/appointments/status")
    public ResponseEntity<?> updateAppointmentStatus(
            @RequestParam String patientId,
            @RequestParam String visitDate,
            @RequestParam String doctorId,
            @RequestParam Short statusId,
            @RequestParam(defaultValue = "system") String userId) {
        try {
            Map<String, Object> result = appointmentSchedulingService.updateAppointmentStatus(
                patientId, visitDate, doctorId, statusId, userId);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to update appointment status: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/appointments/status-options")
    public ResponseEntity<?> getStatusOptions(@RequestParam String clinicId) {
        try {
            List<Map<String, Object>> statusOptions = appointmentSchedulingService.getStatusOptions(clinicId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("statusOptions", statusOptions);
            result.put("clinicId", clinicId);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get status options: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/appointments/gender-options")
    public ResponseEntity<?> getGenderOptions(@RequestParam(defaultValue = "1") Integer languageId) {
        try {
            List<Map<String, Object>> genderOptions = appointmentSchedulingService.getGenderOptions(languageId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("genderOptions", genderOptions);
            result.put("languageId", languageId);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get gender options: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/appointments/online-time-doctor")
    public ResponseEntity<?> updateAppointmentOnlineTimeAndDoctor(
            @RequestParam String patientId,
            @RequestParam Integer patientVisitNo,
            @RequestParam Short shiftId,
            @RequestParam String clinicId,
            @RequestParam(required = false) String onlineAppointmentTime,
            @RequestParam String doctorId,
            @RequestParam Short statusId,
            @RequestParam(defaultValue = "system") String userId) {
        try {
            Map<String, Object> result = appointmentSchedulingService.updateAppointmentOnlineTimeAndDoctor(
                patientId, patientVisitNo, shiftId, clinicId, onlineAppointmentTime, 
                doctorId, statusId, userId);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to update appointment online time and doctor: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}


