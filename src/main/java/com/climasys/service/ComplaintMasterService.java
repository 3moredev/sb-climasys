package com.climasys.service;

import com.climasys.entity.ComplaintMaster;
import com.climasys.repository.ComplaintMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for ComplaintMaster business logic
 * Provides methods for managing complaint master data
 */
@Service
@Transactional
public class ComplaintMasterService {

    private static final Logger logger = LoggerFactory.getLogger(ComplaintMasterService.class);

    @Autowired
    private ComplaintMasterRepository complaintMasterRepository;

    /**
     * Get all complaints for a specific doctor
     * @param doctorId Doctor ID
     * @return List of complaints for the doctor
     */
    @Transactional(readOnly = true)
    public List<ComplaintMaster> getAllComplaintsForDoctor(String doctorId) {
        logger.info("Getting all complaints for doctor: {}", doctorId);
        return complaintMasterRepository.findByDoctorIdOrderByPriorityValueAscShortDescriptionAsc(doctorId);
    }

    /**
     * Get complaints that are visible to operators for a specific doctor
     * This is the main method that replicates the stored procedure functionality
     * @param doctorId Doctor ID
     * @return List of complaints visible to operators
     */
    @Transactional(readOnly = true)
    public List<ComplaintMaster> getComplaintsForOperatorDisplay(String doctorId) {
        logger.info("Getting complaints for operator display for doctor: {}", doctorId);
        return complaintMasterRepository.findComplaintsForOperatorDisplay(doctorId);
    }

    /**
     * Get complaints for operator display in the same format as stored procedure
     * Returns data with concatenated ID field for backward compatibility
     * @param doctorId Doctor ID
     * @return List of complaint data with formatted ID field
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getComplaintsForOperatorDisplayFormatted(String doctorId) {
        logger.info("Getting formatted complaints for operator display for doctor: {}", doctorId);
        return complaintMasterRepository.findComplaintsForOperatorDisplayFormatted(doctorId);
    }

    /**
     * Get all complaint data for a doctor in formatted way (including non-operator visible)
     * @param doctorId Doctor ID
     * @return List of all complaint data for the doctor
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllComplaintsForDoctorFormatted(String doctorId) {
        logger.info("Getting all formatted complaints for doctor: {}", doctorId);
        return complaintMasterRepository.findAllComplaintsForDoctorFormatted(doctorId);
    }

    /**
     * Search complaints by description for a specific doctor
     * @param doctorId Doctor ID
     * @param searchTerm Search term to match against short or full description
     * @return List of matching complaints
     */
    @Transactional(readOnly = true)
    public List<ComplaintMaster> searchComplaintsByDescription(String doctorId, String searchTerm) {
        logger.info("Searching complaints for doctor: {} with term: {}", doctorId, searchTerm);
        return complaintMasterRepository.searchComplaintsByDescription(doctorId, searchTerm);
    }

    /**
     * Search complaints for operator display by description
     * @param doctorId Doctor ID
     * @param searchTerm Search term to match against short or full description
     * @return List of matching complaints visible to operators
     */
    @Transactional(readOnly = true)
    public List<ComplaintMaster> searchComplaintsForOperatorDisplay(String doctorId, String searchTerm) {
        logger.info("Searching complaints for operator display for doctor: {} with term: {}", doctorId, searchTerm);
        return complaintMasterRepository.searchComplaintsForOperatorDisplay(doctorId, searchTerm);
    }

    /**
     * Create a new complaint
     * @param complaint Complaint to create
     * @return Created complaint
     */
    public ComplaintMaster createComplaint(ComplaintMaster complaint) {
        logger.info("Creating new complaint: {}", complaint.getShortDescription());
        
        // Set creation timestamp
        complaint.setCreatedOn(LocalDateTime.now());
        complaint.setModifiedOn(LocalDateTime.now());
        
        // Ensure display_to_operator has a default value
        if (complaint.getDisplayToOperator() == null) {
            complaint.setDisplayToOperator((short) 0);
        }
        
        return complaintMasterRepository.save(complaint);
    }

    /**
     * Update an existing complaint
     * @param complaint Complaint to update
     * @return Updated complaint
     */
    public ComplaintMaster updateComplaint(ComplaintMaster complaint) {
        logger.info("Updating complaint: {}", complaint.getShortDescription());
        
        // Set modification timestamp
        complaint.setModifiedOn(LocalDateTime.now());
        
        return complaintMasterRepository.save(complaint);
    }

    /**
     * Get a complaint by short description and doctor ID
     * @param shortDescription Short description
     * @param doctorId Doctor ID
     * @return Optional complaint
     */
    @Transactional(readOnly = true)
    public Optional<ComplaintMaster> getComplaintByShortDescription(String shortDescription, String doctorId) {
        logger.info("Getting complaint by short description: {} for doctor: {}", shortDescription, doctorId);
        return complaintMasterRepository.findById(shortDescription)
                .filter(complaint -> complaint.getDoctorId().equals(doctorId));
    }

    /**
     * Delete a complaint
     * @param shortDescription Short description of complaint to delete
     * @param doctorId Doctor ID (for security check)
     * @return True if deleted successfully
     */
    public boolean deleteComplaint(String shortDescription, String doctorId) {
        logger.info("Deleting complaint: {} for doctor: {}", shortDescription, doctorId);
        
        Optional<ComplaintMaster> complaintOpt = getComplaintByShortDescription(shortDescription, doctorId);
        if (complaintOpt.isPresent()) {
            complaintMasterRepository.deleteById(shortDescription);
            return true;
        }
        return false;
    }

    /**
     * Toggle display to operator flag for a complaint
     * @param shortDescription Short description
     * @param doctorId Doctor ID
     * @param displayToOperator New display to operator value
     * @return Updated complaint
     */
    public Optional<ComplaintMaster> toggleDisplayToOperator(String shortDescription, String doctorId, boolean displayToOperator) {
        logger.info("Toggling display to operator for complaint: {} to {} for doctor: {}", 
                   shortDescription, displayToOperator, doctorId);
        
        Optional<ComplaintMaster> complaintOpt = getComplaintByShortDescription(shortDescription, doctorId);
        if (complaintOpt.isPresent()) {
            ComplaintMaster complaint = complaintOpt.get();
            complaint.setDisplayToOperator(displayToOperator);
            complaint.setModifiedOn(LocalDateTime.now());
            return Optional.of(complaintMasterRepository.save(complaint));
        }
        return Optional.empty();
    }

    /**
     * Get complaint statistics for a doctor
     * @param doctorId Doctor ID
     * @return Map containing complaint statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getComplaintStatistics(String doctorId) {
        logger.info("Getting complaint statistics for doctor: {}", doctorId);
        
        long totalComplaints = complaintMasterRepository.countByDoctorId(doctorId);
        long operatorVisibleComplaints = complaintMasterRepository.countByDoctorIdAndDisplayToOperator(doctorId, (short) 1);
        
        return Map.of(
            "totalComplaints", totalComplaints,
            "operatorVisibleComplaints", operatorVisibleComplaints,
            "doctorOnlyComplaints", totalComplaints - operatorVisibleComplaints
        );
    }

    /**
     * Check if a complaint exists for a doctor
     * @param doctorId Doctor ID
     * @param shortDescription Short description to check
     * @return True if complaint exists
     */
    @Transactional(readOnly = true)
    public boolean complaintExists(String doctorId, String shortDescription) {
        return complaintMasterRepository.existsByDoctorIdAndShortDescription(doctorId, shortDescription);
    }
}
