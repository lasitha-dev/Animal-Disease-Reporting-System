package com.adrs.controller;

import com.adrs.dto.ChartDataDTO;
import com.adrs.dto.DashboardStatsDTO;
import com.adrs.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for dashboard analytics.
 * Provides endpoints for statistics, charts, and summary data.
 * Available to all authenticated users, but some endpoints are admin-only.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private static final int DEFAULT_TREND_MONTHS = 6;
    
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Get comprehensive dashboard statistics.
     * Includes user counts, configuration counts, and system-wide metrics.
     *
     * @return dashboard statistics
     */
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
    @PreAuthorize("hasRole('ADMIN')")
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
     * @param months number of months to include (default 6)
     * @return chart data for line chart
     */
    @GetMapping("/charts/user-trend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChartDataDTO> getUserRegistrationTrend(
            @RequestParam(value = "months", defaultValue = "6") int months) {
        logger.info("GET /api/dashboard/charts/user-trend?months={} - Fetching user registration trend", months);
        ChartDataDTO chartData = dashboardService.getUserRegistrationTrend(months);
        return ResponseEntity.ok(chartData);
    }

    /**
     * Get farm registration trend for line chart.
     * Shows farm registrations over time.
     *
     * @param months number of months to include (default 6)
     * @return chart data for line chart
     */
    @GetMapping("/charts/farm-trend")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChartDataDTO> getFarmRegistrationTrend(
            @RequestParam(value = "months", defaultValue = "6") int months) {
        logger.info("GET /api/dashboard/charts/farm-trend?months={} - Fetching farm registration trend", months);
        ChartDataDTO chartData = dashboardService.getFarmRegistrationTrend(months);
        return ResponseEntity.ok(chartData);
    }

    /**
     * Get disease report trend for line chart.
     * Shows disease reports over time.
     *
     * @param months number of months to include (default 6)
     * @return chart data for line chart
     */
    @GetMapping("/charts/disease-report-trend")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChartDataDTO> getDiseaseReportTrend(
            @RequestParam(value = "months", defaultValue = "6") int months) {
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
}
