package com.adrs.test.controller;

import com.adrs.dto.FarmTypeDTO;
import com.adrs.dto.AnimalTypeDTO;
import com.adrs.dto.DiseaseDTO;
import com.adrs.model.Disease;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ConfigurationController.
 * Tests REST API endpoints for configuration management with real database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Configuration Controller Integration Tests")
class ConfigurationControllerIntegrationTest {

    private static final String FARM_TYPES_ENDPOINT = "/api/configuration/farm-types";
    private static final String ANIMAL_TYPES_ENDPOINT = "/api/configuration/animal-types";
    private static final String DISEASES_ENDPOINT = "/api/configuration/diseases";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ========================================
    // FARM TYPE TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get all farm types")
    void testGetAllFarmTypes() throws Exception {
        mockMvc.perform(get(FARM_TYPES_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create farm type successfully")
    void testCreateFarmType() throws Exception {
        FarmTypeDTO farmTypeDTO = new FarmTypeDTO();
        farmTypeDTO.setTypeName("Test Farm Type");
        farmTypeDTO.setDescription("Test Description");

        mockMvc.perform(post(FARM_TYPES_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmTypeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.typeName").value("Test Farm Type"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when creating farm type with invalid data")
    void testCreateFarmTypeWithInvalidData() throws Exception {
        FarmTypeDTO farmTypeDTO = new FarmTypeDTO();
        // Missing required typeName

        mockMvc.perform(post(FARM_TYPES_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmTypeDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "VETERINARY_OFFICER")
    @DisplayName("Should return 403 when non-admin tries to create farm type")
    void testCreateFarmTypeAsNonAdmin() throws Exception {
        FarmTypeDTO farmTypeDTO = new FarmTypeDTO();
        farmTypeDTO.setTypeName("Test Farm Type");
        farmTypeDTO.setDescription("Test Description");

        mockMvc.perform(post(FARM_TYPES_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmTypeDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should redirect to login when unauthenticated user tries to access farm types")
    void testGetFarmTypesUnauthenticated() throws Exception {
        mockMvc.perform(get(FARM_TYPES_ENDPOINT))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    // ========================================
    // ANIMAL TYPE TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get all animal types")
    void testGetAllAnimalTypes() throws Exception {
        mockMvc.perform(get(ANIMAL_TYPES_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create animal type successfully")
    void testCreateAnimalType() throws Exception {
        AnimalTypeDTO animalTypeDTO = new AnimalTypeDTO();
        animalTypeDTO.setTypeName("Test Animal Type");
        animalTypeDTO.setDescription("Test Animal Description");

        mockMvc.perform(post(ANIMAL_TYPES_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(animalTypeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.typeName").value("Test Animal Type"))
                .andExpect(jsonPath("$.description").value("Test Animal Description"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get active animal types only")
    void testGetActiveAnimalTypes() throws Exception {
        mockMvc.perform(get(ANIMAL_TYPES_ENDPOINT + "/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    // ========================================
    // DISEASE TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get all diseases")
    void testGetAllDiseases() throws Exception {
        mockMvc.perform(get(DISEASES_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create disease successfully")
    void testCreateDisease() throws Exception {
        DiseaseDTO diseaseDTO = new DiseaseDTO();
        diseaseDTO.setDiseaseName("Test Disease");
        diseaseDTO.setDiseaseCode("TD001");
        diseaseDTO.setDescription("Test Disease Description");
        diseaseDTO.setSeverity(Disease.Severity.MEDIUM);

        mockMvc.perform(post(DISEASES_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diseaseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.diseaseName").value("Test Disease"))
                .andExpect(jsonPath("$.diseaseCode").value("TD001"))
                .andExpect(jsonPath("$.severity").value("MEDIUM"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get diseases by severity")
    void testGetDiseasesBySeverity() throws Exception {
        mockMvc.perform(get(DISEASES_ENDPOINT + "/severity/HIGH"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for invalid severity")
    void testGetDiseasesByInvalidSeverity() throws Exception {
        mockMvc.perform(get(DISEASES_ENDPOINT + "/severity/INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when creating disease with invalid data")
    void testCreateDiseaseWithInvalidData() throws Exception {
        DiseaseDTO diseaseDTO = new DiseaseDTO();
        // Missing required fields

        mockMvc.perform(post(DISEASES_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diseaseDTO)))
                .andExpect(status().isBadRequest());
    }

    // ========================================
    // SECURITY TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "VETERINARY_OFFICER")
    @DisplayName("Should deny access to non-admin for disease creation")
    void testCreateDiseaseAsNonAdmin() throws Exception {
        DiseaseDTO diseaseDTO = new DiseaseDTO();
        diseaseDTO.setDiseaseName("Test Disease");
        diseaseDTO.setDiseaseCode("TD001");
        diseaseDTO.setSeverity(Disease.Severity.LOW);

        mockMvc.perform(post(DISEASES_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diseaseDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should redirect unauthenticated users to login")
    void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get(DISEASES_ENDPOINT))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    // ========================================
    // CSRF TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should reject POST without CSRF token")
    void testCreateWithoutCsrfToken() throws Exception {
        FarmTypeDTO farmTypeDTO = new FarmTypeDTO();
        farmTypeDTO.setTypeName("Test Farm Type");

        mockMvc.perform(post(FARM_TYPES_ENDPOINT)
                        // No CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmTypeDTO)))
                .andExpect(status().isForbidden());
    }
}
