package com.adrs.model;

/**
 * Enum representing the provinces of Sri Lanka.
 * Sri Lanka is divided into 9 administrative provinces.
 */
public enum Province {
    NORTHERN("Northern Province"),
    NORTH_WESTERN("North-Western Province"),
    WESTERN("Western Province"),
    NORTH_CENTRAL("North-Central Province"),
    CENTRAL("Central Province"),
    SABARAGAMUWA("Sabaragamuwa Province"),
    EASTERN("Eastern Province"),
    UVA("Uva Province"),
    SOUTHERN("Southern Province");

    private final String displayName;

    Province(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the province.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}
