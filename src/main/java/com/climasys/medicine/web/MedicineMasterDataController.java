package com.climasys.medicine.web;

import com.climasys.medicine.service.MedicineMasterDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for medicine master data management
 */
@RestController
@RequestMapping("/api/medicine/master-data")
@CrossOrigin(origins = "*")
public class MedicineMasterDataController {

    @Autowired
    private MedicineMasterDataService medicineMasterDataService;

    /**
     * Get active medicines for a doctor
     */
    @GetMapping("/active-medicines/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getActiveMedicines(@PathVariable String doctorId) {
        List<Map<String, Object>> result = medicineMasterDataService.getActiveMedicines(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get active prescriptions for a doctor
     */
    @GetMapping("/active-prescriptions/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getActivePrescriptions(@PathVariable String doctorId) {
        List<Map<String, Object>> result = medicineMasterDataService.getActivePrescriptions(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get BLD (Before Last Date) medicine data
     */
    @GetMapping("/bld-medicine")
    public ResponseEntity<List<Map<String, Object>>> getBLDMedicineData(
            @RequestParam String patientId,
            @RequestParam String visitId) {
        List<Map<String, Object>> result = medicineMasterDataService.getBLDMedicineData(patientId, visitId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get BLD (Before Last Date) prescription data
     */
    @GetMapping("/bld-prescription")
    public ResponseEntity<List<Map<String, Object>>> getBLDPrescriptionData(
            @RequestParam String patientId,
            @RequestParam String visitId) {
        List<Map<String, Object>> result = medicineMasterDataService.getBLDPrescriptionData(patientId, visitId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get medicine categories for a doctor
     */
    @GetMapping("/categories/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getMedicineCategories(@PathVariable String doctorId) {
        List<Map<String, Object>> result = medicineMasterDataService.getMedicineCategories(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get disease master data
     */
    @GetMapping("/diseases/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getDiseaseMasterData(@PathVariable String doctorId) {
        List<Map<String, Object>> result = medicineMasterDataService.getDiseaseMasterData(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get findings master data
     */
    @GetMapping("/findings/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getFindingsMasterData(@PathVariable String doctorId) {
        List<Map<String, Object>> result = medicineMasterDataService.getFindingsMasterData(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get keyword master data
     */
    @GetMapping("/keywords/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getKeywordMasterData(@PathVariable String doctorId) {
        List<Map<String, Object>> result = medicineMasterDataService.getKeywordMasterData(doctorId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get keyword master data for hospital
     */
    @GetMapping("/keywords-hospital/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getKeywordMasterDataForHospital(@PathVariable String doctorId) {
        List<Map<String, Object>> result = medicineMasterDataService.getKeywordMasterDataForHospital(doctorId);
        return ResponseEntity.ok(result);
    }
}
