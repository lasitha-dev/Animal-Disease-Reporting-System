package com.adrs.controller;

import com.adrs.dto.AuthResponse;
import com.adrs.dto.LoginRequest;
import com.adrs.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication-related endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param loginRequest the login credentials
     * @return AuthResponse with JWT token and user details
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login request received for user: {}", loginRequest.getUsername());
        logger.debug("Password length: {}", loginRequest.getPassword() != null ? loginRequest.getPassword().length() : "null");
        logger.debug("Password first char: {}", loginRequest.getPassword() != null && !loginRequest.getPassword().isEmpty() ? loginRequest.getPassword().charAt(0) : "empty");
        AuthResponse authResponse = userService.authenticateUser(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Logs out the current user.
     *
     * @return success message
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logs out the current user")
    public ResponseEntity<String> logout() {
        logger.info("Logout request received");
        // Since we're using JWT, logout is handled on the client-side by removing the token
        return ResponseEntity.ok("Logged out successfully");
    }
}
