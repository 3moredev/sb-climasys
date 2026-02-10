package com.climasys.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Database migration runner to add missing columns on application startup
 * This is a one-time migration to add the file_size column to
 * patient_documents_treatment
 */
@Component
public class DatabaseMigrationRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigrationRunner.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void run(String... args) {
        try {
            System.out.println("DEBUG: DatabaseMigrationRunner is starting...");
            logger.info("Checking for required database migrations...");

            // Create patient_documents_treatment table if it doesn't exist
            String createTableSql = "CREATE TABLE IF NOT EXISTS public.patient_documents_treatment (" +
                    "id SERIAL PRIMARY KEY, " +
                    "patient_id VARCHAR(50), " +
                    "doctor_id VARCHAR(50), " +
                    "clinic_id VARCHAR(50), " +
                    "document_name VARCHAR(200), " +
                    "created_on TIMESTAMP, " +
                    "createdby_name VARCHAR(50), " +
                    "modified_on TIMESTAMP, " +
                    "modified_name VARCHAR(50), " +
                    "delete_flag BOOLEAN DEFAULT FALSE, " +
                    "patient_visit_no INTEGER, " +
                    "visit_date TIMESTAMP, " +
                    "file_size BIGINT" +
                    ")";
            jdbcTemplate.execute(createTableSql);
            logger.info("Successfully ensured patient_documents_treatment table exists");

            // Add file_size column to patient_documents_treatment if it doesn't exist
            // (migrations for existing tables)
            String sql = "ALTER TABLE public.patient_documents_treatment " +
                    "ADD COLUMN IF NOT EXISTS file_size BIGINT";

            jdbcTemplate.execute(sql);
            logger.info("Successfully ensured file_size column exists in patient_documents_treatment table");

        } catch (Exception e) {
            logger.error("Error running database migration: {}", e.getMessage(), e);
            // Don't throw exception - let application start even if migration fails
            // This allows manual intervention if needed
        }
    }
}
