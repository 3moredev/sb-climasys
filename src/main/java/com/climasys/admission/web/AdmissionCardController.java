package com.climasys.admission.web;

import com.climasys.admission.dto.AdmissionCardDTO;
import com.climasys.admission.dto.AdmissionCardRequest;
import com.climasys.admission.service.AdmissionCardService;
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
 * REST Controller for admission card operations
 * Provides endpoints for retrieving patient admission information
 * Matches the fields shown in Manage Admission Card page
 */
@RestController
@RequestMapping("/api/admission")
@Tag(name = "Admission Cards", description = "APIs for managing patient admission cards")
public class AdmissionCardController {
    
    @Autowired
    private AdmissionCardService admissionCardService;
    
    /**
     * Get all admission cards (list of admitted patients)
     * 
     * @param patientId Patient ID (optional)
     * @param doctorId Doctor ID (optional - if not provided, returns all doctors for the clinic)
     * @param clinicId Clinic ID (required)
     * @return List of admission cards with metadata
     */
    @GetMapping("/cards")
    @Operation(
        summary = "Get list of admitted patients",
        description = "Retrieves all admission cards matching the Manage Admission Card page format. " +
                     "Returns fields: Patient Name, Admission/IPD No, IPD File No, Admission Date, " +
                     "Reason of Admission, Discharge Date, Insurance, Company, Advance (Rs). " +
                     "If doctorId is not provided, returns admission cards for all doctors in the clinic.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved admission cards",
                content = @Content(schema = @Schema(implementation = AdmissionCardResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        }
    )
    public ResponseEntity<Map<String, Object>> getAdmissionCards(
            @Parameter(description = "Patient ID (optional - if not provided, returns all patients)")
            @RequestParam(required = false) String patientId,
            
            @Parameter(description = "Doctor ID (optional - if not provided, returns all doctors for the clinic)")
            @RequestParam(required = false) String doctorId,
            
            @Parameter(description = "Clinic ID", required = true)
            @RequestParam String clinicId
    ) {
        try {
            List<AdmissionCardDTO> admissionCards = admissionCardService
                    .getAllAdmissionCards(patientId, doctorId, clinicId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", admissionCards.size());
            response.put("data", admissionCards);
            if (doctorId != null) {
                response.put("doctorId", doctorId);
            }
            response.put("clinicId", clinicId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Search admission cards by patient ID, name, or contact number
     * 
     * @param searchStr Search string
     * @param doctorId Doctor ID (optional - if not provided, searches all doctors for the clinic)
     * @param clinicId Clinic ID (required)
     * @return List of matching admission cards
     */
    @GetMapping("/cards/search")
    @Operation(
        summary = "Search admitted patients",
        description = "Search admission cards by patient ID, patient name, contact number, or IPD number. " +
                     "If doctorId is not provided, searches across all doctors in the clinic.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved matching admission cards"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        }
    )
    public ResponseEntity<Map<String, Object>> searchAdmissionCards(
            @Parameter(description = "Search string (patient ID, name, contact, or IPD number)", required = true)
            @RequestParam String searchStr,
            
            @Parameter(description = "Doctor ID (optional - if not provided, searches all doctors for the clinic)")
            @RequestParam(required = false) String doctorId,
            
            @Parameter(description = "Clinic ID", required = true)
            @RequestParam String clinicId
    ) {
        try {
            List<AdmissionCardDTO> admissionCards = admissionCardService
                    .searchAdmissionCards(searchStr, doctorId, clinicId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", admissionCards.size());
            response.put("data", admissionCards);
            response.put("searchStr", searchStr);
            if (doctorId != null) {
                response.put("doctorId", doctorId);
            }
            response.put("clinicId", clinicId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get admission data by patient ID
     * Retrieves all admission records from admission_data table for a specific patient
     * 
     * @param patientId Patient ID
     * @return List of admission data records
     */
    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get admission data by patient ID",
        description = "Retrieves all admission records from admission_data table for a specific patient. " +
                     "Returns all fields from the admission_data table including IPD reference number, " +
                     "admission date/time, reason, department, insurance details, etc.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved admission data"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "No admission records found for the patient"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        }
    )
    public ResponseEntity<Map<String, Object>> getAdmissionDataByPatientId(
            @Parameter(description = "Patient ID", required = true, example = "01-10-2021-051429")
            @PathVariable String patientId
    ) {
        try {
            List<Map<String, Object>> admissionData = admissionCardService
                    .getAdmissionDataByPatientId(patientId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", admissionData.size());
            response.put("data", admissionData);
            response.put("patientId", patientId);
            
            if (admissionData.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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
     * Insert or update admission card
     * Replicates USP_Insert_AdmissionCard
     */
    @PostMapping
    @Operation(
        summary = "Save admission card",
        description = "Insert new or update existing admission card. IPD Reference Number is auto-generated if not provided.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully saved admission card"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        }
    )
    public ResponseEntity<Map<String, Object>> saveAdmissionCard(
            @Parameter(description = "Admission card request data", required = true)
            @RequestBody AdmissionCardRequest request
    ) {
        try {
            Map<String, Object> result = admissionCardService.saveAdmissionCard(request);
            
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
    
    /**
     * Response schema for Swagger documentation
     */
    @Schema(description = "Admission card response")
    private static class AdmissionCardResponse {
        @Schema(description = "Success status")
        public boolean success;
        
        @Schema(description = "Number of admission cards")
        public int count;
        
        @Schema(description = "List of admission cards")
        public List<AdmissionCardDTO> data;
        
        @Schema(description = "Doctor ID")
        public String doctorId;
        
        @Schema(description = "Clinic ID")
        public String clinicId;
    }
}

