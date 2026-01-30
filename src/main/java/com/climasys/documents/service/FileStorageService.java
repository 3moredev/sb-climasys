package com.climasys.documents.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for handling file storage operations
 * Equivalent to .NET file upload logic using Server.MapPath and SaveAs
 */
@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    // Mapped from Web.config appSettings - using exact same names
    @Value("${climasys.file-upload.upload-patient}")
    private String uploadPatient;

    @Value("${climasys.file-upload.upload-folder-path}")
    private String uploadFolderPath;

    @Value("${climasys.file-upload.upload-profile}")
    private String uploadProfile;

    @Value("${climasys.file-upload.upload-reminders}")
    private String uploadReminders;

    @Value("${climasys.file-upload.upload-treatment-details}")
    private String uploadTreatmentDetails;

    @Value("${climasys.file-upload.upload-keyword-investigations}")
    private String uploadKeywordInvestigations;

    // File deletion configuration
    @Value("${climasys.file-upload.delete.strict-mode:false}")
    private boolean strictMode;

    @Value("${climasys.file-upload.delete.detailed-logging:true}")
    private boolean detailedLogging;

    @Value("${climasys.file-upload.delete.retry-attempts:2}")
    private int retryAttempts;

    @Value("${climasys.file-upload.max-files-per-upload:5}")
    private int maxFilesPerUpload;

    /**
     * Save a single file for a patient
     * Equivalent to .NET: Server.MapPath + SaveAs
     *
     * /**
     * Helper class to return file save result
     */
    public record FileUploadResult(String relativePath, long fileSize) {
    }

    public FileUploadResult saveFileWithResult(MultipartFile file, String patientId, String clinicId, String uploadType)
            throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        // Get the base path based on upload type
        String basePath = getBasePathForType(uploadType);

        // Get current date for folder structure
        String dateStr = java.time.LocalDate.now().toString();

        // Create directory structure: basePath/clinicId/patientId/date/
        Path directoryPath;
        if (clinicId != null && !clinicId.isEmpty()) {
            directoryPath = Paths.get("files", clinicId, basePath, patientId, dateStr);
        } else {
            // Fallback if clinicId is not provided (though it should be)
            directoryPath = Paths.get("files", basePath, patientId, dateStr);
        }

        Path absoluteDirPath = directoryPath.toAbsolutePath();

        // Equivalent to: if (!Directory.Exists(folder)) {
        // Directory.CreateDirectory(folder); }
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
                logger.info("Created directory: {}", absoluteDirPath);
            } catch (IOException e) {
                logger.error("Failed to create directory: {}. Reason: {}", absoluteDirPath, e.getMessage());
                throw new IOException(
                        "Could not create upload directory: " + absoluteDirPath + ". Check server permissions.", e);
            }
        }

        // Verify write permissions
        if (!Files.isWritable(directoryPath)) {
            logger.error("No write permission for directory: {}", absoluteDirPath);
            throw new IOException("Application lacks write permission to: " + absoluteDirPath);
        }

        // Get original filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            originalFilename = "file_" + UUID.randomUUID();
        }

        // Clean filename to prevent path traversal attacks
        String cleanFilename = cleanFilename(originalFilename);

        // Full path for saving
        Path filePath = directoryPath.resolve(cleanFilename);
        Path absoluteFilePath = filePath.toAbsolutePath();

        try {
            // Save the file - Optimized: Use transferTo which may move the temp file
            // (zero-copy)
            file.transferTo(filePath);
            long size = file.getSize();
            logger.info("File saved successfully ({} bytes) to: {}", size, absoluteFilePath);

            // Return relative path for database storage and file size            
            String folderType = uploadType;
            // Fix path mismatch if using patient-documents generic type
            if ("patient-documents".equalsIgnoreCase(uploadType)) {
                folderType = "patient-uploads";
            }

            String relativePath;
            if (clinicId != null && !clinicId.isEmpty()) {
                relativePath = "/files/" + clinicId + "/file-uploads/" + folderType + "/" + patientId + "/" + dateStr + "/"
                        + cleanFilename;
            } else {
                relativePath = "/files/file-uploads/" + folderType + "/" + patientId + "/" + dateStr + "/" + cleanFilename;
            }

            return new FileUploadResult(relativePath, size);
        } catch (IOException e) {
            logger.error("Failed to save file to: {}. Reason: {}", absoluteFilePath, e.getMessage());
            throw new IOException("Failed to save file to " + absoluteFilePath + ". Check disk space and permissions.",
                    e);
        }
    }

    public String saveFile(MultipartFile file, String patientId, String clinicId, String uploadType)
            throws IOException {
        return saveFileWithResult(file, patientId, clinicId, uploadType).relativePath();
    }

    /**
     * Save multiple files for a patient
     * Equivalent to .NET: Handling HttpFileCollection in a loop
     *
     * @param files      Array of MultipartFiles
     * @param patientId  Patient ID
     * @param clinicId   Clinic ID
     * @param uploadType Type of upload
     * @return List of relative paths to saved files
     */
    public List<String> saveMultipleFiles(MultipartFile[] files, String patientId, String clinicId, String uploadType)
            throws IOException {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No files provided");
        }

        // Equivalent to: if (hfc.Count - 1 <= Constants.FILE_COUNT_IPD)
        if (files.length > maxFilesPerUpload) {
            throw new IllegalArgumentException("Too many files. Maximum allowed: " + maxFilesPerUpload);
        }

        List<String> savedFilePaths = new ArrayList<>();

        // Equivalent to: for (int i = 0; i < hfc.Count - 1; i++)
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String filePath = saveFile(file, patientId, clinicId, uploadType);
                savedFilePaths.add(filePath);
            }
        }

        logger.info("Saved {} files for patient: {}", savedFilePaths.size(), patientId);
        return savedFilePaths;
    }

    /**
     * Save multiple files and return detailed results for each
     *
     * @param files      Array of MultipartFiles
     * @param patientId  Patient ID
     * @param clinicId   Clinic ID
     * @param uploadType Type of upload
     * @return List of FileUploadResult objects
     */
    public List<FileUploadResult> saveMultipleFilesWithResults(MultipartFile[] files, String patientId, String clinicId,
            String uploadType)
            throws IOException {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No files provided");
        }

        if (files.length > maxFilesPerUpload) {
            throw new IllegalArgumentException("Too many files. Maximum allowed: " + maxFilesPerUpload);
        }

        List<FileUploadResult> results = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                FileUploadResult result = saveFileWithResult(file, patientId, clinicId, uploadType);
                results.add(result);
            }
        }

        logger.info("Saved {} files with results for patient: {}", results.size(), patientId);
        return results;
    }

    /**
     * Delete a file from the file system
     *
     * @param relativePath Relative path to the file
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteFile(String relativePath) {
        if (detailedLogging) {
            logger.info("Attempting to delete file with path: {}", relativePath);
        }

        // Try multiple attempts with retry logic
        for (int attempt = 1; attempt <= retryAttempts; attempt++) {
            try {
                if (detailedLogging && attempt > 1) {
                    logger.info("Retry attempt {} for file deletion: {}", attempt, relativePath);
                }

                // Use the same path resolution logic as getFileBytes and fileExists
                Path resolvedPath = resolveFilePath(relativePath);

                if (resolvedPath != null && Files.exists(resolvedPath)) {
                    Files.delete(resolvedPath);
                    logger.info("File deleted successfully: {}", resolvedPath.toAbsolutePath());
                    return true;
                }

                if (attempt == retryAttempts) {
                    logger.warn("File not found for deletion after {} attempts. Original path: {}", retryAttempts,
                            relativePath);
                }

            } catch (IOException e) {
                logger.error("Error deleting file (attempt {}): {}", attempt, relativePath, e);
                if (attempt == retryAttempts) {
                    return false;
                }
                // Wait before retry (exponential backoff)
                try {
                    Thread.sleep(100 * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * Check if a file exists
     *
     * @param relativePath Relative path to check
     * @return true if file exists, false otherwise
     */
    public boolean fileExists(String relativePath) {
        try {
            Path resolvedPath = resolveFilePath(relativePath);
            return resolvedPath != null && Files.exists(resolvedPath);
        } catch (Exception e) {
            logger.error("Error checking file existence: {}", relativePath, e);
            return false;
        }
    }

    /**
     * Get file as byte array for download
     * Equivalent to .NET: req.DownloadData(Server.MapPath(strURL))
     *
     * @param relativePath Relative path to file
     * @return File contents as byte array
     */
    public byte[] getFileBytes(String relativePath) throws IOException {
        Path resolvedPath = resolveFilePath(relativePath);

        if (resolvedPath == null || !Files.exists(resolvedPath)) {
            String resolvedStr = resolvedPath != null ? resolvedPath.toAbsolutePath().toString() : "null";
            throw new IOException("File not found: " + relativePath + " (resolved to: " + resolvedStr + ")");
        }

        logger.debug("Reading file from: {}", resolvedPath.toAbsolutePath());
        return Files.readAllBytes(resolvedPath);
    }

    /**
     * Get file input stream for streaming download
     *
     * @param relativePath Relative path to file
     * @return InputStream of the file
     */
    public java.io.InputStream getFileInputStream(String relativePath) throws IOException {
        Path resolvedPath = resolveFilePath(relativePath);

        if (resolvedPath == null || !Files.exists(resolvedPath)) {
            String resolvedStr = resolvedPath != null ? resolvedPath.toAbsolutePath().toString() : "null";
            throw new IOException("File not found: " + relativePath + " (resolved to: " + resolvedStr + ")");
        }

        logger.debug("Opening file stream from: {}", resolvedPath.toAbsolutePath());
        return Files.newInputStream(resolvedPath);
    }

    /**
     * Resolve a relative path stored in the database to the actual file system path
     * Maps database paths like /patient-documents/ to actual folder structure like
     * PatientUploads/
     *
     * @param relativePath Relative path from database
     * @return Resolved Path object, or null if not found
     */
    private Path resolveFilePath(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }

        // Try multiple path resolution strategies (same as deleteFile)
        Path[] possiblePaths = {
                // Strategy 1: Direct path
                Paths.get(relativePath),
                // Strategy 2: Relative to current working directory
                Paths.get(System.getProperty("user.dir"), relativePath),
                // Strategy 3: Relative to parent directory (for sb-climasys subdirectory
                // structure)
                Paths.get(System.getProperty("user.dir"), "..", relativePath),
                // Strategy 4: Relative to upload folder path if configured
                uploadFolderPath != null ? Paths.get(uploadFolderPath, relativePath) : null,
                // Strategy 5: Remove leading slash if present
                relativePath.startsWith("/") ? Paths.get(relativePath.substring(1)) : null,
                // Strategy 6: Try with uploads prefix
                Paths.get("uploads", relativePath),
                // Strategy 7: Try with uploads prefix and remove leading slash
                relativePath.startsWith("/") ? Paths.get("uploads", relativePath.substring(1)) : null,
                // Strategy 8: Map patient-documents to PatientUploads (based on actual file
                // structure)
                relativePath.contains("/patient-documents/")
                        ? Paths.get(
                                relativePath.replace("/patient-documents/", "PatientUploads/").replaceFirst("^/", ""))
                        : null,
                // Strategy 9: Map patient-documents to PatientUploads with absolute path from
                // working dir
                relativePath.startsWith("/patient-documents/") ? Paths.get(System.getProperty("user.dir"),
                        relativePath.replace("/patient-documents/", "PatientUploads/").substring(1)) : null,
                // Strategy 10: Use configured upload-patient path + relative path (remove
                // /patient-documents/ prefix)
                relativePath.startsWith("/patient-documents/")
                        ? Paths.get(uploadPatient, relativePath.substring("/patient-documents/".length()))
                        : null,
                // Strategy 11: Handle new patient-uploads path format
                relativePath.startsWith("/patient-uploads/")
                        ? Paths.get(uploadPatient, relativePath.substring("/patient-uploads/".length()))
                        : null
        };

        // Find the first path that exists
        for (Path filePath : possiblePaths) {
            if (filePath != null && Files.exists(filePath)) {
                if (detailedLogging) {
                    logger.debug("Resolved path {} to {}", relativePath, filePath.toAbsolutePath());
                }
                return filePath;
            }
        }

        // If no existing path found, return the most likely candidate
        if (relativePath.startsWith("/patient-documents/")) {
            // Legacy mapping
            Path mappedPath = Paths.get(uploadPatient, relativePath.substring("/patient-documents/".length()));
            if (detailedLogging) {
                logger.debug("No existing path found, using mapped path (legacy): {}", mappedPath.toAbsolutePath());
            }
            return mappedPath;
        } else if (relativePath.startsWith("/patient-uploads/")) {
            // New mapping
            Path mappedPath = Paths.get(uploadPatient, relativePath.substring("/patient-uploads/".length()));
            if (detailedLogging) {
                logger.debug("No existing path found, using mapped path: {}", mappedPath.toAbsolutePath());
            }
            return mappedPath;
        }

        // Fallback: relative to working directory without leading slash
        Path fallbackPath = relativePath.startsWith("/") ? Paths.get(relativePath.substring(1))
                : Paths.get(relativePath);
        if (detailedLogging) {
            logger.debug("Using fallback path: {}", fallbackPath.toAbsolutePath());
        }
        return fallbackPath;
    }

    /**
     * Get base path based on upload type
     * Mapped from .NET Web.config appSettings
     *
     * @param uploadType Type of upload
     * @return Base path string
     */
    private String getBasePathForType(String uploadType) {
        return switch (uploadType.toLowerCase()) {
            // UPLOADPATIENT - Main patient documents (used in treatment screen)
            case "patient", "patient-documents", "uploadpatient" -> uploadPatient;

            // UPLOADPROFILE - MR profile photos
            case "mr-profile", "profile", "uploadprofile" -> uploadProfile;

            // UPLOADTREATMENTDETAILS - Treatment details documents
            case "treatment", "treatment-details", "uploadtreatmentdetails" -> uploadTreatmentDetails;

            // UPLOADREMINDERS - Reminder attachments
            case "reminders", "uploadreminders" -> uploadReminders;

            // UPLOADFOLDERPATH - General attached documents
            case "attached-documents", "uploadfolderpath" -> uploadFolderPath;

            // UPLOADKEYWORDINVESTIGATIONS - Keyword investigations
            case "keyword-investigations", "uploadkeywordinvestigations" -> uploadKeywordInvestigations;

            default -> uploadPatient; // Default to patient documents
        };
    }

    /**
     * Clean filename to prevent security issues
     * Remove path traversal attempts and invalid characters
     *
     * @param filename Original filename
     * @return Cleaned filename
     */
    private String cleanFilename(String filename) {
        // Remove any path components
        String cleaned = Paths.get(filename).getFileName().toString();

        // Replace any remaining problematic characters
        cleaned = cleaned.replaceAll("[^a-zA-Z0-9._-]", "_");

        return cleaned;
    }

    /**
     * Validate file size
     * Equivalent to .NET: if (filesize > 4)
     *
     * @param file      File to validate
     * @param maxSizeMB Maximum size in MB
     * @return true if valid, false otherwise
     */
    public boolean validateFileSize(MultipartFile file, long maxSizeMB) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        long fileSizeInMB = file.getSize() / (1024 * 1024);
        return fileSizeInMB <= maxSizeMB;
    }

    /**
     * Validate total size of multiple files
     *
     * @param files          Array of files
     * @param maxTotalSizeMB Maximum total size in MB
     * @return true if total size is valid, false otherwise
     */
    public boolean validateTotalFilesSize(MultipartFile[] files, long maxTotalSizeMB) {
        if (files == null || files.length == 0) {
            return false;
        }

        long totalSizeBytes = 0;
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                totalSizeBytes += file.getSize();
            }
        }

        long totalSizeInMB = totalSizeBytes / (1024 * 1024);
        return totalSizeInMB <= maxTotalSizeMB;
    }

    /**
     * Validate multiple files size individually
     *
     * @param filesArray Array of files
     * @param maxSizeMB  Maximum size per file in MB
     * @return true if all files are valid, false otherwise
     */
    public boolean validateMultipleFilesSize(MultipartFile[] files, long maxSizeMB) {
        if (files == null || files.length == 0) {
            return false;
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty() && !validateFileSize(file, maxSizeMB)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get file extension
     *
     * @param filename Filename
     * @return File extension (without dot)
     */
    public String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }

        return "";
    }

    /**
     * Validate file extension
     *
     * @param filename          Filename
     * @param allowedExtensions Array of allowed extensions
     * @return true if extension is allowed, false otherwise
     */
    public boolean validateFileExtension(String filename, String[] allowedExtensions) {
        String extension = getFileExtension(filename);

        for (String allowed : allowedExtensions) {
            if (extension.equalsIgnoreCase(allowed)) {
                return true;
            }
        }

        return false;
    }
}
