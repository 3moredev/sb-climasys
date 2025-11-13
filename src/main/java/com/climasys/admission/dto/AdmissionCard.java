package com.climasys.admission.dto;

import java.math.BigDecimal;

/**
 * Interface projection for admission card data
 * Used by AdmissionCardRepository to map native query results
 */
public interface AdmissionCard {
    Integer getSerialNumber();
    String getPatientName();
    String getAdmissionIpdNo();
    String getIpdFileNo();
    String getAdmissionDate();
    String getReasonOfAdmission();
    String getDischargeDate();
    String getInsurance();
    String getCompany();
    BigDecimal getAdvanceRs();
    String getPatientId();
}

