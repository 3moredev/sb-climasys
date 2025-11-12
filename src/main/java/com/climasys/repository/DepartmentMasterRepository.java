package com.climasys.repository;

import com.climasys.entity.DoctorsDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository for department master operations
 * Replaces USP_GetDDL_Department stored procedure
 */
@Repository
public interface DepartmentMasterRepository extends JpaRepository<DoctorsDepartment, com.climasys.entity.DoctorsDepartmentId> {
    
    /**
     * Get all distinct departments
     * Matches the main query from USP_GetDDL_Department:
     * SELECT distinct Department_Name as Name, Department_Name as ID FROM Doctors_Department
     * 
     * @return List of departments with Name and ID (both are department_name)
     */
    @Query(value = """
        SELECT DISTINCT
            department_name AS name,
            department_name AS id
        FROM doctors_department
        ORDER BY department_name ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findAllDistinctDepartments();
    
    /**
     * Get doctors for a specific department
     * Matches the query from USP_GetDDL_Department when filtering by department name
     * 
     * @param departmentName Department name to filter by
     * @return List of doctors with Name and ID (both are doctor_name)
     */
    @Query(value = """
        SELECT 
            doctor_name AS name,
            doctor_name AS id
        FROM doctors_department
        WHERE department_name = :departmentName
        ORDER BY doctor_name ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findDoctorsByDepartment(@Param("departmentName") String departmentName);
    
    /**
     * Get all distinct department names as strings
     * Simple method for getting just the department names
     * 
     * @return List of distinct department names
     */
    @Query(value = """
        SELECT DISTINCT department_name
        FROM doctors_department
        ORDER BY department_name ASC
        """, nativeQuery = true)
    List<String> findAllDepartmentNames();
}

