package com.climasys.lab.service;

import com.climasys.dto.LabTestAndParameterRequest;
import com.climasys.entity.LabTestMaster;
import com.climasys.entity.LabTestParameter;
import com.climasys.entity.LabTestParameterId;
import com.climasys.repository.LabTestMasterRepository;
import com.climasys.repository.LabTestParameterRepository;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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
    
    @Autowired
    private EntityManager entityManager;
    
    /**
     * Get lab test and parameters for editing
     * This method replaces the USP_Get_LabTestAndParameter stored procedure call
     * Stored procedure signature: USP_Get_LabTestAndParameter(@p_var_DoctorID, @p_var_LabTestId)
     * 
     * @param doctorId Doctor ID to get lab test parameters for
     * @param labTestId Lab test ID to get parameters for
     * @return Map containing lab test and its parameters (matching stored procedure response)
     */
    public Map<String, Object> getLabTestAndParameter(String doctorId, Integer labTestId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Getting lab test and parameters for doctor: {} and lab test ID: {}", doctorId, labTestId);
            
            // Find lab test by doctor ID and lab test ID (may need to search across clinics)
            // Since stored procedure only takes doctorId and labTestId, we search for the first match
            List<LabTestMaster> labTests = labTestMasterRepository.findAll().stream()
                .filter(lt -> lt.getDoctorId().equals(doctorId) && lt.getId().equals(labTestId))
                .toList();
            
            LabTestMaster labTest = null;
            if (!labTests.isEmpty()) {
                labTest = labTests.get(0);
            }
            
            if (labTest == null) {
                response.put("success", false);
                response.put("error", "Lab test not found with doctor ID: " + doctorId + " and lab test ID: " + labTestId);
                return response;
            }
            
            String clinicId = labTest.getClinicId();
            
            // Get parameters for this lab test
            List<LabTestParameter> parameters = labTestParameterRepository
                    .findByDoctorIdAndClinicIdAndLabTestId(doctorId, clinicId, labTestId);
            
            // Convert to response format
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
            
            List<Map<String, Object>> parameterList = parameters.stream()
                    .map(this::convertParameterToMap)
                    .toList();
            
            response.put("success", true);
            response.put("labTest", labTestMap);
            response.put("parameters", parameterList);
            response.put("doctorId", doctorId);
            response.put("labTestId", labTestId);
            response.put("clinicId", clinicId);
            response.put("totalParameterCount", parameterList.size());
            
            logger.info("Found lab test and {} parameters for doctor: {} and lab test ID: {}", 
                    parameterList.size(), doctorId, labTestId);
            
        } catch (Exception e) {
            logger.error("Error getting lab test and parameters for doctor {} and lab test ID {}: {}", 
                    doctorId, labTestId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to get lab test and parameters: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get lab test and parameters for a specific doctor, clinic and lab test description
     * This method replaces the USP_Get_LabTestAndParameter stored procedure call (alternative signature)
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
     * Get lab test parameters for a specific lab test
     * This method replaces the USP_Get_LabTestParameter stored procedure call
     * Stored procedure signature: USP_Get_LabTestParameter(@p_var_DoctorID, @p_var_LabTestId)
     * 
     * @param doctorId Doctor ID
     * @param labTestId Lab test ID
     * @return Map containing lab test parameters
     */
    public Map<String, Object> getLabTestParameter(String doctorId, Integer labTestId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Getting lab test parameters for doctor: {} and lab test ID: {}", doctorId, labTestId);
            
            // Find lab test to get clinic ID
            List<LabTestMaster> labTests = labTestMasterRepository.findAll().stream()
                .filter(lt -> lt.getDoctorId().equals(doctorId) && lt.getId().equals(labTestId))
                .toList();
            
            if (labTests.isEmpty()) {
                response.put("success", false);
                response.put("error", "Lab test not found with doctor ID: " + doctorId + " and lab test ID: " + labTestId);
                return response;
            }
            
            String clinicId = labTests.get(0).getClinicId();
            
            List<LabTestParameter> parameters = labTestParameterRepository
                    .findByDoctorIdAndClinicIdAndLabTestId(doctorId, clinicId, labTestId);
            
            List<Map<String, Object>> parameterList = parameters.stream()
                    .map(this::convertParameterToMap)
                    .toList();
            
            response.put("success", true);
            response.put("labTestParameters", parameterList);
            response.put("doctorId", doctorId);
            response.put("labTestId", labTestId);
            response.put("clinicId", clinicId);
            response.put("totalCount", parameterList.size());
            
            logger.info("Found {} lab test parameters for doctor: {} and lab test ID: {}", 
                    parameterList.size(), doctorId, labTestId);
            
        } catch (Exception e) {
            logger.error("Error getting lab test parameters for doctor {} and lab test ID {}: {}", 
                    doctorId, labTestId, e.getMessage(), e);
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
     * Delete a parameter from a lab test
     * This method replaces the USP_Delete_Parameters stored procedure call
     * Stored procedure signature: USP_Delete_Parameters(@p_var_ID, @p_var_labtest_id)
     * 
     * @param id Parameter ID
     * @param labTestId Lab test ID
     * @return Map containing success status and message
     */
    @Transactional
    public Map<String, Object> deleteParameter(Integer id, Integer labTestId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Deleting parameter with ID: {} for lab test ID: {}", id, labTestId);
            
            // Find the parameter by ID and lab test ID (need to search across all doctors/clinics)
            List<LabTestParameter> parameters = labTestParameterRepository.findAll().stream()
                .filter(p -> p.getId().equals(id) && p.getLabTestId().equals(labTestId))
                .toList();
            
            if (parameters.isEmpty()) {
                logger.warn("Parameter not found with ID: {} and lab test ID: {}", id, labTestId);
                response.put("success", false);
                response.put("error", "Parameter not found with ID: " + id + " and lab test ID: " + labTestId);
                return response;
            }
            
            LabTestParameter parameter = parameters.get(0);
            LabTestParameterId parameterId = new LabTestParameterId(
                parameter.getDoctorId(), 
                parameter.getId(), 
                parameter.getLabTestId(), 
                parameter.getClinicId()
            );
            
            labTestParameterRepository.deleteById(parameterId);
            
            response.put("success", true);
            response.put("message", "Parameter deleted successfully");
            response.put("id", id);
            response.put("labTestId", labTestId);
            
            logger.info("Parameter deleted successfully with ID: {} for lab test ID: {}", id, labTestId);
            
        } catch (Exception e) {
            logger.error("Error deleting parameter with ID: {} for lab test ID: {}: {}", 
                        id, labTestId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to delete parameter: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Delete a lab test parameter (with full context)
     * This method replaces the USP_Delete_LabtestParameter stored procedure call
     * Stored procedure signature: USP_Delete_LabtestParameter(@p_var_Visit_Date, @p_var_Patient_Visit_No, 
     * @p_var_Shift_Id, @p_var_Clinic_Id, @p_var_Doctor_Id, @p_var_Patient_Id, @p_var_LabTest_Description, @p_var_ParameterName)
     * 
     * @param visitDate Visit date
     * @param patientVisitNo Patient visit number
     * @param shiftId Shift ID
     * @param clinicId Clinic ID
     * @param doctorId Doctor ID
     * @param patientId Patient ID
     * @param labTestDescription Lab test description
     * @param parameterName Parameter name
     * @return Map containing success status and message
     */
    @Transactional
    public Map<String, Object> deleteLabtestParameter(String visitDate, String patientVisitNo, String shiftId, 
                                                      String clinicId, String doctorId, String patientId,
                                                      String labTestDescription, String parameterName) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Deleting lab test parameter for visit date: {}, visit no: {}, doctor: {}, clinic: {}, " +
                       "lab test: {}, parameter: {}", visitDate, patientVisitNo, doctorId, clinicId, 
                       labTestDescription, parameterName);
            
            // Find the lab test by description
            LabTestMaster labTest = labTestMasterRepository.findByDoctorIdAndClinicIdAndDescription(
                doctorId, clinicId, labTestDescription);
            
            if (labTest == null) {
                response.put("success", false);
                response.put("error", "Lab test not found with description: " + labTestDescription);
                return response;
            }
            
            // Find the parameter by name
            List<LabTestParameter> parameters = labTestParameterRepository
                .findByDoctorIdAndClinicIdAndLabTestId(doctorId, clinicId, labTest.getId()).stream()
                .filter(p -> parameterName.equals(p.getParameterName()))
                .toList();
            
            if (parameters.isEmpty()) {
                response.put("success", false);
                response.put("error", "Parameter not found with name: " + parameterName);
                return response;
            }
            
            LabTestParameter parameter = parameters.get(0);
            LabTestParameterId parameterId = new LabTestParameterId(
                parameter.getDoctorId(), 
                parameter.getId(), 
                parameter.getLabTestId(), 
                parameter.getClinicId()
            );
            
            labTestParameterRepository.deleteById(parameterId);
            
            response.put("success", true);
            response.put("message", "Lab test parameter deleted successfully");
            response.put("visitDate", visitDate);
            response.put("patientVisitNo", patientVisitNo);
            response.put("shiftId", shiftId);
            response.put("clinicId", clinicId);
            response.put("doctorId", doctorId);
            response.put("patientId", patientId);
            response.put("labTestDescription", labTestDescription);
            response.put("parameterName", parameterName);
            
            logger.info("Lab test parameter deleted successfully");
            
        } catch (Exception e) {
            logger.error("Error deleting lab test parameter: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to delete lab test parameter: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Delete a lab test parameter (with full composite key)
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
    @Transactional(rollbackFor = Exception.class)
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
            
            // Get lab test description from root level (frontend sends Description/New_Description at root)
            String rootNewDescription = request.description();
            String rootOldDescription = request.oldDescription();
            
            if (parameterDataList == null || parameterDataList.isEmpty()) {
                response.put("success", false);
                response.put("error", "Parameter data is required");
                return response;
            }
            
            // Get distinct old/new lab test pairs from parameter data (for MERGE operation)
            // This matches the stored procedure logic: SELECT DISTINCT Old_Lab_Test, New_Lab_Test, Old_Priority, New_Priority
            Map<String, LabTestAndParameterRequest.LabTestParameterData> distinctLabTests = new HashMap<>();
            
            // If root-level description is provided, use it (frontend sends Description/New_Description at root)
            if (rootNewDescription != null && !rootNewDescription.trim().isEmpty()) {
                // Create a synthetic entry with root-level values
                LabTestAndParameterRequest.LabTestParameterData rootLabTestData = 
                    new LabTestAndParameterRequest.LabTestParameterData(
                        null, // parameterName - will be set from parameters array
                        rootOldDescription,
                        rootNewDescription,
                        null, // oldPriority
                        priority
                    );
                String key = (rootOldDescription != null ? rootOldDescription : "") + "|" + rootNewDescription;
                distinctLabTests.put(key, rootLabTestData);
            } else {
                // Extract from parameter data if root-level is not provided
                for (LabTestAndParameterRequest.LabTestParameterData paramData : parameterDataList) {
                    String newLabTest = paramData.newLabTest();
                    String oldLabTest = paramData.oldLabTest();
                    
                    // Use root-level values if parameter-level values are missing
                    if (newLabTest == null || newLabTest.trim().isEmpty()) {
                        newLabTest = rootNewDescription;
                    }
                    if (oldLabTest == null || oldLabTest.trim().isEmpty()) {
                        oldLabTest = rootOldDescription;
                    }
                    
                    if (newLabTest != null && !newLabTest.trim().isEmpty()) {
                        String key = (oldLabTest != null ? oldLabTest : "") + "|" + newLabTest;
                        if (!distinctLabTests.containsKey(key)) {
                            distinctLabTests.put(key, paramData);
                        }
                    }
                }
            }
            
            if (distinctLabTests.isEmpty()) {
                response.put("success", false);
                response.put("error", "At least one new lab test description is required. Please provide 'Description' or 'New_Description' at root level, or 'newLabTest' in parameter data.");
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
                    
                    // Insert parameter using native query to exclude id column
                    // The id column is GENERATED ALWAYS, so we must not include it in INSERT
                    String insertSql = """
                        INSERT INTO lab_test_parameter (
                            doctor_id, clinic_id, lab_test_id, parameter_name,
                            created_on, createdby_name, modified_on, modifiedby_name
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        """;
                    
                    entityManager.createNativeQuery(insertSql)
                            .setParameter(1, doctorId)
                            .setParameter(2, clinicId)
                            .setParameter(3, finalLabTestId)
                            .setParameter(4, parameterName.trim())
                            .setParameter(5, now)
                            .setParameter(6, createdBy)
                            .setParameter(7, now)
                            .setParameter(8, modifiedBy)
                            .executeUpdate();
                    
                    // Flush to execute the INSERT
                    entityManager.flush();
                    
                    // Query back to get the generated ID using the other composite key fields
                    List<LabTestParameter> foundParams = labTestParameterRepository
                            .findByDoctorIdAndLabTestIdAndClinicId(doctorId, finalLabTestId, clinicId);
                    
                    // Find the parameter we just inserted by matching parameter name and timestamps
                    LabTestParameter savedWithId = foundParams.stream()
                            .filter(p -> parameterName.trim().equals(p.getParameterName()) &&
                                       p.getCreatedOn() != null &&
                                       Math.abs(java.time.Duration.between(p.getCreatedOn(), now).getSeconds()) < 5)
                            .findFirst()
                            .orElse(null);
                    
                    if (savedWithId != null) {
                        savedParameters.add(savedWithId);
                    } else {
                        logger.warn("Could not find inserted parameter with name: {} after insert", parameterName);
                    }
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
            
            // Log the full stack trace for debugging
            if (e.getCause() != null) {
                logger.error("Root cause: {}", e.getCause().getMessage(), e.getCause());
            }
            
            // Mark transaction for rollback
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            
            response.put("success", false);
            String errorMessage = e.getMessage();
            if (e.getCause() != null) {
                errorMessage += " - Root cause: " + e.getCause().getMessage();
            }
            response.put("error", "Failed to insert/update lab test and parameters: " + errorMessage);
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
