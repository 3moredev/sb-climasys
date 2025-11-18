package com.climasys.advance.service;

import com.climasys.advance.dto.AdvanceCollectionDTO;
import com.climasys.advance.dto.AdvanceCollectionRequest;
import com.climasys.advance.dto.AdvanceCollectionSearchResult;
import com.climasys.advance.dto.AdvanceCollectionSearchResultDTO;
import com.climasys.advance.dto.AdvanceDetail;
import com.climasys.advance.repository.AdvanceCollectionRepository;
import com.climasys.entity.AdvanceCollectionDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for advance collection operations
 */
@Service
@Transactional
public class AdvanceCollectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdvanceCollectionService.class);
    
    @Autowired
    private AdvanceCollectionRepository advanceCollectionRepository;
    
    /**
     * Get advance details for a patient's IPD
     * Replicates USP_GET_AdvanceDetails
     */
    @Transactional(readOnly = true)
    public List<AdvanceCollectionDTO> getAdvanceDetails(String patientId, String clinicId, String ipdRefNo) {
        logger.info("Getting advance details for patient: {}, IPD: {}", patientId, ipdRefNo);
        
        List<AdvanceDetail> advanceDetails = advanceCollectionRepository
                .findAdvanceDetails(patientId, clinicId, ipdRefNo);
        
        List<AdvanceCollectionDTO> advances = advanceDetails.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        logger.info("Retrieved {} advance record(s)", advances.size());
        return advances;
    }
    
    /**
     * Search patients with advance cards (autocomplete)
     * Replicates USP_Search_Patient_With_AdvanceCard
     */
    @Transactional(readOnly = true)
    public List<AdvanceCollectionSearchResultDTO> searchPatientsWithAdvanceCard(String searchStr, String doctorId) {
        logger.info("Searching patients with advance card: '{}', doctor: {}", searchStr, doctorId);
        
        List<AdvanceCollectionSearchResult> searchResults = advanceCollectionRepository
                .searchPatientsWithAdvanceCard(searchStr, doctorId);
        
        List<AdvanceCollectionSearchResultDTO> results = searchResults.stream()
                .map(this::convertToSearchDTO)
                .collect(Collectors.toList());
        
        logger.info("Found {} matching patient(s)", results.size());
        return results;
    }
    
    /**
     * Insert or update advance collection
     * Replicates USP_Insert_AdvanceCollection
     */
    public Map<String, Object> saveAdvanceCollection(AdvanceCollectionRequest request) {
        logger.info("Saving advance collection for patient: {}, IPD: {}", 
                    request.getPatientId(), request.getIpdRefNo());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if record exists
            boolean exists = advanceCollectionRepository.existsByCompositeKey(
                request.getPatientId(),
                request.getClinicId(),
                request.getIpdRefNo(),
                request.getDate()
            );
            
            if (!exists) {
                // Insert new record using entity
                AdvanceCollectionDetail detail = new AdvanceCollectionDetail();
                detail.setPatientId(request.getPatientId());
                detail.setDoctorId(request.getDoctorId());
                detail.setClinicId(request.getClinicId());
                detail.setIpdRefno(request.getIpdRefNo());
                detail.setDate(request.getDate());
                detail.setAmountReceived(request.getAmountReceived());
                detail.setPaymentById(request.getPaymentById());
                detail.setPaymentRemark(request.getPaymentRemark());
                detail.setShiftId(request.getShiftId());
                detail.setCreatedbyName(request.getLoginId());
                detail.setCreatedOn(java.time.LocalDateTime.now());
                detail.setAdvanceDate(request.getAdvanceDate());
                
                advanceCollectionRepository.save(detail);
                
                response.put("saveStatus", 1);
                response.put("message", "Advance collection saved successfully");
                response.put("ipdRefNo", request.getIpdRefNo());
                logger.info("Inserted new advance collection");
                
            } else {
                // Update using custom query
                advanceCollectionRepository.updateAdvanceCollection(
                    request.getPatientId(),
                    request.getClinicId(),
                    request.getIpdRefNo(),
                    request.getDate(),
                    request.getAmountReceived(),
                    request.getPaymentById(),
                    request.getPaymentRemark(),
                    request.getLoginId(),
                    request.getAdvanceDate()
                );
                
                response.put("saveStatus", 2);
                response.put("message", "Advance collection updated successfully");
                logger.info("Updated existing advance collection");
            }
            
            response.put("success", true);
            
        } catch (Exception e) {
            logger.error("Error saving advance collection", e);
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Convert AdvanceDetail projection to DTO
     */
    private AdvanceCollectionDTO convertToDTO(AdvanceDetail advanceDetail) {
        return new AdvanceCollectionDTO(
            advanceDetail.getAdvanceDate(),
            advanceDetail.getAdvance()
        );
    }
    
    /**
     * Convert AdvanceCollectionSearchResult projection to DTO
     * Maps all fields from the query result to the DTO
     */
    private AdvanceCollectionSearchResultDTO convertToSearchDTO(AdvanceCollectionSearchResult searchResult) {
        AdvanceCollectionSearchResultDTO dto = new AdvanceCollectionSearchResultDTO();
        
        // Map all fields directly
        dto.setSr(searchResult.getSerialNumber());
        dto.setPatientName(searchResult.getPatientName());
        dto.setAdmissionIpdNo(searchResult.getIpdRefNo());
        dto.setAdmissionDate(searchResult.getAdmissionDate());
        dto.setReasonOfAdmission(searchResult.getReasonOfAdmission());
        dto.setInsurance(searchResult.getInsurance());
        dto.setDateOfAdvance(searchResult.getDateOfAdvance());
        dto.setReceiptNo(searchResult.getReceiptNo());
        dto.setAdvance(searchResult.getAdvanceRs());
        dto.setPatientId(searchResult.getPatientId());
        dto.setClinicId(searchResult.getClinicId());
        dto.setDoctorId(searchResult.getDoctorId());
        
        // Legacy fields for backward compatibility
        dto.setIpdRefNo(searchResult.getIpdRefNo());
        
        logger.debug("Mapped search result - IPD: {}, Patient: {}, Name: {}, Advance: {}", 
                dto.getAdmissionIpdNo(), dto.getPatientId(), dto.getPatientName(), dto.getAdvance());
        
        return dto;
    }
}

