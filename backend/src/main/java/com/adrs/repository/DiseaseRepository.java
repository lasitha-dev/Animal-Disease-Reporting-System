package com.adrs.repository;

import com.adrs.model.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Disease entity.
 * Provides CRUD operations and custom queries for disease management.
 */
@Repository
public interface DiseaseRepository extends JpaRepository<Disease, UUID> {

    /**
     * Find a disease by its name.
     *
     * @param diseaseName the name of the disease
     * @return Optional containing the disease if found
     */
    Optional<Disease> findByDiseaseName(String diseaseName);

    /**
     * Find a disease by its code.
     *
     * @param diseaseCode the disease code
     * @return Optional containing the disease if found
     */
    Optional<Disease> findByDiseaseCode(String diseaseCode);

    /**
     * Find all active diseases.
     *
     * @return list of active diseases
     */
    List<Disease> findByIsActiveTrue();

    /**
     * Find all diseases ordered by disease name.
     *
     * @return list of all diseases sorted by name
     */
    List<Disease> findAllByOrderByDiseaseNameAsc();

    /**
     * Find diseases by active status ordered by disease name.
     *
     * @param isActive the active status
     * @return list of diseases with specified status
     */
    List<Disease> findByIsActiveOrderByDiseaseNameAsc(Boolean isActive);

    /**
     * Find diseases by severity level.
     *
     * @param severity the severity level
     * @return list of diseases with specified severity
     */
    List<Disease> findBySeverity(Disease.Severity severity);

    /**
     * Find all notifiable diseases.
     *
     * @return list of notifiable diseases
     */
    List<Disease> findByIsNotifiableTrue();

    /**
     * Find active notifiable diseases.
     *
     * @return list of active notifiable diseases
     */
    List<Disease> findByIsActiveTrueAndIsNotifiableTrue();

    /**
     * Count active diseases.
     *
     * @return count of active diseases
     */
    Long countByIsActiveTrue();

    /**
     * Count inactive diseases.
     *
     * @return count of inactive diseases
     */
    Long countByIsActiveFalse();

    /**
     * Count notifiable diseases.
     *
     * @return count of notifiable diseases
     */
    Long countByIsNotifiableTrue();

    /**
     * Check if a disease exists by name (case-insensitive).
     *
     * @param diseaseName the name to check
     * @return true if exists, false otherwise
     */
    boolean existsByDiseaseNameIgnoreCase(String diseaseName);

    /**
     * Check if a disease code exists (case-insensitive).
     *
     * @param diseaseCode the code to check
     * @return true if exists, false otherwise
     */
    boolean existsByDiseaseCodeIgnoreCase(String diseaseCode);

    /**
     * Count disease reports using this disease.
     *
     * @param diseaseId the disease ID
     * @return count of disease reports using this disease
     */
    @Query("SELECT COUNT(dr) FROM DiseaseReport dr WHERE dr.disease.id = :diseaseId")
    Long countDiseaseReportsUsingDisease(UUID diseaseId);
}
