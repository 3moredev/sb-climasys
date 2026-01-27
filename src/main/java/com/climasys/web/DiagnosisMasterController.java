package com.climasys.web;

import com.climasys.auth.annotation.RefreshSession;

import com.climasys.entity.DiagnosisMaster;
import com.climasys.service.DiagnosisMasterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for DiagnosisMaster management
 * Provides endpoints for managing diagnosis master data
 */
@RestController
@RequestMapping("/api/diagnosis-master")
@RefreshSession
public class DiagnosisMasterController {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosisMasterController.class);

    @Autowired
    private DiagnosisMasterService diagnosisMasterService;

    /**
     * Get all diagnoses for a specific doctor and clinic
     * GET /api/diagnosis-master/doctor/{doctorId}/clinic/{clinicId}
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}")
    public ResponseEntity<?> getAllDiagnosesForDoctorAndClinic(
            @PathVariable String doctorId, 
            @PathVariable String clinicId) {
        try {
            logger.info("Getting all diagnoses for doctor: {} and clinic: {}", doctorId, clinicId);
            List<DiagnosisMaster> diagnoses = diagnosisMasterService.getAllDiagnosesForDoctorAndClinic(doctorId, clinicId);
            return ResponseEntity.ok(diagnoses);
        } catch (Exception e) {
            logger.error("Error getting diagnoses for doctor {} and clinic {}: {}", doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get diagnoses: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all diagnoses for a specific doctor (backward compatibility)
     * GET /api/diagnosis-master/doctor/{doctorId}
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getAllDiagnosesForDoctor(@PathVariable String doctorId) {
        try {
            logger.info("Getting all diagnoses for doctor: {}", doctorId);
            List<DiagnosisMaster> diagnoses = diagnosisMasterService.getAllDiagnosesForDoctor(doctorId);
            return ResponseEntity.ok(diagnoses);
        } catch (Exception e) {
            logger.error("Error getting diagnoses for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get diagnoses: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get diagnosis data in the same format as stored procedure for doctor and clinic
     * Returns data with concatenated ID field for backward compatibility
     * GET /api/diagnosis-master/doctor/{doctorId}/clinic/{clinicId}/formatted
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}/formatted")
    public ResponseEntity<?> getDiagnosesFormattedByDoctorAndClinic(
            @PathVariable String doctorId, 
            @PathVariable String clinicId) {
        try {
            logger.info("Getting formatted diagnoses for doctor: {} and clinic: {}", doctorId, clinicId);
            List<Map<String, Object>> diagnoses = diagnosisMasterService.getDiagnosesFormattedByDoctorAndClinic(doctorId, clinicId);
            return ResponseEntity.ok(diagnoses);
        } catch (Exception e) {
            logger.error("Error getting formatted diagnoses for doctor {} and clinic {}: {}", doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get formatted diagnoses: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get diagnosis data in the same format as stored procedure (backward compatibility)
     * Returns data with concatenated ID field for backward compatibility
     * GET /api/diagnosis-master/doctor/{doctorId}/formatted
     */
    @GetMapping("/doctor/{doctorId}/formatted")
    public ResponseEntity<?> getDiagnosesFormatted(@PathVariable String doctorId) {
        try {
            logger.info("Getting formatted diagnoses for doctor: {}", doctorId);
            List<Map<String, Object>> diagnoses = diagnosisMasterService.getDiagnosesFormatted(doctorId);
            return ResponseEntity.ok(diagnoses);
        } catch (Exception e) {
            logger.error("Error getting formatted diagnoses for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get formatted diagnoses: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all diagnosis data for a clinic in formatted way (including all diagnoses)
     * GET /api/diagnosis-master/clinic/{clinicId}/formatted?doctorId={doctorId}
     */
    @GetMapping("/clinic/{clinicId}/formatted")
    public ResponseEntity<?> getAllDiagnosesForDoctorFormatted(
            @PathVariable String clinicId,
            @RequestParam(required = false) String doctorId) {
        try {
            logger.info("Getting all formatted diagnoses for clinic: {} and doctor: {}", clinicId, doctorId);
            List<Map<String, Object>> diagnoses = diagnosisMasterService.getAllDiagnosesForDoctorFormatted(clinicId, doctorId);
            return ResponseEntity.ok(diagnoses);
        } catch (Exception e) {
            logger.error("Error getting all formatted diagnoses for clinic {} and doctor {}: {}", clinicId, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get all formatted diagnoses: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Search diagnoses by description for a specific doctor
     * GET /api/diagnosis-master/doctor/{doctorId}/search?term={searchTerm}
     */
    @GetMapping("/doctor/{doctorId}/search")
    public ResponseEntity<?> searchDiagnosesByDescription(
            @PathVariable String doctorId,
            @RequestParam String term) {
        try {
            logger.info("Searching diagnoses for doctor: {} with term: {}", doctorId, term);
            List<DiagnosisMaster> diagnoses = diagnosisMasterService.searchDiagnosesByDescription(doctorId, term);
            return ResponseEntity.ok(diagnoses);
        } catch (Exception e) {
            logger.error("Error searching diagnoses for doctor {} with term {}: {}", doctorId, term, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search diagnoses: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get a specific diagnosis by short description, doctor ID, and clinic ID
     * GET /api/diagnosis-master/doctor/{doctorId}/clinic/{clinicId}/diagnosis/{shortDescription}
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}/diagnosis/{shortDescription}")
    public ResponseEntity<?> getDiagnosisByShortDescription(
            @PathVariable String doctorId,
            @PathVariable String clinicId,
            @PathVariable String shortDescription) {
        try {
            logger.info("Getting diagnosis: {} for doctor: {} and clinic: {}", shortDescription, doctorId, clinicId);
            Optional<DiagnosisMaster> diagnosis = diagnosisMasterService.getDiagnosisByShortDescription(shortDescription, doctorId, clinicId);
            if (diagnosis.isPresent()) {
                return ResponseEntity.ok(diagnosis.get());
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Diagnosis not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting diagnosis {} for doctor {} and clinic {}: {}", shortDescription, doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get diagnosis: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Create a new diagnosis
     * POST /api/diagnosis-master
     */
    @PostMapping
    public ResponseEntity<?> createDiagnosis(@RequestBody DiagnosisMaster diagnosis) {
        try {
            logger.info("Creating new diagnosis: {}", diagnosis.getShortDescription());
            DiagnosisMaster createdDiagnosis = diagnosisMasterService.createDiagnosis(diagnosis);
            return ResponseEntity.ok(createdDiagnosis);
        } catch (Exception e) {
            logger.error("Error creating diagnosis: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create diagnosis: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update an existing diagnosis
     * Only updates diagnosis description and priority value
     * Short description, doctor ID, and clinic ID cannot be changed (they are part of the composite key)
     * PUT /api/diagnosis-master
     */
    @PutMapping
    public ResponseEntity<?> updateDiagnosis(@RequestBody DiagnosisMaster diagnosis) {
        try {
            logger.info("Updating diagnosis: {} for doctor: {} and clinic: {}", 
                       diagnosis.getShortDescription(), diagnosis.getDoctorId(), diagnosis.getClinicId());
            DiagnosisMaster updatedDiagnosis = diagnosisMasterService.updateDiagnosis(diagnosis);
            return ResponseEntity.ok(updatedDiagnosis);
        } catch (IllegalArgumentException e) {
            logger.error("Diagnosis not found for update: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating diagnosis: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to update diagnosis: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update an existing diagnosis using path variables
     * Only updates diagnosis description and priority value
     * Short description, doctor ID, and clinic ID cannot be changed (they are part of the composite key)
     * PUT /api/diagnosis-master/doctor/{doctorId}/clinic/{clinicId}/diagnosis/{shortDescription}
     */
    @PutMapping("/doctor/{doctorId}/clinic/{clinicId}/diagnosis/{shortDescription}")
    public ResponseEntity<?> updateDiagnosisByPath(
            @PathVariable String doctorId,
            @PathVariable String clinicId,
            @PathVariable String shortDescription,
            @RequestBody DiagnosisMaster diagnosis) {
        try {
            // Spring Boot automatically URL decodes path variables, but we'll trim any whitespace
            String trimmedShortDescription = shortDescription != null ? shortDescription.trim() : shortDescription;
            
            logger.info("Updating diagnosis with path variables - shortDescription: '{}' (original: '{}'), doctorId: {}, clinicId: {}", 
                       trimmedShortDescription, shortDescription, doctorId, clinicId);
            logger.debug("Request body diagnosis description: {}, priority value: {}", 
                        diagnosis.getDiagnosisDescription(), diagnosis.getPriorityValue());
            
            // Set the composite key fields from path variables to ensure they match
            diagnosis.setShortDescription(trimmedShortDescription);
            diagnosis.setDoctorId(doctorId);
            diagnosis.setClinicId(clinicId);
            
            DiagnosisMaster updatedDiagnosis = diagnosisMasterService.updateDiagnosis(diagnosis);
            return ResponseEntity.ok(updatedDiagnosis);
        } catch (IllegalArgumentException e) {
            logger.error("Diagnosis not found for update: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating diagnosis {} for doctor {} and clinic {}: {}", 
                        shortDescription, doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to update diagnosis: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete a diagnosis
     * DELETE /api/diagnosis-master/doctor/{doctorId}/clinic/{clinicId}/diagnosis/{shortDescription}
     */
    @DeleteMapping("/doctor/{doctorId}/clinic/{clinicId}/diagnosis/{shortDescription}")
    public ResponseEntity<?> deleteDiagnosis(
            @PathVariable String doctorId,
            @PathVariable String clinicId,
            @PathVariable String shortDescription) {
        try {
            logger.info("Deleting diagnosis: {} for doctor: {} and clinic: {}", shortDescription, doctorId, clinicId);
            boolean deleted = diagnosisMasterService.deleteDiagnosis(shortDescription, doctorId, clinicId);
            if (deleted) {
                Map<String, Object> result = new HashMap<>();
                result.put("message", "Diagnosis deleted successfully");
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Diagnosis not found or access denied");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting diagnosis {} for doctor {} and clinic {}: {}", shortDescription, doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to delete diagnosis: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get diagnosis statistics for a doctor
     * GET /api/diagnosis-master/doctor/{doctorId}/statistics
     */
    @GetMapping("/doctor/{doctorId}/statistics")
    public ResponseEntity<?> getDiagnosisStatistics(@PathVariable String doctorId) {
        try {
            logger.info("Getting diagnosis statistics for doctor: {}", doctorId);
            Map<String, Object> statistics = diagnosisMasterService.getDiagnosisStatistics(doctorId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error getting diagnosis statistics for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get diagnosis statistics: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Check if a diagnosis exists for a doctor
     * GET /api/diagnosis-master/doctor/{doctorId}/diagnosis/{shortDescription}/exists
     */
    @GetMapping("/doctor/{doctorId}/diagnosis/{shortDescription}/exists")
    public ResponseEntity<?> checkDiagnosisExists(
            @PathVariable String doctorId,
            @PathVariable String shortDescription) {
        try {
            logger.info("Checking if diagnosis exists: {} for doctor: {}", shortDescription, doctorId);
            boolean exists = diagnosisMasterService.diagnosisExists(doctorId, shortDescription);
            Map<String, Object> result = new HashMap<>();
            result.put("exists", exists);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error checking if diagnosis exists {} for doctor {}: {}", shortDescription, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to check diagnosis existence: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Check if a diagnosis exists for a doctor and clinic
     * GET /api/diagnosis-master/doctor/{doctorId}/clinic/{clinicId}/diagnosis/{shortDescription}/exists
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}/diagnosis/{shortDescription}/exists")
    public ResponseEntity<?> checkDiagnosisExistsForDoctorAndClinic(
            @PathVariable String doctorId,
            @PathVariable String clinicId,
            @PathVariable String shortDescription) {
        try {
            logger.info("Checking if diagnosis exists: {} for doctor: {} and clinic: {}", shortDescription, doctorId, clinicId);
            boolean exists = diagnosisMasterService.diagnosisExists(doctorId, clinicId, shortDescription);
            Map<String, Object> result = new HashMap<>();
            result.put("exists", exists);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error checking if diagnosis exists {} for doctor {} and clinic {}: {}", shortDescription, doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to check diagnosis existence: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
