package com.climasys.repository;

import com.climasys.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    boolean existsByChargesSubCategory(String chargesSubCategory);
    
    boolean existsByChargesSubCategoryAndIdNot(String chargesSubCategory, Long id);
    
    List<SubCategory> findAllByOrderBySortOrderAsc();
}

