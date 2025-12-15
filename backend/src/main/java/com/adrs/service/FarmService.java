package com.adrs.service;

import com.adrs.dto.FarmRequestDTO;
import com.adrs.dto.FarmResponseDTO;
import com.adrs.model.User;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Farm management.
 */
public interface FarmService {

    /**
     * Create a new farm.
     *
     * @param request the farm request data
     * @param createdBy the user creating the farm
     * @return the created farm response
     */
    FarmResponseDTO createFarm(FarmRequestDTO request, User createdBy);

    /**
     * Get a farm by ID.
     *
     * @param id the farm ID
     * @return the farm response
     */
    FarmResponseDTO getFarmById(UUID id);

    /**
     * Get all farms created by a specific user.
     *
     * @param user the user
     * @return list of farm responses
     */
    List<FarmResponseDTO> getFarmsByCreatedBy(User user);

    /**
     * Update an existing farm.
     *
     * @param id the farm ID
     * @param request the farm request data
     * @param updatedBy the user updating the farm
     * @return the updated farm response
     */
    FarmResponseDTO updateFarm(UUID id, FarmRequestDTO request, User updatedBy);

    /**
     * Update animal tags for a farm.
     *
     * @param farmId the farm ID
     * @param animalTags the list of animal tags
     * @param updatedBy the user updating the farm
     * @return the updated farm response
     */
    FarmResponseDTO updateAnimalTags(UUID farmId, List<FarmRequestDTO.AnimalTagDTO> animalTags, User updatedBy);

    /**
     * Get count of farms created by a user.
     *
     * @param user the user
     * @return count of farms
     */
    Long getFarmCountByUser(User user);

    /**
     * Delete a farm (soft delete).
     *
     * @param id the farm ID
     * @param deletedBy the user deleting the farm
     */
    void deleteFarm(UUID id, User deletedBy);
}
