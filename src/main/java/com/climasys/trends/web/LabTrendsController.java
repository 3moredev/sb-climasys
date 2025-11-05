package com.climasys.trends.web;

import com.climasys.trends.dto.LabTrendDTO;
import com.climasys.trends.service.LabTrendsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Lab Trends
 * Provides endpoints for retrieving patient lab test results and trends
 */
@RestController
@RequestMapping("/api/trends/lab")
@Tag(name = "Lab Trends", description = "APIs for retrieving patient lab test results and trends")
public class LabTrendsController {
    
    private static final Logger logger = LoggerFactory.getLogger(LabTrendsController.class);
    
    @Autowired
    private LabTrendsService labTrendsService;
    
    /**
     * Get lab test results for a patient visit
     * GET /api/trends/lab/patients/{patientId}/results
     * 
     * This endpoint replicates the stored procedure USP_Get_PreviousLabReports
     */
    @GetMapping("/patients/{patientId}/results")
    @Operation(
        summary = "Get patient's lab test results",
        description = "Retrieves lab test results for a specific visit including test descriptions, parameters, and values. Based on USP_Get_PreviousLabReports stored procedure."
    )
    public ResponseEntity<?> getLabTrends(
            @Parameter(description = "Patient ID", example = "01-10-2021-051429", required = true)
            @PathVariable String patientId,
            
            @Parameter(description = "Doctor ID", example = "DR-00010", required = true)
            @RequestParam String doctorId,
            
            @Parameter(description = "Clinic ID", example = "CL-00001", required = true)
            @RequestParam String clinicId,
            
            @Parameter(description = "Visit date (YYYY-MM-DD)", example = "2025-10-29", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate visitDate,
            
            @Parameter(description = "Shift ID", example = "1", required = true)
            @RequestParam Short shiftId,
            
            @Parameter(description = "Patient visit number", example = "7", required = true)
            @RequestParam Integer patientVisitNo) {
        
        try {
            logger.info("Getting lab trends for patient: {}, visit: {} on {}", patientId, patientVisitNo, visitDate);
            
            List<LabTrendDTO> labTrends = labTrendsService.getLabTrends(
                    patientId, doctorId, clinicId, visitDate, shiftId, patientVisitNo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lab test results retrieved successfully");
            response.put("data", labTrends);
            response.put("count", labTrends.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting lab trends for patient {}: {}", patientId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get lab test results: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

