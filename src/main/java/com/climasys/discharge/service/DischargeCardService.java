package com.climasys.discharge.service;

import com.climasys.discharge.dto.DischargeCardDTO;
import com.climasys.discharge.repository.DischargeCardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for discharge card operations
 * Replaces USP_Get_Patient_All_Discharge_Cards stored procedure business logic
 * Handles Manage Discharge Card screen functionality
 */
@Service
@Transactional
public class DischargeCardService {
    
    private static final Logger logger = LoggerFactory.getLogger(DischargeCardService.class);
    
    @Autowired
    private DischargeCardRepository dischargeCardRepository;
    
    /**
     * Get all admitted patients for "List of Admitted Patient/s" table
     * Matches Table[5] from USP_Get_Patient_All_Discharge_Cards
     * Includes duplicate removal logic by IPD_RefNo (matching old codebase behavior)
     * 
     * @param doctorId Doctor ID (optional - if null, returns all doctors for the clinic)
     * @param clinicId Clinic ID
     * @return List of admitted patients
     */
    @Transactional(readOnly = true)
    public List<DischargeCardDTO> getAllAdmittedPatients(String doctorId, String clinicId) {
        logger.info("Getting all admitted patients for doctor: {}, clinic: {}", 
                    doctorId != null ? doctorId : "ALL", clinicId);
        
        List<Map<String, Object>> results = dischargeCardRepository
                .findAllAdmittedPatients(doctorId, clinicId);
        
        // Apply duplicate removal logic by IPD_RefNo (matching Table[5] behavior)
        List<DischargeCardDTO> dischargeCards = removeDuplicatesByIpdRefNo(results);
        
        logger.info("Retrieved {} admitted patient(s) after duplicate removal", dischargeCards.size());
        return dischargeCards;
    }
    
    /**
     * Get discharge cards for a specific patient (search results)
     * Matches Table[0] from USP_Get_Patient_All_Discharge_Cards
     * Used when searching for a specific patient
     * 
     * @param patientId Patient ID
     * @param doctorId Doctor ID (optional - if null, returns all doctors for the clinic)
     * @param clinicId Clinic ID
     * @return List of discharge cards for the patient
     */
    @Transactional(readOnly = true)
    public List<DischargeCardDTO> getDischargeCardsByPatient(String patientId, String doctorId, String clinicId) {
        logger.info("Getting discharge cards for patient: {}, doctor: {}, clinic: {}", 
                    patientId, doctorId != null ? doctorId : "ALL", clinicId);
        
        List<Map<String, Object>> results = dischargeCardRepository
                .findDischargeCardsByPatient(patientId, doctorId, clinicId);
        
        List<DischargeCardDTO> dischargeCards = results.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        logger.info("Retrieved {} discharge card(s) for patient: {}", dischargeCards.size(), patientId);
        return dischargeCards;
    }
    
    /**
     * Search discharge cards by patient ID, name, contact, or IPD number
     * Used for the search functionality on Manage Discharge Card screen
     * 
     * @param searchStr Search string
     * @param doctorId Doctor ID (optional - if null, searches all doctors for the clinic)
     * @param clinicId Clinic ID
     * @return List of matching discharge cards
     */
    @Transactional(readOnly = true)
    public List<DischargeCardDTO> searchDischargeCards(String searchStr, String doctorId, String clinicId) {
        logger.info("Searching discharge cards for: '{}', doctor: {}, clinic: {}", 
                    searchStr, doctorId != null ? doctorId : "ALL", clinicId);
        
        List<Map<String, Object>> results = dischargeCardRepository
                .searchDischargeCards(searchStr, doctorId, clinicId);
        
        List<DischargeCardDTO> dischargeCards = results.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        logger.info("Found {} matching discharge card(s)", dischargeCards.size());
        return dischargeCards;
    }
    
    /**
     * Remove duplicates by IPD_RefNo (matching old codebase logic for Table[5])
     * The old codebase removes duplicate IPD_RefNo entries from Table[5]
     * Matches the logic: if UniqueRecordsGroup.Contains(dRow["IPD_RefNo"]) then add to DuplicateRecordsGroup
     */
    private List<DischargeCardDTO> removeDuplicatesByIpdRefNo(List<Map<String, Object>> results) {
        Set<String> seenIpdRefNos = new LinkedHashSet<>();
        List<DischargeCardDTO> uniqueCards = new ArrayList<>();
        
        for (Map<String, Object> result : results) {
            // Try both lowercase and camelCase keys (PostgreSQL typically returns lowercase)
            String ipdRefNo = getStringValue(result, "ipdrefno");
            if (ipdRefNo == null || ipdRefNo.isEmpty()) {
                ipdRefNo = getStringValue(result, "ipdRefNo");
            }
            
            if (ipdRefNo != null && !ipdRefNo.isEmpty()) {
                if (!seenIpdRefNos.contains(ipdRefNo)) {
                    seenIpdRefNos.add(ipdRefNo);
                    uniqueCards.add(mapToDTO(result));
                }
                // Skip duplicates (matching old codebase behavior)
            } else {
                // Include records without IPD_RefNo
                uniqueCards.add(mapToDTO(result));
            }
        }
        
        return uniqueCards;
    }
    
    /**
     * Map database result to DTO
     */
    private DischargeCardDTO mapToDTO(Map<String, Object> result) {
        DischargeCardDTO dto = new DischargeCardDTO();
        
        dto.setSerialNumber(getIntegerValue(result, "serialnumber"));
        dto.setPatientName(getStringValue(result, "patientname"));
        dto.setIpdNo(getStringValue(result, "ipdno"));
        dto.setIpdFileNo(getStringValue(result, "ipdfileno"));
        dto.setAdmissionDate(getStringValue(result, "admissiondate"));
        dto.setDischargeDate(getStringValue(result, "dischargedate"));
        dto.setKeyword(getStringValue(result, "keyword"));
        dto.setAdvanceRs(getBigDecimalValue(result, "advancers"));
        dto.setPatientId(getStringValue(result, "patientid"));
        dto.setIpdRefNo(getStringValue(result, "ipdrefno"));
        
        return dto;
    }
    
    private String getStringValue(Map<String, Object> result, String key) {
        Object value = result.get(key);
        return value != null ? value.toString() : "";
    }
    
    private Integer getIntegerValue(Map<String, Object> result, String key) {
        Object value = result.get(key);
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }
        return 0;
    }
    
    private BigDecimal getBigDecimalValue(Map<String, Object> result, String key) {
        Object value = result.get(key);
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        return BigDecimal.ZERO;
    }
}

