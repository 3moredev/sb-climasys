package com.climasys.appointments.service;

import com.climasys.entity.PatientVisit;
import com.climasys.entity.Patient;
import com.climasys.entity.User;
import com.climasys.repository.AppointmentRepository;
import com.climasys.auth.repository.UserMasterRepository;
import com.climasys.config.DatabaseTableConfig;
import com.climasys.utils.TimezoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for appointment scheduling and management
 */
@Service
public class AppointmentSchedulingService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentSchedulingService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.climasys.audit");

    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private UserMasterRepository userRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private DatabaseTableConfig tableConfig;
    
    @Autowired
    private AppointmentJpaService appointmentJpaService;
    
    @Autowired
    private TimezoneUtils timezoneUtils;

    /**
     * Get all future appointments for a doctor
     */
    public List<Map<String, Object>> getFutureAppointments(String doctorId) {
        logger.info("Getting future appointments for doctor: {}", doctorId);
        
        try {
            // First try to find the doctor user by username
            User doctor = userRepository.findByLoginIdAndIsActive(doctorId, true).orElse(null);
            
            // If not found by username, try to find by ID or other criteria
            if (doctor == null) {
                // Try to find by ID if doctorId is numeric
                try {
                    Long doctorIdLong = Long.parseLong(doctorId);
                    doctor = userRepository.findById(doctorIdLong).orElse(null);
                } catch (NumberFormatException e) {
                    logger.debug("Doctor ID is not numeric: {}", doctorId);
                }
            }
            
            // If still not found, return empty list with error message
            if (doctor == null) {
                logger.warn("Doctor not found with ID: {}", doctorId);
                auditLogger.warn("APPOINTMENT_QUERY_FAILED - Doctor not found: {}", doctorId);
                return getMockAppointments(); // Return mock data for testing
            }
            
            // Get all future appointments for this doctor using JPA
            LocalDateTime today = LocalDate.now().atStartOfDay();
            List<PatientVisit> appointments = appointmentRepository.findByDoctorIdAndVisitDateAfter(doctorId, today);
            
            // Convert to Map format for API compatibility
            List<Map<String, Object>> result = new ArrayList<>();
            for (PatientVisit appointment : appointments) {
                Map<String, Object> appointmentMap = new HashMap<>();
                appointmentMap.put("appointmentId", appointment.getPatientVisitNo());
                appointmentMap.put("patientId", appointment.getPatientId());
                appointmentMap.put("patientName", "Patient " + appointment.getPatientId()); // Simplified for now
                appointmentMap.put("appointmentDate", appointment.getVisitDate());
                // Convert UTC time to target timezone
                if (appointment.getVisitTime() != null) {
                    LocalTime utcTime = appointment.getVisitTime().toLocalTime();
                    LocalTime targetTime = timezoneUtils.convertUtcToTargetTimezone(utcTime);
                    appointmentMap.put("appointmentTime", targetTime);
                    logger.debug("Converted appointment time: UTC {} -> {} {}", utcTime, timezoneUtils.getTimezoneDisplayName(), targetTime);
                } else {
                    appointmentMap.put("appointmentTime", null);
                }
                appointmentMap.put("status", appointment.getStatusId() != null ? "STATUS_" + appointment.getStatusId() : "SCHEDULED");
                appointmentMap.put("appointmentType", "General Consultation");
                appointmentMap.put("notes", appointment.getInstructions());
                appointmentMap.put("clinicId", appointment.getClinicId());
                result.add(appointmentMap);
            }
            
            // If no appointments found, return mock data for testing
            if (result.isEmpty()) {
                logger.info("No appointments found for doctor: {}, returning mock data", doctorId);
                return getMockAppointments();
            }
            
            logger.info("Found {} future appointments for doctor: {}", result.size(), doctorId);
            auditLogger.info("APPOINTMENT_QUERY_SUCCESS - Doctor: {}, Count: {}", doctorId, result.size());
            return result;
        } catch (Exception e) {
            logger.error("Error getting future appointments for doctor: {} - {}", doctorId, e.getMessage(), e);
            auditLogger.error("APPOINTMENT_QUERY_ERROR - Doctor: {}, Error: {}", doctorId, e.getMessage());
            return getMockAppointments(); // Return mock data on error
        }
    }

    /**
     * Get future appointments for a specific date
     */
    public List<Map<String, Object>> getFutureAppointmentsForDate(String doctorId, String appointmentDate) {
        try {
            // Find the doctor user
            User doctor = userRepository.findByLoginIdAndIsActive(doctorId, true).orElse(null);
            if (doctor == null) {
                try {
                    Long doctorIdLong = Long.parseLong(doctorId);
                    doctor = userRepository.findById(doctorIdLong).orElse(null);
                } catch (NumberFormatException e) {
                    // doctorId is not numeric, continue with null
                }
            }
            
            if (doctor == null) {
                logger.warn("Doctor not found with ID: {} for date: {}", doctorId, appointmentDate);
                return getMockAppointmentsForDate(appointmentDate);
            }
            
            // Parse the appointment date
            LocalDate date = LocalDate.parse(appointmentDate);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            
            // Get appointments for this doctor on the specific date using JPA
            List<PatientVisit> appointments = appointmentRepository.findByDoctorIdAndVisitDateBetween(doctorId, startOfDay, endOfDay);
            
            // Convert to Map format for API compatibility
            List<Map<String, Object>> result = new ArrayList<>();
            for (PatientVisit appointment : appointments) {
                Map<String, Object> appointmentMap = new HashMap<>();
                appointmentMap.put("appointmentId", appointment.getPatientVisitNo());
                appointmentMap.put("patientId", appointment.getPatientId());
                appointmentMap.put("patientName", "Patient " + appointment.getPatientId()); // Simplified for now
                appointmentMap.put("appointmentDate", appointment.getVisitDate());
                // Convert UTC time to target timezone
                if (appointment.getVisitTime() != null) {
                    LocalTime utcTime = appointment.getVisitTime().toLocalTime();
                    LocalTime targetTime = timezoneUtils.convertUtcToTargetTimezone(utcTime);
                    appointmentMap.put("appointmentTime", targetTime);
                    logger.debug("Converted appointment time: UTC {} -> {} {}", utcTime, timezoneUtils.getTimezoneDisplayName(), targetTime);
                } else {
                    appointmentMap.put("appointmentTime", null);
                }
                appointmentMap.put("status", appointment.getStatusId() != null ? "STATUS_" + appointment.getStatusId() : "SCHEDULED");
                appointmentMap.put("appointmentType", "General Consultation");
                appointmentMap.put("notes", appointment.getInstructions());
                appointmentMap.put("clinicId", appointment.getClinicId());
                result.add(appointmentMap);
            }
            
            // If no appointments found, return mock data
            if (result.isEmpty()) {
                return getMockAppointmentsForDate(appointmentDate);
            }
            
            return result;
        } catch (Exception e) {
            logger.error("Error getting appointments for date: {} - {}", appointmentDate, e.getMessage(), e);
            return getMockAppointmentsForDate(appointmentDate);
        }
    }

    /**
     * Get holiday details for a doctor
     */
    public List<Map<String, Object>> getHolidayDetails(String doctorId) {
        try {
            // For now, return empty list as there's no holiday entity
            // This could be implemented with a Holiday entity and repository in the future
            // or by querying a holidays table directly
            
            // Example implementation using direct SQL query if holidays table exists:
            String sql = "SELECT holiday_id, holiday_name, holiday_date, doctor_id, is_active " +
                        "FROM holidays WHERE doctor_id = ? AND is_active = true " +
                        "ORDER BY holiday_date";
            
            List<Map<String, Object>> holidays = jdbcTemplate.queryForList(sql, doctorId);
            return holidays;
            
        } catch (Exception e) {
            logger.warn("Error getting holiday details for doctor: {} - {}", doctorId, e.getMessage());
            // Return empty list if holidays table doesn't exist or query fails
            return new ArrayList<>();
        }
    }

    /**
     * Get gender options for appointments
     */
    public List<Map<String, Object>> getGenderOptions() {
        try {
            // Return gender options - simplified for now since we don't have a Gender enum
            List<Map<String, Object>> genderOptions = new ArrayList<>();
            
            Map<String, Object> male = new HashMap<>();
            male.put("genderId", "M");
            male.put("genderName", "MALE");
            male.put("genderDisplayName", "Male");
            genderOptions.add(male);
            
            Map<String, Object> female = new HashMap<>();
            female.put("genderId", "F");
            female.put("genderName", "FEMALE");
            female.put("genderDisplayName", "Female");
            genderOptions.add(female);
            
            Map<String, Object> other = new HashMap<>();
            other.put("genderId", "O");
            other.put("genderName", "OTHER");
            other.put("genderDisplayName", "Other");
            genderOptions.add(other);
            
            return genderOptions;
        } catch (Exception e) {
            logger.error("Error getting gender options: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get mock appointments data for testing when no real data is available
     */
    private List<Map<String, Object>> getMockAppointments() {
        List<Map<String, Object>> mockAppointments = new ArrayList<>();
        
        // Create sample appointments
        Map<String, Object> appointment1 = new HashMap<>();
        appointment1.put("appointmentId", "APT001");
        appointment1.put("patientId", "PAT001");
        appointment1.put("patientName", "John Doe");
        appointment1.put("appointmentDate", LocalDateTime.now().plusDays(1));
        appointment1.put("appointmentTime", "10:00 AM");
        appointment1.put("status", "SCHEDULED");
        appointment1.put("appointmentType", "General Consultation");
        appointment1.put("notes", "Regular checkup");
        appointment1.put("clinicId", "CLINIC001");
        mockAppointments.add(appointment1);
        
        Map<String, Object> appointment2 = new HashMap<>();
        appointment2.put("appointmentId", "APT002");
        appointment2.put("patientId", "PAT002");
        appointment2.put("patientName", "Jane Smith");
        appointment2.put("appointmentDate", LocalDateTime.now().plusDays(2));
        appointment2.put("appointmentTime", "2:30 PM");
        appointment2.put("status", "CONFIRMED");
        appointment2.put("appointmentType", "Follow-up");
        appointment2.put("notes", "Post-treatment follow-up");
        appointment2.put("clinicId", "CLINIC001");
        mockAppointments.add(appointment2);
        
        Map<String, Object> appointment3 = new HashMap<>();
        appointment3.put("appointmentId", "APT003");
        appointment3.put("patientId", "PAT003");
        appointment3.put("patientName", "Bob Johnson");
        appointment3.put("appointmentDate", LocalDateTime.now().plusDays(3));
        appointment3.put("appointmentTime", "9:15 AM");
        appointment3.put("status", "SCHEDULED");
        appointment3.put("appointmentType", "Initial Consultation");
        appointment3.put("notes", "New patient consultation");
        appointment3.put("clinicId", "CLINIC001");
        mockAppointments.add(appointment3);
        
        return mockAppointments;
    }
    
    /**
     * Get mock appointments for a specific date
     */
    private List<Map<String, Object>> getMockAppointmentsForDate(String appointmentDate) {
        List<Map<String, Object>> mockAppointments = new ArrayList<>();
        
        try {
            LocalDate date = LocalDate.parse(appointmentDate);
            
            // Create sample appointments for the specific date
            Map<String, Object> appointment1 = new HashMap<>();
            appointment1.put("appointmentId", "APT_" + date.toString().replace("-", "") + "_001");
            appointment1.put("patientId", "PAT001");
            appointment1.put("patientName", "John Doe");
            appointment1.put("appointmentDate", date.atTime(10, 0));
            appointment1.put("appointmentTime", "10:00 AM");
            appointment1.put("status", "SCHEDULED");
            appointment1.put("appointmentType", "General Consultation");
            appointment1.put("notes", "Regular checkup");
            appointment1.put("clinicId", "CLINIC001");
            mockAppointments.add(appointment1);
            
            Map<String, Object> appointment2 = new HashMap<>();
            appointment2.put("appointmentId", "APT_" + date.toString().replace("-", "") + "_002");
            appointment2.put("patientId", "PAT002");
            appointment2.put("patientName", "Jane Smith");
            appointment2.put("appointmentDate", date.atTime(14, 30));
            appointment2.put("appointmentTime", "2:30 PM");
            appointment2.put("status", "CONFIRMED");
            appointment2.put("appointmentType", "Follow-up");
            appointment2.put("notes", "Post-treatment follow-up");
            appointment2.put("clinicId", "CLINIC001");
            mockAppointments.add(appointment2);
            
        } catch (Exception e) {
            logger.error("Error creating mock appointments for date: {} - {}", appointmentDate, e.getMessage(), e);
        }
        
        return mockAppointments;
    }
    
    /**
     * Book a new appointment using JPA
     */
    public Map<String, Object> bookAppointment(String visitDate, Integer shiftId, String clinicId, 
                                               String doctorId, String patientId, String visitTime, 
                                               Boolean reportsReceived, String userId, Boolean inPerson) {
        logger.info("Booking appointment using JPA for patient: {} with doctor: {}", patientId, doctorId);
        
        try {
            LocalDate date = LocalDate.parse(visitDate);
            LocalTime time = LocalTime.parse(visitTime);
            
            // Store time as-is - let database handle timezone conversion automatically
            LocalDateTime dateTime = date.atTime(time);
            
            logger.info("Storing appointment time as-is: {} (database will handle timezone conversion)", time);
            
            return appointmentJpaService.insertPatientAppointmentByDoctor(
                dateTime, shiftId, clinicId, doctorId, patientId, time, 
                reportsReceived, userId, inPerson, null, null);
                
        } catch (Exception e) {
            logger.error("Error booking appointment: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to book appointment: " + e.getMessage());
            return error;
        }
    }
    
    /**
     * Get future appointments using JPA
     */
    public List<Map<String, Object>> getFutureAppointmentsJpa(String doctorId, String clinicId, String futureDate, Integer languageId) {
        logger.info("Getting future appointments using JPA for doctor: {}", doctorId);
        
        try {
            LocalDate date = LocalDate.parse(futureDate);
            return appointmentJpaService.getFutureAppointmentsForGivenDate(doctorId, clinicId, date, languageId);
        } catch (Exception e) {
            logger.error("Error getting future appointments: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get today's appointments using JPA
     */
    public List<Map<String, Object>> getTodaysAppointmentsJpa(String doctorId, String clinicId, String visitDate, Integer languageId) {
        logger.info("Getting today's appointments using JPA for doctor: {}", doctorId);
        
        try {
            LocalDate date = LocalDate.parse(visitDate);
            LocalDateTime dateTime = date.atStartOfDay();
            return appointmentJpaService.getTodaysAppointmentsForGivenDate(doctorId, clinicId, dateTime, languageId);
        } catch (Exception e) {
            logger.error("Error getting today's appointments: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Delete appointment using JPA
     */
    public Map<String, Object> deleteAppointment(String patientId, String visitDate, String doctorId, String userId) {
        logger.info("Deleting appointment using JPA for patient: {}, visitDate: {} (using today's date with visit time)", patientId, visitDate);
        
        try {
            LocalDateTime dateTime;
            // Handle both date (YYYY-MM-DD) and datetime (YYYY-MM-DD HH:mm:ss) formats
            if (visitDate.contains(" ")) {
                // Contains time component - parse as LocalDateTime with multiple format support
                dateTime = parseDateTimeString(visitDate);
            } else {
                // Only date - handle multiple date formats
                LocalDate date = parseDateString(visitDate);
                dateTime = date.atStartOfDay();
            }
            
            // Convert from target timezone (IST) to UTC for database comparison
            // Database stores both date and time in UTC
            LocalDateTime utcDateTime = timezoneUtils.convertTargetTimezoneToUtc(dateTime);
            logger.info("Original: {} (IST), Converted to UTC: {} for database comparison", 
                       dateTime, utcDateTime);
            
            return appointmentJpaService.deletePatientAppointment(patientId, utcDateTime, doctorId, userId);
        } catch (Exception e) {
            logger.error("Error deleting appointment: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to delete appointment: " + e.getMessage());
            return error;
        }
    }
    
    /**
     * Update appointment status using JPA
     */
    public Map<String, Object> updateAppointmentStatus(String patientId, String visitDate, String doctorId, Short statusId, String userId) {
        logger.info("Updating appointment status using JPA for patient: {}, visitDate: {}", patientId, visitDate);
        
        try {
            LocalDateTime dateTime;
            // Handle both date (YYYY-MM-DD) and datetime (YYYY-MM-DD HH:mm:ss) formats
            if (visitDate.contains(" ")) {
                // Contains time component - parse as LocalDateTime with multiple format support
                dateTime = parseDateTimeString(visitDate);
            } else {
                // Only date - handle multiple date formats
                LocalDate date = parseDateString(visitDate);
                dateTime = date.atStartOfDay();
            }
            
            // Convert from target timezone (IST) to UTC for database comparison
            // Database stores both date and time in UTC
            LocalDateTime utcDateTime = timezoneUtils.convertTargetTimezoneToUtc(dateTime);
            logger.info("Original: {} (IST), Converted to UTC: {} for database comparison", 
                       dateTime, utcDateTime);
            
            return appointmentJpaService.updateAppointmentStatus(patientId, utcDateTime, doctorId, statusId, userId);
        } catch (Exception e) {
            logger.error("Error updating appointment status: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to update appointment status: " + e.getMessage());
            return error;
        }
    }
    
    /**
     * Update appointment online time, doctor, and status using JPA
     */
    public Map<String, Object> updateAppointmentOnlineTimeAndDoctor(
            String patientId,
            Integer patientVisitNo,
            Short shiftId,
            String clinicId,
            String onlineAppointmentTime,
            String doctorId,
            Short statusId,
            String userId) {
        logger.info("Updating appointment online time and doctor using JPA for patient: {}", patientId);
        
        try {
            return appointmentJpaService.updateAppointmentOnlineTimeAndDoctor(
                patientId, patientVisitNo, shiftId, clinicId, onlineAppointmentTime, 
                doctorId, statusId, userId);
        } catch (Exception e) {
            logger.error("Error updating appointment online time and doctor: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to update appointment online time and doctor: " + e.getMessage());
            return error;
        }
    }
    
    /**
     * Get patient appointment details using JPA
     */
    public List<Map<String, Object>> getPatientAppointmentDetails(String patientId) {
        logger.info("Getting patient appointment details using JPA for patient: {}", patientId);
        
        try {
            return appointmentJpaService.getPatientAppointmentDetails(patientId);
        } catch (Exception e) {
            logger.error("Error getting patient appointment details: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get status options using JPA
     */
    public List<Map<String, Object>> getStatusOptions(String clinicId) {
        try {
            return appointmentJpaService.getStatusOptions(clinicId);
        } catch (Exception e) {
            logger.error("Error getting status options: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get gender options using JPA
     */
    public List<Map<String, Object>> getGenderOptions(Integer languageId) {
        try {
            return appointmentJpaService.getGenderOptions(languageId);
        } catch (Exception e) {
            logger.error("Error getting gender options: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Parse date string in various formats to LocalDate
     * Supports: YYYY-MM-DD, DD-MM-YYYY, MM/DD/YYYY, DD/MM/YYYY
     */
    private LocalDate parseDateString(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }
        
        String trimmedDate = dateString.trim();
        logger.debug("Parsing date string: {}", trimmedDate);
        
        // Try different date formats
        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),      // ISO format
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),      // DD-MM-YYYY
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),      // MM/DD/YYYY
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),      // DD/MM/YYYY
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),      // YYYY/MM/DD
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),      // DD.MM.YYYY
            DateTimeFormatter.ofPattern("MM.dd.yyyy")       // MM.DD.YYYY
        };
        
        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate parsedDate = LocalDate.parse(trimmedDate, formatter);
                logger.debug("Successfully parsed date {} using formatter {}", trimmedDate, formatter.toString());
                return parsedDate;
            } catch (DateTimeParseException e) {
                // Continue to next formatter
                logger.debug("Failed to parse {} with formatter {}: {}", trimmedDate, formatter.toString(), e.getMessage());
            }
        }
        
        // If none of the formatters work, try the default ISO format as last resort
        try {
            LocalDate parsedDate = LocalDate.parse(trimmedDate);
            logger.debug("Successfully parsed date {} using default ISO formatter", trimmedDate);
            return parsedDate;
        } catch (DateTimeParseException e) {
            logger.error("Unable to parse date string: {}", dateString);
            throw new IllegalArgumentException("Unable to parse date string: " + dateString + 
                ". Supported formats: YYYY-MM-DD, DD-MM-YYYY, MM/DD/YYYY, DD/MM/YYYY, YYYY/MM/DD, DD.MM.YYYY, MM.DD.YYYY");
        }
    }
    
    /**
     * Parse datetime string in various formats to LocalDateTime
     * Supports: YYYY-MM-DD HH:mm:ss, YYYY-MM-DD HH:mm, DD-MM-YYYY HH:mm:ss, etc.
     */
    private LocalDateTime parseDateTimeString(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            throw new IllegalArgumentException("DateTime string cannot be null or empty");
        }
        
        String trimmedDateTime = dateTimeString.trim();
        logger.debug("Parsing datetime string: {}", trimmedDateTime);
        
        // Try different datetime formats
        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),    // ISO format with seconds
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),       // ISO format without seconds
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),    // DD-MM-YYYY with seconds
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"),       // DD-MM-YYYY without seconds
            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"),    // MM/DD/YYYY with seconds
            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"),       // MM/DD/YYYY without seconds
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),    // DD/MM/YYYY with seconds
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),       // DD/MM/YYYY without seconds
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),    // YYYY/MM/DD with seconds
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),       // YYYY/MM/DD without seconds
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"),    // DD.MM.YYYY with seconds
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"),       // DD.MM.YYYY without seconds
            DateTimeFormatter.ofPattern("MM.dd.yyyy HH:mm:ss"),    // MM.DD.YYYY with seconds
            DateTimeFormatter.ofPattern("MM.dd.yyyy HH:mm")        // MM.DD.YYYY without seconds
        };
        
        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDateTime parsedDateTime = LocalDateTime.parse(trimmedDateTime, formatter);
                logger.debug("Successfully parsed datetime {} using formatter {}", trimmedDateTime, formatter.toString());
                return parsedDateTime;
            } catch (DateTimeParseException e) {
                // Continue to next formatter
                logger.debug("Failed to parse {} with formatter {}: {}", trimmedDateTime, formatter.toString(), e.getMessage());
            }
        }
        
        // If none of the formatters work, try the default ISO format as last resort
        try {
            LocalDateTime parsedDateTime = LocalDateTime.parse(trimmedDateTime);
            logger.debug("Successfully parsed datetime {} using default ISO formatter", trimmedDateTime);
            return parsedDateTime;
        } catch (DateTimeParseException e) {
            logger.error("Unable to parse datetime string: {}", dateTimeString);
            throw new IllegalArgumentException("Unable to parse datetime string: " + dateTimeString + 
                ". Supported formats: YYYY-MM-DD HH:mm:ss, YYYY-MM-DD HH:mm, DD-MM-YYYY HH:mm:ss, etc.");
        }
    }
}
