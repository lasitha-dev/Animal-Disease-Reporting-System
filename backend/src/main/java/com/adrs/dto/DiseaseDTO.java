package com.adrs.dto;

import com.adrs.model.Disease;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Disease entity.
 * Used for transferring disease data between layers without exposing entity details.
 * Includes validation annotations for input validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseDTO {

    private UUID id;

    @NotBlank(message = "Disease name is required")
    @Size(min = 2, max = 100, message = "Disease name must be between 2 and 100 characters")
    private String diseaseName;

    @Size(max = 20, message = "Disease code must not exceed 20 characters")
    private String diseaseCode;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private String[] affectedAnimalTypes;

    private Disease.Severity severity;

    private Boolean isNotifiable;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdByUsername;

    private String updatedByUsername;

    /**
     * Constructor for creating DTO with minimal information.
     *
     * @param diseaseName the name of the disease
     * @param diseaseCode the unique code
     * @param description the description
     * @param severity the severity level
     * @param isNotifiable whether it's notifiable
     */
    public DiseaseDTO(String diseaseName, String diseaseCode, String description,
                      Disease.Severity severity, Boolean isNotifiable) {
        this.diseaseName = diseaseName;
        this.diseaseCode = diseaseCode;
        this.description = description;
        this.severity = severity;
        this.isNotifiable = isNotifiable;
        this.isActive = true;
    }

    /**
     * Constructor for creating DTO with all basic fields.
     *
     * @param id the unique identifier
     * @param diseaseName the name of the disease
     * @param diseaseCode the unique code
     * @param description the description
     * @param severity the severity level
     * @param isNotifiable whether it's notifiable
     * @param isActive the active status
     */
    public DiseaseDTO(UUID id, String diseaseName, String diseaseCode, String description,
                      Disease.Severity severity, Boolean isNotifiable, Boolean isActive) {
        this.id = id;
        this.diseaseName = diseaseName;
        this.diseaseCode = diseaseCode;
        this.description = description;
        this.severity = severity;
        this.isNotifiable = isNotifiable;
        this.isActive = isActive;
    }
}
