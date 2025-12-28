package com.adrs.service.impl;

import com.adrs.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Local file system implementation of FileStorageService.
 * Stores files in the configured upload directory on the local filesystem.
 * This implementation can be easily replaced with cloud storage (S3, GCS, etc.) by
 * creating a new implementation of FileStorageService.
 */
@Service
public class LocalFileStorageService implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileStorageService.class);

    @Value("${app.storage.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.storage.max-file-size:5242880}")
    private long maxFileSize; // 5MB default

    @Value("${app.storage.allowed-types:image/jpeg,image/png,image/webp,image/gif}")
    private String allowedTypesConfig;

    private Path uploadPath;
    private List<String> allowedTypes;

    @PostConstruct
    public void init() {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.allowedTypes = Arrays.asList(allowedTypesConfig.split(","));
        
        try {
            Files.createDirectories(uploadPath);
            logger.info("File storage initialized at: {}", uploadPath);
        } catch (IOException e) {
            logger.error("Could not create upload directory: {}", uploadPath, e);
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subdirectory) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        // Validate file size
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " + (maxFileSize / 1024 / 1024) + "MB");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: " + String.join(", ", allowedTypes));
        }

        // Generate unique filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        String extension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        try {
            // Create subdirectory if specified
            Path targetDirectory = uploadPath;
            if (subdirectory != null && !subdirectory.isEmpty()) {
                targetDirectory = uploadPath.resolve(subdirectory);
                Files.createDirectories(targetDirectory);
            }

            // Store the file
            Path targetLocation = targetDirectory.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Return relative path
            String relativePath = subdirectory != null && !subdirectory.isEmpty() 
                    ? subdirectory + "/" + uniqueFilename 
                    : uniqueFilename;
            
            logger.info("File stored successfully: {}", relativePath);
            return relativePath;

        } catch (IOException e) {
            logger.error("Failed to store file: {}", originalFilename, e);
            throw new RuntimeException("Could not store file " + originalFilename, e);
        }
    }

    @Override
    public Resource loadFileAsResource(String filePath) {
        try {
            Path file = uploadPath.resolve(filePath).normalize();
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                logger.error("File not found or not readable: {}", filePath);
                throw new RuntimeException("File not found: " + filePath);
            }
        } catch (MalformedURLException e) {
            logger.error("Invalid file path: {}", filePath, e);
            throw new RuntimeException("File not found: " + filePath, e);
        }
    }

    @Override
    public boolean deleteFile(String filePath) {
        try {
            Path file = uploadPath.resolve(filePath).normalize();
            boolean deleted = Files.deleteIfExists(file);
            
            if (deleted) {
                logger.info("File deleted successfully: {}", filePath);
            } else {
                logger.warn("File not found for deletion: {}", filePath);
            }
            
            return deleted;
        } catch (IOException e) {
            logger.error("Failed to delete file: {}", filePath, e);
            return false;
        }
    }

    @Override
    public String getFileUrl(String filePath) {
        // For local storage, return the API endpoint path
        return "/api/files/" + filePath;
    }

    @Override
    public boolean fileExists(String filePath) {
        Path file = uploadPath.resolve(filePath).normalize();
        return Files.exists(file);
    }

    /**
     * Extract file extension from filename.
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }
}
