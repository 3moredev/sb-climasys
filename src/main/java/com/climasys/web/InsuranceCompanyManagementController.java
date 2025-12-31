package com.climasys.web;

import com.climasys.entity.InsuranceCompany;
import com.climasys.service.InsuranceCompanyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/insurance-companies")
public class InsuranceCompanyManagementController {

    @Autowired
    private InsuranceCompanyManagementService insuranceCompanyManagementService;

    @GetMapping
    public ResponseEntity<?> getAllInsuranceCompanies() {
        try {
            return ResponseEntity.ok(insuranceCompanyManagementService.getAllInsuranceCompanies());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsuranceCompany> getInsuranceCompanyById(@PathVariable Integer id) {
        return insuranceCompanyManagementService.getInsuranceCompanyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createInsuranceCompany(@RequestBody InsuranceCompany insuranceCompany) {
        try {
            System.out.println("Received insurance company: " + insuranceCompany);
            if (insuranceCompany == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Insurance company data is required"));
            }
            System.out.println("Insurance company name: " + insuranceCompany.getInsuranceCompanyName());
            
            InsuranceCompany createdInsuranceCompany = insuranceCompanyManagementService.createInsuranceCompany(insuranceCompany);
            return ResponseEntity.ok(createdInsuranceCompany);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error creating insurance company: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to create insurance company: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInsuranceCompany(@PathVariable Integer id, @RequestBody InsuranceCompany insuranceCompany) {
        try {
            InsuranceCompany updatedInsuranceCompany = insuranceCompanyManagementService.updateInsuranceCompany(id, insuranceCompany);
            return ResponseEntity.ok(updatedInsuranceCompany);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to update insurance company: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInsuranceCompany(@PathVariable Integer id) {
        try {
            insuranceCompanyManagementService.deleteInsuranceCompany(id);
            return ResponseEntity.ok(Map.of("message", "Insurance Company deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to delete insurance company: " + e.getMessage()));
        }
    }
}

