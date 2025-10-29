package com.adrs.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation.
 * Provides comprehensive API documentation with security schemes.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * Configure OpenAPI documentation.
     *
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Animal Disease Reporting System API")
                        .version("1.0.0")
                        .description("REST API for managing animal disease reporting, farm management, " +
                                "and veterinary administration. Provides endpoints for user management, " +
                                "farm/animal registration, disease reporting, and system configuration.")
                        .contact(new Contact()
                                .name("ADRS Development Team")
                                .email("support@adrs.example.com")
                                .url("https://adrs.example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url(contextPath.isEmpty() ? "/" : contextPath)
                                .description("Current Server")))
                .components(new Components()
                        .addSecuritySchemes("session-auth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("JSESSIONID")
                                .description("Session-based authentication using Spring Security")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("session-auth"));
    }
}
