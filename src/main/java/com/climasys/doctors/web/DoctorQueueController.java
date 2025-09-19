package com.climasys.doctors.web;

import com.climasys.doctors.service.DoctorQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for doctor queue and waiting list management
 */
@RestController
@RequestMapping("/api/doctors/queue")
@CrossOrigin(origins = "*")
public class DoctorQueueController {

    @Autowired
    private DoctorQueueService doctorQueueService;

    /**
     * Get patients waiting for a specific doctor
     */
    @GetMapping("/{doctorId}/waiting-patients")
    public ResponseEntity<List<Map<String, Object>>> getPatientsWaitingForDoctor(@PathVariable String doctorId) {
        List<Map<String, Object>> result = doctorQueueService.getPatientsWaitingForDoctor(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get waiting status for a specific doctor
     */
    @GetMapping("/{doctorId}/waiting-status")
    public ResponseEntity<List<Map<String, Object>>> getDoctorWaitingStatus(@PathVariable String doctorId) {
        List<Map<String, Object>> result = doctorQueueService.getDoctorWaitingStatus(doctorId);
        return ResponseEntity.ok(result);
    }
}
