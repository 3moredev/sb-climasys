package com.climasys.visits.web;

import com.climasys.visits.service.ServiceVisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
public class ServiceVisitController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceVisitController.class);
    private final ServiceVisitService serviceVisitService;

    public ServiceVisitController(ServiceVisitService serviceVisitService) {
        this.serviceVisitService = serviceVisitService;
    }

    @GetMapping("/previous-visit-dates")
    public ResponseEntity<?> getPreviousServiceVisitDates(
            @RequestParam String patientId,
            @RequestParam String doctorId,
            @RequestParam String clinicId,
            @RequestParam(required = false) String todaysVisitDate
    ) {
        LocalDate today = (todaysVisitDate == null || todaysVisitDate.isBlank())
                ? LocalDate.now() : LocalDate.parse(todaysVisitDate);
        Map<String, Object> result = serviceVisitService.getPreviousServiceVisitDates(patientId, doctorId, clinicId, today);
        boolean ok = Boolean.TRUE.equals(result.get("success"));
        return ok ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    @GetMapping("/previous-visit-items")
    public ResponseEntity<?> getPreviousServiceVisitItems(
            @RequestParam String patientId,
            @RequestParam String doctorId,
            @RequestParam String clinicId,
            @RequestParam Short shiftId,
            @RequestParam Integer visitNo,
            @RequestParam String visitDate
    ) {
        logger.info("Received request for previous-visit-items with params: patientId={}, doctorId={}, clinicId={}, shiftId={}, visitNo={}, visitDate={}", 
            patientId, doctorId, clinicId, shiftId, visitNo, visitDate);
        try {
            // Parse visitDate - handle both date-only (2025-10-23) and date-time (2025-10-23 00:00:00) formats
            LocalDate vDate;
            try {
                // Try parsing as date-only first
                vDate = LocalDate.parse(visitDate);
            } catch (DateTimeParseException e) {
                // If that fails, try parsing as date-time and extract the date part
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(visitDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    vDate = dateTime.toLocalDate();
                } catch (DateTimeParseException e2) {
                    // Try ISO date-time format as fallback
                    LocalDateTime dateTime = LocalDateTime.parse(visitDate);
                    vDate = dateTime.toLocalDate();
                }
            }
            Map<String, Object> result = serviceVisitService.getPreviousServiceVisitLineItems(
                    patientId, doctorId, clinicId, shiftId, visitNo, vDate);
            boolean ok = Boolean.TRUE.equals(result.get("success"));
            
            if (ok) {
                logger.info("Successfully returning {} items for previous-visit-items", 
                    result.get("items") != null ? ((java.util.List<?>) result.get("items")).size() : 0);
            } else {
                logger.warn("Failed to fetch previous-visit-items: {}", result.get("error"));
            }
            
            return ok ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            logger.error("Error processing previous-visit-items request: {}", e.getMessage(), e);
            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "Failed to parse request: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResult);
        }
    }
}


