package com.adrs.service.impl;

import com.adrs.dto.ChartDataDTO;
import com.adrs.dto.DashboardStatsDTO;
import com.adrs.dto.DistrictUserDistributionDTO;
import com.adrs.dto.ProvinceUserDistributionDTO;
import com.adrs.model.District;
import com.adrs.model.Province;
import com.adrs.model.User;
import com.adrs.repository.*;
import com.adrs.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of DashboardService.
 * Aggregates statistics from multiple repositories for dashboard display.
 */
@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd");
    
    private final UserRepository userRepository;
    private final FarmTypeRepository farmTypeRepository;
    private final AnimalTypeRepository animalTypeRepository;
    private final DiseaseRepository diseaseRepository;
    private final FarmRepository farmRepository;
    private final AnimalRepository animalRepository;
    private final DiseaseReportRepository diseaseReportRepository;

    public DashboardServiceImpl(UserRepository userRepository,
                                FarmTypeRepository farmTypeRepository,
                                AnimalTypeRepository animalTypeRepository,
                                DiseaseRepository diseaseRepository,
                                FarmRepository farmRepository,
                                AnimalRepository animalRepository,
                                DiseaseReportRepository diseaseReportRepository) {
        this.userRepository = userRepository;
        this.farmTypeRepository = farmTypeRepository;
        this.animalTypeRepository = animalTypeRepository;
        this.diseaseRepository = diseaseRepository;
        this.farmRepository = farmRepository;
        this.animalRepository = animalRepository;
        this.diseaseReportRepository = diseaseReportRepository;
    }

    @Override
    public DashboardStatsDTO getDashboardStatistics() {
        logger.debug("Fetching comprehensive dashboard statistics");
        
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        // User statistics
        stats.setTotalUsers(userRepository.count());
        stats.setActiveUsers(userRepository.countByActiveTrue());
        stats.setInactiveUsers(userRepository.countByActiveFalse());
        stats.setAdminUsers(userRepository.countByRole(User.Role.ADMIN));
        stats.setVeterinaryOfficerUsers(userRepository.countByRole(User.Role.VETERINARY_OFFICER));
        
        // Configuration statistics
        stats.setTotalFarmTypes(farmTypeRepository.count());
        stats.setActiveFarmTypes(farmTypeRepository.countByIsActiveTrue());
        stats.setTotalAnimalTypes(animalTypeRepository.count());
        stats.setActiveAnimalTypes(animalTypeRepository.countByIsActiveTrue());
        stats.setTotalDiseases(diseaseRepository.count());
        stats.setActiveDiseases(diseaseRepository.countByIsActiveTrue());
        stats.setNotifiableDiseases(diseaseRepository.countByIsNotifiableTrue());
        
        // System-wide statistics
        stats.setTotalFarms(farmRepository.count());
        stats.setActiveFarms(farmRepository.countByIsActiveTrue());
        stats.setTotalAnimals(animalRepository.count());
        stats.setTotalDiseaseReports(diseaseReportRepository.count());
        stats.setConfirmedDiseaseReports(diseaseReportRepository.countByIsConfirmedTrue());
        stats.setPendingDiseaseReports(diseaseReportRepository.countByIsConfirmedFalse());
        
        logger.info("Dashboard statistics compiled: {} users, {} farms, {} animals, {} disease reports",
                stats.getTotalUsers(), stats.getTotalFarms(), stats.getTotalAnimals(), stats.getTotalDiseaseReports());
        
        return stats;
    }

    @Override
    public ChartDataDTO getUserRoleDistribution() {
        logger.debug("Fetching user role distribution");
        
        Long adminCount = userRepository.countByRole(User.Role.ADMIN);
        Long vetCount = userRepository.countByRole(User.Role.VETERINARY_OFFICER);
        
        List<String> labels = Arrays.asList("Admin", "Veterinary Officer");
        List<Long> data = Arrays.asList(adminCount, vetCount);
        
        ChartDataDTO chart = new ChartDataDTO(labels, data, "pie");
        logger.debug("User role distribution: Admin={}, VetOfficer={}", adminCount, vetCount);
        
        return chart;
    }

    @Override
    public ChartDataDTO getUserStatusDistribution() {
        logger.debug("Fetching user status distribution");
        
        Long activeCount = userRepository.countByActiveTrue();
        Long inactiveCount = userRepository.countByActiveFalse();
        
        List<String> labels = Arrays.asList("Active", "Inactive");
        List<Long> data = Arrays.asList(activeCount, inactiveCount);
        
        return new ChartDataDTO(labels, data, "pie");
    }

    @Override
    public ChartDataDTO getConfigurationStatusDistribution() {
        logger.debug("Fetching configuration status distribution");
        
        Long activeFarmTypes = farmTypeRepository.countByIsActiveTrue();
        Long activeAnimalTypes = animalTypeRepository.countByIsActiveTrue();
        Long activeDiseases = diseaseRepository.countByIsActiveTrue();
        
        Long inactiveFarmTypes = farmTypeRepository.countByIsActiveFalse();
        Long inactiveAnimalTypes = animalTypeRepository.countByIsActiveFalse();
        Long inactiveDiseases = diseaseRepository.countByIsActiveFalse();
        
        Long totalActive = activeFarmTypes + activeAnimalTypes + activeDiseases;
        Long totalInactive = inactiveFarmTypes + inactiveAnimalTypes + inactiveDiseases;
        
        List<String> labels = Arrays.asList("Active", "Inactive");
        List<Long> data = Arrays.asList(totalActive, totalInactive);
        
        return new ChartDataDTO(labels, data, "pie");
    }

    @Override
    public ChartDataDTO getFarmTypeDistribution() {
        logger.debug("Fetching farm type distribution");
        
        // This would require a query to count farms by farm type
        // For now, return basic data structure
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        
        // TODO: Implement actual farm type distribution query
        // This would require joining farms with farm_types and counting
        
        return new ChartDataDTO(labels, data, "pie");
    }

    @Override
    public ChartDataDTO getDiseaseSeverityDistribution() {
        logger.debug("Fetching disease severity distribution");
        
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        
        // Count diseases by severity
        for (var severity : com.adrs.model.Disease.Severity.values()) {
            List<com.adrs.model.Disease> diseases = diseaseRepository.findBySeverity(severity);
            labels.add(severity.name());
            data.add((long) diseases.size());
        }
        
        return new ChartDataDTO(labels, data, "pie");
    }

    @Override
    public ChartDataDTO getUserRegistrationTrend(int months) {
        logger.debug("Fetching user registration trend for {} months", months);
        
        LocalDateTime startDate = LocalDateTime.now().minusMonths(months);
        
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        
        // Generate monthly labels
        for (int i = months - 1; i >= 0; i--) {
            LocalDateTime monthDate = LocalDateTime.now().minusMonths(i);
            labels.add(monthDate.format(DateTimeFormatter.ofPattern("MMM yyyy")));
            
            // Count users created in this month
            LocalDateTime monthStart = monthDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime monthEnd = monthStart.plusMonths(1);
            
            long count = userRepository.findAll().stream()
                    .filter(u -> u.getCreatedAt() != null &&
                            !u.getCreatedAt().isBefore(monthStart) &&
                            u.getCreatedAt().isBefore(monthEnd))
                    .count();
            data.add(count);
        }
        
        return new ChartDataDTO(labels, data, "line");
    }

    @Override
    public ChartDataDTO getFarmRegistrationTrend(int months) {
        logger.debug("Fetching farm registration trend for {} months", months);
        
        LocalDateTime startDate = LocalDateTime.now().minusMonths(months);
        
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        
        // Generate monthly labels and data
        for (int i = months - 1; i >= 0; i--) {
            LocalDateTime monthDate = LocalDateTime.now().minusMonths(i);
            labels.add(monthDate.format(DateTimeFormatter.ofPattern("MMM yyyy")));
            
            LocalDateTime monthStart = monthDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            Long count = farmRepository.countByCreatedAtAfter(monthStart);
            data.add(count);
        }
        
        return new ChartDataDTO(labels, data, "line");
    }

    @Override
    public ChartDataDTO getDiseaseReportTrend(int months) {
        logger.debug("Fetching disease report trend for {} months", months);
        
        LocalDateTime startDate = LocalDateTime.now().minusMonths(months);
        
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        
        // Generate monthly labels and data
        for (int i = months - 1; i >= 0; i--) {
            LocalDateTime monthDate = LocalDateTime.now().minusMonths(i);
            labels.add(monthDate.format(DateTimeFormatter.ofPattern("MMM yyyy")));
            
            LocalDateTime monthStart = monthDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            Long count = diseaseReportRepository.countByCreatedAtAfter(monthStart);
            data.add(count);
        }
        
        return new ChartDataDTO(labels, data, "line");
    }

    @Override
    public Map<String, Long> getSummaryCounts() {
        logger.debug("Fetching summary counts");
        
        Map<String, Long> counts = new HashMap<>();
        counts.put("adminCount", userRepository.countByRole(User.Role.ADMIN));
        counts.put("vetCount", userRepository.countByRole(User.Role.VETERINARY_OFFICER));
        counts.put("totalFarms", farmRepository.count());
        counts.put("totalAnimals", animalRepository.count());
        counts.put("totalDiseaseReports", diseaseReportRepository.count());
        counts.put("activeFarmTypes", farmTypeRepository.countByIsActiveTrue());
        counts.put("activeAnimalTypes", animalTypeRepository.countByIsActiveTrue());
        counts.put("activeDiseases", diseaseRepository.countByIsActiveTrue());
        counts.put("notifiableDiseases", diseaseRepository.countByIsNotifiableTrue());
        
        return counts;
    }

    @Override
    public List<ProvinceUserDistributionDTO> getUserDistributionByProvince(User.Role role) {
        logger.debug("Fetching user distribution by province" + (role != null ? " for role: " + role : ""));
        
        List<ProvinceUserDistributionDTO> distribution = new ArrayList<>();
        
        // Get province-level counts
        List<Object[]> provinceCounts = (role != null) 
            ? userRepository.countUsersByProvinceAndRole(role)
            : userRepository.countUsersByProvince();
        
        // Build distribution with district breakdown for each province
        for (Object[] row : provinceCounts) {
            Province province = (Province) row[0];
            Long userCount = (Long) row[1];
            
            // Get district breakdown for this province
            Map<String, Long> districtBreakdown = getDistrictBreakdown(province, role);
            
            ProvinceUserDistributionDTO dto = new ProvinceUserDistributionDTO(
                province.name(),
                province.getDisplayName(),
                userCount,
                districtBreakdown
            );
            
            distribution.add(dto);
        }
        
        // Sort by province name for consistent ordering
        distribution.sort(Comparator.comparing(ProvinceUserDistributionDTO::getProvince));
        
        logger.info("Fetched user distribution for {} provinces" + (role != null ? " (role: " + role + ")" : ""), 
                   distribution.size());
        
        return distribution;
    }

    /**
     * Helper method to get district breakdown for a specific province.
     *
     * @param province the province to get district breakdown for
     * @param role     the optional role filter
     * @return map of district display names to user counts
     */
    private Map<String, Long> getDistrictBreakdown(Province province, User.Role role) {
        List<Object[]> districtCounts = (role != null)
            ? userRepository.countUsersByDistrictAndRole(province, role)
            : userRepository.countUsersByDistrict(province);
        
        Map<String, Long> breakdown = new LinkedHashMap<>();
        for (Object[] row : districtCounts) {
            District district = (District) row[0];
            Long count = (Long) row[1];
            breakdown.put(district.getDisplayName(), count);
        }
        
        return breakdown;
    }

    @Override
    public List<DistrictUserDistributionDTO> getUserDistributionByDistrict(User.Role role) {
        logger.debug("Fetching user distribution by district" + (role != null ? " for role: " + role : ""));
        
        List<DistrictUserDistributionDTO> distribution = new ArrayList<>();
        
        // Iterate through all 25 districts in the District enum
        for (District district : District.values()) {
            Long count = (role == null) 
                ? userRepository.countActiveUsersByDistrict(district)
                : userRepository.countActiveUsersByDistrictAndRole(district, role);
            
            DistrictUserDistributionDTO dto = new DistrictUserDistributionDTO(
                district.name(),
                district.getDisplayName(),
                count
            );
            
            distribution.add(dto);
        }
        
        // Sort by display name for consistent ordering
        distribution.sort(Comparator.comparing(DistrictUserDistributionDTO::getDisplayName));
        
        logger.info("Fetched user distribution for {} districts" + (role != null ? " (role: " + role + ")" : ""), 
                   distribution.size());
        
        return distribution;
    }

    @Override
    public List<User> getUsersByDistrictAndRole(District district, User.Role role) {
        logger.debug("Fetching users for district: {} with role: {}", 
                    district != null ? district.name() : "null", 
                    role != null ? role.name() : "all");
        
        if (district == null) {
            logger.warn("Attempted to fetch users with null district");
            return Collections.emptyList();
        }
        
        List<User> users = (role == null)
            ? userRepository.findActiveUsersByDistrict(district)
            : userRepository.findActiveUsersByDistrictAndRole(district, role);
        
        logger.info("Found {} users in district {} {}", 
                   users.size(), 
                   district.getDisplayName(),
                   role != null ? "with role " + role : "");
        
        return users;
    }
}
