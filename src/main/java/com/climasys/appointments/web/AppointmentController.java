package com.climasys.appointments.web;

import com.climasys.auth.annotation.RefreshSession;

import com.climasys.appointments.service.AppointmentSchedulingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Appointments", description = "Appointment management endpoints for booking, updating, and deleting appointments")
@RefreshSession
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

    @Operation(
        summary = "Book New Appointment", 
        description = "Creates a new appointment for a patient with a specific doctor"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointment booked successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid appointment data")
    })
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
    
    @Operation(
        summary = "Delete Appointment", 
        description = "Soft deletes an appointment by setting deleteFlag to true"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointment deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - Appointment not found or invalid parameters")
    })
    @DeleteMapping("/appointments")
    public ResponseEntity<?> deleteAppointment(
            @Parameter(description = "Patient ID", required = true, example = "P-00001")
            @RequestParam String patientId,
            @Parameter(description = "Visit date in YYYY-MM-DD or YYYY-MM-DD HH:mm:ss format", required = true, example = "2024-01-15")
            @RequestParam String visitDate,
            @Parameter(description = "Doctor ID", required = true, example = "DR-00010")
            @RequestParam String doctorId,
            @Parameter(description = "Clinic ID", required = true, example = "CLINIC001")
            @RequestParam String clinicId,
            @Parameter(description = "User ID performing the deletion", required = false, example = "admin")
            @RequestParam(defaultValue = "system") String userId) {
        try {
            Map<String, Object> result = appointmentSchedulingService.deleteAppointment(
                patientId, visitDate, doctorId, clinicId, userId);
            
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
    
    @Operation(
        summary = "Update Appointment Online Time, Doctor, and Status", 
        description = "Updates appointment online time, doctor assignment, and status in a single operation. Equivalent to USP_Update_TodaysVisitOnlineTimeDetails but with status update capability."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointment updated successfully",
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid parameters or appointment not found",
            content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PutMapping("/appointments/online-time-doctor")
    public ResponseEntity<?> updateAppointmentOnlineTimeAndDoctor(
            @Parameter(description = "Patient ID", required = true, example = "P-00001")
            @RequestParam String patientId,
            @Parameter(description = "Patient Visit Number", required = true, example = "1")
            @RequestParam Integer patientVisitNo,
            @Parameter(description = "Shift ID", required = true, example = "1")
            @RequestParam Short shiftId,
            @Parameter(description = "Clinic ID", required = true, example = "CL-00001")
            @RequestParam String clinicId,
            @Parameter(description = "Online appointment time in HH:MM format", required = false, example = "14:30")
            @RequestParam(required = false) String onlineAppointmentTime,
            @Parameter(description = "Doctor ID to assign", required = true, example = "DR-00010")
            @RequestParam String doctorId,
            @Parameter(description = "Status ID (1=Waiting, 2=With Doctor, 3=Completed, etc.)", required = true, example = "2")
            @RequestParam Short statusId,
            @Parameter(description = "User ID performing the update", required = false, example = "admin")
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

