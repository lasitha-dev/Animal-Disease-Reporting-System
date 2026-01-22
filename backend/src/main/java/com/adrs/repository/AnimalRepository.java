package com.adrs.repository;

import com.adrs.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for Animal entity.
 */
@Repository
public interface AnimalRepository extends JpaRepository<Animal, UUID> {

    /**
     * Count all animals.
     *
     * @return count of all animals
     */
    long count();

    /**
     * Delete all animals for a specific farm.
     * Used when hard-deleting a farm.
     *
     * @param farmId the farm ID
     */
    void deleteByFarmId(UUID farmId);
}
