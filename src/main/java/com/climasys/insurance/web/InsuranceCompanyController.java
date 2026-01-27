package com.climasys.insurance.web;

import com.climasys.auth.annotation.RefreshSession;

import com.climasys.insurance.service.InsuranceCompanyService;
import io.swagger.v3.oas.annotations.Operation;
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
 * Controller for insurance company management
 * Includes dropdown endpoint replacing GetDDL_InsuranceComp web service
 */
@RestController
@RequestMapping("/api/insurance/companies")
@Tag(name = "Insurance Company", description = "APIs for managing insurance company data")
@RefreshSession
public class InsuranceCompanyController {

    @Autowired
    private InsuranceCompanyService insuranceCompanyService;

    /**
     * Edit insurance company information
     */
    @PutMapping("/{companyId}")
    public ResponseEntity<List<Map<String, Object>>> editInsuranceCompany(
            @PathVariable String companyId,
            @RequestParam String companyName,
            @RequestParam String userId,
            @RequestParam String doctorId) {
        List<Map<String, Object>> result = insuranceCompanyService.editInsuranceCompany(
                companyName, companyId, userId, doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Insert new insurance company
     */
    @PostMapping
    public ResponseEntity<List<Map<String, Object>>> insertInsuranceCompany(
            @RequestParam String companyName,
            @RequestParam String companyId,
            @RequestParam String userId,
            @RequestParam String doctorId) {
        List<Map<String, Object>> result = insuranceCompanyService.insertInsuranceCompany(
                companyName, companyId, userId, doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete insurance company
     */
    @DeleteMapping("/{companyId}")
    public ResponseEntity<List<Map<String, Object>>> deleteInsuranceCompany(@PathVariable String companyId) {
        List<Map<String, Object>> result = insuranceCompanyService.deleteInsuranceCompany(companyId);
        return ResponseEntity.ok(result);
    }

    /**
     * Check if discharge printing is enabled for insurance
     */
    @GetMapping("/check-discharge-print")
    public ResponseEntity<List<Map<String, Object>>> checkDischargePrintEnabled(
            @RequestParam String patientId,
            @RequestParam String visitId) {
        List<Map<String, Object>> result = insuranceCompanyService.checkDischargePrintEnabled(patientId, visitId);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get all active insurance companies for dropdown
     * Replaces: GetDDL_InsuranceComp web service method
     * Matches: USP_GetDDL_InsuranceComp stored procedure
     * 
     * Returns active insurance companies with Company_Id as ID and Company_Name as Name
     * Used for populating insurance company dropdown in Admission Card form
     * 
     * @return List of insurance companies in format: [{id: "1", name: "Company Name"}, ...]
     */
    @GetMapping("/dropdown")
    @Operation(
        summary = "Get all active insurance companies for dropdown",
        description = "Retrieves all active (non-deleted) insurance companies from insurance_company_master table. " +
                     "Returns companies with Company_Id as ID and Company_Name as Name. " +
                     "This replaces the USP_GetDDL_InsuranceComp stored procedure. " +
                     "Used for populating insurance company dropdown in Admission Card form when Insurance is set to 'Yes'.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved insurance companies",
                content = @Content(schema = @Schema(implementation = InsuranceCompanyDropdownResponse.class))
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        }
    )
    public ResponseEntity<Map<String, Object>> getAllActiveCompaniesForDropdown() {
        try {
            List<Map<String, Object>> companies = insuranceCompanyService.getAllActiveCompaniesForDropdown();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", companies.size());
            response.put("data", companies);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get all active insurance companies as DTO list
     * Alternative endpoint that returns structured DTOs
     * 
     * @return List of insurance company DTOs
     */
    @GetMapping("/dropdown/dto")
    @Operation(
        summary = "Get all active insurance companies as DTOs",
        description = "Retrieves all active insurance companies as structured DTOs",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved insurance companies"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        }
    )
    public ResponseEntity<Map<String, Object>> getAllActiveCompaniesAsDTO() {
        try {
            List<InsuranceCompanyService.InsuranceCompanyDTO> companies = 
                insuranceCompanyService.getAllActiveCompaniesAsDTO();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", companies.size());
            response.put("data", companies);
            
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
    @Schema(description = "Insurance company dropdown response")
    private static class InsuranceCompanyDropdownResponse {
        @Schema(description = "Success status")
        public boolean success;
        
        @Schema(description = "Number of insurance companies")
        public int count;
        
        @Schema(description = "List of insurance companies")
        public List<Map<String, Object>> data;
    }
}