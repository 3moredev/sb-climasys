package com.climasys.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Utility class for timezone conversions
 */
@Component
public class TimezoneUtils {
    
    @Value("${climasys.timezone:}")
    private String configuredTimezone;
    
    /**
     * Get the target timezone for conversions
     * @return ZoneId for the configured timezone or system default
     */
    public ZoneId getTargetTimezone() {
        System.out.println("DEBUG - TimezoneUtils.getTargetTimezone() called");
        System.out.println("DEBUG - configuredTimezone: '" + configuredTimezone + "'");
        
        if (configuredTimezone != null && !configuredTimezone.trim().isEmpty()) {
            System.out.println("DEBUG - Using configured timezone: " + configuredTimezone.trim());
            return ZoneId.of(configuredTimezone.trim());
        } else {
            // Auto-detect if system is in India and use IST
            ZoneId systemDefault = ZoneId.systemDefault();
            System.out.println("DEBUG - System default timezone: " + systemDefault.getId());
            
            // Check if system timezone is in India
            if (isSystemInIndia()) {
                System.out.println("DEBUG - System detected as India, using IST");
                return ZoneId.of("Asia/Kolkata");
            }
            
            System.out.println("DEBUG - Using system default timezone: " + systemDefault.getId());
            return systemDefault;
        }
    }
    
    /**
     * Check if the system is in India based on timezone
     * @return true if system timezone indicates India
     */
    private boolean isSystemInIndia() {
        ZoneId systemZone = ZoneId.systemDefault();
        String zoneId = systemZone.getId();
        
        System.out.println("DEBUG - Checking if system is in India...");
        System.out.println("DEBUG - System timezone ID: " + zoneId);
        
        // Check for Indian timezone patterns
        boolean isIndia = zoneId.equals("Asia/Kolkata") || 
                         zoneId.equals("Asia/Calcutta") || 
                         zoneId.equals("IST") ||
                         zoneId.contains("India") ||
                         zoneId.contains("Kolkata") ||
                         zoneId.contains("Calcutta");
        
        if (!isIndia) {
            // Check if current time offset matches IST (UTC+5:30 = 19800 seconds)
            try {
                int currentOffset = systemZone.getRules().getOffset(java.time.Instant.now()).getTotalSeconds();
                System.out.println("DEBUG - Current timezone offset: " + currentOffset + " seconds");
                System.out.println("DEBUG - IST offset: 19800 seconds (UTC+5:30)");
                isIndia = (currentOffset == 19800);
            } catch (Exception e) {
                System.out.println("DEBUG - Could not check timezone offset: " + e.getMessage());
            }
        }
        
        System.out.println("DEBUG - System in India: " + isIndia);
        return isIndia;
    }
    
    /**
     * Convert UTC time to target timezone
     * @param utcTime UTC time to convert
     * @return LocalTime in target timezone
     */
    public LocalTime convertUtcToTargetTimezone(LocalTime utcTime) {
        try {
            System.out.println("DEBUG - Converting UTC time: " + utcTime);
            
            // Get current date to create a proper datetime for timezone conversion
            LocalDate today = LocalDate.now();
            LocalDateTime utcDateTime = today.atTime(utcTime);
            
            // Convert from UTC to target timezone
            ZonedDateTime utcZoned = utcDateTime.atZone(ZoneId.of("UTC"));
            ZonedDateTime targetZoned = utcZoned.withZoneSameInstant(getTargetTimezone());
            
            // Extract the time part
            LocalTime result = targetZoned.toLocalTime();
            System.out.println("DEBUG - Converted " + utcTime + " UTC to " + result + " " + getTargetTimezone().getId());
            return result;
        } catch (Exception e) {
            System.out.println("ERROR - Timezone conversion failed: " + e.getMessage());
            e.printStackTrace();
            // Fallback to original time if timezone conversion fails
            return utcTime;
        }
    }
    
    /**
     * Convert target timezone time to UTC
     * @param targetTime Time in target timezone to convert
     * @return LocalTime in UTC
     */
    public LocalTime convertTargetTimezoneToUtc(LocalTime targetTime) {
        try {
            // Get current date to create a proper datetime for timezone conversion
            LocalDate today = LocalDate.now();
            LocalDateTime targetDateTime = today.atTime(targetTime);
            
            // Convert from target timezone to UTC
            ZonedDateTime targetZoned = targetDateTime.atZone(getTargetTimezone());
            ZonedDateTime utcZoned = targetZoned.withZoneSameInstant(ZoneId.of("UTC"));
            
            // Extract the time part
            return utcZoned.toLocalTime();
        } catch (Exception e) {
            // Fallback to original time if timezone conversion fails
            return targetTime;
        }
    }
    
    /**
     * Convert target timezone datetime to UTC
     * @param targetDateTime DateTime in target timezone to convert
     * @return LocalDateTime in UTC
     */
    public LocalDateTime convertTargetTimezoneToUtc(LocalDateTime targetDateTime) {
        try {
            System.out.println("DEBUG - Converting datetime from " + getTargetTimezone().getId() + " to UTC: " + targetDateTime);
            
            // Convert from target timezone to UTC
            ZonedDateTime targetZoned = targetDateTime.atZone(getTargetTimezone());
            ZonedDateTime utcZoned = targetZoned.withZoneSameInstant(ZoneId.of("UTC"));
            
            LocalDateTime result = utcZoned.toLocalDateTime();
            System.out.println("DEBUG - Converted " + targetDateTime + " " + getTargetTimezone().getId() + " to " + result + " UTC");
            return result;
        } catch (Exception e) {
            System.out.println("ERROR - Datetime timezone conversion failed: " + e.getMessage());
            e.printStackTrace();
            // Fallback to original datetime if timezone conversion fails
            return targetDateTime;
        }
    }
    
    /**
     * Get timezone display name
     * @return String representation of the timezone
     */
    public String getTimezoneDisplayName() {
        return getTargetTimezone().getId();
    }
    
    /**
     * Check if timezone conversion is available
     * @return true if timezone conversion is working
     */
    public boolean isTimezoneConversionAvailable() {
        try {
            getTargetTimezone();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
