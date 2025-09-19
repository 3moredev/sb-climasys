package com.climasys.billing.web;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final JdbcTemplate jdbcTemplate;

    public BillingController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
}
