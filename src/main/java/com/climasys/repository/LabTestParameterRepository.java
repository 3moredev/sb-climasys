package com.climasys.repository;

import com.climasys.entity.LabTestParameter;
import com.climasys.entity.LabTestParameterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for LabTestParameter entity
 * Provides JPA methods to replace USP_Get_LabTestAndParameter stored procedure functionality
 */
@Repository
public interface LabTestParameterRepository extends JpaRepository<LabTestParameter, LabTestParameterId> {
    
    /**
     * Get lab test parameters by doctor ID and lab test description
     * This replaces the main query from USP_Get_LabTestAndParameter stored procedure
     * 
     * @param doctorId Doctor ID to filter lab test parameters
     * @param labTestDescription Lab test description to filter parameters
     * @return List of lab test parameters with lab test master data
     */
    @Query("SELECT ltp.id, ltp.labTestId, ltm.labTestDescription, ltm.id, ltp.parameterName, " +
           "CONCAT(CAST(ltp.id AS string), '&&&', CAST(ltp.labTestId AS string)) as concatId " +
           "FROM LabTestParameter ltp " +
           "JOIN LabTestMaster ltm ON ltp.labTestId = ltm.id AND ltp.doctorId = ltm.doctorId " +
           "WHERE ltp.doctorId = :doctorId AND ltm.labTestDescription = :labTestDescription " +
           "ORDER BY ltp.id")
    List<Object[]> findLabTestAndParametersByDoctorAndTestDescription(
            @Param("doctorId") String doctorId, 
            @Param("labTestDescription") String labTestDescription);
    
    /**
     * Get lab test parameters by doctor ID and lab test ID
     * 
     * @param doctorId Doctor ID to filter lab test parameters
     * @param labTestId Lab test ID to filter parameters
     * @return List of lab test parameters
     */
    @Query("SELECT ltp FROM LabTestParameter ltp WHERE ltp.doctorId = :doctorId AND ltp.labTestId = :labTestId ORDER BY ltp.id")
    List<LabTestParameter> findByDoctorIdAndLabTestId(@Param("doctorId") String doctorId, @Param("labTestId") Integer labTestId);
    
    /**
     * Get all lab test parameters for a doctor
     * 
     * @param doctorId Doctor ID to filter lab test parameters
     * @return List of all lab test parameters for the doctor
     */
    @Query("SELECT ltp FROM LabTestParameter ltp WHERE ltp.doctorId = :doctorId ORDER BY ltp.labTestId, ltp.id")
    List<LabTestParameter> findByDoctorId(@Param("doctorId") String doctorId);
    
    /**
     * Check if lab test parameter exists for a doctor and lab test
     * 
     * @param doctorId Doctor ID
     * @param labTestId Lab test ID
     * @param parameterName Parameter name
     * @return true if parameter exists, false otherwise
     */
    boolean existsByDoctorIdAndLabTestIdAndParameterName(String doctorId, Integer labTestId, String parameterName);
    
    /**
     * Count lab test parameters for a doctor and lab test
     * 
     * @param doctorId Doctor ID
     * @param labTestId Lab test ID
     * @return Count of parameters for the lab test
     */
    long countByDoctorIdAndLabTestId(String doctorId, Integer labTestId);
}
