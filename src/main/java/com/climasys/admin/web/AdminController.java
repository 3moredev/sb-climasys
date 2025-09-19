package com.climasys.admin.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administration", description = "Administrative functions for user, medicine, and clinic management")
public class AdminController {

    private final JdbcTemplate jdbcTemplate;

    public AdminController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record UserRequest(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank String email,
            @NotBlank String role,
            @NotBlank String clinicId,
            String phone,
            String address,
            Boolean isActive
    ) {}

    public record MedicineRequest(
            @NotBlank String medicineName,
            @NotBlank String medicineCode,
            String description,
            String category,
            String unit,
            Double price,
            Integer stockQuantity,
            Integer reorderLevel,
            String manufacturer,
            Boolean isActive
    ) {}

    public record ClinicRequest(
            @NotBlank String clinicName,
            @NotBlank String clinicCode,
            String address,
            String phone,
            String email,
            String contactPerson,
            Boolean isActive
    ) {}

    // User Management
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserRequest req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_CreateUser");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("Username", req.username());
            parameters.put("Password", req.password());
            parameters.put("FirstName", req.firstName());
            parameters.put("LastName", req.lastName());
            parameters.put("Email", req.email());
            parameters.put("Role", req.role());
            parameters.put("ClinicId", req.clinicId());
            parameters.put("Phone", req.phone());
            parameters.put("Address", req.address());
            parameters.put("IsActive", req.isActive());

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create user: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String clinicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetUsers");

            Map<String, Object> parameters = new HashMap<>();
            if (role != null) parameters.put("Role", role);
            if (clinicId != null) parameters.put("ClinicId", clinicId);
            parameters.put("PageNumber", page);
            parameters.put("PageSize", size);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get users: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody UserRequest req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_UpdateUser");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("UserId", userId);
            parameters.put("Username", req.username());
            parameters.put("Password", req.password());
            parameters.put("FirstName", req.firstName());
            parameters.put("LastName", req.lastName());
            parameters.put("Email", req.email());
            parameters.put("Role", req.role());
            parameters.put("ClinicId", req.clinicId());
            parameters.put("Phone", req.phone());
            parameters.put("Address", req.address());
            parameters.put("IsActive", req.isActive());

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to update user: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_DeleteUser");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("UserId", userId);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to delete user: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Medicine Management
    @PostMapping("/medicines")
    public ResponseEntity<?> createMedicine(@RequestBody MedicineRequest req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_CreateMedicine");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("MedicineName", req.medicineName());
            parameters.put("MedicineCode", req.medicineCode());
            parameters.put("Description", req.description());
            parameters.put("Category", req.category());
            parameters.put("Unit", req.unit());
            parameters.put("Price", req.price());
            parameters.put("StockQuantity", req.stockQuantity());
            parameters.put("ReorderLevel", req.reorderLevel());
            parameters.put("Manufacturer", req.manufacturer());
            parameters.put("IsActive", req.isActive());

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create medicine: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/medicines")
    public ResponseEntity<?> getMedicines(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetMedicines");

            Map<String, Object> parameters = new HashMap<>();
            if (category != null) parameters.put("Category", category);
            if (search != null) parameters.put("Search", search);
            parameters.put("PageNumber", page);
            parameters.put("PageSize", size);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get medicines: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/medicines/reorder-level")
    public ResponseEntity<?> getReorderLevelMedicines() {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetReorderLevelMedicines");

            Map<String, Object> result = jdbcCall.execute();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get reorder level medicines: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/medicines/{medicineId}/stock")
    public ResponseEntity<?> updateMedicineStock(@PathVariable String medicineId, @RequestBody Map<String, Object> stockData) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_UpdateMedicineStock");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("MedicineId", medicineId);
            parameters.putAll(stockData);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to update medicine stock: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Clinic Management
    @PostMapping("/clinics")
    public ResponseEntity<?> createClinic(@RequestBody ClinicRequest req) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_CreateClinic");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ClinicName", req.clinicName());
            parameters.put("ClinicCode", req.clinicCode());
            parameters.put("Address", req.address());
            parameters.put("Phone", req.phone());
            parameters.put("Email", req.email());
            parameters.put("ContactPerson", req.contactPerson());
            parameters.put("IsActive", req.isActive());

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create clinic: " + e.getMessage());
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

    // System Configuration
    @GetMapping("/config")
    public ResponseEntity<?> getSystemConfig() {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetSystemConfig");

            Map<String, Object> result = jdbcCall.execute();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get system config: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/config")
    public ResponseEntity<?> updateSystemConfig(@RequestBody Map<String, Object> configData) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_UpdateSystemConfig");

            Map<String, Object> result = jdbcCall.execute(configData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to update system config: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Backup and Restore
    @PostMapping("/backup")
    public ResponseEntity<?> createBackup(@RequestBody Map<String, Object> backupData) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_CreateBackup");

            Map<String, Object> result = jdbcCall.execute(backupData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create backup: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/restore")
    public ResponseEntity<?> restoreBackup(@RequestBody Map<String, Object> restoreData) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_RestoreBackup");

            Map<String, Object> result = jdbcCall.execute(restoreData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to restore backup: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Audit Logs
    @GetMapping("/audit-logs")
    public ResponseEntity<?> getAuditLogs(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("USP_GetAuditLogs");

            Map<String, Object> parameters = new HashMap<>();
            if (userId != null) parameters.put("UserId", userId);
            if (action != null) parameters.put("Action", action);
            if (dateFrom != null) parameters.put("DateFrom", dateFrom);
            if (dateTo != null) parameters.put("DateTo", dateTo);
            parameters.put("PageNumber", page);
            parameters.put("PageSize", size);

            Map<String, Object> result = jdbcCall.execute(parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get audit logs: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
