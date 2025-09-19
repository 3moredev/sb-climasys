package com.climasys.billing.web;

import com.climasys.billing.service.BillingMasterDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for billing master data management
 */
@RestController
@RequestMapping("/api/billing/master-data")
@CrossOrigin(origins = "*")
public class BillingMasterDataController {

    @Autowired
    private BillingMasterDataService billingMasterDataService;

    /**
     * Get billing categories grouped by doctor
     */
    @GetMapping("/categories/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getBillingCategories(@PathVariable String doctorId) {
        List<Map<String, Object>> result = billingMasterDataService.getBillingCategories(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get companies for billing
     */
    @GetMapping("/companies/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getBillingCompanies(@PathVariable String doctorId) {
        List<Map<String, Object>> result = billingMasterDataService.getBillingCompanies(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete billing charges
     */
    @DeleteMapping("/charges/{chargeId}")
    public ResponseEntity<List<Map<String, Object>>> deleteBillingCharges(@PathVariable String chargeId) {
        List<Map<String, Object>> result = billingMasterDataService.deleteBillingCharges(chargeId);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete bill keyword charges
     */
    @DeleteMapping("/keyword-charges/{keywordChargeId}")
    public ResponseEntity<List<Map<String, Object>>> deleteBillKeywordCharges(@PathVariable String keywordChargeId) {
        List<Map<String, Object>> result = billingMasterDataService.deleteBillKeywordCharges(keywordChargeId);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete bill sub charges
     */
    @DeleteMapping("/sub-charges/{subChargeId}")
    public ResponseEntity<List<Map<String, Object>>> deleteBillSubCharges(@PathVariable String subChargeId) {
        List<Map<String, Object>> result = billingMasterDataService.deleteBillSubCharges(subChargeId);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete master billing detail
     */
    @DeleteMapping("/billing-details/{billingDetailId}")
    public ResponseEntity<List<Map<String, Object>>> deleteMasterBillingDetail(@PathVariable String billingDetailId) {
        List<Map<String, Object>> result = billingMasterDataService.deleteMasterBillingDetail(billingDetailId);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete master company
     */
    @DeleteMapping("/companies/{companyId}")
    public ResponseEntity<List<Map<String, Object>>> deleteMasterCompany(@PathVariable String companyId) {
        List<Map<String, Object>> result = billingMasterDataService.deleteMasterCompany(companyId);
        return ResponseEntity.ok(result);
    }
}
