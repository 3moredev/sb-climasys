package com.climasys.department.web;

import com.climasys.auth.annotation.RefreshSession;

import com.climasys.department.service.DepartmentMasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for department master operations
 * Replaces USP_GetDDL_Department stored procedure
 * Provides endpoints for retrieving department information for dropdowns
 */
@RestController
@RequestMapping("/api/departments")
@Tag(name = "Department Master", description = "APIs for managing department master data")
@RefreshSession
public class DepartmentMasterController {
    
    @Autowired
    private DepartmentMasterService departmentMasterService;
    
    /**
     * Get all departments for dropdown
     * Replaces: GetDDL_Department web service method
     * Matches: USP_GetDDL_Department stored procedure main query
     * 
     * Returns distinct departments with Name and ID (both are department_name)
     * Used for populating department dropdown in Admission Card form
     * 
     * @return List of departments in format: [{name: "Medicine", id: "Medicine"}, ...]
     */
    @GetMapping
    @Operation(
        summary = "Get all departments",
        description = "Retrieves all distinct departments from doctors_department table. " +
                     "Returns departments with Name and ID (both are department_name). " +
                     "This replaces the USP_GetDDL_Department stored procedure main query. " +
                     "Used for populating department dropdown in Admission Card form.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved departments",
                content = @Content(schema = @Schema(implementation = DepartmentResponse.class))
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        }
    )
    public ResponseEntity<Map<String, Object>> getAllDepartments() {
        try {
            List<Map<String, Object>> departments = departmentMasterService.getAllDepartments();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", departments.size());
            response.put("data", departments);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get all departments as DTO list
     * Alternative endpoint that returns structured DTOs
     * 
     * @return List of department DTOs
     */
    @GetMapping("/dto")
    @Operation(
        summary = "Get all departments as DTOs",
        description = "Retrieves all distinct departments as structured DTOs",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved departments"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        }
    )
    public ResponseEntity<Map<String, Object>> getAllDepartmentsAsDTO() {
        try {
            List<DepartmentMasterService.DepartmentDTO> departments = 
                departmentMasterService.getAllDepartmentsAsDTO();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", departments.size());
            response.put("data", departments);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get doctors for a specific department
     * Matches the query from USP_GetDDL_Department when filtering by department name
     * 
     * @param departmentName Department name to filter by
     * @return List of doctors in that department
     */
    @GetMapping("/{departmentName}/doctors")
    @Operation(
        summary = "Get doctors by department",
        description = "Retrieves all doctors for a specific department. " +
                     "Matches the query from USP_GetDDL_Department when filtering by department name.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved doctors for department"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid department name"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        }
    )
    public ResponseEntity<Map<String, Object>> getDoctorsByDepartment(
            @Parameter(description = "Department name", required = true)
            @PathVariable String departmentName) {
        try {
            List<Map<String, Object>> doctors = 
                departmentMasterService.getDoctorsByDepartment(departmentName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", doctors.size());
            response.put("departmentName", departmentName);
            response.put("data", doctors);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get all department names as simple string list
     * 
     * @return List of department names
     */
    @GetMapping("/names")
    @Operation(
        summary = "Get all department names",
        description = "Retrieves all distinct department names as a simple string list",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved department names"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        }
    )
    public ResponseEntity<Map<String, Object>> getAllDepartmentNames() {
        try {
            List<String> departmentNames = departmentMasterService.getAllDepartmentNames();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", departmentNames.size());
            response.put("data", departmentNames);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Response schema for Swagger documentation
     */
    @Schema(description = "Department response")
    private static class DepartmentResponse {
        @Schema(description = "Success status")
        public boolean success;
        
        @Schema(description = "Number of departments")
        public int count;
        
        @Schema(description = "List of departments")
        public List<Map<String, Object>> data;
    }
}
