package com.climasys.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple complaint controller that doesn't require database access
 * For testing basic functionality
 */
@RestController
@RequestMapping("/api/complaint-mock")
public class ComplaintSimpleController {

    /**
     * Get mock complaints for operator display
     * GET /api/complaint-mock/operator-visible/{doctorId}
     */
    @GetMapping("/operator-visible/{doctorId}")
    public ResponseEntity<?> getMockComplaintsForOperatorDisplay(@PathVariable String doctorId) {
        try {
            // Return mock data in the same format as the stored procedure
            List<Map<String, Object>> mockComplaints = List.of(
                Map.of(
                    "id", "Headache*Patient complains of severe headache",
                    "short_description", "Headache",
                    "complaint_description", "Patient complains of severe headache",
                    "priority_value", 1,
                    "display_to_operator", 1
                ),
                Map.of(
                    "id", "Fever*High temperature and body aches",
                    "short_description", "Fever",
                    "complaint_description", "High temperature and body aches",
                    "priority_value", 2,
                    "display_to_operator", 1
                ),
                Map.of(
                    "id", "Cough*Persistent dry cough",
                    "short_description", "Cough",
                    "complaint_description", "Persistent dry cough",
                    "priority_value", 3,
                    "display_to_operator", 1
                )
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("doctor_id", doctorId);
            response.put("complaints", mockComplaints);
            response.put("count", mockComplaints.size());
            response.put("message", "Mock data - database not accessible");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get mock complaints: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Health check
     * GET /api/complaint-mock/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Complaint Simple Service");
        response.put("message", "Mock complaint endpoints are available");
        return ResponseEntity.ok(response);
    }

    /**
     * Test database connectivity
     * GET /api/complaint-mock/db-test
     */
    @GetMapping("/db-test")
    public ResponseEntity<?> testDatabase() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "This endpoint doesn't require database access");
        response.put("note", "Use this to test if the issue is database-related");
        return ResponseEntity.ok(response);
    }
}
