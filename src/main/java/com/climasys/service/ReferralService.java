package com.climasys.service;

import com.climasys.entity.ReferBy;
import com.climasys.entity.ReferByTranslation;
import com.climasys.entity.ReferralDoctor;
import com.climasys.repository.ReferByRepository;
import com.climasys.repository.ReferByTranslationRepository;
import com.climasys.repository.ReferralDoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
                    throw new RuntimeException("Failed to save referral doctor after sequence reset: " + retryException.getMessage(), retryException);
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
}
