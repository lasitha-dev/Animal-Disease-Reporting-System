package com.adrs.service.impl;

import com.adrs.dto.FarmTypeDTO;
import com.adrs.exception.ConfigurationInUseException;
import com.adrs.exception.ConfigurationNotFoundException;
import com.adrs.model.FarmType;
import com.adrs.repository.FarmTypeRepository;
import com.adrs.service.FarmTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of FarmTypeService.
 * Handles all business logic for farm type management including CRUD operations,
 * validation, and cascade logic for related entities.
 */
@Service
@Transactional
public class FarmTypeServiceImpl implements FarmTypeService {

    private static final Logger logger = LoggerFactory.getLogger(FarmTypeServiceImpl.class);
    
    private final FarmTypeRepository farmTypeRepository;

    public FarmTypeServiceImpl(FarmTypeRepository farmTypeRepository) {
        this.farmTypeRepository = farmTypeRepository;
    }

    @Override
    public FarmTypeDTO createFarmType(FarmTypeDTO farmTypeDTO) {
        logger.info("Creating new farm type: {}", farmTypeDTO.getTypeName());
        
        // Check if farm type already exists
        if (farmTypeRepository.existsByTypeNameIgnoreCase(farmTypeDTO.getTypeName())) {
            logger.error("Farm type already exists: {}", farmTypeDTO.getTypeName());
            throw new IllegalArgumentException("Farm type with name '" + farmTypeDTO.getTypeName() + "' already exists");
        }
        
        FarmType farmType = new FarmType();
        farmType.setTypeName(farmTypeDTO.getTypeName());
        farmType.setDescription(farmTypeDTO.getDescription());
        farmType.setIsActive(true);
        
        FarmType savedFarmType = farmTypeRepository.save(farmType);
        logger.info("Farm type created successfully with ID: {}", savedFarmType.getId());
        
        return convertToDTO(savedFarmType);
    }

    @Override
    public FarmTypeDTO updateFarmType(UUID id, FarmTypeDTO farmTypeDTO) {
        logger.info("Updating farm type with ID: {}", id);
        
        FarmType farmType = farmTypeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Farm type not found with ID: {}", id);
                    return new ConfigurationNotFoundException("FarmType", id);
                });
        
        // Check if new name conflicts with existing farm type
        if (!farmType.getTypeName().equalsIgnoreCase(farmTypeDTO.getTypeName()) &&
            farmTypeRepository.existsByTypeNameIgnoreCase(farmTypeDTO.getTypeName())) {
            logger.error("Farm type name already exists: {}", farmTypeDTO.getTypeName());
            throw new IllegalArgumentException("Farm type with name '" + farmTypeDTO.getTypeName() + "' already exists");
        }
        
        farmType.setTypeName(farmTypeDTO.getTypeName());
        farmType.setDescription(farmTypeDTO.getDescription());
        
        FarmType updatedFarmType = farmTypeRepository.save(farmType);
        logger.info("Farm type updated successfully: {}", updatedFarmType.getId());
        
        return convertToDTO(updatedFarmType);
    }

    @Override
    @Transactional(readOnly = true)
    public FarmTypeDTO getFarmTypeById(UUID id) {
        logger.debug("Fetching farm type with ID: {}", id);
        
        FarmType farmType = farmTypeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Farm type not found with ID: {}", id);
                    return new ConfigurationNotFoundException("FarmType", id);
                });
        
        return convertToDTO(farmType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmTypeDTO> getAllFarmTypes() {
        logger.debug("Fetching all farm types");
        
        List<FarmType> farmTypes = farmTypeRepository.findAllByOrderByTypeNameAsc();
        return farmTypes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmTypeDTO> getActiveFarmTypes() {
        logger.debug("Fetching active farm types");
        
        List<FarmType> farmTypes = farmTypeRepository.findByIsActiveTrue();
        return farmTypes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmTypeDTO> getFarmTypesByStatus(Boolean isActive) {
        logger.debug("Fetching farm types with status: {}", isActive);
        
        List<FarmType> farmTypes = farmTypeRepository.findByIsActiveOrderByTypeNameAsc(isActive);
        return farmTypes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FarmTypeDTO toggleFarmTypeStatus(UUID id, Boolean isActive) {
        logger.info("Toggling farm type status for ID: {} to {}", id, isActive);
        
        FarmType farmType = farmTypeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Farm type not found with ID: {}", id);
                    return new ConfigurationNotFoundException("FarmType", id);
                });
        
        farmType.setIsActive(isActive);
        FarmType updatedFarmType = farmTypeRepository.save(farmType);
        
        logger.info("Farm type status updated successfully: {} is now {}", 
                updatedFarmType.getTypeName(), isActive ? "active" : "inactive");
        
        return convertToDTO(updatedFarmType);
    }

    @Override
    public void deleteFarmType(UUID id) {
        logger.info("Attempting to delete farm type with ID: {}", id);
        
        FarmType farmType = farmTypeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Farm type not found with ID: {}", id);
                    return new ConfigurationNotFoundException("FarmType", id);
                });
        
        // Check if farm type is in use
        Long usageCount = farmTypeRepository.countFarmsUsingFarmType(id);
        if (usageCount > 0) {
            logger.error("Cannot delete farm type '{}' - used by {} farms", 
                    farmType.getTypeName(), usageCount);
            throw new ConfigurationInUseException("FarmType", id, usageCount);
        }
        
        farmTypeRepository.delete(farmType);
        logger.info("Farm type deleted successfully: {}", farmType.getTypeName());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getFarmUsageCount(UUID id) {
        logger.debug("Getting usage count for farm type ID: {}", id);
        return farmTypeRepository.countFarmsUsingFarmType(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean farmTypeExists(String typeName) {
        logger.debug("Checking if farm type exists: {}", typeName);
        return farmTypeRepository.existsByTypeNameIgnoreCase(typeName);
    }

    /**
     * Convert FarmType entity to DTO.
     *
     * @param farmType the entity
     * @return the DTO
     */
    private FarmTypeDTO convertToDTO(FarmType farmType) {
        FarmTypeDTO dto = new FarmTypeDTO();
        dto.setId(farmType.getId());
        dto.setTypeName(farmType.getTypeName());
        dto.setDescription(farmType.getDescription());
        dto.setIsActive(farmType.getIsActive());
        dto.setCreatedAt(farmType.getCreatedAt());
        dto.setUpdatedAt(farmType.getUpdatedAt());
        
        if (farmType.getCreatedBy() != null) {
            dto.setCreatedByUsername(farmType.getCreatedBy().getUsername());
        }
        if (farmType.getUpdatedBy() != null) {
            dto.setUpdatedByUsername(farmType.getUpdatedBy().getUsername());
        }
        
        return dto;
    }
}
