package com.climasys.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class for CORS configuration
 */
@Component
public class CorsUtils {

    @Value("${climasys.cors.allowed-origins}")
    private String allowedOrigins;

    /**
     * Get the allowed CORS origins as a string array
     * @return Array of allowed origins
     */
    public String[] getAllowedOriginsArray() {
        return allowedOrigins.split(",");
    }

    /**
     * Get the allowed CORS origins as a list
     * @return List of allowed origins
     */
    public List<String> getAllowedOriginsList() {
        return Arrays.asList(allowedOrigins.split(","));
    }

    /**
     * Get the primary allowed origin (first one in the list)
     * @return Primary allowed origin
     */
    public String getPrimaryOrigin() {
        String[] origins = getAllowedOriginsArray();
        return origins.length > 0 ? origins[0].trim() : "http://localhost:8080";
    }

    /**
     * Check if a given origin is allowed
     * @param origin Origin to check
     * @return true if origin is allowed
     */
    public boolean isOriginAllowed(String origin) {
        if (origin == null) return false;
        return getAllowedOriginsList().stream()
                .anyMatch(allowed -> allowed.trim().equals(origin.trim()));
    }

    /**
     * Get all allowed origins as a comma-separated string
     * @return Comma-separated string of allowed origins
     */
    public String getAllowedOriginsString() {
        return allowedOrigins;
    }
}
