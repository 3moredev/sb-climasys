package com.climasys.insurance.web;

import com.climasys.insurance.service.InsuranceCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for insurance company management
 */
@RestController
@RequestMapping("/api/insurance/companies")
@CrossOrigin(origins = "*")
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
}
