package com.climasys.repository;

import com.climasys.entity.InsuranceCompanyMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository for insurance company master operations
 * Replaces USP_GetDDL_InsuranceComp stored procedure
 */
@Repository
public interface InsuranceCompanyMasterRepository extends JpaRepository<InsuranceCompanyMaster, Integer> {
    
    /**
     * Get all active insurance companies for dropdown
     * Matches the query from USP_GetDDL_InsuranceComp:
     * SELECT DISTINCT Company_Id as ID, Company_Name as Name
     * FROM Insurance_Company_Master
     * WHERE ISNULL(IsDeleted,0) != 1
     * ORDER BY Name
     * 
     * @return List of insurance companies with Name and ID
     */
    @Query(value = """
        SELECT DISTINCT
            company_id AS id,
            company_name AS name
        FROM insurance_company_master
        WHERE (isdeleted IS NULL OR isdeleted = false)
        ORDER BY company_name ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findAllActiveCompaniesForDropdown();
    
    /**
     * Get all active insurance companies as entities
     * 
     * @return List of active insurance companies
     */
    @Query("SELECT icm FROM InsuranceCompanyMaster icm WHERE (icm.isDeleted IS NULL OR icm.isDeleted = false) ORDER BY icm.companyName ASC")
    List<InsuranceCompanyMaster> findAllActiveCompanies();
    
    /**
     * Find insurance company by ID if not deleted
     * 
     * @param companyId Company ID
     * @return Insurance company if found and not deleted
     */
    @Query("SELECT icm FROM InsuranceCompanyMaster icm WHERE icm.companyId = :companyId AND (icm.isDeleted IS NULL OR icm.isDeleted = false)")
    InsuranceCompanyMaster findActiveCompanyById(Integer companyId);
}

