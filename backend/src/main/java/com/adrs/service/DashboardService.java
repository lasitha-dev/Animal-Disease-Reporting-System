package com.adrs.service;

import com.adrs.dto.ChartDataDTO;
import com.adrs.dto.DashboardStatsDTO;
import com.adrs.model.Disease;
import com.adrs.model.User;

import java.util.Map;

/**
 * Service interface for Dashboard analytics.
 * Provides aggregated statistics and chart data for the dashboard.
 */
public interface DashboardService {

    /**
     * Get comprehensive dashboard statistics.
     *
     * @return dashboard stats DTO with all metrics
     */
    DashboardStatsDTO getDashboardStatistics();

    /**
     * Get user distribution by role (pie chart data).
     *
     * @return chart data with user counts by role
     */
    ChartDataDTO getUserRoleDistribution();

    /**
     * Get active vs inactive users (pie chart data).
     *
     * @return chart data with active/inactive user counts
     */
    ChartDataDTO getUserStatusDistribution();

    /**
     * Get configuration status distribution (pie chart data).
     *
     * @return chart data with active/inactive configuration counts
     */
    ChartDataDTO getConfigurationStatusDistribution();

    /**
     * Get farm type distribution (pie chart data).
     *
     * @return chart data with farm counts by type
     */
    ChartDataDTO getFarmTypeDistribution();

    /**
     * Get disease severity distribution (pie chart data).
     *
     * @return chart data with disease counts by severity
     */
    ChartDataDTO getDiseaseSeverityDistribution();

    /**
     * Get user registration trend (line graph data).
     *
     * @param months number of months to include in trend
     * @return chart data with user registrations over time
     */
    ChartDataDTO getUserRegistrationTrend(int months);

    /**
     * Get farm registration trend (line graph data).
     *
     * @param months number of months to include in trend
     * @return chart data with farm registrations over time
     */
    ChartDataDTO getFarmRegistrationTrend(int months);

    /**
     * Get disease report trend (line graph data).
     *
     * @param months number of months to include in trend
     * @return chart data with disease reports over time
     */
    ChartDataDTO getDiseaseReportTrend(int months);

    /**
     * Get summary counts for quick stats display.
     *
     * @return map of key metrics
     */
    Map<String, Long> getSummaryCounts();
}
