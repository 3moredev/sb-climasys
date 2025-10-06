package com.climasys.visits.service;

import com.climasys.entity.PatientVisit;
import com.climasys.repository.PatientVisitRepository;
import com.climasys.repository.DoctorClinicShiftRepository;
import com.climasys.repository.StatusRefRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VisitJpaService {
    
    private static final Logger logger = LoggerFactory.getLogger(VisitJpaService.class);
    
    @Autowired
    private PatientVisitRepository patientVisitRepository;
    
    @Autowired
    private DoctorClinicShiftRepository doctorClinicShiftRepository;
    
    @Autowired
    private StatusRefRepository statusRefRepository;
    
    /**
     * Save or update a comprehensive patient visit using JPA
     */
    @Transactional
    public Map<String, Object> saveComprehensiveVisit(ComprehensiveVisitRequest request) {
        logger.info("Saving comprehensive visit for patient: {} using JPA", request.patientId());
        
        try {
            // Log the search parameters for debugging
            logger.info("Searching for existing visit with parameters:");
            logger.info("  PatientId: {}", request.patientId());
            logger.info("  DoctorId: {}", request.doctorId());
            logger.info("  ClinicId: {}", request.clinicId());
            logger.info("  ShiftId: {}", request.shiftId());
            logger.info("  PatientVisitNo: {}", request.patientVisitNo());
            logger.info("  VisitDate: {}", request.visitDate());
            
            // For lookup, we need to find visits on the same date (ignoring time)
            // Normalize the visit date to start of day for comparison
            LocalDateTime normalizedVisitDate = request.visitDate().toLocalDate().atStartOfDay();
            logger.info("  Normalized VisitDate for lookup: {}", normalizedVisitDate);
            
            // Check if visit already exists using a custom query that compares only the date part
            Optional<PatientVisit> existingVisit = findExistingVisitByDate(
                request.patientId(),
                request.doctorId(),
                request.clinicId(),
                request.shiftId(),
                request.patientVisitNo(),
                request.visitDate().toLocalDate()
            );
            
            logger.info("Existing visit found: {}", existingVisit.isPresent());
            
            // Debug: Check what visits exist for this patient
            List<PatientVisit> allPatientVisits = patientVisitRepository.findByPatientIdAndDeleteFlagOrderByVisitDateDesc(
                request.patientId(), false);
            logger.info("Total visits found for patient {}: {}", request.patientId(), allPatientVisits.size());
            for (PatientVisit pv : allPatientVisits) {
                logger.info("  Existing visit: PatientId={}, DoctorId={}, ClinicId={}, ShiftId={}, PatientVisitNo={}, VisitDate={}", 
                    pv.getPatientId(), pv.getDoctorId(), pv.getClinicId(), pv.getShiftId(), 
                    pv.getPatientVisitNo(), pv.getVisitDate());
            }
            
            PatientVisit visit;
            boolean isUpdate = false;
            
            if (existingVisit.isPresent()) {
                // Update existing visit
                visit = existingVisit.get();
                isUpdate = true;
                logger.info("Updating existing visit for patient: {}", request.patientId());
            } else {
                // Create new visit
                visit = new PatientVisit();
                // Set composite key fields
                visit.setPatientId(request.patientId());
                visit.setDoctorId(request.doctorId());
                visit.setClinicId(request.clinicId());
                visit.setShiftId(request.shiftId());
                visit.setPatientVisitNo(request.patientVisitNo());
                visit.setVisitDate(request.visitDate());
                logger.info("Creating new visit for patient: {}", request.patientId());
            }
            
            // Map all fields from request to entity
            mapRequestToEntity(request, visit);
            
            // Validate required fields before saving
            validateRequiredFields(visit);
            
            // Log key fields for debugging
            logger.info("Mapped visit - PatientId: {}, DoctorId: {}, StatusId: {}, IsSubmit: {}", 
                visit.getPatientId(), visit.getDoctorId(), visit.getStatusId(), visit.getIsSubmitPatientVisitDetails());
            
            // Set audit fields
            LocalDateTime now = LocalDateTime.now();
            if (!isUpdate) {
                visit.setCreatedOn(now);
                visit.setCreatedbyName(request.userId());
            }
            visit.setModifiedOn(now);
            visit.setModifiedbyName(request.userId());
            
            // Save the entity
            PatientVisit savedVisit = patientVisitRepository.save(visit);
            
            logger.info("Successfully saved visit for patient: {}", savedVisit.getPatientId());
            
            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", isUpdate ? "Visit updated successfully" : "Visit created successfully");
            response.put("patientId", savedVisit.getPatientId());
            response.put("doctorId", savedVisit.getDoctorId());
            response.put("clinicId", savedVisit.getClinicId());
            response.put("patientVisitNo", savedVisit.getPatientVisitNo());
            response.put("visitDate", savedVisit.getVisitDate());
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error saving comprehensive visit: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to save visit: " + e.getMessage());
            return error;
        }
    }
    
    /**
     * Find existing visit by composite key and date (ignoring time)
     */
    private Optional<PatientVisit> findExistingVisitByDate(
            String patientId,
            String doctorId,
            String clinicId,
            Short shiftId,
            Integer patientVisitNo,
            java.time.LocalDate visitDate) {
        
        logger.info("Searching for existing visit by date: {}", visitDate);
        
        return patientVisitRepository.findByCompositeKeyAndDate(
            patientId, doctorId, clinicId, shiftId, patientVisitNo, visitDate);
    }
    
    /**
     * Map request fields to PatientVisit entity
     */
    private void mapRequestToEntity(ComprehensiveVisitRequest req, PatientVisit visit) {
        // Patient Vitals
        visit.setPulse(req.pulse());
        visit.setHeightInCms(req.heightInCms());
        visit.setWeightInKgs(req.weightInKgs());
        visit.setBloodPressure(req.bloodPressure());
        visit.setSugar(req.sugar());
        visit.setThtext(req.tft());
        
        // Medical Conditions
        visit.setHypertension(req.hypertension());
        visit.setDiabetes(req.diabetes());
        visit.setCholestrol(req.cholestrol());
        visit.setIhd(req.ihd());
        visit.setTh(req.th());
        visit.setAsthama(req.asthama());
        visit.setSmoking(req.smoking());
        visit.setTobaco(req.tobaco());
        visit.setAlchohol(req.alchohol());
        
        // Additional Fields
        visit.setHabitsComments(req.habitDetails());
        visit.setAllergyDtls(req.allergyDetails());
        visit.setObservation(req.observation());
        visit.setInPerson(req.inPerson());
        visit.setSymptomComment(req.symptomComment());
        visit.setImpression(req.impression());
        visit.setAttendedBy(req.attendedBy());
        visit.setPaymentById(req.paymentById() != null ? req.paymentById().shortValue() : null);
        visit.setPaymentRemark(req.paymentRemark());
        visit.setAttendedById(req.attendedById());
        visit.setFollowUp(req.followUp());
        visit.setIsFollowUp(req.followUpFlag());
        visit.setCurrentMedicines(req.currentMedicines());
        visit.setVisitComments(req.visitComments());
        visit.setCurrentComplaints(req.currentComplaint());
        
        // Clinical Fields
        visit.setTpr(req.tpr());
        visit.setImportantFindings(req.importantFindings());
        visit.setAdditionalComments(req.additionalComments());
        visit.setSystemic(req.systemic());
        visit.setOdeama(req.odeama());
        visit.setPallor(req.pallor());
        visit.setGc(req.gc());
        
        // Gynecological Fields
        visit.setFmp(req.fmp());
        visit.setPrmc(req.prmc());
        visit.setPamc(req.pamc());
        visit.setLmp(req.lmp());
        visit.setObstetricsHistory(req.obstetricHistory());
        visit.setSurgicalHistoryPastHistory(req.surgicalHistory());
        visit.setGynecAdditionalComments(req.menstrualAddComments());
        visit.setFollowUpComment(req.followUpComment());
        visit.setFollowUpDate(req.followUpDate());
        visit.setPregnant(req.pregnant());
        visit.setEdd(req.edd());
        visit.setFollowUpType(req.followUpType());
        
        // Financial Fields
        visit.setFeesToCollect(req.feesToCollect());
        visit.setDiscount(req.discount());
        visit.setOriginalDiscount(req.originalDiscount());
        
        // Status and submission flags
        visit.setStatusId(req.statusId());
        visit.setIsSubmitPatientVisitDetails(req.isSubmitPatientVisitDetails());
        
        // Treatment fields
        visit.setTreatmentComment(req.treatmentComment());
        visit.setTreatmentPlan(req.treatmentPlan());
        visit.setPlan(req.plan());
        visit.setNotes(req.notes());
        visit.setImpressionFinding(req.impressionFinding());
        visit.setAdditionalInstructions(req.additionalInstructions());
        
        // Referral fields
        visit.setReferId(req.referId());
        visit.setReferDoctorDetails(req.referDoctorName());
        visit.setDoctorAddress(req.referralAddress());
        visit.setDoctorMobile(req.referralContact());
        visit.setDoctorEmail(req.referralEmail());
        
        // Instructions and offline fields
        visit.setInstructions(req.instructions() != null ? req.instructions() : "");
        visit.setOfflineReason(req.offlineReason() != null ? req.offlineReason() : "");
        visit.setOfflineFlag(req.offlineFlag() != null ? req.offlineFlag() : false);
        
        // Default values for required fields
        visit.setDeleteFlag(false);
    }
    
    /**
     * Validate required fields before saving
     */
    private void validateRequiredFields(PatientVisit visit) {
        StringBuilder errors = new StringBuilder();
        
        // Check composite key fields
        if (visit.getPatientId() == null || visit.getPatientId().trim().isEmpty()) {
            errors.append("Patient ID is required. ");
        }
        if (visit.getDoctorId() == null || visit.getDoctorId().trim().isEmpty()) {
            errors.append("Doctor ID is required. ");
        }
        if (visit.getClinicId() == null || visit.getClinicId().trim().isEmpty()) {
            errors.append("Clinic ID is required. ");
        }
        if (visit.getShiftId() == null) {
            errors.append("Shift ID is required. ");
        }
        if (visit.getPatientVisitNo() == null) {
            errors.append("Patient Visit Number is required. ");
        }
        if (visit.getVisitDate() == null) {
            errors.append("Visit Date is required. ");
        }
        
        // Check other required fields
        if (visit.getStatusId() == null) {
            errors.append("Status ID is required. ");
        } else {
            // Validate status_id exists in status_ref table
            boolean statusExists = statusRefRepository.existsByIdAndClinicId(visit.getStatusId(), visit.getClinicId());
            if (!statusExists) {
                errors.append("Status ID ").append(visit.getStatusId())
                      .append(" is not valid for clinic ").append(visit.getClinicId()).append(". ");
            }
        }
        if (visit.getDiscount() == null) {
            errors.append("Discount is required. ");
        }
        
        // Validate doctor-clinic-shift relationship
        if (visit.getDoctorId() != null && visit.getClinicId() != null && visit.getShiftId() != null) {
            boolean doctorShiftExists = doctorClinicShiftRepository.existsByIdDoctorIdAndIdClinicIdAndIdShiftId(
                visit.getDoctorId(), visit.getClinicId(), visit.getShiftId());
            if (!doctorShiftExists) {
                errors.append("Doctor ").append(visit.getDoctorId())
                      .append(" is not assigned to clinic ").append(visit.getClinicId())
                      .append(" for shift ").append(visit.getShiftId()).append(". ");
            }
        }
        
        // If there are validation errors, throw exception
        if (errors.length() > 0) {
            String errorMessage = "Validation failed: " + errors.toString().trim();
            logger.error("Validation failed for visit: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }
    
    /**
     * Request record for comprehensive visit data
     */
    public record ComprehensiveVisitRequest(
        // Composite Key Fields
        String patientId,
        String doctorId,
        String clinicId,
        Short shiftId,
        LocalDateTime visitDate,
        Integer patientVisitNo,
        
        // Patient Vitals
        Integer pulse,
        BigDecimal heightInCms,
        BigDecimal weightInKgs,
        String bloodPressure,
        String sugar,
        String tft,
        
        // Medical Conditions
        Boolean hypertension,
        Boolean diabetes,
        Boolean cholestrol,
        Boolean ihd,
        Boolean th,
        Boolean asthama,
        Boolean smoking,
        Boolean tobaco,
        Boolean alchohol,
        
        // Additional Fields
        String habitDetails,
        String allergyDetails,
        String observation,
        Boolean inPerson,
        String symptomComment,
        String impression,
        String attendedBy,
        Integer paymentById,
        String paymentRemark,
        Integer attendedById,
        String followUp,
        Boolean followUpFlag,
        String currentComplaint,
        String currentMedicines,
        String visitComments,
        
        // Clinical Fields
        String tpr,
        String importantFindings,
        String additionalComments,
        String systemic,
        String odeama,
        String pallor,
        String gc,
        
        // Gynecological Fields
        String fmp,
        String prmc,
        String pamc,
        String lmp,
        String obstetricHistory,
        String surgicalHistory,
        String menstrualAddComments,
        String followUpComment,
        LocalDateTime followUpDate,
        Boolean pregnant,
        LocalDateTime edd,
        Short followUpType,
        
        // Financial Fields
        BigDecimal feesToCollect,
        BigDecimal discount,
        BigDecimal originalDiscount,
        
        // Status and User
        Short statusId,
        String userId,
        Boolean isSubmitPatientVisitDetails,
        
        // Treatment fields
        String treatmentComment,
        String treatmentPlan,
        String plan,
        String notes,
        String impressionFinding,
        String additionalInstructions,
        
        // Referral fields
        String referId,
        String referDoctorName,
        String referralAddress,
        String referralContact,
        String referralEmail,
        
        // Additional fields
        String instructions,
        String offlineReason,
        Boolean offlineFlag
    ) {}
}

