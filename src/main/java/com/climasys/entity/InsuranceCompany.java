package com.climasys.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "insurance_company_master")
public class InsuranceCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Integer id;

    @Column(name = "company_name", nullable = false, columnDefinition = "TEXT")
    @JsonProperty("insuranceCompanyName")
    private String insuranceCompanyName;

    // Default constructor required by JPA
    public InsuranceCompany() {
    }

    // Constructor with fields
    public InsuranceCompany(String insuranceCompanyName) {
        this.insuranceCompanyName = insuranceCompanyName;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInsuranceCompanyName() {
        return insuranceCompanyName;
    }

    public void setInsuranceCompanyName(String insuranceCompanyName) {
        this.insuranceCompanyName = insuranceCompanyName;
    }

    @Override
    public String toString() {
        return "InsuranceCompany{" +
                "id=" + id +
                ", insuranceCompanyName='" + insuranceCompanyName + '\'' +
                '}';
    }
}

