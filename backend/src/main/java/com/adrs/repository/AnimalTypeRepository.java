package com.adrs.repository;

import com.adrs.model.AnimalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for AnimalType entity.
 * Provides CRUD operations and custom queries for animal type management.
 */
@Repository
public interface AnimalTypeRepository extends JpaRepository<AnimalType, UUID> {

    /**
     * Find an animal type by its name.
     *
     * @param typeName the name of the animal type
     * @return Optional containing the animal type if found
     */
    Optional<AnimalType> findByTypeName(String typeName);

    /**
     * Find all active animal types.
     *
     * @return list of active animal types
     */
    List<AnimalType> findByIsActiveTrue();

    /**
     * Find all animal types ordered by type name.
     *
     * @return list of all animal types sorted by name
     */
    List<AnimalType> findAllByOrderByTypeNameAsc();

    /**
     * Find animal types by active status ordered by type name.
     *
     * @param isActive the active status
     * @return list of animal types with specified status
     */
    List<AnimalType> findByIsActiveOrderByTypeNameAsc(Boolean isActive);

    /**
     * Count active animal types.
     *
     * @return count of active animal types
     */
    Long countByIsActiveTrue();

    /**
     * Count inactive animal types.
     *
     * @return count of inactive animal types
     */
    Long countByIsActiveFalse();

    /**
     * Check if an animal type exists by name (case-insensitive).
     *
     * @param typeName the name to check
     * @return true if exists, false otherwise
     */
    boolean existsByTypeNameIgnoreCase(String typeName);

    /**
     * Count animals using this animal type.
     *
     * @param animalTypeId the animal type ID
     * @return count of animals using this type
     */
    @Query("SELECT COUNT(a) FROM Animal a WHERE a.animalType.id = :animalTypeId")
    Long countAnimalsUsingAnimalType(UUID animalTypeId);
}
