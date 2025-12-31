package com.climasys.repository;

import com.climasys.entity.InsuranceCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceCompanyRepository extends JpaRepository<InsuranceCompany, Integer> {
    
    @Query("SELECT COUNT(i) > 0 FROM InsuranceCompany i WHERE LOWER(i.insuranceCompanyName) = LOWER(:name)")
    boolean existsByInsuranceCompanyNameIgnoreCase(@Param("name") String insuranceCompanyName);
    
    @Query("SELECT COUNT(i) > 0 FROM InsuranceCompany i WHERE LOWER(i.insuranceCompanyName) = LOWER(:name) AND i.id != :id")
    boolean existsByInsuranceCompanyNameIgnoreCaseAndIdNot(@Param("name") String insuranceCompanyName, @Param("id") Integer id);
}

