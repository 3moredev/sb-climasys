package com.climasys.repository;

import com.climasys.entity.DiagnosisMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository for DiagnosisMaster entity
 * Provides data access methods for diagnosis master data
 */
@Repository
public interface DiagnosisMasterRepository extends JpaRepository<DiagnosisMaster, String> {

    /**
     * Find all diagnoses for a specific doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return List of diagnoses for the doctor and clinic
     */
    List<DiagnosisMaster> findByDoctorIdAndClinicIdOrderByPriorityValueAscShortDescriptionAsc(String doctorId, String clinicId);

    /**
     * Find all diagnoses for a specific doctor (backward compatibility)
     * @param doctorId Doctor ID
     * @return List of diagnoses for the doctor
     */
    List<DiagnosisMaster> findByDoctorIdOrderByPriorityValueAscShortDescriptionAsc(String doctorId);


    /**
     * Get diagnosis data in the same format as the stored procedure USP_Get_PatientProfileRefData for doctor and clinic
     * Returns data with concatenated ID field for backward compatibility
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return List of diagnosis data with formatted ID field
     */
    @Query(value = """
        SELECT 
            TRIM(short_description) || '*' || TRIM(diagnosis_description) AS id,
            TRIM(short_description) AS short_description,
            TRIM(diagnosis_description) AS diagnosis_description,
            priority_value
        FROM diagnosis_master 
        WHERE doctor_id = :doctorId 
        AND clinic_id = :clinicId
        ORDER BY priority_value ASC, short_description ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findDiagnosesFormattedByDoctorAndClinic(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

    /**
     * Get diagnosis data in the same format as the stored procedure USP_Get_PatientProfileRefData (backward compatibility)
     * Returns data with concatenated ID field for backward compatibility
     * @param doctorId Doctor ID
     * @return List of diagnosis data with formatted ID field
     */
    @Query(value = """
        SELECT 
            TRIM(short_description) || '*' || TRIM(diagnosis_description) AS id,
            TRIM(short_description) AS short_description,
            TRIM(diagnosis_description) AS diagnosis_description,
            priority_value
        FROM diagnosis_master 
        WHERE doctor_id = :doctorId 
        ORDER BY priority_value ASC, short_description ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findDiagnosesFormatted(@Param("doctorId") String doctorId);

    /**
     * Check if diagnosis exists for doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param shortDescription Short description
     * @return true if exists, false otherwise
     */
    boolean existsByDoctorIdAndClinicIdAndShortDescription(String doctorId, String clinicId, String shortDescription);

    /**
     * Check if diagnosis exists for doctor (backward compatibility)
     * @param doctorId Doctor ID
     * @param shortDescription Short description
     * @return true if exists, false otherwise
     */
    boolean existsByDoctorIdAndShortDescription(String doctorId, String shortDescription);
}
