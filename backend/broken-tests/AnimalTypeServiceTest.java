package com.adrs.test.service;

import com.adrs.dto.AnimalTypeDTO;
import com.adrs.exception.ConfigurationInUseException;
import com.adrs.exception.ConfigurationNotFoundException;
import com.adrs.model.AnimalType;
import com.adrs.repository.AnimalTypeRepository;
import com.adrs.service.impl.AnimalTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
 * Unit tests for AnimalTypeServiceImpl.
 * Tests business logic with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Animal Type Service Tests")
class AnimalTypeServiceTest {

    private static final String TEST_TYPE_NAME = "Cattle";
    private static final String TEST_DESCRIPTION = "Bovine animals for dairy or beef production";

    @Mock
    private AnimalTypeRepository animalTypeRepository;

    @InjectMocks
    private AnimalTypeServiceImpl animalTypeService;

    private AnimalType testAnimalType;
    private AnimalTypeDTO testAnimalTypeDTO;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        testAnimalType = new AnimalType();
        testAnimalType.setId(testId);
        testAnimalType.setTypeName(TEST_TYPE_NAME);
        testAnimalType.setDescription(TEST_DESCRIPTION);
        testAnimalType.setActive(true);
        testAnimalType.setCreatedAt(LocalDateTime.now());

        testAnimalTypeDTO = new AnimalTypeDTO();
        testAnimalTypeDTO.setTypeName(TEST_TYPE_NAME);
        testAnimalTypeDTO.setDescription(TEST_DESCRIPTION);
        testAnimalTypeDTO.setActive(true);
    }

    @Test
    @DisplayName("Should create animal type successfully")
    void testCreateAnimalType() {
        // Given
        when(animalTypeRepository.existsByTypeNameIgnoreCase(anyString())).thenReturn(false);
        when(animalTypeRepository.save(any(AnimalType.class))).thenReturn(testAnimalType);

        // When
        AnimalTypeDTO result = animalTypeService.createAnimalType(testAnimalTypeDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTypeName()).isEqualTo(TEST_TYPE_NAME);
        assertThat(result.getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertThat(result.isActive()).isTrue();

        verify(animalTypeRepository, times(1)).existsByTypeNameIgnoreCase(TEST_TYPE_NAME);
        verify(animalTypeRepository, times(1)).save(any(AnimalType.class));
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate animal type")
    void testCreateAnimalTypeDuplicate() {
        // Given
        when(animalTypeRepository.existsByTypeNameIgnoreCase(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> animalTypeService.createAnimalType(testAnimalTypeDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(animalTypeRepository, times(1)).existsByTypeNameIgnoreCase(TEST_TYPE_NAME);
        verify(animalTypeRepository, never()).save(any(AnimalType.class));
    }

    @Test
    @DisplayName("Should get all animal types")
    void testGetAllAnimalTypes() {
        // Given
        AnimalType animalType2 = new AnimalType();
        animalType2.setId(UUID.randomUUID());
        animalType2.setTypeName("Poultry");
        animalType2.setDescription("Domestic fowl");
        animalType2.setActive(true);

        List<AnimalType> animalTypes = Arrays.asList(testAnimalType, animalType2);
        when(animalTypeRepository.findAllByOrderByTypeNameAsc()).thenReturn(animalTypes);

        // When
        List<AnimalTypeDTO> result = animalTypeService.getAllAnimalTypes();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTypeName()).isEqualTo(TEST_TYPE_NAME);
        assertThat(result.get(1).getTypeName()).isEqualTo("Poultry");

        verify(animalTypeRepository, times(1)).findAllByOrderByTypeNameAsc();
    }

    @Test
    @DisplayName("Should get animal type by id")
    void testGetAnimalTypeById() {
        // Given
        when(animalTypeRepository.findById(testId)).thenReturn(Optional.of(testAnimalType));

        // When
        AnimalTypeDTO result = animalTypeService.getAnimalTypeById(testId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTypeName()).isEqualTo(TEST_TYPE_NAME);
        assertThat(result.getDescription()).isEqualTo(TEST_DESCRIPTION);

        verify(animalTypeRepository, times(1)).findById(testId);
    }

    @Test
    @DisplayName("Should throw exception when animal type not found by id")
    void testGetAnimalTypeByIdNotFound() {
        // Given
        when(animalTypeRepository.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> animalTypeService.getAnimalTypeById(testId))
                .isInstanceOf(ConfigurationNotFoundException.class)
                .hasMessageContaining("not found");

        verify(animalTypeRepository, times(1)).findById(testId);
    }

    @Test
    @DisplayName("Should update animal type successfully")
    void testUpdateAnimalType() {
        // Given
        AnimalTypeDTO updateDTO = new AnimalTypeDTO();
        updateDTO.setTypeName("Updated Cattle");
        updateDTO.setDescription("Updated description");
        updateDTO.setActive(false);

        when(animalTypeRepository.findById(testId)).thenReturn(Optional.of(testAnimalType));
        when(animalTypeRepository.existsByTypeNameIgnoreCase(anyString())).thenReturn(false);
        when(animalTypeRepository.save(any(AnimalType.class))).thenReturn(testAnimalType);

        // When
        AnimalTypeDTO result = animalTypeService.updateAnimalType(testId, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(animalTypeRepository, times(1)).findById(testId);
        verify(animalTypeRepository, times(1)).save(any(AnimalType.class));
    }

    @Test
    @DisplayName("Should delete animal type successfully")
    void testDeleteAnimalType() {
        // Given
        when(animalTypeRepository.findById(testId)).thenReturn(Optional.of(testAnimalType));
        when(animalTypeRepository.countAnimalsByAnimalTypeId(testId)).thenReturn(0L);
        doNothing().when(animalTypeRepository).delete(any(AnimalType.class));

        // When
        animalTypeService.deleteAnimalType(testId);

        // Then
        verify(animalTypeRepository, times(1)).findById(testId);
        verify(animalTypeRepository, times(1)).countAnimalsByAnimalTypeId(testId);
        verify(animalTypeRepository, times(1)).delete(testAnimalType);
    }

    @Test
    @DisplayName("Should throw exception when deleting animal type in use")
    void testDeleteAnimalTypeInUse() {
        // Given
        when(animalTypeRepository.findById(testId)).thenReturn(Optional.of(testAnimalType));
        when(animalTypeRepository.countAnimalsByAnimalTypeId(testId)).thenReturn(3L);

        // When & Then
        assertThatThrownBy(() -> animalTypeService.deleteAnimalType(testId))
                .isInstanceOf(ConfigurationInUseException.class)
                .hasMessageContaining("in use");

        verify(animalTypeRepository, times(1)).findById(testId);
        verify(animalTypeRepository, times(1)).countAnimalsByAnimalTypeId(testId);
        verify(animalTypeRepository, never()).delete(any(AnimalType.class));
    }

    @Test
    @DisplayName("Should toggle animal type status successfully")
    void testToggleAnimalTypeStatus() {
        // Given
        when(animalTypeRepository.findById(testId)).thenReturn(Optional.of(testAnimalType));
        when(animalTypeRepository.save(any(AnimalType.class))).thenReturn(testAnimalType);

        // When
        AnimalTypeDTO result = animalTypeService.toggleAnimalTypeStatus(testId);

        // Then
        assertThat(result).isNotNull();
        verify(animalTypeRepository, times(1)).findById(testId);
        verify(animalTypeRepository, times(1)).save(any(AnimalType.class));
    }

    @Test
    @DisplayName("Should get active animal types only")
    void testGetActiveAnimalTypes() {
        // Given
        when(animalTypeRepository.findByIsActiveTrueOrderByTypeNameAsc())
                .thenReturn(Arrays.asList(testAnimalType));

        // When
        List<AnimalTypeDTO> result = animalTypeService.getActiveAnimalTypes();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();

        verify(animalTypeRepository, times(1)).findByIsActiveTrueOrderByTypeNameAsc();
    }

    @Test
    @DisplayName("Should get animal usage count")
    void testGetAnimalUsageCount() {
        // Given
        Long expectedCount = 15L;
        when(animalTypeRepository.countAnimalsByAnimalTypeId(testId)).thenReturn(expectedCount);

        // When
        Long result = animalTypeService.getAnimalUsageCount(testId);

        // Then
        assertThat(result).isEqualTo(expectedCount);
        verify(animalTypeRepository, times(1)).countAnimalsByAnimalTypeId(testId);
    }
}
