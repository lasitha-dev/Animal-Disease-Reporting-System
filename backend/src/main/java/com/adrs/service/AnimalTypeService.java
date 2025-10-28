package com.adrs.service;

import com.adrs.dto.AnimalTypeDTO;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for AnimalType management.
 * Defines operations for creating, reading, updating, and deleting animal types.
 */
public interface AnimalTypeService {

    /**
     * Create a new animal type.
     *
     * @param animalTypeDTO the animal type data
     * @return the created animal type
     */
    AnimalTypeDTO createAnimalType(AnimalTypeDTO animalTypeDTO);

    /**
     * Update an existing animal type.
     *
     * @param id the animal type ID
     * @param animalTypeDTO the updated animal type data
     * @return the updated animal type
     */
    AnimalTypeDTO updateAnimalType(UUID id, AnimalTypeDTO animalTypeDTO);

    /**
     * Get an animal type by ID.
     *
     * @param id the animal type ID
     * @return the animal type
     */
    AnimalTypeDTO getAnimalTypeById(UUID id);

    /**
     * Get all animal types.
     *
     * @return list of all animal types
     */
    List<AnimalTypeDTO> getAllAnimalTypes();

    /**
     * Get all active animal types.
     *
     * @return list of active animal types
     */
    List<AnimalTypeDTO> getActiveAnimalTypes();

    /**
     * Get animal types by active status.
     *
     * @param isActive the active status
     * @return list of animal types with specified status
     */
    List<AnimalTypeDTO> getAnimalTypesByStatus(Boolean isActive);

    /**
     * Activate or deactivate an animal type.
     * This will cascade to all animals using this type.
     *
     * @param id the animal type ID
     * @param isActive the new active status
     * @return the updated animal type
     */
    AnimalTypeDTO toggleAnimalTypeStatus(UUID id, Boolean isActive);

    /**
     * Delete an animal type.
     * Throws exception if the animal type is in use.
     *
     * @param id the animal type ID
     */
    void deleteAnimalType(UUID id);

    /**
     * Get count of animals using this animal type.
     *
     * @param id the animal type ID
     * @return count of animals
     */
    Long getAnimalUsageCount(UUID id);

    /**
     * Check if an animal type name already exists.
     *
     * @param typeName the type name to check
     * @return true if exists, false otherwise
     */
    boolean animalTypeExists(String typeName);
}
