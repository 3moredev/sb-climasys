package com.climasys.reference.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reference")
@Tag(name = "Reference Data", description = "Reference data endpoints for genders, blood groups, and other lookup values")
public class ReferenceController {

    private final JdbcTemplate jdbcTemplate;

    public ReferenceController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Operation(summary = "Get Gender Data", description = "Retrieve all available gender options from the database")
    @GetMapping("/genders")
    public ResponseEntity<?> getGenders() {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetGenderData");

            Map<String, Object> result = jdbcCall.execute();
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
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetBloodGroup_Details");

            Map<String, Object> result = jdbcCall.execute();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get blood group data: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/impressions")
    public ResponseEntity<?> getImpressions(@RequestParam String doctorId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetImpressionFinding_Details");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("DoctorId", doctorId);

            Map<String, Object> result = jdbcCall.execute(parameters);
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
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetAreaName_By_AreaId");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("AreaId", id);

            Map<String, Object> result = jdbcCall.execute(parameters);
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
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetAreaDetails_by_QuickRegistration");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("SearchQuery", query);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search areas: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/folders/check")
    public ResponseEntity<?> checkFolderNumber(@RequestParam String folderNo) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_Check_Folder_No");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FolderNo", folderNo);

            Map<String, Object> result = jdbcCall.execute(parameters);
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
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetClinicShifts");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ClinicId", clinicId);
            if (doctorId != null) parameters.put("DoctorId", doctorId);
            if (day != null) parameters.put("Day", day);

            Map<String, Object> result = jdbcCall.execute(parameters);
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
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetDoctors");

            Map<String, Object> parameters = new HashMap<>();
            if (clinicId != null) parameters.put("ClinicId", clinicId);

            Map<String, Object> result = jdbcCall.execute(parameters);
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
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetClinics");

            Map<String, Object> result = jdbcCall.execute();
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
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetOccupations");

            Map<String, Object> result = jdbcCall.execute();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get occupations: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/marital-statuses")
    public ResponseEntity<?> getMaritalStatuses() {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetMaritalStatuses");

            Map<String, Object> result = jdbcCall.execute();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get marital statuses: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/cities")
    public ResponseEntity<?> getCities(@RequestParam(required = false) String stateId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetCities");

            Map<String, Object> parameters = new HashMap<>();
            if (stateId != null) parameters.put("StateId", stateId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get cities: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/states")
    public ResponseEntity<?> getStates(@RequestParam(required = false) String countryId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetStates");

            Map<String, Object> parameters = new HashMap<>();
            if (countryId != null) parameters.put("CountryId", countryId);

            Map<String, Object> result = jdbcCall.execute(parameters);
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
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetCountries");

            Map<String, Object> result = jdbcCall.execute();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get countries: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
