package com.climasys.lab.web;

import com.climasys.lab.service.LabReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller for lab reports and analytics
 */
@RestController
@RequestMapping("/api/lab/reports")
@CrossOrigin(origins = "*")
public class LabReportController {

    @Autowired
    private LabReportService labReportService;

    /**
     * Get labs summary report for a date range
     */
    @GetMapping("/summary")
    public ResponseEntity<List<Map<String, Object>>> getLabsSummaryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        List<Map<String, Object>> result = labReportService.getLabsSummaryReport(
                dateFrom.toString(), dateTo.toString());
        return ResponseEntity.ok(result);
    }

    /**
     * Get lab summary for download report
     */
    @GetMapping("/download-summary")
    public ResponseEntity<List<Map<String, Object>>> getLabSummaryForDownload(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        List<Map<String, Object>> result = labReportService.getLabSummaryForDownload(
                dateFrom.toString(), dateTo.toString());
        return ResponseEntity.ok(result);
    }
}
