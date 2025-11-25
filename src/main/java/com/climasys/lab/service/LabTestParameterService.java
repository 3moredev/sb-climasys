package com.climasys.lab.service;

import com.climasys.dto.LabTestAndParameterRequest;
import com.climasys.entity.LabTestMaster;
import com.climasys.entity.LabTestParameter;
import com.climasys.entity.LabTestParameterId;
import com.climasys.repository.LabTestMasterRepository;
import com.climasys.repository.LabTestParameterRepository;
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
import java.util.stream.Collectors;

/**
 * Service for LabTestParameter operations
 * Replaces the USP_Get_LabTestAndParameter stored procedure functionality using JPA
 */
@Service
public class LabTestParameterService {
    
    private static final Logger logger = LoggerFactory.getLogger(LabTestParameterService.class);
    
    @Autowired
    private LabTestParameterRepository labTestParameterRepository;
    
    @Autowired
    private LabTestMasterRepository labTestMasterRepository;
    
    /**
     * Get lab test and parameters for a specific doctor, clinic and lab test description
     * This method replaces the USP_Get_LabTestAndParameter stored procedure call
     * 
     * @param doctorId Doctor ID to get lab test parameters for
     * @param clinicId Clinic ID to filter lab test parameters
     * @param labTestDescription Lab test description to filter parameters
     * @return Map containing lab test parameters and additional data (matching stored procedure response)
     */
    public Map<String, Object> getLabTestAndParameters(String doctorId, String clinicId, String labTestDescription) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Getting lab test parameters for doctor: {} and clinic: {} and lab test: {}", doctorId, clinicId, labTestDescription);
            
            // Get lab test parameters with lab test master data (main result set from stored procedure)
            List<Object[]> labTestParameters = labTestParameterRepository
                    .findLabTestAndParametersByDoctorAndClinicAndTestDescription(doctorId, clinicId, labTestDescription);
            
            // Convert to response format matching the stored procedure output
            List<Map<String, Object>> parameterList = labTestParameters.stream()
                    .map(this::convertToMap)
                    .toList();
            
            response.put("success", true);
            response.put("labTestParameters", parameterList);
            response.put("doctorId", doctorId);
            response.put("clinicId", clinicId);
            response.put("labTestDescription", labTestDescription);
            response.put("totalCount", parameterList.size());
            
            logger.info("Found {} lab test parameters for doctor: {} and clinic: {} and lab test: {}", 
                    parameterList.size(), doctorId, clinicId, labTestDescription);
            
        } catch (Exception e) {
            logger.error("Error getting lab test parameters for doctor {} and clinic {} and lab test {}: {}", 
                    doctorId, clinicId, labTestDescription, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get lab test parameters: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get lab test parameters by doctor ID, clinic ID and lab test ID
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param labTestId Lab test ID
     * @return Map containing lab test parameters
     */
    public Map<String, Object> getLabTestParametersByTestId(String doctorId, String clinicId, Integer labTestId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Getting lab test parameters for doctor: {} and clinic: {} and lab test ID: {}", doctorId, clinicId, labTestId);
            
            List<LabTestParameter> parameters = labTestParameterRepository
                    .findByDoctorIdAndClinicIdAndLabTestId(doctorId, clinicId, labTestId);
            
            List<Map<String, Object>> parameterList = parameters.stream()
                    .map(this::convertParameterToMap)
                    .toList();
            
            response.put("success", true);
            response.put("labTestParameters", parameterList);
            response.put("doctorId", doctorId);
            response.put("clinicId", clinicId);
            response.put("labTestId", labTestId);
            response.put("totalCount", parameterList.size());
            
            logger.info("Found {} lab test parameters for doctor: {} and clinic: {} and lab test ID: {}", 
                    parameterList.size(), doctorId, clinicId, labTestId);
            
        } catch (Exception e) {
            logger.error("Error getting lab test parameters for doctor {} and clinic {} and lab test ID {}: {}", 
                    doctorId, clinicId, labTestId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get lab test parameters: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get all lab test parameters for a doctor and clinic
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return Map containing all lab test parameters for the doctor and clinic
     */
    public Map<String, Object> getAllLabTestParametersForDoctor(String doctorId, String clinicId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Getting all lab test parameters for doctor: {} and clinic: {}", doctorId, clinicId);
            
            List<LabTestParameter> parameters = labTestParameterRepository.findByDoctorIdAndClinicId(doctorId, clinicId);
            
            List<Map<String, Object>> parameterList = parameters.stream()
                    .map(this::convertParameterToMap)
                    .toList();
            
            response.put("success", true);
            response.put("labTestParameters", parameterList);
            response.put("doctorId", doctorId);
            response.put("clinicId", clinicId);
            response.put("totalCount", parameterList.size());
            
            logger.info("Found {} lab test parameters for doctor: {} and clinic: {}", parameterList.size(), doctorId, clinicId);
            
        } catch (Exception e) {
            logger.error("Error getting all lab test parameters for doctor {} and clinic {}: {}", doctorId, clinicId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get lab test parameters: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Check if lab test parameter exists
     * 
     * @param doctorId Doctor ID
     * @param labTestId Lab test ID
     * @param parameterName Parameter name
     * @return true if parameter exists, false otherwise
     */
    public boolean parameterExists(String doctorId, Integer labTestId, String parameterName) {
        return labTestParameterRepository.existsByDoctorIdAndLabTestIdAndParameterName(doctorId, labTestId, parameterName);
    }
    
    /**
     * Get count of lab test parameters for a doctor and lab test
     * 
     * @param doctorId Doctor ID
     * @param labTestId Lab test ID
     * @return Count of parameters
     */
    public long getParameterCount(String doctorId, Integer labTestId) {
        return labTestParameterRepository.countByDoctorIdAndLabTestId(doctorId, labTestId);
    }
    
    /**
     * Get all lab tests with their parameters for a specific doctor
     * This combines lab test master data with parameter data
     * 
     * @param doctorId Doctor ID to get lab tests and parameters for
     * @return Map containing all lab tests with their parameters
     */
    public Map<String, Object> getAllLabTestsWithParameters(String doctorId, String clinicId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Getting all lab tests with parameters for doctor: {}", doctorId);
            
            // Get all lab tests for the doctor
            List<LabTestMaster> labTests = labTestMasterRepository.findByDoctorIdAndClinicIdOrderByPriorityAndDescription(doctorId, clinicId);
            
            // For each lab test, get its parameters
            List<Map<String, Object>> labTestsWithParameters = labTests.stream()
                    .map(labTest -> {
                        Map<String, Object> labTestMap = new HashMap<>();
                        
                        // Add lab test master data
                        labTestMap.put("ID", labTest.getId());
                        labTestMap.put("Lab_Test_Description", labTest.getLabTestDescription());
                        labTestMap.put("Priority_Value", labTest.getPriorityValue());
                        labTestMap.put("Doctor_ID", labTest.getDoctorId());
                        labTestMap.put("Group_Name", labTest.getGroupName());
                        labTestMap.put("Created_On", labTest.getCreatedOn());
                        labTestMap.put("Createdby_Name", labTest.getCreatedbyName());
                        labTestMap.put("Modified_On", labTest.getModifiedOn());
                        labTestMap.put("Modifiedby_Name", labTest.getModifiedbyName());
                        
                        // Get parameters for this lab test
                        List<LabTestParameter> parameters = labTestParameterRepository
                                .findByDoctorIdAndLabTestIdAndClinicId(doctorId, labTest.getId(), clinicId);
                        
                        // Convert parameters to map format
                        List<Map<String, Object>> parameterList = parameters.stream()
                                .map(this::convertParameterToMap)
                                .collect(Collectors.toList());
                        
                        labTestMap.put("Parameters", parameterList);
                        labTestMap.put("Parameter_Count", parameterList.size());
                        
                        return labTestMap;
                    })
                    .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("labTestsWithParameters", labTestsWithParameters);
            response.put("doctorId", doctorId);
            response.put("totalLabTests", labTestsWithParameters.size());
            response.put("totalParameters", labTestsWithParameters.stream()
                    .mapToInt(test -> (Integer) test.get("Parameter_Count"))
                    .sum());
            
            logger.info("Found {} lab tests with total {} parameters for doctor: {}", 
                    labTestsWithParameters.size(), 
                    labTestsWithParameters.stream().mapToInt(test -> (Integer) test.get("Parameter_Count")).sum(),
                    doctorId);
            
        } catch (Exception e) {
            logger.error("Error getting all lab tests with parameters for doctor {}: {}", doctorId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get lab tests with parameters: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Convert Object[] result to Map for API response (from join query)
     * This matches the format expected by the frontend (similar to stored procedure result)
     * 
     * @param result Object array from join query
     * @return Map representation
     */
    private Map<String, Object> convertToMap(Object[] result) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("ID", result[0]); // ltp.id
        parameterMap.put("Lab_Test_ID", result[1]); // ltp.labTestId
        parameterMap.put("Lab_Test_Description", result[2]); // ltm.labTestDescription
        parameterMap.put("Lab_Test_Master_ID", result[3]); // ltm.id
        parameterMap.put("Parameter_Name", result[4]); // ltp.parameterName
        parameterMap.put("ConcatId", result[5]); // concatId
        
        return parameterMap;
    }
    
    /**
     * Update an existing lab test parameter
     * Only updates parameter name
     * Doctor ID, ID, Lab Test ID, and Clinic ID cannot be changed (they are part of the composite key)
     * 
     * @param parameter Lab test parameter to update (must include doctorId, id, labTestId, and clinicId to identify the record)
     * @return Map containing the updated parameter or error message
     */
    @Transactional
    public Map<String, Object> updateLabTestParameter(LabTestParameter parameter) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String doctorId = parameter.getDoctorId();
            Integer id = parameter.getId();
            Integer labTestId = parameter.getLabTestId();
            String clinicId = parameter.getClinicId();
            
            logger.info("Updating lab test parameter with ID: {} for lab test ID: {}, doctor: {} and clinic: {}", 
                       id, labTestId, doctorId, clinicId);
            
            // Find the existing parameter using the composite key
            LabTestParameterId parameterId = new LabTestParameterId(doctorId, id, labTestId, clinicId);
            Optional<LabTestParameter> existingOpt = labTestParameterRepository.findById(parameterId);
            
            if (existingOpt.isEmpty()) {
                logger.warn("Lab test parameter not found with ID: {}, labTestId: {}, doctorId: {}, clinicId: {}", 
                           id, labTestId, doctorId, clinicId);
                response.put("success", false);
                response.put("error", "Lab test parameter not found with ID: " + id + ", labTestId: " + labTestId + 
                             ", doctorId: " + doctorId + ", clinicId: " + clinicId);
                return response;
            }
            
            LabTestParameter existing = existingOpt.get();
            
            // Only update parameter name
            if (parameter.getParameterName() != null) {
                existing.setParameterName(parameter.getParameterName());
            }
            
            // Update modification timestamp and modifier name if provided
            existing.setModifiedOn(LocalDateTime.now());
            if (parameter.getModifiedbyName() != null) {
                existing.setModifiedbyName(parameter.getModifiedbyName());
            }
            
            LabTestParameter updated = labTestParameterRepository.save(existing);
            
            response.put("success", true);
            response.put("labTestParameter", convertParameterToMap(updated));
            response.put("message", "Lab test parameter updated successfully");
            
            logger.info("Lab test parameter updated successfully with ID: {} for lab test ID: {}, doctor: {} and clinic: {}", 
                       id, labTestId, doctorId, clinicId);
            
        } catch (Exception e) {
            logger.error("Error updating lab test parameter: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to update lab test parameter: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Delete a lab test parameter
     * 
     * @param doctorId Doctor ID
     * @param id Parameter ID
     * @param labTestId Lab test ID
     * @param clinicId Clinic ID
     * @return Map containing success status and message
     */
    @Transactional
    public Map<String, Object> deleteLabTestParameter(String doctorId, Integer id, Integer labTestId, String clinicId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Deleting lab test parameter with ID: {} for lab test ID: {}, doctor: {} and clinic: {}", 
                       id, labTestId, doctorId, clinicId);
            
            LabTestParameterId parameterId = new LabTestParameterId(doctorId, id, labTestId, clinicId);
            
            if (!labTestParameterRepository.existsById(parameterId)) {
                logger.warn("Lab test parameter not found with ID: {}, labTestId: {}, doctorId: {}, clinicId: {}", 
                           id, labTestId, doctorId, clinicId);
                response.put("success", false);
                response.put("error", "Lab test parameter not found with ID: " + id + ", labTestId: " + labTestId + 
                             ", doctorId: " + doctorId + ", clinicId: " + clinicId);
                return response;
            }
            
            labTestParameterRepository.deleteById(parameterId);
            
            response.put("success", true);
            response.put("message", "Lab test parameter deleted successfully");
            response.put("doctorId", doctorId);
            response.put("id", id);
            response.put("labTestId", labTestId);
            response.put("clinicId", clinicId);
            
            logger.info("Lab test parameter deleted successfully with ID: {} for lab test ID: {}, doctor: {} and clinic: {}", 
                       id, labTestId, doctorId, clinicId);
            
        } catch (Exception e) {
            logger.error("Error deleting lab test parameter with ID: {} for lab test ID: {}, doctor: {} and clinic: {}: {}", 
                        id, labTestId, doctorId, clinicId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to delete lab test parameter: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Insert/Update lab test and parameters
     * This method replaces the USP_Insert_LabTest_And_Parameters stored procedure functionality
     * 
     * The stored procedure logic:
     * 1. Gets max ID from Lab_Test_Master (or sets to 1 if none exists)
     * 2. MERGE operation on Lab_Test_Master:
     *    - If exists (matched on doctor_id and old_lab_test description), UPDATE with new description and new priority
     *    - If not matched, INSERT new lab test
     * 3. Gets the ID from the inserted/updated lab test
     * 4. Inserts parameters from the request into Lab_Test_Parameter table
     * 
     * @param request Request containing doctor ID, clinic ID, group name, and parameter data
     * @return Map containing the created/updated lab test and parameters or error message
     */
    @Transactional
    public Map<String, Object> insertLabTestAndParameters(LabTestAndParameterRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String doctorId = request.doctorId();
            String clinicId = request.clinicId();
            String groupName = request.groupName();
            String createdBy = request.createdBy();
            String modifiedBy = request.modifiedBy();
            Integer priority = request.priority();
            List<LabTestAndParameterRequest.LabTestParameterData> parameterDataList = request.parameterData();
            
            logger.info("Inserting/updating lab test and parameters for doctor: {}, clinic: {}", doctorId, clinicId);
            
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
            
            if (parameterDataList == null || parameterDataList.isEmpty()) {
                response.put("success", false);
                response.put("error", "Parameter data is required");
                return response;
            }
            
            // Get distinct old/new lab test pairs from parameter data (for MERGE operation)
            // This matches the stored procedure logic: SELECT DISTINCT Old_Lab_Test, New_Lab_Test, Old_Priority, New_Priority
            Map<String, LabTestAndParameterRequest.LabTestParameterData> distinctLabTests = new HashMap<>();
            for (LabTestAndParameterRequest.LabTestParameterData paramData : parameterDataList) {
                String key = (paramData.oldLabTest() != null ? paramData.oldLabTest() : "") + "|" + 
                            (paramData.newLabTest() != null ? paramData.newLabTest() : "");
                if (!distinctLabTests.containsKey(key) && paramData.newLabTest() != null && !paramData.newLabTest().trim().isEmpty()) {
                    distinctLabTests.put(key, paramData);
                }
            }
            
            if (distinctLabTests.isEmpty()) {
                response.put("success", false);
                response.put("error", "At least one new lab test description is required");
                return response;
            }
            
            // Get max ID from Lab_Test_Master for this doctor and clinic
            Integer maxId = labTestMasterRepository.findMaxIdByDoctorIdAndClinicId(doctorId, clinicId);
            if (maxId == null) {
                maxId = 0;
            }
            maxId = maxId + 1;
            
            LocalDateTime now = LocalDateTime.now();
            Integer finalLabTestId = null;
            
            // Process each distinct lab test pair (MERGE operation)
            // This matches the stored procedure MERGE logic
            for (LabTestAndParameterRequest.LabTestParameterData labTestData : distinctLabTests.values()) {
                String oldDescription = labTestData.oldLabTest();
                String newDescription = labTestData.newLabTest();
                Integer newPriorityValue = labTestData.newPriority() != null ? labTestData.newPriority() : 
                                          (priority != null ? priority : 9);
                
                // MERGE operation: Check if lab test exists with old description
                LabTestMaster existingLabTest = null;
                if (oldDescription != null && !oldDescription.trim().isEmpty()) {
                    existingLabTest = labTestMasterRepository.findByDoctorIdAndClinicIdAndDescription(doctorId, clinicId, oldDescription);
                }
                
                LabTestMaster labTest;
                if (existingLabTest != null) {
                    // UPDATE existing lab test (WHEN MATCHED)
                    logger.info("Updating existing lab test: {} to {} for doctor: {}, clinic: {}", 
                               oldDescription, newDescription, doctorId, clinicId);
                    
                    existingLabTest.setLabTestDescription(newDescription.trim());
                    existingLabTest.setPriorityValue(newPriorityValue);
                    existingLabTest.setModifiedOn(now);
                    if (modifiedBy != null && !modifiedBy.trim().isEmpty()) {
                        existingLabTest.setModifiedbyName(modifiedBy.trim());
                    }
                    
                    labTest = labTestMasterRepository.save(existingLabTest);
                    finalLabTestId = labTest.getId();
                } else {
                    // INSERT new lab test (WHEN NOT MATCHED)
                    logger.info("Creating new lab test: {} for doctor: {}, clinic: {}", 
                               newDescription, doctorId, clinicId);
                    
                    LabTestMaster newLabTest = new LabTestMaster();
                    newLabTest.setDoctorId(doctorId);
                    newLabTest.setClinicId(clinicId);
                    newLabTest.setId(maxId);
                    newLabTest.setLabTestDescription(newDescription.trim());
                    newLabTest.setPriorityValue(newPriorityValue);
                    newLabTest.setCreatedOn(now);
                    newLabTest.setModifiedOn(now);
                    
                    if (groupName != null && !groupName.trim().isEmpty()) {
                        newLabTest.setGroupName(groupName.trim());
                    }
                    if (createdBy != null && !createdBy.trim().isEmpty()) {
                        newLabTest.setCreatedbyName(createdBy.trim());
                    }
                    if (modifiedBy != null && !modifiedBy.trim().isEmpty()) {
                        newLabTest.setModifiedbyName(modifiedBy.trim());
                    }
                    
                    labTest = labTestMasterRepository.save(newLabTest);
                    finalLabTestId = labTest.getId();
                    maxId++; // Increment for next new lab test if any
                }
            }
            
            // Insert parameters (matching stored procedure: INSERT INTO Lab_Test_Parameter)
            if (finalLabTestId != null) {
                List<LabTestParameter> savedParameters = new java.util.ArrayList<>();
                
                for (LabTestAndParameterRequest.LabTestParameterData paramData : parameterDataList) {
                    String parameterName = paramData.parameterName();
                    
                    // Skip if parameter name is null or empty (matching stored procedure: WHERE Parameter_Name IS NOT NULL)
                    if (parameterName == null || parameterName.trim().isEmpty()) {
                        continue;
                    }
                    
                    // Get the next parameter ID for this doctor, lab test, and clinic combination
                    Integer maxParamId = labTestParameterRepository.findMaxIdByDoctorIdAndLabTestIdAndClinicId(
                            doctorId, finalLabTestId, clinicId);
                    Integer nextParamId = (maxParamId == null) ? 1 : maxParamId + 1;
                    
                    // Create new parameter
                    LabTestParameter newParameter = new LabTestParameter();
                    newParameter.setDoctorId(doctorId);
                    newParameter.setClinicId(clinicId);
                    newParameter.setLabTestId(finalLabTestId);
                    newParameter.setId(nextParamId);
                    newParameter.setParameterName(parameterName.trim());
                    newParameter.setCreatedOn(now);
                    newParameter.setModifiedOn(now);
                    
                    if (createdBy != null && !createdBy.trim().isEmpty()) {
                        newParameter.setCreatedbyName(createdBy.trim());
                    }
                    if (modifiedBy != null && !modifiedBy.trim().isEmpty()) {
                        newParameter.setModifiedbyName(modifiedBy.trim());
                    }
                    
                    LabTestParameter saved = labTestParameterRepository.save(newParameter);
                    savedParameters.add(saved);
                }
                
                response.put("success", true);
                response.put("labTestId", finalLabTestId);
                response.put("labTestParameters", savedParameters.stream()
                        .map(this::convertParameterToMap)
                        .collect(Collectors.toList()));
                response.put("message", "Lab test and parameters created/updated successfully");
                
                logger.info("Lab test and parameters created/updated successfully. Lab Test ID: {}, Parameters count: {}", 
                           finalLabTestId, savedParameters.size());
            } else {
                response.put("success", false);
                response.put("error", "Failed to determine lab test ID");
            }
            
        } catch (Exception e) {
            logger.error("Error inserting/updating lab test and parameters: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to insert/update lab test and parameters: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Convert LabTestParameter entity to Map for API response
     * 
     * @param parameter LabTestParameter entity
     * @return Map representation
     */
    private Map<String, Object> convertParameterToMap(LabTestParameter parameter) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("ID", parameter.getId());
        parameterMap.put("Lab_Test_ID", parameter.getLabTestId());
        parameterMap.put("Doctor_ID", parameter.getDoctorId());
        parameterMap.put("Clinic_ID", parameter.getClinicId());
        parameterMap.put("Parameter_Name", parameter.getParameterName());
        parameterMap.put("Created_On", parameter.getCreatedOn());
        parameterMap.put("Createdby_Name", parameter.getCreatedbyName());
        parameterMap.put("Modified_On", parameter.getModifiedOn());
        parameterMap.put("Modifiedby_Name", parameter.getModifiedbyName());
        
        return parameterMap;
    }
}
