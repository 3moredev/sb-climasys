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

    /**
     * Get all clinics
     */
    @GetMapping("/all")
    public ResponseEntity<List<com.climasys.entity.Clinic>> getAllClinics() {
        List<com.climasys.entity.Clinic> result = clinicManagementService.getAllClinics();
        return ResponseEntity.ok(result);
    }

    /**
     * Get count of all clinics
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getClinicCount() {
        long count = clinicManagementService.getClinicCount();
        return ResponseEntity.ok(Map.of("clinicCount", count));
    }

    /**
     * Save a new clinic
     */
    @PostMapping("/save")
    public ResponseEntity<com.climasys.entity.Clinic> saveClinic(@RequestBody com.climasys.entity.Clinic clinic) {
        com.climasys.entity.Clinic savedClinic = clinicManagementService.saveClinic(clinic);
        return ResponseEntity.ok(savedClinic);
    }

    /**
     * Update an existing clinic
     */
    @PutMapping("update/{clinicId}")
    public ResponseEntity<com.climasys.entity.Clinic> updateClinic(
            @PathVariable String clinicId,
            @RequestBody com.climasys.entity.Clinic clinic) {
        com.climasys.entity.Clinic updatedClinic = clinicManagementService.updateClinic(clinicId, clinic);
        return ResponseEntity.ok(updatedClinic);
    }

    /**
     * Delete a clinic
     */
    @DeleteMapping("delete/{clinicId}")
    public ResponseEntity<Void> deleteClinic(@PathVariable String clinicId) {
        clinicManagementService.deleteClinic(clinicId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get clinic by ID
     */
    @GetMapping("/{clinicId}")
    public ResponseEntity<com.climasys.entity.Clinic> getClinicById(@PathVariable String clinicId) {
        com.climasys.entity.Clinic clinic = clinicManagementService.getClinicById(clinicId);
        if (clinic != null) {
            return ResponseEntity.ok(clinic);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
