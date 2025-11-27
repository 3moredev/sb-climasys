package com.climasys.repository;

import com.climasys.entity.ProcedureMaster;
import com.climasys.entity.ProcedureMasterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ProcedureMaster entity
 * Provides data access methods for procedure master data
 */
@Repository
public interface ProcedureMasterRepository extends JpaRepository<ProcedureMaster, ProcedureMasterId> {

    /**
     * Find all procedures for a specific doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return List of procedures for the doctor and clinic
     */
    List<ProcedureMaster> findByDoctorIdAndClinicIdOrderByPriorityValueAscProcedureDescriptionAsc(String doctorId, String clinicId);

    /**
     * Find all procedures for a specific doctor (backward compatibility)
     * @param doctorId Doctor ID
     * @return List of procedures for the doctor
     */
    List<ProcedureMaster> findByDoctorIdOrderByPriorityValueAscProcedureDescriptionAsc(String doctorId);

    /**
     * Search procedures by description for a specific doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param searchTerm Search term to match against procedure description or priority
     * @return List of matching procedures
     */
    @Query("SELECT pm FROM ProcedureMaster pm WHERE pm.doctorId = :doctorId AND pm.clinicId = :clinicId AND " +
           "(LOWER(pm.procedureDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(pm.priorityValue AS string) LIKE CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY pm.priorityValue ASC, pm.procedureDescription ASC")
    List<ProcedureMaster> searchProceduresByDescription(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId, @Param("searchTerm") String searchTerm);

    /**
     * Search procedures by description for a specific doctor (backward compatibility)
     * @param doctorId Doctor ID
     * @param searchTerm Search term to match against procedure description or priority
     * @return List of matching procedures
     */
    @Query("SELECT pm FROM ProcedureMaster pm WHERE pm.doctorId = :doctorId AND " +
           "(LOWER(pm.procedureDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(pm.priorityValue AS string) LIKE CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY pm.priorityValue ASC, pm.procedureDescription ASC")
    List<ProcedureMaster> searchProceduresByDescription(@Param("doctorId") String doctorId, @Param("searchTerm") String searchTerm);

    /**
     * Count procedures for a doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return Number of procedures for the doctor and clinic
     */
    long countByDoctorIdAndClinicId(String doctorId, String clinicId);

    /**
     * Count procedures for a doctor (backward compatibility)
     * @param doctorId Doctor ID
     * @return Number of procedures for the doctor
     */
    long countByDoctorId(String doctorId);

    /**
     * Check if a procedure exists for a doctor and clinic by procedure description
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param procedureDescription Procedure description to check
     * @return True if procedure exists
     */
    boolean existsByDoctorIdAndClinicIdAndProcedureDescription(String doctorId, String clinicId, String procedureDescription);

    /**
     * Check if a procedure exists for a doctor by procedure description (backward compatibility)
     * @param doctorId Doctor ID
     * @param procedureDescription Procedure description to check
     * @return True if procedure exists
     */
    boolean existsByDoctorIdAndProcedureDescription(String doctorId, String procedureDescription);
}

