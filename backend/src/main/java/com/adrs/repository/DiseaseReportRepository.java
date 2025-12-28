package com.adrs.repository;

import com.adrs.model.DiseaseReport;
import com.adrs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}

