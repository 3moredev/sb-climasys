package com.climasys.repository;

import com.climasys.entity.ProcedureFindings;
import com.climasys.entity.ProcedureFindingsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ProcedureFindings entity
 * Provides data access methods for procedure findings data
 */
@Repository
public interface ProcedureFindingsRepository extends JpaRepository<ProcedureFindings, ProcedureFindingsId> {

    /**
     * Find all findings for a specific procedure, doctor, and clinic
     * @param doctorId Doctor ID
     * @param procedureDescription Procedure description
     * @return List of findings for the procedure
     */
    List<ProcedureFindings> findByDoctorIdAndProcedureDescriptionOrderByFindingsDescriptionAsc(String doctorId, String procedureDescription);

    /**
     * Find all findings for a specific procedure and doctor (backward compatibility)
     * @param doctorId Doctor ID
     * @param procedureDescription Procedure description
     * @return List of findings for the procedure
     */
    @Query("SELECT pf FROM ProcedureFindings pf WHERE pf.doctorId = :doctorId AND pf.procedureDescription = :procedureDescription ORDER BY pf.findingsDescription ASC")
    List<ProcedureFindings> findByDoctorIdAndProcedureDescription(@Param("doctorId") String doctorId, @Param("procedureDescription") String procedureDescription);

    /**
     * Delete all findings for a specific procedure
     * @param doctorId Doctor ID
     * @param procedureDescription Procedure description
     */
    void deleteByDoctorIdAndProcedureDescription(String doctorId, String procedureDescription);

    /**
     * Count findings for a procedure
     * @param doctorId Doctor ID
     * @param procedureDescription Procedure description
     * @return Number of findings for the procedure
     */
    long countByDoctorIdAndProcedureDescription(String doctorId, String procedureDescription);

    /**
     * Check if a finding exists for a procedure
     * @param doctorId Doctor ID
     * @param procedureDescription Procedure description
     * @param findingsDescription Findings description to check
     * @return True if finding exists
     */
    boolean existsByDoctorIdAndProcedureDescriptionAndFindingsDescription(String doctorId, String procedureDescription, String findingsDescription);
}

