package com.climasys.doctors.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * Service for clinic management and operations
 */
@Service
public class ClinicManagementService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.api.base-url:http://localhost:8080/api}")
    private String baseUrl;

    /**
     * Get clinic details and information
     */
    public List<Map<String, Object>> getClinicDetails(String clinicId) {
        try {
            String url = baseUrl + "/doctors/stored-procs/clinic-details/" + clinicId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get clinic details: " + e.getMessage(), e);
        }
    }

    /**
     * Get clinic shifts and schedules
     */
    public List<Map<String, Object>> getClinicShifts(String clinicId) {
        try {
            String url = baseUrl + "/doctors/stored-procs/clinic-shifts/" + clinicId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get clinic shifts: " + e.getMessage(), e);
        }
    }

    /**
     * Get clinic shift timings for a specific day
     */
    public List<Map<String, Object>> getClinicShiftTimings(String clinicId, String shiftDay) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/doctors/stored-procs/clinic-shifts-time")
                    .queryParam("clinicId", clinicId)
                    .queryParam("shiftDay", shiftDay)
                    .toUriString();
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get clinic shift timings: " + e.getMessage(), e);
        }
    }
}
