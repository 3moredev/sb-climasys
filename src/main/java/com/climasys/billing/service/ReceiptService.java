package com.climasys.billing.service;

import com.climasys.billing.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service for receipt operations
 * Implements USP_Get_PatientReceiptData_For_Receipt stored procedure
 */
@Service
@Transactional(readOnly = true)
public class ReceiptService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReceiptService.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * Get patient receipt details for printing
     * Equivalent to USP_Get_PatientReceiptData_For_Receipt stored procedure
     * 
     * @param request Receipt details request with patient, clinic, doctor, visit info
     * @return ReceiptDetailsResponse containing receipt and payment details
     */
    public ReceiptDetailsResponse getPatientReceiptDetails(ReceiptDetailsRequest request) {
        logger.info("Getting receipt details for patient: {}, visit: {}", 
                   request.getPatientId(), request.getPvisitno());
        
        ReceiptDetailsResponse response = new ReceiptDetailsResponse();
        
        try {
            // Create stored procedure call
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_Get_PatientReceiptData_For_Receipt");
            
            // Prepare parameters matching the stored procedure signature
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("p_nvar_Patient_Id", request.getPatientId());
            parameters.put("p_tint_Shift_Id", request.getShiftId());
            parameters.put("p_nvar_Clinic_ID", request.getClinicId());
            parameters.put("p_nvar_Doctor_ID", request.getDoctorId());
            parameters.put("p_date_TodyasVisitDate", request.getVisitDate());
            parameters.put("p_nvar_Visit_Type", request.getVisitType());
            parameters.put("p_nvar_pvisitno", request.getPvisitno() != null ? request.getPvisitno() : "");
            
            // Execute stored procedure
            Map<String, Object> result = jdbcCall.execute(parameters);
            
            // Process results
            // The stored procedure returns multiple result sets
            // Table[0] contains receipt details, Table[1] contains payment details
            
            List<ReceiptDetailsDTO> receiptDetailsList = new ArrayList<>();
            List<PaymentDetailsDTO> paymentDetailsList = new ArrayList<>();
            
            // Extract result sets - SimpleJdbcCall may return them with different keys
            // Try common key patterns: "#result-set-1", "result-set-1", or direct list access
            Object firstResultSet = null;
            Object secondResultSet = null;
            
            // Try to find result sets by iterating through result keys
            for (Map.Entry<String, Object> entry : result.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                
                // Skip non-result-set entries
                if (key.startsWith("#result-set-") || key.startsWith("result-set-")) {
                    if (firstResultSet == null) {
                        firstResultSet = value;
                    } else if (secondResultSet == null) {
                        secondResultSet = value;
                        break; // Found both result sets
                    }
                }
            }
            
            // If not found by key pattern, try to get by index (some databases return as list)
            if (firstResultSet == null && result.size() > 0) {
                // Try to get first value that is a List
                for (Object value : result.values()) {
                    if (value instanceof List && firstResultSet == null) {
                        firstResultSet = value;
                    } else if (value instanceof List && secondResultSet == null) {
                        secondResultSet = value;
                        break;
                    }
                }
            }
            
            // Process first result set (receipt details)
            if (firstResultSet instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> receiptRows = (List<Map<String, Object>>) firstResultSet;
                
                for (Map<String, Object> row : receiptRows) {
                    ReceiptDetailsDTO receipt = new ReceiptDetailsDTO();
                    receipt.setReceiptNumber(getStringValue(row, "Receipt_Number"));
                    receipt.setReceiptAmount(getBigDecimalValue(row, "Receipt_Amount"));
                    receipt.setTreatmentDetails(getStringValue(row, "Treatment_Details"));
                    receipt.setFromDate(getLocalDateValue(row, "From_Date"));
                    receipt.setToDate(getLocalDateValue(row, "To_Date"));
                    receipt.setTitle(getIntegerValue(row, "Title"));
                    receipt.setTitleDescription(getStringValue(row, "Title_Description"));
                    receipt.setDiscount(getBigDecimalValue(row, "Discount"));
                    
                    receiptDetailsList.add(receipt);
                }
            }
            
            // Process second result set (payment details)
            if (secondResultSet instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> paymentRows = (List<Map<String, Object>>) secondResultSet;
                
                for (Map<String, Object> row : paymentRows) {
                    PaymentDetailsDTO payment = new PaymentDetailsDTO();
                    payment.setPaymentRemark(getStringValue(row, "payment_Remark"));
                    payment.setPaymentDescription(getStringValue(row, "Payment_Description"));
                    
                    paymentDetailsList.add(payment);
                }
            }
            
            // Log result set keys for debugging
            logger.debug("Stored procedure result keys: {}", result.keySet());
            
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
    
    // Helper methods for safe value extraction
    private String getStringValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value != null ? value.toString() : null;
    }
    
    private BigDecimal getBigDecimalValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            logger.warn("Could not convert {} to BigDecimal: {}", value, e.getMessage());
            return null;
        }
    }
    
    private Integer getIntegerValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            logger.warn("Could not convert {} to Integer: {}", value, e.getMessage());
            return null;
        }
    }
    
    private LocalDate getLocalDateValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) {
            return null;
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
        try {
            return LocalDate.parse(value.toString(), DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            logger.warn("Could not convert {} to LocalDate: {}", value, e.getMessage());
            return null;
        }
    }
}

