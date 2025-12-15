package com.adrs.service.impl;

import com.adrs.dto.FarmRequestDTO;
import com.adrs.dto.FarmResponseDTO;
import com.adrs.exception.ResourceNotFoundException;
import com.adrs.model.*;
import com.adrs.repository.AnimalTypeRepository;
import com.adrs.repository.FarmAnimalRepository;
import com.adrs.repository.FarmRepository;
import com.adrs.repository.FarmTypeRepository;
import com.adrs.service.FarmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of FarmService.
 */
@Service
@Transactional
public class FarmServiceImpl implements FarmService {

    private static final Logger logger = LoggerFactory.getLogger(FarmServiceImpl.class);

    private final FarmRepository farmRepository;
    private final FarmTypeRepository farmTypeRepository;
    private final AnimalTypeRepository animalTypeRepository;
    private final FarmAnimalRepository farmAnimalRepository;

    public FarmServiceImpl(FarmRepository farmRepository,
                          FarmTypeRepository farmTypeRepository,
                          AnimalTypeRepository animalTypeRepository,
                          FarmAnimalRepository farmAnimalRepository) {
        this.farmRepository = farmRepository;
        this.farmTypeRepository = farmTypeRepository;
        this.animalTypeRepository = animalTypeRepository;
        this.farmAnimalRepository = farmAnimalRepository;
    }

    @Override
    public FarmResponseDTO createFarm(FarmRequestDTO request, User createdBy) {
        logger.info("Creating farm: {} by user: {}", request.getFarmName(), createdBy.getUsername());

        // Validate and get farm type
        FarmType farmType = farmTypeRepository.findById(request.getFarmTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm type not found: " + request.getFarmTypeId()));

        // Create farm entity
        Farm farm = new Farm();
        farm.setFarmName(request.getFarmName());
        farm.setFarmType(farmType);
        farm.setDescription(request.getDescription() != null ? request.getDescription() : farmType.getDescription());
        farm.setOwnerName(request.getOwnerName());
        farm.setOwnerContact(request.getOwnerContact());
        farm.setAddress(request.getAddress());
        farm.setProvince(Province.valueOf(request.getProvince()));
        farm.setDistrict(District.valueOf(request.getDistrict()));
        farm.setGpsLatitude(request.getGpsLatitude());
        farm.setGpsLongitude(request.getGpsLongitude());
        farm.setCreatedBy(createdBy);
        farm.setIsActive(true);

        // Save farm first to get ID
        farm = farmRepository.save(farm);

        // Add animal tags if provided
        if (request.getAnimalTags() != null && !request.getAnimalTags().isEmpty()) {
            addAnimalTagsToFarm(farm, request.getAnimalTags());
            farm = farmRepository.save(farm);
        }

        logger.info("Farm created successfully with ID: {}", farm.getId());
        return FarmResponseDTO.fromEntity(farm);
    }

    @Override
    @Transactional(readOnly = true)
    public FarmResponseDTO getFarmById(UUID id) {
        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found: " + id));
        return FarmResponseDTO.fromEntity(farm);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmResponseDTO> getFarmsByCreatedBy(User user) {
        List<Farm> farms = farmRepository.findByCreatedByAndIsActiveTrue(user);
        return farms.stream()
                .map(FarmResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public FarmResponseDTO updateFarm(UUID id, FarmRequestDTO request, User updatedBy) {
        logger.info("Updating farm: {} by user: {}", id, updatedBy.getUsername());

        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found: " + id));

        // Update basic fields
        farm.setFarmName(request.getFarmName());
        farm.setDescription(request.getDescription());
        farm.setOwnerName(request.getOwnerName());
        farm.setOwnerContact(request.getOwnerContact());
        farm.setAddress(request.getAddress());
        farm.setProvince(Province.valueOf(request.getProvince()));
        farm.setDistrict(District.valueOf(request.getDistrict()));
        farm.setGpsLatitude(request.getGpsLatitude());
        farm.setGpsLongitude(request.getGpsLongitude());
        farm.setUpdatedBy(updatedBy);

        // Update farm type if changed
        if (!farm.getFarmType().getId().equals(request.getFarmTypeId())) {
            FarmType farmType = farmTypeRepository.findById(request.getFarmTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Farm type not found: " + request.getFarmTypeId()));
            farm.setFarmType(farmType);
        }

        // Update animal tags
        if (request.getAnimalTags() != null) {
            updateAnimalTagsInternal(farm, request.getAnimalTags());
        }

        farm = farmRepository.save(farm);
        logger.info("Farm updated successfully: {}", id);
        return FarmResponseDTO.fromEntity(farm);
    }

    @Override
    public FarmResponseDTO updateAnimalTags(UUID farmId, List<FarmRequestDTO.AnimalTagDTO> animalTags, User updatedBy) {
        logger.info("Updating animal tags for farm: {} by user: {}", farmId, updatedBy.getUsername());

        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found: " + farmId));

        updateAnimalTagsInternal(farm, animalTags);
        farm.setUpdatedBy(updatedBy);
        farm = farmRepository.save(farm);

        logger.info("Animal tags updated for farm: {}", farmId);
        return FarmResponseDTO.fromEntity(farm);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getFarmCountByUser(User user) {
        return farmRepository.countByCreatedBy(user);
    }

    /**
     * Adds animal tags to a farm.
     */
    private void addAnimalTagsToFarm(Farm farm, List<FarmRequestDTO.AnimalTagDTO> animalTags) {
        int totalAnimals = 0;
        List<FarmAnimal> farmAnimals = new ArrayList<>();

        for (FarmRequestDTO.AnimalTagDTO tagDTO : animalTags) {
            if (tagDTO.getCount() != null && tagDTO.getCount() > 0) {
                AnimalType animalType = animalTypeRepository.findById(tagDTO.getAnimalTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Animal type not found: " + tagDTO.getAnimalTypeId()));

                FarmAnimal farmAnimal = new FarmAnimal(farm, animalType, tagDTO.getCount());
                farmAnimals.add(farmAnimal);
                totalAnimals += tagDTO.getCount();
            }
        }

        farm.setFarmAnimals(farmAnimals);
        farm.setTotalAnimals(totalAnimals);
    }

    /**
     * Updates animal tags for a farm, replacing existing ones.
     */
    private void updateAnimalTagsInternal(Farm farm, List<FarmRequestDTO.AnimalTagDTO> animalTags) {
        // Clear existing tags
        farm.getFarmAnimals().clear();
        
        // Add new tags
        addAnimalTagsToFarm(farm, animalTags);
    }
}
