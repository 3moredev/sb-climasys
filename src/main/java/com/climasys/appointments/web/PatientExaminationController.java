package com.climasys.appointments.web;

import com.climasys.appointments.service.PatientExaminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for patient examination and clinical data
 */
@RestController
@RequestMapping("/api/examinations")
@CrossOrigin(origins = "*")
public class PatientExaminationController {

    @Autowired
    private PatientExaminationService patientExaminationService;

    /**
     * Get gynecological ovulation data for a patient
     */
    @GetMapping("/gynecological/ovulation/{patientId}")
    public ResponseEntity<List<Map<String, Object>>> getGynecologicalOvulationData(@PathVariable String patientId) {
        List<Map<String, Object>> result = patientExaminationService.getGynecologicalOvulationData(patientId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get gynecological delivery registration data
     */
    @GetMapping("/gynecological/delivery-registration/{patientId}")
    public ResponseEntity<List<Map<String, Object>>> getGynecologicalDeliveryRegistration(@PathVariable String patientId) {
        List<Map<String, Object>> result = patientExaminationService.getGynecologicalDeliveryRegistration(patientId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get abdominal examination data
     */
    @GetMapping("/abdominal")
    public ResponseEntity<List<Map<String, Object>>> getAbdominalExaminationData(
            @RequestParam String patientId,
            @RequestParam String visitId) {
        List<Map<String, Object>> result = patientExaminationService.getAbdominalExaminationData(patientId, visitId);
        return ResponseEntity.ok(result);
    }
}
