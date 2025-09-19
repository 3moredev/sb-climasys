package com.climasys.reports.web;

import com.climasys.reports.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportsController {

    private final JdbcTemplate jdbcTemplate;
    private final DashboardService dashboardService;

    public ReportsController(JdbcTemplate jdbcTemplate, DashboardService dashboardService) {
        this.jdbcTemplate = jdbcTemplate;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData(
            @RequestParam(required = false) String clinicId,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String date) {
        try {
            // Use the new DashboardService instead of stored procedure
            Map<String, Object> result = dashboardService.getDashboardData();
            
            // Add request parameters to result for logging/debugging
            if (clinicId != null) result.put("requestedClinicId", clinicId);
            if (doctorId != null) result.put("requestedDoctorId", doctorId);
            if (date != null) result.put("requestedDate", date);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get dashboard data: " + e.getMessage());
            error.put("message", "Dashboard service encountered an error");
            error.put("details", e.getClass().getSimpleName());
            e.printStackTrace(); // Log the full stack trace
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/daily-collection")
    public ResponseEntity<?> getDailyCollectionReport(
            @RequestParam String date,
            @RequestParam(required = false) String clinicId,
            @RequestParam(required = false) String doctorId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetDailyCollectionReport");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("CollectionDate", date);
            if (clinicId != null) parameters.put("ClinicId", clinicId);
            if (doctorId != null) parameters.put("DoctorId", doctorId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get daily collection report: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/patient-statistics")
    public ResponseEntity<?> getPatientStatistics(
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String clinicId,
            @RequestParam(required = false) String doctorId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetPatientStatistics");

            Map<String, Object> parameters = new HashMap<>();
            if (dateFrom != null) parameters.put("DateFrom", dateFrom);
            if (dateTo != null) parameters.put("DateTo", dateTo);
            if (clinicId != null) parameters.put("ClinicId", clinicId);
            if (doctorId != null) parameters.put("DoctorId", doctorId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get patient statistics: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/financial-summary")
    public ResponseEntity<?> getFinancialSummary(
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String clinicId,
            @RequestParam(required = false) String doctorId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetFinancialSummary");

            Map<String, Object> parameters = new HashMap<>();
            if (dateFrom != null) parameters.put("DateFrom", dateFrom);
            if (dateTo != null) parameters.put("DateTo", dateTo);
            if (clinicId != null) parameters.put("ClinicId", clinicId);
            if (doctorId != null) parameters.put("DoctorId", doctorId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get financial summary: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/appointment-summary")
    public ResponseEntity<?> getAppointmentSummary(
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String clinicId,
            @RequestParam(required = false) String doctorId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetAppointmentSummary");

            Map<String, Object> parameters = new HashMap<>();
            if (dateFrom != null) parameters.put("DateFrom", dateFrom);
            if (dateTo != null) parameters.put("DateTo", dateTo);
            if (clinicId != null) parameters.put("ClinicId", clinicId);
            if (doctorId != null) parameters.put("DoctorId", doctorId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get appointment summary: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/lab-summary")
    public ResponseEntity<?> getLabSummary(
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String clinicId,
            @RequestParam(required = false) String doctorId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetLabSummary");

            Map<String, Object> parameters = new HashMap<>();
            if (dateFrom != null) parameters.put("DateFrom", dateFrom);
            if (dateTo != null) parameters.put("DateTo", dateTo);
            if (clinicId != null) parameters.put("ClinicId", clinicId);
            if (doctorId != null) parameters.put("DoctorId", doctorId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get lab summary: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/pharmacy-summary")
    public ResponseEntity<?> getPharmacySummary(
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String clinicId,
            @RequestParam(required = false) String doctorId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetPharmacySummary");

            Map<String, Object> parameters = new HashMap<>();
            if (dateFrom != null) parameters.put("DateFrom", dateFrom);
            if (dateTo != null) parameters.put("DateTo", dateTo);
            if (clinicId != null) parameters.put("ClinicId", clinicId);
            if (doctorId != null) parameters.put("DoctorId", doctorId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get pharmacy summary: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/iiic-summary")
    public ResponseEntity<?> getIIICSummary(
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String clinicId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetIIICSummary");

            Map<String, Object> parameters = new HashMap<>();
            if (dateFrom != null) parameters.put("DateFrom", dateFrom);
            if (dateTo != null) parameters.put("DateTo", dateTo);
            if (clinicId != null) parameters.put("ClinicId", clinicId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get IIIC summary: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/doctor-performance")
    public ResponseEntity<?> getDoctorPerformance(
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String clinicId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetDoctorPerformance");

            Map<String, Object> parameters = new HashMap<>();
            if (dateFrom != null) parameters.put("DateFrom", dateFrom);
            if (dateTo != null) parameters.put("DateTo", dateTo);
            if (clinicId != null) parameters.put("ClinicId", clinicId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get doctor performance: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/monthly-trends")
    public ResponseEntity<?> getMonthlyTrends(
            @RequestParam String year,
            @RequestParam(required = false) String clinicId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetMonthlyTrends");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("Year", year);
            if (clinicId != null) parameters.put("ClinicId", clinicId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get monthly trends: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
