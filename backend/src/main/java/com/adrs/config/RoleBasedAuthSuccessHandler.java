package com.adrs.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom authentication success handler that routes users to role-specific dashboards.
 * ADMIN users are directed to the admin dashboard, while VETERINARY_OFFICER users
 * are directed to the vet dashboard.
 */
@Component
public class RoleBasedAuthSuccessHandler implements AuthenticationSuccessHandler {

    private static final String ADMIN_DASHBOARD_URL = "/dashboard";
    private static final String VET_DASHBOARD_URL = "/vet/dashboard";
    private static final String DEFAULT_URL = "/dashboard";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(authentication);
        response.sendRedirect(request.getContextPath() + targetUrl);
    }

    /**
     * Determines the target URL based on the user's role.
     *
     * @param authentication the authentication object containing user authorities
     * @return the target URL for redirection
     */
    private String determineTargetUrl(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if ("ROLE_ADMIN".equals(role)) {
                return ADMIN_DASHBOARD_URL;
            }
            if ("ROLE_VETERINARY_OFFICER".equals(role)) {
                return VET_DASHBOARD_URL;
            }
        }
        return DEFAULT_URL;
    }
}
