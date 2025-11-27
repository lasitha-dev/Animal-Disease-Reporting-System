package com.adrs.exception;

import java.util.UUID;

/**
 * Exception thrown when attempting to delete or deactivate a configuration entity
 * that is currently in use by other entities in the system.
 * 
 * This exception provides information about the entity being deleted and
 * the count of dependent entities that would be affected.
 */
public class ConfigurationInUseException extends RuntimeException {

    private final String entityType;
    private final UUID entityId;
    private final Long usageCount;

    /**
     * Constructor with entity details and usage count.
     *
     * @param entityType the type of entity (e.g., "FarmType", "AnimalType")
     * @param entityId the UUID of the entity
     * @param usageCount the number of dependent entities using this configuration
     */
    public ConfigurationInUseException(String entityType, UUID entityId, Long usageCount) {
        super(String.format("Cannot delete %s with ID '%s' because it is being used by %d %s(s)",
                entityType, entityId, usageCount, getDependentEntityName(entityType)));
        this.entityType = entityType;
        this.entityId = entityId;
        this.usageCount = usageCount;
    }

    /**
     * Constructor with custom message.
     *
     * @param message the custom error message
     * @param entityType the type of entity
     * @param entityId the UUID of the entity
     * @param usageCount the usage count
     */
    public ConfigurationInUseException(String message, String entityType, UUID entityId, Long usageCount) {
        super(message);
        this.entityType = entityType;
        this.entityId = entityId;
        this.usageCount = usageCount;
    }

    /**
     * Get the dependent entity name based on configuration type.
     *
     * @param entityType the configuration entity type
     * @return the dependent entity name
     */
    private static String getDependentEntityName(String entityType) {
        return switch (entityType) {
            case "FarmType" -> "farm";
            case "AnimalType" -> "animal";
            case "Disease" -> "disease report";
            default -> "entity";
        };
    }

    public String getEntityType() {
        return entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public Long getUsageCount() {
        return usageCount;
    }
}
