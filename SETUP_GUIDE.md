# Setup Guide for Animal Disease Reporting System

This guide will help you set up and run the Animal Disease Reporting System with login, authentication, and user management.

## Prerequisites

- Java 21 or higher
- Maven 3.8+
- PostgreSQL 14+
- Node.js 18+ and npm
- Git

## Backend Setup

### 1. Database Setup

```bash
# Create PostgreSQL database
createdb adrsdb

# Or using psql
psql -U postgres
CREATE DATABASE adrsdb;
\q
```

### 2. Run Database Migrations

```bash
# Navigate to database directory
cd database

# Run initialization script
psql -U postgres -d adrsdb -f init.sql

# Run seed scripts
psql -U postgres -d adrsdb -f seed/01_animal_types.sql
psql -U postgres -d adrsdb -f seed/02_diseases.sql
psql -U postgres -d adrsdb -f seed/03_default_users.sql
psql -U postgres -d adrsdb -f seed/04_default_admin.sql
```

### 3. Configure Environment Variables

```bash
# Copy the example environment file
cp config/.env.example config/.env

# Edit the .env file with your database credentials
# Update DB_PASSWORD, JWT_SECRET, etc.
```

### 4. Build and Run Backend

```bash
# Navigate to backend directory
cd backend

# Clean and install dependencies
mvn clean install -DskipTests

# Run the application
mvn spring-boot:run
```

The backend will start on `http://localhost:8081`

## Frontend Setup

### 1. Install Dependencies

```bash
# Navigate to frontend directory
cd frontend

# Install npm packages
npm install
```

### 2. Configure Environment Variables

```bash
# Copy the example environment file
cp .env.example .env

# The default configuration should work if backend is on port 8081
```

### 3. Run Frontend

```bash
# Start development server
npm run dev
```

The frontend will start on `http://localhost:3000`

## Default Admin Credentials

After running the database seed script, you can login with:

- **Username**: `admin`
- **Password**: `admin123`

**⚠️ IMPORTANT**: Change this password immediately after first login in production!

## Features

### Authentication
- JWT-based authentication
- Secure login system
- Role-based access control (ADMIN, VETERINARY_OFFICER)

### User Management (Admin Only)
- Create new users (Admins and Veterinary Officers)
- Edit existing users
- Activate/Deactivate user accounts
- Delete users
- View all users in a table

### Security
- No public registration (admins create users)
- Password encryption with BCrypt
- JWT token expiration
- Protected API endpoints
- Role-based authorization

## API Documentation

Once the backend is running, you can access:

- **Swagger UI**: `http://localhost:8081/swagger-ui.html`
- **API Docs**: `http://localhost:8081/api-docs`

## Project Structure

### Backend (`/backend`)
```
src/main/java/com/adrs/
├── config/          # Security & JWT configuration
├── controller/      # REST API controllers
├── dto/            # Data Transfer Objects
├── exception/      # Exception handlers
├── model/          # JPA entities
├── repository/     # Data repositories
└── service/        # Business logic
```

### Frontend (`/frontend`)
```
src/
├── components/     # Reusable React components
├── hooks/         # Custom React hooks (auth)
├── pages/         # Page components
├── services/      # API service layer
└── styles/        # CSS files (variables, global, components)
```

## Available Scripts

### Backend
```bash
mvn clean install      # Build project
mvn spring-boot:run   # Run application
mvn test              # Run tests
```

### Frontend
```bash
npm run dev      # Start development server
npm run build    # Build for production
npm run preview  # Preview production build
npm run lint     # Run ESLint
```

## Troubleshooting

### Database Connection Issues
- Verify PostgreSQL is running
- Check database credentials in `application.properties`
- Ensure database `adrsdb` exists

### JWT Token Issues
- Ensure JWT_SECRET is at least 256 bits (32 characters)
- Check token expiration settings

### CORS Issues
- Frontend and backend CORS are pre-configured
- Default allowed origins: `http://localhost:3000`, `http://localhost:5173`

### Port Conflicts
- Backend default: 8081
- Frontend default: 3000
- Change in respective configuration files if needed

## Development Guidelines

### Code Style
- Follow SOLID principles
- Use meaningful variable/method names
- Add Javadoc comments for public methods
- Keep methods under 30 lines
- No hardcoded values (use environment variables)

### Security Best Practices
- Never commit `.env` files
- Use environment variables for all secrets
- Validate all user input
- Use prepared statements (JPA handles this)
- Follow principle of least privilege

### CSS Guidelines
- Use CSS variables from `variables.css`
- No inline styles (except dynamic values)
- Follow BEM naming convention
- Keep styles modular and reusable

## Production Deployment

### Backend
1. Update `application.properties` with production database
2. Set strong JWT_SECRET (minimum 256 bits)
3. Enable HTTPS
4. Set `JPA_DDL_AUTO=validate`
5. Disable Swagger in production
6. Configure proper logging levels

### Frontend
1. Build production bundle: `npm run build`
2. Serve from CDN or static file server
3. Update `VITE_API_URL` to production backend URL
4. Enable HTTPS

### Security Checklist
- [ ] Change default admin password
- [ ] Use strong JWT secret
- [ ] Enable HTTPS
- [ ] Configure firewall rules
- [ ] Set up database backups
- [ ] Enable security headers
- [ ] Configure rate limiting
- [ ] Review and minimize exposed endpoints

## Support

For issues or questions, please refer to the project documentation or contact the development team.
