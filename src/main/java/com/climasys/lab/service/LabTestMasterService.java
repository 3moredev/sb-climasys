package com.climasys.lab.service;

import com.climasys.entity.LabTestMaster;
import com.climasys.repository.LabTestMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for LabTestMaster operations
 * Replaces the USP_Get_LabTest stored procedure functionality using JPA
 */
@Service
public class LabTestMasterService {
    
    private static final Logger logger = LoggerFactory.getLogger(LabTestMasterService.class);
    
    @Autowired
    private LabTestMasterRepository labTestMasterRepository;
    
    /**
     * Get lab tests for a specific doctor and clinic
     * This method replaces the USP_Get_LabTest stored procedure call
     * 
     * @param doctorId Doctor ID to get lab tests for
     * @param clinicId Clinic ID to filter lab tests
     * @return Map containing lab tests and additional data (matching stored procedure response)
     */
    public Map<String, Object> getLabTestsForDoctor(String doctorId, String clinicId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Getting lab tests for doctor: {} and clinic: {}", doctorId, clinicId);
            
            // Get lab tests ordered by priority and description (main result set from stored procedure)
            List<LabTestMaster> labTests = labTestMasterRepository.findByDoctorIdAndClinicIdOrderByPriorityAndDescription(doctorId, clinicId);
            
            // Convert to response format matching the stored procedure output
            List<Map<String, Object>> labTestList = labTests.stream()
                .map(this::convertToMap)
                .toList();
            
            response.put("success", true);
            response.put("labTests", labTestList);
            response.put("doctorId", doctorId);
            response.put("clinicId", clinicId);
            response.put("totalCount", labTests.size());
            
            logger.info("Found {} lab tests for doctor: {} and clinic: {}", labTests.size(), doctorId, clinicId);
            
        } catch (Exception e) {
            logger.error("Error getting lab tests for doctor {} and clinic {}: {}", doctorId, clinicId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get lab tests: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get all lab tests
     * 
     * @return Map containing all lab tests
     */
    public Map<String, Object> getAllLabTests() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Getting all lab tests");
            
            List<LabTestMaster> labTests = labTestMasterRepository.findAllOrderByPriorityAndDescription();
            
            List<Map<String, Object>> labTestList = labTests.stream()
                .map(this::convertToMap)
                .toList();
            
            response.put("success", true);
            response.put("labTests", labTestList);
            response.put("totalCount", labTests.size());
            
            logger.info("Found {} lab tests", labTests.size());
            
        } catch (Exception e) {
            logger.error("Error getting all lab tests: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get lab tests: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Search lab tests by description pattern
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param searchTerm Search term for lab test description
     * @return Map containing matching lab tests
     */
    public Map<String, Object> searchLabTests(String doctorId, String clinicId, String searchTerm) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Searching lab tests for doctor: {} and clinic: {} with term: {}", doctorId, clinicId, searchTerm);
            
            String searchPattern = "%" + searchTerm + "%";
            List<LabTestMaster> labTests = labTestMasterRepository.findByDoctorIdAndClinicIdAndDescriptionLike(doctorId, clinicId, searchPattern);
            
            List<Map<String, Object>> labTestList = labTests.stream()
                .map(this::convertToMap)
                .toList();
            
            response.put("success", true);
            response.put("labTests", labTestList);
            response.put("doctorId", doctorId);
            response.put("clinicId", clinicId);
            response.put("searchTerm", searchTerm);
            response.put("totalCount", labTests.size());
            
            logger.info("Found {} lab tests matching search term: {} for doctor: {} and clinic: {}", labTests.size(), searchTerm, doctorId, clinicId);
            
        } catch (Exception e) {
            logger.error("Error searching lab tests for doctor {} and clinic {} with term {}: {}", doctorId, clinicId, searchTerm, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to search lab tests: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Check if lab test exists for doctor and clinic
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param labTestDescription Lab test description
     * @return true if exists, false otherwise
     */
    public boolean labTestExists(String doctorId, String clinicId, String labTestDescription) {
        try {
            return labTestMasterRepository.existsByDoctorIdAndClinicIdAndDescription(doctorId, clinicId, labTestDescription);
        } catch (Exception e) {
            logger.error("Error checking if lab test exists for doctor {} and clinic {} and description {}: {}", doctorId, clinicId, labTestDescription, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Get lab test by doctor ID, clinic ID and description
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param labTestDescription Lab test description
     * @return LabTestMaster entity or null
     */
    public LabTestMaster getLabTestByDoctorAndDescription(String doctorId, String clinicId, String labTestDescription) {
        try {
            return labTestMasterRepository.findByDoctorIdAndClinicIdAndDescription(doctorId, clinicId, labTestDescription);
        } catch (Exception e) {
            logger.error("Error getting lab test for doctor {} and clinic {} and description {}: {}", doctorId, clinicId, labTestDescription, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Get count of lab tests for doctor and clinic
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return Count of lab tests
     */
    public long getLabTestCountForDoctor(String doctorId, String clinicId) {
        try {
            return labTestMasterRepository.countByDoctorIdAndClinicId(doctorId, clinicId);
        } catch (Exception e) {
            logger.error("Error getting lab test count for doctor {} and clinic {}: {}", doctorId, clinicId, e.getMessage(), e);
            return 0;
        }
    }
    
    /**
     * Get lab tests by group name for a doctor and clinic
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param groupName Group name
     * @return Map containing lab tests in the group
     */
    public Map<String, Object> getLabTestsByGroup(String doctorId, String clinicId, String groupName) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Getting lab tests for doctor: {} and clinic: {} and group: {}", doctorId, clinicId, groupName);
            
            List<LabTestMaster> labTests = labTestMasterRepository.findByDoctorIdAndClinicIdAndGroupName(doctorId, clinicId, groupName);
            
            List<Map<String, Object>> labTestList = labTests.stream()
                .map(this::convertToMap)
                .toList();
            
            response.put("success", true);
            response.put("labTests", labTestList);
            response.put("doctorId", doctorId);
            response.put("clinicId", clinicId);
            response.put("groupName", groupName);
            response.put("totalCount", labTests.size());
            
            logger.info("Found {} lab tests for doctor: {} and clinic: {} and group: {}", labTests.size(), doctorId, clinicId, groupName);
            
        } catch (Exception e) {
            logger.error("Error getting lab tests for doctor {} and clinic {} and group {}: {}", doctorId, clinicId, groupName, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get lab tests: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Convert LabTestMaster entity to Map for API response
     * This matches the format expected by the frontend (similar to stored procedure result)
     * 
     * @param labTest LabTestMaster entity
     * @return Map representation
     */
    private Map<String, Object> convertToMap(LabTestMaster labTest) {
        Map<String, Object> labTestMap = new HashMap<>();
        labTestMap.put("ID", labTest.getId());
        labTestMap.put("Lab_Test_Description", labTest.getLabTestDescription());
        labTestMap.put("Priority_Value", labTest.getPriorityValue());
        labTestMap.put("Doctor_ID", labTest.getDoctorId());
        labTestMap.put("Clinic_ID", labTest.getClinicId());
        labTestMap.put("Group_Name", labTest.getGroupName());
        labTestMap.put("Created_On", labTest.getCreatedOn());
        labTestMap.put("Createdby_Name", labTest.getCreatedbyName());
        labTestMap.put("Modified_On", labTest.getModifiedOn());
        labTestMap.put("Modifiedby_Name", labTest.getModifiedbyName());
        
        return labTestMap;
    }
}
