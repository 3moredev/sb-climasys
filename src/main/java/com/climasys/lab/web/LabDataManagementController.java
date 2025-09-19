package com.climasys.lab.web;

import com.climasys.lab.service.LabDataManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for lab data management operations
 */
@RestController
@RequestMapping("/api/lab/data-management")
@CrossOrigin(origins = "*")
public class LabDataManagementController {

    @Autowired
    private LabDataManagementService labDataManagementService;

    /**
     * Delete lab test records
     */
    @DeleteMapping("/test-records/{testId}")
    public ResponseEntity<List<Map<String, Object>>> deleteLabTestRecords(@PathVariable String testId) {
        List<Map<String, Object>> result = labDataManagementService.deleteLabTestRecords(testId);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete lab test parameter
     */
    @DeleteMapping("/test-parameters/{parameterId}")
    public ResponseEntity<List<Map<String, Object>>> deleteLabTestParameter(@PathVariable String parameterId) {
        List<Map<String, Object>> result = labDataManagementService.deleteLabTestParameter(parameterId);
        return ResponseEntity.ok(result);
    }
}
