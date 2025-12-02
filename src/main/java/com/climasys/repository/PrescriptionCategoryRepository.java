package com.climasys.repository;

import com.climasys.entity.PrescriptionCategory;
import com.climasys.entity.PrescriptionCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for PrescriptionCategory entity
 * Provides data access methods for prescription category master data
 */
@Repository
public interface PrescriptionCategoryRepository extends JpaRepository<PrescriptionCategory, PrescriptionCategoryId> {

    /**
     * Find all categories for a specific doctor
     * @param doctorId Doctor ID
     * @return List of categories for the doctor
     */
    List<PrescriptionCategory> findByDoctorIdOrderByCatShortNameAsc(String doctorId);

    /**
     * Find category by short name and doctor ID
     * @param catShortName Category short name
     * @param doctorId Doctor ID
     * @return Category if found
     */
    PrescriptionCategory findByCatShortNameAndDoctorId(String catShortName, String doctorId);

    /**
     * Check if category exists for doctor
     * @param catShortName Category short name
     * @param doctorId Doctor ID
     * @return true if exists, false otherwise
     */
    boolean existsByCatShortNameAndDoctorId(String catShortName, String doctorId);

    /**
     * Search categories by short name or long description for a specific doctor
     * @param doctorId Doctor ID
     * @param searchTerm Search term to match against short name or long description
     * @return List of matching categories
     */
    @Query("SELECT pc FROM PrescriptionCategory pc WHERE pc.doctorId = :doctorId AND " +
           "(LOWER(pc.catShortName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(pc.catLongDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY pc.catShortName ASC")
    List<PrescriptionCategory> searchCategoriesByDescription(@Param("doctorId") String doctorId, @Param("searchTerm") String searchTerm);
}

