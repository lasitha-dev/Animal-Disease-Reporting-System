package com.adrs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for analytics response.
 * Contains data formatted for Chart.js line charts with multiple datasets.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponseDTO {

    /**
     * Labels for the x-axis (time periods).
     * Format depends on groupBy setting:
     * - WEEKLY: "2026-W02" (ISO week)
     * - MONTHLY: "Jan 2026"
     * - ANNUALLY: "2026"
     */
    private List<String> labels;

    /**
     * Multiple datasets, one per disease.
     * Each dataset contains the disease info and count data.
     */
    private List<DatasetDTO> datasets;

    /**
     * Inner class representing a single dataset for Chart.js.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatasetDTO {
        /**
         * Disease ID for reference.
         */
        private UUID diseaseId;

        /**
         * Disease name for display in legend.
         */
        private String diseaseName;

        /**
         * Disease code for compact display.
         */
        private String diseaseCode;

        /**
         * Data points corresponding to each label.
         * Each value is the count of disease reports for that period.
         */
        private List<Long> data;
    }
}
