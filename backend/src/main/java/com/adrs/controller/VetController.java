package com.adrs.controller;

import com.adrs.dto.*;
import com.adrs.model.DiseaseReport;
import com.adrs.model.Province;
import com.adrs.model.User;
import com.adrs.repository.DiseaseReportRepository;
import com.adrs.repository.FarmAnimalRepository;
import com.adrs.repository.FarmRepository;
import com.adrs.repository.UserRepository;
import com.adrs.service.AnalyticsService;
import com.adrs.service.AnimalTypeService;
import com.adrs.service.DiseaseReportService;
import com.adrs.service.DiseaseService;
import com.adrs.service.FarmService;
import com.adrs.service.FarmTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * REST Controller for veterinary officer operations.
 * Provides endpoints for farm registration and management.
 */
@Tag(name = "Veterinary Operations", description = "APIs for veterinary officers to manage farms and animals")
@RestController
@RequestMapping("/api/vet")
@PreAuthorize("hasRole('VETERINARY_OFFICER')")
@SecurityRequirement(name = "session-auth")
public class VetController {

    private static final Logger logger = LoggerFactory.getLogger(VetController.class);

    private final FarmService farmService;
    private final FarmTypeService farmTypeService;
    private final AnimalTypeService animalTypeService;
    private final DiseaseService diseaseService;
    private final DiseaseReportService diseaseReportService;
    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;
    private final FarmRepository farmRepository;
    private final FarmAnimalRepository farmAnimalRepository;
    private final DiseaseReportRepository diseaseReportRepository;

    public VetController(FarmService farmService,
                        FarmTypeService farmTypeService,
                        AnimalTypeService animalTypeService,
                        DiseaseService diseaseService,
                        DiseaseReportService diseaseReportService,
                        AnalyticsService analyticsService,
                        UserRepository userRepository,
                        FarmRepository farmRepository,
                        FarmAnimalRepository farmAnimalRepository,
                        DiseaseReportRepository diseaseReportRepository) {
        this.farmService = farmService;
        this.farmTypeService = farmTypeService;
        this.animalTypeService = animalTypeService;
        this.diseaseService = diseaseService;
        this.diseaseReportService = diseaseReportService;
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
        this.farmRepository = farmRepository;
        this.farmAnimalRepository = farmAnimalRepository;
        this.diseaseReportRepository = diseaseReportRepository;
    }

    // ========================================
    // FARM ENDPOINTS
    // ========================================

    /**
     * Create a new farm.
     *
     * @param request the farm request data
     * @param userDetails the authenticated user
     * @return the created farm
     */
    @Operation(summary = "Register a new farm", description = "Creates a new farm with the provided details")
    @PostMapping("/farms")
    public ResponseEntity<FarmResponseDTO> createFarm(
            @Valid @RequestBody FarmRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("POST /api/vet/farms - Creating farm: {} by user: {}", 
                    request.getFarmName(), userDetails.getUsername());
        
        User currentUser = getCurrentUser(userDetails);
        FarmResponseDTO response = farmService.createFarm(request, currentUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all farms created by the current vet.
     *
     * @param userDetails the authenticated user
     * @return list of farms
     */
    @Operation(summary = "Get my farms", description = "Retrieves all farms created by the current veterinary officer")
    @GetMapping("/farms")
    public ResponseEntity<List<FarmResponseDTO>> getMyFarms(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("GET /api/vet/farms - Fetching farms for user: {}", userDetails.getUsername());
        
        User currentUser = getCurrentUser(userDetails);
        List<FarmResponseDTO> farms = farmService.getFarmsByCreatedBy(currentUser);
        
        return ResponseEntity.ok(farms);
    }

    /**
     * Get all farms created by other vets (not the current user).
     *
     * @param userDetails the authenticated user
     * @return list of farms
     */
    @Operation(summary = "Get other vets' farms", description = "Retrieves all farms created by other veterinary officers")
    @GetMapping("/farms/others")
    public ResponseEntity<List<FarmResponseDTO>> getOtherVetsFarms(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("GET /api/vet/farms/others - Fetching other vets' farms for user: {}", userDetails.getUsername());
        
        User currentUser = getCurrentUser(userDetails);
        List<FarmResponseDTO> farms = farmService.getFarmsNotCreatedBy(currentUser);
        
        return ResponseEntity.ok(farms);
    }

    /**
     * Get a specific farm by ID.
     *
     * @param id the farm ID
     * @return the farm details
     */
    @Operation(summary = "Get farm by ID", description = "Retrieves a specific farm by its ID")
    @GetMapping("/farms/{id}")
    public ResponseEntity<FarmResponseDTO> getFarmById(@PathVariable UUID id) {
        logger.info("GET /api/vet/farms/{} - Fetching farm details", id);
        
        FarmResponseDTO response = farmService.getFarmById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing farm.
     *
     * @param id the farm ID
     * @param request the farm request data
     * @param userDetails the authenticated user
     * @return the updated farm
     */
    @Operation(summary = "Update farm", description = "Updates an existing farm")
    @PutMapping("/farms/{id}")
    public ResponseEntity<FarmResponseDTO> updateFarm(
            @PathVariable UUID id,
            @Valid @RequestBody FarmRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("PUT /api/vet/farms/{} - Updating farm by user: {}", id, userDetails.getUsername());
        logger.info("Request data: farmName={}, farmTypeId={}, province={}, district={}", 
                    request.getFarmName(), request.getFarmTypeId(), request.getProvince(), request.getDistrict());
        
        try {
            User currentUser = getCurrentUser(userDetails);
            FarmResponseDTO response = farmService.updateFarm(id, request, currentUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating farm: {} - {}", e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Update animal tags for a farm.
     *
     * @param id the farm ID
     * @param animalTags the animal tags
     * @param userDetails the authenticated user
     * @return the updated farm
     */
    @Operation(summary = "Update animal tags", description = "Updates the animal types and counts for a farm")
    @PutMapping("/farms/{id}/animals")
    public ResponseEntity<FarmResponseDTO> updateAnimalTags(
            @PathVariable UUID id,
            @RequestBody List<FarmRequestDTO.AnimalTagDTO> animalTags,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("PUT /api/vet/farms/{}/animals - Updating animal tags by user: {}", 
                    id, userDetails.getUsername());
        
        User currentUser = getCurrentUser(userDetails);
        FarmResponseDTO response = farmService.updateAnimalTags(id, animalTags, currentUser);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a farm (soft delete).
     *
     * @param id the farm ID
     * @param userDetails the authenticated user
     * @return no content
     */
    @Operation(summary = "Delete farm", description = "Soft deletes a farm")
    @DeleteMapping("/farms/{id}")
    public ResponseEntity<Void> deleteFarm(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("DELETE /api/vet/farms/{} - Deleting farm by user: {}", id, userDetails.getUsername());
        
        User currentUser = getCurrentUser(userDetails);
        farmService.deleteFarm(id, currentUser);
        
        return ResponseEntity.noContent().build();
    }

    // ========================================
    // LOOKUP DATA ENDPOINTS
    // ========================================

    /**
     * Get all active farm types for dropdown selection.
     *
     * @return list of active farm types
     */
    @Operation(summary = "Get active farm types", description = "Retrieves all active farm types for selection")
    @GetMapping("/farm-types")
    public ResponseEntity<List<FarmTypeDTO>> getActiveFarmTypes() {
        logger.info("GET /api/vet/farm-types - Fetching active farm types");
        
        List<FarmTypeDTO> farmTypes = farmTypeService.getActiveFarmTypes();
        return ResponseEntity.ok(farmTypes);
    }

    /**
     * Get all active animal types for tag selection.
     *
     * @return list of active animal types
     */
    @Operation(summary = "Get active animal types", description = "Retrieves all active animal types for tag selection")
    @GetMapping("/animal-types")
    public ResponseEntity<List<AnimalTypeDTO>> getActiveAnimalTypes() {
        logger.info("GET /api/vet/animal-types - Fetching active animal types");
        
        List<AnimalTypeDTO> animalTypes = animalTypeService.getActiveAnimalTypes();
        return ResponseEntity.ok(animalTypes);
    }

    /**
     * Get vet dashboard statistics.
     *
     * @param userDetails the authenticated user
     * @return dashboard stats
     */
    @Operation(summary = "Get vet dashboard stats", description = "Retrieves statistics for the vet dashboard")
    @GetMapping("/stats")
    public ResponseEntity<VetStatsDTO> getVetStats(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("GET /api/vet/stats - Fetching stats for user: {}", userDetails.getUsername());
        
        // Fire all independent queries in parallel to reduce latency with remote DB
        CompletableFuture<Long> farmCountFuture = CompletableFuture.supplyAsync(farmRepository::count);
        CompletableFuture<Long> animalsCountFuture = CompletableFuture.supplyAsync(farmAnimalRepository::sumTotalAnimals);
        CompletableFuture<Long> reportCountFuture = CompletableFuture.supplyAsync(diseaseReportRepository::count);
        CompletableFuture<Long> activeOutbreaksFuture = CompletableFuture.supplyAsync(diseaseReportRepository::countByOutcomeOngoing);
        
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        CompletableFuture<List<Object[]>> highRiskDataFuture = CompletableFuture.supplyAsync(
                () -> diseaseReportRepository.findHighRiskFarmData(thirtyDaysAgo));
        
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now();
        CompletableFuture<Long> farmsInspectedFuture = CompletableFuture.supplyAsync(
                () -> diseaseReportRepository.countDistinctFarmsWithReportsInPeriod(startOfMonth, endOfMonth));
        
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        CompletableFuture<Long> pendingFollowupsFuture = CompletableFuture.supplyAsync(
                () -> diseaseReportRepository.countPendingFollowups(sevenDaysAgo));
        
        // Wait for all queries to complete
        CompletableFuture.allOf(farmCountFuture, animalsCountFuture, reportCountFuture,
                activeOutbreaksFuture, highRiskDataFuture, farmsInspectedFuture, pendingFollowupsFuture).join();
        
        Long farmCount = farmCountFuture.join();
        Long animalsCount = animalsCountFuture.join();
        Long reportCount = reportCountFuture.join();
        Long activeOutbreaks = activeOutbreaksFuture.join();
        List<Object[]> highRiskData = highRiskDataFuture.join();
        Long highRiskFarms = (long) highRiskData.size();
        Long farmsInspectedThisMonth = farmsInspectedFuture.join();
        Long pendingFollowups = pendingFollowupsFuture.join();
        
        VetStatsDTO stats = new VetStatsDTO();
        stats.setFarmsCount(farmCount);
        stats.setAnimalsCount(animalsCount != null ? animalsCount : 0L);
        stats.setReportsCount(reportCount);
        stats.setActiveOutbreaks(activeOutbreaks != null ? activeOutbreaks : 0L);
        stats.setHighRiskFarms(highRiskFarms);
        stats.setFarmsInspectedThisMonth(farmsInspectedThisMonth != null ? farmsInspectedThisMonth : 0L);
        stats.setPendingFollowups(pendingFollowups != null ? pendingFollowups : 0L);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get health trend data for the dashboard chart.
     *
     * @param days number of days to include (default 7)
     * @return health trend data with dates, report counts, and resolved counts
     */
    @Operation(summary = "Get health trend data", description = "Retrieves daily report counts for the health trend chart")
    @GetMapping("/health-trend")
    public ResponseEntity<HealthTrendDTO> getHealthTrend(
            @RequestParam(defaultValue = "7") int days) {
        logger.info("GET /api/vet/health-trend?days={} - Fetching health trend data", days);
        
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        List<Object[]> trendData = diseaseReportRepository.getDailyHealthTrend(startDate);
        
        HealthTrendDTO result = new HealthTrendDTO();
        
        // Initialize with zeros for all days
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            result.getLabels().add(date.getDayOfWeek().toString().substring(0, 3));
            result.getReports().add(0L);
            result.getResolved().add(0L);
        }
        
        // Fill in actual data
        for (Object[] row : trendData) {
            LocalDate reportDate = (LocalDate) row[0];
            Long reportCount = (Long) row[1];
            Long resolvedCount = row[2] != null ? ((Number) row[2]).longValue() : 0L;
            
            int dayIndex = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, reportDate);
            if (dayIndex >= 0 && dayIndex < days) {
                result.getReports().set(dayIndex, reportCount);
                result.getResolved().set(dayIndex, resolvedCount);
            }
        }
        
        return ResponseEntity.ok(result);
    }

    // ========================================
    // DISEASE REPORT ENDPOINTS
    // ========================================

    /**
     * Get active diseases for a specific animal type.
     *
     * @param animalTypeId the animal type ID
     * @return list of diseases
     */
    @Operation(summary = "Get diseases by animal type", description = "Retrieves active diseases for a specific animal type")
    @GetMapping("/diseases")
    public ResponseEntity<List<DiseaseDTO>> getDiseasesByAnimalType(@RequestParam UUID animalTypeId) {
        logger.info("GET /api/vet/diseases?animalTypeId={} - Fetching diseases", animalTypeId);
        
        List<DiseaseDTO> diseases = diseaseService.getDiseasesByAnimalType(animalTypeId);
        return ResponseEntity.ok(diseases);
    }

    /**
     * Create a new disease report.
     *
     * @param request the disease report data
     * @param image optional image file
     * @param userDetails the authenticated user
     * @return the created disease report
     */
    @Operation(summary = "Create disease report", description = "Creates a new disease report with optional image")
    @PostMapping(value = "/disease-reports", consumes = {"multipart/form-data"})
    public ResponseEntity<DiseaseReportResponseDTO> createDiseaseReport(
            @RequestPart("report") @Valid DiseaseReportRequestDTO request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("POST /api/vet/disease-reports - Creating report by user: {}", userDetails.getUsername());
        
        User currentUser = getCurrentUser(userDetails);
        DiseaseReportResponseDTO response = diseaseReportService.createReport(request, image, currentUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all disease reports by the current vet.
     *
     * @param userDetails the authenticated user
     * @return list of disease reports
     */
    @Operation(summary = "Get my disease reports", description = "Retrieves all disease reports by the current vet")
    @GetMapping("/disease-reports")
    public ResponseEntity<List<DiseaseReportResponseDTO>> getMyDiseaseReports(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("GET /api/vet/disease-reports - Fetching reports for user: {}", userDetails.getUsername());
        
        User currentUser = getCurrentUser(userDetails);
        List<DiseaseReportResponseDTO> reports = diseaseReportService.getReportsByVet(currentUser);
        
        return ResponseEntity.ok(reports);
    }

    /**
     * Get all disease reports by other vets (not the current user).
     *
     * @param userDetails the authenticated user
     * @return list of disease reports
     */
    @Operation(summary = "Get other vets' disease reports", description = "Retrieves all disease reports by other vets")
    @GetMapping("/disease-reports/others")
    public ResponseEntity<List<DiseaseReportResponseDTO>> getOtherVetsDiseaseReports(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("GET /api/vet/disease-reports/others - Fetching other vets' reports for user: {}", userDetails.getUsername());
        
        User currentUser = getCurrentUser(userDetails);
        List<DiseaseReportResponseDTO> reports = diseaseReportService.getReportsNotByVet(currentUser);
        
        return ResponseEntity.ok(reports);
    }

    /**
     * Get a specific disease report by ID.
     *
     * @param id the report ID
     * @return the disease report
     */
    @Operation(summary = "Get disease report by ID", description = "Retrieves a specific disease report")
    @GetMapping("/disease-reports/{id}")
    public ResponseEntity<DiseaseReportResponseDTO> getDiseaseReportById(@PathVariable UUID id) {
        logger.info("GET /api/vet/disease-reports/{} - Fetching report", id);
        
        DiseaseReportResponseDTO response = diseaseReportService.getReportById(id);
        return ResponseEntity.ok(response);
    }


    /**
     * Update an existing disease report.
     *
     * @param id the report ID
     * @param request the updated disease report data
     * @param image optional new image file
     * @param userDetails the authenticated user
     * @return the updated disease report
     */
    @Operation(summary = "Update disease report", description = "Updates an existing disease report with optional new image")
    @PutMapping(value = "/disease-reports/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<DiseaseReportResponseDTO> updateDiseaseReport(
            @PathVariable UUID id,
            @RequestPart("report") @Valid DiseaseReportRequestDTO request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("PUT /api/vet/disease-reports/{} - Updating report by user: {}", id, userDetails.getUsername());
        
        User currentUser = getCurrentUser(userDetails);
        DiseaseReportResponseDTO response = diseaseReportService.updateReport(id, request, image, currentUser);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a disease report.
     *
     * @param id the report ID
     * @param userDetails the authenticated user
     * @return no content
     */
    @Operation(summary = "Delete disease report", description = "Deletes a disease report")
    @DeleteMapping("/disease-reports/{id}")
    public ResponseEntity<Void> deleteDiseaseReport(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("DELETE /api/vet/disease-reports/{} - Deleting report by user: {}", id, userDetails.getUsername());
        
        User currentUser = getCurrentUser(userDetails);
        diseaseReportService.deleteReport(id, currentUser);
        
        return ResponseEntity.noContent().build();
    }

    // ========================================
    // MAP ENDPOINTS
    // ========================================

    /**
     * Get disease reports for map display, grouped by farm location.
     *
     * @param animalTypeIds optional filter by animal type IDs
     * @return list of disease data grouped by farm
     */
    @Operation(summary = "Get disease reports for map", description = "Retrieves disease reports grouped by farm for map display")
    @GetMapping("/disease-reports/map")
    public ResponseEntity<List<DiseaseMapDTO>> getDiseaseReportsForMap(
            @RequestParam(required = false) List<UUID> animalTypeIds,
            @RequestParam(required = false) List<UUID> diseaseIds,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String outcome) {
        
        logger.info("GET /api/vet/disease-reports/map - Fetching reports for map, animalTypeIds: {}, diseaseIds: {}, province: {}, outcome: {}", animalTypeIds, diseaseIds, province, outcome);
        
        Province provinceEnum = null;
        if (province != null && !province.isEmpty()) {
            try {
                provinceEnum = Province.valueOf(province);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid province value: {}", province);
            }
        }

        DiseaseReport.Outcome outcomeEnum = null;
        if (outcome != null && !outcome.isEmpty()) {
            try {
                outcomeEnum = DiseaseReport.Outcome.valueOf(outcome);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid outcome value: {}", outcome);
            }
        }
        
        List<DiseaseMapDTO> reports = diseaseReportService.getReportsForMap(animalTypeIds, diseaseIds, provinceEnum, outcomeEnum);
        return ResponseEntity.ok(reports);
    }

    /**
     * Get animal types that have disease reports with GPS coordinates.
     *
     * @return list of animal types with disease reports
     */
    @Operation(summary = "Get animal types with disease reports", description = "Retrieves animal types that have disease reports for filtering")
    @GetMapping("/animal-types/with-reports")
    public ResponseEntity<List<AnimalTypeDTO>> getAnimalTypesWithReports() {
        logger.info("GET /api/vet/animal-types/with-reports - Fetching animal types with disease reports");
        
        List<AnimalTypeDTO> animalTypes = diseaseReportService.getAnimalTypesWithReports();
        return ResponseEntity.ok(animalTypes);
    }

    /**
     * Get diseases that have disease reports with GPS coordinates, filtered by animal type IDs.
     *
     * @param animalTypeIds optional filter by animal type IDs
     * @return list of diseases with reports
     */
    @Operation(summary = "Get diseases with disease reports", description = "Retrieves diseases that have disease reports for filtering")
    @GetMapping("/diseases/with-reports")
    public ResponseEntity<List<DiseaseDTO>> getDiseasesWithReports(
            @RequestParam(required = false) List<UUID> animalTypeIds) {
        logger.info("GET /api/vet/diseases/with-reports - Fetching diseases with reports, animalTypeIds: {}", animalTypeIds);
        
        List<DiseaseDTO> diseases = diseaseReportService.getDiseasesWithReports(animalTypeIds);
        return ResponseEntity.ok(diseases);
    }

    /**
     * Get provinces that have farms with GPS coordinates.
     *
     * @return list of provinces with farms
     */
    @Operation(summary = "Get provinces with farms", description = "Retrieves provinces that have registered farms with GPS coordinates")
    @GetMapping("/farms/provinces")
    public ResponseEntity<List<ProvinceDTO>> getProvincesWithFarms() {
        logger.info("GET /api/vet/farms/provinces - Fetching provinces with farms");
        
        List<Province> provinces = farmRepository.findDistinctProvincesWithGpsCoordinates();
        List<ProvinceDTO> provinceDTOs = provinces.stream()
                .map(p -> new ProvinceDTO(p.name(), p.getDisplayName()))
                .toList();
        
        return ResponseEntity.ok(provinceDTOs);
    }

    // ========================================
    // ANALYTICS ENDPOINTS
    // ========================================

    /**
     * Get disease trends for analytics.
     *
     * @param animalTypeId optional filter by animal type
     * @param diseaseIds optional filter by disease IDs
     * @param province optional filter by province
     * @param district optional filter by district
     * @param farmId optional filter by farm
     * @param startDate start of date range
     * @param endDate end of date range
     * @param groupBy aggregation period: WEEKLY, MONTHLY, ANNUALLY
     * @return analytics data for charting
     */
    @Operation(summary = "Get disease trends", description = "Retrieves aggregated disease report data for analytics charts")
    @GetMapping("/analytics/trends")
    public ResponseEntity<AnalyticsResponseDTO> getDiseaseTrends(
            @RequestParam(required = false) UUID animalTypeId,
            @RequestParam(required = false) List<UUID> diseaseIds,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) UUID farmId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "MONTHLY") AnalyticsRequestDTO.GroupBy groupBy,
            @RequestParam(defaultValue = "REPORT_COUNT") AnalyticsRequestDTO.MetricType metricType) {
        
        logger.info("GET /api/vet/analytics/trends - animalTypeId: {}, diseaseIds: {}, province: {}, district: {}, farmId: {}, startDate: {}, endDate: {}, groupBy: {}, metricType: {}",
                animalTypeId, diseaseIds, province, district, farmId, startDate, endDate, groupBy, metricType);
        
        AnalyticsRequestDTO request = new AnalyticsRequestDTO();
        request.setAnimalTypeId(animalTypeId);
        request.setDiseaseIds(diseaseIds);
        request.setProvince(province);
        request.setDistrict(district);
        request.setFarmId(farmId);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setGroupBy(groupBy);
        request.setMetricType(metricType);
        
        AnalyticsResponseDTO response = analyticsService.getDiseaseTrends(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a lightweight list of active farms for analytics dropdown.
     * Supports optional filtering by province and district.
     *
     * @param province optional province filter
     * @param district optional district filter
     * @return list of {id, farmName} objects
     */
    @Operation(summary = "Get farms for analytics dropdown", description = "Returns a lightweight list of active farms, optionally filtered by province/district")
    @GetMapping("/farms/list")
    public ResponseEntity<List<java.util.Map<String, String>>> getFarmsForDropdown(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String district) {
        
        logger.info("GET /api/vet/farms/list - province: {}, district: {}", province, district);
        
        java.util.List<com.adrs.model.Farm> farms;
        
        if (district != null && !district.isEmpty()) {
            try {
                com.adrs.model.District districtEnum = com.adrs.model.District.valueOf(district);
                farms = farmRepository.findByDistrictAndIsActiveTrueOrderByFarmNameAsc(districtEnum);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid district value: {}", district);
                return ResponseEntity.badRequest().build();
            }
        } else if (province != null && !province.isEmpty()) {
            try {
                Province provinceEnum = Province.valueOf(province);
                farms = farmRepository.findByProvinceAndIsActiveTrueOrderByFarmNameAsc(provinceEnum);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid province value: {}", province);
                return ResponseEntity.badRequest().build();
            }
        } else {
            farms = farmRepository.findByIsActiveTrueOrderByFarmNameAsc();
        }
        
        List<java.util.Map<String, String>> result = farms.stream()
                .map(f -> {
                    java.util.Map<String, String> farmMap = new java.util.HashMap<>();
                    farmMap.put("id", f.getId().toString());
                    farmMap.put("farmName", f.getFarmName());
                    return farmMap;
                })
                .toList();
        
        return ResponseEntity.ok(result);
    }

    /**
     * Gets the current User entity from UserDetails.
     */
    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername()));
    }

    /**
     * Simple DTO for vet dashboard stats.
     */
    public static class VetStatsDTO {
        private Long farmsCount;
        private Long animalsCount;
        private Long reportsCount;
        // New KPI fields
        private Long activeOutbreaks;
        private Long highRiskFarms;
        private Long farmsInspectedThisMonth;
        private Long pendingFollowups;

        public Long getFarmsCount() { return farmsCount; }
        public void setFarmsCount(Long farmsCount) { this.farmsCount = farmsCount; }
        public Long getAnimalsCount() { return animalsCount; }
        public void setAnimalsCount(Long animalsCount) { this.animalsCount = animalsCount; }
        public Long getReportsCount() { return reportsCount; }
        public void setReportsCount(Long reportsCount) { this.reportsCount = reportsCount; }
        public Long getActiveOutbreaks() { return activeOutbreaks; }
        public void setActiveOutbreaks(Long activeOutbreaks) { this.activeOutbreaks = activeOutbreaks; }
        public Long getHighRiskFarms() { return highRiskFarms; }
        public void setHighRiskFarms(Long highRiskFarms) { this.highRiskFarms = highRiskFarms; }
        public Long getFarmsInspectedThisMonth() { return farmsInspectedThisMonth; }
        public void setFarmsInspectedThisMonth(Long farmsInspectedThisMonth) { this.farmsInspectedThisMonth = farmsInspectedThisMonth; }
        public Long getPendingFollowups() { return pendingFollowups; }
        public void setPendingFollowups(Long pendingFollowups) { this.pendingFollowups = pendingFollowups; }
    }

    /**
     * DTO for health trend chart data.
     */
    public static class HealthTrendDTO {
        private java.util.List<String> labels = new java.util.ArrayList<>();
        private java.util.List<Long> reports = new java.util.ArrayList<>();
        private java.util.List<Long> resolved = new java.util.ArrayList<>();

        public java.util.List<String> getLabels() { return labels; }
        public void setLabels(java.util.List<String> labels) { this.labels = labels; }
        public java.util.List<Long> getReports() { return reports; }
        public void setReports(java.util.List<Long> reports) { this.reports = reports; }
        public java.util.List<Long> getResolved() { return resolved; }
        public void setResolved(java.util.List<Long> resolved) { this.resolved = resolved; }
    }
}

