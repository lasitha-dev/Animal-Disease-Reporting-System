package com.adrs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO representing user distribution across a province.
 * Contains province information, total user count, and district-level breakdown.
 * Used for dashboard map visualization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceUserDistributionDTO {

    /**
     * Province enum name (e.g., "WESTERN", "CENTRAL")
     */
    private String province;

    /**
     * Human-readable province name (e.g., "Western Province")
     */
    private String displayName;

    /**
     * Total number of users in this province
     */
    private Long userCount;

    /**
     * Breakdown of users by district within this province.
     * Key: District name, Value: User count in that district
     */
    private Map<String, Long> districtBreakdown;

    /**
     * Constructor without district breakdown (for simple queries)
     *
     * @param province    the province enum name
     * @param displayName the display name of the province
     * @param userCount   the total user count
     */
    public ProvinceUserDistributionDTO(String province, String displayName, Long userCount) {
        this.province = province;
        this.displayName = displayName;
        this.userCount = userCount;
    }
}
