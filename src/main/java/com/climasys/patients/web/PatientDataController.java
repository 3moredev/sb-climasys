package com.climasys.patients.web;

import com.climasys.patients.service.PatientDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for patient data management and validation
 */
@RestController
@RequestMapping("/api/patients/data")
@CrossOrigin(origins = "*")
public class PatientDataController {

    @Autowired
    private PatientDataService patientDataService;

    /**
     * Get family information and details
     */
    @GetMapping("/family/{familyId}")
    public ResponseEntity<List<Map<String, Object>>> getFamilyInformation(@PathVariable String familyId) {
        List<Map<String, Object>> result = patientDataService.getFamilyInformation(familyId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get family folder details for record management
     */
    @GetMapping("/family-folder/{familyId}")
    public ResponseEntity<List<Map<String, Object>>> getFamilyFolderDetails(@PathVariable String familyId) {
        List<Map<String, Object>> result = patientDataService.getFamilyFolderDetails(familyId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get admission card data for patient admission
     */
    @GetMapping("/admission-card/{patientId}")
    public ResponseEntity<List<Map<String, Object>>> getAdmissionCardData(@PathVariable String patientId) {
        List<Map<String, Object>> result = patientDataService.getAdmissionCardData(patientId);
        return ResponseEntity.ok(result);
    }

    /**
     * Validate patient billing data before processing
     */
    @GetMapping("/validate-billing")
    public ResponseEntity<List<Map<String, Object>>> validatePatientBillingData(
            @RequestParam String patientId,
            @RequestParam String visitId) {
        List<Map<String, Object>> result = patientDataService.validatePatientBillingData(patientId, visitId);
        return ResponseEntity.ok(result);
    }

    /**
     * Validate patient discharge data
     */
    @GetMapping("/validate-discharge")
    public ResponseEntity<List<Map<String, Object>>> validatePatientDischargeData(
            @RequestParam String patientId,
            @RequestParam String visitId) {
        List<Map<String, Object>> result = patientDataService.validatePatientDischargeData(patientId, visitId);
        return ResponseEntity.ok(result);
    }

    /**
     * Validate patient invoice data
     */
    @GetMapping("/validate-invoice")
    public ResponseEntity<List<Map<String, Object>>> validatePatientInvoiceData(
            @RequestParam String patientId,
            @RequestParam String visitId) {
        List<Map<String, Object>> result = patientDataService.validatePatientInvoiceData(patientId, visitId);
        return ResponseEntity.ok(result);
    }
}
