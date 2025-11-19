package com.climasys.repository;

import com.climasys.entity.ComplaintMaster;
import com.climasys.entity.ComplaintMasterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository for ComplaintMaster entity
 * Provides data access methods for complaint master data
 */
@Repository
public interface ComplaintMasterRepository extends JpaRepository<ComplaintMaster, ComplaintMasterId> {

    /**
     * Find all complaints for a specific doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return List of complaints for the doctor and clinic
     */
    List<ComplaintMaster> findByDoctorIdAndClinicIdOrderByPriorityValueAscShortDescriptionAsc(String doctorId, String clinicId);

    /**
     * Find all complaints for a specific doctor (backward compatibility)
     * @param doctorId Doctor ID
     * @return List of complaints for the doctor
     */
    List<ComplaintMaster> findByDoctorIdOrderByPriorityValueAscShortDescriptionAsc(String doctorId);

    /**
     * Find all complaints for a specific doctor that are displayed to operators
     * @param doctorId Doctor ID
     * @return List of complaints that operators can see
     */
    List<ComplaintMaster> findByDoctorIdAndDisplayToOperatorOrderByPriorityValueAscShortDescriptionAsc(String doctorId, Short displayToOperator);

    /**
     * Find complaints for operator display (display_to_operator = 1) for doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return List of complaints visible to operators
     */
    @Query("SELECT cm FROM ComplaintMaster cm WHERE cm.doctorId = :doctorId AND cm.clinicId = :clinicId AND cm.displayToOperator = 1 ORDER BY cm.priorityValue ASC, cm.shortDescription ASC")
    List<ComplaintMaster> findComplaintsForOperatorDisplayByDoctorAndClinic(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

    /**
     * Find complaints for operator display (display_to_operator = 1) (backward compatibility)
     * @param doctorId Doctor ID
     * @return List of complaints visible to operators
     */
    @Query("SELECT cm FROM ComplaintMaster cm WHERE cm.doctorId = :doctorId AND cm.displayToOperator = 1 ORDER BY cm.priorityValue ASC, cm.shortDescription ASC")
    List<ComplaintMaster> findComplaintsForOperatorDisplay(@Param("doctorId") String doctorId);

    /**
     * Get complaint data in the same format as the stored procedure USP_Get_PatientProfileRefData for doctor and clinic
     * Returns data with concatenated ID field for backward compatibility
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return List of complaint data with formatted ID field
     */
    @Query(value = """
        SELECT 
            TRIM(short_description) || '*' || TRIM(complaint_description) AS id,
            TRIM(short_description) AS short_description,
            TRIM(complaint_description) AS complaint_description,
            priority_value,
            display_to_operator
        FROM complaint_master 
        WHERE doctor_id = :doctorId 
        AND clinic_id = :clinicId
        AND COALESCE(display_to_operator, 0) = 1
        ORDER BY priority_value ASC, short_description ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findComplaintsForOperatorDisplayFormattedByDoctorAndClinic(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

    /**
     * Get complaint data in the same format as the stored procedure USP_Get_PatientProfileRefData (backward compatibility)
     * Returns data with concatenated ID field for backward compatibility
     * @param doctorId Doctor ID
     * @return List of complaint data with formatted ID field
     */
    @Query(value = """
        SELECT 
            TRIM(short_description) || '*' || TRIM(complaint_description) AS id,
            TRIM(short_description) AS short_description,
            TRIM(complaint_description) AS complaint_description,
            priority_value,
            display_to_operator
        FROM complaint_master 
        WHERE doctor_id = :doctorId 
        AND COALESCE(display_to_operator, 0) = 1
        ORDER BY priority_value ASC, short_description ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findComplaintsForOperatorDisplayFormatted(@Param("doctorId") String doctorId);

    /**
     * Get all complaint data for a clinic (including non-operator visible)
     * @param clinicId Clinic ID (mandatory)
     * @param doctorId Doctor ID (optional)
     * @return List of all complaint data for the clinic and optionally doctor
     */
    @Query(value = """
        SELECT 
            TRIM(short_description) || '*' || TRIM(complaint_description) AS id,
            TRIM(short_description) AS short_description,
            TRIM(complaint_description) AS complaint_description,
            priority_value,
            COALESCE(display_to_operator, 0) AS display_to_operator
        FROM complaint_master 
        WHERE clinic_id = :clinicId 
        AND (:doctorId IS NULL OR doctor_id = :doctorId)
        ORDER BY priority_value ASC, short_description ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findAllComplaintsForDoctorFormatted(@Param("clinicId") String clinicId, @Param("doctorId") String doctorId);

    /**
     * Search complaints by description for a specific doctor
     * @param doctorId Doctor ID
     * @param searchTerm Search term to match against short or full description
     * @return List of matching complaints
     */
    @Query("SELECT cm FROM ComplaintMaster cm WHERE cm.doctorId = :doctorId AND " +
           "(LOWER(cm.shortDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(cm.complaintDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY cm.priorityValue ASC, cm.shortDescription ASC")
    List<ComplaintMaster> searchComplaintsByDescription(@Param("doctorId") String doctorId, @Param("searchTerm") String searchTerm);

    /**
     * Search complaints for operator display by description
     * @param doctorId Doctor ID
     * @param searchTerm Search term to match against short or full description
     * @return List of matching complaints visible to operators
     */
    @Query("SELECT cm FROM ComplaintMaster cm WHERE cm.doctorId = :doctorId AND cm.displayToOperator = 1 AND " +
           "(LOWER(cm.shortDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(cm.complaintDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY cm.priorityValue ASC, cm.shortDescription ASC")
    List<ComplaintMaster> searchComplaintsForOperatorDisplay(@Param("doctorId") String doctorId, @Param("searchTerm") String searchTerm);

    /**
     * Count complaints for a doctor
     * @param doctorId Doctor ID
     * @return Number of complaints for the doctor
     */
    long countByDoctorId(String doctorId);

    /**
     * Count complaints for operator display for a doctor
     * @param doctorId Doctor ID
     * @return Number of complaints visible to operators
     */
    long countByDoctorIdAndDisplayToOperator(String doctorId, Short displayToOperator);

    /**
     * Check if a complaint exists for a doctor by short description
     * @param doctorId Doctor ID
     * @param shortDescription Short description to check
     * @return True if complaint exists
     */
    boolean existsByDoctorIdAndShortDescription(String doctorId, String shortDescription);
}
