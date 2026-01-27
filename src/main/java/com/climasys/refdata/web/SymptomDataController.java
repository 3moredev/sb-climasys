package com.climasys.refdata.web;

import com.climasys.auth.annotation.RefreshSession;

import com.climasys.refdata.service.SymptomDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/refdata")
@RefreshSession
public class SymptomDataController {

    private final SymptomDataService symptomDataService;

    public SymptomDataController(SymptomDataService symptomDataService) {
        this.symptomDataService = symptomDataService;
    }

    /**
     * JPA/native-query endpoint equivalent to USP_Get_SymptomData
     */
    @GetMapping("/symptom-data")
    public ResponseEntity<Map<String, Object>> getSymptomData(
            @RequestParam String doctorId,
            @RequestParam String clinicId) {
        return ResponseEntity.ok(symptomDataService.getSymptomData(doctorId, clinicId));
    }
}

