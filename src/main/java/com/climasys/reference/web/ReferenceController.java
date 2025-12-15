package com.climasys.reference.web;

import com.climasys.service.ReferenceDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reference")
@Tag(name = "Reference Data", description = "Reference data endpoints for genders, blood groups, and other lookup values")
public class ReferenceController {

    private final ReferenceDataService referenceDataService;

    @Autowired
    public ReferenceController(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    @Operation(summary = "Get Gender Data", description = "Retrieve all available gender options from the database")
    @GetMapping("/genders")
    public ResponseEntity<?> getGenders() {
        try {
            List<?> result = referenceDataService.getGenders();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get gender data: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Get Blood Group Data", description = "Retrieve all available blood group options from the database")
    @GetMapping("/blood-groups")
    public ResponseEntity<?> getBloodGroups() {
        try {
            List<?> result = referenceDataService.getBloodGroups();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get blood group data: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/impressions")
    public ResponseEntity<?> getImpressions(@RequestParam String doctorId,
            @RequestParam(required = false) String clinicId) {
        try {
            List<?> result = referenceDataService.getImpressions(doctorId, clinicId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get impression data: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/areas/{id}/name")
    public ResponseEntity<?> getAreaName(@PathVariable String id) {
        try {
            Map<String, Object> result = referenceDataService.getAreaName(Integer.parseInt(id));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get area name: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/areas/search")
    public ResponseEntity<?> searchAreas(@RequestParam String query) {
        try {
            List<?> result = referenceDataService.searchAreas(query);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search areas: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/areas/search-advanced")
    public ResponseEntity<?> searchAreasAdvanced(
            @RequestParam String searchStr,
            @RequestParam Integer languageId) {
        try {
            Map<String, Object> result = referenceDataService.searchAreasAdvanced(searchStr, languageId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search areas: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/areas/details")
    public ResponseEntity<?> getAreaDetails(
            @RequestParam String areaName,
            @RequestParam Integer languageId) {
        try {
            Map<String, Object> result = referenceDataService.getAreaDetails(areaName, languageId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get area details: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/folders/check")
    public ResponseEntity<?> checkFolderNumber(@RequestParam String folderNo) {
        try {
            Map<String, Object> result = referenceDataService.checkFolderNumber(folderNo);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to check folder number: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/clinics/{clinicId}/shifts")
    public ResponseEntity<?> getClinicShifts(
            @PathVariable String clinicId,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String day) {
        try {
            List<?> result = referenceDataService.getClinicShifts(clinicId, doctorId, day);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get clinic shifts: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/doctors")
    public ResponseEntity<?> getDoctors(@RequestParam(required = false) String clinicId) {
        try {
            List<?> result = referenceDataService.getDoctors(clinicId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get doctors: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/clinics")
    public ResponseEntity<?> getClinics() {
        try {
            List<?> result = referenceDataService.getClinics();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get clinics: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/occupations")
    public ResponseEntity<?> getOccupations() {
        try {
            List<?> result = referenceDataService.getOccupations();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get occupations: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Get Marital Statuses", description = "Retrieve all available marital status options")
    @GetMapping("/marital-statuses")
    public ResponseEntity<?> getMaritalStatuses() {
        try {
            List<?> result = referenceDataService.getMaritalStatuses();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get marital statuses: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Get Marital Statuses with Translations", description = "Retrieve all available marital status options with translations for a specific language")
    @GetMapping("/marital-statuses/translations")
    public ResponseEntity<?> getMaritalStatusesWithTranslations(
            @RequestParam(defaultValue = "1") Integer languageId) {
        try {
            List<Map<String, Object>> result = referenceDataService.getMaritalStatusesWithTranslations(languageId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("maritalStatuses", result);
            response.put("languageId", languageId);
            response.put("count", result.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to get marital statuses with translations: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/cities")
    public ResponseEntity<?> getCities(@RequestParam(required = false) String stateId) {
        try {
            List<?> result = referenceDataService.getCities(stateId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get cities: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/cities/search")
    public ResponseEntity<?> searchCities(
            @RequestParam String searchStr,
            @RequestParam Integer languageId) {
        try {
            List<?> result = referenceDataService.searchCities(searchStr, languageId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search cities: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/states")
    public ResponseEntity<?> getStates(
            @RequestParam(required = false) String countryId,
            @RequestParam(required = false, defaultValue = "1") Integer languageId) {
        try {
            List<?> result = referenceDataService.getStates(countryId, 1);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get states: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/countries")
    public ResponseEntity<?> getCountries() {
        try {
            List<?> result = referenceDataService.getCountries();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get countries: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/areas")
    public ResponseEntity<?> getAreas(
            @RequestParam String cityId,
            @RequestParam String stateId,
            @RequestParam(required = false, defaultValue = "1") Integer languageId) {
        try {
            List<?> result = referenceDataService.getAreas(cityId, stateId, languageId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get areas: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // =====================================================
    // NEW ENDPOINTS FOR USP_Get_BloodGroupDetails REPLACEMENT
    // =====================================================

    @Operation(summary = "Get Payment Methods", description = "Retrieve all available payment method options")
    @GetMapping("/payment-methods")
    public ResponseEntity<?> getPaymentMethods() {
        try {
            List<?> result = referenceDataService.getPaymentMethods();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get payment methods: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Get Titles", description = "Retrieve all available title options")
    @GetMapping("/titles")
    public ResponseEntity<?> getTitles() {
        try {
            List<?> result = referenceDataService.getTitles();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get titles: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Get Follow-up Types", description = "Retrieve all available follow-up type options")
    @GetMapping("/follow-up-types")
    public ResponseEntity<?> getFollowUpTypes() {
        try {
            List<?> result = referenceDataService.getFollowUpTypes();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get follow-up types: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Get Follow-up After Options", description = "Retrieve all available follow-up after period options")
    @GetMapping("/followup-after")
    public ResponseEntity<?> getFollowupAfterOptions() {
        try {
            List<?> result = referenceDataService.getFollowupAfterOptions();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get follow-up after options: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Get All Reference Data", description = "Retrieve all reference data in the same format as USP_Get_BloodGroupDetails stored procedure")
    @GetMapping("/all-reference-data")
    public ResponseEntity<?> getAllReferenceData() {
        try {
            Map<String, Object> result = referenceDataService.getAllReferenceData();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get all reference data: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
