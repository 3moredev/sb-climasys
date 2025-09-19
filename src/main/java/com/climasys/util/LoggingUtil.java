package com.climasys.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Utility class for consistent logging across the application
 */
public class LoggingUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingUtil.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.climasys.audit");
    
    /**
     * Log method entry with parameters
     */
    public static void logMethodEntry(String methodName, Object... params) {
        StringBuilder sb = new StringBuilder("ENTER: ").append(methodName);
        if (params.length > 0) {
            sb.append(" with params: ");
            for (int i = 0; i < params.length; i += 2) {
                if (i + 1 < params.length) {
                    sb.append(params[i]).append("=").append(params[i + 1]);
                    if (i + 2 < params.length) {
                        sb.append(", ");
                    }
                }
            }
        }
        logger.debug(sb.toString());
    }
    
    /**
     * Log method exit with result
     */
    public static void logMethodExit(String methodName, Object result) {
        logger.debug("EXIT: {} with result: {}", methodName, result);
    }
    
    /**
     * Log method exit without result
     */
    public static void logMethodExit(String methodName) {
        logger.debug("EXIT: {}", methodName);
    }
    
    /**
     * Log business operation for audit trail
     */
    public static void logBusinessOperation(String operation, String userId, String details) {
        auditLogger.info("BUSINESS_OPERATION - {} by user: {} - {}", operation, userId, details);
    }
    
    /**
     * Log security event
     */
    public static void logSecurityEvent(String event, String userId, String details) {
        auditLogger.warn("SECURITY_EVENT - {} for user: {} - {}", event, userId, details);
    }
    
    /**
     * Log performance metrics
     */
    public static void logPerformance(String operation, long durationMs, String details) {
        if (durationMs > 1000) {
            logger.warn("SLOW_OPERATION - {} took {}ms - {}", operation, durationMs, details);
        } else {
            logger.debug("PERFORMANCE - {} took {}ms - {}", operation, durationMs, details);
        }
    }
    
    /**
     * Set MDC context for request tracking
     */
    public static void setRequestContext(String requestId, String userId, String sessionId) {
        MDC.put("requestId", requestId);
        MDC.put("userId", userId);
        MDC.put("sessionId", sessionId);
    }
    
    /**
     * Clear MDC context
     */
    public static void clearRequestContext() {
        MDC.clear();
    }
    
    /**
     * Log database operation
     */
    public static void logDatabaseOperation(String operation, String table, int recordCount) {
        logger.debug("DB_OPERATION - {} on table: {} affecting {} records", operation, table, recordCount);
    }
    
    /**
     * Log external API call
     */
    public static void logExternalApiCall(String apiName, String endpoint, int responseCode, long durationMs) {
        if (responseCode >= 400) {
            logger.warn("EXTERNAL_API_ERROR - {} {} returned {} in {}ms", apiName, endpoint, responseCode, durationMs);
        } else {
            logger.debug("EXTERNAL_API_SUCCESS - {} {} returned {} in {}ms", apiName, endpoint, responseCode, durationMs);
        }
    }
    
    /**
     * Log configuration change
     */
    public static void logConfigurationChange(String configKey, String oldValue, String newValue, String changedBy) {
        auditLogger.info("CONFIG_CHANGE - {} changed from '{}' to '{}' by user: {}", 
            configKey, oldValue, newValue, changedBy);
    }
    
    /**
     * Log data validation error
     */
    public static void logValidationError(String field, String value, String error) {
        logger.warn("VALIDATION_ERROR - Field: {}, Value: {}, Error: {}", field, value, error);
    }
    
    /**
     * Log cache operation
     */
    public static void logCacheOperation(String operation, String key, boolean hit) {
        logger.debug("CACHE_{} - Key: {}, Hit: {}", operation.toUpperCase(), key, hit);
    }
}
