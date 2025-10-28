package com.adrs.repository;

import com.adrs.model.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Farm entity.
 */
@Repository
public interface FarmRepository extends JpaRepository<Farm, UUID> {

    /**
     * Count all active farms.
     *
     * @return count of active farms
     */
    Long countByIsActiveTrue();

    /**
     * Count farms created after a specific date.
     *
     * @param date the date to filter from
     * @return count of farms
     */
    Long countByCreatedAtAfter(LocalDateTime date);

    /**
     * Get farm registrations grouped by date for trend analysis.
     *
     * @param startDate the start date
     * @return list of objects containing date and count
     */
    @Query("SELECT CAST(f.createdAt AS LocalDate) as date, COUNT(f) as count " +
           "FROM Farm f WHERE f.createdAt >= :startDate " +
           "GROUP BY CAST(f.createdAt AS LocalDate) " +
           "ORDER BY CAST(f.createdAt AS LocalDate)")
    List<Object[]> getFarmRegistrationTrend(LocalDateTime startDate);
}
