package com.adrs.repository;

import com.adrs.model.FarmType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for FarmType entity.
 * Provides CRUD operations and custom queries for farm type management.
 */
@Repository
public interface FarmTypeRepository extends JpaRepository<FarmType, UUID> {

    /**
     * Find a farm type by its name.
     *
     * @param typeName the name of the farm type
     * @return Optional containing the farm type if found
     */
    Optional<FarmType> findByTypeName(String typeName);

    /**
     * Find all active farm types.
     *
     * @return list of active farm types
     */
    List<FarmType> findByIsActiveTrue();

    /**
     * Find all farm types ordered by type name.
     *
     * @return list of all farm types sorted by name
     */
    List<FarmType> findAllByOrderByTypeNameAsc();

    /**
     * Find farm types by active status ordered by type name.
     *
     * @param isActive the active status
     * @return list of farm types with specified status
     */
    List<FarmType> findByIsActiveOrderByTypeNameAsc(Boolean isActive);

    /**
     * Count active farm types.
     *
     * @return count of active farm types
     */
    Long countByIsActiveTrue();

    /**
     * Count inactive farm types.
     *
     * @return count of inactive farm types
     */
    Long countByIsActiveFalse();

    /**
     * Check if a farm type exists by name (case-insensitive).
     *
     * @param typeName the name to check
     * @return true if exists, false otherwise
     */
    boolean existsByTypeNameIgnoreCase(String typeName);

    /**
     * Count farms using this farm type.
     *
     * @param farmTypeId the farm type ID
     * @return count of farms using this type
     */
    @Query("SELECT COUNT(f) FROM Farm f WHERE f.farmType.id = :farmTypeId")
    Long countFarmsUsingFarmType(UUID farmTypeId);
}
