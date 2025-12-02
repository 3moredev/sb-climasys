package com.climasys.web;

import com.climasys.entity.PrescriptionMedicines;
import com.climasys.service.PrescriptionMedicinesService;
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
 * REST Controller for PrescriptionMedicines management (Prescription Details)
 * Provides endpoints for managing prescription medicines master data
 * Following the same pattern as MedicineMasterController
 */
@RestController
@RequestMapping("/api/prescription-medicines")
public class PrescriptionMedicinesController {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionMedicinesController.class);

    @Autowired
    private PrescriptionMedicinesService prescriptionMedicinesService;

    /**
     * Get all prescription medicines for a specific doctor
     * GET /api/prescription-medicines/doctor/{doctorId}
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getAllPrescriptionMedicinesForDoctor(@PathVariable String doctorId) {
        try {
            logger.info("Getting all prescription medicines for doctor: {}", doctorId);
            List<PrescriptionMedicines> medicines = prescriptionMedicinesService.getAllPrescriptionMedicinesForDoctor(doctorId);
            return ResponseEntity.ok(medicines);
        } catch (Exception e) {
            logger.error("Error getting prescription medicines for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get prescription medicines: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all active prescription medicines for a specific doctor
     * GET /api/prescription-medicines/doctor/{doctorId}/active
     */
    @GetMapping("/doctor/{doctorId}/active")
    public ResponseEntity<?> getActivePrescriptionMedicinesForDoctor(@PathVariable String doctorId) {
        try {
            logger.info("Getting active prescription medicines for doctor: {}", doctorId);
            List<PrescriptionMedicines> medicines = prescriptionMedicinesService.getActivePrescriptionMedicinesForDoctor(doctorId);
            return ResponseEntity.ok(medicines);
        } catch (Exception e) {
            logger.error("Error getting active prescription medicines for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get active prescription medicines: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get prescription medicines by category and doctor
     * GET /api/prescription-medicines/doctor/{doctorId}/category/{catShortName}
     */
    @GetMapping("/doctor/{doctorId}/category/{catShortName}")
    public ResponseEntity<?> getPrescriptionMedicinesByCategory(
            @PathVariable String doctorId,
            @PathVariable String catShortName) {
        try {
            logger.info("Getting prescription medicines for category: {} and doctor: {}", catShortName, doctorId);
            List<PrescriptionMedicines> medicines = prescriptionMedicinesService.getPrescriptionMedicinesByCategory(catShortName, doctorId);
            return ResponseEntity.ok(medicines);
        } catch (Exception e) {
            logger.error("Error getting prescription medicines for category {} and doctor {}: {}", catShortName, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get prescription medicines: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get prescription medicines by category and subcategory for a specific doctor
     * GET /api/prescription-medicines/doctor/{doctorId}/category/{catShortName}/subcategory/{catsubDescription}
     */
    @GetMapping("/doctor/{doctorId}/category/{catShortName}/subcategory/{catsubDescription}")
    public ResponseEntity<?> getPrescriptionMedicinesByCategoryAndSubCategory(
            @PathVariable String doctorId,
            @PathVariable String catShortName,
            @PathVariable String catsubDescription) {
        try {
            logger.info("Getting prescription medicines for category: {}, subcategory: {} and doctor: {}", catShortName, catsubDescription, doctorId);
            List<PrescriptionMedicines> medicines = prescriptionMedicinesService.getPrescriptionMedicinesByCategoryAndSubCategory(
                    catShortName, catsubDescription, doctorId);
            return ResponseEntity.ok(medicines);
        } catch (Exception e) {
            logger.error("Error getting prescription medicines for category {}, subcategory {} and doctor {}: {}", 
                    catShortName, catsubDescription, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get prescription medicines: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Search prescription medicines by category, subcategory, medicine name, brand name, or priority for a specific doctor
     * GET /api/prescription-medicines/doctor/{doctorId}/search?term={searchTerm}
     */
    @GetMapping("/doctor/{doctorId}/search")
    public ResponseEntity<?> searchPrescriptionMedicinesByDescription(
            @PathVariable String doctorId,
            @RequestParam String term) {
        try {
            logger.info("Searching prescription medicines for doctor: {} with term: {}", doctorId, term);
            List<PrescriptionMedicines> medicines = prescriptionMedicinesService.searchPrescriptionMedicinesByDescription(doctorId, term);
            return ResponseEntity.ok(medicines);
        } catch (Exception e) {
            logger.error("Error searching prescription medicines for doctor {} with term {}: {}", doctorId, term, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search prescription medicines: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get a prescription medicine by all key fields
     * GET /api/prescription-medicines/doctor/{doctorId}/category/{catShortName}/subcategory/{catsubDescription}/medicine/{medicineName}/brand/{brandName}
     */
    @GetMapping("/doctor/{doctorId}/category/{catShortName}/subcategory/{catsubDescription}/medicine/{medicineName}/brand/{brandName}")
    public ResponseEntity<?> getPrescriptionMedicine(
            @PathVariable String doctorId,
            @PathVariable String catShortName,
            @PathVariable String catsubDescription,
            @PathVariable String medicineName,
            @PathVariable String brandName) {
        try {
            logger.info("Getting prescription medicine: {} - {} for category: {}, subcategory: {} and doctor: {}", 
                    medicineName, brandName, catShortName, catsubDescription, doctorId);
            Optional<PrescriptionMedicines> medicine = prescriptionMedicinesService.getPrescriptionMedicine(
                    catShortName, catsubDescription, medicineName, brandName, doctorId);
            if (medicine.isPresent()) {
                return ResponseEntity.ok(medicine.get());
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Prescription medicine not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting prescription medicine {} - {} for category {}, subcategory {} and doctor {}: {}", 
                    medicineName, brandName, catShortName, catsubDescription, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get prescription medicine: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Create a new prescription medicine
     * POST /api/prescription-medicines
     */
    @PostMapping
    public ResponseEntity<?> createPrescriptionMedicine(@RequestBody PrescriptionMedicines prescriptionMedicine) {
        try {
            logger.info("Creating new prescription medicine: {} - {}", prescriptionMedicine.getMedicineName(), prescriptionMedicine.getBrandName());
            PrescriptionMedicines createdMedicine = prescriptionMedicinesService.createPrescriptionMedicine(prescriptionMedicine);
            return ResponseEntity.ok(createdMedicine);
        } catch (Exception e) {
            logger.error("Error creating prescription medicine: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create prescription medicine: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update an existing prescription medicine
     * PUT /api/prescription-medicines
     */
    @PutMapping
    public ResponseEntity<?> updatePrescriptionMedicine(@RequestBody PrescriptionMedicines prescriptionMedicine) {
        try {
            logger.info("Updating prescription medicine: {} - {}", prescriptionMedicine.getMedicineName(), prescriptionMedicine.getBrandName());
            PrescriptionMedicines updatedMedicine = prescriptionMedicinesService.updatePrescriptionMedicine(prescriptionMedicine);
            return ResponseEntity.ok(updatedMedicine);
        } catch (Exception e) {
            logger.error("Error updating prescription medicine: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to update prescription medicine: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete a prescription medicine
     * DELETE /api/prescription-medicines/doctor/{doctorId}/category/{catShortName}/subcategory/{catsubDescription}/medicine/{medicineName}/brand/{brandName}
     */
    @DeleteMapping("/doctor/{doctorId}/category/{catShortName}/subcategory/{catsubDescription}/medicine/{medicineName}/brand/{brandName}")
    public ResponseEntity<?> deletePrescriptionMedicine(
            @PathVariable String doctorId,
            @PathVariable String catShortName,
            @PathVariable String catsubDescription,
            @PathVariable String medicineName,
            @PathVariable String brandName) {
        try {
            logger.info("Deleting prescription medicine: {} - {} for category: {}, subcategory: {} and doctor: {}", 
                    medicineName, brandName, catShortName, catsubDescription, doctorId);
            boolean deleted = prescriptionMedicinesService.deletePrescriptionMedicine(
                    catShortName, catsubDescription, medicineName, brandName, doctorId);
            if (deleted) {
                Map<String, Object> result = new HashMap<>();
                result.put("message", "Prescription medicine deleted successfully");
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Prescription medicine not found or access denied");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting prescription medicine {} - {} for category {}, subcategory {} and doctor {}: {}", 
                    medicineName, brandName, catShortName, catsubDescription, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to delete prescription medicine: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

