package com.climasys.auth.web;

import com.climasys.auth.dto.ChangePasswordRequest;
import com.climasys.auth.dto.LoginRequest;
import com.climasys.auth.dto.LoginResponse;
import com.climasys.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Operation(summary = "User Login", description = "Authenticate user with login credentials and return user details, roles, and system information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        try {
            LoginResponse response = authService.authenticateUser(
                    request.getLoginId(),
                    request.getPassword(),
                    request.getTodaysDay(),
                    request.getLanguageId(),
                    session);

            logger.info("Login Successful - User: '{}', Session ID: '{}'", request.getLoginId(), session.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LoginResponse errorResponse = new LoginResponse();
            errorResponse.setLoginStatus(-1);
            errorResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Operation(summary = "Health Check", description = "Check if the authentication service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Auth service is running")))
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running");
    }

    @Operation(summary = "Change Password", description = "Change user password by validating old password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or password mismatch"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@Valid @RequestBody ChangePasswordRequest request,
            HttpSession session) {
        try {
            String loginId = request.getLoginId();

            logger.info("ChangePassword Request - Request LoginID: '{}', Session ID: '{}'",
                    loginId, (session != null ? session.getId() : "null"));

            // Fallback to session loginId if not provided in request
            if (loginId == null || loginId.trim().isEmpty()) {
                loginId = (String) session.getAttribute("loginId");
                logger.info("ChangePassword - Retrieved LoginID from session: '{}'", loginId);
            }

            if (loginId == null || loginId.trim().isEmpty()) {
                logger.error("ChangePassword Failed - LoginID missing in both request and session");

                // Dump session attributes to debug
                if (session != null) {
                    logger.error("Debug - Session ID: {}", session.getId());
                    java.util.Enumeration<String> attributeNames = session.getAttributeNames();
                    while (attributeNames.hasMoreElements()) {
                        String name = attributeNames.nextElement();
                        logger.error("Debug - Session Attribute: {} = {}", name, session.getAttribute(name));
                    }
                } else {
                    logger.error("Debug - Session is NULL");
                }

                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "User login ID missing. Please login again."));
            }

            boolean success = authService.changePassword(
                    loginId,
                    request.getOldPassword(),
                    request.getNewPassword());

            if (success) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Password changed successfully"));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "Failed to change password. Please check your credentials."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "An error occurred details: " + e.getMessage()));
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        Map<String, String> errors = new java.util.HashMap<>();
        errors.put("status", "error");
        StringBuilder message = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            message.append(fieldName).append(" - ").append(errorMessage).append("; ");
        });
        errors.put("message", message.toString());
        return errors;
    }
}