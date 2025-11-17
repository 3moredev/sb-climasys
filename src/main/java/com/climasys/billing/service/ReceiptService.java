package com.climasys.billing.service;

import com.climasys.billing.dto.*;
import com.climasys.billing.repository.PatientReceiptRepository;
import com.climasys.billing.repository.PatientVisitPaymentRepository;
import com.climasys.billing.repository.PatientVisitBillingRepository;
import com.climasys.entity.PatientReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service for receipt operations
 * Implements receipt retrieval and saving operations using JPA
 */
@Service
public class ReceiptService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReceiptService.class);
    
    @Autowired
    private PatientReceiptRepository patientReceiptRepository;
    
    @Autowired
    private PatientVisitPaymentRepository patientVisitPaymentRepository;
    
    @Autowired
    private PatientVisitBillingRepository patientVisitBillingRepository;
    
    /**
     * Get patient receipt details for printing
     * Equivalent to USP_Get_PatientReceiptData_For_Receipt stored procedure
     * Uses JPA repositories with native queries
     * 
     * @param request Receipt details request with patient, clinic, doctor, visit info
     * @return ReceiptDetailsResponse containing receipt and payment details
     */
    @Transactional(readOnly = true)
    public ReceiptDetailsResponse getPatientReceiptDetails(ReceiptDetailsRequest request) {
        logger.info("Getting receipt details for patient: {}, visit: {}", 
                   request.getPatientId(), request.getPvisitno());
        
        ReceiptDetailsResponse response = new ReceiptDetailsResponse();
        
        try {
            List<ReceiptDetailsDTO> receiptDetailsList = new ArrayList<>();
            List<PaymentDetailsDTO> paymentDetailsList = new ArrayList<>();
            
            // Parse optional patient visit number
            Integer pvisitnoInt = null;
            if (request.getPvisitno() != null && !request.getPvisitno().trim().isEmpty()) {
                try {
                    pvisitnoInt = Integer.parseInt(request.getPvisitno().trim());
                } catch (NumberFormatException e) {
                    logger.error("Invalid pvisitno format: {}", request.getPvisitno());
                }
            }
            
            // Query 1: Get receipt details using JPA repository
            List<Object[]> receiptRows;
            if (pvisitnoInt != null) {
                receiptRows = patientReceiptRepository.findReceiptDetails(
                    request.getPatientId(),
                    request.getClinicId(),
                    request.getDoctorId(),
                    request.getVisitDate(),
                    request.getShiftId().shortValue(),
                    pvisitnoInt
                );
            } else {
                receiptRows = patientReceiptRepository.findReceiptDetailsWithoutVisitNo(
                    request.getPatientId(),
                    request.getClinicId(),
                    request.getDoctorId(),
                    request.getVisitDate(),
                    request.getShiftId().shortValue()
                );
            }
            
            // Process receipt details
            // Object[] indices: 0=receipt_number, 1=receipt_date, 2=receipt_type, 3=receipt_amount,
            // 4=treatment_details, 5=title, 6=title_description, 7=to_date, 8=from_date, 9=discount
            for (Object[] row : receiptRows) {
                ReceiptDetailsDTO receipt = new ReceiptDetailsDTO();
                receipt.setReceiptNumber(getString(row, 0));
                receipt.setReceiptAmount(getBigDecimal(row, 3));
                receipt.setTreatmentDetails(getString(row, 4));
                receipt.setTitle(getInteger(row, 5));
                receipt.setTitleDescription(getString(row, 6));
                receipt.setToDate(getLocalDate(row, 7));
                receipt.setFromDate(getLocalDate(row, 8));
                receipt.setDiscount(getBigDecimal(row, 9));
                
                receiptDetailsList.add(receipt);
            }
            
            // Query 2: Get payment details using JPA repository
            List<Object[]> paymentRows;
            if (pvisitnoInt != null) {
                paymentRows = patientVisitPaymentRepository.findPaymentDetails(
                    request.getPatientId(),
                    request.getClinicId(),
                    request.getDoctorId(),
                    request.getShiftId().shortValue(),
                    request.getVisitDate(),
                    pvisitnoInt
                );
            } else {
                paymentRows = patientVisitPaymentRepository.findPaymentDetailsWithoutVisitNo(
                    request.getPatientId(),
                    request.getClinicId(),
                    request.getDoctorId(),
                    request.getShiftId().shortValue(),
                    request.getVisitDate()
                );
            }
            
            // Process payment details
            // Object[] indices: 0=payment_remark, 1=payment_description
            for (Object[] row : paymentRows) {
                PaymentDetailsDTO payment = new PaymentDetailsDTO();
                payment.setPaymentRemark(getString(row, 0));
                payment.setPaymentDescription(getString(row, 1));
                
                paymentDetailsList.add(payment);
            }
            
            response.setSuccess(true);
            response.setReceiptDetails(receiptDetailsList);
            response.setPaymentDetails(paymentDetailsList);
            response.setMessage("Receipt details retrieved successfully");
            
            logger.info("Retrieved {} receipt detail(s) and {} payment detail(s)", 
                       receiptDetailsList.size(), paymentDetailsList.size());
            
        } catch (Exception e) {
            logger.error("Error getting receipt details for patient: {}", request.getPatientId(), e);
            response.setSuccess(false);
            response.setError("Failed to retrieve receipt details: " + e.getMessage());
            response.setReceiptDetails(Collections.emptyList());
            response.setPaymentDetails(Collections.emptyList());
        }
        
        return response;
    }
    
    // Helper methods to safely extract values from Object[]
    private String getString(Object[] row, int index) {
        if (row == null || index >= row.length || row[index] == null) {
            return null;
        }
        return row[index].toString();
    }
    
    private BigDecimal getBigDecimal(Object[] row, int index) {
        if (row == null || index >= row.length || row[index] == null) {
            return null;
        }
        Object value = row[index];
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            logger.warn("Could not convert {} to BigDecimal: {}", value, e.getMessage());
            return null;
        }
    }
    
    private LocalDate getLocalDate(Object[] row, int index) {
        if (row == null || index >= row.length || row[index] == null) {
            return null;
        }
        Object value = row[index];
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        } else if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).toLocalDate();
        } else if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime().toLocalDate();
        } else if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate();
        } else if (value instanceof java.util.Date) {
            return new java.sql.Date(((java.util.Date) value).getTime()).toLocalDate();
        }
        try {
            return LocalDate.parse(value.toString());
        } catch (Exception e) {
            logger.warn("Could not convert {} to LocalDate: {}", value, e.getMessage());
            return null;
        }
    }
    
    private Integer getInteger(Object[] row, int index) {
        if (row == null || index >= row.length || row[index] == null) {
            return null;
        }
        Object value = row[index];
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            logger.warn("Could not convert {} to Integer: {}", value, e.getMessage());
            return null;
        }
    }
    
    /**
     * Save patient receipt details
     * Equivalent to USP_Insert_ReceiptDetails_Receipt stored procedure
     * 
     * @param request Save receipt request with all receipt and payment details
     * @return SaveReceiptResponse with success status and receipt number
     */
    @Transactional
    public SaveReceiptResponse saveReceipt(SaveReceiptRequest request) {
        logger.info("Saving receipt for patient: {}, visit: {}, amount: {}",
                   request.getPatientId(), request.getPatientVisitNo(), request.getReceiptAmount());
        
        try {
            // 1. Check if receipt already exists for this visit
            List<Object[]> existingReceipts = patientReceiptRepository.findReceiptDetails(
                request.getPatientId(),
                request.getClinicId(),
                request.getDoctorId(),
                request.getVisitDate(),
                request.getShiftId().shortValue(),
                request.getPatientVisitNo()
            );
            
            if (!existingReceipts.isEmpty()) {
                logger.warn("Receipt already exists for patient: {}, visit: {}", 
                           request.getPatientId(), request.getPatientVisitNo());
                return new SaveReceiptResponse(false, "Receipt already exists for this visit");
            }
            
            // 2. Generate receipt number if not provided
            String receiptNumber = request.getReceiptNumber();
            if (receiptNumber == null || receiptNumber.trim().isEmpty()) {
                receiptNumber = generateReceiptNumber(request.getClinicId());
                logger.info("Generated receipt number: {}", receiptNumber);
            }
            
            // 3. Use provided title or default to 1 (Mr.)
            Integer title = request.getTitle();
            if (title == null) {
                title = 1; // Default to 1 (Mr.)
                logger.info("Title not provided, using default: {}", title);
            }
            
            // 4. Create and save PatientReceipt entity
            // Auto-populate fromDate and toDate with current date if not provided
            LocalDate fromDate = request.getFromDate() != null ? request.getFromDate() : LocalDate.now();
            LocalDate toDate = request.getToDate() != null ? request.getToDate() : LocalDate.now();
            
            PatientReceipt receipt = new PatientReceipt();
            receipt.setDoctorId(request.getDoctorId());
            receipt.setClinicId(request.getClinicId());
            receipt.setPatientId(request.getPatientId());
            receipt.setReceiptNumber(receiptNumber);
            receipt.setReceiptDate(LocalDateTime.now());
            receipt.setReceiptType("R");  // R for Receipt (as per original SP)
            receipt.setReceiptAmount(request.getReceiptAmount());
            receipt.setShiftId(request.getShiftId().shortValue());
            receipt.setTreatmentDetails(request.getTreatmentDetails());
            receipt.setTitle(title.shortValue());
            receipt.setFromDate(fromDate.atStartOfDay());
            receipt.setToDate(toDate.atStartOfDay());
            receipt.setVisitType(request.getVisitType());
            receipt.setPatientVisitNo(request.getPatientVisitNo());
            receipt.setCreatedOn(LocalDateTime.now());
            receipt.setCreatedbyName(request.getUserName() != null ? request.getUserName() : request.getUserId());
            receipt.setModifiedOn(LocalDateTime.now());
            receipt.setModifiedbyName(request.getUserName() != null ? request.getUserName() : request.getUserId());
            
            try {
                patientReceiptRepository.save(receipt);
                logger.info("Saved receipt to patient_receipts table: {}", receiptNumber);
            } catch (Exception e) {
                logger.error("Error saving to patient_receipts table", e);
                throw new RuntimeException("Failed to save receipt: " + e.getMessage(), e);
            }
            
            // 5. Update PatientVisit with payment details
            Short paymentById = request.getPaymentById() != null ? 
                               request.getPaymentById().shortValue() : null;
            
            BigDecimal feesCollected = request.getFeesCollected() != null ? 
                                      request.getFeesCollected() : request.getReceiptAmount();
            
            int updatedRows;
            try {
                updatedRows = patientVisitBillingRepository.updatePaymentDetails(
                    request.getPatientId(),
                    request.getClinicId(),
                    request.getDoctorId(),
                    request.getShiftId().shortValue(),
                    request.getVisitDate(),
                    request.getPatientVisitNo(),
                    paymentById,
                    request.getPaymentRemark(),
                    feesCollected,
                    receiptNumber,
                    request.getUserName() != null ? request.getUserName() : request.getUserId()
                );
                
                if (updatedRows > 0) {
                    logger.info("Updated patient_visits with payment details for receipt: {}", receiptNumber);
                } else {
                    logger.warn("No patient_visits record updated for receipt: {}", receiptNumber);
                }
            } catch (Exception e) {
                logger.error("Error updating patient_visits table", e);
                throw new RuntimeException("Failed to update patient visit: " + e.getMessage(), e);
            }
            
            // 6. Return success response
            SaveReceiptResponse response = new SaveReceiptResponse();
            response.setSuccess(true);
            response.setMessage("Receipt saved successfully");
            response.setReceiptNumber(receiptNumber);
            response.setReceiptDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            logger.info("Successfully saved receipt: {} for patient: {}", receiptNumber, request.getPatientId());
            return response;
            
        } catch (Exception e) {
            logger.error("Error saving receipt for patient: {} - Exception: {}", 
                        request.getPatientId(), e.getClass().getSimpleName(), e);
            
            // Mark transaction for rollback
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            
            SaveReceiptResponse response = new SaveReceiptResponse();
            response.setSuccess(false);
            
            // Provide detailed error message
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = e.getClass().getSimpleName();
            }
            
            // Check for common database constraint violations
            if (e.getMessage() != null) {
                if (e.getMessage().contains("duplicate key") || e.getMessage().contains("unique constraint")) {
                    errorMessage = "Receipt already exists with this number";
                } else if (e.getMessage().contains("foreign key") || e.getMessage().contains("violates")) {
                    errorMessage = "Invalid reference data. Please check patient, clinic, doctor, and shift IDs";
                } else if (e.getMessage().contains("null value") || e.getMessage().contains("not-null")) {
                    errorMessage = "Missing required field: " + e.getMessage();
                }
            }
            
            response.setError("Failed to save receipt: " + errorMessage);
            return response;
        }
    }
    
    /**
     * Generate receipt number based on clinic sequence
     * Format: YYYY-#####
     * 
     * @param clinicId Clinic ID
     * @return Generated receipt number
     */
    private String generateReceiptNumber(String clinicId) {
        // Financial year calculation: if month >= April (4), use next year
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        int financialYear = (currentMonth >= 4) ? currentYear + 1 : currentYear;
        
        // For simplicity, using timestamp-based sequence
        // In production, this should query sequence_nos_clinic table
        long sequence = System.currentTimeMillis() % 100000;
        String paddedSequence = String.format("%05d", sequence);
        
        return financialYear + "-" + paddedSequence;
    }
}
