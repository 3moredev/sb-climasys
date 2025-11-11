package com.climasys.admission.service;

import com.climasys.admission.dto.AdmissionCardDTO;
import com.climasys.admission.repository.AdmissionCardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for admission card operations
 * Handles business logic for patient admission information
 */
@Service
@Transactional
public class AdmissionCardService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdmissionCardService.class);
    
    @Autowired
    private AdmissionCardRepository admissionCardRepository;
    
    /**
     * Get all admission cards (list of admitted patients)
     * 
     * @param patientId Patient ID (optional, can be null)
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return List of admission cards
     */
    @Transactional(readOnly = true)
    public List<AdmissionCardDTO> getAllAdmissionCards(String patientId, String doctorId, String clinicId) {
        logger.info("Getting admission cards for doctor: {}, clinic: {}, patient: {}", 
                    doctorId, clinicId, patientId != null ? patientId : "ALL");
        
        List<Map<String, Object>> results = admissionCardRepository
                .findAllAdmissionCards(patientId, doctorId, clinicId);
        
        List<AdmissionCardDTO> admissionCards = results.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        logger.info("Retrieved {} admission card(s)", admissionCards.size());
        return admissionCards;
    }
    
    /**
     * Search admission cards by patient ID, name, or contact
     * 
     * @param searchStr Search string
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return List of matching admission cards
     */
    @Transactional(readOnly = true)
    public List<AdmissionCardDTO> searchAdmissionCards(String searchStr, String doctorId, String clinicId) {
        logger.info("Searching admission cards for: '{}', doctor: {}, clinic: {}", 
                    searchStr, doctorId, clinicId);
        
        List<Map<String, Object>> results = admissionCardRepository
                .searchAdmissionCards(searchStr, doctorId, clinicId);
        
        List<AdmissionCardDTO> admissionCards = results.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        logger.info("Found {} matching admission card(s)", admissionCards.size());
        return admissionCards;
    }
    
    /**
     * Map database result to DTO
     */
    private AdmissionCardDTO mapToDTO(Map<String, Object> result) {
        AdmissionCardDTO dto = new AdmissionCardDTO();
        
        dto.setSerialNumber(getIntegerValue(result, "serialnumber"));
        dto.setPatientName(getStringValue(result, "patientname"));
        dto.setAdmissionIpdNo(getStringValue(result, "admissionipdno"));
        dto.setIpdFileNo(getStringValue(result, "ipdfileno"));
        dto.setAdmissionDate(getStringValue(result, "admissiondate"));
        dto.setReasonOfAdmission(getStringValue(result, "reasonofadmission"));
        dto.setDischargeDate(getStringValue(result, "dischargedate"));
        dto.setInsurance(getStringValue(result, "insurance"));
        dto.setCompany(getStringValue(result, "company"));
        dto.setAdvanceRs(getBigDecimalValue(result, "advancers"));
        dto.setPatientId(getStringValue(result, "patientid"));
        
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

