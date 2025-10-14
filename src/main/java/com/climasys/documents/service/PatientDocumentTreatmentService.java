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

    /**
     * Insert a new patient document treatment record
     * Equivalent to USP_INSERT_PatientDocuments_Treatment
     *
     * @param patientId Patient ID
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param documentName Document name/path
     * @param createdByName User who created the document
     * @param patientVisitNo Patient visit number
     * @param visitDate Visit date
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
     * @param file MultipartFile to upload
     * @param patientId Patient ID
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param createdByName User who uploaded
     * @param patientVisitNo Visit number
     * @param visitDate Visit date
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

            // Validate file size - Equivalent to: long filesize = ((FU_AttachDocument.PostedFile.ContentLength) / 1024) / 1024;
            if (!fileStorageService.validateFileSize(file, maxFileSizeMB)) {
                response.put("success", false);
                response.put("error", "File size exceeds maximum allowed size of " + maxFileSizeMB + " MB");
                return response;
            }

            // Save file to file system - Equivalent to: hpf.SaveAs(Server.MapPath(Savepath));
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
                visitDate
            );

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
     * @param files Array of MultipartFiles
     * @param patientId Patient ID
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param createdByName User who uploaded
     * @param patientVisitNo Visit number
     * @param visitDate Visit date
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

            // Save files to file system - Equivalent to: for (int i = 0; i < hfc.Count - 1; i++)
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
                        visitDate
                    );

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

