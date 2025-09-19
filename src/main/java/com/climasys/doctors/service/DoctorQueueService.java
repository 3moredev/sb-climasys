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
 * Service for doctor queue and waiting list management
 */
@Service
public class DoctorQueueService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.api.base-url:http://localhost:8080/api}")
    private String baseUrl;

    /**
     * Get patients waiting for a specific doctor
     */
    public List<Map<String, Object>> getPatientsWaitingForDoctor(String doctorId) {
        try {
            String url = baseUrl + "/doctors/stored-procs/doctor-waiting-patient/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get patients waiting for doctor: " + e.getMessage(), e);
        }
    }

    /**
     * Get waiting status for a specific doctor
     */
    public List<Map<String, Object>> getDoctorWaitingStatus(String doctorId) {
        try {
            String url = baseUrl + "/doctors/stored-procs/doctor-waiting-status/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctor waiting status: " + e.getMessage(), e);
        }
    }
}
