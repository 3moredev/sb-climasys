package com.climasys.repository;

import com.climasys.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for PaymentMethod entity
 * Provides data access methods for payment method master data
 */
@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    
    /**
     * Find all payment methods ordered by ID
     * @return List of all payment methods
     */
    @Query("SELECT pm FROM PaymentMethod pm ORDER BY pm.id")
    List<PaymentMethod> findAllActiveOrdered();
    
    /**
     * Find all payment methods ordered by ID
     * @return List of all payment methods
     */
    @Query("SELECT pm FROM PaymentMethod pm ORDER BY pm.id")
    List<PaymentMethod> findAllOrdered();
    
    /**
     * Find payment method by description
     * @param description Payment method description
     * @return PaymentMethod if found
     */
    PaymentMethod findByPaymentDescription(String description);
    
    /**
     * Check if payment method exists by description
     * @param description Payment method description
     * @return True if exists
     */
    boolean existsByPaymentDescription(String description);
}
