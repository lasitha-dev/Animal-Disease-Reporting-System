package com.adrs.test.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Test configuration for PostgreSQL Testcontainers.
 * This provides a real PostgreSQL database for integration tests,
 * avoiding H2 compatibility issues with PostgreSQL-specific features like arrays.
 * 
 * Features:
 * - Uses Testcontainers to spin up PostgreSQL in Docker
 * - Automatically configures Spring Boot datasource via @ServiceConnection
 * - Database is shared across all tests for performance
 * - Database is destroyed after test suite completes
 * 
 * Requirements:
 * - Docker must be running on the test machine
 * - Testcontainers dependencies must be in pom.xml
 * 
 * @author ADRS Team
 * @version 1.0
 * @since 2025-01-29
 */
@TestConfiguration(proxyBeanMethods = false)
public class PostgresTestcontainersConfig {

    /**
     * Creates a PostgreSQL container for integration tests.
     * The container is shared across all tests using this configuration.
     * 
     * @ServiceConnection automatically configures Spring Boot's datasource
     * properties from the container (URL, username, password).
     * 
     * @return PostgreSQLContainer configured with PostgreSQL 15
     */
    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true); // Reuse container across test runs for performance
    }
}
