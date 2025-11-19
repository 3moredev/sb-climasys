package com.climasys.web;

import com.climasys.entity.ComplaintMaster;
import com.climasys.service.ComplaintMasterService;
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
 * REST Controller for ComplaintMaster management
 * Provides endpoints for managing complaint master data with operator display filtering
 */
@RestController
@RequestMapping("/api/complaint-master")
public class ComplaintMasterController {

    private static final Logger logger = LoggerFactory.getLogger(ComplaintMasterController.class);

    @Autowired
    private ComplaintMasterService complaintMasterService;

    /**
     * Get all complaints for a specific doctor and clinic
     * GET /api/complaint-master/doctor/{doctorId}/clinic/{clinicId}
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}")
    public ResponseEntity<?> getAllComplaintsForDoctorAndClinic(
            @PathVariable String doctorId, 
            @PathVariable String clinicId) {
        try {
            logger.info("Getting all complaints for doctor: {} and clinic: {}", doctorId, clinicId);
            List<ComplaintMaster> complaints = complaintMasterService.getAllComplaintsForDoctorAndClinic(doctorId, clinicId);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            logger.error("Error getting complaints for doctor {} and clinic {}: {}", doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get complaints: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all complaints for a specific doctor (backward compatibility)
     * GET /api/complaint-master/doctor/{doctorId}
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getAllComplaintsForDoctor(@PathVariable String doctorId) {
        try {
            logger.info("Getting all complaints for doctor: {}", doctorId);
            List<ComplaintMaster> complaints = complaintMasterService.getAllComplaintsForDoctor(doctorId);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            logger.error("Error getting complaints for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get complaints: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get complaints that are visible to operators for a specific doctor and clinic
     * This is the main endpoint that replicates the stored procedure functionality
     * GET /api/complaint-master/doctor/{doctorId}/clinic/{clinicId}/operator-visible
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}/operator-visible")
    public ResponseEntity<?> getComplaintsForOperatorDisplayByDoctorAndClinic(
            @PathVariable String doctorId, 
            @PathVariable String clinicId) {
        try {
            logger.info("Getting complaints for operator display for doctor: {} and clinic: {}", doctorId, clinicId);
            List<ComplaintMaster> complaints = complaintMasterService.getComplaintsForOperatorDisplayByDoctorAndClinic(doctorId, clinicId);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            logger.error("Error getting operator visible complaints for doctor {} and clinic {}: {}", doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get operator visible complaints: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get complaints that are visible to operators for a specific doctor (backward compatibility)
     * This is the main endpoint that replicates the stored procedure functionality
     * GET /api/complaint-master/doctor/{doctorId}/operator-visible
     */
    @GetMapping("/doctor/{doctorId}/operator-visible")
    public ResponseEntity<?> getComplaintsForOperatorDisplay(@PathVariable String doctorId) {
        try {
            logger.info("Getting complaints for operator display for doctor: {}", doctorId);
            List<ComplaintMaster> complaints = complaintMasterService.getComplaintsForOperatorDisplay(doctorId);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            logger.error("Error getting operator visible complaints for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get operator visible complaints: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get complaints for operator display in the same format as stored procedure for doctor and clinic
     * Returns data with concatenated ID field for backward compatibility
     * GET /api/complaint-master/doctor/{doctorId}/clinic/{clinicId}/operator-visible/formatted
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}/operator-visible/formatted")
    public ResponseEntity<?> getComplaintsForOperatorDisplayFormattedByDoctorAndClinic(
            @PathVariable String doctorId, 
            @PathVariable String clinicId) {
        try {
            logger.info("Getting formatted complaints for operator display for doctor: {} and clinic: {}", doctorId, clinicId);
            List<Map<String, Object>> complaints = complaintMasterService.getComplaintsForOperatorDisplayFormattedByDoctorAndClinic(doctorId, clinicId);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            logger.error("Error getting formatted operator visible complaints for doctor {} and clinic {}: {}", doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get formatted operator visible complaints: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get complaints for operator display in the same format as stored procedure (backward compatibility)
     * Returns data with concatenated ID field for backward compatibility
     * GET /api/complaint-master/doctor/{doctorId}/operator-visible/formatted
     */
    @GetMapping("/doctor/{doctorId}/operator-visible/formatted")
    public ResponseEntity<?> getComplaintsForOperatorDisplayFormatted(@PathVariable String doctorId) {
        try {
            logger.info("Getting formatted complaints for operator display for doctor: {}", doctorId);
            List<Map<String, Object>> complaints = complaintMasterService.getComplaintsForOperatorDisplayFormatted(doctorId);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            logger.error("Error getting formatted operator visible complaints for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get formatted operator visible complaints: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all complaint data for a clinic in formatted way (including non-operator visible)
     * GET /api/complaint-master/clinic/{clinicId}/formatted?doctorId={doctorId}
     */
    @GetMapping("/clinic/{clinicId}/formatted")
    public ResponseEntity<?> getAllComplaintsForDoctorFormatted(
            @PathVariable String clinicId,
            @RequestParam(required = false) String doctorId) {
        try {
            logger.info("Getting all formatted complaints for clinic: {} and doctor: {}", clinicId, doctorId);
            List<Map<String, Object>> complaints = complaintMasterService.getAllComplaintsForDoctorFormatted(clinicId, doctorId);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            logger.error("Error getting all formatted complaints for clinic {} and doctor {}: {}", clinicId, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get all formatted complaints: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Search complaints by description for a specific doctor
     * GET /api/complaint-master/doctor/{doctorId}/search?term={searchTerm}
     */
    @GetMapping("/doctor/{doctorId}/search")
    public ResponseEntity<?> searchComplaintsByDescription(
            @PathVariable String doctorId,
            @RequestParam String term) {
        try {
            logger.info("Searching complaints for doctor: {} with term: {}", doctorId, term);
            List<ComplaintMaster> complaints = complaintMasterService.searchComplaintsByDescription(doctorId, term);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            logger.error("Error searching complaints for doctor {} with term {}: {}", doctorId, term, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search complaints: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Search complaints for operator display by description
     * GET /api/complaint-master/doctor/{doctorId}/operator-visible/search?term={searchTerm}
     */
    @GetMapping("/doctor/{doctorId}/operator-visible/search")
    public ResponseEntity<?> searchComplaintsForOperatorDisplay(
            @PathVariable String doctorId,
            @RequestParam String term) {
        try {
            logger.info("Searching operator visible complaints for doctor: {} with term: {}", doctorId, term);
            List<ComplaintMaster> complaints = complaintMasterService.searchComplaintsForOperatorDisplay(doctorId, term);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            logger.error("Error searching operator visible complaints for doctor {} with term {}: {}", doctorId, term, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search operator visible complaints: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get a specific complaint by short description, doctor ID, and clinic ID
     * GET /api/complaint-master/doctor/{doctorId}/clinic/{clinicId}/complaint/{shortDescription}
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}/complaint/{shortDescription}")
    public ResponseEntity<?> getComplaintByShortDescription(
            @PathVariable String doctorId,
            @PathVariable String clinicId,
            @PathVariable String shortDescription) {
        try {
            logger.info("Getting complaint: {} for doctor: {} and clinic: {}", shortDescription, doctorId, clinicId);
            Optional<ComplaintMaster> complaint = complaintMasterService.getComplaintByShortDescription(shortDescription, doctorId, clinicId);
            if (complaint.isPresent()) {
                return ResponseEntity.ok(complaint.get());
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Complaint not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting complaint {} for doctor {} and clinic {}: {}", shortDescription, doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get complaint: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Create a new complaint
     * POST /api/complaint-master
     */
    @PostMapping
    public ResponseEntity<?> createComplaint(@RequestBody ComplaintMaster complaint) {
        try {
            logger.info("Creating new complaint: {}", complaint.getShortDescription());
            ComplaintMaster createdComplaint = complaintMasterService.createComplaint(complaint);
            return ResponseEntity.ok(createdComplaint);
        } catch (Exception e) {
            logger.error("Error creating complaint: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create complaint: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update an existing complaint
     * PUT /api/complaint-master
     */
    @PutMapping
    public ResponseEntity<?> updateComplaint(@RequestBody ComplaintMaster complaint) {
        try {
            logger.info("Updating complaint: {}", complaint.getShortDescription());
            ComplaintMaster updatedComplaint = complaintMasterService.updateComplaint(complaint);
            return ResponseEntity.ok(updatedComplaint);
        } catch (Exception e) {
            logger.error("Error updating complaint: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to update complaint: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete a complaint
     * DELETE /api/complaint-master/doctor/{doctorId}/clinic/{clinicId}/complaint/{shortDescription}
     */
    @DeleteMapping("/doctor/{doctorId}/clinic/{clinicId}/complaint/{shortDescription}")
    public ResponseEntity<?> deleteComplaint(
            @PathVariable String doctorId,
            @PathVariable String clinicId,
            @PathVariable String shortDescription) {
        try {
            logger.info("Deleting complaint: {} for doctor: {} and clinic: {}", shortDescription, doctorId, clinicId);
            boolean deleted = complaintMasterService.deleteComplaint(shortDescription, doctorId, clinicId);
            if (deleted) {
                Map<String, Object> result = new HashMap<>();
                result.put("message", "Complaint deleted successfully");
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Complaint not found or access denied");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting complaint {} for doctor {} and clinic {}: {}", shortDescription, doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to delete complaint: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Toggle display to operator flag for a complaint
     * PATCH /api/complaint-master/doctor/{doctorId}/clinic/{clinicId}/complaint/{shortDescription}/toggle-operator-display
     */
    @PatchMapping("/doctor/{doctorId}/clinic/{clinicId}/complaint/{shortDescription}/toggle-operator-display")
    public ResponseEntity<?> toggleDisplayToOperator(
            @PathVariable String doctorId,
            @PathVariable String clinicId,
            @PathVariable String shortDescription,
            @RequestParam boolean displayToOperator) {
        try {
            logger.info("Toggling display to operator for complaint: {} to {} for doctor: {} and clinic: {}", 
                       shortDescription, displayToOperator, doctorId, clinicId);
            Optional<ComplaintMaster> updatedComplaint = complaintMasterService.toggleDisplayToOperator(
                    shortDescription, doctorId, clinicId, displayToOperator);
            if (updatedComplaint.isPresent()) {
                return ResponseEntity.ok(updatedComplaint.get());
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Complaint not found or access denied");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error toggling display to operator for complaint {} for doctor {} and clinic {}: {}", 
                        shortDescription, doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to toggle display to operator: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get complaint statistics for a doctor
     * GET /api/complaint-master/doctor/{doctorId}/statistics
     */
    @GetMapping("/doctor/{doctorId}/statistics")
    public ResponseEntity<?> getComplaintStatistics(@PathVariable String doctorId) {
        try {
            logger.info("Getting complaint statistics for doctor: {}", doctorId);
            Map<String, Object> statistics = complaintMasterService.getComplaintStatistics(doctorId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error getting complaint statistics for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get complaint statistics: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Check if a complaint exists for a doctor
     * GET /api/complaint-master/doctor/{doctorId}/complaint/{shortDescription}/exists
     */
    @GetMapping("/doctor/{doctorId}/complaint/{shortDescription}/exists")
    public ResponseEntity<?> checkComplaintExists(
            @PathVariable String doctorId,
            @PathVariable String shortDescription) {
        try {
            logger.info("Checking if complaint exists: {} for doctor: {}", shortDescription, doctorId);
            boolean exists = complaintMasterService.complaintExists(doctorId, shortDescription);
            Map<String, Object> result = new HashMap<>();
            result.put("exists", exists);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error checking if complaint exists {} for doctor {}: {}", shortDescription, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to check complaint existence: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
