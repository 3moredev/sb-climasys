package com.climasys.reports.web;

import com.climasys.config.DatabaseTableConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class PatientStatusController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private DatabaseTableConfig tableConfig;

    /**
     * Get patient counts by status for today
     */
    @GetMapping("/patient-status-counts")
    public ResponseEntity<?> getPatientStatusCounts(
            @RequestParam(required = false) String clinicId,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String date) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // Use today's date if not provided
            String targetDate = date != null ? date : "CURRENT_DATE";
            
            // Get patient counts by status
            result.put("waiting", getPatientCountByStatus("waiting", clinicId, doctorId, targetDate));
            result.put("withDoctor", getPatientCountByStatus("with_doctor", clinicId, doctorId, targetDate));
            result.put("completed", getPatientCountByStatus("completed", clinicId, doctorId, targetDate));
            result.put("cancelled", getPatientCountByStatus("cancelled", clinicId, doctorId, targetDate));
            result.put("noShow", getPatientCountByStatus("no_show", clinicId, doctorId, targetDate));
            result.put("inProgress", getPatientCountByStatus("in_progress", clinicId, doctorId, targetDate));
            
            // Get total for today
            result.put("totalToday", getTotalPatientsForDate(clinicId, doctorId, targetDate));
            
            // Add metadata
            result.put("date", targetDate);
            result.put("clinicId", clinicId);
            result.put("doctorId", doctorId);
            result.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get patient status counts: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get patient counts by status for a date range
     */
    @GetMapping("/patient-status-counts-range")
    public ResponseEntity<?> getPatientStatusCountsRange(
            @RequestParam String dateFrom,
            @RequestParam String dateTo,
            @RequestParam(required = false) String clinicId,
            @RequestParam(required = false) String doctorId) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // Get patient counts by status for date range
            result.put("waiting", getPatientCountByStatusRange("waiting", clinicId, doctorId, dateFrom, dateTo));
            result.put("withDoctor", getPatientCountByStatusRange("with_doctor", clinicId, doctorId, dateFrom, dateTo));
            result.put("completed", getPatientCountByStatusRange("completed", clinicId, doctorId, dateFrom, dateTo));
            result.put("cancelled", getPatientCountByStatusRange("cancelled", clinicId, doctorId, dateFrom, dateTo));
            result.put("noShow", getPatientCountByStatusRange("no_show", clinicId, doctorId, dateFrom, dateTo));
            result.put("inProgress", getPatientCountByStatusRange("in_progress", clinicId, doctorId, dateFrom, dateTo));
            
            // Get total for date range
            result.put("totalRange", getTotalPatientsForDateRange(clinicId, doctorId, dateFrom, dateTo));
            
            // Add metadata
            result.put("dateFrom", dateFrom);
            result.put("dateTo", dateTo);
            result.put("clinicId", clinicId);
            result.put("doctorId", doctorId);
            result.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get patient status counts for range: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get detailed patient status breakdown with status names
     */
    @GetMapping("/patient-status-breakdown")
    public ResponseEntity<?> getPatientStatusBreakdown(
            @RequestParam(required = false) String clinicId,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String date) {
        try {
            String targetDate = date != null ? date : "CURRENT_DATE";
            
            // Get detailed breakdown with status names
            String sql = buildStatusBreakdownQuery(clinicId, doctorId, targetDate);
            List<Map<String, Object>> statusBreakdown = jdbcTemplate.queryForList(sql);
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusBreakdown", statusBreakdown);
            result.put("date", targetDate);
            result.put("clinicId", clinicId);
            result.put("doctorId", doctorId);
            result.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get patient status breakdown: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private int getPatientCountByStatus(String status, String clinicId, String doctorId, String date) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM ").append(tableConfig.getPatientVisitsTable());
            sql.append(" WHERE DATE(visit_date) = ").append(date);
            
            if (clinicId != null) {
                sql.append(" AND clinic_id = '").append(clinicId).append("'");
            }
            if (doctorId != null) {
                sql.append(" AND doctor_id = '").append(doctorId).append("'");
            }
            
            // Map status names to status IDs or use status descriptions
            if ("waiting".equals(status)) {
                sql.append(" AND status_id = 1"); // Assuming 1 = waiting
            } else if ("with_doctor".equals(status)) {
                sql.append(" AND status_id = 2"); // Assuming 2 = with doctor
            } else if ("completed".equals(status)) {
                sql.append(" AND status_id = 3"); // Assuming 3 = completed
            } else if ("cancelled".equals(status)) {
                sql.append(" AND status_id = 4"); // Assuming 4 = cancelled
            } else if ("no_show".equals(status)) {
                sql.append(" AND status_id = 5"); // Assuming 5 = no show
            } else if ("in_progress".equals(status)) {
                sql.append(" AND status_id = 6"); // Assuming 6 = in progress
            }
            
            sql.append(" AND (delete_flag = false OR delete_flag IS NULL)");
            
            return jdbcTemplate.queryForObject(sql.toString(), Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    private int getPatientCountByStatusRange(String status, String clinicId, String doctorId, String dateFrom, String dateTo) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM ").append(tableConfig.getPatientVisitsTable());
            sql.append(" WHERE DATE(visit_date) BETWEEN '").append(dateFrom).append("' AND '").append(dateTo).append("'");
            
            if (clinicId != null) {
                sql.append(" AND clinic_id = '").append(clinicId).append("'");
            }
            if (doctorId != null) {
                sql.append(" AND doctor_id = '").append(doctorId).append("'");
            }
            
            // Map status names to status IDs
            if ("waiting".equals(status)) {
                sql.append(" AND status_id = 1");
            } else if ("with_doctor".equals(status)) {
                sql.append(" AND status_id = 2");
            } else if ("completed".equals(status)) {
                sql.append(" AND status_id = 3");
            } else if ("cancelled".equals(status)) {
                sql.append(" AND status_id = 4");
            } else if ("no_show".equals(status)) {
                sql.append(" AND status_id = 5");
            } else if ("in_progress".equals(status)) {
                sql.append(" AND status_id = 6");
            }
            
            sql.append(" AND (delete_flag = false OR delete_flag IS NULL)");
            
            return jdbcTemplate.queryForObject(sql.toString(), Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    private int getTotalPatientsForDate(String clinicId, String doctorId, String date) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM ").append(tableConfig.getPatientVisitsTable());
            sql.append(" WHERE DATE(visit_date) = ").append(date);
            
            if (clinicId != null) {
                sql.append(" AND clinic_id = '").append(clinicId).append("'");
            }
            if (doctorId != null) {
                sql.append(" AND doctor_id = '").append(doctorId).append("'");
            }
            
            sql.append(" AND (delete_flag = false OR delete_flag IS NULL)");
            
            return jdbcTemplate.queryForObject(sql.toString(), Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    private int getTotalPatientsForDateRange(String clinicId, String doctorId, String dateFrom, String dateTo) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM ").append(tableConfig.getPatientVisitsTable());
            sql.append(" WHERE DATE(visit_date) BETWEEN '").append(dateFrom).append("' AND '").append(dateTo).append("'");
            
            if (clinicId != null) {
                sql.append(" AND clinic_id = '").append(clinicId).append("'");
            }
            if (doctorId != null) {
                sql.append(" AND doctor_id = '").append(doctorId).append("'");
            }
            
            sql.append(" AND (delete_flag = false OR delete_flag IS NULL)");
            
            return jdbcTemplate.queryForObject(sql.toString(), Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    private String buildStatusBreakdownQuery(String clinicId, String doctorId, String date) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT sr.status_description as status_name, COUNT(pv.id) as patient_count ");
        sql.append("FROM ").append(tableConfig.getPatientVisitsTable()).append(" pv ");
        sql.append("INNER JOIN ").append(tableConfig.getTableName("status_ref")).append(" sr ON pv.status_id = sr.id ");
        
        if (clinicId != null) {
            sql.append("AND sr.clinic_id = '").append(clinicId).append("' ");
        }
        
        sql.append("WHERE DATE(pv.visit_date) = ").append(date);
        
        if (clinicId != null) {
            sql.append(" AND pv.clinic_id = '").append(clinicId).append("'");
        }
        if (doctorId != null) {
            sql.append(" AND pv.doctor_id = '").append(doctorId).append("'");
        }
        
        sql.append(" AND (pv.delete_flag = false OR pv.delete_flag IS NULL) ");
        sql.append("GROUP BY sr.status_description, sr.id ");
        sql.append("ORDER BY patient_count DESC");
        
        return sql.toString();
    }
}
