package com.adrs.test.controller;

import com.adrs.config.JwtAuthenticationFilter;
import com.adrs.config.JwtTokenProvider;
import com.adrs.controller.AuthController;
import com.adrs.dto.AuthResponse;
import com.adrs.dto.LoginRequest;
import com.adrs.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController.
 * Tests REST API endpoints for authentication.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    private static final String AUTH_LOGIN_ENDPOINT = "/api/auth/login";
    private static final String AUTH_LOGOUT_ENDPOINT = "/api/auth/logout";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_JWT_TOKEN = "test.jwt.token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    /**
     * Set up test data before each test.
     */
    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        authResponse = new AuthResponse(
                TEST_JWT_TOKEN,
                1L,
                TEST_USERNAME,
                "test@example.com",
                "Test",
                "User",
                "VETERINARY_OFFICER"
        );
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void testLoginSuccess() throws Exception {
        // Given
        when(userService.authenticateUser(any(LoginRequest.class))).thenReturn(authResponse);

        // When/Then
        mockMvc.perform(post(AUTH_LOGIN_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TEST_JWT_TOKEN))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("VETERINARY_OFFICER"));

        verify(userService).authenticateUser(any(LoginRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should logout successfully")
    void testLogoutSuccess() throws Exception {
        // When/Then
        mockMvc.perform(post(AUTH_LOGOUT_ENDPOINT)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
