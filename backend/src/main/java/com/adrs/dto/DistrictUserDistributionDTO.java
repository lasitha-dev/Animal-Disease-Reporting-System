package com.adrs.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for district-level user distribution.
 * Used for map visualization of user counts across Sri Lanka's 25 districts.
 */
@Schema(description = "District-level user distribution data")
public class DistrictUserDistributionDTO {

    @Schema(description = "District enum name (e.g., COLOMBO, GAMPAHA)", example = "COLOMBO")
    private String district;

    @Schema(description = "User-friendly district display name", example = "Colombo")
    private String displayName;

    @Schema(description = "Number of users in this district", example = "25")
    private Long userCount;

    /**
     * Default constructor required for Jackson deserialization.
     */
    public DistrictUserDistributionDTO() {
    }

    /**
     * Constructor for creating district distribution data.
     *
     * @param district    District enum name
     * @param displayName User-friendly district name
     * @param userCount   Number of users in the district
     */
    public DistrictUserDistributionDTO(String district, String displayName, Long userCount) {
        this.district = district;
        this.displayName = displayName;
        this.userCount = userCount;
    }

    // Getters and Setters

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getUserCount() {
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    @Override
    public String toString() {
        return "DistrictUserDistributionDTO{" +
                "district='" + district + '\'' +
                ", displayName='" + displayName + '\'' +
                ", userCount=" + userCount +
                '}';
    }
}
