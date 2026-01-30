package com.climasys.documents.web;

import com.climasys.auth.annotation.RefreshSession;

import com.climasys.documents.service.PatientDocumentTreatmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * REST Controller for Patient Document Treatment operations
 * Provides JPA-based endpoints equivalent to stored procedures:
 * - USP_INSERT_PatientDocuments_Treatment
 * - USP_Delete_PatientDocument_treatment
 * - USP_Get_PatientDocumentListsforTreatment
 */
@RestController
@RequestMapping("/api/patient-documents/treatment")
@Tag(name = "Patient Document Treatment", description = "Manage patient treatment-related documents")
@RefreshSession
public class PatientDocumentTreatmentController {

    @Autowired
    private PatientDocumentTreatmentService service;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Request DTO for inserting patient document treatment
     */
    public record InsertDocumentRequest(
            @NotBlank(message = "Patient ID is required") String patientId,
            @NotBlank(message = "Doctor ID is required") String doctorId,
            @NotBlank(message = "Clinic ID is required") String clinicId,
            @NotBlank(message = "Document name is required") String documentName,
            @NotBlank(message = "Created by name is required") String createdByName,
            @NotNull(message = "Patient visit number is required") Integer patientVisitNo,
            String visitDate // Optional, will use current date/time if not provided
    ) {
    }

    /**
     * Request DTO for updating document
     */
    public record UpdateDocumentRequest(
            @NotBlank(message = "Document name is required") String documentName,
            @NotBlank(message = "User ID is required") String userId) {
    }

    /**
     * Insert a new patient document treatment record
     * Equivalent to: USP_INSERT_PatientDocuments_Treatment
     */
    @Operation(summary = "Insert Patient Document Treatment", description = "Creates a new patient document treatment record. Equivalent to USP_INSERT_PatientDocuments_Treatment stored procedure.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document inserted successfully", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input data", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping
    public ResponseEntity<?> insertDocument(@Valid @RequestBody InsertDocumentRequest request) {
        try {
            // Parse visit date or use current date/time
            LocalDateTime visitDate;
            if (request.visitDate() != null && !request.visitDate().trim().isEmpty()) {
                visitDate = parseDateTime(request.visitDate());
            } else {
                visitDate = LocalDateTime.now();
            }

            Map<String, Object> result = service.insertPatientDocumentTreatment(
                    request.patientId(),
                    request.doctorId(),
                    request.clinicId(),
                    request.documentName(),
                    request.createdByName(),
                    request.patientVisitNo(),
                    visitDate,
                    null);

            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Failed to insert document: " + e.getMessage()));
        }
    }

    /**
     * Get all documents for a specific patient visit
     * Equivalent to: USP_Get_PatientDocumentListsforTreatment
     */
    @Operation(summary = "Get Documents by Patient Visit", description = "Retrieves all treatment documents for a specific patient visit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/patient/{patientId}/visit/{visitNo}")
    public ResponseEntity<?> getDocumentsByPatientVisit(
            @Parameter(description = "Patient ID", required = true, example = "P-00001") @PathVariable String patientId,
            @Parameter(description = "Patient visit number", required = true, example = "1") @PathVariable Integer visitNo) {
        try {
            Map<String, Object> result = service.getDocumentsByPatientVisit(patientId, visitNo);

            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Failed to retrieve documents: " + e.getMessage()));
        }
    }

    /**
     * Get all documents for a patient
     */
    @Operation(summary = "Get All Documents by Patient", description = "Retrieves all treatment documents for a specific patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getDocumentsByPatient(
            @Parameter(description = "Patient ID", required = true, example = "P-00001") @PathVariable String patientId) {
        try {
            Map<String, Object> result = service.getDocumentsByPatient(patientId);

            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Failed to retrieve documents: " + e.getMessage()));
        }
    }

    /**
     * Delete a document (soft delete)
     * Equivalent to: USP_Delete_PatientDocument_treatment
     */
    @Operation(summary = "Delete Patient Document Treatment", description = "Soft deletes a document by setting deleteFlag to true. Equivalent to USP_Delete_PatientDocument_treatment stored procedure.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Document not found")
    })
    @DeleteMapping("/{documentId}")
    public ResponseEntity<?> deleteDocument(
            @Parameter(description = "Document ID", required = true, example = "1") @PathVariable Integer documentId,
            @Parameter(description = "User ID performing the deletion", required = false, example = "admin") @RequestParam(defaultValue = "system") String userId) {
        try {
            Map<String, Object> result = service.deleteDocument(documentId, userId);

            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Failed to delete document: " + e.getMessage()));
        }
    }

    /**
     * Delete a document with physical file deletion (transactional)
     * This endpoint ensures both file system and database operations succeed or
     * fail together
     */
    @Operation(summary = "Delete Patient Document with Physical File", description = "Deletes both the physical file from file system and soft deletes the database record as part of a transaction. Ensures data consistency.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document and file deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Document not found or deletion failed")
    })
    @DeleteMapping("/{documentId}/with-file")
    public ResponseEntity<?> deleteDocumentWithPhysicalFile(
            @Parameter(description = "Document ID", required = true, example = "1") @PathVariable Integer documentId,
            @Parameter(description = "User ID performing the deletion", required = false, example = "admin") @RequestParam(defaultValue = "system") String userId) {
        try {
            Map<String, Object> result = service.deleteDocumentWithPhysicalFile(documentId, userId);

            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Failed to delete document with file: " + e.getMessage()));
        }
    }

    /**
     * Update document name
     */
    @Operation(summary = "Update Document Name", description = "Updates the document name for a treatment document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Document not found")
    })
    @PutMapping("/{documentId}")
    public ResponseEntity<?> updateDocument(
            @Parameter(description = "Document ID", required = true, example = "1") @PathVariable Integer documentId,
            @Valid @RequestBody UpdateDocumentRequest request) {
        try {
            Map<String, Object> result = service.updateDocument(
                    documentId,
                    request.documentName(),
                    request.userId());

            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Failed to update document: " + e.getMessage()));
        }
    }

    /**
     * Upload a single file for patient treatment
     * Equivalent to .NET: UploadFileSubmit() method
     */
    @Operation(summary = "Upload Patient Document", description = "Uploads a single file for patient treatment and saves it to the file system. Equivalent to .NET UploadFileSubmit() method.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid file or parameters")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocument(
            @Parameter(description = "File to upload", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "Patient ID", required = true) @RequestParam("patientId") String patientId,
            @Parameter(description = "Doctor ID", required = true) @RequestParam("doctorId") String doctorId,
            @Parameter(description = "Clinic ID", required = true) @RequestParam("clinicId") String clinicId,
            @Parameter(description = "User who uploaded the file", required = true) @RequestParam("createdByName") String createdByName,
            @Parameter(description = "Patient visit number", required = true) @RequestParam("patientVisitNo") Integer patientVisitNo,
            @Parameter(description = "Visit date (optional, defaults to current date/time)") @RequestParam(value = "visitDate", required = false) String visitDate) {
        try {
            // Parse visit date or use current date/time
            LocalDateTime visitDateTime;
            if (visitDate != null && !visitDate.trim().isEmpty()) {
                visitDateTime = parseDateTime(visitDate);
            } else {
                visitDateTime = LocalDateTime.now();
            }

            Map<String, Object> result = service.uploadAndSaveDocument(
                    file,
                    patientId,
                    doctorId,
                    clinicId,
                    createdByName,
                    patientVisitNo,
                    visitDateTime);

            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Failed to upload document: " + e.getMessage()));
        }
    }

    /**
     * Upload multiple files for patient treatment
     * Equivalent to .NET: UploadFileSubmit() with HttpFileCollection
     */
    @Operation(summary = "Upload Multiple Patient Documents", description = "Uploads multiple files for patient treatment. Equivalent to .NET file collection upload.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid files or parameters")
    })
    @PostMapping(value = "/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMultipleDocuments(
            @Parameter(description = "Files to upload (max 5)", required = true) @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "Patient ID", required = true) @RequestParam("patientId") String patientId,
            @Parameter(description = "Doctor ID", required = true) @RequestParam("doctorId") String doctorId,
            @Parameter(description = "Clinic ID", required = true) @RequestParam("clinicId") String clinicId,
            @Parameter(description = "User who uploaded the files", required = true) @RequestParam("createdByName") String createdByName,
            @Parameter(description = "Patient visit number", required = true) @RequestParam("patientVisitNo") Integer patientVisitNo,
            @Parameter(description = "Visit date (optional, defaults to current date/time)") @RequestParam(value = "visitDate", required = false) String visitDate) {
        try {
            // Parse visit date or use current date/time
            LocalDateTime visitDateTime;
            if (visitDate != null && !visitDate.trim().isEmpty()) {
                visitDateTime = parseDateTime(visitDate);
            } else {
                visitDateTime = LocalDateTime.now();
            }

            Map<String, Object> result = service.uploadAndSaveMultipleDocuments(
                    files,
                    patientId,
                    doctorId,
                    clinicId,
                    createdByName,
                    patientVisitNo,
                    visitDateTime);

            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Failed to upload documents: " + e.getMessage()));
        }
    }

    /**
     * Download a document file
     * Equivalent to .NET: lnkDocumentName_Click with req.DownloadData
     */
    @Operation(summary = "Download Patient Document", description = "Downloads a patient document file. Equivalent to .NET document download with WebClient.DownloadData.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(
            @Parameter(description = "Document ID", required = true, example = "1") @PathVariable Integer documentId) {
        try {
            Map<String, Object> result = service.getDocumentFile(documentId);

            if ((Boolean) result.get("success")) {
                byte[] fileBytes = (byte[]) result.get("fileBytes");
                String filename = (String) result.get("filename");
                String contentType = (String) result.get("contentType");

                ByteArrayResource resource = new ByteArrayResource(fileBytes);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Stream a document file
     * Use for large files to avoid loading entire file into memory
     */
    @Operation(summary = "Stream Patient Document", description = "Streams a patient document file. Use for large files to avoid memory issues.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document streamed successfully"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @GetMapping("/stream/{documentId}")
    public ResponseEntity<Resource> streamDocument(
            @Parameter(description = "Document ID", required = true, example = "1") @PathVariable Integer documentId) {
        try {
            Map<String, Object> result = service.getDocumentStream(documentId);

            if ((Boolean) result.get("success")) {
                java.io.InputStream inputStream = (java.io.InputStream) result.get("fileStream");
                String filename = (String) result.get("filename");
                String contentType = (String) result.get("contentType");
                Long fileSize = (Long) result.get("fileSize");

                org.springframework.core.io.InputStreamResource resource = new org.springframework.core.io.InputStreamResource(
                        inputStream);

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(fileSize)
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Parse date/time string with multiple format support
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return LocalDateTime.now();
        }

        try {
            // Try full date-time format first
            if (dateTimeStr.contains(":")) {
                return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
            } else {
                // Date only, set time to start of day
                return LocalDateTime.parse(dateTimeStr + " 00:00:00", DATE_TIME_FORMATTER);
            }
        } catch (Exception e) {
            // If parsing fails, return current date/time
            return LocalDateTime.now();
        }
    }
}
