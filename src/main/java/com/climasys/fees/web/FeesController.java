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
     * Returns patient fees details for individual visits
     * 
     * @param patientId Patient ID (required)
     * @param doctorId Doctor ID (optional, filters by doctor if provided)
     * @param clinicId Clinic ID (required, filters by clinic)
     * @return Response with success, patientId, doctorId, clinicId, header (folder_no, full_name), and rows (list of fee details)
     */
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getFeesDetails(
            @RequestParam String patientId,
            @RequestParam(required = false) String doctorId,
            @RequestParam String clinicId) {
        Map<String, Object> result = feesDetailsService.getPatientFeesDetails(patientId, doctorId, clinicId);
        return ResponseEntity.ok(result);
    }

    /**
     * JPA endpoint equivalent to USP_Get_ConsolidatedFamilyFees
     * Returns consolidated fees aggregated by financial year
     * 
     * @param doctorId Doctor ID (optional, filters by doctor if provided)
     * @param clinicId Clinic ID (required, filters by clinic)
     * @return Response with success, doctorId, clinicId, and rows (list of financial year summaries)
     */
    @GetMapping("/consolidated-family-fees")
    public ResponseEntity<Map<String, Object>> getConsolidatedFamilyFees(
            @RequestParam(required = false) String doctorId,
            @RequestParam String clinicId) {
        Map<String, Object> result = feesDetailsService.getConsolidatedFamilyFees(doctorId, clinicId);
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


