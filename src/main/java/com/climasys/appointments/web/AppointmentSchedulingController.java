package com.climasys.appointments.web;

import com.climasys.appointments.service.AppointmentSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller for appointment scheduling and management
 */
@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentSchedulingController {

    @Autowired
    private AppointmentSchedulingService appointmentSchedulingService;

    /**
     * Get all future appointments for a doctor
     */
    @GetMapping("/{doctorId}/future")
    public ResponseEntity<List<Map<String, Object>>> getFutureAppointments(@PathVariable String doctorId) {
        List<Map<String, Object>> result = appointmentSchedulingService.getFutureAppointments(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get future appointments for a specific date
     */
    @GetMapping("/{doctorId}/future-for-date")
    public ResponseEntity<List<Map<String, Object>>> getFutureAppointmentsForDate(
            @PathVariable String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate) {
        List<Map<String, Object>> result = appointmentSchedulingService.getFutureAppointmentsForDate(
                doctorId, appointmentDate.toString());
        return ResponseEntity.ok(result);
    }

    /**
     * Get holiday details for a doctor
     */
    @GetMapping("/{doctorId}/holidays")
    public ResponseEntity<List<Map<String, Object>>> getHolidayDetails(@PathVariable String doctorId) {
        List<Map<String, Object>> result = appointmentSchedulingService.getHolidayDetails(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get gender options for appointments
     */
    @GetMapping("/gender-options")
    public ResponseEntity<List<Map<String, Object>>> getGenderOptions() {
        List<Map<String, Object>> result = appointmentSchedulingService.getGenderOptions();
        return ResponseEntity.ok(result);
    }
}
