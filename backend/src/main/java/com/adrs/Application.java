package com.adrs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main entry point for the Animal Disease Reporting System application.
 * 
 * This Spring Boot application provides a centralized platform for veterinary officers
 * and administrators to manage animal disease data, farms, and analytics across Sri Lanka.
 * 
 * Key Features:
 * - Role-based access control (Admin, Field Vet, District Vet)
 * - Farm and animal management with GPS coordinates
 * - Disease reporting and tracking
 * - Interactive map visualization
 * - Analytics dashboards and reporting
 * 
 * @author Animal Disease Reporting System Team
 * @version 1.0.0
 * @since 2025-10-27
 */
@SpringBootApplication
@EnableJpaAuditing
public class Application {

    /**
     * Main method to bootstrap the Spring Boot application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
