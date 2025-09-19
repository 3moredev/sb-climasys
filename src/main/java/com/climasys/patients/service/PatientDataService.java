package com.climasys.patients.service;

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
 * Service for patient data management and validation
 */
@Service
public class PatientDataService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.api.base-url:http://localhost:8080/api}")
    private String baseUrl;

    /**
     * Get family information and details
     */
    public List<Map<String, Object>> getFamilyInformation(String familyId) {
        try {
            String url = baseUrl + "/patients/stored-procs/family-details/" + familyId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get family information: " + e.getMessage(), e);
        }
    }

    /**
     * Get family folder details for record management
     */
    public List<Map<String, Object>> getFamilyFolderDetails(String familyId) {
        try {
            String url = baseUrl + "/patients/stored-procs/family-folder-details/" + familyId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get family folder details: " + e.getMessage(), e);
        }
    }

    /**
     * Get admission card data for patient admission
     */
    public List<Map<String, Object>> getAdmissionCardData(String patientId) {
        try {
            String url = baseUrl + "/patients/stored-procs/admission-card-data/" + patientId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get admission card data: " + e.getMessage(), e);
        }
    }

    /**
     * Validate patient billing data before processing
     */
    public List<Map<String, Object>> validatePatientBillingData(String patientId, String visitId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/patients/stored-procs/check-bill-data")
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
            throw new RuntimeException("Failed to validate patient billing data: " + e.getMessage(), e);
        }
    }

    /**
     * Validate patient discharge data
     */
    public List<Map<String, Object>> validatePatientDischargeData(String patientId, String visitId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/patients/stored-procs/check-discharge-data")
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
            throw new RuntimeException("Failed to validate patient discharge data: " + e.getMessage(), e);
        }
    }

    /**
     * Validate patient invoice data
     */
    public List<Map<String, Object>> validatePatientInvoiceData(String patientId, String visitId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/patients/stored-procs/check-invoice-data")
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
            throw new RuntimeException("Failed to validate patient invoice data: " + e.getMessage(), e);
        }
    }
}
