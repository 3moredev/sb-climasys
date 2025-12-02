package com.climasys.service;

import com.climasys.entity.PrescriptionCategory;
import com.climasys.entity.PrescriptionCategoryId;
import com.climasys.repository.PrescriptionCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for PrescriptionCategory business logic
 * Provides methods for managing prescription category master data
 */
@Service
@Transactional
public class PrescriptionCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionCategoryService.class);

    @Autowired
    private PrescriptionCategoryRepository prescriptionCategoryRepository;

    /**
     * Get all categories for a specific doctor
     * @param doctorId Doctor ID
     * @return List of categories for the doctor
     */
    @Transactional(readOnly = true)
    public List<PrescriptionCategory> getAllCategoriesForDoctor(String doctorId) {
        logger.info("Getting all categories for doctor: {}", doctorId);
        return prescriptionCategoryRepository.findByDoctorIdOrderByCatShortNameAsc(doctorId);
    }

    /**
     * Get a category by short name and doctor ID
     * @param catShortName Category short name
     * @param doctorId Doctor ID
     * @return Optional category
     */
    @Transactional(readOnly = true)
    public Optional<PrescriptionCategory> getCategoryByShortName(String catShortName, String doctorId) {
        logger.info("Getting category by short name: {} for doctor: {}", catShortName, doctorId);
        PrescriptionCategory category = prescriptionCategoryRepository.findByCatShortNameAndDoctorId(catShortName, doctorId);
        return Optional.ofNullable(category);
    }

    /**
     * Search categories by short name or long description for a specific doctor
     * @param doctorId Doctor ID
     * @param searchTerm Search term to match against short name or long description
     * @return List of matching categories
     */
    @Transactional(readOnly = true)
    public List<PrescriptionCategory> searchCategoriesByDescription(String doctorId, String searchTerm) {
        logger.info("Searching categories for doctor: {} with term: {}", doctorId, searchTerm);
        return prescriptionCategoryRepository.searchCategoriesByDescription(doctorId, searchTerm);
    }

    /**
     * Create a new category
     * @param category Category to create
     * @return Created category
     */
    public PrescriptionCategory createCategory(PrescriptionCategory category) {
        logger.info("Creating new category: {}", category.getCatShortName());
        
        // Check if category already exists
        if (prescriptionCategoryRepository.existsByCatShortNameAndDoctorId(
                category.getCatShortName(), category.getDoctorId())) {
            throw new RuntimeException("Category with short name '" + category.getCatShortName() + 
                    "' already exists for doctor " + category.getDoctorId());
        }
        
        // Set creation timestamp
        category.setCreatedOn(LocalDateTime.now());
        category.setModifiedOn(LocalDateTime.now());
        
        return prescriptionCategoryRepository.save(category);
    }

    /**
     * Update an existing category
     * @param category Category to update
     * @return Updated category
     */
    public PrescriptionCategory updateCategory(PrescriptionCategory category) {
        logger.info("Updating category: {}", category.getCatShortName());
        
        // Set modification timestamp
        category.setModifiedOn(LocalDateTime.now());
        
        return prescriptionCategoryRepository.save(category);
    }

    /**
     * Delete a category
     * @param catShortName Category short name
     * @param doctorId Doctor ID
     * @return True if deleted successfully
     */
    public boolean deleteCategory(String catShortName, String doctorId) {
        logger.info("Deleting category: {} for doctor: {}", catShortName, doctorId);
        
        Optional<PrescriptionCategory> categoryOpt = getCategoryByShortName(catShortName, doctorId);
        if (categoryOpt.isPresent()) {
            PrescriptionCategoryId id = new PrescriptionCategoryId(catShortName, doctorId);
            prescriptionCategoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

