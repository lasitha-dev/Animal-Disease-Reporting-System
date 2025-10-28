# 📁 Project Structure - Animal Disease Reporting System

This document provides a complete overview of the project structure and organization.

## 📂 Root Directory Structure

```
Animal-Disease-Reporting-System/
├── .git/                           # Git version control
├── .github/                        # GitHub specific files
│   └── instructions/               # Project instructions
│       └── rules.instructions.md   # Coding standards and rules
│
├── backend/                        # Spring Boot backend application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/adrs/     # Java source code
│   │   │   │   ├── config/        # Configuration classes
│   │   │   │   ├── controller/    # REST and Web controllers
│   │   │   │   ├── service/       # Business logic layer
│   │   │   │   ├── repository/    # Data access layer
│   │   │   │   ├── model/         # JPA entities
│   │   │   │   ├── dto/           # Data transfer objects
│   │   │   │   ├── exception/     # Custom exceptions
│   │   │   │   └── Application.java # Main application class
│   │   │   │
│   │   │   └── resources/
│   │   │       ├── static/         # Static resources (CSS, JS, images)
│   │   │       │   ├── css/
│   │   │       │   └── js/
│   │   │       ├── templates/      # Thymeleaf HTML templates
│   │   │       │   ├── layouts/    # Layout templates
│   │   │       │   ├── farms/      # Farm management pages
│   │   │       │   ├── animals/    # Animal management pages
│   │   │       │   ├── diseases/   # Disease reporting pages
│   │   │       │   └── fragments/  # Reusable template fragments
│   │   │       └── application.properties # Application configuration
│   │   │
│   │   └── test/                   # Unit and integration tests
│   │       └── java/com/adrs/test/
│   │           ├── controller/     # Controller tests
│   │           ├── service/        # Service tests
│   │           └── repository/     # Repository tests
│   │
│   ├── logs/                       # Application log files
│   │   └── application.log
│   │
│   ├── scripts/                    # Utility scripts
│   │   ├── start.sh               # Start application script
│   │   └── stop.sh                # Stop application script
│   │
│   ├── target/                     # Maven build output (gitignored)
│   └── pom.xml                     # Maven project configuration
│
├── config/                         # Configuration files
│   ├── .env.example               # Environment variables template
│   └── application.yml            # YAML configuration (alternative)
│
├── database/                       # Database scripts and data
│   ├── migrations/                # Schema migration scripts
│   │   └── migration_template.sql
│   ├── seed/                      # Initial data scripts
│   │   ├── 01_animal_types.sql
│   │   ├── 02_diseases.sql
│   │   └── 03_default_users.sql
│   └── init.sql                   # Database initialization script
│
├── frontend/                       # Future web dashboard (placeholder)
│   ├── public/                    # Static assets
│   ├── src/
│   │   ├── components/            # Reusable UI components
│   │   ├── pages/                 # Page-level components
│   │   ├── services/              # API service calls
│   │   ├── hooks/                 # Custom React hooks
│   │   └── assets/                # Images, fonts, etc.
│   └── README.md
│
├── .gitignore                      # Git ignore rules
├── CHANGELOG.md                    # Version history and changes
├── QUICKSTART.md                   # Quick start guide
├── PROJECT_STRUCTURE.md            # This file - project structure documentation
├── README.md                       # Project overview and documentation
└── SETUP.md                        # Detailed setup instructions
```

## 🎯 Key Directories Explained

### `/backend` - Spring Boot Application

The main backend application built with Spring Boot. Contains all server-side code.

**Key Files:**
- `pom.xml` - Maven dependencies and build configuration
- `Application.java` - Main entry point for Spring Boot
- `application.properties` - Application configuration

**Package Structure:**
- `config/` - Configuration beans and settings
- `controller/` - HTTP request handlers
- `service/` - Business logic implementation
- `repository/` - Database access interfaces
- `model/` - JPA entities (database tables)
- `dto/` - Data transfer objects
- `exception/` - Custom exception classes

### `/database` - Database Management

Contains all database-related scripts and data.

**Structure:**
- `init.sql` - Creates tables, indexes, and triggers
- `seed/` - Initial data for reference tables
- `migrations/` - Schema change scripts (versioned)

### `/config` - Configuration Files

Environment-specific configuration files.

**Files:**
- `.env.example` - Template for environment variables
- `application.yml` - Alternative YAML-based configuration

### `/frontend` - Future Web Dashboard

Placeholder for future modern web framework implementation.
Currently using Thymeleaf templates in `backend/src/main/resources/templates/`

## 🔧 Technology Stack by Directory

### Backend (`/backend`)
- **Framework:** Spring Boot 3.2.0
- **Language:** Java 21
- **Build Tool:** Maven
- **Database:** PostgreSQL 15
- **Template Engine:** Thymeleaf
- **Security:** Spring Security
- **Testing:** JUnit 5, Mockito

### Database (`/database`)
- **Primary DB:** PostgreSQL 15
- **Secondary DB:** MySQL (configured, not primary)
- **Migration Tool:** SQL scripts (manual)

### Frontend (`/frontend`)
- **Current:** Thymeleaf (server-side)
- **Future:** React/Vue.js (client-side)

## 📝 Configuration Files

### Maven (`pom.xml`)
- Spring Boot dependencies
- Database drivers (PostgreSQL, MySQL)
- Thymeleaf and Spring Security
- Testing frameworks (JUnit, Mockito)
- Code coverage (JaCoCo - 80% minimum)
- API documentation (Springdoc OpenAPI)

### Application Properties
- Database connection settings
- Server configuration
- Logging configuration
- Security settings
- Thymeleaf settings
- File upload limits
- Actuator endpoints

## 🔒 Security Configuration

### Protected Files (gitignored)
- `.env` - Environment variables with secrets
- `*.log` - Log files
- `target/` - Build artifacts
- `*.class`, `*.jar` - Compiled files
- IDE-specific files (`.idea/`, `.vscode/`, etc.)

### Environment Variables
All sensitive data is configured via environment variables:
- Database credentials
- Admin passwords
- API keys (future)
- Session secrets (future)

## 📊 Database Schema

### Core Tables
1. **users** - User accounts and roles (Admin, Field Vet, District Vet)
2. **farms** - Farm information with GPS coordinates
3. **animal_types** - Reference data for animal types
4. **animals** - Individual animal records
5. **diseases** - Master list of diseases
6. **disease_reports** - Disease outbreak records

### Relationships
- Users → Farms (created_by)
- Farms → Animals (one-to-many)
- Animals → Animal Types (many-to-one)
- Animals → Disease Reports (one-to-many)
- Diseases → Disease Reports (one-to-many)

## 🧪 Testing Structure

### Unit Tests (`/backend/src/test`)
- Controller tests - HTTP request/response testing
- Service tests - Business logic testing
- Repository tests - Database operations testing

### Integration Tests
- End-to-end workflow testing
- API integration testing
- Database integration testing

### Coverage Goal
- Minimum 80% code coverage (enforced by JaCoCo)

## 📚 Documentation Files

- **README.md** - Project overview and quick start
- **SETUP.md** - Detailed setup instructions
- **CHANGELOG.md** - Version history and changes
- **rules.instructions.md** - Coding standards and guidelines
- **PROJECT_STRUCTURE.md** - This file

## 🚀 Build Artifacts

### Generated Directories (gitignored)
- `backend/target/` - Maven build output
- `backend/logs/` - Application logs
- `node_modules/` - NPM dependencies (future)
- `.mvn/` - Maven wrapper files

### Important Files in `target/`
- `.jar` files - Executable application packages
- `/classes/` - Compiled Java classes
- `/test-classes/` - Compiled test classes
- `/site/` - Generated documentation and reports

## 🔄 Development Workflow

1. **Code Changes** → `backend/src/main/java/`
2. **Build** → `mvn clean package`
3. **Test** → `mvn test`
4. **Run** → `mvn spring-boot:run`
5. **Access** → `http://localhost:8080/adrs`

## 📖 Additional Resources

- **API Docs:** http://localhost:8080/adrs/swagger-ui.html
- **Actuator:** http://localhost:8080/adrs/actuator
- **Logs:** `backend/logs/application.log`

---

## 🎓 Best Practices

### Code Organization
- Follow SOLID principles
- Keep methods small (under 30 lines)
- Use dependency injection
- Write comprehensive Javadocs

### File Naming
- Controllers: `*Controller.java`
- Services: `*Service.java`
- Repositories: `*Repository.java`
- DTOs: `*DTO.java`
- Tests: `*Test.java` or `*Tests.java`

### Package Naming
- Use lowercase for package names
- Follow reverse domain convention: `com.adrs.*`
- Group by feature when complexity increases

---

**Last Updated:** October 27, 2025  
**Version:** 1.0.0-SNAPSHOT
