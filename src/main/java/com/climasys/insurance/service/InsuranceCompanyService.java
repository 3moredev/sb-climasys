package com.climasys.insurance.service;

import com.climasys.repository.InsuranceCompanyMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for insurance company management
 * Includes JPA-based dropdown functionality replacing USP_GetDDL_InsuranceComp
 */
@Service
public class InsuranceCompanyService {
    
    private static final Logger logger = LoggerFactory.getLogger(InsuranceCompanyService.class);
    
    @Autowired
    private InsuranceCompanyMasterRepository insuranceCompanyMasterRepository;

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
    
    /**
     * Get all active insurance companies for dropdown
     * Replaces USP_GetDDL_InsuranceComp stored procedure
     * Matches the business logic: SELECT DISTINCT Company_Id as ID, Company_Name as Name
     * FROM Insurance_Company_Master WHERE ISNULL(IsDeleted,0) != 1 ORDER BY Name
     * 
     * @return List of insurance companies with Name and ID (both from company_name and company_id)
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllActiveCompaniesForDropdown() {
        logger.info("Fetching all active insurance companies for dropdown");
        
        List<Map<String, Object>> companies = insuranceCompanyMasterRepository.findAllActiveCompaniesForDropdown();
        
        logger.info("Retrieved {} active insurance company/companies", companies.size());
        return companies;
    }
    
    /**
     * Get all active insurance companies as DTO list
     * 
     * @return List of insurance company DTOs
     */
    @Transactional(readOnly = true)
    public List<InsuranceCompanyDTO> getAllActiveCompaniesAsDTO() {
        logger.info("Fetching all active insurance companies as DTOs");
        
        List<Map<String, Object>> companies = insuranceCompanyMasterRepository.findAllActiveCompaniesForDropdown();
        
        List<InsuranceCompanyDTO> result = new ArrayList<>();
        for (Map<String, Object> company : companies) {
            InsuranceCompanyDTO dto = new InsuranceCompanyDTO();
            Object idValue = company.get("id");
            dto.setId(idValue != null ? idValue.toString() : "");
            dto.setName(getStringValue(company, "name"));
            result.add(dto);
        }
        
        logger.info("Retrieved {} active insurance company/companies as DTOs", result.size());
        return result;
    }
    
    /**
     * Helper method to get string value from map
     */
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
    
    /**
     * DTO class for insurance company dropdown data
     */
    public static class InsuranceCompanyDTO {
        private String id;
        private String name;
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
}
