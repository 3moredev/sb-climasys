package com.climasys.lab.web;

import com.climasys.lab.service.LabTestParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for Lab Test Parameter operations
 * Provides REST endpoints to replace USP_Get_LabTestAndParameter stored procedure functionality
 */
@RestController
@RequestMapping("/api/lab/master/parameters")
@Tag(name = "Lab Test Parameters", description = "Operations for lab test parameters")
public class LabTestParameterController {
    
    @Autowired
    private LabTestParameterService labTestParameterService;
    
    /**
     * Get lab test parameters for a specific doctor and lab test description
     * This endpoint replaces the USP_Get_LabTestAndParameter stored procedure call
     * 
     * @param doctorId Doctor ID to get lab test parameters for
     * @param labTestDescription Lab test description to filter parameters
     * @return List of lab test parameters for the doctor and lab test
     */
    @Operation(
        summary = "Get Lab Test Parameters by Doctor and Test Description",
        description = "Retrieves all lab test parameters for a specific doctor and lab test description. " +
                     "This replaces the USP_Get_LabTestAndParameter stored procedure functionality."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test parameters retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/doctor/{doctorId}/test/{labTestDescription}")
    public ResponseEntity<?> getLabTestParametersByDoctorAndTest(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Lab Test Description", required = true, example = "Complete Blood Count")
            @PathVariable String labTestDescription) {
        
        try {
            Map<String, Object> result = labTestParameterService.getLabTestAndParameters(doctorId, labTestDescription);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get lab test parameters: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get lab test parameters for a specific doctor and lab test ID
     * 
     * @param doctorId Doctor ID
     * @param labTestId Lab test ID
     * @return List of lab test parameters
     */
    @Operation(
        summary = "Get Lab Test Parameters by Doctor and Test ID",
        description = "Retrieves all lab test parameters for a specific doctor and lab test ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test parameters retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/doctor/{doctorId}/test-id/{labTestId}")
    public ResponseEntity<?> getLabTestParametersByDoctorAndTestId(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Lab Test ID", required = true, example = "1")
            @PathVariable Integer labTestId) {
        
        try {
            Map<String, Object> result = labTestParameterService.getLabTestParametersByTestId(doctorId, labTestId);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get lab test parameters: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get all lab test parameters for a doctor
     * 
     * @param doctorId Doctor ID
     * @return List of all lab test parameters for the doctor
     */
    @Operation(
        summary = "Get All Lab Test Parameters by Doctor",
        description = "Retrieves all lab test parameters for a specific doctor."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test parameters retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid doctor ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getAllLabTestParametersForDoctor(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @PathVariable String doctorId) {
        
        try {
            Map<String, Object> result = labTestParameterService.getAllLabTestParametersForDoctor(doctorId);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get lab test parameters: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Check if lab test parameter exists
     * 
     * @param doctorId Doctor ID
     * @param labTestId Lab test ID
     * @param parameterName Parameter name
     * @return Boolean indicating if parameter exists
     */
    @Operation(
        summary = "Check Lab Test Parameter Exists",
        description = "Checks if a specific lab test parameter exists for a doctor and lab test."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Parameter existence checked successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/exists")
    public ResponseEntity<?> checkParameterExists(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @RequestParam String doctorId,
            @Parameter(description = "Lab Test ID", required = true, example = "1")
            @RequestParam Integer labTestId,
            @Parameter(description = "Parameter Name", required = true, example = "Hemoglobin")
            @RequestParam String parameterName) {
        
        try {
            boolean exists = labTestParameterService.parameterExists(doctorId, labTestId, parameterName);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "exists", exists,
                "doctorId", doctorId,
                "labTestId", labTestId,
                "parameterName", parameterName
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to check parameter existence: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get count of lab test parameters for a doctor and lab test
     * 
     * @param doctorId Doctor ID
     * @param labTestId Lab test ID
     * @return Count of parameters
     */
    @Operation(
        summary = "Get Lab Test Parameter Count",
        description = "Gets the count of lab test parameters for a specific doctor and lab test."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Parameter count retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/count")
    public ResponseEntity<?> getParameterCount(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @RequestParam String doctorId,
            @Parameter(description = "Lab Test ID", required = true, example = "1")
            @RequestParam Integer labTestId) {
        
        try {
            long count = labTestParameterService.getParameterCount(doctorId, labTestId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "count", count,
                "doctorId", doctorId,
                "labTestId", labTestId
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get parameter count: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get all lab tests with their parameters for a doctor
     * 
     * @param doctorId Doctor ID
     * @return All lab tests with their parameters for the doctor
     */
    @Operation(
        summary = "Get All Lab Tests with Parameters by Doctor",
        description = "Retrieves all lab tests with their parameters for a specific doctor. " +
                     "This combines lab test master data with parameter data in a single response."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab tests with parameters retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid doctor ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/doctor/{doctorId}/all-with-parameters")
    public ResponseEntity<?> getAllLabTestsWithParameters(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @PathVariable String doctorId) {
        
        try {
            Map<String, Object> result = labTestParameterService.getAllLabTestsWithParameters(doctorId);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get lab tests with parameters: " + e.getMessage()
            ));
        }
    }
}
