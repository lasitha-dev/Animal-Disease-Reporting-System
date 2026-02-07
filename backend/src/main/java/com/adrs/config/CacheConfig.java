package com.adrs.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration for the application.
 * Uses Caffeine as the cache provider for high-performance in-memory caching.
 * 
 * Caches are configured for reference data that changes infrequently:
 * - animalTypes: Animal type configuration data
 * - diseases: Disease configuration data
 * - farmTypes: Farm type configuration data
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure the cache manager with Caffeine backend.
     * 
     * Cache settings:
     * - 5 minute TTL (time-to-live) after write
     * - Maximum 500 entries per cache
     * - Statistics recording enabled for monitoring
     *
     * @return the configured CacheManager
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "animalTypes", 
            "diseases", 
            "farmTypes"
        );
        
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(500)
            .recordStats());
        
        return cacheManager;
    }
}
