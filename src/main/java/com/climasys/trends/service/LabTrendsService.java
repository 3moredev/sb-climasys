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
     * Get previous lab test results for a patient visit
     * Replicates stored procedure: USP_Get_PreviousLabReports
     * 
     * @param patientId Patient ID
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param visitDate Visit date
     * @param shiftId Shift ID
     * @param patientVisitNo Visit number
     * @return List of lab test results
     */
    @Transactional(readOnly = true)
    public List<LabTrendDTO> getLabTrends(
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


