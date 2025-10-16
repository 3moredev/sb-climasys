package com.climasys.lab.web;

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
     * Used to populate the lab test dropdown in the modal
     * 
     * @param doctorId Doctor ID to get lab tests for
     * @return List of lab tests for the doctor
     */
    @Operation(
        summary = "Get Lab Tests by Doctor",
        description = "Retrieves all lab tests available for a specific doctor, ordered by priority and description. " +
                     "This replaces the USP_Get_LabTest stored procedure functionality."
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
     * @param searchTerm Search term for lab test description
     * @return List of matching lab tests
     */
    @Operation(
        summary = "Search Lab Tests",
        description = "Searches lab tests by description pattern for a specific doctor."
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
            @Parameter(description = "Search term for lab test description", required = true, example = "Blood")
            @RequestParam String searchTerm) {
        
        try {
            Map<String, Object> result = labTestMasterService.searchLabTests(doctorId, searchTerm);
            
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
     * Check if lab test exists for doctor
     * 
     * @param doctorId Doctor ID
     * @param labTestDescription Lab test description
     * @return true if exists, false otherwise
     */
    @Operation(
        summary = "Check Lab Test Exists",
        description = "Checks if a specific lab test exists for a doctor."
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
            @Parameter(description = "Lab test description", required = true, example = "Blood Sugar (Fasting)")
            @RequestParam String labTestDescription) {
        
        try {
            boolean exists = labTestMasterService.labTestExists(doctorId, labTestDescription);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "exists", exists,
                "doctorId", doctorId,
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
     * Get lab test count for doctor
     * 
     * @param doctorId Doctor ID
     * @return Count of lab tests for the doctor
     */
    @Operation(
        summary = "Get Lab Test Count",
        description = "Gets the count of lab tests available for a specific doctor."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/tests/count/{doctorId}")
    public ResponseEntity<?> getLabTestCount(
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @PathVariable String doctorId) {
        
        try {
            long count = labTestMasterService.getLabTestCountForDoctor(doctorId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "count", count,
                "doctorId", doctorId
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get lab test count: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get lab tests by group name for a doctor
     * 
     * @param doctorId Doctor ID
     * @param groupName Group name
     * @return List of lab tests in the group
     */
    @Operation(
        summary = "Get Lab Tests by Group",
        description = "Gets lab tests for a specific doctor filtered by group name."
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
            @Parameter(description = "Group name", required = true, example = "Biochemistry")
            @RequestParam String groupName) {
        
        try {
            Map<String, Object> result = labTestMasterService.getLabTestsByGroup(doctorId, groupName);
            
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
}
