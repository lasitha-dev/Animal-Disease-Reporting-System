package com.adrs.controller;

import com.adrs.dto.UserRequest;
import com.adrs.dto.UserResponse;
import com.adrs.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * MVC controller for user management pages.
 * Handles Thymeleaf template rendering and form submissions.
 */
@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final String USERS_VIEW = "users/user-management";
    private static final String REDIRECT_USERS = "redirect:/users";
    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String ERROR_MESSAGE = "errorMessage";

    @Autowired
    private UserService userService;

    /**
     * Adds the current URI to the model for all requests.
     * This is used for determining active menu items in the layout.
     *
     * @param request the HTTP request
     * @return the current request URI
     */
    @ModelAttribute("currentUri")
    public String getCurrentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /**
     * Displays the user management page with all users.
     *
     * @param model the model for the view
     * @return the user management view name
     */
    @GetMapping
    public String listUsers(Model model) {
        logger.info("User management page requested");
        List<UserResponse> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("userRequest", new UserRequest());
        return USERS_VIEW;
    }

    /**
     * Creates a new user.
     *
     * @param userRequest        the user creation request
     * @param bindingResult      validation results
     * @param redirectAttributes redirect attributes for flash messages
     * @return redirect to users page
     */
    @PostMapping("/create")
    public String createUser(
            @Valid @ModelAttribute("userRequest") UserRequest userRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        logger.info("Create user request received for: {}", userRequest.getUsername());

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Validation failed. Please check the form.");
            return REDIRECT_USERS;
        }

        try {
            userService.createUser(userRequest);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, 
                "User created successfully: " + userRequest.getUsername());
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, 
                "Error creating user: " + e.getMessage());
        }

        return REDIRECT_USERS;
    }

    /**
     * Updates an existing user.
     *
     * @param id                 the user ID
     * @param userRequest        the user update request
     * @param bindingResult      validation results
     * @param redirectAttributes redirect attributes for flash messages
     * @return redirect to users page
     */
    @PostMapping("/update/{id}")
    public String updateUser(
            @PathVariable Long id,
            @Valid @ModelAttribute("userRequest") UserRequest userRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        logger.info("Update user request received for ID: {}", id);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Validation failed. Please check the form.");
            return REDIRECT_USERS;
        }

        try {
            userService.updateUser(id, userRequest);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "User updated successfully");
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, 
                "Error updating user: " + e.getMessage());
        }

        return REDIRECT_USERS;
    }

    /**
     * Deletes a user.
     *
     * @param id                 the user ID
     * @param redirectAttributes redirect attributes for flash messages
     * @return redirect to users page
     */
    @PostMapping("/delete/{id}")
    public String deleteUser(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        logger.info("Delete user request received for ID: {}", id);

        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "User deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, 
                "Error deleting user: " + e.getMessage());
        }

        return REDIRECT_USERS;
    }

    /**
     * Toggles the active status of a user.
     *
     * @param id                 the user ID
     * @param active             the new active status
     * @param redirectAttributes redirect attributes for flash messages
     * @return redirect to users page
     */
    @PostMapping("/toggle-status/{id}")
    public String toggleUserStatus(
            @PathVariable Long id,
            @RequestParam Boolean active,
            RedirectAttributes redirectAttributes) {
        
        logger.info("Toggle user status request received for ID: {} to {}", id, active);

        try {
            userService.toggleUserStatus(id, active);
            String status = Boolean.TRUE.equals(active) ? "activated" : "deactivated";
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, 
                "User " + status + " successfully");
        } catch (Exception e) {
            logger.error("Error toggling user status: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, 
                "Error toggling user status: " + e.getMessage());
        }

        return REDIRECT_USERS;
    }
}
