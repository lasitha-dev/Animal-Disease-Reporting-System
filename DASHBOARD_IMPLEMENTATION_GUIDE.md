# Dashboard Implementation Guide

## Problem Summary

The dashboard was not displaying seeded data (animal types, farm types, and diseases) because:

1. **Database was empty** - The seed scripts hadn't been executed
2. **Seed scripts were incomplete** - Missing UUID generation and timestamps required by JPA entities
3. **Service method issue** - `getSummaryCounts()` was returning total counts instead of active counts

## Issues Fixed

### 1. Updated Seed Scripts

All seed scripts were updated to include:
- UUID generation using `gen_random_uuid()`
- Timestamp fields (`created_at`) using `NOW()`
- Proper column specification in INSERT statements

**Files Updated:**
- `database/seed/01_animal_types.sql` - 8 animal types
- `database/seed/05_farm_types.sql` - 12 farm types  
- `database/seed/02_diseases.sql` - 22 diseases

### 2. Fixed DashboardServiceImpl

Updated `getSummaryCounts()` method to return active counts for configuration items:

```java
counts.put("activeFarmTypes", farmTypeRepository.countByIsActiveTrue());
counts.put("activeAnimalTypes", animalTypeRepository.countByIsActiveTrue());
counts.put("activeDiseases", diseaseRepository.countByIsActiveTrue());
counts.put("notifiableDiseases", diseaseRepository.countByIsNotifiableTrue());
```

### 3. Database Population

Successfully loaded seed data:
- ✅ 8 Animal Types
- ✅ 13 Farm Types (12 new + 1 existing)
- ✅ 22 Diseases

## How the Dashboard Works

### Architecture Flow

```
Browser (dashboard.html)
    ↓
JavaScript (dashboard-charts.js)
    ↓ AJAX Call: GET /api/dashboard/summary
DashboardController
    ↓
DashboardService.getSummaryCounts()
    ↓
Repositories (count queries)
    ↓
PostgreSQL Database
```

### Key Endpoints

1. **Dashboard Statistics**
   - Endpoint: `GET /api/dashboard/summary`
   - Returns: Map with counts (totalFarms, totalAnimals, activeFarmTypes, etc.)
   - Used by: Statistics cards on dashboard

2. **Chart Data**
   - `GET /api/dashboard/charts/farm-types` - Farm type distribution
   - `GET /api/dashboard/charts/disease-severity` - Disease severity breakdown
   - `GET /api/dashboard/charts/user-roles` - User role distribution (admin only)
   - Used by: Chart.js visualizations

3. **Configuration Management**
   - `GET /api/configuration/farm-types` - List all farm types
   - `GET /api/configuration/animal-types` - List all animal types
   - `GET /api/configuration/diseases` - List all diseases
   - Used by: Configuration screen tables

### Frontend Components

**Dashboard Page** (`templates/dashboard/dashboard.html`)
- Statistics cards showing counts
- Chart containers for visualizations
- Role-based visibility using `sec:authorize`

**JavaScript** (`static/js/dashboard-charts.js`)
- Fetches statistics via AJAX
- Initializes Chart.js charts
- Animates number updates
- Handles CSRF tokens for secure requests

**Configuration Page** (`templates/configuration/configuration.html`)
- Tabbed interface for farm types, animal types, diseases
- CRUD operations for each configuration type
- Real-time data loading via AJAX

## Verification Steps

### 1. Check Database Data

```powershell
# Animal Types
psql -U postgres -d adrsdb -c "SELECT type_name, is_active FROM animal_types ORDER BY type_name;"

# Farm Types
psql -U postgres -d adrsdb -c "SELECT type_name, is_active FROM farm_types ORDER BY type_name;"

# Diseases
psql -U postgres -d adrsdb -c "SELECT disease_name, severity, is_notifiable FROM diseases ORDER BY disease_name;"
```

### 2. Test API Endpoints

```powershell
# Using curl (after logging in)
curl http://localhost:8081/api/dashboard/summary

# Using browser dev tools
# 1. Log in as admin (username: admin, password: admin123)
# 2. Navigate to dashboard
# 3. Open browser console (F12)
# 4. Check Network tab for API calls
```

### 3. Verify Dashboard Display

1. **Login**: http://localhost:8081/login
   - Username: `admin`
   - Password: `admin123`

2. **Dashboard**: http://localhost:8081/dashboard
   - Should show counts in statistics cards
   - Should display charts with data

3. **Configuration**: http://localhost:8081/configuration
   - Farm Types tab: Should list 13 farm types
   - Animal Types tab: Should list 8 animal types
   - Diseases tab: Should list 22 diseases

## Common Issues & Solutions

### Issue: Dashboard Shows Zero Counts

**Cause**: Seed data not loaded or service returning wrong data

**Solution**:
```powershell
# Check database
psql -U postgres -d adrsdb -c "SELECT COUNT(*) FROM animal_types;"

# If zero, reload seeds
psql -U postgres -d adrsdb -f "database/seed/01_animal_types.sql"
psql -U postgres -d adrsdb -f "database/seed/05_farm_types.sql"
psql -U postgres -d adrsdb -f "database/seed/02_diseases.sql"
```

### Issue: Charts Not Displaying

**Cause**: Chart.js not loaded or API returning empty data

**Solution**:
1. Check browser console for JavaScript errors
2. Verify Chart.js CDN is accessible
3. Check API response in Network tab
4. Ensure `DashboardService` methods return proper data

### Issue: Configuration Page Empty Tables

**Cause**: AJAX calls failing or CSRF token missing

**Solution**:
1. Check browser console for 403 errors (CSRF)
2. Verify CSRF meta tags in HTML head
3. Check `configuration.js` includes CSRF token in requests
4. Verify user has ADMIN role

## Database Schema

### Key Tables

```sql
-- Animal Types
CREATE TABLE animal_types (
    id UUID PRIMARY KEY,
    type_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Farm Types
CREATE TABLE farm_types (
    id UUID PRIMARY KEY,
    type_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

-- Diseases
CREATE TABLE diseases (
    id UUID PRIMARY KEY,
    disease_name VARCHAR(100) UNIQUE NOT NULL,
    disease_code VARCHAR(20) UNIQUE NOT NULL,
    description TEXT,
    severity VARCHAR(20) NOT NULL,
    is_notifiable BOOLEAN NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

## Files Modified

1. **Seed Scripts**
   - `database/seed/01_animal_types.sql`
   - `database/seed/05_farm_types.sql`
   - `database/seed/02_diseases.sql`

2. **Service Implementation**
   - `backend/src/main/java/com/adrs/service/impl/DashboardServiceImpl.java`

## Next Steps

### To Add More Data

1. **Add Animal Type**:
   ```sql
   INSERT INTO animal_types (id, type_name, description, is_active, created_at) 
   VALUES (gen_random_uuid(), 'Deer', 'Deer farming', TRUE, NOW());
   ```

2. **Add Farm Type**:
   ```sql
   INSERT INTO farm_types (id, type_name, description, is_active, created_at) 
   VALUES (gen_random_uuid(), 'Organic', 'Certified organic farming', TRUE, NOW());
   ```

3. **Add Disease**:
   ```sql
   INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, created_at) 
   VALUES (gen_random_uuid(), 'Test Disease', 'TEST-001', 'Test description', 'LOW', FALSE, TRUE, NOW());
   ```

### To Implement Farm Type Distribution Chart

Currently, the `getFarmTypeDistribution()` method in `DashboardServiceImpl` returns empty data. To implement:

1. Create a custom query in `FarmRepository`:
   ```java
   @Query("SELECT f.farmType.typeName, COUNT(f) FROM Farm f GROUP BY f.farmType.typeName")
   List<Object[]> countFarmsByType();
   ```

2. Update `DashboardServiceImpl.getFarmTypeDistribution()`:
   ```java
   List<Object[]> results = farmRepository.countFarmsByType();
   List<String> labels = new ArrayList<>();
   List<Long> data = new ArrayList<>();
   
   for (Object[] result : results) {
       labels.add((String) result[0]);
       data.add((Long) result[1]);
   }
   
   return new ChartDataDTO(labels, data, "pie");
   ```

## Security Notes

- All configuration endpoints require `ADMIN` role
- Dashboard endpoints require authentication
- CSRF protection enabled on all POST/PUT/DELETE requests
- Session-based authentication (not JWT)

## Testing

```powershell
# Run tests
cd backend
mvn test

# Run specific test
mvn test -Dtest=DashboardServiceTest
```

## Monitoring

Check application logs:
```powershell
# View logs
Get-Content backend/logs/application.log -Tail 50 -Wait

# Search for dashboard access
Select-String -Path backend/logs/application.log -Pattern "Dashboard"
```

## API Documentation

Access Swagger UI at: http://localhost:8081/swagger-ui.html

Key API sections:
- **Dashboard Analytics** - Statistics and charts
- **Configuration Management** - CRUD for farm types, animal types, diseases

---

## Summary

The dashboard now correctly displays:
- ✅ 8 Active Animal Types
- ✅ 13 Active Farm Types
- ✅ 22 Active Diseases
- ✅ 11 Notifiable Diseases
- ✅ Disease severity distribution chart
- ✅ Configuration items in management screen

All seed data is properly loaded and accessible via both the dashboard and configuration screens.
