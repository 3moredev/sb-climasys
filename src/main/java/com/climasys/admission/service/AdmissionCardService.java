package com.climasys.admission.service;

import com.climasys.admission.dto.AdmissionCard;
import com.climasys.admission.dto.AdmissionCardDTO;
import com.climasys.admission.dto.AdmissionCardRequest;
import com.climasys.admission.repository.AdmissionCardRepository;
import com.climasys.entity.AdmissionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * Get all admission cards (list of admitted patients)
     * Matches Table[5] from USP_Get_Patient_All_Discharge_Cards stored procedure
     * Includes duplicate removal by IPD_RefNo (matching old codebase behavior)
     * 
     * @param patientId Patient ID (optional, can be null - not used in stored procedure for Table[5])
     * @param doctorId Doctor ID (optional, can be null - not used in stored procedure WHERE clause for Table[5])
     * @param clinicId Clinic ID (not used in stored procedure WHERE clause for Table[5])
     * @return List of admission cards with duplicates removed by IPD_RefNo
     */
    @Transactional(readOnly = true)
    public List<AdmissionCardDTO> getAllAdmissionCards(String patientId, String doctorId, String clinicId) {
        logger.info("Getting admission cards for doctor: {}, clinic: {}, patient: {}", 
                    doctorId != null ? doctorId : "ALL", clinicId, patientId != null ? patientId : "ALL");
        
        List<Map<String, Object>> results = admissionCardRepository
                .findAllAdmissionCards(patientId, doctorId, clinicId);
        
        // Apply duplicate removal logic by IPD_RefNo (matching Table[5] behavior from climasys2.0)
        List<AdmissionCardDTO> admissionCards = removeDuplicatesByIpdRefNo(results);
        
        logger.info("Retrieved {} admission card(s) after duplicate removal", admissionCards.size());
        return admissionCards;
    }
    
    /**
     * Remove duplicates by IPD_RefNo (matching old codebase logic for Table[5])
     * The old codebase removes duplicate IPD_RefNo entries from Table[5]
     * Matches the logic: if UniqueRecordsGroup.Contains(dRow["IPD_RefNo"]) then add to DuplicateRecordsGroup
     */
    private List<AdmissionCardDTO> removeDuplicatesByIpdRefNo(List<Map<String, Object>> results) {
        Set<String> seenIpdRefNos = new LinkedHashSet<>();
        List<AdmissionCardDTO> uniqueCards = new ArrayList<>();
        
        for (Map<String, Object> result : results) {
            // Try both lowercase and camelCase keys (PostgreSQL typically returns lowercase)
            String ipdRefNo = getStringValue(result, "admissionipdno");
            if (ipdRefNo == null || ipdRefNo.isEmpty()) {
                ipdRefNo = getStringValue(result, "admissionIpdNo");
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
     * Search admission cards by patient ID, name, or contact
     * 
     * @param searchStr Search string
     * @param doctorId Doctor ID (optional, can be null - if null, searches all doctors for the clinic)
     * @param clinicId Clinic ID
     * @return List of matching admission cards
     */
    @Transactional(readOnly = true)
    public List<AdmissionCardDTO> searchAdmissionCards(String searchStr, String doctorId, String clinicId) {
        logger.info("Searching admission cards for: '{}', doctor: {}, clinic: {}", 
                    searchStr, doctorId != null ? doctorId : "ALL", clinicId);
        
        List<Map<String, Object>> results = admissionCardRepository
                .searchAdmissionCards(searchStr, doctorId, clinicId);
        
        List<AdmissionCardDTO> admissionCards = results.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        logger.info("Found {} matching admission card(s)", admissionCards.size());
        return admissionCards;
    }
    
    /**
     * Get admission data by patient ID
     * Returns all admission records for a specific patient from admission_data table
     * 
     * @param patientId Patient ID
     * @return List of admission data records
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAdmissionDataByPatientId(String patientId) {
        logger.info("Getting admission data for patient: {}", patientId);
        
        List<Map<String, Object>> results = admissionCardRepository
                .findByPatientId(patientId);
        
        logger.info("Retrieved {} admission record(s) for patient: {}", results.size(), patientId);
        return results;
    }
    
    /**
     * Insert or update admission card
     * Replicates USP_Insert_AdmissionCard
     */
    public Map<String, Object> saveAdmissionCard(AdmissionCardRequest request) {
        logger.info("Saving admission card for patient: {}, IPD: {}", 
                    request.getPatientId(), request.getIpdRefNo());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String ipdRefNo = request.getIpdRefNo();
            
            // Check if admission already exists (only if ipdRefNo is provided)
            boolean exists = false;
            if (ipdRefNo != null && !ipdRefNo.trim().isEmpty()) {
                exists = admissionCardRepository.existsByCompositeKey(
                    request.getPatientId(),
                    request.getClinicId(),
                    ipdRefNo
                );
            }
            
            if (!exists) {
                // Generate IPD Reference Number if not provided
                if (ipdRefNo == null || ipdRefNo.trim().isEmpty()) {
                    ipdRefNo = generateIpdRefNo(request.getClinicId());
                    if (ipdRefNo == null) {
                        throw new RuntimeException("Failed to generate IPD Reference Number");
                    }
                    logger.info("Generated IPD Reference Number: {}", ipdRefNo);
                }
                
                // Insert new admission
                AdmissionData admission = new AdmissionData();
                admission.setPatientId(request.getPatientId());
                admission.setDoctorId(request.getDoctorId());
                admission.setClinicId(request.getClinicId());
                admission.setIpdRefno(ipdRefNo);
                admission.setRelativeName(request.getRelativeName());
                admission.setRelation(request.getRelation());
                admission.setContactNo(request.getContactNo());
                admission.setAdmissionDate(request.getAdmissionDate());
                admission.setAdmissionTime(request.getAdmissionTime());
                admission.setReasonOfAdmission(request.getReasonOfAdmission());
                admission.setDepartment(request.getDepartment());
                admission.setIsInsurance(request.getIsInsurance() != null ? request.getIsInsurance() : false);
                admission.setInsuranceDetails(request.getInsuranceDetails());
                admission.setTreatingDoctor(request.getTreatingDoctor());
                admission.setConsultantDoctor(request.getConsultingDoctor());
                admission.setIpdFileNo(request.getIpdFileNo());
                admission.setRoomNo(request.getRoomNo());
                admission.setBedNo(request.getBedNo());
                admission.setPackageRemarks(request.getPackageRemarks());
                admission.setShiftId(request.getShiftId());
                admission.setCreatedByName(request.getLoginId());
                admission.setCreatedOn(java.time.LocalDate.now());
                admission.setReferredDoctor(request.getReferredDoctor());
                admission.setCommentsNote(request.getCommentsNote());
                admission.setInsuranceCompanyId(request.getInsuranceCompanyId());
                
                AdmissionData saved = admissionCardRepository.save(admission);
                
                // Insert into discharge_data table (as per USP_Insert_AdmissionCard)
                insertDischargeData(request, ipdRefNo);
                
                response.put("saveStatus", 1);
                response.put("message", "Admission card saved successfully");
                response.put("ipdRefNo", saved.getIpdRefno());
                logger.info("Inserted new admission card with IPD: {}", saved.getIpdRefno());
                
            } else {
                // Update existing admission
                AdmissionData admission = admissionCardRepository.findById(
                    new com.climasys.entity.AdmissionDataId(
                        request.getPatientId(),
                        request.getDoctorId(),
                        request.getClinicId(),
                        ipdRefNo
                    )
                ).orElseThrow(() -> new RuntimeException("Admission not found"));
                
                admission.setRelativeName(request.getRelativeName());
                admission.setRelation(request.getRelation());
                admission.setContactNo(request.getContactNo());
                admission.setAdmissionDate(request.getAdmissionDate());
                admission.setAdmissionTime(request.getAdmissionTime());
                admission.setReasonOfAdmission(request.getReasonOfAdmission());
                admission.setDepartment(request.getDepartment());
                admission.setIsInsurance(request.getIsInsurance());
                admission.setInsuranceDetails(request.getInsuranceDetails());
                admission.setTreatingDoctor(request.getTreatingDoctor());
                admission.setConsultantDoctor(request.getConsultingDoctor());
                admission.setIpdFileNo(request.getIpdFileNo());
                admission.setRoomNo(request.getRoomNo());
                admission.setBedNo(request.getBedNo());
                admission.setPackageRemarks(request.getPackageRemarks());
                admission.setModifiedByName(request.getLoginId());
                admission.setModifiedOn(java.time.LocalDate.now());
                admission.setReferredDoctor(request.getReferredDoctor());
                admission.setCommentsNote(request.getCommentsNote());
                admission.setInsuranceCompanyId(request.getInsuranceCompanyId());
                
                admissionCardRepository.save(admission);
                
                // Update discharge_data table (as per USP_Insert_AdmissionCard)
                updateDischargeData(request, ipdRefNo);
                
                response.put("saveStatus", 2);
                response.put("message", "Admission card updated successfully");
                logger.info("Updated admission card with IPD: {}", ipdRefNo);
            }
            
            response.put("success", true);
            
        } catch (Exception e) {
            logger.error("Error saving admission card", e);
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Generate IPD Reference Number
     * Replicates the logic from USP_Insert_AdmissionCard stored procedure
     * Format: {PrefixChar}-{FinancialYear}-{Month}-{ZeroPaddedSequenceNumber}
     * 
     * @param clinicId Clinic ID
     * @return Generated IPD Reference Number
     */
    private String generateIpdRefNo(String clinicId) {
        try {
            // Get sequence number for IPD entity type
            String sequenceSql = "SELECT last_sequenceno, prefix_char, total_length " +
                    "FROM sequence_nos WHERE clinic_id = ? AND entity_type = 'IPD'";
            
            List<Map<String, Object>> sequenceResult = jdbcTemplate.queryForList(sequenceSql, clinicId);
            
            if (sequenceResult.isEmpty()) {
                // Create default sequence entry if not exists
                String insertSequenceSql = "INSERT INTO sequence_nos " +
                        "(doctor_id, entity_type, entity_name, prefix_char, total_length, last_sequenceno, clinic_id) " +
                        "VALUES (?, 'IPD', 'IPD', '', 5, 0, ?)";
                jdbcTemplate.update(insertSequenceSql, "DEFAULT", clinicId);
                
                // Retry getting sequence
                sequenceResult = jdbcTemplate.queryForList(sequenceSql, clinicId);
                if (sequenceResult.isEmpty()) {
                    logger.error("Failed to create or retrieve IPD sequence for clinic: {}", clinicId);
                    return null;
                }
            }
            
            Map<String, Object> sequenceData = sequenceResult.get(0);
            Long lastSequenceNo = ((Number) sequenceData.get("last_sequenceno")).longValue();
            Integer totalLength = ((Number) sequenceData.get("total_length")).intValue();
            String prefixChar = (String) sequenceData.get("prefix_char");
            if (prefixChar == null) {
                prefixChar = "";
            }
            
            // Increment sequence number (reset to 1 if it's 9999)
            if (lastSequenceNo == 9999) {
                lastSequenceNo = 1L;
            } else {
                lastSequenceNo = lastSequenceNo + 1;
            }
            
            // Calculate financial year: if month >= April, use current year + 1, else current year
            LocalDate today = LocalDate.now();
            int currentMonth = today.getMonthValue();
            int financialYear = today.getYear();
            if (currentMonth >= 4) {
                financialYear = financialYear + 1;
            }
            
            // Get month as 2-digit string
            String monthStr = String.format("%02d", currentMonth);
            
            // Pad sequence number with zeros
            String paddedSequence = String.format("%0" + totalLength + "d", lastSequenceNo);
            
            // Generate IPD Reference Number: {PrefixChar}-{FinancialYear}-{Month}-{ZeroPaddedSequence}
            String ipdRefNo = prefixChar + "-" + financialYear + "-" + monthStr + "-" + paddedSequence;
            
            // Update sequence number
            String updateSequenceSql = "UPDATE sequence_nos SET last_sequenceno = ? " +
                    "WHERE clinic_id = ? AND entity_type = 'IPD'";
            jdbcTemplate.update(updateSequenceSql, lastSequenceNo, clinicId);
            
            logger.info("Generated IPD Reference Number: {} (sequence: {}, financial year: {}, month: {})", 
                    ipdRefNo, lastSequenceNo, financialYear, monthStr);
            
            return ipdRefNo;
        } catch (Exception e) {
            logger.error("Error generating IPD Reference Number for clinic: {}", clinicId, e);
            return null;
        }
    }
    
    /**
     * Insert discharge data record
     * Replicates the INSERT INTO discharge_data logic from USP_Insert_AdmissionCard
     */
    private void insertDischargeData(AdmissionCardRequest request, String ipdRefNo) {
        try {
            String insertSql = "INSERT INTO discharge_data " +
                    "(doctor_id, clinic_id, patient_id, ipd_refno, admission_date, admission_time, " +
                    "treating_doctor, consulting_doctor, ipd_no, createdby_name, created_on, bedno, room, referred_doctor, visit_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?)";
            
            jdbcTemplate.update(insertSql,
                    request.getDoctorId(),
                    request.getClinicId(),
                    request.getPatientId(),
                    ipdRefNo,
                    request.getAdmissionDate(),
                    request.getAdmissionTime(),
                    request.getTreatingDoctor(),
                    request.getConsultingDoctor(),
                    request.getIpdFileNo(),
                    request.getLoginId(),
                    request.getBedNo(),
                    request.getRoomNo(),
                    request.getReferredDoctor(),
                    request.getAdmissionDate()
            );
            
            logger.info("Inserted discharge_data record for IPD: {}", ipdRefNo);
        } catch (Exception e) {
            logger.error("Error inserting discharge_data for IPD: {}", ipdRefNo, e);
            // Don't throw exception - admission_data is already saved
        }
    }
    
    /**
     * Update discharge data record
     * Replicates the UPDATE discharge_data logic from USP_Insert_AdmissionCard
     */
    private void updateDischargeData(AdmissionCardRequest request, String ipdRefNo) {
        try {
            String updateSql = "UPDATE discharge_data SET " +
                    "ipd_no = ?, " +
                    "admission_date = ?, " +
                    "admission_time = ?, " +
                    "treating_doctor = ?, " +
                    "consulting_doctor = ?, " +
                    "bedno = ?, " +
                    "room = ?, " +
                    "modified_on = CURRENT_TIMESTAMP, " +
                    "modifiedby_name = ?, " +
                    "referred_doctor = ? " +
                    "WHERE patient_id = ? AND clinic_id = ? AND ipd_refno = ?";
            
            int updated = jdbcTemplate.update(updateSql,
                    request.getIpdFileNo(),
                    request.getAdmissionDate(),
                    request.getAdmissionTime(),
                    request.getTreatingDoctor(),
                    request.getConsultingDoctor(),
                    request.getBedNo(),
                    request.getRoomNo(),
                    request.getLoginId(),
                    request.getReferredDoctor(),
                    request.getPatientId(),
                    request.getClinicId(),
                    ipdRefNo
            );
            
            logger.info("Updated {} discharge_data record(s) for IPD: {}", updated, ipdRefNo);
        } catch (Exception e) {
            logger.error("Error updating discharge_data for IPD: {}", ipdRefNo, e);
            // Don't throw exception - admission_data is already updated
        }
    }
    
    /**
     * Convert AdmissionCard projection to DTO
     */
    private AdmissionCardDTO convertToDTO(AdmissionCard admissionCard) {
        AdmissionCardDTO dto = new AdmissionCardDTO();
        dto.setSerialNumber(admissionCard.getSerialNumber());
        dto.setPatientName(admissionCard.getPatientName());
        dto.setAdmissionIpdNo(admissionCard.getAdmissionIpdNo());
        dto.setIpdFileNo(admissionCard.getIpdFileNo());
        dto.setAdmissionDate(admissionCard.getAdmissionDate());
        dto.setReasonOfAdmission(admissionCard.getReasonOfAdmission());
        dto.setDischargeDate(admissionCard.getDischargeDate());
        dto.setInsurance(admissionCard.getInsurance());
        dto.setCompany(admissionCard.getCompany());
        dto.setAdvanceRs(admissionCard.getAdvanceRs());
        dto.setDateOfAdvance(""); // Not available in AdmissionCard projection
        dto.setReceiptNo(""); // Not available in AdmissionCard projection
        dto.setPatientId(admissionCard.getPatientId());
        return dto;
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
        dto.setDateOfAdvance(getStringValue(result, "dateofadvance"));
        dto.setReceiptNo(getStringValue(result, "receiptno"));
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

