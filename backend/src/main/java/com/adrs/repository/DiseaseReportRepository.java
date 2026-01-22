package com.adrs.repository;

import com.adrs.model.DiseaseReport;
import com.adrs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for DiseaseReport entity.
 */
@Repository
public interface DiseaseReportRepository extends JpaRepository<DiseaseReport, UUID> {

    /**
     * Count all disease reports.
     *
     * @return count of all disease reports
     */
    long count();

    /**
     * Count confirmed disease reports.
     *
     * @return count of confirmed reports
     */
    Long countByIsConfirmedTrue();

    /**
     * Count pending disease reports.
     *
     * @return count of pending reports
     */
    Long countByIsConfirmedFalse();

    /**
     * Count disease reports created after a specific date.
     *
     * @param date the date to filter from
     * @return count of reports
     */
    Long countByCreatedAtAfter(LocalDateTime date);

    /**
     * Find disease reports by the reporting vet, ordered by creation date descending.
     *
     * @param reportedBy the vet who reported
     * @return list of disease reports
     */
    List<DiseaseReport> findByReportedByOrderByCreatedAtDesc(User reportedBy);

    /**
     * Find disease reports for a specific farm, ordered by creation date descending.
     *
     * @param farmId the farm ID
     * @return list of disease reports
     */
    List<DiseaseReport> findByFarmIdOrderByCreatedAtDesc(UUID farmId);

    /**
     * Count disease reports by the reporting vet.
     *
     * @param reportedBy the vet who reported
     * @return count of reports
     */
    Long countByReportedBy(User reportedBy);

    /**
     * Get disease report trend grouped by date.
     *
     * @param startDate the start date
     * @return list of objects containing date and count
     */
    @Query("SELECT CAST(dr.reportDate AS LocalDate) as date, COUNT(dr) as count " +
           "FROM DiseaseReport dr WHERE dr.createdAt >= :startDate " +
           "GROUP BY CAST(dr.reportDate AS LocalDate) " +
           "ORDER BY CAST(dr.reportDate AS LocalDate)")
    List<Object[]> getDiseaseReportTrend(LocalDateTime startDate);

    /**
     * Find all disease reports where the farm has GPS coordinates.
     *
     * @return list of disease reports with valid GPS locations
     */
    @Query("SELECT dr FROM DiseaseReport dr " +
           "JOIN FETCH dr.farm f " +
           "JOIN FETCH dr.disease d " +
           "JOIN FETCH dr.animalType at " +
           "JOIN FETCH dr.reportedBy rb " +
           "WHERE f.gpsLatitude IS NOT NULL AND f.gpsLongitude IS NOT NULL " +
           "ORDER BY f.id, dr.reportDate DESC")
    List<DiseaseReport> findAllWithFarmGpsCoordinates();

    /**
     * Find all disease reports filtered by animal type IDs where farm has GPS coordinates.
     *
     * @param animalTypeIds list of animal type IDs to filter by
     * @return list of disease reports
     */
    @Query("SELECT dr FROM DiseaseReport dr " +
           "JOIN FETCH dr.farm f " +
           "JOIN FETCH dr.disease d " +
           "JOIN FETCH dr.animalType at " +
           "JOIN FETCH dr.reportedBy rb " +
           "WHERE f.gpsLatitude IS NOT NULL AND f.gpsLongitude IS NOT NULL " +
           "AND at.id IN :animalTypeIds " +
           "ORDER BY f.id, dr.reportDate DESC")
    List<DiseaseReport> findAllWithFarmGpsCoordinatesByAnimalTypeIds(List<UUID> animalTypeIds);

    /**
     * Find distinct animal type IDs that have disease reports.
     *
     * @return list of animal type IDs
     */
    @Query("SELECT DISTINCT dr.animalType.id FROM DiseaseReport dr " +
           "WHERE dr.farm.gpsLatitude IS NOT NULL AND dr.farm.gpsLongitude IS NOT NULL")
    List<UUID> findDistinctAnimalTypeIdsWithReports();

    /**
     * Find disease reports NOT created by a specific vet, ordered by creation date descending.
     *
     * @param reportedBy the vet to exclude
     * @return list of disease reports by other vets
     */
    List<DiseaseReport> findByReportedByNotOrderByCreatedAtDesc(User reportedBy);

    /**
     * Find distinct disease IDs that have reports with GPS coordinates.
     *
     * @return list of disease IDs
     */
    @Query("SELECT DISTINCT dr.disease.id FROM DiseaseReport dr " +
           "WHERE dr.farm.gpsLatitude IS NOT NULL AND dr.farm.gpsLongitude IS NOT NULL")
    List<UUID> findDistinctDiseaseIdsWithReports();

    /**
     * Find distinct disease IDs that have reports with GPS coordinates, filtered by animal type IDs.
     *
     * @param animalTypeIds list of animal type IDs to filter by
     * @return list of disease IDs
     */
    @Query("SELECT DISTINCT dr.disease.id FROM DiseaseReport dr " +
           "WHERE dr.farm.gpsLatitude IS NOT NULL AND dr.farm.gpsLongitude IS NOT NULL " +
           "AND dr.animalType.id IN :animalTypeIds")
    List<UUID> findDistinctDiseaseIdsByAnimalTypeIds(List<UUID> animalTypeIds);

    /**
     * Find all disease reports filtered by animal type and disease IDs where farm has GPS coordinates.
     *
     * @param animalTypeIds list of animal type IDs to filter by
     * @param diseaseIds list of disease IDs to filter by
     * @return list of disease reports
     */
    @Query("SELECT dr FROM DiseaseReport dr " +
           "JOIN FETCH dr.farm f " +
           "JOIN FETCH dr.disease d " +
           "JOIN FETCH dr.animalType at " +
           "JOIN FETCH dr.reportedBy rb " +
           "WHERE f.gpsLatitude IS NOT NULL AND f.gpsLongitude IS NOT NULL " +
           "AND at.id IN :animalTypeIds AND d.id IN :diseaseIds " +
           "ORDER BY f.id, dr.reportDate DESC")
    List<DiseaseReport> findAllWithFarmGpsCoordinatesByAnimalTypeIdsAndDiseaseIds(
            List<UUID> animalTypeIds, List<UUID> diseaseIds);

    /**
     * Find all disease reports filtered by disease IDs where farm has GPS coordinates.
     *
     * @param diseaseIds list of disease IDs to filter by
     * @return list of disease reports
     */
    @Query("SELECT dr FROM DiseaseReport dr " +
           "JOIN FETCH dr.farm f " +
           "JOIN FETCH dr.disease d " +
           "JOIN FETCH dr.animalType at " +
           "JOIN FETCH dr.reportedBy rb " +
           "WHERE f.gpsLatitude IS NOT NULL AND f.gpsLongitude IS NOT NULL " +
           "AND d.id IN :diseaseIds " +
           "ORDER BY f.id, dr.reportDate DESC")
    List<DiseaseReport> findAllWithFarmGpsCoordinatesByDiseaseIds(List<UUID> diseaseIds);

    // ========================================
    // ANALYTICS QUERIES
    // ========================================

    /**
     * Find all disease reports within a date range.
     *
     * @param startDate start of date range (inclusive)
     * @param endDate end of date range (inclusive)
     * @return list of disease reports
     */
    @Query("SELECT dr FROM DiseaseReport dr " +
           "JOIN FETCH dr.disease d " +
           "JOIN FETCH dr.animalType at " +
           "WHERE dr.reportDate >= :startDate AND dr.reportDate <= :endDate " +
           "ORDER BY dr.reportDate")
    List<DiseaseReport> findByReportDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find disease reports within a date range filtered by animal type.
     *
     * @param animalTypeId the animal type ID
     * @param startDate start of date range (inclusive)
     * @param endDate end of date range (inclusive)
     * @return list of disease reports
     */
    @Query("SELECT dr FROM DiseaseReport dr " +
           "JOIN FETCH dr.disease d " +
           "JOIN FETCH dr.animalType at " +
           "WHERE dr.animalType.id = :animalTypeId " +
           "AND dr.reportDate >= :startDate AND dr.reportDate <= :endDate " +
           "ORDER BY dr.reportDate")
    List<DiseaseReport> findByAnimalTypeIdAndReportDateBetween(
            UUID animalTypeId, LocalDate startDate, LocalDate endDate);

    /**
     * Find disease reports within a date range filtered by disease IDs.
     *
     * @param diseaseIds list of disease IDs to filter by
     * @param startDate start of date range (inclusive)
     * @param endDate end of date range (inclusive)
     * @return list of disease reports
     */
    @Query("SELECT dr FROM DiseaseReport dr " +
           "JOIN FETCH dr.disease d " +
           "JOIN FETCH dr.animalType at " +
           "WHERE dr.disease.id IN :diseaseIds " +
           "AND dr.reportDate >= :startDate AND dr.reportDate <= :endDate " +
           "ORDER BY dr.reportDate")
    List<DiseaseReport> findByDiseaseIdInAndReportDateBetween(
            List<UUID> diseaseIds, LocalDate startDate, LocalDate endDate);

    /**
     * Find disease reports within a date range filtered by animal type and disease IDs.
     *
     * @param animalTypeId the animal type ID
     * @param diseaseIds list of disease IDs to filter by
     * @param startDate start of date range (inclusive)
     * @param endDate end of date range (inclusive)
     * @return list of disease reports
     */
    @Query("SELECT dr FROM DiseaseReport dr " +
           "JOIN FETCH dr.disease d " +
           "JOIN FETCH dr.animalType at " +
           "WHERE dr.animalType.id = :animalTypeId " +
           "AND dr.disease.id IN :diseaseIds " +
           "AND dr.reportDate >= :startDate AND dr.reportDate <= :endDate " +
           "ORDER BY dr.reportDate")
    List<DiseaseReport> findByAnimalTypeIdAndDiseaseIdInAndReportDateBetween(
            UUID animalTypeId, List<UUID> diseaseIds, LocalDate startDate, LocalDate endDate);

    /**
     * Find all distinct diseases that have reports for a specific animal type.
     *
     * @param animalTypeId the animal type ID
     * @return list of disease IDs
     */
    @Query("SELECT DISTINCT dr.disease.id FROM DiseaseReport dr " +
           "WHERE dr.animalType.id = :animalTypeId")
    List<UUID> findDistinctDiseaseIdsByAnimalTypeId(UUID animalTypeId);

    // ========================================
    // KPI QUERIES FOR VET DASHBOARD
    // ========================================

    /**
     * Count active outbreaks (reports with ONGOING outcome).
     *
     * @return count of ongoing reports
     */
    @Query("SELECT COUNT(dr) FROM DiseaseReport dr WHERE dr.outcome = 'ONGOING'")
    Long countByOutcomeOngoing();

    /**
     * Count farms with 2 or more disease reports in the last N days.
     *
     * @param startDate the start date for the period
     * @return count of high-risk farms
     */
    @Query("SELECT COUNT(DISTINCT f.id) FROM DiseaseReport dr " +
           "JOIN dr.farm f " +
           "WHERE dr.reportDate >= :startDate " +
           "GROUP BY f.id " +
           "HAVING COUNT(dr) >= 2")
    Long countHighRiskFarms(LocalDate startDate);

    /**
     * Count distinct farms with at least one report in a month.
     *
     * @param startDate start of month
     * @param endDate end of month
     * @return count of farms with reports
     */
    @Query("SELECT COUNT(DISTINCT dr.farm.id) FROM DiseaseReport dr " +
           "WHERE dr.reportDate >= :startDate AND dr.reportDate <= :endDate")
    Long countDistinctFarmsWithReportsInPeriod(LocalDate startDate, LocalDate endDate);

    /**
     * Count pending follow-ups (ONGOING reports older than a specific date).
     *
     * @param beforeDate reports before this date need follow-up
     * @return count of pending follow-ups
     */
    @Query("SELECT COUNT(dr) FROM DiseaseReport dr " +
           "WHERE dr.outcome = 'ONGOING' AND dr.reportDate < :beforeDate")
    Long countPendingFollowups(LocalDate beforeDate);

    /**
     * Get daily report counts for health trend chart.
     *
     * @param startDate start date for trend
     * @return list of [date, report count, resolved count]
     */
    @Query("SELECT dr.reportDate, " +
           "COUNT(dr), " +
           "SUM(CASE WHEN dr.outcome IN ('RECOVERED', 'DIED', 'EUTHANIZED') THEN 1 ELSE 0 END) " +
           "FROM DiseaseReport dr " +
           "WHERE dr.reportDate >= :startDate " +
           "GROUP BY dr.reportDate " +
           "ORDER BY dr.reportDate")
    List<Object[]> getDailyHealthTrend(LocalDate startDate);

    /**
     * Get farms with multiple reports in period (for high-risk calculation).
     *
     * @param startDate the start date for the period
     * @return list of farm IDs with their report counts
     */
    @Query("SELECT dr.farm.id, COUNT(dr) as reportCount FROM DiseaseReport dr " +
           "WHERE dr.reportDate >= :startDate " +
           "GROUP BY dr.farm.id " +
           "HAVING COUNT(dr) >= 2")
    List<Object[]> findHighRiskFarmData(LocalDate startDate);
}

