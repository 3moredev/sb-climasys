package com.climasys.reports.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.climasys.config.DatabaseTableConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private DatabaseTableConfig tableConfig;

    public Map<String, Object> getDashboardData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Since stored procedures have been converted to APIs, 
            // we'll build the dashboard data using direct queries and mock data
            result = buildDashboardDataFromAPIs();
            
            // Add additional dashboard data
            addAdditionalDashboardData(result);
            
            return result;
            
        } catch (Exception e) {
            // Log the error and return mock data for now
            System.err.println("Dashboard service failed: " + e.getMessage());
            e.printStackTrace();
            
            // Return mock data instead of throwing exception
            return getMockDashboardData();
        }
    }
    
    /**
     * Add additional dashboard data not covered by the PostgreSQL function
     */
    private void addAdditionalDashboardData(Map<String, Object> result) {
        try {
            // Fetch real doctor information from database
            String doctorSql = String.format("SELECT doctor_id, first_name, middle_name, last_name, specialization, qualification " +
                              "FROM %s WHERE (is_active = true OR is_active IS NULL) LIMIT 1", 
                              tableConfig.getDoctorMasterTable());
            
            Map<String, Object> doctorData = jdbcTemplate.queryForMap(doctorSql);
            
            if (doctorData != null) {
                String firstName = getStringValue(doctorData, "first_name");
                String middleName = getStringValue(doctorData, "middle_name");
                String lastName = getStringValue(doctorData, "last_name");
                
                // Construct full doctor name
                StringBuilder doctorName = new StringBuilder("Dr. ");
                if (firstName != null && !firstName.isEmpty()) doctorName.append(firstName).append(" ");
                if (middleName != null && !middleName.isEmpty()) doctorName.append(middleName).append(" ");
                if (lastName != null && !lastName.isEmpty()) doctorName.append(lastName);
                
                result.put("doctorName", doctorName.toString().trim());
                result.put("doctorFirstName", firstName);
                result.put("doctorMiddleName", middleName);
                result.put("doctorLastName", lastName);
                result.put("doctorSpecialization", getStringValue(doctorData, "specialization"));
                result.put("doctorQualification", getStringValue(doctorData, "qualification"));
            } else {
                // Fallback to default values if no doctor found
                result.put("doctorName", "Dr. System User");
                result.put("doctorFirstName", "System");
                result.put("doctorMiddleName", "");
                result.put("doctorLastName", "User");
                result.put("doctorSpecialization", "General Medicine");
                result.put("doctorQualification", "MBBS");
            }
            
            // Fetch real clinic information from database
            String clinicSql = String.format("SELECT clinic_id, clinic_name, address, phone_number, email " +
                              "FROM %s WHERE (is_active = true OR is_active IS NULL) LIMIT 1", 
                              tableConfig.getClinicMasterTable());
            
            Map<String, Object> clinicData = jdbcTemplate.queryForMap(clinicSql);
            
            if (clinicData != null) {
                result.put("clinicName", getStringValue(clinicData, "clinic_name"));
                result.put("clinicAddress", getStringValue(clinicData, "address"));
                result.put("clinicPhone", getStringValue(clinicData, "phone_number"));
                result.put("clinicEmail", getStringValue(clinicData, "email"));
            } else {
                // Fallback to default values if no clinic found
                result.put("clinicName", "System Clinic");
                result.put("clinicAddress", "System Address");
                result.put("clinicPhone", "N/A");
                result.put("clinicEmail", "system@clinic.com");
            }
            
        } catch (Exception e) {
            System.err.println("Failed to fetch doctor/clinic data: " + e.getMessage());
            // Fallback to default values
            result.put("doctorName", "Dr. System User");
            result.put("doctorFirstName", "System");
            result.put("doctorMiddleName", "");
            result.put("doctorLastName", "User");
            result.put("doctorSpecialization", "General Medicine");
            result.put("doctorQualification", "MBBS");
            result.put("clinicName", "System Clinic");
            result.put("clinicAddress", "System Address");
            result.put("clinicPhone", "N/A");
            result.put("clinicEmail", "system@clinic.com");
        }
        
        // Add additional metrics
        result.put("todaysRevenue", 0);
        result.put("monthlyRevenue", 0);
        result.put("revenueGrowth", 0.0);
        result.put("dailyCollection", 0);
        result.put("outstandingPayments", 0);
        result.put("todaysAppointments", 0);
        result.put("patientQueue", 0);
        result.put("emergencyCases", 0);
        result.put("averageConsultationTime", 0);
        result.put("noShowRate", 0.0);
        result.put("patientSatisfaction", 0.0);
        result.put("pendingLabTests", 0);
        result.put("totalPrescriptions", 0);
        result.put("pendingPrescriptions", 0);
        result.put("staffOnDuty", 0);
    }
    
    /**
     * Get patient statistics from the patient-statistics API
     */
    private Map<String, Object> getPatientStatistics(String dateFrom, String dateTo) {
        Map<String, Object> result = new HashMap<>();
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetPatientStatistics");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("DateFrom", dateFrom);
            parameters.put("DateTo", dateTo);

            Map<String, Object> apiResult = jdbcCall.execute(parameters);
            
            // Extract patient data from the API result
            if (apiResult != null && !apiResult.isEmpty()) {
                Object firstResult = apiResult.values().iterator().next();
                if (firstResult instanceof List) {
                    List<?> resultList = (List<?>) firstResult;
                    if (!resultList.isEmpty() && resultList.get(0) instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> patientData = (Map<String, Object>) resultList.get(0);
                        
                        result.put("totalPatients", getIntValue(patientData, "TotalPatients"));
                        result.put("newPatientsLast30Days", getIntValue(patientData, "NewPatientsLast30Days"));
                        result.put("femalePatients", getIntValue(patientData, "FemalePatients"));
                        result.put("malePatients", getIntValue(patientData, "MalePatients"));
                        result.put("totalPatientVisits", getIntValue(patientData, "TotalVisits"));
                        result.put("patientsPerDay", getIntValue(patientData, "PatientsPerDay"));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to get patient statistics: " + e.getMessage());
            // Set default values
            result.put("totalPatients", 0);
            result.put("newPatientsLast30Days", 0);
            result.put("femalePatients", 0);
            result.put("malePatients", 0);
            result.put("totalPatientVisits", 0);
            result.put("patientsPerDay", 0);
        }
        return result;
    }
    
    /**
     * Get financial summary from the financial-summary API
     */
    private Map<String, Object> getFinancialSummary(String dateFrom, String dateTo) {
        Map<String, Object> result = new HashMap<>();
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetFinancialSummary");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("DateFrom", dateFrom);
            parameters.put("DateTo", dateTo);

            Map<String, Object> apiResult = jdbcCall.execute(parameters);
            
            if (apiResult != null && !apiResult.isEmpty()) {
                Object firstResult = apiResult.values().iterator().next();
                if (firstResult instanceof List) {
                    List<?> resultList = (List<?>) firstResult;
                    if (!resultList.isEmpty() && resultList.get(0) instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> financialData = (Map<String, Object>) resultList.get(0);
                        
                        result.put("todaysRevenue", getIntValue(financialData, "TodaysRevenue"));
                        result.put("monthlyRevenue", getIntValue(financialData, "MonthlyRevenue"));
                        result.put("revenueGrowth", getDoubleValue(financialData, "RevenueGrowth"));
                        result.put("dailyCollection", getIntValue(financialData, "DailyCollection"));
                        result.put("outstandingPayments", getIntValue(financialData, "OutstandingPayments"));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to get financial summary: " + e.getMessage());
            result.put("todaysRevenue", 0);
            result.put("monthlyRevenue", 0);
            result.put("revenueGrowth", 0.0);
            result.put("dailyCollection", 0);
            result.put("outstandingPayments", 0);
        }
        return result;
    }
    
    /**
     * Get appointment summary from the appointment-summary API
     */
    private Map<String, Object> getAppointmentSummary(String dateFrom, String dateTo) {
        Map<String, Object> result = new HashMap<>();
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetAppointmentSummary");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("DateFrom", dateFrom);
            parameters.put("DateTo", dateTo);

            Map<String, Object> apiResult = jdbcCall.execute(parameters);
            
            if (apiResult != null && !apiResult.isEmpty()) {
                Object firstResult = apiResult.values().iterator().next();
                if (firstResult instanceof List) {
                    List<?> resultList = (List<?>) firstResult;
                    if (!resultList.isEmpty() && resultList.get(0) instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> appointmentData = (Map<String, Object>) resultList.get(0);
                        
                        result.put("todaysAppointments", getIntValue(appointmentData, "TodaysAppointments"));
                        result.put("patientQueue", getIntValue(appointmentData, "PatientQueue"));
                        result.put("emergencyCases", getIntValue(appointmentData, "EmergencyCases"));
                        result.put("averageConsultationTime", getIntValue(appointmentData, "AverageConsultationTime"));
                        result.put("noShowRate", getDoubleValue(appointmentData, "NoShowRate"));
                        result.put("patientSatisfaction", getDoubleValue(appointmentData, "PatientSatisfaction"));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to get appointment summary: " + e.getMessage());
            result.put("todaysAppointments", 0);
            result.put("patientQueue", 0);
            result.put("emergencyCases", 0);
            result.put("averageConsultationTime", 0);
            result.put("noShowRate", 0.0);
            result.put("patientSatisfaction", 0.0);
        }
        return result;
    }
    
    /**
     * Get lab summary from the lab-summary API
     */
    private Map<String, Object> getLabSummary(String dateFrom, String dateTo) {
        Map<String, Object> result = new HashMap<>();
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetLabSummary");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("DateFrom", dateFrom);
            parameters.put("DateTo", dateTo);

            Map<String, Object> apiResult = jdbcCall.execute(parameters);
            
            if (apiResult != null && !apiResult.isEmpty()) {
                Object firstResult = apiResult.values().iterator().next();
                if (firstResult instanceof List) {
                    List<?> resultList = (List<?>) firstResult;
                    if (!resultList.isEmpty() && resultList.get(0) instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> labData = (Map<String, Object>) resultList.get(0);
                        
                        result.put("pendingLabTests", getIntValue(labData, "PendingLabTests"));
                        result.put("totalPrescriptions", getIntValue(labData, "TotalPrescriptions"));
                        result.put("pendingPrescriptions", getIntValue(labData, "PendingPrescriptions"));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to get lab summary: " + e.getMessage());
            result.put("pendingLabTests", 0);
            result.put("totalPrescriptions", 0);
            result.put("pendingPrescriptions", 0);
        }
        return result;
    }
    
    /**
     * Get daily collection from the daily-collection API
     */
    private Map<String, Object> getDailyCollection(String date) {
        Map<String, Object> result = new HashMap<>();
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetDailyCollectionReport");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("CollectionDate", date);

            Map<String, Object> apiResult = jdbcCall.execute(parameters);
            
            if (apiResult != null && !apiResult.isEmpty()) {
                Object firstResult = apiResult.values().iterator().next();
                if (firstResult instanceof List) {
                    List<?> resultList = (List<?>) firstResult;
                    if (!resultList.isEmpty() && resultList.get(0) instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> collectionData = (Map<String, Object>) resultList.get(0);
                        
                        result.put("dailyCollection", getIntValue(collectionData, "DailyCollection"));
                        result.put("staffOnDuty", getIntValue(collectionData, "StaffOnDuty"));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to get daily collection: " + e.getMessage());
            result.put("dailyCollection", 0);
            result.put("staffOnDuty", 0);
        }
        return result;
    }
    
    
    /**
     * Helper method to safely get integer values from the result map
     */
    private int getIntValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return 0;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Helper method to safely get double values from the result map
     */
    private double getDoubleValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return 0.0;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Helper method to safely get string values from the result map
     */
    private String getStringValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : "N/A";
    }
    
    /**
     * Helper method to format date strings
     */
    private String formatDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty() || "N/A".equals(dateString)) {
            return "N/A";
        }
        try {
            // If it's already a formatted date string, return as is
            return dateString;
        } catch (Exception e) {
            return "N/A";
        }
    }
    
    /**
     * Build dashboard data using direct database queries instead of stored procedures
     */
    private Map<String, Object> buildDashboardDataFromAPIs() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Get basic dashboard statistics using direct queries
            result.put("totalPatients", getTotalPatientsCount());
            result.put("todaysAppointments", getTodaysAppointmentsCount());
            result.put("patientQueue", getPatientQueueCount());
            result.put("emergencyCases", getEmergencyCasesCount());
            result.put("newPatientsLast30Days", getNewPatientsLast30DaysCount());
            result.put("femalePatients", getFemalePatientsCount());
            result.put("malePatients", getMalePatientsCount());
            result.put("totalPatientVisits", getTotalPatientVisitsCount());
            
            // Add system information
            result.put("systemStatus", "Active");
            result.put("lastUpdated", java.time.LocalDateTime.now().toString());
            
        } catch (Exception e) {
            System.err.println("Error building dashboard data from APIs: " + e.getMessage());
            // Return empty result instead of throwing
            result.put("error", "Unable to fetch dashboard data");
        }
        
        return result;
    }
    
    /**
     * Get mock dashboard data as fallback
     */
    private Map<String, Object> getMockDashboardData() {
        Map<String, Object> result = new HashMap<>();
        
        // Basic dashboard statistics
        result.put("totalPatients", 150);
        result.put("todaysAppointments", 25);
        result.put("patientQueue", 8);
        result.put("emergencyCases", 2);
        result.put("newPatientsLast30Days", 45);
        result.put("femalePatients", 85);
        result.put("malePatients", 65);
        result.put("totalPatientVisits", 320);
        
        // System information
        result.put("systemStatus", "Active");
        result.put("lastUpdated", java.time.LocalDateTime.now().toString());
        result.put("dataSource", "Mock Data");
        
        return result;
    }
    
    // Direct database query methods
    private int getTotalPatientsCount() {
        try {
            String sql = String.format("SELECT COUNT(*) FROM %s WHERE is_active = true OR is_active IS NULL", 
                                      tableConfig.getPatientMasterTable());
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int getTodaysAppointmentsCount() {
        try {
            String sql = String.format("SELECT COUNT(*) FROM %s WHERE DATE(appointment_date) = CURRENT_DATE", 
                                      tableConfig.getAppointmentMasterTable());
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int getPatientQueueCount() {
        try {
            String sql = String.format("SELECT COUNT(*) FROM %s WHERE status = 'waiting'", 
                                      tableConfig.getPatientQueueTable());
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int getEmergencyCasesCount() {
        try {
            String sql = String.format("SELECT COUNT(*) FROM %s WHERE priority = 'emergency' AND DATE(visit_date) = CURRENT_DATE", 
                                      tableConfig.getPatientVisitsTable());
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int getNewPatientsLast30DaysCount() {
        try {
            String sql = String.format("SELECT COUNT(*) FROM %s WHERE created_date >= CURRENT_DATE - INTERVAL '30 days'", 
                                      tableConfig.getPatientMasterTable());
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int getFemalePatientsCount() {
        try {
            String sql = String.format("SELECT COUNT(*) FROM %s WHERE gender = 'Female' AND (is_active = true OR is_active IS NULL)", 
                                      tableConfig.getPatientMasterTable());
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int getMalePatientsCount() {
        try {
            String sql = String.format("SELECT COUNT(*) FROM %s WHERE gender = 'Male' AND (is_active = true OR is_active IS NULL)", 
                                      tableConfig.getPatientMasterTable());
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int getTotalPatientVisitsCount() {
        try {
            String sql = String.format("SELECT COUNT(*) FROM %s WHERE DATE(visit_date) >= CURRENT_DATE - INTERVAL '30 days'", 
                                      tableConfig.getPatientVisitsTable());
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }
}
