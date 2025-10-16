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
     * Get lab tests by doctor ID, ordered by priority and description
     * This replaces the main query from USP_Get_LabTest stored procedure
     * 
     * @param doctorId Doctor ID to filter lab tests
     * @return List of lab tests ordered by priority value and description
     */
    @Query("SELECT ltm FROM LabTestMaster ltm WHERE ltm.doctorId = :doctorId ORDER BY ltm.priorityValue ASC, ltm.labTestDescription ASC")
    List<LabTestMaster> findByDoctorIdOrderByPriorityAndDescription(@Param("doctorId") String doctorId);
    
    /**
     * Get all lab tests ordered by priority and description
     * 
     * @return List of all lab tests
     */
    @Query("SELECT ltm FROM LabTestMaster ltm ORDER BY ltm.priorityValue ASC, ltm.labTestDescription ASC")
    List<LabTestMaster> findAllOrderByPriorityAndDescription();
    
    /**
     * Get lab tests by doctor ID and description pattern
     * 
     * @param doctorId Doctor ID
     * @param descriptionPattern Description pattern (with wildcards)
     * @return List of matching lab tests
     */
    @Query("SELECT ltm FROM LabTestMaster ltm WHERE ltm.doctorId = :doctorId AND ltm.labTestDescription LIKE :descriptionPattern ORDER BY ltm.priorityValue ASC, ltm.labTestDescription ASC")
    List<LabTestMaster> findByDoctorIdAndDescriptionLike(@Param("doctorId") String doctorId, @Param("descriptionPattern") String descriptionPattern);
    
    /**
     * Check if lab test exists for doctor
     * 
     * @param doctorId Doctor ID
     * @param labTestDescription Lab test description
     * @return true if exists, false otherwise
     */
    @Query("SELECT COUNT(ltm) > 0 FROM LabTestMaster ltm WHERE ltm.doctorId = :doctorId AND ltm.labTestDescription = :labTestDescription")
    boolean existsByDoctorIdAndDescription(@Param("doctorId") String doctorId, @Param("labTestDescription") String labTestDescription);
    
    /**
     * Get lab test by doctor ID and description
     * 
     * @param doctorId Doctor ID
     * @param labTestDescription Lab test description
     * @return LabTestMaster entity or null
     */
    @Query("SELECT ltm FROM LabTestMaster ltm WHERE ltm.doctorId = :doctorId AND ltm.labTestDescription = :labTestDescription")
    LabTestMaster findByDoctorIdAndDescription(@Param("doctorId") String doctorId, @Param("labTestDescription") String labTestDescription);
    
    /**
     * Get distinct doctor IDs that have lab tests
     * 
     * @return List of doctor IDs
     */
    @Query("SELECT DISTINCT ltm.doctorId FROM LabTestMaster ltm ORDER BY ltm.doctorId")
    List<String> findDistinctDoctorIds();
    
    /**
     * Count lab tests by doctor ID
     * 
     * @param doctorId Doctor ID
     * @return Count of lab tests for the doctor
     */
    @Query("SELECT COUNT(ltm) FROM LabTestMaster ltm WHERE ltm.doctorId = :doctorId")
    long countByDoctorId(@Param("doctorId") String doctorId);
    
    /**
     * Get lab tests by group name
     * 
     * @param doctorId Doctor ID
     * @param groupName Group name
     * @return List of lab tests in the group
     */
    @Query("SELECT ltm FROM LabTestMaster ltm WHERE ltm.doctorId = :doctorId AND ltm.groupName = :groupName ORDER BY ltm.priorityValue ASC, ltm.labTestDescription ASC")
    List<LabTestMaster> findByDoctorIdAndGroupName(@Param("doctorId") String doctorId, @Param("groupName") String groupName);
}
