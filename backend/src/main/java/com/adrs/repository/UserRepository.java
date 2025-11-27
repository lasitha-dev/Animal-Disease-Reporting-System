package com.adrs.repository;

import com.adrs.model.Province;
import com.adrs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides CRUD operations and custom query methods for users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given username.
     *
     * @param username the username to check
     * @return true if a user with the username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email the email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds all users with a specific role.
     *
     * @param role the role to filter by
     * @return a list of users with the specified role
     */
    List<User> findByRole(User.Role role);

    /**
     * Finds all active users.
     *
     * @param active the active status to filter by
     * @return a list of active users
     */
    List<User> findByActive(Boolean active);

    /**
     * Count all active users.
     *
     * @return count of active users
     */
    Long countByActiveTrue();

    /**
     * Count all inactive users.
     *
     * @return count of inactive users
     */
    Long countByActiveFalse();

    /**
     * Count users by role.
     *
     * @param role the user role
     * @return count of users with specified role
     */
    Long countByRole(User.Role role);

    /**
     * Find all users by province.
     *
     * @param province the province to filter by
     * @return list of users in the specified province
     */
    List<User> findByProvince(Province province);

    /**
     * Find all users by province and role.
     *
     * @param province the province to filter by
     * @param role     the role to filter by
     * @return list of users matching province and role
     */
    List<User> findByProvinceAndRole(Province province, User.Role role);

    /**
     * Get user distribution by province for all active users.
     * Returns raw data as Object[] where index 0 is Province enum and index 1 is count.
     *
     * @return list of Object[] containing [Province, Long count]
     */
    @Query("SELECT u.province, COUNT(u) FROM User u WHERE u.active = true AND u.province IS NOT NULL GROUP BY u.province")
    List<Object[]> countUsersByProvince();

    /**
     * Get user distribution by province filtered by role.
     * Returns raw data as Object[] where index 0 is Province enum and index 1 is count.
     *
     * @param role the user role to filter by
     * @return list of Object[] containing [Province, Long count]
     */
    @Query("SELECT u.province, COUNT(u) FROM User u WHERE u.active = true AND u.role = :role AND u.province IS NOT NULL GROUP BY u.province")
    List<Object[]> countUsersByProvinceAndRole(@Param("role") User.Role role);

    /**
     * Get user distribution by district for a specific province.
     * Returns raw data as Object[] where index 0 is District enum and index 1 is count.
     *
     * @param province the province to get district breakdown for
     * @return list of Object[] containing [District, Long count]
     */
    @Query("SELECT u.district, COUNT(u) FROM User u WHERE u.active = true AND u.province = :province AND u.district IS NOT NULL GROUP BY u.district")
    List<Object[]> countUsersByDistrict(@Param("province") Province province);

    /**
     * Get user distribution by district for a specific province and role.
     * Returns raw data as Object[] where index 0 is District enum and index 1 is count.
     *
     * @param province the province to get district breakdown for
     * @param role     the user role to filter by
     * @return list of Object[] containing [District, Long count]
     */
    @Query("SELECT u.district, COUNT(u) FROM User u WHERE u.active = true AND u.province = :province AND u.role = :role AND u.district IS NOT NULL GROUP BY u.district")
    List<Object[]> countUsersByDistrictAndRole(@Param("province") Province province, @Param("role") User.Role role);

    // ========================================
    // DISTRICT-LEVEL QUERIES (All Districts)
    // ========================================

    /**
     * Count all active users in a specific district.
     *
     * @param district the district to count users in
     * @return count of active users in the district
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.district = :district AND u.active = true")
    Long countActiveUsersByDistrict(@Param("district") com.adrs.model.District district);

    /**
     * Count all active users in a specific district with a specific role.
     *
     * @param district the district to count users in
     * @param role     the user role to filter by
     * @return count of active users matching district and role
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.district = :district AND u.role = :role AND u.active = true")
    Long countActiveUsersByDistrictAndRole(@Param("district") com.adrs.model.District district, @Param("role") User.Role role);

    /**
     * Find all active users in a specific district.
     *
     * @param district the district to filter by
     * @return list of active users in the specified district
     */
    @Query("SELECT u FROM User u WHERE u.district = :district AND u.active = true")
    List<User> findActiveUsersByDistrict(@Param("district") com.adrs.model.District district);

    /**
     * Find all active users in a specific district with a specific role.
     *
     * @param district the district to filter by
     * @param role     the role to filter by
     * @return list of active users matching district and role
     */
    @Query("SELECT u FROM User u WHERE u.district = :district AND u.role = :role AND u.active = true")
    List<User> findActiveUsersByDistrictAndRole(@Param("district") com.adrs.model.District district, @Param("role") User.Role role);
}
