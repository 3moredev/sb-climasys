package com.climasys.billing.web;

import com.climasys.billing.service.BillingCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for billing calculations and amount management
 */
@RestController
@RequestMapping("/api/billing/calculations")
@CrossOrigin(origins = "*")
public class BillingCalculationController {

    @Autowired
    private BillingCalculationService billingCalculationService;

    /**
     * Calculate total amount for a patient visit
     */
    @GetMapping("/visit-amount")
    public ResponseEntity<List<Map<String, Object>>> calculateVisitAmount(
            @RequestParam String patientId,
            @RequestParam String visitId) {
        List<Map<String, Object>> result = billingCalculationService.calculateVisitAmount(patientId, visitId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get billing status for a patient visit
     */
    @GetMapping("/billing-status")
    public ResponseEntity<List<Map<String, Object>>> getBillingStatus(
            @RequestParam String patientId,
            @RequestParam String visitId) {
        List<Map<String, Object>> result = billingCalculationService.getBillingStatus(patientId, visitId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get default fees for a doctor
     */
    @GetMapping("/default-fees/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getDefaultFees(@PathVariable String doctorId) {
        List<Map<String, Object>> result = billingCalculationService.getDefaultFees(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get default prescription count for a doctor
     */
    @GetMapping("/default-prescription-count/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getDefaultPrescriptionCount(@PathVariable String doctorId) {
        List<Map<String, Object>> result = billingCalculationService.getDefaultPrescriptionCount(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Generate hospital bill for a patient visit
     */
    @GetMapping("/hospital-bill")
    public ResponseEntity<List<Map<String, Object>>> generateHospitalBill(
            @RequestParam String patientId,
            @RequestParam String visitId) {
        List<Map<String, Object>> result = billingCalculationService.generateHospitalBill(patientId, visitId);
        return ResponseEntity.ok(result);
    }
}
