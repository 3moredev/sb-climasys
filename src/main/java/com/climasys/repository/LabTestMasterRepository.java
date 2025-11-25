package com.climasys.repository;

import com.climasys.entity.LabTestMaster;
import com.climasys.entity.LabTestMasterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for LabTestMaster entity
 * Provides JPA methods to replace USP_Get_LabTest stored procedure functionality
 * 
 * Note: The actual database table doesn't have an is_active column, so we removed those filters
 */
@Repository
public interface LabTestMasterRepository extends JpaRepository<LabTestMaster, LabTestMasterId> {
    
    /**
     * Get lab tests by doctor ID and clinic ID, ordered by priority and description
     * This replaces the main query from USP_Get_LabTest stored procedure
     * 
     * @param doctorId Doctor ID to filter lab tests
     * @param clinicId Clinic ID to filter lab tests
     * @return List of lab tests ordered by priority value and description
     */
    @Query("SELECT ltm FROM LabTestMaster ltm WHERE ltm.doctorId = :doctorId AND ltm.clinicId = :clinicId ORDER BY ltm.priorityValue ASC, ltm.labTestDescription ASC")
    List<LabTestMaster> findByDoctorIdAndClinicIdOrderByPriorityAndDescription(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);
    
    /**
     * Get all lab tests ordered by priority and description
     * 
     * @return List of all lab tests
     */
    @Query("SELECT ltm FROM LabTestMaster ltm ORDER BY ltm.priorityValue ASC, ltm.labTestDescription ASC")
    List<LabTestMaster> findAllOrderByPriorityAndDescription();
    
    /**
     * Get lab tests by doctor ID, clinic ID and description pattern
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param descriptionPattern Description pattern (with wildcards)
     * @return List of matching lab tests
     */
    @Query("SELECT ltm FROM LabTestMaster ltm WHERE ltm.doctorId = :doctorId AND ltm.clinicId = :clinicId AND ltm.labTestDescription LIKE :descriptionPattern ORDER BY ltm.priorityValue ASC, ltm.labTestDescription ASC")
    List<LabTestMaster> findByDoctorIdAndClinicIdAndDescriptionLike(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId, @Param("descriptionPattern") String descriptionPattern);
    
    /**
     * Check if lab test exists for doctor and clinic
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param labTestDescription Lab test description
     * @return true if exists, false otherwise
     */
    @Query("SELECT COUNT(ltm) > 0 FROM LabTestMaster ltm WHERE ltm.doctorId = :doctorId AND ltm.clinicId = :clinicId AND ltm.labTestDescription = :labTestDescription")
    boolean existsByDoctorIdAndClinicIdAndDescription(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId, @Param("labTestDescription") String labTestDescription);
    
    /**
     * Get lab test by doctor ID, clinic ID and description
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param labTestDescription Lab test description
     * @return LabTestMaster entity or null
     */
    @Query("SELECT ltm FROM LabTestMaster ltm WHERE ltm.doctorId = :doctorId AND ltm.clinicId = :clinicId AND ltm.labTestDescription = :labTestDescription")
    LabTestMaster findByDoctorIdAndClinicIdAndDescription(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId, @Param("labTestDescription") String labTestDescription);
    
    /**
     * Get distinct doctor IDs that have lab tests
     * 
     * @return List of doctor IDs
     */
    @Query("SELECT DISTINCT ltm.doctorId FROM LabTestMaster ltm ORDER BY ltm.doctorId")
    List<String> findDistinctDoctorIds();
    
    /**
     * Count lab tests by doctor ID and clinic ID
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return Count of lab tests for the doctor and clinic
     */
    @Query("SELECT COUNT(ltm) FROM LabTestMaster ltm WHERE ltm.doctorId = :doctorId AND ltm.clinicId = :clinicId")
    long countByDoctorIdAndClinicId(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);
    
    /**
     * Get lab tests by group name for doctor and clinic
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param groupName Group name
     * @return List of lab tests in the group
     */
    @Query("SELECT ltm FROM LabTestMaster ltm WHERE ltm.doctorId = :doctorId AND ltm.clinicId = :clinicId AND ltm.groupName = :groupName ORDER BY ltm.priorityValue ASC, ltm.labTestDescription ASC")
    List<LabTestMaster> findByDoctorIdAndClinicIdAndGroupName(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId, @Param("groupName") String groupName);
    
    /**
     * Get the maximum ID for a specific doctor and clinic
     * Used to auto-generate the next ID when creating a new lab test
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return Maximum ID or null if no records exist
     */
    @Query("SELECT MAX(ltm.id) FROM LabTestMaster ltm WHERE ltm.doctorId = :doctorId AND ltm.clinicId = :clinicId")
    Integer findMaxIdByDoctorIdAndClinicId(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);
}
