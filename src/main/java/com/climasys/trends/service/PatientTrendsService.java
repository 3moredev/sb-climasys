package com.climasys.trends.service;

import com.climasys.trends.repository.PatientTrendsRepository;
import com.climasys.trends.dto.PatientTrendsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service class for patient trends operations
 * Provides methods for retrieving patient vital signs history and trends
 */
@Service
@Transactional
public class PatientTrendsService {
    
    private static final Logger logger = LoggerFactory.getLogger(PatientTrendsService.class);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    @Autowired
    private PatientTrendsRepository patientTrendsRepository;
    
    /**
     * Get patient's last vital signs from previous visits
     * Replicates stored procedure: USP_Get_PatientLastBPDetails
     * 
     * @param patientId Patient ID
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param shiftId Shift ID
     * @param visitDate Today's visit date
     * @param patientVisitNo Current visit number
     * @return List of previous vitals (max 5 visits)
     */
    @Transactional(readOnly = true)
    public List<PatientTrendsDTO> getPatientTrends(
            String patientId, String doctorId, String clinicId,
            Short shiftId, LocalDate visitDate, Integer patientVisitNo) {
        
        logger.info("Getting trends for patient: {} visit: {}", patientId, patientVisitNo);
        
        List<Map<String, Object>> results = patientTrendsRepository
                .findPreviousTrends(patientId, clinicId, shiftId, patientVisitNo, visitDate);
        
        List<PatientTrendsDTO> trends = new ArrayList<>();
        
        for (Map<String, Object> row : results) {
            PatientTrendsDTO dto = mapResultToDTO(row);
            trends.add(dto);
        }
        
        logger.info("Retrieved {} previous trend records", trends.size());
        return trends;
    }
    
    /**
     * Map database result to DTO with formatted display values
     */
    private PatientTrendsDTO mapResultToDTO(Map<String, Object> row) {
        PatientTrendsDTO dto = new PatientTrendsDTO();
        
        // Basic fields
        dto.setVisitDate(getLocalDate(row, "visit_date"));
        dto.setPatientId(getString(row, "patient_id"));
        dto.setPatientVisitNo(getInteger(row, "patient_visit_no"));
        dto.setStatusId(getShort(row, "status_id"));
        dto.setVisitTime(getLocalTime(row, "visit_time"));
        dto.setShiftId(getShort(row, "shift_id"));
        dto.setShiftDescription(getString(row, "shift_description"));
        
        // Vital signs
        dto.setBloodPressure(getString(row, "blood_pressure"));
        dto.setSugar(getString(row, "sugar"));
        dto.setThtext(getString(row, "thtext"));
        dto.setWeightInKgs(getBigDecimal(row, "weight_in_kgs"));
        dto.setPulse(getInteger(row, "pulse"));
        dto.setHeightInCms(getBigDecimal(row, "height_in_cms"));
        
        // Clinical findings
        dto.setTpr(getString(row, "tpr"));
        dto.setImportantFindings(getString(row, "important_findings"));
        dto.setAdditionalComments(getString(row, "additional_comments"));
        dto.setSystemic(getString(row, "systemic"));
        dto.setOdeama(getString(row, "odeama"));
        dto.setPallor(getString(row, "pallor"));
        dto.setGc(getString(row, "gc"));
        
        // Generate formatted display values
        String dateStr = dto.getVisitDate() != null ? 
                dto.getVisitDate().format(DATE_FORMATTER) : "";
        String shift = dto.getShiftDescription() != null ? dto.getShiftDescription().trim() : "";
        String timeStr = dto.getVisitTime() != null ? 
                dto.getVisitTime().format(TIME_FORMATTER) : "";
        
        // Format: Date : Shift : Time (if time is available)
        String dateTimeStr = timeStr.isEmpty() ? 
                (dateStr + " : " + shift) : 
                (dateStr + " : " + shift + " : " + timeStr);
        
        dto.setLastFiveBpValues(formatValue(dateStr, shift, timeStr, dto.getBloodPressure()));
        dto.setLastFiveSugarValues(formatValue(dateStr, shift, timeStr, dto.getSugar()));
        dto.setLastFiveTHValues(formatValue(dateStr, shift, timeStr, dto.getThtext()));
        dto.setLastFiveWeightValues(formatValue(dateStr, shift, timeStr, 
                dto.getWeightInKgs() != null ? dto.getWeightInKgs().toString() : null));
        
        dto.setPreDates(dateTimeStr);
        dto.setPreBp(nvl(dto.getBloodPressure()));
        dto.setPreSugar(nvl(dto.getSugar()));
        dto.setPreThtext(nvl(dto.getThtext()));
        dto.setPreWeight(nvl(dto.getWeightInKgs() != null ? dto.getWeightInKgs().toString() : null));
        dto.setPrePulse(nvl(dto.getPulse() != null ? dto.getPulse().toString() : null));
        dto.setPreTpr(nvl(dto.getTpr()));
        dto.setPreSystemic(nvl(dto.getSystemic()));
        dto.setPreOdeama(nvl(dto.getOdeama()));
        dto.setPreHeightInCms(nvl(dto.getHeightInCms() != null ? dto.getHeightInCms().toString() : null));
        dto.setPreImportantFindings(nvl(dto.getImportantFindings()));
        dto.setPreAdditionalComments(nvl(dto.getAdditionalComments()));
        dto.setPrePallor(nvl(dto.getPallor()));
        dto.setPreGc(nvl(dto.getGc()));
        
        return dto;
    }
    
    /**
     * Format value for display (Date : Shift : Time : Value)
     */
    private String formatValue(String date, String shift, String time, String value) {
        if (time != null && !time.trim().isEmpty()) {
            return date + " : " + shift + " : " + time + " : " + nvl(value);
        } else {
            return date + " : " + shift + " : " + nvl(value);
        }
    }
    
    /**
     * Handle null values - return "--" for null or empty
     */
    private String nvl(String value) {
        return (value == null || value.trim().isEmpty()) ? "--" : value;
    }
    
    // Helper methods for type conversion
    private String getString(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value != null ? value.toString() : null;
    }
    
    private Integer getInteger(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private Short getShort(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) return null;
        if (value instanceof Short) return (Short) value;
        if (value instanceof Number) return ((Number) value).shortValue();
        try {
            return Short.parseShort(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private BigDecimal getBigDecimal(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return new BigDecimal(value.toString());
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private LocalDate getLocalDate(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) return null;
        if (value instanceof LocalDate) return (LocalDate) value;
        if (value instanceof java.sql.Date) return ((java.sql.Date) value).toLocalDate();
        if (value instanceof java.sql.Timestamp) {
            // Convert Timestamp to LocalDate (extract date part only)
            return ((java.sql.Timestamp) value).toLocalDateTime().toLocalDate();
        }
        // Try to parse as string if it's a string representation
        if (value instanceof String) {
            try {
                return LocalDate.parse((String) value);
            } catch (Exception e) {
                logger.warn("Failed to parse date string: {}", value);
                return null;
            }
        }
        logger.warn("Unsupported date type for key '{}': {}", key, value != null ? value.getClass().getName() : "null");
        return null;
    }
    
    private LocalTime getLocalTime(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) return null;
        if (value instanceof LocalTime) return (LocalTime) value;
        if (value instanceof java.sql.Time) return ((java.sql.Time) value).toLocalTime();
        if (value instanceof java.sql.Timestamp) {
            // Extract time from Timestamp
            return ((java.sql.Timestamp) value).toLocalDateTime().toLocalTime();
        }
        // Try to parse as string if it's a string representation (HH:mm:ss or HH:mm)
        if (value instanceof String) {
            try {
                String timeStr = ((String) value).trim();
                // Handle formats like "15:41:14" or "15:41"
                if (timeStr.contains(":")) {
                    String[] parts = timeStr.split(":");
                    if (parts.length >= 2) {
                        int hour = Integer.parseInt(parts[0]);
                        int minute = Integer.parseInt(parts[1]);
                        return LocalTime.of(hour, minute);
                    }
                }
                return LocalTime.parse(timeStr, TIME_FORMATTER);
            } catch (Exception e) {
                logger.warn("Failed to parse time string: {}", value);
                return null;
            }
        }
        logger.warn("Unsupported time type for key '{}': {}", key, value != null ? value.getClass().getName() : "null");
        return null;
    }
}

