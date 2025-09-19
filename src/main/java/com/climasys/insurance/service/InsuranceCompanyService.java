package com.climasys.insurance.service;

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
 * Service for insurance company management
 */
@Service
public class InsuranceCompanyService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.api.base-url:http://localhost:8080/api}")
    private String baseUrl;

    /**
     * Edit insurance company information
     */
    public List<Map<String, Object>> editInsuranceCompany(
            String companyName, 
            String companyId, 
            String userId, 
            String doctorId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/insurance/stored-procs/edit-company")
                    .queryParam("companyName", companyName)
                    .queryParam("companyId", companyId)
                    .queryParam("userId", userId)
                    .queryParam("doctorId", doctorId)
                    .toUriString();
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.PUT, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to edit insurance company: " + e.getMessage(), e);
        }
    }

    /**
     * Insert new insurance company
     */
    public List<Map<String, Object>> insertInsuranceCompany(
            String companyName, 
            String companyId, 
            String userId, 
            String doctorId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/insurance/stored-procs/insert-company")
                    .queryParam("companyName", companyName)
                    .queryParam("companyId", companyId)
                    .queryParam("userId", userId)
                    .queryParam("doctorId", doctorId)
                    .toUriString();
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.POST, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to insert insurance company: " + e.getMessage(), e);
        }
    }

    /**
     * Delete insurance company
     */
    public List<Map<String, Object>> deleteInsuranceCompany(String companyId) {
        try {
            String url = baseUrl + "/insurance/stored-procs/delete-company/" + companyId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.DELETE, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete insurance company: " + e.getMessage(), e);
        }
    }

    /**
     * Check if discharge printing is enabled for insurance
     */
    public List<Map<String, Object>> checkDischargePrintEnabled(String patientId, String visitId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/insurance/stored-procs/check-enable-print-discharge")
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
            throw new RuntimeException("Failed to check discharge print enabled: " + e.getMessage(), e);
        }
    }
}
