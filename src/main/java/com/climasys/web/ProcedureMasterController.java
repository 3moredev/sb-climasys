package com.climasys.web;

import com.climasys.entity.ProcedureFindings;
import com.climasys.entity.ProcedureMaster;
import com.climasys.service.ProcedureMasterService;
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
 * REST Controller for ProcedureMaster management
 * Provides endpoints for managing procedure master data and findings
 * Following the same pattern as ComplaintMasterController
 */
@RestController
@RequestMapping("/api/procedure-master")
public class ProcedureMasterController {

    private static final Logger logger = LoggerFactory.getLogger(ProcedureMasterController.class);

    @Autowired
    private ProcedureMasterService procedureMasterService;

    /**
     * Get all procedures for a specific doctor and clinic
     * GET /api/procedure-master/doctor/{doctorId}/clinic/{clinicId}
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}")
    public ResponseEntity<?> getAllProceduresForDoctorAndClinic(
            @PathVariable String doctorId, 
            @PathVariable String clinicId) {
        try {
            logger.info("Getting all procedures for doctor: {} and clinic: {}", doctorId, clinicId);
            List<ProcedureMaster> procedures = procedureMasterService.getAllProceduresForDoctorAndClinic(doctorId, clinicId);
            return ResponseEntity.ok(procedures);
        } catch (Exception e) {
            logger.error("Error getting procedures for doctor {} and clinic {}: {}", doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get procedures: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all procedures for a specific doctor (backward compatibility)
     * GET /api/procedure-master/doctor/{doctorId}
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getAllProceduresForDoctor(@PathVariable String doctorId) {
        try {
            logger.info("Getting all procedures for doctor: {}", doctorId);
            List<ProcedureMaster> procedures = procedureMasterService.getAllProceduresForDoctor(doctorId);
            return ResponseEntity.ok(procedures);
        } catch (Exception e) {
            logger.error("Error getting procedures for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get procedures: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Search procedures by description for a specific doctor and clinic
     * GET /api/procedure-master/doctor/{doctorId}/clinic/{clinicId}/search?term={searchTerm}
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}/search")
    public ResponseEntity<?> searchProceduresByDescription(
            @PathVariable String doctorId,
            @PathVariable String clinicId,
            @RequestParam String term) {
        try {
            logger.info("Searching procedures for doctor: {} and clinic: {} with term: {}", doctorId, clinicId, term);
            List<ProcedureMaster> procedures = procedureMasterService.searchProceduresByDescription(doctorId, clinicId, term);
            return ResponseEntity.ok(procedures);
        } catch (Exception e) {
            logger.error("Error searching procedures for doctor {} and clinic {} with term {}: {}", doctorId, clinicId, term, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search procedures: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Search procedures by description for a specific doctor (backward compatibility)
     * GET /api/procedure-master/doctor/{doctorId}/search?term={searchTerm}
     */
    @GetMapping("/doctor/{doctorId}/search")
    public ResponseEntity<?> searchProceduresByDescription(
            @PathVariable String doctorId,
            @RequestParam String term) {
        try {
            logger.info("Searching procedures for doctor: {} with term: {}", doctorId, term);
            List<ProcedureMaster> procedures = procedureMasterService.searchProceduresByDescription(doctorId, term);
            return ResponseEntity.ok(procedures);
        } catch (Exception e) {
            logger.error("Error searching procedures for doctor {} with term {}: {}", doctorId, term, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search procedures: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get a specific procedure by description, doctor ID, and clinic ID
     * GET /api/procedure-master/doctor/{doctorId}/clinic/{clinicId}/procedure/{procedureDescription}
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}/procedure/{procedureDescription}")
    public ResponseEntity<?> getProcedureByDescription(
            @PathVariable String doctorId,
            @PathVariable String clinicId,
            @PathVariable String procedureDescription) {
        try {
            logger.info("Getting procedure: {} for doctor: {} and clinic: {}", procedureDescription, doctorId, clinicId);
            Optional<ProcedureMaster> procedure = procedureMasterService.getProcedureByDescription(procedureDescription, doctorId, clinicId);
            if (procedure.isPresent()) {
                return ResponseEntity.ok(procedure.get());
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Procedure not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting procedure {} for doctor {} and clinic {}: {}", procedureDescription, doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get procedure: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Create a new procedure
     * POST /api/procedure-master
     */
    @PostMapping
    public ResponseEntity<?> createProcedure(@RequestBody ProcedureMaster procedure) {
        try {
            logger.info("Creating new procedure: {}", procedure.getProcedureDescription());
            ProcedureMaster createdProcedure = procedureMasterService.createProcedure(procedure);
            return ResponseEntity.ok(createdProcedure);
        } catch (Exception e) {
            logger.error("Error creating procedure: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create procedure: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update an existing procedure
     * PUT /api/procedure-master
     */
    @PutMapping
    public ResponseEntity<?> updateProcedure(@RequestBody ProcedureMaster procedure) {
        try {
            logger.info("Updating procedure: {}", procedure.getProcedureDescription());
            ProcedureMaster updatedProcedure = procedureMasterService.updateProcedure(procedure);
            return ResponseEntity.ok(updatedProcedure);
        } catch (Exception e) {
            logger.error("Error updating procedure: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to update procedure: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete a procedure
     * DELETE /api/procedure-master/doctor/{doctorId}/clinic/{clinicId}/procedure/{procedureDescription}
     */
    @DeleteMapping("/doctor/{doctorId}/clinic/{clinicId}/procedure/{procedureDescription}")
    public ResponseEntity<?> deleteProcedure(
            @PathVariable String doctorId,
            @PathVariable String clinicId,
            @PathVariable String procedureDescription) {
        try {
            logger.info("Deleting procedure: {} for doctor: {} and clinic: {}", procedureDescription, doctorId, clinicId);
            boolean deleted = procedureMasterService.deleteProcedure(procedureDescription, doctorId, clinicId);
            if (deleted) {
                Map<String, Object> result = new HashMap<>();
                result.put("message", "Procedure deleted successfully");
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Procedure not found or access denied");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting procedure {} for doctor {} and clinic {}: {}", procedureDescription, doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to delete procedure: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all findings for a procedure
     * GET /api/procedure-master/doctor/{doctorId}/procedure/{procedureDescription}/findings
     */
    @GetMapping("/doctor/{doctorId}/procedure/{procedureDescription}/findings")
    public ResponseEntity<?> getFindingsForProcedure(
            @PathVariable String doctorId,
            @PathVariable String procedureDescription) {
        try {
            logger.info("Getting findings for procedure: {} for doctor: {}", procedureDescription, doctorId);
            List<ProcedureFindings> findings = procedureMasterService.getFindingsForProcedure(doctorId, procedureDescription);
            return ResponseEntity.ok(findings);
        } catch (Exception e) {
            logger.error("Error getting findings for procedure {} for doctor {}: {}", procedureDescription, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get findings: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Add a finding to a procedure
     * POST /api/procedure-master/findings
     */
    @PostMapping("/findings")
    public ResponseEntity<?> addFinding(@RequestBody ProcedureFindings finding) {
        try {
            logger.info("Adding finding: {} to procedure: {}", finding.getFindingsDescription(), finding.getProcedureDescription());
            ProcedureFindings createdFinding = procedureMasterService.addFinding(finding);
            return ResponseEntity.ok(createdFinding);
        } catch (Exception e) {
            logger.error("Error adding finding: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to add finding: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete a finding from a procedure
     * DELETE /api/procedure-master/doctor/{doctorId}/procedure/{procedureDescription}/finding/{findingsDescription}
     */
    @DeleteMapping("/doctor/{doctorId}/procedure/{procedureDescription}/finding/{findingsDescription}")
    public ResponseEntity<?> deleteFinding(
            @PathVariable String doctorId,
            @PathVariable String procedureDescription,
            @PathVariable String findingsDescription) {
        try {
            logger.info("Deleting finding: {} from procedure: {} for doctor: {}", findingsDescription, procedureDescription, doctorId);
            boolean deleted = procedureMasterService.deleteFinding(doctorId, procedureDescription, findingsDescription);
            if (deleted) {
                Map<String, Object> result = new HashMap<>();
                result.put("message", "Finding deleted successfully");
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Finding not found or access denied");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting finding {} from procedure {} for doctor {}: {}", findingsDescription, procedureDescription, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to delete finding: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get procedure statistics for a doctor and clinic
     * GET /api/procedure-master/doctor/{doctorId}/clinic/{clinicId}/statistics
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}/statistics")
    public ResponseEntity<?> getProcedureStatistics(
            @PathVariable String doctorId,
            @PathVariable String clinicId) {
        try {
            logger.info("Getting procedure statistics for doctor: {} and clinic: {}", doctorId, clinicId);
            Map<String, Object> statistics = procedureMasterService.getProcedureStatistics(doctorId, clinicId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error getting procedure statistics for doctor {} and clinic {}: {}", doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get procedure statistics: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Check if a procedure exists for a doctor and clinic
     * GET /api/procedure-master/doctor/{doctorId}/clinic/{clinicId}/procedure/{procedureDescription}/exists
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}/procedure/{procedureDescription}/exists")
    public ResponseEntity<?> checkProcedureExists(
            @PathVariable String doctorId,
            @PathVariable String clinicId,
            @PathVariable String procedureDescription) {
        try {
            logger.info("Checking if procedure exists: {} for doctor: {} and clinic: {}", procedureDescription, doctorId, clinicId);
            boolean exists = procedureMasterService.procedureExists(doctorId, clinicId, procedureDescription);
            Map<String, Object> result = new HashMap<>();
            result.put("exists", exists);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error checking if procedure exists {} for doctor {} and clinic {}: {}", procedureDescription, doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to check procedure existence: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

