package com.adrs.dto;

import com.adrs.model.Disease;
import com.adrs.model.DiseaseReport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for disease report data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseReportResponseDTO {

    private UUID id;

    // Farm info
    private UUID farmId;
    private String farmName;
    private String farmAddress;

    // Animal type info
    private UUID animalTypeId;
    private String animalTypeName;
    private Integer affectedCount;

    // Disease info
    private UUID diseaseId;
    private String diseaseName;
    private String diseaseCode;
    private Disease.Severity severity;
    private Boolean isNotifiable;

    // Report details
    private LocalDate reportDate;
    private String symptoms;
    private String diagnosis;
    private String treatment;
    private DiseaseReport.Outcome outcome;
    private String notes;

    // Image
    private String imagePath;
    private String imageUrl;

    // Status
    private Boolean isConfirmed;
    private String confirmedByUsername;
    private LocalDateTime confirmedAt;

    // Audit info
    private String reportedByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
