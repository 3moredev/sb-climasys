package com.climasys.service;

import com.climasys.entity.InsuranceCompany;
import com.climasys.repository.InsuranceCompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InsuranceCompanyManagementService {

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;

    public List<InsuranceCompany> getAllInsuranceCompanies() {
        return insuranceCompanyRepository.findAll();
    }

    public Optional<InsuranceCompany> getInsuranceCompanyById(Integer id) {
        return insuranceCompanyRepository.findById(id);
    }

    @Transactional
    public InsuranceCompany createInsuranceCompany(InsuranceCompany insuranceCompany) {
        if (insuranceCompany == null || insuranceCompany.getInsuranceCompanyName() == null || insuranceCompany.getInsuranceCompanyName().trim().isEmpty()) {
            throw new IllegalArgumentException("Insurance Company name is required");
        }
        
        String companyName = insuranceCompany.getInsuranceCompanyName().trim();
        if (insuranceCompanyRepository.existsByInsuranceCompanyNameIgnoreCase(companyName)) {
            throw new IllegalArgumentException("Insurance Company already exists: " + companyName);
        }
        
        // Set the trimmed name
        insuranceCompany.setInsuranceCompanyName(companyName);
        return insuranceCompanyRepository.save(insuranceCompany);
    }

    @Transactional
    public InsuranceCompany updateInsuranceCompany(Integer id, InsuranceCompany insuranceCompanyDetails) {
        InsuranceCompany insuranceCompany = insuranceCompanyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Insurance Company not found with id: " + id));

        if (insuranceCompanyRepository.existsByInsuranceCompanyNameIgnoreCaseAndIdNot(insuranceCompanyDetails.getInsuranceCompanyName(), id)) {
            throw new IllegalArgumentException("Insurance Company already exists: " + insuranceCompanyDetails.getInsuranceCompanyName());
        }

        insuranceCompany.setInsuranceCompanyName(insuranceCompanyDetails.getInsuranceCompanyName());

        return insuranceCompanyRepository.save(insuranceCompany);
    }

    @Transactional
    public void deleteInsuranceCompany(Integer id) {
        InsuranceCompany insuranceCompany = insuranceCompanyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Insurance Company not found with id: " + id));
        insuranceCompanyRepository.delete(insuranceCompany);
    }
}

