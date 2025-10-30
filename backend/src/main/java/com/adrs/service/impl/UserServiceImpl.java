package com.adrs.service.impl;

import com.adrs.dto.AuthResponse;
import com.adrs.dto.LoginRequest;
import com.adrs.dto.UserRequest;
import com.adrs.dto.UserResponse;
import com.adrs.exception.ResourceNotFoundException;
import com.adrs.model.User;
import com.adrs.repository.UserRepository;
import com.adrs.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for user-related operations.
 * Handles user authentication, creation, updates, and management.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String USER_NOT_FOUND_MSG = "User not found with id: ";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Authenticates a user and updates last login time.
     * Note: This method is kept for backward compatibility but is not used
     * in form-based authentication. Spring Security handles authentication.
     *
     * @param loginRequest the login request
     * @return AuthResponse with user details (token will be empty)
     */
    @Override
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        logger.info("Authenticating user: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update last login time
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        logger.info("User authenticated successfully: {}", loginRequest.getUsername());

        // Return AuthResponse without JWT token (using empty string)
        return new AuthResponse(
                "",  // No JWT token in form-based auth
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name()
        );
    }

    /**
     * Creates a new user in the system.
     *
     * @param userRequest the user creation request
     * @return UserResponse with created user details
     */
    @Override
    public UserResponse createUser(UserRequest userRequest) {
        logger.info("Creating new user: {}", userRequest.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setProvince(userRequest.getProvince());
        user.setDistrict(userRequest.getDistrict());
        user.setRole(userRequest.getRole());
        user.setActive(Boolean.TRUE.equals(userRequest.getActive()));

        User savedUser = userRepository.save(user);
        logger.info("User created successfully: {}", savedUser.getUsername());

        return UserResponse.fromUser(savedUser);
    }

    /**
     * Updates an existing user.
     *
     * @param id          the user ID
     * @param userRequest the user update request
     * @return UserResponse with updated user details
     */
    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        logger.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MSG + id));

        // Check if username is being changed and if it already exists
        if (!user.getUsername().equals(userRequest.getUsername()) &&
                userRepository.existsByUsername(userRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(userRequest.getEmail()) &&
                userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setProvince(userRequest.getProvince());
        user.setDistrict(userRequest.getDistrict());
        user.setRole(userRequest.getRole());
        user.setActive(userRequest.getActive());

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully: {}", updatedUser.getUsername());

        return UserResponse.fromUser(updatedUser);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID
     * @return UserResponse with user details
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        logger.debug("Fetching user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MSG + id));

        return UserResponse.fromUser(user);
    }

    /**
     * Retrieves all users in the system.
     *
     * @return a list of all users
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        logger.debug("Fetching all users");

        return userRepository.findAll().stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the user ID
     */
    @Override
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MSG + id));

        userRepository.delete(user);
        logger.info("User deleted successfully: {}", user.getUsername());
    }

    /**
     * Toggles the active status of a user.
     *
     * @param id     the user ID
     * @param active the new active status
     * @return UserResponse with updated user details
     */
    @Override
    public UserResponse toggleUserStatus(Long id, Boolean active) {
        logger.info("Toggling status for user ID: {} to {}", id, active);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MSG + id));

        user.setActive(active);
        User updatedUser = userRepository.save(user);

        logger.info("User status updated successfully: {}", updatedUser.getUsername());

        return UserResponse.fromUser(updatedUser);
    }
}
