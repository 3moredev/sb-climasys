package com.climasys.lab.web;

import com.climasys.auth.annotation.RefreshSession;

import com.climasys.dto.LabTestResultRequest;
import com.climasys.dto.LabTestResultResponse;
import com.climasys.entity.PatientVisitLabTestResult;
import com.climasys.lab.service.LabTestResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Lab Test Result operations
 * Provides JPA-based endpoints equivalent to USP_Insert_LabTestAllData stored procedure
 * 
 * This controller handles the lab test result submission functionality shown in the modal
 */
@RestController
@RequestMapping("/api/lab/results")
@Tag(name = "Lab Test Results", description = "Lab Test Result management APIs - equivalent to USP_Insert_LabTestAllData")
@RefreshSession
public class LabTestResultController {

    @Autowired
    private LabTestResultService labTestResultService;

    /**
     * Submit lab test results for a patient visit
     * Equivalent to USP_Insert_LabTestAllData stored procedure
     * This endpoint handles the "Submit" button click from the lab results modal
     */
    @Operation(
        summary = "Submit Lab Test Results",
        description = "Saves lab test results for a patient visit. Equivalent to USP_Insert_LabTestAllData stored procedure. " +
                     "This endpoint handles the Submit button click from the lab results entry modal."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test results saved successfully",
            content = @Content(schema = @Schema(implementation = LabTestResultResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid input data",
            content = @Content(schema = @Schema(implementation = LabTestResultResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = LabTestResultResponse.class)))
    })
    @PostMapping("/submit")
    public ResponseEntity<LabTestResultResponse> submitLabTestResults(
            @Parameter(description = "Lab test result data", required = true)
            @Valid @RequestBody LabTestResultRequest request) {
        
        try {
            LabTestResultResponse response = labTestResultService.saveLabTestResults(request);
            
            if (response.success()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            LabTestResultResponse errorResponse = LabTestResultResponse.error(
                "Failed to submit lab test results: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Get lab test results for a specific patient visit
     */
    @Operation(
        summary = "Get Lab Test Results for Visit",
        description = "Retrieves all lab test results for a specific patient visit"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test results retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid parameters"),
        @ApiResponse(responseCode = "404", description = "No lab test results found")
    })
    @GetMapping("/visit")
    public ResponseEntity<?> getLabTestResultsForVisit(
            @Parameter(description = "Patient ID", required = true, example = "P001")
            @RequestParam String patientId,
            
            @Parameter(description = "Patient Visit Number", required = true, example = "1")
            @RequestParam Integer patientVisitNo,
            
            @Parameter(description = "Shift ID", required = true, example = "1")
            @RequestParam Short shiftId,
            
            @Parameter(description = "Clinic ID", required = true, example = "C001")
            @RequestParam String clinicId,
            
            @Parameter(description = "Doctor ID", required = true, example = "D001")
            @RequestParam String doctorId,
            
            @Parameter(description = "Visit Date (supports multiple formats: ISO date-time or YYYY-MM-DD HH:mm:ss). Will be used to find the actual visit and match exact visit date.", required = true, example = "2024-01-15T10:30:00")
            @RequestParam String visitDateStr) {
        
        try {
            // Parse visit date - handle multiple formats
            // Note: URL encoding may convert spaces to + signs, so we need to handle that
            String visitDateStrDecoded = visitDateStr != null ? visitDateStr.replace("+", " ") : null;
            LocalDateTime providedVisitDate = null;
            if (visitDateStrDecoded != null && !visitDateStrDecoded.trim().isEmpty()) {
                try {
                    // Try ISO format first (YYYY-MM-DDTHH:mm:ss) - preferred format
                    providedVisitDate = LocalDateTime.parse(visitDateStrDecoded);
                } catch (Exception e1) {
                    try {
                        // Try with space separator converted to ISO (YYYY-MM-DD HH:mm:ss -> YYYY-MM-DDTHH:mm:ss)
                        providedVisitDate = LocalDateTime.parse(visitDateStrDecoded.replace(" ", "T"));
                    } catch (Exception e2) {
                        try {
                            // Try with custom format (YYYY-MM-DD HH:mm:ss)
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            providedVisitDate = LocalDateTime.parse(visitDateStrDecoded, formatter);
                        } catch (Exception e3) {
                            return ResponseEntity.badRequest().body(
                                LabTestResultResponse.error("Invalid visit date format. Expected: yyyy-MM-ddTHH:mm:ss or yyyy-MM-dd HH:mm:ss. Received: " + visitDateStr));
                        }
                    }
                }
            } else {
                return ResponseEntity.badRequest().body(
                    LabTestResultResponse.error("Visit date is required"));
            }
            
            // CRITICAL: Find the actual visit first to get the exact visit date used when saving
            // This ensures we match the exact visit date (which may differ from the provided date)
            // and handles UTC conversion correctly
            List<PatientVisitLabTestResult> results = labTestResultService.getLabTestResultsWithExactVisitDate(
                    patientId, patientVisitNo, shiftId, clinicId, doctorId, providedVisitDate);
            
            if (results.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                LabTestResultResponse.error("Failed to retrieve lab test results: " + e.getMessage()));
        }
    }

    /**
     * Get all lab test results for a patient
     */
    @Operation(
        summary = "Get All Lab Test Results for Patient",
        description = "Retrieves all lab test results for a specific patient across all visits"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test results retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid patient ID"),
        @ApiResponse(responseCode = "404", description = "No lab test results found")
    })
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientLabTestResults(
            @Parameter(description = "Patient ID", required = true, example = "P001")
            @PathVariable String patientId) {
        
        try {
            List<PatientVisitLabTestResult> results = labTestResultService.getPatientLabTestResults(patientId);
            
            if (results.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                LabTestResultResponse.error("Failed to retrieve patient lab test results: " + e.getMessage()));
        }
    }

    /**
     * Delete lab test results for a specific patient visit
     */
    @Operation(
        summary = "Delete Lab Test Results for Visit",
        description = "Soft deletes all lab test results for a specific patient visit"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test results deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid parameters"),
        @ApiResponse(responseCode = "404", description = "No lab test results found to delete")
    })
    @DeleteMapping("/visit")
    public ResponseEntity<?> deleteLabTestResultsForVisit(
            @Parameter(description = "Patient ID", required = true, example = "P001")
            @RequestParam String patientId,
            
            @Parameter(description = "Patient Visit Number", required = true, example = "1")
            @RequestParam Integer patientVisitNo,
            
            @Parameter(description = "Shift ID", required = true, example = "1")
            @RequestParam Short shiftId,
            
            @Parameter(description = "Clinic ID", required = true, example = "C001")
            @RequestParam String clinicId,
            
            @Parameter(description = "Doctor ID", required = true, example = "D001")
            @RequestParam String doctorId,
            
            @Parameter(description = "Visit Date", required = true, example = "2024-01-15T10:30:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime visitDate,
            
            @Parameter(description = "User ID performing the deletion", required = true, example = "admin")
            @RequestParam String userId) {
        
        try {
            boolean deleted = labTestResultService.deleteLabTestResults(
                    patientId, patientVisitNo, shiftId, clinicId, doctorId, visitDate, userId);
            
            if (deleted) {
                return ResponseEntity.ok(LabTestResultResponse.success(
                        patientId, patientVisitNo, doctorId, clinicId, shiftId, visitDate, 0, 0));
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                LabTestResultResponse.error("Failed to delete lab test results: " + e.getMessage()));
        }
    }

    /**
     * Delete a specific lab test result parameter
     * Equivalent to USP_Delete_LabtestParameter stored procedure
     * This endpoint handles the trash can icon click from the lab results modal
     */
    @Operation(
        summary = "Delete Lab Test Result Parameter",
        description = "Soft deletes a specific lab test result parameter. Equivalent to USP_Delete_LabtestParameter stored procedure. " +
                     "This endpoint handles the trash can icon click from the lab results entry modal."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test result parameter deleted successfully",
            content = @Content(schema = @Schema(implementation = LabTestResultResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid parameters",
            content = @Content(schema = @Schema(implementation = LabTestResultResponse.class))),
        @ApiResponse(responseCode = "404", description = "Lab test result parameter not found",
            content = @Content(schema = @Schema(implementation = LabTestResultResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = LabTestResultResponse.class)))
    })
    @DeleteMapping("/parameter")
    public ResponseEntity<?> deleteLabTestResultParameter(
            @Parameter(description = "Patient ID", required = true, example = "P001")
            @RequestParam(required = false) String patientId,
            
            @Parameter(description = "Patient Visit Number", required = true, example = "1")
            @RequestParam(required = false) Integer patientVisitNo,
            
            @Parameter(description = "Shift ID", required = true, example = "1")
            @RequestParam(required = false) Short shiftId,
            
            @Parameter(description = "Clinic ID", required = true, example = "C001")
            @RequestParam(required = false) String clinicId,
            
            @Parameter(description = "Doctor ID", required = true, example = "D001")
            @RequestParam(required = false) String doctorId,
            
            @Parameter(description = "Visit Date", required = true, example = "2024-01-15T10:30:00")
            @RequestParam(required = false) String visitDateStr,
            
            @Parameter(description = "Lab Test Description", required = true, example = "Complete Blood Count")
            @RequestParam(required = false) String labTestDescription,
            
            @Parameter(description = "Parameter Name", required = true, example = "Hemoglobin")
            @RequestParam(required = false) String parameterName,
            
            @Parameter(description = "User ID performing the deletion", required = true, example = "admin")
            @RequestParam(required = false) String userId) {
        
        try {
            // Validate required parameters
            List<String> missingParams = new ArrayList<>();
            if (patientId == null || patientId.trim().isEmpty()) missingParams.add("patientId");
            if (patientVisitNo == null) missingParams.add("patientVisitNo");
            if (shiftId == null) missingParams.add("shiftId");
            if (clinicId == null || clinicId.trim().isEmpty()) missingParams.add("clinicId");
            if (doctorId == null || doctorId.trim().isEmpty()) missingParams.add("doctorId");
            if (visitDateStr == null || visitDateStr.trim().isEmpty()) missingParams.add("visitDate");
            if (labTestDescription == null || labTestDescription.trim().isEmpty()) missingParams.add("labTestDescription");
            if (parameterName == null || parameterName.trim().isEmpty()) missingParams.add("parameterName");
            if (userId == null || userId.trim().isEmpty()) missingParams.add("userId");
            
            if (!missingParams.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    LabTestResultResponse.error("Missing required parameters: " + String.join(", ", missingParams)));
            }
            
            // Parse visit date with multiple format support
            LocalDateTime visitDate;
            try {
                // Try ISO format first
                visitDate = LocalDateTime.parse(visitDateStr);
            } catch (Exception e1) {
                try {
                    // Try with space separator
                    visitDate = LocalDateTime.parse(visitDateStr.replace(" ", "T"));
                } catch (Exception e2) {
                    try {
                        // Try with custom format
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        visitDate = LocalDateTime.parse(visitDateStr, formatter);
                    } catch (Exception e3) {
                        return ResponseEntity.badRequest().body(
                            LabTestResultResponse.error("Invalid visit date format. Expected: yyyy-MM-ddTHH:mm:ss or yyyy-MM-dd HH:mm:ss. Received: " + visitDateStr));
                    }
                }
            }
            
            boolean deleted = labTestResultService.deleteLabTestResultParameter(
                    patientId, patientVisitNo, shiftId, clinicId, doctorId, visitDate, 
                    labTestDescription, parameterName, userId);
            
            if (deleted) {
                return ResponseEntity.ok(LabTestResultResponse.success(
                        patientId, patientVisitNo, doctorId, clinicId, shiftId, visitDate, 0, 0));
            } else {
                return ResponseEntity.status(404).body(
                    LabTestResultResponse.error("Lab test result parameter not found"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                LabTestResultResponse.error("Failed to delete lab test result parameter: " + e.getMessage()));
        }
    }

    /**
     * Test endpoint to validate parameters
     */
    @Operation(
        summary = "Test Delete Parameter Endpoint",
        description = "Test endpoint to validate parameters for delete operation"
    )
    @GetMapping("/parameter/test")
    public ResponseEntity<?> testDeleteParameter(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) Integer patientVisitNo,
            @RequestParam(required = false) Short shiftId,
            @RequestParam(required = false) String clinicId,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String visitDateStr,
            @RequestParam(required = false) String labTestDescription,
            @RequestParam(required = false) String parameterName,
            @RequestParam(required = false) String userId) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("received_parameters", Map.of(
            "patientId", patientId,
            "patientVisitNo", patientVisitNo,
            "shiftId", shiftId,
            "clinicId", clinicId,
            "doctorId", doctorId,
            "visitDateStr", visitDateStr,
            "labTestDescription", labTestDescription,
            "parameterName", parameterName,
            "userId", userId
        ));
        
        // Validate required parameters
        List<String> missingParams = new ArrayList<>();
        if (patientId == null || patientId.trim().isEmpty()) missingParams.add("patientId");
        if (patientVisitNo == null) missingParams.add("patientVisitNo");
        if (shiftId == null) missingParams.add("shiftId");
        if (clinicId == null || clinicId.trim().isEmpty()) missingParams.add("clinicId");
        if (doctorId == null || doctorId.trim().isEmpty()) missingParams.add("doctorId");
        if (visitDateStr == null || visitDateStr.trim().isEmpty()) missingParams.add("visitDate");
        if (labTestDescription == null || labTestDescription.trim().isEmpty()) missingParams.add("labTestDescription");
        if (parameterName == null || parameterName.trim().isEmpty()) missingParams.add("parameterName");
        if (userId == null || userId.trim().isEmpty()) missingParams.add("userId");
        
        response.put("missing_parameters", missingParams);
        response.put("validation_status", missingParams.isEmpty() ? "PASS" : "FAIL");
        
        // Test date parsing
        if (visitDateStr != null && !visitDateStr.trim().isEmpty()) {
            try {
                LocalDateTime visitDate = LocalDateTime.parse(visitDateStr);
                response.put("date_parsing", "SUCCESS");
                response.put("parsed_date", visitDate.toString());
            } catch (Exception e1) {
                try {
                    LocalDateTime visitDate = LocalDateTime.parse(visitDateStr.replace(" ", "T"));
                    response.put("date_parsing", "SUCCESS (with space replacement)");
                    response.put("parsed_date", visitDate.toString());
                } catch (Exception e2) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime visitDate = LocalDateTime.parse(visitDateStr, formatter);
                        response.put("date_parsing", "SUCCESS (with custom format)");
                        response.put("parsed_date", visitDate.toString());
                    } catch (Exception e3) {
                        response.put("date_parsing", "FAILED");
                        response.put("date_error", e3.getMessage());
                    }
                }
            }
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific lab test result parameter
     */
    @Operation(
        summary = "Get Lab Test Result Parameter",
        description = "Retrieves a specific lab test result parameter for a patient visit"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test result parameter retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid parameters"),
        @ApiResponse(responseCode = "404", description = "Lab test result parameter not found")
    })
    @GetMapping("/parameter")
    public ResponseEntity<?> getLabTestResultParameter(
            @Parameter(description = "Patient ID", required = true, example = "P001")
            @RequestParam String patientId,
            
            @Parameter(description = "Patient Visit Number", required = true, example = "1")
            @RequestParam Integer patientVisitNo,
            
            @Parameter(description = "Shift ID", required = true, example = "1")
            @RequestParam Short shiftId,
            
            @Parameter(description = "Clinic ID", required = true, example = "C001")
            @RequestParam String clinicId,
            
            @Parameter(description = "Doctor ID", required = true, example = "D001")
            @RequestParam String doctorId,
            
            @Parameter(description = "Visit Date", required = true, example = "2024-01-15T10:30:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime visitDate,
            
            @Parameter(description = "Lab Test Description", required = true, example = "Complete Blood Count")
            @RequestParam String labTestDescription,
            
            @Parameter(description = "Parameter Name", required = true, example = "Hemoglobin")
            @RequestParam String parameterName) {
        
        try {
            PatientVisitLabTestResult result = labTestResultService.getLabTestResultParameter(
                    patientId, patientVisitNo, shiftId, clinicId, doctorId, visitDate, 
                    labTestDescription, parameterName);
            
            if (result == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                LabTestResultResponse.error("Failed to retrieve lab test result parameter: " + e.getMessage()));
        }
    }
}