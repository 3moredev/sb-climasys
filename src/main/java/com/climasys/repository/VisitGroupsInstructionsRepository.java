package com.climasys.repository;

import com.climasys.entity.VisitGroupsInstructions;
import com.climasys.entity.VisitGroupsInstructionsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for VisitGroupsInstructions entity
 * Provides data access methods for patient visit instruction groups
 */
@Repository
public interface VisitGroupsInstructionsRepository extends JpaRepository<VisitGroupsInstructions, VisitGroupsInstructionsId> {
    
    /**
     * Find all instructions for a specific patient visit
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param shiftId Shift ID
     * @param patientId Patient ID
     * @param patientVisitNo Visit number
     * @param visitDate Visit date
     * @return List of visit instruction groups
     */
    List<VisitGroupsInstructions> findByDoctorIdAndClinicIdAndShiftIdAndPatientIdAndPatientVisitNoAndVisitDateOrderByGroupDescriptionAscSequenceNoAsc(
            String doctorId, String clinicId, Short shiftId, String patientId, 
            Integer patientVisitNo, LocalDateTime visitDate);
    
    /**
     * Find instructions for a specific group in a patient visit
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param shiftId Shift ID
     * @param patientId Patient ID
     * @param patientVisitNo Visit number
     * @param visitDate Visit date
     * @param groupDescription Group description
     * @return List of instructions in the group for this visit
     */
    List<VisitGroupsInstructions> findByDoctorIdAndClinicIdAndShiftIdAndPatientIdAndPatientVisitNoAndVisitDateAndGroupDescriptionOrderBySequenceNoAsc(
            String doctorId, String clinicId, Short shiftId, String patientId, 
            Integer patientVisitNo, LocalDateTime visitDate, String groupDescription);
    
    /**
     * Delete all instructions for a specific patient visit
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param shiftId Shift ID
     * @param patientId Patient ID
     * @param patientVisitNo Visit number
     * @param visitDate Visit date
     */
    @Modifying
    @Query("DELETE FROM VisitGroupsInstructions vgi WHERE vgi.doctorId = :doctorId " +
           "AND vgi.clinicId = :clinicId AND vgi.shiftId = :shiftId " +
           "AND vgi.patientId = :patientId AND vgi.patientVisitNo = :patientVisitNo " +
           "AND vgi.visitDate = :visitDate")
    void deleteByVisit(@Param("doctorId") String doctorId,
                       @Param("clinicId") String clinicId,
                       @Param("shiftId") Short shiftId,
                       @Param("patientId") String patientId,
                       @Param("patientVisitNo") Integer patientVisitNo,
                       @Param("visitDate") LocalDateTime visitDate);
    
    /**
     * Delete a specific instruction group from a patient visit
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param shiftId Shift ID
     * @param patientId Patient ID
     * @param patientVisitNo Visit number
     * @param visitDate Visit date
     * @param groupDescription Group description
     */
    @Modifying
    @Query("DELETE FROM VisitGroupsInstructions vgi WHERE vgi.doctorId = :doctorId " +
           "AND vgi.clinicId = :clinicId AND vgi.shiftId = :shiftId " +
           "AND vgi.patientId = :patientId AND vgi.patientVisitNo = :patientVisitNo " +
           "AND vgi.visitDate = :visitDate AND vgi.groupDescription = :groupDescription")
    void deleteByVisitAndGroup(@Param("doctorId") String doctorId,
                               @Param("clinicId") String clinicId,
                               @Param("shiftId") Short shiftId,
                               @Param("patientId") String patientId,
                               @Param("patientVisitNo") Integer patientVisitNo,
                               @Param("visitDate") LocalDateTime visitDate,
                               @Param("groupDescription") String groupDescription);
    
    /**
     * Count instruction groups for a patient visit
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param shiftId Shift ID
     * @param patientId Patient ID
     * @param patientVisitNo Visit number
     * @param visitDate Visit date
     * @return Number of instruction groups
     */
    long countByDoctorIdAndClinicIdAndShiftIdAndPatientIdAndPatientVisitNoAndVisitDate(
            String doctorId, String clinicId, Short shiftId, String patientId, 
            Integer patientVisitNo, LocalDateTime visitDate);
    
    /**
     * Get distinct group descriptions for a patient visit
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param shiftId Shift ID
     * @param patientId Patient ID
     * @param patientVisitNo Visit number
     * @param visitDate Visit date
     * @return List of distinct group descriptions
     */
    @Query("SELECT DISTINCT vgi.groupDescription FROM VisitGroupsInstructions vgi " +
           "WHERE vgi.doctorId = :doctorId AND vgi.clinicId = :clinicId " +
           "AND vgi.shiftId = :shiftId AND vgi.patientId = :patientId " +
           "AND vgi.patientVisitNo = :patientVisitNo AND vgi.visitDate = :visitDate " +
           "ORDER BY vgi.groupDescription ASC")
    List<String> findDistinctGroupsByVisit(@Param("doctorId") String doctorId,
                                           @Param("clinicId") String clinicId,
                                           @Param("shiftId") Short shiftId,
                                           @Param("patientId") String patientId,
                                           @Param("patientVisitNo") Integer patientVisitNo,
                                           @Param("visitDate") LocalDateTime visitDate);
}

