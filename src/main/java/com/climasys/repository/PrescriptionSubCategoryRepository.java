package com.climasys.repository;

import com.climasys.entity.PrescriptionSubCategory;
import com.climasys.entity.PrescriptionSubCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for PrescriptionSubCategory entity
 * Provides data access methods for prescription subcategory master data
 */
@Repository
public interface PrescriptionSubCategoryRepository extends JpaRepository<PrescriptionSubCategory, PrescriptionSubCategoryId> {

    /**
     * Find all subcategories for a specific doctor
     * @param doctorId Doctor ID
     * @return List of subcategories for the doctor
     */
    List<PrescriptionSubCategory> findByDoctorIdOrderByCatShortNameAscCatsubDescriptionAsc(String doctorId);

    /**
     * Find all subcategories for a specific category and doctor
     * @param catShortName Category short name
     * @param doctorId Doctor ID
     * @return List of subcategories for the category and doctor
     */
    List<PrescriptionSubCategory> findByCatShortNameAndDoctorIdOrderByCatsubDescriptionAsc(String catShortName, String doctorId);

    /**
     * Find subcategory by category, subcategory description, and doctor ID
     * @param catShortName Category short name
     * @param catsubDescription Subcategory description
     * @param doctorId Doctor ID
     * @return Subcategory if found
     */
    PrescriptionSubCategory findByCatShortNameAndCatsubDescriptionAndDoctorId(String catShortName, String catsubDescription, String doctorId);

    /**
     * Check if subcategory exists for doctor
     * @param catShortName Category short name
     * @param catsubDescription Subcategory description
     * @param doctorId Doctor ID
     * @return true if exists, false otherwise
     */
    boolean existsByCatShortNameAndCatsubDescriptionAndDoctorId(String catShortName, String catsubDescription, String doctorId);

    /**
     * Search subcategories by category name or subcategory description for a specific doctor
     * @param doctorId Doctor ID
     * @param searchTerm Search term to match against category name or subcategory description
     * @return List of matching subcategories
     */
    @Query("SELECT psc FROM PrescriptionSubCategory psc WHERE psc.doctorId = :doctorId AND " +
           "(LOWER(psc.catShortName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(psc.catsubDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY psc.catShortName ASC, psc.catsubDescription ASC")
    List<PrescriptionSubCategory> searchSubCategoriesByDescription(@Param("doctorId") String doctorId, @Param("searchTerm") String searchTerm);
}

