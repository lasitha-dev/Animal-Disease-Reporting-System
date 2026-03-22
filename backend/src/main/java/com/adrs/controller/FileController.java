package com.adrs.controller;

import com.adrs.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Controller for serving uploaded files.
 * Handles requests to /api/files/** for serving images and other uploaded content.
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * Serve uploaded files from a subdirectory.
     *
     * @param subdirectory the subdirectory (e.g., "disease-images")
     * @param filename the filename
     * @param request the HTTP request
     * @return the file as a resource
     */
    @GetMapping("/{subdirectory}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(
            @PathVariable String subdirectory,
            @PathVariable String filename,
            HttpServletRequest request) {
        
        String filePath = subdirectory + "/" + filename;
        logger.debug("Serving file: {}", filePath);
        
        try {
            Resource resource = fileStorageService.loadFileAsResource(filePath);
            
            // Determine content type
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                logger.warn("Could not determine file type for: {}", filePath);
            }
            
            if (contentType == null) {
                // Default to binary stream if type cannot be determined
                contentType = "application/octet-stream";
            }
            
            // Determine the appropriate filename for the response
            String responseFilename = filename;
            if (resource.getFilename() != null) {
                responseFilename = resource.getFilename();
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + responseFilename + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            logger.error("Error serving file: {}", filePath, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Serve uploaded files from root upload directory (no subdirectory).
     *
     * @param filename the filename
     * @param request the HTTP request
     * @return the file as a resource
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveRootFile(
            @PathVariable String filename,
            HttpServletRequest request) {
        
        logger.debug("Serving root file: {}", filename);
        
        try {
            Resource resource = fileStorageService.loadFileAsResource(filename);
            
            // Determine content type
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                logger.warn("Could not determine file type for: {}", filename);
            }
            
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            logger.error("Error serving file: {}", filename, e);
            return ResponseEntity.notFound().build();
        }
    }
}
