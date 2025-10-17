package com.climasys.instructions.service;

import com.climasys.entity.GroupInstructions;
import com.climasys.entity.InstructionsGroupMaster;
import com.climasys.entity.VisitGroupsInstructions;
import com.climasys.instructions.dto.*;
import com.climasys.repository.GroupInstructionsRepository;
import com.climasys.repository.InstructionsGroupMasterRepository;
import com.climasys.repository.VisitGroupsInstructionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for instruction group business logic
 * Provides methods for managing instruction groups and their associations with patient visits
 */
@Service
@Transactional
public class InstructionGroupService {
    
    private static final Logger logger = LoggerFactory.getLogger(InstructionGroupService.class);
    
    @Autowired
    private InstructionsGroupMasterRepository instructionsGroupMasterRepository;
    
    @Autowired
    private GroupInstructionsRepository groupInstructionsRepository;
    
    @Autowired
    private VisitGroupsInstructionsRepository visitGroupsInstructionsRepository;
    
    /**
     * Get all instruction groups for a specific doctor
     * @param doctorId Doctor ID
     * @return List of instruction groups
     */
    @Transactional(readOnly = true)
    public List<InstructionGroupDTO> getAllInstructionGroupsForDoctor(String doctorId) {
        logger.info("Getting all instruction groups for doctor: {}", doctorId);
        
        List<InstructionsGroupMaster> groups = instructionsGroupMasterRepository
                .findByDoctorIdOrderByPriority(doctorId);
        
        return groups.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * Get instruction groups with their details for a doctor
     * Includes all instructions within each group
     * @param doctorId Doctor ID
     * @return List of instruction groups with details
     */
    @Transactional(readOnly = true)
    public List<InstructionGroupDTO> getInstructionGroupsWithDetails(String doctorId) {
        logger.info("Getting instruction groups with details for doctor: {}", doctorId);
        
        List<InstructionsGroupMaster> groups = instructionsGroupMasterRepository
                .findByDoctorIdOrderByPriority(doctorId);
        
        List<InstructionGroupDTO> result = new ArrayList<>();
        
        for (InstructionsGroupMaster group : groups) {
            InstructionGroupDTO dto = convertToDTO(group);
            
            // Get instructions for this group
            List<GroupInstructions> instructions = groupInstructionsRepository
                    .findByDoctorIdAndGroupDescriptionOrderBySequenceNoAscInstructionsDescriptionAsc(
                            doctorId, group.getGroupDescription());
            
            dto.setInstructions(instructions.stream()
                    .map(this::convertToDetailDTO)
                    .collect(Collectors.toList()));
            
            result.add(dto);
        }
        
        return result;
    }
    
    /**
     * Search instruction groups by description
     * @param doctorId Doctor ID
     * @param searchTerm Search term
     * @return List of matching instruction groups
     */
    @Transactional(readOnly = true)
    public List<InstructionGroupDTO> searchInstructionGroups(String doctorId, String searchTerm) {
        logger.info("Searching instruction groups for doctor: {} with term: {}", doctorId, searchTerm);
        
        List<InstructionsGroupMaster> groups = instructionsGroupMasterRepository
                .searchByGroupDescription(doctorId, searchTerm);
        
        return groups.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * Get instructions for a specific group
     * @param doctorId Doctor ID
     * @param groupDescription Group description
     * @return List of instructions in the group
     */
    @Transactional(readOnly = true)
    public List<InstructionDetailDTO> getInstructionsForGroup(String doctorId, String groupDescription) {
        logger.info("Getting instructions for group: {} for doctor: {}", groupDescription, doctorId);
        
        List<GroupInstructions> instructions = groupInstructionsRepository
                .findByDoctorIdAndGroupDescriptionOrderBySequenceNoAscInstructionsDescriptionAsc(
                        doctorId, groupDescription);
        
        return instructions.stream()
                .map(this::convertToDetailDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get instruction groups in formatted way (similar to stored procedure)
     * @param doctorId Doctor ID
     * @return List of formatted instruction data
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getInstructionGroupsFormatted(String doctorId) {
        logger.info("Getting formatted instruction groups for doctor: {}", doctorId);
        return groupInstructionsRepository.findInstructionGroupsFormatted(doctorId);
    }
    
    /**
     * Create a new instruction group with instructions
     * @param request Create instruction group request
     * @return Created instruction group DTO
     */
    public InstructionGroupDTO createInstructionGroup(CreateInstructionGroupRequest request) {
        logger.info("Creating new instruction group: {} for doctor: {}", 
                request.getGroupDescription(), request.getDoctorId());
        
        // Check if group already exists
        if (instructionsGroupMasterRepository.existsByDoctorIdAndGroupDescription(
                request.getDoctorId(), request.getGroupDescription())) {
            throw new IllegalArgumentException("Instruction group already exists: " + request.getGroupDescription());
        }
        
        // Create group master
        InstructionsGroupMaster group = new InstructionsGroupMaster();
        group.setDoctorId(request.getDoctorId());
        group.setGroupDescription(request.getGroupDescription());
        group.setPriorityValue(request.getPriorityValue());
        group.setCreatedByName(request.getCreatedByName());
        group.setCreatedOn(LocalDateTime.now());
        
        instructionsGroupMasterRepository.save(group);
        
        // Create instructions
        List<GroupInstructions> instructions = new ArrayList<>();
        int sequenceNo = 1;
        
        for (InstructionItemRequest item : request.getInstructions()) {
            GroupInstructions instruction = new GroupInstructions();
            instruction.setDoctorId(request.getDoctorId());
            instruction.setGroupDescription(request.getGroupDescription());
            instruction.setInstructionsDescription(item.getInstructionsDescription());
            instruction.setSequenceNo(item.getSequenceNo() != null ? item.getSequenceNo() : sequenceNo++);
            instruction.setPriorityValue(item.getPriorityValue());
            instruction.setCreatedByName(request.getCreatedByName());
            instruction.setCreatedOn(LocalDateTime.now());
            
            instructions.add(instruction);
        }
        
        groupInstructionsRepository.saveAll(instructions);
        
        // Return DTO
        InstructionGroupDTO dto = convertToDTO(group);
        dto.setInstructions(instructions.stream()
                .map(this::convertToDetailDTO)
                .collect(Collectors.toList()));
        
        return dto;
    }
    
    /**
     * Add instruction groups to a patient visit
     * @param request Add instruction group to visit request
     * @return List of visit instruction groups
     */
    public List<VisitInstructionGroupDTO> addInstructionGroupsToVisit(AddInstructionGroupToVisitRequest request) {
        logger.info("Adding instruction groups to visit for patient: {}", request.getPatientId());
        
        List<VisitGroupsInstructions> visitInstructions = new ArrayList<>();
        
        for (String groupDescription : request.getGroupDescriptions()) {
            // Get instructions for this group
            List<GroupInstructions> groupInstructions = groupInstructionsRepository
                    .findByDoctorIdAndGroupDescriptionOrderBySequenceNoAscInstructionsDescriptionAsc(
                            request.getDoctorId(), groupDescription);
            
            // Create visit instructions for each instruction in the group
            for (GroupInstructions instruction : groupInstructions) {
                VisitGroupsInstructions visitInstruction = new VisitGroupsInstructions();
                visitInstruction.setDoctorId(request.getDoctorId());
                visitInstruction.setClinicId(request.getClinicId());
                visitInstruction.setShiftId(request.getShiftId());
                visitInstruction.setPatientId(request.getPatientId());
                visitInstruction.setPatientVisitNo(request.getPatientVisitNo());
                visitInstruction.setVisitDate(request.getVisitDate());
                visitInstruction.setGroupDescription(groupDescription);
                visitInstruction.setInstructionsDescription(instruction.getInstructionsDescription());
                visitInstruction.setSequenceNo(instruction.getSequenceNo());
                visitInstruction.setCreatedByName(request.getCreatedByName());
                visitInstruction.setCreatedOn(LocalDateTime.now());
                
                visitInstructions.add(visitInstruction);
            }
        }
        
        visitGroupsInstructionsRepository.saveAll(visitInstructions);
        
        return visitInstructions.stream()
                .map(this::convertToVisitDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get instruction groups for a patient visit
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param shiftId Shift ID
     * @param patientId Patient ID
     * @param patientVisitNo Visit number
     * @param visitDate Visit date
     * @return List of visit instruction groups
     */
    @Transactional(readOnly = true)
    public List<VisitInstructionGroupDTO> getInstructionGroupsForVisit(
            String doctorId, String clinicId, Short shiftId, String patientId, 
            Integer patientVisitNo, LocalDateTime visitDate) {
        logger.info("Getting instruction groups for visit: patient={}, visitNo={}", patientId, patientVisitNo);
        
        List<VisitGroupsInstructions> visitInstructions = visitGroupsInstructionsRepository
                .findByDoctorIdAndClinicIdAndShiftIdAndPatientIdAndPatientVisitNoAndVisitDateOrderByGroupDescriptionAscSequenceNoAsc(
                        doctorId, clinicId, shiftId, patientId, patientVisitNo, visitDate);
        
        return visitInstructions.stream()
                .map(this::convertToVisitDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Delete instruction group from a visit
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param shiftId Shift ID
     * @param patientId Patient ID
     * @param patientVisitNo Visit number
     * @param visitDate Visit date
     * @param groupDescription Group description to delete
     */
    public void deleteInstructionGroupFromVisit(String doctorId, String clinicId, Short shiftId,
                                                String patientId, Integer patientVisitNo,
                                                LocalDateTime visitDate, String groupDescription) {
        logger.info("Deleting instruction group from visit: group={}, patient={}", groupDescription, patientId);
        
        visitGroupsInstructionsRepository.deleteByVisitAndGroup(
                doctorId, clinicId, shiftId, patientId, patientVisitNo, visitDate, groupDescription);
    }
    
    /**
     * Delete all instruction groups from a visit
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param shiftId Shift ID
     * @param patientId Patient ID
     * @param patientVisitNo Visit number
     * @param visitDate Visit date
     */
    public void deleteAllInstructionGroupsFromVisit(String doctorId, String clinicId, Short shiftId,
                                                    String patientId, Integer patientVisitNo,
                                                    LocalDateTime visitDate) {
        logger.info("Deleting all instruction groups from visit: patient={}, visitNo={}", patientId, patientVisitNo);
        
        visitGroupsInstructionsRepository.deleteByVisit(
                doctorId, clinicId, shiftId, patientId, patientVisitNo, visitDate);
    }
    
    /**
     * Get distinct group descriptions for a visit
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @param shiftId Shift ID
     * @param patientId Patient ID
     * @param patientVisitNo Visit number
     * @param visitDate Visit date
     * @return List of group descriptions
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctGroupsForVisit(String doctorId, String clinicId, Short shiftId,
                                                  String patientId, Integer patientVisitNo,
                                                  LocalDateTime visitDate) {
        logger.info("Getting distinct groups for visit: patient={}, visitNo={}", patientId, patientVisitNo);
        
        return visitGroupsInstructionsRepository.findDistinctGroupsByVisit(
                doctorId, clinicId, shiftId, patientId, patientVisitNo, visitDate);
    }
    
    // ==================== Conversion Methods ====================
    
    private InstructionGroupDTO convertToDTO(InstructionsGroupMaster entity) {
        InstructionGroupDTO dto = new InstructionGroupDTO();
        dto.setDoctorId(entity.getDoctorId());
        dto.setGroupDescription(entity.getGroupDescription());
        dto.setPriorityValue(entity.getPriorityValue());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setCreatedByName(entity.getCreatedByName());
        dto.setModifiedOn(entity.getModifiedOn());
        dto.setModifiedByName(entity.getModifiedByName());
        return dto;
    }
    
    private InstructionDetailDTO convertToDetailDTO(GroupInstructions entity) {
        InstructionDetailDTO dto = new InstructionDetailDTO();
        dto.setDoctorId(entity.getDoctorId());
        dto.setGroupDescription(entity.getGroupDescription());
        dto.setInstructionsDescription(entity.getInstructionsDescription());
        dto.setSequenceNo(entity.getSequenceNo());
        dto.setPriorityValue(entity.getPriorityValue());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setCreatedByName(entity.getCreatedByName());
        dto.setModifiedOn(entity.getModifiedOn());
        dto.setModifiedByName(entity.getModifiedByName());
        // Set concatenated format for backward compatibility
        dto.setInstructionGroup(entity.getGroupDescription() + "*" + entity.getInstructionsDescription());
        return dto;
    }
    
    private VisitInstructionGroupDTO convertToVisitDTO(VisitGroupsInstructions entity) {
        VisitInstructionGroupDTO dto = new VisitInstructionGroupDTO();
        dto.setDoctorId(entity.getDoctorId());
        dto.setClinicId(entity.getClinicId());
        dto.setShiftId(entity.getShiftId());
        dto.setPatientId(entity.getPatientId());
        dto.setPatientVisitNo(entity.getPatientVisitNo());
        dto.setVisitDate(entity.getVisitDate());
        dto.setGroupDescription(entity.getGroupDescription());
        dto.setInstructionsDescription(entity.getInstructionsDescription());
        dto.setSequenceNo(entity.getSequenceNo());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setCreatedByName(entity.getCreatedByName());
        return dto;
    }
}

