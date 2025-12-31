package com.climasys.service;

import com.climasys.entity.SubCategory;
import com.climasys.repository.SubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SubCategoryService {

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    public List<SubCategory> getAllSubCategories() {
        return subCategoryRepository.findAllByOrderBySortOrderAsc();
    }

    public Optional<SubCategory> getSubCategoryById(Long id) {
        return subCategoryRepository.findById(id);
    }

    @Transactional
    public SubCategory createSubCategory(SubCategory subCategory) {
        if (subCategoryRepository.existsByChargesSubCategory(subCategory.getChargesSubCategory())) {
            throw new IllegalArgumentException("Sub Category already exists: " + subCategory.getChargesSubCategory());
        }
        return subCategoryRepository.save(subCategory);
    }

    @Transactional
    public SubCategory updateSubCategory(Long id, SubCategory subCategoryDetails) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sub Category not found with id: " + id));

        if (subCategoryRepository.existsByChargesSubCategoryAndIdNot(subCategoryDetails.getChargesSubCategory(), id)) {
            throw new IllegalArgumentException("Sub Category already exists: " + subCategoryDetails.getChargesSubCategory());
        }

        subCategory.setChargesSubCategory(subCategoryDetails.getChargesSubCategory());
        subCategory.setSortOrder(subCategoryDetails.getSortOrder());

        return subCategoryRepository.save(subCategory);
    }

    @Transactional
    public void deleteSubCategory(Long id) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sub Category not found with id: " + id));
        subCategoryRepository.delete(subCategory);
    }
}

