# Deployment Checklist

Use this checklist before deploying to production.

## 🔒 Security

### Backend Security
- [ ] Change default admin password
- [ ] Generate strong JWT secret (minimum 256 bits)
- [ ] Set `JWT_SECRET` via environment variable
- [ ] Remove or change seed script default credentials
- [ ] Set `JPA_DDL_AUTO=validate` (not update/create)
- [ ] Disable Swagger in production (`SWAGGER_ENABLED=false`)
- [ ] Enable HTTPS
- [ ] Configure proper CORS origins (remove localhost)
- [ ] Set strong database password
- [ ] Review and minimize exposed endpoints
- [ ] Enable rate limiting
- [ ] Configure security headers
- [ ] Set `SESSION_COOKIE_SECURE=true`
- [ ] Review log levels (set to WARN/ERROR in production)
- [ ] Disable SQL logging (`JPA_SHOW_SQL=false`)

### Frontend Security
- [ ] Build production bundle (`npm run build`)
- [ ] Update `VITE_API_URL` to production backend
- [ ] Enable HTTPS
- [ ] Remove development dependencies
- [ ] Configure proper Content Security Policy
- [ ] Enable secure cookies

### Database Security
- [ ] Change default postgres password
- [ ] Create application-specific database user
- [ ] Grant minimum required permissions
- [ ] Enable SSL for database connections
- [ ] Configure firewall rules
- [ ] Set up regular backups
- [ ] Enable database audit logging

## 🔧 Configuration

### Environment Variables
- [ ] Set all environment variables via secure method
- [ ] Never commit `.env` files
- [ ] Document required environment variables
- [ ] Use secrets management system (e.g., AWS Secrets Manager)
- [ ] Rotate secrets regularly

### Backend Configuration
- [ ] Review `application.properties`
- [ ] Configure production database connection
- [ ] Set appropriate pool sizes
- [ ] Configure proper logging
- [ ] Set up log rotation
- [ ] Configure actuator endpoints (secure or disable)
- [ ] Set appropriate session timeout
- [ ] Configure file upload limits

### Frontend Configuration
- [ ] Configure CDN for static assets
- [ ] Enable gzip/brotli compression
- [ ] Set up caching headers
- [ ] Configure error tracking (e.g., Sentry)
- [ ] Set up analytics (if needed)

## 🚀 Deployment

### Pre-deployment
- [ ] Run all tests (`mvn test`)
- [ ] Check code coverage (minimum 80%)
- [ ] Run linting (`npm run lint`)
- [ ] Build backend (`mvn clean install`)
- [ ] Build frontend (`npm run build`)
- [ ] Test production build locally
- [ ] Review all dependencies for vulnerabilities
- [ ] Update dependencies to latest secure versions

### Deployment Steps
- [ ] Deploy database migrations first
- [ ] Deploy backend
- [ ] Verify backend health endpoint
- [ ] Deploy frontend
- [ ] Verify frontend can reach backend
- [ ] Test login functionality
- [ ] Test user management
- [ ] Verify role-based access control

### Post-deployment
- [ ] Monitor application logs
- [ ] Monitor error rates
- [ ] Monitor response times
- [ ] Verify database connections
- [ ] Test all critical paths
- [ ] Verify backup systems
- [ ] Document deployment process

## 📊 Monitoring

### Application Monitoring
- [ ] Set up application performance monitoring (APM)
- [ ] Configure error alerting
- [ ] Monitor API response times
- [ ] Track user sessions
- [ ] Monitor JWT token generation/validation
- [ ] Set up uptime monitoring

### Infrastructure Monitoring
- [ ] Monitor CPU usage
- [ ] Monitor memory usage
- [ ] Monitor disk space
- [ ] Monitor network traffic
- [ ] Set up alerts for resource thresholds
- [ ] Monitor database performance

### Security Monitoring
- [ ] Monitor failed login attempts
- [ ] Track suspicious activities
- [ ] Set up intrusion detection
- [ ] Monitor for SQL injection attempts
- [ ] Track API abuse
- [ ] Monitor for DDoS attacks

## 🔄 Maintenance

### Regular Tasks
- [ ] Review and rotate secrets quarterly
- [ ] Update dependencies monthly
- [ ] Review security logs weekly
- [ ] Check backup integrity weekly
- [ ] Test disaster recovery monthly
- [ ] Review access logs monthly

### Documentation
- [ ] Update API documentation
- [ ] Document deployment process
- [ ] Document rollback procedure
- [ ] Create runbook for common issues
- [ ] Document environment setup
- [ ] Create user guide

## 🧪 Testing

### Pre-production Testing
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] End-to-end tests pass
- [ ] Security tests pass
- [ ] Performance tests pass
- [ ] Load tests pass

### Test Scenarios
- [ ] Login with valid credentials
- [ ] Login with invalid credentials
- [ ] Token expiration handling
- [ ] Create user (admin)
- [ ] Edit user (admin)
- [ ] Delete user (admin)
- [ ] Access user management as non-admin (should fail)
- [ ] Activate/deactivate user
- [ ] Access protected routes without token (should fail)

## 📋 Compliance

### Code Quality
- [ ] Code follows SOLID principles
- [ ] No hardcoded credentials
- [ ] All public methods have Javadoc
- [ ] Error handling is comprehensive
- [ ] Input validation is in place
- [ ] No SQL injection vulnerabilities
- [ ] No XSS vulnerabilities
- [ ] CSRF protection enabled

### Data Protection
- [ ] Passwords are encrypted
- [ ] Sensitive data is not logged
- [ ] User data is properly protected
- [ ] GDPR compliance (if applicable)
- [ ] Data retention policy implemented
- [ ] User consent mechanisms (if needed)

## 🆘 Disaster Recovery

### Backup Strategy
- [ ] Database backups configured
- [ ] Backup frequency defined
- [ ] Backup retention policy set
- [ ] Backups stored in multiple locations
- [ ] Backup restoration tested
- [ ] Documentation for restore process

### Rollback Plan
- [ ] Rollback procedure documented
- [ ] Previous version available
- [ ] Database migration rollback scripts
- [ ] Rollback tested in staging
- [ ] Communication plan for rollback

## ✅ Final Checks

Before going live:
- [ ] All items above completed
- [ ] Stakeholders informed
- [ ] Support team trained
- [ ] Documentation updated
- [ ] Monitoring dashboards configured
- [ ] Alert channels configured
- [ ] Incident response plan ready
- [ ] Change default admin password!

---

**Last Review Date:** _________________

**Reviewed By:** _________________

**Deployment Date:** _________________

**Deployed By:** _________________
