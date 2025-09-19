package com.climasys.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "climasys.database")
public class DatabaseTableConfig {
    
    private Map<String, String> tables;
    
    public Map<String, String> getTables() {
        return tables;
    }
    
    public void setTables(Map<String, String> tables) {
        this.tables = tables;
    }
    
    /**
     * Get table name by key, with fallback to default
     */
    public String getTableName(String tableKey) {
        if (tables != null && tables.containsKey(tableKey)) {
            return tables.get(tableKey);
        }
        // Fallback to default table name
        return tableKey;
    }
    
    /**
     * Get table name by key, with custom fallback
     */
    public String getTableName(String tableKey, String fallback) {
        if (tables != null && tables.containsKey(tableKey)) {
            return tables.get(tableKey);
        }
        return fallback;
    }
    
    // Convenience methods for common tables
    public String getPatientMasterTable() {
        return getTableName("patient_master");
    }
    
    public String getDoctorMasterTable() {
        return getTableName("doctor_master");
    }
    
    public String getClinicMasterTable() {
        return getTableName("clinic_master");
    }
    
    public String getAppointmentMasterTable() {
        return getTableName("appointment_master");
    }
    
    public String getPatientQueueTable() {
        return getTableName("patient_queue");
    }
    
    public String getPatientVisitsTable() {
        return getTableName("patient_visits");
    }
}
