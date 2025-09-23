package com.climasys.patients.web;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final JdbcTemplate jdbcTemplate;

    public PatientController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record QuickRegistrationRequest(
            @NotBlank String doctorId,
            @NotBlank String lastName,
            String middleName,
            @NotBlank String firstName,
            @NotBlank String mobile,
            Integer areaId,
            String cityId,
            String stateId,
            String countryId,
            String dob,
            String age,
            String gender,
            String regYear,
            String familyFolder,
            String registrationStatus,
            String userId,
            String referBy,
            String referDoctorDetails,
            String maritalStatus,
            Integer occupation,
            String address,
            String patientEmail,
            String doctorAddress,
            String doctorMobile,
            String doctorEmail,
            String clinicId
    ) {}

    @PostMapping
    public ResponseEntity<?> quickRegister(@RequestBody QuickRegistrationRequest req) {
        try {
            // Generate a unique patient ID
            String patientId = "P" + System.currentTimeMillis();
            
            // Use direct SQL INSERT instead of stored procedure for PostgreSQL compatibility
            String sql = "INSERT INTO patient_master (" +
                    "id, doctor_id, folder_no, first_name, middle_name, last_name, " +
                    "mobile_1, area_id, city_id, state_id, country_id, date_of_birth, " +
                    "age_given, gender_id, manual_registration_year, registration_status, " +
                    "marital_status_id, occupation_id, address_1, email_id, " +
                    "doctor_address, doctor_mobile, doctor_email, clinic_id, " +
                    "date_of_registration, created_on, createdby_name" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            int rowsAffected = jdbcTemplate.update(sql,
                    patientId,
                    req.doctorId(),
                    req.familyFolder(), // folder_no
                    req.firstName(),
                    req.middleName(),
                    req.lastName(),
                    req.mobile(),
                    req.areaId(),
                    req.cityId(),
                    req.stateId(),
                    req.countryId(),
                    req.dob() != null ? java.sql.Date.valueOf(req.dob()) : null,
                    req.age() != null ? Short.valueOf(req.age()) : null,
                    req.gender(),
                    req.regYear() != null ? Integer.valueOf(req.regYear()) : null,
                    req.registrationStatus(),
                    req.maritalStatus(),
                    req.occupation(),
                    req.address(),
                    req.patientEmail(),
                    req.doctorAddress(),
                    req.doctorMobile(),
                    req.doctorEmail(),
                    req.clinicId(),
                    java.sql.Date.valueOf(java.time.LocalDate.now()), // date_of_registration
                    java.time.LocalDateTime.now(), // created_on
                    req.userId() // createdby_name
            );

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("patientId", patientId);
            result.put("rowsAffected", rowsAffected);
            result.put("message", "Patient registered successfully");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to register patient: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    public record FullRegistrationRequest(
            @NotBlank String patientId,
            @NotBlank String doctorId,
            @NotBlank String lastName,
            String middleName,
            @NotBlank String firstName,
            @NotBlank String mobile,
            Integer areaId,
            String cityId,
            String stateId,
            String countryId,
            String dob,
            String age,
            String gender,
            String regYear,
            String familyFolder,
            String registrationStatus,
            String userId,
            String referBy,
            String referDoctorDetails,
            String maritalStatus,
            Integer occupation,
            String address,
            String patientEmail,
            String doctorAddress,
            String doctorMobile,
            String doctorEmail,
            String clinicId,
            String bloodGroup,
            String emergencyContact,
            String emergencyPhone,
            String allergies,
            String medicalHistory,
            String familyHistory
    ) {}

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatient(@PathVariable String id) {
        try {
            // Use direct SQL query instead of stored procedure for PostgreSQL compatibility
            String sql = "SELECT " +
                    "pm.id, " +
                    "pm.folder_no, " +
                    "pm.first_name || ' ' || COALESCE(pm.middle_name, '') || ' ' || pm.last_name AS full_name, " +
                    "pm.first_name, " +
                    "pm.middle_name, " +
                    "pm.last_name, " +
                    "pm.mobile_1, " +
                    "pm.mobile_2, " +
                    "pm.date_of_birth, " +
                    "pm.age_given, " +
                    "pm.gender_id, " +
                    "pm.address_1, " +
                    "pm.address_2, " +
                    "pm.email_id, " +
                    "pm.emergency_name, " +
                    "pm.emergency_number, " +
                    "pm.date_of_registration, " +
                    "pm.registration_status, " +
                    "pm.doctor_id, " +
                    "pm.clinic_id, " +
                    "pm.country_id, " +
                    "pm.state_id, " +
                    "pm.city_id, " +
                    "pm.area_id, " +
                    "pm.pincode, " +
                    "pm.occupation_id, " +
                    "pm.bloodgroup_id, " +
                    "pm.marital_status_id " +
                    "FROM patient_master pm " +
                    "WHERE pm.id = ? OR pm.folder_no = ?";

            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, id, id);
            
            if (result.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Patient not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(result.get(0));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get patient details: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable String id, @RequestBody FullRegistrationRequest req) {
        try {
            // Use direct SQL UPDATE instead of stored procedure for PostgreSQL compatibility
            String sql = "UPDATE patient_master SET " +
                    "doctor_id = ?, folder_no = ?, first_name = ?, middle_name = ?, last_name = ?, " +
                    "mobile_1 = ?, area_id = ?, city_id = ?, state_id = ?, country_id = ?, " +
                    "date_of_birth = ?, age_given = ?, gender_id = ?, manual_registration_year = ?, " +
                    "registration_status = ?, marital_status_id = ?, occupation_id = ?, " +
                    "address_1 = ?, email_id = ?, doctor_address = ?, doctor_mobile = ?, " +
                    "doctor_email = ?, clinic_id = ?, bloodgroup_id = ?, emergency_name = ?, " +
                    "emergency_number = ?, modified_on = ?, modifiedby_name = ? " +
                    "WHERE id = ? OR folder_no = ?";

            int rowsAffected = jdbcTemplate.update(sql,
                    req.doctorId(),
                    req.familyFolder(), // folder_no
                    req.firstName(),
                    req.middleName(),
                    req.lastName(),
                    req.mobile(),
                    req.areaId(),
                    req.cityId(),
                    req.stateId(),
                    req.countryId(),
                    req.dob() != null ? java.sql.Date.valueOf(req.dob()) : null,
                    req.age() != null ? Short.valueOf(req.age()) : null,
                    req.gender(),
                    req.regYear() != null ? Integer.valueOf(req.regYear()) : null,
                    req.registrationStatus(),
                    req.maritalStatus(),
                    req.occupation(),
                    req.address(),
                    req.patientEmail(),
                    req.doctorAddress(),
                    req.doctorMobile(),
                    req.doctorEmail(),
                    req.clinicId(),
                    req.bloodGroup(),
                    req.emergencyContact(),
                    req.emergencyPhone(),
                    java.time.LocalDateTime.now(), // modified_on
                    req.userId(), // modifiedby_name
                    id, // WHERE id = ?
                    id  // OR folder_no = ?
            );

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("patientId", id);
            result.put("rowsAffected", rowsAffected);
            result.put("message", rowsAffected > 0 ? "Patient updated successfully" : "No patient found with ID: " + id);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to update patient: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}/family")
    public ResponseEntity<?> getFamilyDetails(@PathVariable String id) {
        try {
            // Use direct SQL query instead of stored procedure for PostgreSQL compatibility
            String sql = "SELECT " +
                    "pm.id, " +
                    "pm.folder_no, " +
                    "pm.first_name || ' ' || COALESCE(pm.middle_name, '') || ' ' || pm.last_name AS full_name, " +
                    "pm.first_name, " +
                    "pm.middle_name, " +
                    "pm.last_name, " +
                    "pm.mobile_1, " +
                    "pm.date_of_birth, " +
                    "pm.gender_id, " +
                    "pm.relationship_to_main_patient, " +
                    "pm.family_folder_no " +
                    "FROM patient_master pm " +
                    "WHERE pm.family_folder_no = (SELECT family_folder_no FROM patient_master WHERE id = ? OR folder_no = ?) " +
                    "OR pm.id = ? OR pm.folder_no = ?";

            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, id, id, id, id);
            
            if (result.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "No family details found for patient ID: " + id);
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get family details: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}/max-visit")
    public ResponseEntity<?> getMaxVisitNumber(@PathVariable String id) {
        try {
            // Use direct SQL query instead of stored procedure for PostgreSQL compatibility
            String sql = "SELECT " +
                    "COALESCE(MAX(pv.visit_number), 0) AS max_visit_number, " +
                    "COUNT(pv.visit_number) AS total_visits " +
                    "FROM patient_visits pv " +
                    "WHERE pv.patient_id = (SELECT id FROM patient_master WHERE id = ? OR folder_no = ?)";

            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, id, id);
            
            if (result.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("max_visit_number", 0);
                response.put("total_visits", 0);
                response.put("patient_id", id);
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.ok(result.get(0));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get max visit number: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPatients(
            @RequestParam String query,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            // Use direct SQL query instead of stored procedure for PostgreSQL compatibility
            String baseSql = "SELECT " +
                    "pm.id, " +
                    "pm.folder_no, " +
                    "pm.first_name || ' ' || COALESCE(pm.middle_name, '') || ' ' || pm.last_name AS full_name, " +
                    "pm.first_name, " +
                    "pm.middle_name, " +
                    "pm.last_name, " +
                    "pm.mobile_1, " +
                    "pm.date_of_birth, " +
                    "pm.age_given, " +
                    "pm.gender_id, " +
                    "pm.registration_status, " +
                    "pm.date_of_registration " +
                    "FROM patient_master pm " +
                    "WHERE (LOWER(pm.first_name) LIKE LOWER(?) " +
                    "OR LOWER(pm.last_name) LIKE LOWER(?) " +
                    "OR LOWER(pm.mobile_1) LIKE LOWER(?) " +
                    "OR LOWER(pm.folder_no) LIKE LOWER(?) " +
                    "OR LOWER(pm.id) LIKE LOWER(?))";

            // Add status filter if not "all"
            if (!"all".equals(status)) {
                baseSql += " AND pm.registration_status = ?";
            }

            // Add pagination
            baseSql += " ORDER BY pm.date_of_registration DESC LIMIT ? OFFSET ?";

            String searchPattern = "%" + query + "%";
            List<Map<String, Object>> result;
            
            if (!"all".equals(status)) {
                result = jdbcTemplate.queryForList(baseSql, 
                    searchPattern, searchPattern, searchPattern, searchPattern, searchPattern, 
                    status, size, page * size);
            } else {
                result = jdbcTemplate.queryForList(baseSql, 
                    searchPattern, searchPattern, searchPattern, searchPattern, searchPattern, 
                    size, page * size);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("patients", result);
            response.put("total_count", result.size());
            response.put("page", page);
            response.put("size", size);
            response.put("query", query);
            response.put("status", status);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search patients: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}/visits/dates")
    public ResponseEntity<?> getPreviousVisitDates(@PathVariable String id) {
        try {
            // Use direct SQL query instead of stored procedure for PostgreSQL compatibility
            String sql = "SELECT " +
                    "pv.visit_date, " +
                    "pv.visit_time, " +
                    "pv.visit_number, " +
                    "pv.visit_id, " +
                    "pv.doctor_id, " +
                    "pv.clinic_id, " +
                    "pv.visit_status, " +
                    "pv.visit_type " +
                    "FROM patient_visits pv " +
                    "WHERE pv.patient_id = (SELECT id FROM patient_master WHERE id = ? OR folder_no = ?) " +
                    "ORDER BY pv.visit_date DESC, pv.visit_time DESC";

            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, id, id);
            
            if (result.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("visits", result);
                response.put("total_visits", 0);
                response.put("patient_id", id);
                return ResponseEntity.ok(response);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("visits", result);
            response.put("total_visits", result.size());
            response.put("patient_id", id);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get previous visit dates: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkPatientId(
            @RequestParam String patientId,
            @RequestParam String date,
            @RequestParam String shiftId) {
        try {
            // Use direct SQL query instead of stored procedure for PostgreSQL compatibility
            String sql = "SELECT " +
                    "pm.id, " +
                    "pm.folder_no, " +
                    "pm.first_name || ' ' || COALESCE(pm.middle_name, '') || ' ' || pm.last_name AS full_name, " +
                    "pm.registration_status, " +
                    "CASE WHEN pv.visit_id IS NOT NULL THEN true ELSE false END AS has_visit_on_date, " +
                    "pv.visit_id, " +
                    "pv.visit_number " +
                    "FROM patient_master pm " +
                    "LEFT JOIN patient_visits pv ON pm.id = pv.patient_id " +
                    "AND pv.visit_date = ? " +
                    "AND pv.shift_id = ? " +
                    "WHERE pm.id = ? OR pm.folder_no = ?";

            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, 
                java.sql.Date.valueOf(date), 
                Integer.valueOf(shiftId), 
                patientId, 
                patientId);
            
            if (result.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("patient_exists", false);
                response.put("message", "Patient not found with ID: " + patientId);
                return ResponseEntity.ok(response);
            }
            
            Map<String, Object> patientData = result.get(0);
            Map<String, Object> response = new HashMap<>();
            response.put("patient_exists", true);
            response.put("patient_data", patientData);
            response.put("has_visit_on_date", patientData.get("has_visit_on_date"));
            response.put("visit_date", date);
            response.put("shift_id", shiftId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to check patient ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}


