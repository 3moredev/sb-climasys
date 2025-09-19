package com.climasys.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Climasys Backend API")
                        .description("Spring Boot backend API for Climasys - A comprehensive clinic management system")
                        .version("2.0.0")
                        .contact(new Contact()
                                .name("Climasys Development Team")
                                .email("support@climasys.com")
                                .url("https://climasys.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Development Server"),
                        new Server()
                                .url("https://api.climasys.com")
                                .description("Production Server")
                ));
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
