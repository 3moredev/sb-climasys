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
import java.nio.file.StandardCopyOption;
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
     * @param file MultipartFile to save
     * @param patientId Patient ID for directory structure
     * @param uploadType Type of upload (patient, mr-profile, treatment, etc.)
     * @return Relative path to saved file
     */
    public String saveFile(MultipartFile file, String patientId, String uploadType) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        // Get the base path based on upload type
        String basePath = getBasePathForType(uploadType);
        
        // Create directory structure: basePath/patientId/
        Path directoryPath = Paths.get(basePath, patientId);
        
        // Equivalent to: if (!Directory.Exists(folder)) { Directory.CreateDirectory(folder); }
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
            logger.info("Created directory: {}", directoryPath.toAbsolutePath());
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
        
        // Save the file - Equivalent to: hpf.SaveAs(Server.MapPath(Savepath));
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        logger.info("File saved successfully: {}", filePath.toAbsolutePath());

        // Return relative path for database storage
        // Format: /PatientUploads/patientId/filename
        String relativePath = "/" + uploadType + "/" + patientId + "/" + cleanFilename;
        return relativePath;
    }

    /**
     * Save multiple files for a patient
     * Equivalent to .NET: Handling HttpFileCollection in a loop
     *
     * @param files Array of MultipartFiles
     * @param patientId Patient ID
     * @param uploadType Type of upload
     * @return List of relative paths to saved files
     */
    public List<String> saveMultipleFiles(MultipartFile[] files, String patientId, String uploadType) throws IOException {
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
                String filePath = saveFile(file, patientId, uploadType);
                savedFilePaths.add(filePath);
            }
        }

        logger.info("Saved {} files for patient: {}", savedFilePaths.size(), patientId);
        return savedFilePaths;
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
                
                // Try multiple path resolution strategies
                Path[] possiblePaths = {
                    // Strategy 1: Direct path
                    Paths.get(relativePath),
                    // Strategy 2: Relative to current working directory
                    Paths.get(System.getProperty("user.dir"), relativePath),
                    // Strategy 3: Relative to parent directory (for sb-climasys subdirectory structure)
                    Paths.get(System.getProperty("user.dir"), "..", relativePath),
                    // Strategy 4: Relative to upload folder path if configured
                    uploadFolderPath != null ? Paths.get(uploadFolderPath, relativePath) : null,
                    // Strategy 5: Remove leading slash if present
                    relativePath.startsWith("/") ? Paths.get(relativePath.substring(1)) : null,
                    // Strategy 6: Try with uploads prefix
                    Paths.get("uploads", relativePath),
                    // Strategy 7: Try with uploads prefix and remove leading slash
                    relativePath.startsWith("/") ? Paths.get("uploads", relativePath.substring(1)) : null,
                    // Strategy 8: Map patient-documents to PatientUploads (based on actual file structure)
                    relativePath.replace("/patient-documents/", "PatientUploads/").startsWith("/") ? 
                        Paths.get(relativePath.replace("/patient-documents/", "PatientUploads/").substring(1)) : 
                        Paths.get(relativePath.replace("/patient-documents/", "PatientUploads/")),
                    // Strategy 9: Map patient-documents to PatientUploads with leading slash removed
                    relativePath.startsWith("/patient-documents/") ? 
                        Paths.get(relativePath.replace("/patient-documents/", "PatientUploads/").substring(1)) : null,
                    // Strategy 10: Parent directory + PatientUploads mapping
                    relativePath.startsWith("/patient-documents/") ? 
                        Paths.get(System.getProperty("user.dir"), "..", relativePath.replace("/patient-documents/", "PatientUploads/").substring(1)) : null
                };
                
                for (Path filePath : possiblePaths) {
                    if (filePath == null) continue;
                    
                    if (detailedLogging) {
                        logger.debug("Checking file existence at: {}", filePath.toAbsolutePath());
                    }
                    
                    if (Files.exists(filePath)) {
                        Files.delete(filePath);
                        logger.info("File deleted successfully: {}", filePath.toAbsolutePath());
                        return true;
                    }
                }
                
                if (attempt == retryAttempts) {
                    logger.warn("File not found for deletion at any of the attempted paths after {} attempts. Original path: {}", retryAttempts, relativePath);
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
            Path filePath = Paths.get(relativePath);
            return Files.exists(filePath);
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
        Path filePath = Paths.get(relativePath);
        
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + relativePath);
        }

        return Files.readAllBytes(filePath);
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
     * @param file File to validate
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
     * Validate multiple files size
     *
     * @param files Array of files
     * @param maxSizeMB Maximum size per file in MB
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
     * @param filename Filename
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

