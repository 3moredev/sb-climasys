package com.climasys.lab.service;

import com.climasys.entity.LabTestMaster;
import com.climasys.entity.LabTestParameter;
import com.climasys.repository.LabTestMasterRepository;
import com.climasys.repository.LabTestParameterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
