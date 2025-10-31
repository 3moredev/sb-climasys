package com.climasys.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * Centralized CORS configuration for the application
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Autowired
    private SessionActivityInterceptor sessionActivityInterceptor;

    @Value("${climasys.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${climasys.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${climasys.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${climasys.cors.allow-credentials}")
    private boolean allowCredentials;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionActivityInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/auth/login",
                    "/api/auth/logout",
                    "/api/auth/session/logout",
                    "/api-docs/**",
                    "/swagger-ui/**"
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        
        System.out.println("DEBUG - CORS Configuration:");
        System.out.println("  Allowed Origins: " + origins);
        System.out.println("  Allowed Methods: " + allowedMethods);
        System.out.println("  Allowed Headers: " + allowedHeaders);
        System.out.println("  Allow Credentials: " + allowCredentials);
        
        registry.addMapping("/api/**")
                .allowedOrigins(origins.toArray(new String[0]))
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders(allowedHeaders.split(","))
                .allowCredentials(allowCredentials)
                .maxAge(3600); // Cache preflight response for 1 hour
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }
}
