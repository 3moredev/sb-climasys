package com.climasys.web;

import com.climasys.auth.annotation.RefreshSession;

import com.climasys.entity.PrescriptionCategory;
import com.climasys.service.PrescriptionCategoryService;
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
 * REST Controller for PrescriptionCategory management
 * Provides endpoints for managing prescription category master data
 * Following the same pattern as MedicineMasterController
 */
@RestController
@RequestMapping("/api/prescription-category")
@RefreshSession
public class PrescriptionCategoryController {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionCategoryController.class);

    @Autowired
    private PrescriptionCategoryService prescriptionCategoryService;

    /**
     * Get all categories for a specific doctor
     * GET /api/prescription-category/doctor/{doctorId}
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getAllCategoriesForDoctor(@PathVariable String doctorId) {
        try {
            logger.info("Getting all categories for doctor: {}", doctorId);
            List<PrescriptionCategory> categories = prescriptionCategoryService.getAllCategoriesForDoctor(doctorId);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Error getting categories for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get categories: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Search categories by short name or long description for a specific doctor
     * GET /api/prescription-category/doctor/{doctorId}/search?term={searchTerm}
     */
    @GetMapping("/doctor/{doctorId}/search")
    public ResponseEntity<?> searchCategoriesByDescription(
            @PathVariable String doctorId,
            @RequestParam String term) {
        try {
            logger.info("Searching categories for doctor: {} with term: {}", doctorId, term);
            List<PrescriptionCategory> categories = prescriptionCategoryService.searchCategoriesByDescription(doctorId, term);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Error searching categories for doctor {} with term {}: {}", doctorId, term, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search categories: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get a category by short name and doctor ID
     * GET /api/prescription-category/doctor/{doctorId}/category/{catShortName}
     */
    @GetMapping("/doctor/{doctorId}/category/{catShortName}")
    public ResponseEntity<?> getCategoryByShortName(
            @PathVariable String doctorId,
            @PathVariable String catShortName) {
        try {
            logger.info("Getting category by short name: {} for doctor: {}", catShortName, doctorId);
            Optional<PrescriptionCategory> category = prescriptionCategoryService.getCategoryByShortName(catShortName, doctorId);
            if (category.isPresent()) {
                return ResponseEntity.ok(category.get());
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Category not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting category by short name {} for doctor {}: {}", catShortName, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get category: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Create a new category
     * POST /api/prescription-category
     */
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody PrescriptionCategory category) {
        try {
            logger.info("Creating new category: {}", category.getCatShortName());
            PrescriptionCategory createdCategory = prescriptionCategoryService.createCategory(category);
            return ResponseEntity.ok(createdCategory);
        } catch (Exception e) {
            logger.error("Error creating category: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create category: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update an existing category
     * PUT /api/prescription-category
     */
    @PutMapping
    public ResponseEntity<?> updateCategory(@RequestBody PrescriptionCategory category) {
        try {
            logger.info("Updating category: {}", category.getCatShortName());
            PrescriptionCategory updatedCategory = prescriptionCategoryService.updateCategory(category);
            return ResponseEntity.ok(updatedCategory);
        } catch (Exception e) {
            logger.error("Error updating category: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to update category: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete a category
     * DELETE /api/prescription-category/doctor/{doctorId}/category/{catShortName}
     */
    @DeleteMapping("/doctor/{doctorId}/category/{catShortName}")
    public ResponseEntity<?> deleteCategory(
            @PathVariable String doctorId,
            @PathVariable String catShortName) {
        try {
            logger.info("Deleting category: {} for doctor: {}", catShortName, doctorId);
            boolean deleted = prescriptionCategoryService.deleteCategory(catShortName, doctorId);
            if (deleted) {
                Map<String, Object> result = new HashMap<>();
                result.put("message", "Category deleted successfully");
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Category not found or access denied");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting category {} for doctor {}: {}", catShortName, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to delete category: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
