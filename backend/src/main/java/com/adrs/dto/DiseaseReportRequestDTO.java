package com.adrs.dto;

import com.adrs.model.DiseaseReport;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for creating a new disease report.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseReportRequestDTO {

    @NotNull(message = "Farm ID is required")
    private UUID farmId;

    @NotNull(message = "Animal type ID is required")
    private UUID animalTypeId;

    /**
     * Disease ID - required unless isOtherDisease is true
     */
    private UUID diseaseId;

    /**
     * Flag indicating this is an "Other" disease that needs to be created
     */
    private Boolean isOtherDisease = false;

    /**
     * Name for the new disease (required when isOtherDisease is true)
     */
    @Size(min = 2, max = 100, message = "Disease name must be between 2 and 100 characters")
    private String newDiseaseName;

    /**
     * Code for the new disease (optional when isOtherDisease is true)
     */
    @Size(max = 20, message = "Disease code must not exceed 20 characters")
    private String newDiseaseCode;

    /**
     * Severity for the new disease (required when isOtherDisease is true)
     */
    private String newDiseaseSeverity;

    /**
     * Description for the new disease (optional when isOtherDisease is true)
     */
    @Size(max = 1000, message = "Disease description must not exceed 1000 characters")
    private String newDiseaseDescription;

    /**
     * Notifiable status for the new disease (required when isOtherDisease is true)
     */
    private Boolean newDiseaseIsNotifiable;

    @NotNull(message = "Report date is required")
    @PastOrPresent(message = "Report date cannot be in the future")
    private LocalDate reportDate;

    @Size(max = 2000, message = "Symptoms must not exceed 2000 characters")
    private String symptoms;

    @Size(max = 2000, message = "Diagnosis must not exceed 2000 characters")
    private String diagnosis;

    @Size(max = 2000, message = "Treatment must not exceed 2000 characters")
    private String treatment;

    private DiseaseReport.Outcome outcome;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes;
    
    /**
     * Number of affected animals of this type
     */
    @Min(value = 0, message = "Affected count cannot be negative")
    private Integer affectedCount;
    
    /**
     * Flag to indicate that the existing image should be removed
     */
    private Boolean clearImage = false;

    /**
     * Override for disease name (vet-specific, does not affect admin data)
     */
    @Size(max = 255, message = "Override disease name must not exceed 255 characters")
    private String overrideDiseaseName;

    /**
     * Override for severity (vet-specific, does not affect admin data)
     */
    private String overrideSeverity;

    /**
     * Override for description (vet-specific, does not affect admin data)
     */
    private String overrideDescription;

    /**
     * Override for notifiable status (vet-specific, does not affect admin data)
     */
    private Boolean overrideNotifiable;
}
