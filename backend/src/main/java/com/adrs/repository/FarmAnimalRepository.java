package com.adrs.repository;

import com.adrs.model.Farm;
import com.adrs.model.FarmAnimal;
import com.adrs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
    @Modifying
    void deleteByFarmId(UUID farmId);

    /**
     * Delete all animal entries for a specific farm.
     *
     * @param farm the farm entity
     */
    @Modifying
    void deleteByFarm(Farm farm);

    /**
     * Get the total count of all animals across all farms.
     *
     * @return total animal count (sum of all count values)
     */
    @Query("SELECT COALESCE(SUM(fa.count), 0) FROM FarmAnimal fa")
    Long sumTotalAnimals();

    /**
     * Get the total count of animals for farms created by a specific user.
     *
     * @param user the user who created the farms
     * @return total animal count for user's farms
     */
    @Query("SELECT COALESCE(SUM(fa.count), 0) FROM FarmAnimal fa WHERE fa.farm.createdBy = :user")
    Long sumTotalAnimalsByFarmCreatedBy(User user);
}
