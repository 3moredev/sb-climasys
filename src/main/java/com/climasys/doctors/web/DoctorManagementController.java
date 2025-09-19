package com.climasys.doctors.web;

import com.climasys.doctors.service.DoctorManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for doctor management and operations
 */
@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorManagementController {

    @Autowired
    private DoctorManagementService doctorManagementService;

    /**
     * Get all available doctors in the system
     */
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllDoctors() {
        List<Map<String, Object>> result = doctorManagementService.getAllDoctors();
        return ResponseEntity.ok(result);
    }

    /**
     * Get doctors available for adhoc appointments
     */
    @GetMapping("/adhoc-available")
    public ResponseEntity<List<Map<String, Object>>> getDoctorsForAdhocAppointments() {
        List<Map<String, Object>> result = doctorManagementService.getDoctorsForAdhocAppointments();
        return ResponseEntity.ok(result);
    }

    /**
     * Get doctors assigned to a specific patient
     */
    @GetMapping("/for-patient/{patientId}")
    public ResponseEntity<List<Map<String, Object>>> getDoctorsForPatient(@PathVariable String patientId) {
        List<Map<String, Object>> result = doctorManagementService.getDoctorsForPatient(patientId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get detailed information about a specific doctor
     */
    @GetMapping("/{doctorId}/details")
    public ResponseEntity<List<Map<String, Object>>> getDoctorDetails(@PathVariable String doctorId) {
        List<Map<String, Object>> result = doctorManagementService.getDoctorDetails(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get total count of doctors in the system
     */
    @GetMapping("/count")
    public ResponseEntity<List<Map<String, Object>>> getDoctorCount() {
        List<Map<String, Object>> result = doctorManagementService.getDoctorCount();
        return ResponseEntity.ok(result);
    }

    /**
     * Get doctors who are ready to submit their work
     */
    @GetMapping("/{doctorId}/ready-for-submission")
    public ResponseEntity<List<Map<String, Object>>> getDoctorsReadyForSubmission(@PathVariable String doctorId) {
        List<Map<String, Object>> result = doctorManagementService.getDoctorsReadyForSubmission(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get doctor status reference data
     */
    @GetMapping("/status-reference")
    public ResponseEntity<List<Map<String, Object>>> getDoctorStatusReference() {
        List<Map<String, Object>> result = doctorManagementService.getDoctorStatusReference();
        return ResponseEntity.ok(result);
    }

    /**
     * Get today's visits for a specific doctor
     */
    @GetMapping("/{doctorId}/todays-visits")
    public ResponseEntity<List<Map<String, Object>>> getDoctorTodaysVisits(@PathVariable String doctorId) {
        List<Map<String, Object>> result = doctorManagementService.getDoctorTodaysVisits(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get fees to be collected by a doctor
     */
    @GetMapping("/{doctorId}/fees-to-collect")
    public ResponseEntity<List<Map<String, Object>>> getFeesToCollectByDoctor(@PathVariable String doctorId) {
        List<Map<String, Object>> result = doctorManagementService.getFeesToCollectByDoctor(doctorId);
        return ResponseEntity.ok(result);
    }
}
