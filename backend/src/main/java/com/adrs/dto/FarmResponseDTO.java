package com.adrs.dto;

import com.adrs.model.Farm;
import com.adrs.model.FarmAnimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Response DTO for farm data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmResponseDTO {

    private UUID id;
    private String farmName;
    private UUID farmTypeId;
    private String farmTypeName;
    private String description;
    private String ownerName;
    private String ownerContact;
    private String address;
    private String province;
    private String provinceDisplayName;
    private String district;
    private String districtDisplayName;
    private BigDecimal gpsLatitude;
    private BigDecimal gpsLongitude;
    private Integer totalAnimals;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdByUsername;
    private List<AnimalTagResponseDTO> animalTags = new ArrayList<>();

    /**
     * DTO for animal type tag response.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnimalTagResponseDTO {
        private UUID id;
        private UUID animalTypeId;
        private String animalTypeName;
        private Integer count;
    }

    /**
     * Creates a FarmResponseDTO from a Farm entity.
     *
     * @param farm the farm entity
     * @return the response DTO
     */
    public static FarmResponseDTO fromEntity(Farm farm) {
        FarmResponseDTO dto = new FarmResponseDTO();
        dto.setId(farm.getId());
        dto.setFarmName(farm.getFarmName());
        dto.setDescription(farm.getDescription());
        dto.setOwnerName(farm.getOwnerName());
        dto.setOwnerContact(farm.getOwnerContact());
        dto.setAddress(farm.getAddress());
        dto.setGpsLatitude(farm.getGpsLatitude());
        dto.setGpsLongitude(farm.getGpsLongitude());
        dto.setTotalAnimals(farm.getTotalAnimals());
        dto.setIsActive(farm.getIsActive());
        dto.setCreatedAt(farm.getCreatedAt());
        dto.setUpdatedAt(farm.getUpdatedAt());

        // Farm type
        if (farm.getFarmType() != null) {
            dto.setFarmTypeId(farm.getFarmType().getId());
            dto.setFarmTypeName(farm.getFarmType().getTypeName());
        }

        // Province and district
        if (farm.getProvince() != null) {
            dto.setProvince(farm.getProvince().name());
            dto.setProvinceDisplayName(farm.getProvince().getDisplayName());
        }
        if (farm.getDistrict() != null) {
            dto.setDistrict(farm.getDistrict().name());
            dto.setDistrictDisplayName(farm.getDistrict().getDisplayName());
        }

        // Created by username
        if (farm.getCreatedBy() != null) {
            dto.setCreatedByUsername(farm.getCreatedBy().getUsername());
        }

        // Animal tags
        if (farm.getFarmAnimals() != null && !farm.getFarmAnimals().isEmpty()) {
            dto.setAnimalTags(farm.getFarmAnimals().stream()
                    .map(FarmResponseDTO::mapAnimalTag)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * Maps a FarmAnimal to AnimalTagResponseDTO.
     */
    private static AnimalTagResponseDTO mapAnimalTag(FarmAnimal farmAnimal) {
        AnimalTagResponseDTO tagDTO = new AnimalTagResponseDTO();
        tagDTO.setId(farmAnimal.getId());
        tagDTO.setCount(farmAnimal.getCount());
        if (farmAnimal.getAnimalType() != null) {
            tagDTO.setAnimalTypeId(farmAnimal.getAnimalType().getId());
            tagDTO.setAnimalTypeName(farmAnimal.getAnimalType().getTypeName());
        }
        return tagDTO;
    }
}
