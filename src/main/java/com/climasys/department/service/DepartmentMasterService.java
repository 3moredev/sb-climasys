package com.climasys.department.service;

import com.climasys.repository.DepartmentMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service class for department master operations
 * Replaces USP_GetDDL_Department stored procedure business logic
 */
@Service
@Transactional
public class DepartmentMasterService {
    
    private static final Logger logger = LoggerFactory.getLogger(DepartmentMasterService.class);
    
    @Autowired
    private DepartmentMasterRepository departmentMasterRepository;
    
    /**
     * Get all departments for dropdown
     * Matches the main functionality of USP_GetDDL_Department
     * Returns distinct departments with Name and ID (both are department_name)
     * 
     * @return List of departments in format: [{name: "Medicine", id: "Medicine"}, ...]
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllDepartments() {
        logger.info("Fetching all distinct departments");
        
        List<Map<String, Object>> departments = departmentMasterRepository.findAllDistinctDepartments();
        
        logger.info("Retrieved {} distinct department(s)", departments.size());
        return departments;
    }
    
    /**
     * Get departments as DTO list with consistent structure
     * 
     * @return List of department DTOs
     */
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartmentsAsDTO() {
        logger.info("Fetching all distinct departments as DTOs");
        
        List<Map<String, Object>> departments = departmentMasterRepository.findAllDistinctDepartments();
        
        List<DepartmentDTO> result = new ArrayList<>();
        for (Map<String, Object> dept : departments) {
            DepartmentDTO dto = new DepartmentDTO();
            dto.setId(getStringValue(dept, "id"));
            dto.setName(getStringValue(dept, "name"));
            result.add(dto);
        }
        
        logger.info("Retrieved {} distinct department(s) as DTOs", result.size());
        return result;
    }
    
    /**
     * Get doctors for a specific department
     * Matches the query from USP_GetDDL_Department when filtering by department name
     * 
     * @param departmentName Department name
     * @return List of doctors in that department
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDoctorsByDepartment(String departmentName) {
        logger.info("Fetching doctors for department: {}", departmentName);
        
        List<Map<String, Object>> doctors = departmentMasterRepository.findDoctorsByDepartment(departmentName);
        
        logger.info("Retrieved {} doctor(s) for department: {}", doctors.size(), departmentName);
        return doctors;
    }
    
    /**
     * Get all department names as simple string list
     * 
     * @return List of department names
     */
    @Transactional(readOnly = true)
    public List<String> getAllDepartmentNames() {
        logger.info("Fetching all distinct department names");
        
        List<String> departmentNames = departmentMasterRepository.findAllDepartmentNames();
        
        logger.info("Retrieved {} distinct department name(s)", departmentNames.size());
        return departmentNames;
    }
    
    /**
     * Helper method to get string value from map
     */
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
    
    /**
     * DTO class for department data
     */
    public static class DepartmentDTO {
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

