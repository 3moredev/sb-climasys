package com.climasys.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import jakarta.annotation.PostConstruct;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * Configuration class for timezone settings
 */
@Configuration
@ConfigurationProperties(prefix = "climasys")
public class TimezoneConfig {
    
    private String timezone = "Asia/Kolkata"; // Default to IST
    
    @PostConstruct
    public void init() {
        // Set JVM default timezone to IST
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        System.out.println("DEBUG - TimezoneConfig: Set JVM default timezone to IST (Asia/Kolkata)");
        System.out.println("DEBUG - TimezoneConfig: Current JVM timezone: " + TimeZone.getDefault().getID());
    }
    
    @Bean
    @Primary
    public ZoneId defaultTimeZone() {
        ZoneId istZone = ZoneId.of("Asia/Kolkata");
        System.out.println("DEBUG - TimezoneConfig: Created IST ZoneId: " + istZone.getId());
        return istZone;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
