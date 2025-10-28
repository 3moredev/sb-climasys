package com.climasys.repository;

import com.climasys.entity.MedicineMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository for MedicineMaster entity
 * Provides data access methods for medicine master data
 */
@Repository
public interface MedicineMasterRepository extends JpaRepository<MedicineMaster, String> {

    /**
     * Find all active medicines for a specific doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param active Active status
     * @return List of active medicines for the doctor and clinic
     */
    List<MedicineMaster> findByDoctorIdAndClinicIdAndActiveOrderByPriorityValueAscShortDescriptionAsc(String doctorId, String clinicId, Boolean active);

    /**
     * Find all medicines for a specific doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return List of medicines for the doctor and clinic
     */
    List<MedicineMaster> findByDoctorIdAndClinicIdOrderByPriorityValueAscShortDescriptionAsc(String doctorId, String clinicId);

    /**
     * Find all active medicines for a specific clinic (backward compatibility)
     * @param clinicId Clinic ID
     * @return List of active medicines for the clinic
     */
    List<MedicineMaster> findByClinicIdAndActiveOrderByPriorityValueAscShortDescriptionAsc(String clinicId, Boolean active);

    /**
     * Find all medicines for a specific clinic (backward compatibility)
     * @param clinicId Clinic ID
     * @return List of medicines for the clinic
     */
    List<MedicineMaster> findByClinicIdOrderByPriorityValueAscShortDescriptionAsc(String clinicId);

    /**
     * Get medicine data in the same format as the stored procedure USP_Get_PatientProfileRefData for doctor and clinic
     * Returns data with concatenated ID field for backward compatibility
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return List of medicine data with formatted ID field
     */
    @Query(value = """
        SELECT 
            TRIM(short_description) || '*' || TRIM(medicine_description) AS id,
            TRIM(short_description) AS short_description,
            TRIM(medicine_description) AS medicine_description,
            priority_value,
            active,
            morning,
            afternoon
        FROM medicine_master 
        WHERE doctor_id = :doctorId AND clinic_id = :clinicId
        AND COALESCE(active, true) = true
        ORDER BY priority_value ASC, short_description ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findMedicinesFormattedByDoctorAndClinic(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

    /**
     * Get medicine data in the same format as the stored procedure USP_Get_PatientProfileRefData for clinic
     * Returns data with concatenated ID field for backward compatibility
     * @param clinicId Clinic ID
     * @return List of medicine data with formatted ID field
     */
    @Query(value = """
        SELECT 
            TRIM(short_description) || '*' || TRIM(medicine_description) AS id,
            TRIM(short_description) AS short_description,
            TRIM(medicine_description) AS medicine_description,
            priority_value,
            active,
            morning,
            afternoon
        FROM medicine_master 
        WHERE clinic_id = :clinicId
        AND COALESCE(active, true) = true
        ORDER BY priority_value ASC, short_description ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findMedicinesFormattedByClinic(@Param("clinicId") String clinicId);

    /**
     * Get medicine data in the same format as the stored procedure USP_Get_PatientProfileRefData (backward compatibility)
     * Returns data with concatenated ID field for backward compatibility
     * @return List of medicine data with formatted ID field
     */
    @Query(value = """
        SELECT 
            TRIM(short_description) || '*' || TRIM(medicine_description) AS id,
            TRIM(short_description) AS short_description,
            TRIM(medicine_description) AS medicine_description,
            priority_value,
            active,
            morning,
            afternoon
        FROM medicine_master 
        WHERE COALESCE(active, true) = true
        ORDER BY priority_value ASC, short_description ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findMedicinesFormatted();

    /**
     * Check if medicine exists for doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param shortDescription Short description
     * @return true if exists, false otherwise
     */
    boolean existsByDoctorIdAndClinicIdAndShortDescription(String doctorId, String clinicId, String shortDescription);

    /**
     * Check if medicine exists for clinic
     * @param clinicId Clinic ID
     * @param shortDescription Short description
     * @return true if exists, false otherwise
     */
    boolean existsByClinicIdAndShortDescription(String clinicId, String shortDescription);

    /**
     * Check if medicine exists (backward compatibility)
     * @param shortDescription Short description
     * @return true if exists, false otherwise
     */
    boolean existsByShortDescription(String shortDescription);
}
