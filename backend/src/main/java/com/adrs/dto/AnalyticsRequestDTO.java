package com.adrs.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for analytics request parameters.
 * Used to specify filters and aggregation settings for disease trend analytics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsRequestDTO {

    /**
     * Optional filter by animal type ID.
     * If null, all animal types are included.
     */
    private UUID animalTypeId;

    /**
     * Optional filter by disease IDs.
     * Supports multiple diseases for comparison.
     * If null or empty, all diseases are included.
     */
    private List<UUID> diseaseIds;

    /**
     * Start date for the analytics period (inclusive).
     */
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    /**
     * End date for the analytics period (inclusive).
     */
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    /**
     * Aggregation period: WEEKLY, MONTHLY, or ANNUALLY.
     * Defaults to MONTHLY if not specified.
     */
    private GroupBy groupBy = GroupBy.MONTHLY;

    /**
     * Enum for aggregation periods.
     */
    public enum GroupBy {
        WEEKLY,
        MONTHLY,
        ANNUALLY
    }
}
