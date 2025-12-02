package com.climasys.service;

import com.climasys.entity.PrescriptionMedicines;
import com.climasys.entity.PrescriptionMedicinesId;
import com.climasys.repository.PrescriptionMedicinesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for PrescriptionMedicines business logic
 * Provides methods for managing prescription medicines master data (Prescription Details)
 */
@Service
@Transactional
public class PrescriptionMedicinesService {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionMedicinesService.class);

    @Autowired
    private PrescriptionMedicinesRepository prescriptionMedicinesRepository;

    /**
     * Get all prescription medicines for a specific doctor
     * @param doctorId Doctor ID
     * @return List of prescription medicines for the doctor
     */
    @Transactional(readOnly = true)
    public List<PrescriptionMedicines> getAllPrescriptionMedicinesForDoctor(String doctorId) {
        logger.info("Getting all prescription medicines for doctor: {}", doctorId);
        return prescriptionMedicinesRepository.findByDoctorIdOrderByPriorityValueAscCatShortNameAscCatsubDescriptionAsc(doctorId);
    }

    /**
     * Get all active prescription medicines for a specific doctor
     * @param doctorId Doctor ID
     * @return List of active prescription medicines for the doctor
     */
    @Transactional(readOnly = true)
    public List<PrescriptionMedicines> getActivePrescriptionMedicinesForDoctor(String doctorId) {
        logger.info("Getting active prescription medicines for doctor: {}", doctorId);
        return prescriptionMedicinesRepository.findByDoctorIdAndActiveOrderByPriorityValueAscCatShortNameAscCatsubDescriptionAsc(doctorId, true);
    }

    /**
     * Get prescription medicines by category and doctor
     * @param catShortName Category short name
     * @param doctorId Doctor ID
     * @return List of prescription medicines for the category and doctor
     */
    @Transactional(readOnly = true)
    public List<PrescriptionMedicines> getPrescriptionMedicinesByCategory(String catShortName, String doctorId) {
        logger.info("Getting prescription medicines for category: {} and doctor: {}", catShortName, doctorId);
        return prescriptionMedicinesRepository.findByCatShortNameAndDoctorIdOrderByPriorityValueAsc(catShortName, doctorId);
    }

    /**
     * Get prescription medicines by category and subcategory for a specific doctor
     * @param catShortName Category short name
     * @param catsubDescription Subcategory description
     * @param doctorId Doctor ID
     * @return List of prescription medicines for the category, subcategory, and doctor
     */
    @Transactional(readOnly = true)
    public List<PrescriptionMedicines> getPrescriptionMedicinesByCategoryAndSubCategory(String catShortName, String catsubDescription, String doctorId) {
        logger.info("Getting prescription medicines for category: {}, subcategory: {} and doctor: {}", catShortName, catsubDescription, doctorId);
        return prescriptionMedicinesRepository.findByCatShortNameAndCatsubDescriptionAndDoctorIdOrderByPriorityValueAsc(catShortName, catsubDescription, doctorId);
    }

    /**
     * Get a prescription medicine by all key fields
     * @param catShortName Category short name
     * @param catsubDescription Subcategory description
     * @param medicineName Medicine name
     * @param brandName Brand name
     * @param doctorId Doctor ID
     * @return Optional prescription medicine
     */
    @Transactional(readOnly = true)
    public Optional<PrescriptionMedicines> getPrescriptionMedicine(String catShortName, String catsubDescription, 
                                                                    String medicineName, String brandName, String doctorId) {
        logger.info("Getting prescription medicine: {} - {} for category: {}, subcategory: {} and doctor: {}", 
                medicineName, brandName, catShortName, catsubDescription, doctorId);
        PrescriptionMedicines medicine = prescriptionMedicinesRepository.findByCatShortNameAndCatsubDescriptionAndMedicineNameAndBrandNameAndDoctorId(
                catShortName, catsubDescription, medicineName, brandName, doctorId);
        return Optional.ofNullable(medicine);
    }

    /**
     * Search prescription medicines by category, subcategory, medicine name, brand name, or priority for a specific doctor
     * @param doctorId Doctor ID
     * @param searchTerm Search term to match against various fields
     * @return List of matching prescription medicines
     */
    @Transactional(readOnly = true)
    public List<PrescriptionMedicines> searchPrescriptionMedicinesByDescription(String doctorId, String searchTerm) {
        logger.info("Searching prescription medicines for doctor: {} with term: {}", doctorId, searchTerm);
        return prescriptionMedicinesRepository.searchPrescriptionMedicinesByDescription(doctorId, searchTerm);
    }

    /**
     * Create a new prescription medicine
     * @param prescriptionMedicine Prescription medicine to create
     * @return Created prescription medicine
     */
    public PrescriptionMedicines createPrescriptionMedicine(PrescriptionMedicines prescriptionMedicine) {
        logger.info("Creating new prescription medicine: {} - {} for category: {}, subcategory: {}", 
                prescriptionMedicine.getMedicineName(), prescriptionMedicine.getBrandName(), 
                prescriptionMedicine.getCatShortName(), prescriptionMedicine.getCatsubDescription());
        
        // Check if prescription medicine already exists
        if (prescriptionMedicinesRepository.existsByCatShortNameAndCatsubDescriptionAndMedicineNameAndBrandNameAndDoctorId(
                prescriptionMedicine.getCatShortName(), prescriptionMedicine.getCatsubDescription(),
                prescriptionMedicine.getMedicineName(), prescriptionMedicine.getBrandName(), prescriptionMedicine.getDoctorId())) {
            throw new RuntimeException("Prescription medicine with medicine name '" + prescriptionMedicine.getMedicineName() + 
                    "' and brand name '" + prescriptionMedicine.getBrandName() + 
                    "' already exists for category " + prescriptionMedicine.getCatShortName() + 
                    ", subcategory " + prescriptionMedicine.getCatsubDescription() + 
                    " and doctor " + prescriptionMedicine.getDoctorId());
        }
        
        // Set creation timestamp
        prescriptionMedicine.setCreatedOn(LocalDateTime.now());
        prescriptionMedicine.setModifiedOn(LocalDateTime.now());
        
        // Set active to true by default if not set
        if (prescriptionMedicine.getActive() == null) {
            prescriptionMedicine.setActive(true);
        }
        
        return prescriptionMedicinesRepository.save(prescriptionMedicine);
    }

    /**
     * Update an existing prescription medicine
     * @param prescriptionMedicine Prescription medicine to update
     * @return Updated prescription medicine
     */
    public PrescriptionMedicines updatePrescriptionMedicine(PrescriptionMedicines prescriptionMedicine) {
        logger.info("Updating prescription medicine: {} - {}", prescriptionMedicine.getMedicineName(), prescriptionMedicine.getBrandName());
        
        // Set modification timestamp
        prescriptionMedicine.setModifiedOn(LocalDateTime.now());
        
        return prescriptionMedicinesRepository.save(prescriptionMedicine);
    }

    /**
     * Delete a prescription medicine
     * @param catShortName Category short name
     * @param catsubDescription Subcategory description
     * @param medicineName Medicine name
     * @param brandName Brand name
     * @param doctorId Doctor ID
     * @return True if deleted successfully
     */
    public boolean deletePrescriptionMedicine(String catShortName, String catsubDescription, 
                                              String medicineName, String brandName, String doctorId) {
        logger.info("Deleting prescription medicine: {} - {} for category: {}, subcategory: {} and doctor: {}", 
                medicineName, brandName, catShortName, catsubDescription, doctorId);
        
        Optional<PrescriptionMedicines> medicineOpt = getPrescriptionMedicine(catShortName, catsubDescription, medicineName, brandName, doctorId);
        if (medicineOpt.isPresent()) {
            PrescriptionMedicinesId id = new PrescriptionMedicinesId(catShortName, catsubDescription, medicineName, brandName, doctorId);
            prescriptionMedicinesRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

