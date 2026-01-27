package com.climasys.billing.web;

import com.climasys.auth.annotation.RefreshSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Controller for billing stored procedure endpoints
 * Handles endpoints under /api/billing/stored-procs
 */
@RestController
@RequestMapping("/api/billing/stored-procs")
@RefreshSession
public class BillingStoredProcedureController {

    private static final Logger logger = LoggerFactory.getLogger(BillingStoredProcedureController.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Get category data billing group for a doctor
     * GET /api/billing/stored-procs/category-data-billing-group/{doctorId}
     */
    @GetMapping("/category-data-billing-group/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getCategoryDataBillingGroup(@PathVariable String doctorId) {
        try {
            logger.info("Getting category data billing group for doctorId: {}", doctorId);
            
            // Try the full query first with visit type description
            String sql = """
                SELECT DISTINCT
                    bdm.billing_group_name,
                    bdm.billing_subgroup_name,
                    bdm.billing_details,
                    bdm.default_fees,
                    bdm.visit_type,
                    COALESCE(bvt.billing_visittype_description, '') AS visit_type_description,
                    COALESCE(bdm.isdefault, false) AS isdefault,
                    COALESCE(bdm.sequence_no, 0) AS sequence_no
                FROM billing_details_master bdm
                LEFT JOIN billing_visittype_translations bvt 
                    ON bdm.visit_type = bvt.billing_visittype_id 
                    AND bvt.language_id = 1
                WHERE bdm.doctor_id = ?
                ORDER BY bdm.billing_group_name, bdm.billing_subgroup_name, 
                         COALESCE(bdm.sequence_no, 0) ASC
                """;
            
            List<Map<String, Object>> result;
            try {
                result = jdbcTemplate.queryForList(sql, doctorId);
            } catch (Exception sqlEx) {
                logger.warn("Full query failed, trying simplified query. Error: {}", sqlEx.getMessage());
                // Fallback to simpler query without visit type description join
                String simpleSql = """
                    SELECT DISTINCT
                        bdm.billing_group_name,
                        bdm.billing_subgroup_name,
                        bdm.billing_details,
                        bdm.default_fees,
                        bdm.visit_type,
                        '' AS visit_type_description,
                        COALESCE(bdm.isdefault, false) AS isdefault,
                        COALESCE(bdm.sequence_no, 0) AS sequence_no
                    FROM billing_details_master bdm
                    WHERE bdm.doctor_id = ?
                    ORDER BY bdm.billing_group_name, bdm.billing_subgroup_name, 
                             COALESCE(bdm.sequence_no, 0) ASC
                    """;
                result = jdbcTemplate.queryForList(simpleSql, doctorId);
            }
            
            logger.info("Found {} billing category records for doctorId: {}", result.size(), doctorId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error getting category data billing group for doctorId: {} - Error: {}", doctorId, e.getMessage(), e);
            throw new RuntimeException("Failed to get category data billing group for doctorId " + doctorId + ": " + e.getMessage(), e);
        }
    }

    /**
     * Get billing sub-groups based on selected group/category
     * GET /api/billing/stored-procs/sub-category-data?groupName={groupName}&doctorId={doctorId}
     * 
     * This corresponds to USP_Get_SubCatagoryData stored procedure
     * Used in: AddBillingDetails.aspx.cs - ddlCatagory_SelectedIndexChanged() method
     */
    @GetMapping("/sub-category-data")
    public ResponseEntity<List<Map<String, Object>>> getSubCategoryData(
            @RequestParam String groupName,
            @RequestParam String doctorId) {
        try {
            // URL decode and trim whitespace from input
            // Handle URL-encoded values like "PROFESSIONAL%20FEES" -> "PROFESSIONAL FEES"
            String decodedGroupName = "";
            if (groupName != null) {
                try {
                    // Try to URL decode (handles %20, %2B, etc.)
                    decodedGroupName = URLDecoder.decode(groupName, StandardCharsets.UTF_8).trim();
                } catch (IllegalArgumentException e) {
                    // If decoding fails (already decoded), just use the original value
                    decodedGroupName = groupName.trim();
                }
            }
            String trimmedDoctorId = doctorId != null ? doctorId.trim() : "";
            
            logger.info("Getting sub-category data for groupName (raw): '{}', (decoded): '{}', doctorId: {}", 
                       groupName, decodedGroupName, trimmedDoctorId);
            
            // Query to get billing sub-groups for a specific billing group and doctor
            // Use case-insensitive comparison (ILIKE) and trim to handle case/whitespace variations
            // This matches USP_Get_SubCatagoryData stored procedure logic
            String sql = """
                SELECT 
                    bsm.billing_subgroup_name,
                    bsm.billing_group_name,
                    bsm.doctor_id,
                    bsm.created_on,
                    bsm.createdby_name,
                    bsm.modified_on,
                    bsm.modifiedby_name
                FROM billing_subgroup_master bsm
                WHERE TRIM(bsm.billing_group_name) ILIKE TRIM(?)
                  AND TRIM(bsm.doctor_id) = TRIM(?)
                ORDER BY bsm.billing_subgroup_name
                """;
            
            // Use ILIKE pattern with exact match (case-insensitive)
            String searchPattern = decodedGroupName;
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, searchPattern, trimmedDoctorId);
            
            // If no results with exact match, try to see what groups exist for debugging
            if (result.isEmpty()) {
                logger.warn("No sub-categories found for groupName: '{}' (decoded from '{}'), doctorId: {}. Checking available groups...", 
                           decodedGroupName, groupName, trimmedDoctorId);
                
                // Debug query to see what groups exist for this doctor
                String debugSql = """
                    SELECT DISTINCT billing_group_name 
                    FROM billing_subgroup_master 
                    WHERE TRIM(doctor_id) = TRIM(?)
                    ORDER BY billing_group_name
                    """;
                List<Map<String, Object>> availableGroups = jdbcTemplate.queryForList(debugSql, trimmedDoctorId);
                logger.info("Available billing groups for doctorId {}: {}", trimmedDoctorId, 
                           availableGroups.stream()
                               .map(m -> m.get("billing_group_name"))
                               .toList());
            }
            
            logger.info("Found {} sub-category records for groupName: '{}' (decoded from '{}'), doctorId: {}", 
                       result.size(), decodedGroupName, groupName, trimmedDoctorId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error getting sub-category data for groupName: {}, doctorId: {} - Error: {}", 
                        groupName, doctorId, e.getMessage(), e);
            throw new RuntimeException("Failed to get sub-category data for groupName " + groupName + 
                                     " and doctorId " + doctorId + ": " + e.getMessage(), e);
        }
    }

    /**
     * Get company data for a doctor
     * GET /api/billing/stored-procs/company/{doctorId}
     */
    @GetMapping("/company/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getCompany(@PathVariable String doctorId) {
        try {
            // Query to get company data for billing
            // Note: company_master table structure: company_name (PK), company_description, loginname, loginpassword, delete_flag
            // If there's a doctor-specific company table, adjust accordingly
            String sql = """
                SELECT 
                    company_name,
                    company_description,
                    loginname,
                    loginpassword,
                    delete_flag
                FROM company_master
                WHERE (delete_flag IS NULL OR delete_flag = false)
                ORDER BY company_name
                """;
            
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get company: " + e.getMessage(), e);
        }
    }

    /**
     * Delete bill charges
     * DELETE /api/billing/stored-procs/bill-charges/{chargeId}
     */
    @DeleteMapping("/bill-charges/{chargeId}")
    public ResponseEntity<List<Map<String, Object>>> deleteBillCharges(@PathVariable String chargeId) {
        try {
            // Implementation depends on the actual table structure
            // This is a placeholder - adjust based on actual schema
            String sql = "DELETE FROM bill_charges WHERE charge_id = ?";
            jdbcTemplate.update(sql, chargeId);
            
            Map<String, Object> result = Map.of(
                "message", "Bill charges deleted successfully",
                "chargeId", chargeId
            );
            return ResponseEntity.ok(List.of(result));
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete bill charges: " + e.getMessage(), e);
        }
    }

    /**
     * Delete bill keyword charges
     * DELETE /api/billing/stored-procs/bill-keyword-charges/{keywordChargeId}
     */
    @DeleteMapping("/bill-keyword-charges/{keywordChargeId}")
    public ResponseEntity<List<Map<String, Object>>> deleteBillKeywordCharges(@PathVariable String keywordChargeId) {
        try {
            String sql = "DELETE FROM bill_keyword_charges WHERE keyword_charge_id = ?";
            jdbcTemplate.update(sql, keywordChargeId);
            
            Map<String, Object> result = Map.of(
                "message", "Bill keyword charges deleted successfully",
                "keywordChargeId", keywordChargeId
            );
            return ResponseEntity.ok(List.of(result));
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete bill keyword charges: " + e.getMessage(), e);
        }
    }

    /**
     * Delete bill sub charges
     * DELETE /api/billing/stored-procs/bill-sub-charges/{subChargeId}
     */
    @DeleteMapping("/bill-sub-charges/{subChargeId}")
    public ResponseEntity<List<Map<String, Object>>> deleteBillSubCharges(@PathVariable String subChargeId) {
        try {
            String sql = "DELETE FROM bill_sub_charges WHERE sub_charge_id = ?";
            jdbcTemplate.update(sql, subChargeId);
            
            Map<String, Object> result = Map.of(
                "message", "Bill sub charges deleted successfully",
                "subChargeId", subChargeId
            );
            return ResponseEntity.ok(List.of(result));
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete bill sub charges: " + e.getMessage(), e);
        }
    }

    /**
     * Delete master billing detail
     * DELETE /api/billing/stored-procs/master-billing-detail/{billingDetailId}
     */
    @DeleteMapping("/master-billing-detail/{billingDetailId}")
    public ResponseEntity<List<Map<String, Object>>> deleteMasterBillingDetail(@PathVariable String billingDetailId) {
        try {
            // Assuming billingDetailId is in format: billing_group_name*billing_subgroup_name*billing_details
            String[] parts = billingDetailId.split("\\*");
            if (parts.length == 3) {
                String sql = """
                    DELETE FROM billing_details_master 
                    WHERE billing_group_name = ? 
                      AND billing_subgroup_name = ? 
                      AND billing_details = ?
                    """;
                jdbcTemplate.update(sql, parts[0], parts[1], parts[2]);
            } else {
                throw new IllegalArgumentException("Invalid billing detail ID format");
            }
            
            Map<String, Object> result = Map.of(
                "message", "Master billing detail deleted successfully",
                "billingDetailId", billingDetailId
            );
            return ResponseEntity.ok(List.of(result));
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete master billing detail: " + e.getMessage(), e);
        }
    }

    /**
     * Delete master company
     * DELETE /api/billing/stored-procs/master-company/{companyId}
     */
    @DeleteMapping("/master-company/{companyId}")
    public ResponseEntity<List<Map<String, Object>>> deleteMasterCompany(@PathVariable String companyId) {
        try {
            // company_master uses company_name as primary key, not company_id
            // Set delete_flag instead of hard delete
            String sql = "UPDATE company_master SET delete_flag = true WHERE company_name = ?";
            jdbcTemplate.update(sql, companyId);
            
            Map<String, Object> result = Map.of(
                "message", "Master company deleted successfully",
                "companyName", companyId
            );
            return ResponseEntity.ok(List.of(result));
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete master company: " + e.getMessage(), e);
        }
    }
}
