package com.adrs.controller;

import com.adrs.dto.UserRequest;
import com.adrs.dto.UserResponse;
import com.adrs.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user management endpoints.
 */
@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Creates a new user (admin only).
     *
     * @param userRequest the user creation request
     * @return the created user
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user", description = "Creates a new user (Admin only)")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        logger.info("Create user request received for: {}", userRequest.getUsername());
        UserResponse createdUser = userService.createUser(userRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieves all users (Admin only)")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        logger.info("Get all users request received");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID
     * @return the user details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        logger.info("Get user by ID request received for ID: {}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Updates a user (admin only).
     *
     * @param id          the user ID
     * @param userRequest the user update request
     * @return the updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Updates a user (Admin only)")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                    @Valid @RequestBody UserRequest userRequest) {
        logger.info("Update user request received for ID: {}", id);
        UserResponse updatedUser = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes a user (admin only).
     *
     * @param id the user ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Deletes a user (Admin only)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Delete user request received for ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Toggles the active status of a user (admin only).
     *
     * @param id     the user ID
     * @param active the new active status
     * @return the updated user
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle user status", description = "Activates or deactivates a user (Admin only)")
    public ResponseEntity<UserResponse> toggleUserStatus(@PathVariable Long id,
                                                          @RequestParam Boolean active) {
        logger.info("Toggle user status request received for ID: {} to {}", id, active);
        UserResponse updatedUser = userService.toggleUserStatus(id, active);
        return ResponseEntity.ok(updatedUser);
    }
}
