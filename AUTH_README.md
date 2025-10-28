# Authentication & User Management - Quick Reference

## 🚀 Quick Start

### Option 1: Automated Setup (Windows)
```powershell
.\quickstart.ps1
```

### Option 2: Manual Setup

#### 1. Database Setup
```bash
# Create database
createdb adrsdb

# Run migrations
psql -U postgres -d adrsdb -f database/init.sql
psql -U postgres -d adrsdb -f database/seed/04_default_admin.sql
```

#### 2. Backend
```bash
cd backend
mvn clean install -DskipTests
mvn spring-boot:run
```

#### 3. Frontend
```bash
cd frontend
npm install
npm run dev
```

## 🔐 Default Credentials

**Username:** `admin`  
**Password:** `admin123`

⚠️ **Change immediately after first login!**

## 📋 Features Implemented

### ✅ Authentication System
- JWT-based authentication
- Secure login/logout
- Token expiration
- Protected routes

### ✅ User Management (Admin Only)
- Create users (Admin/Veterinary Officer)
- Edit user information
- Activate/Deactivate accounts
- Delete users
- View all users

### ✅ Security Features
- No public registration
- BCrypt password encryption
- Role-based access control
- Protected API endpoints
- Input validation

### ✅ Code Quality
- SOLID principles
- No hardcoded values
- Environment variables
- Separated CSS files
- Javadoc documentation
- Error handling

## 🌐 URLs

- **Frontend:** http://localhost:3000
- **Backend:** http://localhost:8081
- **API Docs:** http://localhost:8081/swagger-ui.html

## 📁 Key Files

### Backend
- `User.java` - User entity with roles
- `SecurityConfig.java` - Security configuration
- `JwtTokenProvider.java` - JWT utilities
- `AuthController.java` - Authentication endpoints
- `UserController.java` - User management endpoints

### Frontend
- `Login.jsx` - Login page
- `UserManagement.jsx` - User management page
- `useAuth.jsx` - Authentication context
- `authService.js` - Auth API calls
- `variables.css` - Design system

## 🎨 Styling

All styles use CSS variables from `variables.css`:
- Colors: `--color-primary`, `--color-success`, etc.
- Spacing: `--spacing-sm`, `--spacing-md`, etc.
- Typography: `--font-size-base`, `--font-weight-medium`, etc.

No inline styles or hardcoded colors!

## 📖 Documentation

- `SETUP_GUIDE.md` - Detailed setup instructions
- `IMPLEMENTATION_SUMMARY.md` - Complete implementation details
- `rules.instructions.md` - Coding standards

## 🔒 Security Notes

- All credentials via environment variables
- JWT secret must be 256+ bits
- Passwords are BCrypt hashed
- CORS configured for local development
- Admin-only endpoints protected

## 🧪 Testing

```bash
# Backend tests
cd backend
mvn test

# Frontend lint
cd frontend
npm run lint
```

## 📦 Dependencies

### Backend
- Spring Boot 3.2.0
- Spring Security 6
- JWT (jjwt 0.12.3)
- PostgreSQL Driver
- Lombok

### Frontend
- React 18
- React Router 6
- Axios
- Vite 5

## 🎯 User Roles

1. **ADMIN**
   - Full system access
   - User management
   - Create other admins
   
2. **VETERINARY_OFFICER**
   - Standard user access
   - Cannot manage users

## 🔄 Workflow

1. Admin logs in
2. Creates users (admins or officers)
3. New users receive credentials
4. Users log in and access system
5. Admin can deactivate/delete users

## ⚙️ Configuration

### Backend (application.properties)
```properties
JWT_SECRET=your-256-bit-secret
JWT_EXPIRATION_MS=86400000
DB_URL=jdbc:postgresql://localhost:5432/adrsdb
DB_USERNAME=postgres
DB_PASSWORD=your-password
```

### Frontend (.env)
```properties
VITE_API_URL=http://localhost:8081/api
```

## 🐛 Troubleshooting

**Database connection error:**
- Check PostgreSQL is running
- Verify credentials in application.properties

**CORS error:**
- Ensure frontend URL in SecurityConfig
- Check VITE_API_URL in frontend .env

**JWT error:**
- Verify JWT_SECRET is 256+ bits
- Check token expiration setting

**401 Unauthorized:**
- Token may be expired
- Clear localStorage and login again

## 📞 Support

Refer to SETUP_GUIDE.md for detailed troubleshooting and setup instructions.
