package com.adrs.service;

import com.adrs.dto.AuthResponse;
import com.adrs.dto.LoginRequest;
import com.adrs.dto.UserRequest;
import com.adrs.dto.UserResponse;

import java.util.List;

/**
 * Service interface for user-related operations.
 */
public interface UserService {

    /**
     * Authenticates a user and returns an authentication response.
     *
     * @param loginRequest the login request containing username and password
     * @return AuthResponse containing JWT token and user details
     */
    AuthResponse authenticateUser(LoginRequest loginRequest);

    /**
     * Creates a new user in the system.
     *
     * @param userRequest the user creation request
     * @return UserResponse containing the created user details
     */
    UserResponse createUser(UserRequest userRequest);

    /**
     * Updates an existing user.
     *
     * @param id          the user ID
     * @param userRequest the user update request
     * @return UserResponse containing the updated user details
     */
    UserResponse updateUser(Long id, UserRequest userRequest);

    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID
     * @return UserResponse containing the user details
     */
    UserResponse getUserById(Long id);

    /**
     * Retrieves all users in the system.
     *
     * @return a list of all users
     */
    List<UserResponse> getAllUsers();

    /**
     * Deletes a user by ID.
     *
     * @param id the user ID
     */
    void deleteUser(Long id);

    /**
     * Activates or deactivates a user account.
     *
     * @param id     the user ID
     * @param active the active status
     * @return UserResponse containing the updated user details
     */
    UserResponse toggleUserStatus(Long id, Boolean active);
}
