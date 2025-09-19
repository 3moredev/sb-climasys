package com.climasys.doctors.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Service for doctor management and operations
 */
@Service
public class DoctorManagementService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.api.base-url:http://localhost:8080/api}")
    private String baseUrl;

    /**
     * Get all available doctors in the system
     */
    public List<Map<String, Object>> getAllDoctors() {
        try {
            String url = baseUrl + "/doctors/stored-procs/all-doctors";
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all doctors: " + e.getMessage(), e);
        }
    }

    /**
     * Get doctors available for adhoc appointments
     */
    public List<Map<String, Object>> getDoctorsForAdhocAppointments() {
        try {
            String url = baseUrl + "/doctors/stored-procs/all-doctors-adhoc";
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctors for adhoc appointments: " + e.getMessage(), e);
        }
    }

    /**
     * Get doctors assigned to a specific patient
     */
    public List<Map<String, Object>> getDoctorsForPatient(String patientId) {
        try {
            String url = baseUrl + "/doctors/stored-procs/all-doctors-for-patient/" + patientId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctors for patient: " + e.getMessage(), e);
        }
    }

    /**
     * Get detailed information about a specific doctor
     */
    public List<Map<String, Object>> getDoctorDetails(String doctorId) {
        try {
            String url = baseUrl + "/doctors/stored-procs/doctor-details/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctor details: " + e.getMessage(), e);
        }
    }

    /**
     * Get total count of doctors in the system
     */
    public List<Map<String, Object>> getDoctorCount() {
        try {
            String url = baseUrl + "/doctors/stored-procs/doctor-count";
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctor count: " + e.getMessage(), e);
        }
    }

    /**
     * Get doctors who are ready to submit their work
     */
    public List<Map<String, Object>> getDoctorsReadyForSubmission(String doctorId) {
        try {
            String url = baseUrl + "/doctors/stored-procs/doctors-before-submit/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctors ready for submission: " + e.getMessage(), e);
        }
    }

    /**
     * Get doctor status reference data
     */
    public List<Map<String, Object>> getDoctorStatusReference() {
        try {
            String url = baseUrl + "/doctors/stored-procs/doctor-status-reference";
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctor status reference: " + e.getMessage(), e);
        }
    }

    /**
     * Get today's visits for a specific doctor
     */
    public List<Map<String, Object>> getDoctorTodaysVisits(String doctorId) {
        try {
            String url = baseUrl + "/doctors/stored-procs/doctor-todays-visit/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctor today's visits: " + e.getMessage(), e);
        }
    }

    /**
     * Get fees to be collected by a doctor
     */
    public List<Map<String, Object>> getFeesToCollectByDoctor(String doctorId) {
        try {
            String url = baseUrl + "/doctors/stored-procs/fees-to-collect-doctor/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get fees to collect by doctor: " + e.getMessage(), e);
        }
    }
}
