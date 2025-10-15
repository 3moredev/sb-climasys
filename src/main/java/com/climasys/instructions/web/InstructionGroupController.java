package com.climasys.instructions.web;

import com.climasys.instructions.dto.*;
import com.climasys.instructions.service.InstructionGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Instruction Group management
 * Provides endpoints for managing instruction groups and their associations with patient visits
 */
@RestController
@RequestMapping("/api/instruction-groups")
@Tag(name = "Instruction Groups", description = "APIs for managing instruction groups and visit instructions")
public class InstructionGroupController {
    
    private static final Logger logger = LoggerFactory.getLogger(InstructionGroupController.class);
    
    @Autowired
    private InstructionGroupService instructionGroupService;
    
    /**
     * Get all instruction groups for a specific doctor
     * GET /api/instruction-groups/doctor/{doctorId}
     */
    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get all instruction groups for a doctor", 
               description = "Retrieves all instruction groups for the specified doctor")
    public ResponseEntity<?> getAllInstructionGroups(
            @Parameter(description = "Doctor ID", example = "DR-00001")
            @PathVariable String doctorId) {
        try {
            logger.info("Getting all instruction groups for doctor: {}", doctorId);
            List<InstructionGroupDTO> groups = instructionGroupService.getAllInstructionGroupsForDoctor(doctorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Instruction groups retrieved successfully");
            response.put("data", groups);
            response.put("count", groups.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting instruction groups for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get instruction groups: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get instruction groups with their details for a doctor
     * GET /api/instruction-groups/doctor/{doctorId}/with-details
     */
    @GetMapping("/doctor/{doctorId}/with-details")
    @Operation(summary = "Get instruction groups with details", 
               description = "Retrieves all instruction groups with their instruction details for the specified doctor")
    public ResponseEntity<?> getInstructionGroupsWithDetails(
            @Parameter(description = "Doctor ID", example = "DR-00001")
            @PathVariable String doctorId) {
        try {
            logger.info("Getting instruction groups with details for doctor: {}", doctorId);
            List<InstructionGroupDTO> groups = instructionGroupService.getInstructionGroupsWithDetails(doctorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Instruction groups with details retrieved successfully");
            response.put("data", groups);
            response.put("count", groups.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting instruction groups with details for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get instruction groups with details: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get instruction groups in formatted way (similar to stored procedure)
     * GET /api/instruction-groups/doctor/{doctorId}/formatted
     */
    @GetMapping("/doctor/{doctorId}/formatted")
    @Operation(summary = "Get formatted instruction groups", 
               description = "Retrieves instruction groups in formatted way (similar to stored procedure USP_Get_FindingsData)")
    public ResponseEntity<?> getInstructionGroupsFormatted(
            @Parameter(description = "Doctor ID", example = "DR-00001")
            @PathVariable String doctorId) {
        try {
            logger.info("Getting formatted instruction groups for doctor: {}", doctorId);
            List<Map<String, Object>> groups = instructionGroupService.getInstructionGroupsFormatted(doctorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Formatted instruction groups retrieved successfully");
            response.put("data", groups);
            response.put("count", groups.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting formatted instruction groups for doctor {}: {}", doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get formatted instruction groups: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Search instruction groups by description
     * GET /api/instruction-groups/doctor/{doctorId}/search?q={searchTerm}
     */
    @GetMapping("/doctor/{doctorId}/search")
    @Operation(summary = "Search instruction groups", 
               description = "Search instruction groups by description for the specified doctor")
    public ResponseEntity<?> searchInstructionGroups(
            @Parameter(description = "Doctor ID", example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Search term", example = "surgery")
            @RequestParam(name = "q") String searchTerm) {
        try {
            logger.info("Searching instruction groups for doctor: {} with term: {}", doctorId, searchTerm);
            List<InstructionGroupDTO> groups = instructionGroupService.searchInstructionGroups(doctorId, searchTerm);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Instruction groups search completed");
            response.put("data", groups);
            response.put("count", groups.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error searching instruction groups for doctor {} with term {}: {}", 
                    doctorId, searchTerm, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to search instruction groups: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get instructions for a specific group
     * GET /api/instruction-groups/doctor/{doctorId}/group/{groupDescription}
     */
    @GetMapping("/doctor/{doctorId}/group/{groupDescription}")
    @Operation(summary = "Get instructions for a group", 
               description = "Retrieves all instructions for a specific instruction group")
    public ResponseEntity<?> getInstructionsForGroup(
            @Parameter(description = "Doctor ID", example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Group description", example = "Post-Surgery Care")
            @PathVariable String groupDescription) {
        try {
            logger.info("Getting instructions for group: {} for doctor: {}", groupDescription, doctorId);
            List<InstructionDetailDTO> instructions = instructionGroupService.getInstructionsForGroup(doctorId, groupDescription);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Instructions retrieved successfully");
            response.put("data", instructions);
            response.put("count", instructions.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting instructions for group {} for doctor {}: {}", 
                    groupDescription, doctorId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get instructions: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Create a new instruction group
     * POST /api/instruction-groups
     */
    @PostMapping
    @Operation(summary = "Create instruction group", 
               description = "Creates a new instruction group with instructions")
    public ResponseEntity<?> createInstructionGroup(
            @Parameter(description = "Create instruction group request")
            @Valid @RequestBody CreateInstructionGroupRequest request) {
        try {
            InstructionGroupDTO group = instructionGroupService.createInstructionGroup(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Instruction group created successfully");
            response.put("data", group);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error creating instruction group: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (Exception e) {
            logger.error("Error creating instruction group: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to create instruction group: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Add instruction groups to a patient visit
     * POST /api/instruction-groups/visits
     */
    @PostMapping("/visits")
    @Operation(summary = "Add instruction groups to visit", 
               description = "Adds instruction groups to a patient visit")
    public ResponseEntity<?> addInstructionGroupsToVisit(
            @Parameter(description = "Add instruction groups to visit request")
            @Valid @RequestBody AddInstructionGroupToVisitRequest request) {
        try {
            List<VisitInstructionGroupDTO> visitInstructions = 
                    instructionGroupService.addInstructionGroupsToVisit(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Instruction groups added to visit successfully");
            response.put("data", visitInstructions);
            response.put("count", visitInstructions.size());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error adding instruction groups to visit: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to add instruction groups to visit: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get instruction groups for a patient visit
     * GET /api/instruction-groups/visits/{doctorId}/{clinicId}/{shiftId}/{patientId}/{visitNo}
     */
    @GetMapping("/visits/{doctorId}/{clinicId}/{shiftId}/{patientId}/{visitNo}")
    @Operation(summary = "Get instruction groups for visit", 
               description = "Retrieves instruction groups for a specific patient visit")
    public ResponseEntity<?> getInstructionGroupsForVisit(
            @Parameter(description = "Doctor ID", example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Clinic ID", example = "CL-00001")
            @PathVariable String clinicId,
            @Parameter(description = "Shift ID", example = "1")
            @PathVariable Short shiftId,
            @Parameter(description = "Patient ID", example = "11-02-2019-020500")
            @PathVariable String patientId,
            @Parameter(description = "Visit number", example = "1")
            @PathVariable Integer visitNo,
            @Parameter(description = "Visit date", example = "2023-10-13T10:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime visitDate) {
        try {
            logger.info("Getting instruction groups for visit: patient={}, visitNo={}", patientId, visitNo);
            List<VisitInstructionGroupDTO> visitInstructions = 
                    instructionGroupService.getInstructionGroupsForVisit(
                            doctorId, clinicId, shiftId, patientId, visitNo, visitDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Visit instruction groups retrieved successfully");
            response.put("data", visitInstructions);
            response.put("count", visitInstructions.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting instruction groups for visit: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get visit instruction groups: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get distinct group descriptions for a patient visit
     * GET /api/instruction-groups/visits/{doctorId}/{clinicId}/{shiftId}/{patientId}/{visitNo}/groups
     */
    @GetMapping("/visits/{doctorId}/{clinicId}/{shiftId}/{patientId}/{visitNo}/groups")
    @Operation(summary = "Get distinct groups for visit", 
               description = "Retrieves distinct group descriptions for a patient visit")
    public ResponseEntity<?> getDistinctGroupsForVisit(
            @Parameter(description = "Doctor ID", example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Clinic ID", example = "CL-00001")
            @PathVariable String clinicId,
            @Parameter(description = "Shift ID", example = "1")
            @PathVariable Short shiftId,
            @Parameter(description = "Patient ID", example = "11-02-2019-020500")
            @PathVariable String patientId,
            @Parameter(description = "Visit number", example = "1")
            @PathVariable Integer visitNo,
            @Parameter(description = "Visit date", example = "2023-10-13T10:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime visitDate) {
        try {
            logger.info("Getting distinct groups for visit: patient={}, visitNo={}", patientId, visitNo);
            List<String> groups = instructionGroupService.getDistinctGroupsForVisit(
                    doctorId, clinicId, shiftId, patientId, visitNo, visitDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Distinct groups retrieved successfully");
            response.put("data", groups);
            response.put("count", groups.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting distinct groups for visit: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get distinct groups: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Delete instruction group from a visit
     * DELETE /api/instruction-groups/visits/{doctorId}/{clinicId}/{shiftId}/{patientId}/{visitNo}/group/{groupDescription}
     */
    @DeleteMapping("/visits/{doctorId}/{clinicId}/{shiftId}/{patientId}/{visitNo}/group/{groupDescription}")
    @Operation(summary = "Delete instruction group from visit", 
               description = "Deletes a specific instruction group from a patient visit")
    public ResponseEntity<?> deleteInstructionGroupFromVisit(
            @Parameter(description = "Doctor ID", example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Clinic ID", example = "CL-00001")
            @PathVariable String clinicId,
            @Parameter(description = "Shift ID", example = "1")
            @PathVariable Short shiftId,
            @Parameter(description = "Patient ID", example = "11-02-2019-020500")
            @PathVariable String patientId,
            @Parameter(description = "Visit number", example = "1")
            @PathVariable Integer visitNo,
            @Parameter(description = "Group description", example = "Post-Surgery Care")
            @PathVariable String groupDescription,
            @Parameter(description = "Visit date", example = "2023-10-13T10:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime visitDate) {
        try {
            logger.info("Deleting instruction group from visit: group={}, patient={}", groupDescription, patientId);
            instructionGroupService.deleteInstructionGroupFromVisit(
                    doctorId, clinicId, shiftId, patientId, visitNo, visitDate, groupDescription);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Instruction group deleted from visit successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting instruction group from visit: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to delete instruction group: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Delete all instruction groups from a visit
     * DELETE /api/instruction-groups/visits/{doctorId}/{clinicId}/{shiftId}/{patientId}/{visitNo}
     */
    @DeleteMapping("/visits/{doctorId}/{clinicId}/{shiftId}/{patientId}/{visitNo}")
    @Operation(summary = "Delete all instruction groups from visit", 
               description = "Deletes all instruction groups from a patient visit")
    public ResponseEntity<?> deleteAllInstructionGroupsFromVisit(
            @Parameter(description = "Doctor ID", example = "DR-00001")
            @PathVariable String doctorId,
            @Parameter(description = "Clinic ID", example = "CL-00001")
            @PathVariable String clinicId,
            @Parameter(description = "Shift ID", example = "1")
            @PathVariable Short shiftId,
            @Parameter(description = "Patient ID", example = "11-02-2019-020500")
            @PathVariable String patientId,
            @Parameter(description = "Visit number", example = "1")
            @PathVariable Integer visitNo,
            @Parameter(description = "Visit date", example = "2023-10-13T10:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime visitDate) {
        try {
            logger.info("Deleting all instruction groups from visit: patient={}, visitNo={}", patientId, visitNo);
            instructionGroupService.deleteAllInstructionGroupsFromVisit(
                    doctorId, clinicId, shiftId, patientId, visitNo, visitDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "All instruction groups deleted from visit successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting all instruction groups from visit: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to delete instruction groups: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

