package com.climasys.lab.service;

import com.climasys.dto.LabTestResultRequest;
import com.climasys.dto.LabTestResultResponse;
import com.climasys.entity.PatientVisitLabTestResult;
import com.climasys.entity.PatientVisitLabTestResultId;
import com.climasys.repository.PatientVisitLabTestResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing lab test results
 * Equivalent to USP_Insert_LabTestAllData stored procedure functionality
 */
@Service
public class LabTestResultService {

    private static final Logger logger = LoggerFactory.getLogger(LabTestResultService.class);
    
    @Autowired
    private PatientVisitLabTestResultRepository repository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Save lab test results for a patient visit
     * Equivalent to USP_Insert_LabTestAllData stored procedure
     * 
     * @param request Lab test result request containing all test data
     * @return Response with operation result
     */
    @Transactional
    public LabTestResultResponse saveLabTestResults(LabTestResultRequest request) {
        logger.info("Saving lab test results for patient: {}, visit: {}", request.patientId(), request.patientVisitNo());
        
        try {
            // Validate request
            List<String> validationErrors = validateRequest(request);
            if (!validationErrors.isEmpty()) {
                return LabTestResultResponse.error("Validation failed", validationErrors);
            }
            
            // Get the actual visit date from the patient visit record
            LocalDateTime actualVisitDate = getActualVisitDate(
                request.patientId(), 
                request.patientVisitNo(), 
                request.doctorId(), 
                request.clinicId(), 
                request.shiftId()
            );
            
            int recordsInserted = 0;
            int recordsUpdated = 0;
            List<String> errors = new ArrayList<>();
            
            // Process each test parameter
            for (LabTestResultRequest.LabTestParameterData testData : request.testReportData()) {
                try {
                    // Use the actual visit date from the patient visit record
                    LocalDateTime paramVisitDate = actualVisitDate;
                    
                    // Create composite key
                    PatientVisitLabTestResultId id = new PatientVisitLabTestResultId(
                            paramVisitDate,
                            testData.patientVisitNo(),
                            testData.shiftId(),
                            testData.clinicId(),
                            testData.doctorId(),
                            testData.patientId(),
                            testData.labTestDescription(),
                            testData.parameterName()
                    );
                    
                    // Check if record already exists
                    Optional<PatientVisitLabTestResult> existingResult = repository.findById(id);
                    
                    if (existingResult.isPresent()) {
                        // Update existing record
                        PatientVisitLabTestResult existing = existingResult.get();
                        existing.setTestParameterValue(testData.testParameterValue());
                        existing.setModifiedOn(LocalDateTime.now());
                        existing.setModifiedbyName(request.userId());
                        existing.setDoctorName(request.doctorName());
                        existing.setLabName(request.labName());
                        existing.setReportDate(request.reportDate());
                        existing.setComment(request.comment());
                        existing.setDeleteFlag(false);
                        
                        repository.save(existing);
                        recordsUpdated++;
                        logger.debug("Updated lab test result: {}", id);
                        
                    } else {
                        // Create new record
                        PatientVisitLabTestResult newResult = new PatientVisitLabTestResult(
                                paramVisitDate,
                                testData.patientVisitNo(),
                                testData.shiftId(),
                                testData.clinicId(),
                                testData.doctorId(),
                                testData.patientId(),
                                testData.labTestDescription(),
                                testData.parameterName(),
                                testData.testParameterValue()
                        );
                        
                        // Set additional fields
                        newResult.setCreatedbyName(request.userId());
                        newResult.setModifiedbyName(request.userId());
                        newResult.setDoctorName(request.doctorName());
                        newResult.setLabName(request.labName());
                        newResult.setReportDate(request.reportDate());
                        newResult.setComment(request.comment());
                        
                        repository.save(newResult);
                        recordsInserted++;
                        logger.debug("Inserted new lab test result: {}", id);
                    }
                    
                } catch (Exception e) {
                    String errorMsg = String.format("Failed to process test parameter %s: %s", 
                            testData.parameterName(), e.getMessage());
                    errors.add(errorMsg);
                    logger.error(errorMsg, e);
                }
            }
            
            // Check if any records were processed successfully
            if (recordsInserted == 0 && recordsUpdated == 0 && !errors.isEmpty()) {
                return LabTestResultResponse.error("Failed to save any lab test results", errors);
            }
            
            // Log summary
            logger.info("Lab test results saved successfully - Inserted: {}, Updated: {}, Errors: {}", 
                    recordsInserted, recordsUpdated, errors.size());
            
            // Return success response
            return LabTestResultResponse.success(
                    request.patientId(),
                    request.patientVisitNo(),
                    request.doctorId(),
                    request.clinicId(),
                    request.shiftId(),
                    actualVisitDate,
                    recordsInserted,
                    recordsUpdated
            );
            
        } catch (Exception e) {
            logger.error("Error saving lab test results for patient: {}, visit: {}", 
                    request.patientId(), request.patientVisitNo(), e);
            return LabTestResultResponse.error("Failed to save lab test results: " + e.getMessage());
        }
    }
    
    /**
     * Get lab test results for a specific patient visit
     */
    public List<PatientVisitLabTestResult> getLabTestResults(String patientId, Integer patientVisitNo, 
                                                           Short shiftId, String clinicId, String doctorId, 
                                                           LocalDateTime visitDate) {
        logger.info("Getting lab test results for patient: {}, visit: {}", patientId, patientVisitNo);
        
        return repository.findByPatientVisit(patientId, patientVisitNo, shiftId, clinicId, doctorId, visitDate);
    }
    
    /**
     * Get all lab test results for a patient
     */
    public List<PatientVisitLabTestResult> getPatientLabTestResults(String patientId) {
        logger.info("Getting all lab test results for patient: {}", patientId);
        
        return repository.findByPatientIdOrderByVisitDateDesc(patientId);
    }
    
    /**
     * Soft delete lab test results for a patient visit
     */
    @Transactional
    public boolean deleteLabTestResults(String patientId, Integer patientVisitNo, Short shiftId, 
                                      String clinicId, String doctorId, LocalDateTime visitDate, String userId) {
        logger.info("Deleting lab test results for patient: {}, visit: {}", patientId, patientVisitNo);
        
        try {
            int deletedCount = repository.softDeleteByPatientVisit(
                    patientId, patientVisitNo, shiftId, clinicId, doctorId, visitDate, 
                    LocalDateTime.now(), userId);
            
            logger.info("Soft deleted {} lab test results", deletedCount);
            return deletedCount > 0;
            
        } catch (Exception e) {
            logger.error("Error deleting lab test results for patient: {}, visit: {}", 
                    patientId, patientVisitNo, e);
            return false;
        }
    }
    
    /**
     * Soft delete a specific lab test result parameter
     * Equivalent to USP_Delete_LabtestParameter stored procedure
     */
    @Transactional
    public boolean deleteLabTestResultParameter(String patientId, Integer patientVisitNo, Short shiftId,
                                              String clinicId, String doctorId, LocalDateTime visitDate,
                                              String labTestDescription, String parameterName, String userId) {
        logger.info("Deleting lab test result parameter for patient: {}, visit: {}, test: {}, parameter: {}", 
                patientId, patientVisitNo, labTestDescription, parameterName);
        
        try {
            // Check if the parameter exists before attempting to delete
            boolean exists = repository.existsByPatientVisitAndParameter(
                    patientId, patientVisitNo, shiftId, clinicId, doctorId, visitDate, 
                    labTestDescription, parameterName);
            
            if (!exists) {
                logger.warn("Lab test result parameter not found for deletion: patient={}, visit={}, test={}, parameter={}", 
                        patientId, patientVisitNo, labTestDescription, parameterName);
                return false;
            }
            
            // Perform the soft delete
            int deletedCount = repository.softDeleteByPatientVisitAndParameter(
                    patientId, patientVisitNo, shiftId, clinicId, doctorId, visitDate, 
                    labTestDescription, parameterName, LocalDateTime.now(), userId);
            
            logger.info("Soft deleted {} lab test result parameter", deletedCount);
            return deletedCount > 0;
            
        } catch (Exception e) {
            logger.error("Error deleting lab test result parameter for patient: {}, visit: {}, test: {}, parameter: {}", 
                    patientId, patientVisitNo, labTestDescription, parameterName, e);
            return false;
        }
    }
    
    /**
     * Get a specific lab test result parameter
     */
    public PatientVisitLabTestResult getLabTestResultParameter(String patientId, Integer patientVisitNo, Short shiftId,
                                                             String clinicId, String doctorId, LocalDateTime visitDate,
                                                             String labTestDescription, String parameterName) {
        logger.info("Getting lab test result parameter for patient: {}, visit: {}, test: {}, parameter: {}", 
                patientId, patientVisitNo, labTestDescription, parameterName);
        
        return repository.findByPatientVisitAndTestParameter(
                patientId, patientVisitNo, shiftId, clinicId, doctorId, visitDate, 
                labTestDescription, parameterName);
    }
    
    /**
     * Validate the request data
     */
    private List<String> validateRequest(LabTestResultRequest request) {
        List<String> errors = new ArrayList<>();
        
        if (request.testReportData() == null || request.testReportData().isEmpty()) {
            errors.add("Test report data is required");
            return errors;
        }
        
        // Validate each test parameter
        for (int i = 0; i < request.testReportData().size(); i++) {
            LabTestResultRequest.LabTestParameterData testData = request.testReportData().get(i);
            String prefix = "Test parameter " + (i + 1) + ": ";
            
            if (testData.visitDate() == null || testData.visitDate().trim().isEmpty()) {
                errors.add(prefix + "Visit date is required");
            }
            
            if (testData.patientVisitNo() == null) {
                errors.add(prefix + "Patient visit number is required");
            }
            
            if (testData.shiftId() == null) {
                errors.add(prefix + "Shift ID is required");
            }
            
            if (testData.clinicId() == null || testData.clinicId().trim().isEmpty()) {
                errors.add(prefix + "Clinic ID is required");
            }
            
            if (testData.doctorId() == null || testData.doctorId().trim().isEmpty()) {
                errors.add(prefix + "Doctor ID is required");
            }
            
            if (testData.patientId() == null || testData.patientId().trim().isEmpty()) {
                errors.add(prefix + "Patient ID is required");
            }
            
            if (testData.labTestDescription() == null || testData.labTestDescription().trim().isEmpty()) {
                errors.add(prefix + "Lab test description is required");
            }
            
            if (testData.parameterName() == null || testData.parameterName().trim().isEmpty()) {
                errors.add(prefix + "Parameter name is required");
            }
        }
        
        return errors;
    }
    
    /**
     * Get the actual visit date from the patient visit record
     * If exact match not found, try to find the most recent visit for this patient
     */
    private LocalDateTime getActualVisitDate(String patientId, Integer patientVisitNo, 
                                           String doctorId, String clinicId, Short shiftId) {
        // First try exact match
        String exactQuery = "SELECT visit_date FROM patient_visits WHERE " +
                           "patient_id = ? AND patient_visit_no = ? AND doctor_id = ? " +
                           "AND clinic_id = ? AND shift_id = ? AND delete_flag = false";
        
        try {
            LocalDateTime actualVisitDate = jdbcTemplate.queryForObject(
                exactQuery, LocalDateTime.class, patientId, patientVisitNo, doctorId, clinicId, shiftId);
            logger.debug("Found exact visit date: {} for patient: {}, visit: {}", 
                        actualVisitDate, patientId, patientVisitNo);
            return actualVisitDate;
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Exact patient visit not found, trying to find most recent visit for patient: {}, visit: {}", 
                       patientId, patientVisitNo);
            
            // Try to find the most recent visit for this patient with the same doctor and clinic
            String recentQuery = "SELECT visit_date FROM patient_visits WHERE " +
                               "patient_id = ? AND doctor_id = ? AND clinic_id = ? " +
                               "AND delete_flag = false ORDER BY visit_date DESC LIMIT 1";
            
            try {
                LocalDateTime recentVisitDate = jdbcTemplate.queryForObject(
                    recentQuery, LocalDateTime.class, patientId, doctorId, clinicId);
                logger.warn("Using most recent visit date: {} for patient: {} (original visit: {})", 
                           recentVisitDate, patientId, patientVisitNo);
                return recentVisitDate;
            } catch (EmptyResultDataAccessException e2) {
                logger.error("No patient visits found for patient: {}, doctor: {}, clinic: {}", 
                           patientId, doctorId, clinicId);
                throw new IllegalArgumentException("No patient visits found for patient: " + patientId);
            }
        }
    }

    /**
     * Parse visit date from string
     */
    private LocalDateTime parseVisitDate(String visitDateStr) {
        try {
            return LocalDateTime.parse(visitDateStr, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            // Try alternative formats
            try {
                return LocalDateTime.parse(visitDateStr);
            } catch (Exception e2) {
                // Try parsing as date only and add default time
                try {
                    LocalDate date = LocalDate.parse(visitDateStr);
                    return date.atStartOfDay(); // Use 00:00:00 as default time
                } catch (Exception e3) {
                    // Try parsing with space replacement for ISO format
                    try {
                        return LocalDateTime.parse(visitDateStr.replace(" ", "T"));
                    } catch (Exception e4) {
                        logger.error("Failed to parse visit date: {}", visitDateStr, e4);
                        throw new IllegalArgumentException("Invalid visit date format: " + visitDateStr);
                    }
                }
            }
        }
    }
}
