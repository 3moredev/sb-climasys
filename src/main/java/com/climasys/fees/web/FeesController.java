package com.climasys.fees.web;

import com.climasys.fees.service.FeesDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/fees")
public class FeesController {

    private final FeesDetailsService feesDetailsService;

    public FeesController(FeesDetailsService feesDetailsService) {
        this.feesDetailsService = feesDetailsService;
    }

    /**
     * JPA endpoint equivalent to USP_Get_Patient_FeesDetails
     * Params match the original signature where applicable; doctorId is accepted for parity but not used.
     */
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getFeesDetails(
            @RequestParam String patientId,
            @RequestParam(required = false) String doctorId) {
        Map<String, Object> result = feesDetailsService.getPatientFeesDetails(patientId);
        return ResponseEntity.ok(result);
    }

    /**
     * JPA/JDBC equivalent for USP_Get_PatientFolderAmountForBilling
     */
    @GetMapping("/folder-amount")
    public ResponseEntity<Map<String, Object>> getPatientFolderAmountForBilling(
            @RequestParam String clinicId,
            @RequestParam String doctorId,
            @RequestParam String folderNo,
            @RequestParam Integer patientVisitNo) {
        Map<String, Object> result = feesDetailsService.getPatientFolderAmountForBilling(
                clinicId, doctorId, folderNo, patientVisitNo);
        return ResponseEntity.ok(result);
    }
}


