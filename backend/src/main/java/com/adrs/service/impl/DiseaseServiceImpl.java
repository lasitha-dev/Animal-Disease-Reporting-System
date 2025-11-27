package com.adrs.service.impl;

import com.adrs.dto.DiseaseDTO;
import com.adrs.exception.ConfigurationInUseException;
import com.adrs.exception.ConfigurationNotFoundException;
import com.adrs.model.Disease;
import com.adrs.repository.DiseaseRepository;
import com.adrs.service.DiseaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of DiseaseService.
 * Handles all business logic for disease management including CRUD operations,
 * validation, and cascade logic for related entities.
 */
@Service
@Transactional
public class DiseaseServiceImpl implements DiseaseService {

    private static final Logger logger = LoggerFactory.getLogger(DiseaseServiceImpl.class);
    private static final String ENTITY_TYPE = "Disease";
    private static final String NOT_FOUND_MSG = "Disease not found with ID: {}";
    
    private final DiseaseRepository diseaseRepository;

    public DiseaseServiceImpl(DiseaseRepository diseaseRepository) {
        this.diseaseRepository = diseaseRepository;
    }

    @Override
    public DiseaseDTO createDisease(DiseaseDTO diseaseDTO) {
        logger.info("Creating new disease: {}", diseaseDTO.getDiseaseName());
        
        if (diseaseRepository.existsByDiseaseNameIgnoreCase(diseaseDTO.getDiseaseName())) {
            logger.error("Disease already exists: {}", diseaseDTO.getDiseaseName());
            throw new IllegalArgumentException("Disease with name '" + diseaseDTO.getDiseaseName() + "' already exists");
        }
        
        if (diseaseDTO.getDiseaseCode() != null && 
            diseaseRepository.existsByDiseaseCodeIgnoreCase(diseaseDTO.getDiseaseCode())) {
            logger.error("Disease code already exists: {}", diseaseDTO.getDiseaseCode());
            throw new IllegalArgumentException("Disease with code '" + diseaseDTO.getDiseaseCode() + "' already exists");
        }
        
        Disease disease = new Disease();
        disease.setDiseaseName(diseaseDTO.getDiseaseName());
        disease.setDiseaseCode(diseaseDTO.getDiseaseCode());
        disease.setDescription(diseaseDTO.getDescription());
        disease.setAffectedAnimalTypes(diseaseDTO.getAffectedAnimalTypes());
        disease.setSeverity(diseaseDTO.getSeverity());
        disease.setIsNotifiable(diseaseDTO.getIsNotifiable() != null ? diseaseDTO.getIsNotifiable() : false);
        disease.setIsActive(true);
        
        Disease savedDisease = diseaseRepository.save(disease);
        logger.info("Disease created successfully with ID: {}", savedDisease.getId());
        
        return convertToDTO(savedDisease);
    }

    @Override
    public DiseaseDTO updateDisease(UUID id, DiseaseDTO diseaseDTO) {
        logger.info("Updating disease with ID: {}", id);
        
        Disease disease = diseaseRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND_MSG, id);
                    return new ConfigurationNotFoundException(ENTITY_TYPE, id);
                });
        
        if (!disease.getDiseaseName().equalsIgnoreCase(diseaseDTO.getDiseaseName()) &&
            diseaseRepository.existsByDiseaseNameIgnoreCase(diseaseDTO.getDiseaseName())) {
            logger.error("Disease name already exists: {}", diseaseDTO.getDiseaseName());
            throw new IllegalArgumentException("Disease with name '" + diseaseDTO.getDiseaseName() + "' already exists");
        }
        
        if (diseaseDTO.getDiseaseCode() != null &&
            (disease.getDiseaseCode() == null || !disease.getDiseaseCode().equalsIgnoreCase(diseaseDTO.getDiseaseCode())) &&
            diseaseRepository.existsByDiseaseCodeIgnoreCase(diseaseDTO.getDiseaseCode())) {
            logger.error("Disease code already exists: {}", diseaseDTO.getDiseaseCode());
            throw new IllegalArgumentException("Disease with code '" + diseaseDTO.getDiseaseCode() + "' already exists");
        }
        
        disease.setDiseaseName(diseaseDTO.getDiseaseName());
        disease.setDiseaseCode(diseaseDTO.getDiseaseCode());
        disease.setDescription(diseaseDTO.getDescription());
        disease.setAffectedAnimalTypes(diseaseDTO.getAffectedAnimalTypes());
        disease.setSeverity(diseaseDTO.getSeverity());
        disease.setIsNotifiable(diseaseDTO.getIsNotifiable());
        
        Disease updatedDisease = diseaseRepository.save(disease);
        logger.info("Disease updated successfully: {}", updatedDisease.getId());
        
        return convertToDTO(updatedDisease);
    }

    @Override
    @Transactional(readOnly = true)
    public DiseaseDTO getDiseaseById(UUID id) {
        logger.debug("Fetching disease with ID: {}", id);
        
        Disease disease = diseaseRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND_MSG, id);
                    return new ConfigurationNotFoundException(ENTITY_TYPE, id);
                });
        
        return convertToDTO(disease);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiseaseDTO> getAllDiseases() {
        logger.debug("Fetching all diseases");
        
        List<Disease> diseases = diseaseRepository.findAllByOrderByDiseaseNameAsc();
        return diseases.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiseaseDTO> getActiveDiseases() {
        logger.debug("Fetching active diseases");
        
        List<Disease> diseases = diseaseRepository.findByIsActiveTrue();
        return diseases.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiseaseDTO> getDiseasesByStatus(Boolean isActive) {
        logger.debug("Fetching diseases with status: {}", isActive);
        
        List<Disease> diseases = diseaseRepository.findByIsActiveOrderByDiseaseNameAsc(isActive);
        return diseases.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiseaseDTO> getDiseasesBySeverity(Disease.Severity severity) {
        logger.debug("Fetching diseases with severity: {}", severity);
        
        List<Disease> diseases = diseaseRepository.findBySeverity(severity);
        return diseases.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiseaseDTO> getNotifiableDiseases() {
        logger.debug("Fetching notifiable diseases");
        
        List<Disease> diseases = diseaseRepository.findByIsActiveTrueAndIsNotifiableTrue();
        return diseases.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DiseaseDTO toggleDiseaseStatus(UUID id, Boolean isActive) {
        logger.info("Toggling disease status for ID: {} to {}", id, isActive);
        
        Disease disease = diseaseRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND_MSG, id);
                    return new ConfigurationNotFoundException(ENTITY_TYPE, id);
                });
        
        disease.setIsActive(isActive);
        Disease updatedDisease = diseaseRepository.save(disease);
        
        String status = Boolean.TRUE.equals(isActive) ? "active" : "inactive";
        logger.info("Disease status updated successfully: {} is now {}", 
                updatedDisease.getDiseaseName(), status);
        
        return convertToDTO(updatedDisease);
    }

    @Override
    public void deleteDisease(UUID id) {
        logger.info("Attempting to delete disease with ID: {}", id);
        
        Disease disease = diseaseRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND_MSG, id);
                    return new ConfigurationNotFoundException(ENTITY_TYPE, id);
                });
        
        Long usageCount = diseaseRepository.countDiseaseReportsUsingDisease(id);
        if (usageCount > 0) {
            logger.error("Cannot delete disease '{}' - used by {} disease reports", 
                    disease.getDiseaseName(), usageCount);
            throw new ConfigurationInUseException(ENTITY_TYPE, id, usageCount);
        }
        
        diseaseRepository.delete(disease);
        logger.info("Disease deleted successfully: {}", disease.getDiseaseName());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getDiseaseReportUsageCount(UUID id) {
        logger.debug("Getting usage count for disease ID: {}", id);
        return diseaseRepository.countDiseaseReportsUsingDisease(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean diseaseExists(String diseaseName) {
        logger.debug("Checking if disease exists: {}", diseaseName);
        return diseaseRepository.existsByDiseaseNameIgnoreCase(diseaseName);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean diseaseCodeExists(String diseaseCode) {
        logger.debug("Checking if disease code exists: {}", diseaseCode);
        return diseaseRepository.existsByDiseaseCodeIgnoreCase(diseaseCode);
    }

    /**
     * Convert Disease entity to DTO.
     *
     * @param disease the entity
     * @return the DTO
     */
    private DiseaseDTO convertToDTO(Disease disease) {
        DiseaseDTO dto = new DiseaseDTO();
        dto.setId(disease.getId());
        dto.setDiseaseName(disease.getDiseaseName());
        dto.setDiseaseCode(disease.getDiseaseCode());
        dto.setDescription(disease.getDescription());
        dto.setAffectedAnimalTypes(disease.getAffectedAnimalTypes());
        dto.setSeverity(disease.getSeverity());
        dto.setIsNotifiable(disease.getIsNotifiable());
        dto.setIsActive(disease.getIsActive());
        dto.setCreatedAt(disease.getCreatedAt());
        dto.setUpdatedAt(disease.getUpdatedAt());
        
        if (disease.getCreatedBy() != null) {
            dto.setCreatedByUsername(disease.getCreatedBy().getUsername());
        }
        if (disease.getUpdatedBy() != null) {
            dto.setUpdatedByUsername(disease.getUpdatedBy().getUsername());
        }
        
        return dto;
    }
}
