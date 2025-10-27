# ⚡ Quick Start Guide

Get the Animal Disease Reporting System up and running in minutes!

## 🎯 Prerequisites Checklist

- [ ] Java 21+ installed
- [ ] Maven 3.6+ installed
- [ ] PostgreSQL 15+ installed
- [ ] Git installed

## 🚀 Quick Setup (5 Steps)

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/lasitha-dev/Animal-Disease-Reporting-System.git
cd Animal-Disease-Reporting-System
```

### 2️⃣ Setup the Database
```bash
# Create database
psql -U postgres -c "CREATE DATABASE adrsdb;"

# Initialize schema
psql -U postgres -d adrsdb -f database/init.sql

# Load seed data
psql -U postgres -d adrsdb -f database/seed/01_animal_types.sql
psql -U postgres -d adrsdb -f database/seed/02_diseases.sql
psql -U postgres -d adrsdb -f database/seed/03_default_users.sql
```

### 3️⃣ Configure Environment
```bash
cp config/.env.example config/.env
```
*Edit `config/.env` if needed (optional for local development)*

### 4️⃣ Build & Run
```bash
cd backend
mvn spring-boot:run
```
*First build may take a few minutes to download dependencies*

### 5️⃣ Access the Application
Open your browser and navigate to:
```
http://localhost:8080/adrs
```

## 🔐 Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `Admin@123` |
| Field Vet | `field_vet_01` | `Admin@123` |
| District Vet | `district_vet_01` | `Admin@123` |

⚠️ **Change these passwords after first login!**

## 🛠️ Useful Commands

### Application Management
```bash
# Start application
cd backend && mvn spring-boot:run

# Stop application
# Press Ctrl+C in the terminal

# Build without running
mvn clean package

# Run tests
mvn test

# View logs
tail -f backend/logs/application.log
```

### Database Management
```bash
# Access database CLI
psql -U postgres -d adrsdb

# Backup database
pg_dump -U postgres adrsdb > backup.sql

# Restore database
psql -U postgres -d adrsdb < backup.sql

# View all databases
psql -U postgres -l

# Check PostgreSQL status (Windows)
Get-Service postgresql*

# Check PostgreSQL status (Linux/Mac)
sudo systemctl status postgresql
```

## 🌐 Important URLs

| Service | URL |
|---------|-----|
| Application | http://localhost:8080/adrs |
| API Documentation | http://localhost:8080/adrs/swagger-ui.html |
| Health Check | http://localhost:8080/adrs/actuator/health |

## 📁 Key Files & Locations

| Purpose | Location |
|---------|----------|
| Main Application | `backend/src/main/java/com/adrs/Application.java` |
| Configuration | `backend/src/main/resources/application.properties` |
| Environment Variables | `config/.env` |
| Database Schema | `database/init.sql` |
| Logs | `backend/logs/application.log` |
| Dependencies | `backend/pom.xml` |

## 🐛 Quick Troubleshooting

### Port 8080 already in use
```bash
# Find and kill process (Windows)
netstat -ano | findstr :8080
taskkill /PID <process_id> /F

# Or change port in application.properties
server.port=8081
```

### Database connection failed
```bash
# Check if PostgreSQL is running (Windows)
Get-Service postgresql*

# Check if PostgreSQL is running (Linux/Mac)
sudo systemctl status postgresql

# Restart PostgreSQL (Windows - run as Administrator)
Restart-Service postgresql-x64-15

# Restart PostgreSQL (Linux/Mac)
sudo systemctl restart postgresql

# Check connection
psql -U postgres -c "SELECT version();"
```

### Build fails
```bash
# Clean and rebuild
mvn clean install -U

# Skip tests temporarily
mvn clean package -DskipTests
```

### Lombok not working in IDE
1. Enable annotation processing in IDE settings
2. Install Lombok plugin for your IDE
3. Reimport Maven project

## 📚 Next Steps

After successful setup:

1. ✅ Change default passwords
2. 📖 Read the [SETUP.md](SETUP.md) for detailed information
3. 🏗️ Review [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md)
4. 📝 Check [CHANGELOG.md](CHANGELOG.md) for version history
5. 🎯 Follow coding standards in `.github/instructions/rules.instructions.md`
6. 🚀 Start developing!

## 💡 Pro Tips

- Use IntelliJ IDEA or VS Code for best development experience
- Enable "Build project automatically" in IDE
- Use Spring Boot DevTools for hot reload (already included)
- Check logs if something doesn't work as expected
- Run tests before committing: `mvn test`
- Keep your `.env` file updated with your local settings

## 🆘 Getting Help

- 📖 Detailed Setup: [SETUP.md](SETUP.md)
- 🏗️ Project Structure: [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md)
- 📋 Coding Rules: `.github/instructions/rules.instructions.md`
- 🐛 Issues: Create an issue on GitHub

---

**Ready to go? Let's build something amazing! 🎉**

*For detailed instructions, see [SETUP.md](SETUP.md)*
