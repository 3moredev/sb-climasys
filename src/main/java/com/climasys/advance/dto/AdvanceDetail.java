package com.climasys.advance.dto;

import java.math.BigDecimal;

/**
 * Interface projection for advance collection details
 * Used by AdvanceCollectionRepository to map native query results
 * Comprehensive version for "Previous Advance Collection Records" table
 */
public interface AdvanceDetail {
    String getAdmissionIpdNo();
    String getAdmissionDate();
    String getDischargeDate();
    String getReasonOfAdmission();
    String getInsurance();
    String getAdvanceDate();
    String getReceiptNo();
    BigDecimal getAmount();
}

