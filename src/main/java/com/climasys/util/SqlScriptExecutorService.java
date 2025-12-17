package com.climasys.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service to execute SQL scripts for loading dummy data
 */
@Service
public class SqlScriptExecutorService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Execute the dummy daily collection data SQL script
     * 
     * @return Number of statements executed successfully
     */
    @Transactional
    public int executeDummyDailyCollectionScript() throws IOException {
        ClassPathResource resource = new ClassPathResource("db/migration/dummy_daily_collection_data.sql");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String sqlScript = reader.lines().collect(Collectors.joining("\n"));

            // Split the script into individual statements
            List<String> statements = splitSqlStatements(sqlScript);

            int executedCount = 0;
            for (String statement : statements) {
                if (!statement.trim().isEmpty()) {
                    try {
                        jdbcTemplate.execute(statement);
                        executedCount++;
                    } catch (Exception e) {
                        // Log but continue - some statements might fail due to conflicts
                        System.out.println("Statement execution note: " + e.getMessage());
                    }
                }
            }

            return executedCount;
        }
    }

    /**
     * Split SQL script into individual statements
     * Handles multi-line statements and comments
     */
    private List<String> splitSqlStatements(String sqlScript) {
        List<String> statements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();

        String[] lines = sqlScript.split("\n");

        for (String line : lines) {
            String trimmedLine = line.trim();

            // Skip empty lines and comment-only lines
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                continue;
            }

            // Remove inline comments
            int commentIndex = trimmedLine.indexOf("--");
            if (commentIndex > 0) {
                trimmedLine = trimmedLine.substring(0, commentIndex).trim();
            }

            currentStatement.append(trimmedLine).append(" ");

            // Check if statement ends with semicolon
            if (trimmedLine.endsWith(";")) {
                statements.add(currentStatement.toString().trim());
                currentStatement = new StringBuilder();
            }
        }

        // Add any remaining statement
        if (currentStatement.length() > 0) {
            String remaining = currentStatement.toString().trim();
            if (!remaining.isEmpty()) {
                statements.add(remaining);
            }
        }

        return statements;
    }

    /**
     * Verify the inserted data
     * 
     * @return Count of patient visits with today's date
     */
    public int verifyDummyData() {
        String sql = "SELECT COUNT(*) FROM patient_visits WHERE DATE(visit_date) = CURRENT_DATE AND patient_id LIKE 'PAT0%'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
}
