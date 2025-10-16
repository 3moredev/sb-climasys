package com.climasys.lab.web;

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
import java.util.List;

/**
 * REST Controller for Lab Test Result operations
 * Provides JPA-based endpoints equivalent to USP_Insert_LabTestAllData stored procedure
 * 
 * This controller handles the lab test result submission functionality shown in the modal
 */
@RestController
@RequestMapping("/api/lab/results")
@Tag(name = "Lab Test Results", description = "Lab Test Result management APIs - equivalent to USP_Insert_LabTestAllData")
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
            
            @Parameter(description = "Visit Date", required = true, example = "2024-01-15T10:30:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime visitDate) {
        
        try {
            List<PatientVisitLabTestResult> results = labTestResultService.getLabTestResults(
                    patientId, patientVisitNo, shiftId, clinicId, doctorId, visitDate);
            
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
}
