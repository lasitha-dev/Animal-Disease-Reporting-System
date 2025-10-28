package com.adrs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for dashboard statistics.
 * Contains aggregated counts and metrics for dashboard display.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    // User Management Statistics
    private Long totalUsers;
    private Long activeUsers;
    private Long inactiveUsers;
    private Long adminUsers;
    private Long veterinaryOfficerUsers;

    // Configuration Statistics
    private Long totalFarmTypes;
    private Long activeFarmTypes;
    private Long totalAnimalTypes;
    private Long activeAnimalTypes;
    private Long totalDiseases;
    private Long activeDiseases;
    private Long notifiableDiseases;

    // System-wide Statistics
    private Long totalFarms;
    private Long activeFarms;
    private Long totalAnimals;
    private Long totalDiseaseReports;
    private Long confirmedDiseaseReports;
    private Long pendingDiseaseReports;

    /**
     * Constructor with user and configuration stats only.
     */
    public DashboardStatsDTO(Long totalUsers, Long activeUsers, Long totalFarmTypes,
                             Long totalAnimalTypes, Long totalDiseases) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.totalFarmTypes = totalFarmTypes;
        this.totalAnimalTypes = totalAnimalTypes;
        this.totalDiseases = totalDiseases;
    }
}
