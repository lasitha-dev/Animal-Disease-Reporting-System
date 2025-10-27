# 🚀 Setup Guide - Animal Disease Reporting System

This guide will help you set up and run the Animal Disease Reporting System on your local machine.

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

### Required Software

1. **Java Development Kit (JDK) 17 or higher**
   - Download: https://www.oracle.com/java/technologies/downloads/
   - Verify installation: `java -version`

2. **Apache Maven 3.6 or higher**
   - Download: https://maven.apache.org/download.cgi
   - Verify installation: `mvn -version`

3. **PostgreSQL 15 or higher**
   - Download: https://www.postgresql.org/download/
   - Verify installation: `psql --version`

4. **Git**
   - Download: https://git-scm.com/downloads
   - Verify installation: `git --version`

### Recommended IDE

- IntelliJ IDEA (Community or Ultimate)
- Eclipse IDE for Enterprise Java and Web Developers
- Visual Studio Code with Java extensions

---

## 🔧 Installation Steps

### Step 1: Clone the Repository

```bash
git clone https://github.com/lasitha-dev/Animal-Disease-Reporting-System.git
cd Animal-Disease-Reporting-System
```

### Step 2: Database Setup

1. **Create the database:**
   ```bash
   psql -U postgres
   CREATE DATABASE adrsdb;
   \q
   ```

2. **Run initialization script:**
   ```bash
   psql -U postgres -d adrsdb -f database/init.sql
   ```

3. **Load seed data:**
   ```bash
   psql -U postgres -d adrsdb -f database/seed/01_animal_types.sql
   psql -U postgres -d adrsdb -f database/seed/02_diseases.sql
   psql -U postgres -d adrsdb -f database/seed/03_default_users.sql
   ```

### Step 3: Configure Environment Variables

1. **Copy the example environment file:**
   ```bash
   cp config/.env.example config/.env
   ```

2. **Edit `config/.env` with your settings:**
   ```env
   DB_URL=jdbc:postgresql://localhost:5432/adrsdb
   DB_USERNAME=postgres
   DB_PASSWORD=your_secure_password
   ADMIN_USERNAME=admin
   ADMIN_PASSWORD=change_this_password
   ```

3. **Important Security Notes:**
   - Change default passwords immediately
   - Never commit `.env` file to version control
   - Use strong passwords in production

### Step 4: Build the Application

```bash
cd backend
mvn clean install
```

This will:
- Download all dependencies
- Compile the source code
- Run tests
- Create the JAR file

### Step 5: Run the Application

#### Option A: Using Maven

```bash
cd backend
mvn spring-boot:run
```

#### Option B: Using the JAR file

```bash
cd backend
java -jar target/animal-disease-reporting-system-1.0.0-SNAPSHOT.jar
```

#### Option C: Using the start script (Linux/Mac)

```bash
cd backend/scripts
chmod +x start.sh
./start.sh
```

### Step 6: Access the Application

1. **Open your web browser and navigate to:**
   ```
   http://localhost:8080/adrs
   ```

2. **Default Login Credentials:**
   - **Admin:**
     - Username: `admin`
     - Password: `Admin@123`
   
   - **Field Vet:**
     - Username: `field_vet_01`
     - Password: `Admin@123`
   
   - **District Vet:**
     - Username: `district_vet_01`
     - Password: `Admin@123`

   ⚠️ **Change these passwords immediately after first login!**

### Step 7: Verify Installation

1. **Check application health:**
   ```
   http://localhost:8080/adrs/actuator/health
   ```

2. **Access API documentation (Swagger):**
   ```
   http://localhost:8080/adrs/swagger-ui.html
   ```

3. **View application logs:**
   - Logs are stored in: `backend/logs/application.log`

---

## 🛠️ Development Setup

### IDE Configuration

#### IntelliJ IDEA

1. **Open the project:**
   - File → Open → Select the `backend` folder
   
2. **Configure JDK:**
   - File → Project Structure → Project → SDK: Java 17

3. **Enable annotation processing:**
   - Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Check "Enable annotation processing" (for Lombok)

4. **Run configuration:**
   - Create a new Spring Boot run configuration
   - Main class: `com.adrs.Application`

#### VS Code

1. **Install extensions:**
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Lombok Annotations Support

2. **Open the project:**
   - File → Open Folder → Select the root directory

3. **Configure Java home:**
   - Settings → Java: Home → Set to JDK 17 path

### Running Tests

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# View coverage report
open backend/target/site/jacoco/index.html
```

### Code Quality

```bash
# Check code style
mvn checkstyle:check

# Generate project reports
mvn site
```

---

## 🐛 Troubleshooting

### Common Issues

#### 1. Port 8080 already in use

**Solution:**
```bash
# Find process using port 8080
# Windows:
netstat -ano | findstr :8080
taskkill /PID <process_id> /F

# Linux/Mac:
lsof -ti:8080 | xargs kill -9
```

Or change the port in `application.properties`:
```properties
server.port=8081
```

#### 2. Database connection failed

**Check:**
- PostgreSQL is running: `docker-compose ps` or `pg_isready`
- Database credentials in `.env` or `application.properties`
- Database exists: `psql -U postgres -l`
- Firewall allows port 5432

#### 3. Maven build fails

**Solution:**
```bash
# Clean Maven cache
mvn clean

# Update dependencies
mvn clean install -U

# Skip tests temporarily
mvn clean install -DskipTests
```

#### 4. Lombok not working

**Solution:**
- Enable annotation processing in IDE
- Reimport Maven project
- Install Lombok plugin for your IDE

#### 5. Application fails to start

**Check:**
- Java version: `java -version` (should be 17+)
- Maven version: `mvn -version` (should be 3.6+)
- Check logs: `backend/logs/application.log`
- Verify database is accessible

---

## 📚 Additional Resources

- **Spring Boot Documentation:** https://spring.boot.io/docs
- **Thymeleaf Documentation:** https://www.thymeleaf.org/documentation.html
- **PostgreSQL Documentation:** https://www.postgresql.org/docs/
- **Project Wiki:** (Coming soon)
- **API Documentation:** http://localhost:8080/adrs/swagger-ui.html

---

## 🆘 Getting Help

If you encounter any issues:

1. Check this setup guide
2. Review the [CHANGELOG.md](CHANGELOG.md) for known issues
3. Check the logs: `backend/logs/application.log`
4. Open an issue on GitHub: https://github.com/lasitha-dev/Animal-Disease-Reporting-System/issues

---

## 🔐 Production Deployment

For production deployment instructions, see [DEPLOYMENT.md](DEPLOYMENT.md) (coming soon).

**Important production considerations:**
- Use strong, unique passwords
- Set `JPA_DDL_AUTO=validate` (never use `update` in production)
- Enable HTTPS/SSL
- Configure proper firewall rules
- Set up regular database backups
- Configure log rotation
- Use environment-specific configuration files
- Set `THYMELEAF_CACHE=true`
- Disable Swagger in production or restrict access

---

## ✅ Next Steps

After successful setup:

1. Change all default passwords
2. Explore the application features
3. Review the codebase structure
4. Read the API documentation
5. Start developing new features!

---

**Happy Coding! 🎉**
