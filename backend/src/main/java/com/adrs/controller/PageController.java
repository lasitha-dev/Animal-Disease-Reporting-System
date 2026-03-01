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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

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
    private static final String ACCESS_DENIED_VIEW = "error/access-denied";

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
     * Displays the access denied page when a user lacks permissions.
     * This typically occurs when a user's session changes (e.g., logging in
     * as a different user in another browser tab).
     *
     * @return the access denied view name
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "anonymous";
        logger.warn("Access denied page shown to user: {}", username);
        return ACCESS_DENIED_VIEW;
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

    /**
     * Displays the map page for vets showing all farm locations.
     * Uses Google Maps to display farms as markers.
     *
     * @param model the model for the view
     * @return the map view name
     */
    @GetMapping("/vet/map")
    @PreAuthorize("hasRole('VETERINARY_OFFICER')")
    public String vetMap(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Map page accessed by vet: {}", username);
        
        model.addAttribute("username", username);
        model.addAttribute("pageTitle", "Farm Map");
        
        // Google Maps configuration
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        model.addAttribute("mapDefaultLat", mapDefaultLat);
        model.addAttribute("mapDefaultLng", mapDefaultLng);
        model.addAttribute("mapDefaultZoom", mapDefaultZoom);
        
        return "vet/map";
    }

    /**
     * Displays the disease reporting page for vets.
     * Allows vets to submit new disease reports.
     *
     * @param model the model for the view
     * @return the disease reporting view name
     */
    @GetMapping("/vet/disease-reporting")
    @PreAuthorize("hasRole('VETERINARY_OFFICER')")
    public String vetDiseaseReporting(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Disease reporting page accessed by vet: {}", username);
        
        model.addAttribute("username", username);
        model.addAttribute("pageTitle", "Disease Reporting");
        
        return "vet/disease-reporting";
    }

    /**
     * Displays the farm diseases page showing all disease reports for a specific farm.
     * Allows filtering by animal type and disease.
     *
     * @param farmId the farm ID
     * @param model the model for the view
     * @return the farm diseases view name
     */
    @GetMapping("/vet/disease-reporting/farm/{farmId}")
    @PreAuthorize("hasRole('VETERINARY_OFFICER')")
    public String vetFarmDiseases(@PathVariable UUID farmId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Farm diseases page accessed by vet: {} for farm: {}", username, farmId);
        
        model.addAttribute("username", username);
        model.addAttribute("pageTitle", "Farm Diseases");
        model.addAttribute("farmId", farmId.toString());
        
        return "vet/farm-diseases";
    }

    /**
     * Displays the analytics page for vets.
     * Shows disease trend charts with filtering options.
     *
     * @param model the model for the view
     * @return the analytics view name
     */
    @GetMapping("/vet/analytics")
    @PreAuthorize("hasRole('VETERINARY_OFFICER')")
    public String vetAnalytics(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Analytics page accessed by vet: {}", username);
        
        model.addAttribute("username", username);
        model.addAttribute("pageTitle", "Analytics");
        
        return "vet/analytics";
    }
}

