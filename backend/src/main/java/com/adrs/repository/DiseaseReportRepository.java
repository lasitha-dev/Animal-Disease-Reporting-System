package com.adrs.repository;

import com.adrs.model.DiseaseReport;
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
