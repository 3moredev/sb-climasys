package com.climasys.trends.web;

import com.climasys.auth.annotation.RefreshSession;

import com.climasys.trends.dto.PatientTrendsDTO;
import com.climasys.trends.service.PatientTrendsService;
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
 * REST Controller for Patient Trends
 * Provides endpoints for retrieving patient vital signs history and trends
 */
@RestController
@RequestMapping("/api/trends")
@Tag(name = "Patient Trends", description = "APIs for retrieving patient vital signs history and clinical measurement trends")
@RefreshSession
public class PatientTrendsController {
    
    private static final Logger logger = LoggerFactory.getLogger(PatientTrendsController.class);
    
    @Autowired
    private PatientTrendsService patientTrendsService;
    
    /**
     * Get patient's trends from previous visits (last 5 visits)
     * GET /api/trends/patients/{patientId}/previous
     * 
     * This endpoint replicates the stored procedure USP_Get_PatientLastBPDetails
     */
    @GetMapping("/patients/{patientId}/previous")
    @Operation(
        summary = "Get patient's vital signs trends",
        description = "Retrieves the last 5 visits' vital signs including BP, Sugar, TH, Weight, Height, and other clinical measurements. Shows trends over time. Excludes the current visit."
    )
    public ResponseEntity<?> getPatientTrends(
            @Parameter(description = "Patient ID", example = "11-02-2019-020500", required = true)
            @PathVariable String patientId,
            
            @Parameter(description = "Doctor ID", example = "DR-00010")
            @RequestParam(required = false) String doctorId,
            
            @Parameter(description = "Clinic ID", example = "CL-00001", required = true)
            @RequestParam String clinicId,
            
            @Parameter(description = "Shift ID", example = "1", required = true)
            @RequestParam Short shiftId,
            
            @Parameter(description = "Current visit date (YYYY-MM-DD)", example = "2019-02-11", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate visitDate,
            
            @Parameter(description = "Current patient visit number", example = "1", required = true)
            @RequestParam Integer patientVisitNo) {
        
        try {
            logger.info("Getting trends for patient: {}, visit: {}", patientId, patientVisitNo);
            
            List<PatientTrendsDTO> trends = patientTrendsService.getPatientTrends(
                    patientId, doctorId, clinicId, shiftId, visitDate, patientVisitNo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Patient trends retrieved successfully");
            response.put("data", trends);
            response.put("count", trends.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting trends for patient {}: {}", patientId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get patient trends: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get patient's trends using path parameters
     * GET /api/trends/patients/{patientId}/visits/{visitNo}/previous
     * 
     * Alternative endpoint with cleaner URL structure
     */
    @GetMapping("/patients/{patientId}/visits/{visitNo}/previous")
    @Operation(
        summary = "Get patient's trends (path params)",
        description = "Alternative endpoint: Retrieves the last 5 visits' vital signs trends. Uses path parameters for better REST design."
    )
    public ResponseEntity<?> getPatientTrendsAlt(
            @Parameter(description = "Patient ID", example = "11-02-2019-020500", required = true)
            @PathVariable String patientId,
            
            @Parameter(description = "Current patient visit number", example = "1", required = true)
            @PathVariable Integer visitNo,
            
            @Parameter(description = "Doctor ID", example = "DR-00010")
            @RequestParam(required = false) String doctorId,
            
            @Parameter(description = "Clinic ID", example = "CL-00001", required = true)
            @RequestParam String clinicId,
            
            @Parameter(description = "Shift ID", example = "1", required = true)
            @RequestParam Short shiftId,
            
            @Parameter(description = "Current visit date (YYYY-MM-DD)", example = "2019-02-11", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate visitDate) {
        
        try {
            logger.info("Getting trends (alt) for patient: {}, visit: {}", patientId, visitNo);
            
            List<PatientTrendsDTO> trends = patientTrendsService.getPatientTrends(
                    patientId, doctorId, clinicId, shiftId, visitDate, visitNo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Patient trends retrieved successfully");
            response.put("data", trends);
            response.put("count", trends.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting trends (alt) for patient {}: {}", patientId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get patient trends: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
