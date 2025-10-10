package com.climasys.web;

import com.climasys.service.ComplaintMasterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple Complaint Controller for backward compatibility
 * Provides shorter endpoint paths for complaint-related operations
 */
@RestController
@RequestMapping("/api/complain")
public class ComplaintController {

    private static final Logger logger = LoggerFactory.getLogger(ComplaintController.class);

    @Autowired
    private ComplaintMasterService complaintMasterService;

    /**
     * Get complaints for operator display - shorter endpoint
     * GET /api/complain/operator-visible/{doctorId}
     */
    @GetMapping("/operator-visible/{doctorId}")
    public ResponseEntity<?> getComplaintsForOperatorDisplay(@PathVariable String doctorId) {
        try {
            logger.info("Getting complaints for operator display for doctor: {}", doctorId);
            List<Map<String, Object>> complaints = complaintMasterService.getComplaintsForOperatorDisplayFormatted(doctorId);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            logger.error("Error getting operator visible complaints for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get operator visible complaints: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all complaints for a doctor - shorter endpoint
     * GET /api/complain/all/{doctorId}
     */
    @GetMapping("/all/{doctorId}")
    public ResponseEntity<?> getAllComplaintsForDoctor(@PathVariable String doctorId) {
        try {
            logger.info("Getting all complaints for doctor: {}", doctorId);
            List<Map<String, Object>> complaints = complaintMasterService.getAllComplaintsForDoctorFormatted(doctorId);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            logger.error("Error getting all complaints for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get complaints: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Search complaints - shorter endpoint
     * GET /api/complain/search/{doctorId}?term={searchTerm}
     */
    @GetMapping("/search/{doctorId}")
    public ResponseEntity<?> searchComplaints(
            @PathVariable String doctorId,
            @RequestParam String term) {
        try {
            logger.info("Searching complaints for doctor: {} with term: {}", doctorId, term);
            List<Map<String, Object>> complaints = complaintMasterService.getComplaintsForOperatorDisplayFormatted(doctorId);
            // Filter by search term
            List<Map<String, Object>> filteredComplaints = complaints.stream()
                    .filter(complaint -> {
                        String shortDesc = (String) complaint.get("short_description");
                        String fullDesc = (String) complaint.get("complaint_description");
                        String searchLower = term.toLowerCase();
                        return (shortDesc != null && shortDesc.toLowerCase().contains(searchLower)) ||
                               (fullDesc != null && fullDesc.toLowerCase().contains(searchLower));
                    })
                    .toList();
            return ResponseEntity.ok(filteredComplaints);
        } catch (Exception e) {
            logger.error("Error searching complaints for doctor {} with term {}: {}", doctorId, term, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search complaints: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Health check endpoint
     * GET /api/complain/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Complaint Service");
        response.put("message", "Complaint endpoints are available");
        return ResponseEntity.ok(response);
    }

    /**
     * Get available endpoints
     * GET /api/complain/endpoints
     */
    @GetMapping("/endpoints")
    public ResponseEntity<?> getAvailableEndpoints() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Complaint Service");
        response.put("endpoints", Map.of(
            "operator-visible", "GET /api/complain/operator-visible/{doctorId}",
            "all-complaints", "GET /api/complain/all/{doctorId}",
            "search", "GET /api/complain/search/{doctorId}?term={searchTerm}",
            "health", "GET /api/complain/health",
            "full-api", "See /api/complaint-master for complete CRUD operations"
        ));
        return ResponseEntity.ok(response);
    }
}
