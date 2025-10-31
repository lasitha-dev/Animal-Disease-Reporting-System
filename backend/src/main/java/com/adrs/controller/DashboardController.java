package com.adrs.controller;

import com.adrs.dto.ChartDataDTO;
import com.adrs.dto.DashboardStatsDTO;
import com.adrs.dto.DistrictUserDistributionDTO;
import com.adrs.dto.ProvinceUserDistributionDTO;
import com.adrs.dto.UserResponse;
import com.adrs.model.District;
import com.adrs.model.Province;
import com.adrs.model.User;
import com.adrs.service.DashboardService;
import com.adrs.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for dashboard analytics.
 * Provides endpoints for statistics, charts, and summary data.
 * Available to all authenticated users, but some endpoints are admin-only.
 */
@Tag(name = "Dashboard Analytics", description = "APIs for dashboard statistics, charts, and trend data")
@RestController
@RequestMapping("/api/dashboard")
@Validated
@SecurityRequirement(name = "session-auth")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    
    private final DashboardService dashboardService;
    private final UserService userService;

    public DashboardController(DashboardService dashboardService, UserService userService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
    }

    /**
     * Get comprehensive dashboard statistics.
     * Includes user counts, configuration counts, and system-wide metrics.
     *
     * @return dashboard statistics
     */
    @Operation(summary = "Get dashboard statistics", 
               description = "Retrieves comprehensive statistics including user counts, farms, animals, and disease reports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved dashboard statistics"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content)
    })
    @GetMapping("/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardStatsDTO> getDashboardStatistics() {
        logger.info("GET /api/dashboard/stats - Fetching dashboard statistics");
        DashboardStatsDTO stats = dashboardService.getDashboardStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get summary counts for dashboard cards.
     *
     * @return summary counts map
     */
    @Operation(summary = "Get summary counts", 
               description = "Retrieves quick summary counts for dashboard overview cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved summary counts"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content)
    })
    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> getSummaryCounts() {
        logger.info("GET /api/dashboard/summary - Fetching summary counts");
        Map<String, Long> summary = dashboardService.getSummaryCounts();
        return ResponseEntity.ok(summary);
    }

    // ========================================
    // PIE CHART ENDPOINTS
    // ========================================

    /**
     * Get user role distribution for pie chart.
     * Shows breakdown of users by role (ADMIN, VETERINARIAN, FARMER).
     *
     * @return chart data for pie chart
     */
    @GetMapping("/charts/user-roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChartDataDTO> getUserRoleDistribution() {
        logger.info("GET /api/dashboard/charts/user-roles - Fetching user role distribution");
        ChartDataDTO chartData = dashboardService.getUserRoleDistribution();
        return ResponseEntity.ok(chartData);
    }

    /**
     * Get user status distribution for pie chart.
     * Shows breakdown of active vs inactive users.
     *
     * @return chart data for pie chart
     */
    @GetMapping("/charts/user-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChartDataDTO> getUserStatusDistribution() {
        logger.info("GET /api/dashboard/charts/user-status - Fetching user status distribution");
        ChartDataDTO chartData = dashboardService.getUserStatusDistribution();
        return ResponseEntity.ok(chartData);
    }

    /**
     * Get configuration status distribution for pie chart.
     * Shows breakdown of active vs inactive configuration items (farm types, animal types, diseases).
     *
     * @return chart data for pie chart
     */
    @GetMapping("/charts/config-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChartDataDTO> getConfigurationStatusDistribution() {
        logger.info("GET /api/dashboard/charts/config-status - Fetching configuration status distribution");
        ChartDataDTO chartData = dashboardService.getConfigurationStatusDistribution();
        return ResponseEntity.ok(chartData);
    }

    /**
     * Get farm type distribution for pie chart.
     * Shows breakdown of farms by type.
     *
     * @return chart data for pie chart
     */
    @GetMapping("/charts/farm-types")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChartDataDTO> getFarmTypeDistribution() {
        logger.info("GET /api/dashboard/charts/farm-types - Fetching farm type distribution");
        ChartDataDTO chartData = dashboardService.getFarmTypeDistribution();
        return ResponseEntity.ok(chartData);
    }

    /**
     * Get disease severity distribution for pie chart.
     * Shows breakdown of diseases by severity level.
     *
     * @return chart data for pie chart
     */
    @GetMapping("/charts/disease-severity")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChartDataDTO> getDiseaseSeverityDistribution() {
        logger.info("GET /api/dashboard/charts/disease-severity - Fetching disease severity distribution");
        ChartDataDTO chartData = dashboardService.getDiseaseSeverityDistribution();
        return ResponseEntity.ok(chartData);
    }

    // ========================================
    // LINE CHART ENDPOINTS (TRENDS)
    // ========================================

    /**
     * Get user registration trend for line chart.
     * Shows user registrations over time.
     *
     * @param months number of months to include (default 6, must be >= 1)
     * @return chart data for line chart
     */
    @GetMapping("/charts/user-trend")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChartDataDTO> getUserRegistrationTrend(
            @RequestParam(value = "months", defaultValue = "6") @Min(1) int months) {
        logger.info("GET /api/dashboard/charts/user-trend?months={} - Fetching user registration trend", months);
        ChartDataDTO chartData = dashboardService.getUserRegistrationTrend(months);
        return ResponseEntity.ok(chartData);
    }

    /**
     * Get farm registration trend for line chart.
     * Shows farm registrations over time.
     *
     * @param months number of months to include (default 6, must be >= 1)
     * @return chart data for line chart
     */
    @GetMapping("/charts/farm-trend")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChartDataDTO> getFarmRegistrationTrend(
            @RequestParam(value = "months", defaultValue = "6") @Min(1) int months) {
        logger.info("GET /api/dashboard/charts/farm-trend?months={} - Fetching farm registration trend", months);
        ChartDataDTO chartData = dashboardService.getFarmRegistrationTrend(months);
        return ResponseEntity.ok(chartData);
    }

    /**
     * Get disease report trend for line chart.
     * Shows disease reports over time.
     *
     * @param months number of months to include (default 6, must be >= 1)
     * @return chart data for line chart
     */
    @GetMapping("/charts/disease-report-trend")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChartDataDTO> getDiseaseReportTrend(
            @RequestParam(value = "months", defaultValue = "6") @Min(1) int months) {
        logger.info("GET /api/dashboard/charts/disease-report-trend?months={} - Fetching disease report trend", months);
        ChartDataDTO chartData = dashboardService.getDiseaseReportTrend(months);
        return ResponseEntity.ok(chartData);
    }

    /**
     * Get multiple trends in a single request.
     * Useful for loading all trend charts at once.
     *
     * @param months number of months to include (default 6)
     * @return map of chart data
     */
    @GetMapping("/charts/trends")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, ChartDataDTO>> getAllTrends(
            @RequestParam(value = "months", defaultValue = "6") int months) {
        logger.info("GET /api/dashboard/charts/trends?months={} - Fetching all trends", months);
        
        ChartDataDTO farmTrend = dashboardService.getFarmRegistrationTrend(months);
        ChartDataDTO diseaseReportTrend = dashboardService.getDiseaseReportTrend(months);
        
        Map<String, ChartDataDTO> trends = Map.of(
            "farmTrend", farmTrend,
            "diseaseReportTrend", diseaseReportTrend
        );
        
        return ResponseEntity.ok(trends);
    }

    /**
     * Get all pie charts in a single request.
     * Useful for loading all pie charts at once (admin only).
     *
     * @return map of chart data
     */
    @GetMapping("/charts/pie-charts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, ChartDataDTO>> getAllPieCharts() {
        logger.info("GET /api/dashboard/charts/pie-charts - Fetching all pie charts");
        
        ChartDataDTO userRoles = dashboardService.getUserRoleDistribution();
        ChartDataDTO userStatus = dashboardService.getUserStatusDistribution();
        ChartDataDTO configStatus = dashboardService.getConfigurationStatusDistribution();
        ChartDataDTO farmTypes = dashboardService.getFarmTypeDistribution();
        ChartDataDTO diseaseSeverity = dashboardService.getDiseaseSeverityDistribution();
        
        Map<String, ChartDataDTO> pieCharts = Map.of(
            "userRoles", userRoles,
            "userStatus", userStatus,
            "configStatus", configStatus,
            "farmTypes", farmTypes,
            "diseaseSeverity", diseaseSeverity
        );
        
        return ResponseEntity.ok(pieCharts);
    }

    // ========================================
    // GEOGRAPHIC DISTRIBUTION ENDPOINTS
    // ========================================

    /**
     * Get user distribution by province with district breakdown.
     * Optionally filter by user role.
     *
     * @param role optional role filter (ADMIN, VETERINARY_OFFICER)
     * @return list of province distribution data
     */
    @Operation(summary = "Get user distribution by province",
               description = "Retrieves user counts grouped by province with district-level breakdown. Supports role filtering.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved province distribution"),
            @ApiResponse(responseCode = "400", description = "Invalid role parameter", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content)
    })
    @GetMapping("/users/province-distribution")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProvinceUserDistributionDTO>> getUserProvinceDistribution(
            @Parameter(description = "User role to filter by (ADMIN or VETERINARY_OFFICER)")
            @RequestParam(required = false) String role) {
        
        logger.info("GET /api/dashboard/users/province-distribution - Fetching user province distribution" 
                   + (role != null ? " for role: " + role : ""));
        
        User.Role userRole = null;
        if (role != null && !role.isEmpty()) {
            try {
                userRole = User.Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid role parameter: {}", role);
                return ResponseEntity.badRequest().build();
            }
        }
        
        List<ProvinceUserDistributionDTO> distribution = dashboardService.getUserDistributionByProvince(userRole);
        return ResponseEntity.ok(distribution);
    }

    /**
     * Get users by province with optional role filter.
     * Used by the modal to display user details for a specific province.
     *
     * @param province the province enum name
     * @param role     optional role filter
     * @return list of users in the province
     */
    @Operation(summary = "Get users by province",
               description = "Retrieves detailed user list for a specific province, optionally filtered by role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
            @ApiResponse(responseCode = "400", description = "Invalid province or role parameter", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content)
    })
    @GetMapping("/users/by-province")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserResponse>> getUsersByProvince(
            @Parameter(description = "Province enum name (e.g., WESTERN, CENTRAL)")
            @RequestParam String province,
            @Parameter(description = "User role to filter by (ADMIN or VETERINARY_OFFICER)")
            @RequestParam(required = false) String role) {
        
        logger.info("GET /api/dashboard/users/by-province - Province: {}, Role: {}", province, role);
        
        Province provinceEnum;
        try {
            provinceEnum = Province.valueOf(province.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid province parameter: {}", province);
            return ResponseEntity.badRequest().build();
        }
        
        User.Role userRole = null;
        if (role != null && !role.isEmpty()) {
            try {
                userRole = User.Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid role parameter: {}", role);
                return ResponseEntity.badRequest().build();
            }
        }
        
        List<UserResponse> users = userService.getUsersByProvinceAndRole(provinceEnum, userRole);
        return ResponseEntity.ok(users);
    }

    // ========================================
    // DISTRICT-LEVEL USER DISTRIBUTION
    // ========================================

    /**
     * Get user distribution by district (all 25 districts).
     * Optionally filter by user role.
     * Used by the interactive Sri Lanka map visualization.
     *
     * @param role optional role filter
     * @return list of district distribution data
     */
    @Operation(summary = "Get user distribution by district",
               description = "Retrieves user counts for all 25 districts in Sri Lanka. Supports role filtering.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved district distribution"),
            @ApiResponse(responseCode = "400", description = "Invalid role parameter", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content)
    })
    @GetMapping("/users/district-distribution")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DistrictUserDistributionDTO>> getUserDistrictDistribution(
            @Parameter(description = "User role to filter by (ADMIN or VETERINARY_OFFICER)")
            @RequestParam(required = false) String role) {
        
        logger.info("GET /api/dashboard/users/district-distribution - Fetching user district distribution" 
                   + (role != null ? " for role: " + role : ""));
        
        User.Role userRole = null;
        if (role != null && !role.isEmpty()) {
            try {
                userRole = User.Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid role parameter: {}", role);
                return ResponseEntity.badRequest().build();
            }
        }
        
        List<DistrictUserDistributionDTO> distribution = dashboardService.getUserDistributionByDistrict(userRole);
        return ResponseEntity.ok(distribution);
    }

    /**
     * Get users by district with optional role filter.
     * Used by the map modal to display user details for a specific district.
     *
     * @param district the district enum name
     * @param role     optional role filter
     * @return list of users in the district
     */
    @Operation(summary = "Get users by district",
               description = "Retrieves detailed user list for a specific district, optionally filtered by role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
            @ApiResponse(responseCode = "400", description = "Invalid district or role parameter", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content)
    })
    @GetMapping("/users/by-district")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserResponse>> getUsersByDistrict(
            @Parameter(description = "District enum name (e.g., COLOMBO, GAMPAHA, KANDY)")
            @RequestParam String district,
            @Parameter(description = "User role to filter by (ADMIN or VETERINARY_OFFICER)")
            @RequestParam(required = false) String role) {
        
        logger.info("GET /api/dashboard/users/by-district - District: {}, Role: {}", district, role);
        
        District districtEnum;
        try {
            districtEnum = District.valueOf(district.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid district parameter: {}", district);
            return ResponseEntity.badRequest().build();
        }
        
        User.Role userRole = null;
        if (role != null && !role.isEmpty()) {
            try {
                userRole = User.Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid role parameter: {}", role);
                return ResponseEntity.badRequest().build();
            }
        }
        
        List<User> users = dashboardService.getUsersByDistrictAndRole(districtEnum, userRole);
        
        // Convert to UserResponse DTOs
        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::fromUser)
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(userResponses);
    }
}
