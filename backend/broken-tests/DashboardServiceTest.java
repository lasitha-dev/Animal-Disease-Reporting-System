package com.adrs.test.service;

import com.adrs.dto.ChartDataDTO;
import com.adrs.dto.DashboardStatsDTO;
import com.adrs.model.Disease;
import com.adrs.model.User;
import com.adrs.repository.*;
import com.adrs.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DashboardServiceImpl.
 * Tests business logic with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Dashboard Service Tests")
class DashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private DiseaseReportRepository diseaseReportRepository;

    @Mock
    private FarmTypeRepository farmTypeRepository;

    @Mock
    private AnimalTypeRepository animalTypeRepository;

    @Mock
    private DiseaseRepository diseaseRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        // Setup is minimal as we'll mock specific calls per test
    }

    @Test
    @DisplayName("Should get dashboard statistics")
    void testGetDashboardStatistics() {
        // Given
        when(userRepository.count()).thenReturn(25L);
        when(userRepository.countByRole(User.Role.ADMIN)).thenReturn(3L);
        when(userRepository.countByRole(User.Role.VETERINARY_OFFICER)).thenReturn(10L);
        when(userRepository.countByRole(User.Role.FARMER)).thenReturn(12L);
        when(userRepository.countByIsActiveTrue()).thenReturn(22L);
        when(userRepository.countByIsActiveFalse()).thenReturn(3L);

        when(farmRepository.count()).thenReturn(50L);
        when(animalRepository.count()).thenReturn(200L);
        when(diseaseReportRepository.count()).thenReturn(15L);

        when(farmTypeRepository.count()).thenReturn(8L);
        when(farmTypeRepository.countByIsActiveTrue()).thenReturn(7L);
        when(animalTypeRepository.count()).thenReturn(12L);
        when(animalTypeRepository.countByIsActiveTrue()).thenReturn(10L);
        when(diseaseRepository.count()).thenReturn(25L);
        when(diseaseRepository.countByIsActiveTrue()).thenReturn(20L);
        when(diseaseRepository.countByIsNotifiableTrue()).thenReturn(10L);

        // When
        DashboardStatsDTO result = dashboardService.getDashboardStatistics();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalUsers()).isEqualTo(25L);
        assertThat(result.getTotalFarms()).isEqualTo(50L);
        assertThat(result.getTotalAnimals()).isEqualTo(200L);
        assertThat(result.getTotalDiseaseReports()).isEqualTo(15L);
        assertThat(result.getTotalFarmTypes()).isEqualTo(8L);
        assertThat(result.getTotalAnimalTypes()).isEqualTo(12L);
        assertThat(result.getTotalDiseases()).isEqualTo(25L);

        verify(userRepository, times(1)).count();
        verify(farmRepository, times(1)).count();
        verify(animalRepository, times(1)).count();
        verify(diseaseReportRepository, times(1)).count();
    }

    @Test
    @DisplayName("Should get user role distribution")
    void testGetUserRoleDistribution() {
        // Given
        when(userRepository.countByRole(User.Role.ADMIN)).thenReturn(3L);
        when(userRepository.countByRole(User.Role.VETERINARY_OFFICER)).thenReturn(10L);
        when(userRepository.countByRole(User.Role.FARMER)).thenReturn(12L);

        // When
        ChartDataDTO result = dashboardService.getUserRoleDistribution();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLabels()).containsExactly("Admin", "Veterinary Officer", "Farmer");
        assertThat(result.getData()).containsExactly(3L, 10L, 12L);

        verify(userRepository, times(1)).countByRole(User.Role.ADMIN);
        verify(userRepository, times(1)).countByRole(User.Role.VETERINARY_OFFICER);
        verify(userRepository, times(1)).countByRole(User.Role.FARMER);
    }

    @Test
    @DisplayName("Should get user status distribution")
    void testGetUserStatusDistribution() {
        // Given
        when(userRepository.countByIsActiveTrue()).thenReturn(22L);
        when(userRepository.countByIsActiveFalse()).thenReturn(3L);

        // When
        ChartDataDTO result = dashboardService.getUserStatusDistribution();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLabels()).containsExactly("Active", "Inactive");
        assertThat(result.getData()).containsExactly(22L, 3L);

        verify(userRepository, times(1)).countByIsActiveTrue();
        verify(userRepository, times(1)).countByIsActiveFalse();
    }

    @Test
    @DisplayName("Should get disease severity distribution")
    void testGetDiseaseSeverityDistribution() {
        // Given
        when(diseaseRepository.countBySeverity(Disease.Severity.LOW)).thenReturn(5L);
        when(diseaseRepository.countBySeverity(Disease.Severity.MEDIUM)).thenReturn(10L);
        when(diseaseRepository.countBySeverity(Disease.Severity.HIGH)).thenReturn(7L);
        when(diseaseRepository.countBySeverity(Disease.Severity.CRITICAL)).thenReturn(3L);

        // When
        ChartDataDTO result = dashboardService.getDiseaseSeverityDistribution();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLabels()).containsExactly("Low", "Medium", "High", "Critical");
        assertThat(result.getData()).containsExactly(5L, 10L, 7L, 3L);

        verify(diseaseRepository, times(1)).countBySeverity(Disease.Severity.LOW);
        verify(diseaseRepository, times(1)).countBySeverity(Disease.Severity.MEDIUM);
        verify(diseaseRepository, times(1)).countBySeverity(Disease.Severity.HIGH);
        verify(diseaseRepository, times(1)).countBySeverity(Disease.Severity.CRITICAL);
    }

    @Test
    @DisplayName("Should get farm type distribution")
    void testGetFarmTypeDistribution() {
        // Given
        Map<String, Long> mockData = new HashMap<>();
        mockData.put("Dairy Farm", 15L);
        mockData.put("Beef Farm", 10L);
        mockData.put("Poultry Farm", 8L);

        when(farmTypeRepository.countFarmsByFarmType()).thenReturn(mockData);

        // When
        ChartDataDTO result = dashboardService.getFarmTypeDistribution();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLabels()).hasSize(3);
        assertThat(result.getData()).hasSize(3);

        verify(farmTypeRepository, times(1)).countFarmsByFarmType();
    }

    @Test
    @DisplayName("Should get user registration trend for specified months")
    void testGetUserRegistrationTrend() {
        // Given
        int months = 6;
        LocalDate startDate = LocalDate.now().minusMonths(months);

        Object[] data1 = {2025, 5, 5L};
        Object[] data2 = {2025, 6, 8L};
        List<Object[]> mockData = Arrays.asList(data1, data2);

        when(userRepository.countUsersByMonthSince(any(LocalDate.class))).thenReturn(mockData);

        // When
        ChartDataDTO result = dashboardService.getUserRegistrationTrend(months);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLabels()).isNotEmpty();
        assertThat(result.getData()).isNotEmpty();

        verify(userRepository, times(1)).countUsersByMonthSince(any(LocalDate.class));
    }

    @Test
    @DisplayName("Should get farm registration trend")
    void testGetFarmRegistrationTrend() {
        // Given
        int months = 6;
        Object[] data1 = {2025, 5, 10L};
        Object[] data2 = {2025, 6, 15L};
        List<Object[]> mockData = Arrays.asList(data1, data2);

        when(farmRepository.countFarmsByMonthSince(any(LocalDate.class))).thenReturn(mockData);

        // When
        ChartDataDTO result = dashboardService.getFarmRegistrationTrend(months);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLabels()).isNotEmpty();
        assertThat(result.getData()).isNotEmpty();

        verify(farmRepository, times(1)).countFarmsByMonthSince(any(LocalDate.class));
    }

    @Test
    @DisplayName("Should get disease report trend")
    void testGetDiseaseReportTrend() {
        // Given
        int months = 6;
        Object[] data1 = {2025, 5, 3L};
        Object[] data2 = {2025, 6, 5L};
        List<Object[]> mockData = Arrays.asList(data1, data2);

        when(diseaseReportRepository.countReportsByMonthSince(any(LocalDate.class)))
                .thenReturn(mockData);

        // When
        ChartDataDTO result = dashboardService.getDiseaseReportTrend(months);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLabels()).isNotEmpty();
        assertThat(result.getData()).isNotEmpty();

        verify(diseaseReportRepository, times(1)).countReportsByMonthSince(any(LocalDate.class));
    }

    @Test
    @DisplayName("Should use default 6 months for trends when null provided")
    void testGetTrendWithNullMonths() {
        // Given
        Object[] data1 = {2025, 6, 5L};
        List<Object[]> mockData = Arrays.asList(data1);
        when(userRepository.countUsersByMonthSince(any(LocalDate.class))).thenReturn(mockData);

        // When
        ChartDataDTO result = dashboardService.getUserRegistrationTrend(null);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository, times(1)).countUsersByMonthSince(any(LocalDate.class));
    }

    @Test
    @DisplayName("Should get configuration status distribution")
    void testGetConfigurationStatusDistribution() {
        // Given
        when(farmTypeRepository.countByIsActiveTrue()).thenReturn(7L);
        when(farmTypeRepository.countByIsActiveFalse()).thenReturn(1L);
        when(animalTypeRepository.countByIsActiveTrue()).thenReturn(10L);
        when(animalTypeRepository.countByIsActiveFalse()).thenReturn(2L);
        when(diseaseRepository.countByIsActiveTrue()).thenReturn(20L);
        when(diseaseRepository.countByIsActiveFalse()).thenReturn(5L);

        // When
        ChartDataDTO result = dashboardService.getConfigurationStatusDistribution();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLabels()).contains("Farm Types Active", "Animal Types Active", "Diseases Active");
        assertThat(result.getData()).hasSize(6);

        verify(farmTypeRepository, times(1)).countByIsActiveTrue();
        verify(animalTypeRepository, times(1)).countByIsActiveTrue();
        verify(diseaseRepository, times(1)).countByIsActiveTrue();
    }
}
