package com.climasys.billing.service;

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
 * Service for billing calculations and amount management
 */
@Service
public class BillingCalculationService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.api.base-url:http://localhost:8080/api}")
    private String baseUrl;

    /**
     * Calculate total amount for a patient visit
     */
    public List<Map<String, Object>> calculateVisitAmount(String patientId, String visitId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/billing/stored-procs/amount")
                    .queryParam("patientId", patientId)
                    .queryParam("visitId", visitId)
                    .toUriString();
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            List<Map<String, Object>> result = response.getBody();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate visit amount: " + e.getMessage(), e);
        }
    }

    /**
     * Get billing status for a patient visit
     */
    public List<Map<String, Object>> getBillingStatus(String patientId, String visitId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/billing/stored-procs/amount-status")
                    .queryParam("patientId", patientId)
                    .queryParam("visitId", visitId)
                    .toUriString();
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            List<Map<String, Object>> result = response.getBody();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get billing status: " + e.getMessage(), e);
        }
    }

    /**
     * Get default fees for a doctor
     */
    public List<Map<String, Object>> getDefaultFees(String doctorId) {
        try {
            String url = baseUrl + "/billing/stored-procs/default-fees/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            List<Map<String, Object>> result = response.getBody();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get default fees: " + e.getMessage(), e);
        }
    }

    /**
     * Get default prescription count for a doctor
     */
    public List<Map<String, Object>> getDefaultPrescriptionCount(String doctorId) {
        try {
            String url = baseUrl + "/billing/stored-procs/default-prescription-count/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            List<Map<String, Object>> result = response.getBody();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get default prescription count: " + e.getMessage(), e);
        }
    }

    /**
     * Generate hospital bill for a patient visit
     */
    public List<Map<String, Object>> generateHospitalBill(String patientId, String visitId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/billing/stored-procs/hospital-bill")
                    .queryParam("patientId", patientId)
                    .queryParam("visitId", visitId)
                    .toUriString();
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            List<Map<String, Object>> result = response.getBody();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate hospital bill: " + e.getMessage(), e);
        }
    }
}
