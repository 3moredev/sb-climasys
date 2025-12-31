package com.climasys.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sub_category_master")
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "charges_sub_category", nullable = false, unique = true)
    private String chargesSubCategory;

    @Column(name = "sort_order")
    private Integer sortOrder;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChargesSubCategory() {
        return chargesSubCategory;
    }

    public void setChargesSubCategory(String chargesSubCategory) {
        this.chargesSubCategory = chargesSubCategory;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}

