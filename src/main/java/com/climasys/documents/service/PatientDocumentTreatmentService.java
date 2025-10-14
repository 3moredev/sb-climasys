package com.climasys.documents.service;

import com.climasys.entity.PatientDocumentTreatment;
import com.climasys.repository.PatientDocumentTreatmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
}

