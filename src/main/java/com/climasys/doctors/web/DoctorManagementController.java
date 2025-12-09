package com.climasys.doctors.web;

import com.climasys.doctors.service.DoctorManagementService;
import jakarta.servlet.http.HttpSession;
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
public class DoctorManagementController {

    @Autowired
    private DoctorManagementService doctorManagementService;

    /**
     * Get all available doctors in the system
     * Matches USP_Get_AllDoctors stored procedure logic from climasys2.0
     * 
     * Filters doctors by:
     * - Language ID (optional)
     * - Clinic ID (optional)
     * - OPD_DR = 1 (only OPD doctors)
     * - Is_Active = 1 (active users)
     * - Role_Id = 2 (doctor role)
     * - Is_Default_Clinic = 1 (default clinic)
     * 
     * The defaultDoctorId (if provided) will be sorted to appear first in the results.
     * 
     * @param languageId Optional language ID filter
     * @param clinicId Optional clinic ID filter
     * @param defaultDoctorId Optional doctor ID to sort first (typically the logged-in user's doctor)
     * @return List of doctors matching the criteria, with default doctor first if specified
     */
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllDoctors(
            @RequestParam(required = false) Integer languageId,
            @RequestParam(required = false) String clinicId,
            @RequestParam(required = false) String defaultDoctorId,
            HttpSession session) {
        System.out.println("=== GET /api/doctors/all ===");
        System.out.println("languageId: " + languageId);
        System.out.println("clinicId: " + clinicId);
        System.out.println("defaultDoctorId param: " + defaultDoctorId);
        
        // Get default doctor from user_master table using the logged-in user's session
        String defaultDoctorFromUser = doctorManagementService.getDefaultDoctorFromUser(session);
        System.out.println("defaultDoctor from user_master: " + defaultDoctorFromUser);
        
        // Use defaultDoctorFromUser if available, otherwise fall back to parameter
        String effectiveDefaultDoctorId = (defaultDoctorFromUser != null && !defaultDoctorFromUser.isEmpty()) 
            ? defaultDoctorFromUser 
            : defaultDoctorId;
        
        System.out.println("Effective defaultDoctorId: " + effectiveDefaultDoctorId);
        
        List<Map<String, Object>> result = doctorManagementService.getAllDoctors(languageId, clinicId, effectiveDefaultDoctorId);
        System.out.println("Returning " + result.size() + " doctors");
        if (!result.isEmpty() && effectiveDefaultDoctorId != null && !effectiveDefaultDoctorId.isEmpty()) {
            String firstDoctorId = String.valueOf(result.get(0).get("id") != null ? result.get(0).get("id") : result.get(0).get("doctor_id"));
            System.out.println("First doctor ID: " + firstDoctorId + " (matches default: " + effectiveDefaultDoctorId.equals(firstDoctorId) + ")");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all-doctors")
    public ResponseEntity<List<Map<String, Object>>> getAllDoctorsList() {
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

    /**
     * Delete a doctor
     */
    @DeleteMapping("/delete/{doctorId}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String doctorId) {
        doctorManagementService.deleteDoctor(doctorId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get doctor by ID
     */
    @GetMapping("/{doctorId}")
    public ResponseEntity<com.climasys.auth.entity.AuthDoctorMaster> getDoctorById(@PathVariable String doctorId) {
        com.climasys.auth.entity.AuthDoctorMaster doctor = doctorManagementService.getDoctorById(doctorId);
        if (doctor != null) {
            return ResponseEntity.ok(doctor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create or update a doctor
     */
    @PostMapping("/save")
    public ResponseEntity<com.climasys.auth.entity.AuthDoctorMaster> saveDoctor(
            @RequestBody com.climasys.auth.entity.AuthDoctorMaster doctor) {
        com.climasys.auth.entity.AuthDoctorMaster savedDoctor = doctorManagementService.saveDoctor(doctor);
        return ResponseEntity.ok(savedDoctor);
    }

    /**
     * Update a doctor
     */
    @PutMapping("/update/{doctorId}")
    public ResponseEntity<com.climasys.auth.entity.AuthDoctorMaster> updateDoctor(
            @PathVariable String doctorId,
            @RequestBody com.climasys.auth.entity.AuthDoctorMaster doctor) {
        doctor.setDoctorId(doctorId);
        com.climasys.auth.entity.AuthDoctorMaster updatedDoctor = doctorManagementService.saveDoctor(doctor);
        return ResponseEntity.ok(updatedDoctor);
    }
}
