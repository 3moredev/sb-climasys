package com.climasys.patients.web;

import com.climasys.auth.annotation.RefreshSession;

import com.climasys.patients.service.PatientSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for patient search functionality matching USP_Search_Patient_With_OPD stored procedure
 * 
 * This endpoint is used by the Manage Admission Card page for autocomplete search functionality.
 * It replicates the exact business logic from the stored procedure USP_Search_Patient_With_OPD.
 */
@RestController
@RequestMapping("/api/patients")
@Tag(name = "Patient Search", description = "Patient search APIs matching stored procedure USP_Search_Patient_With_OPD")
@RefreshSession
public class PatientSearchController {

    @Autowired
    private PatientSearchService patientSearchService;

    /**
     * Search patients with OPD - JPA implementation of USP_Search_Patient_With_OPD
     * 
     * This endpoint matches the stored procedure logic with multi-clinic support:
     * - Result Set 1: Patient Master search (ID : FirstName MiddleName LastName : Mobile)
     * - Result Set 2: Discharge Data search (IPD_RefNo : ID : FirstName MiddleName LastName : Mobile)
     * - Result Set 3: Discharge Bill search - unprinted bills only (FirstName LastName : IPD_RefNo : Bill_No : Bill_Date)
     * - Result Set 4: Discharge Invoice search - unprinted invoices only (FirstName LastName : IPD_RefNo : Invoice_No : Invoice_Date)
     * 
     * The search string is processed to add % wildcards around words (matching WebService preprocessing).
     * All queries are filtered by clinic_id for proper multi-clinic isolation.
     * 
     * @param prefixText Search text (patient ID, name, mobile, etc.)
     * @param doctorId Doctor ID for filtering bills and invoices (required)
     * @param clinicId Clinic ID for multi-clinic isolation (required)
     * @return Map containing 4 result sets matching stored procedure format
     */
    @GetMapping("/search-with-opd")
    @Operation(
        summary = "Search patients with OPD",
        description = "JPA implementation of USP_Search_Patient_With_OPD stored procedure with multi-clinic support. " +
                      "Returns 4 result sets: Patient Master, Discharge Data, Unprinted Bills, and Unprinted Invoices. " +
                      "All queries are filtered by clinic_id for proper data isolation. " +
                      "Used by Manage Admission Card page for autocomplete search."
    )
    public ResponseEntity<Map<String, Object>> searchPatientWithOPD(
            @Parameter(description = "Search text (patient ID, name, mobile, etc.)", required = true, example = "John")
            @RequestParam String prefixText,
            
            @Parameter(description = "Doctor ID for filtering bills and invoices", required = true, example = "DR-00010")
            @RequestParam String doctorId,
            
            @Parameter(description = "Clinic ID for multi-clinic isolation", required = true, example = "CL-00001")
            @RequestParam String clinicId) {
        
        try {
            Map<String, Object> result = patientSearchService.searchPatientWithOPD(prefixText, doctorId, clinicId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new java.util.HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to search patients: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
