package com.adrs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration for the application.
 * Configures static resource handling and path matching.
 * 
 * This configuration ensures that API endpoints under /api/** are not treated
 * as static resources by Spring Boot's default resource handler.
 * 
 * @author ADRS Team
 * @version 1.0
 * @since 2025-01-29
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Configure resource handlers for static content.
     * Explicitly defines static resource locations to prevent /api/** from being
     * matched by the default resource handler.
     *
     * @param registry the resource handler registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Explicitly configure static resources to exclude /api/**
        registry
                .addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        
        registry
                .addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        
        registry
                .addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
        
        registry
                .addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * Configure path matching for controllers.
     * Ensures that trailing slashes are handled consistently.
     *
     * @param configurer the path match configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(false);
    }
}
