package com.adrs.service;

import com.adrs.dto.FarmTypeDTO;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for FarmType management.
 * Defines operations for creating, reading, updating, and deleting farm types.
 */
public interface FarmTypeService {

    /**
     * Create a new farm type.
     *
     * @param farmTypeDTO the farm type data
     * @return the created farm type
     */
    FarmTypeDTO createFarmType(FarmTypeDTO farmTypeDTO);

    /**
     * Update an existing farm type.
     *
     * @param id the farm type ID
     * @param farmTypeDTO the updated farm type data
     * @return the updated farm type
     */
    FarmTypeDTO updateFarmType(UUID id, FarmTypeDTO farmTypeDTO);

    /**
     * Get a farm type by ID.
     *
     * @param id the farm type ID
     * @return the farm type
     */
    FarmTypeDTO getFarmTypeById(UUID id);

    /**
     * Get all farm types.
     *
     * @return list of all farm types
     */
    List<FarmTypeDTO> getAllFarmTypes();

    /**
     * Get all active farm types.
     *
     * @return list of active farm types
     */
    List<FarmTypeDTO> getActiveFarmTypes();

    /**
     * Get farm types by active status.
     *
     * @param isActive the active status
     * @return list of farm types with specified status
     */
    List<FarmTypeDTO> getFarmTypesByStatus(Boolean isActive);

    /**
     * Activate or deactivate a farm type.
     * This will cascade to all farms using this type.
     *
     * @param id the farm type ID
     * @param isActive the new active status
     * @return the updated farm type
     */
    FarmTypeDTO toggleFarmTypeStatus(UUID id, Boolean isActive);

    /**
     * Delete a farm type.
     * Throws exception if the farm type is in use.
     *
     * @param id the farm type ID
     */
    void deleteFarmType(UUID id);

    /**
     * Get count of farms using this farm type.
     *
     * @param id the farm type ID
     * @return count of farms
     */
    Long getFarmUsageCount(UUID id);

    /**
     * Check if a farm type name already exists.
     *
     * @param typeName the type name to check
     * @return true if exists, false otherwise
     */
    boolean farmTypeExists(String typeName);
}
