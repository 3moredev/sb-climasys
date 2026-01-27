package com.climasys.referral.web;

import com.climasys.auth.annotation.RefreshSession;

import com.climasys.entity.ReferBy;
import com.climasys.entity.ReferByTranslation;
import com.climasys.entity.ReferralDoctor;
import com.climasys.service.ReferralService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/referrals")
@Tag(name = "Referral Management", description = "APIs for managing referral doctors and referral data")
@RefreshSession
public class ReferralController {

    private final ReferralService referralService;

    @Autowired
    public ReferralController(ReferralService referralService) {
        this.referralService = referralService;
    }

    @Operation(summary = "Get Refer By Options", description = "Retrieve all available refer by options")
    @GetMapping("/refer-by")
    public ResponseEntity<?> getReferByOptions() {
        try {
            List<ReferBy> result = referralService.getReferByOptions();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get refer by options: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Get Refer By Translations", description = "Retrieve refer by translations for a specific language")
    @GetMapping("/refer-by/translations")
    public ResponseEntity<?> getReferByTranslations(@RequestParam Integer languageId) {
        try {
            List<ReferByTranslation> result = referralService.getReferByTranslations(languageId);
            
            // Log for debugging
            System.out.println("=== REFERRAL CONTROLLER DEBUG ===");
            System.out.println("Language ID: " + languageId);
            System.out.println("Number of translations returned: " + result.size());
            for (int i = 0; i < result.size(); i++) {
                ReferByTranslation t = result.get(i);
                System.out.println("Translation " + i + ": ID=" + t.getId().getReferId() + 
                                 ", Description=" + t.getReferByDescription());
            }
            System.out.println("=== END REFERRAL CONTROLLER DEBUG ===");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get refer by translations: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Get Referral Doctors", description = "Retrieve all referral doctors for a specific language")
    @GetMapping
    public ResponseEntity<?> getReferralDoctors(@RequestParam Integer languageId) {
        try {
            List<ReferralDoctor> result = referralService.getReferralDoctors(languageId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get referral doctors: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Get Referral Doctor by Mobile", description = "Check if referral doctor exists by mobile number")
    @GetMapping("/by-mobile")
    public ResponseEntity<?> getReferralDoctorByMobile(@RequestParam String mobile) {
        try {
            Map<String, Object> result = referralService.getReferralDoctorDetailsForMobile(mobile);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get referral doctor by mobile: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Search Referral Doctors", description = "Search referral doctors by name")
    @GetMapping("/search")
    public ResponseEntity<?> searchReferralDoctors(@RequestParam String q) {
        try {
            List<ReferralDoctor> result = referralService.searchReferralDoctors(q);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search referral doctors: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Get Referral Doctor Details", description = "Get detailed information about a specific referral doctor")
    @GetMapping("/{rdId}")
    public ResponseEntity<?> getReferralDoctorDetails(@PathVariable Integer rdId, @RequestParam Integer languageId) {
        try {
            ReferralDoctor result = referralService.getReferralDoctorDetails(rdId, languageId);
            if (result != null) {
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Referral doctor not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get referral doctor details: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Save Referral Doctor", description = "Save or update a referral doctor")
    @PostMapping
    public ResponseEntity<?> saveReferralDoctor(@RequestBody ReferralDoctor referralDoctor) {
        try {
            // Validate required fields
            if (referralDoctor.getDoctorName() == null || referralDoctor.getDoctorName().trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Doctor name is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (referralDoctor.getReferId() == null || referralDoctor.getReferId().trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Refer ID is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Set default values
            if (referralDoctor.getDeleteFlag() == null) {
                referralDoctor.setDeleteFlag(false);
            }
            
            ReferralDoctor result = referralService.saveReferralDoctor(referralDoctor);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to save referral doctor: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}