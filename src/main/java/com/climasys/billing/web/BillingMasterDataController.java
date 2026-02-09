package com.climasys.billing.web;

import com.climasys.auth.annotation.RefreshSession;

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
@RefreshSession
public class BillingMasterDataController {

    @Autowired
    private BillingMasterDataService billingMasterDataService;

    /**
     * Insert billing master data
     * POST /api/billing/master-data
     * 
     * Corresponds to USP_Insert_Billing_MasterData stored procedure
     * Used in: AddBillingDetails.aspx.cs - btnSubmit_Click() method
     * Service method: SaveBilling_MasterData()
     * 
     * Request body should contain:
     * - groupName (p_var_group): Billing Group Name
     * - subgroupName (p_var_subgroup): Billing Sub-Group Name
     * - userId (p_var_User_Id): User ID
     * - detail (p_var_detail): Billing Details
     * - defaultFee (p_var_default_fee): Default Fee (Amount)
     * - doctorId (p_var_doctor_ID): Doctor ID
     * - sequenceNo (p_var_Sequence_no): Sequence Number
     * - isDefault (p_var_Isdefault): Is Default (Boolean)
     * - visitType (p_var_VisitType): Visit Type
     * - billingDataTable (p_var_Insert_Billing_Data): Optional DataTable with
     * billing details
     * (OLD_GROUP, OLD_SUBGROUP, OLD_DETAILS, OLD_AMOUNT, NEW_GROUP, NEW_SUBGROUP,
     * NEW_DETAILS, NEW_AMOUNT, DOCTOR_ID, NEW_VISIT_TYPE, OLD_VISIT_TYPE)
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> insertBillingMasterData(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = billingMasterDataService.saveBillingMasterData(request);
        if (Boolean.TRUE.equals(result.get("success"))) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Update billing master data
     * PUT /api/billing/master-data
     * 
     * Corresponds to USP_Insert_Billing_MasterData stored procedure (handles both
     * insert and update)
     * Used in: AddBillingDetails.aspx.cs - btnSubmit_Click() method
     * Service method: SaveBilling_MasterData()
     * 
     * Request body should contain:
     * - groupName (p_var_group): Billing Group Name
     * - subgroupName (p_var_subgroup): Billing Sub-Group Name
     * - userId (p_var_User_Id): User ID
     * - detail (p_var_detail): Billing Details
     * - defaultFee (p_var_default_fee): Default Fee (Amount)
     * - doctorId (p_var_doctor_ID): Doctor ID
     * - sequenceNo (p_var_Sequence_no): Sequence Number
     * - isDefault (p_var_Isdefault): Is Default (Boolean)
     * - visitType (p_var_VisitType): Visit Type
     * - billingDataTable (p_var_Insert_Billing_Data): Optional DataTable with
     * billing details
     * (OLD_GROUP, OLD_SUBGROUP, OLD_DETAILS, OLD_AMOUNT, NEW_GROUP, NEW_SUBGROUP,
     * NEW_DETAILS, NEW_AMOUNT, DOCTOR_ID, NEW_VISIT_TYPE, OLD_VISIT_TYPE)
     */
    @PutMapping
    public ResponseEntity<Map<String, Object>> updateBillingMasterData(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = billingMasterDataService.saveBillingMasterData(request);
        if (Boolean.TRUE.equals(result.get("success"))) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Get billing categories grouped by doctor
     */
    @GetMapping("/categories/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getBillingCategories(@PathVariable String doctorId) {
        List<Map<String, Object>> result = billingMasterDataService.getBillingCategories(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get billing sub-groups based on selected group/category
     * GET
     * /api/billing/master-data/sub-categories?groupName={groupName}&doctorId={doctorId}
     * 
     * This corresponds to USP_Get_SubCatagoryData stored procedure
     * Used in: AddBillingDetails.aspx.cs - ddlCatagory_SelectedIndexChanged()
     * method
     */
    @GetMapping("/sub-categories")
    public ResponseEntity<List<Map<String, Object>>> getBillingSubCategories(
            @RequestParam String groupName,
            @RequestParam String doctorId) {
        List<Map<String, Object>> result = billingMasterDataService.getBillingSubCategories(groupName, doctorId);
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
    public ResponseEntity<List<Map<String, Object>>> deleteMasterBillingDetail(
            @PathVariable String billingDetailId,
            @RequestParam(required = true) String doctorId) {
        List<Map<String, Object>> result = billingMasterDataService.deleteMasterBillingDetail(billingDetailId,
                doctorId);
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