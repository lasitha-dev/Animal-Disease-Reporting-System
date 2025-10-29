package com.adrs.test.controller;

import com.adrs.controller.UserController;
import com.adrs.dto.UserRequest;
import com.adrs.dto.UserResponse;
import com.adrs.model.User;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController.
 * Tests REST API endpoints for user management.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("User Controller Tests")
class UserControllerTest {

    private static final String USERS_ENDPOINT = "/api/users";
    private static final String USER_BY_ID_ENDPOINT = "/api/users/{id}";
    private static final Long TEST_USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserRequest userRequest;
    private UserResponse userResponse;

    /**
     * Set up test data before each test.
     */
    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();
        userRequest.setUsername("testuser");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password123");
        userRequest.setFirstName("Test");
        userRequest.setLastName("User");
        userRequest.setPhoneNumber("+94771234567");
        userRequest.setRole(User.Role.VETERINARY_OFFICER);
        userRequest.setActive(true);

        userResponse = new UserResponse();
        userResponse.setId(TEST_USER_ID);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");
        userResponse.setFirstName("Test");
        userResponse.setLastName("User");
        userResponse.setPhoneNumber("+94771234567");
        userResponse.setRole("VETERINARY_OFFICER");
        userResponse.setActive(true);
        userResponse.setCreatedAt(LocalDateTime.now());
        userResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create user successfully as admin")
    void testCreateUser() throws Exception {
        // Given
        when(userService.createUser(any(UserRequest.class))).thenReturn(userResponse);

        // When/Then
        mockMvc.perform(post(USERS_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_USER_ID))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService).createUser(any(UserRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get user by ID successfully")
    void testGetUserById() throws Exception {
        // Given
        when(userService.getUserById(TEST_USER_ID)).thenReturn(userResponse);

        // When/Then
        mockMvc.perform(get(USER_BY_ID_ENDPOINT, TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_USER_ID))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService).getUserById(TEST_USER_ID);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get all users successfully")
    void testGetAllUsers() throws Exception {
        // Given
        UserResponse user2 = new UserResponse();
        user2.setId(2L);
        user2.setUsername("testuser2");
        user2.setEmail("test2@example.com");
        user2.setFirstName("Test2");
        user2.setLastName("User2");
        user2.setRole("ADMIN");
        user2.setActive(true);

        List<UserResponse> users = Arrays.asList(userResponse, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // When/Then
        mockMvc.perform(get(USERS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[1].username").value("testuser2"));

        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update user successfully as admin")
    void testUpdateUser() throws Exception {
        // Given
        when(userService.updateUser(eq(TEST_USER_ID), any(UserRequest.class))).thenReturn(userResponse);

        // When/Then
        mockMvc.perform(put(USER_BY_ID_ENDPOINT, TEST_USER_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_USER_ID));

        verify(userService).updateUser(eq(TEST_USER_ID), any(UserRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete user successfully as admin")
    void testDeleteUser() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(TEST_USER_ID);

        // When/Then
        mockMvc.perform(delete(USER_BY_ID_ENDPOINT, TEST_USER_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(TEST_USER_ID);
    }
}
