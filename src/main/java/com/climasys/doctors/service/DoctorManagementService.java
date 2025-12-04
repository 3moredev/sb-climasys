package com.climasys.doctors.service;

import com.climasys.auth.entity.AuthDoctorMaster;
import com.climasys.auth.repository.AuthDoctorMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for doctor management and operations
 * Uses JPA implementation with logic from stored procedures
 */
@Service
public class DoctorManagementService {

    @Autowired
    private AuthDoctorMasterRepository doctorMasterRepository;

    /**
     * Get all available doctors in the system
     * Based on stored procedure: usp_get_all_doctors()
     */
    public List<Map<String, Object>> getAllDoctors() {
        try {
            List<AuthDoctorMaster> doctors = doctorMasterRepository.findAll();
            
            return doctors.stream()
                    .map(this::convertDoctorToMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all doctors: " + e.getMessage(), e);
        }
    }

    /**
     * Get doctors available for adhoc appointments
     * Based on stored procedure logic: doctors who are active and available for adhoc
     */
    public List<Map<String, Object>> getDoctorsForAdhocAppointments() {
        try {
            // For now, return all active doctors as adhoc available
            // In a real implementation, this would check availability status
            return getAllDoctors();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctors for adhoc appointments: " + e.getMessage(), e);
        }
    }

    /**
     * Get doctors assigned to a specific patient
     * Based on stored procedure logic: doctors who have treated this patient
     */
    public List<Map<String, Object>> getDoctorsForPatient(String patientId) {
        try {
            // For now, return all active doctors
            // In a real implementation, this would query patient_visits table
            return getAllDoctors();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctors for patient: " + e.getMessage(), e);
        }
    }

    /**
     * Get detailed information about a specific doctor
     * Based on stored procedure logic: get doctor by ID
     */
    public List<Map<String, Object>> getDoctorDetails(String doctorId) {
        try {
            return doctorMasterRepository.findByDoctorId(doctorId)
                    .map(doctor -> List.of(convertDoctorToMap(doctor)))
                    .orElse(List.of());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctor details: " + e.getMessage(), e);
        }
    }

    /**
     * Get total count of doctors in the system
     * Based on stored procedure: usp_get_doctor_count()
     */
    public List<Map<String, Object>> getDoctorCount() {
        try {
            List<AuthDoctorMaster> allDoctors = doctorMasterRepository.findAll();
            long totalDoctors = allDoctors.size();
            long activeDoctors = allDoctors.size(); // All doctors are considered active in this schema
            long inactiveDoctors = totalDoctors - activeDoctors;
            
            Map<String, Object> countData = new HashMap<>();
            countData.put("total_doctors", totalDoctors);
            countData.put("active_doctors", activeDoctors);
            countData.put("inactive_doctors", inactiveDoctors);
            countData.put("doctors_with_clinics", activeDoctors); // Simplified for now
            
            return List.of(countData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctor count: " + e.getMessage(), e);
        }
    }

    /**
     * Get doctors who are ready to submit their work
     * Based on stored procedure logic: doctors with pending work
     */
    public List<Map<String, Object>> getDoctorsReadyForSubmission(String doctorId) {
        try {
            // For now, return empty list
            // In a real implementation, this would check for pending work
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctors ready for submission: " + e.getMessage(), e);
        }
    }

    /**
     * Get doctor status reference data
     * Based on stored procedure logic: get status options
     */
    public List<Map<String, Object>> getDoctorStatusReference() {
        try {
            // Return common doctor status options
            return List.of(
                Map.of("status_id", 1, "status_name", "Active"),
                Map.of("status_id", 2, "status_name", "Inactive"),
                Map.of("status_id", 3, "status_name", "On Leave"),
                Map.of("status_id", 4, "status_name", "Retired")
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctor status reference: " + e.getMessage(), e);
        }
    }

    /**
     * Get today's visits for a specific doctor
     * Based on stored procedure logic: get today's visits
     */
    public List<Map<String, Object>> getDoctorTodaysVisits(String doctorId) {
        try {
            // For now, return empty list
            // In a real implementation, this would query patient_visits table
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctor today's visits: " + e.getMessage(), e);
        }
    }

    /**
     * Get fees to be collected by a doctor
     * Based on stored procedure logic: get pending fees
     */
    public List<Map<String, Object>> getFeesToCollectByDoctor(String doctorId) {
        try {
            // For now, return empty list
            // In a real implementation, this would query billing tables
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get fees to collect by doctor: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to convert AuthDoctorMaster entity to Map
     * Based on stored procedure output format
     */
    private Map<String, Object> convertDoctorToMap(AuthDoctorMaster doctor) {
        Map<String, Object> doctorMap = new HashMap<>();
        doctorMap.put("doctor_id", doctor.getDoctorId());
        
        // Build full name like in stored procedure
        StringBuilder fullName = new StringBuilder();
        if (doctor.getFirstName() != null) fullName.append(doctor.getFirstName());
        if (doctor.getMiddleName() != null) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(doctor.getMiddleName());
        }
        if (doctor.getLastName() != null) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(doctor.getLastName());
        }
        doctorMap.put("doctor_name", fullName.toString());
        
        doctorMap.put("qualification", doctor.getDoctorQual());
        doctorMap.put("specialization", doctor.getSpeciality());
        doctorMap.put("phone", doctor.getMobile1());
        doctorMap.put("email", doctor.getEmailid());
        doctorMap.put("is_active", true); // All doctors are considered active in this schema
        doctorMap.put("address", doctor.getResidentialAdd1()); // Use residential address
        // OPD/IPD doctor flags
        doctorMap.put("opd_dr", doctor.getOpdDr() != null ? doctor.getOpdDr() : false);
        doctorMap.put("ipd_dr", doctor.getIpdDr() != null ? doctor.getIpdDr() : false);
        
        return doctorMap;
    }
}
