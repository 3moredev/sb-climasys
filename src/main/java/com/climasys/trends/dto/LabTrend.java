package com.climasys.trends.dto;

import java.time.LocalDate;

/**
 * Interface projection for lab trend results
 * Used by LabTrendsRepository to map native query results
 */
public interface LabTrend {
    LocalDate getVisitDate();
    Integer getPatientVisitNo();
    String getLabTestDescription();
    String getParameterName();
    String getParameterValue();
    String getDoctorName();
    String getLabName();
    String getReportDate();
    String getPatientFullName();
    String getComment();
    Integer getPatientLastVisitNo();
}

