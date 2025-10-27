# Changelog

All notable changes to the Animal Disease Reporting System will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed
- Upgraded Java runtime from Java 17 to Java 21 LTS
- Updated Maven compiler plugin to target Java 21

### Planned Features
- User authentication and authorization with Spring Security
- Farm management CRUD operations
- Animal registration and management
- Disease reporting system
- Interactive map visualization with Leaflet.js
- Analytics dashboards for Admin and District Vets
- Role-based access control (Admin, Field Vet, District Vet)
- API documentation with Swagger/OpenAPI
- Comprehensive unit and integration tests
- Data export and reporting features

## [1.0.0-SNAPSHOT] - 2025-10-27

### Added
- Initial project structure setup
- Maven configuration with Spring Boot 3.2.0
- PostgreSQL database configuration
- Thymeleaf template engine setup
- Spring Security dependency
- Spring Data JPA for database operations
- Database schema initialization script
- Seed data for animal types, diseases, and default users
- Environment variable configuration system
- Comprehensive .gitignore file
- Project documentation and README
- Logging configuration with file output
- Frontend placeholder directory structure
- JaCoCo code coverage plugin (80% minimum)
- Springdoc OpenAPI for API documentation

### Project Structure
- `/backend` - Spring Boot application
  - `/src/main/java/com/adrs` - Java source code
    - `/config` - Configuration classes
    - `/controller` - REST and web controllers
    - `/service` - Business logic layer
    - `/repository` - Data access layer
    - `/model` - JPA entities
    - `/dto` - Data transfer objects
    - `/exception` - Custom exceptions
  - `/src/main/resources` - Application resources
    - `/templates` - Thymeleaf HTML templates
    - `/static` - CSS, JavaScript, images
  - `/src/test` - Unit and integration tests
  - `/logs` - Application log files
  - `/scripts` - Utility scripts

- `/database` - Database scripts
  - `/migrations` - Schema migration scripts
  - `/seed` - Initial data scripts
  - `init.sql` - Database initialization

- `/config` - Configuration files
  - `.env.example` - Environment variable template
  - `application.yml` - Alternative YAML config

- `/frontend` - Future web dashboard (placeholder)

### Database Schema
- `users` - User accounts and roles
- `farms` - Farm information with GPS coordinates
- `animal_types` - Animal type reference data
- `animals` - Animal records linked to farms
- `diseases` - Disease master list
- `disease_reports` - Disease outbreak records

### Security
- Environment variable based configuration
- No hardcoded credentials
- BCrypt password hashing
- Session management configuration
- HTTPS ready configuration

### Technical Stack
- Java 21
- Spring Boot 3.2.0
- PostgreSQL 15
- Thymeleaf
- Maven 3.x

---

## Version Guidelines

- **Major version (X.0.0)**: Breaking changes, major feature additions
- **Minor version (0.X.0)**: New features, backwards compatible
- **Patch version (0.0.X)**: Bug fixes, minor improvements

---

## Categories

- **Added**: New features
- **Changed**: Changes in existing functionality
- **Deprecated**: Soon-to-be removed features
- **Removed**: Removed features
- **Fixed**: Bug fixes
- **Security**: Security vulnerabilities fixes

---

[Unreleased]: https://github.com/lasitha-dev/Animal-Disease-Reporting-System/compare/v1.0.0...HEAD
[1.0.0-SNAPSHOT]: https://github.com/lasitha-dev/Animal-Disease-Reporting-System/releases/tag/v1.0.0
