package com.climasys.documents.web;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            String visitDate  // Optional, will use current date/time if not provided
    ) {}

    /**
     * Request DTO for updating document
     */
    public record UpdateDocumentRequest(
            @NotBlank(message = "Document name is required") String documentName,
            @NotBlank(message = "User ID is required") String userId
    ) {}

    /**
     * Insert a new patient document treatment record
     * Equivalent to: USP_INSERT_PatientDocuments_Treatment
     */
    @Operation(
        summary = "Insert Patient Document Treatment",
        description = "Creates a new patient document treatment record. Equivalent to USP_INSERT_PatientDocuments_Treatment stored procedure."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document inserted successfully",
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid input data",
            content = @Content(schema = @Schema(implementation = Map.class)))
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
                visitDate
            );

            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Failed to insert document: " + e.getMessage()
            ));
        }
    }

    /**
     * Get all documents for a specific patient visit
     * Equivalent to: USP_Get_PatientDocumentListsforTreatment
     */
    @Operation(
        summary = "Get Documents by Patient Visit",
        description = "Retrieves all treatment documents for a specific patient visit"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Documents retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/patient/{patientId}/visit/{visitNo}")
    public ResponseEntity<?> getDocumentsByPatientVisit(
            @Parameter(description = "Patient ID", required = true, example = "P-00001")
            @PathVariable String patientId,
            @Parameter(description = "Patient visit number", required = true, example = "1")
            @PathVariable Integer visitNo) {
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
                "error", "Failed to retrieve documents: " + e.getMessage()
            ));
        }
    }

    /**
     * Get all documents for a patient
     */
    @Operation(
        summary = "Get All Documents by Patient",
        description = "Retrieves all treatment documents for a specific patient"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Documents retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getDocumentsByPatient(
            @Parameter(description = "Patient ID", required = true, example = "P-00001")
            @PathVariable String patientId) {
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
                "error", "Failed to retrieve documents: " + e.getMessage()
            ));
        }
    }

    /**
     * Delete a document (soft delete)
     * Equivalent to: USP_Delete_PatientDocument_treatment
     */
    @Operation(
        summary = "Delete Patient Document Treatment",
        description = "Soft deletes a document by setting deleteFlag to true. Equivalent to USP_Delete_PatientDocument_treatment stored procedure."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - Document not found")
    })
    @DeleteMapping("/{documentId}")
    public ResponseEntity<?> deleteDocument(
            @Parameter(description = "Document ID", required = true, example = "1")
            @PathVariable Integer documentId,
            @Parameter(description = "User ID performing the deletion", required = false, example = "admin")
            @RequestParam(defaultValue = "system") String userId) {
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
                "error", "Failed to delete document: " + e.getMessage()
            ));
        }
    }

    /**
     * Update document name
     */
    @Operation(
        summary = "Update Document Name",
        description = "Updates the document name for a treatment document"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document updated successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - Document not found")
    })
    @PutMapping("/{documentId}")
    public ResponseEntity<?> updateDocument(
            @Parameter(description = "Document ID", required = true, example = "1")
            @PathVariable Integer documentId,
            @Valid @RequestBody UpdateDocumentRequest request) {
        try {
            Map<String, Object> result = service.updateDocument(
                documentId, 
                request.documentName(), 
                request.userId()
            );
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Failed to update document: " + e.getMessage()
            ));
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

