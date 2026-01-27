package com.climasys.documents.service;

import com.climasys.entity.PatientDocumentTreatment;
import com.climasys.repository.PatientDocumentTreatmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing Patient Document Treatment records
 * Equivalent to USP_INSERT_PatientDocuments_Treatment stored procedure
 */
@Service
public class PatientDocumentTreatmentService {

    private static final Logger logger = LoggerFactory.getLogger(PatientDocumentTreatmentService.class);

    @Autowired
    private PatientDocumentTreatmentRepository repository;

    @Autowired
    private FileStorageService fileStorageService;

    @Value("${climasys.file-upload.max-file-size:4}")
    private long maxFileSizeMB;

    @Value("${climasys.file-upload.delete.require-physical-deletion:true}")
    private boolean requirePhysicalDeletion;

    /**
     * Insert a new patient document treatment record
     * Equivalent to USP_INSERT_PatientDocuments_Treatment
     *
     * @param patientId      Patient ID
     * @param doctorId       Doctor ID
     * @param clinicId       Clinic ID
     * @param documentName   Document name/path
     * @param createdByName  User who created the document
     * @param patientVisitNo Patient visit number
     * @param visitDate      Visit date
     * @return Map containing success status and document details
     */
    @Transactional
    public Map<String, Object> insertPatientDocumentTreatment(
            String patientId,
            String doctorId,
            String clinicId,
            String documentName,
            String createdByName,
            Integer patientVisitNo,
            LocalDateTime visitDate) {

        logger.info("Inserting patient document treatment - PatientId: {}, DocumentName: {}, VisitNo: {}",
                patientId, documentName, patientVisitNo);

        Map<String, Object> response = new HashMap<>();

        try {
            // Create new document record
            PatientDocumentTreatment document = new PatientDocumentTreatment();
            document.setPatientId(patientId);
            document.setDoctorId(doctorId);
            document.setClinicId(clinicId);
            document.setDocumentName(documentName);
            document.setCreatedbyName(createdByName);
            document.setPatientVisitNo(patientVisitNo);
            document.setVisitDate(visitDate);
            document.setCreatedOn(LocalDateTime.now());
            document.setDeleteFlag(false);

            // Save to database
            PatientDocumentTreatment savedDocument = repository.save(document);

            logger.info("Successfully inserted patient document treatment with ID: {}", savedDocument.getId());

            // Build response
            response.put("success", true);
            response.put("message", "Patient document treatment inserted successfully");
            response.put("documentId", savedDocument.getId());
            response.put("patientId", savedDocument.getPatientId());
            response.put("doctorId", savedDocument.getDoctorId());
            response.put("clinicId", savedDocument.getClinicId());
            response.put("documentName", savedDocument.getDocumentName());
            response.put("patientVisitNo", savedDocument.getPatientVisitNo());
            response.put("visitDate", savedDocument.getVisitDate());
            response.put("createdOn", savedDocument.getCreatedOn());
            response.put("createdBy", savedDocument.getCreatedbyName());

        } catch (Exception e) {
            logger.error("Error inserting patient document treatment: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to insert patient document treatment: " + e.getMessage());
        }

        return response;
    }

    /**
     * Get all documents for a specific patient visit
     */
    public Map<String, Object> getDocumentsByPatientVisit(String patientId, Integer visitNo) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<PatientDocumentTreatment> documents = repository.findByPatientIdAndVisitNo(patientId, visitNo);

            response.put("success", true);
            response.put("documents", documents);
            response.put("count", documents.size());
            response.put("patientId", patientId);
            response.put("visitNo", visitNo);

        } catch (Exception e) {
            logger.error("Error retrieving patient documents: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to retrieve documents: " + e.getMessage());
        }

        return response;
    }

    /**
     * Get all documents for a patient
     */
    public Map<String, Object> getDocumentsByPatient(String patientId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<PatientDocumentTreatment> documents = repository.findByPatientId(patientId);

            response.put("success", true);
            response.put("documents", documents);
            response.put("count", documents.size());
            response.put("patientId", patientId);

        } catch (Exception e) {
            logger.error("Error retrieving patient documents: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to retrieve documents: " + e.getMessage());
        }

        return response;
    }

    /**
     * Soft delete a document (set deleteFlag to true)
     * Equivalent to USP_Delete_PatientDocument_treatment
     */
    @Transactional
    public Map<String, Object> deleteDocument(Integer documentId, String userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<PatientDocumentTreatment> documentOpt = repository.findById(documentId);

            if (documentOpt.isPresent()) {
                PatientDocumentTreatment document = documentOpt.get();
                document.setDeleteFlag(true);
                document.setModifiedOn(LocalDateTime.now());
                document.setModifiedName(userId);

                repository.save(document);

                logger.info("Successfully deleted document ID: {} by user: {}", documentId, userId);

                response.put("success", true);
                response.put("message", "Document deleted successfully");
                response.put("documentId", documentId);
            } else {
                response.put("success", false);
                response.put("error", "Document not found with ID: " + documentId);
            }

        } catch (Exception e) {
            logger.error("Error deleting document: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to delete document: " + e.getMessage());
        }

        return response;
    }

    /**
     * Delete document with physical file deletion as part of a transaction
     * This method ensures both file system and database operations succeed or fail
     * together
     */
    @Transactional
    public Map<String, Object> deleteDocumentWithPhysicalFile(Integer documentId, String userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<PatientDocumentTreatment> documentOpt = repository.findById(documentId);

            if (documentOpt.isPresent()) {
                PatientDocumentTreatment document = documentOpt.get();
                String filePath = document.getDocumentName(); // document_name stores the file path

                // Step 1: Delete physical file first - MUST succeed before database update
                boolean fileDeleted = false;
                String fileStatus = "unknown";

                if (filePath != null && !filePath.trim().isEmpty()) {
                    logger.info("Attempting to delete file: {} for document ID: {}", filePath, documentId);

                    // Try different path formats to handle various storage scenarios
                    fileDeleted = fileStorageService.deleteFile(filePath);

                    if (!fileDeleted) {
                        logger.warn("Failed to delete physical file: {} for document ID: {}", filePath, documentId);

                        // Try alternative path resolution
                        String alternativePath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
                        if (!alternativePath.equals(filePath)) {
                            logger.info("Trying alternative path: {}", alternativePath);
                            fileDeleted = fileStorageService.deleteFile(alternativePath);
                        }

                        if (!fileDeleted) {
                            // Check if file exists at all
                            boolean fileExists = fileStorageService.fileExists(filePath) ||
                                    fileStorageService.fileExists(alternativePath);

                            if (!fileExists) {
                                fileStatus = "not_found";
                                logger.info("Physical file not found for document ID: {} - treating as already deleted",
                                        documentId);
                                fileDeleted = true; // Consider it successful if file doesn't exist
                            } else {
                                fileStatus = "deletion_failed";
                                logger.error("All attempts to delete physical file failed for document ID: {}",
                                        documentId);

                                if (requirePhysicalDeletion) {
                                    // CRITICAL: Do not proceed with database update if file deletion failed
                                    response.put("success", false);
                                    response.put("error",
                                            "Failed to delete physical file. Database record not updated.");
                                    response.put("documentId", documentId);
                                    response.put("fileDeleted", false);
                                    response.put("fileStatus", fileStatus);
                                    response.put("filePath", filePath);
                                    return response; // Exit early - no database update
                                } else {
                                    // Legacy behavior: continue with soft delete even if file deletion fails
                                    logger.warn(
                                            "Continuing with database update despite failed file deletion (requirePhysicalDeletion=false)");
                                }
                            }
                        } else {
                            fileStatus = "deleted_alternative_path";
                            logger.info("Successfully deleted physical file using alternative path: {}",
                                    alternativePath);
                        }
                    } else {
                        fileStatus = "deleted";
                        logger.info("Successfully deleted physical file: {} for document ID: {}", filePath, documentId);
                    }
                } else {
                    fileStatus = "no_path";
                    logger.info("No file path found for document ID: {} - proceeding with database update only",
                            documentId);
                    fileDeleted = true; // Consider it successful if no file path
                }

                // Step 2: Soft delete in database ONLY if file deletion was successful (or if
                // requirePhysicalDeletion is false)
                if (fileDeleted || (!requirePhysicalDeletion && "deletion_failed".equals(fileStatus))) {
                    document.setDeleteFlag(true);
                    document.setModifiedOn(LocalDateTime.now());
                    document.setModifiedName(userId);

                    repository.save(document);
                    logger.info("Database record updated successfully for document ID: {}", documentId);
                } else {
                    logger.error("Skipping database update due to failed file deletion for document ID: {}",
                            documentId);
                    response.put("success", false);
                    response.put("error", "File deletion failed. Database record not updated.");
                    response.put("documentId", documentId);
                    response.put("fileDeleted", false);
                    response.put("fileStatus", fileStatus);
                    response.put("filePath", filePath);
                    return response; // Exit early - no database update
                }

                logger.info("Successfully deleted document ID: {} by user: {} (file status: {})",
                        documentId, userId, fileStatus);

                response.put("success", true);
                response.put("message", "Document deleted successfully");
                response.put("documentId", documentId);
                response.put("fileDeleted", fileDeleted);
                response.put("fileStatus", fileStatus);
                response.put("filePath", filePath);

                // Add specific message based on file status
                if ("not_found".equals(fileStatus)) {
                    response.put("message", "Document deleted successfully (file was already missing)");
                } else if ("deleted".equals(fileStatus)) {
                    response.put("message", "Document and file deleted successfully");
                } else if ("deletion_failed".equals(fileStatus)) {
                    response.put("message", "Document deleted successfully (file deletion failed)");
                } else if ("no_path".equals(fileStatus)) {
                    response.put("message", "Document deleted successfully (no file path)");
                }

            } else {
                response.put("success", false);
                response.put("error", "Document not found with ID: " + documentId);
            }

        } catch (Exception e) {
            logger.error("Error deleting document with physical file: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to delete document: " + e.getMessage());

            // Transaction will be rolled back automatically due to @Transactional
            // This ensures database consistency even if file operations fail
        }

        return response;
    }

    /**
     * Update document name
     */
    @Transactional
    public Map<String, Object> updateDocument(Integer documentId, String documentName, String userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<PatientDocumentTreatment> documentOpt = repository.findById(documentId);

            if (documentOpt.isPresent()) {
                PatientDocumentTreatment document = documentOpt.get();
                document.setDocumentName(documentName);
                document.setModifiedOn(LocalDateTime.now());
                document.setModifiedName(userId);

                PatientDocumentTreatment updatedDocument = repository.save(document);

                logger.info("Successfully updated document ID: {} by user: {}", documentId, userId);

                response.put("success", true);
                response.put("message", "Document updated successfully");
                response.put("document", updatedDocument);
            } else {
                response.put("success", false);
                response.put("error", "Document not found with ID: " + documentId);
            }

        } catch (Exception e) {
            logger.error("Error updating document: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to update document: " + e.getMessage());
        }

        return response;
    }

    /**
     * Upload and save a single file for patient treatment
     * Equivalent to .NET: UploadFileSubmit() method
     * 
     * @param file           MultipartFile to upload
     * @param patientId      Patient ID
     * @param doctorId       Doctor ID
     * @param clinicId       Clinic ID
     * @param createdByName  User who uploaded
     * @param patientVisitNo Visit number
     * @param visitDate      Visit date
     * @return Map containing success status and document details
     */
    @Transactional
    public Map<String, Object> uploadAndSaveDocument(
            MultipartFile file,
            String patientId,
            String doctorId,
            String clinicId,
            String createdByName,
            Integer patientVisitNo,
            LocalDateTime visitDate) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validate file is not empty
            if (file == null || file.isEmpty()) {
                response.put("success", false);
                response.put("error", "File is empty or not provided");
                return response;
            }

            // Validate file size - Equivalent to: long filesize =
            // ((FU_AttachDocument.PostedFile.ContentLength) / 1024) / 1024;
            if (!fileStorageService.validateFileSize(file, maxFileSizeMB)) {
                response.put("success", false);
                response.put("error", "File size exceeds maximum allowed size of " + maxFileSizeMB + " MB");
                return response;
            }

            // Validate file extension
            String[] allowedExtensions = { "jpg", "jpeg", "png", "gif", "pdf", "xls", "xlsx", "doc", "docx" };
            if (!fileStorageService.validateFileExtension(file.getOriginalFilename(), allowedExtensions)) {
                response.put("success", false);
                response.put("error", "Invalid file format. Allowed: Image, PDF, Excel, DOC.");
                return response;
            }

            // Save file to file system - Equivalent to:
            // hpf.SaveAs(Server.MapPath(Savepath));
            String savedFilePath = fileStorageService.saveFile(file, patientId, "patient-documents");

            logger.info("File saved to: {} for patient: {}", savedFilePath, patientId);

            // Save document record to database
            Map<String, Object> dbResult = insertPatientDocumentTreatment(
                    patientId,
                    doctorId,
                    clinicId,
                    savedFilePath,
                    createdByName,
                    patientVisitNo,
                    visitDate);

            return dbResult;

        } catch (IOException e) {
            logger.error("Error saving file: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to save file: " + e.getMessage());
            return response;
        } catch (Exception e) {
            logger.error("Error uploading document: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to upload document: " + e.getMessage());
            return response;
        }
    }

    /**
     * Upload and save multiple files for patient treatment
     * Equivalent to .NET: UploadFileSubmit() with HttpFileCollection
     * 
     * @param files          Array of MultipartFiles
     * @param patientId      Patient ID
     * @param doctorId       Doctor ID
     * @param clinicId       Clinic ID
     * @param createdByName  User who uploaded
     * @param patientVisitNo Visit number
     * @param visitDate      Visit date
     * @return Map containing success status and list of uploaded documents
     */
    @Transactional
    public Map<String, Object> uploadAndSaveMultipleDocuments(
            MultipartFile[] files,
            String patientId,
            String doctorId,
            String clinicId,
            String createdByName,
            Integer patientVisitNo,
            LocalDateTime visitDate) {

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> uploadedDocuments = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try {
            // Validate files array
            if (files == null || files.length == 0) {
                response.put("success", false);
                response.put("error", "No files provided");
                return response;
            }

            // Validate file count and sizes
            if (!fileStorageService.validateMultipleFilesSize(files, maxFileSizeMB)) {
                response.put("success", false);
                response.put("error", "One or more files exceed maximum allowed size of " + maxFileSizeMB + " MB");
                return response;
            }

            // Validate file extensions for all files
            String[] allowedExtensions = { "jpg", "jpeg", "png", "gif", "pdf", "xls", "xlsx", "doc", "docx" };
            for (MultipartFile file : files) {
                if (!file.isEmpty()
                        && !fileStorageService.validateFileExtension(file.getOriginalFilename(), allowedExtensions)) {
                    response.put("success", false);
                    response.put("error", "Invalid format in one or more files. Allowed: Image, PDF, Excel, DOC.");
                    return response;
                }
            }

            // Save files to file system - Equivalent to: for (int i = 0; i < hfc.Count - 1;
            // i++)
            List<String> savedFilePaths = fileStorageService.saveMultipleFiles(files, patientId, "patient-documents");

            logger.info("Saved {} files for patient: {}", savedFilePaths.size(), patientId);

            // Save each document record to database
            for (String filePath : savedFilePaths) {
                try {
                    Map<String, Object> dbResult = insertPatientDocumentTreatment(
                            patientId,
                            doctorId,
                            clinicId,
                            filePath,
                            createdByName,
                            patientVisitNo,
                            visitDate);

                    if ((Boolean) dbResult.get("success")) {
                        uploadedDocuments.add(dbResult);
                    } else {
                        errors.add("Failed to save record for file: " + filePath);
                    }
                } catch (Exception e) {
                    logger.error("Error saving document record: {}", e.getMessage(), e);
                    errors.add("Error saving file " + filePath + ": " + e.getMessage());
                }
            }

            // Build response
            response.put("success", uploadedDocuments.size() > 0);
            response.put("message", "Uploaded " + uploadedDocuments.size() + " of " + files.length + " files");
            response.put("uploadedCount", uploadedDocuments.size());
            response.put("totalFiles", files.length);
            response.put("documents", uploadedDocuments);

            if (!errors.isEmpty()) {
                response.put("errors", errors);
            }

        } catch (IOException e) {
            logger.error("Error saving files: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to save files: " + e.getMessage());
            return response;
        } catch (Exception e) {
            logger.error("Error uploading documents: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to upload documents: " + e.getMessage());
            return response;
        }

        return response;
    }

    /**
     * Get file bytes for download
     * Equivalent to .NET: req.DownloadData(Server.MapPath(strURL))
     * 
     * @param documentId Document ID
     * @return Map with file bytes and metadata
     */
    public Map<String, Object> getDocumentFile(Integer documentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<PatientDocumentTreatment> documentOpt = repository.findById(documentId);

            if (documentOpt.isPresent()) {
                PatientDocumentTreatment document = documentOpt.get();
                String filePath = document.getDocumentName();

                // Get file bytes
                byte[] fileBytes = fileStorageService.getFileBytes(filePath);

                // Extract filename from path
                String filename = filePath.substring(filePath.lastIndexOf('/') + 1);

                response.put("success", true);
                response.put("fileBytes", fileBytes);
                response.put("filename", filename);
                response.put("documentId", documentId);
                response.put("contentType", determineContentType(filename));
            } else {
                response.put("success", false);
                response.put("error", "Document not found with ID: " + documentId);
            }

        } catch (IOException e) {
            logger.error("Error reading file for document ID {}: {}", documentId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to read file: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error retrieving document file: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to retrieve document: " + e.getMessage());
        }

        return response;
    }

    /**
     * Determine content type based on file extension
     */
    private String determineContentType(String filename) {
        String extension = fileStorageService.getFileExtension(filename);

        return switch (extension.toLowerCase()) {
            case "pdf" -> "application/pdf";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "txt" -> "text/plain";
            default -> "application/octet-stream";
        };
    }
}
