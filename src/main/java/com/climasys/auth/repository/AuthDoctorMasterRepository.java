package com.climasys.auth.repository;

import com.climasys.auth.entity.AuthDoctorMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthDoctorMasterRepository extends JpaRepository<AuthDoctorMaster, String> {
    
    // Simple method to find doctor by ID (since isActive column doesn't exist in database)
    Optional<AuthDoctorMaster> findByDoctorId(String doctorId);
    
    /**
     * Get all OPD doctors filtered by language, clinic, role, and default clinic
     * This matches the logic from USP_Get_AllDoctors stored procedure (Table[1])
     * 
     * Filters:
     * - OPD_DR = 1 (only OPD doctors)
     * - Is_Active = 1 (active users)
     * - Language_Id = languageId
     * - Role_Id = 2 (doctor role)
     * - Is_Default_Clinic = 1 (default clinic)
     * - Clinic_ID = clinicId
     * 
     * @param languageId Language ID filter
     * @param clinicId Clinic ID filter
     * @return List of doctors matching the criteria
     */
    @Query(value = """
        SELECT DISTINCT 
            DM.prefix,
            DM.first_name,
            DM.last_name,
            DM.doctor_id,
            DM.speciality,
            TRIM(COALESCE(DM.prefix, '') || ' ' || COALESCE(DM.first_name, '') || ' - ' || COALESCE(DM.speciality, '')) as name_with_prefix
        FROM doctor_master DM
        INNER JOIN user_master UM ON UM.doctor_id = DM.doctor_id
        INNER JOIN user_role UR ON UR.user_id = UM.id
        WHERE DM.opd_dr = true
          AND UM.is_active = true
          AND UM.language_id = :languageId
          AND UR.role_id = 2
          AND UR.is_default_clinic = true
          AND UR.clinic_id = :clinicId
        ORDER BY DM.first_name
        """, nativeQuery = true)
    List<Object[]> findAllOpdDoctorsByLanguageAndClinic(@Param("languageId") Integer languageId, @Param("clinicId") String clinicId);
    
    /**
     * Get all active OPD doctors (simplified version without clinic/language filtering)
     * This matches Table[1] from USP_Get_AllDoctors when clinic/language filters are not needed
     * 
     * @return List of active OPD doctors
     */
    @Query(value = """
        SELECT DISTINCT 
            DM.prefix,
            DM.first_name,
            DM.last_name,
            DM.doctor_id,
            DM.speciality,
            TRIM(COALESCE(DM.prefix, '') || ' ' || COALESCE(DM.first_name, '') || ' - ' || COALESCE(DM.speciality, '')) as name_with_prefix
        FROM doctor_master DM
        INNER JOIN user_master UM ON UM.doctor_id = DM.doctor_id
        WHERE DM.opd_dr = true
          AND UM.is_active = true
        ORDER BY DM.first_name
        """, nativeQuery = true)
    List<Object[]> findAllActiveOpdDoctors();
}