package com.climasys.repository;

import com.climasys.entity.GroupInstructions;
import com.climasys.entity.GroupInstructionsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository for GroupInstructions entity
 * Provides data access methods for instruction details within groups
 */
@Repository
public interface GroupInstructionsRepository extends JpaRepository<GroupInstructions, GroupInstructionsId> {
    
    /**
     * Find all instructions for a specific doctor and group
     * @param doctorId Doctor ID
     * @param groupDescription Group description
     * @return List of instructions in the group
     */
    List<GroupInstructions> findByDoctorIdAndGroupDescriptionOrderBySequenceNoAscInstructionsDescriptionAsc(
            String doctorId, String groupDescription);
    
    /**
     * Find all instructions for a specific doctor
     * @param doctorId Doctor ID
     * @return List of all instructions for the doctor
     */
    List<GroupInstructions> findByDoctorIdOrderByGroupDescriptionAscSequenceNoAsc(String doctorId);
    
    /**
     * Get instruction groups with concatenated instruction format
     * Similar to stored procedure USP_Get_FindingsData
     * @param doctorId Doctor ID
     * @return List of instruction data with concatenated format
     */
    @Query(value = """
        SELECT 
            gi.group_description,
            gi.instructions_description,
            gi.sequence_no,
            TRIM(gi.group_description) || '*' || TRIM(gi.instructions_description) AS instruction_group
        FROM group_instructions gi
        WHERE gi.doctor_id = :doctorId
        ORDER BY gi.group_description ASC, 
                 COALESCE(gi.sequence_no, 999999) ASC,
                 gi.instructions_description ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findInstructionGroupsFormatted(@Param("doctorId") String doctorId);
    
    /**
     * Get instructions for a specific group with formatted output
     * @param doctorId Doctor ID
     * @param groupDescription Group description
     * @return List of formatted instruction data
     */
    @Query(value = """
        SELECT 
            gi.group_description,
            gi.instructions_description,
            gi.sequence_no,
            gi.priority_value,
            TRIM(gi.group_description) || '*' || TRIM(gi.instructions_description) AS instruction_group
        FROM group_instructions gi
        WHERE gi.doctor_id = :doctorId 
        AND gi.group_description = :groupDescription
        ORDER BY COALESCE(gi.sequence_no, 999999) ASC,
                 gi.instructions_description ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findInstructionsByGroupFormatted(@Param("doctorId") String doctorId, 
                                                               @Param("groupDescription") String groupDescription);
    
    /**
     * Search instructions by description for a specific doctor and group
     * @param doctorId Doctor ID
     * @param groupDescription Group description
     * @param searchTerm Search term
     * @return List of matching instructions
     */
    @Query("SELECT gi FROM GroupInstructions gi WHERE gi.doctorId = :doctorId " +
           "AND gi.groupDescription = :groupDescription " +
           "AND LOWER(gi.instructionsDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY gi.sequenceNo ASC, gi.instructionsDescription ASC")
    List<GroupInstructions> searchInstructions(@Param("doctorId") String doctorId,
                                               @Param("groupDescription") String groupDescription,
                                               @Param("searchTerm") String searchTerm);
    
    /**
     * Count instructions in a specific group
     * @param doctorId Doctor ID
     * @param groupDescription Group description
     * @return Number of instructions in the group
     */
    long countByDoctorIdAndGroupDescription(String doctorId, String groupDescription);
    
    /**
     * Delete all instructions for a specific group
     * @param doctorId Doctor ID
     * @param groupDescription Group description
     */
    void deleteByDoctorIdAndGroupDescription(String doctorId, String groupDescription);
    
    /**
     * Check if instruction exists
     * @param doctorId Doctor ID
     * @param groupDescription Group description
     * @param instructionsDescription Instruction description
     * @return True if instruction exists
     */
    boolean existsByDoctorIdAndGroupDescriptionAndInstructionsDescription(
            String doctorId, String groupDescription, String instructionsDescription);
}

