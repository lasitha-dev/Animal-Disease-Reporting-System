package com.adrs.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enum representing the districts of Sri Lanka.
 * Each district belongs to a specific province.
 */
public enum District {
    // Northern Province
    JAFFNA(Province.NORTHERN, "Jaffna"),
    KILINOCHCHI(Province.NORTHERN, "Kilinochchi"),
    MANNAR(Province.NORTHERN, "Mannar"),
    MULLAITIVU(Province.NORTHERN, "Mullaitivu"),
    VAVUNIYA(Province.NORTHERN, "Vavuniya"),

    // North-Western Province
    PUTTALAM(Province.NORTH_WESTERN, "Puttalam"),
    KURUNEGALA(Province.NORTH_WESTERN, "Kurunegala"),

    // Western Province
    GAMPAHA(Province.WESTERN, "Gampaha"),
    COLOMBO(Province.WESTERN, "Colombo"),
    KALUTARA(Province.WESTERN, "Kalutara"),

    // North-Central Province
    ANURADHAPURA(Province.NORTH_CENTRAL, "Anuradhapura"),
    POLONNARUWA(Province.NORTH_CENTRAL, "Polonnaruwa"),

    // Central Province
    MATALE(Province.CENTRAL, "Matale"),
    KANDY(Province.CENTRAL, "Kandy"),
    NUWARA_ELIYA(Province.CENTRAL, "Nuwara Eliya"),

    // Sabaragamuwa Province
    KEGALLE(Province.SABARAGAMUWA, "Kegalle"),
    RATNAPURA(Province.SABARAGAMUWA, "Ratnapura"),

    // Eastern Province
    TRINCOMALEE(Province.EASTERN, "Trincomalee"),
    BATTICALOA(Province.EASTERN, "Batticaloa"),
    AMPARA(Province.EASTERN, "Ampara"),

    // Uva Province
    BADULLA(Province.UVA, "Badulla"),
    MONARAGALA(Province.UVA, "Monaragala"),

    // Southern Province
    HAMBANTOTA(Province.SOUTHERN, "Hambantota"),
    MATARA(Province.SOUTHERN, "Matara"),
    GALLE(Province.SOUTHERN, "Galle");

    private final Province province;
    private final String displayName;

    District(Province province, String displayName) {
        this.province = province;
        this.displayName = displayName;
    }

    /**
     * Gets the province to which this district belongs.
     *
     * @return the province
     */
    public Province getProvince() {
        return province;
    }

    /**
     * Gets the display name of the district.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets all districts that belong to a specific province.
     *
     * @param province the province
     * @return list of districts in the province
     */
    public static List<District> getDistrictsByProvince(Province province) {
        if (province == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(District.values())
                .filter(district -> district.getProvince() == province)
                .toList();
    }

    /**
     * Gets all available provinces.
     *
     * @return list of all provinces
     */
    public static List<Province> getAllProvinces() {
        return Arrays.asList(Province.values());
    }
}
