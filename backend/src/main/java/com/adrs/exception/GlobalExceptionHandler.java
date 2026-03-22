package com.adrs.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST API controllers.
 * Provides consistent JSON error responses across all REST controllers.
 *
 * <p>Has higher priority ({@code @Order(1)}) than {@link MvcExceptionHandler}
 * so that {@code @RestController} endpoints always receive JSON responses,
 * even though MvcExceptionHandler also matches them.</p>
 */
@RestControllerAdvice
@Order(1)
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles ResourceNotFoundException.
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles ConfigurationNotFoundException.
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(ConfigurationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleConfigurationNotFoundException(ConfigurationNotFoundException ex) {
        logger.error("Configuration not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles ConfigurationInUseException.
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(ConfigurationInUseException.class)
    public ResponseEntity<Map<String, Object>> handleConfigurationInUseException(ConfigurationInUseException ex) {
        logger.error("Configuration in use: {} (usage count: {})", ex.getMessage(), ex.getUsageCount());
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles IllegalArgumentException.
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Invalid argument: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles BadCredentialsException.
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException ex) {
        logger.error("Authentication failed: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }

    /**
     * Handles UsernameNotFoundException.
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        logger.error("User not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }

    /**
     * Handles validation errors.
     *
     * @param ex the exception
     * @return error response with field-specific errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.error("Validation failed: {}", errors);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles constraint violation exceptions from @Validated.
     *
     * @param ex the constraint violation exception
     * @return error response with validation errors
     */
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            jakarta.validation.ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        });

        logger.error("Constraint violation: {}", errors);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Invalid Request Parameter");
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles type mismatch exceptions when request parameters cannot be converted.
     *
     * @param ex the type mismatch exception
     * @return error response with type mismatch details
     */
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String invalidValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        
        String message = String.format("Parameter '%s' with value '%s' could not be converted to type %s", 
                                      paramName, invalidValue, requiredType);
        
        logger.error("Type mismatch: {}", message);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Type Mismatch");
        response.put("message", message);
        response.put("parameter", paramName);
        response.put("invalidValue", invalidValue);
        response.put("requiredType", requiredType);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles access denied exceptions when user lacks required permissions.
     *
     * @param ex the access denied exception
     * @return error response with 403 status
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {
        logger.error("Access denied: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("error", "Access Denied");
        response.put("message", "You do not have permission to access this resource");

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles duplicate resource exceptions when a unique constraint would be violated.
     *
     * @param ex the duplicate resource exception
     * @return error response with 409 Conflict status
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateResourceException(DuplicateResourceException ex) {
        logger.error("Duplicate resource: {} - field: {}, value: {}", ex.getEntityType(), ex.getFieldName(), ex.getFieldValue());

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "Duplicate Resource");
        response.put("message", ex.getMessage());
        response.put("entityType", ex.getEntityType());
        response.put("field", ex.getFieldName());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handles database integrity constraint violations (e.g., unique constraint violations
     * that bypass service-layer checks due to race conditions).
     *
     * @param ex the data integrity violation exception
     * @return error response with 409 Conflict status and a user-friendly message
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());

        String userMessage = parseConstraintViolationMessage(ex);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "Data Integrity Violation");
        response.put("message", userMessage);

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handles all other exceptions.
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    /**
     * Parses a DataIntegrityViolationException to extract a user-friendly message
     * based on the violated constraint name.
     *
     * @param ex the data integrity violation exception
     * @return a user-friendly error message
     */
    private String parseConstraintViolationMessage(DataIntegrityViolationException ex) {
        String rootCauseMessage = ex.getMostSpecificCause().getMessage();
        String lowerMessage = rootCauseMessage.toLowerCase();

        // Parse PostgreSQL unique constraint violation messages
        if (lowerMessage.contains("unique") || lowerMessage.contains("duplicate")) {
            if (lowerMessage.contains("username") || lowerMessage.contains("uk_users_username")) {
                return "A user with this username already exists";
            }
            if (lowerMessage.contains("email") || lowerMessage.contains("uk_users_email")) {
                return "A user with this email already exists";
            }
            if (lowerMessage.contains("farm_name") || lowerMessage.contains("uk_farms_farm_name")) {
                return "A farm with this name already exists";
            }
            if (lowerMessage.contains("type_name")) {
                return "An entry with this name already exists";
            }
            if (lowerMessage.contains("disease_name")) {
                return "A disease with this name already exists";
            }
            if (lowerMessage.contains("disease_code")) {
                return "A disease with this code already exists";
            }
            if (lowerMessage.contains("tag_number")) {
                return "An animal with this tag number already exists";
            }
            return "A record with this value already exists. Please use a different value.";
        }

        return "A data integrity error occurred. Please check your input and try again.";
    }

    /**
     * Builds a standardized error response.
     *
     * @param status  the HTTP status
     * @param message the error message
     * @return error response
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);

        return new ResponseEntity<>(response, status);
    }
}
