package com.climasys.billing.service;

import com.climasys.entity.PatientVisit;
import com.climasys.entity.PatientVisitBillingInfo;
import com.climasys.entity.PatientVisitBillingInfoId;
import com.climasys.entity.PatientVisitBillingInfoOverwrite;
import com.climasys.entity.PatientVisitBillingInfoOverwriteId;
import com.climasys.entity.PatientVisitId;
import com.climasys.repository.PatientVisitBillingInfoOverwriteRepository;
import com.climasys.repository.PatientVisitBillingInfoRepository;
import com.climasys.repository.PatientVisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for managing patient visit billing breakup data.
 * Replicates the logic of USP_Insert_Billing_BreakupData and USP_Insert_Billing_BreakupData_Overwrite stored procedures.
 */
@Service
public class BillingBreakupService {
    
    private static final Logger logger = LoggerFactory.getLogger(BillingBreakupService.class);
    
    @Autowired
    private PatientVisitBillingInfoRepository billingInfoRepository;
    
    @Autowired
    private PatientVisitBillingInfoOverwriteRepository billingInfoOverwriteRepository;
    
    @Autowired
    private PatientVisitRepository patientVisitRepository;
    
    /**
     * Save billing breakup data to the base table (patient_visit_billinginfo).
     * This replicates the logic of USP_Insert_Billing_BreakupData stored procedure.
     * 
     * @param billingData List of billing items, each containing:
     *                    - doctorId, clinicId, shiftId, patientId, patientVisitNo, visitDate
     *                    - billingGroupName, billingSubgroupName, billingDetails
     *                    - defaultFees, collectedFees
     * @param userId User ID for audit fields
     * @param doctorId Doctor ID
     * @param shiftId Shift ID
     * @param patientId Patient ID
     * @param clinicId Clinic ID
     * @param visitDate Visit date
     * @param patientVisitNo Patient visit number
     * @return Map with success status and message
     */
    @Transactional
    public Map<String, Object> saveBillingBreakupData(
            List<Map<String, Object>> billingData,
            String userId,
            String doctorId,
            Short shiftId,
            String patientId,
            String clinicId,
            LocalDateTime visitDate,
            Integer patientVisitNo) {
        
        try {
            logger.info("Saving billing breakup data for patient: {}, visit: {}", patientId, patientVisitNo);
            
            // Resolve the exact visitDate from DB to satisfy FK constraints
            LocalDateTime effectiveVisitDate = resolveEffectiveVisitDate(doctorId, clinicId, shiftId, patientId, patientVisitNo, visitDate);
            if (effectiveVisitDate == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Parent visit not found for given identifiers; cannot save billing data.");
                return response;
            }
            
            // Step 1: Soft delete existing records (set delete_flag = true)
            // This replicates: UPDATE Patient_Visit_BillingInfo SET Delete_Flag=1 WHERE ...
            int deletedCount = billingInfoRepository.softDeleteByVisit(
                doctorId, clinicId, shiftId, patientId, effectiveVisitDate, patientVisitNo
            );
            logger.info("Soft deleted {} existing billing records", deletedCount);
            
            // Step 2: MERGE (insert or update) billing data
            // This replicates the MERGE logic from the stored procedure
            LocalDateTime now = LocalDateTime.now();
            Set<String> processedKeys = new HashSet<>();
            
            for (Map<String, Object> item : billingData) {
                // Handle different field name formats (camelCase or original)
                String billingGroupName = toStringSafe(
                    getValue(item, "billingGroupName", "Billing_Group_Name", "billing_group_name"), "");
                String billingSubgroupName = toStringSafe(
                    getValue(item, "billingSubgroupName", "Billing_Subgroup_Name", "billing_subgroup_name"), "");
                String billingDetails = toStringSafe(
                    getValue(item, "billingDetails", "Billing_Details", "billing_details"), "");
                
                // Create composite key
                String key = String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s",
                    effectiveVisitDate, doctorId, clinicId, shiftId, patientId, patientVisitNo,
                    billingGroupName, billingSubgroupName, billingDetails);
                
                // Skip duplicates (DISTINCT logic from stored procedure)
                if (processedKeys.contains(key)) {
                    logger.debug("Skipping duplicate billing item: {}", key);
                    continue;
                }
                processedKeys.add(key);
                
                // Extract values from item map (handle different field name formats)
                BigDecimal defaultFees = toBigDecimal(
                    getValue(item, "defaultFees", "Default_fees", "default_fees"));
                BigDecimal collectedFees = toBigDecimal(
                    getValue(item, "collectedFees", "Collected_Fees", "collected_fees"));
                
                // Create composite ID
                PatientVisitBillingInfoId id = new PatientVisitBillingInfoId(
                    effectiveVisitDate, doctorId, clinicId, shiftId, patientId, patientVisitNo,
                    billingGroupName, billingSubgroupName, billingDetails
                );
                
                // Check if record exists
                Optional<PatientVisitBillingInfo> existing = billingInfoRepository.findById(id);
                
                if (existing.isPresent()) {
                    // UPDATE existing record (WHEN MATCHED)
                    PatientVisitBillingInfo entity = existing.get();
                    entity.setCollectedFees(collectedFees);
                    entity.setModifiedOn(now);
                    entity.setModifiedbyName(userId);
                    entity.setDeleteFlag(false);
                    billingInfoRepository.save(entity);
                    logger.debug("Updated billing record: {}", id);
                } else {
                    // INSERT new record (WHEN NOT MATCHED)
                    PatientVisitBillingInfo entity = new PatientVisitBillingInfo();
                    entity.setVisitDate(effectiveVisitDate);
                    entity.setDoctorId(doctorId);
                    entity.setClinicId(clinicId);
                    entity.setShiftId(shiftId);
                    entity.setPatientId(patientId);
                    entity.setPatientVisitNo(patientVisitNo);
                    entity.setBillingGroupName(billingGroupName);
                    entity.setBillingSubgroupName(billingSubgroupName);
                    entity.setBillingDetails(billingDetails);
                    entity.setDefaultFees(defaultFees);
                    entity.setCollectedFees(collectedFees);
                    entity.setCreatedOn(now);
                    entity.setCreatedbyName(userId);
                    entity.setModifiedOn(now);
                    entity.setModifiedbyName(userId);
                    entity.setDeleteFlag(false);
                    billingInfoRepository.save(entity);
                    logger.debug("Inserted new billing record: {}", id);
                }
            }
            
            logger.info("Successfully saved {} billing breakup items", processedKeys.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Billing breakup data saved successfully");
            response.put("itemsProcessed", processedKeys.size());
            return response;
            
        } catch (Exception e) {
            logger.error("Error saving billing breakup data", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to save billing breakup data: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * Save billing breakup data to the overwrite table (patient_visit_billinginfooverwrite).
     * This replicates the logic of USP_Insert_Billing_BreakupData_Overwrite stored procedure.
     * 
     * @param billingData List of billing items
     * @param userId User ID for audit fields
     * @param doctorId Doctor ID
     * @param shiftId Shift ID
     * @param patientId Patient ID
     * @param clinicId Clinic ID
     * @param visitDate Visit date
     * @param patientVisitNo Patient visit number
     * @return Map with success status and message
     */
    @Transactional
    public Map<String, Object> saveBillingBreakupDataOverwrite(
            List<Map<String, Object>> billingData,
            String userId,
            String doctorId,
            Short shiftId,
            String patientId,
            String clinicId,
            LocalDateTime visitDate,
            Integer patientVisitNo) {
        
        try {
            logger.info("Saving billing breakup data (overwrite) for patient: {}, visit: {}", patientId, patientVisitNo);
            
            // Resolve the exact visitDate from DB to satisfy FK constraints
            LocalDateTime effectiveVisitDate = resolveEffectiveVisitDate(doctorId, clinicId, shiftId, patientId, patientVisitNo, visitDate);
            if (effectiveVisitDate == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Parent visit not found for given identifiers; cannot save billing overwrite data.");
                return response;
            }
            
            // Step 1: Soft delete existing records (set delete_flag = true)
            int deletedCount = billingInfoOverwriteRepository.softDeleteByVisit(
                doctorId, clinicId, shiftId, patientId, effectiveVisitDate, patientVisitNo
            );
            logger.info("Soft deleted {} existing billing overwrite records", deletedCount);
            
            // Step 2: MERGE (insert or update) billing data
            LocalDateTime now = LocalDateTime.now();
            Set<String> processedKeys = new HashSet<>();
            
            for (Map<String, Object> item : billingData) {
                // Handle different field name formats (camelCase or original)
                String billingGroupName = toStringSafe(
                    getValue(item, "billingGroupName", "Billing_Group_Name", "billing_group_name"), "");
                String billingSubgroupName = toStringSafe(
                    getValue(item, "billingSubgroupName", "Billing_Subgroup_Name", "billing_subgroup_name"), "");
                String billingDetails = toStringSafe(
                    getValue(item, "billingDetails", "Billing_Details", "billing_details"), "");
                
                // Create composite key for duplicate check
                String key = String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s",
                    effectiveVisitDate, doctorId, clinicId, shiftId, patientId, patientVisitNo,
                    billingGroupName, billingSubgroupName, billingDetails);
                
                // Skip duplicates (DISTINCT logic from stored procedure)
                if (processedKeys.contains(key)) {
                    logger.debug("Skipping duplicate billing overwrite item: {}", key);
                    continue;
                }
                processedKeys.add(key);
                
                // Extract values from item map (handle different field name formats)
                BigDecimal defaultFees = toBigDecimal(
                    getValue(item, "defaultFees", "Default_fees", "default_fees"));
                BigDecimal collectedFees = toBigDecimal(
                    getValue(item, "collectedFees", "Collected_Fees", "collected_fees"));
                
                // Create composite ID
                PatientVisitBillingInfoOverwriteId id = new PatientVisitBillingInfoOverwriteId(
                    effectiveVisitDate, doctorId, clinicId, shiftId, patientId, patientVisitNo,
                    billingGroupName, billingSubgroupName, billingDetails
                );
                
                // Check if record exists
                Optional<PatientVisitBillingInfoOverwrite> existing = billingInfoOverwriteRepository.findById(id);
                
                if (existing.isPresent()) {
                    // UPDATE existing record (WHEN MATCHED)
                    PatientVisitBillingInfoOverwrite entity = existing.get();
                    entity.setCollectedFees(collectedFees);
                    entity.setModifiedOn(now);
                    entity.setModifiedbyName(userId);
                    entity.setDeleteFlag(false);
                    billingInfoOverwriteRepository.save(entity);
                    logger.debug("Updated billing overwrite record: {}", id);
                } else {
                    // INSERT new record (WHEN NOT MATCHED)
                    PatientVisitBillingInfoOverwrite entity = new PatientVisitBillingInfoOverwrite();
                    entity.setVisitDate(effectiveVisitDate);
                    entity.setDoctorId(doctorId);
                    entity.setClinicId(clinicId);
                    entity.setShiftId(shiftId);
                    entity.setPatientId(patientId);
                    entity.setPatientVisitNo(patientVisitNo);
                    entity.setBillingGroupName(billingGroupName);
                    entity.setBillingSubgroupName(billingSubgroupName);
                    entity.setBillingDetails(billingDetails);
                    entity.setDefaultFees(defaultFees);
                    entity.setCollectedFees(collectedFees);
                    entity.setCreatedOn(now);
                    entity.setCreatedbyName(userId);
                    entity.setModifiedOn(now);
                    entity.setModifiedbyName(userId);
                    entity.setDeleteFlag(false);
                    billingInfoOverwriteRepository.save(entity);
                    logger.debug("Inserted new billing overwrite record: {}", id);
                }
            }
            
            logger.info("Successfully saved {} billing breakup overwrite items", processedKeys.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Billing breakup overwrite data saved successfully");
            response.put("itemsProcessed", processedKeys.size());
            return response;
            
        } catch (Exception e) {
            logger.error("Error saving billing breakup overwrite data", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to save billing breakup overwrite data: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * Resolve the exact visitDate from DB to satisfy FK constraints.
     * Similar to logic in VisitJpaService.saveMedicineOverwrite
     */
    private LocalDateTime resolveEffectiveVisitDate(
            String doctorId, String clinicId, Short shiftId,
            String patientId, Integer patientVisitNo, LocalDateTime visitDate) {
        
        PatientVisitId initialVisitId = new PatientVisitId(doctorId, clinicId, shiftId, patientId, patientVisitNo, visitDate);
        Optional<PatientVisit> parentVisitOpt = patientVisitRepository.findById(initialVisitId);
        
        if (parentVisitOpt.isPresent()) {
            return parentVisitOpt.get().getVisitDate();
        } else {
            // Fallback: match by same keys but only by date part (ignore time discrepancies)
            Optional<PatientVisit> parentByDateOpt = patientVisitRepository.findByCompositeKeyAndDate(
                patientId, doctorId, clinicId, shiftId, patientVisitNo, visitDate.toLocalDate()
            );
            if (parentByDateOpt.isPresent()) {
                return parentByDateOpt.get().getVisitDate();
            }
        }
        
        return null;
    }
    
    /**
     * Get value from map trying multiple key variations
     */
    private Object getValue(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return null;
    }
    
    private String toStringSafe(Object value, String defaultValue) {
        if (value == null) return defaultValue;
        return value.toString().trim();
    }
    
    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        try {
            return new BigDecimal(value.toString());
        } catch (Exception e) {
            logger.warn("Failed to convert value to BigDecimal: {}", value);
            return null;
        }
    }
}

