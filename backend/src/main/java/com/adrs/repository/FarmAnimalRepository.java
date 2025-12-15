package com.adrs.repository;

import com.adrs.model.FarmAnimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for FarmAnimal entity.
 */
@Repository
public interface FarmAnimalRepository extends JpaRepository<FarmAnimal, UUID> {

    /**
     * Find all animal entries for a specific farm.
     *
     * @param farmId the farm ID
     * @return list of farm animals
     */
    List<FarmAnimal> findByFarmId(UUID farmId);

    /**
     * Delete all animal entries for a specific farm.
     *
     * @param farmId the farm ID
     */
    void deleteByFarmId(UUID farmId);
}
