package com.climasys.lab.web;

import com.climasys.entity.LabTestMaster;
import com.climasys.lab.service.LabTestMasterService;
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
 * Controller for Lab Test Master operations
 * Provides REST endpoints to replace USP_Get_LabTest stored procedure functionality
 * 
 * This controller provides the lab test list functionality for the modal shown in the image
 */
@RestController
@RequestMapping("/api/lab/master")
@Tag(name = "Lab Test Master", description = "Lab Test Master management APIs")
public class LabTestMasterController {
    
    @Autowired
    private LabTestMasterService labTestMasterService;
    
    /**
     * Get lab tests for a specific doctor
     * This endpoint replaces the USP_Get_LabTest stored procedure call
     * Stored procedure signature: USP_Get_LabTest(@p_var_Doctor_ID)
     * Used to populate the lab test dropdown in the modal
     * 
     * @param doctorId Doctor ID to get lab tests for
     * @return List of lab tests for the doctor
     */
    @Operation(
        summary = "Get Lab Tests by Doctor",
        description = "Retrieves all lab tests available for a specific doctor, ordered by priority and description. " +
                     "This replaces the USP_Get_LabTest stored procedure functionality. " +
                     "Stored procedure signature: USP_Get_LabTest(@p_var_Doctor_ID)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab tests retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid doctor ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/tests/doctor/{doctorId}")
    public ResponseEntity<?> getLabTestsByDoctor(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @PathVariable String doctorId) {
        
        try {
            Map<String, Object> result = labTestMasterService.getLabTestsForDoctor(doctorId);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get lab tests: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get lab tests for a specific doctor and clinic
     * This endpoint replaces the USP_Get_LabTest stored procedure call (extended version with clinic)
     * Used to populate the lab test dropdown in the modal
     * 
     * @param doctorId Doctor ID to get lab tests for
     * @param clinicId Clinic ID to filter lab tests
     * @return List of lab tests for the doctor and clinic
     */
    @Operation(
        summary = "Get Lab Tests by Doctor and Clinic",
        description = "Retrieves all lab tests available for a specific doctor and clinic, ordered by priority and description. " +
                     "This replaces the USP_Get_LabTest stored procedure functionality (extended version with clinic)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab tests retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid doctor ID or clinic ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/tests/doctor/{doctorId}/clinic/{clinicId}")
    public ResponseEntity<?> getLabTestsByDoctorAndClinic(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Clinic ID", required = true, example = "CLINIC001")
            @PathVariable String clinicId) {
        
        try {
            Map<String, Object> result = labTestMasterService.getLabTestsForDoctor(doctorId, clinicId);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get lab tests: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get all lab tests
     * 
     * @return List of all lab tests
     */
    @Operation(
        summary = "Get All Lab Tests",
        description = "Retrieves all lab tests from the system, ordered by priority and description."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab tests retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/tests/all")
    public ResponseEntity<?> getAllLabTests() {
        try {
            Map<String, Object> result = labTestMasterService.getAllLabTests();
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get lab tests: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Search lab tests by description
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param searchTerm Search term for lab test description
     * @return List of matching lab tests
     */
    @Operation(
        summary = "Search Lab Tests",
        description = "Searches lab tests by description pattern for a specific doctor and clinic."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab tests retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/tests/search")
    public ResponseEntity<?> searchLabTests(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @RequestParam String doctorId,
            @Parameter(description = "Clinic ID", required = true, example = "CLINIC001")
            @RequestParam String clinicId,
            @Parameter(description = "Search term for lab test description", required = true, example = "Blood")
            @RequestParam String searchTerm) {
        
        try {
            Map<String, Object> result = labTestMasterService.searchLabTests(doctorId, clinicId, searchTerm);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to search lab tests: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Check if lab test exists for doctor and clinic
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param labTestDescription Lab test description
     * @return true if exists, false otherwise
     */
    @Operation(
        summary = "Check Lab Test Exists",
        description = "Checks if a specific lab test exists for a doctor and clinic."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/tests/exists")
    public ResponseEntity<?> checkLabTestExists(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @RequestParam String doctorId,
            @Parameter(description = "Clinic ID", required = true, example = "CLINIC001")
            @RequestParam String clinicId,
            @Parameter(description = "Lab test description", required = true, example = "Blood Sugar (Fasting)")
            @RequestParam String labTestDescription) {
        
        try {
            boolean exists = labTestMasterService.labTestExists(doctorId, clinicId, labTestDescription);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "exists", exists,
                "doctorId", doctorId,
                "clinicId", clinicId,
                "labTestDescription", labTestDescription
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to check lab test existence: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get lab test count for doctor and clinic
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return Count of lab tests for the doctor and clinic
     */
    @Operation(
        summary = "Get Lab Test Count",
        description = "Gets the count of lab tests available for a specific doctor and clinic."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/tests/count/{doctorId}/clinic/{clinicId}")
    public ResponseEntity<?> getLabTestCount(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Clinic ID", required = true, example = "CLINIC001")
            @PathVariable String clinicId) {
        
        try {
            long count = labTestMasterService.getLabTestCountForDoctor(doctorId, clinicId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "count", count,
                "doctorId", doctorId,
                "clinicId", clinicId
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get lab test count: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get lab tests by group name for a doctor and clinic
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param groupName Group name
     * @return List of lab tests in the group
     */
    @Operation(
        summary = "Get Lab Tests by Group",
        description = "Gets lab tests for a specific doctor and clinic filtered by group name."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab tests retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/tests/group")
    public ResponseEntity<?> getLabTestsByGroup(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @RequestParam String doctorId,
            @Parameter(description = "Clinic ID", required = true, example = "CLINIC001")
            @RequestParam String clinicId,
            @Parameter(description = "Group name", required = true, example = "Biochemistry")
            @RequestParam String groupName) {
        
        try {
            Map<String, Object> result = labTestMasterService.getLabTestsByGroup(doctorId, clinicId, groupName);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get lab tests by group: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Create a new lab test
     * 
     * @param labTest Lab test to create (must include doctorId, clinicId, and labTestDescription)
     * @return Created lab test
     */
    @Operation(
        summary = "Create Lab Test",
        description = "Creates a new lab test. Requires doctorId, clinicId, and labTestDescription. " +
                     "ID will be auto-generated if not provided. Group name and priority value are optional."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Lab test created successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid data or lab test already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/tests")
    public ResponseEntity<?> createLabTest(@RequestBody LabTestMaster labTest) {
        try {
            Map<String, Object> result = labTestMasterService.createLabTest(labTest);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.status(201).body(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to create lab test: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Update an existing lab test
     * Only updates lab test description, group name, and priority value
     * Doctor ID, ID, and Clinic ID cannot be changed (they are part of the composite key)
     * 
     * @param labTest Lab test to update
     * @return Updated lab test
     */
    @Operation(
        summary = "Update Lab Test",
        description = "Updates an existing lab test. Only updates description, group name, and priority value. " +
                     "Doctor ID, ID, and Clinic ID cannot be changed."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test updated successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid data or lab test not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/tests")
    public ResponseEntity<?> updateLabTest(@RequestBody LabTestMaster labTest) {
        try {
            Map<String, Object> result = labTestMasterService.updateLabTest(labTest);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to update lab test: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Delete a lab test
     * 
     * @param doctorId Doctor ID
     * @param id Lab test ID
     * @param clinicId Clinic ID
     * @return Success message
     */
    @Operation(
        summary = "Delete Lab Test",
        description = "Deletes a lab test by doctor ID, lab test ID, and clinic ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lab test deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - lab test not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/tests/doctor/{doctorId}/id/{id}/clinic/{clinicId}")
    public ResponseEntity<?> deleteLabTest(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Lab Test ID", required = true, example = "1")
            @PathVariable Integer id,
            @Parameter(description = "Clinic ID", required = true, example = "CLINIC001")
            @PathVariable String clinicId) {
        
        try {
            Map<String, Object> result = labTestMasterService.deleteLabTest(doctorId, id, clinicId);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to delete lab test: " + e.getMessage()
            ));
        }
    }
}
