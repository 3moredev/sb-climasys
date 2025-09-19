package com.climasys.lab.service;

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
 * Service for lab test management and operations
 */
@Service
public class LabTestService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.api.base-url:http://localhost:8080/api}")
    private String baseUrl;

    /**
     * Get lab test information by test ID
     */
    public List<Map<String, Object>> getLabTestInfo(String testId) {
        try {
            String url = baseUrl + "/lab/stored-procs/lab-test/" + testId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get lab test info: " + e.getMessage(), e);
        }
    }

    /**
     * Get lab test with parameters
     */
    public List<Map<String, Object>> getLabTestWithParameters(String testId) {
        try {
            String url = baseUrl + "/lab/stored-procs/lab-test-and-parameter/" + testId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get lab test with parameters: " + e.getMessage(), e);
        }
    }

    /**
     * Get lab test details
     */
    public List<Map<String, Object>> getLabTestDetails(String testId) {
        try {
            String url = baseUrl + "/lab/stored-procs/lab-test-details/" + testId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get lab test details: " + e.getMessage(), e);
        }
    }

    /**
     * Get lab test details (version 1)
     */
    public List<Map<String, Object>> getLabTestDetailsV1(String testId) {
        try {
            String url = baseUrl + "/lab/stored-procs/lab-test-details-v1/" + testId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get lab test details v1: " + e.getMessage(), e);
        }
    }

    /**
     * Get lab test details (version 12)
     */
    public List<Map<String, Object>> getLabTestDetailsV12(String testId) {
        try {
            String url = baseUrl + "/lab/stored-procs/lab-test-details-v12/" + testId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get lab test details v12: " + e.getMessage(), e);
        }
    }

    /**
     * Get lab test ID by test name
     */
    public List<Map<String, Object>> getLabTestIdByName(String testName) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/lab/stored-procs/lab-test-id")
                    .queryParam("testName", testName)
                    .toUriString();
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get lab test ID by name: " + e.getMessage(), e);
        }
    }

    /**
     * Get lab test parameters
     */
    public List<Map<String, Object>> getLabTestParameters(String testId) {
        try {
            String url = baseUrl + "/lab/stored-procs/lab-test-parameter/" + testId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get lab test parameters: " + e.getMessage(), e);
        }
    }

    /**
     * Get lab test data for appointment deletion
     */
    public List<Map<String, Object>> getLabTestDataForAppointmentDeletion(String appointmentId) {
        try {
            String url = baseUrl + "/lab/stored-procs/lab-test-data-for-delete-appointment/" + appointmentId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get lab test data for appointment deletion: " + e.getMessage(), e);
        }
    }
}
