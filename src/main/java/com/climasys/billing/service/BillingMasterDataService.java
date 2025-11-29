package com.climasys.billing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for billing master data management
 */
@Service
public class BillingMasterDataService {

    private static final Logger logger = LoggerFactory.getLogger(BillingMasterDataService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${app.api.base-url:http://localhost:8080/api}")
    private String baseUrl;

    /**
     * Get billing categories grouped by doctor
     */
    public List<Map<String, Object>> getBillingCategories(String doctorId) {
        try {
            String url = baseUrl + "/billing/stored-procs/category-data-billing-group/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get billing categories: " + e.getMessage(), e);
        }
    }

    /**
     * Get billing sub-groups based on selected group/category
     * Corresponds to USP_Get_SubCatagoryData stored procedure
     * 
     * @param groupName The billing group/category name
     * @param doctorId The doctor ID
     * @return List of billing sub-groups for the specified group and doctor
     */
    public List<Map<String, Object>> 
    getBillingSubCategories(String groupName, String doctorId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/billing/stored-procs/sub-category-data")
                    .queryParam("groupName", groupName)
                    .queryParam("doctorId", doctorId)
                    .toUriString();
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get billing sub-categories: " + e.getMessage(), e);
        }
    }

    /**
     * Get companies for billing
     */
    public List<Map<String, Object>> getBillingCompanies(String doctorId) {
        try {
            String url = baseUrl + "/billing/stored-procs/company/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get billing companies: " + e.getMessage(), e);
        }
    }

    /**
     * Delete billing charges
     */
    public List<Map<String, Object>> deleteBillingCharges(String chargeId) {
        try {
            String url = baseUrl + "/billing/stored-procs/bill-charges/" + chargeId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.DELETE, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete billing charges: " + e.getMessage(), e);
        }
    }

    /**
     * Delete bill keyword charges
     */
    public List<Map<String, Object>> deleteBillKeywordCharges(String keywordChargeId) {
        try {
            String url = baseUrl + "/billing/stored-procs/bill-keyword-charges/" + keywordChargeId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.DELETE, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete bill keyword charges: " + e.getMessage(), e);
        }
    }

    /**
     * Delete bill sub charges
     */
    public List<Map<String, Object>> deleteBillSubCharges(String subChargeId) {
        try {
            String url = baseUrl + "/billing/stored-procs/bill-sub-charges/" + subChargeId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.DELETE, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete bill sub charges: " + e.getMessage(), e);
        }
    }

    /**
     * Delete master billing detail
     */
    public List<Map<String, Object>> deleteMasterBillingDetail(String billingDetailId) {
        try {
            String url = baseUrl + "/billing/stored-procs/master-billing-detail/" + billingDetailId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.DELETE, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete master billing detail: " + e.getMessage(), e);
        }
    }

    /**
     * Delete master company
     */
    public List<Map<String, Object>> deleteMasterCompany(String companyId) {
        try {
            String url = baseUrl + "/billing/stored-procs/master-company/" + companyId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.DELETE, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete master company: " + e.getMessage(), e);
        }
    }

    /**
     * Save billing master data (insert or update)
     * Corresponds to USP_Insert_Billing_MasterData stored procedure
     * Service method: SaveBilling_MasterData()
     * 
     * @param request Map containing billing master data parameters
     * @return Map with success status and message
     */
    @Transactional
    public Map<String, Object> saveBillingMasterData(Map<String, Object> request) {
        try {
            logger.info("Saving billing master data for doctorId: {}", request.get("doctorId"));
            
            LocalDateTime now = LocalDateTime.now();
            String userId = getStringValue(request, "userId", "user_Id");
            String doctorId = getStringValue(request, "doctorId", "doctor_ID");
            String groupName = getStringValue(request, "groupName", "group");
            String subgroupName = getStringValue(request, "subgroupName", "subgroup");
            String detail = getStringValue(request, "detail");
            BigDecimal defaultFee = getBigDecimalValue(request, "defaultFee", "default_fee");
            Integer sequenceNo = getIntegerValue(request, "sequenceNo", "sequence_no");
            Boolean isDefault = getBooleanValue(request, "isDefault", "isdefault");
            String visitType = getStringValue(request, "visitType", "visitType");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> billingDataTable = (List<Map<String, Object>>) request.get("billingDataTable");
            
            // Step 1: Ensure billing group exists
            if (groupName != null && !groupName.trim().isEmpty()) {
                ensureBillingGroupExists(doctorId, groupName, userId, now);
            }
            
            // Step 2: Ensure billing subgroup exists
            if (subgroupName != null && !subgroupName.trim().isEmpty()) {
                ensureBillingSubgroupExists(doctorId, groupName, subgroupName, userId, now);
            }
            
            // Step 3: Handle bulk operations from DataTable
            if (billingDataTable != null && !billingDataTable.isEmpty()) {
                return saveBillingMasterDataBulk(billingDataTable, userId, now);
            } else if (detail != null && !detail.trim().isEmpty()) {
                // Step 4: Handle single record operation
                return saveBillingMasterDataSingle(doctorId, groupName, subgroupName, detail, 
                                                  defaultFee, sequenceNo, isDefault, visitType, userId, now);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Either detail or billingDataTable must be provided");
                return response;
            }
            
        } catch (Exception e) {
            logger.error("Error saving billing master data: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to save billing master data: " + e.getMessage());
            return response;
        }
    }

    private void ensureBillingGroupExists(String doctorId, String groupName, String userId, LocalDateTime now) {
        String checkSql = "SELECT COUNT(*) FROM billing_group_master WHERE doctor_id = ? AND billing_group_name = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, doctorId, groupName);
        
        if (count == null || count == 0) {
            String insertSql = "INSERT INTO billing_group_master (doctor_id, billing_group_name, created_on, createdby_name) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(insertSql, doctorId, groupName, now, userId);
            logger.debug("Inserted billing group: {} for doctor: {}", groupName, doctorId);
        }
    }

    private void ensureBillingSubgroupExists(String doctorId, String groupName, String subgroupName, String userId, LocalDateTime now) {
        if (subgroupName == null || subgroupName.trim().isEmpty()) {
            return;
        }
        
        String checkSql = "SELECT COUNT(*) FROM billing_subgroup_master WHERE doctor_id = ? AND billing_group_name = ? AND billing_subgroup_name = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, doctorId, groupName, subgroupName);
        
        if (count == null || count == 0) {
            String insertSql = "INSERT INTO billing_subgroup_master (doctor_id, billing_group_name, billing_subgroup_name, created_on, createdby_name) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(insertSql, doctorId, groupName, subgroupName, now, userId);
            logger.debug("Inserted billing subgroup: {} for group: {} and doctor: {}", subgroupName, groupName, doctorId);
        }
    }

    /**
     * Normalize visit type to single character (character(1) constraint)
     * Returns first character if value is provided, null otherwise
     */
    private String normalizeVisitType(String visitType) {
        if (visitType == null || visitType.trim().isEmpty()) {
            return null;
        }
        // Take only the first character
        String trimmed = visitType.trim();
        return trimmed.length() > 0 ? String.valueOf(trimmed.charAt(0)) : null;
    }

    private Map<String, Object> saveBillingMasterDataSingle(String doctorId, String groupName, String subgroupName, 
                                                           String detail, BigDecimal defaultFee, Integer sequenceNo, 
                                                           Boolean isDefault, String visitType, String userId, LocalDateTime now) {
        // visit_type is character(1), so truncate to first character or set to null if empty
        String normalizedVisitType = normalizeVisitType(visitType);
        
        String checkSql = """
            SELECT COUNT(*) FROM billing_details_master 
            WHERE doctor_id = ? AND billing_group_name = ? AND billing_subgroup_name = ? AND billing_details = ?
            """;
        
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, doctorId, groupName, subgroupName, detail);
        
        if (count != null && count > 0) {
            // UPDATE existing record
            String updateSql = """
                UPDATE billing_details_master 
                SET default_fees = ?, sequence_no = ?, isdefault = ?, visit_type = ?, modified_on = ?, modifiedby_name = ?
                WHERE doctor_id = ? AND billing_group_name = ? AND billing_subgroup_name = ? AND billing_details = ?
                """;
            
            jdbcTemplate.update(updateSql, defaultFee, sequenceNo, isDefault != null ? isDefault : false, 
                              normalizedVisitType, now, userId, doctorId, groupName, subgroupName, detail);
            logger.info("Updated billing master data: {} - {} - {}", groupName, subgroupName, detail);
        } else {
            // INSERT new record
            String insertSql = """
                INSERT INTO billing_details_master 
                    (doctor_id, billing_group_name, billing_subgroup_name, billing_details, 
                     default_fees, sequence_no, isdefault, visit_type, created_on, createdby_name, modified_on, modifiedby_name)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
            
            jdbcTemplate.update(insertSql, doctorId, groupName, subgroupName, detail, defaultFee, sequenceNo, 
                              isDefault != null ? isDefault : false, normalizedVisitType, now, userId, now, userId);
            logger.info("Inserted billing master data: {} - {} - {}", groupName, subgroupName, detail);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Billing master data saved successfully");
        return response;
    }

    private Map<String, Object> saveBillingMasterDataBulk(List<Map<String, Object>> billingDataTable, String userId, LocalDateTime now) {
        int processedCount = 0;
        
        for (Map<String, Object> row : billingDataTable) {
            try {
                String oldGroup = getStringValue(row, "OLD_GROUP", "oldGroup");
                String oldSubgroup = getStringValue(row, "OLD_SUBGROUP", "oldSubgroup");
                String oldDetails = getStringValue(row, "OLD_DETAILS", "oldDetails");
                String newGroup = getStringValue(row, "NEW_GROUP", "newGroup");
                String newSubgroup = getStringValue(row, "NEW_SUBGROUP", "newSubgroup");
                String newDetails = getStringValue(row, "NEW_DETAILS", "newDetails");
                BigDecimal newAmount = getBigDecimalValue(row, "NEW_AMOUNT", "newAmount");
                String rowDoctorId = getStringValue(row, "DOCTOR_ID", "doctorId");
                String newVisitType = getStringValue(row, "NEW_VISIT_TYPE", "newVisitType");
                
                String effectiveDoctorId = rowDoctorId != null && !rowDoctorId.isEmpty() ? rowDoctorId : null;
                if (effectiveDoctorId == null) {
                    logger.warn("Skipping row - no doctor ID provided");
                    continue;
                }
                
                if (oldGroup != null && oldSubgroup != null && oldDetails != null) {
                    // Update operation
                    if (newGroup != null && !newGroup.isEmpty()) {
                        ensureBillingGroupExists(effectiveDoctorId, newGroup, userId, now);
                    }
                    if (newGroup != null && newSubgroup != null && !newSubgroup.isEmpty()) {
                        ensureBillingSubgroupExists(effectiveDoctorId, newGroup, newSubgroup, userId, now);
                    }
                    
                    // Normalize visit type to single character
                    String normalizedNewVisitType = normalizeVisitType(newVisitType);
                    
                    String updateSql = """
                        UPDATE billing_details_master 
                        SET billing_group_name = COALESCE(?, billing_group_name),
                            billing_subgroup_name = COALESCE(?, billing_subgroup_name),
                            billing_details = COALESCE(?, billing_details),
                            default_fees = COALESCE(?, default_fees),
                            visit_type = COALESCE(?, visit_type),
                            modified_on = ?, modifiedby_name = ?
                        WHERE doctor_id = ? AND billing_group_name = ? AND billing_subgroup_name = ? AND billing_details = ?
                        """;
                    
                    int updated = jdbcTemplate.update(updateSql, newGroup, newSubgroup, newDetails, newAmount, 
                                                     normalizedNewVisitType, now, userId, effectiveDoctorId, oldGroup, oldSubgroup, oldDetails);
                    if (updated > 0) {
                        processedCount++;
                    }
                } else if (newGroup != null && newSubgroup != null && newDetails != null) {
                    // Insert operation
                    ensureBillingGroupExists(effectiveDoctorId, newGroup, userId, now);
                    ensureBillingSubgroupExists(effectiveDoctorId, newGroup, newSubgroup, userId, now);
                    
                    String checkSql = """
                        SELECT COUNT(*) FROM billing_details_master 
                        WHERE doctor_id = ? AND billing_group_name = ? AND billing_subgroup_name = ? AND billing_details = ?
                        """;
                    Integer exists = jdbcTemplate.queryForObject(checkSql, Integer.class, 
                                                               effectiveDoctorId, newGroup, newSubgroup, newDetails);
                    
                    // Normalize visit type to single character
                    String normalizedNewVisitType = normalizeVisitType(newVisitType);
                    
                    if (exists != null && exists > 0) {
                        String updateSql = """
                            UPDATE billing_details_master 
                            SET default_fees = ?, visit_type = ?, modified_on = ?, modifiedby_name = ?
                            WHERE doctor_id = ? AND billing_group_name = ? AND billing_subgroup_name = ? AND billing_details = ?
                            """;
                        jdbcTemplate.update(updateSql, newAmount, normalizedNewVisitType, now, userId, 
                                          effectiveDoctorId, newGroup, newSubgroup, newDetails);
                    } else {
                        String insertSql = """
                            INSERT INTO billing_details_master 
                                (doctor_id, billing_group_name, billing_subgroup_name, billing_details, 
                                 default_fees, visit_type, created_on, createdby_name, modified_on, modifiedby_name)
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                            """;
                        jdbcTemplate.update(insertSql, effectiveDoctorId, newGroup, newSubgroup, newDetails, 
                                          newAmount, normalizedNewVisitType, now, userId, now, userId);
                    }
                    processedCount++;
                }
            } catch (Exception e) {
                logger.error("Error processing billing data row: {}", e.getMessage(), e);
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Billing master data saved successfully");
        response.put("processedCount", processedCount);
        return response;
    }

    private String getStringValue(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value == null) {
                // Try camelCase version
                String camelKey = toCamelCase(key);
                value = map.get(camelKey);
            }
            if (value != null) {
                return value.toString().trim();
            }
        }
        return null;
    }

    private BigDecimal getBigDecimalValue(Map<String, Object> map, String... keys) {
        Object value = null;
        for (String key : keys) {
            value = map.get(key);
            if (value == null) {
                String camelKey = toCamelCase(key);
                value = map.get(camelKey);
            }
            if (value != null) break;
        }
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
        try {
            return new BigDecimal(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getIntegerValue(Map<String, Object> map, String... keys) {
        Object value = null;
        for (String key : keys) {
            value = map.get(key);
            if (value == null) {
                String camelKey = toCamelCase(key);
                value = map.get(camelKey);
            }
            if (value != null) break;
        }
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean getBooleanValue(Map<String, Object> map, String... keys) {
        Object value = null;
        for (String key : keys) {
            value = map.get(key);
            if (value == null) {
                String camelKey = toCamelCase(key);
                value = map.get(camelKey);
            }
            if (value != null) break;
        }
        if (value == null) return null;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).intValue() != 0;
        return Boolean.parseBoolean(value.toString());
    }

    private String toCamelCase(String str) {
        if (str == null || str.isEmpty()) return str;
        String[] parts = str.toLowerCase().split("_");
        StringBuilder result = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                result.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    result.append(parts[i].substring(1));
                }
            }
        }
        return result.toString();
    }
}
