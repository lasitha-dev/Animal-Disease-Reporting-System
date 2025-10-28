package com.adrs.test.integration;

import com.adrs.dto.AuthResponse;
import com.adrs.dto.LoginRequest;
import com.adrs.dto.UserRequest;
import com.adrs.dto.UserResponse;
import com.adrs.model.User;
import com.adrs.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for authentication and user management.
 * Tests end-to-end functionality with real components.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("Authentication and User Management Integration Tests")
class AuthenticationIntegrationTest {

    private static final String TEST_USERNAME = "integrationtest";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_EMAIL = "integration@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    /**
     * Set up test data before each test.
     */
    @BeforeEach
    void setUp() {
        // Clean up any existing test data
        userRepository.deleteAll();

        // Create a test user
        testUser = new User();
        testUser.setUsername(TEST_USERNAME);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        testUser.setFirstName("Integration");
        testUser.setLastName("Test");
        testUser.setPhoneNumber("+94771234567");
        testUser.setRole(User.Role.ADMIN);
        testUser.setActive(true);
        
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("Should complete full authentication flow successfully")
    void testFullAuthenticationFlow() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        // When - Login
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn();

        // Then - Verify response
        String responseContent = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseContent, AuthResponse.class);
        
        assertThat(authResponse.getToken()).isNotNull();
        assertThat(authResponse.getUsername()).isEqualTo(TEST_USERNAME);
        
        // And - Verify user's last login was updated
        User updatedUser = userRepository.findByUsername(TEST_USERNAME).orElseThrow();
        assertThat(updatedUser.getLastLogin()).isNotNull();
    }

    @Test
    @DisplayName("Should fail authentication with incorrect password")
    void testAuthenticationFailureIncorrectPassword() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword("wrongpassword");

        // When/Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should fail authentication with non-existent user")
    void testAuthenticationFailureNonExistentUser() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword(TEST_PASSWORD);

        // When/Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should complete full user CRUD flow")
    void testFullUserCrudFlow() throws Exception {
        // Step 1: Login to get JWT token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                AuthResponse.class
        );
        String token = authResponse.getToken();

        // Step 2: Create a new user
        UserRequest createRequest = new UserRequest();
        createRequest.setUsername("newuser");
        createRequest.setEmail("newuser@example.com");
        createRequest.setPassword("newpassword123");
        createRequest.setFirstName("New");
        createRequest.setLastName("User");
        createRequest.setPhoneNumber("+94771234568");
        createRequest.setRole(User.Role.VETERINARY_OFFICER);
        createRequest.setActive(true);

        MvcResult createResult = mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andReturn();

        UserResponse createdUser = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                UserResponse.class
        );
        Long userId = createdUser.getId();

        // Step 3: Get the created user
        mockMvc.perform(get("/api/users/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("newuser"));

        // Step 4: Update the user
        UserRequest updateRequest = new UserRequest();
        updateRequest.setUsername("newuser");
        updateRequest.setEmail("newuser@example.com");
        updateRequest.setPassword("newpassword123");
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("Name");
        updateRequest.setPhoneNumber("+94771234568");
        updateRequest.setRole(User.Role.VETERINARY_OFFICER);
        updateRequest.setActive(true);

        mockMvc.perform(put("/api/users/" + userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"));

        // Step 5: Get all users
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2)); // Original test user + new user

        // Step 6: Delete the user
        mockMvc.perform(delete("/api/users/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // Step 7: Verify user is deleted
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    @DisplayName("Should enforce authentication for protected endpoints")
    void testAuthenticationRequired() throws Exception {
        // When/Then - Try to access protected endpoints without token
        // Spring Security returns 403 Forbidden for anonymous users accessing protected resources
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should prevent duplicate username during user creation")
    void testDuplicateUsernameValidation() throws Exception {
        // Given - Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                AuthResponse.class
        );
        String token = authResponse.getToken();

        // When - Try to create user with duplicate username
        UserRequest duplicateRequest = new UserRequest();
        duplicateRequest.setUsername(TEST_USERNAME); // Duplicate username
        duplicateRequest.setEmail("different@example.com");
        duplicateRequest.setPassword("password123");
        duplicateRequest.setFirstName("Duplicate");
        duplicateRequest.setLastName("User");
        duplicateRequest.setRole(User.Role.VETERINARY_OFFICER);
        duplicateRequest.setActive(true);

        // Then - Should fail
        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should prevent duplicate email during user creation")
    void testDuplicateEmailValidation() throws Exception {
        // Given - Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                AuthResponse.class
        );
        String token = authResponse.getToken();

        // When - Try to create user with duplicate email
        UserRequest duplicateRequest = new UserRequest();
        duplicateRequest.setUsername("differentuser");
        duplicateRequest.setEmail(TEST_EMAIL); // Duplicate email
        duplicateRequest.setPassword("password123");
        duplicateRequest.setFirstName("Duplicate");
        duplicateRequest.setLastName("User");
        duplicateRequest.setRole(User.Role.VETERINARY_OFFICER);
        duplicateRequest.setActive(true);

        // Then - Should fail
        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest());
    }
}
