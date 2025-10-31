# User Management Province and District Feature - Implementation Summary

## Overview
This document describes the implementation of province and district dropdown fields in the user management CRUD form with cascading selection functionality.

## Changes Made

### 1. Backend Model Layer

#### Province Enum (`Province.java`)
- Created new enum with all 9 provinces of Sri Lanka
- Each province has a display name for UI presentation
- Enum values: NORTHERN, NORTH_WESTERN, WESTERN, NORTH_CENTRAL, CENTRAL, SABARAGAMUWA, EASTERN, UVA, SOUTHERN

#### District Enum (`District.java`)
- Created new enum with all 25 districts of Sri Lanka
- Each district is mapped to its parent province
- Provides static method `getDistrictsByProvince(Province)` for cascading dropdown functionality
- Districts include: Jaffna, Kilinochchi, Mannar, Mullaitivu, Vavuniya (Northern); Puttalam, Kurunegala (North-Western); Gampaha, Colombo, Kalutara (Western); Anuradhapura, Polonnaruwa (North-Central); Matale, Kandy, Nuwara Eliya (Central); Kegalle, Ratnapura (Sabaragamuwa); Trincomalee, Batticaloa, Ampara (Eastern); Badulla, Monaragala (Uva); Hambantota, Matara, Galle (Southern)

#### User Entity Updates (`User.java`)
- Added `province` field (Province enum, nullable)
- Added `district` field (District enum, nullable)
- Both fields stored as VARCHAR(30) in database using EnumType.STRING

### 2. Data Transfer Objects

#### UserRequest DTO (`UserRequest.java`)
- Added `province` field (Province type, optional)
- Added `district` field (District type, optional)
- No validation constraints as these fields are optional

#### UserResponse DTO (`UserResponse.java`)
- Added `province` field (String type, contains enum name)
- Added `district` field (String type, contains enum name)
- Updated `fromUser()` static method to convert enum values to strings

### 3. Service Layer

#### UserServiceImpl Updates (`UserServiceImpl.java`)
- Updated `createUser()` method to set province and district from request
- Updated `updateUser()` method to update province and district fields

### 4. REST API Layer

#### LocationController (`LocationController.java`)
- New REST controller at `/api/locations`
- **GET /api/locations/provinces** - Returns all provinces with value and label
- **GET /api/locations/districts?provinceName={province}** - Returns districts for a specific province
- **GET /api/locations/districts/all** - Returns all districts with province mapping
- All endpoints return JSON with consistent structure: `{value: "ENUM_NAME", label: "Display Name"}`

### 5. Frontend Layer

#### HTML Template Updates (`user-management.html`)
- Added province dropdown in Create User modal
- Added district dropdown in Create User modal (disabled until province is selected)
- Added province dropdown in Edit User modal
- Added district dropdown in Edit User modal (disabled until province is selected)
- Updated edit button data attributes to include `data-province` and `data-district`

#### JavaScript Updates (`user-management.js`)
- Added `loadProvinces(selectId)` function to fetch and populate province dropdowns
- Added `loadDistricts(provinceName, selectId)` function for cascading district dropdown
- Updated `openEditModal()` to accept and handle province and district parameters
- Added event listeners for province change to trigger district loading
- District dropdown is automatically enabled/disabled based on province selection
- Includes CSRF token in all API requests for security

### 6. Database Migration

#### Migration Script (`002_add_province_district_to_users.sql`)
- Adds `province` column (VARCHAR(30), nullable)
- Adds `district` column (VARCHAR(30), nullable)
- Includes column comments for documentation

### 7. Test Updates

#### UserServiceTest Updates
- Added Province and District imports
- Updated test user setup to include Western province and Colombo district
- Updated user request setup with test province and district

#### UserRepositoryTest Updates
- Added Province and District imports
- Updated test user setup to include Western province and Colombo district

#### UserControllerTest Updates
- Added Province and District imports
- Updated user request and response setup with test province and district values

## Usage

### Creating a User with Location
1. Click "Create User" button
2. Fill in required fields (username, email, password, etc.)
3. Select a province from the province dropdown
4. Once province is selected, district dropdown becomes enabled
5. Select a district from the filtered list
6. Submit the form

### Editing a User's Location
1. Click "Edit" button on a user row
2. Modal opens with existing values pre-populated
3. If user has a province, districts are automatically loaded
4. Change province to see different districts
5. Update and submit

### Cascading Dropdown Behavior
- District dropdown is initially disabled
- When province is selected, district dropdown populates with relevant districts only
- If province is changed, district dropdown resets and repopulates
- District selection is cleared when province changes

## API Endpoints

### Get All Provinces
```
GET /api/locations/provinces
Response: [
  {"value": "WESTERN", "label": "Western Province"},
  {"value": "CENTRAL", "label": "Central Province"},
  ...
]
```

### Get Districts by Province
```
GET /api/locations/districts?provinceName=WESTERN
Response: [
  {"value": "COLOMBO", "label": "Colombo"},
  {"value": "GAMPAHA", "label": "Gampaha"},
  {"value": "KALUTARA", "label": "Kalutara"}
]
```

## Database Schema Changes

```sql
ALTER TABLE users 
ADD COLUMN province VARCHAR(30);

ALTER TABLE users 
ADD COLUMN district VARCHAR(30);
```

To apply the migration:
```powershell
psql -U postgres -d adrsdb -f database/migrations/002_add_province_district_to_users.sql
```

## Testing

All existing tests have been updated to include province and district data:
- `UserServiceTest` - Tests service layer with location data
- `UserRepositoryTest` - Tests repository operations with location fields
- `UserControllerTest` - Tests API endpoints with location parameters

Run tests:
```powershell
cd backend
mvn test
```

## Code Quality

All code follows project standards:
- ✅ SOLID principles applied
- ✅ No hardcoded strings (constants used where appropriate)
- ✅ Proper Javadoc comments on all public methods
- ✅ Enums stored separately for reusability
- ✅ Cascading logic handled client-side with REST API support
- ✅ CSRF protection maintained in JavaScript
- ✅ Proper error handling in API endpoints
- ✅ Tests updated to maintain coverage

## Security Considerations

- CSRF tokens are included in all JavaScript API calls
- Province and district values are validated server-side via enum types
- Invalid province names return 400 Bad Request
- Location fields are optional (nullable)
- No sensitive data in location enums

## Future Enhancements

Potential improvements:
1. Add province/district-based filtering in user list
2. Show location in user table view
3. Add location-based analytics to dashboard
4. Create location-based reports
5. Add multi-language support for province/district names
