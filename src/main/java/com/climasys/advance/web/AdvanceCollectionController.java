package com.climasys.advance.web;

import com.climasys.advance.dto.*;
import com.climasys.advance.service.AdvanceCollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    
    /**
     * Get admission card data for Advance Collection screen
     * Replicates USP_Get_Patient_AdmissionCard_data
     */
    @GetMapping("/admission-card-data")
    @Operation(
        summary = "Get admission card data",
        description = "Retrieves admission card data including previous advance records, current advance details, admission data, and total advance amount"
    )
    public ResponseEntity<Map<String, Object>> getAdmissionCardData(
            @Parameter(description = "Patient ID", required = true, example = "01-10-2021-051429")
            @RequestParam String patientId,
            
            @Parameter(description = "Clinic ID", required = true, example = "CL-00001")
            @RequestParam String clinicId,
            
            @Parameter(description = "Doctor ID", required = true, example = "DR-00010")
            @RequestParam String doctorId,
            
            @Parameter(description = "IPD Reference Number", required = true, example = "IPD-2022-08-0312")
            @RequestParam String ipdRefNo,
            
            @Parameter(description = "IPD Date", required = true, example = "2022-08-20")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ipdDate
    ) {
        try {
            // Convert LocalDate to LocalDateTime at start of day for database comparison
            LocalDateTime ipdDateTime = ipdDate != null ? ipdDate.atStartOfDay() : null;
            AdmissionCardDataResponse response = advanceCollectionService.getAdmissionCardData(
                    patientId, clinicId, doctorId, ipdRefNo, ipdDateTime);
            
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
     * Save advance receipt details
     * Replicates USP_Insert_AdvanceReceiptDetails
     */
    @PostMapping("/receipt")
    @Operation(
        summary = "Save advance receipt details",
        description = "Saves receipt details for advance collection and updates advance_collection_details with receipt number"
    )
    public ResponseEntity<Map<String, Object>> saveAdvanceReceiptDetails(
            @Parameter(description = "Receipt details request", required = true)
            @RequestBody ReceiptDetailsRequest request
    ) {
        try {
            Map<String, Object> result = advanceCollectionService.saveAdvanceReceiptDetails(request);
            
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
     * Get hospital bill receipt data for printing
     * Replicates USP_Get_PatientHospitalBillReceiptData
     */
    @GetMapping("/receipt-data")
    @Operation(
        summary = "Get hospital bill receipt data",
        description = "Retrieves receipt details for printing, including receipt information and payment details"
    )
    public ResponseEntity<Map<String, Object>> getHospitalBillReceiptData(
            @Parameter(description = "Patient ID", required = true, example = "01-10-2021-051429")
            @RequestParam String patientId,
            
            @Parameter(description = "Shift ID", required = true, example = "1")
            @RequestParam Integer shiftId,
            
            @Parameter(description = "Clinic ID", required = true, example = "CL-00001")
            @RequestParam String clinicId,
            
            @Parameter(description = "Doctor ID", required = true, example = "DR-00010")
            @RequestParam String doctorId,
            
            @Parameter(description = "Visit Date", required = true, example = "2022-08-20")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate visitDate,
            
            @Parameter(description = "Visit Type", required = true, example = "A")
            @RequestParam String visitType,
            
            @Parameter(description = "Bill Number", required = false)
            @RequestParam(required = false) String billNo,
            
            @Parameter(description = "Receipt Number", required = true, example = "R-100")
            @RequestParam String receiptNo
    ) {
        try {
            HospitalBillReceiptResponse response = advanceCollectionService.getHospitalBillReceiptData(
                    patientId, shiftId, clinicId, doctorId, visitDate, visitType, billNo, receiptNo);
            
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
}

