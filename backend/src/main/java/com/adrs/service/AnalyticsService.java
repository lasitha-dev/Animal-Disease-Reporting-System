package com.adrs.service;

import com.adrs.dto.AnalyticsRequestDTO;
import com.adrs.dto.AnalyticsResponseDTO;

/**
 * Service interface for analytics operations.
 * Provides methods to retrieve disease report trend data.
 */
public interface AnalyticsService {

    /**
     * Get disease trends aggregated by the specified time period.
     *
     * @param request the analytics request with filters and groupBy settings
     * @return analytics response with labels and datasets for charting
     */
    AnalyticsResponseDTO getDiseaseTrends(AnalyticsRequestDTO request);
}
