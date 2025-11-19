package com.climasys.advance.service;

import com.climasys.advance.dto.*;
import com.climasys.advance.dto.PreviousAdvanceCollectionDTO;
import com.climasys.advance.repository.AdvanceCollectionRepository;
import com.climasys.advance.repository.PatientIpdReceiptRepository;
import com.climasys.entity.AdvanceCollectionDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    
    @Autowired
    private PatientIpdReceiptRepository patientIpdReceiptRepository;
    
    /**
     * Get advance details for a patient's IPD
     * Replicates USP_GET_AdvanceDetails
     * Returns comprehensive data for "Previous Advance Collection Records" table
     */
    @Transactional(readOnly = true)
    public List<PreviousAdvanceCollectionDTO> getAdvanceDetails(String patientId, String clinicId, String ipdRefNo) {
        logger.info("Getting advance details for patient: {}, IPD: {}", patientId, ipdRefNo);
        
        List<AdvanceDetail> advanceDetails = advanceCollectionRepository
                .findComprehensiveAdvanceDetails(patientId, clinicId, ipdRefNo);
        
        List<PreviousAdvanceCollectionDTO> advances = advanceDetails.stream()
                .map(this::convertAdvanceDetailToDTO)
                .collect(Collectors.toList());
        
        logger.info("Retrieved {} advance record(s)", advances.size());
        return advances;
    }
    
    /**
     * Convert AdvanceDetail interface projection to PreviousAdvanceCollectionDTO
     */
    private PreviousAdvanceCollectionDTO convertAdvanceDetailToDTO(AdvanceDetail advanceDetail) {
        PreviousAdvanceCollectionDTO dto = new PreviousAdvanceCollectionDTO();
        dto.setAdmissionIpdNo(advanceDetail.getAdmissionIpdNo());
        dto.setAdmissionDate(advanceDetail.getAdmissionDate());
        dto.setDischargeDate(advanceDetail.getDischargeDate());
        dto.setReasonOfAdmission(advanceDetail.getReasonOfAdmission());
        dto.setInsurance(advanceDetail.getInsurance());
        dto.setAdvanceDate(advanceDetail.getAdvanceDate());
        dto.setReceiptNo(advanceDetail.getReceiptNo());
        dto.setAmount(advanceDetail.getAmount());
        return dto;
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
            // Convert LocalDate to LocalDateTime at start of day for database operations
            LocalDateTime dateTime = request.getDate() != null ? request.getDate().atStartOfDay() : null;
            LocalDateTime advanceDateTime = request.getAdvanceDate() != null ? request.getAdvanceDate().atStartOfDay() : null;
            
            // Check if record exists
            boolean exists = advanceCollectionRepository.existsByCompositeKey(
                request.getPatientId(),
                request.getClinicId(),
                request.getIpdRefNo(),
                dateTime
            );
            
            if (!exists) {
                // Insert new record using entity
                AdvanceCollectionDetail detail = new AdvanceCollectionDetail();
                detail.setPatientId(request.getPatientId());
                detail.setDoctorId(request.getDoctorId());
                detail.setClinicId(request.getClinicId());
                detail.setIpdRefno(request.getIpdRefNo());
                detail.setDate(dateTime);
                detail.setAmountReceived(request.getAmountReceived());
                detail.setPaymentById(request.getPaymentById());
                detail.setPaymentRemark(request.getPaymentRemark());
                detail.setShiftId(request.getShiftId());
                detail.setCreatedbyName(request.getLoginId());
                detail.setCreatedOn(java.time.LocalDateTime.now());
                detail.setAdvanceDate(advanceDateTime);
                
                advanceCollectionRepository.save(detail);
                
                response.put("saveStatus", 1);
                response.put("message", "Advance collection saved successfully");
                response.put("ipdRefNo", request.getIpdRefNo());
                logger.info("Inserted new advance collection");
                
            } else {
                // Update using custom query
                int updatedRows = advanceCollectionRepository.updateAdvanceCollection(
                    request.getPatientId(),
                    request.getClinicId(),
                    request.getIpdRefNo(),
                    dateTime,
                    request.getAmountReceived(),
                    request.getPaymentById(),
                    request.getPaymentRemark(),
                    request.getLoginId(),
                    advanceDateTime
                );
                
                if (updatedRows == 0) {
                    logger.warn("No rows updated for advance collection. Patient: {}, IPD: {}, Date: {}", 
                               request.getPatientId(), request.getIpdRefNo(), dateTime);
                    response.put("saveStatus", 0);
                    response.put("message", "No records found to update. The record may have been deleted or the date doesn't match.");
                    response.put("success", false);
                    return response;
                }
                
                response.put("saveStatus", 2);
                response.put("message", "Advance collection updated successfully");
                logger.info("Updated {} existing advance collection record(s)", updatedRows);
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
     * Convert AdvanceDetail projection to DTO (basic version - for backward compatibility)
     * Note: This is used by the basic findAdvanceDetails() method
     */
    private AdvanceCollectionDTO convertToDTO(AdvanceDetail advanceDetail) {
        return new AdvanceCollectionDTO(
            advanceDetail.getAdvanceDate(),
            advanceDetail.getAmount()
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
    
    /**
     * Get admission card data for Advance Collection screen
     * Replicates USP_Get_Patient_AdmissionCard_data
     * Returns 4 tables: Previous advance records, Current advance details, Admission data, Total advance
     */
    @Transactional(readOnly = true)
    public AdmissionCardDataResponse getAdmissionCardData(
            String patientId, String clinicId, String doctorId, String ipdRefNo, LocalDateTime ipdDate) {
        logger.info("Getting admission card data for patient: {}, IPD: {}", patientId, ipdRefNo);
        
        AdmissionCardDataResponse response = new AdmissionCardDataResponse();
        
        // Table[0]: Previous advance collection records
        List<Map<String, Object>> previousRecords = advanceCollectionRepository
                .getPreviousAdvanceRecords(patientId, clinicId, ipdRefNo);
        response.setPreviousAdvanceRecords(previousRecords.stream()
                .map(this::mapToPreviousAdvanceRecord)
                .collect(Collectors.toList()));
        
        // Table[1]: Current advance collection details
        Map<String, Object> currentDetailsMap = advanceCollectionRepository
                .getCurrentAdvanceDetails(patientId, clinicId, ipdRefNo, ipdDate);
        if (currentDetailsMap != null && !currentDetailsMap.isEmpty()) {
            response.setCurrentAdvanceDetails(mapToCurrentAdvanceDetails(currentDetailsMap));
        }
        
        // Table[2]: Admission data
        Map<String, Object> admissionDataMap = advanceCollectionRepository
                .getAdmissionData(patientId, ipdRefNo);
        if (admissionDataMap != null && !admissionDataMap.isEmpty()) {
            response.setAdmissionData(mapToAdmissionData(admissionDataMap));
        }
        
        // Table[3]: Total advance amount
        BigDecimal totalAdvance = advanceCollectionRepository
                .getTotalAdvanceAmount(patientId, ipdRefNo);
        response.setTotalAdvance(totalAdvance);
        
        logger.info("Retrieved admission card data for patient: {}, IPD: {}", patientId, ipdRefNo);
        return response;
    }
    
    /**
     * Save advance receipt details
     * Replicates USP_Insert_AdvanceReceiptDetails
     */
    @Transactional
    public Map<String, Object> saveAdvanceReceiptDetails(ReceiptDetailsRequest request) {
        logger.info("Saving advance receipt details for patient: {}, receipt: {}", 
                    request.getPatientId(), request.getReceiptNo());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if receipt exists (GETSTATUS logic)
            boolean receiptExists = patientIpdReceiptRepository.receiptExists(
                request.getPaymentDate(),
                request.getDoctorId(),
                request.getClinicId(),
                request.getPatientId(),
                request.getShiftId(),
                request.getReceiptNo() != null ? request.getReceiptNo() : ""
            );
            
            String receiptNo = request.getReceiptNo();
            
            // Generate receipt number if not provided (sequence logic)
            if (receiptNo == null || receiptNo.trim().isEmpty()) {
                receiptNo = generateReceiptNumber(request.getDoctorId(), request.getClinicId());
                response.put("getStatus", 1);
            } else {
                response.put("getStatus", receiptExists ? 0 : 1);
            }
            
            // Upsert receipt in patient_ipd_receipts
            if (request.getReceiptData() != null && !request.getReceiptData().isEmpty()) {
                for (ReceiptDetailsRequest.ReceiptDataItem item : request.getReceiptData()) {
                    patientIpdReceiptRepository.upsertReceipt(
                        item.getDoctorId(),
                        item.getClinicId(),
                        item.getPatientId(),
                        item.getReceiptNumber(),
                        item.getReceiptDate(),
                        request.getReceiptType(),
                        item.getReceiptAmount(),
                        request.getUserId(),
                        item.getShiftId(),
                        request.getTreatmentDetails(),
                        request.getTitle() != null ? request.getTitle().shortValue() : null,
                        request.getFromDate(),
                        request.getToDate(),
                        request.getVisitType()
                    );
                }
            } else {
                // Insert single receipt
                patientIpdReceiptRepository.upsertReceipt(
                    request.getDoctorId(),
                    request.getClinicId(),
                    request.getPatientId(),
                    receiptNo,
                    request.getPaymentDate(),
                    request.getReceiptType(),
                    request.getReceiptAmount(),
                    request.getUserId(),
                    request.getShiftId(),
                    request.getTreatmentDetails(),
                    request.getTitle() != null ? request.getTitle().shortValue() : null,
                    request.getFromDate(),
                    request.getToDate(),
                    request.getVisitType()
                );
            }
            
            // Update advance_collection_details with receipt number if visit type is 'A'
            if ("A".equals(request.getVisitType())) {
                logger.info("Updating advance_collection_details with receipt number: {} for patient: {}, date: {}, paymentDate: {}, IPD: {}", 
                           receiptNo, request.getPatientId(), request.getDate(), request.getPaymentDate(), request.getIpdRefNo());
                
                // Ensure paymentDate is not null - use current date if null
                LocalDateTime receiptDate = request.getPaymentDate();
                if (receiptDate == null) {
                    receiptDate = LocalDateTime.now();
                    logger.warn("PaymentDate was null, using current date: {}", receiptDate);
                }
                
                int updatedRows = advanceCollectionRepository.updateReceiptNumber(
                    receiptNo,
                    receiptDate,
                    request.getTreatmentDetails(),
                    request.getDate(),
                    request.getDoctorId(),
                    request.getClinicId(),
                    request.getPatientId(),
                    request.getIpdRefNo()
                );
                
                if (updatedRows == 0) {
                    logger.warn("No advance_collection_details records were updated. Patient: {}, Date: {}, IPD: {}", 
                               request.getPatientId(), request.getDate(), request.getIpdRefNo());
                } else {
                    logger.info("Updated {} advance_collection_details record(s) with receipt number: {}, receipt_date: {}", 
                               updatedRows, receiptNo, receiptDate);
                }
            }
            
            response.put("success", true);
            response.put("saveStatus", 1);
            response.put("receiptNo", receiptNo);
            response.put("message", "Receipt details saved successfully");
            
            logger.info("Saved advance receipt details for patient: {}, receipt: {}", 
                       request.getPatientId(), receiptNo);
            
        } catch (Exception e) {
            logger.error("Error saving advance receipt details", e);
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get hospital bill receipt data for printing
     * Replicates USP_Get_PatientHospitalBillReceiptData
     */
    @Transactional(readOnly = true)
    public HospitalBillReceiptResponse getHospitalBillReceiptData(
            String patientId, Integer shiftId, String clinicId, String doctorId,
            LocalDate visitDate, String visitType, String billNo, String receiptNo) {
        logger.info("Getting hospital bill receipt data for patient: {}, receipt: {}", 
                    patientId, receiptNo);
        
        HospitalBillReceiptResponse response = new HospitalBillReceiptResponse();
        
        // Table[0]: Receipt details from Patient_IPD_Receipts
        Map<String, Object> receiptDetailsMap = patientIpdReceiptRepository.getReceiptDetails(
            patientId, clinicId, doctorId, receiptNo, visitType);
        if (receiptDetailsMap != null && !receiptDetailsMap.isEmpty()) {
            response.setReceiptDetails(mapToReceiptDetails(receiptDetailsMap));
        }
        
        // Table[1]: Payment details from Discharge_Bill_Hdr (if exists)
        if (billNo != null && !billNo.trim().isEmpty()) {
            boolean billPaymentExists = patientIpdReceiptRepository.billPaymentExists(
                patientId, clinicId, receiptNo, billNo);
            
            if (billPaymentExists) {
                Map<String, Object> billPaymentMap = patientIpdReceiptRepository.getBillPaymentDetails(
                    patientId, clinicId, receiptNo, billNo);
                if (billPaymentMap != null && !billPaymentMap.isEmpty()) {
                    response.setBillPaymentDetails(mapToPaymentDetails(billPaymentMap));
                }
            } else {
                // Return empty payment details
                response.setBillPaymentDetails(new HospitalBillReceiptResponse.PaymentDetails(
                    null, "", ""));
            }
        }
        
        // Table[2]: Payment details from Advance_Collection_details (if exists)
        boolean advancePaymentExists = patientIpdReceiptRepository.advancePaymentExists(
            patientId, clinicId, receiptNo);
        
        if (advancePaymentExists) {
            Map<String, Object> advancePaymentMap = patientIpdReceiptRepository.getAdvancePaymentDetails(
                patientId, clinicId, receiptNo);
            if (advancePaymentMap != null && !advancePaymentMap.isEmpty()) {
                response.setAdvancePaymentDetails(mapToPaymentDetails(advancePaymentMap));
            }
        } else {
            // Return empty payment details
            response.setAdvancePaymentDetails(new HospitalBillReceiptResponse.PaymentDetails(
                null, "", ""));
        }
        
        logger.info("Retrieved hospital bill receipt data for patient: {}, receipt: {}", 
                   patientId, receiptNo);
        return response;
    }
    
    // Helper methods for mapping
    private AdmissionCardDataResponse.PreviousAdvanceRecord mapToPreviousAdvanceRecord(Map<String, Object> map) {
        AdmissionCardDataResponse.PreviousAdvanceRecord record = 
                new AdmissionCardDataResponse.PreviousAdvanceRecord();
        record.setIpdRefNo(getStringValue(map, "ipdrefno"));
        record.setAdmissionDate(getStringValue(map, "admissiondate"));
        record.setReasonOfAdmission(getStringValue(map, "reasonofadmission"));
        record.setReceiptNumber(getStringValue(map, "receiptnumber"));
        record.setDate(getLocalDateTimeValue(map, "date"));
        record.setIsInsurance(getStringValue(map, "isinsurance"));
        record.setDateOfAdvance(getStringValue(map, "dateofadvance"));
        record.setDoctorId(getStringValue(map, "doctorid"));
        record.setAmountReceived(getBigDecimalValue(map, "amountreceived"));
        record.setDischargeDate(getStringValue(map, "dischargedate"));
        record.setSumTotal(getBigDecimalValue(map, "sumtotal"));
        record.setValidDischargeDate(getStringValue(map, "validdischargedate"));
        return record;
    }
    
    private AdmissionCardDataResponse.CurrentAdvanceDetails mapToCurrentAdvanceDetails(Map<String, Object> map) {
        AdmissionCardDataResponse.CurrentAdvanceDetails details = 
                new AdmissionCardDataResponse.CurrentAdvanceDetails();
        details.setAmountReceived(getBigDecimalValue(map, "amountreceived"));
        details.setAdvanceDate(getLocalDateTimeValue(map, "advancedate"));
        details.setPaymentById(getShortValue(map, "paymentbyid"));
        details.setPaymentRemark(getStringValue(map, "paymentremark"));
        details.setReceiptNumber(getStringValue(map, "receiptnumber"));
        details.setReceiptDate(getLocalDateValue(map, "receiptdate"));
        return details;
    }
    
    private AdmissionCardDataResponse.AdmissionData mapToAdmissionData(Map<String, Object> map) {
        AdmissionCardDataResponse.AdmissionData data = 
                new AdmissionCardDataResponse.AdmissionData();
        data.setAdmissionDate(getLocalDateValue(map, "admissiondate"));
        data.setIpdFileNo(getStringValue(map, "ipdfileno"));
        data.setDepartment(getStringValue(map, "department"));
        data.setReasonOfAdmission(getStringValue(map, "reasonofadmission"));
        data.setInsuranceDetails(getStringValue(map, "insurancedetails"));
        data.setIsInsurance(getStringValue(map, "isinsurance"));
        data.setPackageRemarks(getStringValue(map, "packageremarks"));
        data.setBillNo(getStringValue(map, "billno"));
        data.setBillDate(getLocalDateValue(map, "billdate"));
        data.setInvoiceNo(getStringValue(map, "invoiceno"));
        data.setAdmissionTime(getLocalDateTimeValue(map, "admissiontime"));
        data.setDischargeDate(getLocalDateValue(map, "dischargedate"));
        data.setDischargeTime(getLocalDateTimeValue(map, "dischargetime"));
        data.setRoomNo(getStringValue(map, "roomno"));
        data.setBedNo(getStringValue(map, "bedno"));
        return data;
    }
    
    private HospitalBillReceiptResponse.ReceiptDetails mapToReceiptDetails(Map<String, Object> map) {
        HospitalBillReceiptResponse.ReceiptDetails details = 
                new HospitalBillReceiptResponse.ReceiptDetails();
        details.setReceiptNumber(getStringValue(map, "receiptnumber"));
        details.setReceiptDate(getLocalDateValue(map, "receiptdate"));
        details.setReceiptType(getStringValue(map, "receipttype"));
        details.setReceiptAmount(getBigDecimalValue(map, "receiptamount"));
        details.setTreatmentDetails(getStringValue(map, "treatmentdetails"));
        details.setTitle(getShortValue(map, "title"));
        details.setTitleDescription(getStringValue(map, "titledescription"));
        details.setToDate(getLocalDateValue(map, "todate"));
        details.setFromDate(getLocalDateValue(map, "fromdate"));
        return details;
    }
    
    private HospitalBillReceiptResponse.PaymentDetails mapToPaymentDetails(Map<String, Object> map) {
        HospitalBillReceiptResponse.PaymentDetails details = 
                new HospitalBillReceiptResponse.PaymentDetails();
        details.setAmount(getBigDecimalValue(map, "amount"));
        details.setPaymentDescription(getStringValue(map, "paymentdescription"));
        details.setPaymentRemark(getStringValue(map, "paymentremark"));
        return details;
    }
    
    // Helper methods for extracting values from Map
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            // Try lowercase
            value = map.get(key.toLowerCase());
        }
        return value != null ? value.toString() : null;
    }
    
    private BigDecimal getBigDecimalValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            value = map.get(key.toLowerCase());
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        if (value != null) {
            try {
                return new BigDecimal(value.toString());
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }
    
    private Short getShortValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            value = map.get(key.toLowerCase());
        }
        if (value instanceof Short) {
            return (Short) value;
        }
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }
        return null;
    }
    
    private LocalDate getLocalDateValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            value = map.get(key.toLowerCase());
        }
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate();
        }
        if (value instanceof java.util.Date) {
            return new java.sql.Date(((java.util.Date) value).getTime()).toLocalDate();
        }
        return null;
    }
    
    private LocalDateTime getLocalDateTimeValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            value = map.get(key.toLowerCase());
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime();
        }
        if (value instanceof java.util.Date) {
            return new java.sql.Timestamp(((java.util.Date) value).getTime()).toLocalDateTime();
        }
        return null;
    }
    
    /**
     * Generate receipt number using sequence logic
     * Replicates the sequence generation logic from USP_Insert_AdvanceReceiptDetails
     * 
     * @param doctorId Doctor ID (required for sequence creation due to foreign key)
     * @param clinicId Clinic ID
     * @return Generated receipt number
     */
    private String generateReceiptNumber(String doctorId, String clinicId) {
        Map<String, Object> seq = advanceCollectionRepository.getSequenceForIrc(clinicId);
        
        if (seq == null || seq.isEmpty()) {
            // Create default sequence entry if not exists
            // Use try-catch to prevent transaction rollback if sequence already exists
            try {
                advanceCollectionRepository.createDefaultIrcSequence(doctorId, clinicId);
            } catch (Exception e) {
                // Sequence might already exist, try to get it again
                logger.debug("Sequence creation may have failed (possibly already exists): {}", e.getMessage());
            }
            
            // Retry getting sequence
            seq = advanceCollectionRepository.getSequenceForIrc(clinicId);
            if (seq == null || seq.isEmpty()) {
                throw new RuntimeException("Failed to create or retrieve IRC sequence for clinic: " + clinicId);
            }
        }
        
        Long lastSequenceNo = ((Number) seq.get("last_sequenceno")).longValue();
        Integer totalLength = ((Number) seq.get("total_length")).intValue();
        
        if (lastSequenceNo == 99999) {
            lastSequenceNo = 1L;
        } else {
            lastSequenceNo = lastSequenceNo + 1;
        }
        
        // Calculate financial year
        int currentMonth = LocalDate.now().getMonthValue();
        int financialYear = (currentMonth >= 4) ? LocalDate.now().getYear() + 1 : LocalDate.now().getYear();
        
        // Generate receipt number: I-{FY}-{padded_sequence}
        String paddedSequence = String.format("%0" + totalLength + "d", lastSequenceNo);
        String receiptNo = "I-" + financialYear + "-" + paddedSequence;
        
        // Update sequence
        advanceCollectionRepository.updateIrcSequence(lastSequenceNo, clinicId);
        
        logger.info("Generated receipt number: {} (sequence: {}, financial year: {})", 
                    receiptNo, lastSequenceNo, financialYear);
        
        return receiptNo;
    }
}

