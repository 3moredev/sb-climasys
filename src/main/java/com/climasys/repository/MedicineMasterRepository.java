package com.climasys.repository;

import com.climasys.entity.MedicineMaster;
import com.climasys.entity.MedicineMasterId;
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
public interface MedicineMasterRepository extends JpaRepository<MedicineMaster, MedicineMasterId> {

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
            afternoon,
            night,
            no_of_days,
            instruction
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
            afternoon,
            night,
            no_of_days,
            instruction
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
            afternoon,
            night,
            no_of_days,
            instruction
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

    /**
     * Find all medicines for a specific doctor (backward compatibility)
     * @param doctorId Doctor ID
     * @return List of medicines for the doctor
     */
    List<MedicineMaster> findByDoctorIdOrderByPriorityValueAscShortDescriptionAsc(String doctorId);

    /**
     * Search medicines by short description, medicine description, or priority for a specific doctor
     * @param doctorId Doctor ID
     * @param searchTerm Search term to match against short description, medicine description, or priority
     * @return List of matching medicines
     */
    @Query("SELECT mm FROM MedicineMaster mm WHERE mm.doctorId = :doctorId AND " +
           "(LOWER(mm.shortDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(mm.medicineDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(mm.priorityValue AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY mm.priorityValue ASC, mm.shortDescription ASC")
    List<MedicineMaster> searchMedicinesByDescriptionOrPriority(@Param("doctorId") String doctorId, @Param("searchTerm") String searchTerm);

    /**
     * Search medicines by short description, medicine description, or priority for a specific doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param searchTerm Search term to match against short description, medicine description, or priority
     * @return List of matching medicines
     */
    @Query("SELECT mm FROM MedicineMaster mm WHERE mm.doctorId = :doctorId AND mm.clinicId = :clinicId AND " +
           "(LOWER(mm.shortDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(mm.medicineDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(mm.priorityValue AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY mm.priorityValue ASC, mm.shortDescription ASC")
    List<MedicineMaster> searchMedicinesByDescriptionOrPriorityAndClinic(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId, @Param("searchTerm") String searchTerm);

    /**
     * Check if medicine exists for doctor
     * @param doctorId Doctor ID
     * @param shortDescription Short description
     * @return true if exists, false otherwise
     */
    boolean existsByDoctorIdAndShortDescription(String doctorId, String shortDescription);
}
