package com.climasys.appointments.service;

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
 * Service for patient examination and clinical data
 */
@Service
public class PatientExaminationService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.api.base-url:http://localhost:8080/api}")
    private String baseUrl;

    /**
     * Get gynecological ovulation data for a patient
     */
    public List<Map<String, Object>> getGynecologicalOvulationData(String patientId) {
        try {
            String url = baseUrl + "/appointments/stored-procs/gynec-ovulation-data/" + patientId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get gynecological ovulation data: " + e.getMessage(), e);
        }
    }

    /**
     * Get gynecological delivery registration data
     */
    public List<Map<String, Object>> getGynecologicalDeliveryRegistration(String patientId) {
        try {
            String url = baseUrl + "/appointments/stored-procs/gynec-delivery-registration/" + patientId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get gynecological delivery registration: " + e.getMessage(), e);
        }
    }

    /**
     * Get abdominal examination data
     */
    public List<Map<String, Object>> getAbdominalExaminationData(String patientId, String visitId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/appointments/stored-procs/abdominal-examination-data")
                    .queryParam("patientId", patientId)
                    .queryParam("visitId", visitId)
                    .toUriString();
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get abdominal examination data: " + e.getMessage(), e);
        }
    }
}
