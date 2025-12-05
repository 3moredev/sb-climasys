package com.climasys.billing.web;

import com.climasys.billing.dto.OPDDailyCollectionDTO;
import com.climasys.billing.service.OPDDailyCollectionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for OPD Daily Collection operations
 * Provides JPA-based API for USP_Get_OPDDailyCollection_For_Operator stored procedure
 */
@RestController
@RequestMapping("/api/billing/opd-daily-collection")
public class OPDDailyCollectionController {
    
    private final OPDDailyCollectionService opdDailyCollectionService;
    
    public OPDDailyCollectionController(OPDDailyCollectionService opdDailyCollectionService) {
        this.opdDailyCollectionService = opdDailyCollectionService;
    }
    
    /**
     * Get OPD Daily Collection data for a date range
     * 
     * @param fromDate Start date (format: yyyy-MM-dd)
     * @param toDate End date (format: yyyy-MM-dd)
     * @param clinicId Clinic ID (required)
     * @param doctorId Doctor ID (optional, use "All" or "0" for all doctors)
     * @param roleId Role ID (default: 3 for operator)
     * @param languageId Language ID (default: 1 for English)
     * @return List of OPD Daily Collection records
     */
    @GetMapping
    public ResponseEntity<?> getOPDDailyCollection(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam String clinicId,
            @RequestParam(required = false, defaultValue = "All") String doctorId,
            @RequestParam(required = false, defaultValue = "3") Integer roleId,
            @RequestParam(required = false, defaultValue = "1") Integer languageId
    ) {
        try {
            List<OPDDailyCollectionDTO> results = opdDailyCollectionService.getOPDDailyCollection(
                    fromDate,
                    toDate,
                    clinicId,
                    doctorId,
                    roleId,
                    languageId
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results);
            response.put("count", results.size());
            response.put("fromDate", fromDate.toString());
            response.put("toDate", toDate.toString());
            response.put("clinicId", clinicId);
            response.put("doctorId", doctorId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get OPD daily collection: " + e.getMessage());
            error.put("message", e.getClass().getSimpleName());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get OPD Daily Collection data for today
     * Convenience endpoint that uses today's date for both fromDate and toDate
     * 
     * @param clinicId Clinic ID (required)
     * @param doctorId Doctor ID (optional, use "All" or "0" for all doctors)
     * @param roleId Role ID (default: 3 for operator)
     * @param languageId Language ID (default: 1 for English)
     * @return List of OPD Daily Collection records
     */
    @GetMapping("/today")
    public ResponseEntity<?> getOPDDailyCollectionToday(
            @RequestParam String clinicId,
            @RequestParam(required = false, defaultValue = "All") String doctorId,
            @RequestParam(required = false, defaultValue = "3") Integer roleId,
            @RequestParam(required = false, defaultValue = "1") Integer languageId
    ) {
        LocalDate today = LocalDate.now();
        return getOPDDailyCollection(today, today, clinicId, doctorId, roleId, languageId);
    }
}

