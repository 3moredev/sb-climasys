package com.climasys.trends.web;

import com.climasys.auth.annotation.RefreshSession;

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
@RefreshSession
public class LabTrendsController {
    
    private static final Logger logger = LoggerFactory.getLogger(LabTrendsController.class);
    
    @Autowired
    private LabTrendsService labTrendsService;
    
    /**
     * Get all lab test results for a patient (all previous visits)
     * GET /api/trends/lab/patients/{patientId}/results
     * 
     * This endpoint replicates the stored procedure USP_Get_LabTestDetails12
     * Returns all previous lab test results for the patient across all visit dates
     * Matches the Lab Trend popup behavior shown in the UI
     * Filtered by clinic_id for multi-clinic isolation
     */
    @GetMapping("/patients/{patientId}/results")
    @Operation(
        summary = "Get all patient's lab test results",
        description = "Retrieves all previous lab test results for a patient across all visit dates. " +
                     "Based on USP_Get_LabTestDetails12 stored procedure. " +
                     "Returns results ordered by visit date descending (most recent first). " +
                     "Filtered by clinic_id for multi-clinic isolation."
    )
    public ResponseEntity<?> getLabTrends(
            @Parameter(description = "Patient ID", example = "01-10-2021-051429", required = true)
            @PathVariable String patientId,
            
            @Parameter(description = "Clinic ID", example = "CL-00001", required = true)
            @RequestParam String clinicId) {
        
        try {
            logger.info("Getting all lab trends for patient: {}, clinic: {}", patientId, clinicId);
            
            List<LabTrendDTO> labTrends = labTrendsService.getAllLabTrendsForPatient(patientId, clinicId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lab test results retrieved successfully");
            response.put("data", labTrends);
            response.put("count", labTrends.size());
            response.put("clinicId", clinicId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting lab trends for patient {} in clinic {}: {}", patientId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get lab test results: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get lab test results for a specific patient visit (date-specific)
     * GET /api/trends/lab/patients/{patientId}/visits/{visitNo}/results
     * 
     * This endpoint replicates the stored procedure USP_Get_PreviousLabReports
     * Returns lab test results for a specific visit date only
     */
    @GetMapping("/patients/{patientId}/visits/{visitNo}/results")
    @Operation(
        summary = "Get patient's lab test results for a specific visit",
        description = "Retrieves lab test results for a specific visit date including test descriptions, parameters, and values. " +
                     "Based on USP_Get_PreviousLabReports stored procedure."
    )
    public ResponseEntity<?> getLabTrendsForVisit(
            @Parameter(description = "Patient ID", example = "01-10-2021-051429", required = true)
            @PathVariable String patientId,
            
            @Parameter(description = "Patient visit number", example = "7", required = true)
            @PathVariable Integer visitNo,
            
            @Parameter(description = "Doctor ID", example = "DR-00010", required = true)
            @RequestParam String doctorId,
            
            @Parameter(description = "Clinic ID", example = "CL-00001", required = true)
            @RequestParam String clinicId,
            
            @Parameter(description = "Visit date (YYYY-MM-DD)", example = "2025-10-29", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate visitDate,
            
            @Parameter(description = "Shift ID", example = "1", required = true)
            @RequestParam Short shiftId) {
        
        try {
            logger.info("Getting lab trends for patient: {}, visit: {} on {}", patientId, visitNo, visitDate);
            
            List<LabTrendDTO> labTrends = labTrendsService.getLabTrendsForVisit(
                    patientId, doctorId, clinicId, visitDate, shiftId, visitNo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lab test results retrieved successfully");
            response.put("data", labTrends);
            response.put("count", labTrends.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting lab trends for patient {} visit {}: {}", patientId, visitNo, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get lab test results: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
