package com.climasys.auth.web;

import com.climasys.common.crypto.LegacyCrypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("${climasys.encryption.key:PA1ANDE61INI6}")
    private String encryptionKey;

    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        try {
            // Test basic database connection
            String result = jdbcTemplate.queryForObject("SELECT 'Database connection successful' as message", String.class);
            return ResponseEntity.ok(Map.of("status", "success", "message", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/function")
    public ResponseEntity<Map<String, Object>> testFunction() {
        try {
            // Test if the login function exists
            String result = jdbcTemplate.queryForObject(
                "SELECT usp_get_logindetails_json('test', 'test', 'Monday', 1)", 
                String.class
            );
            return ResponseEntity.ok(Map.of("status", "success", "function_result", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Test controller is running");
    }
    
    @GetMapping("/crypto")
    public ResponseEntity<Map<String, Object>> testCrypto() {
        try {
            // Test password encryption and decryption
            String testPassword = "test123";
            String encrypted = LegacyCrypto.encryptUnicode(encryptionKey, testPassword);
            String decrypted = LegacyCrypto.decryptUnicode(encryptionKey, encrypted);
            
            boolean isWorking = testPassword.equals(decrypted);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "original_password", testPassword,
                "encrypted_password", encrypted,
                "decrypted_password", decrypted,
                "encryption_working", isWorking
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", "error", 
                "message", e.getMessage()
            ));
        }
    }
}
