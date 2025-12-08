package com.climasys.patients.web;

import com.climasys.patients.exception.AreaValidationException;
import com.climasys.patients.exception.GenderValidationException;
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

            // Validate area_id, city_id, state_id, country_id combination exists in area_master (using id column)
            Integer areaId = req.areaId();
            String cityId = req.cityId();
            String stateId = req.stateId();
            String countryId = req.countryId();
            
            // Area ID is mandatory - check if provided
            if (areaId == null) {
                throw new AreaValidationException("Area ID is required and cannot be null");
            }
            
            // Validate area combination exists in area_master table
            if (cityId != null && stateId != null && countryId != null) {
                String areaValidationSql = "SELECT COUNT(*) FROM area_master WHERE id = ? AND city_id = ? AND state_id = ? AND country_id = ?";
                Integer count = jdbcTemplate.queryForObject(areaValidationSql, Integer.class, areaId, cityId, stateId, countryId);
                
                if (count == null || count == 0) {
                    throw new AreaValidationException(areaId, cityId, stateId, countryId);
                }
            } else {
                throw new AreaValidationException("City ID, State ID, and Country ID are required when Area ID is provided");
            }

            // Validate gender_id exists in gender_master table
            String genderId = req.gender();
            if (genderId == null || genderId.trim().isEmpty()) {
                throw new GenderValidationException("Gender is required and cannot be null or empty", true);
            }
            
            String genderValidationSql = "SELECT COUNT(*) FROM gender_master WHERE id = ?";
            Integer genderCount = jdbcTemplate.queryForObject(genderValidationSql, Integer.class, genderId);
            
            if (genderCount == null || genderCount == 0) {
                throw new GenderValidationException(genderId);
            }

            // Check for duplicate patient (matching stored procedure logic)
            String duplicateCheckSql = "SELECT ID FROM patient_master " +
                    "WHERE last_name = ? AND first_name = ? AND gender_id = ? AND clinic_id = ?";
            
            List<Map<String, Object>> existingPatients = jdbcTemplate.queryForList(duplicateCheckSql,
                    req.lastName(), req.firstName(), genderId, req.clinicId());
            
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
                    areaId, // Use validated areaId
                    req.cityId(),
                    req.stateId(),
                    req.countryId(),
                    req.dob() != null ? java.sql.Date.valueOf(req.dob()) : null,
                    req.age() != null ? Short.valueOf(req.age()) : null,
                    genderId,
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
                    "pm.marital_status_id, " +
                    "pm.refer_id, " +
                    "pm.refer_doctor_details, " +
                    "pm.doctor_address, " +
                    "pm.doctor_mobile, " +
                    "pm.doctor_email " +
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
                    "emergency_number = ?, refer_id = ?, refer_doctor_details = ?, " +
                    "modified_on = ?, modifiedby_name = ? " +
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
                    req.referBy(),
                    req.referDoctorDetails(),
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
    public ResponseEntity<?> getPreviousVisitDates(
            @PathVariable String id,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String clinicId) {
        // This endpoint is specifically for the "Last Visit" column in the appointment screen
        // It should only return completed visits (not waiting, in-progress, cancelled, etc.)
        // It includes today's completed visits if the patient has completed a visit today
        try {
            System.out.println("DEBUG - getPreviousVisitDates called with patient ID: " + id + 
                ", doctorId: " + doctorId + ", clinicId: " + clinicId);
            
            // First, dynamically determine the correct status IDs based on doctor and clinic
            String completedStatusId = null;
            String excludedStatusIds = "4, 5, 11, 12"; // Default exclusions
            
            if (doctorId != null && clinicId != null) {
                // Try to find the "Completed" status for this doctor and clinic
                String statusQuery = "SELECT id FROM status_ref WHERE " +
                        "status_description ILIKE '%complete%' " +
                        "AND (doctor_id = ? OR doctor_id IS NULL) " +
                        "AND (clinic_id = ? OR clinic_id IS NULL) " +
                        "ORDER BY CASE WHEN doctor_id = ? THEN 1 ELSE 2 END, " +
                        "CASE WHEN clinic_id = ? THEN 1 ELSE 2 END " +
                        "LIMIT 1";
                
                try {
                    List<Map<String, Object>> statusResult = jdbcTemplate.queryForList(statusQuery, doctorId, clinicId, doctorId, clinicId);
                    if (!statusResult.isEmpty()) {
                        completedStatusId = statusResult.get(0).get("id").toString();
                        System.out.println("DEBUG - Found completed status ID: " + completedStatusId + " for doctor: " + doctorId + ", clinic: " + clinicId);
                    }
                } catch (Exception e) {
                    System.out.println("DEBUG - Could not find completed status, using default filtering: " + e.getMessage());
                }
                
                // Also get excluded status IDs (cancelled, no-show, etc.)
                String excludedStatusQuery = "SELECT id FROM status_ref WHERE " +
                        "(status_description ILIKE '%cancel%' OR status_description ILIKE '%no%show%' OR status_description ILIKE '%invalid%') " +
                        "AND (doctor_id = ? OR doctor_id IS NULL) " +
                        "AND (clinic_id = ? OR clinic_id IS NULL)";
                
                try {
                    List<Map<String, Object>> excludedResult = jdbcTemplate.queryForList(excludedStatusQuery, doctorId, clinicId);
                    if (!excludedResult.isEmpty()) {
                        excludedStatusIds = excludedResult.stream()
                            .map(row -> row.get("id").toString())
                            .collect(java.util.stream.Collectors.joining(","));
                        System.out.println("DEBUG - Found excluded status IDs: " + excludedStatusIds);
                    }
                } catch (Exception e) {
                    System.out.println("DEBUG - Could not find excluded statuses, using default: " + e.getMessage());
                }
            }
            
            // Build the query with dynamic status filtering
            // For "Last Visit" column, we ALWAYS want to show only completed visits
            String statusFilter;
            if (completedStatusId != null) {
                // If we found a completed status, only show completed visits
                statusFilter = "AND pv.status_id = " + completedStatusId;
                System.out.println("DEBUG - Using completed status filter: status_id = " + completedStatusId);
            } else {
                // If no completed status found, try to find it with broader search
                System.out.println("DEBUG - No completed status found, trying broader search...");
                
                // Try broader search for completed status
                String broaderStatusQuery = "SELECT id FROM status_ref WHERE " +
                        "(status_description ILIKE '%complete%' OR status_description ILIKE '%finish%' OR status_description ILIKE '%done%') " +
                        "ORDER BY id LIMIT 1";
                
                try {
                    List<Map<String, Object>> broaderResult = jdbcTemplate.queryForList(broaderStatusQuery);
                    if (!broaderResult.isEmpty()) {
                        completedStatusId = broaderResult.get(0).get("id").toString();
                        statusFilter = "AND pv.status_id = " + completedStatusId;
                        System.out.println("DEBUG - Found completed status with broader search: " + completedStatusId);
                    } else {
                        // Last resort: exclude only clearly non-completed statuses
                        statusFilter = "AND pv.status_id NOT IN (" + excludedStatusIds + ")";
                        System.out.println("DEBUG - No completed status found, using exclusion filter: " + excludedStatusIds);
                    }
                } catch (Exception e) {
                    System.out.println("DEBUG - Broader search failed, using exclusion filter: " + e.getMessage());
                    statusFilter = "AND pv.status_id NOT IN (" + excludedStatusIds + ")";
                }
            }
            
            String sql = "SELECT " +
                    "pv.visit_date, " +
                    "pv.visit_time, " +
                    "pv.patient_visit_no, " +
                    "pv.doctor_id, " +
                    "pv.clinic_id, " +
                    "pv.status_id, " +
                    "pv.shift_id, " +
                    "pv.patient_last_visit_no " +
                    "FROM patient_visits pv " +
                    "WHERE pv.patient_id = (SELECT id FROM patient_master WHERE id = ? OR folder_no = ?) " +
                    "AND pv.delete_flag = false " +
                    statusFilter + " " +
                    "ORDER BY pv.visit_date DESC, pv.visit_time DESC " + // Sort newest first so first item is the latest completed visit
                    "LIMIT 10"; // Get last 10 valid previous visits

            System.out.println("DEBUG - Simplified Query: " + sql);
            System.out.println("DEBUG - Parameters: patient_id=" + id + ", folder_no=" + id);

            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, id, id);
            
            System.out.println("DEBUG - Found " + result.size() + " completed visits for patient " + id + " (including today's if completed)");
            
            // Debug: Print the actual results
            for (int i = 0; i < Math.min(result.size(), 5); i++) {
                Map<String, Object> row = result.get(i);
                System.out.println("DEBUG - Visit " + i + ": " + 
                    "Date=" + row.get("visit_date") + 
                    ", Time=" + row.get("visit_time") + 
                    ", VisitNo=" + row.get("patient_visit_no") + 
                    ", Status=" + row.get("status_id") + 
                    ", Doctor=" + row.get("doctor_id") +
                    ", PatientId=" + id);
            }
            
            // Additional debug: Check if all patients are getting the same date
            if (!result.isEmpty()) {
                Map<String, Object> firstVisit = result.get(0);
                System.out.println("DEBUG - First visit date for patient " + id + ": " + firstVisit.get("visit_date"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("visits", result);
            response.put("total_visits", result.size());
            response.put("patient_id", id);
            response.put("uses_direct_query", true);
            response.put("completed_status_id", completedStatusId);
            response.put("status_filter_used", statusFilter);
            response.put("doctor_id", doctorId);
            response.put("clinic_id", clinicId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("ERROR - getPreviousVisitDates failed: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get previous visit dates: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}/visits/debug")
    public ResponseEntity<?> debugPatientVisits(@PathVariable String id) {
        try {
            System.out.println("DEBUG - debugPatientVisits called with patient ID: " + id);
            
            // Get all visits for this patient (no filtering) to see what's actually in the DB
            String sql = "SELECT " +
                    "pv.visit_date, " +
                    "pv.visit_time, " +
                    "pv.patient_visit_no, " +
                    "pv.doctor_id, " +
                    "pv.clinic_id, " +
                    "pv.status_id, " +
                    "pv.shift_id, " +
                    "pv.patient_last_visit_no, " +
                    "pv.delete_flag " +
                    "FROM patient_visits pv " +
                    "WHERE pv.patient_id = (SELECT id FROM patient_master WHERE id = ? OR folder_no = ?) " +
                    "ORDER BY pv.visit_date DESC, pv.visit_time DESC " +
                    "LIMIT 20";

            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, id, id);
            
            System.out.println("DEBUG - Found " + result.size() + " total visits for patient " + id);
            
            // Debug: Print all visits
            for (int i = 0; i < result.size(); i++) {
                Map<String, Object> row = result.get(i);
                System.out.println("DEBUG - Visit " + i + ": " + 
                    "Date=" + row.get("visit_date") + 
                    ", Time=" + row.get("visit_time") + 
                    ", VisitNo=" + row.get("patient_visit_no") + 
                    ", Status=" + row.get("status_id") + 
                    ", Doctor=" + row.get("doctor_id") +
                    ", DeleteFlag=" + row.get("delete_flag") +
                    ", LastVisitNo=" + row.get("patient_last_visit_no"));
            }
            
            // Also get status breakdown
            String statusSql = "SELECT " +
                    "pv.status_id, " +
                    "COUNT(*) as count " +
                    "FROM patient_visits pv " +
                    "WHERE pv.patient_id = (SELECT id FROM patient_master WHERE id = ? OR folder_no = ?) " +
                    "AND pv.delete_flag = false " +
                    "GROUP BY pv.status_id " +
                    "ORDER BY pv.status_id";
            
            List<Map<String, Object>> statusResult = jdbcTemplate.queryForList(statusSql, id, id);
            System.out.println("DEBUG - Status breakdown for patient " + id + ":");
            for (Map<String, Object> statusRow : statusResult) {
                System.out.println("DEBUG - Status " + statusRow.get("status_id") + ": " + statusRow.get("count") + " visits");
            }
            
            // Also get status descriptions for the status IDs found
            if (!statusResult.isEmpty()) {
                String statusIds = statusResult.stream()
                    .map(row -> row.get("status_id").toString())
                    .collect(java.util.stream.Collectors.joining(","));
                
                String statusDescSql = "SELECT id, status_description FROM status_ref WHERE id IN (" + statusIds + ")";
                List<Map<String, Object>> statusDescResult = jdbcTemplate.queryForList(statusDescSql);
                System.out.println("DEBUG - Status descriptions for patient " + id + ":");
                for (Map<String, Object> descRow : statusDescResult) {
                    System.out.println("DEBUG - Status ID " + descRow.get("id") + ": " + descRow.get("status_description"));
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("all_visits", result);
            response.put("status_breakdown", statusResult);
            response.put("total_visits", result.size());
            response.put("patient_id", id);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("ERROR - debugPatientVisits failed: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to debug patient visits: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/status-ref")
    public ResponseEntity<?> getStatusRefData() {
        try {
            System.out.println("DEBUG - Getting status_ref table data");
            
            // Get all status references to see what's actually in the database
            String sql = "SELECT id, status_description, clinic_id, doctor_id FROM status_ref ORDER BY id";
            
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            
            System.out.println("DEBUG - Found " + result.size() + " status references");
            
            // Debug: Print all status references
            for (Map<String, Object> row : result) {
                System.out.println("DEBUG - Status ID " + row.get("id") + ": " + 
                    row.get("status_description") + 
                    " (Clinic: " + row.get("clinic_id") + 
                    ", Doctor: " + row.get("doctor_id") + ")");
            }
            
            // Also get what status IDs are actually used in patient_visits
            String visitsStatusSql = "SELECT status_id, COUNT(*) as count FROM patient_visits WHERE delete_flag = false GROUP BY status_id ORDER BY status_id";
            List<Map<String, Object>> visitsStatusResult = jdbcTemplate.queryForList(visitsStatusSql);
            
            System.out.println("DEBUG - Status IDs actually used in patient_visits:");
            for (Map<String, Object> row : visitsStatusResult) {
                System.out.println("DEBUG - Status ID " + row.get("status_id") + ": " + row.get("count") + " visits");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status_ref", result);
            response.put("visits_status_usage", visitsStatusResult);
            response.put("total_statuses", result.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("ERROR - getStatusRefData failed: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get status_ref data: " + e.getMessage());
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


