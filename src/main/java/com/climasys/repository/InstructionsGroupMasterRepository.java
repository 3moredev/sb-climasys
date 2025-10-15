package com.climasys.repository;

import com.climasys.entity.InstructionsGroupMaster;
import com.climasys.entity.InstructionsGroupMasterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for InstructionsGroupMaster entity
 * Provides data access methods for instruction group master data
 */
@Repository
public interface InstructionsGroupMasterRepository extends JpaRepository<InstructionsGroupMaster, InstructionsGroupMasterId> {
    
    /**
     * Find all instruction groups for a specific doctor
     * @param doctorId Doctor ID
     * @return List of instruction groups for the doctor
     */
    List<InstructionsGroupMaster> findByDoctorIdOrderByPriorityValueAscGroupDescriptionAsc(String doctorId);
    
    /**
     * Search instruction groups by description for a specific doctor
     * @param doctorId Doctor ID
     * @param searchTerm Search term to match against group description
     * @return List of matching instruction groups
     */
    @Query("SELECT igm FROM InstructionsGroupMaster igm WHERE igm.doctorId = :doctorId AND " +
           "LOWER(igm.groupDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY igm.priorityValue ASC, igm.groupDescription ASC")
    List<InstructionsGroupMaster> searchByGroupDescription(@Param("doctorId") String doctorId, 
                                                           @Param("searchTerm") String searchTerm);
    
    /**
     * Count instruction groups for a doctor
     * @param doctorId Doctor ID
     * @return Number of instruction groups for the doctor
     */
    long countByDoctorId(String doctorId);
    
    /**
     * Check if an instruction group exists for a doctor
     * @param doctorId Doctor ID
     * @param groupDescription Group description to check
     * @return True if instruction group exists
     */
    boolean existsByDoctorIdAndGroupDescription(String doctorId, String groupDescription);
    
    /**
     * Find instruction groups ordered by priority
     * @param doctorId Doctor ID
     * @return List of instruction groups ordered by priority
     */
    @Query("SELECT igm FROM InstructionsGroupMaster igm WHERE igm.doctorId = :doctorId " +
           "ORDER BY COALESCE(igm.priorityValue, 999999) ASC, igm.groupDescription ASC")
    List<InstructionsGroupMaster> findByDoctorIdOrderByPriority(@Param("doctorId") String doctorId);
}

