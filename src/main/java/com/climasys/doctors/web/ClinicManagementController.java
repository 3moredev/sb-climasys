package com.climasys.doctors.web;

import com.climasys.doctors.service.ClinicManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for clinic management and operations
 */
@RestController
@RequestMapping("/api/clinics")
@CrossOrigin(origins = "*")
public class ClinicManagementController {

    @Autowired
    private ClinicManagementService clinicManagementService;

    /**
     * Get clinic details and information
     */
    @GetMapping("/{clinicId}/details")
    public ResponseEntity<List<Map<String, Object>>> getClinicDetails(@PathVariable String clinicId) {
        List<Map<String, Object>> result = clinicManagementService.getClinicDetails(clinicId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get clinic shifts and schedules
     */
    @GetMapping("/{clinicId}/shifts")
    public ResponseEntity<List<Map<String, Object>>> getClinicShifts(@PathVariable String clinicId) {
        List<Map<String, Object>> result = clinicManagementService.getClinicShifts(clinicId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get clinic shift timings for a specific day
     */
    @GetMapping("/{clinicId}/shift-timings")
    public ResponseEntity<List<Map<String, Object>>> getClinicShiftTimings(
            @PathVariable String clinicId,
            @RequestParam String shiftDay) {
        List<Map<String, Object>> result = clinicManagementService.getClinicShiftTimings(clinicId, shiftDay);
        return ResponseEntity.ok(result);
    }
}
