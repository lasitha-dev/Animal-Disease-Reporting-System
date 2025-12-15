package com.adrs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating or updating a farm.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmRequestDTO {

    @NotBlank(message = "Farm name is required")
    @Size(max = 100, message = "Farm name must not exceed 100 characters")
    private String farmName;

    @NotNull(message = "Farm type is required")
    private UUID farmTypeId;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 100, message = "Owner name must not exceed 100 characters")
    private String ownerName;

    @Pattern(regexp = "^(\\+94[0-9]{9})?$", message = "Contact must be a valid Sri Lankan number (+94XXXXXXXXX)")
    private String ownerContact;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Province is required")
    private String province;

    @NotNull(message = "District is required")
    private String district;

    private BigDecimal gpsLatitude;
    private BigDecimal gpsLongitude;

    /**
     * List of animal types with their counts for this farm.
     * This is optional and can be added later.
     */
    private List<AnimalTagDTO> animalTags = new ArrayList<>();

    /**
     * DTO for animal type tag with count.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnimalTagDTO {
        @NotNull(message = "Animal type ID is required")
        private UUID animalTypeId;

        @NotNull(message = "Count is required")
        private Integer count;
    }
}
