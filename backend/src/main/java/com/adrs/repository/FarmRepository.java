package com.adrs.repository;

import com.adrs.model.Farm;
import com.adrs.model.Province;
import com.adrs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Find all farms created by a specific user.
     *
     * @param user the user who created the farms
     * @return list of farms
     */
    List<Farm> findByCreatedBy(User user);

    /**
     * Find all active farms created by a specific user.
     *
     * @param user the user who created the farms
     * @return list of active farms
     */
    List<Farm> findByCreatedByAndIsActiveTrue(User user);

    /**
     * Count farms created by a specific user.
     *
     * @param user the user
     * @return count of farms
     */
    Long countByCreatedBy(User user);

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

    /**
     * Find all active farms NOT created by a specific user.
     * Used to show farms registered by other vets.
     *
     * @param user the user to exclude
     * @return list of active farms created by other users
     */
    List<Farm> findByCreatedByNotAndIsActiveTrue(User user);

    /**
     * Find distinct provinces that have farms with GPS coordinates.
     *
     * @return list of provinces with GPS-enabled farms
     */
    @Query("SELECT DISTINCT f.province FROM Farm f WHERE f.gpsLatitude IS NOT NULL AND f.gpsLongitude IS NOT NULL AND f.isActive = true ORDER BY f.province")
    List<Province> findDistinctProvincesWithGpsCoordinates();

    /**
     * Find all active farms in a specific province with GPS coordinates, created by a user.
     *
     * @param user the user who created the farms
     * @param province the province to filter by
     * @return list of farms
     */
    @Query("SELECT f FROM Farm f WHERE f.createdBy = :user AND f.isActive = true AND f.province = :province AND f.gpsLatitude IS NOT NULL AND f.gpsLongitude IS NOT NULL")
    List<Farm> findByCreatedByAndProvinceWithGps(@Param("user") User user, @Param("province") Province province);

    /**
     * Find all active farms NOT created by a specific user in a specific province with GPS coordinates.
     *
     * @param user the user to exclude
     * @param province the province to filter by
     * @return list of farms
     */
    @Query("SELECT f FROM Farm f WHERE f.createdBy != :user AND f.isActive = true AND f.province = :province AND f.gpsLatitude IS NOT NULL AND f.gpsLongitude IS NOT NULL")
    List<Farm> findByCreatedByNotAndProvinceWithGps(@Param("user") User user, @Param("province") Province province);
}

