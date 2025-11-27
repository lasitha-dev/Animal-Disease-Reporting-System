package com.adrs.test.service;

import com.adrs.dto.DiseaseDTO;
import com.adrs.exception.ConfigurationInUseException;
import com.adrs.exception.ConfigurationNotFoundException;
import com.adrs.model.Disease;
import com.adrs.repository.DiseaseRepository;
import com.adrs.service.impl.DiseaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DiseaseServiceImpl.
 * Tests business logic with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Disease Service Tests")
class DiseaseServiceTest {

    private static final String TEST_DISEASE_NAME = "Foot and Mouth Disease";
    private static final String TEST_DISEASE_CODE = "FMD-001";
    private static final String TEST_DESCRIPTION = "Highly contagious viral disease";
    private static final String TEST_USERNAME = "testuser";

    @Mock
    private DiseaseRepository diseaseRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DiseaseServiceImpl diseaseService;

    private Disease testDisease;
    private DiseaseDTO testDiseaseDTO;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        testDisease = new Disease();
        testDisease.setId(testId);
        testDisease.setDiseaseName(TEST_DISEASE_NAME);
        testDisease.setDiseaseCode(TEST_DISEASE_CODE);
        testDisease.setDescription(TEST_DESCRIPTION);
        testDisease.setSeverity(Disease.Severity.HIGH);
        testDisease.setNotifiable(true);
        testDisease.setAffectedAnimalTypes("Cattle, Sheep, Goats");
        testDisease.setActive(true);
        testDisease.setCreatedAt(LocalDateTime.now());

        testDiseaseDTO = new DiseaseDTO();
        testDiseaseDTO.setDiseaseName(TEST_DISEASE_NAME);
        testDiseaseDTO.setDiseaseCode(TEST_DISEASE_CODE);
        testDiseaseDTO.setDescription(TEST_DESCRIPTION);
        testDiseaseDTO.setSeverity(Disease.Severity.HIGH);
        testDiseaseDTO.setNotifiable(true);
        testDiseaseDTO.setAffectedAnimalTypes("Cattle, Sheep, Goats");
        testDiseaseDTO.setActive(true);

        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(TEST_USERNAME);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Should create disease successfully")
    void testCreateDisease() {
        // Given
        when(diseaseRepository.existsByDiseaseCodeIgnoreCase(anyString())).thenReturn(false);
        when(diseaseRepository.save(any(Disease.class))).thenReturn(testDisease);

        // When
        DiseaseDTO result = diseaseService.createDisease(testDiseaseDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDiseaseName()).isEqualTo(TEST_DISEASE_NAME);
        assertThat(result.getDiseaseCode()).isEqualTo(TEST_DISEASE_CODE);
        assertThat(result.getSeverity()).isEqualTo(Disease.Severity.HIGH);
        assertThat(result.isNotifiable()).isTrue();

        verify(diseaseRepository, times(1)).existsByDiseaseCodeIgnoreCase(TEST_DISEASE_CODE);
        verify(diseaseRepository, times(1)).save(any(Disease.class));
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate disease")
    void testCreateDiseaseDuplicate() {
        // Given
        when(diseaseRepository.existsByDiseaseCodeIgnoreCase(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> diseaseService.createDisease(testDiseaseDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(diseaseRepository, times(1)).existsByDiseaseCodeIgnoreCase(TEST_DISEASE_CODE);
        verify(diseaseRepository, never()).save(any(Disease.class));
    }

    @Test
    @DisplayName("Should get all diseases")
    void testGetAllDiseases() {
        // Given
        Disease disease2 = new Disease();
        disease2.setId(UUID.randomUUID());
        disease2.setDiseaseName("Avian Influenza");
        disease2.setDiseaseCode("AI-001");
        disease2.setSeverity(Disease.Severity.CRITICAL);
        disease2.setActive(true);

        List<Disease> diseases = Arrays.asList(testDisease, disease2);
        when(diseaseRepository.findAllByOrderByDiseaseNameAsc()).thenReturn(diseases);

        // When
        List<DiseaseDTO> result = diseaseService.getAllDiseases();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDiseaseName()).isEqualTo(TEST_DISEASE_NAME);
        assertThat(result.get(1).getDiseaseName()).isEqualTo("Avian Influenza");

        verify(diseaseRepository, times(1)).findAllByOrderByDiseaseNameAsc();
    }

    @Test
    @DisplayName("Should get disease by id")
    void testGetDiseaseById() {
        // Given
        when(diseaseRepository.findById(testId)).thenReturn(Optional.of(testDisease));

        // When
        DiseaseDTO result = diseaseService.getDiseaseById(testId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDiseaseName()).isEqualTo(TEST_DISEASE_NAME);
        assertThat(result.getDiseaseCode()).isEqualTo(TEST_DISEASE_CODE);

        verify(diseaseRepository, times(1)).findById(testId);
    }

    @Test
    @DisplayName("Should throw exception when disease not found by id")
    void testGetDiseaseByIdNotFound() {
        // Given
        when(diseaseRepository.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> diseaseService.getDiseaseById(testId))
                .isInstanceOf(ConfigurationNotFoundException.class)
                .hasMessageContaining("not found");

        verify(diseaseRepository, times(1)).findById(testId);
    }

    @Test
    @DisplayName("Should update disease successfully")
    void testUpdateDisease() {
        // Given
        DiseaseDTO updateDTO = new DiseaseDTO();
        updateDTO.setDiseaseName("Updated Disease Name");
        updateDTO.setDiseaseCode(TEST_DISEASE_CODE);
        updateDTO.setDescription("Updated description");
        updateDTO.setSeverity(Disease.Severity.MEDIUM);
        updateDTO.setNotifiable(false);
        updateDTO.setActive(true);

        when(diseaseRepository.findById(testId)).thenReturn(Optional.of(testDisease));
        when(diseaseRepository.existsByDiseaseCodeIgnoreCase(anyString())).thenReturn(false);
        when(diseaseRepository.save(any(Disease.class))).thenReturn(testDisease);

        // When
        DiseaseDTO result = diseaseService.updateDisease(testId, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(diseaseRepository, times(1)).findById(testId);
        verify(diseaseRepository, times(1)).save(any(Disease.class));
    }

    @Test
    @DisplayName("Should delete disease successfully")
    void testDeleteDisease() {
        // Given
        when(diseaseRepository.findById(testId)).thenReturn(Optional.of(testDisease));
        when(diseaseRepository.countDiseaseReportsByDiseaseId(testId)).thenReturn(0L);
        doNothing().when(diseaseRepository).delete(any(Disease.class));

        // When
        diseaseService.deleteDisease(testId);

        // Then
        verify(diseaseRepository, times(1)).findById(testId);
        verify(diseaseRepository, times(1)).countDiseaseReportsByDiseaseId(testId);
        verify(diseaseRepository, times(1)).delete(testDisease);
    }

    @Test
    @DisplayName("Should throw exception when deleting disease in use")
    void testDeleteDiseaseInUse() {
        // Given
        when(diseaseRepository.findById(testId)).thenReturn(Optional.of(testDisease));
        when(diseaseRepository.countDiseaseReportsByDiseaseId(testId)).thenReturn(7L);

        // When & Then
        assertThatThrownBy(() -> diseaseService.deleteDisease(testId))
                .isInstanceOf(ConfigurationInUseException.class)
                .hasMessageContaining("in use");

        verify(diseaseRepository, times(1)).findById(testId);
        verify(diseaseRepository, times(1)).countDiseaseReportsByDiseaseId(testId);
        verify(diseaseRepository, never()).delete(any(Disease.class));
    }

    @Test
    @DisplayName("Should toggle disease status successfully")
    void testToggleDiseaseStatus() {
        // Given
        when(diseaseRepository.findById(testId)).thenReturn(Optional.of(testDisease));
        when(diseaseRepository.save(any(Disease.class))).thenReturn(testDisease);

        // When
        DiseaseDTO result = diseaseService.toggleDiseaseStatus(testId);

        // Then
        assertThat(result).isNotNull();
        verify(diseaseRepository, times(1)).findById(testId);
        verify(diseaseRepository, times(1)).save(any(Disease.class));
    }

    @Test
    @DisplayName("Should get notifiable diseases only")
    void testGetNotifiableDiseases() {
        // Given
        when(diseaseRepository.findByIsNotifiableTrueAndIsActiveTrueOrderByDiseaseNameAsc())
                .thenReturn(Arrays.asList(testDisease));

        // When
        List<DiseaseDTO> result = diseaseService.getNotifiableDiseases();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isNotifiable()).isTrue();
        assertThat(result.get(0).isActive()).isTrue();

        verify(diseaseRepository, times(1))
                .findByIsNotifiableTrueAndIsActiveTrueOrderByDiseaseNameAsc();
    }

    @Test
    @DisplayName("Should get diseases by severity")
    void testGetDiseasesBySeverity() {
        // Given
        Disease.Severity severity = Disease.Severity.HIGH;
        when(diseaseRepository.findBySeverityAndIsActiveTrueOrderByDiseaseNameAsc(severity))
                .thenReturn(Arrays.asList(testDisease));

        // When
        List<DiseaseDTO> result = diseaseService.getDiseasesBySeverity(severity);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSeverity()).isEqualTo(Disease.Severity.HIGH);

        verify(diseaseRepository, times(1))
                .findBySeverityAndIsActiveTrueOrderByDiseaseNameAsc(severity);
    }

    @Test
    @DisplayName("Should get disease report usage count")
    void testGetDiseaseReportUsageCount() {
        // Given
        Long expectedCount = 20L;
        when(diseaseRepository.countDiseaseReportsByDiseaseId(testId)).thenReturn(expectedCount);

        // When
        Long result = diseaseService.getDiseaseReportUsageCount(testId);

        // Then
        assertThat(result).isEqualTo(expectedCount);
        verify(diseaseRepository, times(1)).countDiseaseReportsByDiseaseId(testId);
    }
}
