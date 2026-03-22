package com.adrs.exception;

/**
 * Exception thrown when attempting to create or update a resource
 * that would result in a duplicate value for a unique field.
 *
 * <p>Provides information about the entity type and the specific field
 * that caused the conflict, enabling clear user-facing error messages.</p>
 */
public class DuplicateResourceException extends RuntimeException {

    private final String entityType;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * Constructor with entity type, field name, and the duplicate value.
     *
     * @param entityType the type of entity (e.g., "User", "Farm", "FarmType")
     * @param fieldName  the field that has a duplicate value (e.g., "username", "farmName")
     * @param fieldValue the duplicate value that was attempted
     */
    public DuplicateResourceException(String entityType, String fieldName, Object fieldValue) {
        super(String.format("%s with %s '%s' already exists", entityType, fieldName, fieldValue));
        this.entityType = entityType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Constructor with a custom message.
     *
     * @param message    the custom error message
     * @param entityType the type of entity
     * @param fieldName  the field that has a duplicate value
     * @param fieldValue the duplicate value
     */
    public DuplicateResourceException(String message, String entityType, String fieldName, Object fieldValue) {
        super(message);
        this.entityType = entityType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Gets the entity type that caused the conflict.
     *
     * @return the entity type name
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Gets the field name that has a duplicate value.
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Gets the duplicate field value that caused the conflict.
     *
     * @return the field value
     */
    public Object getFieldValue() {
        return fieldValue;
    }
}
