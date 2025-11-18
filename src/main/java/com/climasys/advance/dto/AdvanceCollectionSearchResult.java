package com.climasys.advance.dto;

import java.math.BigDecimal;

/**
 * Interface projection for advance collection search results
 * Used by AdvanceCollectionRepository to map native query results
 */
public interface AdvanceCollectionSearchResult {
    Integer getSerialNumber();
    String getPatientName();
    String getIpdRefNo();
    String getAdmissionDate();
    String getReasonOfAdmission();
    String getInsurance();
    String getDateOfAdvance();
    String getReceiptNo();
    BigDecimal getAdvanceRs();
    String getPatientId();
    String getClinicId();
    String getDoctorId();
}

