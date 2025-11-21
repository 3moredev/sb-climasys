package com.climasys.service;

import com.climasys.entity.DiagnosisMaster;
import com.climasys.entity.DiagnosisMasterId;
import com.climasys.repository.DiagnosisMasterRepository;
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
 * Service class for DiagnosisMaster business logic
 * Provides methods for managing diagnosis master data
 */
@Service
@Transactional
public class DiagnosisMasterService {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosisMasterService.class);

    @Autowired
    private DiagnosisMasterRepository diagnosisMasterRepository;

    /**
     * Get all diagnoses for a specific doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return List of diagnoses for the doctor and clinic
     */
    @Transactional(readOnly = true)
    public List<DiagnosisMaster> getAllDiagnosesForDoctorAndClinic(String doctorId, String clinicId) {
        logger.info("Getting all diagnoses for doctor: {} and clinic: {}", doctorId, clinicId);
        return diagnosisMasterRepository.findByDoctorIdAndClinicIdOrderByPriorityValueAscShortDescriptionAsc(doctorId, clinicId);
    }

    /**
     * Get all diagnoses for a specific doctor (backward compatibility)
     * @param doctorId Doctor ID
     * @return List of diagnoses for the doctor
     */
    @Transactional(readOnly = true)
    public List<DiagnosisMaster> getAllDiagnosesForDoctor(String doctorId) {
        logger.info("Getting all diagnoses for doctor: {}", doctorId);
        return diagnosisMasterRepository.findByDoctorIdOrderByPriorityValueAscShortDescriptionAsc(doctorId);
    }

    /**
     * Get diagnosis data in the same format as the stored procedure USP_Get_PatientProfileRefData for doctor and clinic
     * Returns data with concatenated ID field for backward compatibility
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return List of diagnosis data with formatted ID field
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDiagnosesFormattedByDoctorAndClinic(String doctorId, String clinicId) {
        logger.info("Getting formatted diagnoses for doctor: {} and clinic: {}", doctorId, clinicId);
        return diagnosisMasterRepository.findDiagnosesFormattedByDoctorAndClinic(doctorId, clinicId);
    }

    /**
     * Get diagnosis data in the same format as the stored procedure USP_Get_PatientProfileRefData (backward compatibility)
     * Returns data with concatenated ID field for backward compatibility
     * @param doctorId Doctor ID
     * @return List of diagnosis data with formatted ID field
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDiagnosesFormatted(String doctorId) {
        logger.info("Getting formatted diagnoses for doctor: {}", doctorId);
        return diagnosisMasterRepository.findDiagnosesFormatted(doctorId);
    }

    /**
     * Get all diagnosis data for a clinic in formatted way
     * @param clinicId Clinic ID (mandatory)
     * @param doctorId Doctor ID (optional, can be null)
     * @return List of all diagnosis data for the clinic and optionally doctor
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllDiagnosesForDoctorFormatted(String clinicId, String doctorId) {
        logger.info("Getting all formatted diagnoses for clinic: {} and doctor: {}", clinicId, doctorId);
        return diagnosisMasterRepository.findAllDiagnosesForDoctorFormatted(clinicId, doctorId);
    }

    /**
     * Search diagnoses by description for a specific doctor
     * @param doctorId Doctor ID
     * @param searchTerm Search term to match against short or full description
     * @return List of matching diagnoses
     */
    @Transactional(readOnly = true)
    public List<DiagnosisMaster> searchDiagnosesByDescription(String doctorId, String searchTerm) {
        logger.info("Searching diagnoses for doctor: {} with term: {}", doctorId, searchTerm);
        return diagnosisMasterRepository.searchDiagnosesByDescription(doctorId, searchTerm);
    }

    /**
     * Create a new diagnosis
     * @param diagnosis Diagnosis to create
     * @return Created diagnosis
     */
    public DiagnosisMaster createDiagnosis(DiagnosisMaster diagnosis) {
        logger.info("Creating new diagnosis: {}", diagnosis.getShortDescription());
        
        // Set creation timestamp
        diagnosis.setCreatedOn(LocalDateTime.now());
        diagnosis.setModifiedOn(LocalDateTime.now());
        
        return diagnosisMasterRepository.save(diagnosis);
    }

    /**
     * Update an existing diagnosis
     * Only updates diagnosis description and priority value
     * Short description, doctor ID, and clinic ID cannot be changed (they are part of the composite key)
     * @param diagnosis Diagnosis to update (must include shortDescription, doctorId, and clinicId to identify the record)
     * @return Updated diagnosis
     * @throws IllegalArgumentException if diagnosis not found
     */
    public DiagnosisMaster updateDiagnosis(DiagnosisMaster diagnosis) {
        String shortDesc = diagnosis.getShortDescription();
        String doctorId = diagnosis.getDoctorId();
        String clinicId = diagnosis.getClinicId();
        
        logger.info("Updating diagnosis: '{}' for doctor: {} and clinic: {}", 
                   shortDesc, doctorId, clinicId);
        logger.debug("Diagnosis description to update: {}, Priority value to update: {}", 
                    diagnosis.getDiagnosisDescription(), diagnosis.getPriorityValue());
        
        // Find the existing diagnosis using the composite key (try exact match first)
        DiagnosisMasterId id = new DiagnosisMasterId(shortDesc, doctorId, clinicId);
        Optional<DiagnosisMaster> existingOpt = diagnosisMasterRepository.findById(id);
        
        // If exact match fails, try to find by normalized comparison (handles whitespace and ? prefix issues)
        if (existingOpt.isEmpty()) {
            logger.debug("Exact match not found, trying normalized search for: '{}'", normalizeShortDescription(shortDesc));
            existingOpt = findDiagnosisByNormalizedShortDescription(shortDesc, doctorId, clinicId);
        }
        
        if (existingOpt.isEmpty()) {
            logger.warn("Diagnosis not found with shortDescription: '{}', doctorId: {}, clinicId: {}", 
                       shortDesc, doctorId, clinicId);
            // Try to find if there's a similar one (in case of whitespace issues)
            List<DiagnosisMaster> similar = diagnosisMasterRepository.findByDoctorIdAndClinicIdOrderByPriorityValueAscShortDescriptionAsc(doctorId, clinicId);
            logger.debug("Found {} diagnoses for doctor {} and clinic {}", similar.size(), doctorId, clinicId);
            if (!similar.isEmpty()) {
                logger.debug("Available short descriptions: {}", 
                    similar.stream().map(DiagnosisMaster::getShortDescription).toList());
            }
            throw new IllegalArgumentException("Diagnosis not found with shortDescription: '" + 
                shortDesc + "', doctorId: " + doctorId + ", clinicId: " + clinicId);
        }
        
        DiagnosisMaster existing = existingOpt.get();
        
        // Only update diagnosis description and priority value
        if (diagnosis.getDiagnosisDescription() != null) {
            existing.setDiagnosisDescription(diagnosis.getDiagnosisDescription());
        }
        if (diagnosis.getPriorityValue() != null) {
            existing.setPriorityValue(diagnosis.getPriorityValue());
        }
        
        // Update modification timestamp and modifier name if provided
        existing.setModifiedOn(LocalDateTime.now());
        if (diagnosis.getModifiedByName() != null) {
            existing.setModifiedByName(diagnosis.getModifiedByName());
        }
        
        return diagnosisMasterRepository.save(existing);
    }

    /**
     * Normalize short description for comparison (trim whitespace and remove leading ?)
     * @param shortDescription Short description to normalize
     * @return Normalized short description
     */
    private String normalizeShortDescription(String shortDescription) {
        if (shortDescription == null) {
            return null;
        }
        String normalized = shortDescription.trim();
        if (normalized.startsWith("?")) {
            normalized = normalized.substring(1).trim();
        }
        return normalized;
    }
    
    /**
     * Find diagnosis by normalized short description comparison
     * Handles cases where database has whitespace or ? prefix
     * @param shortDescription Short description to find
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return Optional diagnosis if found
     */
    private Optional<DiagnosisMaster> findDiagnosisByNormalizedShortDescription(String shortDescription, String doctorId, String clinicId) {
        String normalizedShortDesc = normalizeShortDescription(shortDescription);
        List<DiagnosisMaster> allDiagnoses = diagnosisMasterRepository.findByDoctorIdAndClinicIdOrderByPriorityValueAscShortDescriptionAsc(doctorId, clinicId);
        
        for (DiagnosisMaster dm : allDiagnoses) {
            String dbShortDesc = dm.getShortDescription();
            if (dbShortDesc != null) {
                String normalizedDbShortDesc = normalizeShortDescription(dbShortDesc);
                if (normalizedShortDesc != null && normalizedShortDesc.equalsIgnoreCase(normalizedDbShortDesc)) {
                    logger.debug("Found match using normalized comparison: '{}' matches '{}'", 
                               shortDescription, dbShortDesc);
                    return Optional.of(dm);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Get a diagnosis by short description, doctor ID, and clinic ID
     * @param shortDescription Short description
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return Optional diagnosis
     */
    @Transactional(readOnly = true)
    public Optional<DiagnosisMaster> getDiagnosisByShortDescription(String shortDescription, String doctorId, String clinicId) {
        logger.info("Getting diagnosis by short description: {} for doctor: {} and clinic: {}", shortDescription, doctorId, clinicId);
        
        // Try exact match first
        DiagnosisMasterId id = new DiagnosisMasterId(shortDescription, doctorId, clinicId);
        Optional<DiagnosisMaster> result = diagnosisMasterRepository.findById(id);
        
        // If exact match fails, try normalized comparison
        if (result.isEmpty()) {
            result = findDiagnosisByNormalizedShortDescription(shortDescription, doctorId, clinicId);
        }
        
        return result;
    }

    /**
     * Delete a diagnosis
     * @param shortDescription Short description of diagnosis to delete
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return True if deleted successfully
     */
    public boolean deleteDiagnosis(String shortDescription, String doctorId, String clinicId) {
        logger.info("Deleting diagnosis: {} for doctor: {} and clinic: {}", shortDescription, doctorId, clinicId);
        
        Optional<DiagnosisMaster> diagnosisOpt = getDiagnosisByShortDescription(shortDescription, doctorId, clinicId);
        if (diagnosisOpt.isPresent()) {
            DiagnosisMaster diagnosis = diagnosisOpt.get();
            // Use the actual short description from the found diagnosis to ensure correct deletion
            DiagnosisMasterId id = new DiagnosisMasterId(diagnosis.getShortDescription(), doctorId, clinicId);
            diagnosisMasterRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get diagnosis statistics for a doctor
     * @param doctorId Doctor ID
     * @return Map containing diagnosis statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDiagnosisStatistics(String doctorId) {
        logger.info("Getting diagnosis statistics for doctor: {}", doctorId);
        
        long totalDiagnoses = diagnosisMasterRepository.countByDoctorId(doctorId);
        
        return Map.of(
            "totalDiagnoses", totalDiagnoses,
            "doctorId", doctorId
        );
    }

    /**
     * Check if a diagnosis exists for a doctor
     * @param doctorId Doctor ID
     * @param shortDescription Short description to check
     * @return True if diagnosis exists
     */
    @Transactional(readOnly = true)
    public boolean diagnosisExists(String doctorId, String shortDescription) {
        return diagnosisMasterRepository.existsByDoctorIdAndShortDescription(doctorId, shortDescription);
    }

    /**
     * Check if a diagnosis exists for a doctor and clinic
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param shortDescription Short description to check
     * @return True if diagnosis exists
     */
    @Transactional(readOnly = true)
    public boolean diagnosisExists(String doctorId, String clinicId, String shortDescription) {
        return diagnosisMasterRepository.existsByDoctorIdAndClinicIdAndShortDescription(doctorId, clinicId, shortDescription);
    }
}

