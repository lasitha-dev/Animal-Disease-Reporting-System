package com.adrs.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for handling page requests and navigation.
 * Returns Thymeleaf template views.
 */
@Controller
public class PageController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    @Value("${app.google.maps.api-key:}")
    private String googleMapsApiKey;

    @Value("${app.map.default-center-lat:7.8731}")
    private Double mapDefaultLat;

    @Value("${app.map.default-center-lng:80.7718}")
    private Double mapDefaultLng;

    @Value("${app.map.default-zoom:7}")
    private Integer mapDefaultZoom;
    private static final String ERROR_PARAM = "error";
    private static final String LOGOUT_PARAM = "logout";
    private static final String LOGIN_VIEW = "auth/login";
    private static final String DASHBOARD_VIEW = "dashboard/dashboard";
    private static final String VET_DASHBOARD_VIEW = "vet/vet-dashboard";
    private static final String VET_FARM_REGISTER_VIEW = "vet/farm-register";

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
     * Displays the admin dashboard page.
     *
     * @param model the model for the view
     * @return the dashboard view name
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Admin dashboard accessed by user: {}", username);
        
        model.addAttribute("username", username);
        return DASHBOARD_VIEW;
    }

    /**
     * Redirects root path based on user role.
     * Admins go to admin dashboard, vets go to vet dashboard.
     *
     * @return redirect to appropriate dashboard
     */
    @GetMapping("/")
    public String root(Authentication authentication) {
        if (authentication != null) {
            boolean isVet = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_VETERINARY_OFFICER"));
            if (isVet) {
                return "redirect:/vet/dashboard";
            }
        }
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

    // ========================================
    // VETERINARY OFFICER PAGES
    // ========================================

    /**
     * Displays the veterinary officer dashboard page.
     *
     * @param model the model for the view
     * @return the vet dashboard view name
     */
    @GetMapping("/vet/dashboard")
    @PreAuthorize("hasRole('VETERINARY_OFFICER')")
    public String vetDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Vet dashboard accessed by: {}", username);
        
        model.addAttribute("username", username);
        model.addAttribute("pageTitle", "Vet Dashboard");
        return VET_DASHBOARD_VIEW;
    }

    /**
     * Displays the farms management page for vets.
     * Shows registered farms with option to add new farms via modal.
     *
     * @param model the model for the view
     * @return the farms view name
     */
    @GetMapping("/vet/farms")
    @PreAuthorize("hasRole('VETERINARY_OFFICER')")
    public String vetFarms(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Farms page accessed by vet: {}", username);
        
        model.addAttribute("username", username);
        model.addAttribute("pageTitle", "My Farms");
        
        // Google Maps configuration
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        model.addAttribute("mapDefaultLat", mapDefaultLat);
        model.addAttribute("mapDefaultLng", mapDefaultLng);
        model.addAttribute("mapDefaultZoom", mapDefaultZoom);
        
        return "vet/farms";
    }
}

