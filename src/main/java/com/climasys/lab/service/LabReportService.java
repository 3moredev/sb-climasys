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
 * Service for lab reports and analytics
 */
@Service
public class LabReportService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.api.base-url:http://localhost:8080/api}")
    private String baseUrl;

    /**
     * Get labs summary report for a date range
     */
    public List<Map<String, Object>> getLabsSummaryReport(String dateFrom, String dateTo) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/lab/stored-procs/labs-summary-report")
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
            throw new RuntimeException("Failed to get labs summary report: " + e.getMessage(), e);
        }
    }

    /**
     * Get lab summary for download report
     */
    public List<Map<String, Object>> getLabSummaryForDownload(String dateFrom, String dateTo) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/lab/stored-procs/lab-summary-for-download")
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
            throw new RuntimeException("Failed to get lab summary for download: " + e.getMessage(), e);
        }
    }
}
