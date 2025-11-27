# Sri Lanka District Map Implementation Guide

## Current Status
The user requested district-level visualization (25 districts) instead of the inaccurate province-level map. An accurate SVG implementation exists at `backend/src/main/resources/static/srilankan-map-svg-master/`.

## Changes Required

### 1. Backend Changes - District-Level Data API

#### Update `DashboardController.java`
Add new endpoint for district-level data:
```java
@GetMapping("/api/dashboard/users/district-distribution")
public ResponseEntity<List<DistrictUserDistributionDTO>> getDistrictDistribution(
        @RequestParam(required = false) User.Role role) {
    // Implementation needed
}
```

#### Create `DistrictUserDistributionDTO.java`
```java
package com.adrs.dto;

public class DistrictUserDistributionDTO {
    private String district;  // District enum name (COLOMBO, GAMPAHA, etc.)
    private String displayName; // User-friendly name
    private Long userCount;
    
    // Constructors, getters, setters
}
```

#### Update `UserRepository.java`
Add query methods:
```java
@Query("SELECT COUNT(u) FROM User u WHERE u.district = :district")
Long countUsersByDistrict(@Param("district") District district);

@Query("SELECT COUNT(u) FROM User u WHERE u.district = :district AND u.role = :role")
Long countUsersByDistrictAndRole(@Param("district") District district, @Param("role") User.Role role);

List<User> findByDistrictAndRole(District district, User.Role role);
List<User> findByDistrict(District district);
```

#### Update `DashboardService.java` and `DashboardServiceImpl.java`
```java
// Service interface
List<DistrictUserDistributionDTO> getUserDistributionByDistrict(User.Role role);

// Implementation
public List<DistrictUserDistributionDTO> getUserDistributionByDistrict(User.Role role) {
    List<DistrictUserDistributionDTO> distribution = new ArrayList<>();
    
    for (District district : District.values()) {
        Long count = (role == null) 
            ? userRepository.countUsersByDistrict(district)
            : userRepository.countUsersByDistrictAndRole(district, role);
            
        distribution.add(new DistrictUserDistributionDTO(
            district.name(),
            district.getDisplayName(),
            count
        ));
    }
    
    return distribution.stream()
        .sorted(Comparator.comparing(DistrictUserDistributionDTO::getDisplayName))
        .collect(Collectors.toList());
}
```

### 2. Frontend Changes

#### Add Raphael.js CDN to `dashboard.html`
Add before closing `</body>` tag (before other scripts):
```html
<!-- Raphael.js for SVG rendering -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/raphael/2.3.0/raphael.min.js"></script>
```

#### Complete `sri-lanka-map.js` with all 25 districts
The file at `backend/src/main/resources/static/js/sri-lanka-map-partial.js` has the structure. Add remaining districts to `DISTRICT_MAPPING` object from `backend/src/main/resources/static/srilankan-map-svg-master/script.js`.

All 25 districts with paths:
1. COLOMBO - Colombo ✅ (already added)
2. GAMPAHA - Gampaha ✅ (already added)
3. KALUTARA - Kaḷutara ✅ (already added)
4. MAHANUVARA - Mahanuvara (Kandy)
5. MATALE - Matale
6. NUWARA_ELIYA - Nuwara Eliya
7. GALLE - Galle
8. MATARA - Matara
9. HAMBANTOTA - Hambantota
10. JAFFNA - Jaffna
11. KILINOCHCHI - Kilinochchi
12. MANNAR - Mannar
13. VAVUNIYA - Vavuniya
14. MULLAITIVU - Mullaitivu
15. BATTICALOA - Batticaloa
16. AMPARA - Ampara
17. TRINCOMALEE - Trincomalee
18. KURUNEGALA - Kurunegala
19. PUTTALAM - Puttalam
20. ANURADHAPURA - Anuradhapura
21. POLONNARUWA - Polonnaruwa
22. BADULLA - Badulla
23. MONERAGALA - Moneragala
24. RATNAPURA - Ratnapura
25. KEGALLE - Kegalle

Extract from script.js (lines 5-89) - each `var districtName = rsr.path("...")` statement contains the SVG path.

#### Update `dashboard.js` 
Change API endpoint call from province to district:
```javascript
// OLD
fetch('/api/dashboard/users/province-distribution?role=' + role)

// NEW
fetch('/api/dashboard/users/district-distribution?role=' + role)
```

Update modal click handler event name:
```javascript
// OLD
document.addEventListener('provinceClicked', function(e) {

// NEW  
document.addEventListener('districtClicked', function(e) {
```

Update modal fetch URL:
```javascript
// OLD
fetch(`/api/dashboard/users/by-province?province=${provinceKey}&role=${role}`)

// NEW
fetch(`/api/dashboard/users/by-district?district=${districtKey}&role=${role}`)
```

### 3. Files to Modify

1. **Backend Java Files**:
   - `DashboardController.java` - Add district endpoint
   - `DashboardService.java` - Add district method signature
   - `DashboardServiceImpl.java` - Implement district aggregation
   - `UserRepository.java` - Add district query methods
   - Create `DistrictUserDistributionDTO.java`

2. **Frontend JavaScript Files**:
   - `sri-lanka-map.js` - Replace with complete 25-district version
   - `dashboard.js` - Update API endpoints and event handlers

3. **Frontend HTML Files**:
   - `dashboard.html` - Add Raphael.js CDN script tag

### 4. Testing Checklist

- [ ] Backend compiles without errors
- [ ] District endpoint returns correct data structure
- [ ] Map initializes with Raphael.js
- [ ] All 25 districts render correctly
- [ ] Hover shows district name and count
- [ ] Click opens modal with user list
- [ ] Role filter (All/Admin/Vet) updates colors
- [ ] Colors reflect user counts (gray=0, light blue=1-5, medium blue=6-15, dark blue=16+)
- [ ] Tooltip positioning works correctly

### 5. Complete DISTRICT_MAPPING Object Structure

Copy this template and fill remaining districts from script.js:
```javascript
const DISTRICT_MAPPING = {
    'COLOMBO': { name: 'Colombo', path: "..." },
    'GAMPAHA': { name: 'Gampaha', path: "..." },
    'KALUTARA': { name: 'Kaḷutara', path: "..." },
    'MAHANUVARA': { name: 'Mahanuvara', path: "..." },
    'MATALE': { name: 'Matale', path: "..." },
    'NUWARA_ELIYA': { name: 'Nuwara Eliya', path: "..." },
    'GALLE': { name: 'Galle', path: "..." },
    'MATARA': { name: 'Matara', path: "..." },
    'HAMBANTOTA': { name: 'Hambantota', path: "..." },
    'JAFFNA': { name: 'Jaffna', path: "..." },
    'KILINOCHCHI': { name: 'Kilinochchi', path: "..." },
    'MANNAR': { name: 'Mannar', path: "..." },
    'VAVUNIYA': { name: 'Vavuniya', path: "..." },
    'MULLAITIVU': { name: 'Mullaitivu', path: "..." },
    'BATTICALOA': { name: 'Batticaloa', path: "..." },
    'AMPARA': { name: 'Ampara', path: "..." },
    'TRINCOMALEE': { name: 'Trincomalee', path: "..." },
    'KURUNEGALA': { name: 'Kurunegala', path: "..." },
    'PUTTALAM': { name: 'Puttalam', path: "..." },
    'ANURADHAPURA': { name: 'Anuradhapura', path: "..." },
    'POLONNARUWA': { name: 'Polonnaruwa', path: "..." },
    'BADULLA': { name: 'Badulla', path: "..." },
    'MONERAGALA': { name: 'Moneragala', path: "..." },
    'RATNAPURA': { name: 'Ratnapura', path: "..." },
    'KEGALLE': { name: 'Kegalle', path: "..." }
};
```

### 6. Key Files Locations

- Original accurate map: `backend/src/main/resources/static/srilankan-map-svg-master/script.js`
- Current (inaccurate) map: `backend/src/main/resources/static/js/sri-lanka-map.js`
- Backup created: `backend/src/main/resources/static/js/sri-lanka-map-backup.js`
- Partial new version: `backend/src/main/resources/static/js/sri-lanka-map-partial.js`

### 7. Backend Data Flow

```
User clicks district → 
districtClicked event → 
dashboard.js fetches /api/dashboard/users/by-district?district=COLOMBO&role=ADMIN →
DashboardController.getUsersByDistrict() →
UserService.getUsersByDistrictAndRole() →
UserRepository.findByDistrictAndRole() →
Returns List<User> →
Modal displays users
```

### 8. Next Steps

1. Complete all 22 remaining district paths in DISTRICT_MAPPING
2. Create DistrictUserDistributionDTO.java
3. Add repository query methods
4. Implement service layer methods
5. Add controller endpoints
6. Update dashboard.js API calls
7. Add Raphael.js CDN to dashboard.html
8. Replace sri-lanka-map.js with complete version
9. Test end-to-end functionality

---

## Important Notes

- **District enum names** must match exactly between frontend and backend (e.g., 'NUWARA_ELIYA' not 'NUWARA ELIYA')
- **SVG paths are long strings** - copy exactly from script.js without modification
- **Raphael.js version** - Use 2.3.0 as tested in original implementation
- **Color scale thresholds** - Adjust if needed based on actual user distribution
- **Tooltip positioning** - May need refinement based on container layout

## Files Already Created

1. `sri-lanka-map-partial.js` - Structure complete, needs 22 more districts added
2. This implementation guide

## Files Backed Up

1. `sri-lanka-map-backup.js` - Original inaccurate version (province-level)
