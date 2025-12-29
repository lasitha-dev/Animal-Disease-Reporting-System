package com.adrs.dto;

import com.adrs.model.DiseaseReport;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Disease ID is required")
    private UUID diseaseId;

    @NotNull(message = "Report date is required")
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
    private Integer affectedCount;
    
    /**
     * Flag to indicate that the existing image should be removed
     */
    private Boolean clearImage = false;
}
