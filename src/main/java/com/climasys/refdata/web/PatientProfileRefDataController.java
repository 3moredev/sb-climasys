package com.climasys.refdata.web;

import com.climasys.refdata.service.PatientProfileRefDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/refdata")
public class PatientProfileRefDataController {

    private final PatientProfileRefDataService refDataService;

    public PatientProfileRefDataController(PatientProfileRefDataService refDataService) {
        this.refDataService = refDataService;
    }

    /**
     * JPA/native-query endpoint equivalent to USP_Get_PatientProfileRefData
     */
    @GetMapping("/patient-profile")
    public ResponseEntity<Map<String, Object>> getPatientProfileRefData(
            @RequestParam String doctorId,
            @RequestParam String clinicId) {
        return ResponseEntity.ok(refDataService.getRefData(doctorId, clinicId));
    }
}


