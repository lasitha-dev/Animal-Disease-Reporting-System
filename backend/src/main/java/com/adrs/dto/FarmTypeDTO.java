package com.adrs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for FarmType entity.
 * Used for transferring farm type data between layers without exposing entity details.
 * Includes validation annotations for input validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmTypeDTO {

    private UUID id;

    @NotBlank(message = "Farm type name is required")
    @Size(min = 2, max = 50, message = "Farm type name must be between 2 and 50 characters")
    private String typeName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdByUsername;

    private String updatedByUsername;

    /**
     * Constructor for creating DTO with minimal information.
     *
     * @param typeName the name of the farm type
     * @param description the description of the farm type
     */
    public FarmTypeDTO(String typeName, String description) {
        this.typeName = typeName;
        this.description = description;
        this.isActive = true;
    }

    /**
     * Constructor for creating DTO with all basic fields.
     *
     * @param id the unique identifier
     * @param typeName the name of the farm type
     * @param description the description
     * @param isActive the active status
     */
    public FarmTypeDTO(UUID id, String typeName, String description, Boolean isActive) {
        this.id = id;
        this.typeName = typeName;
        this.description = description;
        this.isActive = isActive;
    }
}
