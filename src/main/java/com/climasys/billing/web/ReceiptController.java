package com.climasys.billing.web;

import com.climasys.auth.annotation.RefreshSession;

import com.climasys.billing.dto.*;
import com.climasys.billing.service.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST Controller for receipt operations
 * Provides endpoints for retrieving receipt details for printing
 */
@RestController
@RequestMapping("/api/receipts")
@Tag(name = "Receipt", description = "APIs for managing patient receipts")
@RefreshSession
public class ReceiptController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReceiptController.class);
    
    @Autowired
    private ReceiptService receiptService;
    
    /**
     * Get patient receipt details for printing
     * Equivalent to USP_Get_PatientReceiptData_For_Receipt stored procedure
     * Used in Collection screen (ReceivePaymentNew.aspx) for Print Receipt functionality
     * 
     * @param patientId Patient ID (required)
     * @param shiftId Shift ID (required)
     * @param clinicId Clinic ID (required)
     * @param doctorId Doctor ID (required)
     * @param visitDate Visit Date (required)
     * @param visitType Visit Type (required)
     * @param pvisitno Patient Visit Number (optional)
     * @return ReceiptDetailsResponse containing receipt and payment details
     */
    @GetMapping("/details")
    @Operation(
        summary = "Get patient receipt details",
        description = "Retrieves receipt details for printing from Collection screen. " +
                     "Equivalent to USP_Get_PatientReceiptData_For_Receipt stored procedure."
    )
    public ResponseEntity<ReceiptDetailsResponse> getReceiptDetails(
            @Parameter(description = "Patient ID", required = true, example = "01-10-2021-051429")
            @RequestParam String patientId,
            
            @Parameter(description = "Shift ID", required = true, example = "1")
            @RequestParam Integer shiftId,
            
            @Parameter(description = "Clinic ID", required = true, example = "CL-00001")
            @RequestParam String clinicId,
            
            @Parameter(description = "Doctor ID", required = true, example = "DR-00001")
            @RequestParam String doctorId,
            
            @Parameter(description = "Visit Date", required = true, example = "2025-11-17")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate visitDate,
            
            @Parameter(description = "Visit Type", required = true, example = "O")
            @RequestParam String visitType,
            
            @Parameter(description = "Patient Visit Number", required = false, example = "29")
            @RequestParam(required = false) String pvisitno
    ) {
        logger.info("Receipt details request - Patient: {}, Visit: {}, Date: {}", 
                   patientId, pvisitno, visitDate);
        
        ReceiptDetailsRequest request = new ReceiptDetailsRequest();
        request.setPatientId(patientId);
        request.setShiftId(shiftId);
        request.setClinicId(clinicId);
        request.setDoctorId(doctorId);
        request.setVisitDate(visitDate);
        request.setVisitType(visitType);
        request.setPvisitno(pvisitno);
        
        ReceiptDetailsResponse response = receiptService.getPatientReceiptDetails(request);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get patient receipt details using POST method with request body
     * Alternative endpoint for complex requests
     * 
     * @param request Receipt details request
     * @return ReceiptDetailsResponse containing receipt and payment details
     */
    @PostMapping("/details")
    @Operation(
        summary = "Get patient receipt details (POST)",
        description = "Retrieves receipt details using POST method with request body"
    )
    public ResponseEntity<ReceiptDetailsResponse> getReceiptDetailsPost(
            @Valid @RequestBody ReceiptDetailsRequest request
    ) {
        logger.info("Receipt details POST request - Patient: {}, Visit: {}", 
                   request.getPatientId(), request.getPvisitno());
        
        ReceiptDetailsResponse response = receiptService.getPatientReceiptDetails(request);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Save patient receipt details
     * Equivalent to USP_Insert_ReceiptDetails_Receipt stored procedure
     * Used on Collection screen when submitting payment
     * 
     * @param request Save receipt request with all payment and receipt details
     * @return SaveReceiptResponse with success status and receipt number
     */
    @PostMapping("/save")
    @Operation(
        summary = "Save patient receipt",
        description = "Saves receipt details when payment is submitted on Collection screen. " +
                     "Equivalent to USP_Insert_ReceiptDetails_Receipt stored procedure."
    )
    public ResponseEntity<SaveReceiptResponse> saveReceipt(
            @Valid @RequestBody SaveReceiptRequest request
    ) {
        logger.info("Save receipt request - Patient: {}, Amount: {}", 
                   request.getPatientId(), request.getReceiptAmount());
        
        SaveReceiptResponse response = receiptService.saveReceipt(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
