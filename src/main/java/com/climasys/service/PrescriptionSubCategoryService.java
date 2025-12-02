package com.climasys.service;

import com.climasys.entity.PrescriptionSubCategory;
import com.climasys.entity.PrescriptionSubCategoryId;
import com.climasys.repository.PrescriptionSubCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for PrescriptionSubCategory business logic
 * Provides methods for managing prescription subcategory master data
 */
@Service
@Transactional
public class PrescriptionSubCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionSubCategoryService.class);

    @Autowired
    private PrescriptionSubCategoryRepository prescriptionSubCategoryRepository;

    /**
     * Get all subcategories for a specific doctor
     * @param doctorId Doctor ID
     * @return List of subcategories for the doctor
     */
    @Transactional(readOnly = true)
    public List<PrescriptionSubCategory> getAllSubCategoriesForDoctor(String doctorId) {
        logger.info("Getting all subcategories for doctor: {}", doctorId);
        return prescriptionSubCategoryRepository.findByDoctorIdOrderByCatShortNameAscCatsubDescriptionAsc(doctorId);
    }

    /**
     * Get all subcategories for a specific category and doctor
     * @param catShortName Category short name
     * @param doctorId Doctor ID
     * @return List of subcategories for the category and doctor
     */
    @Transactional(readOnly = true)
    public List<PrescriptionSubCategory> getSubCategoriesByCategory(String catShortName, String doctorId) {
        logger.info("Getting subcategories for category: {} and doctor: {}", catShortName, doctorId);
        return prescriptionSubCategoryRepository.findByCatShortNameAndDoctorIdOrderByCatsubDescriptionAsc(catShortName, doctorId);
    }

    /**
     * Get a subcategory by category, subcategory description, and doctor ID
     * @param catShortName Category short name
     * @param catsubDescription Subcategory description
     * @param doctorId Doctor ID
     * @return Optional subcategory
     */
    @Transactional(readOnly = true)
    public Optional<PrescriptionSubCategory> getSubCategoryByDescription(String catShortName, String catsubDescription, String doctorId) {
        logger.info("Getting subcategory by description: {} for category: {} and doctor: {}", catsubDescription, catShortName, doctorId);
        PrescriptionSubCategory subCategory = prescriptionSubCategoryRepository.findByCatShortNameAndCatsubDescriptionAndDoctorId(
                catShortName, catsubDescription, doctorId);
        return Optional.ofNullable(subCategory);
    }

    /**
     * Search subcategories by category name or subcategory description for a specific doctor
     * @param doctorId Doctor ID
     * @param searchTerm Search term to match against category name or subcategory description
     * @return List of matching subcategories
     */
    @Transactional(readOnly = true)
    public List<PrescriptionSubCategory> searchSubCategoriesByDescription(String doctorId, String searchTerm) {
        logger.info("Searching subcategories for doctor: {} with term: {}", doctorId, searchTerm);
        return prescriptionSubCategoryRepository.searchSubCategoriesByDescription(doctorId, searchTerm);
    }

    /**
     * Create a new subcategory
     * @param subCategory Subcategory to create
     * @return Created subcategory
     */
    public PrescriptionSubCategory createSubCategory(PrescriptionSubCategory subCategory) {
        logger.info("Creating new subcategory: {} for category: {}", subCategory.getCatsubDescription(), subCategory.getCatShortName());
        
        // Check if subcategory already exists
        if (prescriptionSubCategoryRepository.existsByCatShortNameAndCatsubDescriptionAndDoctorId(
                subCategory.getCatShortName(), subCategory.getCatsubDescription(), subCategory.getDoctorId())) {
            throw new RuntimeException("Subcategory with description '" + subCategory.getCatsubDescription() + 
                    "' already exists for category " + subCategory.getCatShortName() + " and doctor " + subCategory.getDoctorId());
        }
        
        // Set creation timestamp
        subCategory.setCreatedOn(LocalDateTime.now());
        subCategory.setModifiedOn(LocalDateTime.now());
        
        return prescriptionSubCategoryRepository.save(subCategory);
    }

    /**
     * Update an existing subcategory
     * @param subCategory Subcategory to update
     * @return Updated subcategory
     */
    public PrescriptionSubCategory updateSubCategory(PrescriptionSubCategory subCategory) {
        logger.info("Updating subcategory: {} for category: {}", subCategory.getCatsubDescription(), subCategory.getCatShortName());
        
        // Set modification timestamp
        subCategory.setModifiedOn(LocalDateTime.now());
        
        return prescriptionSubCategoryRepository.save(subCategory);
    }

    /**
     * Delete a subcategory
     * @param catShortName Category short name
     * @param catsubDescription Subcategory description
     * @param doctorId Doctor ID
     * @return True if deleted successfully
     */
    public boolean deleteSubCategory(String catShortName, String catsubDescription, String doctorId) {
        logger.info("Deleting subcategory: {} for category: {} and doctor: {}", catsubDescription, catShortName, doctorId);
        
        Optional<PrescriptionSubCategory> subCategoryOpt = getSubCategoryByDescription(catShortName, catsubDescription, doctorId);
        if (subCategoryOpt.isPresent()) {
            PrescriptionSubCategoryId id = new PrescriptionSubCategoryId(catShortName, catsubDescription, doctorId);
            prescriptionSubCategoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

