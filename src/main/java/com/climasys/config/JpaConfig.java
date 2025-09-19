package com.climasys.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {"com.climasys.repository", "com.climasys.auth.repository"})
@EntityScan(basePackages = {"com.climasys.entity", "com.climasys.auth.entity"})
@EnableTransactionManagement
public class JpaConfig {
    // JPA configuration with explicit entity and repository scanning
}
