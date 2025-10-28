# Troubleshooting Guide

Common issues and solutions for the Animal Disease Reporting System.

## 🔧 Backend Issues

### Database Connection Failed

**Symptoms:**
- Application fails to start
- Error: "Connection refused" or "Unable to connect to database"

**Solutions:**
1. Check PostgreSQL is running:
   ```powershell
   Get-Service -Name postgresql*
   ```

2. Verify database exists:
   ```bash
   psql -U postgres -l
   ```

3. Check credentials in `application.properties`:
   ```properties
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   ```

4. Test database connection:
   ```bash
   psql -U postgres -d adrsdb
   ```

### JWT Token Issues

**Symptoms:**
- "Invalid JWT signature" errors
- 401 Unauthorized on API calls
- Immediate logout after login

**Solutions:**
1. Check JWT secret is at least 256 bits (32 characters):
   ```properties
   JWT_SECRET=YourVerySecureSecretKeyThatIsAtLeast256BitsLong
   ```

2. Verify token expiration:
   ```properties
   JWT_EXPIRATION_MS=86400000  # 24 hours
   ```

3. Clear browser localStorage and login again

4. Check system clock is synchronized

### Port Already in Use

**Symptoms:**
- "Port 8081 is already in use"

**Solutions:**
1. Find process using port:
   ```powershell
   Get-NetTCPConnection -LocalPort 8081
   ```

2. Kill the process or change port:
   ```properties
   SERVER_PORT=8082
   ```

### Maven Build Fails

**Symptoms:**
- Build errors during `mvn clean install`

**Solutions:**
1. Clean Maven cache:
   ```bash
   mvn clean
   mvn dependency:purge-local-repository
   ```

2. Check Java version:
   ```bash
   java -version  # Should be 21+
   ```

3. Update Maven:
   ```bash
   mvn -version  # Should be 3.8+
   ```

## 🎨 Frontend Issues

### Cannot Connect to Backend

**Symptoms:**
- Network errors in browser console
- "Failed to fetch" errors
- CORS errors

**Solutions:**
1. Verify backend is running on port 8081

2. Check proxy configuration in `vite.config.js`:
   ```javascript
   proxy: {
     '/api': {
       target: 'http://localhost:8081',
       changeOrigin: true,
     }
   }
   ```

3. Verify CORS configuration in `SecurityConfig.java`:
   ```java
   configuration.setAllowedOrigins(List.of(
     "http://localhost:3000",
     "http://localhost:5173"
   ));
   ```

4. Check `.env` file:
   ```
   VITE_API_URL=http://localhost:8081/api
   ```

### npm install Fails

**Symptoms:**
- Error during `npm install`
- Dependency resolution errors

**Solutions:**
1. Clear npm cache:
   ```bash
   npm cache clean --force
   ```

2. Delete node_modules and package-lock.json:
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```

3. Check Node version:
   ```bash
   node -version  # Should be 18+
   ```

### Build Errors

**Symptoms:**
- Errors during `npm run build`
- Import/export errors

**Solutions:**
1. Check for missing dependencies:
   ```bash
   npm install
   ```

2. Fix ESLint errors:
   ```bash
   npm run lint
   ```

3. Clear build cache:
   ```bash
   rm -rf dist
   npm run build
   ```

## 🔐 Authentication Issues

### Cannot Login

**Symptoms:**
- "Invalid username or password" error
- Correct credentials not working

**Solutions:**
1. Verify default admin exists in database:
   ```sql
   SELECT * FROM users WHERE username = 'admin';
   ```

2. Re-run admin seed script:
   ```bash
   psql -U postgres -d adrsdb -f database/seed/04_default_admin.sql
   ```

3. Check password is correct: `admin123`

4. Verify user is active:
   ```sql
   UPDATE users SET active = true WHERE username = 'admin';
   ```

### Token Expires Too Quickly

**Symptoms:**
- Logged out after short time
- Frequent re-login required

**Solutions:**
1. Increase token expiration:
   ```properties
   JWT_EXPIRATION_MS=86400000  # 24 hours
   ```

2. Check browser localStorage isn't being cleared

### 401 Unauthorized on API Calls

**Symptoms:**
- API calls fail with 401
- Token seems valid

**Solutions:**
1. Check Authorization header is being sent:
   - Open browser DevTools > Network
   - Check request headers

2. Verify token format:
   ```
   Authorization: Bearer <token>
   ```

3. Check token hasn't expired

4. Clear localStorage and login again

## 👥 User Management Issues

### Cannot Create Users

**Symptoms:**
- "Access Denied" when creating users
- 403 Forbidden error

**Solutions:**
1. Verify logged in as ADMIN:
   ```javascript
   console.log(localStorage.getItem('user'))
   ```

2. Check role in database:
   ```sql
   SELECT username, role FROM users;
   ```

3. Re-login to refresh token

### Email/Username Already Exists

**Symptoms:**
- "Email already exists" error
- "Username already exists" error

**Solutions:**
1. Check existing users:
   ```sql
   SELECT username, email FROM users;
   ```

2. Use unique username/email

3. Delete duplicate if needed:
   ```sql
   DELETE FROM users WHERE id = <duplicate_id>;
   ```

## 🗄️ Database Issues

### Migrations Not Running

**Symptoms:**
- Tables don't exist
- Column not found errors

**Solutions:**
1. Check JPA DDL mode:
   ```properties
   JPA_DDL_AUTO=update  # or create
   ```

2. Manually run migration scripts:
   ```bash
   psql -U postgres -d adrsdb -f database/init.sql
   ```

3. Verify database structure:
   ```sql
   \dt  -- List tables
   \d users  -- Describe users table
   ```

### Data Not Persisting

**Symptoms:**
- Data disappears after restart
- Changes not saved

**Solutions:**
1. Check transaction management:
   - Verify `@Transactional` annotations
   - Check for rollback conditions

2. Verify database isn't being recreated:
   ```properties
   JPA_DDL_AUTO=update  # NOT create-drop
   ```

3. Check database logs for errors

## 🎨 Styling Issues

### Styles Not Applying

**Symptoms:**
- Page looks unstyled
- CSS not loading

**Solutions:**
1. Verify CSS imports in components:
   ```javascript
   import '../../styles/auth.css';
   ```

2. Check browser console for 404 errors

3. Clear browser cache

4. Check CSS variables are defined:
   ```css
   :root {
     --color-primary: #2563eb;
   }
   ```

### Responsive Issues

**Symptoms:**
- Layout broken on mobile
- Elements overlapping

**Solutions:**
1. Check viewport meta tag in `index.html`:
   ```html
   <meta name="viewport" content="width=device-width, initial-scale=1.0" />
   ```

2. Test with browser DevTools responsive mode

3. Add media queries if needed

## 🔍 Debugging Tips

### Enable Debug Logging

**Backend:**
```properties
LOG_LEVEL_APP=DEBUG
LOG_LEVEL_SECURITY=DEBUG
JPA_SHOW_SQL=true
```

**Frontend:**
```javascript
console.log('Current user:', localStorage.getItem('user'));
console.log('Token:', localStorage.getItem('token'));
```

### Check Network Requests

1. Open browser DevTools (F12)
2. Go to Network tab
3. Filter by XHR/Fetch
4. Check request/response details

### Common Error Messages

| Error | Likely Cause | Solution |
|-------|-------------|----------|
| 401 Unauthorized | Invalid/expired token | Re-login |
| 403 Forbidden | Insufficient permissions | Check user role |
| 404 Not Found | Wrong URL/endpoint | Verify API path |
| 500 Internal Server Error | Backend exception | Check server logs |
| CORS error | Frontend not allowed | Update CORS config |

## 📊 Performance Issues

### Slow API Responses

**Solutions:**
1. Check database indexes
2. Enable query logging to find slow queries
3. Increase connection pool size:
   ```properties
   DB_POOL_SIZE=20
   ```

### High Memory Usage

**Solutions:**
1. Reduce pool sizes
2. Check for memory leaks
3. Enable JVM monitoring
4. Adjust heap size:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx512m"
   ```

## 🆘 Still Having Issues?

1. Check application logs:
   - Backend: `backend/logs/application.log`
   - Browser console for frontend

2. Review documentation:
   - `SETUP_GUIDE.md`
   - `IMPLEMENTATION_SUMMARY.md`

3. Common debugging steps:
   ```bash
   # Backend health check
   curl http://localhost:8081/actuator/health
   
   # Test login endpoint
   curl -X POST http://localhost:8081/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin123"}'
   ```

4. Verify environment:
   - Java version: `java -version`
   - Maven version: `mvn -version`
   - Node version: `node -version`
   - PostgreSQL: `psql --version`

## 📝 Reporting Issues

When reporting issues, include:
- Error message (exact text)
- Steps to reproduce
- Environment (OS, Java, Node versions)
- Relevant log excerpts
- Browser console errors (for frontend issues)
- Network tab screenshots (for API issues)
