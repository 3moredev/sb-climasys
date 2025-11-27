package com.climasys.service;

import com.climasys.entity.ProcedureFindings;
import com.climasys.entity.ProcedureFindingsId;
import com.climasys.entity.ProcedureMaster;
import com.climasys.entity.ProcedureMasterId;
import com.climasys.repository.ProcedureFindingsRepository;
import com.climasys.repository.ProcedureMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for ProcedureMaster business logic
 * Provides methods for managing procedure master data and findings
 */
@Service
@Transactional
public class ProcedureMasterService {

    private static final Logger logger = LoggerFactory.getLogger(ProcedureMasterService.class);

    @Autowired
    private ProcedureMasterRepository procedureMasterRepository;

    @Autowired
    private ProcedureFindingsRepository procedureFindingsRepository;

    /**
     * Get all procedures for a specific doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return List of procedures for the doctor and clinic
     */
    @Transactional(readOnly = true)
    public List<ProcedureMaster> getAllProceduresForDoctorAndClinic(String doctorId, String clinicId) {
        logger.info("Getting all procedures for doctor: {} and clinic: {}", doctorId, clinicId);
        return procedureMasterRepository.findByDoctorIdAndClinicIdOrderByPriorityValueAscProcedureDescriptionAsc(doctorId, clinicId);
    }

    /**
     * Get all procedures for a specific doctor (backward compatibility)
     * @param doctorId Doctor ID
     * @return List of procedures for the doctor
     */
    @Transactional(readOnly = true)
    public List<ProcedureMaster> getAllProceduresForDoctor(String doctorId) {
        logger.info("Getting all procedures for doctor: {}", doctorId);
        return procedureMasterRepository.findByDoctorIdOrderByPriorityValueAscProcedureDescriptionAsc(doctorId);
    }

    /**
     * Search procedures by description for a specific doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param searchTerm Search term to match against procedure description or priority
     * @return List of matching procedures
     */
    @Transactional(readOnly = true)
    public List<ProcedureMaster> searchProceduresByDescription(String doctorId, String clinicId, String searchTerm) {
        logger.info("Searching procedures for doctor: {} and clinic: {} with term: {}", doctorId, clinicId, searchTerm);
        return procedureMasterRepository.searchProceduresByDescription(doctorId, clinicId, searchTerm);
    }

    /**
     * Search procedures by description for a specific doctor (backward compatibility)
     * @param doctorId Doctor ID
     * @param searchTerm Search term to match against procedure description or priority
     * @return List of matching procedures
     */
    @Transactional(readOnly = true)
    public List<ProcedureMaster> searchProceduresByDescription(String doctorId, String searchTerm) {
        logger.info("Searching procedures for doctor: {} with term: {}", doctorId, searchTerm);
        return procedureMasterRepository.searchProceduresByDescription(doctorId, searchTerm);
    }

    /**
     * Get a procedure by description, doctor ID, and clinic ID
     * @param procedureDescription Procedure description
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return Optional procedure
     */
    @Transactional(readOnly = true)
    public Optional<ProcedureMaster> getProcedureByDescription(String procedureDescription, String doctorId, String clinicId) {
        logger.info("Getting procedure by description: {} for doctor: {} and clinic: {}", procedureDescription, doctorId, clinicId);
        ProcedureMasterId id = new ProcedureMasterId(procedureDescription, doctorId, clinicId);
        return procedureMasterRepository.findById(id);
    }

    /**
     * Create a new procedure
     * @param procedure Procedure to create
     * @return Created procedure
     */
    public ProcedureMaster createProcedure(ProcedureMaster procedure) {
        logger.info("Creating new procedure: {}", procedure.getProcedureDescription());
        
        // Set creation timestamp
        procedure.setCreatedOn(LocalDateTime.now());
        procedure.setModifiedOn(LocalDateTime.now());
        
        return procedureMasterRepository.save(procedure);
    }

    /**
     * Update an existing procedure
     * @param procedure Procedure to update
     * @return Updated procedure
     */
    public ProcedureMaster updateProcedure(ProcedureMaster procedure) {
        logger.info("Updating procedure: {}", procedure.getProcedureDescription());
        
        // Set modification timestamp
        procedure.setModifiedOn(LocalDateTime.now());
        
        return procedureMasterRepository.save(procedure);
    }

    /**
     * Delete a procedure and all its findings
     * @param procedureDescription Procedure description
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return True if deleted successfully
     */
    public boolean deleteProcedure(String procedureDescription, String doctorId, String clinicId) {
        logger.info("Deleting procedure: {} for doctor: {} and clinic: {}", procedureDescription, doctorId, clinicId);
        
        Optional<ProcedureMaster> procedureOpt = getProcedureByDescription(procedureDescription, doctorId, clinicId);
        if (procedureOpt.isPresent()) {
            // Delete all findings for this procedure first
            procedureFindingsRepository.deleteByDoctorIdAndProcedureDescription(doctorId, procedureDescription);
            
            // Delete the procedure
            ProcedureMasterId id = new ProcedureMasterId(procedureDescription, doctorId, clinicId);
            procedureMasterRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get all findings for a procedure
     * @param doctorId Doctor ID
     * @param procedureDescription Procedure description
     * @return List of findings for the procedure
     */
    @Transactional(readOnly = true)
    public List<ProcedureFindings> getFindingsForProcedure(String doctorId, String procedureDescription) {
        logger.info("Getting findings for procedure: {} for doctor: {}", procedureDescription, doctorId);
        return procedureFindingsRepository.findByDoctorIdAndProcedureDescriptionOrderByFindingsDescriptionAsc(doctorId, procedureDescription);
    }

    /**
     * Add a finding to a procedure
     * @param finding Finding to add
     * @return Created finding
     */
    public ProcedureFindings addFinding(ProcedureFindings finding) {
        logger.info("Adding finding: {} to procedure: {}", finding.getFindingsDescription(), finding.getProcedureDescription());
        
        // Set creation timestamp
        finding.setCreatedOn(LocalDateTime.now());
        finding.setModifiedOn(LocalDateTime.now());
        
        return procedureFindingsRepository.save(finding);
    }

    /**
     * Delete a finding from a procedure
     * @param doctorId Doctor ID
     * @param procedureDescription Procedure description
     * @param findingsDescription Findings description
     * @return True if deleted successfully
     */
    public boolean deleteFinding(String doctorId, String procedureDescription, String findingsDescription) {
        logger.info("Deleting finding: {} from procedure: {} for doctor: {}", findingsDescription, procedureDescription, doctorId);
        
        if (procedureFindingsRepository.existsByDoctorIdAndProcedureDescriptionAndFindingsDescription(doctorId, procedureDescription, findingsDescription)) {
            ProcedureFindingsId id = new ProcedureFindingsId(doctorId, procedureDescription, findingsDescription);
            procedureFindingsRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get procedure statistics for a doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return Map containing procedure statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getProcedureStatistics(String doctorId, String clinicId) {
        logger.info("Getting procedure statistics for doctor: {} and clinic: {}", doctorId, clinicId);
        
        long totalProcedures = procedureMasterRepository.countByDoctorIdAndClinicId(doctorId, clinicId);
        
        return Map.of(
            "totalProcedures", totalProcedures,
            "doctorId", doctorId,
            "clinicId", clinicId
        );
    }

    /**
     * Check if a procedure exists for a doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param procedureDescription Procedure description to check
     * @return True if procedure exists
     */
    @Transactional(readOnly = true)
    public boolean procedureExists(String doctorId, String clinicId, String procedureDescription) {
        return procedureMasterRepository.existsByDoctorIdAndClinicIdAndProcedureDescription(doctorId, clinicId, procedureDescription);
    }
}

