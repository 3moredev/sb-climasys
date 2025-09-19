package com.climasys.medicine.service;

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
 * Service for medicine master data management
 */
@Service
public class MedicineMasterDataService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.api.base-url:http://localhost:8080/api}")
    private String baseUrl;

    /**
     * Get active medicines for a doctor
     */
    public List<Map<String, Object>> getActiveMedicines(String doctorId) {
        try {
            String url = baseUrl + "/medicine/stored-procs/active-medicine/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get active medicines: " + e.getMessage(), e);
        }
    }

    /**
     * Get active prescriptions for a doctor
     */
    public List<Map<String, Object>> getActivePrescriptions(String doctorId) {
        try {
            String url = baseUrl + "/medicine/stored-procs/active-prescription/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get active prescriptions: " + e.getMessage(), e);
        }
    }

    /**
     * Get BLD (Before Last Date) medicine data
     */
    public List<Map<String, Object>> getBLDMedicineData(String patientId, String visitId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/medicine/stored-procs/bld-medicine")
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
            throw new RuntimeException("Failed to get BLD medicine data: " + e.getMessage(), e);
        }
    }

    /**
     * Get BLD (Before Last Date) prescription data
     */
    public List<Map<String, Object>> getBLDPrescriptionData(String patientId, String visitId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/medicine/stored-procs/bld-prescription")
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
            throw new RuntimeException("Failed to get BLD prescription data: " + e.getMessage(), e);
        }
    }

    /**
     * Get medicine categories for a doctor
     */
    public List<Map<String, Object>> getMedicineCategories(String doctorId) {
        try {
            String url = baseUrl + "/medicine/stored-procs/category-data/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get medicine categories: " + e.getMessage(), e);
        }
    }

    /**
     * Get disease master data
     */
    public List<Map<String, Object>> getDiseaseMasterData(String doctorId) {
        try {
            String url = baseUrl + "/medicine/stored-procs/disease-data/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get disease master data: " + e.getMessage(), e);
        }
    }

    /**
     * Get findings master data
     */
    public List<Map<String, Object>> getFindingsMasterData(String doctorId) {
        try {
            String url = baseUrl + "/medicine/stored-procs/findings-data/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get findings master data: " + e.getMessage(), e);
        }
    }

    /**
     * Get keyword master data
     */
    public List<Map<String, Object>> getKeywordMasterData(String doctorId) {
        try {
            String url = baseUrl + "/medicine/stored-procs/keyword-master-data/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get keyword master data: " + e.getMessage(), e);
        }
    }

    /**
     * Get keyword master data for hospital
     */
    public List<Map<String, Object>> getKeywordMasterDataForHospital(String doctorId) {
        try {
            String url = baseUrl + "/medicine/stored-procs/keyword-master-data-hospital/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get keyword master data for hospital: " + e.getMessage(), e);
        }
    }
}
