package com.climasys.lab.service;

import com.climasys.dto.LabTestResultRequest;
import com.climasys.dto.LabTestResultResponse;
import com.climasys.entity.PatientVisitLabTestResult;
import com.climasys.entity.PatientVisitLabTestResultId;
import com.climasys.repository.PatientVisitLabTestResultRepository;
import com.climasys.repository.PatientVisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private PatientVisitRepository patientVisitRepository;
    
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
            
            // Parse visit date from the first test parameter
            LocalDateTime visitDate = parseVisitDate(request.testReportData().get(0).visitDate());
            
            int recordsInserted = 0;
            int recordsUpdated = 0;
            List<String> errors = new ArrayList<>();
            
            // Process each test parameter
            for (LabTestResultRequest.LabTestParameterData testData : request.testReportData()) {
                try {
                    // Note: Lab test date can be different from visit date
                    // Find the visit using composite key (excluding date) to get the actual visit date for FK
                    var visitOptional = patientVisitRepository
                            .findFirstByCompositeKeyWithoutDate(
                                    testData.patientId(),
                                    testData.doctorId(),
                                    testData.clinicId(),
                                    testData.shiftId(),
                                    testData.patientVisitNo()
                            );
                    boolean visitExists = visitOptional.isPresent();
                    
                    if (!visitExists) {
                        String errorMsg = String.format("Patient visit does not exist for patient: %s, visit: %s, doctor: %s, clinic: %s, shift: %s", 
                                testData.patientId(), testData.patientVisitNo(), testData.doctorId(), 
                                testData.clinicId(), testData.shiftId());
                        errors.add(errorMsg);
                        logger.error(errorMsg);
                        continue; // Skip this test parameter and continue with the next one
                    }
                    
                    // Use the exact visitDate from patient_visits to satisfy FK constraint
                    // The lab test date (testData.visitDate()) may be different, but we must use the visit's actual date for FK
                    LocalDateTime exactVisitDate = visitOptional.get().getVisitDate();
                    logger.debug("Lab test date from request: {}, Using actual visit date for FK: {}", 
                            testData.visitDate(), exactVisitDate);

                    // Create composite key
                    PatientVisitLabTestResultId id = new PatientVisitLabTestResultId(
                            exactVisitDate,
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
                                exactVisitDate,
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
                    visitDate,
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
     * First finds the actual visit to get the exact visit date used when saving,
     * then queries lab test results using that exact date for correct appointment matching
     */
    public List<PatientVisitLabTestResult> getLabTestResultsWithExactVisitDate(String patientId, Integer patientVisitNo, 
                                                           Short shiftId, String clinicId, String doctorId, 
                                                           LocalDateTime providedVisitDate) {
        logger.info("Getting lab test results for patient: {}, visit: {}, provided date: {}", 
                patientId, patientVisitNo, providedVisitDate);
        
        // Strategy: Match stored procedure USP_Get_PreviousLabReports behavior
        // The stored procedure uses DATE comparison (date-only, ignoring time)
        // So we should prioritize date-only comparison over exact timestamp matching
        
        // First attempt: Try date-only comparison with provided date (matches stored procedure logic)
        // This is the primary method as it matches USP_Get_PreviousLabReports behavior
        List<PatientVisitLabTestResult> results = repository.findByPatientVisitByDateOnly(
                patientId, patientVisitNo, shiftId, clinicId, doctorId, providedVisitDate);
        
        if (!results.isEmpty()) {
            logger.info("Found {} lab test results using date-only comparison (provided date): {}", results.size(), providedVisitDate);
            return results;
        }
        
        logger.debug("No results found with provided date (date-only), trying to find visit and use its exact date...");
        
        // Second attempt: Find the actual visit to get the exact visit date used when saving
        var visitOptional = patientVisitRepository.findFirstByCompositeKeyWithoutDate(
                patientId, doctorId, clinicId, shiftId, patientVisitNo);
        
        if (!visitOptional.isPresent()) {
            logger.warn("Patient visit not found for patient: {}, visit: {}, doctor: {}, clinic: {}, shift: {}", 
                    patientId, patientVisitNo, doctorId, clinicId, shiftId);
            
            // Last attempt: Try without exact date to see if any results exist
            List<PatientVisitLabTestResult> allResults = repository.findByPatientVisitWithoutExactDate(
                    patientId, patientVisitNo, shiftId, clinicId, doctorId);
            if (!allResults.isEmpty()) {
                logger.error("Visit not found but {} lab test results exist for this composite key! This indicates data inconsistency.", 
                        allResults.size());
                logger.error("Results found with dates: {}", allResults.stream()
                        .map(r -> r.getVisitDate().toString())
                        .distinct()
                        .collect(java.util.stream.Collectors.joining(", ")));
            }
            return new ArrayList<>();
        }
        
        // Get the exact visit date from the visit record (this is what was used when saving)
        LocalDateTime exactVisitDate = visitOptional.get().getVisitDate();
        logger.info("Found visit with exact date: {} (provided date: {})", exactVisitDate, providedVisitDate);
        
        // Third attempt: Try date-only comparison with exact visit date (matches stored procedure)
        results = repository.findByPatientVisitByDateOnly(
                patientId, patientVisitNo, shiftId, clinicId, doctorId, exactVisitDate);
        
        if (!results.isEmpty()) {
            logger.info("Found {} lab test results using date-only comparison (exact visit date): {}", results.size(), exactVisitDate);
            return results;
        }
        
        // Fourth attempt: Try exact timestamp match with exact visit date (for precision)
        logger.debug("No results found with date-only comparison, trying exact timestamp match...");
        results = repository.findByPatientVisit(
                patientId, patientVisitNo, shiftId, clinicId, doctorId, exactVisitDate);
        
        if (!results.isEmpty()) {
            logger.info("Found {} lab test results using exact timestamp match: {}", results.size(), exactVisitDate);
            return results;
        }
        
        // Debug: If still no results found, check what dates exist in the database
        logger.warn("No results found with any date matching method. Checking database directly...");
        logger.warn("Query parameters - patientId: {}, patientVisitNo: {}, shiftId: {}, clinicId: {}, doctorId: {}, visitDate: {}", 
                patientId, patientVisitNo, shiftId, clinicId, doctorId, exactVisitDate);
        
        // Try to find any results for this composite key without date restriction
        List<PatientVisitLabTestResult> allResults = repository.findByPatientVisitWithoutExactDate(
                patientId, patientVisitNo, shiftId, clinicId, doctorId);
        logger.warn("Found {} total lab test results for this composite key (without date restriction): {}", 
                allResults.size(), allResults.stream()
                    .map(r -> "date=" + r.getVisitDate() + ", test=" + r.getLabTestDescription())
                    .collect(java.util.stream.Collectors.joining("; ")));
        
        if (!allResults.isEmpty()) {
            LocalDateTime firstResultDate = allResults.get(0).getVisitDate();
            long diffMs = java.time.Duration.between(exactVisitDate, firstResultDate).toMillis();
            logger.error("DATE MISMATCH DETECTED! Visit date: {}, First result date: {}, Difference: {}ms", 
                    exactVisitDate, firstResultDate, diffMs);
            
            // Last resort: Try date-only comparison with result's date
            results = repository.findByPatientVisitByDateOnly(
                    patientId, patientVisitNo, shiftId, clinicId, doctorId, firstResultDate);
            if (!results.isEmpty()) {
                logger.info("Found {} results using date-only comparison with result date: {}", results.size(), firstResultDate);
            }
        }
        
        return results;
    }
    
    /**
     * Get lab test results for a specific patient visit (legacy method - kept for backward compatibility)
     * Note: This method tries exact date match first, then falls back to composite key lookup
     * For correct appointment matching, use getLabTestResultsWithExactVisitDate instead
     */
    public List<PatientVisitLabTestResult> getLabTestResults(String patientId, Integer patientVisitNo, 
                                                           Short shiftId, String clinicId, String doctorId, 
                                                           LocalDateTime visitDate) {
        logger.info("Getting lab test results for patient: {}, visit: {}, date: {}", patientId, patientVisitNo, visitDate);
        
        // First try exact date match
        List<PatientVisitLabTestResult> results = repository.findByPatientVisit(
                patientId, patientVisitNo, shiftId, clinicId, doctorId, visitDate);
        
        // If not found with exact date, try without exact date (fallback - not recommended for production)
        if (results.isEmpty()) {
            logger.debug("No results found with exact date match, trying without exact date for patient: {}, visit: {}", 
                    patientId, patientVisitNo);
            results = repository.findByPatientVisitWithoutExactDate(
                    patientId, patientVisitNo, shiftId, clinicId, doctorId);
            if (!results.isEmpty()) {
                logger.info("Found {} lab test results using composite key (without exact date match)", results.size());
            }
        }
        
        return results;
    }
    
    /**
     * Get lab test results by composite key without exact date (for cases where date may differ)
     */
    public List<PatientVisitLabTestResult> getLabTestResultsByCompositeKey(String patientId, Integer patientVisitNo, 
                                                           Short shiftId, String clinicId, String doctorId) {
        logger.info("Getting lab test results by composite key (without exact date) for patient: {}, visit: {}", 
                patientId, patientVisitNo);
        
        return repository.findByPatientVisitWithoutExactDate(
                patientId, patientVisitNo, shiftId, clinicId, doctorId);
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
                logger.error("Failed to parse visit date: {}", visitDateStr, e2);
                throw new IllegalArgumentException("Invalid visit date format: " + visitDateStr);
            }
        }
    }
}