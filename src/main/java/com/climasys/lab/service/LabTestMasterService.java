package com.climasys.lab.service;

import com.climasys.entity.LabTestMaster;
import com.climasys.entity.LabTestMasterId;
import com.climasys.repository.LabTestMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * Update an existing lab test
     * Only updates lab test description, group name, and priority value
     * Doctor ID, ID, and Clinic ID cannot be changed (they are part of the composite key)
     * 
     * @param labTest Lab test to update (must include doctorId, id, and clinicId to identify the record)
     * @return Map containing the updated lab test or error message
     */
    @Transactional
    public Map<String, Object> updateLabTest(LabTestMaster labTest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String doctorId = labTest.getDoctorId();
            Integer id = labTest.getId();
            String clinicId = labTest.getClinicId();
            
            logger.info("Updating lab test with ID: {} for doctor: {} and clinic: {}", id, doctorId, clinicId);
            
            // Find the existing lab test using the composite key
            LabTestMasterId labTestId = new LabTestMasterId(doctorId, id, clinicId);
            Optional<LabTestMaster> existingOpt = labTestMasterRepository.findById(labTestId);
            
            if (existingOpt.isEmpty()) {
                logger.warn("Lab test not found with ID: {}, doctorId: {}, clinicId: {}", id, doctorId, clinicId);
                response.put("success", false);
                response.put("error", "Lab test not found with ID: " + id + ", doctorId: " + doctorId + ", clinicId: " + clinicId);
                return response;
            }
            
            LabTestMaster existing = existingOpt.get();
            
            // Only update allowed fields (description, group name, priority value)
            if (labTest.getLabTestDescription() != null) {
                existing.setLabTestDescription(labTest.getLabTestDescription());
            }
            if (labTest.getGroupName() != null) {
                existing.setGroupName(labTest.getGroupName());
            }
            if (labTest.getPriorityValue() != null) {
                existing.setPriorityValue(labTest.getPriorityValue());
            }
            
            // Update modification timestamp and modifier name if provided
            existing.setModifiedOn(LocalDateTime.now());
            if (labTest.getModifiedbyName() != null) {
                existing.setModifiedbyName(labTest.getModifiedbyName());
            }
            
            LabTestMaster updated = labTestMasterRepository.save(existing);
            
            response.put("success", true);
            response.put("labTest", convertToMap(updated));
            response.put("message", "Lab test updated successfully");
            
            logger.info("Lab test updated successfully with ID: {} for doctor: {} and clinic: {}", id, doctorId, clinicId);
            
        } catch (Exception e) {
            logger.error("Error updating lab test: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to update lab test: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Create a new lab test
     * 
     * @param labTest Lab test to create (must include doctorId, clinicId, and labTestDescription)
     * @return Map containing the created lab test or error message
     */
    @Transactional
    public Map<String, Object> createLabTest(LabTestMaster labTest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String doctorId = labTest.getDoctorId();
            String clinicId = labTest.getClinicId();
            String labTestDescription = labTest.getLabTestDescription();
            
            // Validate required fields
            if (doctorId == null || doctorId.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "Doctor ID is required");
                return response;
            }
            
            if (clinicId == null || clinicId.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "Clinic ID is required");
                return response;
            }
            
            if (labTestDescription == null || labTestDescription.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "Lab test description is required");
                return response;
            }
            
            // Check if lab test already exists
            if (labTestExists(doctorId, clinicId, labTestDescription)) {
                logger.warn("Lab test already exists for doctor: {}, clinic: {}, description: {}", doctorId, clinicId, labTestDescription);
                response.put("success", false);
                response.put("error", "Lab test already exists with the same description for this doctor and clinic");
                return response;
            }
            
            // Auto-generate ID if not provided
            Integer id = labTest.getId();
            if (id == null) {
                Integer maxId = labTestMasterRepository.findMaxIdByDoctorIdAndClinicId(doctorId, clinicId);
                id = (maxId == null) ? 1 : maxId + 1;
                labTest.setId(id);
                logger.info("Auto-generated ID: {} for doctor: {} and clinic: {}", id, doctorId, clinicId);
            } else {
                // Check if ID already exists for this doctor/clinic combination
                LabTestMasterId labTestId = new LabTestMasterId(doctorId, id, clinicId);
                if (labTestMasterRepository.existsById(labTestId)) {
                    logger.warn("Lab test ID already exists: {} for doctor: {} and clinic: {}", id, doctorId, clinicId);
                    response.put("success", false);
                    response.put("error", "Lab test ID already exists for this doctor and clinic");
                    return response;
                }
            }
            
            // Set creation timestamp and creator name if not provided
            if (labTest.getCreatedOn() == null) {
                labTest.setCreatedOn(LocalDateTime.now());
            }
            
            // Set modification timestamp
            labTest.setModifiedOn(LocalDateTime.now());
            
            logger.info("Creating lab test with ID: {} for doctor: {} and clinic: {}", id, doctorId, clinicId);
            
            LabTestMaster saved = labTestMasterRepository.save(labTest);
            
            response.put("success", true);
            response.put("labTest", convertToMap(saved));
            response.put("message", "Lab test created successfully");
            
            logger.info("Lab test created successfully with ID: {} for doctor: {} and clinic: {}", id, doctorId, clinicId);
            
        } catch (Exception e) {
            logger.error("Error creating lab test: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to create lab test: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Delete a lab test
     * 
     * @param doctorId Doctor ID
     * @param id Lab test ID
     * @param clinicId Clinic ID
     * @return Map containing success status and message
     */
    @Transactional
    public Map<String, Object> deleteLabTest(String doctorId, Integer id, String clinicId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Deleting lab test with ID: {} for doctor: {} and clinic: {}", id, doctorId, clinicId);
            
            LabTestMasterId labTestId = new LabTestMasterId(doctorId, id, clinicId);
            
            if (!labTestMasterRepository.existsById(labTestId)) {
                logger.warn("Lab test not found with ID: {}, doctorId: {}, clinicId: {}", id, doctorId, clinicId);
                response.put("success", false);
                response.put("error", "Lab test not found with ID: " + id + ", doctorId: " + doctorId + ", clinicId: " + clinicId);
                return response;
            }
            
            labTestMasterRepository.deleteById(labTestId);
            
            response.put("success", true);
            response.put("message", "Lab test deleted successfully");
            response.put("doctorId", doctorId);
            response.put("id", id);
            response.put("clinicId", clinicId);
            
            logger.info("Lab test deleted successfully with ID: {} for doctor: {} and clinic: {}", id, doctorId, clinicId);
            
        } catch (Exception e) {
            logger.error("Error deleting lab test with ID: {} for doctor: {} and clinic: {}: {}", 
                        id, doctorId, clinicId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to delete lab test: " + e.getMessage());
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
