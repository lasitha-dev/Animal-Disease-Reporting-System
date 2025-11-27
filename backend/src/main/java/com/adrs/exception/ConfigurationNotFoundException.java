package com.adrs.exception;

import java.util.UUID;

/**
 * Exception thrown when a configuration entity (FarmType, AnimalType, Disease)
 * is not found in the system.
 * 
 * This exception extends RuntimeException and can be caught by global
 * exception handlers to return appropriate HTTP responses.
 */
public class ConfigurationNotFoundException extends RuntimeException {

    private final String entityType;
    private final UUID id;
    private final String identifier;

    /**
     * Constructor with entity type and ID.
     *
     * @param entityType the type of entity (e.g., "FarmType", "AnimalType")
     * @param id the UUID of the entity
     */
    public ConfigurationNotFoundException(String entityType, UUID id) {
        super(String.format("%s with ID '%s' not found", entityType, id));
        this.entityType = entityType;
        this.id = id;
        this.identifier = id.toString();
    }

    /**
     * Constructor with entity type and string identifier.
     *
     * @param entityType the type of entity
     * @param identifier the identifier (e.g., name or code)
     */
    public ConfigurationNotFoundException(String entityType, String identifier) {
        super(String.format("%s with identifier '%s' not found", entityType, identifier));
        this.entityType = entityType;
        this.id = null;
        this.identifier = identifier;
    }

    /**
     * Constructor with custom message.
     *
     * @param message the custom error message
     */
    public ConfigurationNotFoundException(String message) {
        super(message);
        this.entityType = "Configuration";
        this.id = null;
        this.identifier = null;
    }

    public String getEntityType() {
        return entityType;
    }

    public UUID getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }
}
