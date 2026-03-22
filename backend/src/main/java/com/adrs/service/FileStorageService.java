package com.adrs.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for file storage operations.
 * This abstraction allows easy migration from local storage to cloud storage (S3, GCS, etc.)
 */
public interface FileStorageService {

    /**
     * Store a file in the specified subdirectory.
     *
     * @param file the file to store
     * @param subdirectory the subdirectory within the base upload directory
     * @return the relative path to the stored file
     */
    String storeFile(MultipartFile file, String subdirectory);

    /**
     * Load a file as a resource.
     *
     * @param filePath the relative path to the file
     * @return the file as a Resource
     */
    Resource loadFileAsResource(String filePath);

    /**
     * Delete a file.
     *
     * @param filePath the relative path to the file
     * @return true if file was deleted, false otherwise
     */
    boolean deleteFile(String filePath);

    /**
     * Get the URL/path for accessing a stored file.
     *
     * @param filePath the relative path to the file
     * @return the URL or path for accessing the file
     */
    String getFileUrl(String filePath);

    /**
     * Check if a file exists.
     *
     * @param filePath the relative path to the file
     * @return true if file exists, false otherwise
     */
    boolean fileExists(String filePath);
}
