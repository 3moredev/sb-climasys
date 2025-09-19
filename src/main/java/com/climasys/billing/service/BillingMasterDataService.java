package com.climasys.billing.service;

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
 * Service for billing master data management
 */
@Service
public class BillingMasterDataService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.api.base-url:http://localhost:8080/api}")
    private String baseUrl;

    /**
     * Get billing categories grouped by doctor
     */
    public List<Map<String, Object>> getBillingCategories(String doctorId) {
        try {
            String url = baseUrl + "/billing/stored-procs/category-data-billing-group/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get billing categories: " + e.getMessage(), e);
        }
    }

    /**
     * Get companies for billing
     */
    public List<Map<String, Object>> getBillingCompanies(String doctorId) {
        try {
            String url = baseUrl + "/billing/stored-procs/company/" + doctorId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get billing companies: " + e.getMessage(), e);
        }
    }

    /**
     * Delete billing charges
     */
    public List<Map<String, Object>> deleteBillingCharges(String chargeId) {
        try {
            String url = baseUrl + "/billing/stored-procs/bill-charges/" + chargeId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.DELETE, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete billing charges: " + e.getMessage(), e);
        }
    }

    /**
     * Delete bill keyword charges
     */
    public List<Map<String, Object>> deleteBillKeywordCharges(String keywordChargeId) {
        try {
            String url = baseUrl + "/billing/stored-procs/bill-keyword-charges/" + keywordChargeId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.DELETE, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete bill keyword charges: " + e.getMessage(), e);
        }
    }

    /**
     * Delete bill sub charges
     */
    public List<Map<String, Object>> deleteBillSubCharges(String subChargeId) {
        try {
            String url = baseUrl + "/billing/stored-procs/bill-sub-charges/" + subChargeId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.DELETE, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete bill sub charges: " + e.getMessage(), e);
        }
    }

    /**
     * Delete master billing detail
     */
    public List<Map<String, Object>> deleteMasterBillingDetail(String billingDetailId) {
        try {
            String url = baseUrl + "/billing/stored-procs/master-billing-detail/" + billingDetailId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.DELETE, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete master billing detail: " + e.getMessage(), e);
        }
    }

    /**
     * Delete master company
     */
    public List<Map<String, Object>> deleteMasterCompany(String companyId) {
        try {
            String url = baseUrl + "/billing/stored-procs/master-company/" + companyId;
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, 
                    HttpMethod.DELETE, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete master company: " + e.getMessage(), e);
        }
    }
}
