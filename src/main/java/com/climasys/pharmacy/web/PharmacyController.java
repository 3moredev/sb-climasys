package com.climasys.pharmacy.web;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {

    private final JdbcTemplate jdbcTemplate;

    public PharmacyController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/visits/{visitId}/medicines")
    public ResponseEntity<?> addMedicines(@PathVariable String visitId, @RequestBody Map<String, Object> payload) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_InsertMedicineGrid");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.putAll(payload);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/visits/{visitId}/prescriptions")
    public ResponseEntity<?> addPrescriptions(@PathVariable String visitId, @RequestBody Map<String, Object> payload) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_InsertPrescriptionGrid");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);
            parameters.putAll(payload);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/medicines")
    public ResponseEntity<?> getMedicines(@RequestParam(required = false) String search) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetMedicines");

            Map<String, Object> parameters = new HashMap<>();
            if (search != null) {
                parameters.put("SearchTerm", search);
            }

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/visits/{visitId}/medicines")
    public ResponseEntity<?> getVisitMedicines(@PathVariable String visitId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetVisitMedicines");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("VisitId", visitId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}


