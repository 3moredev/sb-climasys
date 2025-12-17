package com.climasys.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for executing SQL scripts and loading dummy data
 * This is a utility endpoint for development/testing purposes
 */
@RestController
@RequestMapping("/api/util")
@CrossOrigin(origins = "*")
public class SqlScriptExecutorController {

    @Autowired
    private SqlScriptExecutorService sqlScriptExecutorService;

    /**
     * Execute the dummy daily collection data script
     * POST http://localhost:8080/api/util/load-dummy-daily-collection
     */
    @PostMapping("/load-dummy-daily-collection")
    public ResponseEntity<Map<String, Object>> loadDummyDailyCollectionData() {
        Map<String, Object> response = new HashMap<>();

        try {
            int statementsExecuted = sqlScriptExecutorService.executeDummyDailyCollectionScript();
            int recordsInserted = sqlScriptExecutorService.verifyDummyData();

            response.put("success", true);
            response.put("message", "Dummy data loaded successfully");
            response.put("statementsExecuted", statementsExecuted);
            response.put("patientVisitsInserted", recordsInserted);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error loading dummy data: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Verify the dummy data
     * GET http://localhost:8080/api/util/verify-dummy-data
     */
    @GetMapping("/verify-dummy-data")
    public ResponseEntity<Map<String, Object>> verifyDummyData() {
        Map<String, Object> response = new HashMap<>();

        try {
            int recordCount = sqlScriptExecutorService.verifyDummyData();

            response.put("success", true);
            response.put("patientVisitsCount", recordCount);
            response.put("message", recordCount > 0
                    ? "Found " + recordCount + " dummy patient visits for today"
                    : "No dummy data found for today");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error verifying dummy data: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }
}
