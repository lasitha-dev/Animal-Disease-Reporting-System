package com.adrs.service;

import com.adrs.dto.DiseaseDTO;
import com.adrs.model.Disease;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Disease management.
 * Defines operations for creating, reading, updating, and deleting diseases.
 */
public interface DiseaseService {

    /**
     * Create a new disease.
     *
     * @param diseaseDTO the disease data
     * @return the created disease
     */
    DiseaseDTO createDisease(DiseaseDTO diseaseDTO);

    /**
     * Update an existing disease.
     *
     * @param id the disease ID
     * @param diseaseDTO the updated disease data
     * @return the updated disease
     */
    DiseaseDTO updateDisease(UUID id, DiseaseDTO diseaseDTO);

    /**
     * Get a disease by ID.
     *
     * @param id the disease ID
     * @return the disease
     */
    DiseaseDTO getDiseaseById(UUID id);

    /**
     * Get all diseases.
     *
     * @return list of all diseases
     */
    List<DiseaseDTO> getAllDiseases();

    /**
     * Get all active diseases.
     *
     * @return list of active diseases
     */
    List<DiseaseDTO> getActiveDiseases();

    /**
     * Get diseases by active status.
     *
     * @param isActive the active status
     * @return list of diseases with specified status
     */
    List<DiseaseDTO> getDiseasesByStatus(Boolean isActive);

    /**
     * Get diseases by severity level.
     *
     * @param severity the severity level
     * @return list of diseases with specified severity
     */
    List<DiseaseDTO> getDiseasesBySeverity(Disease.Severity severity);

    /**
     * Get all notifiable diseases.
     *
     * @return list of notifiable diseases
     */
    List<DiseaseDTO> getNotifiableDiseases();

    /**
     * Activate or deactivate a disease.
     * This will cascade to all disease reports using this disease.
     *
     * @param id the disease ID
     * @param isActive the new active status
     * @return the updated disease
     */
    DiseaseDTO toggleDiseaseStatus(UUID id, Boolean isActive);

    /**
     * Delete a disease.
     * Throws exception if the disease is in use.
     *
     * @param id the disease ID
     */
    void deleteDisease(UUID id);

    /**
     * Get count of disease reports using this disease.
     *
     * @param id the disease ID
     * @return count of disease reports
     */
    Long getDiseaseReportUsageCount(UUID id);

    /**
     * Check if a disease name already exists.
     *
     * @param diseaseName the disease name to check
     * @return true if exists, false otherwise
     */
    boolean diseaseExists(String diseaseName);

    /**
     * Check if a disease code already exists.
     *
     * @param diseaseCode the disease code to check
     * @return true if exists, false otherwise
     */
    boolean diseaseCodeExists(String diseaseCode);
}
