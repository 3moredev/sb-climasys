package com.climasys.web;

import com.climasys.auth.annotation.RefreshSession;

import com.climasys.entity.PrescriptionSubCategory;
import com.climasys.service.PrescriptionSubCategoryService;
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
 * REST Controller for PrescriptionSubCategory management
 * Provides endpoints for managing prescription subcategory master data
 * Following the same pattern as MedicineMasterController
 */
@RestController
@RequestMapping("/api/prescription-subcategory")
@RefreshSession
public class PrescriptionSubCategoryController {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionSubCategoryController.class);

    @Autowired
    private PrescriptionSubCategoryService prescriptionSubCategoryService;

    /**
     * Get all subcategories for a specific doctor
     * GET /api/prescription-subcategory/doctor/{doctorId}
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getAllSubCategoriesForDoctor(@PathVariable String doctorId) {
        try {
            logger.info("Getting all subcategories for doctor: {}", doctorId);
            List<PrescriptionSubCategory> subCategories = prescriptionSubCategoryService.getAllSubCategoriesForDoctor(doctorId);
            return ResponseEntity.ok(subCategories);
        } catch (Exception e) {
            logger.error("Error getting subcategories for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get subcategories: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all subcategories for a specific category and doctor
     * GET /api/prescription-subcategory/doctor/{doctorId}/category/{catShortName}
     */
    @GetMapping("/doctor/{doctorId}/category/{catShortName}")
    public ResponseEntity<?> getSubCategoriesByCategory(
            @PathVariable String doctorId,
            @PathVariable String catShortName) {
        try {
            logger.info("Getting subcategories for category: {} and doctor: {}", catShortName, doctorId);
            List<PrescriptionSubCategory> subCategories = prescriptionSubCategoryService.getSubCategoriesByCategory(catShortName, doctorId);
            return ResponseEntity.ok(subCategories);
        } catch (Exception e) {
            logger.error("Error getting subcategories for category {} and doctor {}: {}", catShortName, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get subcategories: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Search subcategories by category name or subcategory description for a specific doctor
     * GET /api/prescription-subcategory/doctor/{doctorId}/search?term={searchTerm}
     */
    @GetMapping("/doctor/{doctorId}/search")
    public ResponseEntity<?> searchSubCategoriesByDescription(
            @PathVariable String doctorId,
            @RequestParam String term) {
        try {
            logger.info("Searching subcategories for doctor: {} with term: {}", doctorId, term);
            List<PrescriptionSubCategory> subCategories = prescriptionSubCategoryService.searchSubCategoriesByDescription(doctorId, term);
            return ResponseEntity.ok(subCategories);
        } catch (Exception e) {
            logger.error("Error searching subcategories for doctor {} with term {}: {}", doctorId, term, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search subcategories: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get a subcategory by category, subcategory description, and doctor ID
     * GET /api/prescription-subcategory/doctor/{doctorId}/category/{catShortName}/subcategory/{catsubDescription}
     */
    @GetMapping("/doctor/{doctorId}/category/{catShortName}/subcategory/{catsubDescription}")
    public ResponseEntity<?> getSubCategoryByDescription(
            @PathVariable String doctorId,
            @PathVariable String catShortName,
            @PathVariable String catsubDescription) {
        try {
            logger.info("Getting subcategory by description: {} for category: {} and doctor: {}", catsubDescription, catShortName, doctorId);
            Optional<PrescriptionSubCategory> subCategory = prescriptionSubCategoryService.getSubCategoryByDescription(
                    catShortName, catsubDescription, doctorId);
            if (subCategory.isPresent()) {
                return ResponseEntity.ok(subCategory.get());
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Subcategory not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting subcategory by description {} for category {} and doctor {}: {}", 
                    catsubDescription, catShortName, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get subcategory: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Create a new subcategory
     * POST /api/prescription-subcategory
     */
    @PostMapping
    public ResponseEntity<?> createSubCategory(@RequestBody PrescriptionSubCategory subCategory) {
        try {
            logger.info("Creating new subcategory: {} for category: {}", subCategory.getCatsubDescription(), subCategory.getCatShortName());
            PrescriptionSubCategory createdSubCategory = prescriptionSubCategoryService.createSubCategory(subCategory);
            return ResponseEntity.ok(createdSubCategory);
        } catch (Exception e) {
            logger.error("Error creating subcategory: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create subcategory: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update an existing subcategory
     * PUT /api/prescription-subcategory
     */
    @PutMapping
    public ResponseEntity<?> updateSubCategory(@RequestBody PrescriptionSubCategory subCategory) {
        try {
            logger.info("Updating subcategory: {} for category: {}", subCategory.getCatsubDescription(), subCategory.getCatShortName());
            PrescriptionSubCategory updatedSubCategory = prescriptionSubCategoryService.updateSubCategory(subCategory);
            return ResponseEntity.ok(updatedSubCategory);
        } catch (Exception e) {
            logger.error("Error updating subcategory: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to update subcategory: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete a subcategory
     * DELETE /api/prescription-subcategory/doctor/{doctorId}/category/{catShortName}/subcategory/{catsubDescription}
     */
    @DeleteMapping("/doctor/{doctorId}/category/{catShortName}/subcategory/{catsubDescription}")
    public ResponseEntity<?> deleteSubCategory(
            @PathVariable String doctorId,
            @PathVariable String catShortName,
            @PathVariable String catsubDescription) {
        try {
            logger.info("Deleting subcategory: {} for category: {} and doctor: {}", catsubDescription, catShortName, doctorId);
            boolean deleted = prescriptionSubCategoryService.deleteSubCategory(catShortName, catsubDescription, doctorId);
            if (deleted) {
                Map<String, Object> result = new HashMap<>();
                result.put("message", "Subcategory deleted successfully");
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Subcategory not found or access denied");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting subcategory {} for category {} and doctor {}: {}", 
                    catsubDescription, catShortName, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to delete subcategory: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
