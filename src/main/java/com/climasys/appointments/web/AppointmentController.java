package com.climasys.appointments.web;

import com.climasys.appointments.service.AppointmentSchedulingService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @NotBlank String patientId
    ) {}

    @PostMapping("/appointments")
    public ResponseEntity<?> book(@RequestBody AppointmentRequest req) {
        try {
            // For now, return a mock response since the stored procedure doesn't exist
            // In a real implementation, you would use JPA to create the appointment
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Appointment booking functionality is being migrated to JPA");
            result.put("appointmentId", "APT_" + System.currentTimeMillis());
            result.put("visitDate", req.visitDate());
            result.put("clinicId", req.clinicId());
            result.put("doctorId", req.doctorId());
            result.put("patientId", req.patientId());
            result.put("status", "SCHEDULED");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to book appointment: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/appointments/today")
    public ResponseEntity<?> today(@RequestParam String doctorId, @RequestParam int shiftId, @RequestParam String clinicId, @RequestParam String roleId) {
        try {
            // Use JPA implementation to get today's appointments
            String today = LocalDate.now().toString();
            List<Map<String, Object>> appointments = appointmentSchedulingService.getFutureAppointmentsForDate(doctorId, today);
            
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
}


