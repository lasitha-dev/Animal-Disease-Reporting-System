package com.adrs.repository;

import com.adrs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
