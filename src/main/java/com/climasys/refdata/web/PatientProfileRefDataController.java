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

    /**
     * JPA equivalent to USP_Search_PrescriptionForPatientProfile
     * Searches prescriptions matching the stored procedure logic exactly.
     * Returns two result sets: one filtered by doctor_id/clinic_id and one with all active prescriptions for clinic.
     * 
     * Note: Added clinic_id parameter for multi-clinic support (stored procedure doesn't use it, but table has clinic_id column)
     * 
     * @param prefixText Search text (can be partial medicine name or brand name)
     * @param doctorId Doctor ID for filtering
     * @param clinicId Clinic ID for filtering (required for multi-clinic support)
     * @return Map containing resultSet1 (doctor/clinic-filtered) and resultSet2 (all active for clinic)
     */
    @GetMapping("/prescription-search")
    public ResponseEntity<Map<String, Object>> searchPrescriptionForPatientProfile(
            @RequestParam String prefixText,
            @RequestParam String doctorId,
            @RequestParam String clinicId) {
        return ResponseEntity.ok(refDataService.searchPrescriptionForPatientProfile(prefixText, doctorId, clinicId));
    }
}


