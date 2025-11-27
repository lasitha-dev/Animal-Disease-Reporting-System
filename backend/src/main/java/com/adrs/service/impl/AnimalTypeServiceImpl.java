package com.adrs.service.impl;

import com.adrs.dto.AnimalTypeDTO;
import com.adrs.exception.ConfigurationInUseException;
import com.adrs.exception.ConfigurationNotFoundException;
import com.adrs.model.AnimalType;
import com.adrs.repository.AnimalTypeRepository;
import com.adrs.service.AnimalTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of AnimalTypeService.
 * Handles all business logic for animal type management including CRUD operations,
 * validation, and cascade logic for related entities.
 */
@Service
@Transactional
public class AnimalTypeServiceImpl implements AnimalTypeService {

    private static final Logger logger = LoggerFactory.getLogger(AnimalTypeServiceImpl.class);
    private static final String ENTITY_TYPE = "AnimalType";
    private static final String NOT_FOUND_MSG = "Animal type not found with ID: {}";
    
    private final AnimalTypeRepository animalTypeRepository;

    public AnimalTypeServiceImpl(AnimalTypeRepository animalTypeRepository) {
        this.animalTypeRepository = animalTypeRepository;
    }

    @Override
    public AnimalTypeDTO createAnimalType(AnimalTypeDTO animalTypeDTO) {
        logger.info("Creating new animal type: {}", animalTypeDTO.getTypeName());
        
        if (animalTypeRepository.existsByTypeNameIgnoreCase(animalTypeDTO.getTypeName())) {
            logger.error("Animal type already exists: {}", animalTypeDTO.getTypeName());
            throw new IllegalArgumentException("Animal type with name '" + animalTypeDTO.getTypeName() + "' already exists");
        }
        
        AnimalType animalType = new AnimalType();
        animalType.setTypeName(animalTypeDTO.getTypeName());
        animalType.setDescription(animalTypeDTO.getDescription());
        animalType.setIsActive(true);
        
        AnimalType savedAnimalType = animalTypeRepository.save(animalType);
        logger.info("Animal type created successfully with ID: {}", savedAnimalType.getId());
        
        return convertToDTO(savedAnimalType);
    }

    @Override
    public AnimalTypeDTO updateAnimalType(UUID id, AnimalTypeDTO animalTypeDTO) {
        logger.info("Updating animal type with ID: {}", id);
        
        AnimalType animalType = animalTypeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND_MSG, id);
                    return new ConfigurationNotFoundException(ENTITY_TYPE, id);
                });
        
        if (!animalType.getTypeName().equalsIgnoreCase(animalTypeDTO.getTypeName()) &&
            animalTypeRepository.existsByTypeNameIgnoreCase(animalTypeDTO.getTypeName())) {
            logger.error("Animal type name already exists: {}", animalTypeDTO.getTypeName());
            throw new IllegalArgumentException("Animal type with name '" + animalTypeDTO.getTypeName() + "' already exists");
        }
        
        animalType.setTypeName(animalTypeDTO.getTypeName());
        animalType.setDescription(animalTypeDTO.getDescription());
        
        AnimalType updatedAnimalType = animalTypeRepository.save(animalType);
        logger.info("Animal type updated successfully: {}", updatedAnimalType.getId());
        
        return convertToDTO(updatedAnimalType);
    }

    @Override
    @Transactional(readOnly = true)
    public AnimalTypeDTO getAnimalTypeById(UUID id) {
        logger.debug("Fetching animal type with ID: {}", id);
        
        AnimalType animalType = animalTypeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND_MSG, id);
                    return new ConfigurationNotFoundException(ENTITY_TYPE, id);
                });
        
        return convertToDTO(animalType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnimalTypeDTO> getAllAnimalTypes() {
        logger.debug("Fetching all animal types");
        
        List<AnimalType> animalTypes = animalTypeRepository.findAllByOrderByTypeNameAsc();
        return animalTypes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnimalTypeDTO> getActiveAnimalTypes() {
        logger.debug("Fetching active animal types");
        
        List<AnimalType> animalTypes = animalTypeRepository.findByIsActiveTrue();
        return animalTypes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnimalTypeDTO> getAnimalTypesByStatus(Boolean isActive) {
        logger.debug("Fetching animal types with status: {}", isActive);
        
        List<AnimalType> animalTypes = animalTypeRepository.findByIsActiveOrderByTypeNameAsc(isActive);
        return animalTypes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AnimalTypeDTO toggleAnimalTypeStatus(UUID id, Boolean isActive) {
        logger.info("Toggling animal type status for ID: {} to {}", id, isActive);
        
        AnimalType animalType = animalTypeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND_MSG, id);
                    return new ConfigurationNotFoundException(ENTITY_TYPE, id);
                });
        
        animalType.setIsActive(isActive);
        AnimalType updatedAnimalType = animalTypeRepository.save(animalType);
        
        String status = Boolean.TRUE.equals(isActive) ? "active" : "inactive";
        logger.info("Animal type status updated successfully: {} is now {}", 
                updatedAnimalType.getTypeName(), status);
        
        return convertToDTO(updatedAnimalType);
    }

    @Override
    public void deleteAnimalType(UUID id) {
        logger.info("Attempting to delete animal type with ID: {}", id);
        
        AnimalType animalType = animalTypeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND_MSG, id);
                    return new ConfigurationNotFoundException(ENTITY_TYPE, id);
                });
        
        Long usageCount = animalTypeRepository.countAnimalsUsingAnimalType(id);
        if (usageCount > 0) {
            logger.error("Cannot delete animal type '{}' - used by {} animals", 
                    animalType.getTypeName(), usageCount);
            throw new ConfigurationInUseException(ENTITY_TYPE, id, usageCount);
        }
        
        animalTypeRepository.delete(animalType);
        logger.info("Animal type deleted successfully: {}", animalType.getTypeName());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAnimalUsageCount(UUID id) {
        logger.debug("Getting usage count for animal type ID: {}", id);
        return animalTypeRepository.countAnimalsUsingAnimalType(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean animalTypeExists(String typeName) {
        logger.debug("Checking if animal type exists: {}", typeName);
        return animalTypeRepository.existsByTypeNameIgnoreCase(typeName);
    }

    /**
     * Convert AnimalType entity to DTO.
     *
     * @param animalType the entity
     * @return the DTO
     */
    private AnimalTypeDTO convertToDTO(AnimalType animalType) {
        AnimalTypeDTO dto = new AnimalTypeDTO();
        dto.setId(animalType.getId());
        dto.setTypeName(animalType.getTypeName());
        dto.setDescription(animalType.getDescription());
        dto.setIsActive(animalType.getIsActive());
        dto.setCreatedAt(animalType.getCreatedAt());
        dto.setUpdatedAt(animalType.getUpdatedAt());
        
        return dto;
    }
}
