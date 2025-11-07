package com.climasys.fees.web;

import com.climasys.fees.service.FeesDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/fees")
public class FeesController {

    private final FeesDetailsService feesDetailsService;

    public FeesController(FeesDetailsService feesDetailsService) {
        this.feesDetailsService = feesDetailsService;
    }

    /**
     * JPA endpoint equivalent to USP_Get_Patient_FeesDetails
     * Returns patient fees details for individual visits
     * 
     * @param patientId Patient ID (required)
     * @param doctorId Doctor ID (optional, filters by doctor if provided)
     * @param clinicId Clinic ID (required, filters by clinic)
     * @return Response with success, patientId, doctorId, clinicId, header (folder_no, full_name), and rows (list of fee details)
     */
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getFeesDetails(
            @RequestParam String patientId,
            @RequestParam(required = false) String doctorId,
            @RequestParam String clinicId) {
        Map<String, Object> result = feesDetailsService.getPatientFeesDetails(patientId, doctorId, clinicId);
        return ResponseEntity.ok(result);
    }

    /**
     * JPA endpoint equivalent to USP_Get_ConsolidatedFamilyFees
     * Returns consolidated fees aggregated by financial year for a specific patient
     * 
     * @param patientId Patient ID (required)
     * @param doctorId Doctor ID (optional, filters by doctor if provided)
     * @param clinicId Clinic ID (required, filters by clinic)
     * @return Response with success, patientId, doctorId, clinicId, and rows (list of financial year summaries)
     */
    @GetMapping("/consolidated-family-fees")
    public ResponseEntity<Map<String, Object>> getConsolidatedFamilyFees(
            @RequestParam String patientId,
            @RequestParam(required = false) String doctorId,
            @RequestParam String clinicId) {
        Map<String, Object> result = feesDetailsService.getConsolidatedFamilyFees(patientId, doctorId, clinicId);
        return ResponseEntity.ok(result);
    }

    /**
     * JPA/JDBC equivalent for USP_Get_PatientFolderAmountForBilling.
     * Returns ALL visits for a patient from patient_visits_services table.
     * This is used to calculate the total A/C balance by summing all visit balances.
     * 
     * @param clinicId Clinic ID (required)
     * @param doctorId Doctor ID (required, though not used in WHERE clause currently)
     * @param patientId Patient ID (required)
     * @return Response with success, clinicId, doctorId, patientId, rows (list of all visits with billing details), and totalAcBalance (sum of all balances)
     */
    @GetMapping("/folder-amount")
    public ResponseEntity<Map<String, Object>> getPatientFolderAmountForBilling(
            @RequestParam String clinicId,
            @RequestParam String doctorId,
            @RequestParam String patientId) {
        Map<String, Object> result = feesDetailsService.getPatientFolderAmountForBilling(
                clinicId, doctorId, patientId);
        return ResponseEntity.ok(result);
    }
}


