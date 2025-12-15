package com.adrs.controller;

import com.adrs.dto.*;
import com.adrs.model.User;
import com.adrs.repository.UserRepository;
import com.adrs.service.AnimalTypeService;
import com.adrs.service.FarmService;
import com.adrs.service.FarmTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for veterinary officer operations.
 * Provides endpoints for farm registration and management.
 */
@Tag(name = "Veterinary Operations", description = "APIs for veterinary officers to manage farms and animals")
@RestController
@RequestMapping("/api/vet")
@PreAuthorize("hasRole('VETERINARY_OFFICER')")
@SecurityRequirement(name = "session-auth")
public class VetController {

    private static final Logger logger = LoggerFactory.getLogger(VetController.class);

    private final FarmService farmService;
    private final FarmTypeService farmTypeService;
    private final AnimalTypeService animalTypeService;
    private final UserRepository userRepository;

    public VetController(FarmService farmService,
                        FarmTypeService farmTypeService,
                        AnimalTypeService animalTypeService,
                        UserRepository userRepository) {
        this.farmService = farmService;
        this.farmTypeService = farmTypeService;
        this.animalTypeService = animalTypeService;
        this.userRepository = userRepository;
    }

    // ========================================
    // FARM ENDPOINTS
    // ========================================

    /**
     * Create a new farm.
     *
     * @param request the farm request data
     * @param userDetails the authenticated user
     * @return the created farm
     */
    @Operation(summary = "Register a new farm", description = "Creates a new farm with the provided details")
    @PostMapping("/farms")
    public ResponseEntity<FarmResponseDTO> createFarm(
            @Valid @RequestBody FarmRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("POST /api/vet/farms - Creating farm: {} by user: {}", 
                    request.getFarmName(), userDetails.getUsername());
        
        User currentUser = getCurrentUser(userDetails);
        FarmResponseDTO response = farmService.createFarm(request, currentUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all farms created by the current vet.
     *
     * @param userDetails the authenticated user
     * @return list of farms
     */
    @Operation(summary = "Get my farms", description = "Retrieves all farms created by the current veterinary officer")
    @GetMapping("/farms")
    public ResponseEntity<List<FarmResponseDTO>> getMyFarms(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("GET /api/vet/farms - Fetching farms for user: {}", userDetails.getUsername());
        
        User currentUser = getCurrentUser(userDetails);
        List<FarmResponseDTO> farms = farmService.getFarmsByCreatedBy(currentUser);
        
        return ResponseEntity.ok(farms);
    }

    /**
     * Get a specific farm by ID.
     *
     * @param id the farm ID
     * @return the farm details
     */
    @Operation(summary = "Get farm by ID", description = "Retrieves a specific farm by its ID")
    @GetMapping("/farms/{id}")
    public ResponseEntity<FarmResponseDTO> getFarmById(@PathVariable UUID id) {
        logger.info("GET /api/vet/farms/{} - Fetching farm details", id);
        
        FarmResponseDTO response = farmService.getFarmById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing farm.
     *
     * @param id the farm ID
     * @param request the farm request data
     * @param userDetails the authenticated user
     * @return the updated farm
     */
    @Operation(summary = "Update farm", description = "Updates an existing farm")
    @PutMapping("/farms/{id}")
    public ResponseEntity<FarmResponseDTO> updateFarm(
            @PathVariable UUID id,
            @Valid @RequestBody FarmRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("PUT /api/vet/farms/{} - Updating farm by user: {}", id, userDetails.getUsername());
        logger.info("Request data: farmName={}, farmTypeId={}, province={}, district={}", 
                    request.getFarmName(), request.getFarmTypeId(), request.getProvince(), request.getDistrict());
        
        try {
            User currentUser = getCurrentUser(userDetails);
            FarmResponseDTO response = farmService.updateFarm(id, request, currentUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating farm: {} - {}", e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Update animal tags for a farm.
     *
     * @param id the farm ID
     * @param animalTags the animal tags
     * @param userDetails the authenticated user
     * @return the updated farm
     */
    @Operation(summary = "Update animal tags", description = "Updates the animal types and counts for a farm")
    @PutMapping("/farms/{id}/animals")
    public ResponseEntity<FarmResponseDTO> updateAnimalTags(
            @PathVariable UUID id,
            @RequestBody List<FarmRequestDTO.AnimalTagDTO> animalTags,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("PUT /api/vet/farms/{}/animals - Updating animal tags by user: {}", 
                    id, userDetails.getUsername());
        
        User currentUser = getCurrentUser(userDetails);
        FarmResponseDTO response = farmService.updateAnimalTags(id, animalTags, currentUser);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a farm (soft delete).
     *
     * @param id the farm ID
     * @param userDetails the authenticated user
     * @return no content
     */
    @Operation(summary = "Delete farm", description = "Soft deletes a farm")
    @DeleteMapping("/farms/{id}")
    public ResponseEntity<Void> deleteFarm(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("DELETE /api/vet/farms/{} - Deleting farm by user: {}", id, userDetails.getUsername());
        
        User currentUser = getCurrentUser(userDetails);
        farmService.deleteFarm(id, currentUser);
        
        return ResponseEntity.noContent().build();
    }

    // ========================================
    // LOOKUP DATA ENDPOINTS
    // ========================================

    /**
     * Get all active farm types for dropdown selection.
     *
     * @return list of active farm types
     */
    @Operation(summary = "Get active farm types", description = "Retrieves all active farm types for selection")
    @GetMapping("/farm-types")
    public ResponseEntity<List<FarmTypeDTO>> getActiveFarmTypes() {
        logger.info("GET /api/vet/farm-types - Fetching active farm types");
        
        List<FarmTypeDTO> farmTypes = farmTypeService.getActiveFarmTypes();
        return ResponseEntity.ok(farmTypes);
    }

    /**
     * Get all active animal types for tag selection.
     *
     * @return list of active animal types
     */
    @Operation(summary = "Get active animal types", description = "Retrieves all active animal types for tag selection")
    @GetMapping("/animal-types")
    public ResponseEntity<List<AnimalTypeDTO>> getActiveAnimalTypes() {
        logger.info("GET /api/vet/animal-types - Fetching active animal types");
        
        List<AnimalTypeDTO> animalTypes = animalTypeService.getActiveAnimalTypes();
        return ResponseEntity.ok(animalTypes);
    }

    /**
     * Get vet dashboard statistics.
     *
     * @param userDetails the authenticated user
     * @return dashboard stats
     */
    @Operation(summary = "Get vet dashboard stats", description = "Retrieves statistics for the vet dashboard")
    @GetMapping("/stats")
    public ResponseEntity<VetStatsDTO> getVetStats(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("GET /api/vet/stats - Fetching stats for user: {}", userDetails.getUsername());
        
        User currentUser = getCurrentUser(userDetails);
        Long farmCount = farmService.getFarmCountByUser(currentUser);
        
        VetStatsDTO stats = new VetStatsDTO();
        stats.setFarmsCount(farmCount);
        stats.setAnimalsCount(0L); // TODO: Implement when needed
        stats.setReportsCount(0L); // TODO: Implement when needed
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Gets the current User entity from UserDetails.
     */
    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername()));
    }

    /**
     * Simple DTO for vet dashboard stats.
     */
    public static class VetStatsDTO {
        private Long farmsCount;
        private Long animalsCount;
        private Long reportsCount;

        public Long getFarmsCount() { return farmsCount; }
        public void setFarmsCount(Long farmsCount) { this.farmsCount = farmsCount; }
        public Long getAnimalsCount() { return animalsCount; }
        public void setAnimalsCount(Long animalsCount) { this.animalsCount = animalsCount; }
        public Long getReportsCount() { return reportsCount; }
        public void setReportsCount(Long reportsCount) { this.reportsCount = reportsCount; }
    }
}
