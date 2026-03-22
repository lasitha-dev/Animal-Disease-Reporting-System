package com.adrs.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Exception handler for MVC {@link Controller} endpoints.
 *
 * <p>Catches exceptions from view controllers and redirects with flash
 * attribute error messages instead of returning JSON responses.
 * This complements {@link GlobalExceptionHandler} which handles
 * {@code @RestController} exceptions.</p>
 */
@ControllerAdvice(annotations = Controller.class)
@Order(2)
public class MvcExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(MvcExceptionHandler.class);
    private static final String ERROR_MESSAGE = "errorMessage";

    /**
     * Handles duplicate resource exceptions in MVC controllers.
     *
     * @param ex                 the duplicate resource exception
     * @param redirectAttributes redirect attributes for flash messages
     * @param request            the HTTP request
     * @return redirect to the referring page
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public String handleDuplicateResourceException(
            DuplicateResourceException ex,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        logger.error("Duplicate resource in MVC context: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR_MESSAGE, ex.getMessage());
        return resolveRedirect(request);
    }

    /**
     * Handles data integrity violations in MVC controllers.
     *
     * @param ex                 the data integrity violation exception
     * @param redirectAttributes redirect attributes for flash messages
     * @param request            the HTTP request
     * @return redirect to the referring page
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        logger.error("Data integrity violation in MVC context: {}", ex.getMostSpecificCause().getMessage());
        redirectAttributes.addFlashAttribute(ERROR_MESSAGE,
                "A record with this value already exists. Please use a different value.");
        return resolveRedirect(request);
    }

    /**
     * Handles resource not found exceptions in MVC controllers.
     *
     * @param ex                 the resource not found exception
     * @param redirectAttributes redirect attributes for flash messages
     * @param request            the HTTP request
     * @return redirect to the referring page
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(
            ResourceNotFoundException ex,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        logger.error("Resource not found in MVC context: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR_MESSAGE, ex.getMessage());
        return resolveRedirect(request);
    }

    /**
     * Handles illegal argument exceptions in MVC controllers.
     *
     * @param ex                 the illegal argument exception
     * @param redirectAttributes redirect attributes for flash messages
     * @param request            the HTTP request
     * @return redirect to the referring page
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(
            IllegalArgumentException ex,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        logger.error("Invalid argument in MVC context: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR_MESSAGE, ex.getMessage());
        return resolveRedirect(request);
    }

    /**
     * Resolves the redirect target from the Referer header, falling back
     * to the current request URI path.
     *
     * @param request the HTTP request
     * @return the redirect path
     */
    private String resolveRedirect(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            // Extract path from the full URL to avoid open redirect
            try {
                java.net.URI uri = java.net.URI.create(referer);
                return "redirect:" + uri.getPath();
            } catch (IllegalArgumentException e) {
                logger.warn("Could not parse Referer header: {}", referer);
            }
        }
        return "redirect:" + request.getRequestURI();
    }
}
