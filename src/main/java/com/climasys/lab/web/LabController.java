package com.climasys.lab.web;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/lab")
public class LabController {

    private final JdbcTemplate jdbcTemplate;

    public LabController(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    @GetMapping("/tests")
    public ResponseEntity<?> listTests(@RequestParam String doctorId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_BindLabTest");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("DoctorId", doctorId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/visits/{visitId}/tests")
    public ResponseEntity<?> addTests(@PathVariable String visitId, @RequestBody Map<String, Object> payload) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_InsertLabTestGrid");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.putAll(payload);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/visits/{visitId}/results")
    public ResponseEntity<?> saveResults(@PathVariable String visitId, @RequestBody Map<String, Object> payload) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_Save_TestReport");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.putAll(payload);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/visits/{visitId}/results/previous")
    public ResponseEntity<?> getPreviousTestResults(@PathVariable String visitId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_Get_LastTestReports_Details");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}


