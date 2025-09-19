package com.climasys.patients.web;

import com.climasys.patients.service.PatientReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller for patient reports and analytics
 */
@RestController
@RequestMapping("/api/patients/reports")
@CrossOrigin(origins = "*")
public class PatientReportController {

    @Autowired
    private PatientReportService patientReportService;

    /**
     * Get comprehensive patient report for a date range
     */
    @GetMapping("/comprehensive")
    public ResponseEntity<List<Map<String, Object>>> getPatientReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        List<Map<String, Object>> result = patientReportService.getPatientReport(
                dateFrom.toString(), dateTo.toString());
        return ResponseEntity.ok(result);
    }

    /**
     * Get patient distribution by area/region
     */
    @GetMapping("/distribution-by-area")
    public ResponseEntity<List<Map<String, Object>>> getPatientDistributionByArea(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        List<Map<String, Object>> result = patientReportService.getPatientDistributionByArea(
                dateFrom.toString(), dateTo.toString());
        return ResponseEntity.ok(result);
    }

    /**
     * Get family-based fee analysis
     */
    @GetMapping("/family-fee-analysis")
    public ResponseEntity<List<Map<String, Object>>> getFamilyFeeAnalysis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        List<Map<String, Object>> result = patientReportService.getFamilyFeeAnalysis(
                dateFrom.toString(), dateTo.toString());
        return ResponseEntity.ok(result);
    }

    /**
     * Get operator-wise family fee analysis
     */
    @GetMapping("/operator-family-fee-analysis")
    public ResponseEntity<List<Map<String, Object>>> getOperatorFamilyFeeAnalysis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        List<Map<String, Object>> result = patientReportService.getOperatorFamilyFeeAnalysis(
                dateFrom.toString(), dateTo.toString());
        return ResponseEntity.ok(result);
    }

    /**
     * Get consolidated fee report
     */
    @GetMapping("/consolidated-fee-report")
    public ResponseEntity<List<Map<String, Object>>> getConsolidatedFeeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        List<Map<String, Object>> result = patientReportService.getConsolidatedFeeReport(
                dateFrom.toString(), dateTo.toString());
        return ResponseEntity.ok(result);
    }

    /**
     * Get discharge summary by date range
     */
    @GetMapping("/discharge-summary")
    public ResponseEntity<List<Map<String, Object>>> getDischargeSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        List<Map<String, Object>> result = patientReportService.getDischargeSummary(
                dateFrom.toString(), dateTo.toString());
        return ResponseEntity.ok(result);
    }
}
