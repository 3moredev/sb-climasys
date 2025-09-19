package com.climasys.lab.service;

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
 * Service for lab data management operations
 */
@Service
public class LabDataManagementService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.api.base-url:http://localhost:8080/api}")
    private String baseUrl;

    /**
     * Delete lab test records
     */
    public List<Map<String, Object>> deleteLabTestRecords(String testId) {
        try {
            String url = baseUrl + "/lab/stored-procs/lab-test-records/" + testId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.DELETE, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete lab test records: " + e.getMessage(), e);
        }
    }

    /**
     * Delete lab test parameter
     */
    public List<Map<String, Object>> deleteLabTestParameter(String parameterId) {
        try {
            String url = baseUrl + "/lab/stored-procs/lab-test-parameter/" + parameterId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.DELETE, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete lab test parameter: " + e.getMessage(), e);
        }
    }
}
