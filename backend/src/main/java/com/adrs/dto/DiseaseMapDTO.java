package com.adrs.dto;

import com.adrs.model.Disease;
import com.adrs.model.DiseaseReport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO for disease report data displayed on the map.
 * Groups disease reports by farm location.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseMapDTO {

    // Farm information
    private UUID farmId;
    private String farmName;
    private BigDecimal gpsLatitude;
    private BigDecimal gpsLongitude;
    private String address;
    private String districtDisplayName;
    private String provinceDisplayName;
    private String ownerName;

    // List of diseases at this farm
    private List<DiseaseInfo> diseases = new ArrayList<>();

    /**
     * Nested class for individual disease report info.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiseaseInfo {
        private UUID reportId;
        private String diseaseName;
        private String diseaseCode;
        private Disease.Severity severity;
        private Boolean isNotifiable;
        private UUID animalTypeId;
        private String animalTypeName;
        private Integer affectedCount;
        private LocalDate reportDate;
        private DiseaseReport.Outcome outcome;
        private String reportedByUsername;
        
        // Effective values (overrides applied)
        private String effectiveDiseaseName;
        private Disease.Severity effectiveSeverity;
        private Boolean effectiveNotifiable;
    }
}
