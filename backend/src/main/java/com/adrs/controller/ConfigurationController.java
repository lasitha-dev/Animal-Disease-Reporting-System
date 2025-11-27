package com.adrs.controller;

import com.adrs.dto.FarmTypeDTO;
import com.adrs.dto.AnimalTypeDTO;
import com.adrs.dto.DiseaseDTO;
import com.adrs.model.Disease;
import com.adrs.service.FarmTypeService;
import com.adrs.service.AnimalTypeService;
import com.adrs.service.DiseaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for configuration management.
 * Provides CRUD endpoints for farm types, animal types, and diseases.
 * All endpoints are restricted to ADMIN role only.
 */
@Tag(name = "Configuration Management", description = "APIs for managing system configuration including farm types, animal types, and diseases")
@RestController
@RequestMapping("/api/configuration")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "session-auth")
public class ConfigurationController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);
    
    private final FarmTypeService farmTypeService;
    private final AnimalTypeService animalTypeService;
    private final DiseaseService diseaseService;

    public ConfigurationController(FarmTypeService farmTypeService,
                                   AnimalTypeService animalTypeService,
                                   DiseaseService diseaseService) {
        this.farmTypeService = farmTypeService;
        this.animalTypeService = animalTypeService;
        this.diseaseService = diseaseService;
    }

    // ========================================
    // FARM TYPE ENDPOINTS
    // ========================================

    /**
     * Get all farm types.
     *
     * @return list of all farm types
     */
    @Operation(summary = "Get all farm types", description = "Retrieves a list of all farm types in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of farm types"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required", content = @Content)
    })
    @GetMapping("/farm-types")
    public ResponseEntity<List<FarmTypeDTO>> getAllFarmTypes() {
        logger.info("GET /api/configuration/farm-types - Fetching all farm types");
        List<FarmTypeDTO> farmTypes = farmTypeService.getAllFarmTypes();
        return ResponseEntity.ok(farmTypes);
    }

    /**
     * Get a farm type by ID.
     *
     * @param id the farm type ID
     * @return the farm type
     */
    @Operation(summary = "Get farm type by ID", description = "Retrieves a specific farm type by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved farm type"),
            @ApiResponse(responseCode = "404", description = "Farm type not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required", content = @Content)
    })
    @GetMapping("/farm-types/{id}")
    public ResponseEntity<FarmTypeDTO> getFarmTypeById(
            @Parameter(description = "UUID of the farm type to retrieve", required = true)
            @PathVariable UUID id) {
        logger.info("GET /api/configuration/farm-types/{} - Fetching farm type", id);
        FarmTypeDTO farmType = farmTypeService.getFarmTypeById(id);
        return ResponseEntity.ok(farmType);
    }

    /**
     * Create a new farm type.
     *
     * @param farmTypeDTO the farm type data
     * @return the created farm type
     */
    @Operation(summary = "Create farm type", description = "Creates a new farm type configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Farm type created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required", content = @Content)
    })
    @PostMapping("/farm-types")
    public ResponseEntity<FarmTypeDTO> createFarmType(@Valid @RequestBody FarmTypeDTO farmTypeDTO) {
        logger.info("POST /api/configuration/farm-types - Creating farm type: {}", farmTypeDTO.getTypeName());
        FarmTypeDTO createdFarmType = farmTypeService.createFarmType(farmTypeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFarmType);
    }

    /**
     * Update a farm type.
     *
     * @param id the farm type ID
     * @param farmTypeDTO the updated farm type data
     * @return the updated farm type
     */
    @PutMapping("/farm-types/{id}")
    public ResponseEntity<FarmTypeDTO> updateFarmType(@PathVariable UUID id,
                                                      @Valid @RequestBody FarmTypeDTO farmTypeDTO) {
        logger.info("PUT /api/configuration/farm-types/{} - Updating farm type", id);
        FarmTypeDTO updatedFarmType = farmTypeService.updateFarmType(id, farmTypeDTO);
        return ResponseEntity.ok(updatedFarmType);
    }

    /**
     * Toggle farm type status (activate/deactivate).
     *
     * @param id the farm type ID
     * @param status the new status
     * @return the updated farm type
     */
    @PatchMapping("/farm-types/{id}/status")
    public ResponseEntity<FarmTypeDTO> toggleFarmTypeStatus(@PathVariable UUID id,
                                                            @RequestBody Map<String, Boolean> status) {
        logger.info("PATCH /api/configuration/farm-types/{}/status - Toggling status", id);
        Boolean isActive = status.get("isActive");
        FarmTypeDTO updatedFarmType = farmTypeService.toggleFarmTypeStatus(id, isActive);
        return ResponseEntity.ok(updatedFarmType);
    }

    /**
     * Delete a farm type.
     *
     * @param id the farm type ID
     * @return no content
     */
    @DeleteMapping("/farm-types/{id}")
    public ResponseEntity<Void> deleteFarmType(@PathVariable UUID id) {
        logger.info("DELETE /api/configuration/farm-types/{} - Deleting farm type", id);
        farmTypeService.deleteFarmType(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get farm usage count for a farm type.
     *
     * @param id the farm type ID
     * @return usage count
     */
    @GetMapping("/farm-types/{id}/usage")
    public ResponseEntity<Map<String, Long>> getFarmTypeUsage(@PathVariable UUID id) {
        logger.info("GET /api/configuration/farm-types/{}/usage - Fetching usage count", id);
        Long count = farmTypeService.getFarmUsageCount(id);
        return ResponseEntity.ok(Map.of("usageCount", count));
    }

    // ========================================
    // ANIMAL TYPE ENDPOINTS
    // ========================================

    /**
     * Get all animal types.
     *
     * @return list of all animal types
     */
    @GetMapping("/animal-types")
    public ResponseEntity<List<AnimalTypeDTO>> getAllAnimalTypes() {
        logger.info("GET /api/configuration/animal-types - Fetching all animal types");
        List<AnimalTypeDTO> animalTypes = animalTypeService.getAllAnimalTypes();
        return ResponseEntity.ok(animalTypes);
    }

    /**
     * Get active animal types only.
     *
     * @return list of active animal types
     */
    @GetMapping("/animal-types/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARY_OFFICER', 'FARMER')")
    public ResponseEntity<List<AnimalTypeDTO>> getActiveAnimalTypes() {
        logger.info("GET /api/configuration/animal-types/active - Fetching active animal types");
        List<AnimalTypeDTO> animalTypes = animalTypeService.getActiveAnimalTypes();
        return ResponseEntity.ok(animalTypes);
    }

    /**
     * Get an animal type by ID.
     *
     * @param id the animal type ID
     * @return the animal type
     */
    @GetMapping("/animal-types/{id}")
    public ResponseEntity<AnimalTypeDTO> getAnimalTypeById(@PathVariable UUID id) {
        logger.info("GET /api/configuration/animal-types/{} - Fetching animal type", id);
        AnimalTypeDTO animalType = animalTypeService.getAnimalTypeById(id);
        return ResponseEntity.ok(animalType);
    }

    /**
     * Create a new animal type.
     *
     * @param animalTypeDTO the animal type data
     * @return the created animal type
     */
    @PostMapping("/animal-types")
    public ResponseEntity<AnimalTypeDTO> createAnimalType(@Valid @RequestBody AnimalTypeDTO animalTypeDTO) {
        logger.info("POST /api/configuration/animal-types - Creating animal type: {}", animalTypeDTO.getTypeName());
        AnimalTypeDTO createdAnimalType = animalTypeService.createAnimalType(animalTypeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAnimalType);
    }

    /**
     * Update an animal type.
     *
     * @param id the animal type ID
     * @param animalTypeDTO the updated animal type data
     * @return the updated animal type
     */
    @PutMapping("/animal-types/{id}")
    public ResponseEntity<AnimalTypeDTO> updateAnimalType(@PathVariable UUID id,
                                                          @Valid @RequestBody AnimalTypeDTO animalTypeDTO) {
        logger.info("PUT /api/configuration/animal-types/{} - Updating animal type", id);
        AnimalTypeDTO updatedAnimalType = animalTypeService.updateAnimalType(id, animalTypeDTO);
        return ResponseEntity.ok(updatedAnimalType);
    }

    /**
     * Toggle animal type status (activate/deactivate).
     *
     * @param id the animal type ID
     * @param status the new status
     * @return the updated animal type
     */
    @PatchMapping("/animal-types/{id}/status")
    public ResponseEntity<AnimalTypeDTO> toggleAnimalTypeStatus(@PathVariable UUID id,
                                                                @RequestBody Map<String, Boolean> status) {
        logger.info("PATCH /api/configuration/animal-types/{}/status - Toggling status", id);
        Boolean isActive = status.get("isActive");
        AnimalTypeDTO updatedAnimalType = animalTypeService.toggleAnimalTypeStatus(id, isActive);
        return ResponseEntity.ok(updatedAnimalType);
    }

    /**
     * Delete an animal type.
     *
     * @param id the animal type ID
     * @return no content
     */
    @DeleteMapping("/animal-types/{id}")
    public ResponseEntity<Void> deleteAnimalType(@PathVariable UUID id) {
        logger.info("DELETE /api/configuration/animal-types/{} - Deleting animal type", id);
        animalTypeService.deleteAnimalType(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get animal usage count for an animal type.
     *
     * @param id the animal type ID
     * @return usage count
     */
    @GetMapping("/animal-types/{id}/usage")
    public ResponseEntity<Map<String, Long>> getAnimalTypeUsage(@PathVariable UUID id) {
        logger.info("GET /api/configuration/animal-types/{}/usage - Fetching usage count", id);
        Long count = animalTypeService.getAnimalUsageCount(id);
        return ResponseEntity.ok(Map.of("usageCount", count));
    }

    // ========================================
    // DISEASE ENDPOINTS
    // ========================================

    /**
     * Get all diseases.
     *
     * @return list of all diseases
     */
    @GetMapping("/diseases")
    public ResponseEntity<List<DiseaseDTO>> getAllDiseases() {
        logger.info("GET /api/configuration/diseases - Fetching all diseases");
        List<DiseaseDTO> diseases = diseaseService.getAllDiseases();
        return ResponseEntity.ok(diseases);
    }

    /**
     * Get diseases by severity level.
     *
     * @param severity the severity level
     * @return list of diseases with specified severity
     */
    @GetMapping("/diseases/severity/{severity}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARY_OFFICER', 'FARMER')")
    public ResponseEntity<List<DiseaseDTO>> getDiseasesBySeverity(@PathVariable String severity) {
        logger.info("GET /api/configuration/diseases/severity/{} - Fetching diseases by severity", severity);
        try {
            Disease.Severity severityEnum = Disease.Severity.valueOf(severity.toUpperCase());
            List<DiseaseDTO> diseases = diseaseService.getDiseasesBySeverity(severityEnum);
            return ResponseEntity.ok(diseases);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid severity level: " + severity + 
                ". Valid values are: LOW, MEDIUM, HIGH, CRITICAL");
        }
    }

    /**
     * Get a disease by ID.
     *
     * @param id the disease ID
     * @return the disease
     */
    @GetMapping("/diseases/{id}")
    public ResponseEntity<DiseaseDTO> getDiseaseById(@PathVariable UUID id) {
        logger.info("GET /api/configuration/diseases/{} - Fetching disease", id);
        DiseaseDTO disease = diseaseService.getDiseaseById(id);
        return ResponseEntity.ok(disease);
    }

    /**
     * Create a new disease.
     *
     * @param diseaseDTO the disease data
     * @return the created disease
     */
    @PostMapping("/diseases")
    public ResponseEntity<DiseaseDTO> createDisease(@Valid @RequestBody DiseaseDTO diseaseDTO) {
        logger.info("POST /api/configuration/diseases - Creating disease: {}", diseaseDTO.getDiseaseName());
        DiseaseDTO createdDisease = diseaseService.createDisease(diseaseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDisease);
    }

    /**
     * Update a disease.
     *
     * @param id the disease ID
     * @param diseaseDTO the updated disease data
     * @return the updated disease
     */
    @PutMapping("/diseases/{id}")
    public ResponseEntity<DiseaseDTO> updateDisease(@PathVariable UUID id,
                                                    @Valid @RequestBody DiseaseDTO diseaseDTO) {
        logger.info("PUT /api/configuration/diseases/{} - Updating disease", id);
        DiseaseDTO updatedDisease = diseaseService.updateDisease(id, diseaseDTO);
        return ResponseEntity.ok(updatedDisease);
    }

    /**
     * Toggle disease status (activate/deactivate).
     *
     * @param id the disease ID
     * @param status the new status
     * @return the updated disease
     */
    @PatchMapping("/diseases/{id}/status")
    public ResponseEntity<DiseaseDTO> toggleDiseaseStatus(@PathVariable UUID id,
                                                          @RequestBody Map<String, Boolean> status) {
        logger.info("PATCH /api/configuration/diseases/{}/status - Toggling status", id);
        Boolean isActive = status.get("isActive");
        DiseaseDTO updatedDisease = diseaseService.toggleDiseaseStatus(id, isActive);
        return ResponseEntity.ok(updatedDisease);
    }

    /**
     * Delete a disease.
     *
     * @param id the disease ID
     * @return no content
     */
    @DeleteMapping("/diseases/{id}")
    public ResponseEntity<Void> deleteDisease(@PathVariable UUID id) {
        logger.info("DELETE /api/configuration/diseases/{} - Deleting disease", id);
        diseaseService.deleteDisease(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get disease report usage count for a disease.
     *
     * @param id the disease ID
     * @return usage count
     */
    @GetMapping("/diseases/{id}/usage")
    public ResponseEntity<Map<String, Long>> getDiseaseUsage(@PathVariable UUID id) {
        logger.info("GET /api/configuration/diseases/{}/usage - Fetching usage count", id);
        Long count = diseaseService.getDiseaseReportUsageCount(id);
        return ResponseEntity.ok(Map.of("usageCount", count));
    }

    /**
     * Get notifiable diseases.
     *
     * @return list of notifiable diseases
     */
    @GetMapping("/diseases/notifiable")
    public ResponseEntity<List<DiseaseDTO>> getNotifiableDiseases() {
        logger.info("GET /api/configuration/diseases/notifiable - Fetching notifiable diseases");
        List<DiseaseDTO> diseases = diseaseService.getNotifiableDiseases();
        return ResponseEntity.ok(diseases);
    }
}
