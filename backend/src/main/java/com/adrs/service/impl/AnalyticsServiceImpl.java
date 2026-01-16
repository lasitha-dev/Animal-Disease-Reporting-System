package com.adrs.service.impl;

import com.adrs.dto.AnalyticsRequestDTO;
import com.adrs.dto.AnalyticsRequestDTO.GroupBy;
import com.adrs.dto.AnalyticsRequestDTO.MetricType;
import com.adrs.dto.AnalyticsResponseDTO;
import com.adrs.dto.AnalyticsResponseDTO.DatasetDTO;
import com.adrs.model.Disease;
import com.adrs.model.DiseaseReport;
import com.adrs.repository.DiseaseReportRepository;
import com.adrs.repository.DiseaseRepository;
import com.adrs.service.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of AnalyticsService.
 * Provides disease trend analytics with aggregation by week, month, or year.
 */
@Service
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceImpl.class);

    private final DiseaseReportRepository diseaseReportRepository;
    private final DiseaseRepository diseaseRepository;

    public AnalyticsServiceImpl(DiseaseReportRepository diseaseReportRepository,
                                DiseaseRepository diseaseRepository) {
        this.diseaseReportRepository = diseaseReportRepository;
        this.diseaseRepository = diseaseRepository;
    }

    @Override
    public AnalyticsResponseDTO getDiseaseTrends(AnalyticsRequestDTO request) {
        logger.info("Getting disease trends: animalTypeId={}, diseaseIds={}, startDate={}, endDate={}, groupBy={}, metricType={}",
                request.getAnimalTypeId(), request.getDiseaseIds(), request.getStartDate(), 
                request.getEndDate(), request.getGroupBy(), request.getMetricType());

        // Fetch disease reports based on filters
        List<DiseaseReport> reports = fetchReports(request);
        logger.debug("Fetched {} disease reports", reports.size());

        // Get all unique diseases from the reports or from disease IDs
        Map<UUID, Disease> diseaseMap = getDiseaseMap(request, reports);
        
        // Generate time period labels
        List<String> labels = generateLabels(request.getStartDate(), request.getEndDate(), request.getGroupBy());
        
        // Create datasets for each disease
        List<DatasetDTO> datasets = createDatasets(reports, diseaseMap, labels, 
                request.getStartDate(), request.getEndDate(), request.getGroupBy(), request.getMetricType());

        AnalyticsResponseDTO response = new AnalyticsResponseDTO();
        response.setLabels(labels);
        response.setDatasets(datasets);

        logger.info("Returning analytics with {} labels and {} datasets", labels.size(), datasets.size());
        return response;
    }

    /**
     * Fetch disease reports based on the request filters.
     */
    private List<DiseaseReport> fetchReports(AnalyticsRequestDTO request) {
        UUID animalTypeId = request.getAnimalTypeId();
        List<UUID> diseaseIds = request.getDiseaseIds();
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        boolean hasAnimalTypeFilter = animalTypeId != null;
        boolean hasDiseaseFilter = diseaseIds != null && !diseaseIds.isEmpty();

        if (hasAnimalTypeFilter && hasDiseaseFilter) {
            return diseaseReportRepository.findByAnimalTypeIdAndDiseaseIdInAndReportDateBetween(
                    animalTypeId, diseaseIds, startDate, endDate);
        } else if (hasAnimalTypeFilter) {
            return diseaseReportRepository.findByAnimalTypeIdAndReportDateBetween(
                    animalTypeId, startDate, endDate);
        } else if (hasDiseaseFilter) {
            return diseaseReportRepository.findByDiseaseIdInAndReportDateBetween(
                    diseaseIds, startDate, endDate);
        } else {
            return diseaseReportRepository.findByReportDateBetween(startDate, endDate);
        }
    }

    /**
     * Get a map of disease ID to Disease entity for the relevant diseases.
     */
    private Map<UUID, Disease> getDiseaseMap(AnalyticsRequestDTO request, List<DiseaseReport> reports) {
        Set<UUID> diseaseIds;
        
        if (request.getDiseaseIds() != null && !request.getDiseaseIds().isEmpty()) {
            // Use the specified disease IDs
            diseaseIds = new HashSet<>(request.getDiseaseIds());
        } else {
            // Get diseases from the reports
            diseaseIds = reports.stream()
                    .map(r -> r.getDisease().getId())
                    .collect(Collectors.toSet());
        }

        List<Disease> diseases = diseaseRepository.findAllById(diseaseIds);
        return diseases.stream()
                .collect(Collectors.toMap(Disease::getId, d -> d));
    }

    /**
     * Generate time period labels based on the groupBy setting.
     */
    private List<String> generateLabels(LocalDate startDate, LocalDate endDate, GroupBy groupBy) {
        List<String> labels = new ArrayList<>();
        LocalDate current = startDate;

        switch (groupBy) {
            case WEEKLY:
                // Align to start of week (Monday)
                current = current.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
                DateTimeFormatter weekDayFormatter = DateTimeFormatter.ofPattern("MMM d");
                while (!current.isAfter(endDate)) {
                    LocalDate weekEnd = current.plusDays(6);
                    // Format: "Dec 9-15" or "Dec 30-Jan 5" if crossing month boundary
                    String startStr = current.format(weekDayFormatter);
                    String endStr;
                    if (current.getMonth() == weekEnd.getMonth()) {
                        // Same month: "Dec 9-15"
                        endStr = String.valueOf(weekEnd.getDayOfMonth());
                    } else {
                        // Different months: "Dec 30-Jan 5"
                        endStr = weekEnd.format(weekDayFormatter);
                    }
                    labels.add(startStr + "-" + endStr);
                    current = current.plusWeeks(1);
                }
                break;
                
            case MONTHLY:
                // Align to start of month
                current = current.withDayOfMonth(1);
                DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
                while (!current.isAfter(endDate)) {
                    labels.add(current.format(monthFormatter));
                    current = current.plusMonths(1);
                }
                break;
                
            case ANNUALLY:
                // Align to start of year
                current = current.withDayOfYear(1);
                while (!current.isAfter(endDate)) {
                    labels.add(String.valueOf(current.getYear()));
                    current = current.plusYears(1);
                }
                break;
        }

        return labels;
    }

    /**
     * Create datasets for Chart.js, one per disease.
     */
    private List<DatasetDTO> createDatasets(List<DiseaseReport> reports, Map<UUID, Disease> diseaseMap,
                                            List<String> labels, LocalDate startDate, LocalDate endDate, 
                                            GroupBy groupBy, MetricType metricType) {
        List<DatasetDTO> datasets = new ArrayList<>();

        // Group reports by disease
        Map<UUID, List<DiseaseReport>> reportsByDisease = reports.stream()
                .collect(Collectors.groupingBy(r -> r.getDisease().getId()));

        // Create a dataset for each disease
        for (Map.Entry<UUID, Disease> entry : diseaseMap.entrySet()) {
            UUID diseaseId = entry.getKey();
            Disease disease = entry.getValue();
            List<DiseaseReport> diseaseReports = reportsByDisease.getOrDefault(diseaseId, Collections.emptyList());

            // Count reports or animals per period based on metric type
            Map<String, Long> countsByPeriod = countByPeriod(diseaseReports, groupBy, metricType);

            // Create data array matching labels
            List<Long> data = labels.stream()
                    .map(label -> countsByPeriod.getOrDefault(label, 0L))
                    .collect(Collectors.toList());

            DatasetDTO dataset = new DatasetDTO();
            dataset.setDiseaseId(diseaseId);
            dataset.setDiseaseName(disease.getDiseaseName());
            dataset.setDiseaseCode(disease.getDiseaseCode());
            dataset.setData(data);

            datasets.add(dataset);
        }

        // Sort by disease name for consistent ordering
        datasets.sort(Comparator.comparing(DatasetDTO::getDiseaseName));

        return datasets;
    }

    /**
     * Count by time period based on metric type.
     * REPORT_COUNT: Count each report as 1
     * ANIMAL_COUNT: Sum affectedCount values (null treated as 0)
     */
    private Map<String, Long> countByPeriod(List<DiseaseReport> reports, GroupBy groupBy, MetricType metricType) {
        Map<String, Long> counts = new HashMap<>();

        for (DiseaseReport report : reports) {
            LocalDate reportDate = report.getReportDate();
            String periodKey = getPeriodKey(reportDate, groupBy);
            
            long value;
            if (metricType == MetricType.ANIMAL_COUNT) {
                // Sum affected animals (null treated as 0)
                value = report.getAffectedCount() != null ? report.getAffectedCount().longValue() : 0L;
            } else {
                // Count each report as 1
                value = 1L;
            }
            
            counts.merge(periodKey, value, Long::sum);
        }

        return counts;
    }

    /**
     * Get the period key for a given date based on groupBy setting.
     */
    private String getPeriodKey(LocalDate date, GroupBy groupBy) {
        switch (groupBy) {
            case WEEKLY:
                // Align to start of week (Monday) to match label generation
                LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
                LocalDate weekEnd = weekStart.plusDays(6);
                DateTimeFormatter weekDayFormatter = DateTimeFormatter.ofPattern("MMM d");
                String startStr = weekStart.format(weekDayFormatter);
                String endStr;
                if (weekStart.getMonth() == weekEnd.getMonth()) {
                    // Same month: "Dec 9-15"
                    endStr = String.valueOf(weekEnd.getDayOfMonth());
                } else {
                    // Different months: "Dec 30-Jan 5"
                    endStr = weekEnd.format(weekDayFormatter);
                }
                return startStr + "-" + endStr;
                
            case MONTHLY:
                DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
                return date.format(monthFormatter);
                
            case ANNUALLY:
                return String.valueOf(date.getYear());
                
            default:
                return date.toString();
        }
    }
}
