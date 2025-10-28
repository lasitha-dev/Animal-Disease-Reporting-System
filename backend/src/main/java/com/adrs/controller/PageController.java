package com.adrs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for handling page requests and navigation.
 * Returns Thymeleaf template views.
 */
@Controller
public class PageController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);
    private static final String ERROR_PARAM = "error";
    private static final String LOGOUT_PARAM = "logout";
    private static final String LOGIN_VIEW = "auth/login";
    private static final String DASHBOARD_VIEW = "dashboard/dashboard";

    /**
     * Displays the login page.
     *
     * @param error  optional error parameter
     * @param logout optional logout parameter
     * @param model  the model for the view
     * @return the login view name
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(value = ERROR_PARAM, required = false) String error,
            @RequestParam(value = LOGOUT_PARAM, required = false) String logout,
            Model model) {
        
        logger.info("Login page requested");

        if (error != null) {
            model.addAttribute(ERROR_PARAM, "Invalid username or password");
            logger.warn("Login attempt failed - invalid credentials");
        }

        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
            logger.info("User logged out successfully");
        }

        return LOGIN_VIEW;
    }

    /**
     * Displays the dashboard page.
     *
     * @param model the model for the view
     * @return the dashboard view name
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Dashboard accessed by user: {}", username);
        
        model.addAttribute("username", username);
        return DASHBOARD_VIEW;
    }

    /**
     * Redirects root path to dashboard.
     *
     * @return redirect to dashboard
     */
    @GetMapping("/")
    public String root() {
        logger.debug("Root path accessed, redirecting to dashboard");
        return "redirect:/dashboard";
    }

    /**
     * Displays the configuration management page.
     * Admin-only access.
     *
     * @param model the model for the view
     * @return the configuration view name
     */
    @GetMapping("/configuration")
    @PreAuthorize("hasRole('ADMIN')")
    public String configuration(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Configuration page accessed by admin: {}", username);
        
        model.addAttribute("username", username);
        return "configuration/configuration";
    }
}
