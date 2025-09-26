package com.climasys.patients.web;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public ResponseEntity<?> quickRegister(@RequestBody QuickRegistrationRequest req) {
        try {
            // Data validation and NULL handling (matching stored procedure logic)
            String maritalStatus = req.maritalStatus();
            if (maritalStatus != null && maritalStatus.trim().isEmpty()) {
                maritalStatus = null;
            }
            
            Integer occupation = req.occupation();
            if (occupation != null && occupation == 0) {
                occupation = null;
            }

            // Check for duplicate patient (matching stored procedure logic)
            String duplicateCheckSql = "SELECT ID FROM patient_master " +
                    "WHERE last_name = ? AND first_name = ? AND gender_id = ? AND clinic_id = ?";
            
            List<Map<String, Object>> existingPatients = jdbcTemplate.queryForList(duplicateCheckSql,
                    req.lastName(), req.firstName(), req.gender(), req.clinicId());
            
            if (!existingPatients.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("SAVE_STATUS", 0);
                result.put("message", "Duplicate patient found");
                return ResponseEntity.ok(result);
            }

            // Generate patient ID using sequence numbers (matching stored procedure logic)
            String patientId = generatePatientId(req.clinicId());
            if (patientId == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Failed to generate patient ID");
                return ResponseEntity.badRequest().body(error);
            }

            // Insert patient record
            String sql = "INSERT INTO patient_master (" +
                    "id, doctor_id, folder_no, first_name, middle_name, last_name, " +
                    "mobile_1, area_id, city_id, state_id, country_id, date_of_birth, " +
                    "age_given, gender_id, manual_registration_year, registration_status, " +
                    "marital_status_id, occupation_id, address_1, email_id, " +
                    "doctor_address, doctor_mobile, doctor_email, clinic_id, " +
                    "date_of_registration, created_on, createdby_name, modified_on, modifiedby_name, " +
                    "patient_last_visit_no, refer_id, refer_doctor_details" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(sql,
                    patientId,
                    req.doctorId(),
                    null, // folder_no - set to NULL since not required
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
                    maritalStatus,
                    occupation,
                    req.address(),
                    req.patientEmail(),
                    req.doctorAddress(),
                    req.doctorMobile(),
                    req.doctorEmail(),
                    req.clinicId(),
                    java.sql.Date.valueOf(java.time.LocalDate.now()), // date_of_registration
                    java.time.LocalDateTime.now(), // created_on
                    req.userId(), // createdby_name
                    java.time.LocalDateTime.now(), // modified_on
                    req.userId(), // modifiedby_name
                    0, // patient_last_visit_no
                    req.referBy(),
                    req.referDoctorDetails()
            );

            // Return response matching stored procedure format
            Map<String, Object> result = new HashMap<>();
            result.put("SAVE_STATUS", 1);
            result.put("ID", patientId);
            result.put("message", "Patient registered successfully");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("ErrorNumber", -1);
            error.put("ErrorMessage", "Failed to register patient: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private String generatePatientId(String clinicId) {
        try {
            // Get sequence number for PAT entity type
            String sequenceSql = "SELECT last_sequenceno, prefix_char, total_length " +
                    "FROM sequence_nos WHERE clinic_id = ? AND entity_type = 'PAT'";
            
            List<Map<String, Object>> sequenceResult = jdbcTemplate.queryForList(sequenceSql, clinicId);
            
            if (sequenceResult.isEmpty()) {
                // Create default sequence entry if not exists
                String insertSequenceSql = "INSERT INTO sequence_nos " +
                        "(doctor_id, entity_type, entity_name, prefix_char, total_length, last_sequenceno, clinic_id) " +
                        "VALUES (?, 'PAT', 'PATIENT', '', 5, 0, ?)";
                jdbcTemplate.update(insertSequenceSql, "DEFAULT", clinicId);
                
                // Retry getting sequence
                sequenceResult = jdbcTemplate.queryForList(sequenceSql, clinicId);
                if (sequenceResult.isEmpty()) {
                    return null;
                }
            }
            
            Map<String, Object> sequenceData = sequenceResult.get(0);
            Long lastSequenceNo = ((Number) sequenceData.get("last_sequenceno")).longValue();
            Integer totalLength = ((Number) sequenceData.get("total_length")).intValue();
            
            // Increment sequence number
            lastSequenceNo = lastSequenceNo + 1;
            
            // Generate patient ID in format: DD-MM-YYYY-XXXXX
            java.time.LocalDate today = java.time.LocalDate.now();
            String dateStr = String.format("%02d-%02d-%04d", 
                    today.getDayOfMonth(), today.getMonthValue(), today.getYear());
            
            // Pad sequence number with zeros
            String paddedSequence = String.format("%0" + totalLength + "d", lastSequenceNo);
            String patientId = dateStr + "-" + paddedSequence;
            
            // Update sequence number
            String updateSequenceSql = "UPDATE sequence_nos SET last_sequenceno = ? " +
                    "WHERE clinic_id = ? AND entity_type = 'PAT'";
            jdbcTemplate.update(updateSequenceSql, lastSequenceNo, clinicId);
            
            return patientId;
        } catch (Exception e) {
            return null;
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
                    null, // folder_no - set to NULL since not required
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


