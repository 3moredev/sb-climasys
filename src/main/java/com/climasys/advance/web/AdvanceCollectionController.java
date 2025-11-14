package com.climasys.advance.web;

import com.climasys.advance.dto.AdvanceCollectionDTO;
import com.climasys.advance.dto.AdvanceCollectionRequest;
import com.climasys.advance.dto.AdvanceCollectionSearchResultDTO;
import com.climasys.advance.service.AdvanceCollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for advance collection operations
 * Provides endpoints for Manage Advance Collection page
 */
@RestController
@RequestMapping("/api/advance-collection")
@Tag(name = "Advance Collection", description = "APIs for managing advance collections and payments")
public class AdvanceCollectionController {
    
    @Autowired
    private AdvanceCollectionService advanceCollectionService;
    
    /**
     * Get advance details for a patient's IPD
     * Replicates USP_GET_AdvanceDetails
     */
    @GetMapping("/details")
    @Operation(
        summary = "Get advance collection details",
        description = "Retrieves advance payment details for a specific patient's IPD admission"
    )
    public ResponseEntity<Map<String, Object>> getAdvanceDetails(
            @Parameter(description = "Patient ID", required = true, example = "01-10-2021-051429")
            @RequestParam String patientId,
            
            @Parameter(description = "Clinic ID", required = true, example = "CL-00001")
            @RequestParam String clinicId,
            
            @Parameter(description = "IPD Reference Number", required = true, example = "IPD-2022-08-0312")
            @RequestParam String ipdRefNo
    ) {
        try {
            List<AdvanceCollectionDTO> details = advanceCollectionService
                    .getAdvanceDetails(patientId, clinicId, ipdRefNo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", details.size());
            response.put("data", details);
            response.put("patientId", patientId);
            response.put("clinicId", clinicId);
            response.put("ipdRefNo", ipdRefNo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Search patients with advance cards (autocomplete)
     * Replicates USP_Search_Patient_With_AdvanceCard
     */
    @GetMapping("/search")
    @Operation(
        summary = "Search patients with advance cards",
        description = "Autocomplete search for patients with advance cards by patient ID, name, or IPD number. " +
                     "Doctor ID is optional - if not provided, searches across all doctors."
    )
    public ResponseEntity<Map<String, Object>> searchPatientsWithAdvanceCard(
            @Parameter(description = "Search string (patient ID, name, or IPD number)", required = true, example = "JYOTI")
            @RequestParam String searchStr,
            
            @Parameter(description = "Doctor ID (optional - if not provided, searches across all doctors)", required = false, example = "DR-00010")
            @RequestParam(required = false) String doctorId
    ) {
        try {
            List<AdvanceCollectionSearchResultDTO> searchResults = advanceCollectionService
                    .searchPatientsWithAdvanceCard(searchStr, doctorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", searchResults.size());
            response.put("data", searchResults);
            response.put("searchStr", searchStr);
            if (doctorId != null) {
                response.put("doctorId", doctorId);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Insert or update advance collection
     * Replicates USP_Insert_AdvanceCollection
     */
    @PostMapping
    @Operation(
        summary = "Save advance collection",
        description = "Insert new or update existing advance collection record"
    )
    public ResponseEntity<Map<String, Object>> saveAdvanceCollection(
            @Parameter(description = "Advance collection request data", required = true)
            @RequestBody AdvanceCollectionRequest request
    ) {
        try {
            Map<String, Object> result = advanceCollectionService.saveAdvanceCollection(request);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

