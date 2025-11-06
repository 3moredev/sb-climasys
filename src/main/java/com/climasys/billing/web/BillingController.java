package com.climasys.billing.web;

import com.climasys.billing.service.BillingBreakupService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final JdbcTemplate jdbcTemplate;
    private final BillingBreakupService billingBreakupService;

    public BillingController(JdbcTemplate jdbcTemplate, BillingBreakupService billingBreakupService) {
        this.jdbcTemplate = jdbcTemplate;
        this.billingBreakupService = billingBreakupService;
    }

    public record PaymentRequest(
            @NotBlank String visitId,
            @NotBlank String patientId,
            @NotBlank String amount,
            @NotBlank String paymentMode,
            String paymentReference,
            String notes,
            String userId
    ) {}

    public record ReceiptRequest(
            @NotBlank String visitId,
            @NotBlank String patientId,
            @NotBlank String totalAmount,
            @NotBlank String paidAmount,
            String discountAmount,
            String taxAmount,
            String paymentMode,
            String receiptNumber,
            String userId
    ) {}

    @PostMapping("/payments")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_ProcessPayment");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", req.visitId());
            parameters.put("PatientId", req.patientId());
            parameters.put("Amount", req.amount());
            parameters.put("PaymentMode", req.paymentMode());
            parameters.put("PaymentReference", req.paymentReference());
            parameters.put("Notes", req.notes());
            parameters.put("UserId", req.userId());

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to process payment: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/receipts")
    public ResponseEntity<?> generateReceipt(@RequestBody ReceiptRequest req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GenerateReceipt");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", req.visitId());
            parameters.put("PatientId", req.patientId());
            parameters.put("TotalAmount", req.totalAmount());
            parameters.put("PaidAmount", req.paidAmount());
            parameters.put("DiscountAmount", req.discountAmount());
            parameters.put("TaxAmount", req.taxAmount());
            parameters.put("PaymentMode", req.paymentMode());
            parameters.put("ReceiptNumber", req.receiptNumber());
            parameters.put("UserId", req.userId());

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to generate receipt: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/receipts/{receiptId}")
    public ResponseEntity<?> getReceipt(@PathVariable String receiptId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetReceiptDetails");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ReceiptId", receiptId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get receipt: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/visits/{visitId}/billing")
    public ResponseEntity<?> getVisitBilling(@PathVariable String visitId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetVisitBillingDetails");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get visit billing: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/visits/{visitId}/fees")
    public ResponseEntity<?> addFee(@PathVariable String visitId, @RequestBody Map<String, Object> feeData) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_AddVisitFee");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.putAll(feeData);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to add fee: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/daily-collection")
    public ResponseEntity<?> getDailyCollection(
            @RequestParam String date,
            @RequestParam(required = false) String clinicId,
            @RequestParam(required = false) String doctorId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetDailyCollection");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("CollectionDate", date);
            if (clinicId != null) parameters.put("ClinicId", clinicId);
            if (doctorId != null) parameters.put("DoctorId", doctorId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get daily collection: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/invoices")
    public ResponseEntity<?> getInvoices(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetInvoices");

            Map<String, Object> parameters = new HashMap<>();
            if (patientId != null) parameters.put("PatientId", patientId);
            if (dateFrom != null) parameters.put("DateFrom", dateFrom);
            if (dateTo != null) parameters.put("DateTo", dateTo);
            parameters.put("PageNumber", page);
            parameters.put("PageSize", size);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get invoices: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/discounts")
    public ResponseEntity<?> applyDiscount(@RequestBody Map<String, Object> discountData) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_ApplyDiscount");

            Map<String, Object> result = jdbcCall.execute(discountData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to apply discount: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Submit billing breakup details for a patient visit.
     * This endpoint replicates the functionality of USP_Insert_Billing_BreakupData stored procedure.
     * 
     * Request body should contain:
     * - userId: User ID for audit fields
     * - doctorId: Doctor ID
     * - shiftId: Shift ID
     * - patientId: Patient ID
     * - clinicId: Clinic ID
     * - visitDate: Visit date (ISO format: yyyy-MM-ddTHH:mm:ss)
     * - patientVisitNo: Patient visit number
     * - billingData: List of billing items, each containing:
     *   - billingGroupName: Billing group name
     *   - billingSubgroupName: Billing subgroup name
     *   - billingDetails: Billing details
     *   - defaultFees: Default fees amount
     *   - collectedFees: Collected fees amount
     * - useOverwrite: (optional) If true, also saves to overwrite table
     */
    @PostMapping("/breakup/submit")
    public ResponseEntity<?> submitBillingBreakup(@RequestBody Map<String, Object> request) {
        try {
            // Extract required fields
            String userId = (String) request.get("userId");
            String doctorId = (String) request.get("doctorId");
            Object shiftIdObj = request.get("shiftId");
            Short shiftId = shiftIdObj instanceof Number ? ((Number) shiftIdObj).shortValue() : Short.parseShort(shiftIdObj.toString());
            String patientId = (String) request.get("patientId");
            String clinicId = (String) request.get("clinicId");
            String visitDateStr = (String) request.get("visitDate");
            Object patientVisitNoObj = request.get("patientVisitNo");
            Integer patientVisitNo = patientVisitNoObj instanceof Number ? ((Number) patientVisitNoObj).intValue() : Integer.parseInt(patientVisitNoObj.toString());
            Boolean useOverwrite = (Boolean) request.getOrDefault("useOverwrite", false);
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> billingData = (List<Map<String, Object>>) request.get("billingData");
            
            if (userId == null || doctorId == null || patientId == null || clinicId == null || 
                visitDateStr == null || patientVisitNo == null || billingData == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Missing required fields: userId, doctorId, patientId, clinicId, visitDate, patientVisitNo, billingData");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Parse visit date - handle both date-only and date-time formats
            LocalDateTime visitDate;
            try {
                // Try ISO date-time format first (yyyy-MM-ddTHH:mm:ss)
                visitDate = LocalDateTime.parse(visitDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e1) {
                try {
                    // Try date-only format (yyyy-MM-dd) - set to start of day
                    LocalDate dateOnly = LocalDate.parse(visitDateStr);
                    visitDate = dateOnly.atStartOfDay();
                } catch (Exception e2) {
                    try {
                        // Try alternative date-time format without 'T' separator
                        visitDate = LocalDateTime.parse(visitDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    } catch (Exception e3) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("error", "Invalid visitDate format. Expected formats: 'yyyy-MM-ddTHH:mm:ss', 'yyyy-MM-dd', or 'yyyy-MM-dd HH:mm:ss'. Received: " + visitDateStr);
                        error.put("success", false);
                        return ResponseEntity.badRequest().body(error);
                    }
                }
            }
            
            // Save to base table
            Map<String, Object> result = billingBreakupService.saveBillingBreakupData(
                billingData, userId, doctorId, shiftId, patientId, clinicId, visitDate, patientVisitNo
            );
            
            // If useOverwrite is true, also save to overwrite table
            if (Boolean.TRUE.equals(useOverwrite)) {
                Map<String, Object> overwriteResult = billingBreakupService.saveBillingBreakupDataOverwrite(
                    billingData, userId, doctorId, shiftId, patientId, clinicId, visitDate, patientVisitNo
                );
                
                // Merge results
                result.put("overwrite", overwriteResult);
            }
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to submit billing breakup: " + e.getMessage());
            error.put("success", false);
            return ResponseEntity.badRequest().body(error);
        }
    }
}
