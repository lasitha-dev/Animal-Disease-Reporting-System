package com.adrs.test.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for DashboardController.
 * Tests REST API endpoints for dashboard analytics with real database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Dashboard Controller Integration Tests")
class DashboardControllerIntegrationTest {

    private static final String DASHBOARD_STATS_ENDPOINT = "/api/dashboard/stats";
    private static final String DASHBOARD_SUMMARY_ENDPOINT = "/api/dashboard/summary";
    private static final String USER_ROLE_CHART_ENDPOINT = "/api/dashboard/charts/user-roles";
    private static final String USER_STATUS_CHART_ENDPOINT = "/api/dashboard/charts/user-status";
    private static final String DISEASE_SEVERITY_CHART_ENDPOINT = "/api/dashboard/charts/disease-severity";
    private static final String FARM_TYPE_CHART_ENDPOINT = "/api/dashboard/charts/farm-types";
    private static final String USER_TREND_ENDPOINT = "/api/dashboard/charts/user-trend";
    private static final String FARM_TREND_ENDPOINT = "/api/dashboard/charts/farm-trend";
    private static final String DISEASE_TREND_ENDPOINT = "/api/dashboard/charts/disease-report-trend";
    private static final String CONFIG_STATUS_CHART_ENDPOINT = "/api/dashboard/charts/config-status";

    @Autowired
    private MockMvc mockMvc;

    // ========================================
    // STATISTICS TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get dashboard statistics")
    void testGetDashboardStatistics() throws Exception {
        mockMvc.perform(get(DASHBOARD_STATS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalUsers").exists())
                .andExpect(jsonPath("$.activeUsers").exists())
                .andExpect(jsonPath("$.totalFarms").exists())
                .andExpect(jsonPath("$.totalAnimals").exists())
                .andExpect(jsonPath("$.totalDiseaseReports").exists());
    }

    @Test
    @WithMockUser(roles = "VETERINARY_OFFICER")
    @DisplayName("Should allow authenticated non-admin to access dashboard stats")
    void testGetDashboardStatisticsAsNonAdmin() throws Exception {
        mockMvc.perform(get(DASHBOARD_STATS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should redirect unauthenticated users to login")
    void testGetDashboardStatisticsUnauthenticated() throws Exception {
        mockMvc.perform(get(DASHBOARD_STATS_ENDPOINT))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get dashboard summary counts")
    void testGetSummaryCounts() throws Exception {
        mockMvc.perform(get(DASHBOARD_SUMMARY_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isMap());
    }

    // ========================================
    // CHART DATA TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get user role distribution chart data")
    void testGetUserRoleDistribution() throws Exception {
        mockMvc.perform(get(USER_ROLE_CHART_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get user status distribution chart data")
    void testGetUserStatusDistribution() throws Exception {
        mockMvc.perform(get(USER_STATUS_CHART_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get disease severity distribution chart data")
    void testGetDiseaseSeverityDistribution() throws Exception {
        mockMvc.perform(get(DISEASE_SEVERITY_CHART_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get farm type distribution chart data")
    void testGetFarmTypeDistribution() throws Exception {
        mockMvc.perform(get(FARM_TYPE_CHART_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get configuration status distribution chart data")
    void testGetConfigurationStatusDistribution() throws Exception {
        mockMvc.perform(get(CONFIG_STATUS_CHART_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.data").isArray());
    }

    // ========================================
    // TREND TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get user registration trend")
    void testGetUserRegistrationTrend() throws Exception {
        mockMvc.perform(get(USER_TREND_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.chartType").value("line"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get user registration trend with custom months")
    void testGetUserRegistrationTrendWithCustomMonths() throws Exception {
        mockMvc.perform(get(USER_TREND_ENDPOINT).param("months", "12"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.chartType").value("line"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get farm registration trend")
    void testGetFarmRegistrationTrend() throws Exception {
        mockMvc.perform(get(FARM_TREND_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get farm registration trend with custom months")
    void testGetFarmRegistrationTrendWithCustomMonths() throws Exception {
        mockMvc.perform(get(FARM_TREND_ENDPOINT).param("months", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get disease report trend")
    void testGetDiseaseReportTrend() throws Exception {
        mockMvc.perform(get(DISEASE_TREND_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get disease report trend with custom months")
    void testGetDiseaseReportTrendWithCustomMonths() throws Exception {
        mockMvc.perform(get(DISEASE_TREND_ENDPOINT).param("months", "6"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.data").isArray());
    }

    // ========================================
    // SECURITY TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "VETERINARY_OFFICER")
    @DisplayName("Should allow non-admin to access chart data")
    void testGetChartDataAsNonAdmin() throws Exception {
        mockMvc.perform(get(USER_ROLE_CHART_ENDPOINT))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should redirect unauthenticated users to login")
    void testGetChartDataUnauthenticated() throws Exception {
        mockMvc.perform(get(USER_ROLE_CHART_ENDPOINT))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "VETERINARY_OFFICER")
    @DisplayName("Should allow non-admin to access trend data")
    void testGetTrendDataAsNonAdmin() throws Exception {
        mockMvc.perform(get(USER_TREND_ENDPOINT))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should redirect unauthenticated users to login")
    void testGetTrendDataUnauthenticated() throws Exception {
        mockMvc.perform(get(FARM_TREND_ENDPOINT))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    // ========================================
    // ERROR HANDLING TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle invalid trend months parameter")
    void testGetTrendWithInvalidMonths() throws Exception {
        mockMvc.perform(get(USER_TREND_ENDPOINT).param("months", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle negative trend months parameter")
    void testGetTrendWithNegativeMonths() throws Exception {
        mockMvc.perform(get(USER_TREND_ENDPOINT).param("months", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should use default months when parameter is missing")
    void testGetTrendWithMissingMonths() throws Exception {
        mockMvc.perform(get(FARM_TREND_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.data").isArray());
    }
}
