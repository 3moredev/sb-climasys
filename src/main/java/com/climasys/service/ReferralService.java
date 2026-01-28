package com.climasys.service;

import com.climasys.entity.ReferBy;
import com.climasys.entity.ReferByTranslation;
import com.climasys.entity.ReferralDoctor;
import com.climasys.repository.ReferByRepository;
import com.climasys.repository.ReferByTranslationRepository;
import com.climasys.repository.ReferralDoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class ReferralService {

    @Autowired
    private ReferByRepository referByRepository;

    @Autowired
    private ReferByTranslationRepository referByTranslationRepository;

    @Autowired
    private ReferralDoctorRepository referralDoctorRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ReferBy> getReferByOptions() {
        return referByRepository.findAllOrdered();
    }

    public List<ReferByTranslation> getReferByTranslations(Integer languageId) {
        List<ReferByTranslation> translations = referByTranslationRepository.findByLanguageId(languageId);

        // Additional safety: Remove any remaining duplicates based on referId
        return translations.stream()
                .collect(java.util.stream.Collectors.toMap(
                        t -> t.getId().getReferId(),
                        t -> t,
                        (existing, replacement) -> existing // Keep first occurrence if duplicates found
                ))
                .values()
                .stream()
                .sorted((t1, t2) -> t1.getId().getReferId().compareTo(t2.getId().getReferId()))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<ReferralDoctor> getReferralDoctors(Integer languageId) {
        return referralDoctorRepository.findByLanguageId(languageId);
    }

    public List<ReferralDoctor> getReferralDoctorsByMobile(String mobile) {
        return referralDoctorRepository.findByDoctorMob(mobile);
    }

    public List<ReferralDoctor> searchReferralDoctors(String searchStr) {
        return referralDoctorRepository.findByDoctorNameContaining(searchStr);
    }

    public ReferralDoctor getReferralDoctorDetails(Integer rdId, Integer languageId) {
        return referralDoctorRepository.findByRdIdAndLanguageId(rdId, languageId);
    }

    public ReferralDoctor saveReferralDoctor(ReferralDoctor referralDoctor) {
        try {
            // Validate clinic_id is provided and exists
            if (referralDoctor.getClinicId() == null || referralDoctor.getClinicId().trim().isEmpty()) {
                throw new IllegalArgumentException("clinic_id is required and cannot be null or empty.");
            }

            if (!validateClinicId(referralDoctor.getClinicId())) {
                throw new IllegalArgumentException(
                        "Invalid clinic_id: " + referralDoctor.getClinicId() + ". Clinic does not exist.");
            }

            // Check for duplicate doctor name in the same clinic (case-insensitive)
            if (referralDoctor.getRdId() == null || referralDoctor.getRdId() == 0) {
                String trimmedName = referralDoctor.getDoctorName().trim();
                String trimmedClinicId = referralDoctor.getClinicId().trim();
                List<ReferralDoctor> existingDoctors = referralDoctorRepository.findByDoctorNameIgnoreCaseAndClinicId(
                        trimmedName, trimmedClinicId);

                // Also check for name duplicates in the specific language if provided
                boolean isDuplicate = existingDoctors.stream()
                        .anyMatch(rd -> rd.getDoctorName().trim().equalsIgnoreCase(trimmedName) &&
                                (referralDoctor.getLanguageId() == null
                                        || rd.getLanguageId().equals(referralDoctor.getLanguageId())));

                if (isDuplicate) {
                    throw new IllegalArgumentException(
                            "Referral doctor with name '" + trimmedName + "' already exists in this clinic.");
                }
            }

            return referralDoctorRepository.save(referralDoctor);
        } catch (Exception e) {
            // If there's a sequence sync issue, try to fix it and retry
            if (e.getMessage().contains("duplicate key value violates unique constraint")) {
                try {
                    // Reset sequence to correct value
                    referralDoctorRepository.resetSequence();
                    // Retry the save operation
                    return referralDoctorRepository.save(referralDoctor);
                } catch (Exception retryException) {
                    throw new RuntimeException(
                            "Failed to save referral doctor after sequence reset: " + retryException.getMessage(),
                            retryException);
                }
            }
            throw e;
        }
    }

    public Map<String, Object> getReferralDoctorDetailsForMobile(String mobile) {
        Map<String, Object> result = new HashMap<>();
        List<ReferralDoctor> doctors = referralDoctorRepository.findByDoctorMob(mobile);

        if (!doctors.isEmpty()) {
            ReferralDoctor doctor = doctors.get(0);
            result.put("exists", true);
            result.put("doctor", doctor);
        } else {
            result.put("exists", false);
        }

        return result;
    }

    /**
     * Validate that a clinic_id exists in the clinic_master table
     * This provides application-level validation since clinic_master has a
     * composite primary key
     * 
     * @param clinicId The clinic ID to validate
     * @return true if the clinic exists, false otherwise
     */
    private boolean validateClinicId(String clinicId) {
        try {
            String sql = "SELECT COUNT(*) FROM clinic_master WHERE clinic_id = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, clinicId);
            return count != null && count > 0;
        } catch (Exception e) {
            // Log the error but return false to indicate validation failed
            System.err.println("Error validating clinic_id " + clinicId + ": " + e.getMessage());
            return false;
        }
    }
}
