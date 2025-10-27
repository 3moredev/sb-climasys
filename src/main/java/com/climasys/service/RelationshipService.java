package com.climasys.service;

import com.climasys.entity.DoctorClinicShift;
import com.climasys.entity.DoctorClinicShiftId;
import com.climasys.entity.StatusRef;
import com.climasys.repository.DoctorClinicShiftRepository;
import com.climasys.repository.StatusRefRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for dynamically creating required database relationships
 */
@Service
public class RelationshipService {
    
    private static final Logger logger = LoggerFactory.getLogger(RelationshipService.class);
    
    @Autowired
    private DoctorClinicShiftRepository doctorClinicShiftRepository;
    
    @Autowired
    private StatusRefRepository statusRefRepository;
    
    /**
     * Ensure doctor-clinic-shift relationship exists
     */
    public void ensureDoctorClinicShiftRelationship(String doctorId, String clinicId, Short shiftId) {
        try {
            boolean exists = doctorClinicShiftRepository.existsByIdDoctorIdAndIdClinicIdAndIdShiftId(
                doctorId, clinicId, shiftId);
            
            if (!exists) {
                logger.info("Creating doctor-clinic-shift relationship: Doctor={}, Clinic={}, Shift={}", 
                    doctorId, clinicId, shiftId);
                
                DoctorClinicShiftId shiftIdObj = new DoctorClinicShiftId(shiftId, clinicId, doctorId);
                DoctorClinicShift relationship = new DoctorClinicShift(shiftIdObj);
                doctorClinicShiftRepository.save(relationship);
                
                logger.info("Successfully created doctor-clinic-shift relationship");
            }
        } catch (Exception e) {
            logger.error("Failed to create doctor-clinic-shift relationship: {}", e.getMessage());
            throw new RuntimeException("Failed to create doctor-clinic-shift relationship", e);
        }
    }
    
    /**
     * Ensure status reference exists for clinic
     */
    public void ensureStatusReference(Short statusId, String clinicId, String doctorId, String statusDescription) {
        try {
            boolean exists = statusRefRepository.existsByIdAndClinicId(statusId, clinicId);
            
            if (!exists) {
                logger.info("Creating status reference: StatusId={}, ClinicId={}, DoctorId={}, Description={}", 
                    statusId, clinicId, doctorId, statusDescription);
                
                StatusRef statusRef = new StatusRef();
                statusRef.setId(statusId);
                statusRef.setClinicId(clinicId);
                statusRef.setDoctorId(doctorId);
                statusRef.setStatusDescription(statusDescription);
                statusRefRepository.save(statusRef);
                
                logger.info("Successfully created status reference");
            }
        } catch (Exception e) {
            logger.error("Failed to create status reference: {}", e.getMessage());
            throw new RuntimeException("Failed to create status reference", e);
        }
    }
    
    /**
     * Ensure all required relationships exist for a visit
     */
    public void ensureVisitRelationships(String doctorId, String clinicId, Short shiftId) {
        logger.info("Ensuring relationships for visit: Doctor={}, Clinic={}, Shift={}", 
            doctorId, clinicId, shiftId);
        
        // Ensure doctor-clinic-shift relationship
        ensureDoctorClinicShiftRelationship(doctorId, clinicId, shiftId);
        
        // Ensure basic status references exist
        ensureStatusReference((short) 1, clinicId, doctorId, "Waiting");
        ensureStatusReference((short) 2, clinicId, doctorId, "With Doctor");
        
        logger.info("All required relationships ensured for visit");
    }
}
