package com.climasys.auth.web;

import com.climasys.auth.service.HttpSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for HTTP session management endpoints
 */
@RestController
@RequestMapping("/api/auth/session")
@CrossOrigin(origins = "*")
@Tag(name = "Session Management", description = "Endpoints for managing HTTP sessions")
public class HttpSessionController {

    @Autowired
    private HttpSessionService httpSessionService;

    @Operation(
        summary = "Get Session Information",
        description = "Retrieve current session information including clinic and doctor IDs from HTTP session"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Session information retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No active session",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class)
            )
        )
    })
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSessionInfo(HttpSession session) {
        try {
            Map<String, Object> sessionInfo = httpSessionService.getSessionInfo(session);
            
            if (sessionInfo.containsKey("error")) {
                return ResponseEntity.status(401).body(sessionInfo);
            }
            
            return ResponseEntity.ok(sessionInfo);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error processing request: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Operation(
        summary = "Get Doctor ID",
        description = "Get doctor ID from current session"
    )
    @GetMapping("/doctor-id")
    public ResponseEntity<Map<String, Object>> getDoctorId(HttpSession session) {
        try {
            String doctorId = httpSessionService.getDoctorId(session);
            
            Map<String, Object> response = new HashMap<>();
            if (doctorId != null) {
                response.put("doctorId", doctorId);
                response.put("status", "success");
            } else {
                response.put("error", "No active session or doctor ID not found");
                return ResponseEntity.status(401).body(response);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error processing request: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Operation(
        summary = "Get Clinic ID",
        description = "Get clinic ID from current session"
    )
    @GetMapping("/clinic-id")
    public ResponseEntity<Map<String, Object>> getClinicId(HttpSession session) {
        try {
            String clinicId = httpSessionService.getClinicId(session);
            
            Map<String, Object> response = new HashMap<>();
            if (clinicId != null) {
                response.put("clinicId", clinicId);
                response.put("status", "success");
            } else {
                response.put("error", "No active session or clinic ID not found");
                return ResponseEntity.status(401).body(response);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error processing request: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Operation(
        summary = "Get Login ID",
        description = "Get login ID from current session"
    )
    @GetMapping("/login-id")
    public ResponseEntity<Map<String, Object>> getLoginId(HttpSession session) {
        try {
            String loginId = httpSessionService.getLoginId(session);
            
            Map<String, Object> response = new HashMap<>();
            if (loginId != null) {
                response.put("loginId", loginId);
                response.put("status", "success");
            } else {
                response.put("error", "No active session or login ID not found");
                return ResponseEntity.status(401).body(response);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error processing request: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Operation(
        summary = "Get User ID",
        description = "Get user ID from current session"
    )
    @GetMapping("/user-id")
    public ResponseEntity<Map<String, Object>> getUserId(HttpSession session) {
        try {
            Long userId = httpSessionService.getUserId(session);
            
            Map<String, Object> response = new HashMap<>();
            if (userId != null) {
                response.put("userId", userId);
                response.put("status", "success");
            } else {
                response.put("error", "No active session or user ID not found");
                return ResponseEntity.status(401).body(response);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error processing request: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Operation(
        summary = "Validate Session",
        description = "Check if the current session is valid"
    )
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateSession(HttpSession session) {
        try {
            boolean isValid = httpSessionService.isValidSession(session);
            boolean isExpired = httpSessionService.isSessionExpired(session);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("expired", isExpired);
            response.put("sessionId", session != null ? session.getId() : null);
            response.put("status", isValid ? "success" : "invalid");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error processing request: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Operation(
        summary = "Logout",
        description = "Invalidate the current session and logout user"
    )
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        try {
            httpSessionService.clearSession(session);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Logout successful");
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error processing logout: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Operation(
        summary = "Update Session Timeout",
        description = "Update the session timeout duration"
    )
    @PostMapping("/timeout")
    public ResponseEntity<Map<String, Object>> updateSessionTimeout(
            @RequestParam int timeoutInSeconds, 
            HttpSession session) {
        try {
            httpSessionService.updateSessionTimeout(session, timeoutInSeconds);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Session timeout updated successfully");
            response.put("timeoutInSeconds", timeoutInSeconds);
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error updating session timeout: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
