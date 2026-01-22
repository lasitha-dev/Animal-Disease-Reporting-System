package com.adrs.service.impl;

import com.adrs.dto.FarmRequestDTO;
import com.adrs.dto.FarmResponseDTO;
import com.adrs.exception.ResourceNotFoundException;
import com.adrs.model.*;
import com.adrs.repository.AnimalRepository;
import com.adrs.repository.AnimalTypeRepository;
import com.adrs.repository.DiseaseReportRepository;
import com.adrs.repository.FarmAnimalRepository;
import com.adrs.repository.FarmRepository;
import com.adrs.repository.FarmTypeRepository;
import com.adrs.service.FarmService;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
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
    private final AnimalRepository animalRepository;
    private final DiseaseReportRepository diseaseReportRepository;
    private final EntityManager entityManager;

    public FarmServiceImpl(FarmRepository farmRepository,
                          FarmTypeRepository farmTypeRepository,
                          AnimalTypeRepository animalTypeRepository,
                          FarmAnimalRepository farmAnimalRepository,
                          AnimalRepository animalRepository,
                          DiseaseReportRepository diseaseReportRepository,
                          EntityManager entityManager) {
        this.farmRepository = farmRepository;
        this.farmTypeRepository = farmTypeRepository;
        this.animalTypeRepository = animalTypeRepository;
        this.farmAnimalRepository = farmAnimalRepository;
        this.animalRepository = animalRepository;
        this.diseaseReportRepository = diseaseReportRepository;
        this.entityManager = entityManager;
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
    @Transactional(readOnly = true)
    public List<FarmResponseDTO> getFarmsNotCreatedBy(User user) {
        List<Farm> farms = farmRepository.findByCreatedByNotAndIsActiveTrue(user);
        return farms.stream()
                .map(FarmResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public FarmResponseDTO updateFarm(UUID id, FarmRequestDTO request, User updatedBy) {
        logger.info("Updating farm: {} by user: {}", id, updatedBy.getUsername());

        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found: " + id));

        // Verify ownership - only the vet who created the farm can update it
        if (farm.getCreatedBy() == null || !farm.getCreatedBy().getId().equals(updatedBy.getId())) {
            logger.warn("Access denied: User {} attempted to update farm {} created by {}", 
                    updatedBy.getUsername(), id, 
                    farm.getCreatedBy() != null ? farm.getCreatedBy().getUsername() : "unknown");
            throw new AccessDeniedException("You can only update farms that you registered");
        }

        // Update basic fields
        farm.setFarmName(request.getFarmName());
        farm.setDescription(request.getDescription());
        farm.setOwnerName(request.getOwnerName());
        
        // Handle empty ownerContact
        String ownerContact = request.getOwnerContact();
        farm.setOwnerContact(ownerContact != null && !ownerContact.trim().isEmpty() ? ownerContact : null);
        
        farm.setAddress(request.getAddress());
        farm.setProvince(Province.valueOf(request.getProvince()));
        farm.setDistrict(District.valueOf(request.getDistrict()));
        farm.setGpsLatitude(request.getGpsLatitude());
        farm.setGpsLongitude(request.getGpsLongitude());
        farm.setUpdatedBy(updatedBy);

        // Update farm type if changed (with null safety)
        UUID currentFarmTypeId = farm.getFarmType() != null ? farm.getFarmType().getId() : null;
        UUID requestFarmTypeId = request.getFarmTypeId();
        
        if (requestFarmTypeId != null && !requestFarmTypeId.equals(currentFarmTypeId)) {
            FarmType farmType = farmTypeRepository.findById(requestFarmTypeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Farm type not found: " + requestFarmTypeId));
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
     * Note: This method adds to the existing collection, it does NOT replace it.
     * The caller should clear the collection first if needed.
     */
    private void addAnimalTagsToFarm(Farm farm, List<FarmRequestDTO.AnimalTagDTO> animalTags) {
        int totalAnimals = 0;

        for (FarmRequestDTO.AnimalTagDTO tagDTO : animalTags) {
            if (tagDTO.getCount() != null && tagDTO.getCount() > 0) {
                AnimalType animalType = animalTypeRepository.findById(tagDTO.getAnimalTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Animal type not found: " + tagDTO.getAnimalTypeId()));

                FarmAnimal farmAnimal = new FarmAnimal(farm, animalType, tagDTO.getCount());
                farm.getFarmAnimals().add(farmAnimal); // Add to existing collection, don't replace
                totalAnimals += tagDTO.getCount();
            }
        }

        farm.setTotalAnimals(totalAnimals);
    }

    /**
     * Updates animal tags for a farm, replacing existing ones.
     * Uses collection.clear() which triggers orphanRemoval automatically.
     * We flush after clear to ensure DELETE statements execute before INSERTs.
     */
    private void updateAnimalTagsInternal(Farm farm, List<FarmRequestDTO.AnimalTagDTO> animalTags) {
        // Clear existing - orphanRemoval will delete them
        farm.getFarmAnimals().clear();
        
        // Flush to execute DELETEs before we INSERT new records
        // This prevents unique constraint violations on (farm_id, animal_type_id)
        entityManager.flush();
        
        // Add new tags to the existing (now empty) collection
        if (animalTags != null && !animalTags.isEmpty()) {
            addAnimalTagsToFarm(farm, animalTags);
        } else {
            farm.setTotalAnimals(0);
        }
    }

    @Override
    public void deleteFarm(UUID id, User deletedBy) {
        logger.info("Deleting farm: {} by user: {}", id, deletedBy.getUsername());

        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found: " + id));

        // Verify ownership - only the vet who created the farm can delete it
        if (farm.getCreatedBy() == null || !farm.getCreatedBy().getId().equals(deletedBy.getId())) {
            logger.warn("Access denied: User {} attempted to delete farm {} created by {}", 
                    deletedBy.getUsername(), id, 
                    farm.getCreatedBy() != null ? farm.getCreatedBy().getUsername() : "unknown");
            throw new AccessDeniedException("You can only delete farms that you registered");
        }

        // Hard delete - remove the farm and all related records
        // Note: FarmAnimals are cascade-deleted via orphanRemoval=true
        // Disease reports and animals need to be deleted first due to FK constraints
        diseaseReportRepository.deleteByFarmId(id);
        animalRepository.deleteByFarmId(id);
        farmRepository.delete(farm);

        logger.info("Farm hard deleted successfully: {}", id);
    }
}
