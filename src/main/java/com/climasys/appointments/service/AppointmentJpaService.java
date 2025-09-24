package com.climasys.appointments.service;

import com.climasys.entity.*;
import com.climasys.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JPA-based service for appointment operations based on stored procedures
 */
@Service
public class AppointmentJpaService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentJpaService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.climasys.audit");

    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private DoctorMasterRepository doctorMasterRepository;
    
    @Autowired
    private StatusRefRepository statusRefRepository;
    
    @Autowired
    private GenderTranslationsRepository genderTranslationsRepository;
    
    @Autowired
    private FollowUpTypeRepository followUpTypeRepository;

    /**
     * USP_Get_FutureAppointments_All_New equivalent
     * Get all future appointments for a clinic
     */
    public List<Map<String, Object>> getFutureAppointmentsAllNew(String clinicId, LocalDate futureDate, Integer languageId) {
        logger.info("Getting future appointments for clinic: {} from date: {}", clinicId, futureDate);
        
        try {
            LocalDateTime futureDateTime = futureDate.atStartOfDay();
            List<PatientVisit> appointments = appointmentRepository.getFutureAppointmentsAllNew(clinicId, futureDateTime, languageId);
            return convertAppointmentsToMapList(appointments, languageId);
        } catch (Exception e) {
            logger.error("Error getting future appointments: {}", e.getMessage(), e);
            auditLogger.error("APPOINTMENT_QUERY_ERROR - Clinic: {}, Date: {}, Error: {}", clinicId, futureDate, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * USP_Get_FutureAppointments_ForGivenDate equivalent
     * Get future appointments for a specific doctor and date
     */
    public List<Map<String, Object>> getFutureAppointmentsForGivenDate(String doctorId, String clinicId, LocalDate futureDate, Integer languageId) {
        logger.info("Getting future appointments for doctor: {} on date: {}", doctorId, futureDate);
        
        try {
            LocalDateTime futureDateDateTime = futureDate.atStartOfDay();
            List<PatientVisit> appointments = appointmentRepository.getFutureAppointmentsForGivenDate(doctorId, clinicId, futureDateDateTime, languageId);
            return convertAppointmentsToMapList(appointments, languageId);
        } catch (Exception e) {
            logger.error("Error getting future appointments for date: {}", e.getMessage(), e);
            auditLogger.error("APPOINTMENT_QUERY_ERROR - Doctor: {}, Date: {}, Error: {}", doctorId, futureDate, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * USP_Get_TodaysAppointments_ForGivenDate equivalent
     * Get today's appointments for a specific doctor and date
     */
    public List<Map<String, Object>> getTodaysAppointmentsForGivenDate(String doctorId, String clinicId, LocalDateTime visitDate, Integer languageId) {
        logger.info("Getting today's appointments for doctor: {} on date: {}", doctorId, visitDate);
        
        try {
            List<PatientVisit> appointments = appointmentRepository.getTodaysAppointmentsForGivenDate(doctorId, clinicId, visitDate, languageId);
            return convertAppointmentsToMapList(appointments, languageId);
        } catch (Exception e) {
            logger.error("Error getting today's appointments: {}", e.getMessage(), e);
            auditLogger.error("APPOINTMENT_QUERY_ERROR - Doctor: {}, Date: {}, Error: {}", doctorId, visitDate, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * USP_Insert_PatientAppointment_ByDoctor equivalent
     * Book a new appointment
     */
    @Transactional
    public Map<String, Object> insertPatientAppointmentByDoctor(
            LocalDateTime visitDate,
            Integer shiftId,
            String clinicId,
            String doctorId,
            String patientId,
            LocalTime visitTime,
            Boolean reportsReceived,
            String userId,
            Boolean inPerson,
            String scheduleDay,
            LocalTime visitTimeTo) {
        
        logger.info("Booking appointment for patient: {} with doctor: {} on {}", patientId, doctorId, visitDate);
        
        try {
            // Check for conflicts
            Long conflictCount = appointmentRepository.countConflictingAppointments(doctorId, visitDate, visitTime);
            if (conflictCount > 0) {
                throw new RuntimeException("Appointment time conflict detected");
            }
            
            // Get next patient visit number
            Integer nextVisitNo = appointmentRepository.getNextPatientVisitNo(patientId);
            
            // Create new appointment
            PatientVisit appointment = new PatientVisit();
            appointment.setDoctorId(doctorId);
            appointment.setClinicId(clinicId);
            appointment.setShiftId(shiftId.shortValue());
            appointment.setPatientId(patientId);
            appointment.setPatientVisitNo(nextVisitNo);
            appointment.setVisitDate(visitDate);
            appointment.setVisitTime(java.sql.Time.valueOf(visitTime));
            appointment.setReportsReceived(reportsReceived);
            appointment.setInPerson(inPerson);
            appointment.setStatusId((short) 3); // Default status: Scheduled
            appointment.setCreatedOn(LocalDateTime.now());
            appointment.setCreatedbyName(userId);
            appointment.setDeleteFlag(false);
            appointment.setDiscount(BigDecimal.ZERO); // Set default discount to 0
            appointment.setOriginalDiscount(BigDecimal.ZERO); // Set default original discount to 0
            
            // Save appointment
            PatientVisit savedAppointment = appointmentRepository.save(appointment);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("appointmentId", savedAppointment.getPatientVisitNo());
            result.put("patientId", patientId);
            result.put("doctorId", doctorId);
            result.put("visitDate", visitDate);
            result.put("visitTime", visitTime);
            result.put("status", "SCHEDULED");
            
            auditLogger.info("APPOINTMENT_CREATED - Patient: {}, Doctor: {}, Date: {}, Time: {}", 
                           patientId, doctorId, visitDate, visitTime);
            
            return result;
            
        } catch (Exception e) {
            logger.error("Error booking appointment: {}", e.getMessage(), e);
            auditLogger.error("APPOINTMENT_CREATE_ERROR - Patient: {}, Doctor: {}, Error: {}", 
                            patientId, doctorId, e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    /**
     * USP_Delete_PatientAppointment equivalent
     * Soft delete an appointment
     */
    @Transactional
    public Map<String, Object> deletePatientAppointment(String patientId, LocalDateTime visitDate, String doctorId, String userId) {
        logger.info("Deleting appointment for patient: {} on {}", patientId, visitDate);
        
        try {
            int deletedCount = appointmentRepository.softDeleteAppointment(
                patientId, visitDate, doctorId, LocalDateTime.now(), userId);
            
            Map<String, Object> result = new HashMap<>();
            if (deletedCount > 0) {
                result.put("success", true);
                result.put("message", "Appointment deleted successfully");
                auditLogger.info("APPOINTMENT_DELETED - Patient: {}, Doctor: {}, Date: {}", 
                               patientId, doctorId, visitDate);
            } else {
                result.put("success", false);
                result.put("message", "No appointment found to delete");
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("Error deleting appointment: {}", e.getMessage(), e);
            auditLogger.error("APPOINTMENT_DELETE_ERROR - Patient: {}, Doctor: {}, Error: {}", 
                            patientId, doctorId, e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    /**
     * Update appointment status
     */
    @Transactional
    public Map<String, Object> updateAppointmentStatus(String patientId, LocalDateTime visitDate, String doctorId, Short statusId, String userId) {
        logger.info("Updating appointment status for patient: {} to status: {}", patientId, statusId);
        
        try {
            int updatedCount = appointmentRepository.updateAppointmentStatus(
                patientId, visitDate, doctorId, statusId, LocalDateTime.now(), userId);
            
            Map<String, Object> result = new HashMap<>();
            if (updatedCount > 0) {
                result.put("success", true);
                result.put("message", "Appointment status updated successfully");
                auditLogger.info("APPOINTMENT_STATUS_UPDATED - Patient: {}, Doctor: {}, Status: {}", 
                               patientId, doctorId, statusId);
            } else {
                result.put("success", false);
                result.put("message", "No appointment found to update");
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("Error updating appointment status: {}", e.getMessage(), e);
            auditLogger.error("APPOINTMENT_STATUS_UPDATE_ERROR - Patient: {}, Doctor: {}, Error: {}", 
                            patientId, doctorId, e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    /**
     * Get appointment details by patient ID
     */
    public List<Map<String, Object>> getPatientAppointmentDetails(String patientId) {
        logger.info("Getting appointment details for patient: {}", patientId);
        
        try {
            List<PatientVisit> appointments = appointmentRepository.findByPatientIdAndActive(patientId);
            return convertAppointmentsToMapList(appointments, 1); // Default language ID
        } catch (Exception e) {
            logger.error("Error getting patient appointment details: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Get status options
     */
    public List<Map<String, Object>> getStatusOptions(String clinicId) {
        try {
            List<StatusRef> statuses = statusRefRepository.findByClinicIdAndActive(clinicId);
            return statuses.stream().map(status -> {
                Map<String, Object> statusMap = new HashMap<>();
                statusMap.put("id", status.getId());
                statusMap.put("description", status.getStatusDescription());
                statusMap.put("code", status.getStatusCode());
                return statusMap;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting status options: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Get gender options
     */
    public List<Map<String, Object>> getGenderOptions(Integer languageId) {
        try {
            List<GenderTranslations> genders = genderTranslationsRepository.findByLanguageIdAndActive(languageId);
            return genders.stream().map(gender -> {
                Map<String, Object> genderMap = new HashMap<>();
                genderMap.put("genderId", gender.getGenderId());
                genderMap.put("description", gender.getGenderDescription());
                genderMap.put("code", gender.getGenderCode());
                return genderMap;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting gender options: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Get follow-up type options
     */
    public List<Map<String, Object>> getFollowUpTypeOptions() {
        try {
            List<FollowUpType> followUpTypes = followUpTypeRepository.findAllActive();
            return followUpTypes.stream().map(followUp -> {
                Map<String, Object> followUpMap = new HashMap<>();
                followUpMap.put("id", followUp.getId());
                followUpMap.put("description", followUp.getFollowUpDescription());
                followUpMap.put("code", followUp.getFollowUpCode());
                return followUpMap;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting follow-up type options: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Convert PatientVisit entities to Map format for API compatibility
     */
    private List<Map<String, Object>> convertAppointmentsToMapList(List<PatientVisit> appointments, Integer languageId) {
        return appointments.stream().map(appointment -> {
            Map<String, Object> appointmentMap = new HashMap<>();
            
            // Basic appointment info
            appointmentMap.put("patientVisitNo", appointment.getPatientVisitNo());
            appointmentMap.put("patientId", appointment.getPatientId());
            appointmentMap.put("doctorId", appointment.getDoctorId());
            appointmentMap.put("clinicId", appointment.getClinicId());
            appointmentMap.put("visitDate", appointment.getVisitDate());
            appointmentMap.put("visitTime", appointment.getVisitTime());
            appointmentMap.put("statusId", appointment.getStatusId());
            appointmentMap.put("shiftId", appointment.getShiftId());
            appointmentMap.put("folderNo", appointment.getFolderNo());
            appointmentMap.put("instructions", appointment.getInstructions());
            appointmentMap.put("inPerson", appointment.getInPerson());
            appointmentMap.put("reportsReceived", appointment.getReportsReceived());
            appointmentMap.put("onlineAppointmentTime", appointment.getOnlineAppointmentTime());
            
            // Format visit time for display
            if (appointment.getVisitTime() != null) {
                appointmentMap.put("visitTimeFormatted", appointment.getVisitTime().toString());
            }
            
            // Format visit date for display
            if (appointment.getVisitDate() != null) {
                appointmentMap.put("visitDateFormatted", appointment.getVisitDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
            }
            
            return appointmentMap;
        }).collect(Collectors.toList());
    }
}
