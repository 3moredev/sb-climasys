package com.climasys.trends.service;

import com.climasys.trends.repository.LabTrendsRepository;
import com.climasys.trends.dto.LabTrendDTO;
import com.climasys.trends.dto.LabTrend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for lab trends operations
 * Provides methods for retrieving patient lab test results history and trends
 */
@Service
@Transactional
public class LabTrendsService {
    
    private static final Logger logger = LoggerFactory.getLogger(LabTrendsService.class);
    
    @Autowired
    private LabTrendsRepository labTrendsRepository;
    
    /**
     * Get all previous lab test results for a patient (all dates)
     * Replicates stored procedure: USP_Get_LabTestDetails12
     * Matches the Lab Trend popup behavior - returns all previous lab results across all visit dates
     * 
     * @param patientId Patient ID
     * @return List of lab test results for all previous visits
     */
    @Transactional(readOnly = true)
    public List<LabTrendDTO> getAllLabTrendsForPatient(String patientId) {
        
        logger.info("Getting all lab trends for patient: {}", patientId);
        
        List<LabTrend> labTrends = labTrendsRepository.findAllLabTrendsForPatient(patientId);
        
        // Convert to DTOs
        List<LabTrendDTO> dtos = labTrends.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        logger.info("Retrieved {} lab test records for patient {}", dtos.size(), patientId);
        return dtos;
    }
    
    /**
     * Get previous lab test results for a specific patient visit (date-specific)
     * Replicates stored procedure: USP_Get_PreviousLabReports
     * 
     * @param patientId Patient ID
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param visitDate Visit date
     * @param shiftId Shift ID
     * @param patientVisitNo Visit number
     * @return List of lab test results for the specific visit
     */
    @Transactional(readOnly = true)
    public List<LabTrendDTO> getLabTrendsForVisit(
            String patientId, String doctorId, String clinicId,
            LocalDate visitDate, Short shiftId, Integer patientVisitNo) {
        
        logger.info("Getting lab trends for patient: {} visit: {} on {}", patientId, patientVisitNo, visitDate);
        
        List<LabTrend> labTrends = labTrendsRepository
                .findPreviousLabReports(patientId, doctorId, clinicId, visitDate, shiftId, patientVisitNo);
        
        // Convert to DTOs
        List<LabTrendDTO> dtos = labTrends.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        logger.info("Retrieved {} lab test records", dtos.size());
        return dtos;
    }
    
    /**
     * Convert LabTrend to DTO
     */
    private LabTrendDTO convertToDTO(LabTrend labTrend) {
        return new LabTrendDTO(
            labTrend.getVisitDate(),
            labTrend.getPatientVisitNo(),
            labTrend.getLabTestDescription(),
            labTrend.getParameterName(),
            labTrend.getParameterValue(),
            labTrend.getDoctorName(),
            labTrend.getLabName(),
            labTrend.getReportDate(),
            labTrend.getPatientFullName(),
            labTrend.getComment(),
            labTrend.getPatientLastVisitNo()
        );
    }
}


