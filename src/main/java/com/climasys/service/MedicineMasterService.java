package com.climasys.service;

import com.climasys.entity.MedicineMaster;
import com.climasys.entity.MedicineMasterId;
import com.climasys.repository.MedicineMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for MedicineMaster business logic
 * Provides methods for managing medicine master data
 * Following the same pattern as ProcedureMasterService
 */
@Service
@Transactional
public class MedicineMasterService {

    private static final Logger logger = LoggerFactory.getLogger(MedicineMasterService.class);

    @Autowired
    private MedicineMasterRepository medicineMasterRepository;

    /**
     * Get all medicines for a specific doctor and clinic
     * 
     * @param doctorId Doctor ID
     * @param clinicId Clinic ID
     * @return List of medicines for the doctor and clinic
     */
    @Transactional(readOnly = true)
    public List<MedicineMaster> getAllMedicinesForDoctorAndClinic(String doctorId, String clinicId) {
        logger.info("Getting all medicines for doctor: {} and clinic: {}", doctorId, clinicId);
        return medicineMasterRepository.findByDoctorIdAndClinicIdOrderByPriorityValueAscShortDescriptionAsc(doctorId,
                clinicId);
    }

    /**
     * Get all medicines for a specific doctor (backward compatibility)
     * 
     * @param doctorId Doctor ID
     * @return List of medicines for the doctor
     */
    @Transactional(readOnly = true)
    public List<MedicineMaster> getAllMedicinesForDoctor(String doctorId) {
        logger.info("Getting all medicines for doctor: {}", doctorId);
        return medicineMasterRepository.findByDoctorIdOrderByPriorityValueAscShortDescriptionAsc(doctorId);
    }

    /**
     * Search medicines by short description, medicine description, or priority for
     * a specific doctor and clinic
     * 
     * @param doctorId   Doctor ID
     * @param clinicId   Clinic ID
     * @param searchTerm Search term to match against short description, medicine
     *                   description, or priority
     * @return List of matching medicines
     */
    @Transactional(readOnly = true)
    public List<MedicineMaster> searchMedicinesByDescription(String doctorId, String clinicId, String searchTerm) {
        logger.info("Searching medicines for doctor: {} and clinic: {} with term: {}", doctorId, clinicId, searchTerm);
        return medicineMasterRepository.searchMedicinesByDescriptionOrPriorityAndClinic(doctorId, clinicId, searchTerm);
    }

    /**
     * Search medicines by short description, medicine description, or priority for
     * a specific doctor (backward compatibility)
     * 
     * @param doctorId   Doctor ID
     * @param searchTerm Search term to match against short description, medicine
     *                   description, or priority
     * @return List of matching medicines
     */
    @Transactional(readOnly = true)
    public List<MedicineMaster> searchMedicinesByDescription(String doctorId, String searchTerm) {
        logger.info("Searching medicines for doctor: {} with term: {}", doctorId, searchTerm);
        return medicineMasterRepository.searchMedicinesByDescriptionOrPriority(doctorId, searchTerm);
    }

    /**
     * Get a medicine by short description, doctor ID, and clinic ID
     * 
     * @param shortDescription Short description
     * @param doctorId         Doctor ID
     * @param clinicId         Clinic ID
     * @return Optional medicine
     */
    @Transactional(readOnly = true)
    public Optional<MedicineMaster> getMedicineByShortDescription(String shortDescription, String doctorId,
            String clinicId) {
        logger.info("Getting medicine by short description: {} for doctor: {} and clinic: {}", shortDescription,
                doctorId, clinicId);
        MedicineMasterId id = new MedicineMasterId(shortDescription, doctorId, clinicId);
        return medicineMasterRepository.findById(id);
    }

    /**
     * Create a new medicine
     * 
     * @param medicine Medicine to create
     * @return Created medicine
     */
    public MedicineMaster createMedicine(MedicineMaster medicine) {
        logger.info("Creating new medicine: {}", medicine.getShortDescription());

        // Check if medicine already exists
        if (medicineMasterRepository.existsByDoctorIdAndClinicIdAndShortDescription(
                medicine.getDoctorId(), medicine.getClinicId(), medicine.getShortDescription())) {
            throw new RuntimeException("Medicine with short description '" + medicine.getShortDescription() +
                    "' already exists for doctor " + medicine.getDoctorId() + " and clinic " + medicine.getClinicId());
        }

        // Set creation timestamp
        medicine.setCreatedOn(LocalDateTime.now());
        medicine.setModifiedOn(LocalDateTime.now());

        // Set active to true by default if not set
        if (medicine.getActive() == null) {
            medicine.setActive(true);
        }

        return medicineMasterRepository.save(medicine);
    }

    /**
     * Update an existing medicine
     * 
     * @param medicine Medicine to update
     * @return Updated medicine
     */
    public MedicineMaster updateMedicine(MedicineMaster medicine) {
        logger.info("Updating medicine: {}", medicine.getShortDescription());

        MedicineMasterId id = new MedicineMasterId(
                medicine.getShortDescription(),
                medicine.getDoctorId(),
                medicine.getClinicId());

        Optional<MedicineMaster> existingOpt = medicineMasterRepository.findById(id);
        if (existingOpt.isPresent()) {
            MedicineMaster existing = existingOpt.get();

            // Update fields from the input
            existing.setMedicineDescription(medicine.getMedicineDescription());
            existing.setPriorityValue(medicine.getPriorityValue());
            existing.setMorning(medicine.getMorning());
            existing.setAfternoon(medicine.getAfternoon());
            existing.setNight(medicine.getNight());
            existing.setNoOfDays(medicine.getNoOfDays());
            existing.setInstruction(medicine.getInstruction());
            existing.setActive(medicine.getActive());
            existing.setModifiedByName(medicine.getModifiedByName());

            // Set modification timestamp
            existing.setModifiedOn(LocalDateTime.now());

            return medicineMasterRepository.save(existing);
        } else {
            // If it doesn't exist, we fall back to a simple save (which might create)
            // or we could throw an exception. Given the logic in the popup (old delete/new
            // create),
            // this fallback handles unexpected states gracefully.
            medicine.setModifiedOn(LocalDateTime.now());
            return medicineMasterRepository.save(medicine);
        }
    }

    /**
     * Delete a medicine
     * 
     * @param shortDescription Short description
     * @param doctorId         Doctor ID
     * @param clinicId         Clinic ID
     * @return True if deleted successfully
     */
    public boolean deleteMedicine(String shortDescription, String doctorId, String clinicId) {
        logger.info("Deleting medicine: {} for doctor: {} and clinic: {}", shortDescription, doctorId, clinicId);

        Optional<MedicineMaster> medicineOpt = getMedicineByShortDescription(shortDescription, doctorId, clinicId);
        if (medicineOpt.isPresent()) {
            MedicineMasterId id = new MedicineMasterId(shortDescription, doctorId, clinicId);
            medicineMasterRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
