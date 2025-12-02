package com.climasys.repository;

import com.climasys.entity.PrescriptionMedicines;
import com.climasys.entity.PrescriptionMedicinesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for PrescriptionMedicines entity
 * Provides data access methods for prescription medicines master data (Prescription Details)
 */
@Repository
public interface PrescriptionMedicinesRepository extends JpaRepository<PrescriptionMedicines, PrescriptionMedicinesId> {

    /**
     * Find all prescription medicines for a specific doctor
     * @param doctorId Doctor ID
     * @return List of prescription medicines for the doctor
     */
    List<PrescriptionMedicines> findByDoctorIdOrderByPriorityValueAscCatShortNameAscCatsubDescriptionAsc(String doctorId);

    /**
     * Find all active prescription medicines for a specific doctor
     * @param doctorId Doctor ID
     * @param active Active status
     * @return List of active prescription medicines for the doctor
     */
    List<PrescriptionMedicines> findByDoctorIdAndActiveOrderByPriorityValueAscCatShortNameAscCatsubDescriptionAsc(String doctorId, Boolean active);

    /**
     * Find prescription medicines by category and doctor
     * @param catShortName Category short name
     * @param doctorId Doctor ID
     * @return List of prescription medicines for the category and doctor
     */
    List<PrescriptionMedicines> findByCatShortNameAndDoctorIdOrderByPriorityValueAsc(String catShortName, String doctorId);

    /**
     * Find prescription medicines by category and subcategory for a specific doctor
     * @param catShortName Category short name
     * @param catsubDescription Subcategory description
     * @param doctorId Doctor ID
     * @return List of prescription medicines for the category, subcategory, and doctor
     */
    List<PrescriptionMedicines> findByCatShortNameAndCatsubDescriptionAndDoctorIdOrderByPriorityValueAsc(String catShortName, String catsubDescription, String doctorId);

    /**
     * Find prescription medicine by all key fields
     * @param catShortName Category short name
     * @param catsubDescription Subcategory description
     * @param medicineName Medicine name
     * @param brandName Brand name
     * @param doctorId Doctor ID
     * @return Prescription medicine if found
     */
    PrescriptionMedicines findByCatShortNameAndCatsubDescriptionAndMedicineNameAndBrandNameAndDoctorId(
            String catShortName, String catsubDescription, String medicineName, String brandName, String doctorId);

    /**
     * Check if prescription medicine exists
     * @param catShortName Category short name
     * @param catsubDescription Subcategory description
     * @param medicineName Medicine name
     * @param brandName Brand name
     * @param doctorId Doctor ID
     * @return true if exists, false otherwise
     */
    boolean existsByCatShortNameAndCatsubDescriptionAndMedicineNameAndBrandNameAndDoctorId(
            String catShortName, String catsubDescription, String medicineName, String brandName, String doctorId);

    /**
     * Search prescription medicines by category, subcategory, medicine name, brand name, or priority for a specific doctor
     * @param doctorId Doctor ID
     * @param searchTerm Search term to match against various fields
     * @return List of matching prescription medicines
     */
    @Query("SELECT pm FROM PrescriptionMedicines pm WHERE pm.doctorId = :doctorId AND " +
           "(LOWER(pm.catShortName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(pm.catsubDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(pm.medicineName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(pm.brandName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(pm.priorityValue AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY pm.priorityValue ASC, pm.catShortName ASC, pm.catsubDescription ASC")
    List<PrescriptionMedicines> searchPrescriptionMedicinesByDescription(@Param("doctorId") String doctorId, @Param("searchTerm") String searchTerm);
}

