package com.climasys.discharge.web;

import com.climasys.discharge.dto.DischargeCardDTO;
import com.climasys.discharge.dto.DischargeCardDetailResponse;
import com.climasys.discharge.dto.UpdateDischargeCardRequest;
import com.climasys.discharge.service.DischargeCardService;
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
 * REST Controller for discharge card operations
 * Replaces USP_Get_Patient_All_Discharge_Cards stored procedure
 * Provides endpoints for Manage Discharge Card screen
 */
@RestController
@RequestMapping("/api/discharge")
@Tag(name = "Discharge Cards", description = "APIs for managing patient discharge cards")
public class DischargeCardController {
    
    @Autowired
    private DischargeCardService dischargeCardService;
    
    /**
     * Get all admitted patients for "List of Admitted Patient/s" table
     * Replaces: GetPatient_AllDischargeCard web service call with empty patient ID
     * Matches: Table[5] from USP_Get_Patient_All_Discharge_Cards
     * 
     * Returns all admitted patients with duplicate removal by IPD_RefNo
     * Used on page load to populate the "List of Admitted Patient/s" table
     * 
     * @param doctorId Doctor ID (optional - if not provided, returns all doctors for the clinic)
     * @param clinicId Clinic ID (required)
     * @return List of admitted patients
     */
    @GetMapping("/admitted-patients")
    @Operation(
        summary = "Get all admitted patients",
        description = "Retrieves all admitted patients for the 'List of Admitted Patient/s' table. " +
                     "Matches Table[5] from USP_Get_Patient_All_Discharge_Cards stored procedure. " +
                     "Includes duplicate removal logic by IPD_RefNo. " +
                     "Returns fields: Sr., Patient Name, IPD No, IPD File No, Admission Date, " +
                     "Discharge Date, keyword / Operation, Advance (Rs). " +
                     "If doctorId is not provided, returns admission cards for all doctors in the clinic.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved admitted patients",
                content = @Content(schema = @Schema(implementation = DischargeCardResponse.class))
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
    public ResponseEntity<Map<String, Object>> getAllAdmittedPatients(
            @Parameter(description = "Doctor ID (optional - if not provided, returns all doctors for the clinic)")
            @RequestParam(required = false) String doctorId,
            
            @Parameter(description = "Clinic ID", required = true)
            @RequestParam String clinicId
    ) {
        try {
            List<DischargeCardDTO> dischargeCards = dischargeCardService
                    .getAllAdmittedPatients(doctorId, clinicId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", dischargeCards.size());
            response.put("data", dischargeCards);
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
     * Get discharge cards for a specific patient (search results)
     * Replaces: GetPatient_AllDischargeCard web service call with patient ID
     * Matches: Table[0] from USP_Get_Patient_All_Discharge_Cards
     * 
     * @param patientId Patient ID (required)
     * @param doctorId Doctor ID (optional - if not provided, returns all doctors for the clinic)
     * @param clinicId Clinic ID (required)
     * @return List of discharge cards for the patient
     */
    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get discharge cards by patient",
        description = "Retrieves all discharge cards for a specific patient. " +
                     "Matches Table[0] from USP_Get_Patient_All_Discharge_Cards stored procedure. " +
                     "Used for search results when a patient is searched.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved discharge cards for patient"
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
    public ResponseEntity<Map<String, Object>> getDischargeCardsByPatient(
            @Parameter(description = "Patient ID", required = true)
            @PathVariable String patientId,
            
            @Parameter(description = "Doctor ID (optional - if not provided, returns all doctors for the clinic)")
            @RequestParam(required = false) String doctorId,
            
            @Parameter(description = "Clinic ID", required = true)
            @RequestParam String clinicId
    ) {
        try {
            List<DischargeCardDTO> dischargeCards = dischargeCardService
                    .getDischargeCardsByPatient(patientId, doctorId, clinicId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", dischargeCards.size());
            response.put("data", dischargeCards);
            response.put("patientId", patientId);
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
     * Search discharge cards by patient ID, name, contact, or IPD number
     * Used for the search functionality on Manage Discharge Card screen
     * 
     * @param searchStr Search string (patient ID, name, contact, or IPD number)
     * @param doctorId Doctor ID (optional - if not provided, searches all doctors for the clinic)
     * @param clinicId Clinic ID (required)
     * @return List of matching discharge cards
     */
    @GetMapping("/search")
    @Operation(
        summary = "Search discharge cards",
        description = "Search discharge cards by patient ID, patient name, contact number, or IPD number. " +
                     "Used for the search functionality on Manage Discharge Card screen. " +
                     "If doctorId is not provided, searches across all doctors in the clinic.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved matching discharge cards"
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
    public ResponseEntity<Map<String, Object>> searchDischargeCards(
            @Parameter(description = "Search string (patient ID, name, contact, or IPD number)", required = true)
            @RequestParam String searchStr,
            
            @Parameter(description = "Doctor ID (optional - if not provided, searches all doctors for the clinic)")
            @RequestParam(required = false) String doctorId,
            
            @Parameter(description = "Clinic ID", required = true)
            @RequestParam String clinicId
    ) {
        try {
            List<DischargeCardDTO> dischargeCards = dischargeCardService
                    .searchDischargeCards(searchStr, doctorId, clinicId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", dischargeCards.size());
            response.put("data", dischargeCards);
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
     * Response schema for Swagger documentation
     */
    @Schema(description = "Discharge card response")
    private static class DischargeCardResponse {
        @Schema(description = "Success status")
        public boolean success;
        
        @Schema(description = "Number of discharge cards")
        public int count;
        
        @Schema(description = "List of discharge cards")
        public List<DischargeCardDTO> data;
        
        @Schema(description = "Doctor ID")
        public String doctorId;
        
        @Schema(description = "Clinic ID")
        public String clinicId;
    }
    
    /**
     * Get discharge card details for a specific patient and IPD
     * Replaces: GetPatient_DischargeCardDetails web service call
     * Matches: USP_Get_Patient_DischargeCard_Data stored procedure
     * 
     * Returns all discharge card data including main data, investigations, invoices, bills, labour card, and advance information
     * 
     * @param patientId Patient ID (required)
     * @param shiftId Shift ID (required)
     * @param clinicId Clinic ID (required)
     * @param doctorId Doctor ID (required)
     * @param ipdNo IPD Number (required)
     * @param invoiceNo Invoice Number (optional)
     * @return Discharge card detail response
     */
    @GetMapping("/details")
    @Operation(
        summary = "Get discharge card details",
        description = "Retrieves complete discharge card details for a specific patient and IPD. " +
                     "Matches USP_Get_Patient_DischargeCard_Data stored procedure. " +
                     "Returns main discharge data, investigations, invoices, bills, labour card, and advance information.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved discharge card details"
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
    public ResponseEntity<Map<String, Object>> getDischargeCardDetails(
            @Parameter(description = "Patient ID", required = true)
            @RequestParam String patientId,
            
            @Parameter(description = "Shift ID", required = true)
            @RequestParam Integer shiftId,
            
            @Parameter(description = "Clinic ID", required = true)
            @RequestParam String clinicId,
            
            @Parameter(description = "Doctor ID", required = true)
            @RequestParam String doctorId,
            
            @Parameter(description = "IPD Number", required = true)
            @RequestParam String ipdNo,
            
            @Parameter(description = "Invoice Number (optional)")
            @RequestParam(required = false) String invoiceNo
    ) {
        try {
            DischargeCardDetailResponse response = dischargeCardService.getDischargeCardDetails(
                    patientId, shiftId, clinicId, doctorId, ipdNo, invoiceNo);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Save/Update discharge card details
     * Replaces: SaveDischargeDetails web service call
     * Matches: USP_Insert_DischargeData stored procedure
     * 
     * Saves or updates discharge card information including all details, investigations, and related data
     * 
     * @param request Update discharge card request
     * @return Save status and IPD number
     */
    @PostMapping("/save")
    @Operation(
        summary = "Save/Update discharge card",
        description = "Saves or updates discharge card details. " +
                     "Matches USP_Insert_DischargeData stored procedure. " +
                     "Includes date conflict checking, IPD_RefNo generation, and updates to discharge_data and admission_data tables.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully saved discharge card details"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request or date conflict"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        }
    )
    public ResponseEntity<Map<String, Object>> saveDischargeCardDetails(
            @Parameter(description = "Update discharge card request", required = true)
            @RequestBody UpdateDischargeCardRequest request
    ) {
        try {
            Map<String, Object> result = dischargeCardService.saveDischargeCardDetails(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.get("saveStatus").equals(1));
            response.putAll(result);
            
            if (result.get("saveStatus").equals(1)) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

