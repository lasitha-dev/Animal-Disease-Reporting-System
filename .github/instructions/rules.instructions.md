---
applyTo: '**'
---
1. General Coding Standards

Follow SOLID principles in all object-oriented code.

Code must be clean, modular, and easy to maintain.

Avoid code smells, duplicated logic, and god classes.

Use meaningful, descriptive names for classes, methods, and variables.

Keep methods small and focused — ideally under 30 lines.

Every public method and class should have Javadoc comments describing its purpose and behavior.

Implement proper exception handling and logging instead of silently catching errors.

All configuration and credentials must be read from environment variables — never hardcoded.

2. Security Guidelines

Never expose API keys, URLs, or database credentials inside the code.

Use environment variables for all sensitive information.

Sanitize and validate all user input to prevent XSS, SQL injection, and data tampering.

Ensure all API endpoints enforce role-based access control (RBAC).

Use HTTPS and secure authentication mechanisms (JWT, Spring Security, etc.).

Follow the principle of least privilege for database and service access.

3. Coding Practices for AI Agent & Developers

Use dependency injection for all service dependencies (no tight coupling).

Implement interfaces for service and repository layers.

Avoid global or static state whenever possible.

Always close resources (database connections, streams, etc.) properly.

Log all errors and important operations using a logging framework (e.g., SLF4J, Logback).

Never commit .env, credentials, or sensitive configuration files to version control.

Keep all commit messages clear and concise, describing what was changed and why.

4. Project File Structure

Follow the standard Maven layout for Spring Boot applications:
project-root/
│
├── README.md
├── .gitignore
├── docker-compose.yml
│
├── backend/
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/adrs/
│   │   │   │   ├── Application.java
│   │   │   │   ├── config/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   ├── dto/
│   │   │   │   └── exception/
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── static/
│   │   │       ├── templates/
│   │   │       │   ├── layouts/
│   │   │       │   ├── farms/
│   │   │       │   ├── animals/
│   │   │       │   └── diseases/
│   │   │       └── templates/fragments/
│   │   └── test/
│   │       ├── java/com/adrs/test/controller/
│   │       ├── java/com/adrs/test/service/
│   │       └── java/com/adrs/test/repository/
│   │
│   ├── logs/
│   │   └── application.log
│   │
│   └── scripts/
│       ├── start.sh
│       ├── stop.sh
│       └── db_migration.sql
│
├── frontend/              # 🔹 Future placeholder for web dashboard
│   ├── package.json
│   ├── vite.config.js     # or webpack.config.js
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── hooks/
│   │   ├── assets/
│   │   └── App.jsx
│   └── README.md
│
├── config/
│   ├── application.yml
│   └── .env.example
│
└── database/
    ├── migrations/
    ├── seed/
    └── init.sql


5. Framework and Technology Rules

Backend: Spring Boot

Frontend Template Engine: Thymeleaf

Database: PostgreSQL

OS Environment: Linux (VM-based deployment)

Remote Logging: Must remain enabled and secure.

Use proper annotations:

@Controller or @RestController for controllers.

@Service for business logic.

@Repository for data persistence.

@Entity for JPA models.

6. Testing Requirements

Include unit tests and integration tests for all modules.

Use JUnit and Mockito for testing.

Maintain minimum 80% test coverage.

Test all CRUD operations and service-layer logic.

Validate all API endpoints with mock data before deployment.

7. Performance Guidelines

Use lazy loading for JPA relationships to avoid unnecessary queries.

Apply pagination for large dataset queries.

Cache frequently accessed data where appropriate.

Optimize database queries and indexes.

8. Documentation Rules

Maintain a detailed README.md at all times.

Generate API documentation using Swagger or Springdoc OpenAPI.

Keep a CHANGELOG.md to record version history.

Comment every significant block of code and describe complex logic briefly.

9. Deployment Rules

All deployment configurations (DB credentials, secrets, ports) must come from environment variables.

.env file must be included only as .env.example with placeholder values.

Logs must be redirected to the /logs directory for remote access.

No sensitive data should ever be printed or logged in plaintext.

Ensure proper error messages without exposing stack traces in production.