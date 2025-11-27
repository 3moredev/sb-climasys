package com.climasys.web;

import com.climasys.entity.MedicineMaster;
import com.climasys.service.MedicineMasterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for MedicineMaster management
 * Provides endpoints for managing medicine master data
 * Following the same pattern as ProcedureMasterController
 */
@RestController
@RequestMapping("/api/medicine-master")
public class MedicineMasterController {

    private static final Logger logger = LoggerFactory.getLogger(MedicineMasterController.class);

    @Autowired
    private MedicineMasterService medicineMasterService;

    /**
     * Get all medicines for a specific doctor and clinic
     * GET /api/medicine-master/doctor/{doctorId}/clinic/{clinicId}
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}")
    public ResponseEntity<?> getAllMedicinesForDoctorAndClinic(
            @PathVariable String doctorId, 
            @PathVariable String clinicId) {
        try {
            logger.info("Getting all medicines for doctor: {} and clinic: {}", doctorId, clinicId);
            List<MedicineMaster> medicines = medicineMasterService.getAllMedicinesForDoctorAndClinic(doctorId, clinicId);
            return ResponseEntity.ok(medicines);
        } catch (Exception e) {
            logger.error("Error getting medicines for doctor {} and clinic {}: {}", doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get medicines: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all medicines for a specific doctor (backward compatibility)
     * GET /api/medicine-master/doctor/{doctorId}
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getAllMedicinesForDoctor(@PathVariable String doctorId) {
        try {
            logger.info("Getting all medicines for doctor: {}", doctorId);
            List<MedicineMaster> medicines = medicineMasterService.getAllMedicinesForDoctor(doctorId);
            return ResponseEntity.ok(medicines);
        } catch (Exception e) {
            logger.error("Error getting medicines for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get medicines: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Search medicines by short description, medicine description, or priority for a specific doctor and clinic
     * GET /api/medicine-master/doctor/{doctorId}/clinic/{clinicId}/search?term={searchTerm}
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}/search")
    public ResponseEntity<?> searchMedicinesByDescription(
            @PathVariable String doctorId,
            @PathVariable String clinicId,
            @RequestParam String term) {
        try {
            logger.info("Searching medicines for doctor: {} and clinic: {} with term: {}", doctorId, clinicId, term);
            List<MedicineMaster> medicines = medicineMasterService.searchMedicinesByDescription(doctorId, clinicId, term);
            return ResponseEntity.ok(medicines);
        } catch (Exception e) {
            logger.error("Error searching medicines for doctor {} and clinic {} with term {}: {}", doctorId, clinicId, term, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search medicines: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Search medicines by short description, medicine description, or priority for a specific doctor (backward compatibility)
     * GET /api/medicine-master/doctor/{doctorId}/search?term={searchTerm}
     */
    @GetMapping("/doctor/{doctorId}/search")
    public ResponseEntity<?> searchMedicinesByDescription(
            @PathVariable String doctorId,
            @RequestParam String term) {
        try {
            logger.info("Searching medicines for doctor: {} with term: {}", doctorId, term);
            List<MedicineMaster> medicines = medicineMasterService.searchMedicinesByDescription(doctorId, term);
            return ResponseEntity.ok(medicines);
        } catch (Exception e) {
            logger.error("Error searching medicines for doctor {} with term {}: {}", doctorId, term, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search medicines: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get a medicine by short description, doctor ID, and clinic ID
     * GET /api/medicine-master/doctor/{doctorId}/clinic/{clinicId}/medicine/{shortDescription}
     */
    @GetMapping("/doctor/{doctorId}/clinic/{clinicId}/medicine/{shortDescription}")
    public ResponseEntity<?> getMedicineByShortDescription(
            @PathVariable String doctorId,
            @PathVariable String clinicId,
            @PathVariable String shortDescription) {
        try {
            logger.info("Getting medicine by short description: {} for doctor: {} and clinic: {}", shortDescription, doctorId, clinicId);
            Optional<MedicineMaster> medicine = medicineMasterService.getMedicineByShortDescription(shortDescription, doctorId, clinicId);
            if (medicine.isPresent()) {
                return ResponseEntity.ok(medicine.get());
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Medicine not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting medicine by short description {} for doctor {} and clinic {}: {}", shortDescription, doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get medicine: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Create a new medicine
     * POST /api/medicine-master
     */
    @PostMapping
    public ResponseEntity<?> createMedicine(@RequestBody MedicineMaster medicine) {
        try {
            logger.info("Creating new medicine: {}", medicine.getShortDescription());
            MedicineMaster createdMedicine = medicineMasterService.createMedicine(medicine);
            return ResponseEntity.ok(createdMedicine);
        } catch (Exception e) {
            logger.error("Error creating medicine: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create medicine: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update an existing medicine
     * PUT /api/medicine-master
     */
    @PutMapping
    public ResponseEntity<?> updateMedicine(@RequestBody MedicineMaster medicine) {
        try {
            logger.info("Updating medicine: {}", medicine.getShortDescription());
            MedicineMaster updatedMedicine = medicineMasterService.updateMedicine(medicine);
            return ResponseEntity.ok(updatedMedicine);
        } catch (Exception e) {
            logger.error("Error updating medicine: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to update medicine: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete a medicine
     * DELETE /api/medicine-master/doctor/{doctorId}/clinic/{clinicId}/medicine/{shortDescription}
     */
    @DeleteMapping("/doctor/{doctorId}/clinic/{clinicId}/medicine/{shortDescription}")
    public ResponseEntity<?> deleteMedicine(
            @PathVariable String doctorId,
            @PathVariable String clinicId,
            @PathVariable String shortDescription) {
        try {
            logger.info("Deleting medicine: {} for doctor: {} and clinic: {}", shortDescription, doctorId, clinicId);
            boolean deleted = medicineMasterService.deleteMedicine(shortDescription, doctorId, clinicId);
            if (deleted) {
                Map<String, Object> result = new HashMap<>();
                result.put("message", "Medicine deleted successfully");
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Medicine not found or access denied");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting medicine {} for doctor {} and clinic {}: {}", shortDescription, doctorId, clinicId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to delete medicine: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

