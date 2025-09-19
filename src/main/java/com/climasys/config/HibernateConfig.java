package com.climasys.config;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class HibernateConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return new HibernatePropertiesCustomizer() {
            @Override
            public void customize(Map<String, Object> hibernateProperties) {
                // Fix for Hibernate 6.x NPE issue with Spring Boot 3.x
                hibernateProperties.put(AvailableSettings.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, true);
                hibernateProperties.put(AvailableSettings.JDBC_TIME_ZONE, "UTC");
                hibernateProperties.put(AvailableSettings.ORDER_INSERTS, true);
                hibernateProperties.put(AvailableSettings.ORDER_UPDATES, true);
                hibernateProperties.put(AvailableSettings.STATEMENT_BATCH_SIZE, 25);
                
                // Critical fix for the NPE issue
                hibernateProperties.put("hibernate.connection.autocommit", false);
                hibernateProperties.put("hibernate.jdbc.batch_size", 25);
                hibernateProperties.put("hibernate.order_inserts", true);
                hibernateProperties.put("hibernate.order_updates", true);
                
                // Additional properties to ensure proper initialization
                hibernateProperties.put("hibernate.connection.provider_disables_autocommit", true);
                hibernateProperties.put("hibernate.jdbc.time_zone", "UTC");
                
                // Disable problematic features that can cause NPE
                hibernateProperties.put("hibernate.temp.use_jdbc_metadata_defaults", false);
                hibernateProperties.put("hibernate.jdbc.lob.non_contextual_creation", true);
                
                // Additional properties for database setup - using none to prevent schema changes
                hibernateProperties.put("hibernate.hbm2ddl.auto", "none");
                hibernateProperties.put("hibernate.show_sql", false);
                hibernateProperties.put("hibernate.format_sql", false);
                hibernateProperties.put("hibernate.use_sql_comments", false);
                
                // PostgreSQL specific optimizations
                hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                hibernateProperties.put("hibernate.connection.driver_class", "org.postgresql.Driver");
                hibernateProperties.put("hibernate.default_schema", "public");
            }
        };
    }
}
