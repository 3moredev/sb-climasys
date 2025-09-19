package com.climasys.lab.web;

import com.climasys.lab.service.LabTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for lab test management and operations
 */
@RestController
@RequestMapping("/api/lab/tests")
@CrossOrigin(origins = "*")
public class LabTestController {

    @Autowired
    private LabTestService labTestService;

    /**
     * Get lab test information by test ID
     */
    @GetMapping("/{testId}")
    public ResponseEntity<List<Map<String, Object>>> getLabTestInfo(@PathVariable String testId) {
        List<Map<String, Object>> result = labTestService.getLabTestInfo(testId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get lab test with parameters
     */
    @GetMapping("/{testId}/with-parameters")
    public ResponseEntity<List<Map<String, Object>>> getLabTestWithParameters(@PathVariable String testId) {
        List<Map<String, Object>> result = labTestService.getLabTestWithParameters(testId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get lab test details
     */
    @GetMapping("/{testId}/details")
    public ResponseEntity<List<Map<String, Object>>> getLabTestDetails(@PathVariable String testId) {
        List<Map<String, Object>> result = labTestService.getLabTestDetails(testId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get lab test details (version 1)
     */
    @GetMapping("/{testId}/details-v1")
    public ResponseEntity<List<Map<String, Object>>> getLabTestDetailsV1(@PathVariable String testId) {
        List<Map<String, Object>> result = labTestService.getLabTestDetailsV1(testId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get lab test details (version 12)
     */
    @GetMapping("/{testId}/details-v12")
    public ResponseEntity<List<Map<String, Object>>> getLabTestDetailsV12(@PathVariable String testId) {
        List<Map<String, Object>> result = labTestService.getLabTestDetailsV12(testId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get lab test ID by test name
     */
    @GetMapping("/by-name/{testName}")
    public ResponseEntity<List<Map<String, Object>>> getLabTestIdByName(@PathVariable String testName) {
        List<Map<String, Object>> result = labTestService.getLabTestIdByName(testName);
        return ResponseEntity.ok(result);
    }

    /**
     * Get lab test parameters
     */
    @GetMapping("/{testId}/parameters")
    public ResponseEntity<List<Map<String, Object>>> getLabTestParameters(@PathVariable String testId) {
        List<Map<String, Object>> result = labTestService.getLabTestParameters(testId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get lab test data for appointment deletion
     */
    @GetMapping("/for-appointment-deletion/{appointmentId}")
    public ResponseEntity<List<Map<String, Object>>> getLabTestDataForAppointmentDeletion(@PathVariable String appointmentId) {
        List<Map<String, Object>> result = labTestService.getLabTestDataForAppointmentDeletion(appointmentId);
        return ResponseEntity.ok(result);
    }
}
