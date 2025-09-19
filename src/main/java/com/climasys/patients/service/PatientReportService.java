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
 * Service for patient reports and analytics
 */
@Service
public class PatientReportService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.api.base-url:http://localhost:8080/api}")
    private String baseUrl;

    /**
     * Get comprehensive patient report for a date range
     */
    public List<Map<String, Object>> getPatientReport(String dateFrom, String dateTo) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/patients/stored-procs/all-patients-report")
                    .queryParam("dateFrom", dateFrom)
                    .queryParam("dateTo", dateTo)
                    .toUriString();
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get patient report: " + e.getMessage(), e);
        }
    }

    /**
     * Get patient distribution by area/region
     */
    public List<Map<String, Object>> getPatientDistributionByArea(String dateFrom, String dateTo) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/patients/stored-procs/area-wise-patients")
                    .queryParam("dateFrom", dateFrom)
                    .queryParam("dateTo", dateTo)
                    .toUriString();
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get patient distribution by area: " + e.getMessage(), e);
        }
    }

    /**
     * Get family-based fee analysis
     */
    public List<Map<String, Object>> getFamilyFeeAnalysis(String dateFrom, String dateTo) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/patients/stored-procs/consolidated-family-fees")
                    .queryParam("dateFrom", dateFrom)
                    .queryParam("dateTo", dateTo)
                    .toUriString();
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get family fee analysis: " + e.getMessage(), e);
        }
    }

    /**
     * Get operator-wise family fee analysis
     */
    public List<Map<String, Object>> getOperatorFamilyFeeAnalysis(String dateFrom, String dateTo) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/patients/stored-procs/consolidated-family-fees-operator")
                    .queryParam("dateFrom", dateFrom)
                    .queryParam("dateTo", dateTo)
                    .toUriString();
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get operator family fee analysis: " + e.getMessage(), e);
        }
    }

    /**
     * Get consolidated fee report
     */
    public List<Map<String, Object>> getConsolidatedFeeReport(String dateFrom, String dateTo) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/patients/stored-procs/consolidated-fees")
                    .queryParam("dateFrom", dateFrom)
                    .queryParam("dateTo", dateTo)
                    .toUriString();
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get consolidated fee report: " + e.getMessage(), e);
        }
    }

    /**
     * Get discharge summary by date range
     */
    public List<Map<String, Object>> getDischargeSummary(String dateFrom, String dateTo) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/patients/stored-procs/discharge-data-by-date")
                    .queryParam("dateFrom", dateFrom)
                    .queryParam("dateTo", dateTo)
                    .toUriString();
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get discharge summary: " + e.getMessage(), e);
        }
    }
}
