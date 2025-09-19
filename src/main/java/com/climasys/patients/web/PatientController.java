package com.climasys.patients.web;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_Insert_PatientRegistration");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("DoctorId", req.doctorId());
            parameters.put("LastName", req.lastName());
            parameters.put("MiddleName", req.middleName());
            parameters.put("FirstName", req.firstName());
            parameters.put("Mobile", req.mobile());
            parameters.put("AreaId", req.areaId());
            parameters.put("CityId", req.cityId());
            parameters.put("StateId", req.stateId());
            parameters.put("CountryId", req.countryId());
            parameters.put("DOB", req.dob());
            parameters.put("Age", req.age());
            parameters.put("Gender", req.gender());
            parameters.put("RegYear", req.regYear());
            parameters.put("FamilyFolder", req.familyFolder());
            parameters.put("RegistrationStatus", req.registrationStatus());
            parameters.put("UserId", req.userId());
            parameters.put("ReferBy", req.referBy());
            parameters.put("ReferDoctorDetails", req.referDoctorDetails());
            parameters.put("MaritalStatus", req.maritalStatus());
            parameters.put("Occupation", req.occupation());
            parameters.put("Address", req.address());
            parameters.put("PatientEmail", req.patientEmail());
            parameters.put("DoctorAddress", req.doctorAddress());
            parameters.put("DoctorMobile", req.doctorMobile());
            parameters.put("DoctorEmail", req.doctorEmail());
            parameters.put("ClinicId", req.clinicId());

            Map<String, Object> result = jdbcCall.execute(parameters);
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
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_Get_Patient_Details");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("PatientId", id);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get patient details: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable String id, @RequestBody FullRegistrationRequest req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_Save_FullRegistrationDetails");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("PatientId", id);
            parameters.put("DoctorId", req.doctorId());
            parameters.put("LastName", req.lastName());
            parameters.put("MiddleName", req.middleName());
            parameters.put("FirstName", req.firstName());
            parameters.put("Mobile", req.mobile());
            parameters.put("AreaId", req.areaId());
            parameters.put("CityId", req.cityId());
            parameters.put("StateId", req.stateId());
            parameters.put("CountryId", req.countryId());
            parameters.put("DOB", req.dob());
            parameters.put("Age", req.age());
            parameters.put("Gender", req.gender());
            parameters.put("RegYear", req.regYear());
            parameters.put("FamilyFolder", req.familyFolder());
            parameters.put("RegistrationStatus", req.registrationStatus());
            parameters.put("UserId", req.userId());
            parameters.put("ReferBy", req.referBy());
            parameters.put("ReferDoctorDetails", req.referDoctorDetails());
            parameters.put("MaritalStatus", req.maritalStatus());
            parameters.put("Occupation", req.occupation());
            parameters.put("Address", req.address());
            parameters.put("PatientEmail", req.patientEmail());
            parameters.put("DoctorAddress", req.doctorAddress());
            parameters.put("DoctorMobile", req.doctorMobile());
            parameters.put("DoctorEmail", req.doctorEmail());
            parameters.put("ClinicId", req.clinicId());
            parameters.put("BloodGroup", req.bloodGroup());
            parameters.put("EmergencyContact", req.emergencyContact());
            parameters.put("EmergencyPhone", req.emergencyPhone());
            parameters.put("Allergies", req.allergies());
            parameters.put("MedicalHistory", req.medicalHistory());
            parameters.put("FamilyHistory", req.familyHistory());

            Map<String, Object> result = jdbcCall.execute(parameters);
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
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetFamilyFolderDetails");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("PatientId", id);

            Map<String, Object> result = jdbcCall.execute(parameters);
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
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_Get_Patient_MaxVisit_No");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("PatientId", id);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
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
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_Search_PatientPending_Details");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("SearchQuery", query);
            parameters.put("Status", status);
            parameters.put("PageNumber", page);
            parameters.put("PageSize", size);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to search patients: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}/visits/dates")
    public ResponseEntity<?> getPreviousVisitDates(@PathVariable String id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetPreviousVisitDates");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("PatientId", id);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
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
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_Check_Patient_Id");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("PatientId", patientId);
            parameters.put("VisitDate", date);
            parameters.put("ShiftId", shiftId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to check patient ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}


