package com.climasys.patients.service;

import com.climasys.repository.PatientSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for patient search functionality matching USP_Search_Patient_With_OPD stored procedure
 * 
 * This service replicates the business logic from the stored procedure:
 * - Processes search string with % wildcards around words (matching WebService preprocessing)
 * - Returns 4 result sets matching the stored procedure output
 */
@Service
public class PatientSearchService {

    @Autowired
    private PatientSearchRepository patientSearchRepository;

    /**
     * Search patients with OPD matching USP_Search_Patient_With_OPD stored procedure
     * Enhanced with multi-clinic support via clinic_id filtering
     * 
     * @param prefixText Search text (can be patient ID, name, mobile, etc.)
     * @param doctorId Doctor ID for filtering bills and invoices
     * @param clinicId Clinic ID for multi-clinic isolation
     * @return Map containing 4 result sets matching stored procedure format
     */
    public Map<String, Object> searchPatientWithOPD(String prefixText, String doctorId, String clinicId) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        // Process search string: match original WebService logic exactly
        // Original code: if (prefixText.Split(' ').Length > 0) - always processes (array always has at least 1 element)
        String searchStr = prefixText;
        if (prefixText != null && !prefixText.trim().isEmpty()) {
            String[] words = prefixText.trim().split("\\s+");
            StringBuilder filter = new StringBuilder();
            for (String word : words) {
                filter.append("%").append(word).append("%");
            }
            searchStr = filter.toString();
        }
        
        // Get all 4 result sets matching stored procedure with clinic_id filtering
        List<String> resultSet1 = patientSearchRepository.searchPatientMaster(searchStr, clinicId);
        List<String> resultSet2 = patientSearchRepository.searchDischargeData(searchStr, clinicId);
        List<String> resultSet3 = patientSearchRepository.searchDischargeBills(searchStr, doctorId, clinicId);
        List<String> resultSet4 = patientSearchRepository.searchDischargeInvoices(searchStr, doctorId, clinicId);
        
        // Match the stored procedure response structure
        // The original returns 4 separate result sets, but for API we'll combine them
        result.put("resultSet1", resultSet1); // Patient Master
        result.put("resultSet2", resultSet2); // Discharge Data
        result.put("resultSet3", resultSet3); // Discharge Bills (unprinted)
        result.put("resultSet4", resultSet4); // Discharge Invoices (unprinted)
        result.put("success", true);
        result.put("searchStr", prefixText);
        result.put("doctorId", doctorId);
        result.put("clinicId", clinicId);
        
        return result;
    }
}

