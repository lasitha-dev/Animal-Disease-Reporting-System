package com.adrs.test.service;

import com.adrs.dto.FarmTypeDTO;
import com.adrs.exception.ConfigurationInUseException;
import com.adrs.exception.ConfigurationNotFoundException;
import com.adrs.model.FarmType;
import com.adrs.repository.FarmTypeRepository;
import com.adrs.service.impl.FarmTypeServiceImpl;
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
 * Unit tests for FarmTypeServiceImpl.
 * Tests business logic with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Farm Type Service Tests")
class FarmTypeServiceTest {

    private static final String TEST_TYPE_NAME = "Dairy Farm";
    private static final String TEST_DESCRIPTION = "Farm specializing in dairy production";
    private static final String TEST_USERNAME = "testuser";

    @Mock
    private FarmTypeRepository farmTypeRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private FarmTypeServiceImpl farmTypeService;

    private FarmType testFarmType;
    private FarmTypeDTO testFarmTypeDTO;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        testFarmType = new FarmType();
        testFarmType.setId(testId);
        testFarmType.setTypeName(TEST_TYPE_NAME);
        testFarmType.setDescription(TEST_DESCRIPTION);
        testFarmType.setIsActive(true);
        testFarmType.setCreatedAt(LocalDateTime.now());

        testFarmTypeDTO = new FarmTypeDTO();
        testFarmTypeDTO.setTypeName(TEST_TYPE_NAME);
        testFarmTypeDTO.setDescription(TEST_DESCRIPTION);
        testFarmTypeDTO.setActive(true);

        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(TEST_USERNAME);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Should create farm type successfully")
    void testCreateFarmType() {
        // Given
        when(farmTypeRepository.existsByTypeNameIgnoreCase(anyString())).thenReturn(false);
        when(farmTypeRepository.save(any(FarmType.class))).thenReturn(testFarmType);

        // When
        FarmTypeDTO result = farmTypeService.createFarmType(testFarmTypeDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTypeName()).isEqualTo(TEST_TYPE_NAME);
        assertThat(result.getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertThat(result.isActive()).isTrue();

        verify(farmTypeRepository, times(1)).existsByTypeNameIgnoreCase(TEST_TYPE_NAME);
        verify(farmTypeRepository, times(1)).save(any(FarmType.class));
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate farm type")
    void testCreateFarmType_Duplicate() {
        // Given
        when(farmTypeRepository.existsByTypeNameIgnoreCase(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> farmTypeService.createFarmType(testFarmTypeDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(farmTypeRepository, times(1)).existsByTypeNameIgnoreCase(TEST_TYPE_NAME);
        verify(farmTypeRepository, never()).save(any(FarmType.class));
    }

    @Test
    @DisplayName("Should get all farm types")
    void testGetAllFarmTypes() {
        // Given
        FarmType farmType2 = new FarmType();
        farmType2.setId(UUID.randomUUID());
        farmType2.setTypeName("Beef Farm");
        farmType2.setDescription("Farm specializing in beef production");
        farmType2.setIsActive(true);

        List<FarmType> farmTypes = Arrays.asList(testFarmType, farmType2);
        when(farmTypeRepository.findAllByOrderByTypeNameAsc()).thenReturn(farmTypes);

        // When
        List<FarmTypeDTO> result = farmTypeService.getAllFarmTypes();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTypeName()).isEqualTo(TEST_TYPE_NAME);
        assertThat(result.get(1).getTypeName()).isEqualTo("Beef Farm");

        verify(farmTypeRepository, times(1)).findAllByOrderByTypeNameAsc();
    }

    @Test
    @DisplayName("Should get farm type by id")
    void testGetFarmTypeById() {
        // Given
        when(farmTypeRepository.findById(testId)).thenReturn(Optional.of(testFarmType));

        // When
        FarmTypeDTO result = farmTypeService.getFarmTypeById(testId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTypeName()).isEqualTo(TEST_TYPE_NAME);
        assertThat(result.getDescription()).isEqualTo(TEST_DESCRIPTION);

        verify(farmTypeRepository, times(1)).findById(testId);
    }

    @Test
    @DisplayName("Should throw exception when farm type not found by id")
    void testGetFarmTypeById_NotFound() {
        // Given
        when(farmTypeRepository.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> farmTypeService.getFarmTypeById(testId))
                .isInstanceOf(ConfigurationNotFoundException.class)
                .hasMessageContaining("not found");

        verify(farmTypeRepository, times(1)).findById(testId);
    }

    @Test
    @DisplayName("Should update farm type successfully")
    void testUpdateFarmType() {
        // Given
        FarmTypeDTO updateDTO = new FarmTypeDTO();
        updateDTO.setTypeName("Updated Dairy Farm");
        updateDTO.setDescription("Updated description");
        updateDTO.setActive(false);

        when(farmTypeRepository.findById(testId)).thenReturn(Optional.of(testFarmType));
        when(farmTypeRepository.existsByTypeNameIgnoreCase(anyString())).thenReturn(false);
        when(farmTypeRepository.save(any(FarmType.class))).thenReturn(testFarmType);

        // When
        FarmTypeDTO result = farmTypeService.updateFarmType(testId, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(farmTypeRepository, times(1)).findById(testId);
        verify(farmTypeRepository, times(1)).save(any(FarmType.class));
    }

    @Test
    @DisplayName("Should throw exception when updating to duplicate name")
    void testUpdateFarmType_DuplicateName() {
        // Given
        FarmTypeDTO updateDTO = new FarmTypeDTO();
        updateDTO.setTypeName("Existing Farm Type");
        updateDTO.setDescription("Updated description");

        when(farmTypeRepository.findById(testId)).thenReturn(Optional.of(testFarmType));
        when(farmTypeRepository.existsByTypeNameIgnoreCase(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> farmTypeService.updateFarmType(testId, updateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(farmTypeRepository, times(1)).findById(testId);
        verify(farmTypeRepository, times(1)).existsByTypeNameIgnoreCase(anyString());
        verify(farmTypeRepository, never()).save(any(FarmType.class));
    }

    @Test
    @DisplayName("Should delete farm type successfully")
    void testDeleteFarmType() {
        // Given
        when(farmTypeRepository.findById(testId)).thenReturn(Optional.of(testFarmType));
        when(farmTypeRepository.countFarmsByFarmTypeId(testId)).thenReturn(0L);
        doNothing().when(farmTypeRepository).delete(any(FarmType.class));

        // When
        farmTypeService.deleteFarmType(testId);

        // Then
        verify(farmTypeRepository, times(1)).findById(testId);
        verify(farmTypeRepository, times(1)).countFarmsByFarmTypeId(testId);
        verify(farmTypeRepository, times(1)).delete(testFarmType);
    }

    @Test
    @DisplayName("Should throw exception when deleting farm type in use")
    void testDeleteFarmType_InUse() {
        // Given
        when(farmTypeRepository.findById(testId)).thenReturn(Optional.of(testFarmType));
        when(farmTypeRepository.countFarmsByFarmTypeId(testId)).thenReturn(5L);

        // When & Then
        assertThatThrownBy(() -> farmTypeService.deleteFarmType(testId))
                .isInstanceOf(ConfigurationInUseException.class)
                .hasMessageContaining("in use");

        verify(farmTypeRepository, times(1)).findById(testId);
        verify(farmTypeRepository, times(1)).countFarmsByFarmTypeId(testId);
        verify(farmTypeRepository, never()).delete(any(FarmType.class));
    }

    @Test
    @DisplayName("Should toggle farm type status successfully")
    void testToggleFarmTypeStatus() {
        // Given
        when(farmTypeRepository.findById(testId)).thenReturn(Optional.of(testFarmType));
        when(farmTypeRepository.save(any(FarmType.class))).thenReturn(testFarmType);

        // When
        FarmTypeDTO result = farmTypeService.toggleFarmTypeStatus(testId);

        // Then
        assertThat(result).isNotNull();
        verify(farmTypeRepository, times(1)).findById(testId);
        verify(farmTypeRepository, times(1)).save(any(FarmType.class));
    }

    @Test
    @DisplayName("Should get active farm types only")
    void testGetActiveFarmTypes() {
        // Given
        when(farmTypeRepository.findByIsActiveTrueOrderByTypeNameAsc())
                .thenReturn(Arrays.asList(testFarmType));

        // When
        List<FarmTypeDTO> result = farmTypeService.getActiveFarmTypes();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();

        verify(farmTypeRepository, times(1)).findByIsActiveTrueOrderByTypeNameAsc();
    }

    @Test
    @DisplayName("Should get farm usage count")
    void testGetFarmUsageCount() {
        // Given
        Long expectedCount = 10L;
        when(farmTypeRepository.countFarmsByFarmTypeId(testId)).thenReturn(expectedCount);

        // When
        Long result = farmTypeService.getFarmUsageCount(testId);

        // Then
        assertThat(result).isEqualTo(expectedCount);
        verify(farmTypeRepository, times(1)).countFarmsByFarmTypeId(testId);
    }
}
