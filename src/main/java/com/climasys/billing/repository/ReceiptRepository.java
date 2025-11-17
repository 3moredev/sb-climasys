package com.climasys.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for receipt operations
 * Note: Stored procedure calls are handled in the service layer using SimpleJdbcCall
 */
@Repository
public interface ReceiptRepository extends JpaRepository<Object, String> {
    // This repository is a placeholder for future JPA-based queries
    // Currently, stored procedures are called directly from the service layer
}

