package com.climasys.lab.web;

import com.climasys.dto.LabTestAndParameterRequest;
import com.climasys.entity.LabTestParameter;
import com.climasys.lab.service.LabTestParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
     * Get lab test parameters for a specific doctor, clinic and lab test description
     * This endpoint replaces the USP_Get_LabTestAndParameter stored procedure call
     * 
     * @param doctorId Doctor ID to get lab test parameters for
     * @param clinicId Clinic ID to filter lab test parameters
     * @param labTestDescription Lab test description to filter parameters
     * @return List of lab test parameters for the doctor, clinic and lab test
     */
    @Operation(
        summary = "Get Lab Test Parameters by Doctor, Clinic and Test Description",
        description = "Retrieves all lab test parameters for a specific doctor, clinic and lab test description. " +
                     "This replaces the USP_Get_LabTestAndParameter stored procedure functionality."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test parameters retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}/test/{labTestDescription}")
    public ResponseEntity<?> getLabTestParametersByDoctorClinicAndTest(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Clinic ID", required = true, example = "CLINIC001")
            @PathVariable String clinicId,
            @Parameter(description = "Lab Test Description", required = true, example = "Complete Blood Count")
            @PathVariable String labTestDescription) {
        
        try {
            Map<String, Object> result = labTestParameterService.getLabTestAndParameters(doctorId, clinicId, labTestDescription);
            
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
     * Get lab test parameters for a specific doctor, clinic and lab test ID
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param labTestId Lab test ID
     * @return List of lab test parameters
     */
    @Operation(
        summary = "Get Lab Test Parameters by Doctor, Clinic and Test ID",
        description = "Retrieves all lab test parameters for a specific doctor, clinic and lab test ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test parameters retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}/test-id/{labTestId}")
    public ResponseEntity<?> getLabTestParametersByDoctorClinicAndTestId(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Clinic ID", required = true, example = "CLINIC001")
            @PathVariable String clinicId,
            @Parameter(description = "Lab Test ID", required = true, example = "1")
            @PathVariable Integer labTestId) {
        
        try {
            Map<String, Object> result = labTestParameterService.getLabTestParametersByTestId(doctorId, clinicId, labTestId);
            
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
     * Get all lab test parameters for a doctor and clinic
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return List of all lab test parameters for the doctor and clinic
     */
    @Operation(
        summary = "Get All Lab Test Parameters by Doctor and Clinic",
        description = "Retrieves all lab test parameters for a specific doctor and clinic."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test parameters retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid doctor ID or clinic ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}")
    public ResponseEntity<?> getAllLabTestParametersForDoctorAndClinic(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Clinic ID", required = true, example = "CLINIC001")
            @PathVariable String clinicId) {
        
        try {
            Map<String, Object> result = labTestParameterService.getAllLabTestParametersForDoctor(doctorId, clinicId);
            
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
     * Get all lab tests with their parameters for a doctor and clinic
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return All lab tests with their parameters for the doctor and clinic
     */
    @Operation(
        summary = "Get All Lab Tests with Parameters by Doctor and Clinic",
        description = "Retrieves all lab tests with their parameters for a specific doctor and clinic. " +
                     "This combines lab test master data with parameter data in a single response."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab tests with parameters retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid doctor ID or clinic ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}/all-with-parameters")
    public ResponseEntity<?> getAllLabTestsWithParameters(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Clinic ID", required = true, example = "CLINIC001")
            @PathVariable String clinicId) {
        
        try {
            Map<String, Object> result = labTestParameterService.getAllLabTestsWithParameters(doctorId, clinicId);
            
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
    
    /**
     * Insert/Update lab test and parameters
     * This endpoint replaces the USP_Insert_LabTest_And_Parameters stored procedure functionality
     * 
     * The stored procedure logic:
     * 1. MERGE operation on Lab_Test_Master (update if exists with old description, insert if not)
     * 2. Gets the lab test ID from the inserted/updated lab test
     * 3. Inserts parameters from the request into Lab_Test_Parameter table
     * 
     * @param request Request containing doctor ID, clinic ID, group name, and parameter data
     * @return Created/updated lab test and parameters
     */
    @Operation(
        summary = "Insert/Update Lab Test and Parameters",
        description = "Inserts or updates lab test master and its parameters. " +
                     "This replaces the USP_Insert_LabTest_And_Parameters stored procedure functionality. " +
                     "If a lab test with the old description exists, it will be updated with the new description and priority. " +
                     "Otherwise, a new lab test will be created. Parameters will be inserted for the lab test."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test and parameters created/updated successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<?> insertLabTestAndParameters(
            @Parameter(description = "Request containing doctor ID, clinic ID, group name, and parameter data", required = true)
            @Valid @RequestBody LabTestAndParameterRequest request) {
        
        try {
            Map<String, Object> result = labTestParameterService.insertLabTestAndParameters(request);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to insert/update lab test and parameters: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Update an existing lab test parameter
     * Only updates parameter name
     * Doctor ID, ID, Lab Test ID, and Clinic ID cannot be changed (they are part of the composite key)
     * 
     * @param parameter Lab test parameter to update
     * @return Updated lab test parameter
     */
    @Operation(
        summary = "Update Lab Test Parameter",
        description = "Updates an existing lab test parameter. Only updates parameter name. " +
                     "Doctor ID, ID, Lab Test ID, and Clinic ID cannot be changed."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test parameter updated successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid data or parameter not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping
    public ResponseEntity<?> updateLabTestParameter(@RequestBody LabTestParameter parameter) {
        try {
            Map<String, Object> result = labTestParameterService.updateLabTestParameter(parameter);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to update lab test parameter: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Delete a lab test parameter
     * 
     * @param doctorId Doctor ID
     * @param id Parameter ID
     * @param labTestId Lab test ID
     * @param clinicId Clinic ID
     * @return Success message
     */
    @Operation(
        summary = "Delete Lab Test Parameter",
        description = "Deletes a lab test parameter by doctor ID, parameter ID, lab test ID, and clinic ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test parameter deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - parameter not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/doctor/{doctorId}/id/{id}/lab-test/{labTestId}/clinic/{clinicId}")
    public ResponseEntity<?> deleteLabTestParameter(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Parameter ID", required = true, example = "1")
            @PathVariable Integer id,
            @Parameter(description = "Lab Test ID", required = true, example = "1")
            @PathVariable Integer labTestId,
            @Parameter(description = "Clinic ID", required = true, example = "CLINIC001")
            @PathVariable String clinicId) {
        
        try {
            Map<String, Object> result = labTestParameterService.deleteLabTestParameter(doctorId, id, labTestId, clinicId);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to delete lab test parameter: " + e.getMessage()
            ));
        }
    }
}
